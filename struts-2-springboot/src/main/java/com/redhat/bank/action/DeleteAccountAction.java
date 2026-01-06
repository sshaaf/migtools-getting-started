package com.redhat.bank.action;

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

/**
 * Struts Action to delete (deactivate) an account.
 */
public class DeleteAccountAction extends Action {

    private static final Log log = LogFactory.getLog(DeleteAccountAction.class);
    
    private AccountDAO accountDAO = new AccountDAO();

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        String idParam = request.getParameter("id");
        log.info("DeleteAccountAction: Deleting account ID = " + idParam);
        
        if (idParam == null || idParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Account ID is required");
            return mapping.findForward("error");
        }
        
        try {
            Long id = Long.parseLong(idParam);
            accountDAO.delete(id);
            
            ActionMessages messages = new ActionMessages();
            messages.add(ActionMessages.GLOBAL_MESSAGE, 
                new ActionMessage("message.account.deleted"));
            saveMessages(request, messages);
            
            log.info("Successfully deleted account ID: " + id);
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid account ID format");
            return mapping.findForward("error");
        } catch (Exception e) {
            log.error("Error deleting account", e);
            request.setAttribute("errorMessage", "Error deleting account: " + e.getMessage());
            return mapping.findForward("error");
        }
        
        return mapping.findForward("success");
    }
}

