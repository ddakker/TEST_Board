<%@ page language="java" contentType="text/html; charset=euc-kr" isELIgnored = "false"%>

<%

String physicalFileName = (String) request.getAttribute("physicalFileName");
String originalFileName = (String) request.getAttribute("originalFileName");

if( physicalFileName != null && originalFileName != null ){
	String errorCode		= (String) request.getAttribute("errorCode");
	if( errorCode.equals("1") ){
		out.print("<script>");
		out.print("alert('JSP || ASP || PHP || APSX �� ���ȿ� ���� �Ǵ� ������ ���ε� �Ͻ� �� �����ϴ�.');");
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
	<input type="submit" value="���ε�">
</form>