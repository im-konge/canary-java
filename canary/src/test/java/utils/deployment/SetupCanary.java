/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package utils.deployment;

import utils.deployment.installation.BundleInstallation;
import utils.deployment.installation.InstallationMethod;

public class SetupCanary {
    private InstallationMethod installationMethod;

    public SetupCanary() {
        this.installationMethod = getInstallationMethod();
    }

    public void deployCanary() {
        installationMethod.deploy();
    }

    public void deleteCanary() {
        installationMethod.delete();
    }

    private InstallationMethod getInstallationMethod() {
        return new BundleInstallation();
    }
}
