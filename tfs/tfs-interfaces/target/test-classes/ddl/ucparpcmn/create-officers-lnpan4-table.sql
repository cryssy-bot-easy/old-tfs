CREATE TABLE UCPARUCMN7."LNPAN4" (
		"POFFCD" CHAR(3) DEFAULT ' ' NOT NULL,
		"PGDOBD" CHAR(1) DEFAULT ' ' NOT NULL,
		"PUCOD" DECIMAL(5 , 0) DEFAULT 0 NOT NULL,
		"PBRN" DECIMAL(3 , 0) DEFAULT 0 NOT NULL,
		"PLUSID" CHAR(10) DEFAULT ' ' NOT NULL,
		"PMNT6" DECIMAL(6 , 0) DEFAULT 0 NOT NULL,
		"PMNT7" DECIMAL(7 , 0) DEFAULT 0 NOT NULL,
		"PNVTME" DECIMAL(6 , 0) DEFAULT 0 NOT NULL
	);

CREATE UNIQUE INDEX UCPARUCMN7."LNPAN4" ON UCPARUCMN7."LNPAN4" ("POFFCD" ASC, "PGDOBD" ASC);