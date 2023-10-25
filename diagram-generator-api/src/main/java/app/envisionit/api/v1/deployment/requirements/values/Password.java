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
package app.envisionit.api.v1.deployment.requirements.values;

import app.envisionit.api.v1.deployment.requirements.Value;
import app.envisionit.api.v1.deployment.requirements.values.passwords.GeneratedPassword;
import app.envisionit.api.v1.deployment.requirements.values.passwords.ProvidedBase64EncodedPassword;
import app.envisionit.api.v1.deployment.requirements.values.passwords.ProvidedPassword;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator= ObjectIdGenerators.IntSequenceGenerator.class)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = GeneratedPassword.class, name = "GeneratedPassword"),
        @JsonSubTypes.Type(value = ProvidedBase64EncodedPassword.class, name = "ProvidedBase64EncodedPassword"),
        @JsonSubTypes.Type(value = ProvidedPassword.class, name = "ProvidedPassword")
})
public class Password extends Value {

}
