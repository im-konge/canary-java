/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CanaryConfigurationUtils {

    public static Map<String, String> createTopicConfig(String topicConfig) {
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

            // remove spaces between/before key/value
            topicConfigMap.put(keyAndValue[0].replaceAll(" ", ""), keyAndValue[1].replaceAll(" ", ""));
        }

        return topicConfigMap;
    }

    public static double[] createLatencyBuckets(String latencyBuckets) {
        String[] values = latencyBuckets.split(",");
        double[] latencyBucketsArr = new double[values.length];

        int index = 0;
        for (String value : values) {
            // the empty value should not be parsed
            if (value.length() != 0) {
                latencyBucketsArr[index] = Double.parseDouble(value);
                index++;
            }
        }

        return latencyBucketsArr;
    }

    public static Duration parseDurationOrDefault(String value, Duration defaultValue) {
        return Duration.ofMillis(parseLongOrDefault(value, defaultValue.toMillis()));
    }

    public static boolean parseBooleanOrDefault(String value, boolean defaultValue) {
        return parseOrDefault(value, Boolean::parseBoolean, defaultValue);
    }

    public static long parseLongOrDefault(String value, long defaultValue) {
        return parseOrDefault(value, Long::parseLong, defaultValue);
    }

    public static int parseIntOrDefault(String value, int defaultValue) {
        return parseOrDefault(value, Integer::parseInt, defaultValue);
    }

    public static String parseStringOrDefault(String value, String defaultValue) {
        return parseOrDefault(value, String::toString, defaultValue);
    }

    private static <T> T parseOrDefault(String value, Function<String, T> converter, T defaultValue) {
        T returnValue = defaultValue;

        if (value != null) {
            returnValue = converter.apply(value);
        }
        return returnValue;
    }
}
