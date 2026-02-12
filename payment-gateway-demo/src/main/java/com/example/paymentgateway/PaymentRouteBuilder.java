package com.example.paymentgateway;

import org.apache.camel.builder.RouteBuilder;

/**
 * Payment gateway routes. Uses wmq: URIs (Kantra: replace with jms:).
 * Kantra optional: karaf-routebuilder-spring-component.
 */
public class PaymentRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("wmq:queue:PAYMENTS.IN")
            .routeId("paymentInRoute")
            .to("bean:paymentProcessor")
            .to("wmq:queue:PAYMENTS.OUT");

        from("wmq:queue:PAYMENTS.AUTH")
            .routeId("paymentAuthRoute")
            .to("bean:paymentProcessor")
            .to("wmq:queue:PAYMENTS.CONFIRMED");
    }
}
