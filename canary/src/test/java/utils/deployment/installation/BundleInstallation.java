/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package utils.deployment.installation;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import utils.Constants;
import utils.StUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import static utils.k8s.KubeClusterResource.cmdKubeClient;
import static utils.k8s.KubeClusterResource.kubeClient;

public class BundleInstallation extends InstallationMethod {
    private static final Logger LOGGER = LogManager.getLogger(BundleInstallation.class);
    private static Stack<String> createdFiles = new Stack<>();

    @Override
    public void deploy() {
        List<File> canaryFiles = Arrays.stream(new File(Constants.INSTALL_PATH).listFiles()).sorted()
                .filter(File::isFile)
                .collect(Collectors.toList());

        canaryFiles.forEach(file -> {
            if (!file.getName().contains("README")) {
                if (!file.getName().contains("Deployment")) {
                    LOGGER.info(String.format("Creating file: %s", file.getAbsolutePath()));
                    cmdKubeClient().namespace(Constants.NAMESPACE).apply(file);
                    createdFiles.add(file.getAbsolutePath());
                } else {
                    deployCanary(file);
                }
            }
        });
    }

    @Override
    public void delete() {
        while (!createdFiles.empty()) {
            String fileToBeDeleted = createdFiles.pop();
            LOGGER.info("Deleting file: {}", fileToBeDeleted);
            cmdKubeClient().namespace(Constants.NAMESPACE).delete(fileToBeDeleted);
        }
    }

    private void deployCanary(File deploymentFile) {
        Deployment canaryDep = StUtils.configFromYaml(deploymentFile, Deployment.class);
        String deploymentImage = canaryDep.getSpec().getTemplate().getSpec().getContainers().get(0).getImage();

        canaryDep = new DeploymentBuilder(canaryDep)
            .editSpec()
                .editTemplate()
                    .editSpec()
                        .editContainer(0)
                            .withImage(StUtils.changeOrgAndTag(deploymentImage))
                        .endContainer()
                    .endSpec()
                .endTemplate()
            .endSpec()
            .build();

        createdFiles.add(deploymentFile.getAbsolutePath());
        kubeClient().createOrReplaceDeployment(canaryDep);
        StUtils.waitForDeploymentReady(Constants.NAMESPACE, Constants.DEPLOYMENT_NAME);
    }
}
