/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
import config.CanaryConfiguration;

public class Main {
    public static void main(String[] args) {
        CanaryConfiguration configuration = new CanaryConfiguration();

        Canary canary = new Canary(configuration);

        canary.start();

        canary.reconcile();

        canary.stop();
    }
}
