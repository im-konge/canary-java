/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package services.clients;

import config.CanaryConfiguration;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.Properties;

public class Producer {

    private final KafkaProducer<String, String> producer;
    private final String topicName;
    private final String producerId;

    public Producer(CanaryConfiguration configuration) {
        Properties properties = ClientConfiguration.producerProperties(configuration);

        this.producer = new KafkaProducer<>(properties);
        this.topicName = configuration.getTopic();
        this.producerId = configuration.getClientId();
    }

    public void sendMessages() {
        for (int i = 0; i < 100; i++) {
            this.producer.send(new ProducerRecord<>(this.topicName, null, null, null, generateMessage(i)));
        }
    }

    private String generateMessage(int messageId) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        return new JSONObject()
            .put("producerId", this.producerId)
            .put("messageId", String.valueOf(messageId))
            .put("timestamp", timestamp.toString())
            .toString();
    }
}
