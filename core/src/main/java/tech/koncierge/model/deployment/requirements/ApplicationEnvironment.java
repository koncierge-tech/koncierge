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

public class ApplicationEnvironment {

    private Application application;
    private Environment environment;
    private String domainName;
    private List<ApplicationEnvironment> subsequentApplicationEnvironments = new ArrayList<>();

    public ApplicationEnvironment(Application application, Environment environment, String domainName) {
        this.application = application;
        application.getApplicationEnvironments().add(this);
        this.environment = environment;
        this.domainName = domainName;
    }

    public Application getApplication() {
        return application;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public String getDomainName() {
        return domainName;
    }

    public void addSubsequentApplicationEnvironment(ApplicationEnvironment subsequentApplicationEnvironment) {
        subsequentApplicationEnvironments.add(subsequentApplicationEnvironment);
    }

    public List<ApplicationEnvironment> getSubsequentApplicationEnvironments() {
        return subsequentApplicationEnvironments;
    }
}
