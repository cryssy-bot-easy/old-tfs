--<ScriptOptions statementTerminator="@"/>
CREATE OR REPLACE PROCEDURE TFSDB2S.TRG_PRCD_JHPARL_UPD(IN P_IS_BOC_PROCESSOR CHAR(1)
													, IN P_JLRFBR DECIMAL(3, 0)
													, IN P_IS_BOC_PROCESSOR_OLD CHAR(1))
LANGUAGE SQL
SPECIFIC TRG_PRCD_JHPARL_UPD
--######################################################################################################
--# Description:    Trigger as procedure for updated units
--# Created by:     Cedrick C. Nungay 
--# Date created:   04/04/2023
--######################################################################################################
--# Input:  P_IS_BOC_PROCESSOR      :   BOC Processor flag status
--#         P_JLRFBR                :   Branch code
--#         P_IS_BOC_PROCESSOR_OLD  :   Previous BOC Processor flag status
--######################################################################################################
P1: BEGIN
    DECLARE V_UNIT_TEMPLATE VARCHAR(10);
    DECLARE V_ID BIGINT;

    IF P_IS_BOC_PROCESSOR <> P_IS_BOC_PROCESSOR_OLD AND P_IS_BOC_PROCESSOR = 'Y' THEN
        IF P_JLRFBR <> '111' THEN
            SET V_UNIT_TEMPLATE = '111';
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
    END IF;
END P1@
