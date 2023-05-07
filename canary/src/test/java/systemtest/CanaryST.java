/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package systemtest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import utils.Constants;
import utils.StUtils;
import utils.executor.Exec;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static utils.k8s.KubeClusterResource.cmdKubeClient;
import static utils.k8s.KubeClusterResource.kubeClient;

public class CanaryST extends AbstractST {

    private static final Logger LOGGER = LogManager.getLogger(CanaryST.class);
    private static final String CANARY_PREFIX = "strimzi-canary";
    @Test
    void testCanaryFunctionality() throws InterruptedException {
        LOGGER.info("Wait some time to pass few reconciliations");
        Thread.sleep(30000);

        String canaryPod = kubeClient().listPodsByPrefixInName(Constants.NAMESPACE, CANARY_PREFIX).get(0).getMetadata().getName();
        String logs = kubeClient().logsInSpecificNamespace(Constants.NAMESPACE, canaryPod);

        LOGGER.info("Check log of Canary pod: {}/{} for mention about sending messages", canaryPod, Constants.NAMESPACE);
        assertThat(logs.contains("successfully sent"), is(true));

        LOGGER.info("Check log of Canary pod: {}/{} for mention about receiving messages", canaryPod, Constants.NAMESPACE);
        assertThat(logs.contains("Received message with value"), is(true));

        LOGGER.info("Checking metrics of Canary");

        List<String> executableCommand = Arrays.asList(cmdKubeClient().toString(), "exec", canaryPod,
            "-n", Constants.NAMESPACE,
            "--", "curl", "localhost:8080/metrics");

        String metrics = Exec.exec(executableCommand).out().trim();

        assertMetricNotZero(metrics, "strimzi_canary_records_consumed_total\\{clientid=\"strimzi-canary-client\",partition=\"0\",\\}");
        assertMetricNotZero(metrics, "strimzi_canary_records_produced_total\\{clientid=\"strimzi-canary-client\",partition=\"0\",\\}");
        assertMetricNotZero(metrics, "strimzi_canary_records_consumed_latency_ms_count\\{clientid=\"strimzi-canary-client\",partition=\"0\",\\}");
        assertMetricNotZero(metrics, "strimzi_canary_records_produced_latency_ms_count\\{clientid=\"strimzi-canary-client\",partition=\"0\",\\}");
        assertMetricNotZero(metrics, "strimzi_canary_records_produced_latency_ms_sum\\{clientid=\"strimzi-canary-client\",partition=\"0\",\\}");
        assertMetricNotZero(metrics, "strimzi_canary_records_consumed_latency_ms_sum\\{clientid=\"strimzi-canary-client\",partition=\"0\",\\}");

        LOGGER.info("Checking Liveness and Readiness endpoints");

        executableCommand = Arrays.asList(cmdKubeClient().toString(), "exec", canaryPod,
            "-n", Constants.NAMESPACE,
            "--", "curl", "localhost:8080/liveness");

        assertThat(Exec.exec(executableCommand).out().trim(), is("{\"status\": \"ok\"}"));

        executableCommand = Arrays.asList(cmdKubeClient().toString(), "exec", canaryPod,
            "-n", Constants.NAMESPACE,
            "--", "curl", "localhost:8080/readiness");

        assertThat(Exec.exec(executableCommand).out().trim(), is("{\"status\": \"ok\"}"));

        executableCommand = Arrays.asList(cmdKubeClient().toString(), "exec", canaryPod,
            "-n", Constants.NAMESPACE,
            "--", "curl", "localhost:8080/status");

        assertThat(Exec.exec(executableCommand).out().trim().contains("Percentage"), is(true));
    }

    @Test
    void testFailOfKafka() throws InterruptedException {
        LOGGER.info("Wait some time to pass few reconciliations");
        Thread.sleep(30000);

        String canaryPod = kubeClient().listPodsByPrefixInName(Constants.NAMESPACE, CANARY_PREFIX).get(0).getMetadata().getName();
        String logs = kubeClient().logsInSpecificNamespace(Constants.NAMESPACE, canaryPod);

        LOGGER.info("Check log of Canary pod: {}/{} for mention about sending messages", canaryPod, Constants.NAMESPACE);
        assertThat(logs.contains("successfully sent"), is(true));

        LOGGER.info("Deleting Kafka from namespace {}", Constants.NAMESPACE);
        Exec.exec("kubectl", "delete", "kafka", "-n", Constants.NAMESPACE, "--all");

        LOGGER.info("Waiting for failed reconciliation");
        StUtils.waitFor("logs containing info about failed send operation", StUtils.GLOBAL_POLL_INTERVAL_MEDIUM, StUtils.GLOBAL_TIMEOUT, () -> {
            String newLogs = kubeClient().logsInSpecificNamespace(Constants.NAMESPACE, canaryPod);
            return newLogs.contains("Failed to send message with ID");
        });

        LOGGER.info("Checking that metrics contain counter for failed operations");

        List<String> executableCommand = Arrays.asList(cmdKubeClient().toString(), "exec", canaryPod,
            "-n", Constants.NAMESPACE,
            "--", "curl", "localhost:8080/metrics");

        String metrics = Exec.exec(executableCommand).out().trim();

        assertMetricNotZero(metrics, "strimzi_canary_records_produced_failed_total\\{clientid=\"strimzi-canary-client\",partition=\"0\",\\}");

        LOGGER.info("Deploying Kafka back");
        deployKafkaCluster();

        LOGGER.info("Wait for logs to contain message about successful send operation");
        StUtils.waitFor("logs containing info about successful send operation", StUtils.GLOBAL_POLL_INTERVAL_MEDIUM, StUtils.GLOBAL_TIMEOUT, () -> {
            Exec.exec("kubectl", "logs", canaryPod, "--since=30s", "-n", Constants.NAMESPACE);
            String newLogs = kubeClient().logsInSpecificNamespace(Constants.NAMESPACE, canaryPod);
            return newLogs.contains("successfully sent");
        });
    }

    private Double createPatternAndCollectMetric(String metrics, String desiredMetric) {
        Pattern pattern = Pattern.compile(desiredMetric + " ([\\d.][^\\n]+)", Pattern.CASE_INSENSITIVE);
        Matcher t = pattern.matcher(metrics);
        if (t.find()) {
            return Double.parseDouble(t.group(1));
        }

        return 0.0;
    }

    private void assertMetricNotZero(String metrics, String desiredMetric) {
        Double value = createPatternAndCollectMetric(metrics, desiredMetric);
        assertThat(value, is(not(0.0)));
    }
}
