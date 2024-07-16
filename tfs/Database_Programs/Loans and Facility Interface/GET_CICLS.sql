--<ScriptOptions statementTerminator="@">
CREATE OR REPLACE PROCEDURE TFSDB2S.GET_CICLS(IN P_DATE TIMESTAMP)
SPECIFIC GET_CICLS
DYNAMIC RESULT SETS 1
LANGUAGE SQL
--######################################################################################################
--# Description:    Retrieves records used for CICLS Handoff File
--# Created by:     Cedrick C. Nungay 
--# Date created:   02/08/2024
--#
--######################################################################################################
--# Input:  P_DATE  :   Process date
--######################################################################################################

P1: BEGIN
    DECLARE C_CICLS CURSOR WITH RETURN FOR
    	WITH TEMP_HEADER AS (
            SELECT * FROM 
            (
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
										'250' AS "CICLS_PRODUCT_CODE",
					COALESCE(A.ORGLMT_IRR, 0) AS "APPROVED_AMOUNT",
					COALESCE(A.TOSBAL_IRR, 0) AS "OUTSTANDING_CURRENT",
					0 AS "OUTSTANDING_PAST_DUE",
					D.AFFAMT AS "FACILITY_CREDIT_LIMIT",
					A.AFCPNO AS "FACILITY_REF_NUMBER",
					D.AFFCDE AS "FACILITY_CODE",
					' ' AS "LOAN_AVAILMENT_ID"
					, 'C' AS "FLAG"
				FROM TFSDB2S.LNCLST_CICLS A
				LEFT JOIN TFSDB2S.CFMAST B
					ON A.CIFNO = B.CFCIF#
				LEFT OUTER JOIN TFSDB2S.LNAPPF D
					ON A.AFCPNO = D.AFCPNO
					AND A.CIFNO = D.AFAPNO
				UNION ALL
				SELECT A.CIFNO
					, CASE WHEN CFINDI = 'Y' 
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
						END AS CLIENT_NAME
					,TRIM(CONCAT(REPLACE(B.CFTINN,  '-', ''), '')) AS "TIN_NUMBER", '0'
					, A.CREATED_DATE
					, C.CICLS_PRODUCT_CODE
					, COALESCE(A.LORGAMT, 0) AS "APPROVED_AMOUNT"
					, COALESCE(A.LORGAMT, 0) AS "OUTSTANDING_CURRENT"
					, COALESCE(A.OUTSTANDING_PAST_DUE, 0)
					, E.AFFAMT AS "FACILITY_CREDIT_LIMIT"
					, D.FACILITYREFERENCENUMBER
					, E.AFFCDE AS "FACILITY_CODE"
					, A.LOAN_AVAILMENT_ID
					, 'L' AS "FLAG"
				FROM TFSDB2S.LNTFINT_CICLS A
				INNER JOIN TFSDB2S.CFMAST B
					ON A.CIFNO = B.CFCIF#
				INNER JOIN PAYMENTDETAIL D
					ON A.ACCTNO = D.PNNUMBER
				LEFT JOIN TFSDB2S.PROD_PAYMNT_CICLS_MAPPING C
				    ON D.PAYMENTINSTRUMENTTYPE = C.MOP_PRODUCT
				LEFT OUTER JOIN TFSDB2S.LNAPPF E
					ON D.FACILITYREFERENCENUMBER = E.AFCPNO
					AND A.FCODE = E.AFFCDE
					AND A.AANO = E.AFAPNO
				,(SELECT ACCTNO, MAX(TRSEQ) AS TRSEQ FROM TFSDB2S.LNTFINT_CICLS
                        WHERE TRUNLINK = 'N'
                        GROUP BY ACCTNO
                          ) FILTER_ACCTNO
                        WHERE A.ACCTNO = FILTER_ACCTNO.ACCTNO
                        AND A.TRSEQ = FILTER_ACCTNO.TRSEQ
            )
            	WHERE CICLS_PRODUCT_CODE <> '300'
				OR CICLS_PRODUCT_CODE = ''
				OR CICLS_PRODUCT_CODE IS NULL
        ),
		TEMP_DETAILS AS (
				SELECT *
				FROM (
				SELECT 	CLIENT_NUMBER
						, CLIENT_NAME
						, TIN_NUMBER
						, TRAN_TYPE
						, PROCESS_DATE
						, CICLS_PRODUCT_CODE
						, FACILITY_CREDIT_LIMIT AS "APPROVED_AMOUNT"
						, SUM(OUTSTANDING_CURRENT) AS "OUTSTANDING_CURRENT"
						, SUM(OUTSTANDING_PAST_DUE) AS "OUTSTANDING_PAST_DUE"
						, FACILITY_REF_NUMBER 
						, 'HEADER' AS "TYPE"
						, FACILITY_CODE AS "COMPARISON"
						, COUNT(CLIENT_NUMBER) AS "COUNT"
					FROM TEMP_HEADER
					WHERE FACILITY_CODE <> 'STL'
					AND FACILITY_CODE IS NOT NULL 
					GROUP BY FACILITY_REF_NUMBER, FACILITY_CODE, FACILITY_CREDIT_LIMIT, CICLS_PRODUCT_CODE,
							  TIN_NUMBER, CLIENT_NUMBER,CLIENT_NAME, PROCESS_DATE,TRAN_TYPE
					 HAVING COUNT = 1 OR COUNT = 2
					UNION ALL
							SELECT 	CLIENT_NUMBER
						, CLIENT_NAME
						, TIN_NUMBER
						, TRAN_TYPE
						, PROCESS_DATE
						, CICLS_PRODUCT_CODE
						, SUM(OUTSTANDING_CURRENT) AS "APPROVED_AMOUNT"
						, SUM(OUTSTANDING_CURRENT) AS "OUTSTANDING_CURRENT"
						, SUM(OUTSTANDING_PAST_DUE) AS "OUTSTANDING_PAST_DUE"
						, FACILITY_REF_NUMBER 
						, 'HEADER' AS "TYPE"
						, FACILITY_CODE AS "COMPARISON"
						, COUNT(CLIENT_NUMBER) AS "COUNT"
					FROM TEMP_HEADER
					WHERE FACILITY_CODE = 'STL'
					OR FACILITY_CODE IS NULL 
					GROUP BY FACILITY_REF_NUMBER, FACILITY_CODE, FACILITY_CREDIT_LIMIT, CICLS_PRODUCT_CODE,
							  TIN_NUMBER, CLIENT_NUMBER,CLIENT_NAME, PROCESS_DATE,TRAN_TYPE
					 HAVING COUNT = 1 OR COUNT = 2
					UNION ALL
						SELECT 	CLIENT_NUMBER
						, CLIENT_NAME
						, TIN_NUMBER
						, TRAN_TYPE
						, PROCESS_DATE
						, CICLS_PRODUCT_CODE
						, CASE WHEN FACILITY_CODE IS NULL OR FACILITY_CODE = 'STL' THEN SUM(OUTSTANDING_CURRENT) ELSE 0 END
						, SUM(OUTSTANDING_CURRENT)
						, SUM(OUTSTANDING_PAST_DUE)
						, FACILITY_REF_NUMBER
						, 'DETAILS' AS "TYPE"
						, FACILITY_CODE AS "COMPARISON"
						, COUNT(CLIENT_NUMBER) AS "COUNT"
						FROM TEMP_HEADER 
							  GROUP BY FACILITY_REF_NUMBER,FACILITY_CODE, FACILITY_CREDIT_LIMIT, CICLS_PRODUCT_CODE,
							  TIN_NUMBER, CLIENT_NUMBER,CLIENT_NAME, PROCESS_DATE,TRAN_TYPE
						HAVING COUNT > 2
				)    
				ORDER BY CLIENT_NUMBER
                                , "COMPARISON" ASC
                , "TYPE" DESC
		)
		
		SELECT CLIENT_NUMBER
			, CLIENT_NAME
			, TIN_NUMBER
			, TRAN_TYPE
			, PROCESS_DATE
			, CICLS_PRODUCT_CODE
			, APPROVED_AMOUNT
			, OUTSTANDING_CURRENT
			,OUTSTANDING_PAST_DUE
			, FACILITY_REF_NUMBER
			,COUNT 
		FROM TEMP_DETAILS;
	
    OPEN C_CICLS;
END P1@