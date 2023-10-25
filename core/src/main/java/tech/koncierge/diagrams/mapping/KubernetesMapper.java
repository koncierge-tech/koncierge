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
package tech.koncierge.diagrams.mapping;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import tech.koncierge.model.kubernetes.ClusterConfiguration;
import tech.koncierge.model.kubernetes.KubernetesConfiguration;
import tech.koncierge.model.kubernetes.Manifest;
import tech.koncierge.model.kubernetes.PropertyMap;
import tech.koncierge.model.kubernetes.PropertyValue;
import tech.koncierge.model.kubernetes.PropertyValueList;
import tech.koncierge.model.kubernetes.SimpleValue;
import tech.koncierge.model.kubernetes.Specification;
import tech.koncierge.model.kubernetes.simple.IntValue;
import tech.koncierge.model.kubernetes.simple.StringValue;

@Mapper
public interface KubernetesMapper {

    KubernetesMapper INSTANCE = Mappers.getMapper(KubernetesMapper.class);

    default app.envisionit.api.v1.kubernetes.simple.IntValue map(IntValue intValue, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext) {
        app.envisionit.api.v1.kubernetes.simple.IntValue result = new app.envisionit.api.v1.kubernetes.simple.IntValue();
        result.setIntValue((Integer) intValue.getValue());
        return result;
    }

    default app.envisionit.api.v1.kubernetes.simple.StringValue map(StringValue stringValue, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext) {
        app.envisionit.api.v1.kubernetes.simple.StringValue result = new app.envisionit.api.v1.kubernetes.simple.StringValue();
        result.setStringValue((String) stringValue.getValue());
        return result;
    }

    app.envisionit.api.v1.kubernetes.ClusterConfiguration map(ClusterConfiguration clusterConfiguration, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    app.envisionit.api.v1.kubernetes.KubernetesConfiguration map(KubernetesConfiguration kubernetesConfiguration, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    app.envisionit.api.v1.kubernetes.Manifest map(Manifest manifest, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    app.envisionit.api.v1.kubernetes.PropertyMap map(PropertyMap propertyMap, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    default app.envisionit.api.v1.kubernetes.PropertyValue map(PropertyValue propertyValue, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext) {
        if (propertyValue instanceof PropertyMap) {
            return map((PropertyMap) propertyValue, cycleAvoidingMappingContext);
        } else if (propertyValue instanceof PropertyValueList) {
            return map((PropertyValueList) propertyValue, cycleAvoidingMappingContext);
        } else if (propertyValue instanceof Specification) {
            return map((Specification) propertyValue, cycleAvoidingMappingContext);
        } else if (propertyValue instanceof SimpleValue) {
            return map((SimpleValue) propertyValue, cycleAvoidingMappingContext);
        } else {
            throw new IllegalArgumentException("Unexpected type: " + propertyValue.getClass());
        }
    }

    app.envisionit.api.v1.kubernetes.PropertyValueList map(PropertyValueList propertyValueList, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    default app.envisionit.api.v1.kubernetes.SimpleValue map(SimpleValue simpleValue, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext) {
        if (simpleValue instanceof StringValue) {
            return map((StringValue) simpleValue, cycleAvoidingMappingContext);
        } else if (simpleValue instanceof IntValue) {
            return map((IntValue) simpleValue, cycleAvoidingMappingContext);
        } else {
            throw new IllegalArgumentException("Unexpected type: " + simpleValue.getClass());
        }
    }

    app.envisionit.api.v1.kubernetes.Specification map(Specification specification, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);
}
