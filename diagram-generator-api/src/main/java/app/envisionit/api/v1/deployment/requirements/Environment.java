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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator= ObjectIdGenerators.IntSequenceGenerator.class)
public enum Environment {

    LOCAL("LOCAL", "Local", false, false, false),
    DEV("DEV", "Development", false, false, false),
    INT("INT", "Integration", false, false, true),
    ACC("ACC", "Acceptance", false, false, true),
    PERF("PERF", "Performance", true, true, true),
    PROD("PROD", "Production", true, true, true);

    private String shortName;

    private String longName;
    private boolean doBackups;
    private boolean performanceOptimized;
    private boolean strictSecurity;

    Environment(String shortName, String longName, boolean doBackups, boolean performanceOptimized, boolean strictSecurity) {
        this.shortName = shortName;
        this.longName = longName;
        this.doBackups = doBackups;
        this.performanceOptimized = performanceOptimized;
        this.strictSecurity = strictSecurity;
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

    public boolean isDoBackups() {
        return doBackups;
    }

    public boolean isPerformanceOptimized() {
        return performanceOptimized;
    }

    public boolean isStrictSecurity() {
        return strictSecurity;
    }
}
