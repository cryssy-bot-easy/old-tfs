ALTER TABLE TRANSACTIONLOG ADD TEMP_EXCHANGERATE NUMERIC(31,8);

update transactionlog set TEMP_EXCHANGERATE = EXCHANGERATE;

alter table TRANSACTIONLOG drop EXCHANGERATE;

ALTER TABLE TRANSACTIONLOG ADD EXCHANGERATE NUMERIC(31,8);

update transactionlog set EXCHANGERATE = TEMP_EXCHANGERATE;

alter table TRANSACTIONLOG drop TEMP_EXCHANGERATE;