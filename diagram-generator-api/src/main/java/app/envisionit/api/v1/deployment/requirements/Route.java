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

import app.envisionit.api.v1.deployment.requirements.routes.DefaultRoute;
import app.envisionit.api.v1.deployment.requirements.routes.PathPrefixRoute;
import app.envisionit.api.v1.deployment.requirements.routes.SubDomainRoute;
import app.envisionit.api.v1.deployment.requirements.values.DomainName;
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
        @JsonSubTypes.Type(value = DefaultRoute.class, name = "DefaultRoute"),
        @JsonSubTypes.Type(value = PathPrefixRoute.class, name = "PathPrefixRoute"),
        @JsonSubTypes.Type(value = SubDomainRoute.class, name = "SubDomainRoute")
})
public class Route {

    private Port port;

    private DomainName domainName;

    public void setPort(Port port) {
        this.port = port;
    }

    public void setDomainName(DomainName domainName) {
        this.domainName = domainName;
    }

    public Port getPort() {
        return port;
    }

    public DomainName getDomainName() {
        return domainName;
    }
}
