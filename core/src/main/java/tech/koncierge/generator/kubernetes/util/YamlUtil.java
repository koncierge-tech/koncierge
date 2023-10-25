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
package tech.koncierge.generator.kubernetes.util;

import tech.koncierge.config.Config;
import tech.koncierge.model.kubernetes.PropertyValue;
import tech.koncierge.model.kubernetes.PropertyValueList;
import tech.koncierge.model.kubernetes.SimpleValue;
import tech.koncierge.model.kubernetes.Specification;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class YamlUtil {

    public static void printK8sYaml(Config config, String fileName, List list) {
        String yaml = generateK8sYaml(list);
        File file = new File(getOutputDirectory(config), fileName + ".yaml");
        writeStringToFile(file, yaml);
    }

    public static String generateK8sYaml(List list) {
        final DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);

        final Yaml yaml = new Yaml(options);

        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            Object object = list.get(i);
            Specification specification = (Specification) object;
            Map<String, Object> newMap = new LinkedHashMap<>();
            convertSpecificationsToMaps(specification.getProperties(), newMap);

            StringWriter writer = new StringWriter();
            yaml.dump(newMap, writer);
            buf.append(writer);
            if (i < list.size() - 1) {
                buf.append("---\n");
            }
        }

        return buf.toString();
    }

    private static Map convertSpecificationsToMaps(Map map) {
        Map newMap = new LinkedHashMap<>();
        return convertSpecificationsToMaps(map, newMap);
    }

    private static Map convertSpecificationsToMaps(Map map, Map newMap) {
        for (Object key : map.keySet()) {
            Object o = map.get(key);
            if (o instanceof Specification) {
                Specification specification = (Specification) o;
                newMap.put(key, convertSpecificationsToMaps(specification.getProperties()));
            } else if (o instanceof PropertyValueList) {
                PropertyValueList propertyValueList = (PropertyValueList) o;
                List<Object> newList = new ArrayList<>();
                newMap.put(key, newList);
                for (PropertyValue propertyValue : propertyValueList.getPropertyValues()) {
                    if (propertyValue instanceof Specification) {
                        Specification specification = (Specification) propertyValue;
                        newList.add(convertSpecificationsToMaps(specification.getProperties()));
                    } else if (propertyValue instanceof SimpleValue) {
                        SimpleValue simpleValue = (SimpleValue) propertyValue;
                        newList.add(simpleValue.getValue());
                    } else {
                        throw new RuntimeException("Unknown property value type: " + propertyValue.getClass().getName());
                    }
                }
            } else if (o instanceof Map) {
                Map<String, Object> subMap = (Map<String, Object>) o;
                newMap.put(key, convertSpecificationsToMaps(subMap));
            } else if (o instanceof SimpleValue) {
                SimpleValue simpleValue = (SimpleValue) o;
                newMap.put(key, simpleValue.getValue());
            } else {
                newMap.put(key, o);
            }
        }
        return newMap;
    }

    public static File getOutputDirectory(Config config) {
        File outputDirectoryFile;
        if (config.getOutputDirectory().startsWith("/")) {
            outputDirectoryFile = new File(config.getOutputDirectory());
        } else {
            File directory = new File(config.getWorkingDirectory());
            outputDirectoryFile = new File(directory, config.getOutputDirectory());
        }
        outputDirectoryFile.mkdirs();
        return outputDirectoryFile;
    }

    public static void writeStringToFile(File file, String contents) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file, "UTF-8");
            writer.print(contents);
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new RuntimeException("Could not write contents to file " + file.getName() + ": " + e.getMessage());
        } finally {
            writer.close();
        }
    }
}
