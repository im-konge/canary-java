/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
import clients.AdminClient;
import clients.Consumer;
import clients.Producer;
import common.metrics.MetricsRegistry;
import config.CanaryConfiguration;
import config.CanaryConstants;
import connection.ConnectionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import status.StatusService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Canary {

    private static final Logger LOGGER = LogManager.getLogger(Canary.class);
    private static final int THREAD_POOL_SIZE = 3;

    private Producer producer;
    private Consumer consumer;
    private AdminClient adminClient;
    private CanaryConfiguration canaryConfiguration;
    private StatusService status;
    private ConnectionService connection;
    private final ScheduledExecutorService scheduledExecutor;

    Canary(CanaryConfiguration configuration) {
        this.producer = new Producer(configuration);
        this.consumer = new Consumer(configuration);
        this.adminClient = new AdminClient(configuration);
        this.status = new StatusService(configuration);
        this.connection = new ConnectionService(configuration, adminClient);

        this.canaryConfiguration = configuration;
        this.scheduledExecutor = Executors.newScheduledThreadPool(THREAD_POOL_SIZE, r -> new Thread(r, "canary"));
    }

    public Producer getProducer() {
        return this.producer;
    }

    public Consumer getConsumer() {
        return this.consumer;
    }

    public AdminClient getAdminClient() {
        return this.adminClient;
    }

    public CanaryConfiguration getCanaryConfiguration() {
        return this.canaryConfiguration;
    }

    public StatusService getStatusService() {
        return this.status;
    }

    public ConnectionService getConnectionService() {
        return this.connection;
    }

    public void start() {
        LOGGER.info("Starting Canary with configuration: {}", this.getCanaryConfiguration().toString());

        waitForClusterExpectedSize();

        LOGGER.info("Kafka cluster have expected number of brokers, continuing with start operations");

        this.getAdminClient().start();
        this.getConsumer().start();
        this.getProducer().start();

        scheduledExecutor.scheduleAtFixedRate(this::reconcile, 0,  canaryConfiguration.getReconcileInterval(), TimeUnit.MILLISECONDS);
        scheduledExecutor.scheduleAtFixedRate(this.getStatusService()::statusCheck, 0,  canaryConfiguration.getStatusCheckInterval(), TimeUnit.MILLISECONDS);
        scheduledExecutor.scheduleAtFixedRate(this.getConnectionService()::checkConnection, 0,  canaryConfiguration.getStatusCheckInterval(), TimeUnit.MILLISECONDS);
    }

    private void reconcile() {
        try {
            CompletableFuture.allOf(
                this.getProducer().sendMessages().toCompletableFuture(),
                this.getConsumer().receiveMessages().toCompletableFuture()
            ).get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        LOGGER.info("Shutting down Canary");

        this.getProducer().stop();
        this.getConsumer().stop();
        this.getAdminClient().stop();

        scheduledExecutor.shutdownNow();
    }

    private void waitForClusterExpectedSize() {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1, r -> new Thread(r, "canary"));

        CountDownLatch latch = new CountDownLatch(1);

        LOGGER.info("Waiting for Kafka cluster to have expected number of brokers");

        executorService.scheduleAtFixedRate(() -> {
            if (this.getAdminClient().hasClusterExpectedSize()) {
                executorService.shutdownNow();
                latch.countDown();
            } else {
                MetricsRegistry.getInstance().getExpectedClusterSizeErrorTotal().increment();
            }
        }, 0, canaryConfiguration.getReconcileInterval(), TimeUnit.MILLISECONDS);

        try {
            latch.await();
            executorService.awaitTermination(CanaryConstants.TASK_TERMINATION_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            LOGGER.error("Failed to wait for task completion due to: {}", e.getMessage());
            e.printStackTrace();
        } finally {
            if (!executorService.isShutdown()) {
                executorService.shutdownNow();
            }
        }
    }
}
