<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/page" prefix="page" %>
<html>
	<head> 
		<title><decorator:title default="Mysterious page..." /></title>
		<link href="<%= request.getContextPath() %>/decorators/main.css" rel="stylesheet" type="text/css">
		<decorator:head />
	</head>

	<body>
		<table width="100%" height="100%"> 
			<tr>
				<td valign="top">				
					<page:applyDecorator name="left1" />
				</td>
				<td width="100%">
					<table width="100%" height="100%">
						<tr>
							<td id="pageTitle">
								<decorator:title />
							</td>
						</tr>
						<tr>
							<td valign="top" height="100%">								
								<decorator:body />
							</td>
						</tr>
						<tr>
							<td id="footer">
								<page:applyDecorator name="foot1" />
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>


	</body>
</html>