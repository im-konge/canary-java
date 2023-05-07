/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package common.timewindow;

import config.CanaryConstants;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TimeWindowRingTest {

    @Test
    void testTimeWindowRingCreationWithCorrectValues() {
        long timeWindow = 250000;
        long sampling = 25000;

        TimeWindowRing timeWindowRing = new TimeWindowRing(timeWindow, sampling);

        assertThat(timeWindowRing.getBuffer().length, is((int) (timeWindow / sampling)));
        assertThat(timeWindowRing.getCount(), is(0L));
        assertThat(timeWindowRing.isEmpty(), is(true));
    }

    @Test
    void testTimeWindowRingCreationWithBigTimeWindow() {
        long timeWindow = 100000000;
        long sampling = 1;

        TimeWindowRing timeWindowRing = new TimeWindowRing(timeWindow, sampling);

        assertThat(timeWindowRing.getBuffer().length, is(CanaryConstants.MAX_TIME_WINDOW_RING_BUFFER_BUCKETS));
    }

    @Test
    void testTimeWindowRingPutValues() {
        long timeWindow = 60000;
        long sampling = 20000;
        int count = 0;

        TimeWindowRing timeWindowRing = new TimeWindowRing(timeWindow, sampling);

        assertThat(timeWindowRing.isEmpty(), is(true));

        timeWindowRing.putValue(count);

        assertThat(timeWindowRing.isEmpty(), is(false));
        assertThat(timeWindowRing.getCount(), is(1L));
        assertThat(timeWindowRing.getHead(), is(0L));
        assertThat(timeWindowRing.getTail(), is(0L));

        timeWindowRing.putValue(++count);

        assertThat(timeWindowRing.getCount(), is(2L));
        assertThat(timeWindowRing.getHead(), is(1L));
        assertThat(timeWindowRing.getTail(), is(0L));

        timeWindowRing.putValue(++count);

        assertThat(timeWindowRing.getCount(), is(3L));
        assertThat(timeWindowRing.getHead(), is(2L));
        assertThat(timeWindowRing.getTail(), is(0L));

        timeWindowRing.putValue(++count);

        assertThat(timeWindowRing.getCount(), is(3L));
        assertThat(timeWindowRing.getHead(), is(3L));
        assertThat(timeWindowRing.getTail(), is(1L));
    }
}
