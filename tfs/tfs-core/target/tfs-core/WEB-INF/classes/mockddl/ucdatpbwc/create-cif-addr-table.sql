
	
	
CREATE TABLE UCDATUBWC7.CFADDR (
		"CFCIF#" CHAR(7) DEFAULT ' ' NOT NULL,
		CFASEQ DECIMAL(3 , 0) DEFAULT 0 NOT NULL,
		CFNA2 CHAR(40) DEFAULT ' ' NOT NULL,
		CFNA3 CHAR(40) DEFAULT ' ' NOT NULL,
		CFNA4 CHAR(40) DEFAULT ' ' NOT NULL,
		CFNA5 CHAR(40) DEFAULT ' ' NOT NULL,
		CFZIP DECIMAL(9 , 0) DEFAULT 0 NOT NULL,
		CFFORN CHAR(1) DEFAULT ' ' NOT NULL,
		CFADSC CHAR(20) DEFAULT ' ' NOT NULL,
		CFUSE CHAR(1) DEFAULT ' ' NOT NULL,
		CFINVC CHAR(1) DEFAULT ' ' NOT NULL,
		CFMAIL CHAR(1) DEFAULT ' ' NOT NULL,
		CFAYRS DECIMAL(3 , 0) DEFAULT 0 NOT NULL,
		CFAYRC CHAR(1) DEFAULT ' ' NOT NULL,
		CFAPTY CHAR(5) DEFAULT ' ' NOT NULL,
		CFADLM DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		CFADL6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		CFADI7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		CFADI6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		CFADR7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		CFADR6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		CFVUSR CHAR(10) DEFAULT ' ' NOT NULL,
		CFVDT6 DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		CFVDT7 DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		CFVTME DECIMAL(6 , 0) DEFAULT 0 NOT NULL
	);
	
	
  INSERT INTO UCDATUBWC7.CFADDR ("CFCIF#",CFASEQ,CFNA2,CFNA3,CFNA4) VALUES ('1234567',1,'8017 TANGUELI ST.                       ','SAN ANOTONIO                            ','MAKATI CITY                             ');
INSERT INTO UCDATUBWC7.CFADDR ("CFCIF#",CFASEQ,CFNA2,CFNA3,CFNA4) VALUES ('2345342',2,'8017 TANGUELI ST SAN ANOTIO             ','PEMBO                                   ','MAKATI CITY                             ');
INSERT INTO UCDATUBWC7.CFADDR ("CFCIF#",CFASEQ,CFNA2,CFNA3,CFNA4) VALUES ('3234567',12,'8017 TANGUELI ST.                       ','SAN ANOTONIO                            ','MAKATI CITY                             ');
INSERT INTO UCDATUBWC7.CFADDR ("CFCIF#",CFASEQ,CFNA2,CFNA3,CFNA4) VALUES ('9806767',13,'#21 LOWER BRGY. TO-ONG                  ','CEBU CITY                               ','                                        ');