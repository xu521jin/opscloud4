package com.baiyi.opscloud.datasource.kubernetes.fe;

import com.baiyi.opscloud.common.datasource.KubernetesConfig;
import com.baiyi.opscloud.datasource.kubernetes.base.BaseKubernetesTest;
import com.baiyi.opscloud.datasource.kubernetes.driver.KubernetesDeploymentDriver;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

/**
 * @Author baiyi
 * @Date 2023/8/18 09:36
 * @Version 1.0
 */
public class KubernetesFrontEndTest extends BaseKubernetesTest {

    private final static String NAMESPACE = "pre";

    @Test
    void updateResources() {
        KubernetesConfig kubernetesConfig = getConfigById(KubernetesClusterConfigs.ACK_FE);
        List<Deployment> deploymentList = KubernetesDeploymentDriver.list(kubernetesConfig.getKubernetes(), NAMESPACE);

        deploymentList.forEach(deployment -> {
            final String appName = deployment.getMetadata().getName();
            Optional<Container> optionalContainer = deployment.getSpec().getTemplate().getSpec().getContainers()
                    .stream()
                    .filter(c -> c.getName().startsWith(appName))
                    .findFirst();
            if (optionalContainer.isPresent()) {
                Container container = optionalContainer.get();
                // resource
                container.getResources().getLimits().put("cpu", new Quantity("200m"));
                container.getResources().getLimits().put("memory", new Quantity("128Mi"));
                container.getResources().getRequests().put("cpu", new Quantity("50m"));
                container.getResources().getRequests().put("memory", new Quantity("64Mi"));
                KubernetesDeploymentDriver.update(kubernetesConfig.getKubernetes(), deployment);
                print(deployment.getMetadata().getName());
            }
        });

    }
}
