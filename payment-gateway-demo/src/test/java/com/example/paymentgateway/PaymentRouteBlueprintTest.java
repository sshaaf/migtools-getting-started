package com.example.paymentgateway;

import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.junit.Test;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Blueprint-based test for payment routes.
 * Kantra: karaf-camel-blueprint-test-support, karaf-junit4-test-to-junit5, camel4-assertMockEndpointsSatisfied.
 */
public class PaymentRouteBlueprintTest extends CamelBlueprintTestSupport {

    @Override
    protected String getBlueprintDescriptor() {
        return "OSGI-INF/blueprint/camel-context-test.xml";
    }

    @Override
    protected String useOverridePropertiesWithConfigAdmin(Dictionary<String, String> props) throws Exception {
        props.put("wmq.host", "localhost");
        props.put("wmq.port", "1414");
        return "com.example.paymentgateway";
    }

    @Override
    protected Dictionary<String, String> loadOverrideProperties() {
        Dictionary<String, String> props = new Hashtable<>();
        props.put("wmq.host", "localhost");
        return props;
    }

    @Test
    public void testPaymentRouteReceivesMessage() throws Exception {
        MockEndpoint mockOut = getMockEndpoint("mock:result");
        mockOut.expectedMinimumMessageCount(1);

        template.sendBody("direct:paymentIn", "PAY-001:100.00:USD");

        assertMockEndpointsSatisfied();
    }
}
