<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Date" %>
<!DOCTYPE html>
<html>
<head>
    <title>🏁 Hot Rods Car Repair Shop</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }
        .header {
            text-align: center;
            padding: 40px 0;
        }
        .header h1 {
            font-size: 3em;
            margin: 0;
            text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
        }
        .nav-menu {
            background: rgba(255,255,255,0.1);
            border-radius: 10px;
            padding: 20px;
            margin: 20px 0;
            text-align: center;
        }
        .nav-menu a {
            display: inline-block;
            margin: 10px 20px;
            padding: 15px 30px;
            background: rgba(255,255,255,0.2);
            color: white;
            text-decoration: none;
            border-radius: 5px;
            transition: all 0.3s ease;
        }
        .nav-menu a:hover {
            background: rgba(255,255,255,0.3);
            transform: translateY(-2px);
        }
        .features {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 20px;
            margin: 40px 0;
        }
        .feature-card {
            background: rgba(255,255,255,0.1);
            border-radius: 10px;
            padding: 20px;
            text-align: center;
        }
        .footer {
            text-align: center;
            padding: 20px;
            margin-top: 40px;
            border-top: 1px solid rgba(255,255,255,0.2);
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🏁 Hot Rods Car Repair</h1>
            <p>Your Premier Destination for Classic & Performance Car Service</p>
            <p><em>Current Time: <%= new Date() %></em></p>
        </div>

        <div class="nav-menu">
            <h2>Management System</h2>
            <a href="customers">👥 Customer Management</a>
            <a href="vehicles">🚗 Vehicle Registry</a>
            <a href="service-orders">🔧 Service Orders</a>
            <a href="reports">📊 Reports</a>
        </div>

        <div class="features">
            <div class="feature-card">
                <h3>🏎️ Classic Car Specialists</h3>
                <p>Expert service for vintage hot rods, muscle cars, and classic automobiles. Our mechanics understand the unique needs of performance vehicles.</p>
            </div>
            
            <div class="feature-card">
                <h3>⚡ Performance Tuning</h3>
                <p>Engine modifications, exhaust systems, suspension upgrades, and custom performance enhancements for maximum horsepower.</p>
            </div>
            
            <div class="feature-card">
                <h3>🔧 Complete Restoration</h3>
                <p>Full frame-off restorations, bodywork, paint, interior, and mechanical rebuilds to bring your classic back to showroom condition.</p>
            </div>
            
            <div class="feature-card">
                <h3>🏆 Race Preparation</h3>
                <p>Track-ready modifications, safety equipment installation, and race car maintenance for drag racing and circuit competition.</p>
            </div>
        </div>

        <%-- Deprecated JSP scriptlet usage --%>
        <%
            // This is a deprecated pattern - business logic in JSP
            String todayMessage = "";
            java.util.Calendar cal = java.util.Calendar.getInstance();
            int dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK);
            
            if (dayOfWeek == java.util.Calendar.SATURDAY || dayOfWeek == java.util.Calendar.SUNDAY) {
                todayMessage = "Weekend Special: 10% off all diagnostic services!";
            } else {
                todayMessage = "Weekday service available - Call for appointment!";
            }
        %>
        
        <div class="nav-menu">
            <h3>📞 Contact Information</h3>
            <p><strong>Address:</strong> 1234 Speed Demon Lane, Motor City, MC 48201</p>
            <p><strong>Phone:</strong> (555) HOT-RODS (468-7637)</p>
            <p><strong>Email:</strong> service@hotrodsrepair.com</p>
            <p><strong>Hours:</strong> Mon-Fri 8AM-6PM, Sat 9AM-4PM</p>
            <p><em><%= todayMessage %></em></p>
        </div>

        <div class="footer">
            <p>&copy; <%= java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) %> Hot Rods Car Repair Shop</p>
            <p>Built with JBoss EAP 7 - Demonstrating deprecated Java EE patterns</p>
        </div>
    </div>

    <%-- Deprecated inline JavaScript in JSP --%>
    <script>
        // Deprecated pattern - JavaScript directly in JSP
        function showWelcome() {
            alert('Welcome to Hot Rods Car Repair!\n\nThis application demonstrates deprecated JBoss EAP 7 patterns for migration analysis.');
        }
        
        // Auto-show welcome after 2 seconds
        setTimeout(showWelcome, 2000);
        
        // Deprecated pattern - mixing server-side and client-side logic
        var serverTime = '<%= new Date() %>';
        console.log('Server time when page loaded: ' + serverTime);
    </script>
</body>
</html>
