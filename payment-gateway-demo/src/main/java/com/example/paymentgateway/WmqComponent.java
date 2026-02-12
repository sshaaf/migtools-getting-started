package com.example.paymentgateway;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

import java.util.Map;

/**
 * Custom WebSphere MQ Camel component (source stack).
 * Kantra rule: karaf-defaultcomponent-extend - replace with JmsComponent + IBM MQ.
 */
public class WmqComponent extends DefaultComponent {

    public WmqComponent() {
        super();
    }

    public WmqComponent(CamelContext context) {
        super(context);
    }

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        return new WmqEndpoint(uri, this);
    }
}
