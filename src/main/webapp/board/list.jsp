<%@ page language="java" contentType="text/html; charset=euc-kr" isELIgnored = "false"%>
<%@ page import = "dak.web.framework.util.ParamUtils" %>
<%@ page import = "dak.web.framework.util.CookieBox" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@include file="/board/include/common.jsp" %>

<html>
<head>
<title><bean:message key="page.title"/></title>

<script type="text/javascript" src="<html:rewrite page='/board/board.js' />"></script>

<LINK href="<html:rewrite page='/board/board.css' />" type="text/css" rel="stylesheet">
</head>

<body>

<table width="660" cellspacing="0" cellpadding="0">
<tr>
	<td align="right">
		<select id="selectSearchKind">
			<option value="1" <c:if test = "${ param.searchKind == 1 }">selected</c:if>>제목</option>
			<option value="2" <c:if test = "${ param.searchKind == 2 }">selected</c:if>>작성자</option>
			<option value="3" <c:if test = "${ param.searchKind == 3 }">selected</c:if>>내용</option>
		</select>
		<input type="text" id="txtSearchStr" value="${ param.searchStr }" style="width:100px;height:19px" />
		<img id="btSearch" src="<html:rewrite page='/images/bt_board_search2.gif' />" border="0" align="absmiddle" onclick="$.onSearchClicked('${param.boardCode}')" style="cursor:pointer">
		<c:if test = "${ param.searchKind != null }">
		<img id="btSearch" src="<html:rewrite page='/images/bt_board_cancel2.gif' />" border="0" align="absmiddle" onclick="$.onSearchCancelClicked('${param.boardCode}')" style="cursor:pointer">
		</c:if>
	</td>
</tr>
</table>

<table id="tbList" width="660" cellspacing="0" cellpadding="0">
<tr><td colspan="5" height="2" bgcolor="#61ACE6"></td></tr>
<tr height="30">
	<th width="40" background="<html:rewrite page='/images/bg_board_hd.gif' />">번호</th>	
	<th width="390" background="<html:rewrite page='/images/bg_board_hd.gif' />">제목</th>
	<th width="90" background="<html:rewrite page='/images/bg_board_hd.gif' />">작성자</th>
	<th width="90" background="<html:rewrite page='/images/bg_board_hd.gif' />">등록일</th>
	<th width="50" background="<html:rewrite page='/images/bg_board_hd.gif' />">조회</th>
</tr>
<tr><td colspan="5" height="1" bgcolor="#DDDDDD"></td></tr>
<c:forEach var="list" items="${list}">
<tr>
	<td align="center" height="29">${ list.num }</td>
	<td align="left" onclick="$.goToContent('${ list.b_seq }', '${ list.b_id }', '${ list.b_secret }', '${ list.b_delete }')" style="padding-left:10px;cursor:pointer">
		<c:if test = "${ list.b_level != 0 }">
		<img src="" height="0" width="${ list.b_level*10 }" border="0">
		<img id="btSearch" src="<html:rewrite page='/images/icon_board_reply.gif' />" border="0" align="absmiddle">
		</c:if>
		<c:if test = "${ list.b_delete == 1 }">
		<strike style="color:#CCCCCC">${ list.b_subject }</strike>
		</c:if>		
		<c:if test = "${ list.b_delete == 0 }">			
			${ list.b_subject }
			<c:if test = "${ list.b_secret == 1 }">
			&nbsp;<img id="btSearch" src="<html:rewrite page='/images/icon_secret.gif' />" border="0" align="absmiddle">
			</c:if>
		</c:if>
		<% if( B_COMMENT ){ %>
		<c:if test = "${ list.comment_cnt > 0 }">
			<font color="#E45D1B" style="font-size:8pt">(${ list.comment_cnt })</font>
		</c:if>
		<% } %>
	</td>
	<td align="center">
		<c:if test = "${ list.b_delete == 0 }">
		${ list.b_name }
		</c:if>		
	</td>
	<td align="center">
		<c:if test = "${ list.b_delete == 0 }">
		${ list.b_regdate2 }
		</c:if>	
	</td>
	<td align="center">
		<c:if test = "${ list.b_delete == 0 }">
		${ list.b_hit }
		</c:if>	
	</td>
</tr>
<tr><td colspan="5" height="1" bgcolor="#DDDDDD"></td></tr>
</c:forEach>
<tr><td colspan="5" height="10"></td></tr>
<tr>
	<td colspan="5">
		<table width="100%" cellspacing="0" cellpadding="0">
		<tr>
			<td align="left" width="100">&nbsp;</td>			
			<td align="center" valign="middle">
				<%
				
				int blockPage = 5;
				
				int nowPage = ParamUtils.getParameterInt(request, "nowPage", "1");
								
				int totalPage = Integer.parseInt(request.getAttribute("totalPage")+"");
				
				int intTemp = -1;
				int intLoop = -1;
				
				if( nowPage != 1 ){
				%>				
					<a href="list.do?boardCode=${ param.boardCode }&nowPage=1"><img src="<html:rewrite page='/images/bt_board_pageFirst.gif' />" border="0" align="absmiddle" valign="middle"></a>
					<a href="list.do?boardCode=${ param.boardCode }&nowPage=<%= nowPage - 1 %>"><img src="<html:rewrite page='/images/bt_board_pagePrev.gif' />" border="0" align="absmiddle" valign="middle"></a>
				<%          
				}else{
				%>
					<img src="<html:rewrite page='/images/bt_board_pageFirst.gif' />" border="0" align="absmiddle" valign="middle">
					<img src="<html:rewrite page='/images/bt_board_pagePrev.gif' />" border="0" align="absmiddle" valign="middle">
				<%
				}
					
				intTemp = ((nowPage - 1) / blockPage) * blockPage + 1;

				intLoop = 1;

				while( !( (intLoop > blockPage) || (intTemp > totalPage) ) ){
					if( intTemp == nowPage ){
						out.print("<font color=red style=font-size:8pt><b>" + intTemp + "</b></font>&nbsp;" );
					}else{
					%>
						<a href="list.do?boardCode=${ param.boardCode }&nowPage=<%= intTemp %>" style="font-size:8pt"><%= intTemp %></a>&nbsp;
					<%
					}
					intTemp = intTemp + 1;
					intLoop = intLoop + 1;
				}

				if( nowPage != totalPage ){
				%>
					<a href=list.do?boardCode=${ param.boardCode }&nowPage=<%= nowPage + 1 %>><img src="<html:rewrite page='/images/bt_board_pageNext.gif' />" border="0" align="absmiddle" valign="middle"></a>
					<a href=list.do?boardCode=${ param.boardCode }&nowPage=${ totalPage }><img src="<html:rewrite page='/images/bt_board_pageLast.gif' />" border="0" align="absmiddle"></a>
				<% }else{ %>
					<img src="<html:rewrite page='/images/bt_board_pageNext.gif' />" border="0" align="absmiddle" valign="middle">
					<img src="<html:rewrite page='/images/bt_board_pageLast.gif' />" border="0" align="absmiddle">
				<% } %>
			</td>
			<td align="right" width="100">	
				<% if( ADMIN || (!B_ADMIN && !B_LOGIN && ID.equals("")) || (!B_ADMIN && !ID.equals("")) ){ %>
					<a href="write_form.do?boardCode=${ param.boardCode }"><img src="<html:rewrite page='/images/bt_board_write.gif' />" border="0" align="absmiddle"></a>
				<% } %>		
			</td>
		</tr>
		</table>
	</td>
</tr>
</table>


<!-- ****************** 내용 보기 ****************** -->


<table id="tbContent" width="660" cellspacing="0" cellpadding="0">
<tr>
	<td colspan="3" height="2" bgcolor="#61ACE6">
	</td>
</tr>
<tr><td colspan="3" height="1" bgcolor="#DDDDDD"></td></tr>
<tr>
	<th align="right" width="90" style="padding-right:10px"><font color="#5081BC">제목</font></th>
	<td width="1"><img src="<html:rewrite page='/images/bg_board_hdLine.gif' />" border="0"></td>
	<td style="padding-left:10px" width="569"><div id="contentSubject"></div></td>
</tr>
<tr><td colspan="3" height="1" bgcolor="#DDDDDD"></td></tr>
<tr>
	<th align="right" style="padding-right:10px"><font color="#5081BC">작성자</font></th>
	<td width="1"><img src="<html:rewrite page='/images/bg_board_hdLine.gif' />" border="0"></td>
	<td style="padding-left:10px">
		<table width="100%" cellspacing="0" cellpadding="0">
		<tr>
			<td width="100"><div id="contentName"></div></td>
			<th align="right" width="90" style="padding-right:10px"><font color="#5081BC">등록일</font></th>
			<td width="1"><img src="<html:rewrite page='/images/bg_board_hdLine.gif' />" border="0"></td>
			<td style="padding-left:10px"><div id="contentRegdate"></div></td>
			<th align="right" width="90" style="padding-right:10px"><font color="#5081BC">조회수</font></th>
			<td width="1"><img src="<html:rewrite page='/images/bg_board_hdLine.gif' />" border="0"></td>
			<td style="padding-left:10px"><div id="contentHit"></div></td>
		</tr>
		</table>
	</td>
</tr>
<tr><td colspan="3" height="1" bgcolor="#DDDDDD"></td></tr>
<tr><td colspan="3" height="3"></td></tr>
<tr>
	<th align="right" width="90" style="padding-right:10px"><font color="#5081BC">내용</font></th>
	<td width="1"><img src="<html:rewrite page='/images/bg_board_hdLine.gif' />" border="0"></td>
	<td style="padding-left:10px;padding-top:10px" height="300" valign="top">
		<div id="contentContent"></div>
	</td>
</tr>
<tr><td colspan="3" height="3"></td></tr>
<tr><td colspan="3" height="1" bgcolor="#DDDDDD"></td></tr>
<tr><td colspan="3" height="3"></td></tr>
<tr>
	<th align="right" width="90" style="padding-right:10px"><font color="#5081BC">첨부파일</font></th>
	<td width="1"><img src="<html:rewrite page='/images/bg_board_hdLine.gif' />" border="0"></td>
	<td style="padding-left:10px">
		<div id="contentFileList"></div>
	</td>
</tr>
<tr><td colspan="3" height="3"></td></tr>
<tr><td colspan="3" height="1" bgcolor="#61ACE6"></td></tr>
<tr><td colspan="3" height="10"></td></tr>
<tr>
	<td colspan="3" align="right">
		<% if( (B_REPLY && ADMIN) || (B_REPLY && !B_LOGIN && ID.equals("")) || (B_REPLY && !B_ADMIN && !ID.equals("")) ){ %>
			<img src="<html:rewrite page='/images/bt_board_reply.gif' />" border="0" onclick="$.replyWrite('${ param.boardCode }')" style="cursor:pointer">
		<% } %>
		
		<% if( ADMIN || (!B_ADMIN && !ID.equals("")) ){			// 바로 삭제/수정 %>
			<img id="btEdit" src="<html:rewrite page='/images/bt_board_edit.gif' />" border="0" onclick="$.go2boardEditForm()" style="cursor:pointer;">
			<img id="btDelete" src="<html:rewrite page='/images/bt_board_delete.gif' />" border="0" onclick="$.go2boardDelete()" style="cursor:pointer;">
		<% }else if( !B_ADMIN && !B_LOGIN && ID.equals("") ){	// 비밀번호를 입력 받을 사용자 %>
			<img id="btEdit" src="<html:rewrite page='/images/bt_board_edit.gif' />" border="0" onclick="$.inputPwdView('boardEdit')" style="cursor:pointer">
			<img id="btDelete" src="<html:rewrite page='/images/bt_board_delete.gif' />" border="0" onclick="$.inputPwdView('boardDelete')" style="cursor:pointer">
		<% } %>
		
		<img src="<html:rewrite page='/images/bt_board_list.gif' />" border="0" onclick="$.goToContent('list')" style="cursor:pointer">
	</td>
</tr>
<tr><td colspan="3" height="10"></td></tr>
<tr>
	<td colspan="3">
		<table width="100%" style="padding:10,15,10,15" cellspacing="0" cellpadding="0" background="<html:rewrite page='/images/bg_board_comment.gif' />">
		<tr><td bgcolor="#E7E7E7" height="3"></td></tr>
		<tr>
			<td>
				<% if( B_COMMENT ){ %>
					<img src="<html:rewrite page='/images/icon_board_comment.gif' />" border="0" />
					<span id="divCommentCnt" style="cursor:pointer" onclick="$.callMentView('<%= B_LOGIN %>')"></span>
				<% } %>
			</td>
		</tr>
		<tr>
			<td colspan="0" id="divMent" style="display:none">
				<div id="divMentUserInfo">
				<b>이름</b> <input type='text' style='width:80px;height:19px' id='c_name' maxlength='12' value="<%= NAME %>"> 
				<b>비밀번호</b> <input type='password' style='width:50px;height:19px' id='c_pwd' maxlength='20'>
				<br>
				</div>
				<input type="text" style="width:570px;height:35px;" id="c_ment" maxlength="500">
				<input type="image" id="c_send" align="absmiddle" src="<html:rewrite page='/images/bt_board_write_comment.gif' />" onClick="$.mentWrite()">
				<br>
				<div id='divMentRow'></div>
			</td>
		</tr>
		<tr><td bgcolor="#E7E7E7" height="3"></td></tr>
		</table>
	</td>
</tr>
</table>



<div id="divInput_pwd" style="position:absolute; left:0; top:0; width:240px; display:none; border:0;">
<table align="center" cellspacing="0" cellpadding="1" bgcolor="#CCCCCC">
<tr>
	<td>
		<table align="center" cellspacing="0" cellpadding="0">			
		<tr>
			<td colspan="5" align="center" height="30" bgcolor="#FFFFFF" id="divInput_pwd_Drag" class="handle"><b>비밀번호를 입력해 주십시요.</b></td>
		</tr>
		<tr>
			<td colspan="5" class="rowLine"></td>
		</tr>
		<tr style="color:#FFFFFF; background-color:#61ACE6;font-weight:bold;" height="30">
			<td width="20"></td>
			<td align="center"><font color="white"><b>비밀번호</b></font></td>
			<td width="10"></td>
			<td align="left">
				<input type="hidden" id="boardCode" value="${ param.boardCode }">
				<input type="hidden" id="nowPage" value="${ param.nowPage }">
				<input type="hidden" id="input_where"><!-- 게시판글, 댓글, 파일 -->
				<input type="hidden" id="input_seq">
				<input type="hidden" id="input_id">
				<input type="password" id="input_pwd" maxlength="20" size="18" onkeypress="$.inputPwdEnter()">
			</td>
			<td width="20"></td>
		</tr>
		<tr><td colspan="5" height="5" bgcolor="white"></td></tr>
		<tr>
			<td colspan="5" align="right" bgcolor="#FFFFFF">
			<img src="<html:rewrite page='/images/bt_board_send_s.gif' />" border="0" onClick="$.inputPwdSend()" style="cursor:pointer"><img src="<html:rewrite page='/images/bt_board_cancel_s.gif' />" border="0" onclick="$.inputPwdView();" style="cursor:pointer">
			</td>
		</tr>
		</table>
	</td>
</tr>
		
</table>
</div>
</body>
</html>