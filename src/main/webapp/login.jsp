<%@ page language="java" contentType="text/html; charset=euc-kr" isELIgnored = "false"%>
<%@ page import = "dak.web.framework.util.CookieBox" %>

<script type="text/javascript" src="<html:rewrite page='/js/jquery-1.3.2.js' />"></script>
<script type="text/javascript" src="<html:rewrite page='/js/jqueryPlugin_cookie.js' />"></script>



<%

String id  = request.getParameter("id");
String name  = new String(request.getParameter("name").getBytes("8859_1"), "euc-kr");

response.addCookie( CookieBox.createCookie("ID", id) );
if( id.equals("admin") ){
	response.addCookie( CookieBox.createCookie("ADMIN", "1") );
	response.addCookie( CookieBox.createCookie("NAME", name) );
}else{
	response.addCookie( CookieBox.createCookie("ADMIN", "0") );
	response.addCookie( CookieBox.createCookie("NAME", name) );
}

response.sendRedirect(request.getContextPath() + "/");

%>