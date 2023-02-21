/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package common.timewindow;

import config.CanaryConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TimeWindowRing {
    private static final Logger LOGGER = LogManager.getLogger(TimeWindowRing.class);

    private int[] buffer;
    private int head;
    private int tail;
    private int count;
    private final long size;
    private final long sampling;

    public TimeWindowRing(
        long size,
        long sampling
    ) {
        int bufferSize = (int) (size / sampling);
        if (bufferSize > CanaryConstants.MAX_TIME_WINDOW_RING_BUFFER_BUCKETS) {
            bufferSize = CanaryConstants.MAX_TIME_WINDOW_RING_BUFFER_BUCKETS;
            LOGGER.warn("Time window {} ms too wide with {} ms sampling; resized to {} ms", size, sampling, bufferSize * sampling);
        }
        this.buffer = new int[bufferSize];
        this.head = -1;
        this.tail = -1;
        this.count = 0;
        this.size = bufferSize * sampling;
        this.sampling = sampling;
    }

    public void putValue(int value) {
        incrementHead();
        this.buffer[this.head] = value;

        if (this.head == this.tail || this.tail == -1) {
            incrementTail();
        }

        if (this.count < this.buffer.length) {
            this.count++;
        }
    }

    public int getHead() {
        return this.buffer[this.head];
    }

    public int getTail() {
        return this.buffer[this.tail];
    }

    public int getCount() {
        return this.count;
    }

    public boolean isEmpty() {
        return this.tail == -1 && this.head == -1;
    }

    private void incrementHead() {
        this.head = (this.head + 1) % this.buffer.length;
    }

    private void incrementTail() {
        this.tail = (this.tail + 1) % this.buffer.length;
    }
}
