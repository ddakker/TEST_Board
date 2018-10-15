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
				$.log.debug("���� url: " + url + "?" + params);
				$.showLoding();
			},
			complete: function(XMLHttpRequest, textStatus){
				$.log.debug("���� url: " + url + "?" + params);
				$.hideLoding();				
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				$.log.error("------------------------------------------");
				$.log.error("���� url: " + url + "?" + params);
				$.log.error("error:\n\n XMLHttpRequest: " + XMLHttpRequest + "\ntextStatus: " + textStatus + "\nerrorThrown: " + errorThrown);
				$.log.error("------------------------------------------");
				//jAlert("������ ����� ������ �߻��Ͽ����ϴ�. ����^^;<br>�����ڿ��� �������ּ���.", "����", function(bResult){
				//	;
				//});
				alert("������ ����� ������ �߻��Ͽ����ϴ�. ����^^;<br>�����ڿ��� �������ּ���.");
				return;
			},
			success: function(data, textStatus){				
				var xmlData  = $.textToXML(data);
				
				// ���⿡ �ɸ��� error.vm���� �� ���̴�. ���� ó��
				try{
					var errorMsg 	 = xmlData.getElementsByTagName("message").item(0).firstChild.nodeValue;
					var exceptionMsg = xmlData.getElementsByTagName("exception").item(0).firstChild.nodeValue;
					$.log.error("------------------------------------------");
					$.log.error("���� url: " + url + "?" + params);
					$.log.error("error:\n\n message: " + errorMsg + "\exception: " + exceptionMsg);
					$.log.error("------------------------------------------");
					alert("���� �����Ͻ� ó�� ����\n\n�����ڿ��� �����Ͽ��ֽʽÿ�.");
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
