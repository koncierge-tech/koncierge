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

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import tech.koncierge.model.deployment.requirements.DeploymentRequirements;

import java.util.Set;

public class DeploymentRequirementsValidator {

    private DeploymentRequirements deploymentRequirements;

    public DeploymentRequirementsValidator(DeploymentRequirements deploymentRequirements) {
        this.deploymentRequirements = deploymentRequirements;
    }

    public void validate() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Set<ConstraintViolation<DeploymentRequirements>> violations = factory.getValidator().validate(deploymentRequirements);
        if (!violations.isEmpty()) {
            throw new ValidationException(violations);
        }
    }
}
