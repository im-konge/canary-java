/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package services.clients;

import config.CanaryConfiguration;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.topic.Topic;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class AdminClient {

    private static final Logger LOGGER = LogManager.getLogger(AdminClient.class);
    private final Admin adminClient;

    private final Topic topic;

    public AdminClient(CanaryConfiguration configuration) {
        Properties properties = ClientConfiguration.adminProperties(configuration);

        this.adminClient = Admin.create(properties);
        this.topic = new Topic(configuration.getTopic(), configuration.getTopicConfig());
    }

    private boolean isTopicCreated() {
        ListTopicsResult topicList = this.adminClient.listTopics();
        try {
            return topicList.names().get().contains(this.topic.topicName());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void createTopic() {
        LOGGER.info("Creating KafkaTopic: {} with configuration:\n {}", this.topic.topicName(), this.topic.topicConfig().toString());

        // override cleanup policy because it needs to be "delete" (canary doesn't use keys on messages)
        this.topic.topicConfig().put(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE);

        NewTopic topic = new NewTopic(this.topic.topicName(), -1, (short) -1).configs(this.topic.topicConfig());
        CreateTopicsResult creationResult = this.adminClient.createTopics(Collections.singletonList(topic));

        KafkaFuture<Void> kafkaFuture = creationResult.all();

        try {
            kafkaFuture.get();
            LOGGER.info("KafkaTopic: {} successfully created", this.topic.topicName());
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Failed to create KafkaTopic: {} due to:\n {}", this.topic.topicName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteTopic() {
        LOGGER.info("Deleting KafkaTopic: {}", this.topic.topicName());

        DeleteTopicsResult deleteResult = this.adminClient.deleteTopics(Collections.singletonList(this.topic.topicName()));

        KafkaFuture<Void> kafkaFuture = deleteResult.all();

        try {
            kafkaFuture.get();
            LOGGER.info("KafkaTopic: {} successfully deleted", this.topic.topicName());
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Failed to delete KafkaTopic: {} due to:\n {}", this.topic.topicName(), e.getMessage());
            e.printStackTrace();
        }
    }
}
