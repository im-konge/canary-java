/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package systemtest;

import utils.Constants;
import utils.StUtils;
import utils.deployment.SetupCanary;
import utils.executor.Exec;
import utils.k8s.KubeClusterResource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import java.util.Collections;

import static utils.k8s.KubeClusterResource.kubeClient;

public class AbstractST {

    private static final Logger LOGGER = LogManager.getLogger(AbstractST.class);

    protected static KubeClusterResource cluster;

    protected static SetupCanary setupCanary;

    @BeforeEach
    void beforeEachTest(TestInfo testInfo) {
        LOGGER.info(String.join("", Collections.nCopies(76, "#")));
        LOGGER.info(String.format("%s.%s - STARTED", testInfo.getTestClass().get().getName(), testInfo.getTestMethod().get().getName()));
    }

    @BeforeAll
    static void setupClusterAndCanary() {
        cluster = KubeClusterResource.getInstance();

        // simple teardown before all tests
        if (kubeClient().getNamespace(Constants.NAMESPACE) != null) {
            StUtils.deleteNamespaceWithWait(Constants.NAMESPACE);
        }

        kubeClient().createNamespace(Constants.NAMESPACE);
        deployKafkaCluster();

        setupCanary = new SetupCanary();
        setupCanary.deployCanary();
    }

    @AfterAll
    static void teardown() {
        setupCanary.deleteCanary();
        StUtils.deleteNamespaceWithWait(Constants.NAMESPACE);
    }

    static public void deployKafkaCluster() {
        LOGGER.info("Deploy Strimzi in {} namespace", Constants.NAMESPACE);
        Exec.exec("kubectl", "apply", "-f", "https://strimzi.io/install/latest?namespace=" + Constants.NAMESPACE, "-n", Constants.NAMESPACE);
        StUtils.waitForDeploymentReady(Constants.NAMESPACE, "strimzi-cluster-operator");

        LOGGER.info("Deploy Kafka in {} namespace", Constants.NAMESPACE);
        Exec.exec("kubectl", "apply", "-f", "https://strimzi.io/examples/latest/kafka/kafka-persistent-single.yaml", "-n", Constants.NAMESPACE);

        LOGGER.info("Wait for Kafka to be ready");
        Exec.exec("kubectl", "wait", "kafka/my-cluster", "--for=condition=Ready", "--timeout=300s", "-n", Constants.NAMESPACE);
    }
}
