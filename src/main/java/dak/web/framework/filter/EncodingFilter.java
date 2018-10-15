/**
 * date :2008. 06. 12
 * author: ddakker@naver.com
 */
package dak.web.framework.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import dak.web.framework.util.ParamUtils;


/**
 * @author ddakker
 *
 */
public class EncodingFilter implements Filter {
	private String encoding = null;
	
	protected FilterConfig filterConfig = null;
	
	public void destroy() {	
		this.encoding = null;
		this.filterConfig = null;	
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
	
		if (request.getCharacterEncoding() == null) {
			String encoding = selectEncoding(request);
			//String ajax     = ParamUtils.getParameter((HttpServletRequest)request, "ajax", "0");

			// ajax로 올라오는 경우 HTTPRequest가 UTF-8만 지원되니.. UTF-8로 변경
			//if( ajax.equals("1") )
			//	encoding = "UTF-8";
			
			if (encoding != null)
				request.setCharacterEncoding(encoding);
		}		
		chain.doFilter(request, response);
	}

	public void init(FilterConfig filterConfig) throws ServletException {

		this.filterConfig = filterConfig;
		this.encoding = filterConfig.getInitParameter("encoding");
	}

	protected String selectEncoding(ServletRequest request) {
		return (this.encoding);
	}

	public FilterConfig getFilterConfig() {
		return filterConfig;
	}

	public void setFilterConfig(FilterConfig cfg) {
		filterConfig = cfg;
	}
} 