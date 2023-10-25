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

import tech.koncierge.config.Config;
import tech.koncierge.generator.kubernetes.util.YamlUtil;
import tech.koncierge.model.deployment.requirements.Application;
import tech.koncierge.model.deployment.requirements.ApplicationEnvironment;
import tech.koncierge.model.deployment.requirements.Component;
import tech.koncierge.model.deployment.requirements.Database;
import tech.koncierge.model.deployment.requirements.DeploymentRequirements;
import tech.koncierge.model.deployment.requirements.Environment;
import tech.koncierge.model.deployment.requirements.Port;
import tech.koncierge.model.deployment.requirements.Route;
import tech.koncierge.model.deployment.requirements.Storage;
import tech.koncierge.model.deployment.requirements.StorageUsage;
import tech.koncierge.model.deployment.requirements.Variable;
import tech.koncierge.model.deployment.requirements.routes.DefaultRoute;
import tech.koncierge.model.deployment.requirements.routes.SubDomainRoute;
import tech.koncierge.model.deployment.requirements.values.ConstantValue;
import tech.koncierge.model.deployment.requirements.values.DomainName;
import tech.koncierge.model.deployment.requirements.values.HostName;
import tech.koncierge.model.deployment.requirements.values.Password;
import tech.koncierge.model.deployment.requirements.values.passwords.ProvidedBase64EncodedPassword;
import tech.koncierge.model.kubernetes.ClusterConfiguration;
import tech.koncierge.model.kubernetes.KubernetesConfiguration;
import tech.koncierge.model.kubernetes.Manifest;
import tech.koncierge.model.kubernetes.PropertyMap;
import tech.koncierge.model.kubernetes.PropertyValueList;
import tech.koncierge.model.kubernetes.Specification;
import tech.koncierge.model.kubernetes.simple.StringValue;
import tech.koncierge.validation.DeploymentRequirementsValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static tech.koncierge.generator.kubernetes.KubernetesConstants.*;

public class KubernetesGenerator {

    private Config config;
    private DeploymentRequirements deploymentRequirements;

    public KubernetesGenerator(Config config, DeploymentRequirements deploymentRequirements) {
        this.config = config;
        this.deploymentRequirements = deploymentRequirements;
    }

    public KubernetesConfiguration generate() {
        Map<Environment, ClusterConfiguration> clusterConfigurationByEnvironment = generateClusterConfigurations();

        List<ClusterConfiguration> clusterConfigurations = collectAndNameClusterConfigurations(clusterConfigurationByEnvironment);

        printClusterConfigurations(clusterConfigurations);

        KubernetesConfiguration kubernetesConfiguration = new KubernetesConfiguration();
        kubernetesConfiguration.getClusterConfigurations().addAll(clusterConfigurations);

        return kubernetesConfiguration;
    }

    public Map<Environment, ClusterConfiguration> generateClusterConfigurations() {
        new DeploymentRequirementsValidator(deploymentRequirements).validate();

        Map<Environment, ClusterConfiguration> clusterConfigurationByEnvironment = new HashMap<>();

        for (Environment environment : Environment.values()) {
            clusterConfigurationByEnvironment.put(environment, new ClusterConfiguration(environment.getShortName()));
        }

        for (Application application : deploymentRequirements.getApplications()) {
            String applicationShortName = application.getShortName();

            String namespace = toKubernetesNamespace(applicationShortName);

            List<Label> applicationLabels = new ArrayList<>();
            applicationLabels.add(new Label("app", namespace));

            for (ApplicationEnvironment applicationEnvironment : application.getApplicationEnvironments()) {

                List<Manifest> manifests = new ArrayList<>();

                for (Component component : application.getComponents()) {
                    String name = toKubernetesNamespace(component.getName());

                    List<Label> componentLabels = new ArrayList<>(applicationLabels);
                    componentLabels.add(new Label("component", name));

                    if (requiresSecret(component)) {
                        Manifest secret = createSecret(namespace, name, component, componentLabels);
                        manifests.add(secret);
                    }

                    if (requiresConfigMap(component)) {
                        Manifest configMap = createConfigMap(namespace, name, component, componentLabels, applicationEnvironment);
                        manifests.add(configMap);
                    }

                    Manifest deployment = createDeployment(namespace, name, component, componentLabels);
                    manifests.add(deployment);

                    Manifest service = createService(namespace, name, component, componentLabels);
                    manifests.add(service);
                }

                for (Storage storage : application.getStorages()) {
                    String name = getStorageName(storage, application);

                    Manifest persistentVolume = createPersistentVolume(namespace, name, storage);
                    manifests.add(persistentVolume);

                    Manifest pvc = createPersistentVolumeClaim(namespace, name, storage);
                    manifests.add(pvc);
                }

                if (!application.getStorages().isEmpty()) {
                    Manifest storageClass = createStorageClass(namespace, applicationEnvironment);
                    manifests.add(storageClass);
                }

                if (applicationEnvironment.getEnvironment().isHttpsEnabled()) {
                    Manifest issuer = createIssuer(namespace);
                    manifests.add(issuer);
                }

                Manifest ingress = createIngress(namespace, namespace, applicationEnvironment);
                manifests.add(ingress);

                clusterConfigurationByEnvironment.get(applicationEnvironment.getEnvironment()).getManifests().addAll(manifests);

            }

        }

        return clusterConfigurationByEnvironment;
    }

    private String getStorageName(Storage storage, Application application) {
        Component primaryComponent = getFirstReadWriteComponent(storage, application);
        if (primaryComponent == null) {
            primaryComponent = getFirstComponent(storage, application);
        }
        // TODO If the component has many storages, then we need to append a number to the name
        return toKubernetesNamespace(primaryComponent.getName());
    }

    private Component getFirstReadWriteComponent(Storage storage, Application application) {
        for (Component component : application.getComponents()) {
            for (StorageUsage storageUsage : component.getStorageUsages()) {
                if (storageUsage.getStorage().equals(storage) && storageUsage.getMode().equals(StorageUsage.Mode.ReadWrite)) {
                    return component;
                }
            }
        }
        return null;
    }

    private Component getFirstComponent(Storage storage, Application application) {
        for (Component component : application.getComponents()) {
            for (StorageUsage storageUsage : component.getStorageUsages()) {
                if (storageUsage.getStorage().equals(storage)) {
                    return component;
                }
            }
        }
        return null;
    }

    public List<ClusterConfiguration> collectAndNameClusterConfigurations(Map<Environment, ClusterConfiguration> clusterConfigurationByEnvironment) {
        List<ClusterConfiguration> clusterConfigurations = new ArrayList<>();

        for (Environment environment : Environment.values()) {
            ClusterConfiguration clusterConfiguration = clusterConfigurationByEnvironment.get(environment);
            if (clusterConfiguration.getManifests().isEmpty()) {
                continue;
            }

            clusterConfiguration.setClusterName("Kubernetes-Manifests-" + environment.getShortName());
            clusterConfigurations.add(clusterConfiguration);
        }

        return clusterConfigurations;
    }

    private void printClusterConfigurations(List<ClusterConfiguration> clusterConfigurations) {
        for (ClusterConfiguration clusterConfiguration : clusterConfigurations) {
            YamlUtil.printK8sYaml(config, clusterConfiguration.getClusterName(), clusterConfiguration.getManifests());
        }
    }

    private int getNumberOfReplicas() {
        return 1;
    }

    public static String toKubernetesNamespace(String s) {
        if (s == null) {
            return "";
        }
        return s.toLowerCase().replaceAll("[^a-z0-9]", "-");
    }

    private Manifest createDeployment(String namespace, String name, Component component, List<Label> labels) {
        Manifest deployment = new Manifest(DEPLOYMENT_SCHEMA_ID);

        PropertyMap metadataMap = new PropertyMap();
        metadataMap.put("namespace", namespace);

        String deploymentName = name;
        metadataMap.put("name", deploymentName);

        deployment.getProperties().put("metadata", metadataMap);

        Specification deploymentSpec = new Specification(DEPLOYMENT_SPEC_SCHEMA_ID);
        deployment.getProperties().put("spec", deploymentSpec);

        deploymentSpec.getProperties().put("replicas", getNumberOfReplicas());

        PropertyMap selectorMap = new PropertyMap();
        deploymentSpec.getProperties().put("selector", selectorMap);

        PropertyMap matchLabelsMap = new PropertyMap();
        selectorMap.put("matchLabels", matchLabelsMap);
        matchLabelsMap.putAll(toPropertyMap(labels));

        Specification podTemplateSpec = new Specification(POD_TEMPLATE_SPEC_SCHEMA_ID);
        deploymentSpec.getProperties().put("template", podTemplateSpec);

        PropertyMap podTemplateSpecMetadataMap = new PropertyMap();
        podTemplateSpec.getProperties().put("metadata", podTemplateSpecMetadataMap);

        PropertyMap labelsMap = new PropertyMap();
        podTemplateSpecMetadataMap.put("labels", labelsMap);
        labelsMap.putAll(toPropertyMap(labels));

        PropertyMap podSpec = new PropertyMap();  // TODO Should be a Specification io.k8s.api.core.v1.PodSpec
        podTemplateSpec.getProperties().put("spec", podSpec);

        PropertyValueList containers = new PropertyValueList();
        podSpec.put("containers", containers);

        Specification container = new Specification(CONTAINER_SCHEMA_ID);

        containers.getPropertyValues().add(container);

        container.getProperties().put("name", name);
        container.getProperties().put("image", getImage(component));
        addArgs(component, container);

        if (!component.getVariables().isEmpty()) {
            Specification environmentVariables = new Specification(ENVIRONMENT_VARIABLE_SCHEMA_ID);

            PropertyValueList environmentVariableList = new PropertyValueList();
            container.getProperties().put("env", environmentVariableList);

            for (Variable variable : component.getVariables()) {
                Specification environmentVariableSpecification = new Specification(ENVIRONMENT_VARIABLE_SCHEMA_ID);
                environmentVariableList.getPropertyValues().add(environmentVariableSpecification);

                environmentVariableSpecification.getProperties().put("name", variable.getKey());

                if (variable.getValue() instanceof Password) {
                    Specification environmentVariableSource = new Specification(ENVIRONMENT_VARIABLE_SOURCE_SCHEMA_ID);
                    environmentVariableSpecification.putProperty("valueFrom", environmentVariableSource);

                    Specification secretKeyRef = new Specification(SECRET_KEY_SELECTOR_SCHEMA_ID);
                    environmentVariableSource.putProperty("secretKeyRef", secretKeyRef);

                    secretKeyRef.getProperties().put("name", name);
                    secretKeyRef.getProperties().put("key", variable.getKey());
                } else {
                    Specification environmentVariableSource = new Specification(ENVIRONMENT_VARIABLE_SOURCE_SCHEMA_ID);
                    environmentVariableSpecification.putProperty("valueFrom", environmentVariableSource);

                    Specification configMapKeyRef = new Specification(CONFIG_MAP_KEY_SELECTOR_SCHEMA_ID);
                    environmentVariableSource.putProperty("configMapKeyRef", configMapKeyRef);

                    configMapKeyRef.getProperties().put("name", name);
                    configMapKeyRef.getProperties().put("key", variable.getKey());
                }
            }

        }

        if (!component.getStorageUsages().isEmpty()) {
            PropertyValueList volumeMounts = new PropertyValueList();
            container.getProperties().put("volumeMounts", volumeMounts);

            PropertyValueList volumes = new PropertyValueList();
            podSpec.put("volumes", volumes);

            for (StorageUsage storageUsage : component.getStorageUsages()) {
                Specification volumeMount = new Specification(VOLUME_MOUNT_SCHEMA_ID);
                volumeMounts.getPropertyValues().add(volumeMount);

                String storageName = getStorageName(storageUsage.getStorage(), storageUsage.getComponent().getApplication());

                volumeMount.getProperties().put("name", storageName);
                volumeMount.getProperties().put("mountPath", storageUsage.getMountPath());

                Specification volume = new Specification(VOLUME_SCHEMA_ID);
                volumes.getPropertyValues().add(volume);

                volume.getProperties().put("name", storageName);

                Specification persistentVolumeClaim = new Specification(PERSISTENT_VOLUME_CLAIM_VOLUME_SOURCE_SCHEMA_ID);
                volume.getProperties().put("persistentVolumeClaim", persistentVolumeClaim);

                String claimName = getClaimName(storageName, component);
                persistentVolumeClaim.getProperties().put("claimName", claimName + "-pvc");
            }
        }


        PropertyValueList ports = new PropertyValueList();
        container.getProperties().put("ports", ports);

        Specification port = new Specification(CONTAINER_PORT_SCHEMA_ID);
        port.getProperties().put("containerPort", getTargetPort(component));
        ports.getPropertyValues().add(port);

        return deployment;
    }

    private String getClaimName(String storageName, Component component) {
        String componentName = toKubernetesNamespace(component.getName());
        return storageName;
    }

    private Manifest createService(String namespace, String name, Component component, List<Label> labels) {
        Manifest service = new Manifest(SERVICE_SCHEMA_ID);

        PropertyMap metadataMap = new PropertyMap();
        metadataMap.put("namespace", namespace);

        String deploymentName = name;
        metadataMap.put("name", deploymentName);

        service.getProperties().put("metadata", metadataMap);

        Specification serviceSpecification = new Specification(SERVICE_SPEC_SCHEMA_ID);
        service.getProperties().put("spec", serviceSpecification);

        PropertyMap selectorMap = new PropertyMap();
        selectorMap.putAll(toMap(labels));
        serviceSpecification.getProperties().put("selector", selectorMap);

        PropertyValueList ports = new PropertyValueList();
        serviceSpecification.getProperties().put("ports", ports);

        for (Port port : component.getPorts()) {
            Specification servicePort = new Specification(SERVICE_PORT_SCHEMA_ID);
            servicePort.getProperties().put("port", getPort(component));
            servicePort.getProperties().put("targetPort", port.getNumber());
            servicePort.getProperties().put("protocol", port.getProtocol().toString());
            ports.getPropertyValues().add(servicePort);
        }

        serviceSpecification.getProperties().put("type", getServiceType(component));

        return service;
    }

    private Manifest createSecret(String namespace, String name, Component component, List<Label> labels) {
        Manifest secret = new Manifest(KubernetesConstants.SECRET_SCHEMA_ID);

        PropertyMap metadataMap = new PropertyMap();
        metadataMap.put("namespace", namespace);

        String deploymentName = name;
        metadataMap.put("name", deploymentName);

        secret.getProperties().put("metadata", metadataMap);

        PropertyMap dataMap = new PropertyMap();
        secret.getProperties().put("data", dataMap);

        for (Variable variable : component.getVariables()) {
            if (variable.getValue() instanceof Password) {
                Password password = (Password) variable.getValue();
                if (password instanceof ProvidedBase64EncodedPassword) {
                    ProvidedBase64EncodedPassword providedBase64EncodedPassword = (ProvidedBase64EncodedPassword) password;
                    dataMap.put(variable.getKey(), providedBase64EncodedPassword.getEncodedValue());
                } else {
                    throw new RuntimeException("Unsupported password type: " + password.getClass().getName());
                }
            } else {
                //throw new RuntimeException("Unsupported value type: " + environmentVariable.getValue().getClass().getName());
            }
        }

        return secret;
    }

    private Manifest createConfigMap(String namespace, String name, Component component, List<Label> labels, ApplicationEnvironment applicationEnvironment) {
        Manifest configMap = new Manifest(CONFIG_MAP_SCHEMA_ID);

        PropertyMap metadataMap = new PropertyMap();
        metadataMap.put("namespace", namespace);

        String deploymentName = name;
        metadataMap.put("name", deploymentName);

        configMap.getProperties().put("metadata", metadataMap);

        PropertyMap dataMap = new PropertyMap();
        configMap.getProperties().put("data", dataMap);

        for (Variable variable : component.getVariables()) {

            if (variable.getValue() instanceof HostName) {
                dataMap.put(variable.getKey(), toKubernetesNamespace(((HostName) variable.getValue()).getComponent().getName()));
            } else if (variable.getValue() instanceof DomainName) {
                DomainName domainName = (DomainName) variable.getValue();
                if (domainName.getRoute() != null && domainName.getRoute() instanceof SubDomainRoute) {
                    dataMap.put(variable.getKey(), ((SubDomainRoute) domainName.getRoute()).getSubDomain() + "." + applicationEnvironment.getDomainName());
                } else {
                    throw new RuntimeException("Unsupported value type: " + variable.getValue().getClass().getName());
                }
            } else if (variable.getValue() instanceof ConstantValue) {
                dataMap.put(variable.getKey(), ((ConstantValue) variable.getValue()).getValue());
            } else {
                //throw new RuntimeException("Unsupported value type: " + environmentVariable.getValue().getClass().getName());
            }
        }

        return configMap;
    }

    private Manifest createPersistentVolume(String namespace, String name, Storage storage) {
        Manifest pvc = new Manifest(PERSISTENT_VOLUME_SCHEMA_ID);

        PropertyMap metadataMap = new PropertyMap();
        pvc.getProperties().put("metadata", metadataMap);

        metadataMap.put("namespace", namespace);
        metadataMap.put("name", name + "-pv");

        Specification spec = new Specification(PERSISTENT_VOLUME_SPEC_SCHEMA_ID);
        pvc.getProperties().put("spec", spec);

        spec.getProperties().put("storageClassName", getStorageClassName(namespace));

        PropertyMap capacity = new PropertyMap();
        spec.getProperties().put("capacity", capacity);
        capacity.put("storage", storage.getSizeInKubernetesFormat());

        PropertyValueList accessModes = new PropertyValueList();
        spec.getProperties().put("accessModes", accessModes);

        accessModes.add(new StringValue(getAccessMode(storage)));

        PropertyMap hostPath = new PropertyMap();
        spec.getProperties().put("hostPath", hostPath);
        hostPath.put("path", config.getStorageClassPersistentVolumeDirectory() + "/" + name);

        return pvc;
    }

    private Manifest createPersistentVolumeClaim(String namespace, String name, Storage storage) {
        Manifest pvc = new Manifest(PERSISTENT_VOLUME_CLAIM_SCHEMA_ID);

        PropertyMap metadataMap = new PropertyMap();
        pvc.getProperties().put("metadata", metadataMap);

        metadataMap.put("namespace", namespace);
        metadataMap.put("name", name + "-pvc");

        Specification persistentVolumeClaimSpec = new Specification(PERSISTENT_VOLUME_CLAIM_SPEC_SCHEMA_ID);
        pvc.getProperties().put("spec", persistentVolumeClaimSpec);

        persistentVolumeClaimSpec.getProperties().put("storageClassName", getStorageClassName(namespace));

        persistentVolumeClaimSpec.getProperties().put("volumeName", name + "-pv");

        PropertyValueList accessModes = new PropertyValueList();
        persistentVolumeClaimSpec.getProperties().put("accessModes", accessModes);

        accessModes.add(new StringValue(getAccessMode(storage)));

        Specification resources = new Specification(RESOURCE_REQUIREMENTS_SCHEMA_ID);
        persistentVolumeClaimSpec.putProperty("resources", resources);

        PropertyMap requests = new PropertyMap();
        resources.putProperty("requests", requests);

        requests.put("storage", storage.getSizeInKubernetesFormat());

        return pvc;
    }

    private Manifest createStorageClass(String namespace, ApplicationEnvironment applicationEnvironment) {
        Manifest storageClass = new Manifest(STORAGE_CLASS_SCHEMA_ID);

        PropertyMap metadataMap = new PropertyMap();
        storageClass.getProperties().put("metadata", metadataMap);

        metadataMap.put("namespace", namespace);
        metadataMap.put("name", getStorageClassName(namespace));

        storageClass.getProperties().put("provisioner", config.getStorageClassProvisioner());

        storageClass.getProperties().put("reclaimPolicy", getReclaimPolicy(applicationEnvironment));

        PropertyMap parameters = new PropertyMap();
        storageClass.putProperty("parameters", parameters);

        parameters.put("pvDir", config.getStorageClassPersistentVolumeDirectory());

        storageClass.getProperties().put("volumeBindingMode", "WaitForFirstConsumer");

        return storageClass;
    }

    private Manifest createIssuer(String namespace) {
        Manifest issuer = new Manifest(CERT_MANAGER_ISSUER_SCHEMA_ID);
        PropertyMap metadataMap = new PropertyMap();
        issuer.getProperties().put("metadata", metadataMap);

        metadataMap.put("namespace", namespace);
        metadataMap.put("name", getIssuerName(namespace));

        Specification issuerSpec = new Specification(CERT_MANAGER_ISSUER_SPEC_SCHEMA_ID);
        issuer.getProperties().put("spec", issuerSpec);

        Specification acme = new Specification(CERT_MANAGER_ACME_ISSUER_SCHEMA_ID);
        issuerSpec.getProperties().put("acme", acme);

        acme.getProperties().put("email", config.getAutomatedCertificateManagementEnvironmentAdministratorEmail());
        acme.getProperties().put("server", config.getAutomatedCertificateManagementEnvironmentServer());

        PropertyMap privateKeySecretRef = new PropertyMap();
        acme.getProperties().put("privateKeySecretRef", privateKeySecretRef);

        privateKeySecretRef.put("name", getPrivateKeySecretName(namespace));

        PropertyValueList solvers = new PropertyValueList();
        acme.getProperties().put("solvers", solvers);

        Specification http01 = new Specification(CERT_MANAGER_ACME_CHALLENGE_SOLVER_SCHEMA_ID);
        solvers.add(http01);

        Specification http01Solver = new Specification(CERT_MANAGER_ACME_CHALLENGE_SOLVER_HTTP_O1_SCHEMA_ID);
        http01.putProperty("http01", http01Solver);

        PropertyMap ingress = new PropertyMap();
        http01Solver.getProperties().put("ingress", ingress);

        ingress.put("class", config.getAutomatedCertificateManagementEnvironmentHttp01SolverIngressClass());

        return issuer;
    }

    private String getIssuerName(String namespace) {
        return namespace + "-issuer";
    }

    private String getStorageClassName(String namespace) {
        return namespace + "-data";
    }

    private String getPrivateKeySecretName(String namespace) {
        return namespace + "-account-key";
    }

    private String getReclaimPolicy(ApplicationEnvironment applicationEnvironment) {
        if (applicationEnvironment.getEnvironment().isDoBackups()) {
            return "Retain";
        }
        return "Delete";
    }

    private String getAccessMode(Storage storage) {
        if (StorageUsage.Mode.ReadWrite.equals(getHighestStorageUsageMode(storage))) {
            return "ReadWriteOnce";
        }
        return "ReadOnlyMany";
    }

    private StorageUsage.Mode getHighestStorageUsageMode(Storage storage) {
        StorageUsage.Mode highestMode = null;
        for (Component component : storage.getApplication().getComponents()) {
            for (StorageUsage storageUsage : component.getStorageUsages()) {
                if (storageUsage.getStorage().equals(storage)) {
                    if (highestMode == null || storageUsage.getMode().ordinal() > highestMode.ordinal()) {
                        highestMode = storageUsage.getMode();
                    }
                }
            }
        }
        return highestMode;
    }

    private Manifest createIngress(String namespace, String name, ApplicationEnvironment applicationEnvironment) {
        Manifest ingress = new Manifest(INGRESS_SCHEMA_ID);

        PropertyMap metadataMap = new PropertyMap();
        ingress.getProperties().put("metadata", metadataMap);

        metadataMap.put("namespace", namespace);
        metadataMap.put("name", name);

        if (applicationEnvironment.getEnvironment().isHttpsEnabled()) {
            PropertyMap annotations = new PropertyMap();
            metadataMap.put("annotations", annotations);

            annotations.put("cert-manager.io/issuer", getIssuerName(namespace));
            annotations.put("ingress.kubernetes.io/force-ssl-redirect", config.getIngressForceSslRedirect());
            annotations.put("kubernetes.io/ingress.class", config.getAutomatedCertificateManagementEnvironmentHttp01SolverIngressClass());
            annotations.put("kubernetes.io/tls-acme", "true");
        }

        Specification ingressSpec = new Specification(INGRESS_SPEC_SCHEMA_ID);
        ingress.getProperties().put("spec", ingressSpec);

        if (applicationEnvironment.getEnvironment().isHttpsEnabled()) {
            PropertyValueList tls = new PropertyValueList();
            ingressSpec.putProperty("tls", tls);

            Specification ingressTls = new Specification(INGRESS_TLS_SCHEMA_ID);
            tls.add(ingressTls);

            ingressTls.getProperties().put("secretName", getPrivateKeySecretName(namespace));

            PropertyValueList hosts = new PropertyValueList();
            ingressTls.putProperty("hosts", hosts);

            for (Route route : applicationEnvironment.getApplication().getRoutes()) {
                hosts.add(new StringValue(getHostname(route, applicationEnvironment)));
            }
        }

        PropertyValueList rules = new PropertyValueList();
        ingressSpec.putProperty("rules", rules);

        for (Route route : applicationEnvironment.getApplication().getRoutes()) {
            if (route instanceof SubDomainRoute) {
                SubDomainRoute subDomainRoute = (SubDomainRoute) route;
                rules.add(createRule(getHostname(route, applicationEnvironment), toKubernetesNamespace(subDomainRoute.getPort().getComponent().getName())));
            } else if (route instanceof DefaultRoute) {
                DefaultRoute defaultRoute = (DefaultRoute) route;
                rules.add(createRule(getHostname(route, applicationEnvironment), toKubernetesNamespace(defaultRoute.getPort().getComponent().getName())));
            } else {
                throw new RuntimeException("Unsupported route type: " + route.getClass().getName());
            }
        }

        return ingress;
    }

    private String getHostname(Route route, ApplicationEnvironment applicationEnvironment) {
        if (route instanceof SubDomainRoute) {
            SubDomainRoute subDomainRoute = (SubDomainRoute) route;
            return subDomainRoute.getSubDomain() + "." + applicationEnvironment.getDomainName();
        } else if (route instanceof DefaultRoute) {
            return applicationEnvironment.getDomainName();
        } else {
            throw new RuntimeException("Unsupported route type: " + route.getClass().getName());
        }
    }

    private Specification createRule(String hostName, String serviceName) {
        Specification rule = new Specification(INGRESS_RULE_SCHEMA_ID);

        rule.getProperties().put("host", hostName);

        Specification http = new Specification(HTTP_INGRESS_RULE_VALUE_SCHEMA_ID);
        rule.putProperty("http", http);

        PropertyValueList paths = new PropertyValueList();
        http.putProperty("paths", paths);

        Specification path = new Specification(HTTP_INGRESS_PATH_SCHEMA_ID);
        paths.add(path);

        path.getProperties().put("path", "/");
        path.getProperties().put("pathType", "Prefix");

        Specification backend = new Specification(INGRESS_BACKEND_SCHEMA_ID);
        path.putProperty("backend", backend);

        Specification service = new Specification(INGRESS_SERVICE_BACKEND_SCHEMA_ID);
        backend.putProperty("service", service);

        service.getProperties().put("name", serviceName);

        Specification serviceBackendPort = new Specification(SERVICE_BACKEND_PORT_SCHEMA_ID);
        service.putProperty("port", serviceBackendPort);

        serviceBackendPort.getProperties().put("number", 80);
        return rule;
    }

    private PropertyMap toMap(List<Label> labels) {
        PropertyMap map = new PropertyMap();
        for (Label label : labels) {
            map.put(label.key, new StringValue(label.value));
        }
        return map;
    }

    private PropertyMap toPropertyMap(List<Label> labels) {
        PropertyMap map = new PropertyMap();
        for (Label label : labels) {
            map.put(label.key, new StringValue(label.value));
        }
        return map;
    }

    private boolean requiresSecret(Component component) {
        return component.getVariables().stream().anyMatch(environmentVariable -> environmentVariable.getValue() instanceof Password);
    }

    private boolean requiresConfigMap(Component component) {
        return component.getVariables().stream().anyMatch(environmentVariable ->
                environmentVariable.getValue() instanceof HostName
                        || environmentVariable.getValue() instanceof DomainName
                        || environmentVariable.getValue() instanceof ConstantValue);
    }

    private String getImage(Component component) {
        if (component instanceof Database) {
            if ("mysql".equals(((Database) component).getDatabaseType())) {
                return "mysql:5.7";
            } else if ("postgres".equals(((Database) component).getDatabaseType())) {
                return "postgres:latest";
            } else {
                throw new RuntimeException("Unsupported database type: " + ((Database) component).getDatabaseType());
            }
        }
        return component.getImage();
    }

    private void addArgs(Component component, Specification container) {
        if (component instanceof Database) {
            if ("mysql".equals(((Database) component).getDatabaseType())) {
                PropertyValueList args = new PropertyValueList();
                args.add(new StringValue("--ignore-db-dir=lost+found"));
                container.getProperties().put("args", args);
            }
        }
    }

    private short getPort(Component component) {
        if (component instanceof Database) {
            return 3306;
        }
        return 80;
    }

    private short getTargetPort(Component component) {
        if (component instanceof Database) {
            return 3306;
        }
        if (component.getPorts().isEmpty()) {
            throw new RuntimeException("Must have at least one port");
        }
        return (short) component.getPorts().get(0).getNumber();
    }

    private String getServiceType(Component component) {
        if (component instanceof Database) {
            return "ClusterIP";
        }
        return "LoadBalancer";
    }


}

class Label {

    public String key;

    public String value;

    public Label(String key, String value) {
        this.key = key;
        this.value = value;
    }
}