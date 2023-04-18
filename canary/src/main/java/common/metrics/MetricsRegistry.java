/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package common.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
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
    private final Map<String, DistributionSummary> recordsProducedLatency = new ConcurrentHashMap<>(1);
    private final Map<String, Counter> recordsConsumedTotal = new ConcurrentHashMap<>(1);
    private final Map<String, Counter> consumerErrorTotal = new ConcurrentHashMap<>(1);
    private final Map<String, DistributionSummary> recordsConsumedLatency = new ConcurrentHashMap<>(1);

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

    public DistributionSummary getRecordsProducedLatency(String clientId, int partition, double[] buckets) {
        String metricName = METRICS_PREFIX + "records_produced_latency";
        Tags tags = Tags.of(Tag.of("clientid", clientId), Tag.of("partition", String.valueOf(partition)));
        String description = "Records produced latency in milliseconds";
        String key = metricName + "," + tags;

        return recordsProducedLatency.computeIfAbsent(key, func -> histogram(metricName, description, tags, buckets));
    }

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

    public DistributionSummary getRecordsConsumedLatency(String clientId, int partition, double[] buckets) {
        String metricName = METRICS_PREFIX + "records_consumed_latency";
        Tags tags = Tags.of(Tag.of("clientid", clientId), Tag.of("partition", String.valueOf(partition)));
        String description = "Records end-to-end latency in milliseconds";
        String key = metricName + "," + tags;

        return recordsConsumedLatency.computeIfAbsent(key, func -> histogram(metricName, description, tags, buckets));
    }

    private Counter counter(String metricName, String metricDescription, Tags tags) {
        return Counter
            .builder(metricName)
            .description(metricDescription)
            .tags(tags)
            .register(prometheusMeterRegistry);
    }

    private DistributionSummary histogram(String metricName, String metricDescription, Tags tags, double[] buckets) {
        return DistributionSummary
            .builder(metricName)
            .baseUnit("ms")
            .description(metricDescription)
            .tags(tags)
            .serviceLevelObjectives(buckets)
            .register(prometheusMeterRegistry);
    }
}
