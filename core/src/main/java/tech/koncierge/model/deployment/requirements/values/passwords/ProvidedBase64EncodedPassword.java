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
package tech.koncierge.model.deployment.requirements.values.passwords;

import tech.koncierge.model.deployment.requirements.Application;

public class ProvidedBase64EncodedPassword extends ProvidedPassword {

    private String encodedValue;

    public ProvidedBase64EncodedPassword(Application application, String encodedValue) {
        super(application);
        this.encodedValue = encodedValue;
    }

    public String getEncodedValue() {
        return encodedValue;
    }

}
