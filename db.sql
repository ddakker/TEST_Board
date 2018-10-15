/*delete from member;
delete from board_config;
delete from board;
delete from board_comment;
delete from board_file;*/


CREATE TABLE `member` (
  `id` varchar(20) NOT NULL default '',
  `pwd` varchar(20) NOT NULL default '',
  `name` varchar(20) NOT NULL default '',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=euckr COMMENT='회원관리';

insert into `member` values ('admin', 'dkdk', '어드민');
insert into `member` values ('ddakker', 'dkdk', '구용');
insert into `member` values ('ddakker0', 'dkdk', '꾸용');

CREATE TABLE `board_config` (
  `b_boardCode` int(11) NOT NULL default '0' COMMENT 'pk',
  `b_boardName` varchar(20) NOT NULL default '' COMMENT '게시판 이름',
  `b_login` int(11) NOT NULL default '0' COMMENT '로그인 유무',
  `b_admin` int(11) NOT NULL default '0' COMMENT '관리자 전용 유무',
  `b_comment` int(11) NOT NULL default '0' COMMENT '댓글 유무',
  `b_reply` int(11) NOT NULL default '0' COMMENT '리플 유무',
  PRIMARY KEY  (`b_boardCode`)
) ENGINE=InnoDB DEFAULT CHARSET=euckr;

insert into board_config values (0, '게시판1', 1, 0, 1, 1);

CREATE TABLE `board` (
  `b_seq` int(11) NOT NULL default '0' COMMENT 'pk',
  `b_list` int(11) NOT NULL default '0' COMMENT '답글정렬 관련',
  `b_level` int(11) NOT NULL default '0' COMMENT '답글정렬 관련',
  `b_ridx` int(11) NOT NULL default '0' COMMENT '답글정렬 관련',
  `b_id` varchar(20) NOT NULL default '' COMMENT '아이디',
  `b_name` varchar(20) NOT NULL default '' COMMENT '이름',
  `b_subject` varchar(200) NOT NULL default '' COMMENT '제목',
  `b_content` text NOT NULL COMMENT '글내용',
  `b_regdate` datetime NOT NULL default '0000-00-00 00:00:00' COMMENT '쓴날짜',
  `b_editdate` datetime NOT NULL default '0000-00-00 00:00:00' COMMENT '수정날짜',
  `b_pwd` varchar(20) NOT NULL default '' COMMENT '비밀번호',
  `b_ip` varchar(15) NOT NULL default '' COMMENT '아이피',
  `b_hit` int(11) NOT NULL default '0' COMMENT '조회수',
  `b_secret` int(11) NOT NULL default '0' COMMENT '비밀글여부',
  `b_delete` int(11) NOT NULL default '0' COMMENT '삭제여부',
  `b_boardCode` int(11) NOT NULL default '0' COMMENT '게시판 구분 코드',
  PRIMARY KEY  (`b_seq`),
  KEY `BOARD_CONFIG_FK` (`b_boardCode`),
  CONSTRAINT `BOARD_CONFIG_FK` FOREIGN KEY (`b_boardCode`) REFERENCES `board_config` (`b_boardCode`)
) ENGINE=InnoDB DEFAULT CHARSET=euckr;


CREATE TABLE `board_comment` (
  `c_seq` int(11) NOT NULL default '0' COMMENT 'pk',
  `c_id` varchar(20) NOT NULL default '' COMMENT '아이디',
  `c_name` varchar(20) NOT NULL default '' COMMENT '이름',
  `c_comment` text NOT NULL COMMENT '내용',
  `c_regdate` datetime NOT NULL default '0000-00-00 00:00:00' COMMENT '쓴날짜',
  `c_pwd` varchar(20) NOT NULL default '' COMMENT '비밀번호',
  `c_ip` varchar(15) NOT NULL default '' COMMENT '아이피',
  `b_seq` int(11) NOT NULL default '0' COMMENT 'fk',
  KEY `BOARD_COMMENT_FK` (`b_seq`),
  CONSTRAINT `BOARD_COMMENT_FK` FOREIGN KEY (`b_seq`) REFERENCES `board` (`b_seq`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=euckr;

CREATE TABLE `board_file` (
  `f_seq` int(11) NOT NULL default '0' COMMENT 'pk',
  `f_originalName` varchar(200) NOT NULL default '' COMMENT '파일명',
  `f_physicalName` varchar(200) NOT NULL default '' COMMENT '실제 저장된 파일명',
  `b_seq` int(11) NOT NULL default '0' COMMENT 'fk',
  KEY `BOARD_FILE_FK` (`b_seq`),
  CONSTRAINT `BOARD_FILE_FK` FOREIGN KEY (`b_seq`) REFERENCES `board` (`b_seq`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=euckr;
