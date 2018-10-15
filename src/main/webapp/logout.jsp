<%@ page language="java" contentType="text/html; charset=euc-kr" isELIgnored = "false"%>
<%@ page import = "dak.web.framework.util.CookieBox" %>

<%


response.addCookie( CookieBox.createCookie("ID", "") );
response.addCookie( CookieBox.createCookie("ADMIN", "") );
response.addCookie( CookieBox.createCookie("NAME", "") );

response.sendRedirect(request.getContextPath() + "/");

%>