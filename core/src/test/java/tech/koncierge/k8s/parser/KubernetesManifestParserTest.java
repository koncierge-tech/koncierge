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
package tech.koncierge.k8s.parser;

import org.junit.jupiter.api.Test;
import tech.koncierge.generator.kubernetes.util.YamlUtil;
import tech.koncierge.model.kubernetes.Specification;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KubernetesManifestParserTest {

    @Test
    public void test() {
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("kubernetes/example-manifest.yaml");
        List<Specification> specifications = new KubernetesManifestParser().parseSpecification(inputStream);
        assertEquals(2, specifications.size());

        Specification specification1 = specifications.get(0);
        assertEquals("io.k8s.api.apps.v1.Deployment", specification1.getSchemaId());

        Specification specification2 = specifications.get(1);
        assertEquals("io.k8s.api.core.v1.Service", specification2.getSchemaId());

        String renderedYaml = YamlUtil.generateK8sYaml(specifications);
        assertEquals(readFileAsString("kubernetes/example-manifest.yaml"), renderedYaml);
    }

    private String readFileAsString(String fileName) {
        String text = new Scanner(getClass().getClassLoader().getResourceAsStream(fileName), "UTF-8").useDelimiter("\\A").next();
        return text;
    }
}
