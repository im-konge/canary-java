/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package config;

import common.Environment;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;

public class CanaryConfiguration {
    private final String bootstrapServers;
    private final int bootstrapBackOffMaxAttempts;
    private final Duration bootstrapBackOffScale;
    private final String topic;
    private final Map<String, String> topicConfig;
    private final long reconcileInterval;
    private final String clientId;
    private final String consumerGroupId;
    private final float[] producerLatencyBuckets;
    private final float[] endToEndLatencyBuckets;
    private final int expectedClusterSize;
    private final String kafkaVersion;
    private final boolean tlsEnabled;
    private final String tlsCaCert;
    private final String tlsClientCert;
    private final String tlsClientKey;
    private final boolean tlsInsecureSkipVerify;
    private final String saslMechanism;
    private final String saslUser;
    private final String saslPassword;
    private final Duration connectionCheckInterval;
    private final float[] connectionCheckLatencyBuckets;
    private final Duration statusCheckInterval;
    private final Duration statusTimeWindow;

    public CanaryConfiguration() {
        this.bootstrapServers = Environment.getStringOrDefault(CanaryConstants.BOOTSTRAP_SERVERS_ENV, CanaryConstants.BOOTSTRAP_SERVERS_DEFAULT);
        this.bootstrapBackOffMaxAttempts = Environment.getIntOrDefault(CanaryConstants.BOOTSTRAP_BACKOFF_MAX_ATTEMPTS_ENV, CanaryConstants.BOOTSTRAP_BACKOFF_MAX_ATTEMPTS_DEFAULT);
        this.bootstrapBackOffScale = Environment.getDurationOrDefault(CanaryConstants.BOOTSTRAP_BACKOFF_SCALE_ENV, CanaryConstants.BOOTSTRAP_BACKOFF_SCALE_DEFAULT);
        this.topic = Environment.getStringOrDefault(CanaryConstants.TOPIC_ENV, CanaryConstants.TOPIC_DEFAULT);
        this.topicConfig = CanaryConfigurationUtils.createTopicConfig(Environment.getStringOrDefault(CanaryConstants.TOPIC_CONFIG_ENV, ""));
        this.reconcileInterval = Environment.getLongOrDefault(CanaryConstants.RECONCILE_INTERVAL_ENV, CanaryConstants.RECONCILE_INTERVAL_DEFAULT);
        this.clientId = Environment.getStringOrDefault(CanaryConstants.CLIENT_ID_ENV, CanaryConstants.CLIENT_ID_DEFAULT);
        this.consumerGroupId = Environment.getStringOrDefault(CanaryConstants.CONSUMER_GROUP_ID_ENV, CanaryConstants.CONSUMER_GROUP_ID_DEFAULT);
        this.producerLatencyBuckets = CanaryConfigurationUtils.createLatencyBuckets(Environment.getStringOrDefault(CanaryConstants.PRODUCER_LATENCY_BUCKETS_ENV, CanaryConstants.PRODUCER_LATENCY_BUCKETS_DEFAULT));
        this.endToEndLatencyBuckets = CanaryConfigurationUtils.createLatencyBuckets(Environment.getStringOrDefault(CanaryConstants.ENDTOEND_LATENCY_BUCKETS_ENV, CanaryConstants.ENDTOEND_LATENCY_BUCKETS_DEFAULT));
        this.expectedClusterSize = Environment.getIntOrDefault(CanaryConstants.EXPECTED_CLUSTER_SIZE_ENV, CanaryConstants.EXPECTED_CLUSTER_SIZE_DEFAULT);
        this.kafkaVersion = Environment.getStringOrDefault(CanaryConstants.KAFKA_VERSION_ENV, CanaryConstants.KAFKA_VERSION_DEFAULT);
        this.tlsEnabled = Environment.getBooleanOrDefault(CanaryConstants.TLS_ENABLED_ENV, CanaryConstants.TLS_ENABLED_DEFAULT);
        this.tlsCaCert = Environment.getStringOrDefault(CanaryConstants.TLS_CA_CERT_ENV, "");
        this.tlsClientCert = Environment.getStringOrDefault(CanaryConstants.TLS_CLIENT_CERT_ENV, "");
        this.tlsClientKey = Environment.getStringOrDefault(CanaryConstants.TLS_CLIENT_KEY_ENV, "");
        this.tlsInsecureSkipVerify = Environment.getBooleanOrDefault(CanaryConstants.TLS_INSECURE_SKIP_VERIFY_ENV, CanaryConstants.TLS_INSECURE_SKIP_VERIFY_DEFAULT);
        this.saslMechanism = Environment.getStringOrDefault(CanaryConstants.SASL_MECHANISM_ENV, "");
        this.saslUser = Environment.getStringOrDefault(CanaryConstants.SASL_USER_ENV, "");
        this.saslPassword = Environment.getStringOrDefault(CanaryConstants.SASL_PASSWORD_ENV, "");
        this.connectionCheckInterval = Environment.getDurationOrDefault(CanaryConstants.CONNECTION_CHECK_INTERVAL_MS_ENV, CanaryConstants.CONNECTION_CHECK_INTERVAL_MS_DEFAULT);
        this.connectionCheckLatencyBuckets = CanaryConfigurationUtils.createLatencyBuckets(Environment.getStringOrDefault(CanaryConstants.CONNECTION_CHECK_LATENCY_BUCKETS_ENV, CanaryConstants.CONNECTION_CHECK_LATENCY_BUCKETS_DEFAULT));
        this.statusCheckInterval = Environment.getDurationOrDefault(CanaryConstants.STATUS_CHECK_INTERVAL_MS_ENV, CanaryConstants.STATUS_CHECK_INTERVAL_MS_DEFAULT);
        this.statusTimeWindow = Environment.getDurationOrDefault(CanaryConstants.STATUS_TIME_WINDOW_MS_ENV, CanaryConstants.STATUS_TIME_WINDOW_MS_DEFAULT);

        // check if username and password is specified in case that SASL mechanism isn't empty
        if (!this.saslMechanism.isEmpty()) {
            if (this.saslUser.isEmpty()) {
                throw new IllegalArgumentException("SASL user must be specified");
            }
            if (this.saslPassword.isEmpty()) {
                throw new IllegalArgumentException("SASL password must be specified");
            }
        }
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public int getBootstrapBackOffMaxAttempts() {
        return bootstrapBackOffMaxAttempts;
    }

    public Duration getBootstrapBackOffScale() {
        return bootstrapBackOffScale;
    }

    public String getTopic() {
        return topic;
    }

    public Map<String, String> getTopicConfig() {
        return topicConfig;
    }

    public long getReconcileInterval() {
        return reconcileInterval;
    }

    public String getClientId() {
        return clientId;
    }

    public String getConsumerGroupId() {
        return consumerGroupId;
    }

    public float[] getProducerLatencyBuckets() {
        return producerLatencyBuckets;
    }

    public float[] getEndToEndLatencyBuckets() {
        return endToEndLatencyBuckets;
    }

    public int getExpectedClusterSize() {
        return expectedClusterSize;
    }

    public String getKafkaVersion() {
        return kafkaVersion;
    }

    public boolean isTlsEnabled() {
        return tlsEnabled;
    }

    public String getTlsCaCert() {
        return tlsCaCert;
    }

    public String getTlsClientCert() {
        return tlsClientCert;
    }

    public String getTlsClientKey() {
        return tlsClientKey;
    }

    public boolean isTlsInsecureSkipVerify() {
        return tlsInsecureSkipVerify;
    }

    public String getSaslMechanism() {
        return saslMechanism;
    }

    public String getSaslUser() {
        return saslUser;
    }

    public String getSaslPassword() {
        return saslPassword;
    }

    public Duration getConnectionCheckInterval() {
        return connectionCheckInterval;
    }

    public float[] getConnectionCheckLatencyBuckets() {
        return connectionCheckLatencyBuckets;
    }

    public Duration getStatusCheckInterval() {
        return statusCheckInterval;
    }

    public Duration getStatusTimeWindow() {
        return statusTimeWindow;
    }

    @Override
    public String toString() {
        String tlsCaCert = getTlsCaCert().equals("") ? "" : "[CA cert]";
        String tlsClientCert = getTlsClientCert().equals("") ? "" : "[Client cert]";
        String tlsClientKey = getTlsClientKey().equals("") ? "" : "[Client key]";

        String saslUser = "";
        String saslPassword = "";

        if (getSaslMechanism().equals("PLAIN")) {
            saslUser = getSaslUser().equals("") ? "" : "[SASL user]";
            saslPassword = getSaslPassword().equals("") ? "" : "[SASL password]";
        }

        return "CanaryConfiguration{" +
            "bootstrapServers='" + getBootstrapServers() + '\'' +
            ", bootstrapBackOffMaxAttempts='" + getBootstrapBackOffMaxAttempts() + '\'' +
            ", bootstrapBackOffScale=" + getBootstrapBackOffScale() +
            ", topic=" + getTopic() +
            ", topicConfig='" + getTopicConfig() + '\'' +
            ", reconcileInterval='" + getReconcileInterval() + '\'' +
            ", clientId='" + getClientId() + '\'' +
            ", consumerGroupId='" + getConsumerGroupId() + '\'' +
            ", producerLatencyBuckets='" + Arrays.toString(getProducerLatencyBuckets()) + '\'' +
            ", endToEndLatencyBuckets='" + Arrays.toString(getEndToEndLatencyBuckets()) + '\'' +
            ", expectedClusterSize='" + getExpectedClusterSize() + '\'' +
            ", kafkaVersion='" + getKafkaVersion() + '\'' +
            ", tlsEnabled='" + isTlsEnabled() + '\'' +
            ", tlsCaCert='" + tlsCaCert + '\'' +
            ", tlsClientCert='" + tlsClientCert + '\'' +
            ", tlsClientKey='" + tlsClientKey + '\'' +
            ", tlsInsecureSkipVerify='" + isTlsInsecureSkipVerify() + '\'' +
            ", saslMechanism='" + getSaslMechanism() + '\'' +
            ", saslUser='" + saslUser + '\'' +
            ", saslPassword='" + saslPassword + '\'' +
            ", connectionCheckInterval='" + getConnectionCheckInterval() + '\'' +
            ", connectionCheckLatencyBuckets='" + Arrays.toString(getConnectionCheckLatencyBuckets()) + '\'' +
            ", statusCheckInterval='" + getStatusCheckInterval() + '\'' +
            ", statusTimeWindow='" + getStatusTimeWindow() + '\'' +
            '}';
    }
}
