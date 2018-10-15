
var ROOT_PATH = "TEST_Board";

var _bSeq;	// 현재 상세페이지  seq
var _bCommentEdit = false; // 댓글 수정 여부(댓글 쓰기/삭제)


$(document).ready(function(){
	
	$("#tbList").css("position", "absolute").css("top", 95);
	$("#tbContent").hide().css("position", "absolute");
	$("#tbContent").css("top", $("#tbList").height() + 95);
	
	$.log.debug("-- height: " + $("#tbList").height());
	$.log.debug("-- con top: " + $("#tbContent").position().top);
	$.log.debug("-- con top: " + $("#tbContent").css("top"));
	
	$.log.debug("-- lis top: " + $("#tbList").position().top);
	$.log.debug("-- lis top: " + $("#tbList").css("top"));
			
});


$.onSearchClicked = function(boardCode){
	
	var searchStr  = $("#txtSearchStr").val();
	var searchKind = $("#selectSearchKind").val();
	
	if( $.trim(searchStr) == "" ){
		alert("검색어를 입력하십시요.");		
		$("#txtSearchStr").trigger("focus");
		return;
	}
	
	location.href = "list.do?boardCode=" + boardCode + "&searchKind=" + searchKind + "&searchStr=" + searchStr;
}

$.onSearchCancelClicked = function(boardCode){
	location.href = "list.do?boardCode=" + boardCode;
}


$.goToContent = function(seq, b_id, secret, b_delete){
	if( b_delete == "1" ){
		alert("삭제된 글입니다.");
		return;
	}

	$("body").attr("scrollTop", 0);	
	if( seq == "list" ){	
		$.inputPwdHide();
		
		var moveSize = $("#tbList").height();
		$("#tbContent").css("background-color", "#FFFFFF").animate({top: "+=" + moveSize + "px", opacity:0}, 300, null, function(a){
			// 댓글이 추가/삭제 됐을 경우 리스트를 새로 불러와야 하므로
			if( _bCommentEdit == true ){			
				location.reload();
			}else{
				$.callMentHide();
			}
		});		
	}else{
		_bSeq = seq;
		
		// 비밀글 이라면
		if( secret && secret == "1" && $.cookie("ID") != b_id && $.cookie("ADMIN") != "1" ){
			$.inputPwdView('secret', seq, b_id);
			return;
		}
	
		/**
		 Ajax로 content를 불러와서 현재 글의 ID값이 java변수로 가질 수 없어 javascript에서 처리
		 로그인 사용자의 경우 자기 자신의 글에 대해서만 삭제/수정 버튼이 보인다.
		 display 속성으로 조정하려 하였으나 UI가 깨져 아래와 같이 처리
		*/
		if( $.cookie("ID") !=null && $.cookie("ID") != b_id ){
			$("#btDelete").attr("width", "0");
			$("#btEdit").attr("width", "0");
		}else{
			$("#btDelete").attr("width", "45");
			$("#btEdit").attr("width", "45");
		}
		
		var params  = {};
		params.seq	= seq;
		var url		= "content.do";
		$.httpService(url, $.param(params), $.callGetContent_Result, $.httpService.JSON);
	}
}

$.callGetContent_Result = function(data){
	var name 		= data.list[0].item[0].b_name[0].Text;
	var subject 	= data.list[0].item[0].b_subject[0].Text;
	var regdate 	= data.list[0].item[0].b_regdate2[0].Text;
	var hit 		= data.list[0].item[0].b_hit[0].Text;
	var content 	= data.list[0].item[0].b_content[0].Text;
	var comment_cnt = data.list[0].item[0].comment_cnt[0].Text;
	
	$("#contentName").text(name);
	$("#contentSubject").text(subject);
	$("#contentRegdate").text(regdate);
	$("#contentHit").text(hit);
	$("#contentContent").html(content);
	$("#divCommentCnt").html("<b>댓글보기(" + comment_cnt + ")</b>");
	
	// 첨부 파일
	
	var fileListSize = data.totalItemCount2[0].Text;	
	$("#contentFileList").empty();
	for( var i=0; i<fileListSize; i++ ){		
		$("#contentFileList").append("<div onclick=\"$.fileDownLoad('" + data.list2[0].item[i].f_seq[0].Text + "')\" style='cursor:pointer'><img src='/"+ROOT_PATH+"/images/icon_file.gif' border='0'>&nbsp;" + data.list2[0].item[i].f_originalName[0].Text + "</div>");
	}
	
	var moveSize = $("#tbList").height();
	$("#tbContent").css("background-color", "#FFFFFF").show().animate({top: "-=" + moveSize + "px", opacity:1}, 300);
}

// 멘트 쓰기
$.mentWrite = function(){
	var seq  = _bSeq;
	var ment = $("#c_ment").val();
	var id   = $.cookie('ID');
	var name = $("#c_name").val();
	var pwd  = $("#c_pwd").val();
	
	if( $.trim(ment) == "" ){
		alert("댓글을 입력하세요.");
		$("#c_ment").trigger("focus");
		return;
	}
	
	if( $.trim(name) == "" ){
		alert("이름을 입력하세요.");
		$("#c_name").trigger("focus");
		return;
	}
	
	if( $.trim(pwd) == "" ){
		alert("비밀번호를 입력하세요.");
		$("#c_pwd").trigger("focus");
		return;
	}
	
	if( $.trim(pwd).length < 4 ){
		alert("비밀번호는 4자 이상 입력하셔야 합니다.");
		$("#c_pwd").trigger("focus");
		return;
	}
	
	var params  = {};
	params.seq	= seq;
	params.ment	= ment;
	params.id	= id;
	params.name	= name;
	params.pwd	= pwd;
	var url		= "mentWrite.do";
	$.httpService(url, $.param(params), $.callMentWrite_Result, $.httpService.JSON);
}
$.callMentWrite_Result = function(data){
	if( data.totalItemCount[0].Text > 0 ){
		$("#c_ment").val("");
		
		if( $.cookie("ID") == null || $.cookie("ID") == "" ){
			$("#c_name").val("");
			$("#c_pwd").val("");
		}
		
		$.callMentList();
		
		_bCommentEdit = true;
	}
}


$.callMentView = function(B_LOGIN){
	if( $("#divMent").css("display") == "block" ){
		$.callMentHide();
		return;
	}
	
	$("#c_ment").val("");
	
	// 로그인이 되 있다면.
	if( $.cookie("ID") != null && $.cookie("ID") != "" ){			
		$("#c_name").attr("disabled", "disabled");
		$("#c_pwd").attr("disabled", "disabled");
		$("#c_pwd").val("******************");
		
		$("#divMentUserInfo").hide();
	}else{
		$("#c_name").attr("disabled", "");
		$("#c_pwd").attr("disabled", "");
		$("#c_pwd").val("");
		
		// 로그인 시에만 글쓰기가 가능할 경우		
		if( B_LOGIN !=null && B_LOGIN == "true" ){
			$("#c_name").attr("disabled", "disabled");
			$("#c_pwd").attr("disabled", "disabled");
			$("#c_ment").val("로그인 후 사용 가능합니다.").attr("disabled", "disabled");
			$("#c_send").attr("disabled", "disabled");
		}
		
		$("#divMentUserInfo").show();
	}
	
	$("#divMent").show();	
		
	$("#c_ment").trigger("focus");
	
	$.callMentList();
}

$.callMentHide = function(where){
	$("#divMentRow").empty();
	$("#divMent").hide();
	$("body").attr("scrollTop", 0);
}

// 멘트 가져오기
$.callMentList = function(){
	var params   = {};
	params.b_seq = _bSeq;
	var url		 = "mentList.do";
	$.httpService(url, $.param(params), $.callMentList_Result, $.httpService.JSON);
}
$.callMentList_Result = function(data){
	$("#divMentRow").empty();
	
	var itemArr		= data.list[0].item;
	var itemArrSize = eval(data.totalItemCount[0].Text);
	if( itemArr != undefined ){		
		for( var i=0; i<itemArrSize; i++ ){
			var c_seq     = itemArr[i].c_seq[0].Text;
			var c_id	  = itemArr[i].c_id[0].Text;
			var c_name    = itemArr[i].c_name[0].Text;
			var c_ment    = itemArr[i].c_comment[0].Text;
			var c_regdate = itemArr[i].c_regdate2[0].Text;
			
			var bLast = false;
			if( i == (itemArrSize-1) ) bLast = true;
			$.mentAppend(c_seq, c_id, c_name, c_ment, c_regdate, bLast);
		}
	}
	
	$("#divCommentCnt").html("<b>댓글보기(" + itemArrSize + ")</b>");
	$("#divMent").show();
}

// 맨트 생성해서 끼워넣기
$.mentAppend = function(c_seq, c_id, c_name, c_ment, c_regdate, bLast){
		
	c_ment = c_ment.split("<").join("&lt;");
	c_ment = c_ment.split(">").join("&gt;");
	c_name = c_name.split("<").join("&lt;");
	c_name = c_name.split(">").join("&gt;");
	
	var mentHTML = "";
	
	mentHTML += "<table width='100%' height='30' cellspacing='0' cellpadding='0'>";
	mentHTML += "<tr>";
	mentHTML += "	<td width='75'><font color='#F06524'>" + c_name + "</font></td>";
	mentHTML += "	<td>" + c_ment + "&nbsp;&nbsp;<font color='#989898' style='font-size:8pt'>(" + c_regdate + ")</font></td>";
	mentHTML += "	<td width='20' align='right'>";
	if( $.cookie("ID") != null && $.cookie("ID") != "" ){
		if( ($.cookie("ADMIN") !=null && $.cookie("ADMIN") == "1" ) || $.cookie("ID") == c_id ){
			mentHTML += "<img src='/" + ROOT_PATH + "/images/bt_board_delete_small.gif' class='hand' onclick=\"$.callDeleteMent('" + c_seq + "')\">";
		}
	}else{
		mentHTML += "<img src='/" + ROOT_PATH + "/images/bt_board_delete_small.gif' class='hand' onclick=\"$.inputPwdView('ment', '" + c_seq + "')\">";
	}	
	mentHTML += "	</td";
	mentHTML += "</tr>";
	mentHTML += "<tr><td colspan='3' background='/" + ROOT_PATH + "/images/bg_board_comment_line.gif' height='1'></td></tr>";
	mentHTML += "</table>";
	
	
	$("#divMentRow").append("<div id='divMentRow_" + c_seq + "'>" + mentHTML + "</div>");
	
	if( bLast ){
		$("body").attr("scrollTop", $("#divMentRow_" + c_seq).position().top);
	}
}

// 댓글 삭제
$.callDeleteMent = function(c_seq, c_pwd){
	if( !c_pwd ){
		if( !confirm("삭제 하시겠습니까?") )
			return;
	}
		
	var params   = {};
	params.c_seq = c_seq;
	params.c_pwd = c_pwd;
	var url		 = "mentDelete.do";
	$.httpService(url, $.param(params), $.callDeleteMent_Result, $.httpService.JSON);
}
$.callDeleteMent_Result = function(data){
	if( data.totalItemCount[0].Text == "1" ){	
		$.callMentList();
		$.inputPwdHide();
		
		_bCommentEdit = true;
	}else{
		alert("비밀번호가 일치하지 않습니다.");
		$("#input_pwd").trigger("focus");
	}
}


// 비 로그인 자 삭제 시 비밀번호 입력 받기
$.inputPwdView = function(where, seq, id){
	
	if( $("#divInput_pwd").css("display") == "none" ){
		
		$("#input_where").val(where);
		$("#input_seq").val(seq);
		$("#input_id").val(id);
		
		var mouseX	  = event.clientX + $(window).scrollLeft();
		var mouseY	  = event.clientY + $(window).scrollTop();

		var disWidth  = $("#divInput_pwd").width();
		var disHeight = $("#divInput_pwd").height();

		$("#divInput_pwd").draggable({ handle: $("#divInput_pwd_Drag") });
		$("#divInput_pwd").css("left", (mouseX-disWidth)<0?10:(mouseX-disWidth)).css("top", (mouseY-disHeight-5)<0?10:(mouseY-disHeight-5)).fadeIn(300, function(e){
			$("#input_pwd").trigger("focus");
		});		
	}else{
		$.inputPwdHide();
	}
}
$.inputPwdHide = function(){
	$("#input_pwd").val("");
	$("#divInput_pwd").hide();
}

// 비밀번호 입력창에서 엔터
$.inputPwdEnter = function() {
	if( event.keyCode == 13 ){
		$.inputPwdSend();
	}
}

// 비밀번호 입력창에서 확인. 진행
$.inputPwdSend = function(){
	var input_where = $("#input_where").val();	
	var input_seq   = $("#input_seq").val();
	var input_pwd   = $("#input_pwd").val();
			
	if( input_where == "ment" ){
		$.callDeleteMent(input_seq, input_pwd);
	}else if( input_where == "boardDelete" || input_where == "boardEdit" || input_where == "secret" ){
		$.checkPWD(input_pwd, input_where);
	}
	
}



// 글 수정/삭제 시 비밀번호 체크
$.checkPWD = function(b_pwd, where){
	
	
	
	var params   = {};
	params.b_seq = _bSeq;
	params.b_pwd = b_pwd;
	var url		 = "getBoardPwd.do";
	$.httpService(url, $.param(params), 
				 function(data){
				 	if( data.totalItemCount[0].Text == "1" ){
						if( where == "boardEdit" ){
							$.go2boardEditForm();
						}else if( where == "boardDelete" ){
							$.go2boardDelete(b_pwd);							
						}else if( where == "secret" ){
							var input_id   = $("#input_id").val();
							$.goToContent(_bSeq, input_id, '0');
						}
						$.inputPwdView();
					}else{
						alert("비밀번호가 일치하지 않습니다.");
						$("#input_pwd").val("").trigger("focus");
					}
				 }, 
				 $.httpService.JSON);
}

// 삭제 진행
$.go2boardDelete = function(b_pwd){
	if( !b_pwd )
		if( !confirm("삭제 하시겠습니까?") )
			return;
		
	var boardCode = $("#boardCode").val();
	var nowPage   = $("#nowPage").val();
	location.href = "delete.do?boardCode=" + boardCode + "&nowPage=" + nowPage +"&b_seq=" + _bSeq + "&b_pwd=" + b_pwd;
}

// 수정 페이지 이동
$.go2boardEditForm = function(){
	var boardCode = $("#boardCode").val();
	var nowPage   = $("#nowPage").val();
	location.href = "edit_form.do?boardCode=" + boardCode + "&nowPage=" + nowPage +"&seq=" + _bSeq;
}

// 답변 등록
$.replyWrite = function(boardCode){
	location.href = "write_form.do?boardCode=" + boardCode + "&b_seq=" + _bSeq;
}

// 파일 다운로드
$.fileDownLoad = function(f_seq){
	location.href = "fileDownLoad.do?f_seq=" + f_seq;
}
