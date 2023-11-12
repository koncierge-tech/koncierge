package tech.koncierge.examples.example3;

import tech.koncierge.config.Config;
import tech.koncierge.diagrams.DiagramGenerator;
import tech.koncierge.k8s.parser.KubernetesManifestParser;
import tech.koncierge.k8s.util.KubernetesManifestFileUtil;
import tech.koncierge.model.kubernetes.ClusterConfiguration;
import tech.koncierge.model.kubernetes.KubernetesConfiguration;
import tech.koncierge.model.kubernetes.Manifest;
import tech.koncierge.util.WorkingDirectoryUtil;

import java.io.File;
import java.util.List;

public class VisualizeExistingManifestsExample {

    // For example, check out the code from https://github.com/Microservice-API-Patterns/LakesideMutual and provide the path to the manifests directory here:
    public static final String MANIFESTS_PATH = "/Users/evert/data/projects/LakesideMutual/kubernetes/manifests";

    public static void main(String[] args) {
        String workingDirectory = WorkingDirectoryUtil.getWorkingDirectory(VisualizeExistingManifestsExample.class);
        Config config = new Config(workingDirectory);

        List<File> files = KubernetesManifestFileUtil.getManifestFiles(MANIFESTS_PATH);
        KubernetesManifestParser parser = new KubernetesManifestParser();
        List<Manifest> manifests = parser.parseSpecifications(files);

        KubernetesConfiguration kubernetesConfiguration = new KubernetesConfiguration();
        ClusterConfiguration clusterConfiguration = new ClusterConfiguration("PROD");
        kubernetesConfiguration.getClusterConfigurations().add(clusterConfiguration);
        clusterConfiguration.setManifests(manifests);

        DiagramGenerator.generateKubernetesConfigurationDiagrams(config, kubernetesConfiguration);
    }
}
