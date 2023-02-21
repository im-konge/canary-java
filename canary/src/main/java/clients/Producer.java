/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package clients;

import common.Message;
import common.metrics.MetricsRegistry;
import config.CanaryConfiguration;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class Producer implements Client {

    private static final Logger LOGGER = LogManager.getLogger(Producer.class);
    private final KafkaProducer<String, String> producer;
    private final String topicName;
    private final String producerId;
    private final Properties properties;
    private final int expectedClusterSize;
    private final double[] producerLatencyBuckets;

    public Producer(CanaryConfiguration configuration) {
        this.properties = ClientConfiguration.producerProperties(configuration);
        this.producer = new KafkaProducer<>(this.properties);
        this.topicName = configuration.getTopic();
        this.producerId = configuration.getClientId();
        this.expectedClusterSize = configuration.getExpectedClusterSize();
        this.producerLatencyBuckets = configuration.getProducerLatencyBuckets();
    }

    public CompletionStage<Integer> sendMessages() {
        LOGGER.info("Sending messages to KafkaTopic: {}", topicName);
        CompletableFuture<Integer> future = new CompletableFuture<>();

        for (int i = 0; i < this.expectedClusterSize; i++) {
            try {
                Message generatedMessage = createMessage(i);
                LOGGER.info("Sending message: {} to partition: {}", generatedMessage, i);
                this.producer.send(new ProducerRecord<>(this.topicName, i, null, null, generatedMessage)).get();

                // incrementing different counter for Status check
                MessageCountHolder.getInstance().incrementProducedMessagesCount();

                long sendDuration = System.currentTimeMillis() - generatedMessage.timestamp();

                MetricsRegistry.getInstance().getRecordsProducedTotal(producerId, i).increment();
                MetricsRegistry.getInstance().getRecordsProducedLatency(producerId, i, producerLatencyBuckets).record(sendDuration);
            } catch (Exception exception) {
                LOGGER.error("Failed to send message with ID: {}", i);
                MetricsRegistry.getInstance().getRecordsProducedFailedTotal(producerId, i).increment();
                future.completeExceptionally(exception);
            }
        }

        future.complete(expectedClusterSize);
        LOGGER.info("All messages successfully sent");

        return future;
    }

    private Message createMessage(int messageId) {
        return new Message(producerId, messageId, System.currentTimeMillis());
    }

    @Override
    public void start() {
        LOGGER.info("Starting KafkaProducer with properties: {}", properties);
    }

    @Override
    public void stop() {
        LOGGER.info("Stopping KafkaProducer");
    }
}
