CREATE TABLE "SWIFTMESSAGELOG"
(
   ID bigint PRIMARY KEY NOT NULL,
   SWIFT_MESSAGE varchar(255),
   DATE_CREATED timestamp,
   FILENAME varchar(255),
   SENT boolean
)
;
CREATE UNIQUE INDEX SQL121005132938430 ON "SWIFTMESSAGELOG"(ID)
;
