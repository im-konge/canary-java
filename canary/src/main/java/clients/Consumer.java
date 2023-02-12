/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package clients;

import common.metrics.MetricsRegistry;
import config.CanaryConfiguration;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class Consumer implements Client {

    private static final Logger LOGGER = LogManager.getLogger(Consumer.class);
    private final KafkaConsumer<String, String> consumer;
    private final String topicName;
    private final Properties properties;
    private final int expectedClusterSize;
    private final String clientId;

    public Consumer(CanaryConfiguration configuration) {
        this.properties = ClientConfiguration.consumerProperties(configuration);
        this.consumer = new KafkaConsumer<>(properties);
        this.topicName = configuration.getTopic();
        this.expectedClusterSize = configuration.getExpectedClusterSize();
        this.clientId = configuration.getClientId();
    }

    private void assignPartitions() {
        LOGGER.info("Assigning: {} number of partitions", String.valueOf(expectedClusterSize));
        List<TopicPartition> topicPartitions = getTopicPartitions();

        this.consumer.assign(topicPartitions);
    }

    private List<TopicPartition> getTopicPartitions() {
        List<TopicPartition> topicPartitions = new ArrayList<>();

        for (int i = 0; i < expectedClusterSize; i++) {
            topicPartitions.add(new TopicPartition(topicName, i));
        }

        return topicPartitions;
    }

    private void unsubscribe() {
        LOGGER.info("Unsubscribing from topic: {}", topicName);
        this.consumer.unsubscribe();
    }

    public CompletionStage<Void> receiveMessages() {
        LOGGER.info("Receiving messages from KafkaTopic: {}", topicName);
        CompletableFuture<Void> future = new CompletableFuture<>();

        // poll all messages
        ConsumerRecords<String, String> receivedMessages = this.consumer.poll(Duration.ofMillis(15000));

        if (receivedMessages.count() == expectedClusterSize) {
            // commit current offset
            this.consumer.commitSync();
            receivedMessages.forEach(message -> MetricsRegistry.getInstance().getRecordsConsumedTotal(clientId, message.partition()).increment());

            future.complete(null);
            LOGGER.info("All messages successfully received");
        } else {
            LOGGER.error("Failed to poll all the messages");
            MetricsRegistry.getInstance().getConsumerErrorTotal(clientId).increment();
            future.completeExceptionally(new RuntimeException("Failed to poll all the messages. Polled: " + receivedMessages.count()));
        }

        return future;
    }

    @Override
    public void start() {
        LOGGER.info("Starting KafkaConsumer with properties: {}", properties);
        assignPartitions();
    }

    @Override
    public void stop() {
        LOGGER.info("Stopping KafkaConsumer");
        unsubscribe();
        this.consumer.close();
    }
}
