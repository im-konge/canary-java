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
import org.apache.kafka.common.KafkaException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import status.StatusService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Canary {

    private static final Logger LOGGER = LogManager.getLogger(Canary.class);
    private static final int THREAD_POOL_SIZE = 2;

    private Producer producer;
    private Consumer consumer;
    private AdminClient adminClient;
    private CanaryConfiguration canaryConfiguration;
    private StatusService status;
    private final ScheduledExecutorService scheduledExecutor;
    private final ConsumerInfiniteRunnable consumerInfiniteRunnable;
    private final Thread consumerThread;

    Canary(CanaryConfiguration configuration) {
        Producer producer;
        Consumer consumer;
        AdminClient adminClient;

        try {
            producer = new Producer(configuration);
            consumer = new Consumer(configuration);
            adminClient = new AdminClient(configuration);
        } catch (KafkaException e) {
            MetricsRegistry.getInstance().getClientCreationErrorTotal().increment();
            LOGGER.error("Failed to create Kafka client: {}", e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        this.producer = producer;
        this.consumer = consumer;
        this.adminClient = adminClient;
        this.status = new StatusService(configuration);

        this.canaryConfiguration = configuration;
        this.scheduledExecutor = Executors.newScheduledThreadPool(THREAD_POOL_SIZE, r -> new Thread(r, "canary"));
        this.consumerInfiniteRunnable = new ConsumerInfiniteRunnable(this.consumer);
        this.consumerThread = new Thread(this.consumerInfiniteRunnable, "canary-consumer");
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

    public Thread getConsumerThread() {
        return consumerThread;
    }

    public ConsumerInfiniteRunnable getConsumerInfiniteRunnable() {
        return consumerInfiniteRunnable;
    }

    public void start() {
        LOGGER.info("Starting Canary with configuration: {}", this.getCanaryConfiguration().toString());

        waitForClusterExpectedSize();

        LOGGER.info("Kafka cluster have expected number of brokers, continuing with start operations");

        this.getAdminClient().start();
        this.getConsumer().start();
        this.getProducer().start();

        this.getConsumerThread().start();
        scheduledExecutor.scheduleAtFixedRate(this.getProducer()::sendMessages, 0,  canaryConfiguration.getReconcileInterval(), TimeUnit.MILLISECONDS);
        scheduledExecutor.scheduleAtFixedRate(this.getStatusService()::statusCheck, 0,  canaryConfiguration.getStatusCheckInterval(), TimeUnit.MILLISECONDS);
    }

    public void stop() {
        LOGGER.info("Shutting down Canary");

        this.stopConsumerThread();
        this.getProducer().stop();
        this.getConsumer().stop();
        this.getAdminClient().stop();

        scheduledExecutor.shutdownNow();
    }

    private void stopConsumerThread() {
        try {
            this.getConsumerInfiniteRunnable().stop();
            this.getConsumerThread().join();
        } catch (InterruptedException e) {
            LOGGER.error("Failed to close the Consumer thread: {}", e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
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

    public static class ConsumerInfiniteRunnable implements Runnable {
        private volatile boolean running = true;
        private final Consumer consumer;

        ConsumerInfiniteRunnable(Consumer consumer) {
            this.consumer = consumer;
        }

        public void stop() {
            running = false;
        }

        public void run() {
            while (running) {
                this.consumer.receiveMessages();
            }
        }
    }
}
