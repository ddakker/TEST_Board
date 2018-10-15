/* 
 * Copyright (c) 2009 ddakker
 * author: ddakker@naver.com
 * Date: 2009-05-01
 */
(function($){
	var XML  = 0;
	var JSON = 1;
	var TEXT = 2;

	$.httpService = function(url, params, callBackFunction, bType) {
		bType = bType ? bType : TEXT;
		params = params == "" ? "ajax=1" : params + "&ajax=1";
		$.ajax({
			type: "POST",
			dataType: "text",
			url: url,
			data: params,				
			beforeSend: function(XMLHttpRequest){									
				$.log.debug("시작 url: " + url + "?" + params);
				$.showLoding();
			},
			complete: function(XMLHttpRequest, textStatus){
				$.log.debug("성공 url: " + url + "?" + params);
				$.hideLoding();				
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				$.log.error("------------------------------------------");
				$.log.error("실패 url: " + url + "?" + params);
				$.log.error("error:\n\n XMLHttpRequest: " + XMLHttpRequest + "\ntextStatus: " + textStatus + "\nerrorThrown: " + errorThrown);
				$.log.error("------------------------------------------");
				//jAlert("서버와 통신중 문제가 발생하였습니다. 지성^^;<br>관리자에게 문의해주세요.", "오류", function(bResult){
				//	;
				//});
				alert("서버와 통신중 문제가 발생하였습니다. 지성^^;<br>관리자에게 문의해주세요.");
				return;
			},
			success: function(data, textStatus){				
				var xmlData  = $.textToXML(data);
				
				// 여기에 걸리면 error.vm으로 간 것이다. 에러 처리
				try{
					var errorMsg 	 = xmlData.getElementsByTagName("message").item(0).firstChild.nodeValue;
					var exceptionMsg = xmlData.getElementsByTagName("exception").item(0).firstChild.nodeValue;
					$.log.error("------------------------------------------");
					$.log.error("실패 url: " + url + "?" + params);
					$.log.error("error:\n\n message: " + errorMsg + "\exception: " + exceptionMsg);
					$.log.error("------------------------------------------");
					alert("서버 비지니스 처리 오류\n\n관리자에게 문의하여주십시요.");
				}catch(e){}
				
				
				if( bType == XML ){
					callBackFunction(xmlData, textStatus);
				}else if( bType == JSON ){								
					var jsonData = $.xmlToJSON(xmlData);
					callBackFunction(jsonData, textStatus);
				}else{
					callBackFunction(data, textStatus);
				}				
			}
		});
		
	};

	$.httpService.XML  = XML;
	$.httpService.JSON = JSON;
	$.httpService.TEXT = TEXT;
})(jQuery);
