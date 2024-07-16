CREATE TABLE "MTMESSAGE"
(
   ID bigint,
   TRADESERVICEREFERENCENUMBER varchar(12),
   DOCUMENTNUMBER varchar(21),
   MTSTATUS varchar(12),
   DATERECEIVED timestamp,
   MTTYPE varchar(5),
   INSTRUCTION varchar(200),
   USERACTIVEDIRECTORYID varchar(20),
   MODIFIEDDATE timestamp,
   MTDIRECTION varchar(10),
   "MESSAGE" clob
)
;