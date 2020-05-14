# Corona Virus Demo 

![](Outbreak.png)


## Folder Structure

```
--Setup
-- src
	|--VirusDispatcher	(Despachar o resultado do laboratório para os manipuladores pelo Channel)
	|--channel (Canal Knative para receber eventos)
		|--Alpha
		|--Mers
		|--Unknown
		|--Noval (COVID-19)
	|--handlers (Manipuladores que enviam aviso ao Painel por eventos do canal)
		|--Alpha
		|--Mers
		|--Unknown
		|--Noval (COVID-19)
	|--Simulator
		|--SimulatorSend (Enviar dados de laboratório falsos)
		|--Dashboard (Enviar notificação para o Painel)
		|--SimulatorCloudEvent (RC 1- Problema com dois Camel K
operadores)
-- ui
```
## Ambiente base de instalação
Instalar os Operadores

1. OpenShift Serverless Operator
1. Knative Eventing Operator
1. AMQ Streams
1. Camel K


Criar projeto para configurar fluxos AMQ (Kafka)

```
oc new-project streams

```

Crie o cluster do Kafka Cluster no projeto de fluxos

```
apiVersion: kafka.strimzi.io/v1beta1
kind: Kafka
metadata:
  name: my-cluster
  namespace: streams
spec:
  kafka:
    version: 2.3.0
    replicas: 3
    listeners:
      plain: {}
      tls: {}
    config:
      offsets.topic.replication.factor: 3
      transaction.state.log.replication.factor: 3
      transaction.state.log.min.isr: 2
      log.message.format.version: '2.3'
    storage:
      type: ephemeral
  zookeeper:
    replicas: 3
    storage:
      type: ephemeral
  entityOperator:
    topicOperator: {}
    userOperator: {}

```

E também crie o Tópico Kafka

```
apiVersion: kafka.strimzi.io/v1beta1
kind: KafkaTopic
metadata:
  name: my-topic
  labels:
    strimzi.io/cluster: my-cluster
  namespace: streams
spec:
  partitions: 10
  replicas: 3
  config:
    retention.ms: 604800000
    segment.bytes: 1073741824

```

E também crie o Serviço Knative, se não existir.
```
apiVersion: v1
kind: Namespace
metadata:
 name: knative-serving
```

```
apiVersion: serving.knative.dev/v1alpha1
kind: KnativeServing
metadata:
 name: knative-serving
 namespace: knative-serving
                              
```

Criar espaço para nome para a demonstração

```
oc new-project outbreak

```

Create Camel K Integration Platform

```
apiVersion: camel.apache.org/v1
kind: IntegrationPlatform
metadata:
  name: example
  namespace: outbreak
spec: {}
```


## Install applications

Setup Dashboard

```
oc new-app quay.io/weimeilin79/myui:latest
oc expose service myui
```

Get your Dashboad location

```
oc get route
```


### Setup application

#### Existing virus outbreak handler

- Setup Channel, under src/channel

```
oc create -f channelalpha.yaml		
oc create -f channelmers.yaml
oc create -f channelunknown.yaml
oc create -f channelnoval.yaml		
```

- Install the existing virus outbreak handler, under src/handlers

```
kamel run -d camel-jackson AlphaHandler.java
kamel run -d camel-jackson MersHandler.yaml
kamel run -d camel-jackson UnknownHandler.groovy
```


- Start sending in lab result, under src/simulator

```
kamel run -d camel-jackson -d camel-bean SimulateSender.java 
kamel run Dashboard.java
```

- Start dispatching virus result to handlers, under src/

```
kamel run -d camel-jackson VirusDispatcher.java --dev
```

- Go to Dashboard to see the virus


#### Adding COVID-19 handler

- Install the new COVID19 outbreak handler, under src/handlers

```
kamel run -d camel-jackson NovalHandler.java
```

- Update your VirusDispatcher.java under src/ add the following condition ***(You should be using DEV mode)***

```
	      .when().simple("${body.genuses} == 'Novalvirus'")
             .marshal(jacksonDataFormat)
             .log("MERS - ${body}")
             .to("knative:channel/noval-handler")
```

- Go to Dashboard to see the new COVID 19 virus appears
-
