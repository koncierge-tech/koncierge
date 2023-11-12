package tech.koncierge.k8s.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class KubernetesManifestFileUtil {

    public static List<File> getManifestFiles(String path) {
        File directory = new File(path);
        File[] files = directory.listFiles();
        List<File> manifestFiles = new ArrayList<>();
        for (File file : files) {
            if (file.isDirectory()) {
                manifestFiles.addAll(getManifestFiles(file.getAbsolutePath()));
            } else if (file.getName().endsWith(".yaml")) {
                manifestFiles.add(file);
            }
        }
        return manifestFiles;
    }
}
