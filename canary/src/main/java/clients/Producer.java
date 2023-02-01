/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package clients;

import com.google.gson.JsonObject;
import config.CanaryConfiguration;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;
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

    public Producer(CanaryConfiguration configuration) {
        this.properties = ClientConfiguration.producerProperties(configuration);
        this.producer = new KafkaProducer<>(this.properties);
        this.topicName = configuration.getTopic();
        this.producerId = configuration.getClientId();
        this.expectedClusterSize = configuration.getExpectedClusterSize();
    }

    public CompletionStage<Integer> sendMessages() {
        LOGGER.info("Sending messages to KafkaTopic: {}", topicName);
        CompletableFuture<Integer> future = new CompletableFuture<>();

        for (int i = 0; i < this.expectedClusterSize; i++) {
            try {
                String generatedMessage = generateMessage(i);
                LOGGER.info("Sending message: {} to partition: {}", generatedMessage, i);
                this.producer.send(new ProducerRecord<>(this.topicName, i, null, null, generatedMessage)).get();
            } catch (Exception exception) {
                LOGGER.error("Failed to send message with ID: {}", i);
                future.completeExceptionally(exception);
            }
        }

        future.complete(expectedClusterSize);
        LOGGER.info("All messages successfully sent");

        return future;
    }

    private String generateMessage(int messageId) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        JsonObject message = new JsonObject();
        message.addProperty("producerId", this.producerId);
        message.addProperty("messageId", String.valueOf(messageId));
        message.addProperty("timestamp", timestamp.toString());

        return message.toString();
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
