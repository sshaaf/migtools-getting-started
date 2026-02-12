package com.example.paymentgateway;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Creates test schema for Blueprint tests.
 */
public class TestDbInit {

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void init() {
        if (jdbcTemplate != null) {
            jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS payments (id VARCHAR(255), payload VARCHAR(4000), created_at TIMESTAMP)");
        }
    }
}
