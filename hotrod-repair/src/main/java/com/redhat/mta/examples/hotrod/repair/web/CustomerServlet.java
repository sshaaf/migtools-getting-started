package com.redhat.mta.examples.hotrod.repair.web;

import com.redhat.mta.examples.hotrod.repair.entity.Customer;
import com.redhat.mta.examples.hotrod.repair.service.CustomerServiceLocal;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

/**
 * Customer servlet using deprecated Servlet 3.1 patterns.
 * 
 * Deprecated features for EAP 8 migration:
 * - javax.servlet imports (migrate to jakarta.servlet)
 * - javax.ejb imports (migrate to jakarta.ejb)
 * - Legacy servlet patterns and response handling
 * - Deprecated HTML generation in servlets
 */
@WebServlet(name = "CustomerServlet", urlPatterns = {"/customers", "/customers/*"})
public class CustomerServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(CustomerServlet.class.getName());
    private static final long serialVersionUID = 1L;

    @EJB
    private CustomerServiceLocal customerService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        logger.info("Processing GET request for customers");
        
        String pathInfo = request.getPathInfo();
        String action = request.getParameter("action");
        
        // Deprecated response content type setting
        response.setContentType("text/html;charset=UTF-8");
        
        try {
            if (pathInfo != null && pathInfo.length() > 1) {
                // Handle /customers/{id}
                String customerIdStr = pathInfo.substring(1);
                Long customerId = Long.parseLong(customerIdStr);
                handleGetCustomer(request, response, customerId);
            } else if ("search".equals(action)) {
                handleSearchCustomers(request, response);
            } else {
                handleListCustomers(request, response);
            }
        } catch (Exception e) {
            logger.severe("Error processing GET request: " + e.getMessage());
            handleError(response, "Error retrieving customer information: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        logger.info("Processing POST request for customers");
        
        String action = request.getParameter("action");
        
        try {
            if ("create".equals(action)) {
                handleCreateCustomer(request, response);
            } else if ("update".equals(action)) {
                handleUpdateCustomer(request, response);
            } else if ("deactivate".equals(action)) {
                handleDeactivateCustomer(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
            }
        } catch (Exception e) {
            logger.severe("Error processing POST request: " + e.getMessage());
            handleError(response, "Error processing customer request: " + e.getMessage());
        }
    }

    private void handleListCustomers(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        List<Customer> customers = customerService.findAllCustomers();
        
        // Deprecated HTML generation in servlet
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Hot Rods Car Repair - Customers</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 20px; }");
        out.println("table { border-collapse: collapse; width: 100%; }");
        out.println("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
        out.println("th { background-color: #f2f2f2; }");
        out.println(".header { background-color: #333; color: white; padding: 10px; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        
        out.println("<div class='header'>");
        out.println("<h1>🏁 Hot Rods Car Repair - Customer Management</h1>");
        out.println("</div>");
        
        out.println("<h2>Customer List</h2>");
        out.println("<p><a href='customer-form.jsp'>Add New Customer</a></p>");
        
        // Deprecated search form in servlet
        out.println("<form method='get' action='customers'>");
        out.println("<input type='hidden' name='action' value='search'>");
        out.println("Search: <input type='text' name='searchTerm' placeholder='Enter name...'>");
        out.println("<input type='submit' value='Search'>");
        out.println("</form><br>");
        
        out.println("<table>");
        out.println("<tr>");
        out.println("<th>ID</th><th>Name</th><th>Email</th><th>Phone</th><th>City</th><th>Status</th><th>Actions</th>");
        out.println("</tr>");
        
        for (Customer customer : customers) {
            out.println("<tr>");
            out.println("<td>" + customer.getCustomerId() + "</td>");
            out.println("<td>" + customer.getFirstName() + " " + customer.getLastName() + "</td>");
            out.println("<td>" + customer.getEmail() + "</td>");
            out.println("<td>" + customer.getPhoneNumber() + "</td>");
            out.println("<td>" + (customer.getCity() != null ? customer.getCity() : "") + "</td>");
            out.println("<td>" + (customer.getIsActive() ? "Active" : "Inactive") + "</td>");
            out.println("<td>");
            out.println("<a href='customers/" + customer.getCustomerId() + "'>View</a> | ");
            out.println("<a href='customer-form.jsp?id=" + customer.getCustomerId() + "'>Edit</a>");
            if (customer.getIsActive()) {
                out.println(" | <a href='javascript:deactivateCustomer(" + customer.getCustomerId() + ")'>Deactivate</a>");
            }
            out.println("</td>");
            out.println("</tr>");
        }
        
        out.println("</table>");
        
        // Deprecated JavaScript in servlet
        out.println("<script>");
        out.println("function deactivateCustomer(customerId) {");
        out.println("  if (confirm('Are you sure you want to deactivate this customer?')) {");
        out.println("    var form = document.createElement('form');");
        out.println("    form.method = 'POST';");
        out.println("    form.action = 'customers';");
        out.println("    var actionInput = document.createElement('input');");
        out.println("    actionInput.type = 'hidden';");
        out.println("    actionInput.name = 'action';");
        out.println("    actionInput.value = 'deactivate';");
        out.println("    var idInput = document.createElement('input');");
        out.println("    idInput.type = 'hidden';");
        out.println("    idInput.name = 'customerId';");
        out.println("    idInput.value = customerId;");
        out.println("    form.appendChild(actionInput);");
        out.println("    form.appendChild(idInput);");
        out.println("    document.body.appendChild(form);");
        out.println("    form.submit();");
        out.println("  }");
        out.println("}");
        out.println("</script>");
        
        out.println("</body>");
        out.println("</html>");
    }

    private void handleGetCustomer(HttpServletRequest request, HttpServletResponse response, Long customerId) 
            throws IOException {
        
        Customer customer = customerService.findCustomerById(customerId);
        
        if (customer == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Customer not found");
            return;
        }
        
        // Deprecated HTML generation for single customer view
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head><title>Customer Details - " + customer.getFirstName() + " " + customer.getLastName() + "</title>");
        out.println("<style>body { font-family: Arial, sans-serif; margin: 20px; }</style>");
        out.println("</head>");
        out.println("<body>");
        
        out.println("<h1>Customer Details</h1>");
        out.println("<p><a href='customers'>← Back to Customer List</a></p>");
        
        out.println("<table>");
        out.println("<tr><th>Field</th><th>Value</th></tr>");
        out.println("<tr><td>ID</td><td>" + customer.getCustomerId() + "</td></tr>");
        out.println("<tr><td>Name</td><td>" + customer.getFirstName() + " " + customer.getLastName() + "</td></tr>");
        out.println("<tr><td>Email</td><td>" + customer.getEmail() + "</td></tr>");
        out.println("<tr><td>Phone</td><td>" + customer.getPhoneNumber() + "</td></tr>");
        out.println("<tr><td>Address</td><td>" + (customer.getAddress() != null ? customer.getAddress() : "") + "</td></tr>");
        out.println("<tr><td>City</td><td>" + (customer.getCity() != null ? customer.getCity() : "") + "</td></tr>");
        out.println("<tr><td>State</td><td>" + (customer.getState() != null ? customer.getState() : "") + "</td></tr>");
        out.println("<tr><td>ZIP</td><td>" + (customer.getZipCode() != null ? customer.getZipCode() : "") + "</td></tr>");
        out.println("<tr><td>Registration Date</td><td>" + customer.getRegistrationDate() + "</td></tr>");
        out.println("<tr><td>Last Visit</td><td>" + (customer.getLastVisitDate() != null ? customer.getLastVisitDate() : "Never") + "</td></tr>");
        out.println("<tr><td>Status</td><td>" + (customer.getIsActive() ? "Active" : "Inactive") + "</td></tr>");
        out.println("</table>");
        
        out.println("<p><a href='customer-form.jsp?id=" + customer.getCustomerId() + "'>Edit Customer</a></p>");
        
        out.println("</body>");
        out.println("</html>");
    }

    private void handleSearchCustomers(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String searchTerm = request.getParameter("searchTerm");
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            handleListCustomers(request, response);
            return;
        }
        
        List<Customer> customers = customerService.searchCustomersByName(searchTerm.trim());
        
        // Store search results in session - deprecated pattern
        HttpSession session = request.getSession();
        session.setAttribute("searchResults", customers);
        session.setAttribute("searchTerm", searchTerm);
        
        // Forward to JSP - deprecated pattern, should use modern templating
        request.setAttribute("customers", customers);
        request.setAttribute("searchTerm", searchTerm);
        try {
            request.getRequestDispatcher("/customer-search-results.jsp").forward(request, response);
        } catch (ServletException e) {
            handleError(response, "Error forwarding to search results: " + e.getMessage());
        }
    }

    private void handleCreateCustomer(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // Deprecated parameter extraction pattern
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        String address = request.getParameter("address");
        String city = request.getParameter("city");
        String state = request.getParameter("state");
        String zipCode = request.getParameter("zipCode");
        String notes = request.getParameter("customerNotes");
        String contactMethod = request.getParameter("preferredContactMethod");
        
        Customer customer = new Customer(firstName, lastName, email, phoneNumber);
        customer.setAddress(address);
        customer.setCity(city);
        customer.setState(state);
        customer.setZipCode(zipCode);
        customer.setCustomerNotes(notes);
        customer.setPreferredContactMethod(contactMethod);
        
        Customer createdCustomer = customerService.createCustomer(customer);
        
        // Deprecated redirect pattern
        response.sendRedirect("customers/" + createdCustomer.getCustomerId());
    }

    private void handleUpdateCustomer(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        Long customerId = Long.parseLong(request.getParameter("customerId"));
        Customer customer = customerService.findCustomerById(customerId);
        
        if (customer == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Customer not found");
            return;
        }
        
        // Deprecated manual parameter mapping
        customer.setFirstName(request.getParameter("firstName"));
        customer.setLastName(request.getParameter("lastName"));
        customer.setPhoneNumber(request.getParameter("phoneNumber"));
        customer.setAddress(request.getParameter("address"));
        customer.setCity(request.getParameter("city"));
        customer.setState(request.getParameter("state"));
        customer.setZipCode(request.getParameter("zipCode"));
        customer.setCustomerNotes(request.getParameter("customerNotes"));
        customer.setPreferredContactMethod(request.getParameter("preferredContactMethod"));
        
        Customer updatedCustomer = customerService.updateCustomer(customer);
        
        response.sendRedirect("customers/" + updatedCustomer.getCustomerId());
    }

    private void handleDeactivateCustomer(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        Long customerId = Long.parseLong(request.getParameter("customerId"));
        customerService.deactivateCustomer(customerId);
        
        response.sendRedirect("customers");
    }

    private void handleError(HttpServletResponse response, String errorMessage) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head><title>Error</title></head>");
        out.println("<body>");
        out.println("<h1>Error</h1>");
        out.println("<p>" + errorMessage + "</p>");
        out.println("<p><a href='customers'>Back to Customer List</a></p>");
        out.println("</body>");
        out.println("</html>");
    }
}
