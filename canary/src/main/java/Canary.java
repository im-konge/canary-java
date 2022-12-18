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

public class Canary {

    private static final Logger LOGGER = LogManager.getLogger(Canary.class);
    private Producer producer;
    private Consumer consumer;
    private AdminClient adminClient;
    private CanaryConfiguration canaryConfiguration;

    Canary(CanaryConfiguration configuration) {
        this.producer = new Producer(configuration);
        this.consumer = new Consumer(configuration);
        this.adminClient = new AdminClient(configuration);
        this.canaryConfiguration = configuration;
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

        if (this.getAdminClient().isTopicCreated()) {
            LOGGER.warn("Topic {} already created, going to delete it", this.getCanaryConfiguration().getTopic());
            this.getAdminClient().deleteTopic();
        }

        this.getAdminClient().createTopic();
    }

    public void reconcile() {
        this.getAdminClient().createTopicIfNotExists();
        this.getProducer().sendMessages();
        this.getConsumer().receiveMessages();
    }

    public void stop() {
        LOGGER.info("Stopping Canary");
        this.getAdminClient().deleteTopic();
    }
}
