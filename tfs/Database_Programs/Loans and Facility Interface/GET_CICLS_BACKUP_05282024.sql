--<ScriptOptions statementTerminator="@">
CREATE OR REPLACE PROCEDURE TFSDB2S.GET_CICLS(IN P_DATE TIMESTAMP)
SPECIFIC GET_CICLS
DYNAMIC RESULT SETS 1
LANGUAGE SQL
--######################################################################################################
--# Description:    Retrieves records used for CICLS Handoff File
--# Created by:     Cedrick C. Nungay 
--# Date created:   02/08/2024
--######################################################################################################
--# Input:  P_DATE  :   Process date
--######################################################################################################
P1: BEGIN
    DECLARE C_CICLS CURSOR WITH RETURN FOR
        SELECT A.CIFNO AS "CLIENT_NUMBER",
            CASE WHEN CFINDI = 'Y' 
                            THEN COALESCE(TRIM(B.CFNA1A), '') || ' ' ||
                                    CASE WHEN B.CFNA1B = '' THEN
                                        COALESCE(TRIM(B.CFNA1), '')
                                    ELSE
                                        COALESCE(TRIM(B.CFNA1B),'')  || ' ' || COALESCE(TRIM(B.CFNA1), '')
                                    END
                    WHEN CFINDI = 'N'
                            THEN COALESCE(CL_NAME,
                                    COALESCE(TRIM(B.CFNA1A), '') || ' ' ||
                                    CASE WHEN CFNA1B = '' THEN
                                        COALESCE(TRIM(B.CFNA1), '')
                                    ELSE
                                        COALESCE(TRIM(B.CFNA1B),'')  || ' ' || COALESCE(TRIM(B.CFNA1), '')
                                    END
                                    )
                ELSE ''
                END AS CLIENT_NAME,
            TRIM(CONCAT(REPLACE(B.CFTINN,  '-', ''), '')) AS "TIN_NUMBER",
            '0' AS TRAN_TYPE,
            A.CREATED_DATE PROCESS_DATE,
            C.CICLS_PRODUCT_CODE,
            COALESCE(A.ORGLMT_IRR, 0) AS "APPROVED_AMOUNT",
            COALESCE(A.TOSBAL_IRR, 0) AS "OUTSTANDING_CURRENT",
            0 AS "OUTSTANDING_PAST_DUE"
        FROM TFSDB2S.LNCLST_CICLS A
        LEFT JOIN TFSDB2S.CFMAST B
            ON A.CIFNO = B.CFCIF#
        LEFT JOIN TFSDB2S.PROD_PAYMNT_CICLS_MAPPING C
            ON A.PRODUCT_ID = C.PROD_ID
        LEFT JOIN TFSDB2S.LNAPPF D
            ON A.AFCPNO = D.AFCPNO
            
        UNION ALL

        SELECT A.CIFNO
             , B.CFSNME SHORTNAME
             , TRIM(CONCAT(REPLACE(B.CFTINN,  '-', ''), '')) AS "TIN_NUMBER"
             , '0'
             , A.CREATED_DATE
             , C.CICLS_PRODUCT_CODE
             , COALESCE(A.LORGAMT, 0)
             , COALESCE(A.LPMTAMT, 0)
             , COALESCE(A.OUTSTANDING_PAST_DUE, 0)
        FROM TFSDB2S.LNTFINT_CICLS A
        INNER JOIN TFSDB2S.CFMAST B
            ON A.CIFNO = B.CFCIF#
        LEFT JOIN TFSDB2S.PROD_PAYMNT_CICLS_MAPPING C
            ON A.PRODUCT_ID = C.PROD_ID
        INNER JOIN PAYMENTDETAIL D
            ON A.ACCTNO = D.PNNUMBER
        LEFT JOIN TFSDB2S.LNAPPF E
            ON D.FACILITYREFERENCENUMBER = E.AFCPNO
        WHERE C.CICLS_PRODUCT_CODE <> '300'
        OR C.CICLS_PRODUCT_CODE <> ''
   	    OR C.CICLS_PRODUCT_CODE IS NOT NULL;

    OPEN C_CICLS;
END P1@

CALL TFSDB2S.GET_CICLS(NOW())@