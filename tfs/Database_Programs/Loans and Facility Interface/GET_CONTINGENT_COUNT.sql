


--<ScriptOptions statementTerminator="@">
CREATE OR REPLACE PROCEDURE TFSDB2S.GET_CONTINGENT_COUNT(IN P_DOCUMENTNUMBER VARCHAR(21))
SPECIFIC GET_CONTINGENT_COUNT
DYNAMIC RESULT SETS 1
LANGUAGE SQL
--######################################################################################################
--# Description:    Retrieves Contingent COunt
--# Created by:   CCP  
--# Date created:
--#
--######################################################################################################
--# Input: 
--######################################################################################################

P1: BEGIN
    DECLARE C_CHECKIFEXIST CURSOR WITH RETURN FOR
    	
		SELECT COUNT(*)
		FROM TFSDB2S.LNCLST
		WHERE ACCTNO = REPLACE(P_DOCUMENTNUMBER, '-', '');
	
    OPEN C_CHECKIFEXIST;
END P1 @