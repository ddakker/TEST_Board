/**
 * date :2008. 06. 12
 * author: ddakker@naver.com
 */
package dak.web.framework.servlet;


import java.io.IOException;
import java.sql.Connection;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import dak.web.framework.util.ParamUtils;


/**
 * @author ddakker
 *
 */
public class ActionServlet extends org.apache.struts.action.ActionServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        preProcess(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        preProcess(request, response);
    }

    public void preProcess(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    	log.info("\n\n\n\n\n========== ActionServlet ==========");
    	
    	String params = ParamUtils.printParameter(request);
    	if( log.isDebugEnabled() ){
    		String ajax = request.getParameter("ajax");
    		try{    			
	    		if( ajax == null ){
	    			params = new String(params.getBytes("8859_1"), "euc-kr");
	    		}else{
	    			params = new String(params.getBytes("8859_1"), "utf-8");
	    		}
    		}catch(Exception e){
    			log.debug("e: " + e);
    		}    		
    		log.debug( params );
		}
    	
    	process(request, response);
    }
}
