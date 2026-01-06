package com.redhat.bank.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.redhat.bank.dao.AccountDAO;
import com.redhat.bank.form.AccountForm;
import com.redhat.bank.model.Account;

/**
 * Struts Action to prepare the edit form for an account.
 */
public class EditAccountAction extends Action {

    private static final Log log = LogFactory.getLog(EditAccountAction.class);
    
    private AccountDAO accountDAO = new AccountDAO();

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        String idParam = request.getParameter("id");
        log.info("EditAccountAction: Preparing to edit account ID = " + idParam);
        
        AccountForm accountForm = (AccountForm) form;
        
        // If ID is provided, load existing account for editing
        if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                Long id = Long.parseLong(idParam);
                Account account = accountDAO.findById(id);
                
                if (account == null) {
                    request.setAttribute("errorMessage", "Account not found");
                    return mapping.findForward("error");
                }
                
                // Populate form with existing account data
                accountForm.setId(String.valueOf(account.getId()));
                accountForm.setAccountNumber(account.getAccountNumber());
                accountForm.setHolderName(account.getHolderName());
                accountForm.setAccountType(account.getAccountType());
                accountForm.setBalance(account.getBalance().toString());
                accountForm.setEmail(account.getEmail());
                accountForm.setPhone(account.getPhone());
                
                request.setAttribute("editing", true);
                log.info("Loaded account for editing: " + account.getAccountNumber());
                
            } catch (Exception e) {
                log.error("Error loading account for edit", e);
                request.setAttribute("errorMessage", "Error loading account: " + e.getMessage());
                return mapping.findForward("error");
            }
        } else {
            // New account - generate account number
            String newAccountNumber = generateAccountNumber();
            accountForm.setAccountNumber(newAccountNumber);
            accountForm.setAccountType("SAVINGS");
            accountForm.setBalance("0.00");
            request.setAttribute("editing", false);
            log.info("Preparing new account form with number: " + newAccountNumber);
        }
        
        return mapping.findForward("success");
    }

    private String generateAccountNumber() {
        // Generate a simple account number
        long timestamp = System.currentTimeMillis() % 100000;
        return String.format("ACC-%05d-%d", timestamp, (int)(Math.random() * 1000));
    }
}

