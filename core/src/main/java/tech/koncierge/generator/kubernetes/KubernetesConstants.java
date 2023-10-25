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
package tech.koncierge.generator.kubernetes;

public class KubernetesConstants {

    // TODO The CERT_MANAGER Schema IDs below are made-up. Try to get a schema for https://cert-manager.io/docs/reference/api-docs/#cert-manager.io/v1.ClusterIssuer
    public static final String CERT_MANAGER_ACME_CHALLENGE_SOLVER_SCHEMA_ID = "io.cert-manager.v1.ACMEChallengeSolver";
    public static final String CERT_MANAGER_ACME_CHALLENGE_SOLVER_HTTP_O1_SCHEMA_ID = "io.cert-manager.v1.ACMEChallengeSolverHTTP01";
    public static final String CERT_MANAGER_ACME_ISSUER_SCHEMA_ID = "io.cert-manager.v1.ACMEIssuer";
    public static final String CERT_MANAGER_ISSUER_SCHEMA_ID = "io.cert-manager.v1.Issuer";
    public static final String CERT_MANAGER_ISSUER_SPEC_SCHEMA_ID = "io.cert-manager.v1.IssuerSpec";

    public static final String CONFIG_MAP_SCHEMA_ID = "io.k8s.api.core.v1.ConfigMap";
    public static final String CONFIG_MAP_KEY_SELECTOR_SCHEMA_ID = "io.k8s.api.core.v1.ConfigMapKeySelector";
    public static final String CONTAINER_SCHEMA_ID = "io.k8s.api.core.v1.Container";
    public static final String CONTAINER_PORT_SCHEMA_ID = "io.k8s.api.core.v1.ContainerPort";
    public static final String DEPLOYMENT_SCHEMA_ID = "io.k8s.api.apps.v1.Deployment";
    public static final String DEPLOYMENT_SPEC_SCHEMA_ID = "io.k8s.api.apps.v1.DeploymentSpec";
    public static final String ENVIRONMENT_VARIABLE_SCHEMA_ID = "io.k8s.api.core.v1.EnvVar";
    public static final String ENVIRONMENT_VARIABLE_SOURCE_SCHEMA_ID = "io.k8s.api.core.v1.EnvVarSource";
    public static final String HTTP_INGRESS_PATH_SCHEMA_ID = "io.k8s.api.networking.v1.HTTPIngressPath";
    public static final String HTTP_INGRESS_RULE_VALUE_SCHEMA_ID = "io.k8s.api.networking.v1.HTTPIngressRuleValue";
    public static final String INGRESS_SCHEMA_ID = "io.k8s.api.networking.v1.Ingress";
    public static final String INGRESS_BACKEND_SCHEMA_ID = "io.k8s.api.networking.v1.IngressBackend";
    public static final String INGRESS_RULE_SCHEMA_ID = "io.k8s.api.networking.v1.IngressRule";
    public static final String INGRESS_SERVICE_BACKEND_SCHEMA_ID = "io.k8s.api.networking.v1.IngressServiceBackend";
    public static final String INGRESS_SPEC_SCHEMA_ID = "io.k8s.api.networking.v1.IngressSpec";
    public static final String INGRESS_TLS_SCHEMA_ID = "io.k8s.api.networking.v1.IngressTLS";
    public static final String PERSISTENT_VOLUME_SCHEMA_ID = "io.k8s.api.core.v1.PersistentVolume";
    public static final String PERSISTENT_VOLUME_CLAIM_SCHEMA_ID = "io.k8s.api.core.v1.PersistentVolumeClaim";
    public static final String PERSISTENT_VOLUME_CLAIM_SPEC_SCHEMA_ID = "io.k8s.api.core.v1.PersistentVolumeClaimSpec";
    public static final String PERSISTENT_VOLUME_SPEC_SCHEMA_ID = "io.k8s.api.core.v1.PersistentVolumeSpec";
    public static final String PERSISTENT_VOLUME_CLAIM_VOLUME_SOURCE_SCHEMA_ID = "io.k8s.api.core.v1.PersistentVolumeClaimVolumeSource";
    public static final String POD_TEMPLATE_SPEC_SCHEMA_ID = "io.k8s.api.core.v1.PodTemplateSpec";
    public static final String RESOURCE_REQUIREMENTS_SCHEMA_ID = "io.k8s.api.core.v1.ResourceRequirements";
    public static final String SECRET_SCHEMA_ID = "io.k8s.api.core.v1.Secret";
    public static final String SECRET_KEY_SELECTOR_SCHEMA_ID = "io.k8s.api.core.v1.SecretKeySelector";
    public static final String SERVICE_SCHEMA_ID = "io.k8s.api.core.v1.Service";
    public static final String SERVICE_BACKEND_PORT_SCHEMA_ID = "io.k8s.api.networking.v1.ServiceBackendPort";
    public static final String SERVICE_PORT_SCHEMA_ID = "io.k8s.api.core.v1.ServicePort";
    public static final String SERVICE_SPEC_SCHEMA_ID = "io.k8s.api.core.v1.ServiceSpec";
    public static final String STORAGE_CLASS_SCHEMA_ID = "io.k8s.api.storage.v1.StorageClass";
    public static final String VOLUME_MOUNT_SCHEMA_ID = "io.k8s.api.core.v1.VolumeMount";
    public static final String VOLUME_SCHEMA_ID = "io.k8s.api.core.v1.Volume";
}
