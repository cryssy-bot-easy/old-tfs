CREATE TABLE "LETTEROFCREDIT"
(
   DOCUMENTNUMBER varchar(255) PRIMARY KEY ,
   DOCUMENTTYPE varchar(255),
   PURPOSE varchar(255),
   PROCESSDATE timestamp,
   EXPIRYDATE timestamp,
   REASONFORCANCELLATION varchar(255),
   CANCELLATIONDATE timestamp,
   TENOR varchar(255),
   TYPE varchar(255),
   PAYMENTMODE varchar(255),
   USANCEPERIOD bigint,
   USANCEPERIODSTART varchar(255),
   EXPIRYCOUNTRYCODE varchar(255),
   PARTIALSHIPMENT varchar(255),
   PARTIALDELIVERY varchar(255),
   TRANSSHIPMENT varchar(255),
   IRREVOCABLE varchar(255),
   NEGOTIATIONRESTRICTION varchar(255),
   ADVISETHROUGHBANK varchar(255),
   PRICETERM varchar(255),
   REVOLVINGAMOUNT bigint,
   REVOLVINGPERIOD varchar(255),
   DAYSREVOLVING int,
   CUMULATIVE varchar(255),
   AGGREGATEAMOUNT bigint,
   CASHFLAG varchar(255),
   TOTALNEGOTIATEDAMOUNT bigint,
   CASHAMOUNT bigint,
   TOTALNEGOTIATEDCASHAMOUNT bigint,
   OUTSTANDINGBALANCE bigint,
   REFUNDAMOUNT bigint,
   PORTOFORIGINATION varchar(255),
   PORTOFDESTINATION varchar(255),
   PORTOFORIGINCOUNTRYCODE varchar(255),
   IMPORTERADDRESS varchar(255),
   BENEFICIARYADDRESS varchar(255),
   BENEFICIARYNAME varchar(255),
   ADVISINGBANKCODE varchar(255),
   CONFIRMINGBANKCODE varchar(255),
   REIMBURSINGCURRENCY varchar(255),
   DRAWEE varchar(255),
   ADVISEMEDIUM varchar(255),
   LATESTSHIPMENTDATE timestamp,
   DISPATCHPLACE varchar(255),
   FINALDESTINATIONPLACE varchar(255),
   APPLICABLERULES varchar(255),
   FORMOFDOCUMENTARYCREDIT varchar(255),
   DESTINATIONBANK varchar(255),
   ISSUEDATE timestamp,
   PRICETERMNARRATIVE varchar(3000),
   CONFIRMATIONINSTRUCTIONSFLAG varchar(255),
   MARINEINSURANCE varchar(255),
   GENERALDESCRIPTIONOFGOODS varchar(255),
   CWTFLAG varchar(255),
   ADVANCECORRESCHARGESFLAG varchar(255),
   OTHERPRICETERM varchar(255),
   ADVISETHROUGHBANKIDENTIFIERCODE varchar(255),
   TENOROFDRAFTNARRATIVE varchar(255),
   MAXIMUMCREDITAMOUNT varchar(255),
   SHIPMENTPERIOD varchar(255),
   AVAILABLEWITHFLAG varchar(255),
   ADVISETHROUGHBANKLOCATION varchar(255),
   PERIODFORPRESENTATION varchar(255),
   PERIODFORPRESENTATIONADVISETHROUGHBANK varchar(255),
   MIXEDPAYMENTDETAILS varchar(255),
   IMPORTERNAME varchar(255),
   PLACEOFFINALDESTINATION varchar(255),
   EXPORTERNAME varchar(255),
   PLACEOFTAKINGDISPATCHORRECEIPT varchar(3000),
   EXPORTERADDRESS varchar(255),
   NEGATIVETOLERANCELIMIT bigint,
   REIMBURSINGBANKFLAG varchar(255),
   ADVISETHROUGHBANKNAMEANDADDRESS varchar(255),
   IDENTIFIERCODE varchar(255),
   AVAILABLEBY varchar(255),
   REIMBURSINGBANKNAMEANDADDRESS varchar(255),
   SENDERTORECEIVERINFORMATION varchar(255),
   REIMBURSINGBANKIDENTIFIERCODE varchar(255),
   NAMEANDADDRESS varchar(255),
   REIMBURSINGACCOUNTTYPE varchar(255),
   IMPORTERCBCODE varchar(255),
   BSPCOUNTRYCODE varchar(255),
   IMPORTERCIFNUMBER varchar(255),
   DEFERREDPAYMENTDETAILS varchar(255),
   REIMBURSINGBANKACCOUNTNUMBER varchar(255),
   POSITIVETOLERANCELIMIT bigint,
   LATESTDATESHIPMENT timestamp,
   AVAILABLEWITH varchar(255),
   ADDITIONALAMOUNTSCOVERED varchar(255),
   PORTOFDISCHARGEORDESTINATION varchar(255),
   ADVISETHROUGHBANKFLAG varchar(255),
   SENDERTORECEIVERINFORMATIONNARRATIVE varchar(255),
   EXPORTERCBCODE varchar(255),
   PORTOFLOADINGORDEPARTURE varchar(255),
   STANDBYTAGGING varchar(255),
   FURTHERIDENTIFICATION varchar(255),
   PURPOSEOFSTANDBY varchar(255),
   FORMATTYPE varchar(255),
   DETAILSOFGUARANTEE varchar(255),
   APPLICANTNAME varchar(255),
   APPLICANTADDRESS varchar(255),
   PLACEOFRECEIPT varchar(255),
   PLACEOFDELIVERY varchar(255),
   OTHERDOCUMENTSINSTRUCTIONS varchar(255),
   SHIPMENTCOUNT int,
   DATECLOSED timestamp,
   CURRENTAMOUNT bigint,
   NUMBEROFAMENDMENTS int,
   LASTAMENDMENTDATE timestamp,
   LASTREINSTATEMENTDATE timestamp,
   LASTNEGOTIATIONDATE timestamp
)
;