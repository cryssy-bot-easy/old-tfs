CREATE TABLE "TFS"."ROUTES"
(
   ID bigint PRIMARY KEY NOT NULL,
   SENDERACTIVEDIRECTORYID varchar(255),
   RECEIVERACTIVEDIRECTORYID varchar(255),
   DATESENT timestamp,
   STATUS varchar(255),
   ROUTINGINFORMATIONID varchar(255)
)
;

