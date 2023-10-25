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
package tech.koncierge.config;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Config {

    private static Map<String, String> apiKeyByModelId = null;

    private PropertiesConfiguration config;

    public Config(String workingDirectory) {
        init(workingDirectory);
    }

    public static String getApiKey(String modelId) {
        if (apiKeyByModelId == null) {
            init();
        }
        String apiKey = apiKeyByModelId.get(modelId);
        return apiKey;
    }

    private void init(String workingDirectory) {
        Configurations configs = new Configurations();
        try {
            if (workingDirectory == null) {
                config = configs.properties((String)null);
                return;
            }
            PropertiesConfiguration config = configs.properties(workingDirectory + "/config/config.properties");
            config.setProperty("working.directory", workingDirectory);
            this.config = config;
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private static void init() {
        Map<String, String> map = new HashMap<>();

        Configurations configs = new Configurations();

        try {
            PropertiesConfiguration config = configs.properties("config/api-keys.properties");
            Iterator<String> keys = config.getKeys();
            while (keys.hasNext()) {
                String key = keys.next();
                map.put(key, config.getString(key));
            }
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }

        apiKeyByModelId = map;
    }

    public String getWorkingDirectory() {
        return config.getString("working.directory");
    }

    public String getOutputDirectory() {
        return config.getString("output.directory");
    }

    public String getModelId() {
        return config.getString("generate.diagrams.model.id");
    }

    public String getApiKey() {
        return config.getString("generate.diagrams.api.key");
    }

    public boolean isGenerateDiagramsEnabled() {
        return config.getBoolean("generate.diagrams.enabled");
    }

    public String getStorageClassProvisioner() {
        return config.getString("storage.class.provisioner", "microk8s.io/hostpath");
    }

    public String getStorageClassPersistentVolumeDirectory() {
        return config.getString("generate.k8s.storage.class.pv.dir", "/data/k8s-storage");
    }

    public String getAutomatedCertificateManagementEnvironmentAdministratorEmail() {
        return config.getString("generate.k8s.acme.admin.email", "acme.administrator@example.com");
    }

    public String getAutomatedCertificateManagementEnvironmentServer() {
        return config.getString("generate.k8s.acme.server", "https://acme-staging-v02.api.letsencrypt.org/directory");
    }

    public String getAutomatedCertificateManagementEnvironmentHttp01SolverIngressClass() {
        return config.getString("generate.k8s.acme.solvers.http01.ingress.class", "contour");
    }

    public String getIngressForceSslRedirect() {
        return config.getString("generate.k8s.ingress.force.ssl.redirect", "false");
    }
}
