package dev.shaaf.konveyor.fuse2camel4.examples.aws;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;

public class StepFunctionsRouteBefore extends RouteBuilder {

    private static final String STATE_MACHINE_ARN = "arn:aws:states:us-east-1:123456789012:stateMachine:OrderProcessingWorkflow";

    @Override
    public void configure() throws Exception {

        // JSON data format for input
        JsonDataFormat jsonFormat = new JsonDataFormat();
        jsonFormat.setLibrary(JsonLibrary.Jackson);

        from("direct:startStepFunction")
                // ❌ DEPRECATED HEADER: Triggers rule xml-changed-camel41-00008
                .setHeader("CamelAwsStateMachineExecutionArn", constant(STATE_MACHINE_ARN))
                .setHeader("CamelAwsStateMachineExecutionName", simple("Execution-${date:now:yyyyMMdd-HHmmss}"))
                .setHeader("CamelAwsStateMachineExecutionInput", bodyAs(String.class))
                .marshal(jsonFormat)
                .log("Starting Step Functions execution for order: ${body}")
                // Sending to AWS2 Step Functions component
                .to("aws2-step-functions://default?operation=startExecution");

        from("direct:listExecutions")
                // ❌ Also uses deprecated header name
                .setHeader("CamelAwsStateMachineExecutionArn", constant(STATE_MACHINE_ARN))
                .to("aws2-step-functions://default?operation=listExecutions")
                .log("Listed executions: ${body}");
    }
}