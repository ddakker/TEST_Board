<%@ page language="java" contentType="text/html; charset=euc-kr" isELIgnored = "false"%>
<%@ page import = "java.util.Map" %>
<%@ page import = "dak.web.framework.util.CookieBox" %>
<%@ page import = "dak.web.framework.util.StringUtils" %>
<%@ page import = "dak.web.framework.util.ParamUtils" %>
<%@ page import = "dak.web.board.BoardDao" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@page import="dak.web.framework.util.StringUtils"%>
<script type="text/javascript" src="<html:rewrite page='/js/jquery-1.3.2.js' />"></script>
<script type="text/javascript" src="<html:rewrite page='/js/jqueryPlugin_cookie.js' />"></script>
<script type="text/javascript" src="<html:rewrite page='/js/jqueryPlugin_dateFormat.js' />"></script>
<script type="text/javascript" src="<html:rewrite page='/js/jqueryPlugin_log.js' />"></script>
<script type="text/javascript" src="<html:rewrite page='/js/jqueryPlugin_httpService.js' />"></script>
<script type="text/javascript" src="<html:rewrite page='/js/jqueryPlugin_xml2json.js' />"></script>

<script type="text/javascript" src="<html:rewrite page='/js/ui.core.js' />"></script>
<script type="text/javascript" src="<html:rewrite page='/js/ui.draggable.js' />"></script>


<script>
// 로딩바 띄우기
$.showLoding = function(){

}

// 로딩바 내리기
$.hideLoding = function(){

}
</script>

<%

response.setHeader("Pragma","ref-cache"); //HTTP 1.0
response.setDateHeader ("Expires", 0);


// 로그인 정보 불러오기
String  adminTemp = new CookieBox(request).getValue("ADMIN");
adminTemp = StringUtils.nvl(adminTemp, "0");

boolean ADMIN = adminTemp.equals("1")?true:false;
String  ID    = new CookieBox(request).getValue("ID");
String  NAME  = new CookieBox(request).getValue("NAME");

ID   = ID   == null ? "" : ID;
NAME = NAME == null ? "" : NAME;

// 해당 게시판 정보 불러오기
String boardCode = ParamUtils.getParameter(request, "boardCode", "0");
System.out.println("com boardCode: " + boardCode);
Map configMap = BoardDao.getInstance().getBoardConfig(boardCode);
	System.out.println("com configMap: " + configMap);
String  B_NAME    = (String) configMap.get("b_boardName");

boolean B_ADMIN   = ((Integer) configMap.get("b_admin"))   == 1 ? true : false; // cookie ADMIN 값과 동일
boolean B_LOGIN   = ((Integer) configMap.get("b_login"))   == 1 ? true : false;
boolean B_REPLY   = ((Integer) configMap.get("b_reply"))   == 1 ? true : false;
boolean B_COMMENT = ((Integer) configMap.get("b_comment")) == 1 ? true : false;

%>
<table cellspacing="0" cellpadding="0" width="660" height="30" background="<html:rewrite page='/images/bg_topManagerMenu.gif' />" style="padding:3,3,3,3">
<tr>
	<td align="center" width="70">
		<a href="<%= request.getContextPath() %>/"><img id="btSearch" src="<html:rewrite page='/images/bt_board_home.gif' />" border="0" align="absmiddle"></a>
	</td>
	<td valign="middle">
		<c:if test = "${ cookie.ID.value == null || cookie.ID.value == '' }">
		<a href="<%= request.getContextPath() %>/"><img src="<html:rewrite page='/images/bt_board_logIn.gif' />" border="0" align="absmiddle"></a>
		</c:if>
		<c:if test = "${ cookie.ID.value != null && cookie.ID.value != '' }">
		<a href="<%= request.getContextPath() %>/logout.jsp">
			<%= NAME %>
			<img src="<html:rewrite page='/images/bt_board_logout.gif' />" border="0" align="absmiddle">
		</a>
		</c:if>
	</td>
	<td align="right">
		<c:if test = "${ cookie.ADMIN.value != null && cookie.ADMIN.value == '1' }">
			<a href="manager.do"><img src="<html:rewrite page='/images/bt_boardManager.gif' />" border="0" align="absmiddle"></a>
		</c:if>
	</td>
</tr>
</table>
<br>

<div id="logElement" style="position:absolute; top:10; left:690px; width:500; height:500; border:1px solid #CCCCCC;"></div>