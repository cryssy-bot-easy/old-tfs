CREATE TABLE "TRADESERVICE"
(
   TRADESERVICEID varchar(36) PRIMARY KEY NOT NULL,
   SERVICEINSTRUCTIONID varchar(12),
   TRADESERVICEREFERENCENUMBER varchar(20),
   DOCUMENTNUMBER varchar(21),
   STATUS varchar(15),
   PROCESSID bigint,
   DETAILS clob(400000),
   CHARGESCURRENCY varchar(3),
   APPROVERS varchar(50),
   AMOUNT bigint,
   DEFAULTAMOUNT bigint,
   CURRENCY varchar(3),
   CIFNUMBER varchar(10),
   CIFNAME varchar(100),
   MAINCIFNUMBER varchar(10),
   MAINCIFNAME varchar(100),
   FACILITYID varchar(11),
   FACILITYTYPE varchar(4),
   ACCOUNTOFFICER varchar(80),
   SERVICETYPE varchar(30),
   DOCUMENTTYPE varchar(20),
   DOCUMENTCLASS varchar(20),
   DOCUMENTSUBTYPE1 varchar(10),
   DOCUMENTSUBTYPE2 varchar(10),
   USERACTIVEDIRECTORYID varchar(20),
   LASTUSER varchar(20),
   PREPAREDBY varchar(20),
   CREATEDDATE timestamp,
   MODIFIEDDATE timestamp,
   NARRATIVE clob(500),
   REASONFORCANCELLATION clob(500),
   PROCESSINGUNITCODE varchar(10),
   CCBDBRANCHUNITCODE varchar(10),
   PASSONRATETHIRDTOUSDSERVICECHARGE bigint,
   PASSONRATEUSDTOPHPSERVICECHARGE bigint,
   PASSONRATETHIRDTOPHPSERVICECHARGE bigint,
   PASSONURRSERVICECHARGE bigint,
   SPECIALRATETHIRDTOUSDSERVICECHARGE bigint,
   SPECIALRATEUSDTOPHPSERVICECHARGE bigint,
   SPECIALRATETHIRDTOPHPSERVICECHARGE bigint,
   SPECIALRATEURRSERVICECHARGE bigint,
   PAYMENTSTATUS varchar(20),
   REINSTATEFLAG smallint,
   CREATEDBY varchar(20)
)
;