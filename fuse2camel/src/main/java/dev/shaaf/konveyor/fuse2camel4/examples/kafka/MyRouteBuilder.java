package dev.shaaf.konveyor.fuse2camel4.examples.kafka;


import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.properties.PropertiesComponent; // ❌ Removed in Camel 4

public class MyRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // ❌ Triggers rule: camel4-properties-component-removal-001
        PropertiesComponent properties = new PropertiesComponent();
        properties.setLocation("classpath:application.properties");
        getContext().addComponent("properties", properties);

        // ❌ Triggers rule: xml-removed-camel4-00001 (via camel-activemq dependency)
        from("activemq:queue:orders")
            .log("Received order: ${body}")
            .to("activemq:queue:processed");
    }
}