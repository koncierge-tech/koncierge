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
package tech.koncierge.model.deployment.requirements.values;

import tech.koncierge.model.deployment.requirements.Component;
import tech.koncierge.model.deployment.requirements.Route;
import tech.koncierge.model.deployment.requirements.Value;

public class DomainName extends Value {

    private Component component;

    private Route route;

    public DomainName(Component component) {
        super(component.getApplication());
        this.component = component;
    }

    public DomainName(Route route) {
        super(route.getApplication());
        this.route = route;
    }

    public Component getComponent() {
        return component;
    }

    public Route getRoute() {
        return route;
    }

}
