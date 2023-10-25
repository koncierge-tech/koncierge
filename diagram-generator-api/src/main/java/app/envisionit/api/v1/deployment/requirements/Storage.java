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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator= ObjectIdGenerators.IntSequenceGenerator.class)
public class Storage {

    public enum Size {
        Kilobytes("k"), Megabytes("M"), Gigabytes("G"), Terabytes("T"), Petabytes("P"), Exabytes("E"),
        Kibibytes("Ki"), Mebibytes("Mi"), Gibibytes("Gi"), Tebibytes("Ti"), Pebibytes("Pi"), Exbibytes("Ei");

        private String abbreviation;

        Size(String abbreviation) {
            this.abbreviation = abbreviation;
        }
    }

    private boolean ephemeral;

    private Size sizeUnits;

    private int sizeQuantity;

    public boolean isEphemeral() {
        return ephemeral;
    }

    public void setEphemeral(boolean ephemeral) {
        this.ephemeral = ephemeral;
    }

    public Size getSizeUnits() {
        return sizeUnits;
    }

    public void setSizeUnits(Size sizeUnits) {
        this.sizeUnits = sizeUnits;
    }

    public int getSizeQuantity() {
        return sizeQuantity;
    }

    public void setSizeQuantity(int sizeQuantity) {
        this.sizeQuantity = sizeQuantity;
    }

    @JsonIgnore
    public String getSizeInKubernetesFormat() {
        return sizeQuantity + sizeUnits.abbreviation;
    }

}
