# Corona Virus Demo 

![](Outbreak.png)


## Estrutura de pastas

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


## Instalar aplicativos

Painel de configuração

```
oc new-app quay.io/gedasilv/myui
oc expose service myui
```

Obtenha a localização do seu dashboard

```
oc get route
```


### Configuração do aplicativo

#### Manipulador de surto de vírus existente

- Canal de instalação, em src/channel

```
oc create -f channelalpha.yaml		
oc create -f channelmers.yaml
oc create -f channelunknown.yaml
oc create -f channelnoval.yaml		
```

- Instale o manipulador de surtos de vírus existente, em src/handlers

```
kamel run -d camel-jackson AlphaHandler.java
kamel run -d camel-jackson MersHandler.yaml
kamel run -d camel-jackson UnknownHandler.groovy
```


- Comece a enviar o resultado do laboratório, em src/simulator

```
kamel run -d camel-jackson -d camel-bean SimulateSender.java 
kamel run Dashboard.java
```

- Comece a despachar o resultado do vírus para os manipuladores, em src/

```
kamel run -d camel-jackson VirusDispatcher.java --dev
```

- Acesse o Painel para ver o vírus


#### Adicionando manipulador COVID-19

- Instale o novo manipulador de surtos COVID19, em src/handlers

```
kamel run -d camel-jackson NovalHandler.java
```

- Atualize seu VirusDispatcher.java em src / adicione a seguinte condição ***(Você deveria estar usando o modo DEV)***

```
	      .when().simple("${body.genuses} == 'Novalvirus'")
             .marshal(jacksonDataFormat)
             .log("MERS - ${body}")
             .to("knative:channel/noval-handler")
```

- Vá para o Painel para ver o novo vírus COVID 19 aparece
-
