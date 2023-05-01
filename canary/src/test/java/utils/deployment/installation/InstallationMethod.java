/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package utils.deployment.installation;

public abstract class InstallationMethod {
    /**
     * Deploy Canary
     */
    public abstract void deploy();

    /**
     * Delete Canary
     */
    public abstract void delete();
}
