/* 
 * Copyright (c) 2009 ddakker
 * author: ddakker@naver.com
 * Date: 2009-05-01
 */
(function($){
	var ERROR = 0;
	var INFO  = 1;
	var DEBUG = 2;

	var level = DEBUG;
	
	$.log = {};	

	$.log.error = function(str) {
		if( level >= ERROR )
			$.print("[ERROR] " + str);
	};

	$.log.info = function(str) {
		if( level >= INFO )
			$.print("[INFO] " + str);
	};

	$.log.debug = function(str) {
		if( level >= DEBUG )
			$.print("[DEBUG] " + str);
	};

	$.print = function(str) {		

		var log = $("#logElement").html();		
		if( log == null ){
			alert("logElement 란 id값의 text View 성 노트를 생성하세요.");
		}else{
			str = str.split("<").join("&lt;");
			str = str.split(">").join("&gt;");
			str = str.split(" ").join("&nbsp;");

			//var div = document.createElement("div");
			//div.innerText = "[" + $.formatDate('yyyy-MM-dd HH:mm:ss:S') + "]" +str;
			//$("#logElement").prepend(div);
			var infoStr = "[" + $.formatDate('yyyy-MM-dd HH:mm:ss:S') + "]" + str;
			//$("#logElement").before("<div>" + infoStr + "</div>");
			$("#logElement").html($("#logElement").html() + "<br>" + infoStr);
		}
	};

	$.log.isError = function() {
		if( level >= ERROR )
			return true;
		else
			return false;
	};

	$.log.isInfo = function() {
		if( level >= INFO )
			return true;
		else
			return false;
	};

	$.log.isDebug = function() {
		if( level >= DEBUG )
			return true;
		else
			return false;
	};
})(jQuery);
