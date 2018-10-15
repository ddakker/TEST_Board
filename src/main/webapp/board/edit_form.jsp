<%@ page language="java" contentType="text/html; charset=euc-kr" isELIgnored = "false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>

<%@include file="/board/include/common.jsp" %>

<html>
<head>
<title><bean:message key="page.title"/></title>

<script>
$(document).ready(function(){
	// 로그인이 되 있다면.
	if( $.cookie("ID") != null && $.cookie("ID") != "" ){
		//document.form.pwd.value = "***********";
		//document.form.pwd.disabled = true;
		//document.form.name.disabled = true; 
		$("#name").attr("disabled", "disabled");
		$("#pwd").attr("disabled", "disabled");
	}
	$("#subject").trigger("focus")
	
});


$.fileUploadForm = function() {
	var windowWidth = 300;
	var bodyWidth   = $("body").width();	
	var windowX      = (bodyWidth/2) - (windowWidth/2);
	
	window.open("fileUpload_form.do", "fileUpload", "top=100, left=" + windowX + ", width=" + windowWidth + ", height=200");
}
//$.uploadFileSetting = function(physicalFileName, originalFileName) {
function uploadFileSetting(physicalFileName, originalFileName) {
	var fileExt = false;
	$("#fileList option").each(function () {
    	var name = $(this).text();
    	if( name == originalFileName ){    		
    		fileExt = true;    		
    	}
    });
	if( fileExt == true ){
		alert("동일한 파일명입니다.");		
	}else{
		$("#fileList").append("<option value='" + physicalFileName + "' value2='now'>" + originalFileName + "</option>");
	}
}

$.fileDelete = function() {
		
	var selectedName  = "";
	
	var selected      = false;
	$("#fileList option:selected").each(function () {
		var value = $(this).val();		
		var value2 = $(this).attr("value2");		
		if( value != "" ){
			selected = true;
			if( value2 != "db" ){
    			selectedName  += "," + value;
    		}else{
    			$("#deleteFSeqArr").val( $("#deleteFSeqArr").val() + "," + value ); 
    		}
    	}
    });

    selectedName  = selectedName.substring(1);
    
    if( selected == false ){
    	alert("파일을 한개 이상 선택 하십시요.");
    	return;
    }
    
    if( !confirm("삭제 하시겠습니까?") )
		return;
    
    var params  = {};
	params.fileNameArr = selectedName;
	var url			   = "fileDelete.do";
	$.httpService(url, $.param(params), 
				  function(data){
				  	var resultCnt = eval(data.totalItemCount[0].Text);
					if( resultCnt < 0 ){
						alert("파일 삭제 중 오류가 발생하였습니다.");
					}else{
						$("#fileList option:selected").each(function () {
							var value = $(this).val();		
							if( value != "" ){
					    		$(this).remove();
					    	}
					    });
						alert("삭제 되었습니다.");
					}
				  },
				  $.httpService.JSON);
}
</script>


<LINK href='<html:rewrite page="/board/board.css"/>' type="text/css" rel="stylesheet">
</head>

<body>


<table width="660" cellspacing="0" cellpadding="0">
<tr>
	<td colspan="3" height="2" bgcolor="#61ACE6">
		<form name="form" method="post" action="edit.do" onSubmit="return _onSubmit(this)">
		<input type="hidden" name="physicalFileNameArr" value="">
		<input type="hidden" name="originalFileNameArr" value="">
		<input type="hidden" name="deleteFSeqArr" id="deleteFSeqArr"   value="">
		<input type="hidden" name="id" value="${ cookie.ID.value }">
		<input type="hidden" name="boardCode" value="${ param.boardCode }">
		<input type="hidden" name="nowPage" value="${ param.nowPage }">
		<input type="hidden" name="b_seq" value="${ param.seq }">
	</td>
</tr>
<tr>
	<th align="right" width="90" style="padding-right:10px">작성자</th>
	<td width="1"><img src="<html:rewrite page='/images/bg_board_hdLine.gif' />" border="0"></td>
	<td style="padding-left:10px"><input type="text" name="name" id="name" maxLength="20" style="width:150px" value="${ content.b_name }"></td>
</tr>
<tr><td colspan="3" height="1" bgcolor="#DDDDDD"></td></tr>
<tr>
	<th align="right" width="90" style="padding-right:10px">제목</th>
	<td width="1"><img src="<html:rewrite page='/images/bg_board_hdLine.gif' />" border="0"></td>
	<td style="padding-left:10px">
		<input type="text" name="subject" id="subject" maxLength="200" style="width:380px" value="${ content.b_subject }">
		&nbsp;&nbsp;<label for="secret" title="자신과 관리자 이외 열람 할수 없습니다.">비밀글</label>
		 <input type="checkbox" name="secret" id="secret"
		 	<c:if test = "${ content.b_secret == '1' }">
		 	value="1" checked
		 	</c:if>
		 >
	</td>
</tr>
<tr><td colspan="3" height="1" bgcolor="#DDDDDD"></td></tr>
<tr><td colspan="3" height="3"></td></tr>
<tr>
	<th align="right" width="90" style="padding-right:10px">내용</th>
	<td width="1"><img src="<html:rewrite page='/images/bg_board_hdLine.gif' />" border="0"></td>
	<td style="padding-left:10px">
		<script type="text/javascript" src="<html:rewrite page='/board/SmartEditorBasic/js/HuskyEZCreator.js' />"></script>	
		<textarea name="content" id="content" style="width:100%; height:250px; display:none;">${ content.b_content }</textarea>
		<script>
		var oEditors = [];
		nhn.husky.EZCreator.createInIFrame({
			oAppRef: oEditors,
			elPlaceHolder: "content",
			sSkinURI: "SmartEditorBasic/SEditorSkin.html",
			fCreator: "createSEditorInIFrame"
			
			
		});
		
		function _onSubmit(elClicked){
			// 에디터의 내용을 에디터 생성시에 사용했던 textarea에 넣어 줍니다.
			oEditors.getById["content"].exec("UPDATE_IR_FIELD", []);
			try{
			
				var name 	= elClicked.name.value;
				var subject = elClicked.subject.value;
				var content = elClicked.content.value;
				var pwd 	= elClicked.pwd.value;
				//alert(name);
				//return false;
				if( $.trim(name) == "" ){
					alert("이름을 입력하십시요.");
					elClicked.name.focus();
					return false;
				}
				
				if( $.trim(subject) == "" ){
					alert("제목을 입력하십시요.");
					elClicked.subject.focus();
					return false;
				}
				
				if( $.trim(content) == "" ){
					alert("내용을 입력하십시요.");
					elClicked.content.focus();
					return false;
				}
				
				if( $.trim(pwd) == "" ){
					alert("비밀번호를 입력하십시요.");
					elClicked.pwd.focus();
					return false;
				}
							
				if( $.trim(pwd).length < 4 ){
					alert("비밀번호는 4자 이상입니다.");
					elClicked.pwd.focus();
					return false;
				}
				
				$("#name").attr("disabled", "");
				$("#pwd").attr("disabled", "");
				
				var physicalFileNameArr = "";
				var originalFileNameArr = "";
				// 파일 리스트 구하기
				$("#fileList option").each(function () {
					if( $(this).val() != "" && $(this).attr("value2") == "now" ){
				    	originalFileNameArr += "," + $(this).text();
				    	physicalFileNameArr += "," + $(this).val();
			    	}
			    });
			    
			    originalFileNameArr   = originalFileNameArr.substring(1);
			    physicalFileNameArr   = physicalFileNameArr.substring(1);
			    var deleteFSeqArr 	  = $("#deleteFSeqArr").val();
			    deleteFSeqArr     	  = deleteFSeqArr.substring(1);
			    
			    document.form.originalFileNameArr.value = originalFileNameArr;
			    document.form.physicalFileNameArr.value = physicalFileNameArr;
			    document.form.deleteFSeqArr.value   = deleteFSeqArr;	    
				
				elClicked.form.submit();
			
			}catch(e){}
		}
		</script>
	</td>
</tr>
<tr><td colspan="3" height="3"></td></tr>
<tr><td colspan="3" height="1" bgcolor="#DDDDDD"></td></tr>
<tr><td colspan="3" height="3"></td></tr>
<tr>
	<th align="right" width="90" style="padding-right:10px">첨부파일</th>
	<td width="1"><img src="<html:rewrite page='/images/bg_board_hdLine.gif' />" border="0"></td>
	<td style="padding-left:10px">
		<table width="100%" cellspacing="0" cellpadding="0">
		<tr>
			<td rowspan="2">
				<select id="fileList" size="3" style="width:490" MULTIPLE>
					<option value="">--------------------------------- 첨부된 파일들 ---------------------------------</option>
					<c:forEach var="fileList" items="${fileList}">
						<option value="${ fileList.f_seq }" value2="db">${ fileList.f_originalName }</option>
					</c:forEach>
				</select>
			</td>
			<td><input type="button" value=" 첨 부 " class="btBox" onclick="$.fileUploadForm()"></td>
		</tr>
		<tr>
			<td><input type="button" value=" 삭 제 " class="btBox" onclick="$.fileDelete()"></td>
		</tr>
		</table>
	</td>
</tr>
<tr><td colspan="3" height="3"></td></tr>
<tr><td colspan="3" height="1" bgcolor="#DDDDDD"></td></tr>
<tr>
	<th align="right" width="90" style="padding-right:10px">비밀번호</th>
	<td width="1"><img src="<html:rewrite page='/images/bg_board_hdLine.gif' />" border="0"></td>
	<td style="padding-left:10px"><input type="password" maxLength="10" name="pwd" id="pwd" style="width:100px" value="${ content.b_pwd }"></td>
</tr>
<tr><td colspan="3" height="1" bgcolor="#61ACE6"></td></tr>
<tr><td colspan="3" height="10"></td></tr>
<tr>
	<td colspan="3" align="right">
		<input type="image" src="<html:rewrite page='/images/bt_board_edit.gif' />" border="0">
		<img src="<html:rewrite page='/images/bt_board_cancel.gif' />" border="0" onclick="history.back()" style="cursor:pointer">
		</form>
	</td>
</tr>
</table>
</body>

</html>