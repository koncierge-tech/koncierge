<img src="images/koncierge-logo.svg" alt="Koncierge" style="float: right; margin-right: 10px; margin-left: 10px;  height: 150px" />

# Prepare Kubernetes for a local developer environment on a Mac with Microk8s

[See other configuration options](prepare-k8s.md).

<p style="color: red"><b>Warning:</b> These instructions don't work yet - the DNS for domain "test" doesn't work. 
Use the instructions for Minikube instead.</p>
<p style="color: red">If someone can get it to work, please submit a pull request with updated instructions.</p>

## Step 1: Install Kubernetes

### Step 1.1: Install Microk8s

We suggest Microk8s as Kubernetes implementation, because it can be used in both local developer environments
and production environments. 

Follow the instructions at https://microk8s.io/docs/install-alternatives

```shell
$ brew install ubuntu/microk8s/microk8s
...
Run `microk8s install` to start with MicroK8s
$ microk8s install
VM disk size requested exceeds free space on host.
warning: "--mem" long option will be deprecated in favour of "--memory" in a future release. Please update any scripts, etc.
Launched: microk8s-vm
microk8s (1.27/stable) v1.27.5 from Canonical✓ installed
microk8s-integrator-macos 0.1 from Canonical✓ installed
MicroK8s is up and running. See the available commands with `microk8s --help`.
```

To confirm that it is working:

```shell
$ microk8s version
MicroK8s v1.27.5 revision 5891
```

#### Troubleshooting:

If you get an error like this:

```shell
$ microk8s install
VM disk size requested exceeds free space on host.
*** Warning! The Hyperkit driver is deprecated and will be removed in an upcoming release. ***

When you are ready to have your instances migrated, please stop them (multipass stop --all) and switch to the QEMU driver (multipass set local.driver=qemu).

start failed: The following errors occurred:
microk8s-vm: timed out waiting for response
An error occurred with the instance when trying to start with 'multipass': returned exit code 2.
Ensure that 'multipass' is setup correctly and try again.
```

Then fix it as follows:
1. Remove the multipass VM created by Microk8s
    ```shell
    $ multipass list
    *** Warning! The Hyperkit driver is deprecated and will be removed in an upcoming release. ***
    
    When you are ready to have your instances migrated, please stop them (multipass stop --all) and switch to the QEMU driver (multipass set local.driver=qemu).
    
    Name                    State             IPv4             Image
    microk8s-vm             Running           192.168.191.5    Ubuntu 22.04 LTS
    $ multipass delete microk8s-vm
    $ multipass purge
    ```
2. Switch off your VPN.
3. Run `microk8s install`.
4. Switch on your VPN again.

### Step 1.2: Stop microk8s when not needed

On a local developer environment, you might not want to run it all the time. You can stop and start it as follows:

```shell
$ microk8s stop
Stopped.
$ microk8s start
$ microk8s status
microk8s is running
high-availability: no
  datastore master nodes: 127.0.0.1:19001
  datastore standby nodes: none
addons:
  enabled:
    dns                  # (core) CoreDNS
    ha-cluster           # (core) Configure high availability on the current node
    helm                 # (core) Helm - the package manager for Kubernetes
    helm3                # (core) Helm 3 - the package manager for Kubernetes
  disabled:
    cert-manager         # (core) Cloud native certificate management
    community            # (core) The community addons repository
    dashboard            # (core) The Kubernetes dashboard
    gpu                  # (core) Automatic enablement of Nvidia CUDA
    host-access          # (core) Allow Pods connecting to Host services smoothly
    hostpath-storage     # (core) Storage class; allocates storage from host directory
    ingress              # (core) Ingress controller for external access
    kube-ovn             # (core) An advanced network fabric for Kubernetes
    mayastor             # (core) OpenEBS MayaStor
    metallb              # (core) Loadbalancer for your Kubernetes cluster
    metrics-server       # (core) K8s Metrics Server for API access to service metrics
    minio                # (core) MinIO object storage
    observability        # (core) A lightweight observability stack for logs, traces and metrics
    prometheus           # (core) Prometheus operator for monitoring and logging
    rbac                 # (core) Role-Based Access Control for authorisation
    registry             # (core) Private image registry exposed on localhost:32000
    storage              # (core) Alias to hostpath-storage add-on, deprecated
```

## Step 2: Setup tools

### Step 2.1: Setup kubectl

Setup an alias so that the command "kubectl" executes "microk8s kubectl".

Make a backup of your `.bash_profile` file:

```shell
$ cp ~/.bash_profile ~/.bash_profile.bak
$ nano ~/.bash_profile
```

Add this line at the end of the file:
```shell
alias kubectl='microk8s kubectl'
```

View the changes and make them active:

```shell
$ diff ~/.bash_profile.bak ~/.bash_profile
35a36,38
> 
> alias kubectl='microk8s kubectl'
> 
$ source ~/.bash_profile
```

Confirm that it is working:

```shell
$ kubectl get nodes
NAME          STATUS   ROLES    AGE   VERSION
microk8s-vm   Ready    <none>   8h    v1.27.5
$ kubectl config view
apiVersion: v1
clusters:
- cluster:
    certificate-authority-data: DATA+OMITTED
    server: https://192.168.191.4:16443
  name: microk8s-cluster
contexts:
- context:
    cluster: microk8s-cluster
    user: admin
  name: microk8s
current-context: microk8s
kind: Config
preferences: {}
users:
- name: admin
  user:
    token: REDACTED
```

### Step 2.2: Setup k9s

Follow the instructions at https://k9scli.io/topics/install/

Point **k9s** at microk8s:

```shell
$ cp ~/.kube/config ~/.kube/config.bak
$ kubectl config view --raw > ~/.kube/config
$ k9s
```

You should see something like this:

![k9s_showing_microk8s.png](images%2Fk9s_showing_microk8s.png)


## Steps 3: Setup and test domain names

You need to set up DNS so that the domain name of each of our environments points to the Kubernetes cluster where that environment is deployed.

For example, in [Example 2](..%2Fexamples%2Fexample2%2FREADME.md), the production environment is deployed to _example.com_.
In this case the domain _example.com_ will point to the _frontend_ service and _api.example.com_ will point to the _backend_ service.
It is the [ingress](https://kubernetes.io/docs/concepts/services-networking/ingress/) that is generated by Koncierge that handles the sub-domain,
but the main domain must be set up to point to the cluster where the production environment is deployed.

If you want to test on your local environment, you can set up a _test_ domain on your laptop that points to your local cluster.
The [ingress](https://kubernetes.io/docs/concepts/services-networking/ingress/) that is generated will then point _todo.test_ to the
_frontend_ service and _api.todo.test_ to the _backend_ service.

### Step 3.1: Enable required addons in microk8s

Clean microk8s:

```shell
$ microk8s disable ha-cluster --force
Infer repository core for addon ha-cluster
Reverting to a non-HA setup
Generating new cluster certificates.
Waiting for node to start. .  
Enabling flanneld and etcd
HA disabled
$ microk8s status
microk8s is running
high-availability: no
  datastore endpoints:
    127.0.0.1:12379
addons:
  enabled:
    helm                 # (core) Helm - the package manager for Kubernetes
    helm3                # (core) Helm 3 - the package manager for Kubernetes
  disabled:
    cert-manager         # (core) Cloud native certificate management
    community            # (core) The community addons repository
    dashboard            # (core) The Kubernetes dashboard
    dns                  # (core) CoreDNS
    gpu                  # (core) Automatic enablement of Nvidia CUDA
    ha-cluster           # (core) Configure high availability on the current node
    host-access          # (core) Allow Pods connecting to Host services smoothly
    hostpath-storage     # (core) Storage class; allocates storage from host directory
    ingress              # (core) Ingress controller for external access
    kube-ovn             # (core) An advanced network fabric for Kubernetes
    mayastor             # (core) OpenEBS MayaStor
    metallb              # (core) Loadbalancer for your Kubernetes cluster
    metrics-server       # (core) K8s Metrics Server for API access to service metrics
    minio                # (core) MinIO object storage
    observability        # (core) A lightweight observability stack for logs, traces and metrics
    prometheus           # (core) Prometheus operator for monitoring and logging
    rbac                 # (core) Role-Based Access Control for authorisation
    registry             # (core) Private image registry exposed on localhost:32000
    storage              # (core) Alias to hostpath-storage add-on, deprecated
```

Enable persistent storage on the host machine's file system:

```shell
$ microk8s enable hostpath-storage
Infer repository core for addon hostpath-storage
Enabling default storage class.
WARNING: Hostpath storage is not suitable for production environments.
         A hostpath volume can grow beyond the size limit set in the volume claim manifest.

deployment.apps/hostpath-provisioner created
storageclass.storage.k8s.io/microk8s-hostpath created
serviceaccount/microk8s-hostpath created
clusterrole.rbac.authorization.k8s.io/microk8s-hostpath created
clusterrolebinding.rbac.authorization.k8s.io/microk8s-hostpath created
Storage will be available soon.
$ microk8s status
microk8s is running
high-availability: no
  datastore endpoints:
    127.0.0.1:12379
addons:
  enabled:
    helm                 # (core) Helm - the package manager for Kubernetes
    helm3                # (core) Helm 3 - the package manager for Kubernetes
    hostpath-storage     # (core) Storage class; allocates storage from host directory
    storage              # (core) Alias to hostpath-storage add-on, deprecated
  disabled:
    cert-manager         # (core) Cloud native certificate management
    community            # (core) The community addons repository
    dashboard            # (core) The Kubernetes dashboard
    dns                  # (core) CoreDNS
    gpu                  # (core) Automatic enablement of Nvidia CUDA
    ha-cluster           # (core) Configure high availability on the current node
    host-access          # (core) Allow Pods connecting to Host services smoothly
    ingress              # (core) Ingress controller for external access
    kube-ovn             # (core) An advanced network fabric for Kubernetes
    mayastor             # (core) OpenEBS MayaStor
    metallb              # (core) Loadbalancer for your Kubernetes cluster
    metrics-server       # (core) K8s Metrics Server for API access to service metrics
    minio                # (core) MinIO object storage
    observability        # (core) A lightweight observability stack for logs, traces and metrics
    prometheus           # (core) Prometheus operator for monitoring and logging
    rbac                 # (core) Role-Based Access Control for authorisation
    registry             # (core) Private image registry exposed on localhost:32000
```

Enable DNS:

(Note that 1.1.1.1 below is Cloudflare's DNS server. You could substitute it for another one.)

```shell
$ microk8s enable dns:1.1.1.1
Infer repository core for addon dns
Enabling DNS
Will use  1.1.1.1 as upstream nameservers
Applying manifest
serviceaccount/coredns created
configmap/coredns created
deployment.apps/coredns created
service/kube-dns created
clusterrole.rbac.authorization.k8s.io/coredns created
clusterrolebinding.rbac.authorization.k8s.io/coredns created
DNS is enabled
$ microk8s status
microk8s is running
high-availability: no
  datastore endpoints:
    127.0.0.1:12379
addons:
  enabled:
    dns                  # (core) CoreDNS
    helm                 # (core) Helm - the package manager for Kubernetes
    helm3                # (core) Helm 3 - the package manager for Kubernetes
    hostpath-storage     # (core) Storage class; allocates storage from host directory
    storage              # (core) Alias to hostpath-storage add-on, deprecated
  disabled:
    cert-manager         # (core) Cloud native certificate management
    community            # (core) The community addons repository
    dashboard            # (core) The Kubernetes dashboard
    gpu                  # (core) Automatic enablement of Nvidia CUDA
    ha-cluster           # (core) Configure high availability on the current node
    host-access          # (core) Allow Pods connecting to Host services smoothly
    ingress              # (core) Ingress controller for external access
    kube-ovn             # (core) An advanced network fabric for Kubernetes
    mayastor             # (core) OpenEBS MayaStor
    metallb              # (core) Loadbalancer for your Kubernetes cluster
    metrics-server       # (core) K8s Metrics Server for API access to service metrics
    minio                # (core) MinIO object storage
    observability        # (core) A lightweight observability stack for logs, traces and metrics
    prometheus           # (core) Prometheus operator for monitoring and logging
    rbac                 # (core) Role-Based Access Control for authorisation
    registry             # (core) Private image registry exposed on localhost:32000
```

// TODO rbac if necessary

Find the internal IP address of the Microk8s instance
```shell
MacBook-Pro-2:~ evert$ microk8s kubectl get node -o json | grep -B 1 InternalIP
                        "address": "192.168.191.5",
                        "type": "InternalIP"
```

Install the load balancer:

(Use the InternalIP from above in the command.)

```shell
$ microk8s enable metallb:"192.168.191.5-192.168.191.5"
Infer repository core for addon metallb
Enabling MetalLB
Applying Metallb manifest
customresourcedefinition.apiextensions.k8s.io/addresspools.metallb.io created
customresourcedefinition.apiextensions.k8s.io/bfdprofiles.metallb.io created
customresourcedefinition.apiextensions.k8s.io/bgpadvertisements.metallb.io created
customresourcedefinition.apiextensions.k8s.io/bgppeers.metallb.io created
customresourcedefinition.apiextensions.k8s.io/communities.metallb.io created
customresourcedefinition.apiextensions.k8s.io/ipaddresspools.metallb.io created
customresourcedefinition.apiextensions.k8s.io/l2advertisements.metallb.io created
namespace/metallb-system created
serviceaccount/controller created
serviceaccount/speaker created
clusterrole.rbac.authorization.k8s.io/metallb-system:controller created
clusterrole.rbac.authorization.k8s.io/metallb-system:speaker created
role.rbac.authorization.k8s.io/controller created
role.rbac.authorization.k8s.io/pod-lister created
clusterrolebinding.rbac.authorization.k8s.io/metallb-system:controller created
clusterrolebinding.rbac.authorization.k8s.io/metallb-system:speaker created
rolebinding.rbac.authorization.k8s.io/controller created
secret/webhook-server-cert created
service/webhook-service created
rolebinding.rbac.authorization.k8s.io/pod-lister created
daemonset.apps/speaker created
deployment.apps/controller created
validatingwebhookconfiguration.admissionregistration.k8s.io/validating-webhook-configuration created
Waiting for Metallb controller to be ready.
error: timed out waiting for the condition on deployments/controller
MetalLB controller is still not ready
deployment.apps/controller condition met
ipaddresspool.metallb.io/default-addresspool created
l2advertisement.metallb.io/default-advertise-all-pools created
MetalLB is enabled
```


### Step 3.2: Setup DNS

There are no specific instructions on how to do this for Microk8s, so follow the instructions for step 3 at https://minikube.sigs.k8s.io/docs/handbook/addons/ingress-dns/.
Use the InternalIP that you found above where the instructions refer to "minikube ip".

```shell
$ sudo nano /etc/resolver/test
$ cat /etc/resolver/test
domain test
nameserver 192.168.191.5
search_order 1
timeout 5
$ ping test
ping: cannot resolve test: Unknown host
```

Reload Mac OS mDNS resolver for the changes to take effect:

```shell
$ sudo launchctl unload -w /System/Library/LaunchDaemons/com.apple.mDNSResponder.plist
Password:
/System/Library/LaunchDaemons/com.apple.mDNSResponder.plist: Operation not permitted while System Integrity Protection is engaged
Unload failed: 150: Operation not permitted while System Integrity Protection is engaged
```

If you get the error "Operation not permitted while System Integrity Protection is engaged", 
follow the instructions at https://apple.stackexchange.com/questions/281957/unload-nfsd-operation-not-permitted-while-system-integrity-protection-is-engag

```shell
$ csrutil status
System Integrity Protection status: enabled.
```

Try again after disabling system integrity protection:

```shell
$ sudo launchctl unload -w /System/Library/LaunchDaemons/com.apple.mDNSResponder.plist
Password:
$ sudo launchctl load -w /System/Library/LaunchDaemons/com.apple.mDNSResponder.plist
$ ping test
ping: cannot resolve test: Unknown host
```

Try again after enabling system integrity protection again:


### Step 3.3: Test DNS

```shell
MacBook-Pro-2:files evert$ ls
ingress-test.yaml
MacBook-Pro-2:files evert$ kubectl create namespace ingress-test
namespace/ingress-test created
MacBook-Pro-2:files evert$ kubectl apply -f ingress-test.yaml 
deployment.apps/hello-world-app created
ingress.networking.k8s.io/example-ingress created
service/hello-world-app created
service/hello-world-app configured
MacBook-Pro-2:files evert$ k9s
MacBook-Pro-2:files evert$ curl http://hello-john.test
^C
MacBook-Pro-2:files evert$ ping hello-john.test
^C
MacBook-Pro-2:files evert$ k9s
MacBook-Pro-2:files evert$ curl http://192.168.191.5:80
curl: (7) Failed to connect to 192.168.191.5 port 80 after 122 ms: Couldn't connect to server

```