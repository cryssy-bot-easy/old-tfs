--<ScriptOptions statementTerminator="@"/>
CREATE OR REPLACE TRIGGER TFSDB2S.TRG_GLIS_DELETE
BEFORE DELETE ON INT_ACCENTRYACTUAL
REFERENCING OLD AS ACCENTRYACTUAL_OLD
FOR EACH ROW
--######################################################################################################
--# Description:    Trigger on deletion of INT_ACCENTRYACTUAL record that removes GLIS records
--# Created by:     Cedrick C. Nungay 
--# Date created:   04/04/2023
--######################################################################################################
BEGIN
    DELETE FROM TFSDB2S.GLIS_HAND_OFF_FILE
    WHERE ACCENTRY_ID = ACCENTRYACTUAL_OLD.ID;
END
