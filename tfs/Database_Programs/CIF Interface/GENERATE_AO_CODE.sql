--<ScriptOptions statementTerminator="@"/>
CREATE OR REPLACE PROCEDURE TFSDB2S.GENERATE_AO_CODE(IN P_NAME CHAR(50), IN P_BRANCH DECIMAL(5)
											   , OUT P_AO_CODE CHAR(3))
LANGUAGE SQL
SPECIFIC GENERATE_AO_CODE
--######################################################################################################
--# Description: Generates account officer code based on the ff. rule:
--#					Code = M01 - where M is equivalent to the first letter of the Last Name 
--#	    					01 - is equivalent to the sequence number (ranging from 01 to 99)
--#								>> (e.g. Maningas - M01, Malabanan - M02, Mendoza - M03)
--#					If sequence 99 is reached, new pattern will be applied..
--#				    	M1A - M - Last Name
--#							1 - Numeric Sequence (From 1 - 9)
--#							A - Alpha sequence (From A to Z)
--#							e.g.
--#								M01, M02, M03...M99
--#								M1A, M1B, M1C...M1Z
--#								M2A,M2Z
--# Created by: 	Cedrick C. Nungay 
--# Date created: 	09/06/2023
--######################################################################################################
--# Input:	P_NAME		:	Officer name
--#			P_BRANCH	:	Unit code
--# Output:	P_AO_CODE	:	Officer code
--######################################################################################################
P1: BEGIN
	DECLARE V_LASTNAME VARCHAR(50);
	DECLARE V_COUNT INTEGER;
	DECLARE V_IS_EXISTS CHAR(1) DEFAULT 'Y';

	SET V_LASTNAME = RIGHT(TRIM(UCASE(P_NAME)), LENGTH(TRIM(P_NAME)) - INSTR(TRIM(P_NAME), ' ', -1));
	IF V_LASTNAME = 'BM' THEN
		SET P_AO_CODE = P_BRANCH;
	ELSE
		SET V_COUNT = (SELECT COUNT(*) FROM TFSDB2S.JHOFFR WHERE LEFT(JHOOFF, 1) = LEFT(V_LASTNAME, 1));
		WHILE V_IS_EXISTS = 'Y' DO
			IF V_COUNT >= 99 THEN
				SET P_AO_CODE = LEFT(V_LASTNAME, 1) ||
					-- FIRST DIGIT, DIVISIBLE BY 26
					(TRANSLATE((V_COUNT - 100)/ 26, '123456789', '012345678')) ||
					-- SECOND DIGIT, LETTERS
					CASE WHEN MOD((V_COUNT - 100), 26) >= 20 THEN
						TRANSLATE(MOD((V_COUNT - 120), 26), 'UVWXYZ', '012345')
					WHEN MOD((V_COUNT - 100), 26) >= 10 THEN
						TRANSLATE(MOD((V_COUNT - 110), 26), 'KLMNOPQRST', '0123456789')
					ELSE
						TRANSLATE(MOD((V_COUNT - 100), 26), 'ABCDEFGHIJ', '0123456789')
					END;
			ELSEIF V_COUNT < 10 THEN
				SET P_AO_CODE = LEFT(V_LASTNAME, 1) || '0' || (V_COUNT + 1);
			ELSE
				SET P_AO_CODE = LEFT(V_LASTNAME, 1) || (V_COUNT + 1);
			END IF;
			IF NOT EXISTS(SELECT 1 FROM TFSDB2S.JHOFFR WHERE JHOOFF = P_AO_CODE) THEN
				SET V_IS_EXISTS = 'N';
			ELSE
				SET V_COUNT = V_COUNT + 1;
			END IF;
		END WHILE;
	END IF;
END P1@