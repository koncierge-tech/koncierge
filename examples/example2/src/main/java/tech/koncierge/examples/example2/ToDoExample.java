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
package tech.koncierge.examples.example2;

import tech.koncierge.config.Config;
import tech.koncierge.diagrams.DiagramGenerator;
import tech.koncierge.generator.ConfigurationGenerator;
import tech.koncierge.model.deployment.requirements.Storage;
import tech.koncierge.model.deployment.requirements.StorageUsage;
import tech.koncierge.model.deployment.requirements.Application;
import tech.koncierge.model.deployment.requirements.ApplicationEnvironment;
import tech.koncierge.model.deployment.requirements.Component;
import tech.koncierge.model.deployment.requirements.Database;
import tech.koncierge.model.deployment.requirements.DeploymentRequirements;
import tech.koncierge.model.deployment.requirements.Environment;
import tech.koncierge.model.deployment.requirements.Port;
import tech.koncierge.model.deployment.requirements.Route;
import tech.koncierge.model.deployment.requirements.Value;
import tech.koncierge.model.deployment.requirements.Variable;
import tech.koncierge.model.deployment.requirements.routes.DefaultRoute;
import tech.koncierge.model.deployment.requirements.routes.SubDomainRoute;
import tech.koncierge.model.deployment.requirements.values.ConstantValue;
import tech.koncierge.model.deployment.requirements.values.passwords.ProvidedBase64EncodedPassword;
import tech.koncierge.model.kubernetes.KubernetesConfiguration;
import tech.koncierge.util.WorkingDirectoryUtil;

public class ToDoExample {

    public static void main(String[] args) {
        DeploymentRequirements deploymentRequirements = createDeploymentRequirements();

        String workingDirectory = WorkingDirectoryUtil.getWorkingDirectory(ToDoExample.class);
        Config config = new Config(workingDirectory);

        DiagramGenerator.generateDeploymentRequirementsDiagrams(config, deploymentRequirements);

        KubernetesConfiguration kubernetesConfiguration = new ConfigurationGenerator(config, deploymentRequirements).generate();

        DiagramGenerator.generateKubernetesConfigurationDiagrams(config, kubernetesConfiguration);
    }

    public static DeploymentRequirements createDeploymentRequirements() {
        // This example is based on https://betterprogramming.pub/kubernetes-a-detailed-example-of-deployment-of-a-stateful-application-de3de33c8632

        DeploymentRequirements deploymentRequirements = new DeploymentRequirements();

        Application application = new Application(deploymentRequirements);
        application.setShortName("To Do");
        application.setLongName("To Do Application");

        ApplicationEnvironment localApplicationEnvironment = new ApplicationEnvironment(application, Environment.LOCAL, "todo.test");
        ApplicationEnvironment devApplicationEnvironment = new ApplicationEnvironment(application, Environment.DEV, "todo.dev.internal.example.com");
        ApplicationEnvironment intApplicationEnvironment = new ApplicationEnvironment(application, Environment.INT, "todo.int.internal.example.com");
        ApplicationEnvironment perfApplicationEnvironment = new ApplicationEnvironment(application, Environment.PERF, "todo.perf.internal.example.com");
        ApplicationEnvironment prodApplicationEnvironment = new ApplicationEnvironment(application, Environment.PROD, "example.com");
        devApplicationEnvironment.addSubsequentApplicationEnvironment(intApplicationEnvironment);
        intApplicationEnvironment.addSubsequentApplicationEnvironment(perfApplicationEnvironment);
        intApplicationEnvironment.addSubsequentApplicationEnvironment(prodApplicationEnvironment);
        perfApplicationEnvironment.addSubsequentApplicationEnvironment(prodApplicationEnvironment);

        Component backendComponent = new Component(application);
        backendComponent.setName("Backend");
        backendComponent.setImage("kubernetesdemo/to-do-app-backend");
        Port backendPort = new Port(backendComponent, 8080);

        Variable dbUsernameVariable = new Variable(backendComponent, "DB_USERNAME");

        Variable dbPasswordVariable = new Variable(backendComponent, "DB_PASSWORD");

        Variable dbHostVariable = new Variable(backendComponent, "DB_HOST");

        Variable dbNameVariable = new Variable(backendComponent, "DB_NAME");

        Component frontendComponent = new Component(application);
        frontendComponent.setName("Frontend");
        frontendComponent.setImage("kubernetesdemo/to-do-app-frontend");
        Port frontendPort = new Port(frontendComponent, 8080);

        Variable serverUriVariable = new Variable(frontendComponent, "SERVER_URI");

        Database database = new Database(application);
        database.setName("Database");
        database.setDatabaseType("mysql");
        Port databasePort = new Port(database, 3306);

        Storage storage = new Storage(application, Storage.Size.Gibibytes, 1);
        new StorageUsage(database, storage, StorageUsage.Mode.ReadWrite, "/var/lib/mysql");

        Variable mysqlRootPasswordVariable = new Variable(database, "MYSQL_ROOT_PASSWORD");

        Variable mysqlUserVariable = new Variable(database, "MYSQL_USER");

        Variable mysqlPasswordVariable = new Variable(database, "MYSQL_PASSWORD");

        Variable mysqlDatabaseVariable = new Variable(database, "MYSQL_DATABASE");

        Value dbUsernameValue = new ProvidedBase64EncodedPassword(application, "dXNlcg==");
        dbUsernameValue.assignTo(dbUsernameVariable);
        dbUsernameValue.assignTo(mysqlUserVariable);

        Value dbPasswordValue = new ProvidedBase64EncodedPassword(application, "ZGV2ZWxvcA==");
        dbPasswordValue.assignTo(dbPasswordVariable);
        dbPasswordValue.assignTo(mysqlPasswordVariable);

        Value mysqlRootPasswordValue = new ProvidedBase64EncodedPassword(application, "bWFnaWM=");
        mysqlRootPasswordValue.assignTo(mysqlRootPasswordVariable);

        Value databaseHostNameValue = database.getHostName();
        databaseHostNameValue.assignTo(dbHostVariable);

        Value databaseName = new ConstantValue(application, "todo");
        databaseName.assignTo(mysqlDatabaseVariable);
        databaseName.assignTo(dbNameVariable);

        new DefaultRoute(application, frontendPort);
        Route backendRoute = new SubDomainRoute(application, backendPort, "api");
        backendRoute.getDomainName().assignTo(serverUriVariable);

        return deploymentRequirements;
    }

}
