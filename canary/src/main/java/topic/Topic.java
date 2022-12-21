/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package topic;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public record Topic(String topicName, Map<String, String> topicConfig) {
    public Topic(String topicName, Map<String, String> topicConfig) {
        this.topicConfig = Objects.requireNonNullElse(topicConfig, new HashMap<>(Collections.emptyMap()));
        this.topicName = topicName;
    }
}
