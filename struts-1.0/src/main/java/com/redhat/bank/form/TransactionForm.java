package com.redhat.bank.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * Struts ActionForm for transaction operations (deposit/withdraw).
 */
public class TransactionForm extends ActionForm {

    private static final long serialVersionUID = 1L;

    private String accountId;
    private String amount;
    private String transactionType; // DEPOSIT or WITHDRAW

    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        this.accountId = null;
        this.amount = null;
        this.transactionType = null;
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();

        if (accountId == null || accountId.trim().isEmpty()) {
            errors.add("accountId", new ActionMessage("error.accountId.required"));
        }

        if (amount == null || amount.trim().isEmpty()) {
            errors.add("amount", new ActionMessage("error.amount.required"));
        } else {
            try {
                double amt = Double.parseDouble(amount);
                if (amt <= 0) {
                    errors.add("amount", new ActionMessage("error.amount.positive"));
                }
            } catch (NumberFormatException e) {
                errors.add("amount", new ActionMessage("error.amount.invalid"));
            }
        }

        return errors;
    }

    // Getters and Setters
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
}

