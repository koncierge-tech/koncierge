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
package tech.koncierge.model.kubernetes.simple;

import tech.koncierge.model.kubernetes.SimpleValue;

public class IntValue implements SimpleValue {

    private int intValue;

    public IntValue(int value) {
        this.intValue = value;
    }

    @Override
    public Object getValue() {
        return intValue;
    }
}
