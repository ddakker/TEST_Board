/**
 * date :2008. 06. 12
 * author: ddakker@naver.com
 */
package dak.web.framework.util;

import java.io.File;

public class FileUtils {
	
	/**
	 * 파일을 삭제 한다.
	 * @param path
	 * @param fileName
	 * @return
	 */
	public static boolean fileDelete(String path, String fileName){
		return fileDelete(new File( path + "/" + fileName ));
	}
	public static boolean fileDelete(File file){
		boolean result = false;
		try{
			result = file.delete();
		}catch(Exception e){
			result = false;
		}		
		return result;
	}
}
