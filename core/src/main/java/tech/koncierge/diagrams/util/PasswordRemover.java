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
package tech.koncierge.diagrams.util;

import app.envisionit.api.v1.deployment.requirements.Application;
import app.envisionit.api.v1.deployment.requirements.DeploymentRequirements;
import app.envisionit.api.v1.deployment.requirements.Value;
import app.envisionit.api.v1.deployment.requirements.values.passwords.ProvidedBase64EncodedPassword;
import app.envisionit.api.v1.kubernetes.ClusterConfiguration;
import app.envisionit.api.v1.kubernetes.KubernetesConfiguration;
import app.envisionit.api.v1.kubernetes.Manifest;
import app.envisionit.api.v1.kubernetes.PropertyMap;
import app.envisionit.api.v1.kubernetes.PropertyValue;
import app.envisionit.api.v1.kubernetes.Specification;
import app.envisionit.api.v1.kubernetes.simple.StringValue;
import tech.koncierge.generator.kubernetes.KubernetesConstants;


public class PasswordRemover {

    public static void removePasswords(DeploymentRequirements remoteDeploymentRequirements) {
        for (Application application : remoteDeploymentRequirements.getApplications()) {
            for (Value value : application.getValues()) {
                if (value instanceof ProvidedBase64EncodedPassword) {
                    ProvidedBase64EncodedPassword providedBase64EncodedPassword = (ProvidedBase64EncodedPassword) value;
                    providedBase64EncodedPassword.setEncodedValue("REDACTED");
                }
            }
        }
    }

    public static void removePasswords(KubernetesConfiguration remoteKubernetesConfiguration) {
        for (ClusterConfiguration clusterConfiguration : remoteKubernetesConfiguration.getClusterConfigurations()) {
            for (Manifest manifest : clusterConfiguration.getManifests()) {
                if (KubernetesConstants.SECRET_SCHEMA_ID.equals(manifest.getSchemaId())) {
                    PropertyValue data = manifest.getProperties().get("data");
                    if (data != null && data instanceof PropertyMap) {
                        PropertyMap dataMap = (PropertyMap) data;
                        for (PropertyValue value : dataMap.values()) {
                            if (value instanceof StringValue) {
                                StringValue stringValue = (StringValue) value;
                                stringValue.setStringValue("REDACTED");
                            }
                        }
                    }
                }
            }
        }
    }
}
