/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package common;

import java.time.Duration;
import java.util.function.Function;

/**
 * Environment class handles parsing environment variables to
 * corresponding types - String, int, long, Duration, float, boolean
 * - or returns default, if env. variable is not set
 */
public class Environment {

    public static Duration getDurationOrDefault(String var, Duration defaultValue) {
        return Duration.ofMillis(getLongOrDefault(var, defaultValue.toMillis()));
    }

    public static boolean getBooleanOrDefault(String var, boolean defaultValue) {
        return getOrDefault(var, Boolean::parseBoolean, defaultValue);
    }

    public static long getLongOrDefault(String var, long defaultValue) {
        return getOrDefault(var, Long::parseLong, defaultValue);
    }

    public static int getIntOrDefault(String var, int defaultValue) {
        return getOrDefault(var, Integer::parseInt, defaultValue);
    }

    public static String getStringOrDefault(String var, String defaultValue) {
        return getOrDefault(var, String::toString, defaultValue);
    }

    private static <T> T getOrDefault(String var, Function<String, T> converter, T defaultValue) {
        String value = System.getenv(var);

        T returnValue = defaultValue;

        if (value != null) {
            returnValue = converter.apply(value);
        }
        return returnValue;
    }
}
