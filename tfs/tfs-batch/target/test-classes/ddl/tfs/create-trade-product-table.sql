CREATE TABLE "TRADEPRODUCT"
(
   DOCUMENTNUMBER varchar(21) PRIMARY KEY NOT NULL,
   PRODUCTTYPE varchar(20) NOT NULL,
   CIFNUMBER varchar(10),
   CIFNAME varchar(100),
   MAINCIFNUMBER varchar(10),
   MAINCIFNAME varchar(100),
   ACCOUNTOFFICER varchar(100),
   CCBDBRANCHUNITCODE varchar(10),
   FACILITYID varchar(11),
   FACILITYTYPE varchar(4),
   STATUS varchar(12),
   AMOUNT bigint,
   CURRENCY varchar(3),
   FACILITYREFERENCENUMBER varchar(20),
   PASSONRATETHIRDTOUSD bigint,
   PASSONRATETHIRDTOPHP bigint,
   PASSONRATEUSDTOPHP bigint,
   SPECIALRATETHIRDTOUSD bigint,
   SPECIALRATETHIRDTOPHP bigint,
   SPECIALRATEUSDTOPHP bigint,
   URR bigint
)
;

CREATE UNIQUE INDEX SQL121104163410230 ON "TRADEPRODUCT"(DOCUMENTNUMBER)
;


INSERT INTO "TRADEPRODUCT" (DOCUMENTNUMBER,PRODUCTTYPE,CIFNUMBER,CIFNAME,MAINCIFNUMBER,MAINCIFNAME,ACCOUNTOFFICER,CCBDBRANCHUNITCODE,FACILITYID,FACILITYTYPE,STATUS,AMOUNT,CURRENCY) VALUES ('909-01-932-12-933461','s','s','s','L031004','s','s','s','1','FCN','OPEN',4014000.00,'PHP');
INSERT INTO "TRADEPRODUCT" (DOCUMENTNUMBER,PRODUCTTYPE,CIFNUMBER,CIFNAME,MAINCIFNUMBER,MAINCIFNAME,ACCOUNTOFFICER,CCBDBRANCHUNITCODE,FACILITYID,FACILITYTYPE,STATUS,AMOUNT,CURRENCY) VALUES ('909-01-932-12-746051','s','s','s','L031004','s','s','s','2','FCN','OPEN',32299999.00,'PHP');
INSERT INTO "TRADEPRODUCT" (DOCUMENTNUMBER,PRODUCTTYPE,CIFNUMBER,CIFNAME,MAINCIFNUMBER,MAINCIFNAME,ACCOUNTOFFICER,CCBDBRANCHUNITCODE,FACILITYID,FACILITYTYPE,STATUS,AMOUNT,CURRENCY) VALUES ('909-01-932-12-352251','s','s','s','L031004','s','s','s','3','FCN','OPEN',5131415195.00,'PHP');