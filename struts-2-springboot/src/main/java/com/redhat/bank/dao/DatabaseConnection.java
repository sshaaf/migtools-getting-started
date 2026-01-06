package com.redhat.bank.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Database connection manager for the bank application.
 * Uses H2 embedded database for simplicity.
 */
public class DatabaseConnection {

    private static final Log log = LogFactory.getLog(DatabaseConnection.class);
    
    private static final String DB_URL = "jdbc:h2:mem:bankdb;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    
    private static boolean initialized = false;

    static {
        try {
            Class.forName("org.h2.Driver");
            log.info("H2 Database driver loaded successfully");
        } catch (ClassNotFoundException e) {
            log.error("Failed to load H2 driver", e);
            throw new RuntimeException("Failed to load H2 driver", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        if (!initialized) {
            initializeDatabase(conn);
            initialized = true;
        }
        return conn;
    }

    private static void initializeDatabase(Connection conn) throws SQLException {
        log.info("Initializing database schema and sample data...");
        
        Statement stmt = conn.createStatement();
        
        // Create accounts table
        String createTableSQL = 
            "CREATE TABLE IF NOT EXISTS accounts (" +
            "    id BIGINT AUTO_INCREMENT PRIMARY KEY," +
            "    account_number VARCHAR(20) NOT NULL UNIQUE," +
            "    holder_name VARCHAR(100) NOT NULL," +
            "    account_type VARCHAR(20) NOT NULL," +
            "    balance DECIMAL(15,2) DEFAULT 0.00," +
            "    email VARCHAR(100)," +
            "    phone VARCHAR(20)," +
            "    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "    active BOOLEAN DEFAULT TRUE" +
            ")";
        stmt.execute(createTableSQL);
        log.info("Accounts table created");

        // Insert sample data
        String[] insertStatements = {
            "INSERT INTO accounts (account_number, holder_name, account_type, balance, email, phone) " +
            "VALUES ('ACC-001-2024', 'John Smith', 'SAVINGS', 15000.50, 'john.smith@email.com', '555-0101')",
            
            "INSERT INTO accounts (account_number, holder_name, account_type, balance, email, phone) " +
            "VALUES ('ACC-002-2024', 'Sarah Johnson', 'CHECKING', 8750.25, 'sarah.j@email.com', '555-0102')",
            
            "INSERT INTO accounts (account_number, holder_name, account_type, balance, email, phone) " +
            "VALUES ('ACC-003-2024', 'Tech Solutions Inc.', 'BUSINESS', 125000.00, 'finance@techsolutions.com', '555-0103')",
            
            "INSERT INTO accounts (account_number, holder_name, account_type, balance, email, phone) " +
            "VALUES ('ACC-004-2024', 'Maria Garcia', 'SAVINGS', 3200.75, 'maria.garcia@email.com', '555-0104')",
            
            "INSERT INTO accounts (account_number, holder_name, account_type, balance, email, phone) " +
            "VALUES ('ACC-005-2024', 'Robert Chen', 'CHECKING', 22500.00, 'r.chen@email.com', '555-0105')"
        };

        for (String sql : insertStatements) {
            try {
                stmt.execute(sql);
            } catch (SQLException e) {
                // Ignore duplicate key errors on restart
                if (!e.getMessage().contains("Unique index or primary key violation")) {
                    throw e;
                }
            }
        }
        
        stmt.close();
        log.info("Sample data inserted successfully");
    }
}
