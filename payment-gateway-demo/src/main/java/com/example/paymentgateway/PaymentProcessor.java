package com.example.paymentgateway;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Processes payment messages from the gateway.
 * Uses exchange.getIn() / exchange.getOut() for Camel 2.x (Kantra: migrate to getMessage()).
 */
public class PaymentProcessor implements Processor {

    private PaymentDao paymentDao;

    public void setPaymentDao(PaymentDao paymentDao) {
        this.paymentDao = paymentDao;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String body = exchange.getIn().getBody(String.class);
        String paymentId = exchange.getIn().getHeader("PaymentId", String.class);
        if (paymentId == null) {
            paymentId = "PAY-" + System.currentTimeMillis();
        }
        if (paymentDao != null) {
            paymentDao.savePayment(paymentId, body);
        }
        exchange.getOut().setBody("OK:" + paymentId);
        exchange.getOut().setHeader("PaymentId", paymentId);
    }
}
