/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import common.time.TimeUtils;

import java.sql.Timestamp;

public record Message(
    String producerId,
    int messageId,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TimeUtils.DEFAULT_TIME_PATTERN, timezone = TimeUtils.DEFAULT_TIMEZONE) Timestamp timestamp
) {

    public String getJsonMessage() {
        ObjectNode message = JsonNodeFactory.instance.objectNode();
        message.put("producerId", this.producerId);
        message.put("messageId", String.valueOf(this.messageId));
        message.put("timestamp", TimeUtils.getDateFormat().format(this.timestamp));

        return message.toString();
    }

    public static Message parseFromJson(String jsonMessage) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(TimeUtils.getDateFormat());

        try {
            return mapper.readValue(jsonMessage, Message.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
