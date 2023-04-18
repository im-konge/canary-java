/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package config;

public class CanaryConstants {
    /**
     * Canary configuration's environment variable constants
     */
    public static final String BOOTSTRAP_SERVERS_ENV = "KAFKA_BOOTSTRAP_SERVERS";
    public static final String TOPIC_ENV = "TOPIC";
    public static final String TOPIC_CONFIG_ENV = "TOPIC_CONFIG";
    public static final String RECONCILE_INTERVAL_ENV = "RECONCILE_INTERVAL_MS";
    public static final String CLIENT_ID_ENV = "CLIENT_ID";
    public static final String CONSUMER_GROUP_ID_ENV = "CONSUMER_GROUP_ID";
    public static final String PRODUCER_LATENCY_BUCKETS_ENV = "PRODUCER_LATENCY_BUCKETS";
    public static final String ENDTOEND_LATENCY_BUCKETS_ENV = "ENDTOEND_LATENCY_BUCKETS";
    public static final String EXPECTED_CLUSTER_SIZE_ENV = "EXPECTED_CLUSTER_SIZE";
    public static final String TLS_ENABLED_ENV = "TLS_ENABLED";
    public static final String TLS_CA_CERT_ENV = "TLS_CA_CERT";
    public static final String TLS_CLIENT_CERT_ENV = "TLS_CLIENT_CERT";
    public static final String TLS_CLIENT_KEY_ENV = "TLS_CLIENT_KEY";
    public static final String SASL_MECHANISM_ENV = "SASL_MECHANISM";
    public static final String SASL_USER_ENV = "SASL_USER";
    public static final String SASL_PASSWORD_ENV = "SASL_PASSWORD";
    public static final String STATUS_CHECK_INTERVAL_MS_ENV = "STATUS_CHECK_INTERVAL_MS";
    public static final String STATUS_TIME_WINDOW_MS_ENV = "STATUS_TIME_WINDOW_MS";

    /**
     * Canary configuration's defaults
     */
    public static final String BOOTSTRAP_SERVERS_DEFAULT = "localhost:9092";
    public static final String TOPIC_DEFAULT = "__strimzi_canary";
    public static final long RECONCILE_INTERVAL_DEFAULT = 30000;
    public static final String CLIENT_ID_DEFAULT = "strimzi-canary-client";
    public static final String CONSUMER_GROUP_ID_DEFAULT = "strimzi-canary-group";
    public static final String PRODUCER_LATENCY_BUCKETS_DEFAULT = "2,5,10,20,50,100,200,400";
    public static final String ENDTOEND_LATENCY_BUCKETS_DEFAULT = "5,10,20,50,100,200,400,800";
    public static final int EXPECTED_CLUSTER_SIZE_DEFAULT = 1;
    public static final boolean TLS_ENABLED_DEFAULT = false;
    public static final long STATUS_CHECK_INTERVAL_MS_DEFAULT = 30000;
    public static final long STATUS_TIME_WINDOW_MS_DEFAULT = 300000;
    public static final long TASK_TERMINATION_TIMEOUT = 120000;
    public static final int MAX_TIME_WINDOW_RING_BUFFER_BUCKETS = 384;
}
