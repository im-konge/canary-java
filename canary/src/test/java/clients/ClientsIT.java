/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package clients;

import common.Message;
import config.CanaryConfiguration;
import config.CanaryConstants;
import io.strimzi.test.container.StrimziKafkaContainer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

public class ClientsIT {

    private static StrimziKafkaContainer kafkaContainer;
    private static KafkaProducer<String, String> kafkaProducer;
    private static KafkaConsumer<String, String> kafkaConsumer;
    private static String topicName;
    private static Producer producer;
    private static Consumer consumer;
    private static AdminClient adminClient;

    private static final Logger LOGGER = LogManager.getLogger(ClientsIT.class);

    @Test
    void checkThatTopicIsCreated() {
        assertThat(adminClient.isTopicCreated(), is(true));
    }

    @Test
    void testReceiveMessages() throws ExecutionException, InterruptedException {
        int sentMessage = 1;
        int triesCounter = 0;
        Message generatedMessage = new Message("my-producer", sentMessage, System.currentTimeMillis());

        LOGGER.info("Send one message to Kafka using official Kafka producer");
        kafkaProducer.send(new ProducerRecord<>(topicName, 0, null, null, generatedMessage.getJsonMessage())).get();

        LOGGER.info("First poll for fetching metadata about cluster");
        consumer.receiveMessages();

        while (triesCounter < 3) {
            LOGGER.info("Polling the message");
            consumer.receiveMessages();
            triesCounter++;
            Thread.sleep(1000);
        }

        LOGGER.info("Check that message counter is increased");
        assertThat(MessageCountHolder.getInstance().getConsumedMessagesCount(), is(not(0)));
    }

    @Test
    void testSendMessage() {
        int sentMessage = 1;

        LOGGER.info("Send one message to Kafka");
        producer.sendMessages();

        kafkaConsumer.subscribe(Collections.singletonList(topicName));

        LOGGER.info("First poll for fetching metadata about cluster");
        kafkaConsumer.poll(Duration.ofMillis(1000));

        LOGGER.info("Second poll for polling the message");
        kafkaConsumer.poll(Duration.ofMillis(1000));

        LOGGER.info("Check that message counter is increased");
        assertThat(MessageCountHolder.getInstance().getProducedMessagesCount(), is(sentMessage));
    }

    @BeforeAll
    public static void beforeAll() {
        kafkaContainer = new StrimziKafkaContainer()
            .withBrokerId(1);

        kafkaContainer.start();

        topicName = "my-custom-topic-name";
        String clientId = "my-custom-client";
        String consumerGroup = "my-random-consumer-group-4543323";

        Map<String, String> testConfigurationMap = new HashMap<>();
        testConfigurationMap.put(CanaryConstants.BOOTSTRAP_SERVERS_ENV, kafkaContainer.getBootstrapServers());
        testConfigurationMap.put(CanaryConstants.TOPIC_ENV, topicName);
        testConfigurationMap.put(CanaryConstants.CLIENT_ID_ENV, clientId);
        testConfigurationMap.put(CanaryConstants.CONSUMER_GROUP_ID_ENV, consumerGroup);

        CanaryConfiguration configuration = CanaryConfiguration.fromMap(testConfigurationMap);

        producer = new Producer(configuration);
        consumer = new Consumer(configuration);
        adminClient = new AdminClient(configuration);

        testConfigurationMap.put(CanaryConstants.CLIENT_ID_ENV, "kafka-consumer-client");
        testConfigurationMap.put(CanaryConstants.CONSUMER_GROUP_ID_ENV, "another-consumer-group");

        configuration = CanaryConfiguration.fromMap(testConfigurationMap);

        kafkaProducer = new KafkaProducer<>(ClientConfiguration.producerProperties(configuration));
        kafkaConsumer = new KafkaConsumer<>(ClientConfiguration.consumerProperties(configuration));

        adminClient.start();
        producer.start();
        consumer.start();
    }
}
