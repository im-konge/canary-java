/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package utils;

public interface Constants {
    String USER_PATH = System.getProperty("user.dir");
    String INSTALL_PATH = USER_PATH + "/packaging/install/";
    String DEPLOYMENT_NAME = "strimzi-canary";
    String NAMESPACE = "canary-namespace";
    String DEPLOYMENT = "deployment";
}
