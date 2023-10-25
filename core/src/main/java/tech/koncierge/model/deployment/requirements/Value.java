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
package tech.koncierge.model.deployment.requirements;

import java.util.ArrayList;
import java.util.List;

public abstract class Value {

    private Application application;

    private List<Variable> assignToVariables = new ArrayList<Variable>();

    public Value(Application application) {
        this.application = application;
        application.getValues().add(this);
    }

    public void assignTo(Variable variable) {
        assignToVariables.add(variable);
        variable.setValue(this);
    }

    public List<Variable> getAssignToVariables() {
        return assignToVariables;
    }
}
