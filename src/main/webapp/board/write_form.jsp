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
	// �α����� �� �ִٸ�.
	if( $.cookie("ID") != null && $.cookie("ID") != "" ){
		//document.form.pwd.value = "***********";
		//document.form.pwd.disabled = true;
		//document.form.name.disabled = true; 
		$("#name").attr("disabled", "disabled");
		$("#pwd").attr("disabled", "disabled");
		$("#pwd").val("***********");
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
		alert("������ ���ϸ��Դϴ�.");		
	}else{
		$("#fileList").append("<option value='" + physicalFileName + "'>" + originalFileName + "</option>");
	}
}

$.fileDelete = function() {
	if( !confirm("���� �Ͻðڽ��ϱ�?") )
		return;
		
	var selectedName  = "";
	
	$("#fileList option:selected").each(function () {
		var value = $(this).val();		
		if( value != "" ){
    		selectedName  += "," + value;
    	}
    });
    selectedName  = selectedName.substring(1);
    
    if( selectedName == "" ){
    	alert("������ �Ѱ� �̻� ���� �Ͻʽÿ�.");
    	return;
    }
    
    var params  = {};
	params.fileNameArr = selectedName;
	var url			   = "fileDelete.do";
	$.httpService(url, $.param(params), 
				  function(data){
				  	var resultCnt = eval(data.totalItemCount[0].Text);
					if( resultCnt < 1 ){
						alert("���� ���� �� ������ �߻��Ͽ����ϴ�.");
					}else{
						$("#fileList option:selected").each(function () {
							var value = $(this).val();		
							if( value != "" ){
					    		$(this).remove();
					    	}
					    });
						alert("���� �Ǿ����ϴ�.");
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
		<form name="form" method="post" action="write.do" onSubmit="return _onSubmit(this)">
		<input type="hidden" name="physicalFileNameArr" value="">
		<input type="hidden" name="originalFileNameArr" value="">
		<input type="hidden" name="id" value="${ cookie.ID.value }">
		<input type="hidden" name="boardCode" value="${ param.boardCode }">
		<input type="hidden" name="b_seq" value="${ param.b_seq }">
	</td>
</tr>
<tr>
	<th align="right" width="90" style="padding-right:10px"><font color="#5081BC">����</font></th>
	<td width="1"><img src="<html:rewrite page='/images/bg_board_hdLine.gif' />" border="0"></td>
	<td style="padding-left:10px">
		<input type="text" name="subject" id="subject" maxLength="200" style="width:400px">
		&nbsp;&nbsp;<label for="secret" title="�ڽŰ� ������ �̿� ���� �Ҽ� �����ϴ�.">��б�</label> 
		 <input type="checkbox" name="secret" id="secret" value="1">
	</td>
</tr>
<tr><td colspan="3" height="1" bgcolor="#DDDDDD"></td></tr>
<tr>
	<th align="right" width="90" style="padding-right:10px"><font color="#5081BC">�ۼ���</font></th>
	<td width="1"><img src="<html:rewrite page='/images/bg_board_hdLine.gif' />" border="0"></td>
	<td style="padding-left:10px">
		<table width="100%" cellspacing="0" cellpadding="0">
		<tr>
			<td width="300"><input type="text" name="name" id="name" maxLength="20" style="width:120px" value="<%= NAME %>"></td>
			<th align="right" width="90" style="padding-right:10px"><font color="#5081BC">��й�ȣ</font></th>
			<td width="1"><img src="<html:rewrite page='/images/bg_board_hdLine.gif' />" border="0"></td>
			<td style="padding-left:10px"><input type="password" maxLength="10" name="pwd" id="pwd" style="width:100px"></td>
		</tr>
		</table>
	</td>
</tr>
<tr><td colspan="3" height="1" bgcolor="#DDDDDD"></td></tr>
<tr><td colspan="3" height="3"></td></tr>
<tr>
	<th align="right" width="90" style="padding-right:10px"><font color="#5081BC">����</font></th>
	<td width="1"><img src="<html:rewrite page='/images/bg_board_hdLine.gif' />" border="0"></td>
	<td style="padding-left:10px">
		<script type="text/javascript" src="<html:rewrite page='/board/SmartEditorBasic/js/HuskyEZCreator.js' />"></script>	
		<textarea name="content" id="content" style="width:100%; height:250px; display:none;"></textarea>
		<script>
		var oEditors = [];
		nhn.husky.EZCreator.createInIFrame({
			oAppRef: oEditors,
			elPlaceHolder: "content",
			sSkinURI: "SmartEditorBasic/SEditorSkin.html",
			fCreator: "createSEditorInIFrame"
		});
		
		function _onSubmit(elClicked){
			// �������� ������ ������ �����ÿ� ����ߴ� textarea�� �־� �ݴϴ�.
			oEditors.getById["content"].exec("UPDATE_IR_FIELD", []);
			try{
			
			var name 	= elClicked.name.value;
			var subject = elClicked.subject.value;
			var content = elClicked.content.value;
			var pwd 	= elClicked.pwd.value;
			//alert(name);
			//return false;
			if( $.trim(name) == "" ){
				alert("�̸��� �Է��Ͻʽÿ�.");
				elClicked.name.focus();
				return false;
			}
			
			if( $.trim(subject) == "" ){
				alert("������ �Է��Ͻʽÿ�.");
				elClicked.subject.focus();
				return false;
			}
			
			if( $.trim(content) == "" ){
				alert("������ �Է��Ͻʽÿ�.");
				elClicked.content.focus();
				return false;
			}
			
			if( $.trim(pwd) == "" ){
				alert("��й�ȣ�� �Է��Ͻʽÿ�.");
				elClicked.pwd.focus();
				return false;
			}
						
			if( $.trim(pwd).length < 4 ){
				alert("��й�ȣ�� 4�� �̻��Դϴ�.");
				elClicked.pwd.focus();
				return false;
			}
			
			$("#name").attr("disabled", "");
			$("#pwd").attr("disabled", "");
			
			var physicalFileNameArr = "";
			var originalFileNameArr = "";
			// ���� ����Ʈ ���ϱ�
			$("#fileList option").each(function () {
				if( $(this).val() != "" ){
			    	originalFileNameArr += "," + $(this).text();
			    	physicalFileNameArr += "," + $(this).val();
		    	}
		    });
		    
		    originalFileNameArr = originalFileNameArr.substring(1);
		    physicalFileNameArr = physicalFileNameArr.substring(1);
		    
		    document.form.originalFileNameArr.value = originalFileNameArr;
		    document.form.physicalFileNameArr.value = physicalFileNameArr;
			
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
	<th align="right" width="90" style="padding-right:10px"><font color="#5081BC">÷������</font></th>
	<td width="1"><img src="<html:rewrite page='/images/bg_board_hdLine.gif' />" border="0"></td>
	<td style="padding-left:10px">
		<table width="100%" cellspacing="0" cellpadding="0">
		<tr>
			<td rowspan="2">
				<select id="fileList" size="3" style="width:490" MULTIPLE>
					<option value="">--------------------------------- ÷�ε� ���ϵ� ---------------------------------</option>
				</select>
			</td>
			<td><input type="button" value=" ÷ �� " class="btBox" onclick="$.fileUploadForm()"></td>
		</tr>
		<tr>
			<td><input type="button" value=" �� �� " class="btBox" onclick="$.fileDelete()"></td>
		</tr>
		</table>
	</td>
</tr>
<tr><td colspan="3" height="3"></td></tr>
<tr><td colspan="3" height="1" bgcolor="#61ACE6"></td></tr>
<tr><td colspan="3" height="10"></td></tr>
<tr>
	<td colspan="3" align="right">
		<input type="image" src="<html:rewrite page='/images/bt_board_add.gif' />" border="0">
		<img src="<html:rewrite page='/images/bt_board_cancel.gif' />" border="0" onclick="history.back()" style="cursor:pointer">
		</form>
	</td>
</tr>
</table>
</body>

</html>