/**
 * date :2009. 06. 20
 * author: ddakker@naver.com
 */
package dak.web.board;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dak.web.framework.action.MappingDispatchAction;
import dak.web.framework.util.CookieBox;
import dak.web.framework.util.FileUtils;
import dak.web.framework.util.ParamUtils;
import dak.web.framework.util.StringUtils;
import net.sf.json.JSONObject;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

public class BoardAction extends MappingDispatchAction {
	
	private Log log;
	public BoardAction(){
		log = LogFactory.getLog(getClass());
	}
	
	/**
	 * �� ����
	 * @param mapping
	 * @param recieve
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ActionForward write(ActionMapping mapping, ActionForm recieve, HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{ 
			
			String id 		 = new CookieBox(request).getValue("ID");
			id    = StringUtils.nvl(id, "");
			
			String b_seq	  = ParamUtils.getParameter(request, "b_seq");		// ���� �̸� ����
			
			int    nowPage    = ParamUtils.getParameterInt(request, "nowPage", "1");
			
			String boardCode  = ParamUtils.getParameter(request, "boardCode");
			String name 	  = ParamUtils.getParameter(request, "name");
			String subject 	  = ParamUtils.getParameter(request, "subject");
			String content 	  = ParamUtils.getParameter(request, "content");
			String pwd 		  = ParamUtils.getParameter(request, "pwd");
			String secret	  = ParamUtils.getParameter(request, "secret", "0");
			String ip 		  = request.getRemoteAddr();
			
			//name    = new String(name.getBytes("8859_1"),    "euc-kr");
			//subject = new String(subject.getBytes("8859_1"), "euc-kr");
			//content = new String(content.getBytes("8859_1"), "euc-kr");
			
			if( id != null && !id.equals("") ){
				pwd = BoardDao.getInstance().getUserPWD(id);
			}
			
			String [] originalFileNameArr = ParamUtils.getParameterComma(request, "originalFileNameArr", ",");
			String [] physicalFileNameArr = ParamUtils.getParameterComma(request, "physicalFileNameArr", ",");

			Map fileMap = null;
			int size = originalFileNameArr.length;
			if( size > 0 ){
				fileMap = new HashMap();
				
				String sessionID = request.getRequestedSessionId();
				long   currentTimeMillis = System.currentTimeMillis();
				
				ServletConfig config = this.getServlet();
				String upload_path    = config.getInitParameter("upload_path");
				
				for( int i=0; i<size; i++ ){
					String originalFileName = originalFileNameArr[i];
					String physicalFileName = physicalFileNameArr[i];
					
					//originalFileName = new String(originalFileName.getBytes("8859_1"),    "euc-kr");
					//physicalFileName = new String(physicalFileName.getBytes("8859_1"),    "euc-kr");
															
					String lastName = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();
					String saveName = sessionID + "_" + currentTimeMillis + "_" + i + lastName;
					
					if( lastName.equals(".jsp") || lastName.equals(".php") || lastName.equals(".asp") || lastName.equals(".apsx") || lastName.equals(".c") || lastName.equals(".cp") ){
						throw new Exception("�÷����� �ȵǴ� Ȯ���ڰ� �ö����");
					}
					
					File fileMove1 = new File( upload_path + "/temp/" + physicalFileName );
					File fileMove2 = new File( upload_path + "/" + saveName );
					boolean result = fileMove1.renameTo(fileMove2);
					
					if( result == false ){
						throw new Exception("�ӽ��������� ���ε� ������ �ű�� �� ����");
					}
					
					fileMap.put(originalFileName, saveName);
				}
			}
			
			BoardDao dao = BoardDao.getInstance();
			
			long b_list  = 0;
			int  b_level = 0;
			int  b_ridx  = 0;
			if( b_seq.equals("") ){	// ������
				b_list    = dao.getWriteBeforeInfo(boardCode);
			}else{					// �����̶��
				Map map = dao.getReplyBeforeInfo(b_seq);
				b_list  = (Integer) map.get("b_list");
				b_level = (Integer) map.get("b_level");
				b_ridx  = (Integer) map.get("b_ridx");
				
				b_level++;
				b_ridx++;
			}
			
			long returnSeq = dao.write(boardCode, b_list, b_level, b_ridx, id, name, subject, content, pwd, ip, secret, fileMap);
			log.debug("action returnSeq: " + returnSeq);
			if( returnSeq == 0 ) 	   throw new SQLException("�۾��� ����: DB INSERT ERROR�� ���� �ʾ����� ���������� INSERT�� ���� �ʾҴ�.");
			else if( returnSeq == -1)  throw new SQLException("�۾��� ����: DAO ���� ��������.");
			
			return new ActionForward("list.do?boardCode=" + boardCode + "&nowPage=" + nowPage, true); //mapping.findForward("success");
		}catch(Exception e){
			log.error("== ExceptionError " + e);
			request.setAttribute("message", "");
			request.setAttribute("exception", e);
			
			return mapping.findForward("error");
		}
	}
	
	/**
	 * �� ����
	 * @param mapping
	 * @param recieve
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ActionForward edit(ActionMapping mapping, ActionForm recieve, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		try{
			
			ServletConfig config = this.getServlet();
			String upload_path    = config.getInitParameter("upload_path");
			
			String id 		 = new CookieBox(request).getValue("ID");
			id    = StringUtils.nvl(id, "");
			
			String boardCode  = ParamUtils.getParameter(request, "boardCode");
			int    nowPage    = ParamUtils.getParameterInt(request, "nowPage", "1");
			
			String b_seq	  = ParamUtils.getParameter(request, "b_seq");
			String name 	  = ParamUtils.getParameter(request, "name");
			String subject 	  = ParamUtils.getParameter(request, "subject");
			String content 	  = ParamUtils.getParameter(request, "content");
			String pwd 		  = ParamUtils.getParameter(request, "pwd");
			String secret	  = ParamUtils.getParameter(request, "secret", "0");
			String ip 		  = request.getRemoteAddr();
			
			//name    = new String(name.getBytes("8859_1"),    "euc-kr");
			//subject = new String(subject.getBytes("8859_1"), "euc-kr");
			//content = new String(content.getBytes("8859_1"), "euc-kr");
			
			if( id != null && !id.equals("") ){
				pwd = BoardDao.getInstance().getUserPWD(id);
			}
			
			String [] originalFileNameArr = ParamUtils.getParameterComma(request, "originalFileNameArr", ",");	// ���� �߰��� ����(���� ��)
			String [] physicalFileNameArr = ParamUtils.getParameterComma(request, "physicalFileNameArr", ",");	// ���� �߰��� ����(����� ��)
			String [] deleteFSeqArr  	  = ParamUtils.getParameterComma(request, "deleteFSeqArr", ",");	// ���� ���� �� ���� �� ���
									
			Map fileMap = null;
			int size = originalFileNameArr.length;
			if( size > 0 ){
				fileMap = new HashMap();
				
				String sessionID = request.getRequestedSessionId();
				long   currentTimeMillis = System.currentTimeMillis();
				
				for( int i=0; i<size; i++ ){
					String originalFileName = originalFileNameArr[i];
					String physicalFileName = physicalFileNameArr[i];
					
					//originalFileName = new String(originalFileName.getBytes("8859_1"),    "euc-kr");
					//physicalFileName = new String(physicalFileName.getBytes("8859_1"),    "euc-kr");
										
					String lastName = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();
					String saveName = sessionID + "_" + currentTimeMillis + "_" + i + lastName;
					
					if( lastName.equals(".jsp") || lastName.equals(".php") || lastName.equals(".asp") || lastName.equals(".apsx") || lastName.equals(".c") || lastName.equals(".cp") ){
						throw new Exception("�÷����� �ȵǴ� Ȯ���ڰ� �ö����");
					}
					
					File fileMove1 = new File( upload_path + "/temp/" + physicalFileName );
					File fileMove2 = new File( upload_path + "/" + saveName );
					boolean result = fileMove1.renameTo(fileMove2);
					
					if( result == false ){
						throw new Exception("�ӽ��������� ���ε� ������ �ű�� �� ����");
					}
					
					fileMap.put(originalFileName, saveName);
				}
			}
						
			BoardDao.getInstance().edit(b_seq, name, subject, content, pwd, ip, secret, fileMap, deleteFSeqArr, upload_path);			
			
			//request.setAttribute("totalCount", totalCount);			
			
			return new ActionForward("list.do?boardCode=" + boardCode + "&nowPage=" + nowPage, true); //mapping.findForward("success");			
		}catch(Exception e){
			log.error("== ExceptionError " + e);
			request.setAttribute("message", "");
			request.setAttribute("exception", e);
			
			return mapping.findForward("error");
		}
	}
	
	/**
	 * �� ��� �ҷ�����
	 * @param mapping
	 * @param recieve
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ActionForward list(ActionMapping mapping, ActionForm recieve, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		try{
			
			int    pageSize  = 10;
			
			String boardCode  = ParamUtils.getParameter(request, "boardCode");
			int    nowPage    = ParamUtils.getParameterInt(request, "nowPage", "1");
			
			String searchKind = ParamUtils.getParameter(request, "searchKind");
			String searchStr  = "%" + ParamUtils.getParameter(request, "searchStr") + "%";
			
			log.debug("boardCode: " + boardCode);
			log.debug("nowPage: " + nowPage);
			log.debug("searchKind: " + searchKind);
			log.debug("searchStr: " + searchStr);
			
		
			int ofset  = ((nowPage-1) * pageSize);
			int length = pageSize;
			
			BoardDao dao = BoardDao.getInstance();
			
			Map fileMap = new HashMap();
			Map  listInfo = dao.listInfo(boardCode, pageSize, searchKind, searchStr);			
			List listTemp = dao.list(boardCode, ofset, length, searchKind, searchStr);
			
			long totalCount = Long.parseLong( listInfo.get("TOTAL_COUNT").toString() );
			long totalPage  = Long.parseLong( listInfo.get("TOTAL_PAGE").toString() );

			List list        = new ArrayList();
			int nowNum       = (int) (totalCount - ((nowPage-1) * pageSize));
			int listTempSize = listTemp.size();
			for( int i=0; i<listTempSize; i++ ){
				
				Map map = (Map) listTemp.get(i);
				map.put("num", nowNum);
				
				//JSONObject jsonMap = JSONObject.fromObject(map);
				//map.put("jsonMap", jsonMap);
				
				list.add( map );
				
				nowNum--;
			}
			//JSONArray listJson = JSONArray.fromObject(list);
			request.setAttribute("totalCount", totalCount);			
			request.setAttribute("totalPage",  totalPage);
			request.setAttribute("list",  	   list);
			log.debug("1111");
			return mapping.findForward("success");			
		}catch(Exception e){
			log.error("== ExceptionError " + e);
			request.setAttribute("message", "");
			request.setAttribute("exception", e);
			
			return mapping.findForward("error");
		}
	}
	
	/**
	 * Ư�� �Ѱ��� �� �ҷ�����(��������)
	 * @param mapping
	 * @param recieve
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ActionForward content(ActionMapping mapping, ActionForm recieve, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		try{
			
			String seq 		 = ParamUtils.getParameter(request, "seq");
									
			List content  = BoardDao.getInstance().content(seq);
			List fileList = BoardDao.getInstance().content_fileList(seq);
			//if( true ) throw new Exception();
			BoardDao.getInstance().hitIncre(seq);
			
			
			// Ajax ó����� �ش� VM�� ���ؼ� XML����
			if( ParamUtils.getParameterInt(request, "ajax") == 1 ){
				request.setAttribute("totalItemCount", content.size());
				request.setAttribute("list",   content);
				
				request.setAttribute("totalItemCount2", fileList.size());
				request.setAttribute("list2",   fileList);
				
				return mapping.findForward("successVM");
			}else{
				request.setAttribute("content",   (Map)content.get(0));
				request.setAttribute("fileList",  fileList);
				
				return mapping.findForward("success");
			}
				
			
		}catch(Exception e){
			log.error("== ExceptionError " + e);
			request.setAttribute("message", "");
			request.setAttribute("exception", e);
			
			return mapping.findForward("errorVM");
		}
	}
	
	/**
	 * �Խñ� ����
	 * @param mapping
	 * @param recieve
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ActionForward delete(ActionMapping mapping, ActionForm recieve, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		try{
			
			ServletConfig config = this.getServlet();
			String upload_path    = config.getInitParameter("upload_path");
			
			String admin	 = new CookieBox(request).getValue("ADMIN");
			String id 		 = new CookieBox(request).getValue("ID");
			admin = StringUtils.nvl(admin, "");
			id    = StringUtils.nvl(id, "");
			
			String boardCode = ParamUtils.getParameter(request, "boardCode");
			String nowPage	 = ParamUtils.getParameter(request, "nowPage");			
			String b_seq 	 = ParamUtils.getParameter(request, "b_seq");
			String b_pwd 	 = ParamUtils.getParameter(request, "b_pwd");
			
			nowPage = nowPage.equals("")?"1":nowPage;
			
			if( admin.equals("1") ){
				b_pwd = null;
			}else{
				// �α��� ����� �� ��� UI�󿡼� �Է� ���� �����Ƿ� ��й�ȣ ���ؼ� �����Ѵ�.
				if( id != null && !id.equals("") ){
					b_pwd = BoardDao.getInstance().getBoardPwd(b_seq);
				}
			}
			
			int resultCnt = BoardDao.getInstance().delete(boardCode, b_seq, b_pwd, upload_path);
			if( resultCnt != 1 ){
				throw new Exception("�� ���� ����");
			}
			return new ActionForward("list.do?boardCode=" + boardCode + "&nowPage=" + nowPage, true);	
			
		}catch(Exception e){
			log.error("== ExceptionError " + e);
			request.setAttribute("message", "");
			request.setAttribute("exception", e);
			
			return mapping.findForward("error");
		}
	}
	
	/**
	 * ��� ����
	 * @param mapping
	 * @param recieve
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ActionForward mentWrite(ActionMapping mapping, ActionForm recieve, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		try{
			
			String id 		 = new CookieBox(request).getValue("ID");
			id = StringUtils.nvl(id, "");
			
			String b_seq 	 = ParamUtils.getParameter(request, "seq");
			String ment 	 = ParamUtils.getParameter(request, "ment");
			String name 	 = ParamUtils.getParameter(request, "name");
			String pwd 		 = ParamUtils.getParameter(request, "pwd");
			String ip 		 = request.getRemoteAddr();
			
			//ment = new String(ment.getBytes("8859_1"), "utf-8");
			//name = new String(name.getBytes("8859_1"), "utf-8");
			
			// �α��� ����� �� ��� UI�󿡼� �Է� ���� �����Ƿ� ��й�ȣ ���ؼ� �����Ѵ�.
			if( id != null && !id.equals("") ){
				pwd = BoardDao.getInstance().getUserPWD(id);
			}
			
			long c_seq  = BoardDao.getInstance().mentWrite(b_seq, ment, id, name, pwd, ip);
			
			request.setAttribute("totalItemCount", c_seq);
			
			return mapping.findForward("successVM");
			
		}catch(Exception e){
			log.error("== ExceptionError " + e);
			request.setAttribute("message", "");
			request.setAttribute("exception", e);
			
			return mapping.findForward("error");
		}
	}
	
	/**
	 * ��� ����Ʈ �ҷ�����
	 * @param mapping
	 * @param recieve
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ActionForward mentList(ActionMapping mapping, ActionForm recieve, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		try{
			
			String b_seq 	 = ParamUtils.getParameter(request, "b_seq");
			List list  = BoardDao.getInstance().mentList(b_seq);
			
			request.setAttribute("totalItemCount", list.size());
			request.setAttribute("list", 		   list);
			
			return mapping.findForward("successVM");
			
		}catch(Exception e){
			log.error("== ExceptionError " + e);
			request.setAttribute("message", e.getMessage());
			request.setAttribute("exception", e);//detailMessage	"������"	

			
			return mapping.findForward("errorVM");
		}
	}
	
	/**
	 * ��� ����
	 * @param mapping
	 * @param recieve
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ActionForward mentDelete(ActionMapping mapping, ActionForm recieve, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		try{
			
			String admin	 = new CookieBox(request).getValue("ADMIN");
			String id 		 = new CookieBox(request).getValue("ID");
			admin = StringUtils.nvl(admin, "");
			id = StringUtils.nvl(id, "");
			
			String c_seq 	 = ParamUtils.getParameter(request, "c_seq");
			String c_pwd 	 = ParamUtils.getParameter(request, "c_pwd");
			
			if( admin.equals("1") ){
				c_pwd = null;
			}else{
				// �α��� ����� �� ��� UI�󿡼� �Է� ���� �����Ƿ� ��й�ȣ ���ؼ� �����Ѵ�.
				if( id != null && !id.equals("") ){
					c_pwd = BoardDao.getInstance().getMentPwd(c_seq);
				}
			}
			
			int resultCnt = BoardDao.getInstance().mentDelete(c_seq, c_pwd);
			
			request.setAttribute("totalItemCount", resultCnt);
			
			return mapping.findForward("successVM");
			
		}catch(Exception e){
			log.error("== ExceptionError " + e);
			request.setAttribute("message", e.getMessage());
			request.setAttribute("exception", e);//detailMessage	"������"	

			
			return mapping.findForward("errorVM");
		}
	}
	
	/**
	 * ���� ���ε�(�ܰ�)
	 * @param mapping
	 * @param recieve
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ActionForward fileUpload(ActionMapping mapping, ActionForm recieve, HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			
			ServletConfig config = this.getServlet();
			String upload_path    = config.getInitParameter("upload_path") + "/temp";
			String upload_maxSize = config.getInitParameter("upload_maxSize");
			
			String boardCode = request.getParameter("boardCode");
			
			MultipartRequest multi = new MultipartRequest(request, upload_path, (Integer.parseInt(upload_maxSize)*1024*1024), "euc-kr", new DefaultFileRenamePolicy());
			
			String boardCode2 = multi.getParameter("file1");
			
			String physicalFileName = null;
			String originalFileName = null;
			for (Enumeration e = multi.getFileNames(); e.hasMoreElements() ; ){
				String name = (String)e.nextElement();
				physicalFileName = multi.getFilesystemName(name);
				originalFileName = multi.getOriginalFileName(name);
			}
			
			String lastName = physicalFileName.substring(physicalFileName.lastIndexOf(".")).toLowerCase();
			if( lastName.equals(".jsp") || lastName.equals(".php") || lastName.equals(".asp") || lastName.equals(".apsx") || lastName.equals(".c") || lastName.equals(".cp") ){				
				boolean result = FileUtils.fileDelete(upload_path, physicalFileName);
				if( result == false) throw new Exception("���Ͼ��ε� ��.. ���ȿ� ���� �Ǵ� ���� ���� �� ����");
				
				request.setAttribute("errorCode", "1");
			}else{
				request.setAttribute("errorCode", "0");
			}
			
			
			request.setAttribute("physicalFileName", physicalFileName);
			request.setAttribute("originalFileName", originalFileName);
			return mapping.findForward("success");
			
		}catch(Exception e){
			log.error("== ExceptionError " + e);
			request.setAttribute("message", e.getMessage());
			request.setAttribute("exception", e);	

			
			return mapping.findForward("error");
		}
	}
	
	/**
	 * ���� ����(�ٰ� ����)
	 * @param mapping
	 * @param recieve
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ActionForward fileDelete(ActionMapping mapping, ActionForm recieve, HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			int resultCnt = 0;
			
			ServletConfig config = this.getServlet();
			String upload_path   = config.getInitParameter("upload_path");
			
			String [] fileName = ParamUtils.getParameterComma(request, "fileNameArr", ",");
			String [] fileKind = ParamUtils.getParameterComma(request, "fileKindArr", ",");
			
			int fileNameSize = fileName.length;
			int fileKindSize = fileKind.length;
									
			// ���� ������ �ö���� ���(�� ���� �� �ӽ� ������ ����� ���)
			if( fileNameSize > 0 ){
				for( int i=0; i<fileNameSize; i++ ){
					String physicalFileName = fileName[i];
					//physicalFileName = new String(physicalFileName.getBytes("8859_1"),    "utf-8");
					
					boolean result = false;
					if( fileKindSize != 0 && fileKind[i].equals("db") ){
						result = FileUtils.fileDelete(upload_path, physicalFileName);
					}else{
						result = FileUtils.fileDelete(upload_path + "/temp", physicalFileName);
					}
					
					if( result ){
						resultCnt++;
					}else{
						throw new Exception("���� ���� �� ����");
					}
				}
			}
			
			request.setAttribute("totalItemCount", resultCnt);
			
			return mapping.findForward("successVM");
			
		}catch(Exception e){
			log.error("== ExceptionError " + e);
			request.setAttribute("message", e.getMessage());
			request.setAttribute("exception", e);	

			
			return mapping.findForward("errorVM");
		}
	}
	
	/**
	 * ��й�ȣ üũ
	 * @param mapping
	 * @param recieve
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ActionForward getBoardPwd(ActionMapping mapping, ActionForm recieve, HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			
			String b_seq = ParamUtils.getParameter(request, "b_seq");
			String b_pwd = ParamUtils.getParameter(request, "b_pwd");
						
			String db_pwd = BoardDao.getInstance().getBoardPwd(b_seq);			
			
			String result = "0";
			if( db_pwd.equals(b_pwd) ){
				result = "1";
			}
			request.setAttribute("totalItemCount", result);
			
			return mapping.findForward("successVM");
			
		}catch(Exception e){
			log.error("== ExceptionError " + e);
			request.setAttribute("message", e.getMessage());
			request.setAttribute("exception", e);	

			
			return mapping.findForward("errorVM");
		}
	}
	
	/**
	 * ���� �ٿ�ε�
	 * @param mapping
	 * @param recieve
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ActionForward fileDownLoad(ActionMapping mapping, ActionForm recieve, HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ServletConfig config = this.getServlet();
			String upload_path    = config.getInitParameter("upload_path");
			
			String f_seq = ParamUtils.getParameter(request, "f_seq");
			
			Map fileMap = BoardDao.getInstance().getFileName(f_seq);
			
			String f_physicalName = (String) fileMap.get("f_physicalName");
			String f_originalName = (String) fileMap.get("f_originalName");
			f_originalName = new String(f_originalName.getBytes("euc-kr"),"8859_1");
			
			
			response.setContentType("application/octet-stream"); 
			 
			String Agent = request.getHeader("USER-AGENT");


			if(Agent.indexOf("MSIE")>=0){
				int i=Agent.indexOf('M',2);//�ι�° 'M'�ڰ� �ִ� ��ġ
				String IEV = Agent.substring(i+5,i+8);

				if(IEV.equalsIgnoreCase("5.5")){
					response.setHeader("Content-Disposition", "filename="+f_originalName);
				}else{
					response.setHeader("Content-Disposition", "attachment;filename="+f_originalName); 
				}
			}else{
				response.setHeader("Content-Disposition", "attachment;filename="+f_originalName);
			}
			
			
			byte b[] = new byte[1 * 1024];	// 1KB 
			File file = new File(upload_path+"/"+f_physicalName);
			if (file.isFile()){  
				try{					
					BufferedInputStream fin = new BufferedInputStream(new FileInputStream(file));  
					BufferedOutputStream outs = new BufferedOutputStream(response.getOutputStream());

					int read = 0;  
					while ((read = fin.read(b)) != -1){  
						outs.write(b,0,read);
					}
					outs.flush();
					outs.close();  
					fin.close(); 
				}catch(Exception e){
					if( log.isDebugEnabled() ){
						log.debug("EXCEPTION: DownLoadAction.execute()");
						log.debug("EXCEPTION MSG: " + e);
					}
				}
			}
			
			return mapping.findForward("success");
		}catch(Exception e){
			//log.error("== ExceptionError " + e);
			request.setAttribute("message", e.getMessage());
			request.setAttribute("exception", e);	

			
			return mapping.findForward("error");
		}
	}
	
	/**
	 * �Խ��� ���� ��������
	 * @param mapping
	 * @param recieve
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ActionForward boardList(ActionMapping mapping, ActionForm recieve, HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			
			String ADMIN 		 = new CookieBox(request).getValue("ADMIN");
			if( !ADMIN.equals("1") ){
				throw new Exception("��.. �����ڸ� ���� �� ����");
			}
			
			List boardList = BoardDao.getInstance().getBoardList();
			
			request.setAttribute("totalItemCount", boardList.size());
			request.setAttribute("boardList", 	   boardList);
			
			return mapping.findForward("success");
		}catch(Exception e){
			log.error("== ExceptionError " + e);
			request.setAttribute("message", e.getMessage());
			request.setAttribute("exception", e);
			
			return mapping.findForward("error");
		}
	}
	
	/**
	 * �Խ��� ���� ���� �ϱ�
	 * @param mapping
	 * @param recieve
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ActionForward boardConfigEdit(ActionMapping mapping, ActionForm recieve, HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			
			String ADMIN 		 = new CookieBox(request).getValue("ADMIN");
			if( !ADMIN.equals("1") ){
				throw new Exception("��.. �����ڸ� ���� �� ����");
			}
			
			String b_boardCode 	= ParamUtils.getParameter(request, "b_boardCode");
			String b_boardName 	= ParamUtils.getParameter(request, "b_boardName");
			String b_login 	    = ParamUtils.getParameter(request, "b_login", "0");
			String b_admin 	    = ParamUtils.getParameter(request, "b_admin", "0");
			String b_comment 	= ParamUtils.getParameter(request, "b_comment", "0");
			String b_reply 		= ParamUtils.getParameter(request, "b_reply", "0");
			
			//b_boardName    = new String(b_boardName.getBytes("8859_1"),    "euc-kr");
			
			int result = BoardDao.getInstance().boardConfigEdit(b_boardCode, b_boardName, b_login, b_admin, b_comment, b_reply);
			if( result != 1 ){
				throw new Exception("�Խ��� ���� ���� �� ����..");
			}
			
			return mapping.findForward("success");
		}catch(Exception e){
			log.error("== ExceptionError " + e);
			request.setAttribute("message", e.getMessage());
			request.setAttribute("exception", e);
			
			return mapping.findForward("error");
		}
	}
	
	/**
	 * �Խ��� �߰��ϱ�
	 * @param mapping
	 * @param recieve
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ActionForward boardConfigAdd(ActionMapping mapping, ActionForm recieve, HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			
			String ADMIN 		 = new CookieBox(request).getValue("ADMIN");
			if( !ADMIN.equals("1") ){
				throw new Exception("��.. �����ڸ� ���� �� ����");
			}
			
			String b_boardName 	= ParamUtils.getParameter(request, "b_boardName");
			String b_login 	    = ParamUtils.getParameter(request, "b_login", "0");
			String b_admin 	    = ParamUtils.getParameter(request, "b_admin", "0");
			String b_comment 	= ParamUtils.getParameter(request, "b_comment", "0");
			String b_reply 		= ParamUtils.getParameter(request, "b_reply", "0");
			
			long result = BoardDao.getInstance().boardConfigAdd(b_boardName, b_login, b_admin, b_comment, b_reply);
			if( result < 0 ){
				throw new Exception("�Խ��� �߰� �� ����..");
			}
			
			return mapping.findForward("success");
		}catch(Exception e){
			log.error("== ExceptionError " + e);
			request.setAttribute("message", e.getMessage());
			request.setAttribute("exception", e);
			
			return mapping.findForward("error");
		}
	}
	
	public ActionForward test1(ActionMapping mapping, ActionForm recieve, HttpServletRequest request, HttpServletResponse response) throws Exception {
		log.debug("test1");
		//Thread.sleep(5000);
		log.debug("test11");
		return mapping.findForward("errorVM");
	}
	
	public ActionForward test2(ActionMapping mapping, ActionForm recieve, HttpServletRequest request, HttpServletResponse response) throws Exception {
		log.debug("test2");
		Thread.sleep(10000);
		log.debug("test22");
		return mapping.findForward("errorVM");
	}
}

