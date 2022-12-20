/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package config;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static common.Environment.*;

public class CanaryConfiguration {
    private final String[] bootstrapServers;
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
    // TODO: maybe not needed
    private final String dynamicConfigFile;
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
    // TODO: maybe not needed
    private final Duration dynamicConfigWatcherInterval;

    public CanaryConfiguration() {
        this.bootstrapServers = getStringOrDefault(CanaryConstants.BOOTSTRAP_SERVERS_ENV, CanaryConstants.BOOTSTRAP_SERVERS_DEFAULT).split(",");
        this.bootstrapBackOffMaxAttempts = getIntOrDefault(CanaryConstants.BOOTSTRAP_BACKOFF_MAX_ATTEMPTS_ENV, CanaryConstants.BOOTSTRAP_BACKOFF_MAX_ATTEMPTS_DEFAULT);
        this.bootstrapBackOffScale = getDurationOrDefault(CanaryConstants.BOOTSTRAP_BACKOFF_SCALE_ENV, CanaryConstants.BOOTSTRAP_BACKOFF_SCALE_DEFAULT);
        this.topic = getStringOrDefault(CanaryConstants.TOPIC_ENV, CanaryConstants.TOPIC_DEFAULT);
        this.topicConfig = createTopicConfig(getStringOrDefault(CanaryConstants.TOPIC_CONFIG_ENV, ""));
        this.reconcileInterval = getLongOrDefault(CanaryConstants.RECONCILE_INTERVAL_ENV, CanaryConstants.RECONCILE_INTERVAL_DEFAULT);
        this.clientId = getStringOrDefault(CanaryConstants.CLIENT_ID_ENV, CanaryConstants.CLIENT_ID_DEFAULT);
        this.consumerGroupId = getStringOrDefault(CanaryConstants.CONSUMER_GROUP_ID_ENV, CanaryConstants.CONSUMER_GROUP_ID_DEFAULT);
        this.producerLatencyBuckets = createLatencyBuckets(getStringOrDefault(CanaryConstants.PRODUCER_LATENCY_BUCKETS_ENV, CanaryConstants.PRODUCER_LATENCY_BUCKETS_DEFAULT));
        this.endToEndLatencyBuckets = createLatencyBuckets(getStringOrDefault(CanaryConstants.ENDTOEND_LATENCY_BUCKETS_ENV, CanaryConstants.ENDTOEND_LATENCY_BUCKETS_DEFAULT));
        this.expectedClusterSize = getIntOrDefault(CanaryConstants.EXPECTED_CLUSTER_SIZE_ENV, CanaryConstants.EXPECTED_CLUSTER_SIZE_DEFAULT);
        this.kafkaVersion = getStringOrDefault(CanaryConstants.KAFKA_VERSION_ENV, CanaryConstants.KAFKA_VERSION_DEFAULT);
        this.dynamicConfigFile = getStringOrDefault(CanaryConstants.DYNAMIC_CONFIG_FILE_ENV, "");
        this.tlsEnabled = getBooleanOrDefault(CanaryConstants.TLS_ENABLED_ENV, CanaryConstants.TLS_ENABLED_DEFAULT);
        this.tlsCaCert = getStringOrDefault(CanaryConstants.TLS_CA_CERT_ENV, "");
        this.tlsClientCert = getStringOrDefault(CanaryConstants.TLS_CLIENT_CERT_ENV, "");
        this.tlsClientKey = getStringOrDefault(CanaryConstants.TLS_CLIENT_KEY_ENV, "");
        this.tlsInsecureSkipVerify = getBooleanOrDefault(CanaryConstants.TLS_INSECURE_SKIP_VERIFY_ENV, CanaryConstants.TLS_INSECURE_SKIP_VERIFY_DEFAULT);
        this.saslMechanism = getStringOrDefault(CanaryConstants.SASL_MECHANISM_ENV, "");
        this.saslUser = getStringOrDefault(CanaryConstants.SASL_USER_ENV, "");
        this.saslPassword = getStringOrDefault(CanaryConstants.SASL_PASSWORD_ENV, "");
        this.connectionCheckInterval = getDurationOrDefault(CanaryConstants.CONNECTION_CHECK_INTERVAL_MS_ENV, CanaryConstants.CONNECTION_CHECK_INTERVAL_MS_DEFAULT);
        this.connectionCheckLatencyBuckets = createLatencyBuckets(getStringOrDefault(CanaryConstants.CONNECTION_CHECK_LATENCY_BUCKETS_ENV, CanaryConstants.CONNECTION_CHECK_LATENCY_BUCKETS_DEFAULT));
        this.statusCheckInterval = getDurationOrDefault(CanaryConstants.STATUS_CHECK_INTERVAL_MS_ENV, CanaryConstants.STATUS_CHECK_INTERVAL_MS_DEFAULT);
        this.statusTimeWindow = getDurationOrDefault(CanaryConstants.STATUS_TIME_WINDOW_MS_ENV, CanaryConstants.STATUS_TIME_WINDOW_MS_DEFAULT);
        this.dynamicConfigWatcherInterval = getDurationOrDefault(CanaryConstants.DYNAMIC_CONFIG_WATCHER_INTERVAL_ENV, CanaryConstants.DYNAMIC_CONFIG_WATCHER_INTERVAL_DEFAULT);
    }

    private static Map<String, String> createTopicConfig(String topicConfig) {
        if (topicConfig.length() == 0) {
            return null;
        }

        Map<String, String> topicConfigMap = new HashMap<>();
        String[] keyPairs = topicConfig.split(";");

        for (String keyPair : keyPairs) {
            // TODO: this should also check, if we are at the end of the array
            if (keyPair.length() == 0) {
                break;
            }

            String[] keyAndValue = keyPair.split("=");

            // key has to be not empty and the whole keyAndValue should contain 2 records (key, value)
            if (keyAndValue.length != 2 || keyAndValue[0].length() == 0) {
                throw new IllegalArgumentException(String.format("Error parsing topic configuration - %s: %s is not a valid key-value pair", topicConfig, keyPair));
            }

            topicConfigMap.put(keyAndValue[0], keyAndValue[1]);
        }

        return topicConfigMap;
    }

    private static float[] createLatencyBuckets(String latencyBuckets) {
        String[] values = latencyBuckets.split(",");
        float[] latencyBucketsArr = {};

        // TODO: maybe some more intelligent way how to do this
        int index = 0;
        for (String value : values) {
            // the empty value should not be parsed
            if (value.length() != 0) {
                latencyBucketsArr[index] = Float.parseFloat(value);
                index++;
            }
        }

        return latencyBucketsArr;
    }


    public String[] getBootstrapServers() {
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

    public String getDynamicConfigFile() {
        return dynamicConfigFile;
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

    public Duration getDynamicConfigWatcherInterval() {
        return dynamicConfigWatcherInterval;
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
            "bootstrapServers='" + Arrays.toString(getBootstrapServers()) + '\'' +
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
            ", dynamicConfigFile='" + getDynamicConfigFile() + '\'' +
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
            ", dynamicConfigWatcherInterval='" + getDynamicConfigWatcherInterval() + '\'' +
            '}';
    }
}
