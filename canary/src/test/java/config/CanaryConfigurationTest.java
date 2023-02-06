/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package config;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class CanaryConfigurationTest {
    @Test
    void testDefaultCanaryConfiguration() {
        CanaryConfiguration canaryConfiguration = new CanaryConfiguration();
        float[] defaultProducerLatencyBuckets = {2.0F, 5.0F, 10.0F, 20.0F, 50.0F, 100.0F, 200.0F, 400.0F};
        float[] defaultE2ELatencyBuckets = {5.0F, 10.0F, 20.0F, 50.0F, 100.0F, 200.0F, 400.0F, 800.0F};
        float[] defaultConnectionCheckLatency = {100.0F, 200.0F, 400.0F, 800.0F, 1600.0F};

        assertThat(canaryConfiguration.getBootstrapServers(), is(CanaryConstants.BOOTSTRAP_SERVERS_DEFAULT));
        assertThat(canaryConfiguration.getBootstrapBackOffMaxAttempts(), is(CanaryConstants.BOOTSTRAP_BACKOFF_MAX_ATTEMPTS_DEFAULT));
        assertThat(canaryConfiguration.getBootstrapBackOffScale(), is(CanaryConstants.BOOTSTRAP_BACKOFF_SCALE_DEFAULT));
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
        assertThat(canaryConfiguration.isTlsInsecureSkipVerify(), is(CanaryConstants.TLS_INSECURE_SKIP_VERIFY_DEFAULT));
        assertThat(canaryConfiguration.getSaslMechanism(), is(""));
        assertThat(canaryConfiguration.getSaslUser(), is(""));
        assertThat(canaryConfiguration.getSaslPassword(), is(""));
        assertThat(canaryConfiguration.getConnectionCheckInterval(), is(CanaryConstants.CONNECTION_CHECK_INTERVAL_MS_DEFAULT));
        assertThat(canaryConfiguration.getConnectionCheckLatencyBuckets(), is(defaultConnectionCheckLatency));
        assertThat(canaryConfiguration.getStatusCheckInterval(), is(CanaryConstants.STATUS_CHECK_INTERVAL_MS_DEFAULT));
        assertThat(canaryConfiguration.getStatusTimeWindow(), is(CanaryConstants.STATUS_TIME_WINDOW_MS_DEFAULT));
    }

    @Test
    void testCustomCanaryConfiguration() {
        String bootstrapServer = "my-cluster-kafka-bootstrap:9092";
        int bootstrapBackoffMax = 3;
        Duration bootstrapBackOffScale = Duration.ofMillis(23);
        String topicName = "my-custom-topic-name";
        Duration reconciliationInterval = Duration.ofMillis(39);
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
        String saslMechanism = "SCRAM-SHA-512";
        String saslUser = "alice";
        String saslPassword = "in-wonderland";
        Duration connectionCheckInterval = Duration.ofMillis(123000);
        Duration statusCheckInterval = Duration.ofMillis(9999);
        Duration statusTimeWindow = Duration.ofMillis(65523);

        System.setProperty(CanaryConstants.BOOTSTRAP_SERVERS_ENV, bootstrapServer);
        System.setProperty(CanaryConstants.BOOTSTRAP_BACKOFF_MAX_ATTEMPTS_ENV, String.valueOf(bootstrapBackoffMax));
        System.setProperty(CanaryConstants.BOOTSTRAP_BACKOFF_SCALE_ENV, bootstrapBackOffScale.toString());
        System.setProperty(CanaryConstants.TOPIC_ENV, topicName);
        System.setProperty(CanaryConstants.RECONCILE_INTERVAL_ENV, reconciliationInterval.toString());
        System.setProperty(CanaryConstants.CLIENT_ID_ENV, clientId);
        System.setProperty(CanaryConstants.CONSUMER_GROUP_ID_ENV, consumerGroup);
        System.setProperty(CanaryConstants.PRODUCER_LATENCY_BUCKETS_ENV, producerLatencyBuckets);
        System.setProperty(CanaryConstants.ENDTOEND_LATENCY_BUCKETS_ENV, e2ELatencyBuckets);
        System.setProperty(CanaryConstants.EXPECTED_CLUSTER_SIZE_ENV, String.valueOf(expectedClusterSize));
        System.setProperty(CanaryConstants.TLS_ENABLED_ENV, String.valueOf(tlsEnabled));
        System.setProperty(CanaryConstants.TLS_CA_CERT_ENV, tlsCaCert);
        System.setProperty(CanaryConstants.TLS_CLIENT_CERT_ENV, tlsClientCert);
        System.setProperty(CanaryConstants.TLS_CLIENT_KEY_ENV, tlsClientKey);
        System.setProperty(CanaryConstants.SASL_MECHANISM_ENV, saslMechanism);
        System.setProperty(CanaryConstants.SASL_USER_ENV, saslUser);
        System.setProperty(CanaryConstants.SASL_PASSWORD_ENV, saslPassword);
        System.setProperty(CanaryConstants.CONNECTION_CHECK_INTERVAL_MS_ENV, connectionCheckInterval.toString());
        System.setProperty(CanaryConstants.CONNECTION_CHECK_LATENCY_BUCKETS_ENV, connectionCheckLatencyBuckets);
        System.setProperty(CanaryConstants.STATUS_CHECK_INTERVAL_MS_ENV, statusCheckInterval.toString());
        System.setProperty(CanaryConstants.STATUS_TIME_WINDOW_MS_ENV, statusTimeWindow.toString());

        CanaryConfiguration canaryConfiguration = new CanaryConfiguration();

        assertThat(canaryConfiguration.getBootstrapServers(), is(bootstrapServer));
        assertThat(canaryConfiguration.getBootstrapBackOffMaxAttempts(), is(bootstrapBackoffMax));
        assertThat(canaryConfiguration.getBootstrapBackOffScale(), is(bootstrapBackOffScale));
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
        assertThat(canaryConfiguration.getConnectionCheckInterval(), is(connectionCheckInterval));
        assertThat(canaryConfiguration.getConnectionCheckLatencyBuckets(), is(connectionCheckLatencyBucketsFloat));
        assertThat(canaryConfiguration.getStatusCheckInterval(), is(statusCheckInterval));
        assertThat(canaryConfiguration.getStatusTimeWindow(), is(statusTimeWindow));
    }
}
