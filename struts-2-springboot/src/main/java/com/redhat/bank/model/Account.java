package com.redhat.bank.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Account entity representing a bank account.
 * This is a simple JavaBean used throughout the Struts application.
 */
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String accountNumber;
    private String holderName;
    private String accountType; // SAVINGS, CHECKING, BUSINESS
    private BigDecimal balance;
    private String email;
    private String phone;
    private Date createdDate;
    private boolean active;

    public Account() {
        this.balance = BigDecimal.ZERO;
        this.active = true;
        this.createdDate = new Date();
    }

    public Account(String accountNumber, String holderName, String accountType, BigDecimal balance) {
        this();
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.accountType = accountType;
        this.balance = balance;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", accountNumber='" + accountNumber + '\'' +
                ", holderName='" + holderName + '\'' +
                ", accountType='" + accountType + '\'' +
                ", balance=" + balance +
                '}';
    }
}

