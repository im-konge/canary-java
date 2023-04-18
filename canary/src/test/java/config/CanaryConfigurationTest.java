/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package config;

import common.security.SaslType;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;

public class CanaryConfigurationTest {
    @Test
    void testDefaultCanaryConfiguration() {
        CanaryConfiguration canaryConfiguration = CanaryConfiguration.fromMap(Collections.emptyMap());
        float[] defaultProducerLatencyBuckets = {2.0F, 5.0F, 10.0F, 20.0F, 50.0F, 100.0F, 200.0F, 400.0F};
        float[] defaultE2ELatencyBuckets = {5.0F, 10.0F, 20.0F, 50.0F, 100.0F, 200.0F, 400.0F, 800.0F};

        assertThat(canaryConfiguration.getBootstrapServers(), is(CanaryConstants.BOOTSTRAP_SERVERS_DEFAULT));
        assertThat(canaryConfiguration.getTopic(), is(CanaryConstants.TOPIC_DEFAULT));
        assertThat(canaryConfiguration.getTopicConfig(), nullValue());
        assertThat(canaryConfiguration.getReconcileInterval(), is(CanaryConstants.RECONCILE_INTERVAL_DEFAULT));
        assertThat(canaryConfiguration.getClientId(), is(CanaryConstants.CLIENT_ID_DEFAULT));
        assertThat(canaryConfiguration.getConsumerGroupId(), is(CanaryConstants.CONSUMER_GROUP_ID_DEFAULT));
        assertThat(canaryConfiguration.getProducerLatencyBuckets(), is(defaultProducerLatencyBuckets));
        assertThat(canaryConfiguration.getEndToEndLatencyBuckets(), is(defaultE2ELatencyBuckets));
        assertThat(canaryConfiguration.getExpectedClusterSize(), is(CanaryConstants.EXPECTED_CLUSTER_SIZE_DEFAULT));
        assertThat(canaryConfiguration.isTlsEnabled(), is(CanaryConstants.TLS_ENABLED_DEFAULT));
        assertThat(canaryConfiguration.getTlsCaCert(), is(""));
        assertThat(canaryConfiguration.getTlsClientCert(), is(""));
        assertThat(canaryConfiguration.getTlsClientKey(), is(""));
        assertThat(canaryConfiguration.getSaslMechanism(), is(""));
        assertThat(canaryConfiguration.getSaslUser(), is(""));
        assertThat(canaryConfiguration.getSaslPassword(), is(""));
        assertThat(canaryConfiguration.getStatusCheckInterval(), is(CanaryConstants.STATUS_CHECK_INTERVAL_MS_DEFAULT));
        assertThat(canaryConfiguration.getStatusTimeWindow(), is(CanaryConstants.STATUS_TIME_WINDOW_MS_DEFAULT));
    }

    @Test
    void testCustomCanaryConfiguration() {
        String bootstrapServer = "my-cluster-kafka-bootstrap:9092";
        int bootstrapBackoffMax = 3;

        Duration bootstrapBackOffScale = Duration.ofMillis(23);
        String bootstrapBackOffScaleString = "23";

        String topicName = "my-custom-topic-name";

        long reconciliationInterval = 39;

        String clientId = "my-custom-client";
        String consumerGroup = "my-random-consumer-group-4543323";

        String producerLatencyBuckets = "7,21,45,80,122,200,440,880,2000";
        float[] producerLatencyBucketsFloat = {7.0F, 21.0F, 45.0F, 80.0F, 122.0F, 200.0F, 440.0F, 880.0F, 2000.0F};

        String e2ELatencyBuckets = "7,440,880,2000";
        float[] e2ELatencyBucketsFloat = {7.0F, 440.0F, 880.0F, 2000.0F};

        String connectionCheckLatencyBuckets = "41,440,999,2000";
        float[] connectionCheckLatencyBucketsFloat = {41.0F, 440.0F, 999.0F, 2000.0F};

        int expectedClusterSize = 4;
        boolean tlsEnabled = true;
        String tlsCaCert = "ca-certificate-value";
        String tlsClientCert = "client-certificate-value";
        String tlsClientKey = "client-key-value";
        String saslMechanism = SaslType.SCRAM_SHA_512.toString();
        String saslUser = "alice";
        String saslPassword = "in-wonderland";

        Duration connectionCheckInterval = Duration.ofMillis(123000);
        String connectionCheckIntervalString = "123000";

        Duration statusCheckInterval = Duration.ofMillis(9999);
        String statusCheckIntervalString = "9999";

        Duration statusTimeWindow = Duration.ofMillis(65523);
        String statusTimeWindowString = "65523";

        Map<String, String> testConfigurationMap = new HashMap<>();
        testConfigurationMap.put(CanaryConstants.BOOTSTRAP_SERVERS_ENV, bootstrapServer);
        testConfigurationMap.put(CanaryConstants.TOPIC_ENV, topicName);
        testConfigurationMap.put(CanaryConstants.RECONCILE_INTERVAL_ENV, String.valueOf(reconciliationInterval));
        testConfigurationMap.put(CanaryConstants.CLIENT_ID_ENV, clientId);
        testConfigurationMap.put(CanaryConstants.CONSUMER_GROUP_ID_ENV, consumerGroup);
        testConfigurationMap.put(CanaryConstants.PRODUCER_LATENCY_BUCKETS_ENV, producerLatencyBuckets);
        testConfigurationMap.put(CanaryConstants.ENDTOEND_LATENCY_BUCKETS_ENV, e2ELatencyBuckets);
        testConfigurationMap.put(CanaryConstants.EXPECTED_CLUSTER_SIZE_ENV, String.valueOf(expectedClusterSize));
        testConfigurationMap.put(CanaryConstants.TLS_ENABLED_ENV, String.valueOf(tlsEnabled));
        testConfigurationMap.put(CanaryConstants.TLS_CA_CERT_ENV, tlsCaCert);
        testConfigurationMap.put(CanaryConstants.TLS_CLIENT_CERT_ENV, tlsClientCert);
        testConfigurationMap.put(CanaryConstants.TLS_CLIENT_KEY_ENV, tlsClientKey);
        testConfigurationMap.put(CanaryConstants.SASL_MECHANISM_ENV, saslMechanism);
        testConfigurationMap.put(CanaryConstants.SASL_USER_ENV, saslUser);
        testConfigurationMap.put(CanaryConstants.SASL_PASSWORD_ENV, saslPassword);
        testConfigurationMap.put(CanaryConstants.STATUS_CHECK_INTERVAL_MS_ENV, statusCheckIntervalString);
        testConfigurationMap.put(CanaryConstants.STATUS_TIME_WINDOW_MS_ENV, statusTimeWindowString);

        CanaryConfiguration canaryConfiguration = CanaryConfiguration.fromMap(testConfigurationMap);

        assertThat(canaryConfiguration.getBootstrapServers(), is(bootstrapServer));
        assertThat(canaryConfiguration.getTopic(), is(topicName));
        assertThat(canaryConfiguration.getReconcileInterval(), is(reconciliationInterval));
        assertThat(canaryConfiguration.getClientId(), is(clientId));
        assertThat(canaryConfiguration.getConsumerGroupId(), is(consumerGroup));
        assertThat(canaryConfiguration.getProducerLatencyBuckets(), is(producerLatencyBucketsFloat));
        assertThat(canaryConfiguration.getEndToEndLatencyBuckets(), is(e2ELatencyBucketsFloat));
        assertThat(canaryConfiguration.getExpectedClusterSize(), is(expectedClusterSize));
        assertThat(canaryConfiguration.isTlsEnabled(), is(tlsEnabled));
        assertThat(canaryConfiguration.getTlsCaCert(), is(tlsCaCert));
        assertThat(canaryConfiguration.getTlsClientCert(), is(tlsClientCert));
        assertThat(canaryConfiguration.getTlsClientKey(), is(tlsClientKey));
        assertThat(canaryConfiguration.getSaslMechanism(), is(saslMechanism));
        assertThat(canaryConfiguration.getSaslUser(), is(saslUser));
        assertThat(canaryConfiguration.getSaslPassword(), is(saslPassword));
        assertThat(canaryConfiguration.getStatusCheckInterval(), is(statusCheckInterval));
        assertThat(canaryConfiguration.getStatusTimeWindow(), is(statusTimeWindow));
    }

    @Test
    void testSaslConfiguration() {
        String saslMechanism = SaslType.SCRAM_SHA_512.toString();

        Map<String, String> testCanaryConfiguration = new HashMap<>();
        testCanaryConfiguration.put(CanaryConstants.SASL_MECHANISM_ENV, saslMechanism);

        assertThrows(IllegalArgumentException.class, () -> CanaryConfiguration.fromMap(testCanaryConfiguration));

        String saslUser = "alice";
        testCanaryConfiguration.put(CanaryConstants.SASL_USER_ENV, saslUser);

        assertThrows(IllegalArgumentException.class, () -> CanaryConfiguration.fromMap(testCanaryConfiguration));

        String saslPassword = "alice-password";
        testCanaryConfiguration.put(CanaryConstants.SASL_PASSWORD_ENV, saslPassword);

        CanaryConfiguration canaryConfiguration = CanaryConfiguration.fromMap(testCanaryConfiguration);
        assertThat(canaryConfiguration.getSaslMechanism(), is(saslMechanism));
        assertThat(canaryConfiguration.getSaslUser(), is(saslUser));
        assertThat(canaryConfiguration.getSaslPassword(), is(saslPassword));
    }
}
