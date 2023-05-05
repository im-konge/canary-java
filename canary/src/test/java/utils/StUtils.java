/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.LabelSelector;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.readiness.Readiness;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import utils.k8s.exception.WaitException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.time.Duration;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static utils.k8s.KubeClusterResource.cmdKubeClient;
import static utils.k8s.KubeClusterResource.kubeClient;

@SuppressWarnings({"checkstyle:ClassFanOutComplexity", "checkstyle:CyclomaticComplexity", "checkstyle:NPathComplexity"})
public final class StUtils {

    private static final Logger LOGGER = LogManager.getLogger(StUtils.class);

    public static final long GLOBAL_TIMEOUT = Duration.ofMinutes(5).toMillis();
    public static final long GLOBAL_POLL_INTERVAL = Duration.ofSeconds(1).toMillis();
    public static final long GLOBAL_POLL_INTERVAL_MEDIUM = Duration.ofSeconds(10).toMillis();

    private static final Pattern IMAGE_PATTERN_FULL_PATH = Pattern.compile("^(?<registry>[^/]*)/(?<org>[^/]*)/(?<image>[^:]*):(?<tag>.*)$");
    private static final Pattern IMAGE_PATTERN = Pattern.compile("^(?<org>[^/]*)/(?<image>[^:]*):(?<tag>.*)$");

    private StUtils() {
        // All static methods
    }

    /**
     * Poll the given {@code ready} function every {@code pollIntervalMs} milliseconds until it returns true,
     * or throw a WaitException if it doesn't returns true within {@code timeoutMs} milliseconds.
     * @return The remaining time left until timeout occurs
     * (helpful if you have several calls which need to share a common timeout),
     * */
    public static long waitFor(String description, long pollIntervalMs, long timeoutMs, BooleanSupplier ready) {
        return waitFor(description, pollIntervalMs, timeoutMs, ready, () -> { });
    }

    public static long waitFor(String description, long pollIntervalMs, long timeoutMs, BooleanSupplier ready, Runnable onTimeout) {
        LOGGER.debug("Waiting for {}", description);
        long deadline = System.currentTimeMillis() + timeoutMs;
        String exceptionMessage = null;
        int exceptionCount = 0;
        StringWriter stackTraceError = new StringWriter();

        while (true) {
            boolean result;
            try {
                result = ready.getAsBoolean();
            } catch (Exception e) {
                exceptionMessage = e.getMessage();
                if (++exceptionCount == 1 && exceptionMessage != null) {
                    // Log the first exception as soon as it occurs
                    LOGGER.error("Exception waiting for {}, {}", description, exceptionMessage);
                    // log the stacktrace
                    e.printStackTrace(new PrintWriter(stackTraceError));
                }
                result = false;
            }
            long timeLeft = deadline - System.currentTimeMillis();
            if (result) {
                return timeLeft;
            }
            if (timeLeft <= 0) {
                if (exceptionCount > 1) {
                    LOGGER.error("Exception waiting for {}, {}", description, exceptionMessage);

                    if (!stackTraceError.toString().isEmpty()) {
                        // printing handled stacktrace
                        LOGGER.error(stackTraceError.toString());
                    }
                }
                onTimeout.run();
                WaitException waitException = new WaitException("Timeout after " + timeoutMs + " ms waiting for " + description);
                waitException.printStackTrace();
                throw waitException;
            }
            long sleepTime = Math.min(pollIntervalMs, timeLeft);
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("{} not ready, will try again in {} ms ({}ms till timeout)", description, sleepTime, timeLeft);
            }
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                return deadline - System.currentTimeMillis();
            }
        }
    }

    public static void deleteNamespaceWithWait(String namespace) {
        LOGGER.info("Deleting namespace: {}", namespace);
        kubeClient().deleteNamespace(namespace);
        waitFor("namespace to be deleted", GLOBAL_POLL_INTERVAL, GLOBAL_TIMEOUT,
            () -> kubeClient().getNamespace(namespace) == null);

        LOGGER.info("Namespace: {} deleted", namespace);
    }

    public static void waitForDeploymentReady(String namespaceName, String deploymentName) {
        LOGGER.info("Waiting for deployment: {} to become ready", deploymentName);
        waitFor(String.format("Deployment: %s to become ready", deploymentName), GLOBAL_POLL_INTERVAL, GLOBAL_TIMEOUT,
            () -> kubeClient().getDeploymentStatus(namespaceName, deploymentName));

        LOGGER.info("Deployment: {} in namespace: {} is ready", deploymentName, namespaceName);
    }

    /**
     * Wait until the given Deployment has been deleted.
     * @param namespaceName Namespace name
     * @param name The name of the Deployment.
     */
    public static void waitForDeploymentDeletion(String namespaceName, String name) {
        LOGGER.debug("Waiting for Deployment {} deletion", name);
        waitFor("Deployment " + name + " to be deleted", GLOBAL_POLL_INTERVAL, GLOBAL_TIMEOUT,
                () -> {
                    if (kubeClient(namespaceName).getDeployment(namespaceName, name) == null) {
                        return true;
                    } else {
                        LOGGER.warn("Deployment {} is not deleted yet! Triggering force delete by cmd client!", name);
                        cmdKubeClient(namespaceName).deleteByName(Constants.DEPLOYMENT, name);
                        return false;
                    }
                });
        LOGGER.debug("Deployment {} was deleted", name);
    }

    public static void waitForPodsReady(String namespaceName, LabelSelector selector, int expectPods, boolean containers) {
        int[] counter = {0};

        waitFor("All pods matching " + selector + "to be ready",
            GLOBAL_POLL_INTERVAL, GLOBAL_TIMEOUT,
            () -> {
                List<Pod> pods = kubeClient(namespaceName).listPods(namespaceName, selector);
                if (pods.isEmpty() && expectPods == 0) {
                    LOGGER.debug("Expected pods are ready");
                    return true;
                }
                if (pods.isEmpty()) {
                    LOGGER.debug("Not ready (no pods matching {})", selector);
                    return false;
                }
                if (pods.size() != expectPods) {
                    LOGGER.debug("Expected pods {} are not ready", selector);
                    return false;
                }
                for (Pod pod : pods) {
                    if (!Readiness.isPodReady(pod)) {
                        LOGGER.debug("Not ready (at least 1 pod not ready: {})", pod.getMetadata().getName());
                        counter[0] = 0;
                        return false;
                    } else {
                        if (containers) {
                            for (ContainerStatus cs : pod.getStatus().getContainerStatuses()) {
                                LOGGER.debug("Not ready (at least 1 container of pod {} not ready: {})", pod.getMetadata().getName(), cs.getName());
                                if (!Boolean.TRUE.equals(cs.getReady())) {
                                    return false;
                                }
                            }
                        }
                    }
                }
                LOGGER.debug("Pods {} are ready",
                    pods.stream().map(p -> p.getMetadata().getName()).collect(Collectors.joining(", ")));
                // When pod is up, it will check that are rolled pods are stable for next 10 polls and then it return true
                return ++counter[0] > 10;
            });
    }

    public static <T> T configFromYaml(File yamlFile, Class<T> c) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            return mapper.readValue(yamlFile, c);
        } catch (InvalidFormatException e) {
            throw new IllegalArgumentException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String changeOrgAndTag(String image) {
        Matcher m = IMAGE_PATTERN_FULL_PATH.matcher(image);
        if (m.find()) {
            String registry = setImageProperties(m.group("registry"), Environment.CANARY_REGISTRY);
            String org = setImageProperties(m.group("org"), Environment.CANARY_ORG);
            String tag = setImageProperties(m.group("tag"), Environment.CANARY_TAG);

            String newImage = registry + "/" + org + "/" + m.group("image") + ":" + tag;

            LOGGER.info("Updating container image to {}", newImage);

            return newImage;
        }

        m = IMAGE_PATTERN.matcher(image);
        if (m.find()) {
            String registry = Environment.CANARY_REGISTRY != null ? Environment.CANARY_REGISTRY + "/" : "";
            String org = setImageProperties(m.group("org"), Environment.CANARY_ORG);
            String tag = setImageProperties(m.group("tag"), Environment.CANARY_TAG);

            String newImage = registry + org + "/" + m.group("image") + ":"  + tag;

            LOGGER.info("Updating container image to {}", newImage);

            return newImage;
        }

        return image;
    }

    private static String setImageProperties(String current, String envVar) {
        if (envVar != null && !current.equals(envVar)) {
            return envVar;
        }
        return current;
    }
}
