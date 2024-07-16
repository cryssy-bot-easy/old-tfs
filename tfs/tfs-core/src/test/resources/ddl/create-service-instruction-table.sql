create table SERVICEINSTRUCTION (
  SERVICEINSTRUCTIONID varchar(255) not null,
  TYPE varchar(255),
  STATUS varchar(255),
  DETAILS varchar(4000),
  USERACTIVEDIRECTORYID varchar(255),
  DATEAPPROVED timestamp,
  CREATEDDATE timestamp,
  MODIFIEDDATE timestamp,
  primary key (serviceinstructionid)
);


