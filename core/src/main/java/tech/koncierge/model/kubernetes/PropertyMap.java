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

import tech.koncierge.model.kubernetes.simple.IntValue;
import tech.koncierge.model.kubernetes.simple.StringValue;

import java.util.LinkedHashMap;

public class PropertyMap extends LinkedHashMap<String, PropertyValue> implements PropertyValue {

    public void put(String key, String value) {
        super.put(key, new StringValue(value));
    }

    public void put(String key, int value) {
        super.put(key, new IntValue(value));
    }

    public PropertyValue put(String key, Object value) {
        if (!(value instanceof PropertyValue)) {
            throw new RuntimeException("Value must be a PropertyValue");
        }
        super.put(key, (PropertyValue) value);
        return (PropertyValue) value;
    }

    @Override
    public PropertyValue put(String key, PropertyValue value) {
        return super.put(key, value);
    }
}
