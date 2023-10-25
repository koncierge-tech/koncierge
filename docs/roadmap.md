<img src="images/koncierge-logo.svg" alt="Koncierge" style="float: right; margin-right: 10px; margin-left: 10px;  height: 150px" />

# Koncierge roadmap

Below are some features we plan on adding to Koncierge in the future:

## Deployment architecture model

Currently, we have a Deployment Requirements model and generate a Kubernetes Configuration model
from it (with the option of generating a diagram representing each). 

In future there will also be a deployment architecture model that will fit in between the two. 
Thus, it will be a conceptual representation of the deployment architecture. 

The generator will be split into two stages: The first stage will convert the Deployment Requirements 
model into a Deployment Architecture model. The second stage will convert the Deployment Architecture
model into the Kubernetes Configuration model. 

One advantage with this is that it makes it easier to make the configuration generator
introduce components that the developer did not specifically ask for but that are implied, 
e.g. components that provide dashboards, enable log file access, make database backups, etc. 

## Enforce security best practices

The Deployment Requirements contain all the information needed to be able to deduce all the network
connections that are required between the respective pods. Thus, it is possible to tighten the 
security and only allow network connections that are necessary. 

## Continuous delivery integration

Koncierge will generate the YAML files in a format and structure compatible with FluxCD, ArgoCD, etc.

This means you can integrate Koncierge in your deployment pipeline so that when a change to the Deployment Requirements
code is committed, it kickstarts a process that ends with the changes deployed to Kubernetes.

## Configuration generator options

There will be options that your platform engineering team can set to better align the configuration
that is generated with how your organization prefers to use Kubernetes, e.g:

1. How to divide the configuration among clusters and namespaces. 
   1. A cluster for each of DEV, INT, PROD, etc?
   3. A namespace for each of DEV, INT, PROD, etc?
   4. Other options.
5. Performance optimized vs budget optimized. 
   1. This preference is specified per environment.
   7. It allows your platform engineering team to tweak the configuration generator to produce the best
      trade-off for your organization.
6. More password management options
   1. Passwords that are automatically generated and then stored securely.
   8. Password manager integration.
   9. Storing passwords on a persistent volume. 
   10. SOPS encrypted passwords for use in FluxCD. 

## Generative AI integration

We could use _prompt engineering_ to show generative AI (like ChatGPT) how to generate code for 
Koncierge. 

This would enable users to describe in words their deployment requirements. This would be valuable
for non-Java developers. 

---
If you want to work on one of these, please create an issue for it and describe what you plan on doing 
and assign it to yourself. 

This would enable others to see that you are busy with it and possibly reach
out to you to collaborate. 

It would also enable users to vote for it so that you can see how in-demand your feature is. 