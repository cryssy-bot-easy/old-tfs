--<ScriptOptions statementTerminator="@"/>
CREATE OR REPLACE TRIGGER TFSDB2S.TRG_GLIS_NEW
AFTER INSERT ON INT_ACCENTRYACTUAL
REFERENCING NEW AS ACCENTRYACTUAL
FOR EACH ROW
--######################################################################################################
--# Description:    Trigger on creation of INT_ACCENTRYACTUAL record that generates GLIS records
--# Created by:     Cedrick C. Nungay 
--# Date created:   04/04/2023
--######################################################################################################
BEGIN ATOMIC
    DECLARE V_COMPANY_NUMBER VARCHAR(4);
    DECLARE V_LBP_ACCOUNTINGCODE VARCHAR(50);
    DECLARE V_PARTICULARS VARCHAR(100);
    DECLARE V_TRANSACTION_CODE CHAR(2);
    DECLARE V_RESP_CTR_NUMBER VARCHAR(10);
    DECLARE V_JLRFID SMALLINT;
    DECLARE V_TRAN_DES_DOCUMENT_NUMBER VARCHAR(100);
    DECLARE V_REMARKS VARCHAR(500);
    DECLARE V_UNITCODE VARCHAR(10);

    IF ACCENTRYACTUAL.STATUS IN ('APPROVED','PRE_APPROVED','POST_APPROVED','POSTED', 'EXPIRED','REINSTATED') AND ACCENTRYACTUAL.POSTINGDATE IS NULL THEN
        SET V_COMPANY_NUMBER = (SELECT CODE FROM TFSDB2S.COMPANY_NUMBERS A
            WHERE A.BOOKCODE = ACCENTRYACTUAL.BOOKCODE AND A.BOOKCURRENCY = ACCENTRYACTUAL.BOOKCURRENCY);
        SET V_LBP_ACCOUNTINGCODE = (SELECT LBP_ACCOUNTINGCODE FROM TFSDB2S.REF_GLMAPPING
                WHERE ACCOUNTINGCODE = ACCENTRYACTUAL.ACCOUNTINGCODE
                    AND BOOKCODE = ACCENTRYACTUAL.BOOKCODE
                    AND BOOKCURRENCY = ACCENTRYACTUAL.BOOKCURRENCY);
        SET V_PARTICULARS = CASE WHEN LOCATE('|', ACCENTRYACTUAL.PARTICULARS) != 0 THEN
                LEFT(ACCENTRYACTUAL.PARTICULARS, LOCATE('|', ACCENTRYACTUAL.PARTICULARS) - 1)
            ELSE ACCENTRYACTUAL.PARTICULARS END;
        SET V_TRANSACTION_CODE = CASE WHEN TRIM(ACCENTRYACTUAL.CONTINGENTFLAG) = '*MEMO' THEN
                DECODE(UCASE(ACCENTRYACTUAL.ENTRYTYPE), 'DEBIT', '71', '72')
            ELSE DECODE(UCASE(ACCENTRYACTUAL.ENTRYTYPE), 'DEBIT', '61', '62') END;
        SET V_UNITCODE = (SELECT UNITCODE FROM CDTPAYMENTREQUEST
            WHERE IEDIEIRDNO = (SELECT TRADESERVICEREFERENCENUMBER FROM TRADESERVICE WHERE TRADESERVICEID = ACCENTRYACTUAL.TRADESERVICEID));
        SET (V_JLRFID, V_RESP_CTR_NUMBER) = (SELECT JLRFID, LBP_RESP_CTR_NUMBER FROM TFSDB2S.JHPARL
                WHERE JLRFBR = (CASE WHEN ACCENTRYACTUAL.PRODUCTID = 'CDT' THEN COALESCE(DECODE(V_UNITCODE, '', NULL, V_UNITCODE), '909') ELSE '909' END));
        SET V_TRAN_DES_DOCUMENT_NUMBER = CASE WHEN ACCENTRYACTUAL.DOCUMENTNUMBER IS NULL OR ACCENTRYACTUAL.DOCUMENTNUMBER = ''
                THEN (SELECT TRADESERVICEREFERENCENUMBER FROM TRADESERVICE
                    WHERE TRADESERVICEID = ACCENTRYACTUAL.TRADESERVICEID)
                ELSE ACCENTRYACTUAL.DOCUMENTNUMBER END;
        IF NOT EXISTS(SELECT 1 FROM TFSDB2S.REF_GLMAPPING
                WHERE ACCOUNTINGCODE = ACCENTRYACTUAL.ACCOUNTINGCODE
                    AND BOOKCODE = ACCENTRYACTUAL.BOOKCODE
                    AND BOOKCURRENCY = ACCENTRYACTUAL.BOOKCURRENCY) THEN
            SET V_REMARKS = 'NOT MAPPED: ' || ACCENTRYACTUAL.ACCOUNTINGCODE;
        ELSEIF NOT TRIM(ACCENTRYACTUAL.CONTINGENTFLAG) = '*MEMO' 
                AND (SELECT SUM(DECODE(UCASE(ENTRYTYPE), 'DEBIT', ORIGINALAMOUNT, ORIGINALAMOUNT * -1))
                    FROM INT_ACCENTRYACTUAL WHERE GLTSNUMBER = ACCENTRYACTUAL.GLTSNUMBER AND CONTINGENTFLAG <> '*MEMO') <> 0 THEN
            SET V_REMARKS = 'Total debit and credit were not balanced.';
        END IF;

        INSERT INTO TFSDB2S.GLIS_HAND_OFF_FILE(
            COMPANY_NUMBER,
            ACCOUNT_NUMBER,
            UCPB_ACCOUNTING_CODE,
            UCPB_PARTICULARS,
            TRANSACTION_CODE,
            RESP_CTR_NUMBER,
            SOURCE_CODE,
            AMOUNT,       
            PESOAMOUNT,       
            DATE,
            REF_TRAN_SEQ_NUMBER,
            TRAN_DES_DOCUMENT_NUMBER,
            CREATED_DATE,
            REMARKS,
            JLRFID,
            IS_APPROVED,
            ACCENTRY_ID)
        VALUES(
            V_COMPANY_NUMBER,
            V_LBP_ACCOUNTINGCODE,
            ACCENTRYACTUAL.ACCOUNTINGCODE,
            V_PARTICULARS,
            V_TRANSACTION_CODE,
            V_RESP_CTR_NUMBER,
            'TR1',
            ACCENTRYACTUAL.ORIGINALAMOUNT,       
            ACCENTRYACTUAL.PESOAMOUNT,       
            NOW,
            ACCENTRYACTUAL.GLTSNUMBER,
            V_TRAN_DES_DOCUMENT_NUMBER,
            NOW,
            V_REMARKS,
            V_JLRFID,
            'N',
            ACCENTRYACTUAL.ID);
    END IF;
END
