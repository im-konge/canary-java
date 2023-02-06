/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package clients;

import config.CanaryConfiguration;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.CreatePartitionsResult;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewPartitions;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import topic.Topic;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class AdminClient implements Client {

    private static final Logger LOGGER = LogManager.getLogger(AdminClient.class);
    private final Admin adminClient;
    private final Properties properties;
    private final Topic topic;
    private final int expectedClusterSize;

    public AdminClient(CanaryConfiguration configuration) {
        this.properties = ClientConfiguration.adminProperties(configuration);
        this.adminClient = Admin.create(properties);
        this.topic = new Topic(configuration.getTopic(), configuration.getTopicConfig());
        this.expectedClusterSize = configuration.getExpectedClusterSize();
    }

    public void createOrReplaceTopicIfNotExists() {
        if (!isTopicCreated()) {
            LOGGER.warn("KafkaTopic: {} not created, going to create it now", this.topic.topicName());
            createTopic();
        } else if (shouldUpdateTopic()) {
            LOGGER.warn("KafkaTopic: {} partitions needs to be updated, going to do it now", this.topic.topicName());
            updateTopic();
        }
    }

    public boolean isTopicCreated() {
        ListTopicsResult topicList = this.adminClient.listTopics();
        try {
            return topicList.names().get().contains(this.topic.topicName());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean shouldUpdateTopic() {
        DescribeTopicsResult topicDesc = this.adminClient.describeTopics(Collections.singletonList(this.topic.topicName()));

        try {
            return topicDesc.topicNameValues().get(this.topic.topicName()).get().partitions().size() < this.expectedClusterSize;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void createTopic() {
        LOGGER.info("Creating KafkaTopic: {} with configuration:\n {}", this.topic.topicName(), this.topic.topicConfig());

        // override cleanup policy because it needs to be "delete" (canary doesn't use keys on messages)
        this.topic.topicConfig().put(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE);

        NewTopic topic = new NewTopic(this.topic.topicName(), this.expectedClusterSize, (short) this.expectedClusterSize).configs(this.topic.topicConfig());
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

    public void updateTopic() {
        LOGGER.info("Updating KafkaTopic: {} to have {} partitions", this.topic.topicName(), this.expectedClusterSize);

        Map<String, NewPartitions> newPartitionSet = Collections.singletonMap(topic.topicName(), NewPartitions.increaseTo(expectedClusterSize));

        CreatePartitionsResult createPartitionsResult = this.adminClient.createPartitions(newPartitionSet);

        KafkaFuture<Void> kafkaFuture = createPartitionsResult.all();

        try {
            kafkaFuture.get();
            LOGGER.info("KafkaTopic: {} successfully updated to {} partitions", this.topic.topicName(), this.expectedClusterSize);
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Failed to update KafkaTopic: {} due to:\n {}", this.topic.topicName(), e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        LOGGER.info("Starting Admin client with properties: {}", properties);
        createOrReplaceTopicIfNotExists();
    }

    @Override
    public void stop() {
        LOGGER.info("Stopping Admin client");
        this.adminClient.close();
    }
}
