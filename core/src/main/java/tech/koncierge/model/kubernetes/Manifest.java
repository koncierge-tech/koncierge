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
package tech.koncierge.model.kubernetes;

public class Manifest extends Specification {

    public Manifest(String schemaId) {
        super(schemaId);

        String kind = getKind(schemaId);
        properties.put("apiVersion", getApiVersion(kind));
        properties.put("kind", kind);
    }

    private static String getKind(String schemaId) {
        int lastDot = schemaId.lastIndexOf(".");
        if (lastDot == -1) {
            throw new RuntimeException("Unknown schemaId");
        }
        String kind = schemaId.substring(lastDot + 1);
        return kind;
    }

    private static String getApiVersion(String kind) {
        if ("Deployment".equals(kind)) {
            return "apps/v1";
        } else if ("Ingress".equals(kind)) {
            return "networking.k8s.io/v1";
        } else if ("StorageClass".equals(kind)) {
            return "storage.k8s.io/v1";
        } else if ("Issuer".equals(kind)) {
            return "cert-manager.io/v1";
        } else {
            return "v1";
        }
    }
}
