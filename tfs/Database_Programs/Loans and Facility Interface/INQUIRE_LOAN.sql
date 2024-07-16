

--<ScriptOptions statementTerminator="@">

CREATE OR REPLACE PROCEDURE TFSDB2S.INQUIRE_LOAN(IN P_MAIN_CIFNUMBER CHAR(20), IN P_FACILITY_CODE CHAR(3)
    , IN P_FACILITY_ID DECIMAL(11, 0), IN P_TRANSACTION_SEQUENCENUMBER DECIMAL(5, 0), IN P_PNNUMBER DECIMAL(19, 0)
    , IN P_BRANCH_NUMBER DECIMAL(3, 0), IN P_REPORTING_BRANCH DECIMAL(3, 0),  IN P_CURRENCY_TYPE CHAR(4)
    , IN P_CIF_NUMBER CHAR(7), IN P_LOAN_TERM DECIMAL(5, 0), IN P_LOANTERM_CODE CHAR(1), IN P_ORIGINAL_BALANCE DECIMAL(15, 2)
    , IN P_ORIGINAL_LOANDATE DECIMAL(6, 0), IN P_INTEREST_RATE DECIMAL(7, 6), IN P_MATURITY_DATE DECIMAL(6, 0)
    , IN P_PAYMENT_FREQUENCY DECIMAL(5, 0), IN P_PAYMENT_FREQUENCYCODE CHAR(1), IN P_INTPAYMENT_FREQUENCY DECIMAL(5, 0)
    , IN P_INTPAYMENT_FREQUENCYCODE CHAR(1), IN P_GROUP_CODE DECIMAL(3, 0), IN P_DOCUMENT_NUMBER VARCHAR(21)
    , IN P_ORDER_EXPIRYDATE DECIMAL(6, 0), IN P_UNLINK_FLAG CHAR(1), IN P_TRUST_USERID CHAR(10)
    , IN P_CREDITOR_CODE DECIMAL(12, 0), IN P_PAYMENT_CODE DECIMAL(1, 0), IN P_OVERRIDE_FLAG CHAR(1)
    , IN P_AGRIAGRA_TAGGING CHAR(1))
SPECIFIC INQUIRE_LOAN
LANGUAGE SQL
P1: BEGIN

    DECLARE V_LORGAMT_SELL_RATE DECIMAL(19, 2);
    DECLARE V_LORGAMT_IRR DECIMAL(19, 2);
	DECLARE V_INTERMEDIATE_BALANCE DECIMAL(31, 8);
    DECLARE V_SPECIAL_RATE_USD DECIMAL(31, 8);
    DECLARE V_SPECIAL_RATE_THIRD DECIMAL(31, 8);
            
    SET V_SPECIAL_RATE_USD = COALESCE((SELECT CAST(SPECIALRATEUSDTOPHPSERVICECHARGE AS DECIMAL(31, 8)) FROM TRADESERVICE WHERE REPLACE(DOCUMENTNUMBER, '-', '') = REPLACE(P_DOCUMENT_NUMBER, '-', '') AND SERVICETYPE = 'OPENING'), 1);
    SET V_SPECIAL_RATE_THIRD = COALESCE((SELECT CAST(SPECIALRATETHIRDTOUSDSERVICECHARGE AS DECIMAL(31, 8)) FROM TRADESERVICE WHERE REPLACE(DOCUMENTNUMBER, '-', '') = REPLACE(P_DOCUMENT_NUMBER, '-', '') AND SERVICETYPE = 'OPENING'), 1);
    
    IF UPPER(TRIM(P_CURRENCY_TYPE)) = 'PHP' THEN
        SET V_INTERMEDIATE_BALANCE = CAST(P_ORIGINAL_BALANCE AS DECIMAL(31, 8));
    ELSEIF UPPER(TRIM(P_CURRENCY_TYPE)) = 'USD' THEN
        SET V_INTERMEDIATE_BALANCE = CAST(P_ORIGINAL_BALANCE AS DECIMAL(31, 8)) * V_SPECIAL_RATE_USD;
    ELSE
        SET V_INTERMEDIATE_BALANCE = CAST(P_ORIGINAL_BALANCE AS DECIMAL(31, 8)) * V_SPECIAL_RATE_THIRD * V_SPECIAL_RATE_USD;
    END IF;

    SET V_LORGAMT_SELL_RATE = CAST(ROUND(V_INTERMEDIATE_BALANCE, 2) AS DECIMAL(19, 2));

     SET V_LORGAMT_IRR = (
        CASE WHEN UPPER(TRIM(P_CURRENCY_TYPE)) = 'PHP' THEN P_ORIGINAL_BALANCE
            ELSE (P_ORIGINAL_BALANCE * (SELECT JFXDCR FROM TFSDB2S.JHFXDT WHERE JFXDCD = P_CURRENCY_TYPE AND JFXDBC = 'PHP' AND JFXDRN = 3))
        END );

    INSERT INTO TFSDB2S.LNTFINT(AANO, FCODE, FSEQ, TRSEQ, ACCTNO
        , ACTYPE, "BR#", "RBR#", "TYPE", CURTYP, SNAME, CIFNO
        , TERM, TMCODE, ORGAMT, ORGDT6, RATE, PMTAMT
        , CFPDT, DRLIMT, MATDT6, OFFCR, FREQ, FRCODE
        , IPFREQ, IPCODE, GLBOOK, "GROUP", TNUMBR, TIMPOR
        , TAMTOR, TEXP6, TRSTS, TRUNLINK, TRUSERID, CRDTCD, PMTCOD
        , REQSTS, SYSCOD, RS4FLG, LOAN_STATUS, LORGAMT, LORGAMT_IRR)
    VALUES(P_MAIN_CIFNUMBER, P_FACILITY_CODE, P_FACILITY_ID, P_TRANSACTION_SEQUENCENUMBER, P_PNNUMBER
        , ' ', P_BRANCH_NUMBER, P_REPORTING_BRANCH,  ' ', P_CURRENCY_TYPE, ' ', P_CIF_NUMBER
        , P_LOAN_TERM, P_LOANTERM_CODE, P_ORIGINAL_BALANCE, P_ORIGINAL_LOANDATE, P_INTEREST_RATE, 0
        , ' ', P_ORIGINAL_BALANCE, P_MATURITY_DATE, ' ', P_PAYMENT_FREQUENCY, P_PAYMENT_FREQUENCYCODE
        , P_INTPAYMENT_FREQUENCY, P_INTPAYMENT_FREQUENCYCODE, ' ', P_GROUP_CODE, P_DOCUMENT_NUMBER, ' '
        , 0, P_ORDER_EXPIRYDATE, 'Y', P_UNLINK_FLAG, P_TRUST_USERID, P_CREDITOR_CODE, P_PAYMENT_CODE
        , P_OVERRIDE_FLAG, 'TF', P_AGRIAGRA_TAGGING, 'UNPAID'
        , V_LORGAMT_SELL_RATE, V_LORGAMT_IRR);
END P1 