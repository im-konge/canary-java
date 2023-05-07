/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package common.timewindow;

import config.CanaryConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *   TimeWindowRing represents a struct leveraging a buffer of buckets to store values
 *   covering a sliding time window of specified "timeWindowSize" and sampled with "sampling" rate
 *   NOTE: internal buffer size is determined by time windows size and sampling rate capped at {@link CanaryConstants#MAX_TIME_WINDOW_RING_BUFFER_BUCKETS}
 *
 *   {@link TimeWindowRing#tail} (T): provides the first added value in the current sliding time window, it moves forward when the buffer is full and new values come
 *   {@link TimeWindowRing#head} (H): provides the last added value in the current sliding time window, it always moves forward since the beginning restarting when buffer is full
 *
 * <pre>
 *  --------------------------------------
 *  | v1 (T, H) |  |  | .... |  |  |  |  |  --> 1st value added
 *  --------------------------------------
 *
 *  -----------------------------------------
 *  | v1 (T) | v2 (H) |  | .... |  |  |  |  |  --> 2nd value added
 *  -----------------------------------------
 *
 *  ---------------------------------------------
 *  | v1 (T) | v2 | v3 | .... | vN (H) |  |  |  |  --> N values added in the time window buckets
 *  ---------------------------------------------
 *
 *  -------------------------------------------------------
 *  | v1 (T) | v2 | v3 | .... | vN | vN+1 | vN+2 | vX (H) |  --> reached the end of the buffer
 *  -------------------------------------------------------
 *
 *  ---------------------------------------------------------
 *  | vX+1 (H) | v2 (T) | v3 | .... | vN | vN+1 | vN+2 | vX |  --> start to fill the buffer using first location kicking out old value (time window is moving)
 *  ---------------------------------------------------------
 * </pre>
 */
public class TimeWindowRing {
    private static final Logger LOGGER = LogManager.getLogger(TimeWindowRing.class);

    private long[] buffer;
    private int head;
    private int tail;
    private int count;

    public TimeWindowRing(
        long timeWindowSize,
        long sampling
    ) {
        int bufferSize = (int) (timeWindowSize / sampling);
        if (bufferSize > CanaryConstants.MAX_TIME_WINDOW_RING_BUFFER_BUCKETS) {
            bufferSize = CanaryConstants.MAX_TIME_WINDOW_RING_BUFFER_BUCKETS;
            LOGGER.warn("Time window {} ms too wide with {} ms sampling; resized to {} ms", timeWindowSize, sampling, bufferSize * sampling);
        }
        this.buffer = new long[bufferSize];
        this.head = -1;
        this.tail = -1;
        this.count = 0;
    }

    /**
     * Method inserting new value to buffer:
     * <ol>
     *  <li> moves head to new position
     *  <li> puts value on head position
     *  <li> moves tail if buffer is filled and head == tail
     *  <li> increases {@link TimeWindowRing#count} if the buffer is not fully filled
     * </ol>
     * @param value new value to be added into the buffer
     */
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

    /**
     * Method returning value on head
     * @return value on head index in buffer
     */
    public long getHead() {
        return this.buffer[this.head];
    }

    /**
     * Method returning value on tail
     * @return value on tail index in buffer
     */
    public long getTail() {
        return this.buffer[this.tail];
    }

    /**
     * Method returning actual count of values stored in buffer
     * @return count of values stored in buffer
     */
    public long getCount() {
        return this.count;
    }

    /**
     * Method returning whole buffer
     * @return buffer
     */
    public long[] getBuffer() {
        return buffer;
    }

    /**
     * Checks whether buffer is empty
     * @return result if buffer is empty
     */
    public boolean isEmpty() {
        return this.tail == -1 && this.head == -1;
    }

    /**
     * Method for moving head to new position
     * In case head is at the end of buffer, moves it to beginning of buffer
     */
    private void incrementHead() {
        this.head = (this.head + 1) % this.buffer.length;
    }

    /**
     * Method for moving tail to new position
     * In case tail is at the end of buffer, moves it to beginning of buffer
     */
    private void incrementTail() {
        this.tail = (this.tail + 1) % this.buffer.length;
    }
}
