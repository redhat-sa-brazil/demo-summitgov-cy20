# Recursos das aplicações (demo-apps)

## Estrutura de Diretórios

```
├── demo-etherpad (GitOps com ACM)
│   ├── configmap.yaml
│   ├── deployment.yaml
│   ├── kustomization.yaml
│   ├── namespace.yaml
│   └── service.yaml
├── demo-saude-digital (Camel-K com Knative)
│   ├── camel-k-channels.yaml
│   ├── camel-k-integration-platforms.yaml
│   ├── camel-k-integrations.yaml
│   ├── deployments.yaml
│   ├── kustomization.yaml
│   ├── namespaces.yaml
│   ├── openshift-routes.yaml
│   └── services.yaml
├── demo-saude-digital-streams (Kafka com Strimzi)
    ├── kafka-clusters.yaml
    ├── kafka-topics.yaml
    ├── kustomization.yaml
    └── namespaces.yaml
```

## Informações Gerais

O conteúdo desse repositório faz uso do Kustomize para gerar o manifesto final a ser implantado nos ambientes Kubernetes/OpenShift. Caso não tenha familiaridade, recomenda-se a leitura da [sua documentação](https://github.com/kubernetes-sigs/kustomize).
