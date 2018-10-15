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
	 * Y : �⵵�� ��Ī
	 * M : ���� ��Ī
	 * W : �ָ� ��Ī
	 * D : ���� ��Ī
	 * HH : ���� / ����
	 * HH24 : 24�ð�
	 * MI : ��
	 * SS : ��
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
