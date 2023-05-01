/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package systemtest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

public class CanaryST extends AbstractST {

    private static final Logger LOGGER = LogManager.getLogger(CanaryST.class);

    @Test
    void testCanaryFunctionality() {
        LOGGER.info("let'sgo");
        // check logs for message successfully sent
        // check /metrics endpoint for few metrics
        // check /liveness and /readiness
    }
}
