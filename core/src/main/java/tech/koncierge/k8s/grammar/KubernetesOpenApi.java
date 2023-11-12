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
package tech.koncierge.k8s.grammar;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.parser.OpenAPIV3Parser;
public class KubernetesOpenApi {

    private static OpenAPI openAPI;

    public static OpenAPI getOpenAPI() {
        if (openAPI == null) {
            openAPI = parse();
        }
        return openAPI;
    }

    private static OpenAPI parse() {
        OpenAPIV3Parser parser = new OpenAPIV3Parser();
        OpenAPI openAPI = parser.read("/kubernetes/k8s-swagger.json");
        return openAPI;
    }

    public static Schema getSchema(String key) {
        return getOpenAPI().getComponents().getSchemas().get(key);
    }

    public static String findSchemaId(String kind) {
        for (String key : getOpenAPI().getComponents().getSchemas().keySet()) {
            String partAfterLastDot = key.substring(key.lastIndexOf(".") + 1);
            if (partAfterLastDot.equals(kind)) {
                return key;
            }
        }
        return null;
    }

    public static String getSchemaNameFromRef(String ref) {
        String key = ref.substring(ref.lastIndexOf("/") + 1);
        return key;
    }

    public static Schema getSchemaFromRef(String ref) {
        String key = ref.substring(ref.lastIndexOf("/") + 1);
        return getSchema(key);
    }

    public static void main(String[] args) {
        OpenAPI openAPI = parse();
        System.out.println(openAPI);
    }
}

