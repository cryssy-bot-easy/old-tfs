--<ScriptOptions statementTerminator="@"/>
CREATE OR REPLACE PROCEDURE TFSDB2S.GENERATE_HOLIDAYS(P_DATE TIMESTAMP)
LANGUAGE SQL
SPECIFIC GENERATE_HOLIDAYS
--######################################################################################################
--# Description: 	Executes by a batch program that generates holidays
--# Created by: 	Cedrick C. Nungay 
--# Date created: 	09/06/2023
--######################################################################################################
--# Input:	P_DATE		:	Date of execution
--######################################################################################################
P1: BEGIN
	FOR REC AS (
		SELECT * FROM (
			SELECT HOLIDAY_ID, CDATE, HOLDAY, BUSDAY, CBRNBR, DESCRIPTION, JLRFID
			, DATE(TIMESTAMP_FORMAT(LPAD(TO_CHAR(CDATE), 6, '0'),'MMDDYY')) HOLIDAY_DATE
			FROM TFSDB2S.JHCLDR WHERE IS_REGULAR = 'Y')
		WHERE TO_CHAR(HOLIDAY_DATE, 'YYYY') = TO_CHAR(P_DATE, 'YYYY')
	) DO
		INSERT INTO TFSDB2S.JHCLDR(CDATE, HOLDAY, CBRNBR, CREATED_BY
			, DESCRIPTION, IS_REGULAR, JLRFID, CREATED_DATE)
		SELECT TO_CHAR(ADD_YEARS(REC.HOLIDAY_DATE, A.NUM), 'MMDDYY'), 'Y', REC.CBRNBR, 'BATCH', REC.DESCRIPTION, 'Y', REC.JLRFID
		FROM TABLE(VALUES(1), (2), (3)) A(NUM)
		WHERE NOT EXISTS(SELECT 1 FROM TFSDB2S.JHCLDR
			WHERE CDATE = TO_CHAR(ADD_YEARS(HOLIDAY_DATE, A.NUM), 'MMDDYY')
				AND DESCRIPTION = REC.DESCRIPTION
				AND JLRFID = REC.JLRFID);
	END FOR;
END P1@
