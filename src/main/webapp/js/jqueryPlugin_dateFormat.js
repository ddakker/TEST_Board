/*
예제)
'yyyy','yy','MMMM','MMM','MM','M','dd','d','EEEE','EEE','a',
'HH','H','hh','h','mm','m','ss','s','S',
'EEEE MMMM, d yyyy hh:mm:ss.S a',
'M/d/yy HH:mm'

$.log("모두: " + $.formatDate('yyyy-MM-dd HH:mm'));
$.log("년도(2009): " + $.dateFullYear());
$.log("년도(09): " + $.dateYear());
$.log("월: " + $.dateMonth());
$.log("일: " + $.dateDate());
$.log("요일(kr): " + $.dateDay());
$.log("요일(en): " + $.dateDay('EEEE'));
*/
(function($){
  $.formatDate = function(pattern) {
	var date = new Date();
    var result = [];
    while (pattern.length>0) {
      $.formatDate.patternParts.lastIndex = 0;
      var matched = $.formatDate.patternParts.exec(pattern);
      if (matched) {
        result.push($.formatDate.patternValue[matched[0]].call(this,date));
        pattern = pattern.slice(matched[0].length);
      }
      else {
        result.push(pattern.charAt(0));
        pattern = pattern.slice(1);
      }
    }
    return result.join('');
  };

  $.dateFullYear = function() {
	var date = new Date();
	pattern = "yyyy";

    var result = [];
    while (pattern.length>0) {
      $.formatDate.patternParts.lastIndex = 0;
      var matched = $.formatDate.patternParts.exec(pattern);
      if (matched) {
        result.push($.formatDate.patternValue[matched[0]].call(this,date));
        pattern = pattern.slice(matched[0].length);
      }
      else {
        result.push(pattern.charAt(0));
        pattern = pattern.slice(1);
      }
    }
    return result.join('');
  };

  $.dateYear = function() {
	var date = new Date();
	pattern = "yy";

    var result = [];
    while (pattern.length>0) {
      $.formatDate.patternParts.lastIndex = 0;
      var matched = $.formatDate.patternParts.exec(pattern);
      if (matched) {
        result.push($.formatDate.patternValue[matched[0]].call(this,date));
        pattern = pattern.slice(matched[0].length);
      }
      else {
        result.push(pattern.charAt(0));
        pattern = pattern.slice(1);
      }
    }
    return result.join('');
  };

  $.dateMonth = function() {
	var date = new Date();
	pattern = "MM";

    var result = [];
    while (pattern.length>0) {
      $.formatDate.patternParts.lastIndex = 0;
      var matched = $.formatDate.patternParts.exec(pattern);
      if (matched) {
        result.push($.formatDate.patternValue[matched[0]].call(this,date));
        pattern = pattern.slice(matched[0].length);
      }
      else {
        result.push(pattern.charAt(0));
        pattern = pattern.slice(1);
      }
    }
    return result.join('');
  };

  $.dateDate = function() {
	var date = new Date();
	pattern = "dd";

    var result = [];
    while (pattern.length>0) {
      $.formatDate.patternParts.lastIndex = 0;
      var matched = $.formatDate.patternParts.exec(pattern);
      if (matched) {
        result.push($.formatDate.patternValue[matched[0]].call(this,date));
        pattern = pattern.slice(matched[0].length);
      }
      else {
        result.push(pattern.charAt(0));
        pattern = pattern.slice(1);
      }
    }
    return result.join('');
  };

  $.dateDay = function(pattern) {
	var date = new Date();
	if( pattern == null ){
		pattern = "KKK";
    }

    var result = [];
    while (pattern.length>0) {
      $.formatDate.patternParts.lastIndex = 0;
      var matched = $.formatDate.patternParts.exec(pattern);
      if (matched) {
        result.push($.formatDate.patternValue[matched[0]].call(this,date));
        pattern = pattern.slice(matched[0].length);
      }
      else {
        result.push(pattern.charAt(0));
        pattern = pattern.slice(1);
      }
    }
    return result.join('');
  };

  $.formatDate.patternParts =
    /^(yy(yy)?|M(M(M(M)?)?)?|d(d)?|EEE(E)?|KKK(K)?|a|H(H)?|h(h)?|m(m)?|s(s)?|S)/;

  $.formatDate.monthNames = [
    'January','February','March','April','May','June','July',
    'August','September','October','November','December'
  ];

  $.formatDate.dayNames = [
    'Sunday','Monday','Tuesday','Wednesday','Thursday','Friday',
    'Saturday'
  ];

  $.formatDate.dayNamesKr = [
    '일요일','월요일','화요일','수요일','목요일','금요일',
    '토요일'
  ];

  $.formatDate.patternValue = {
    yy: function(date) {
      return $.toFixedWidth(date.getFullYear(),2);
    },
    yyyy: function(date) {
      return date.getFullYear().toString();
    },
    MMMM: function(date) {
      return $.formatDate.monthNames[date.getMonth()];
    },
    MMM: function(date) {
      return $.formatDate.monthNames[date.getMonth()].substr(0,3);
    },
    MM: function(date) {
      return $.toFixedWidth(date.getMonth()+1,2);
    },
    M: function(date) {
      return date.getMonth()+1;
    },
    dd: function(date) {
      return $.toFixedWidth(date.getDate(),2);
    },
    d: function(date) {
      return date.getDate();
    },
    EEEE: function(date) {
      return $.formatDate.dayNames[date.getDay()];
    },
    EEE: function(date) {
      return $.formatDate.dayNames[date.getDay()].substr(0,3);
    },
	KKKK: function(date) {
      return $.formatDate.dayNamesKr[date.getDay()];
    },
    KKK: function(date) {
      return $.formatDate.dayNamesKr[date.getDay()].substr(0,1);
    },
    HH: function(date) {
      return $.toFixedWidth(date.getHours(),2);
    },
    H: function(date) {
      return date.getHours();
    },
    hh: function(date) {
      var hours = date.getHours();
      return $.toFixedWidth(hours>12 ? hours - 12 : hours,2);
    },
    h: function(date) {
      return date.getHours()%12;
    },
    mm: function(date) {
      return $.toFixedWidth(date.getMinutes(),2);
    },
    m: function(date) {
      return date.getMinutes();
    },
    ss: function(date) {
      return $.toFixedWidth(date.getSeconds(),2);
    },
    s: function(date) {
      return date.getSeconds();
    },
    S: function(date) {
      return $.toFixedWidth(date.getMilliseconds(),3);
    },
    a: function(date) {
      return date.getHours() < 12 ? 'AM' : 'PM';
    }
  };

  $.toFixedWidth = function(value,length,fill) {
    if (!fill) fill = '0';
    var result = value.toString();
    var padding = length - result.length;
    if (padding < 0) {
      result = result.substr(-padding);
    }
    else {
      for (var n = 0; n < padding; n++) result = fill + result;
    }
    return result;
  };

})(jQuery);
