<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- 
    Index page - redirects to the main application
    This is typical of Struts 1.x applications
--%>
<%
    response.sendRedirect(request.getContextPath() + "/welcome.do");
%>

