package com.example.paymentgateway;

import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;

/**
 * WebSphere MQ consumer (stub for demo).
 */
public class WmqConsumer extends DefaultConsumer {

    public WmqConsumer(WmqEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
    }
}
