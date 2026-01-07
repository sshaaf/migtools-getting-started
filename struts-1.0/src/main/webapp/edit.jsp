<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bank Account System - <c:choose><c:when test="${editing}">Edit</c:when><c:otherwise>Create</c:otherwise></c:choose> Account</title>
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
        }
        .header {
            background: rgba(0, 0, 0, 0.3);
            padding: 20px 40px;
            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .header h1 {
            color: #00d4ff;
            font-size: 28px;
            font-weight: 300;
            letter-spacing: 2px;
        }
        .container {
            max-width: 700px;
            margin: 0 auto;
            padding: 30px;
        }
        .btn {
            display: inline-block;
            padding: 12px 24px;
            border-radius: 8px;
            text-decoration: none;
            font-size: 14px;
            font-weight: 500;
            transition: all 0.3s;
            border: none;
            cursor: pointer;
        }
        .btn-secondary {
            background: rgba(255, 255, 255, 0.1);
            color: #fff;
            border: 1px solid rgba(255, 255, 255, 0.2);
        }
        .btn-secondary:hover {
            background: rgba(255, 255, 255, 0.2);
        }
        .btn-primary {
            background: linear-gradient(135deg, #00d4ff 0%, #0099cc 100%);
            color: #000;
        }
        .btn-primary:hover {
            box-shadow: 0 5px 20px rgba(0, 212, 255, 0.4);
        }
        .card {
            background: rgba(255, 255, 255, 0.03);
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 16px;
            overflow: hidden;
        }
        .card-header {
            background: rgba(0, 0, 0, 0.3);
            padding: 25px 30px;
            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        }
        .card-header h2 {
            font-size: 20px;
            color: #fff;
        }
        .card-header p {
            font-size: 13px;
            color: #888;
            margin-top: 5px;
        }
        .card-body {
            padding: 30px;
        }
        .form-group {
            margin-bottom: 25px;
        }
        .form-group label {
            display: block;
            font-size: 11px;
            text-transform: uppercase;
            letter-spacing: 1px;
            color: #888;
            margin-bottom: 10px;
        }
        .form-group input,
        .form-group select {
            width: 100%;
            padding: 14px 18px;
            background: rgba(0, 0, 0, 0.3);
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 8px;
            color: #fff;
            font-size: 16px;
            transition: border-color 0.3s;
        }
        .form-group input:focus,
        .form-group select:focus {
            outline: none;
            border-color: #00d4ff;
        }
        .form-group input:disabled,
        .form-group input[readonly] {
            background: rgba(0, 0, 0, 0.5);
            color: #00d4ff;
        }
        .form-group select option {
            background: #1a1a2e;
            color: #fff;
        }
        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
        }
        .form-actions {
            display: flex;
            gap: 15px;
            margin-top: 30px;
            padding-top: 20px;
            border-top: 1px solid rgba(255, 255, 255, 0.1);
        }
        .errors {
            background: rgba(255, 77, 77, 0.1);
            border: 1px solid rgba(255, 77, 77, 0.3);
            border-radius: 8px;
            padding: 15px 20px;
            margin-bottom: 20px;
            color: #ff4d4d;
        }
        .errors ul {
            margin: 0;
            padding-left: 20px;
        }
        .account-number-display {
            font-family: 'Courier New', monospace;
            color: #00d4ff;
            font-size: 18px;
            letter-spacing: 2px;
        }
        .help-text {
            font-size: 12px;
            color: #666;
            margin-top: 8px;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>LEGACY BANK</h1>
        <html:link action="/listAccounts" styleClass="btn btn-secondary">
            ← Back to List
        </html:link>
    </div>
    
    <div class="container">
        <!-- Validation Errors -->
        <html:errors/>
        
        <div class="card">
            <div class="card-header">
                <h2>
                    <c:choose>
                        <c:when test="${editing}">Edit Account</c:when>
                        <c:otherwise>Create New Account</c:otherwise>
                    </c:choose>
                </h2>
                <p>
                    <c:choose>
                        <c:when test="${editing}">Update the account information below</c:when>
                        <c:otherwise>Fill in the details to create a new bank account</c:otherwise>
                    </c:choose>
                </p>
            </div>
            <div class="card-body">
                <html:form action="/saveAccount" method="post">
                    <html:hidden property="id"/>
                    
                    <div class="form-group">
                        <label>Account Number</label>
                        <c:choose>
                            <c:when test="${editing}">
                                <html:text property="accountNumber" disabled="true" styleClass="account-number-display"/>
                                <html:hidden property="accountNumber"/>
                            </c:when>
                            <c:otherwise>
                                <html:text property="accountNumber" readonly="true" styleClass="account-number-display"/>
                            </c:otherwise>
                        </c:choose>
                        <p class="help-text">Account numbers are automatically generated</p>
                    </div>
                    
                    <div class="form-group">
                        <label>Account Holder Name *</label>
                        <html:text property="holderName" maxlength="100"/>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label>Account Type *</label>
                            <html:select property="accountType">
                                <html:option value="SAVINGS">Savings Account</html:option>
                                <html:option value="CHECKING">Checking Account</html:option>
                                <html:option value="BUSINESS">Business Account</html:option>
                            </html:select>
                        </div>
                        
                        <div class="form-group">
                            <label>Initial Balance ($)</label>
                            <html:text property="balance"/>
                        </div>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label>Email Address</label>
                            <html:text property="email" maxlength="100"/>
                        </div>
                        
                        <div class="form-group">
                            <label>Phone Number</label>
                            <html:text property="phone" maxlength="20"/>
                        </div>
                    </div>
                    
                    <div class="form-actions">
                        <html:submit styleClass="btn btn-primary">
                            <c:choose>
                                <c:when test="${editing}">Update Account</c:when>
                                <c:otherwise>Create Account</c:otherwise>
                            </c:choose>
                        </html:submit>
                        <html:link action="/listAccounts" styleClass="btn btn-secondary">
                            Cancel
                        </html:link>
                    </div>
                </html:form>
            </div>
        </div>
    </div>
</body>
</html>
