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
import com.redhat.bank.form.TransactionForm;

/**
 * Struts Action to handle deposit and withdrawal transactions.
 */
public class TransactionAction extends Action {

    private static final Log log = LogFactory.getLog(TransactionAction.class);
    
    private AccountDAO accountDAO = new AccountDAO();

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        TransactionForm txForm = (TransactionForm) form;
        String transactionType = txForm.getTransactionType();
        
        log.info("TransactionAction: Processing " + transactionType + 
                 " for account " + txForm.getAccountId());
        
        try {
            Long accountId = Long.parseLong(txForm.getAccountId());
            BigDecimal amount = new BigDecimal(txForm.getAmount());
            
            ActionMessages messages = new ActionMessages();
            
            if ("DEPOSIT".equals(transactionType)) {
                accountDAO.deposit(accountId, amount);
                messages.add(ActionMessages.GLOBAL_MESSAGE, 
                    new ActionMessage("message.deposit.success", amount));
                log.info("Deposit successful: " + amount);
                
            } else if ("WITHDRAW".equals(transactionType)) {
                boolean success = accountDAO.withdraw(accountId, amount);
                if (success) {
                    messages.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("message.withdraw.success", amount));
                    log.info("Withdrawal successful: " + amount);
                } else {
                    request.setAttribute("errorMessage", "Insufficient funds for withdrawal");
                    return mapping.findForward("error");
                }
            } else {
                request.setAttribute("errorMessage", "Invalid transaction type");
                return mapping.findForward("error");
            }
            
            saveMessages(request, messages);
            
            // Redirect back to account view
            request.setAttribute("accountId", accountId);
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid amount format");
            return mapping.findForward("error");
        } catch (Exception e) {
            log.error("Error processing transaction", e);
            request.setAttribute("errorMessage", "Error processing transaction: " + e.getMessage());
            return mapping.findForward("error");
        }
        
        return mapping.findForward("success");
    }
}

