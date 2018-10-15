/**
 * date :2008. 06. 12
 * author: ddakker@naver.com
 */
package dak.web.framework.util;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ParamUtils {
	private static Log log;
	static 
    {
        log = LogFactory.getLog(dak.web.framework.util.ParamUtils.class);
    }
	 /**
	  * request를 타고 넘어온 모든 피라미터 문자열 생성
      * @param request
      * @return
      */
    public static String printParameter(HttpServletRequest request){
		
		String paramsMsg = "\n";
		
		paramsMsg += "==================== printParameter ====================" + "\n";
		
		Map map = request.getParameterMap();
		
		if( map != null ){
		
			Set set = map.keySet();
			Iterator it = set.iterator();
			while( it.hasNext() ){
				String key	 = (String)it.next();
				String value = null;
				
				if( map.get(key) instanceof String[]) {
					String [] tempStr = (String[]) map.get(key);
					value = tempStr[0];
				}else{
					value = map.get(key).toString();
				}
				
				paramsMsg += "==== " + key + " : " + value + "\n";
				
			}
		}
		
		paramsMsg += "==================== printParameter ====================" + "\n";
		
		
		return paramsMsg;
	}
    
    /**
     * null 일경우 "" 공백으로 리턴
     * @param request
     * @param param
     * @return
     */
    public static String getParameter(HttpServletRequest request, String param) {
    	
    	
    	return getParameter(request, param, "");
    }
    
    public static String getParameter(HttpServletRequest request, String param, String nvl) {
    	String ajax = request.getParameter("ajax");
    	
    	String str = null; 
    	
    	try{
	    	if( ajax == null )
	    		str = new String(request.getParameter(param).getBytes("8859_1"), "euc-kr");
	    	else
	    		str = new String(request.getParameter(param).getBytes("8859_1"), "utf-8");
    	}catch(Exception e){
    		str = request.getParameter(param);
    	}
    	
    	return StringUtils.nvl(str, nvl);
    }
    
    public static int getParameterInt(HttpServletRequest request, String param) {
    	    	
    	
    	return getParameterInt(request, param, "-1");
    }
    
    public static int getParameterInt(HttpServletRequest request, String param, String nvl) {
    	
    	String str = request.getParameter(param);
    	
    	str = StringUtils.nvl(str, nvl);
        	
    	return Integer.parseInt( str );
    }
    
    public static String[] getParameterComma(HttpServletRequest request, String param, String regex) {
    	String value = getParameter(request, param);
    	if( value.trim().length() == 0 ){
    		return new String[0];
    	}else{    	
    		return value.split(regex);
    	}
    }
}
