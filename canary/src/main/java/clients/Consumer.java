/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package clients;

import common.Message;
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
    private final double[] consumerLatencyBuckets;

    public Consumer(CanaryConfiguration configuration) {
        this.properties = ClientConfiguration.consumerProperties(configuration);
        this.consumer = new KafkaConsumer<>(properties);
        this.topicName = configuration.getTopic();
        this.expectedClusterSize = configuration.getExpectedClusterSize();
        this.clientId = configuration.getClientId();
        this.consumerLatencyBuckets = configuration.getEndToEndLatencyBuckets();
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
        CompletableFuture<Void> future = new CompletableFuture<>();

        try {
            // poll all messages
            ConsumerRecords<String, String> receivedMessages = this.consumer.poll(Duration.ofMillis(100));
            long receivedTime = System.currentTimeMillis();

            // commit current offset
            this.consumer.commitSync();

            receivedMessages.forEach(message -> {
                LOGGER.info("Received message with value: {} from partition: {}", message.value(), message.partition());
                Message receivedMessage = Message.parseFromJson(message.value());

                long receiveDuration = receivedTime - receivedMessage.timestamp();

                LOGGER.info("End to end latency for message: {} to partition: {} is {}ms", message.value(), message.partition(), receiveDuration);

                // incrementing different counter for Status check
                MessageCountHolder.getInstance().incrementConsumedMessagesCount();

                MetricsRegistry.getInstance().getRecordsConsumedTotal(clientId, message.partition()).increment();
                MetricsRegistry.getInstance().getRecordsConsumedLatency(clientId, message.partition(), consumerLatencyBuckets).record(receiveDuration);
            });

            future.complete(null);

        } catch (Exception e) {
            LOGGER.error("Failed to poll messages due to: {}", e.getMessage());
            e.printStackTrace();
            MetricsRegistry.getInstance().getConsumerErrorTotal(clientId).increment();
            future.completeExceptionally(new RuntimeException("Failed to poll the messages due to: " + e.getMessage()));
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
