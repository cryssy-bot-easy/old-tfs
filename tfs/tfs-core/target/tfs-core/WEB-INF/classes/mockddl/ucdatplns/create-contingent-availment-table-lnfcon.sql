CREATE TABLE UCDATPLNS.LNTFCON (
		AANO CHAR(20) DEFAULT ' ' NOT NULL,
		FCODE CHAR(3) DEFAULT ' ' NOT NULL,
  	    FSEQ DECIMAL(11 , 0) DEFAULT 0 NOT NULL,
	    TRSEQ DECIMAL(5 , 0) DEFAULT 0 NOT NULL,
		AVLAMT DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		TOCUR CHAR(4) DEFAULT ' ' NOT NULL,
		TRSTS CHAR(1) DEFAULT ' ' NOT NULL,
		TRERR CHAR(40) DEFAULT ' ' NOT NULL
	);
	
CREATE TABLE UCDATULNS2.LNTFCON (
		AANO CHAR(20) DEFAULT ' ' NOT NULL,
		FCODE CHAR(3) DEFAULT ' ' NOT NULL,
  	    FSEQ DECIMAL(11 , 0) DEFAULT 0 NOT NULL,
	    TRSEQ DECIMAL(5 , 0) DEFAULT 0 NOT NULL,
		AVLAMT DECIMAL(15 , 2) DEFAULT 0 NOT NULL,
		TOCUR CHAR(4) DEFAULT ' ' NOT NULL,
		TRSTS CHAR(1) DEFAULT ' ' NOT NULL,
		TRERR CHAR(40) DEFAULT ' ' NOT NULL
	);