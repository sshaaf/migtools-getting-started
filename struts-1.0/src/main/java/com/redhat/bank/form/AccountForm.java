package com.redhat.bank.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * Struts ActionForm for Account data.
 * This form handles validation and data binding for account operations.
 */
public class AccountForm extends ActionForm {

    private static final long serialVersionUID = 1L;

    private String id;
    private String accountNumber;
    private String holderName;
    private String accountType;
    private String balance;
    private String email;
    private String phone;

    // Reset form fields
    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        this.id = null;
        this.accountNumber = null;
        this.holderName = null;
        this.accountType = "SAVINGS";
        this.balance = "0.00";
        this.email = null;
        this.phone = null;
    }

    // Validate form input
    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();

        // Only validate on save actions
        String path = mapping.getPath();
        if (path != null && (path.contains("save") || path.contains("create"))) {
            
            if (holderName == null || holderName.trim().isEmpty()) {
                errors.add("holderName", new ActionMessage("error.holderName.required"));
            }

            if (accountType == null || accountType.trim().isEmpty()) {
                errors.add("accountType", new ActionMessage("error.accountType.required"));
            }

            if (balance != null && !balance.trim().isEmpty()) {
                try {
                    Double.parseDouble(balance);
                } catch (NumberFormatException e) {
                    errors.add("balance", new ActionMessage("error.balance.invalid"));
                }
            }

            if (email != null && !email.trim().isEmpty()) {
                if (!email.contains("@")) {
                    errors.add("email", new ActionMessage("error.email.invalid"));
                }
            }
        }

        return errors;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
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
}
