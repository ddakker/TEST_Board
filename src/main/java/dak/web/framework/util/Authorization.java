/**
 * date :2008. 06. 12
 * author: ddakker@naver.com
 */
package dak.web.framework.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import dak.web.framework.user.UserProfile;


/**
 * @author ddakker
 *
 */
public class Authorization {
	HttpSession session = null;
	
	public Authorization(HttpServletRequest request) {
		
		session = request.getSession(true);
		
	}
	
	public Boolean isAnonymous() {
		
		UserProfile userProfile = (UserProfile) session.getAttribute("LOGIN");
		
		if( userProfile == null ){			
			return false;
		}else{
			
			return true;
		}
		
	}
	
	public UserProfile getUserProfile() {
		
		UserProfile userProfile = (UserProfile) session.getAttribute("LOGIN");
		
		return userProfile;
		
	}
	
	public void setProfile(UserProfile uProfile) {
		
		session.setAttribute("LOGIN", uProfile);
	}
}
