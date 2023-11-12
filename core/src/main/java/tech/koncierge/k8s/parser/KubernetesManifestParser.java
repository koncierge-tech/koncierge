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
package tech.koncierge.k8s.parser;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import org.yaml.snakeyaml.Yaml;
import tech.koncierge.k8s.grammar.KubernetesOpenApi;
import tech.koncierge.model.kubernetes.Manifest;
import tech.koncierge.model.kubernetes.PropertyValueList;
import tech.koncierge.model.kubernetes.SimpleValue;
import tech.koncierge.model.kubernetes.Specification;
import tech.koncierge.model.kubernetes.simple.IntValue;
import tech.koncierge.model.kubernetes.simple.StringValue;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KubernetesManifestParser {

    public List<Manifest> parseSpecifications(List<File> files) {
        List<Manifest> manifests = new ArrayList<>();
        for (File file : files) {
            try {
                InputStream inputStream = file.toURI().toURL().openStream();
                manifests.addAll(parseSpecification(inputStream));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return manifests;
    }

    public List<Manifest> parseSpecification(InputStream inputStream) {
        List<Manifest> manifests = new ArrayList<>();
        List<Map<String, Object>> yamlMaps = readYamlFile(inputStream);
        for (Map<String, Object> yamlMap : yamlMaps) {
            Manifest specification = new Manifest(getTopLevelSchemaId(yamlMap));
            parseProperties(specification, yamlMap);
            manifests.add(specification);
        }

        return manifests;
    }

    private void parseProperties(Specification specification, Map<String, Object> yamlMap) {
        for (String key : yamlMap.keySet()) {
            Object value = yamlMap.get(key);
            processValue(specification, key, value);
        }
    }

    private void processValue(Specification specification, String key, Object value) {
        Schema specificationSchema = getSchema(specification.getSchemaId());
        if (value instanceof Map) {
            Map<String, Object> valueMap = (Map<String, Object>) value;
            Specification propertySpecification = new Specification(getSchemaId(specificationSchema, key));
            specification.getProperties().put(key, propertySpecification);
            parseProperties(propertySpecification, valueMap);
        } else if (value instanceof List) {
            List<Object> valueList = (List<Object>) value;
            PropertyValueList propertyValueList = new PropertyValueList();
            specification.getProperties().put(key, propertyValueList);
            String propertySchemaId = getSchemaId(specificationSchema, key);
            for (Object listValue : valueList) {
                Specification propertySpecification = new Specification(propertySchemaId);
                propertySpecification.setSchemaId(propertySchemaId);
                propertyValueList.getPropertyValues().add(propertySpecification);
                Map<String, Object> valueMap = (Map<String, Object>) listValue;
                parseProperties(propertySpecification, valueMap);
            }
        } else {
            if (value != null && value instanceof Integer) {
                SimpleValue simpleValue = new IntValue((Integer) value);
                specification.getProperties().put(key, simpleValue);
            } else {
                SimpleValue simpleValue = new StringValue(value == null? null : value.toString());
                specification.getProperties().put(key, simpleValue);
            }
        }
    }

    private Schema getSchema(String schemaId) {
        OpenAPI api = KubernetesOpenApi.getOpenAPI();
        Schema schema = api.getComponents().getSchemas().get(schemaId);
        return schema;
    }

    private Schema getSchema(Schema parentSchema, String propertyName) {
        if (parentSchema.getProperties() == null) {
            return null;
        }
        Schema schema = (Schema) parentSchema.getProperties().get(propertyName);
        if (schema.getItems() != null) {
            return KubernetesOpenApi.getSchemaFromRef(schema.getItems().get$ref());
        }
        if (schema.get$ref() != null) {
            return KubernetesOpenApi.getSchemaFromRef(schema.get$ref());
        }
        return schema;
    }

    private String getSchemaId(Schema parentSchema, String propertyName) {
        if (parentSchema.getProperties() == null) {
            return null;
        }
        Schema schema = (Schema) parentSchema.getProperties().get(propertyName);
        if (schema.getItems() != null) {
            String id = schema.getItems().get$ref();
            return id.substring(id.lastIndexOf("/") + 1);
        }
        if (schema.get$ref() != null) {
            String id = schema.get$ref();
            return id.substring(id.lastIndexOf("/") + 1);
        }
        return schema.getType();
    }

    private String getTopLevelSchemaId(Map<String, Object> yamlMap) {
        if (yamlMap.containsKey("kind")) {
            String kind = (String) yamlMap.get("kind");
            String schemaId = KubernetesOpenApi.findSchemaId(kind);
            return schemaId;
        }
        throw new RuntimeException("Could not determine top level schema");
    }

    public List<Map<String, Object>> readYamlFile(InputStream inputStream) {
        Yaml yaml = new Yaml();
        Iterable<Object> obj = yaml.loadAll(inputStream);
        List<Map<String, Object>> ret = new ArrayList<>();
        while (obj.iterator().hasNext()) {
            ret.add((Map<String, Object>) obj.iterator().next());
        }
        return ret;
    }

}
