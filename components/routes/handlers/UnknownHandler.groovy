from("knative:channel/unknown-handler")
.to('log:info?showAll=false')
.to('kafka:my-topic?brokers=my-cluster-kafka-bootstrap.results-streams.svc:9092')
