package com.redhat.summit.gov;

import org.apache.camel.builder.RouteBuilder;


public class Dashboard extends RouteBuilder{

	private static final String STREAMS_URL = "my-cluster-kafka-bootstrap.demo-saude-digital-streams.svc:9092";
	@Override
	public void configure() throws Exception {
		// TODO Auto-generated method stub
	
	
	
	from("kafka:my-topic?brokers="+STREAMS_URL)
    	//.setBody().simple("Type:[Virus] Genuses:[MERSvirus]")
    	.log("${body}")
	.to("ahc-ws://myui:8181/echo");
		
	}
}