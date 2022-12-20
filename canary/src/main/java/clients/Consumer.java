/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package clients;

import config.CanaryConfiguration;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class Consumer implements Client {

    private static final Logger LOGGER = LogManager.getLogger(Consumer.class);
    private final KafkaConsumer<String, String> consumer;
    private final String topicName;
    private final Properties properties;

    public Consumer(CanaryConfiguration configuration) {
        this.properties = ClientConfiguration.consumerProperties(configuration);
        this.consumer = new KafkaConsumer<>(properties);
        this.topicName = configuration.getTopic();
    }

    private void subscribe() {
        LOGGER.info("Subscribing to topic: {}", topicName);
        this.consumer.subscribe(Collections.singletonList(topicName));
    }

    private void unsubscribe() {
        LOGGER.info("Unsubscribing from topic: {}", topicName);
        this.consumer.unsubscribe();
    }

    public CompletionStage<Void> receiveMessages() {
        CompletableFuture<Void> future = new CompletableFuture<>();

        // poll all messages
        // TODO: add assignment of topic partitions etc
        ConsumerRecords<String, String> records = this.consumer.poll(Duration.ofMillis(30000));

        // this will differ
        if (records.count() == 100) {
            future.complete(null);
        } else {
            LOGGER.error("Failed to poll all the messages");
            future.completeExceptionally(new Exception(String.join("Failed to poll all the messages. Polled: %s", Integer.toString(records.count()))));
        }

        return future;
    }

    @Override
    public void start() {
        LOGGER.info("Starting KafkaConsumer with properties: {}", properties);
        subscribe();
    }

    @Override
    public void stop() {
        LOGGER.info("Stopping KafkaConsumer");
        unsubscribe();
    }
}
