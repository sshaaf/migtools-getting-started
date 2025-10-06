package dev.shaaf.konveyor.fuse2camel4.examples.aws;

import org.apache.camel.builder.RouteBuilder;

public class JavaDSL extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:startExecution")
                // ❌ Old header name (deprecated in Camel 4.1)
                .setHeader("CamelAwsStateMachineExecutionArn", constant("arn:aws:states:us-east-1:123456789012:stateMachine:MyStateMachine"))
                .setHeader("CamelAwsStateMachineExecutionName", constant("MyExecution"))
                .setHeader("CamelAwsStateMachineExecutionInput", constant("{\"input\": \"value\"}"))
                .to("aws2-step-functions://default?operation=startExecution");
    }
}