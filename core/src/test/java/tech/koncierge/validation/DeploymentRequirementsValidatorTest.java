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
package tech.koncierge.validation;

import org.junit.jupiter.api.Test;
import tech.koncierge.model.deployment.requirements.Application;
import tech.koncierge.model.deployment.requirements.ApplicationEnvironment;
import tech.koncierge.model.deployment.requirements.Component;
import tech.koncierge.model.deployment.requirements.Database;
import tech.koncierge.model.deployment.requirements.DeploymentRequirements;
import tech.koncierge.model.deployment.requirements.Environment;
import tech.koncierge.model.deployment.requirements.Variable;
import tech.koncierge.model.deployment.requirements.values.ConstantValue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeploymentRequirementsValidatorTest {

    @Test
    public void testNoApplications() {
        DeploymentRequirements deploymentRequirements = new DeploymentRequirements();
        ValidationException validationException = assertThrowsValidationException(deploymentRequirements);
        assertContainsConstraintViolation(validationException, "applications", "must not be empty");
    }

    @Test
    public void testEmptyApplication() {
        DeploymentRequirements deploymentRequirements = new DeploymentRequirements();
        Application application = new Application(deploymentRequirements);

        ValidationException validationException = assertThrowsValidationException(deploymentRequirements);

        assertEquals(4, validationException.getViolations().size());

        assertContainsConstraintViolation(validationException, "applications[0].components", "must not be empty");
        assertContainsConstraintViolation(validationException, "applications[0].longName", "must not be empty");
        assertContainsConstraintViolation(validationException, "applications[0].shortName", "must not be empty");
        assertContainsConstraintViolation(validationException, "applications[0].applicationEnvironments", "must not be empty");
    }

    @Test
    public void testEmptyComponent() {
        DeploymentRequirements deploymentRequirements = new DeploymentRequirements();

        Application application = new Application(deploymentRequirements);
        application.setLongName("My First Application");
        application.setShortName("My First");

        ApplicationEnvironment localApplicationEnvironment = new ApplicationEnvironment(application, Environment.LOCAL, "myfirst.test");

        Component component = new Component(application);

        ValidationException validationException = assertThrowsValidationException(deploymentRequirements);

        assertEquals(1, validationException.getViolations().size());

        assertContainsConstraintViolation(validationException, "applications[0].components[0].name", "must not be empty");
    }

    @Test
    public void testComponentWithEmptyVariable() {
        DeploymentRequirements deploymentRequirements = new DeploymentRequirements();

        Application application = new Application(deploymentRequirements);
        application.setLongName("My First Application");
        application.setShortName("My First");

        ApplicationEnvironment localApplicationEnvironment = new ApplicationEnvironment(application, Environment.LOCAL, "myfirst.test");

        Component component = new Component(application);
        component.setName("My First Component");

        Variable variable = new Variable(component, "My First Variable");

        ValidationException validationException = assertThrowsValidationException(deploymentRequirements);

        assertEquals(1, validationException.getViolations().size());

        assertContainsConstraintViolation(validationException, "applications[0].components[0].variables[0].value", "must not be null");
    }

    @Test
    public void testDatabaseWithEmptyType() {
        DeploymentRequirements deploymentRequirements = new DeploymentRequirements();

        Application application = new Application(deploymentRequirements);
        application.setLongName("My First Application");
        application.setShortName("My First");

        ApplicationEnvironment localApplicationEnvironment = new ApplicationEnvironment(application, Environment.LOCAL, "myfirst.test");

        Database database = new Database(application);
        database.setName("My First Database");

        ValidationException validationException = assertThrowsValidationException(deploymentRequirements);

        assertEquals(1, validationException.getViolations().size());

        assertContainsConstraintViolation(validationException, "applications[0].components[0].databaseType", "must not be empty");
    }

    @Test
    public void testValid() {
        DeploymentRequirements deploymentRequirements = new DeploymentRequirements();

        Application application = new Application(deploymentRequirements);
        application.setLongName("My First Application");
        application.setShortName("My First");

        ApplicationEnvironment localApplicationEnvironment = new ApplicationEnvironment(application, Environment.LOCAL, "myfirst.test");

        Component component = new Component(application);
        component.setName("My First Component");

        Variable variable = new Variable(component, "MY_VARIABLE");
        variable.setValue(new ConstantValue(application, "My First Value"));

        Database database = new Database(application);
        database.setName("My First Database");
        database.setDatabaseType("mysql");

        DeploymentRequirementsValidator validator = new DeploymentRequirementsValidator(deploymentRequirements);
        validator.validate();
    }

    private ValidationException assertThrowsValidationException(DeploymentRequirements deploymentRequirements) {
        DeploymentRequirementsValidator validator = new DeploymentRequirementsValidator(deploymentRequirements);

        return assertThrows(ValidationException.class, () -> validator.validate());
    }

    private void assertContainsConstraintViolation(ValidationException exception, String propertyPath, String message) {
        boolean found = exception.getViolations().stream()
                .anyMatch(violation ->
                        violation.getMessage().equals(message)
                                && violation.getPropertyPath().toString().equals(propertyPath));
        assertTrue(found);
    }
}
