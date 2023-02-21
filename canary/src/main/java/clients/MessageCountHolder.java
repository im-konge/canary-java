/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package clients;

public class MessageCountHolder {
    private int producedMessagesCount;
    private int consumedMessagesCount;
    private static MessageCountHolder instance;

    private MessageCountHolder() {
        this.producedMessagesCount = 0;
        this.consumedMessagesCount = 0;
    }

    public static MessageCountHolder getInstance() {
        if (instance == null) {
            instance = new MessageCountHolder();
        }
        return instance;
    }

    public void incrementProducedMessagesCount() {
        this.producedMessagesCount++;
    }

    public void incrementConsumedMessagesCount() {
        this.consumedMessagesCount++;
    }

    public int getProducedMessagesCount() {
        return this.producedMessagesCount;
    }

    public int getConsumedMessagesCount() {
        return this.consumedMessagesCount;
    }
}
