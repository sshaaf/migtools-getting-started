package com.example.paymentgateway;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;

/**
 * WebSphere MQ producer (stub for demo).
 */
public class WmqProducer extends DefaultProducer {

    public WmqProducer(WmqEndpoint endpoint) {
        super(endpoint);
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        // Stub: in real impl would send to WebSphere MQ
    }
}
