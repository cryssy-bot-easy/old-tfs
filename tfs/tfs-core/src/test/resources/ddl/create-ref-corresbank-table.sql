CREATE TABLE "REF_CORRESPONDENT_BANK"
(
   ID bigint PRIMARY KEY NOT NULL,
   BANKCODE varchar(13),
   CURRENCY varchar(3),
   BANKNAME varchar(50),
   CBCREDITORCODE varchar(12),
   GLBANKCODE varchar(8),
   DEPOSITORYFLAG varchar(1),
   SWIFTFLAG varchar(1),
   SWIFTBANKCODE varchar(13),
   BANKGROUPCODE varchar(3),
   SWIFTBRANCHCODE varchar(4),
   RBUACCOUNT varchar(35),
   ACCOUNTTYPE int,
   COUNTRYCODE int,
   RISKINDICATOR int,
   COUNTERPARTYTYPE int,
   RESELIGIBILITY int,
   CREDITORCODE int
)
;
