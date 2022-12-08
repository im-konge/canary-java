/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package clients;

import config.CanaryConfiguration;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class Consumer {

    private final KafkaConsumer<String, String> consumer;
    private final String topicName;

    public Consumer(CanaryConfiguration configuration) {
        Properties properties = ClientConfiguration.consumerProperties(configuration);

        this.consumer = new KafkaConsumer<>(properties);
        this.topicName = configuration.getTopic();
    }

    public void receiveMessages() {
        // subscribe to topic
        this.consumer.subscribe(Collections.singletonList(this.topicName));

        // poll all messages
        // TODO: add assignment of topic partitions etc
        ConsumerRecords<String, String> records = this.consumer.poll(Duration.ofMillis(30000));
    }
}
