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
import com.redhat.bank.model.Account;

/**
 * Struts Action to view a single account's details.
 */
public class ViewAccountAction extends Action {

    private static final Log log = LogFactory.getLog(ViewAccountAction.class);
    
    private AccountDAO accountDAO = new AccountDAO();

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        String idParam = request.getParameter("id");
        log.info("ViewAccountAction: Viewing account ID = " + idParam);
        
        if (idParam == null || idParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Account ID is required");
            return mapping.findForward("error");
        }
        
        try {
            Long id = Long.parseLong(idParam);
            Account account = accountDAO.findById(id);
            
            if (account == null) {
                request.setAttribute("errorMessage", "Account not found with ID: " + id);
                return mapping.findForward("error");
            }
            
            request.setAttribute("account", account);
            log.info("Found account: " + account.getAccountNumber());
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid account ID format");
            return mapping.findForward("error");
        } catch (Exception e) {
            log.error("Error viewing account", e);
            request.setAttribute("errorMessage", "Error loading account: " + e.getMessage());
            return mapping.findForward("error");
        }
        
        return mapping.findForward("success");
    }
}

