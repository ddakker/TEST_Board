
* 사용 프레임웍
   - Struts, iBatis, velocity(AJAX 사용 편의 위해), MultiPardUpload
* UI 사용 프레임웍
   - Struts TagLib, JSTL, EL  
* JS 사용 프레임웍
   - jQuery(core, ui), jindo(Naver OpenSource, Naver SmartEditor 위해 사용) 


* 컨텍스 변경 시 수정 사항
   - /WEB-INF/web.xml  init-param - upload_path 부분 현재 파일 업로드 경로 설정
   - /board/board.js   ROOT_PATH  - 현재 ContextPath명 입력해주기
   - /WEB-INF/src/lnj/web/framework/util/ParamUtils.java - encoding 관련 타입 수정 관련
     ( 단, 파라미터 얻오는 코딩은 무조껀 ParamUtils class를 이용해야 함 )
   - /WEB-INF/src/lnj/web/sqls/SqlMapConfig.xml - DB Connect 정보 설정



* 로그인/비 로그인 기능
* 리플 기능
* 짧은 답변 기능
* 비밀글 등록 기능
* 게시글 검색 기능(제목,내용,작성자)
* 다중 파일 업로드 기능
* 파일 다운로드 기능
* 페이징 기능
* 관리자 모드 제공
   - 게시판 이름 수정 가능
   - 관리자 전용 게시판 가능
   - 로그인 여부에 따라 사용제한 가능
   - 리플 기능 사용 여부 수정 가능
   - 짧은 답변 사용 여부 수정 가능

   