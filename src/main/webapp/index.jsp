<%@ page language="java" contentType="text/html; charset=euc-kr" isELIgnored = "false"%>
<%@ page import = "dak.web.framework.util.CookieBox" %>
<%@ page import = "dak.web.framework.util.ParamUtils" %>
<%@ page import = "dak.web.board.BoardDao" %>
<%@ page import = "java.util.List" %>
<%@ page import = "java.util.Map" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
String  NAME  = new CookieBox(request).getValue("NAME");

%>
<body>
<center>

<br><br><br><br> 

<c:if test = "${ cookie.ID.value != null && cookie.ID.value != '' }">
<a href="<%= request.getContextPath() %>/logout.jsp"><%= NAME %>: 로그아웃</a>
</c:if> 

<br><br><br><br>
<%

List boardList = BoardDao.getInstance().getBoardList();

for( int i=0; i<boardList.size(); i++ ){
%>
<a href="<%= request.getContextPath() %>/board/list.do?boardCode=<%= ((Map) boardList.get(i)).get("b_boardCode") %>"><%= ((Map) boardList.get(i)).get("b_boardName") %></a> |
<%
}
%>
<br><br><br><br>

<script>
function send(admin){
	if( admin == "1" ){
		document.form.id.value = "admin";
		document.form.name.value = "관리자";
	}else if( admin == "0" ){ 
		document.form.id.value = "ddakker";
		document.form.name.value = "이구용";
	}else if( admin == "2" ){ 
		document.form.id.value = "ddakker0";
		document.form.name.value = "꾸용";
	}
	
	document.form.submit();
}
</script>

<c:if test = "${ cookie.ID.value == null || cookie.ID.value == '' }">
<div>
	<form name="form" action="login.jsp" method="post">
	<input type="hidden" name="id" value="">
	<input type="hidden" name="name" value="">
	<input type="button" value="관리자 로그인" onclick="send('1')">
	<input type="button" value="일반인 1 로그인" onclick="send('0')">
	<input type="button" value="일반인 2 로그인" onclick="send('2')">
	</form>
</div>
</c:if>
<div>

	<a href="/board/manager.do">게시판 관리자</a>

</div>




</center>

</body>