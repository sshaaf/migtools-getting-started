<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bank Account System - Account Details</title>
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
            max-width: 900px;
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
        .btn-success {
            background: linear-gradient(135deg, #4ade80 0%, #22c55e 100%);
            color: #000;
        }
        .btn-warning {
            background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%);
            color: #000;
        }
        .card {
            background: rgba(255, 255, 255, 0.03);
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 16px;
            overflow: hidden;
            margin-bottom: 30px;
        }
        .card-header {
            background: rgba(0, 0, 0, 0.3);
            padding: 25px 30px;
            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        }
        .card-header h2 {
            font-size: 14px;
            text-transform: uppercase;
            letter-spacing: 2px;
            color: #888;
            margin-bottom: 10px;
        }
        .account-number-display {
            font-family: 'Courier New', monospace;
            font-size: 28px;
            color: #00d4ff;
            letter-spacing: 3px;
        }
        .card-body {
            padding: 30px;
        }
        .info-grid {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 25px;
        }
        .info-item {
            padding: 20px;
            background: rgba(0, 0, 0, 0.2);
            border-radius: 12px;
        }
        .info-item label {
            display: block;
            font-size: 11px;
            text-transform: uppercase;
            letter-spacing: 1px;
            color: #666;
            margin-bottom: 8px;
        }
        .info-item .value {
            font-size: 18px;
            color: #fff;
        }
        .balance-display {
            grid-column: span 2;
            text-align: center;
            padding: 40px;
            background: linear-gradient(135deg, rgba(0, 212, 255, 0.1) 0%, rgba(0, 153, 204, 0.1) 100%);
            border: 1px solid rgba(0, 212, 255, 0.2);
        }
        .balance-display label {
            font-size: 12px;
            color: #888;
            margin-bottom: 10px;
        }
        .balance-display .value {
            font-size: 48px;
            font-weight: 700;
            color: #4ade80;
        }
        .badge {
            display: inline-block;
            padding: 6px 16px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
            text-transform: uppercase;
        }
        .badge-savings {
            background: rgba(74, 222, 128, 0.2);
            color: #4ade80;
        }
        .badge-checking {
            background: rgba(96, 165, 250, 0.2);
            color: #60a5fa;
        }
        .badge-business {
            background: rgba(251, 191, 36, 0.2);
            color: #fbbf24;
        }
        .transaction-section {
            background: rgba(255, 255, 255, 0.03);
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 16px;
            padding: 30px;
        }
        .transaction-section h3 {
            font-size: 16px;
            margin-bottom: 20px;
            color: #fff;
        }
        .transaction-form {
            display: flex;
            gap: 15px;
            align-items: flex-end;
        }
        .form-group {
            flex: 1;
        }
        .form-group label {
            display: block;
            font-size: 11px;
            text-transform: uppercase;
            letter-spacing: 1px;
            color: #888;
            margin-bottom: 8px;
        }
        .form-group input {
            width: 100%;
            padding: 12px 16px;
            background: rgba(0, 0, 0, 0.3);
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 8px;
            color: #fff;
            font-size: 16px;
        }
        .form-group input:focus {
            outline: none;
            border-color: #00d4ff;
        }
        .action-buttons {
            display: flex;
            gap: 10px;
            margin-top: 30px;
        }
        .message {
            padding: 15px 20px;
            border-radius: 8px;
            margin-bottom: 20px;
        }
        .message-success {
            background: rgba(74, 222, 128, 0.1);
            border: 1px solid rgba(74, 222, 128, 0.3);
            color: #4ade80;
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
        <!-- Success Messages -->
        <html:messages id="message" message="true">
            <div class="message message-success">
                <bean:write name="message"/>
            </div>
        </html:messages>

        <logic:notEmpty name="account">
            <!-- Account Details Card -->
            <div class="card">
                <div class="card-header">
                    <h2>Account Details</h2>
                    <div class="account-number-display">
                        <bean:write name="account" property="accountNumber"/>
                    </div>
                </div>
                <div class="card-body">
                    <div class="info-grid">
                        <div class="info-item balance-display">
                            <label>Current Balance</label>
                            <div class="value">
                                $<fmt:formatNumber value="${account.balance}" pattern="#,##0.00"/>
                            </div>
                        </div>
                        
                        <div class="info-item">
                            <label>Account Holder</label>
                            <div class="value"><bean:write name="account" property="holderName"/></div>
                        </div>
                        
                        <div class="info-item">
                            <label>Account Type</label>
                            <div class="value">
                                <c:choose>
                                    <c:when test="${account.accountType == 'SAVINGS'}">
                                        <span class="badge badge-savings">Savings</span>
                                    </c:when>
                                    <c:when test="${account.accountType == 'CHECKING'}">
                                        <span class="badge badge-checking">Checking</span>
                                    </c:when>
                                    <c:when test="${account.accountType == 'BUSINESS'}">
                                        <span class="badge badge-business">Business</span>
                                    </c:when>
                                </c:choose>
                            </div>
                        </div>
                        
                        <div class="info-item">
                            <label>Email Address</label>
                            <div class="value"><bean:write name="account" property="email"/></div>
                        </div>
                        
                        <div class="info-item">
                            <label>Phone Number</label>
                            <div class="value"><bean:write name="account" property="phone"/></div>
                        </div>
                        
                        <div class="info-item">
                            <label>Created Date</label>
                            <div class="value">
                                <fmt:formatDate value="${account.createdDate}" pattern="MMM dd, yyyy HH:mm"/>
                            </div>
                        </div>
                        
                        <div class="info-item">
                            <label>Status</label>
                            <div class="value">
                                <c:choose>
                                    <c:when test="${account.active}">
                                        <span style="color: #4ade80;">● Active</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span style="color: #ff4d4d;">● Inactive</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Quick Transaction Section -->
            <div class="transaction-section">
                <h3>Quick Transaction</h3>
                
                <html:form action="/transaction" method="post">
                    <html:hidden property="accountId" value="${account.id}"/>
                    <div class="transaction-form">
                        <div class="form-group">
                            <label>Amount ($)</label>
                            <html:text property="amount" styleId="amount"/>
                        </div>
                        <button type="submit" name="transactionType" value="DEPOSIT" class="btn btn-success">
                            Deposit
                        </button>
                        <button type="submit" name="transactionType" value="WITHDRAW" class="btn btn-warning">
                            Withdraw
                        </button>
                    </div>
                </html:form>
            </div>

            <!-- Action Buttons -->
            <div class="action-buttons">
                <html:link action="/editAccount" paramId="id" paramName="account" paramProperty="id" styleClass="btn btn-primary">
                    Edit Account
                </html:link>
                <html:link action="/deleteAccount" paramId="id" paramName="account" paramProperty="id" styleClass="btn btn-secondary" onclick="return confirm('Are you sure you want to delete this account?');">
                    Delete Account
                </html:link>
            </div>
        </logic:notEmpty>
    </div>
</body>
</html>

