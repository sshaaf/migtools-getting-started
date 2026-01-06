package com.redhat.bank.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.redhat.bank.dao.AccountDAO;
import com.redhat.bank.model.Account;

/**
 * Struts Action to list all bank accounts.
 * Demonstrates classic Struts 1.x action pattern.
 */
public class ListAccountsAction extends Action {

    private static final Log log = LogFactory.getLog(ListAccountsAction.class);
    
    private AccountDAO accountDAO = new AccountDAO();

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        log.info("ListAccountsAction: Fetching all accounts");
        
        try {
            List<Account> accounts = accountDAO.findAll();
            request.setAttribute("accounts", accounts);
            
            // Calculate summary statistics
            double totalBalance = 0;
            int savingsCount = 0;
            int checkingCount = 0;
            int businessCount = 0;
            
            for (Account account : accounts) {
                totalBalance += account.getBalance().doubleValue();
                if ("SAVINGS".equals(account.getAccountType())) {
                    savingsCount++;
                } else if ("CHECKING".equals(account.getAccountType())) {
                    checkingCount++;
                } else if ("BUSINESS".equals(account.getAccountType())) {
                    businessCount++;
                }
            }
            
            request.setAttribute("totalBalance", totalBalance);
            request.setAttribute("savingsCount", savingsCount);
            request.setAttribute("checkingCount", checkingCount);
            request.setAttribute("businessCount", businessCount);
            
            log.info("Found " + accounts.size() + " accounts with total balance: " + totalBalance);
            
        } catch (Exception e) {
            log.error("Error fetching accounts", e);
            request.setAttribute("errorMessage", "Error loading accounts: " + e.getMessage());
            return mapping.findForward("error");
        }
        
        return mapping.findForward("success");
    }
}

