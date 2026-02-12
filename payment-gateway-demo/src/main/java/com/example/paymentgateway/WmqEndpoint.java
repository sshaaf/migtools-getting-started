package com.example.paymentgateway;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;

/**
 * WebSphere MQ endpoint for WmqComponent.
 */
public class WmqEndpoint extends DefaultEndpoint {

    public WmqEndpoint(String endpointUri, WmqComponent component) {
        super(endpointUri, component);
    }

    @Override
    public Producer createProducer() throws Exception {
        return new WmqProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        return new WmqConsumer(this, processor);
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
