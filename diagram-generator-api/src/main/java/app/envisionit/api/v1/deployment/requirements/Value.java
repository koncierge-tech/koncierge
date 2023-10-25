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
package app.envisionit.api.v1.deployment.requirements;

import app.envisionit.api.v1.deployment.requirements.values.ConstantValue;
import app.envisionit.api.v1.deployment.requirements.values.DomainName;
import app.envisionit.api.v1.deployment.requirements.values.HostName;
import app.envisionit.api.v1.deployment.requirements.values.Password;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.ArrayList;
import java.util.List;

@JsonIdentityInfo(generator= ObjectIdGenerators.IntSequenceGenerator.class)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ConstantValue.class, name = "ConstantValue"),
        @JsonSubTypes.Type(value = DomainName.class, name = "DomainName"),
        @JsonSubTypes.Type(value = HostName.class, name = "HostName"),
        @JsonSubTypes.Type(value = Password.class, name = "Password")
})
public class Value {

    private List<Variable> assignToVariables = new ArrayList<Variable>();

    public void setAssignToVariables(List<Variable> assignToVariables) {
        this.assignToVariables = assignToVariables;
    }

    public List<Variable> getAssignToVariables() {
        return assignToVariables;
    }
}
