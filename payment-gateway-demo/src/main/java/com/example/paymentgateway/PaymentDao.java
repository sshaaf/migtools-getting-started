package com.example.paymentgateway;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * DAO for payment records using Oracle via OSGi DataSource.
 * Kantra rule: karaf-jdbc-setter-injection-to-constructor - prefer constructor injection.
 */
public class PaymentDao {

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void savePayment(String paymentId, String payload) {
        if (jdbcTemplate != null) {
            jdbcTemplate.update(
                "INSERT INTO payments (id, payload, created_at) VALUES (?, ?, CURRENT_TIMESTAMP)",
                paymentId, payload);
        }
    }
}
