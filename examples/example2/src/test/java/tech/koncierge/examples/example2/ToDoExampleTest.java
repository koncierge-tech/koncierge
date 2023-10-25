/*
Copyright 2023 The Koncierge Authors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package tech.koncierge.examples.example2;

import org.junit.jupiter.api.Test;
import tech.koncierge.config.Config;
import tech.koncierge.generator.kubernetes.KubernetesGenerator;
import tech.koncierge.generator.kubernetes.util.YamlUtil;
import tech.koncierge.model.deployment.requirements.DeploymentRequirements;
import tech.koncierge.model.deployment.requirements.Environment;
import tech.koncierge.model.kubernetes.ClusterConfiguration;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ToDoExampleTest {

    @Test
    public void testDEV() {
        test(0, "/ExpectedOutput-LOCAL.yaml");
    }

    @Test
    public void testPROD() {
        test(4, "/ExpectedOutput-PROD.yaml");
    }

    private void test(int clusterConfigurationIndex, String expectedOutputFile) {
        DeploymentRequirements deploymentRequirements = ToDoExample.createDeploymentRequirements();

        KubernetesGenerator kubernetesGenerator = new KubernetesGenerator(new Config(null), deploymentRequirements);
        Map<Environment, ClusterConfiguration> clusterConfigurationMap = kubernetesGenerator.generateClusterConfigurations();
        List<ClusterConfiguration> clusterConfigurations = kubernetesGenerator.collectAndNameClusterConfigurations(clusterConfigurationMap);
        ClusterConfiguration clusterConfiguration = clusterConfigurations.get(clusterConfigurationIndex);

        String yaml = YamlUtil.generateK8sYaml(clusterConfiguration.getManifests());

        assertEquals(readFileAsString(expectedOutputFile), yaml);
    }

    private String readFileAsString(String fileName) {
        String text = new Scanner(getClass().getResourceAsStream(fileName), "UTF-8").useDelimiter("\\A").next();
        return text;
    }
}
