CREATE TABLE "DOCUMENTAGAINSTACCEPTANCE"
(
   DOCUMENTNUMBER varchar(21) PRIMARY KEY NOT NULL,
   TSNUMBER varchar(12),
   DOCUMENTTYPE varchar(10),
   REMITTINGBANK varchar(10),
   PROCESSDATE timestamp,
   REMITTINGBANKREFERENCENUMBER varchar(16),
   CURRENCY varchar(3),
   AMOUNT bigint,
   OUTSTANDINGAMOUNT bigint,
   DATEOFBLAIRWAYBILL timestamp,
   MATURITYDATE timestamp,
   IMPORTERCIFNUMBER varchar(10),
   ORIGINALPORT varchar(3),
   IMPORTERCBCODE varchar(15),
   IMPORTERNAME varchar(50),
   IMPORTERADDRESS varchar(160),
   SENDERTORECEIVERINFORMATION varchar(100),
   BENEFICIARYNAME varchar(50),
   BENEFICIARYADDRESS varchar(160),
   LASTTRANSACTION varchar(50),
   CANCELLEDDATE timestamp,
   SETTLEDDATE timestamp,
   PROCESSINGUNITCODE varchar(10)
)
;