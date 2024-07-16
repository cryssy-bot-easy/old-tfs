--<ScriptOptions statementTerminator="@"/>
CREATE OR REPLACE PROCEDURE TFSDB2S.TRG_PRCD_JHPARL_NEW(IN P_IS_BOC_PROCESSOR CHAR(1)
													, IN P_JLRFBR DECIMAL(3, 0)
													, IN P_JLTYPE VARCHAR(2)
													, IN P_JLRFID SMALLINT)
LANGUAGE SQL
SPECIFIC TRG_PRCD_JHPARL_NEW
--######################################################################################################
--# Description:    Trigger as procedure for new units
--# Created by:     Cedrick C. Nungay 
--# Date created:   04/04/2023
--######################################################################################################
--# Input:  P_IS_BOC_PROCESSOR  :   BOC Processor flag status
--#         P_JLRFBR            :   Branch code
--#         P_JLTYPE            :   Branch Type
--#         P_JLRFID            :   Branch ID
--######################################################################################################
P1: BEGIN
    DECLARE V_UNIT_TEMPLATE VARCHAR(10);
    DECLARE V_ID BIGINT;

    IF UCASE(P_IS_BOC_PROCESSOR) = 'Y' THEN
        SET V_UNIT_TEMPLATE = '111';
    END IF;

    IF V_UNIT_TEMPLATE IS NOT NULL AND P_JLRFBR <> '111' THEN
        INSERT INTO TFSDB2S.GLMAST(BRANCH, ACCTNO, GMCTYP, BOOKCD, TITLE, SHORTT, ACTYPE, VALPST)
        SELECT P_JLRFBR, ACCTNO, GMCTYP, BOOKCD, TITLE, SHORTT, ACTYPE, VALPST
        FROM TFSDB2S.GLMAST A
        WHERE BRANCH = V_UNIT_TEMPLATE
            AND NOT EXISTS(SELECT 1 FROM TFSDB2S.GLMAST
                WHERE BRANCH = P_JLRFBR AND ACCTNO = A.ACCTNO AND GMCTYP = A.GMCTYP AND BOOKCD = A.BOOKCD);

        IF NOT EXISTS(SELECT 1 FROM DOC_NUM_SEQUENCE WHERE UNIT_CODE = P_JLRFBR AND SEQUENCE_YEAR = TO_CHAR(NOW, 'YYYY')) THEN
            SET V_ID = (SELECT MAX(ID) FROM DOC_NUM_SEQUENCE);
            FOR REC AS (SELECT DOCUMENT_TYPE FROM DOC_NUM_SEQUENCE
                    WHERE UNIT_CODE = V_UNIT_TEMPLATE
                    AND SEQUENCE_YEAR = TO_CHAR(NOW, 'YYYY')) DO
                SET V_ID = V_ID + 1;
                INSERT INTO DOC_NUM_SEQUENCE(ID, DOCUMENT_TYPE, SEQUENCE, UNIT_CODE, SEQUENCE_YEAR)
                VALUES(V_ID, REC.DOCUMENT_TYPE, 1, P_JLRFBR, TO_CHAR(NOW, 'YYYY'));
            END FOR;
        END IF;
    END IF;

	IF P_JLTYPE = 'BR' THEN
	    -- CREATE REGULAR HOLIDAYS FOR THE UNIT
	    FOR REC AS (
	        SELECT * FROM (
	            SELECT DISTINCT CDATE, DESCRIPTION
	            , DATE(TIMESTAMP_FORMAT(LPAD(TO_CHAR(CDATE), 6, '0'),'MMDDYY')) HOLIDAY_DATE
	            FROM TFSDB2S.JHCLDR WHERE IS_REGULAR = 'Y' AND HOLDAY = 'Y')
	        WHERE TO_CHAR(HOLIDAY_DATE, 'YYYY') = TO_CHAR(NOW(), 'YYYY')
	    ) DO
	        INSERT INTO TFSDB2S.JHCLDR(CDATE, HOLDAY, CBRNBR, CREATED_BY
	            , DESCRIPTION, IS_REGULAR, JLRFID)
	        SELECT TO_CHAR(ADD_YEARS(REC.HOLIDAY_DATE, A.NUM), 'MMDDYY'), 'Y', P_JLRFBR, 'UNITTRIGGER', REC.DESCRIPTION, 'Y', P_JLRFID
	        FROM TABLE(VALUES(0), (1), (2), (3)) A(NUM);
	    END FOR;
    END IF;
END P1@
