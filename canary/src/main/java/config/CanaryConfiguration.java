/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package config;

import java.util.Arrays;
import java.util.Map;

import static config.CanaryConfigurationUtils.createLatencyBuckets;
import static config.CanaryConfigurationUtils.createTopicConfig;
import static config.CanaryConfigurationUtils.parseBooleanOrDefault;
import static config.CanaryConfigurationUtils.parseIntOrDefault;
import static config.CanaryConfigurationUtils.parseLongOrDefault;
import static config.CanaryConfigurationUtils.parseStringOrDefault;

public class CanaryConfiguration {
    private final String bootstrapServers;
    private final String topic;
    private final Map<String, String> topicConfig;
    private final long reconcileInterval;
    private final String clientId;
    private final String consumerGroupId;
    private final double[] producerLatencyBuckets;
    private final double[] endToEndLatencyBuckets;
    private final int expectedClusterSize;
    private final boolean tlsEnabled;
    private final String tlsCaCert;
    private final String tlsClientCert;
    private final String tlsClientKey;
    private final String saslMechanism;
    private final String saslUser;
    private final String saslPassword;
    private final long statusCheckInterval;
    private final long statusTimeWindow;

    @SuppressWarnings({"checkstyle:ParameterNumber"})
    public CanaryConfiguration(
        String bootstrapServers,
        String topic,
        Map<String, String> topicConfig,
        long reconcileInterval,
        String clientId,
        String consumerGroupId,
        double[] producerLatencyBuckets,
        double[] endToEndLatencyBuckets,
        int expectedClusterSize,
        boolean tlsEnabled,
        String tlsCaCert,
        String tlsClientCert,
        String tlsClientKey,
        String saslMechanism,
        String saslUser,
        String saslPassword,
        long statusCheckInterval,
        long statusTimeWindow
    ) {
        this.bootstrapServers = bootstrapServers;
        this.topic = topic;
        this.topicConfig = topicConfig;
        this.reconcileInterval = reconcileInterval;
        this.clientId = clientId;
        this.consumerGroupId = consumerGroupId;
        this.producerLatencyBuckets = producerLatencyBuckets;
        this.endToEndLatencyBuckets = endToEndLatencyBuckets;
        this.expectedClusterSize = expectedClusterSize;
        this.tlsEnabled = tlsEnabled;
        this.tlsCaCert = tlsCaCert;
        this.tlsClientCert = tlsClientCert;
        this.tlsClientKey = tlsClientKey;
        this.saslMechanism = saslMechanism;
        this.saslUser = saslUser;
        this.saslPassword = saslPassword;
        this.statusCheckInterval = statusCheckInterval;
        this.statusTimeWindow = statusTimeWindow;
    }

    public static CanaryConfiguration fromMap(Map<String, String> map) {

        String bootstrapServers = parseStringOrDefault(map.get(CanaryConstants.BOOTSTRAP_SERVERS_ENV), CanaryConstants.BOOTSTRAP_SERVERS_DEFAULT);
        String topic = parseStringOrDefault(map.get(CanaryConstants.TOPIC_ENV), CanaryConstants.TOPIC_DEFAULT);
        Map<String, String> topicConfig = createTopicConfig(parseStringOrDefault(map.get(CanaryConstants.TOPIC_CONFIG_ENV), ""));
        long reconcileInterval = parseLongOrDefault(map.get(CanaryConstants.RECONCILE_INTERVAL_ENV), CanaryConstants.RECONCILE_INTERVAL_DEFAULT);
        String clientId = parseStringOrDefault(map.get(CanaryConstants.CLIENT_ID_ENV), CanaryConstants.CLIENT_ID_DEFAULT);
        String consumerGroupId = parseStringOrDefault(map.get(CanaryConstants.CONSUMER_GROUP_ID_ENV), CanaryConstants.CONSUMER_GROUP_ID_DEFAULT);
        double[] producerLatencyBuckets = createLatencyBuckets(parseStringOrDefault(map.get(CanaryConstants.PRODUCER_LATENCY_BUCKETS_ENV), CanaryConstants.PRODUCER_LATENCY_BUCKETS_DEFAULT));
        double[] endToEndLatencyBuckets = createLatencyBuckets(parseStringOrDefault(map.get(CanaryConstants.ENDTOEND_LATENCY_BUCKETS_ENV), CanaryConstants.ENDTOEND_LATENCY_BUCKETS_DEFAULT));
        int expectedClusterSize = parseIntOrDefault(map.get(CanaryConstants.EXPECTED_CLUSTER_SIZE_ENV), CanaryConstants.EXPECTED_CLUSTER_SIZE_DEFAULT);
        boolean tlsEnabled = parseBooleanOrDefault(map.get(CanaryConstants.TLS_ENABLED_ENV), CanaryConstants.TLS_ENABLED_DEFAULT);
        String tlsCaCert = parseStringOrDefault(map.get(CanaryConstants.TLS_CA_CERT_ENV), "");
        String tlsClientCert = parseStringOrDefault(map.get(CanaryConstants.TLS_CLIENT_CERT_ENV), "");
        String tlsClientKey = parseStringOrDefault(map.get(CanaryConstants.TLS_CLIENT_KEY_ENV), "");
        String saslMechanism = parseStringOrDefault(map.get(CanaryConstants.SASL_MECHANISM_ENV), "");
        String saslUser = parseStringOrDefault(map.get(CanaryConstants.SASL_USER_ENV), "");
        String saslPassword = parseStringOrDefault(map.get(CanaryConstants.SASL_PASSWORD_ENV), "");
        long statusCheckInterval = parseLongOrDefault(map.get(CanaryConstants.STATUS_CHECK_INTERVAL_MS_ENV), CanaryConstants.STATUS_CHECK_INTERVAL_MS_DEFAULT);
        long statusTimeWindow = parseLongOrDefault(map.get(CanaryConstants.STATUS_TIME_WINDOW_MS_ENV), CanaryConstants.STATUS_TIME_WINDOW_MS_DEFAULT);

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
            topic,
            topicConfig,
            reconcileInterval,
            clientId,
            consumerGroupId,
            producerLatencyBuckets,
            endToEndLatencyBuckets,
            expectedClusterSize,
            tlsEnabled,
            tlsCaCert,
            tlsClientCert,
            tlsClientKey,
            saslMechanism,
            saslUser,
            saslPassword,
            statusCheckInterval,
            statusTimeWindow
        );
    }

    public String getBootstrapServers() {
        return bootstrapServers;
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

    public double[] getProducerLatencyBuckets() {
        return producerLatencyBuckets;
    }

    public double[] getEndToEndLatencyBuckets() {
        return endToEndLatencyBuckets;
    }

    public int getExpectedClusterSize() {
        return expectedClusterSize;
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

    public String getSaslMechanism() {
        return saslMechanism;
    }

    public String getSaslUser() {
        return saslUser;
    }

    public String getSaslPassword() {
        return saslPassword;
    }

    public long getStatusCheckInterval() {
        return statusCheckInterval;
    }

    public long getStatusTimeWindow() {
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
            ", topic=" + getTopic() +
            ", topicConfig='" + getTopicConfig() + '\'' +
            ", reconcileInterval='" + getReconcileInterval() + '\'' +
            ", clientId='" + getClientId() + '\'' +
            ", consumerGroupId='" + getConsumerGroupId() + '\'' +
            ", producerLatencyBuckets='" + Arrays.toString(getProducerLatencyBuckets()) + '\'' +
            ", endToEndLatencyBuckets='" + Arrays.toString(getEndToEndLatencyBuckets()) + '\'' +
            ", expectedClusterSize='" + getExpectedClusterSize() + '\'' +
            ", tlsEnabled='" + isTlsEnabled() + '\'' +
            ", tlsCaCert='" + tlsCaCert + '\'' +
            ", tlsClientCert='" + tlsClientCert + '\'' +
            ", tlsClientKey='" + tlsClientKey + '\'' +
            ", saslMechanism='" + getSaslMechanism() + '\'' +
            ", saslUser='" + saslUser + '\'' +
            ", saslPassword='" + saslPassword + '\'' +
            ", statusCheckInterval='" + getStatusCheckInterval() + '\'' +
            ", statusTimeWindow='" + getStatusTimeWindow() + '\'' +
            '}';
    }
}
