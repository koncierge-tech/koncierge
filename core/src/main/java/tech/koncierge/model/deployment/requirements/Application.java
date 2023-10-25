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

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

public class Application {

    @NotEmpty
    private String shortName;

    @NotEmpty
    private String longName;

    @NotEmpty
    @Valid
    private List<Component> components = new ArrayList<>();

    @NotEmpty
    private List<ApplicationEnvironment> applicationEnvironments = new ArrayList<>();

    private List<Route> routes = new ArrayList<>();

    private List<Value> values = new ArrayList<>();

    private List<Storage> storages = new ArrayList<>();

    public Application(DeploymentRequirements deploymentRequirements) {
        deploymentRequirements.getApplications().add(this);
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public List<Component> getComponents() {
        return components;
    }

    public void setComponents(List<Component> components) {
        this.components = components;
    }

    public List<ApplicationEnvironment> getApplicationEnvironments() {
        return applicationEnvironments;
    }

    public void setApplicationEnvironments(List<ApplicationEnvironment> applicationEnvironments) {
        this.applicationEnvironments = applicationEnvironments;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public List<Value> getValues() {
        return values;
    }

    public List<Storage> getStorages() {
        return storages;
    }
}
