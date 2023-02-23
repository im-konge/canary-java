/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package status;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ConsumingStatusTest {

    @Test
    void testJsonPrint() {
        long timeWindow = 60000;
        float percentage = 99.88F;
        String expectedJsonString = String.format("""
            {
              "Consuming" : {
                "TimeWindow" : %s,
                "Percentage" : %s
              }
            }""", timeWindow, percentage);

        ConsumingStatus consumingStatus = new ConsumingStatus(timeWindow, percentage);
        assertThat(consumingStatus.toJsonString(), is(expectedJsonString));
    }
}
