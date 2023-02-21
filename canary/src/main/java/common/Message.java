/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public record Message(
    String producerId,
    int messageId,
    long timestamp
) {

    public String getJsonMessage() {
        ObjectNode message = JsonNodeFactory.instance.objectNode();
        message.put("producerId", this.producerId);
        message.put("messageId", String.valueOf(this.messageId));
        message.put("timestamp", String.valueOf(this.timestamp));

        return message.toString();
    }

    public static Message parseFromJson(String jsonMessage) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(jsonMessage, Message.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
