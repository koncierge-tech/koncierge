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
package app.envisionit.api;

public class DiagramRequest {

    private String modelId;
    private String apiKey;
    private String apiVersion;
    private String deploymentRequirementsPayload;
    private String kubernetesConfigurationPayload;

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getDeploymentRequirementsPayload() {
        return deploymentRequirementsPayload;
    }

    public void setDeploymentRequirementsPayload(String deploymentRequirementsPayload) {
        this.deploymentRequirementsPayload = deploymentRequirementsPayload;
    }

    public String getKubernetesConfigurationPayload() {
        return kubernetesConfigurationPayload;
    }

    public void setKubernetesConfigurationPayload(String kubernetesConfigurationPayload) {
        this.kubernetesConfigurationPayload = kubernetesConfigurationPayload;
    }
}
