/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package services.topic;

import java.util.Map;

public record Topic(String topicName, Map<String, String> topicConfig) { }
