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
package tech.koncierge.diagrams.mapping;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;
import org.mapstruct.factory.Mappers;
import tech.koncierge.model.deployment.requirements.Application;
import tech.koncierge.model.deployment.requirements.ApplicationEnvironment;
import tech.koncierge.model.deployment.requirements.Component;
import tech.koncierge.model.deployment.requirements.Database;
import tech.koncierge.model.deployment.requirements.DeploymentRequirements;
import tech.koncierge.model.deployment.requirements.Environment;
import tech.koncierge.model.deployment.requirements.Port;
import tech.koncierge.model.deployment.requirements.Route;
import tech.koncierge.model.deployment.requirements.Storage;
import tech.koncierge.model.deployment.requirements.StorageUsage;
import tech.koncierge.model.deployment.requirements.Value;
import tech.koncierge.model.deployment.requirements.Variable;
import tech.koncierge.model.deployment.requirements.routes.DefaultRoute;
import tech.koncierge.model.deployment.requirements.routes.PathPrefixRoute;
import tech.koncierge.model.deployment.requirements.routes.SubDomainRoute;
import tech.koncierge.model.deployment.requirements.values.ConstantValue;
import tech.koncierge.model.deployment.requirements.values.DomainName;
import tech.koncierge.model.deployment.requirements.values.HostName;
import tech.koncierge.model.deployment.requirements.values.Password;
import tech.koncierge.model.deployment.requirements.values.passwords.GeneratedPassword;
import tech.koncierge.model.deployment.requirements.values.passwords.ProvidedBase64EncodedPassword;
import tech.koncierge.model.deployment.requirements.values.passwords.ProvidedPassword;

@Mapper
public interface DeploymentRequirementsMapper {

    DeploymentRequirementsMapper INSTANCE = Mappers.getMapper( DeploymentRequirementsMapper.class );

    app.envisionit.api.v1.deployment.requirements.routes.DefaultRoute map(DefaultRoute defaultRoute, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    app.envisionit.api.v1.deployment.requirements.routes.PathPrefixRoute map(PathPrefixRoute pathPrefixRoute, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    app.envisionit.api.v1.deployment.requirements.routes.SubDomainRoute map(SubDomainRoute subDomainRoute, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    app.envisionit.api.v1.deployment.requirements.values.passwords.GeneratedPassword map(GeneratedPassword generatedPassword, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    app.envisionit.api.v1.deployment.requirements.values.passwords.ProvidedBase64EncodedPassword map(ProvidedBase64EncodedPassword providedBase64EncodedPassword, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    app.envisionit.api.v1.deployment.requirements.values.passwords.ProvidedPassword map(ProvidedPassword providedPassword, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    app.envisionit.api.v1.deployment.requirements.values.ConstantValue map(ConstantValue constantValue, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    app.envisionit.api.v1.deployment.requirements.values.DomainName map(DomainName domainName, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    app.envisionit.api.v1.deployment.requirements.values.HostName map(HostName hostName, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    @SubclassMapping( source = GeneratedPassword.class, target = app.envisionit.api.v1.deployment.requirements.values.passwords.GeneratedPassword.class)
    @SubclassMapping( source = ProvidedBase64EncodedPassword.class, target = app.envisionit.api.v1.deployment.requirements.values.passwords.ProvidedBase64EncodedPassword.class)
    @SubclassMapping( source = ProvidedPassword.class, target = app.envisionit.api.v1.deployment.requirements.values.passwords.ProvidedPassword.class)
    app.envisionit.api.v1.deployment.requirements.values.Password map(Password password, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    app.envisionit.api.v1.deployment.requirements.Application map(Application application, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    app.envisionit.api.v1.deployment.requirements.ApplicationEnvironment map(ApplicationEnvironment applicationEnvironment, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    app.envisionit.api.v1.deployment.requirements.Component map(Component component, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    app.envisionit.api.v1.deployment.requirements.Database map(Database database, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    app.envisionit.api.v1.deployment.requirements.DeploymentRequirements map(DeploymentRequirements deploymentRequirements, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    app.envisionit.api.v1.deployment.requirements.Environment map(Environment environment, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    app.envisionit.api.v1.deployment.requirements.Port map(Port port, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    @SubclassMapping( source = DefaultRoute.class, target = app.envisionit.api.v1.deployment.requirements.routes.DefaultRoute.class)
    @SubclassMapping( source = PathPrefixRoute.class, target = app.envisionit.api.v1.deployment.requirements.routes.PathPrefixRoute.class)
    @SubclassMapping( source = SubDomainRoute.class, target = app.envisionit.api.v1.deployment.requirements.routes.SubDomainRoute.class)
    app.envisionit.api.v1.deployment.requirements.Route map(Route route, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    app.envisionit.api.v1.deployment.requirements.Storage map(Storage storage, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    app.envisionit.api.v1.deployment.requirements.StorageUsage map(StorageUsage storageUsage, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    @SubclassMapping( source = ConstantValue.class, target = app.envisionit.api.v1.deployment.requirements.values.ConstantValue.class)
    @SubclassMapping( source = DomainName.class, target = app.envisionit.api.v1.deployment.requirements.values.DomainName.class)
    @SubclassMapping( source = HostName.class, target = app.envisionit.api.v1.deployment.requirements.values.HostName.class)
    @SubclassMapping( source = Password.class, target = app.envisionit.api.v1.deployment.requirements.values.Password.class)
    app.envisionit.api.v1.deployment.requirements.Value map(Value value, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    app.envisionit.api.v1.deployment.requirements.Variable map(Variable variable, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);
}
