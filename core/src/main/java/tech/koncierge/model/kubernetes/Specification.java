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

public class Specification implements PropertyValue {

    private String schemaId;

    protected PropertyMap properties = new PropertyMap();

    public Specification(String schemaId) {
        this.schemaId = schemaId;
        if (schemaId == null) {
            throw new RuntimeException("schemaId cannot be null");
        }

    }

    public String getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
    }

    public PropertyMap getProperties() {
        return properties;
    }

    public void putProperty(String key, PropertyValue value) {
        properties.put(key, value);
    }

}
