/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package common.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MetricsRegistry {
    private static MetricsRegistry instance = null;
    private final PrometheusMeterRegistry prometheusMeterRegistry;
    private static final String METRICS_PREFIX = "strimzi_canary_";

    private final Map<String, Counter> recordsProducedTotal = new ConcurrentHashMap<>(1);
    private final Map<String, Counter> clientCreationErrorTotal = new ConcurrentHashMap<>(1);
    private final Map<String, Counter> expectedClusterSizeErrorTotal = new ConcurrentHashMap<>(1);
    private final Map<String, Counter> topicCreationFailedTotal = new ConcurrentHashMap<>(1);
    private final Map<String, Counter> describeClusterErrorTotal = new ConcurrentHashMap<>(1);
    private final Map<String, Counter> topicDescribeErrorTotal = new ConcurrentHashMap<>(1);
    private final Map<String, Counter> recordsProducedFailedTotal = new ConcurrentHashMap<>(1);
    private final Map<String, Counter> producerRefreshMetadataErrorTotal = new ConcurrentHashMap<>(1);
//    private final Map<String, Timer> recordsProducedLatency = new ConcurrentHashMap<>(1);
    private final Map<String, Counter> recordsConsumedTotal = new ConcurrentHashMap<>(1);
    private final Map<String, Counter> consumerErrorTotal = new ConcurrentHashMap<>(1);
    private final Map<String, Counter> consumerRefreshMetadataErrorTotal = new ConcurrentHashMap<>(1);
//    private final Map<String, Timer> recordsConsumedLatency = new ConcurrentHashMap<>(1);
    private final Map<String, Counter> connectionErrorTotal = new ConcurrentHashMap<>(1);
//    private final Map<String, Timer> connectionLatency = new ConcurrentHashMap<>(1);

    private MetricsRegistry(PrometheusMeterRegistry prometheusMeterRegistry) {
        this.prometheusMeterRegistry = prometheusMeterRegistry;
    }

    public static MetricsRegistry getInstance() {
        if (instance == null) {
            instance = new MetricsRegistry(new PrometheusMeterRegistry(PrometheusConfig.DEFAULT));
        }
        return instance;
    }

    public PrometheusMeterRegistry getPrometheusMeterRegistry() {
        return prometheusMeterRegistry;
    }

    public Counter getRecordsProducedTotal(String clientId, int partition) {
        String metricName = METRICS_PREFIX + "records_produced_total";
        Tags tags = Tags.of(Tag.of("clientid", clientId), Tag.of("partition", String.valueOf(partition)));
        String description = "The total number of records produced";
        String key = metricName + "," + tags;

        return recordsProducedTotal.computeIfAbsent(key, func -> counter(metricName, description, tags));
    }

    public Counter getClientCreationErrorTotal() {
        String metricName = METRICS_PREFIX + "client_creation_error_total";
        String description = "Total number of errors while creating Kafka producer, consumer or admin";

        return clientCreationErrorTotal.computeIfAbsent(metricName, func -> counter(metricName, description, null));
    }

    public Counter getExpectedClusterSizeErrorTotal() {
        String metricName = METRICS_PREFIX + "expected_cluster_size_error_total";
        String description = "Total number of errors while waiting for Kafka cluster having the expected size";

        return expectedClusterSizeErrorTotal.computeIfAbsent(metricName, func -> counter(metricName, description, null));
    }

    public Counter getTopicCreationFailedTotal(String topicName) {
        String metricName = METRICS_PREFIX + "topic_creation_failed_total";
        Tags tags = Tags.of(Tag.of("topic", topicName));
        String description = "Total number of errors while creating the canary topic";
        String key = metricName + "," + tags;

        return topicCreationFailedTotal.computeIfAbsent(key, func -> counter(metricName, description, tags));
    }

    public Counter getDescribeClusterErrorTotal() {
        String metricName = METRICS_PREFIX + "describe_cluster_error_total";
        String description = "Total number of errors while describing cluster";

        return describeClusterErrorTotal.computeIfAbsent(metricName, func -> counter(metricName, description, null));
    }

    public Counter getTopicDescribeErrorTotal(String topicName) {
        String metricName = METRICS_PREFIX + "topic_describe_error_total";
        Tags tags = Tags.of(Tag.of("topic", topicName));
        String description = "Total number of errors while getting Canary topic metadata";
        String key = metricName + "," + tags;

        return topicDescribeErrorTotal.computeIfAbsent(key, func -> counter(metricName, description, tags));
    }

    public Counter getRecordsProducedFailedTotal(String clientId, int partition) {
        String metricName = METRICS_PREFIX + "records_produced_failed_total";
        Tags tags = Tags.of(Tag.of("clientid", clientId), Tag.of("partition", String.valueOf(partition)));
        String description = "The total number of records failed to produce";
        String key = metricName + "," + tags;

        return recordsProducedFailedTotal.computeIfAbsent(key, func -> counter(metricName, description, tags));
    }

    public Counter getProducerRefreshMetadataErrorTotal(String clientId) {
        String metricName = METRICS_PREFIX + "producer_refresh_metadata_error_total";
        Tags tags = Tags.of(Tag.of("clientid", clientId));
        String description = "Total number of errors while refreshing producer metadata";
        String key = metricName + "," + tags;

        return producerRefreshMetadataErrorTotal.computeIfAbsent(key, func -> counter(metricName, description, tags));
    }

//    public Timer getRecordsProducedLatency(String clientId, int partition) {
//        String metricName = METRICS_PREFIX + "records_produced_latency";
//        Tags tags = Tags.of(Tag.of("clientid", clientId), Tag.of("partition", String.valueOf(partition)));
//        String description = "Records produced latency in milliseconds";
//        String key = metricName + "," + tags;
//
//        return recordsProducedLatency.computeIfAbsent(key, func -> timer(metricName, description, tags));
//    }

    public Counter getRecordsConsumedTotal(String clientId, int partition) {
        String metricName = METRICS_PREFIX + "records_consumed_total";
        Tags tags = Tags.of(Tag.of("clientid", clientId), Tag.of("partition", String.valueOf(partition)));
        String description = "The total number of records consumed";
        String key = metricName + "," + tags;

        return recordsConsumedTotal.computeIfAbsent(key, func -> counter(metricName, description, tags));
    }

    public Counter getConsumerErrorTotal(String clientId) {
        String metricName = METRICS_PREFIX + "consumer_error_total";
        Tags tags = Tags.of(Tag.of("clientid", clientId));
        String description = "Total number of errors reported by the consumer";
        String key = metricName + "," + tags;

        return consumerErrorTotal.computeIfAbsent(key, func -> counter(metricName, description, tags));
    }

    public Counter getConsumerRefreshMetadataErrorTotal(String clientId) {
        String metricName = METRICS_PREFIX + "consumer_refresh_metadata_error_total";
        Tags tags = Tags.of(Tag.of("clientid", clientId));
        String description = "Total number of errors while refreshing consumer metadata";
        String key = metricName + "," + tags;

        return consumerRefreshMetadataErrorTotal.computeIfAbsent(key, func -> counter(metricName, description, tags));
    }

//    public Timer getRecordsConsumedLatency(String clientId, int partition) {
//        String metricName = METRICS_PREFIX + "records_consumed_latency";
//        Tags tags = Tags.of(Tag.of("clientid", clientId), Tag.of("partition", String.valueOf(partition)));
//        String description = "Records end-to-end latency in milliseconds";
//        String key = metricName + "," + tags;
//
//        return recordsConsumedLatency.computeIfAbsent(key, func -> timer(metricName, description, tags));
//    }

    public Counter getConnectionErrorTotal(String brokerId, boolean connected) {
        String metricName = METRICS_PREFIX + "connection_error_total";
        Tags tags = Tags.of(Tag.of("brokerid", brokerId), Tag.of("connected", String.valueOf(connected)));
        String description = "Total number of errors while checking the connection to Kafka brokers";
        String key = metricName + "," + tags;

        return connectionErrorTotal.computeIfAbsent(key, func -> counter(metricName, description, tags));
    }

//    public Timer getConnectionLatency(String brokerId, boolean connected) {
//        String metricName = METRICS_PREFIX + "connection_latency";
//        Tags tags = Tags.of(Tag.of("brokerid", brokerId), Tag.of("connected", String.valueOf(connected)));
//        String description = "Latency in milliseconds for established or failed connections";
//        String key = metricName + "," + tags;
//
//        return connectionLatency.computeIfAbsent(key, func -> timer(metricName, description, tags));
//    }

    private Counter counter(String metricName, String metricDescription, Tags tags) {
        return Counter
            .builder(metricName)
            .description(metricDescription)
            .tags(tags)
            .register(prometheusMeterRegistry);
    }

//    private Timer timer(String metricName, String metricDescription, Tags tags) {
//        return Timer
//            .builder(metricName)
//            .description(metricDescription)
//            .tags(tags)
//            .register(prometheusMeterRegistry);
//    }
}
