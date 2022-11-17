/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package config;

import java.time.Duration;

public class CanaryConstants {
    /**
     * Canary configuration's environment variable constants
     */
    public static final String BOOTSTRAP_SERVERS_ENV = "KAFKA_BOOTSTRAP_SERVERS";
    public static final String BOOTSTRAP_BACKOFF_MAX_ATTEMPTS_ENV = "KAFKA_BOOTSTRAP_BACKOFF_MAX_ATTEMPTS";
    public static final String BOOTSTRAP_BACKOFF_SCALE_ENV = "KAFKA_BOOTSTRAP_BACKOFF_SCALE";
    public static final String TOPIC_ENV = "TOPIC";
    public static final String TOPIC_CONFIG_ENV = "TOPIC_CONFIG";
    public static final String RECONCILE_INTERVAL_ENV = "RECONCILE_INTERVAL_MS";
    public static final String CLIENT_ID_ENV = "CLIENT_ID";
    public static final String CONSUMER_GROUP_ID_ENV = "CONSUMER_GROUP_ID";
    public static final String PRODUCER_LATENCY_BUCKETS_ENV = "PRODUCER_LATENCY_BUCKETS";
    public static final String ENDTOEND_LATENCY_BUCKETS_ENV = "ENDTOEND_LATENCY_BUCKETS";
    public static final String EXPECTED_CLUSTER_SIZE_ENV = "EXPECTED_CLUSTER_SIZE";
    public static final String KAFKA_VERSION_ENV = "KAFKA_VERSION";
    public static final String TLS_ENABLED_ENV = "TLS_ENABLED";
    public static final String TLS_CA_CERT_ENV = "TLS_CA_CERT";
    public static final String TLS_CLIENT_CERT_ENV = "TLS_CLIENT_CERT";
    public static final String TLS_CLIENT_KEY_ENV = "TLS_CLIENT_KEY";
    public static final String TLS_INSECURE_SKIP_VERIFY_ENV = "TLS_INSECURE_SKIP_VERIFY";
    public static final String SASL_MECHANISM_ENV = "SASL_MECHANISM";
    public static final String SASL_USER_ENV = "SASL_USER";
    public static final String SASL_PASSWORD_ENV = "SASL_PASSWORD";
    public static final String CONNECTION_CHECK_INTERVAL_MS_ENV = "CONNECTION_CHECK_INTERVAL_MS";
    public static final String CONNECTION_CHECK_LATENCY_BUCKETS_ENV = "CONNECTION_CHECK_LATENCY_BUCKETS";
    public static final String STATUS_CHECK_INTERVAL_MS_ENV = "STATUS_CHECK_INTERVAL_MS";
    public static final String STATUS_TIME_WINDOW_MS_ENV = "STATUS_TIME_WINDOW_MS";
    public static final String DYNAMIC_CONFIG_FILE_ENV = "DYNAMIC_CONFIG_FILE";
    public static final String DYNAMIC_CONFIG_WATCHER_INTERVAL_ENV = "DYNAMIC_CONFIG_WATCHER_INTERVAL";

    //    SaramaLogEnabledEnvVar              = "SARAMA_LOG_ENABLED" ---> not needed FMPOV, by default `false`
    //    VerbosityLogLevelEnvVar             = "VERBOSITY_LOG_LEVEL" ---> log level should be surely configurable, but we should go with log4j2 levels (and not 0,1,2) --> backwards compatibility problem?

    /**
     * Canary configuration's defaults
     */
    public static final String BOOTSTRAP_SERVERS_DEFAULT = "localhost:9092";
    public static final int BOOTSTRAP_BACKOFF_MAX_ATTEMPTS_DEFAULT = 10;
    public static final Duration BOOTSTRAP_BACKOFF_SCALE_DEFAULT = Duration.ofMillis(5000);
    public static final String TOPIC_DEFAULT = "__strimzi_canary";
    public static final Duration RECONCILE_INTERVAL_DEFAULT = Duration.ofMillis(30000);
    public static final String CLIENT_ID_DEFAULT = "strimzi-canary-client";
    public static final String CONSUMER_GROUP_ID_DEFAULT = "strimzi-canary-group";
    public static final String PRODUCER_LATENCY_BUCKETS_DEFAULT = "2,5,10,20,50,100,200,400";
    public static final String ENDTOEND_LATENCY_BUCKETS_DEFAULT = "5,10,20,50,100,200,400,800";
    public static final int EXPECTED_CLUSTER_SIZE_DEFAULT = -1;
    public static final String KAFKA_VERSION_DEFAULT = "3.3.1";
    public static final boolean TLS_ENABLED_DEFAULT = false;
    public static final boolean TLS_INSECURE_SKIP_VERIFY_DEFAULT = false;
    public static final Duration CONNECTION_CHECK_INTERVAL_MS_DEFAULT = Duration.ofMillis(120000);
    public static final String CONNECTION_CHECK_LATENCY_BUCKETS_DEFAULT = "100,200,400,800,1600";
    public static final Duration STATUS_CHECK_INTERVAL_MS_DEFAULT = Duration.ofMillis(30000);
    public static final Duration STATUS_TIME_WINDOW_MS_DEFAULT = Duration.ofMillis(300000);
    public static final Duration DYNAMIC_CONFIG_WATCHER_INTERVAL_DEFAULT = Duration.ofMillis(30000);
}
