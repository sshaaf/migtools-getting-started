<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bank Account System - Error</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
            min-height: 100vh;
            color: #e8e8e8;
            display: flex;
            flex-direction: column;
        }
        .header {
            background: rgba(0, 0, 0, 0.3);
            padding: 20px 40px;
            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        }
        .header h1 {
            color: #00d4ff;
            font-size: 28px;
            font-weight: 300;
            letter-spacing: 2px;
        }
        .container {
            flex: 1;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 30px;
        }
        .error-card {
            background: rgba(255, 255, 255, 0.03);
            border: 1px solid rgba(255, 77, 77, 0.3);
            border-radius: 16px;
            padding: 50px;
            text-align: center;
            max-width: 500px;
        }
        .error-icon {
            font-size: 80px;
            margin-bottom: 20px;
        }
        .error-title {
            font-size: 24px;
            color: #ff4d4d;
            margin-bottom: 15px;
        }
        .error-message {
            font-size: 16px;
            color: #888;
            margin-bottom: 30px;
            line-height: 1.6;
        }
        .btn {
            display: inline-block;
            padding: 14px 28px;
            border-radius: 8px;
            text-decoration: none;
            font-size: 14px;
            font-weight: 500;
            transition: all 0.3s;
        }
        .btn-primary {
            background: linear-gradient(135deg, #00d4ff 0%, #0099cc 100%);
            color: #000;
        }
        .btn-primary:hover {
            box-shadow: 0 5px 20px rgba(0, 212, 255, 0.4);
        }
        .error-details {
            margin-top: 30px;
            padding: 20px;
            background: rgba(0, 0, 0, 0.3);
            border-radius: 8px;
            text-align: left;
            font-family: 'Courier New', monospace;
            font-size: 12px;
            color: #666;
            overflow-x: auto;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>LEGACY BANK</h1>
    </div>
    
    <div class="container">
        <div class="error-card">
            <div class="error-icon">⚠️</div>
            <h2 class="error-title">Something Went Wrong</h2>
            <p class="error-message">
                <c:choose>
                    <c:when test="${not empty errorMessage}">
                        ${errorMessage}
                    </c:when>
                    <c:when test="${not empty exception}">
                        ${exception.message}
                    </c:when>
                    <c:otherwise>
                        An unexpected error occurred while processing your request.
                        Please try again or contact support if the problem persists.
                    </c:otherwise>
                </c:choose>
            </p>
            <html:link action="/listAccounts" styleClass="btn btn-primary">
                Return to Account List
            </html:link>
            
            <c:if test="${not empty exception}">
                <div class="error-details">
                    <strong>Technical Details:</strong><br/>
                    Exception: ${exception.class.name}<br/>
                    Message: ${exception.message}
                </div>
            </c:if>
        </div>
    </div>
</body>
</html>

