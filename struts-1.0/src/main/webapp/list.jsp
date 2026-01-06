<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bank Account System - Account List</title>
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
        }
        .header h1 {
            color: #00d4ff;
            font-size: 28px;
            font-weight: 300;
            letter-spacing: 2px;
        }
        .header .subtitle {
            color: #888;
            font-size: 12px;
            margin-top: 5px;
            text-transform: uppercase;
            letter-spacing: 3px;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 30px;
        }
        .stats-row {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 20px;
            margin-bottom: 30px;
        }
        .stat-card {
            background: rgba(255, 255, 255, 0.05);
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 12px;
            padding: 25px;
            text-align: center;
            transition: transform 0.3s, box-shadow 0.3s;
        }
        .stat-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 40px rgba(0, 212, 255, 0.2);
        }
        .stat-card .value {
            font-size: 32px;
            font-weight: 700;
            color: #00d4ff;
            margin-bottom: 5px;
        }
        .stat-card .label {
            font-size: 12px;
            color: #888;
            text-transform: uppercase;
            letter-spacing: 1px;
        }
        .toolbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }
        .toolbar h2 {
            font-size: 20px;
            font-weight: 400;
            color: #fff;
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
        .btn-primary {
            background: linear-gradient(135deg, #00d4ff 0%, #0099cc 100%);
            color: #000;
        }
        .btn-primary:hover {
            box-shadow: 0 5px 20px rgba(0, 212, 255, 0.4);
            transform: translateY(-2px);
        }
        .btn-secondary {
            background: rgba(255, 255, 255, 0.1);
            color: #fff;
            border: 1px solid rgba(255, 255, 255, 0.2);
        }
        .btn-secondary:hover {
            background: rgba(255, 255, 255, 0.2);
        }
        .btn-danger {
            background: rgba(255, 77, 77, 0.2);
            color: #ff4d4d;
            border: 1px solid rgba(255, 77, 77, 0.3);
        }
        .btn-danger:hover {
            background: rgba(255, 77, 77, 0.3);
        }
        .btn-sm {
            padding: 8px 16px;
            font-size: 12px;
        }
        .table-container {
            background: rgba(255, 255, 255, 0.03);
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 12px;
            overflow: hidden;
        }
        table {
            width: 100%;
            border-collapse: collapse;
        }
        th {
            background: rgba(0, 0, 0, 0.3);
            padding: 15px 20px;
            text-align: left;
            font-size: 11px;
            text-transform: uppercase;
            letter-spacing: 1px;
            color: #888;
            font-weight: 600;
        }
        td {
            padding: 18px 20px;
            border-bottom: 1px solid rgba(255, 255, 255, 0.05);
        }
        tr:hover td {
            background: rgba(255, 255, 255, 0.02);
        }
        .account-number {
            font-family: 'Courier New', monospace;
            color: #00d4ff;
            font-weight: 600;
        }
        .balance {
            font-weight: 600;
            color: #4ade80;
        }
        .badge {
            display: inline-block;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 11px;
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
        .actions {
            display: flex;
            gap: 8px;
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
        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #666;
        }
        .empty-state p {
            margin-bottom: 20px;
        }
        .footer {
            text-align: center;
            padding: 30px;
            color: #555;
            font-size: 12px;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>LEGACY BANK</h1>
        <div class="subtitle">Struts 1.x Account Management System</div>
    </div>
    
    <div class="container">
        <!-- Success Messages -->
        <html:messages id="message" message="true">
            <div class="message message-success">
                <bean:write name="message"/>
            </div>
        </html:messages>
        
        <!-- Statistics Cards -->
        <div class="stats-row">
            <div class="stat-card">
                <div class="value">${fn:length(accounts)}</div>
                <div class="label">Total Accounts</div>
            </div>
            <div class="stat-card">
                <div class="value">$<fmt:formatNumber value="${totalBalance}" pattern="#,##0.00"/></div>
                <div class="label">Total Balance</div>
            </div>
            <div class="stat-card">
                <div class="value">${savingsCount}</div>
                <div class="label">Savings</div>
            </div>
            <div class="stat-card">
                <div class="value">${checkingCount + businessCount}</div>
                <div class="label">Checking & Business</div>
            </div>
        </div>
        
        <!-- Toolbar -->
        <div class="toolbar">
            <h2>All Accounts</h2>
            <html:link action="/editAccount" styleClass="btn btn-primary">
                + New Account
            </html:link>
        </div>
        
        <!-- Accounts Table -->
        <div class="table-container">
            <logic:notEmpty name="accounts">
                <table>
                    <thead>
                        <tr>
                            <th>Account Number</th>
                            <th>Account Holder</th>
                            <th>Type</th>
                            <th>Balance</th>
                            <th>Email</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <logic:iterate id="account" name="accounts">
                            <tr>
                                <td class="account-number">
                                    <bean:write name="account" property="accountNumber"/>
                                </td>
                                <td><bean:write name="account" property="holderName"/></td>
                                <td>
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
                                </td>
                                <td class="balance">
                                    $<fmt:formatNumber value="${account.balance}" pattern="#,##0.00"/>
                                </td>
                                <td><bean:write name="account" property="email"/></td>
                                <td class="actions">
                                    <html:link action="/viewAccount" paramId="id" paramName="account" paramProperty="id" styleClass="btn btn-secondary btn-sm">
                                        View
                                    </html:link>
                                    <html:link action="/editAccount" paramId="id" paramName="account" paramProperty="id" styleClass="btn btn-secondary btn-sm">
                                        Edit
                                    </html:link>
                                    <html:link action="/deleteAccount" paramId="id" paramName="account" paramProperty="id" styleClass="btn btn-danger btn-sm" onclick="return confirm('Are you sure you want to delete this account?');">
                                        Delete
                                    </html:link>
                                </td>
                            </tr>
                        </logic:iterate>
                    </tbody>
                </table>
            </logic:notEmpty>
            
            <logic:empty name="accounts">
                <div class="empty-state">
                    <p>No accounts found in the system.</p>
                    <html:link action="/editAccount" styleClass="btn btn-primary">
                        Create First Account
                    </html:link>
                </div>
            </logic:empty>
        </div>
    </div>
    
    <div class="footer">
        Legacy Bank &copy; 2024 - Struts 1.x Demo Application
    </div>
</body>
</html>

