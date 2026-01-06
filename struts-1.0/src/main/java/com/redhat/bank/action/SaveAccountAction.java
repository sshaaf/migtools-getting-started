package com.redhat.bank.action;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.redhat.bank.dao.AccountDAO;
import com.redhat.bank.form.AccountForm;
import com.redhat.bank.model.Account;

/**
 * Struts Action to save (create or update) an account.
 */
public class SaveAccountAction extends Action {

    private static final Log log = LogFactory.getLog(SaveAccountAction.class);
    
    private AccountDAO accountDAO = new AccountDAO();

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        AccountForm accountForm = (AccountForm) form;
        log.info("SaveAccountAction: Saving account - " + accountForm.getHolderName());
        
        try {
            Account account;
            boolean isNew = (accountForm.getId() == null || accountForm.getId().trim().isEmpty());
            
            if (isNew) {
                // Create new account
                account = new Account();
                account.setAccountNumber(accountForm.getAccountNumber());
            } else {
                // Load existing account
                account = accountDAO.findById(Long.parseLong(accountForm.getId()));
                if (account == null) {
                    request.setAttribute("errorMessage", "Account not found");
                    return mapping.findForward("error");
                }
            }
            
            // Update account fields
            account.setHolderName(accountForm.getHolderName());
            account.setAccountType(accountForm.getAccountType());
            account.setBalance(new BigDecimal(accountForm.getBalance()));
            account.setEmail(accountForm.getEmail());
            account.setPhone(accountForm.getPhone());
            
            if (isNew) {
                accountDAO.create(account);
                log.info("Created new account: " + account.getAccountNumber());
            } else {
                accountDAO.update(account);
                log.info("Updated account: " + account.getAccountNumber());
            }
            
            // Add success message
            ActionMessages messages = new ActionMessages();
            messages.add(ActionMessages.GLOBAL_MESSAGE, 
                new ActionMessage("message.account.saved", account.getAccountNumber()));
            saveMessages(request, messages);
            
        } catch (Exception e) {
            log.error("Error saving account", e);
            request.setAttribute("errorMessage", "Error saving account: " + e.getMessage());
            return mapping.findForward("error");
        }
        
        return mapping.findForward("success");
    }
}

