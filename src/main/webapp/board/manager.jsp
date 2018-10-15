<%@ page language="java" contentType="text/html; charset=euc-kr" isELIgnored = "false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@include file="/board/include/common.jsp" %>
<html>
<head>
<title><bean:message key="page.title"/></title>

<LINK href="<html:rewrite page='/board/board.css' />" type="text/css" rel="stylesheet">
</head>

<body>

<table cellspacing="0" cellpadding="0" border="1">
<tr>
	<th style="padding:5,10,5,10">게시판 이름</th>
	<th style="padding:5,5,5,5">로그인 전용</th>
	<th style="padding:5,5,5,5">관리자 전용</th>
	<th style="padding:5,5,5,5">댓글 사용</th>
	<th style="padding:5,5,5,5">답글 사용</th>
	<th style="padding:5,5,5,5">수정</th>
</tr>
<c:forEach var="boardList" items="${boardList}">
<tr>
	<td align="center">
		<form method="POST" action="boardConfigEdit.do" style="margin:0">
		<input type="hidden" name="b_boardCode" value="${ boardList.b_boardCode }">
		<input type="text" name="b_boardName" style="width:100" value="${ boardList.b_boardName }">
	</td>
	<td align="center">
		<input type="checkbox" name="b_login" value="1" 
		<c:if test = "${ boardList.b_login == '1' }">
	 	checked
	 	</c:if>
		>
	</td>
	<td align="center">
		<input type="checkbox" name="b_admin" value="1"
		<c:if test = "${ boardList.b_admin == '1' }">
	 	checked
	 	</c:if>
		>
	</td>
	<td align="center">
		<input type="checkbox" name="b_comment" value="1"
		<c:if test = "${ boardList.b_comment == '1' }">
	 	checked
	 	</c:if>
		>
	</td>
	<td align="center">
		<input type="checkbox" name="b_reply" value="1"
		<c:if test = "${ boardList.b_reply == '1' }">
	 	checked
	 	</c:if>
		>
	</td>
	<td align="center">
		<input type="submit" value="수정">
		</form>
	</td>
</tr>
</c:forEach>
</table>

<br>
<table cellspacing="0" cellpadding="0" border="1">
<tr>
	<th style="padding:5,10,5,10">게시판 이름</th>
	<th style="padding:5,5,5,5">로그인 전용</th>
	<th style="padding:5,5,5,5">관리자 전용</th>
	<th style="padding:5,5,5,5">댓글 사용</th>
	<th style="padding:5,5,5,5">답글 사용</th>
	<th style="padding:5,5,5,5">추가</th>
</tr>
	<td>
		<form method="POST" action="boardConfigAdd.do">
		<input type="text" name="b_boardName" style="width:100">
	</td>
	<td align="center">
		<input type="checkbox" name="b_login">
	</td>
	<td align="center">
		<input type="checkbox" name="b_admin">
	</td>
	<td align="center">
		<input type="checkbox" name="b_comment">
	</td>
	<td align="center">
		<input type="checkbox" name="b_reply">
	</td>
	<td align="center">
		<input type="submit" value="추가">
		</form>
	</td>
</table>
</body>
</html>