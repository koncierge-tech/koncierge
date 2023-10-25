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

import jakarta.validation.constraints.NotEmpty;

public class Database extends Component {

    @NotEmpty
    private String databaseType;

    private String requiredDiskSpace;

    public Database(Application application) {
        super(application);
    }

    public String getDatabaseType() {
        return databaseType;
    }

    /**
     *
     * @param databaseType 'mariadb', 'mysql', 'postgresql', 'oracle', etc
     */
    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    @Deprecated
    public String getRequiredDiskSpace() {
        return requiredDiskSpace;
    }

    /**
     *
     * @param requiredDiskSpace e.g. '1Gi'
     */
    @Deprecated
    public void setRequiredDiskSpace(String requiredDiskSpace) {
        this.requiredDiskSpace = requiredDiskSpace;
    }
}
