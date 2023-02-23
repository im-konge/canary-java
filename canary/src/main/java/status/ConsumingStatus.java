/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package status;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public record ConsumingStatus(long timeWindow, float percentage) {

    public String toJsonString() {

        ObjectNode root = JsonNodeFactory.instance.objectNode();
        ObjectNode values = JsonNodeFactory.instance.objectNode();

        values.put("TimeWindow", this.timeWindow);
        values.put("Percentage", this.percentage);

        root.set("Consuming", values);

        return root.toPrettyString();
    }
}
