/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package status;

import clients.MessageCountHolder;
import config.CanaryConfiguration;
import config.CanaryConstants;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class StatusServiceTest {
    private static final long CHECK_INTERVAL = 30000;
    private static final long TIME_WINDOW_INTERVAL = 300000;

    private static final Map<String, String> STATUS_SERVICE_CONFIG = Map.of(
        CanaryConstants.STATUS_CHECK_INTERVAL_MS_ENV, String.valueOf(CHECK_INTERVAL),
        CanaryConstants.STATUS_TIME_WINDOW_MS_ENV, String.valueOf(TIME_WINDOW_INTERVAL)
    );

    @Test
    void testStatusServiceCreation() {
        StatusService statusService = createStatusService();

        assertThat(statusService.getStatusCheckInterval(), is(CHECK_INTERVAL));
        assertThat(statusService.getConsumerRing().isEmpty(), is(true));
        assertThat(statusService.getProducerRing().isEmpty(), is(true));
    }

    @Test
    void testGetConsumingStatusWithEmptyData() {
        StatusService statusService = createStatusService();

        ConsumingStatus consumingStatus = statusService.getConsumingStatus();
        assertThat(consumingStatus.percentage(), is(-1.0F));
        assertThat(consumingStatus.timeWindow(), is(0L));
    }

    @Test
    void testCalculateConsumedPercentageWithEmptyData() {
        StatusService statusService = createStatusService();

        assertThat(statusService.calculateConsumedPercentage(), is(-1.0F));
    }

    @Test
    void testStatusCheckProcess() {
        StatusService statusService = createStatusService();

        statusService.statusCheck();
        ConsumingStatus consumingStatus = statusService.getConsumingStatus();
        int counter = 1;

        assertThat(statusService.calculateConsumedPercentage(), is(-1.0F));
        assertThat(consumingStatus.percentage(), is(-1.0F));
        assertThat(consumingStatus.timeWindow(), is(CHECK_INTERVAL * counter));

        // increase number of messages counter for producer
        MessageCountHolder.getInstance().incrementProducedMessagesCount();
        statusService.statusCheck();
        counter++;
        consumingStatus = statusService.getConsumingStatus();

        assertThat(statusService.calculateConsumedPercentage(), is(-1.0F));
        assertThat(consumingStatus.percentage(), is(-1.0F));
        assertThat(consumingStatus.timeWindow(), is(CHECK_INTERVAL * counter));

        // increase number of messages counter for both producer and consumer
        MessageCountHolder.getInstance().incrementProducedMessagesCount();
        MessageCountHolder.getInstance().incrementConsumedMessagesCount();

        statusService.statusCheck();
        counter++;
        consumingStatus = statusService.getConsumingStatus();

        assertThat(statusService.calculateConsumedPercentage(), is(50.0F));
        assertThat(consumingStatus.percentage(), is(50.0F));
        assertThat(consumingStatus.timeWindow(), is(CHECK_INTERVAL * counter));

        // increase number of messages counter for both producer and consumer
        MessageCountHolder.getInstance().incrementConsumedMessagesCount();

        statusService.statusCheck();
        counter++;
        consumingStatus = statusService.getConsumingStatus();

        assertThat(statusService.calculateConsumedPercentage(), is(100.0F));
        assertThat(consumingStatus.percentage(), is(100.0F));
        assertThat(consumingStatus.timeWindow(), is(CHECK_INTERVAL * counter));
    }

    private StatusService createStatusService() {
        CanaryConfiguration canaryConfiguration = CanaryConfiguration.fromMap(STATUS_SERVICE_CONFIG);
        return new StatusService(canaryConfiguration);
    }
}
