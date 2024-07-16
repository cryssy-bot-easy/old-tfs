
CREATE TABLE UCPARUCMN.JHFXPR (
		JFXSEQ DECIMAL(3 , 0) DEFAULT 0 NOT NULL,
		JFXRDS CHAR(32) DEFAULT ' ' NOT NULL,
		JFXMNT CHAR(1) DEFAULT ' ' NOT NULL,
		JFXBOS CHAR(1) DEFAULT ' ' NOT NULL,
		JHVUSR CHAR(10) DEFAULT ' ' NOT NULL,
		JHVDT6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		JHVDT7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		JHVTME DECIMAL(6 , 0) DEFAULT 0 NOT NULL
	);

DELETE FROM UCPARUCMN.JHFXPR;

INSERT INTO UCPARUCMN.JHFXPR (JFXSEQ,JFXRDS,JFXMNT,JFXBOS,JHVUSR,JHVDT6,JHVDT7,JHVTME) VALUES (1,'BANK NOTE BUYING RATE           ',' ',' ','          ',0,0,0);
INSERT INTO UCPARUCMN.JHFXPR (JFXSEQ,JFXRDS,JFXMNT,JFXBOS,JHVUSR,JHVDT6,JHVDT7,JHVTME) VALUES (2,'BANK NOTE SELL/INVISIBLES       ',' ',' ','          ',0,0,0);
INSERT INTO UCPARUCMN.JHFXPR (JFXSEQ,JFXRDS,JFXMNT,JFXBOS,JHVUSR,JHVDT6,JHVDT7,JHVTME) VALUES (3,'URR - BOOKING RATE              ',' ',' ','          ',0,0,0);
INSERT INTO UCPARUCMN.JHFXPR (JFXSEQ,JFXRDS,JFXMNT,JFXBOS,JHVUSR,JHVDT6,JHVDT7,JHVTME) VALUES (11,'DEMAND DRAFT BUY/EBP EXPORTS BUY',' ',' ','          ',0,0,0);
INSERT INTO UCPARUCMN.JHFXPR (JFXSEQ,JFXRDS,JFXMNT,JFXBOS,JHVUSR,JHVDT6,JHVDT7,JHVTME) VALUES (12,'DEMAND DRAFT SELL/LC CASH SELL  ',' ',' ','          ',0,0,0);
INSERT INTO UCPARUCMN.JHFXPR (JFXSEQ,JFXRDS,JFXMNT,JFXBOS,JHVUSR,JHVDT6,JHVDT7,JHVTME) VALUES (13,'COLL /TT/ FCDU W/DRAWAL BUYING  ',' ',' ','          ',0,0,0);
INSERT INTO UCPARUCMN.JHFXPR (JFXSEQ,JFXRDS,JFXMNT,JFXBOS,JHVUSR,JHVDT6,JHVDT7,JHVTME) VALUES (14,'TT SELL /LC REGULAR SELL        ',' ',' ','          ',0,0,0);
INSERT INTO UCPARUCMN.JHFXPR (JFXSEQ,JFXRDS,JFXMNT,JFXBOS,JHVUSR,JHVDT6,JHVDT7,JHVTME) VALUES (15,'DAILY BASE RATE                 ',' ',' ','          ',0,0,0);
INSERT INTO UCPARUCMN.JHFXPR (JFXSEQ,JFXRDS,JFXMNT,JFXBOS,JHVUSR,JHVDT6,JHVDT7,JHVTME) VALUES (16,'EXPORTS COLL/TT BUY RATE        ',' ',' ','          ',0,0,0);
INSERT INTO UCPARUCMN.JHFXPR (JFXSEQ,JFXRDS,JFXMNT,JFXBOS,JHVUSR,JHVDT6,JHVDT7,JHVTME) VALUES (17,'LC CASH SELL RATE               ',' ',' ','          ',0,0,0);
INSERT INTO UCPARUCMN.JHFXPR (JFXSEQ,JFXRDS,JFXMNT,JFXBOS,JHVUSR,JHVDT6,JHVDT7,JHVTME) VALUES (18,'EOD REVALUATION RATE            ',' ',' ','          ',0,0,0);
INSERT INTO UCPARUCMN.JHFXPR (JFXSEQ,JFXRDS,JFXMNT,JFXBOS,JHVUSR,JHVDT6,JHVDT7,JHVTME) VALUES (19,'EXPORTS BUY RATE                ',' ',' ','          ',0,0,0);
