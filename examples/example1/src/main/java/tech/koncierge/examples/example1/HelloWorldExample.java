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
package tech.koncierge.examples.example1;

import tech.koncierge.config.Config;
import tech.koncierge.diagrams.DiagramGenerator;
import tech.koncierge.generator.ConfigurationGenerator;
import tech.koncierge.model.deployment.requirements.Application;
import tech.koncierge.model.deployment.requirements.ApplicationEnvironment;
import tech.koncierge.model.deployment.requirements.Component;
import tech.koncierge.model.deployment.requirements.DeploymentRequirements;
import tech.koncierge.model.deployment.requirements.Environment;
import tech.koncierge.model.deployment.requirements.Port;
import tech.koncierge.model.deployment.requirements.routes.DefaultRoute;
import tech.koncierge.model.kubernetes.KubernetesConfiguration;
import tech.koncierge.util.WorkingDirectoryUtil;

public class HelloWorldExample {

    public static void main(String[] args) {
        DeploymentRequirements deploymentRequirements = createDeploymentRequirements();

        String workingDirectory = WorkingDirectoryUtil.getWorkingDirectory(HelloWorldExample.class);
        Config config = new Config(workingDirectory);

        DiagramGenerator.generateDeploymentRequirementsDiagrams(config, deploymentRequirements);

        KubernetesConfiguration kubernetesConfiguration = new ConfigurationGenerator(config, deploymentRequirements).generate();

        DiagramGenerator.generateKubernetesConfigurationDiagrams(config, kubernetesConfiguration);
    }

    public static DeploymentRequirements createDeploymentRequirements() {
        DeploymentRequirements deploymentRequirements = new DeploymentRequirements();

        Application application = new Application(deploymentRequirements);
        application.setShortName("My First");
        application.setLongName("My First Application");

        ApplicationEnvironment devApplicationEnvironment = new ApplicationEnvironment(application, Environment.DEV, "first.dev.internal.test");

        Component backendComponent = new Component(application);
        backendComponent.setName("Backend");
        backendComponent.setImage("quay.io/rnoushi/hello_world:latest");

        Port backendPort = new Port(backendComponent, 8080);
        new DefaultRoute(application, backendPort);

        return deploymentRequirements;
    }

}
