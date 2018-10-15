<%@ page language="java" contentType="text/html; charset=euc-kr"%>
<link rel="stylesheet" href="http://8.1.1.143:9100/css/style_nhcard.css" type="text/css">
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<script>

var nh = {};
nh.lnj = {};
nh.lnj.js = {};
nh.lnj.js.utils = {};

/**
 * Flah 삽입
 * @param id		id 값
 * @param swf		swf 경로 및 파일 명
 * @param width		가로 사이즈
 * @param height	세로 사이즈
 * @param flashvars	파리미터
 * @param trans		wmode(Window, Opaque, Transparent, direct, gpu)
 * @param bgcolor	배경색
 * @param version	flash player version
 */
nh.lnj.js.utils.insertFlash = function(id, swf, width, height, flashvars, trans, bgcolor, version) {
	if( !id )		 id = "";	
	if( !flashvars ) flashvars = "";
	if( !trans )	 trans = "Window";
	if( !bgcolor )	 bgcolor = "#FFFFFF";
	if( !version )	 version = "10,0,22,87";

	document.write("<object id='" + id + "' classid='clsid:D27CDB6E-AE6D-11cf-96B8-444553540000' codebase='http://fpdownload.macromedia.com/get/flashplayer/current/swflash.cab#version=" + version + "' width='" + width + "' height='" + height + "' style='position:absoulte;left:0;top:0'>");
	document.write("<param name='movie' value='" + swf + "' />");
	document.write("<param name='quality' value='high' />");
	document.write("<param name='menu' value='0' /> ");
	document.write("<param name='allowScriptAccess' value='sameDomain' />");
	document.write("<param name='bgcolor' value='" + bgcolor + "' />");
	document.write("<param name=FlashVars value='" + flashvars + "' />");
	document.write("<param name='wmode' value='" + trans + "'/>");	
	document.write("<embed name='" + id + "' type='application/x-shockwave-flash' src='" + swf + "' wmode='" + trans + "' flashvars='" + flashvars + "' quality='high' bgcolor='" + bgcolor + "' menu='0' allowScriptAccess='sameDomain' width='" + width + "' height='" + height + "' pluginspage='http://www.adobe.com/go/getflashplayer'></embed>");
	document.write("</object>");
}

/**
 * 페이징 구현 하여 divName 에 출력
 * @param pDivName		출력한 DIV id 값
 * @param pFomName		form submit 할 form name 값
 * @param pReqInputName	요청 페이지 번호를 셋팅할 input name 값
 * @param pBlockPage 	한 페이지에 표현 될 페이지 수
 * @param bTotalPage 	총 페이지 수
 * @param pNowPage 		현재 페이지
 */
nh.lnj.js.utils.printPagging = function(pDivName, pFomName, pReqInputName, pBlockPage, bTotalPage, pNowPage) {
	var numLine  = "http://8.1.1.143:9100/imgs/nhcard/page.gif";
	
	var firstImg = "http://8.1.1.143:9100/imgs/nhcard/ico_start.gif";
	var prevImg  = "http://8.1.1.143:9100/imgs/nhcard/ico_pre.gif";
	var nextImg  = "http://8.1.1.143:9100/imgs/nhcard/ico_next.gif";
	var lastImg  = "http://8.1.1.143:9100/imgs/nhcard/ico_end.gif";
	
	var firstOffImg = "http://8.1.1.143:9100/imgs/nhcard/ico_start_off.gif";
	var prevOffImg  = "http://8.1.1.143:9100/imgs/nhcard/ico_pre_off.gif";
	var nextOffImg  = "http://8.1.1.143:9100/imgs/nhcard/ico_next_off.gif";
	var lastOffImg  = "http://8.1.1.143:9100/imgs/nhcard/ico_end_off.gif";
	
	var blockPage  = pBlockPage;
	var totalPage  = bTotalPage;
	var nowPage    = pNowPage;
	
	var intTemp = -1;
	var intLoop = -1;
	
	var pagingStr = "";
	
	pagingStr += "<div class='board_page'>";
	pagingStr += "<div class='pager'>";
	
	if( nowPage != 1 ){
		pagingStr += "<img src='" + firstImg + "' style='cursor:pointer' onclick=\"new nh.lnj.js.utils.goToNextPage('" + pFomName + "', '" + pReqInputName + "', '1')\" />&nbsp;";
	}else{
		pagingStr += "<img src='" + firstOffImg + "' />&nbsp;";
	}
	
	intTemp = Math.floor((nowPage - 1) / blockPage) * blockPage + 1;
	
	if( intTemp == 1 ){
		pagingStr += "<img src='" + prevOffImg + "' />&nbsp;";
	}else{
		pagingStr += "<img src='" + prevImg + "' style='cursor:pointer' onclick=\"new nh.lnj.js.utils.goToNextPage('" + pFomName + "', '" + pReqInputName + "', '" + (intTemp-blockPage) + "')\" />&nbsp;";
	}

	intLoop = 1;
	while( !( (intLoop > blockPage) || (intTemp > totalPage) ) ){
		if( intTemp == nowPage ){
			pagingStr += "<span class='current'>" + intTemp + "</span>";
		}else{
			pagingStr += "<span class='no' onclick=\"new nh.lnj.js.utils.goToNextPage('" + pFomName + "', '" + pReqInputName + "', '" + intTemp + "')\">" +  intTemp + "</span>";
		}
		
		if( intTemp != totalPage && (intTemp%blockPage) != 0 ){
			pagingStr += "<img src='" + numLine + "' />";
		}else{
			pagingStr += "&nbsp";
		}
			
		intTemp = intTemp + 1;
		intLoop = intLoop + 1;		
	}

	if( intTemp > totalPage ){
		pagingStr += "<img src='" + nextOffImg + "' />&nbsp;";
	}else{
		pagingStr += "<img src='" + nextImg + "' style='cursor:pointer' onclick=\"new nh.lnj.js.utils.goToNextPage('" + pFomName + "', '" + pReqInputName + "', '" + intTemp + "')\" />&nbsp;";
	}

	if( nowPage != totalPage ){
		pagingStr += "<img src='" + lastImg + "' style='cursor:pointer' onclick=\"new nh.lnj.js.utils.goToNextPage('" + pFomName + "', '" + pReqInputName + "', '" + totalPage + "')\" />";
	}else{
		pagingStr += "<img src='" + lastOffImg + "' />";
	}
	
	pagingStr += "</div>";
	pagingStr += "</div>";
	
	document.getElementById(pDivName).innerHTML = pagingStr;
}

/**
 * form submit
 * @param pFomName		form submit 할 form name 값
 * @param pReqInputName	요청 페이지 번호를 셋팅할 input name 값
 * @param pPageNum		요청 페이지
 */
nh.lnj.js.utils.goToNextPage = function(pFomName, pReqInputName, pPageNum) {
	eval("document." + pFomName + "." + pReqInputName).value = pPageNum;
	eval("document." + pFomName).submit();
}


</script>

 

<form name="formPage">
<input type="hidden" name="RqtPagNbr_4" value="<%=request.getParameter("RqtPagNbr_4")%>">
<input type="hidden" name="PagNum_4" value="<%=request.getParameter("PagNum_4")%>">
</form>

<div id="divPaging">페이지 정보 들어갈 자리</div>
<script>
 	// (출력한 DIV id 값, form submit 할 form name 값, 요청 페이지 번호를 셋팅할 input name 값, 한 페이지에 표현 될 페이지 수, 총 페이지 수, 현재 페이지)
	new nh.lnj.js.utils.printPagging("divPaging", "formPage", "RqtPagNbr_4", 5, <%=request.getParameter("PagNum_4")%>, <%=request.getParameter("RqtPagNbr_4")%>);
</script>
