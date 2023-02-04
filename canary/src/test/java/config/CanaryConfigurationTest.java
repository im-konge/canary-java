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

    }
}
