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
import tech.koncierge.model.deployment.requirements.values.DomainName;
import tech.koncierge.model.deployment.requirements.values.HostName;

import java.util.ArrayList;
import java.util.List;

public class Component {

    private Application application;

    @NotEmpty
    private String name;

    private String image;

    private HostName hostName;

    private DomainName domainName;

    @Valid
    private List<Variable> variables = new ArrayList<>();

    private List<Port> ports = new ArrayList<>();

    private List<StorageUsage> storageUsages = new ArrayList<>();

    public Component(Application application) {
        this.application = application;
        application.getComponents().add(this);
        hostName = new HostName(this);
        domainName = new DomainName(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public void setVariables(List<Variable> variables) {
        this.variables = variables;
    }

    public HostName getHostName() {
        return hostName;
    }

    public DomainName getDomainName() {
        return domainName;
    }

    public Application getApplication() {
        return application;
    }

    public List<Port> getPorts() {
        return ports;
    }

    public List<StorageUsage> getStorageUsages() {
        return storageUsages;
    }
}
