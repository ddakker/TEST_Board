/**
 * date :2008. 06. 12
 * author: ddakker@naver.com
 */
package dak.web.framework.util;


public class StringUtils {

	public static String[] split(String str, String regex) {
		String returnStr[] = new String[0];
		if( str != null && str.trim().length() != 0 ){
			returnStr = str.split(regex);
		}
		return returnStr;
	}
	
	public static String nvl(String value, String changeValue) {
		
		return value==null?changeValue:value;
	}
	
	public int realLength(String s) {
        return s.getBytes().length;
    }
	
	public String nLeft(String s, int len, String tail) {
		if (s == null)
			return null;

		int srcLen = realLength(s);
		if (srcLen < len)
			return s;

		String tmpTail = tail;
		if (tail == null)
			tmpTail = "";

		int tailLen = realLength(tmpTail);
		if (tailLen > len)
			return "";

		char a;
		int i = 0;
		int realLen = 0;
		for (i = 0; i < len - tailLen && realLen < len - tailLen; i++) {
			a = s.charAt(i);
			if ((a & 0xFF00) == 0)
				realLen += 1;
			else
				realLen += 2;
		}
		while (realLength(s.substring(0, i)) > len - tailLen) {
			i--;
		}
		return s.substring(0, i) + tmpTail;
	}
		
}
