<%@ page language="java" contentType="text/html; charset=euc-kr" isELIgnored = "false"%>

<%

String physicalFileName = (String) request.getAttribute("physicalFileName");
String originalFileName = (String) request.getAttribute("originalFileName");

if( physicalFileName != null && originalFileName != null ){
	String errorCode		= (String) request.getAttribute("errorCode");
	if( errorCode.equals("1") ){
		out.print("<script>");
		out.print("alert('JSP || ASP || PHP || APSX 등 보안에 문제 되는 파일은 업로드 하실 수 없습니다.');");
		out.print("</script>");
		
		physicalFileName = null;
		originalFileName = null;
	}else{
	
		out.print("<script>");
		out.print("opener.uploadFileSetting('" + physicalFileName + "', '" + originalFileName + "');");
		out.print("window.close();");
		out.print("</script>");
		return;
	}
}

%>


<form name="form" method="post" action="fileUpload.do" enctype="multipart/form-data">
	<input type="file" name="file1">
	<input type="submit" value="업로드">
</form>