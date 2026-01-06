package com.redhat.bank.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.redhat.bank.model.Account;

/**
 * Data Access Object for Account operations.
 * Uses JDBC for database interactions - typical of Struts 1.x era applications.
 */
public class AccountDAO {

    private static final Log log = LogFactory.getLog(AccountDAO.class);

    /**
     * Retrieve all accounts from the database.
     */
    public List<Account> findAll() throws SQLException {
        List<Account> accounts = new ArrayList<Account>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM accounts WHERE active = TRUE ORDER BY id");

            while (rs.next()) {
                accounts.add(mapResultSetToAccount(rs));
            }
            log.info("Found " + accounts.size() + " accounts");
        } finally {
            closeResources(rs, stmt, conn);
        }

        return accounts;
    }

    /**
     * Find account by ID.
     */
    public Account findById(Long id) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement("SELECT * FROM accounts WHERE id = ?");
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToAccount(rs);
            }
        } finally {
            closeResources(rs, pstmt, conn);
        }

        return null;
    }

    /**
     * Find account by account number.
     */
    public Account findByAccountNumber(String accountNumber) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement("SELECT * FROM accounts WHERE account_number = ?");
            pstmt.setString(1, accountNumber);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToAccount(rs);
            }
        } finally {
            closeResources(rs, pstmt, conn);
        }

        return null;
    }

    /**
     * Create a new account.
     */
    public Account create(Account account) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO accounts (account_number, holder_name, account_type, balance, email, phone) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            pstmt.setString(1, account.getAccountNumber());
            pstmt.setString(2, account.getHolderName());
            pstmt.setString(3, account.getAccountType());
            pstmt.setBigDecimal(4, account.getBalance());
            pstmt.setString(5, account.getEmail());
            pstmt.setString(6, account.getPhone());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating account failed, no rows affected.");
            }

            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                account.setId(rs.getLong(1));
            }

            log.info("Created new account: " + account.getAccountNumber());
            return account;
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }

    /**
     * Update an existing account.
     */
    public Account update(Account account) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "UPDATE accounts SET holder_name = ?, account_type = ?, balance = ?, " +
                        "email = ?, phone = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, account.getHolderName());
            pstmt.setString(2, account.getAccountType());
            pstmt.setBigDecimal(3, account.getBalance());
            pstmt.setString(4, account.getEmail());
            pstmt.setString(5, account.getPhone());
            pstmt.setLong(6, account.getId());

            pstmt.executeUpdate();
            log.info("Updated account: " + account.getAccountNumber());
            return account;
        } finally {
            closeResources(null, pstmt, conn);
        }
    }

    /**
     * Delete (deactivate) an account.
     */
    public void delete(Long id) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement("UPDATE accounts SET active = FALSE WHERE id = ?");
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
            log.info("Deactivated account with ID: " + id);
        } finally {
            closeResources(null, pstmt, conn);
        }
    }

    /**
     * Deposit money into an account.
     */
    public void deposit(Long accountId, BigDecimal amount) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement("UPDATE accounts SET balance = balance + ? WHERE id = ?");
            pstmt.setBigDecimal(1, amount);
            pstmt.setLong(2, accountId);
            pstmt.executeUpdate();
            log.info("Deposited " + amount + " to account ID: " + accountId);
        } finally {
            closeResources(null, pstmt, conn);
        }
    }

    /**
     * Withdraw money from an account.
     */
    public boolean withdraw(Long accountId, BigDecimal amount) throws SQLException {
        Account account = findById(accountId);
        if (account == null || account.getBalance().compareTo(amount) < 0) {
            return false; // Insufficient funds or account not found
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement("UPDATE accounts SET balance = balance - ? WHERE id = ?");
            pstmt.setBigDecimal(1, amount);
            pstmt.setLong(2, accountId);
            pstmt.executeUpdate();
            log.info("Withdrew " + amount + " from account ID: " + accountId);
            return true;
        } finally {
            closeResources(null, pstmt, conn);
        }
    }

    /**
     * Map ResultSet row to Account object.
     */
    private Account mapResultSetToAccount(ResultSet rs) throws SQLException {
        Account account = new Account();
        account.setId(rs.getLong("id"));
        account.setAccountNumber(rs.getString("account_number"));
        account.setHolderName(rs.getString("holder_name"));
        account.setAccountType(rs.getString("account_type"));
        account.setBalance(rs.getBigDecimal("balance"));
        account.setEmail(rs.getString("email"));
        account.setPhone(rs.getString("phone"));
        account.setCreatedDate(rs.getTimestamp("created_date"));
        account.setActive(rs.getBoolean("active"));
        return account;
    }

    /**
     * Close database resources properly.
     */
    private void closeResources(ResultSet rs, Statement stmt, Connection conn) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            log.warn("Error closing ResultSet", e);
        }
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            log.warn("Error closing Statement", e);
        }
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            log.warn("Error closing Connection", e);
        }
    }
}
