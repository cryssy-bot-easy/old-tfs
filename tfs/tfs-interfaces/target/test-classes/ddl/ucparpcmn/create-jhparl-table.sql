CREATE TABLE UCPARUCMN7."JHPARL" (
		"JLUNIT" DECIMAL(5 , 0) DEFAULT 0 NOT NULL,
		"JLDESC" CHAR(40) DEFAULT ' ' NOT NULL,
		"JLTYPE" CHAR(2) DEFAULT ' ' NOT NULL,
		"JLCONI" CHAR(1) DEFAULT ' ' NOT NULL,
		"JLBRUN" CHAR(1) DEFAULT ' ' NOT NULL,
		"JLCCPC" CHAR(1) DEFAULT ' ' NOT NULL,
		"JLRFBR" DECIMAL(3 , 0) DEFAULT 0 NOT NULL,
		"JLMISC" CHAR(2) DEFAULT ' ' NOT NULL,
		"JLGRP" DECIMAL(3 , 0) DEFAULT 0 NOT NULL,
		"JLLVL" DECIMAL(3 , 0) DEFAULT 0 NOT NULL,
		"JLNLVL" DECIMAL(5 , 0) DEFAULT 0 NOT NULL,
		"JLUIC1" CHAR(1) DEFAULT ' ' NOT NULL,
		"JLUIC2" CHAR(1) DEFAULT ' ' NOT NULL,
		"JLUIC3" CHAR(1) DEFAULT ' ' NOT NULL,
		"JLUIC4" CHAR(1) DEFAULT ' ' NOT NULL,
		"JLRSC1" DECIMAL(3 , 0) DEFAULT 0 NOT NULL,
		"JLRSC2" DECIMAL(3 , 0) DEFAULT 0 NOT NULL,
		"JLRSC3" DECIMAL(3 , 0) DEFAULT 0 NOT NULL,
		"JLRSC4" DECIMAL(3 , 0) DEFAULT 0 NOT NULL,
		"JLUSRN" CHAR(10) DEFAULT ' ' NOT NULL,
		"JLDAT6" DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		"JLTIME" DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		"JLOUNT" DECIMAL(4 , 0) DEFAULT 0 NOT NULL
	);

-- CREATE UNIQUE INDEX "UCPARUCMN2"."JHPARL" ON "UCPARUCMN2"."JHPARL" ("JLUNIT" ASC);
--
-- CREATE INDEX "UCPARUCMN2"."JHPARLL1" ON "UCPARUCMN2"."JHPARL" ("JLRFBR" ASC, "JLRFBR" ASC);