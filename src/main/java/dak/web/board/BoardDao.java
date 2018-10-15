/**
 * date :2009. 06. 20
 * author: ddakker@naver.com
 */
package dak.web.board;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibatis.sqlmap.client.SqlMapClient;

import dak.web.framework.dao.SqlMapConfig;
import dak.web.framework.util.FileUtils;

public class BoardDao {
	
	private static Log 			log		 = null;
	private static SqlMapClient sqlMap   = null;
	private static BoardDao 	boardDao = null;
	
	private BoardDao(){}
	
	public static BoardDao getInstance(){
		if( boardDao == null ){
			log      = LogFactory.getLog("dak.web.board.BoardDao");
			sqlMap   = SqlMapConfig.getSqlMapInstance();
			boardDao = new BoardDao();
		}
		return boardDao;
	}
	
	/**
	 * �ش� �Խ��� ���� �ҷ�����
	 * @param boardCode
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map getBoardConfig(String boardCode) throws Exception {
		return (Map) sqlMap.queryForObject("SELECT_BOARD_CONFIG", boardCode);
	}
	
	/**
	 * �Խ������� ����Ʈ ������
	 * @param boardCode
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List getBoardList() throws Exception {
		return (List) sqlMap.queryForList("SELECT_BOARD_LIST");
	}
	
	
			
	/**
	 * �Խ��� �� ���� 
	 * @param boardCode	�Խ��� ����
	 * @param id		���̵�
	 * @param name		�ۼ���
	 * @param subject	����
	 * @param content	����
	 * @param pwd		��й�ȣ
	 * @param ip		������
	 * @param filename	���ϸ�: Map key:���ε� ���ϸ�, value:��ũ�� ����� ���ϸ�
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public long write(String boardCode, long b_list, int b_level, int b_ridx, String id, String name, String subject, String content, String pwd, String ip, String secret, Map fileMap) throws Exception {
		long resultSeq = 0;
		System.out.println("pwd: " + pwd);
		try{
			sqlMap.startTransaction();
			
			Map params = new HashMap();
			params.put("boardCode", boardCode);
			params.put("b_list", 	b_list);
			params.put("id", 		id);
			params.put("name", 		name);
			params.put("subject", 	subject);
			params.put("content", 	content);
			params.put("pwd", 		pwd);
			params.put("ip", 		ip);
			params.put("secret",	secret);
			
			if( b_level == 0 ){	// ������
				params.put("b_level", "0");
				params.put("b_ridx",  "0");
			}else{				// �����̶��
				
				Map params2 = new HashMap();
				params2.put("boardCode", boardCode);
				params2.put("b_list",	 b_list);
				params2.put("b_ridx", 	 b_ridx);
				// ���� ��� �� ������ ridx ����� ���Ѵ�.
				List updateList = (List) sqlMap.queryForList("SELECT_REPLY_BEFORE_UPDATE_INFO", params2);
				int updateListSize = updateList.size();
				for( int i=0; i<updateListSize; i++ ){
					int resultCnt = (Integer) sqlMap.update("UPDATE_REPLY_RIDX_INCRE", ((Map)updateList.get(i)).get("b_seq"));
				}
				
				params.put("b_level", b_level);
				params.put("b_ridx",  b_ridx);				
			}
			resultSeq = (Long) sqlMap.insert("INSERT_WRITE", params);
			
			
			if( fileMap != null ){
				
				Set set = fileMap.keySet();
				Iterator it = set.iterator();
				while( it.hasNext() ){
					String originalFileName	 = (String)it.next();
					String physicalFileName = fileMap.get(originalFileName).toString();
					
					params = new HashMap();
					params.put("originalFileName", originalFileName);
					params.put("physicalFileName", physicalFileName);
					params.put("b_seq", 		   resultSeq);
					
					long resultFSeq = (Long) sqlMap.insert("INSERT_FILE_WRITE", params);
					if( resultFSeq < 0 ){
						throw new Exception("���� ��� INSERT �� ����");
					}
				}
			}
			
			sqlMap.commitTransaction();
		}catch(Exception e){
			resultSeq = -1;			
			log.error("Exception: " + e);
			throw new Exception("�� ����  �� ����");
		}finally{
			sqlMap.endTransaction();			
		}
		return resultSeq;
		
	}
	
	/**
	 * ���⸦ ���� b_list �� ���ϱ�
	 * @param boardCode
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public long getWriteBeforeInfo(String boardCode) throws Exception {
		return (Long) sqlMap.queryForObject("SELECT_WRITE_BEFORE_INFO", boardCode);
	}
	
	/**
	 * �亯 �ޱ� ���� ���� ���ϱ�
	 * @param boardCode
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map getReplyBeforeInfo(String b_seq) throws Exception {
		return (Map) sqlMap.queryForObject("SELECT_REPLY_BEFORE_INFO", b_seq);
	}
	
	@SuppressWarnings("unchecked")
	public long edit(String seq, String name, String subject, String content, String pwd, String ip, String secret, Map fileMap, String [] deleteFSeqArr, String upload_path) throws Exception {
		
		try{
			sqlMap.startTransaction();
			
			int deleteFSeqArrSize = deleteFSeqArr.length;
			for( int i=0; i<deleteFSeqArrSize; i++ ){
				String fileName = (String) ((Map) getFileName(deleteFSeqArr[i])).get("f_physicalName");//(String) sqlMap.queryForObject("SELECT_FILE_NAME", deleteFSeqArr[i]);
				log.debug("--fileName: " + fileName);
				boolean result = FileUtils.fileDelete(upload_path, fileName);
				if( result == false ){
					throw new IOException("���� ���Ͽ��� ���� �� ��û�� ���� ���� �� ����:���� ���� ���� �ֳ� �Ф�");
				}
				
				int delResult = (Integer) sqlMap.delete("DELETE_FILE", deleteFSeqArr[i]);
				if( delResult != 1 ){
					throw new SQLException("���� ���Ͽ��� ���� �� ��û�� ���� DB���� �� ����:���� ���� ���� �ֳ� �Ф�");
				}
			}
			
			Map params = new HashMap();
			params.put("seq", 		seq);
			params.put("name", 		name);
			params.put("subject", 	subject);
			params.put("content", 	content);
			params.put("pwd", 		pwd);
			params.put("ip", 		ip);
			params.put("secret",	secret);
			
			int updateResult = (Integer) sqlMap.update("UPDATE_BOARD", params);
			log.debug("updateResult: " + updateResult);
			
			
			// ���Ӱ� �÷����� ���� DB�� ���
			if( fileMap != null ){
				
				Set set = fileMap.keySet();
				Iterator it = set.iterator();
				while( it.hasNext() ){
					String originalFileName	 = (String)it.next();
					String physicalFileName = fileMap.get(originalFileName).toString();
					
					params = new HashMap();
					params.put("originalFileName", originalFileName);
					params.put("physicalFileName", physicalFileName);
					params.put("b_seq", 		   seq);
					
					long resultFSeq = (Long) sqlMap.insert("INSERT_FILE_WRITE", params);
					if( resultFSeq < 0 ){
						throw new Exception("���� ��� INSERT �� ����");
					}
				}
			}
			
			
			sqlMap.commitTransaction();
		}catch(Exception e){
			log.error("Exception: " + e);
			throw new Exception("�� ����  �� ����");
		}finally{
			sqlMap.endTransaction();
		}
		
		return 0;
	}
	
	/**
	 * �Խ��� ����Ʈ ������ ���´�.
	 * @param boardCode	�Խ��� ����
	 * @param pageSize	�Խ��ǿ� ������ ����Ʈ ����
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map listInfo(String boardCode, int pageSize, String searchKind, String searchStr) throws Exception {
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("boardCode", boardCode);
		params.put("pageSize", 	pageSize);
		
		if( !searchKind.equals("") ){
			params.put("searchKind", searchKind);
			params.put("searchStr",  searchStr);
		}
				
		return (Map) sqlMap.queryForObject("SELECT_LIST_INFO", params);
	}
	
	/**
	 * �Խ��� ����Ʈ�� ����¡ ó���Ͽ� �����´�.
	 * @param boardCode	�Խ��� ����
	 * @param ofset		����Ʈ ��𼭺���
	 * @param length	����Ʈ �����
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List list(String boardCode, int ofset, int length, String searchKind, String searchStr) throws Exception {
		sqlMap   = SqlMapConfig.getSqlMapInstance();
		Map params = new HashMap();
		params.put("boardCode", boardCode);
		params.put("ofset", 	ofset);
		params.put("length", 	length);
		
		if( !searchKind.equals("") ){
			params.put("searchKind", searchKind);
			params.put("searchStr",  searchStr);
		}
		return (List) sqlMap.queryForList("SELECT_LIST", params);
	}
	
	/**
	 * Ư�� �� �ҷ�����
	 * @param seq		Ư�� �� pk
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List content(String seq) throws Exception {
		return (List) sqlMap.queryForList("SELECT_CONTENT", seq);
	}
	
	/**
	 * �Խñ� ����
	 * @param b_seq
	 * @param b_pwd
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Integer delete(String boardCode, String b_seq, String b_pwd, String upload_path) throws Exception {
		int result = 0;
		try{
			
			sqlMap.startTransaction();
			Map params = new HashMap();
			params.put("b_seq", b_seq);
			params.put("b_pwd", b_pwd);
			
			List fileList = content_fileList(b_seq); //(List) sqlMap.queryForList("SELECT_CONTENT_FILE_LIST", b_seq);
			
			// ���� �Ǵ� �ۿ� ������ �ִٸ� ���� �ϸ� �ȵǰ� ���� �÷��׸� 1�� ������Ʈ �Ѵ�.
			Map map = getReplyBeforeInfo(b_seq);
			int b_list  = (Integer) map.get("b_list");
			int b_ridx  = (Integer) map.get("b_ridx");
			b_ridx++;
			
			Map params2 = new HashMap();
			params2.put("boardCode", boardCode);
			params2.put("b_list",	 b_list);
			params2.put("b_ridx", 	 b_ridx);
			List updateList = (List) sqlMap.queryForList("SELECT_REPLY_BEFORE_UPDATE_INFO", params2);
			log.debug("updateList: " + updateList.size());
			if( updateList.size() > 0 ){
				result = (Integer) sqlMap.delete("UPDATE_BOARD_DELETE_FLAG", params);
			}else{
				result = (Integer) sqlMap.delete("DELETE_BOARD", params);
			}
			
			if( result != 1 ){
				throw new Exception("�� DB ���� ����");
			}
			
			int fileListSize = fileList.size();
			for( int i=0; i<fileListSize; i++ ){
				String fileName = (String) ((Map)fileList.get(i)).get("f_physicalName");
				
				boolean bResult = FileUtils.fileDelete(upload_path, fileName);
				if( bResult == false ){
					throw new Exception("�� ���� �� �ۿ� ���Ե� ���� ���� �� ������ �߻��Ͽ����ϴ�. ������ �ս� �ɼ� �ִ� ����..");
				}
			}
		
			sqlMap.commitTransaction();
		}catch(Exception e){
			result = -1;			
			log.error("Exception: " + e);
			throw new Exception("�� ����  �� ����");
		}finally{
			sqlMap.endTransaction();
		}
		
		return result;
	}
	
	
	
	/**
	 * Ư�� �� ��ȸ�� ����
	 * @param seq	Ư�� �� pk
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public int hitIncre(String seq) throws Exception {
		return (Integer) sqlMap.update("UPDATE_CONTENT_HIT_INCRE", seq);
	}
	
	/**
	 * Ư�� ���� ���ϸ�� �ҷ�����
	 * @param seq		Ư�� �� pk
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List content_fileList(String seq) throws Exception {
		return (List) sqlMap.queryForList("SELECT_CONTENT_FILE_LIST", seq);
	}
	
	/**
	 * ��� ����
	 * @param seq	�θ�� PK
	 * @param ment	���
	 * @param id	���̵�
	 * @param name	�̸�
	 * @param pwd	��й�ȣ
	 * @param ip	������
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public long mentWrite(String seq, String ment, String id, String name, String pwd, String ip) throws Exception {
		Map params = new HashMap();
		params.put("b_seq",  seq);
		params.put("ment", ment);
		params.put("id", 	 id);
		params.put("name", name);
		params.put("pwd",  pwd);
		params.put("ip", 	 ip);		
		
		return (Long) sqlMap.insert("INSERT_MENT_WRITE", params);
	}
	
	/**
	 * ��� ����Ʈ ���
	 * @param b_seq	�θ� PK
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List mentList(String b_seq) throws Exception {
		
		return (List) sqlMap.queryForList("SELECT_MENT_LIST", b_seq);
	}
	
	/**
	 * �ش� ���� �α��� ��й�ȣ ���
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String getUserPWD(String id) throws Exception {
		
		return (String) sqlMap.queryForObject("SELECT_USER_PWD", id);
	}
	
	/**
	 * �ش� ��� �����
	 * @param c_seq
	 * @param c_pwd
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public int mentDelete(String c_seq, String c_pwd) throws Exception {
		
		Map params = new HashMap();
		params.put("c_seq", c_seq);
		params.put("c_pwd", c_pwd);
		
		return (Integer) sqlMap.delete("DELETE_COMMENT", params);
	}
	
	/**
	 * ����ҿ� ����� ���ϸ�/���� ���ϸ� ���, f_physicalName, f_originalName
	 * @param f_seq
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map getFileName(String f_seq) throws Exception {
		return (Map) sqlMap.queryForObject("SELECT_FILE_NAME", f_seq);
	}
	
	/**
	 * �� ��й�ȣ ��������
	 * @param b_seq
	 * @param b_pwd
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String getBoardPwd(String b_seq) throws Exception {
		
		return (String) sqlMap.queryForObject("SELECT_GET_BOARD_PWD", b_seq);
	}
	
	/**
	 * ��� ��й�ȣ ��������
	 * @param b_seq
	 * @param b_pwd
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String getMentPwd(String c_seq) throws Exception {
		
		return (String) sqlMap.queryForObject("SELECT_GET_MENT_PWD", c_seq);
	}
	
	/**
	 * �Խ��� ���� �����ϱ�
	 * @param b_boardCode
	 * @param b_boardName
	 * @param b_login
	 * @param b_admin
	 * @param b_comment
	 * @param b_reply
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public int boardConfigEdit(String b_boardCode, String b_boardName, String b_login, String b_admin, String b_comment, String b_reply) throws Exception {
		
		Map params = new HashMap();
		params.put("b_boardCode", 	b_boardCode);
		params.put("b_boardName", 	b_boardName);
		params.put("b_login", 		b_login);
		params.put("b_admin", 		b_admin);
		params.put("b_comment", 	b_comment);
		params.put("b_reply", 		b_reply);
		
		return (Integer) sqlMap.update("UPDATE_BOARD_CONFIG", params);
	}
	
	/**
	 * �Խ��� �߰��ϱ�
	 * @param b_boardCode
	 * @param b_boardName
	 * @param b_login
	 * @param b_admin
	 * @param b_comment
	 * @param b_reply
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public long boardConfigAdd(String b_boardName, String b_login, String b_admin, String b_comment, String b_reply) throws Exception {
		
		Map params = new HashMap();
		params.put("b_boardName", 	b_boardName);
		params.put("b_login", 		b_login);
		params.put("b_admin", 		b_admin);
		params.put("b_comment", 	b_comment);
		params.put("b_reply", 		b_reply);
		
		return (Long) sqlMap.insert("INSERT_BOARD_CONFIG_ADD", params);
	}
	
}
