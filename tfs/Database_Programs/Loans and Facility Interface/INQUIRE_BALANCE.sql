--<ScriptOptions statementTerminator="@">
CREATE OR REPLACE PROCEDURE TFSDB2S.INQUIRE_BALANCE(IN P_CIFNUMBER CHAR(20)
                                                , IN P_FACILITY_CODE CHAR(3)
                                                , IN P_FACILITY_ID DECIMAL(11)
                                                , IN P_TRAN_SEQ_NO DECIMAL(5))
SPECIFIC INQUIRE_BALANCE
LANGUAGE SQL
--######################################################################################################
--# Description:    Saves balance on TFSDB2S.LNTFCON used on balance checking
--# Created by:     Cedrick C. Nungay 
--# Date created:   01/22/2024
--######################################################################################################
--# Input:  P_CIFNUMBER     :   CIF Number
--#         P_FACILITY_CODE :   Facility code
--#         P_FACILITY_ID   :   Facility ID
--#         P_TRAN_SEQ_NO   :   Transaction Sequence number
--######################################################################################################
P1: BEGIN
    DECLARE V_CURRENCY CHAR(4);
    DECLARE V_OUTSTANDING_BAL DECIMAL(15, 2);

    SET (V_CURRENCY) =
        (SELECT A.AFCUR
        FROM TFSDB2S.LNAPPF A WHERE A.AFCIF# = P_CIFNUMBER
            AND A.AFFCDE = P_FACILITY_CODE AND A.AFSEQ = P_FACILITY_ID);

    SET V_OUTSTANDING_BAL = TFSDB2S.GET_BALANCE(P_CIFNUMBER, P_FACILITY_CODE, P_FACILITY_ID);

    INSERT INTO TFSDB2S.LNTFCON (AANO, FCODE, FSEQ, TRSEQ, AVLAMT, TOCUR, TRSTS, CREATED_DATE) 
    VALUES (P_CIFNUMBER, P_FACILITY_CODE, P_FACILITY_ID, P_TRAN_SEQ_NO, V_OUTSTANDING_BAL, V_CURRENCY, 'Y', NOW());
END P1@
