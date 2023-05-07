/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package status;

import clients.MessageCountHolder;
import common.timewindow.TimeWindowRing;
import config.CanaryConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DecimalFormat;

public class StatusService {

    private static final Logger LOGGER = LogManager.getLogger(StatusService.class);

    private long statusCheckInterval;
    private TimeWindowRing producerRing;
    private TimeWindowRing consumerRing;

    public StatusService(CanaryConfiguration canaryConfiguration) {
        this.statusCheckInterval = canaryConfiguration.getStatusCheckInterval();
        this.producerRing = new TimeWindowRing(canaryConfiguration.getStatusTimeWindow(), statusCheckInterval);
        this.consumerRing = new TimeWindowRing(canaryConfiguration.getStatusTimeWindow(), statusCheckInterval);
    }

    public void statusCheck() {
        this.producerRing.putValue(MessageCountHolder.getInstance().getProducedMessagesCount());
        this.consumerRing.putValue(MessageCountHolder.getInstance().getConsumedMessagesCount());

        LOGGER.info("Status check: produced [head = {}, tail = {}, count = {}], consumed [head = {}, tail = {}, count = {}]",
            producerRing.getHead(), producerRing.getTail(), producerRing.getCount(),
            consumerRing.getHead(), consumerRing.getTail(), consumerRing.getCount()
        );
    }

    public ConsumingStatus getConsumingStatus() {
        float calculatedPercentage = calculateConsumedPercentage();

        if (calculatedPercentage == -1) {
            LOGGER.warn("No data samples available in the time window ring");
        }

        return new ConsumingStatus(
            this.statusCheckInterval * this.consumerRing.getCount(),
            calculatedPercentage
        );
    }

    public float calculateConsumedPercentage() {
        if (this.producerRing.isEmpty() || this.consumerRing.isEmpty()) {
            return -1;
        }

        long produced = this.producerRing.getHead() - this.producerRing.getTail();
        long consumed = this.consumerRing.getHead() - this.consumerRing.getTail();

        if (produced == 0 || consumed == 0) {
            return -1;
        }

        float percentage = (float) (consumed * 100) / produced;

        return Float.parseFloat(new DecimalFormat("#.##").format(percentage));
    }

    public long getStatusCheckInterval() {
        return statusCheckInterval;
    }

    public TimeWindowRing getConsumerRing() {
        return consumerRing;
    }

    public TimeWindowRing getProducerRing() {
        return producerRing;
    }
}
