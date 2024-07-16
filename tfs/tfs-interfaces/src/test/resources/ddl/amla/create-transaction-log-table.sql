--<ScriptOptions statementTerminator=";"/>

CREATE TABLE "TRANSACTIONLOG"
(
   	TXNREFERENCENUMBER VARCHAR(20) PRIMARY KEY NOT NULL,
    TXNDATE TIMESTAMP,
    DEALNUMBER VARCHAR(50),
    TRANSACTIONTYPECODE VARCHAR(5),
    TRANSACTIONSUBTYPE VARCHAR(15),
    TRANSACTIONMODE VARCHAR(15),
    TRANSACTIONAMOUNT DECIMAL(21,2),
    DEBIT_CREDIT_FLAG VARCHAR(1),
    DIRECTION VARCHAR(1),
    BRANCHCODE VARCHAR(15),
    ACCOUNTNUMBER VARCHAR(50),
    SETTLEMENTCURRENCY VARCHAR(5),
    EXCHANGERATE DECIMAL(24,8),
    SETTLEMENTAMOUNT DECIMAL(18,2),
    PURPOSE VARCHAR(255),
    CPACCOUNTNO VARCHAR(50),
    CPNAME1 VARCHAR(100),
    CPNAME2 VARCHAR(100),
    CPNAME3 VARCHAR(100),
    CP_ADDRESS1 VARCHAR(100),
    CP_ADDRESS2 VARCHAR(100),
    CP_ADDRESS3 VARCHAR(100),
    CPINSTITUTION VARCHAR(100),
    CPINSTITUTIONCOUNTRY VARCHAR(50),
    CORRESPONDENTBANKNAME VARCHAR(100),
    CORRESPONDENTCOUNTRYCODE VARCHAR(50),
    CORRESPONDENTADDRESS1 VARCHAR(100),
    CORRESPONDENTADDRESS2 VARCHAR(100),
    CORRESPONDENTADDRESS3 VARCHAR(100),
    INTRINSTITUTIONNAME VARCHAR(100),
    INTRINSTITUTIONCOUNTRY VARCHAR(50),
    INTRINSTITUTIONADDR1 VARCHAR(100),
    INTRINSTITUTIONADDR2 VARCHAR(100),
    INTRINSTITUTIONADDR3 VARCHAR(100),
    BENEFICIARYNAME1 VARCHAR(100),
    BENEFICIARYNAME2 VARCHAR(100),
    BENEFICIARYNAME3 VARCHAR(100),
    BENEFICIARYCOUNTRY VARCHAR(50),
    BENEFICIARYADDR1 VARCHAR(100),
    BENEFICIARYADDR2 VARCHAR(100),
    BENEFICIARYADDR3 VARCHAR(100),
    PRODUCTTYPE VARCHAR(100),
    PRODUCTOWNERNAME1 VARCHAR(100),
    PRODUCTOWNERNAME2 VARCHAR(100),
    PRODUCTOWNERNAME3 VARCHAR(100),
    PRODUCTOWNERADDR1 VARCHAR(100),
    PRODUCTOWNERADDR2 VARCHAR(100),
    PRODUCTOWNERADDR3 VARCHAR(100),
    INCEPTIONDATE TIMESTAMP,
    MATURITYDATE TIMESTAMP,
    NARRATION VARCHAR(255),
    REMARKS VARCHAR(255),
    NATURE VARCHAR(50),
    FUNDSSOURCE VARCHAR(20),
    CERTIFIEDDOCUMENTS VARCHAR(1),
    INPUTDATE TIMESTAMP,
    REGULARDOCUMENTS VARCHAR(1),
    TRANSACTIONCODE VARCHAR(5),
    PAYMENTMODE VARCHAR(50)
)
;
CREATE UNIQUE INDEX SQL121014182320080 ON "TRANSACTIONLOG"(TXNREFERENCENUMBER)
;
