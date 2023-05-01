/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package config;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CanaryConfigurationUtilsTest {
    @Test
    void testCreateLatencyBuckets() {
        String latencyBuckets = "7,21,45,80,122,200,440,880,2000";
        double[] latencyBucketsDesired = {7.0, 21.0, 45.0, 80.0, 122.0, 200.0, 440.0, 880.0, 2000.0};

        assertThat(CanaryConfigurationUtils.createLatencyBuckets(latencyBuckets), is(latencyBucketsDesired));
    }

    @Test
    void testCreateLatencyBucketsWithInvalidValues() {
        String invalidLatencyBuckets = "7,a,45,80,b,200,440,880,2000";

        assertThrows(NumberFormatException.class, () -> CanaryConfigurationUtils.createLatencyBuckets(invalidLatencyBuckets));
    }

    @Test
    void testCreateTopicConfig() {
        String topicConfiguration = "retention.ms=600000; segment.bytes=16384";
        Map<String, String> topicConfigMap = Map.of(
            "retention.ms", "600000",
            "segment.bytes", "16384"
        );

        assertThat(CanaryConfigurationUtils.createTopicConfig(topicConfiguration), is(topicConfigMap));
    }

    @Test
    void testCreateTopicConfigWithInvalidValues() {
        String topicConfigurationInvalid1 = "retention=ms=60000;segment.bytes=123";
        String topicConfigurationInvalid2 = "retention.ms=60000;=123";

        assertThrows(IllegalArgumentException.class, () -> CanaryConfigurationUtils.createTopicConfig(topicConfigurationInvalid1));
        assertThrows(IllegalArgumentException.class, () -> CanaryConfigurationUtils.createTopicConfig(topicConfigurationInvalid2));
    }
}
