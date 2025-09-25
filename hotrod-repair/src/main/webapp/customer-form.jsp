<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.redhat.mta.examples.hotrod.repair.entity.Customer" %>
<%@ page import="com.redhat.mta.examples.hotrod.repair.service.CustomerServiceLocal" %>
<%@ page import="javax.naming.InitialContext" %>
<!DOCTYPE html>
<html>
<head>
    <title>Customer Form - Hot Rods Car Repair</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }
        .container { max-width: 600px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .header { background-color: #333; color: white; padding: 15px; margin: -20px -20px 20px -20px; border-radius: 8px 8px 0 0; }
        .form-group { margin-bottom: 15px; }
        label { display: block; margin-bottom: 5px; font-weight: bold; }
        input[type="text"], input[type="email"], input[type="tel"], textarea, select {
            width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box;
        }
        textarea { height: 100px; resize: vertical; }
        .button-group { text-align: center; margin-top: 20px; }
        .btn { padding: 10px 20px; margin: 0 10px; border: none; border-radius: 4px; cursor: pointer; text-decoration: none; display: inline-block; }
        .btn-primary { background-color: #007bff; color: white; }
        .btn-secondary { background-color: #6c757d; color: white; }
        .btn:hover { opacity: 0.8; }
        .error { color: red; margin-top: 10px; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🏁 Hot Rods Car Repair - Customer Form</h1>
        </div>

        <%-- Deprecated JSP scriptlet for business logic --%>
        <%
            Customer customer = null;
            String customerId = request.getParameter("id");
            String pageTitle = "Add New Customer";
            String formAction = "customers";
            String actionValue = "create";
            
            // Deprecated pattern - business logic in JSP
            if (customerId != null && !customerId.trim().isEmpty()) {
                try {
                    // Deprecated JNDI lookup pattern in JSP
                    InitialContext ctx = new InitialContext();
                    CustomerServiceLocal customerService = (CustomerServiceLocal) ctx.lookup("java:app/hotrods-car-repair/CustomerService!com.redhat.mta.examples.hotrod.repair.service.CustomerServiceLocal");
                    customer = customerService.findCustomerById(Long.parseLong(customerId));
                    
                    if (customer != null) {
                        pageTitle = "Edit Customer";
                        actionValue = "update";
                    }
                } catch (Exception e) {
                    // Deprecated error handling in JSP
                    out.println("<div class='error'>Error loading customer: " + e.getMessage() + "</div>");
                }
            }
        %>

        <h2><%= pageTitle %></h2>
        
        <form method="post" action="<%= formAction %>" onsubmit="return validateForm()">
            <input type="hidden" name="action" value="<%= actionValue %>">
            <% if (customer != null) { %>
                <input type="hidden" name="customerId" value="<%= customer.getCustomerId() %>">
            <% } %>
            
            <div class="form-group">
                <label for="firstName">First Name *</label>
                <input type="text" id="firstName" name="firstName" required
                       value="<%= customer != null ? customer.getFirstName() : "" %>">
            </div>
            
            <div class="form-group">
                <label for="lastName">Last Name *</label>
                <input type="text" id="lastName" name="lastName" required
                       value="<%= customer != null ? customer.getLastName() : "" %>">
            </div>
            
            <div class="form-group">
                <label for="email">Email *</label>
                <input type="email" id="email" name="email" required
                       value="<%= customer != null ? customer.getEmail() : "" %>"
                       <%= customer != null ? "readonly" : "" %>>
            </div>
            
            <div class="form-group">
                <label for="phoneNumber">Phone Number *</label>
                <input type="tel" id="phoneNumber" name="phoneNumber" required
                       value="<%= customer != null ? customer.getPhoneNumber() : "" %>">
            </div>
            
            <div class="form-group">
                <label for="address">Address</label>
                <input type="text" id="address" name="address"
                       value="<%= customer != null && customer.getAddress() != null ? customer.getAddress() : "" %>">
            </div>
            
            <div class="form-group">
                <label for="city">City</label>
                <input type="text" id="city" name="city"
                       value="<%= customer != null && customer.getCity() != null ? customer.getCity() : "" %>">
            </div>
            
            <div class="form-group">
                <label for="state">State</label>
                <input type="text" id="state" name="state" maxlength="2"
                       value="<%= customer != null && customer.getState() != null ? customer.getState() : "" %>">
            </div>
            
            <div class="form-group">
                <label for="zipCode">ZIP Code</label>
                <input type="text" id="zipCode" name="zipCode"
                       value="<%= customer != null && customer.getZipCode() != null ? customer.getZipCode() : "" %>">
            </div>
            
            <div class="form-group">
                <label for="preferredContactMethod">Preferred Contact Method</label>
                <select id="preferredContactMethod" name="preferredContactMethod">
                    <option value="">Select...</option>
                    <option value="EMAIL" <%= customer != null && "EMAIL".equals(customer.getPreferredContactMethod()) ? "selected" : "" %>>Email</option>
                    <option value="PHONE" <%= customer != null && "PHONE".equals(customer.getPreferredContactMethod()) ? "selected" : "" %>>Phone</option>
                    <option value="TEXT" <%= customer != null && "TEXT".equals(customer.getPreferredContactMethod()) ? "selected" : "" %>>Text Message</option>
                </select>
            </div>
            
            <div class="form-group">
                <label for="customerNotes">Customer Notes</label>
                <textarea id="customerNotes" name="customerNotes" placeholder="Special instructions, vehicle preferences, etc..."><%= customer != null && customer.getCustomerNotes() != null ? customer.getCustomerNotes() : "" %></textarea>
            </div>
            
            <div class="button-group">
                <button type="submit" class="btn btn-primary">
                    <%= customer != null ? "Update Customer" : "Create Customer" %>
                </button>
                <a href="customers" class="btn btn-secondary">Cancel</a>
            </div>
        </form>
    </div>

    <%-- Deprecated inline JavaScript in JSP --%>
    <script>
        // Deprecated client-side validation pattern
        function validateForm() {
            var firstName = document.getElementById('firstName').value.trim();
            var lastName = document.getElementById('lastName').value.trim();
            var email = document.getElementById('email').value.trim();
            var phoneNumber = document.getElementById('phoneNumber').value.trim();
            
            if (firstName.length < 2) {
                alert('First name must be at least 2 characters long');
                return false;
            }
            
            if (lastName.length < 2) {
                alert('Last name must be at least 2 characters long');
                return false;
            }
            
            // Deprecated email validation pattern
            var emailPattern = /^[A-Za-z0-9+_.-]+@(.+)$/;
            if (!emailPattern.test(email)) {
                alert('Please enter a valid email address');
                return false;
            }
            
            // Deprecated phone validation pattern
            var phonePattern = /^\+?[1-9]\d{1,14}$/;
            var cleanPhone = phoneNumber.replace(/[\s\-\(\)]/g, '');
            if (!phonePattern.test(cleanPhone)) {
                alert('Please enter a valid phone number');
                return false;
            }
            
            return true;
        }
        
        // Deprecated pattern - auto-formatting in client-side JavaScript
        document.getElementById('phoneNumber').addEventListener('input', function(e) {
            var value = e.target.value.replace(/\D/g, '');
            if (value.length >= 6) {
                value = value.replace(/(\d{3})(\d{3})(\d{4})/, '($1) $2-$3');
            } else if (value.length >= 3) {
                value = value.replace(/(\d{3})(\d{3})/, '($1) $2');
            }
            e.target.value = value;
        });
        
        // Deprecated pattern - state code auto-uppercase
        document.getElementById('state').addEventListener('input', function(e) {
            e.target.value = e.target.value.toUpperCase();
        });
    </script>
</body>
</html>
