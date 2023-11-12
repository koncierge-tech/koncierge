<img src="../../docs/images/koncierge-logo.svg" alt="Koncierge" style="float: right; margin-right: 10px; margin-left: 10px;  height: 150px" />

# Example 3 - Visualize Existing Manifests

## Introduction

This example is different from the others. Instead of generating the Kubernetes manifests,
in this example we use existing hand-coded manifests and generate diagrams for them.

## How to run it

### Step 1: Parse the YAML files and run the diagram generator

1. [Set up your IDE and build the project](..%2F..%2Fdocs%2Fide-setup.md).
2. Set up your preferences in `examples/example3/config/config.properties`. You'll need to set the `generate.diagrams.model.id` and `enerate.diagrams.api.key` properties which you can get at https://envisionit.app/api-key, e.g.
   ```properties
   generate.diagrams.enabled=true
   generate.diagrams.model.id=<your-model-id>
   generate.diagrams.api.key=<your-api-key>
   ```
2. Run the **main()** method.
   1. In IntelliJ, right-click on `src/main/java/tech/koncierge/examples/example3/VisualizeExistingManifestsExample.java` and select **Run 'VisualizeExistingManifestsExample.main()'**

### Step 2: View the diagram

The output should show a link where you can see the diagram, for example:

```shell
2023-11-12 06:01:07 INFO  RemoteDiagramPoster:42 - Response code: 200
2023-11-12 06:01:07 INFO  DiagramGenerator:80 - Diagram generated successfully. You can view the diagram at: https://envisionit.app/model/b040d217def04767839a329238c2257c

Process finished with exit code 0
```

Click on *Kubernetes Configuration* to see the diagram. 

Here is the Kubernetes Configuration diagram for the [Lakeside Mutual](https://github.com/Microservice-API-Patterns/LakesideMutual) example:

https://envisionit.app/diagram/b040d217def04767839a329238c2257c/k8s