/**
 * date :2008. 06. 12
 * author: ddakker@naver.com
 */
package dak.web.framework.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
	
	/**
	 * flag : 
	 * Y : 년도를 지칭
	 * M : 월을 지칭
	 * W : 주를 지칭
	 * D : 일을 지칭
	 * HH : 오전 / 오후
	 * HH24 : 24시간
	 * MI : 분
	 * SS : 초
	 * YYYYMMDDHH24MISS
	 * 
	 * @param date
	 * @param s
	 * @return
	 */
	public static String dateFormatter(Date date, String s)
	{
		s = s.toLowerCase();
		int i = s.indexOf("hh24");
		if(i != -1)
			s = s.substring(0, i) + "HH" + s.substring(i + 4);
		
		i = s.indexOf("mm");
		if(i != -1)
			s = s.substring(0, i) + "MM" + s.substring(i + 2);
		
		i = s.indexOf("mi");
		if(i != -1)
			s = s.substring(0, i) + "mm" + s.substring(i + 2);
		return new SimpleDateFormat(s).format(date);
	}
    
}
