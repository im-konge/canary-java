/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
import clients.AdminClient;
import clients.Consumer;
import clients.Producer;
import config.CanaryConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Canary {

    private static final Logger LOGGER = LogManager.getLogger(Canary.class);
    private Producer producer;
    private Consumer consumer;
    private AdminClient adminClient;
    private CanaryConfiguration canaryConfiguration;
    private final ScheduledExecutorService scheduledExecutor;

    Canary(CanaryConfiguration configuration) {
        this.producer = new Producer(configuration);
        this.consumer = new Consumer(configuration);
        this.adminClient = new AdminClient(configuration);
        this.canaryConfiguration = configuration;
        this.scheduledExecutor = Executors.newScheduledThreadPool(1, r -> new Thread(r, "canary"));
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

    public void start() {
        LOGGER.info("Starting Canary with configuration: {}", this.getCanaryConfiguration().toString());

        this.getAdminClient().start();
        this.getConsumer().start();
        this.getProducer().start();

        scheduledExecutor.scheduleAtFixedRate(this::reconcile, canaryConfiguration.getReconcileInterval(),  canaryConfiguration.getReconcileInterval(), TimeUnit.MILLISECONDS);
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
}