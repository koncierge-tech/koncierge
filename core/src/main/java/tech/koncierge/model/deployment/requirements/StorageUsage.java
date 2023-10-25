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

public class StorageUsage {

    public enum Mode {
        ReadOnly, ReadWrite
    }

    private Component component;

    private Storage storage;

    private Mode mode;

    private String mountPath;

    public StorageUsage(Component component, Storage storage, Mode mode, String mountPath) {
        this.component = component;
        this.storage = storage;
        this.mode = mode;
        this.mountPath = mountPath;
        component.getStorageUsages().add(this);
    }

    public Component getComponent() {
        return component;
    }

    public Storage getStorage() {
        return storage;
    }

    public Mode getMode() {
        return mode;
    }

    public String getMountPath() {
        return mountPath;
    }
}
