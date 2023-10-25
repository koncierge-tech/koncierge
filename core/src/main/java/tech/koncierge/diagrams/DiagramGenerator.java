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
package tech.koncierge.diagrams;

import app.envisionit.api.DiagramRequest;
import app.envisionit.api.DiagramResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.koncierge.diagrams.mapping.CycleAvoidingMappingContext;
import tech.koncierge.diagrams.mapping.DeploymentRequirementsMapper;
import tech.koncierge.diagrams.mapping.KubernetesMapper;
import tech.koncierge.diagrams.util.PasswordRemover;
import tech.koncierge.diagrams.util.RemoteDiagramPoster;
import tech.koncierge.config.Config;
import tech.koncierge.model.deployment.requirements.DeploymentRequirements;
import tech.koncierge.model.kubernetes.KubernetesConfiguration;

public class DiagramGenerator {

    private static Logger logger = LoggerFactory.getLogger(DiagramGenerator.class);

    public static void generateDeploymentRequirementsDiagrams(Config config, DeploymentRequirements deploymentRequirements) {
        if (!config.isGenerateDiagramsEnabled()) {
            return;
        }

        app.envisionit.api.v1.deployment.requirements.DeploymentRequirements remoteDeploymentRequirements = convertToRemoteModel(deploymentRequirements);
        PasswordRemover.removePasswords(remoteDeploymentRequirements);
        String deploymentRequirementsJson = convertToJsonString(remoteDeploymentRequirements);

        DiagramRequest diagramRequest = new DiagramRequest();
        diagramRequest.setApiVersion("v1");
        diagramRequest.setDeploymentRequirementsPayload(deploymentRequirementsJson);

        generateDiagrams(config, diagramRequest);
    }

    public static void generateKubernetesConfigurationDiagrams(Config config, KubernetesConfiguration kubernetesConfiguration) {
        if (!config.isGenerateDiagramsEnabled()) {
            return;
        }

        app.envisionit.api.v1.kubernetes.KubernetesConfiguration remoteKubernetesConfiguration = convertToRemoteModel(kubernetesConfiguration);
        PasswordRemover.removePasswords(remoteKubernetesConfiguration);
        String kubernetesConfigurationJson = convertToJsonString(remoteKubernetesConfiguration);

        DiagramRequest diagramRequest = new DiagramRequest();
        diagramRequest.setApiVersion("v1");
        diagramRequest.setKubernetesConfigurationPayload(kubernetesConfigurationJson);

        generateDiagrams(config, diagramRequest);
    }

    private static void generateDiagrams(Config config, DiagramRequest diagramRequest) {
        diagramRequest.setModelId(config.getModelId());
        diagramRequest.setApiKey(config.getApiKey());

        String diagramRequestJson = convertToJsonString(diagramRequest);

        String response = RemoteDiagramPoster.postDiagramRequest(diagramRequestJson);
        DiagramResponse diagramResponse = parseResponse(response);
        if (diagramResponse.getErrorMessage() != null) {
            logger.info("There was an error generating the diagram: {}", diagramResponse.getErrorMessage());
        } else if (diagramResponse.getDiagramUrl() != null) {
            logger.info("Diagram generated successfully. You can view the diagram at: {}", diagramResponse.getDiagramUrl());
        } else {
            logger.info("There was an unexpected error generating the diagram");
        }
    }

    private static app.envisionit.api.v1.deployment.requirements.DeploymentRequirements convertToRemoteModel(DeploymentRequirements deploymentRequirements) {
        DeploymentRequirementsMapper mapper = DeploymentRequirementsMapper.INSTANCE;
        app.envisionit.api.v1.deployment.requirements.DeploymentRequirements remoteDeploymentRequirements = mapper.map(deploymentRequirements, new CycleAvoidingMappingContext());
        return remoteDeploymentRequirements;
    }

    private static app.envisionit.api.v1.kubernetes.KubernetesConfiguration convertToRemoteModel(KubernetesConfiguration kubernetesConfiguration) {
        KubernetesMapper mapper = KubernetesMapper.INSTANCE;
        app.envisionit.api.v1.kubernetes.KubernetesConfiguration remoteKubernetesConfiguration = mapper.map(kubernetesConfiguration, new CycleAvoidingMappingContext());
        return remoteKubernetesConfiguration;
    }

    private static String convertToJsonString(Object diagramRequest) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(diagramRequest);
            return json;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static app.envisionit.api.v1.deployment.requirements.DeploymentRequirements parseJson(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            app.envisionit.api.v1.deployment.requirements.DeploymentRequirements diagramRequest = mapper.readValue(jsonString, app.envisionit.api.v1.deployment.requirements.DeploymentRequirements.class);
            return diagramRequest;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static DiagramResponse parseResponse(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            DiagramResponse diagramResponse = mapper.readValue(json, DiagramResponse.class);
            return diagramResponse;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
