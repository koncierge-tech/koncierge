<img src="../../docs/images/koncierge-logo.svg" alt="Koncierge" style="float: right; margin-right: 10px; margin-left: 10px;  height: 150px" />

# Example 2 - To Do List Application

## Introduction

This example shows how to deploy a simple To Do List application with a frontend, a backend and a MySQL database to Kubernetes without any required prior knowledge of Kubernetes. 

This example is based on https://betterprogramming.pub/kubernetes-a-detailed-example-of-deployment-of-a-stateful-application-de3de33c8632.

You don't need the source code of the backend and frontend implementations in order to run this example, but if you want to study them, you can find them here:
- Backend: https://github.com/shri-kanth/kubernetes-demo-backend
- Frontend: https://github.com/shri-kanth/kubernetes-demo-frontend

Something important to note in the source code is the environment variables that must be provided 
with values when it is deployed:
- Backend:
  - https://github.com/shri-kanth/kubernetes-demo-backend/blob/master/src/main/resources/application.yml
- Frontend: 
  - https://github.com/shri-kanth/kubernetes-demo-frontend/blob/master/initialize.js

## How to run it

### Step 1: Generate the Kubernetes YAML files

1. [Set up your IDE and build the project](..%2F..%2Fdocs%2Fide-setup.md).
2. Set up your preferences in `examples/example2/config/config.properties`.
2. Run the **main()** method.
   1. In IntelliJ, right-click on `src/main/java/tech/koncierge/examples/example2/ToDoExample.java` and select **Run 'ToDoExample.main()'**
4. You should see the generated files in the **output** directory:
    ```shell
    $ cd examples/example2
    $ cd output/
    $ ls -l
    total 80
    -rw-r--r--  1 user  wheel  5272 Oct 21 06:30 Kubernetes-Manifests-DEV.yaml
    -rw-r--r--  1 user  wheel  5272 Oct 21 06:30 Kubernetes-Manifests-INT.yaml
    -rw-r--r--  1 user  wheel  5212 Oct 21 06:30 Kubernetes-Manifests-LOCAL.yaml
    -rw-r--r--  1 user  wheel  5275 Oct 21 06:30 Kubernetes-Manifests-PERF.yaml
    -rw-r--r--  1 user  wheel  5833 Oct 21 06:30 Kubernetes-Manifests-PROD.yaml
    ```
   
### Step 2: Deploy it to Kubernetes

[See deploy.md](..%2F..%2Fdocs%2Fdeploy.md).

For Minikube, remember to start the tunnel with `minikube tunnel`.

### Step 3: Test that it is working

Browse to http://todo.test

You should see something like this:

![example2-screen.png](docs%2Fimages%2Fexample2-screen.png)

You should be able to add items like this:

![example2-screen2.png](docs%2Fimages%2Fexample2-screen2.png)

## How to generate the diagrams

You could make changes to `src/main/java/tech/koncierge/examples/example2/ToDoExample.java`
and run it again to see the effect on the diagrams.

To generate the diagrams you need an API key which is free if you don't mind anyone seeing
your diagrams. You can get an API key at https://envisionit.app/api-key

Insert the Model ID and API Key that you received into `examples/example2/config/config.properties` and set **enabled** to **true**:

```properties
generate.diagrams.enabled=true
generate.diagrams.model.id=<your-model-id>
generate.diagrams.api.key=<your-api-key>
```

Run it again and the output should show a link where you can see the diagrams, for example:

```shell
Response code: OK
Diagram generated successfully. You can view the diagram at: https://envisionit.app/model/testModelId2
Application short name: To Do
Response code: OK
Diagram generated successfully. You can view the diagram at: https://envisionit.app/model/testModelId2

Process finished with exit code 0
```