/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package config;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;

import static config.CanaryConfigurationUtils.createLatencyBuckets;
import static config.CanaryConfigurationUtils.createTopicConfig;
import static config.CanaryConfigurationUtils.parseBooleanOrDefault;
import static config.CanaryConfigurationUtils.parseDurationOrDefault;
import static config.CanaryConfigurationUtils.parseIntOrDefault;
import static config.CanaryConfigurationUtils.parseLongOrDefault;
import static config.CanaryConfigurationUtils.parseStringOrDefault;

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

    @SuppressWarnings({"checkstyle:ParameterNumber"})
    public CanaryConfiguration(
        String bootstrapServers,
        int bootstrapBackOffMaxAttempts,
        Duration bootstrapBackOffScale,
        String topic,
        Map<String, String> topicConfig,
        long reconcileInterval,
        String clientId,
        String consumerGroupId,
        float[] producerLatencyBuckets,
        float[] endToEndLatencyBuckets,
        int expectedClusterSize,
        String kafkaVersion,
        boolean tlsEnabled,
        String tlsCaCert,
        String tlsClientCert,
        String tlsClientKey,
        boolean tlsInsecureSkipVerify,
        String saslMechanism,
        String saslUser,
        String saslPassword,
        Duration connectionCheckInterval,
        float[] connectionCheckLatencyBuckets,
        Duration statusCheckInterval,
        Duration statusTimeWindow
    ) {
        this.bootstrapServers = bootstrapServers;
        this.bootstrapBackOffMaxAttempts = bootstrapBackOffMaxAttempts;
        this.bootstrapBackOffScale = bootstrapBackOffScale;
        this.topic = topic;
        this.topicConfig = topicConfig;
        this.reconcileInterval = reconcileInterval;
        this.clientId = clientId;
        this.consumerGroupId = consumerGroupId;
        this.producerLatencyBuckets = producerLatencyBuckets;
        this.endToEndLatencyBuckets = endToEndLatencyBuckets;
        this.expectedClusterSize = expectedClusterSize;
        this.kafkaVersion = kafkaVersion;
        this.tlsEnabled = tlsEnabled;
        this.tlsCaCert = tlsCaCert;
        this.tlsClientCert = tlsClientCert;
        this.tlsClientKey = tlsClientKey;
        this.tlsInsecureSkipVerify = tlsInsecureSkipVerify;
        this.saslMechanism = saslMechanism;
        this.saslUser = saslUser;
        this.saslPassword = saslPassword;
        this.connectionCheckInterval = connectionCheckInterval;
        this.connectionCheckLatencyBuckets = connectionCheckLatencyBuckets;
        this.statusCheckInterval = statusCheckInterval;
        this.statusTimeWindow = statusTimeWindow;
    }

    public static CanaryConfiguration fromMap(Map<String, String> map) {

        String bootstrapServers = parseStringOrDefault(map.get(CanaryConstants.BOOTSTRAP_SERVERS_ENV), CanaryConstants.BOOTSTRAP_SERVERS_DEFAULT);
        int bootstrapBackOffMaxAttempts = parseIntOrDefault(map.get(CanaryConstants.BOOTSTRAP_BACKOFF_MAX_ATTEMPTS_ENV), CanaryConstants.BOOTSTRAP_BACKOFF_MAX_ATTEMPTS_DEFAULT);
        Duration bootstrapBackOffScale = parseDurationOrDefault(map.get(CanaryConstants.BOOTSTRAP_BACKOFF_SCALE_ENV), CanaryConstants.BOOTSTRAP_BACKOFF_SCALE_DEFAULT);
        String topic = parseStringOrDefault(map.get(CanaryConstants.TOPIC_ENV), CanaryConstants.TOPIC_DEFAULT);
        Map<String, String> topicConfig = createTopicConfig(parseStringOrDefault(map.get(CanaryConstants.TOPIC_CONFIG_ENV), ""));
        long reconcileInterval = parseLongOrDefault(map.get(CanaryConstants.RECONCILE_INTERVAL_ENV), CanaryConstants.RECONCILE_INTERVAL_DEFAULT);
        String clientId = parseStringOrDefault(map.get(CanaryConstants.CLIENT_ID_ENV), CanaryConstants.CLIENT_ID_DEFAULT);
        String consumerGroupId = parseStringOrDefault(map.get(CanaryConstants.CONSUMER_GROUP_ID_ENV), CanaryConstants.CONSUMER_GROUP_ID_DEFAULT);
        float[] producerLatencyBuckets = createLatencyBuckets(parseStringOrDefault(map.get(CanaryConstants.PRODUCER_LATENCY_BUCKETS_ENV), CanaryConstants.PRODUCER_LATENCY_BUCKETS_DEFAULT));
        float[] endToEndLatencyBuckets = createLatencyBuckets(parseStringOrDefault(map.get(CanaryConstants.ENDTOEND_LATENCY_BUCKETS_ENV), CanaryConstants.ENDTOEND_LATENCY_BUCKETS_DEFAULT));
        int expectedClusterSize = parseIntOrDefault(map.get(CanaryConstants.EXPECTED_CLUSTER_SIZE_ENV), CanaryConstants.EXPECTED_CLUSTER_SIZE_DEFAULT);
        String kafkaVersion = parseStringOrDefault(map.get(CanaryConstants.KAFKA_VERSION_ENV), CanaryConstants.KAFKA_VERSION_DEFAULT);
        boolean tlsEnabled = parseBooleanOrDefault(map.get(CanaryConstants.TLS_ENABLED_ENV), CanaryConstants.TLS_ENABLED_DEFAULT);
        String tlsCaCert = parseStringOrDefault(map.get(CanaryConstants.TLS_CA_CERT_ENV), "");
        String tlsClientCert = parseStringOrDefault(map.get(CanaryConstants.TLS_CLIENT_CERT_ENV), "");
        String tlsClientKey = parseStringOrDefault(map.get(CanaryConstants.TLS_CLIENT_KEY_ENV), "");
        boolean tlsInsecureSkipVerify = parseBooleanOrDefault(map.get(CanaryConstants.TLS_INSECURE_SKIP_VERIFY_ENV), CanaryConstants.TLS_INSECURE_SKIP_VERIFY_DEFAULT);
        String saslMechanism = parseStringOrDefault(map.get(CanaryConstants.SASL_MECHANISM_ENV), "");
        String saslUser = parseStringOrDefault(map.get(CanaryConstants.SASL_USER_ENV), "");
        String saslPassword = parseStringOrDefault(map.get(CanaryConstants.SASL_PASSWORD_ENV), "");
        Duration connectionCheckInterval = parseDurationOrDefault(map.get(CanaryConstants.CONNECTION_CHECK_INTERVAL_MS_ENV), CanaryConstants.CONNECTION_CHECK_INTERVAL_MS_DEFAULT);
        float[] connectionCheckLatencyBuckets = createLatencyBuckets(parseStringOrDefault(map.get(CanaryConstants.CONNECTION_CHECK_LATENCY_BUCKETS_ENV), CanaryConstants.CONNECTION_CHECK_LATENCY_BUCKETS_DEFAULT));
        Duration statusCheckInterval = parseDurationOrDefault(map.get(CanaryConstants.STATUS_CHECK_INTERVAL_MS_ENV), CanaryConstants.STATUS_CHECK_INTERVAL_MS_DEFAULT);
        Duration statusTimeWindow = parseDurationOrDefault(map.get(CanaryConstants.STATUS_TIME_WINDOW_MS_ENV), CanaryConstants.STATUS_TIME_WINDOW_MS_DEFAULT);

        // check if username and password is specified in case that SASL mechanism isn't empty
        if (!saslMechanism.isEmpty()) {
            if (saslUser.isEmpty()) {
                throw new IllegalArgumentException("SASL user must be specified");
            }
            if (saslPassword.isEmpty()) {
                throw new IllegalArgumentException("SASL password must be specified");
            }
        }

        return new CanaryConfiguration(
            bootstrapServers,
            bootstrapBackOffMaxAttempts,
            bootstrapBackOffScale,
            topic,
            topicConfig,
            reconcileInterval,
            clientId,
            consumerGroupId,
            producerLatencyBuckets,
            endToEndLatencyBuckets,
            expectedClusterSize,
            kafkaVersion,
            tlsEnabled,
            tlsCaCert,
            tlsClientCert,
            tlsClientKey,
            tlsInsecureSkipVerify,
            saslMechanism,
            saslUser,
            saslPassword,
            connectionCheckInterval,
            connectionCheckLatencyBuckets,
            statusCheckInterval,
            statusTimeWindow
        );
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
