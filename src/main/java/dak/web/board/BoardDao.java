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
	 * 해당 게시판 정보 불러오기
	 * @param boardCode
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map getBoardConfig(String boardCode) throws Exception {
		return (Map) sqlMap.queryForObject("SELECT_BOARD_CONFIG", boardCode);
	}
	
	/**
	 * 게시판종류 리스트 얻어오기
	 * @param boardCode
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List getBoardList() throws Exception {
		return (List) sqlMap.queryForList("SELECT_BOARD_LIST");
	}
	
	
			
	/**
	 * 게시판 글 쓰기 
	 * @param boardCode	게시판 종류
	 * @param id		아이디
	 * @param name		작성자
	 * @param subject	제목
	 * @param content	내용
	 * @param pwd		비밀번호
	 * @param ip		아이피
	 * @param filename	파일명: Map key:업로드 파일명, value:디스크에 저장된 파일명
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
			
			if( b_level == 0 ){	// 쓰기라면
				params.put("b_level", "0");
				params.put("b_ridx",  "0");
			}else{				// 리플이라면
				
				Map params2 = new HashMap();
				params2.put("boardCode", boardCode);
				params2.put("b_list",	 b_list);
				params2.put("b_ridx", 	 b_ridx);
				// 리플 등록 전 증가될 ridx 목록을 구한다.
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
						throw new Exception("파일 목록 INSERT 중 실패");
					}
				}
			}
			
			sqlMap.commitTransaction();
		}catch(Exception e){
			resultSeq = -1;			
			log.error("Exception: " + e);
			throw new Exception("글 쓰기  중 실패");
		}finally{
			sqlMap.endTransaction();			
		}
		return resultSeq;
		
	}
	
	/**
	 * 쓰기를 위한 b_list 값 구하기
	 * @param boardCode
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public long getWriteBeforeInfo(String boardCode) throws Exception {
		return (Long) sqlMap.queryForObject("SELECT_WRITE_BEFORE_INFO", boardCode);
	}
	
	/**
	 * 답변 달기 위한 정보 구하기
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
					throw new IOException("기존 파일에서 삭제 된 요청된 파일 삭제 중 실패:파일 유실 문제 있네 ㅠㅠ");
				}
				
				int delResult = (Integer) sqlMap.delete("DELETE_FILE", deleteFSeqArr[i]);
				if( delResult != 1 ){
					throw new SQLException("기존 파일에서 삭제 된 요청된 파일 DB삭제 중 실패:파일 유실 문제 있네 ㅠㅠ");
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
			
			
			// 새롭게 올려지는 파일 DB에 등록
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
						throw new Exception("파일 목록 INSERT 중 실패");
					}
				}
			}
			
			
			sqlMap.commitTransaction();
		}catch(Exception e){
			log.error("Exception: " + e);
			throw new Exception("글 수정  중 실패");
		}finally{
			sqlMap.endTransaction();
		}
		
		return 0;
	}
	
	/**
	 * 게시판 리스트 정보를 얻어온다.
	 * @param boardCode	게시판 종류
	 * @param pageSize	게시판에 보여질 리스트 갯수
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
	 * 게시판 리스트를 페이징 처리하여 가져온다.
	 * @param boardCode	게시판 종류
	 * @param ofset		리스트 어디서부터
	 * @param length	리스트 몇개까지
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
	 * 특정 글 불러오기
	 * @param seq		특정 글 pk
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List content(String seq) throws Exception {
		return (List) sqlMap.queryForList("SELECT_CONTENT", seq);
	}
	
	/**
	 * 게시글 삭제
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
			
			// 삭제 되는 글에 리플이 있다면 삭제 하면 안되고 삭제 플래그를 1로 업데이트 한다.
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
				throw new Exception("글 DB 삭제 실패");
			}
			
			int fileListSize = fileList.size();
			for( int i=0; i<fileListSize; i++ ){
				String fileName = (String) ((Map)fileList.get(i)).get("f_physicalName");
				
				boolean bResult = FileUtils.fileDelete(upload_path, fileName);
				if( bResult == false ){
					throw new Exception("글 삭제 중 글에 포함된 파일 삭제 중 오류가 발생하였습니다. 파일이 손실 될수 있는 문제..");
				}
			}
		
			sqlMap.commitTransaction();
		}catch(Exception e){
			result = -1;			
			log.error("Exception: " + e);
			throw new Exception("글 삭제  중 실패");
		}finally{
			sqlMap.endTransaction();
		}
		
		return result;
	}
	
	
	
	/**
	 * 특정 글 조회수 증가
	 * @param seq	특정 글 pk
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public int hitIncre(String seq) throws Exception {
		return (Integer) sqlMap.update("UPDATE_CONTENT_HIT_INCRE", seq);
	}
	
	/**
	 * 특정 글의 파일목록 불러오기
	 * @param seq		특정 글 pk
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List content_fileList(String seq) throws Exception {
		return (List) sqlMap.queryForList("SELECT_CONTENT_FILE_LIST", seq);
	}
	
	/**
	 * 댓글 쓰기
	 * @param seq	부모글 PK
	 * @param ment	댓글
	 * @param id	아이디
	 * @param name	이름
	 * @param pwd	비밀번호
	 * @param ip	아이피
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
	 * 댓글 리스트 얻기
	 * @param b_seq	부모 PK
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List mentList(String b_seq) throws Exception {
		
		return (List) sqlMap.queryForList("SELECT_MENT_LIST", b_seq);
	}
	
	/**
	 * 해당 유저 로그인 비밀번호 얻기
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String getUserPWD(String id) throws Exception {
		
		return (String) sqlMap.queryForObject("SELECT_USER_PWD", id);
	}
	
	/**
	 * 해당 댓글 지우기
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
	 * 저장소에 저장된 파일명/실제 파일명 얻기, f_physicalName, f_originalName
	 * @param f_seq
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map getFileName(String f_seq) throws Exception {
		return (Map) sqlMap.queryForObject("SELECT_FILE_NAME", f_seq);
	}
	
	/**
	 * 글 비밀번호 가져오기
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
	 * 댓글 비밀번호 가져오기
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
	 * 게시판 정보 수정하기
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
	 * 게시판 추가하기
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
