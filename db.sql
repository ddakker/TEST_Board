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
) ENGINE=InnoDB DEFAULT CHARSET=euckr COMMENT='ȸ������';

insert into `member` values ('admin', 'dkdk', '����');
insert into `member` values ('ddakker', 'dkdk', '����');
insert into `member` values ('ddakker0', 'dkdk', '�ٿ�');

CREATE TABLE `board_config` (
  `b_boardCode` int(11) NOT NULL default '0' COMMENT 'pk',
  `b_boardName` varchar(20) NOT NULL default '' COMMENT '�Խ��� �̸�',
  `b_login` int(11) NOT NULL default '0' COMMENT '�α��� ����',
  `b_admin` int(11) NOT NULL default '0' COMMENT '������ ���� ����',
  `b_comment` int(11) NOT NULL default '0' COMMENT '��� ����',
  `b_reply` int(11) NOT NULL default '0' COMMENT '���� ����',
  PRIMARY KEY  (`b_boardCode`)
) ENGINE=InnoDB DEFAULT CHARSET=euckr;

insert into board_config values (0, '�Խ���1', 1, 0, 1, 1);

CREATE TABLE `board` (
  `b_seq` int(11) NOT NULL default '0' COMMENT 'pk',
  `b_list` int(11) NOT NULL default '0' COMMENT '������� ����',
  `b_level` int(11) NOT NULL default '0' COMMENT '������� ����',
  `b_ridx` int(11) NOT NULL default '0' COMMENT '������� ����',
  `b_id` varchar(20) NOT NULL default '' COMMENT '���̵�',
  `b_name` varchar(20) NOT NULL default '' COMMENT '�̸�',
  `b_subject` varchar(200) NOT NULL default '' COMMENT '����',
  `b_content` text NOT NULL COMMENT '�۳���',
  `b_regdate` datetime NOT NULL default '0000-00-00 00:00:00' COMMENT '����¥',
  `b_editdate` datetime NOT NULL default '0000-00-00 00:00:00' COMMENT '������¥',
  `b_pwd` varchar(20) NOT NULL default '' COMMENT '��й�ȣ',
  `b_ip` varchar(15) NOT NULL default '' COMMENT '������',
  `b_hit` int(11) NOT NULL default '0' COMMENT '��ȸ��',
  `b_secret` int(11) NOT NULL default '0' COMMENT '��бۿ���',
  `b_delete` int(11) NOT NULL default '0' COMMENT '��������',
  `b_boardCode` int(11) NOT NULL default '0' COMMENT '�Խ��� ���� �ڵ�',
  PRIMARY KEY  (`b_seq`),
  KEY `BOARD_CONFIG_FK` (`b_boardCode`),
  CONSTRAINT `BOARD_CONFIG_FK` FOREIGN KEY (`b_boardCode`) REFERENCES `board_config` (`b_boardCode`)
) ENGINE=InnoDB DEFAULT CHARSET=euckr;


CREATE TABLE `board_comment` (
  `c_seq` int(11) NOT NULL default '0' COMMENT 'pk',
  `c_id` varchar(20) NOT NULL default '' COMMENT '���̵�',
  `c_name` varchar(20) NOT NULL default '' COMMENT '�̸�',
  `c_comment` text NOT NULL COMMENT '����',
  `c_regdate` datetime NOT NULL default '0000-00-00 00:00:00' COMMENT '����¥',
  `c_pwd` varchar(20) NOT NULL default '' COMMENT '��й�ȣ',
  `c_ip` varchar(15) NOT NULL default '' COMMENT '������',
  `b_seq` int(11) NOT NULL default '0' COMMENT 'fk',
  KEY `BOARD_COMMENT_FK` (`b_seq`),
  CONSTRAINT `BOARD_COMMENT_FK` FOREIGN KEY (`b_seq`) REFERENCES `board` (`b_seq`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=euckr;

CREATE TABLE `board_file` (
  `f_seq` int(11) NOT NULL default '0' COMMENT 'pk',
  `f_originalName` varchar(200) NOT NULL default '' COMMENT '���ϸ�',
  `f_physicalName` varchar(200) NOT NULL default '' COMMENT '���� ����� ���ϸ�',
  `b_seq` int(11) NOT NULL default '0' COMMENT 'fk',
  KEY `BOARD_FILE_FK` (`b_seq`),
  CONSTRAINT `BOARD_FILE_FK` FOREIGN KEY (`b_seq`) REFERENCES `board` (`b_seq`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=euckr;
