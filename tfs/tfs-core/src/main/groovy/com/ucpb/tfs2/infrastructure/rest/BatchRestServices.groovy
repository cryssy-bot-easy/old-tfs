package com.ucpb.tfs2.infrastructure.rest
import com.google.gson.Gson
import com.ucpb.tfs.application.service.AccountingService
import com.ucpb.tfs.application.service.AllocationUnitCodeService
import com.ucpb.tfs.application.service.AmlaExpiredLcService
import com.ucpb.tfs.application.service.CifNormalizationLogService
import com.ucpb.tfs.batch.job.*
import com.ucpb.tfs.core.batch.process.CifNormalization
import com.ucpb.tfs.core.batch.process.CifNormalizationProcess
import com.ucpb.tfs.core.batch.process.FacilityReferenceNormalization


import com.ucpb.tfs.domain.cdt.services.PAS5FilesLoaderService
import com.ucpb.tfs.domain.product.TradeProductRepository
import com.ucpb.tfs.domain.service.ItrsService
import com.ucpb.tfs.domain.service.TradeServiceRepository
import com.ucpb.tfs.interfaces.services.HolidayService
import com.ucpb.tfs.parsers.handlers.RmaBankHandler
import com.ucpb.tfs.util.FileUtil
import com.ucpb.tfs.utils.DateUtil
import com.ucpb.tfs2.application.service.RefBankService
import com.ucpb.tfs2.application.service.casa.exception.CasaServiceException


import groovy.io.FileType
import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.config.PropertiesFactoryBean
import org.springframework.stereotype.Component
import org.springframework.util.Assert
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler

import javax.annotation.Resource
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.*
import javax.xml.parsers.SAXParser
import javax.xml.parsers.SAXParserFactory
import java.text.SimpleDateFormat
import java.util.List;

/**
 */

/*	PROLOGUE:
 * 	(revision)
	SCR/ER Number: IBD-15-0828-01
	SCR/ER Description: Comparison of Balances in DW and SIBS-GL
	[Revised by:] Jesse James Joson
	[Date revised:] 09/17/2015
	Program [Revision] Details: add new methods that will execute SIBS extraction
	INPUT: extractSibs
	OUTPUT: Daily_Master_GL_Summary.xls & Daily_Master_GL_DailyBalance_Summary.xls
	PROCESS: execute SIBS extraction
 */
/*	PROLOGUE:
 * 	(revision)
	SCR/ER Number: 20150820-072
	SCR/ER Description: To catch duplication in CIF.
	[Revised by:] Jesse James Joson
	[Date revised:] 10/13/2015
	Program [Revision] Details: add a response whether to failed in UI or Success.
	PROJECT: CORE
	MEMBER TYPE  : Groovy
	Project Name: BatchRestServices 
 */

/*  PROLOGUE:
 * 	(revision)
	SCR/ER Number: SCR IBD-16-0219-01
	SCR/ER Description: Generate CIC File
	[Revised by:] Jesse James Joson
	[Date Deployed:]  02/24/2016
	Program [Revision] Details: This class will call the methods that will generate CIC File
	PROJECT: CORE
	MEMBER TYPE  : Groovy
	Project Name: BatchRestServices
 */
/*  PROLOGUE:
 * 	(revision)
	SCR/ER Number:  
	SCR/ER Description: Sibs Disconnection
	[Revised by:] Allan Comboy Jr.
	[Date Deployed:]  06/07/2016
	Program [Revision] Details: to reconnect when disconnected to sibs
	PROJECT: CORE
	MEMBER TYPE  : Groovy
	Project Name: BatchRestServices
 */

/*  PROLOGUE:
 * 	(revision)
	SCR/ER Number:  20160607-024
	SCR/ER Description: Amla wrong status
	[Revised by:] Allan Comboy Jr.
	[Date Deployed:]  06/07/2016
	Program [Revision] Details: to Address Amla status affected by deployment 06/07/2016
	PROJECT: CORE
	MEMBER TYPE  : Groovy
	Project Name: BatchRestServices
 */

/*  PROLOGUE:
 * 	(revision)
	SCR/ER Number:  20160613-044
	SCR/ER Description: Program abnormally terminates during SIBS DB access time-out.
	[Revised by:] Allan Comboy Jr.
	[Date Deployed:]  06/14/2016
	Program [Revision] Details: to reconnect when disconnected to sibs (for 4 additional programs)
	PROJECT: CORE
	MEMBER TYPE  : Groovy
	Project Name: BatchRestServices
 */
/**  PROLOGUE:
 * 	(revision)
	SCR/ER Number: ER# 20140909-038
	SCR/ER Description: CIF Normalization Not Working in TFS
	[Revised by:] Jesse James Joson
	[Date Deployed:] 08/05/2016
	Program [Revision] Details: The CIF Normalization was redesigned, since not all tables are normalized.
	PROJECT: CORE
	MEMBER TYPE  : Groovy
	Project Name: BatchRestServices
 */

/**  PROLOGUE:
 * 	(revision)
	SCR/ER Number: SCR# IBD-16-0615-01
	SCR/ER Description: To comply with the requirement for CIF archiving/purging of inactive accounts in TFS.
	[Revised by:] Jesse James Joson
	[Date Revised:] 09/22/2016
	Program [Revision] Details: Additional methods to run account purging
	PROJECT: CORE
	MEMBER TYPE  : Groovy
	Project Name: BatchRestServices
 */

/**  PROLOGUE:
 * 	(revision)
	SCR/ER Number: ER# 20160905-019
	SCR/ER Description: Revert ets to pending cannot connect to SIBS.
	[Revised by:] Jesse James Joson
	[Date Revised:] 10/13/2016
	Program [Revision] Details: To include Revert ets to pending for those program connecting to SIBS.
	PROJECT: CORE
	MEMBER TYPE  : Groovy
	Project Name: BatchRestServices
 */

/**  PROLOGUE:
 * 	(revision)
	SCR/ER Number:
	SCR/ER Description:
	[Revised by:] Cedrick C. Nungay
	[Date Revised:] 09/29/2017
	Program [Revision] Details: Updated service for throwing of exception if the file is not found.
	PROJECT: CORE
	MEMBER TYPE  : Groovy
	Project Name: BatchRestServices
*/
/**  PROLOGUE:
 * 	(revision)
	 SCR/ER Number:
	 SCR/ER Description:
	 [Revised by:] Prochina, Daniel Jericho B.
	 [Date Revised:] 06/20/2018
	 Program [Revision] Details: To include service for ITRS Interface.
	 PROJECT: CORE
	 MEMBER TYPE  : Groovy
	 Project Name: BatchRestServices
 */

/**  PROLOGUE:
 * 	(revision)
	SCR/ER Number: ER# 20180614-030
	SCR/ER Description: Update Allocation Unit Code module of TFS bactch encountered FAILED message during execution.
	[Revised by:] Jesse James Joson
	[Date Revised:] 7/13/2018
	Program [Revision] Details: Update method to add the sibsretry parameter to the return value upon execution of Update Allocation Unit Code.
	PROJECT: CORE
	MEMBER TYPE  : Groovy
	Project Name: BatchRestServices
 */

 /**  PROLOGUE:
 * 	(revision)
	SCR/ER Number:
	SCR/ER Description:
	[Revised by:] Crystiann Puso
	[Date Revised:] 2/23/2024
	Program [Revision] Details: Added service and functions for Process Cicls and Cicls Report Generator
	PROJECT: CORE 
	MEMBER TYPE  : Groovy
	Project Name: BatchRestServices
 */  
 
@Component
@Path("/batch")
class BatchRestServices {
	def TestTimehehe = false;
	def sibsReq = false;
	def sibsRecon = false;
	def sibsStatus = false;
	def checkConn = false;
    private static final String REPORT_DATE = "date"

	private static final String GL_QUERY =
				"SELECT " +
				"GL.RESPONDINGUNITCODE, " +
				"GL.BOOKCODE, " +
		  	    "CASE WHEN B.LBP_ACCOUNTINGCODE IS NULL OR B.LBP_ACCOUNTINGCODE = '' THEN " +
		  	        "GL.ACCOUNTINGCODE ELSE B.LBP_ACCOUNTINGCODE " +
		  	    "END AS ACCOUNTINGCODE, " +
		  	    "CASE WHEN B.LBP_ACCOUNTINGCODE IS NULL OR B.LBP_ACCOUNTINGCODE = '' THEN " +
		  	        "'NOT MAPPED: ' || GL.PARTICULARS ELSE B.LBP_PARTICULARS " +
		  	    "END AS PARTICULARS, " +
				"GL.ORIGINALCURRENCY, " +
				"GL.EFFECTIVEDATE, " +
				"GL.ENTRYTYPE, " +
				"GL.PESOAMOUNT, " +
				"GL.ORIGINALAMOUNT," +
				"GL.UNITCODE, " +
				"(CASE TS.DOCUMENTCLASS WHEN 'INDEMNITY' THEN TS.DOCUMENTNUMBER  WHEN 'CDT' THEN TS.TRADESERVICEREFERENCENUMBER ELSE TS.TRADEPRODUCTNUMBER END) AS DOCUMENTNUMBER, " +
				"GL.ID, " +
				"(CASE WHEN (GL.PRODUCTID='CDT') THEN TS.TRADESERVICEREFERENCENUMBER ELSE TS.SERVICEINSTRUCTIONID END) AS SERVICEINSTRUCTIONID, " +
				"GL.TRANSACTIONSHORTNAME, " +
				"GL.SERVICETYPE, " +
				"TS.DOCUMENTCLASS, " +
				"TS.DOCUMENTTYPE, " +
				"GL.UCPBPRODUCTID, " +
				"(CASE WHEN(TS.DOCUMENTCLASS='CDT') THEN CDT.CLIENT_NAME ELSE TS.CIFNAME END) AS CIFNAME, " +
				"TS.TRADESERVICEREFERENCENUMBER " +
				"FROM INT_ACCENTRYACTUAL GL " +
				"INNER JOIN TRADESERVICE TS ON TS.TRADESERVICEID = GL.TRADESERVICEID " +
				"LEFT JOIN CDTPAYMENTREQUEST CDT ON TS.TRADESERVICEREFERENCENUMBER = CDT.IEDIEIRDNO " +
    			"LEFT JOIN TFSDB2S.REF_GLMAPPING B " +
	 			"ON GL.ACCOUNTINGCODE = B.ACCOUNTINGCODE " +
		   		"AND GL.BOOKCODE = B.BOOKCODE " +
		   		"AND GL.BOOKCURRENCY = B.BOOKCURRENCY " +
				"WHERE " +
				"DAYS(GL.EFFECTIVEDATE) = DAYS(CAST(? AS TIMESTAMP)) AND GL.STATUS IN ('APPROVED','PRE_APPROVED','POST_APPROVED','POSTED', 'EXPIRED','REINSTATED') AND " +
				"GL.TRADESERVICEID NOT IN (SELECT GLSUB.TRADESERVICEID FROM INT_ACCENTRYACTUAL GLSUB WHERE (GLSUB.WITHERROR IS NOT NULL AND (LENGTH(TRIM(GLSUB.WITHERROR)) > 0))) "+
				
			//added for Auto Expired Cancellation
				"UNION " +
				"SELECT  " +
				"GLE.RESPONDINGUNITCODE, " +
				"GLE.BOOKCODE, " +
		  	    "CASE WHEN B.LBP_ACCOUNTINGCODE IS NULL OR B.LBP_ACCOUNTINGCODE = '' THEN " +
		  	        "GLE.ACCOUNTINGCODE ELSE B.LBP_ACCOUNTINGCODE " +
		  	    "END AS ACCOUNTINGCODE, " +
		  	    "CASE WHEN B.LBP_ACCOUNTINGCODE IS NULL OR B.LBP_ACCOUNTINGCODE = '' THEN " +
		  	        "'NOT MAPPED: ' || GLE.PARTICULARS ELSE B.LBP_PARTICULARS " +
		  	    "END AS PARTICULARS, " +
				"GLE.ORIGINALCURRENCY, " +
				"GLE.EFFECTIVEDATE, " +
				"GLE.ENTRYTYPE, " +
				"GLE.PESOAMOUNT, " +
				"GLE.ORIGINALAMOUNT,  " +
				"GLE.UNITCODE,  " +
				"TPE.DOCUMENTNUMBER as DOCUMENTNUMBER,  " +
				"GLE.ID,  " +
				//no tradeServiceId since it is not a transaction
				"\'\',  " +
				"GLE.TRANSACTIONSHORTNAME,  " +
				"GLE.SERVICETYPE, " +
				"\'\', " +
				"\'\',  " +
				"GLE.UCPBPRODUCTID, " +
				"TPE.CIFNAME, " +
				"TPE.DOCUMENTNUMBER " +
				"FROM INT_ACCENTRYACTUAL GLE " +
			//change from tradeservice to tradeproduct for the multiple upload of expired lc bug
				"INNER JOIN TRADEPRODUCT TPE ON TPE.DOCUMENTNUMBER = GLE.DOCUMENTNUMBER " +
    			"LEFT JOIN TFSDB2S.REF_GLMAPPING B " +
	 			"ON GLE.ACCOUNTINGCODE = B.ACCOUNTINGCODE " +
		   		"AND GLE.BOOKCODE = B.BOOKCODE " +
		   		"AND GLE.BOOKCURRENCY = B.BOOKCURRENCY " +
				"where " +
				"DATE(GLE.EFFECTIVEDATE) = ? AND GLE.ACCEVTRANID = 'CANCELLATION-EXPIRED-LC' AND "+
				 //"GLE.ISPOSTED IS NULL AND GLE.status='EXPIRED' AND " +  //@carlo uncomment changging cancellation to effective date
				"TPE.PRODUCTTYPE='LC' " +
			//for untag gl
			"UNION " +
			"SELECT "+
				" GLP.RESPONDINGUNITCODE, " +
					"GLP.BOOKCODE, " +
			  	    "CASE WHEN B.LBP_ACCOUNTINGCODE IS NULL OR B.LBP_ACCOUNTINGCODE = '' THEN " +
			  	        "GLP.ACCOUNTINGCODE ELSE B.LBP_ACCOUNTINGCODE " +
			  	    "END AS ACCOUNTINGCODE, " +
			  	    "CASE WHEN B.LBP_ACCOUNTINGCODE IS NULL OR B.LBP_ACCOUNTINGCODE = '' THEN " +
			  	        "'NOT MAPPED: ' || GLP.PARTICULARS ELSE B.LBP_PARTICULARS " +
			  	    "END AS PARTICULARS, " +
					"GLP.ORIGINALCURRENCY, " +
					"GLP.EFFECTIVEDATE, " +
					"GLP.ENTRYTYPE, " +
					"GLP.PESOAMOUNT, " +
					"GLP.ORIGINALAMOUNT, " +
					"GLP.UNITCODE, " +
					"(CASE TSP.DOCUMENTCLASS WHEN 'INDEMNITY' THEN TSP.DOCUMENTNUMBER WHEN 'CDT' THEN TSP.TRADESERVICEREFERENCENUMBER ELSE TSP.TRADEPRODUCTNUMBER END) AS DOCUMENTNUMBER, " +
					"GLP.ID, " +
					"(CASE WHEN (GLP.PRODUCTID='CDT') THEN TSP.TRADESERVICEREFERENCENUMBER ELSE TSP.SERVICEINSTRUCTIONID END) AS SERVICEINSTRUCTIONID, " +
					"GLP.TRANSACTIONSHORTNAME, " +
					"GLP.SERVICETYPE, " +
					"TSP.DOCUMENTCLASS, " +
					"TSP.DOCUMENTTYPE, " +
					"GLP.UCPBPRODUCTID, " +
					"(CASE WHEN(TSP.DOCUMENTCLASS='CDT') THEN CDT.CLIENT_NAME ELSE TSP.CIFNAME END) AS CIFNAME, " +
					"TSP.TRADESERVICEREFERENCENUMBER " +
				"FROM INT_ACCENTRYACTUAL GLP " +
					"INNER JOIN TRADESERVICE TSP ON TSP.TRADESERVICEID = GLP.TRADESERVICEID " +
					"LEFT JOIN CDTPAYMENTREQUEST CDT ON TSP.TRADESERVICEREFERENCENUMBER = CDT.IEDIEIRDNO " +
	    			"LEFT JOIN TFSDB2S.REF_GLMAPPING B " +
		 			"ON GLP.ACCOUNTINGCODE = B.ACCOUNTINGCODE " +
			   		"AND GLP.BOOKCODE = B.BOOKCODE " +
			   		"AND GLP.BOOKCURRENCY = B.BOOKCURRENCY " +
				"WHERE " +
					"GLP.ISPOSTED <> 1 AND GLP.STATUS IN ('APPROVED','PRE_APPROVED','POST_APPROVED','POSTED', 'EXPIRED','REINSTATED') AND " +
					"GLP.TRADESERVICEID NOT IN (SELECT GLSUBP.TRADESERVICEID FROM INT_ACCENTRYACTUAL GLSUBP WHERE (GLSUBP.WITHERROR IS NOT NULL AND (LENGTH(TRIM(GLSUBP.WITHERROR)) > 0))) ";
					
					
					
    private static final String TRANSACTION_LOG_QUERY = "SELECT * from TRANSACTIONLOG WHERE DAYS(TXNDATE) = DAYS(cast(? as timestamp)) " + 
											"AND TRANSACTIONTYPECODE IN ('ICOBD','ICDCC','IDLDC','IDLDD','IDLDM','IDLIC','IDLID','IDLIM','IDLSD','IDNIC','IDNID','IDNIM','IOBLC','IOBLK','IOBLM','IOBNC','IOBNK','IOBNM','ILCC') or BATCHFLAG = 1 " +
											"ORDER BY TXNDATE ASC";

    private static final String ACCOUNT_LOG_QUERY = "select * from ACCOUNTLOG WHERE DAYS(DATE_CREATED) = DAYS(cast(? as timestamp)) or BATCHFLAG = 1 ORDER BY DATE_CREATED ASC";

    private static final String CUSTOMER_ACCOUNT_LOG_QUERY = "SELECT * from CUSTOMERACCOUNT WHERE DAYS(DATE_CREATED) = DAYS(cast(? as timestamp)) or BATCHFLAG = 1 ORDER BY DATE_CREATED ASC";

    private static final String CUSTOMER_LOG_QUERY = "SELECT ID, customerType, firstName, middleName, lastName,gender,dateOfBirth," +
            "placeOfBirth,maritalStatus,'' AS DUMMY,nationality,cntry_iso,businessAddress1,businessAddress2,businessAddress3,businessAddress4," +
            "businessAddressZipCode,lengthOfStayInPresentAddress,permanentAddress1,permanentAddress2,permanentAddress3,permanentAddress4,permanentAddressZipCode," +
            "lengthOfStayInPermanentAddress,occupation,natureOfBusiness,monthlyIncome,annualIncome,financialStatus,individualOrCorporate,financialStatus,residencePhoneNumber," +
            "officePhoneNumber,mobileNumber,natureofselfemployment,sourceoffunds " +
            "from CUSTOMERLOG cust " +
            "left join REF_TFCNTRY cntry on cust.nationOfBirth = cntry.cntry_cd " +
            "WHERE DAYS(LAST_UPDATED) = DAYS(cast(? as timestamp)) ORDER BY LAST_UPDATED ASC";

    private static final String AMLA_TOTALS_QUERY = "select 'TRN71.txt' as FILENAME, count(TXNREFERENCENUMBER) as TOTALRECORDS, sum(transactionAmount) as TOTALAMOUNT from transactionLog where DAYS(TXNDATE) = DAYS(cast(? as timestamp))" +
			"AND TRANSACTIONTYPECODE IN ('ICOBD','ICDCC','IDLDC','IDLDD','IDLDM','IDLIC','IDLID','IDLIM','IDLSD','IDNIC','IDNID','IDNIM','IOBLC','IOBLK','IOBLM','IOBNC','IOBNK','IOBNM','ILCC') " +
			" union " +
            "select 'ACC71.txt' as FILENAME, count(ID) as TOTALRECORDS, null as TOTALAMOUNT from accountLog where DAYS(DATE_CREATED) = DAYS(cast(? as timestamp))" +
            " union " +
            "select 'CAC71.txt' as FILENAME, count(ID) as TOTALRECORDS, null as TOTALAMOUNT from customerAccount where DAYS(DATE_CREATED) = DAYS(cast(? as timestamp))";

    private static final String DATE_FORMAT = "MM-dd-yyyy";
    private static final String DATE_FORMAT_DESC = "yyyy-MM-dd";
	private static final String DATE_FORMAT_BATCH_CONTROLLER = "MM-dd-yyyy";

    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT);
    private static final SimpleDateFormat DATE_FORMATTER_DESC = new SimpleDateFormat(DATE_FORMAT_DESC);
	private static final SimpleDateFormat DATE_FORMATTER_BATCH_CONTROLLER = new SimpleDateFormat(DATE_FORMAT_BATCH_CONTROLLER);
	private static final String GL_PARAMETER_FILE_SQL = "SELECT DISTINCT GLCODE, RECORDTYPE FROM GL_ENTRY_TYPES";
    private static final String DW_PARAMETER_FILE_SQL = "SELECT * FROM DW_REFERENCE WHERE REF_TYPE = 'CBR'";
    private static final String CBR_PARAMETER_FILE_SQL = "SELECT * FROM DW_REFERENCE WHERE REF_TYPE = 'CBR1' ORDER BY FIELD, PRODUCTID";

	private static final String SYNCHRONIZE_ROUTING_STATUS = "{CALL SYNCHRONIZE_LC_STATUS(?,?,?)}";

    //TODO MOVE THIS QUERIES PROPERTIES INTO PROPERTIES FILE


    @Autowired
    @Qualifier("transactionLogGeneratorJob")
    private CsvReportGeneratorJob transactionLogJob;

    @Autowired
    @Qualifier("accountLogGeneratorJob")
    private CsvReportGeneratorJob accountLogJob;

    @Autowired
    @Qualifier("customerAccountLogGeneratorJob")
    private CsvReportGeneratorJob customerAccountLogGeneratorJob;

    @Autowired
    @Qualifier("customerLogGeneratorJob")
    private CsvReportGeneratorJob customerLogGeneratorJob;
	
	@Autowired
	@Qualifier("amlaTotalsGeneratorJob")
	private CsvReportGeneratorJob amlaTotalsLogJob;
	
    @Autowired
    @Qualifier("synchronizeRoutingStatusCsvJob")
    private CsvReportGeneratorJob synchronizeRoutingStatusCsvJob;

    @Autowired
    @Qualifier("glReportGeneratorJob")
    private GlReportGeneratorJob glReportGenerator;

    @Autowired
    @Qualifier("glisReportGeneratorJob")
    private GlisReportGeneratorJob glisReportGenerator;

	@Autowired
	@Qualifier("ciclsProcessorJob")
    private CiclsProcessorJob ciclsProcessorJob;

	@Autowired
	@Qualifier("ciclsReportGeneratorJob")
    private CiclsReportGeneratorJob ciclsReportGeneratorJob;

	@Autowired
	@Qualifier("cifPurgingGeneratorJob")
	private CifPurgingGeneratorJob cifPurgingGeneratorJob;
	
	@Autowired
	@Qualifier("cifPurgingJob")
	private CifPurgingJob cifPurgingJob;
	
	@Autowired
	private AmlaExpiredLcService amlaExpiredLcService;
	
    @Autowired
    @Qualifier("abortPendingEtsReversalJob")
    private AbortPendingEtsReversalJob abortPendingEtsReversalJob;

	//ER# 20140909-038 : Start
	@Autowired
    @Qualifier("cifNormalizationProcess")
    private CifNormalizationProcess cifNormalizationProcess;
	//ER# 20140909-038 : End
	
    @Autowired
    @Qualifier("facilityReferenceNormalization")
    private FacilityReferenceNormalization facilityReferenceNormalization;

    @Autowired
    private MasterFileReportGeneratorJob masterFileReportGeneratorJob;
	
	@Autowired
	private MasterExcelFileGeneratorJob masterExcelFileGeneratorJob;

    @Autowired
    private AllocationFileReportGeneratorJob glAllocationsReportJob;

	@Autowired
	private AllocationExcelFileGeneratorJob glAllocationsExcelJob;

	@Autowired
	private SibsExtractionJob sibsExtractionJob;
	
	@Autowired
	private YearEndInsertJob yearEndInsertJob;
	
	@Autowired
	private CicExtractionJob cicExtractionJob;    // IBD-16-0219-01
		
    @Autowired
    @Resource(name = "dailyBalanceRecorderJob")
    private SpringJob dailyBalanceRecorderJob;
//    private SqlRunnerJob dailyBalanceRecorderJob;

//    @Autowired
//    @Resource(name = "tradeServiceRevertJob")
//    private SpringJob tradeServiceRevertJob;
    
    @Autowired
    @Resource(name = "serviceInstructionRevertJob")
    private SpringJob serviceInstructionRevertJob;

    @Autowired
    @Resource(name = "tradeProductExpireJob")
    private SpringJob tradeProductExpireJob;

    @Autowired
    @Qualifier("setExpiredLcsStatusToExpired")
    private SpringJob setExpiredLcsStatusToExpired;

    @Autowired
    @Resource(name = "cancelDormantEtsJob")
    private SpringJob cancelDormantEtsJob;
    
    @Autowired
    @Resource(name = "uploadRoutingJob")
    private SpringJob uploadRoutingJob;

    @Autowired
    @Qualifier("glParameterFileJob")
    private FixedFileReportGeneratorJob glParameterFileJob;

    @Autowired
    @Qualifier("dwParameterRecord")
    private FixedFileReportGeneratorJob dwParameterRecord;

    @Autowired
    @Qualifier("cbrParameterFileGenerator")
    private FixedFileReportGeneratorJob cbrParameterFileGenerator;
	
	@Autowired
	private PAS5FilesLoaderService pas5FilesLoaderService;

    @Autowired
    private TradeProductRepository tradeProductRepository;
	
    @Autowired
    private TradeServiceRepository tradeServiceRepository;

    @Autowired
    private CifNormalizationLogService cifNormalizationLogService;

    @Autowired
    @Resource(name = "batchFacilityRevaluationJob")
    private SpringJob batchFacilityRevaluationJob;

    @Autowired
    private PurgeSecEmloyeeAuditJob purgeSecEmloyeeAuditJob;

	@Autowired
	RefBankService refBankService
	
	@Autowired
	PropertiesFactoryBean appProperties

    @Autowired
    AllocationUnitCodeService allocationUnitCodeService

	@Autowired
	AccountingService accountingService

	@Autowired
	ItrsService itrsService

    @Autowired
    @Qualifier("rerouteTradeServiceJob")
    private RerouteTradeServiceJob rerouteTradeServiceJob;

	@Autowired
	private HolidayService holidayService;
    
	def timerStarts = 0

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getInterfaceDirectories")
	public Response getInterfaceDirectories(@Context UriInfo allUri) {
		def directories = [:]
		Gson gson = new Gson();
		try{
			String itrsDirectory = appProperties.object.getProperty('itrs.batch.directory')	
			directories << [itrs: itrsDirectory]
			return Response.status(200).entity(gson.toJson([directories : directories])).build();
		} catch(Exception e) {
			e.printStackTrace();
			return Response.status(200).entity(gson.toJson([directories : []])).build();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/extractItrs")
	public Response extractItrs(@Context UriInfo allUri){
		println "\n--- START: ITRS Interface, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
		Gson gson = new Gson();
		MultivaluedMap<String, String> parameters = allUri.getQueryParameters();
		String reportDate = parameters?.getFirst(REPORT_DATE)?.toString();
		Assert.notNull(reportDate,"Report date must not be null!")
		try{
			reportDate = DATE_FORMATTER_DESC.format(DATE_FORMATTER_BATCH_CONTROLLER.parse(reportDate)) + "-00.00.00"
			String directory = appProperties.object.getProperty('itrs.batch.directory')			
			println "reportDate:"+reportDate
			itrsService.execute(reportDate, directory);
			println "\n--- END (SUCCESS): ITRS Interface, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : true,name:'ITRS Interface'])).build();
		}catch(Exception e){
			e.printStackTrace();
			System.err.println("ITRS Interface: "+e.getCause()?.getMessage())
			println "\n--- END (FAILED): ITRS Interface, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : false])).build();
		}
	}
	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/master")
    public Response generateMasterFile(@Context UriInfo allUri){
		println "\n--- START: Master File, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
        Gson gson = new Gson();
        MultivaluedMap<String, String> parameters = allUri.getQueryParameters();
        String reportDate = parameters?.getFirst(REPORT_DATE)?.toString();
        Assert.notNull(reportDate,"Report date must not be null!")
        println "reportDate:"+reportDate
		try{
			String masterDate = DATE_FORMATTER_DESC.format(DATE_FORMATTER_BATCH_CONTROLLER.parse(reportDate)) + "-00.00.00"
            println "masterDate:"+masterDate
            masterFileReportGeneratorJob.execute(masterDate);
			masterExcelFileGeneratorJob.execute(masterDate);
			println "\n--- END (SUCCESS): Master File, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : true,name:'Master File'])).build();
		}catch(Exception e){
			e.printStackTrace();
			System.err.println("Master: "+e.getCause()?.getMessage())
			println "\n--- END (FAILED): Master File, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : false])).build();
		}
    }
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/extractSibs")
	public Response extractSibs(@Context UriInfo allUri){
		println "\n--- START: Extract Sibs, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
		Gson gson = new Gson();
		MultivaluedMap<String, String> parameters = allUri.getQueryParameters();
		String reportDate = parameters?.getFirst(REPORT_DATE)?.toString();
		Assert.notNull(reportDate,"Report date must not be null!")
		println "reportDate:"+reportDate
		try{
			String masterDate = DATE_FORMATTER_DESC.format(DATE_FORMATTER_BATCH_CONTROLLER.parse(reportDate)) + "-00.00.00"
			println "masterDate:"+masterDate
			
		
						//test

								def selfTimer = 0;
								def tryConn = true;
								System.out.println("Connecting to SIBS...");
									//Log.debug("Connecting to SIBS...");
								while(tryConn == true){
								try{
								
									
								if(selfTimer >= 30) //Terminator
								tryConn = false;
						
								if(selfTimer != 0){

								timerStarts = 1000
								while(timerStarts != 60000){
																
								Thread.sleep(1000); //sleep for 1 second
								timerStarts += 1000;


																
								}

								}
						
				
					sibsExtractionJob.execute(masterDate);
					
					tryConn = false;
					sibsReq = false;
					sibsRecon = false;
					
					//set false to exit loop if success
//					tryConn = false;
					println "\n--- END (SUCCESS): Extract SIBS GL Accounts, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
					return Response.status(200).entity(gson.toJson([success : true,name:'Extract SIBS GL Accounts'])).build();
			
					}catch(Exception e) {
		
					
										
					if(e.toString().toLowerCase().contains("unable to connect to sibs")){
						
					sibsReq = true;
					sibsRecon = true;
					println ("count " + selfTimer)
					if(tryConn == false){
						sibsReq = false;
						sibsRecon = false;
						e.printStackTrace();
						System.err.println("expireLcs: "+e.getCause()?.getMessage())
						println "\n--- CANNOT CONNECT TO SIBS (PLEASE TRY AFTER 15 Minutes): Extract SIBS GL Accounts, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
						return Response.status(200).entity(gson.toJson([sibsretry : true,name:'Extract SIBS GL Accounts'])).build();
					}
					
					

							   System.out.println("Error Message Start : " + e + " : Error Message End");
							   selfTimer++;
							   System.out.println("Reconnecting to SIBS...");
			//				   Log.debug("Reconnecting to SIBS...");
			
							}else{
							sibsReq = false;
							sibsRecon = false;
							e.printStackTrace();
							throw new IllegalArgumentException(e.printStackTrace()); 
							}
					
					}
			
					}
					//test end
	
			println "\n--- END (SUCCESS): Extract SIBS GL Accounts, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : true,name:'Extract SIBS GL Accounts'])).build();
		}catch(Exception e){

			e.printStackTrace();
			System.err.println("Master: "+e.getCause()?.getMessage())
			println "\n--- END (FAILED): Extract SIBS GL Accounts, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : false])).build();
		}
	}
	
	// Start IBD-16-0219-01
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/extractCic")
	public Response extractCic(@Context UriInfo allUri){
		println "\n--- START: CIC Report, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
		Gson gson = new Gson();
		MultivaluedMap<String, String> parameters = allUri.getQueryParameters();
		String reportDate = parameters?.getFirst(REPORT_DATE)?.toString();
		Assert.notNull(reportDate,"Report date must not be null!")
		println "reportDate:"+reportDate
		try{
			
			
			String masterDate = DATE_FORMATTER_DESC.format(DATE_FORMATTER_BATCH_CONTROLLER.parse(reportDate))
			println "Batch Run Date: "+masterDate
			Date runDate = DATE_FORMATTER_BATCH_CONTROLLER.parse(reportDate)
			println "runDate: " + runDate
			String systemDate = DATE_FORMATTER_DESC.format(new Date())
			println "System Date: "+systemDate
			
			def selfTimer = 0;
			def tryConn = true;
			System.out.println("Connecting to SIBS...");
				//Log.debug("Connecting to SIBS...");
			while(tryConn == true){
			try{
			
				
			if(selfTimer >= 30) //Terminator
			tryConn = false;
	
			if(selfTimer != 0){

			timerStarts = 1000
			while(timerStarts != 60000){
											
			Thread.sleep(1000); //sleep for 1 second
			timerStarts += 1000;


											
			}

			}
			
			
			
			cicExtractionJob.executeMonthly(masterDate,systemDate,runDate);
			
			
			

			
			//set false to exit loop if success
			tryConn = false;
			sibsReq = false;
			sibsRecon = false;
			
			//set false to exit loop if success
//					tryConn = false;
					println "\n--- END (SUCCESS): CIC Report, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : true,name:'CIC Report'])).build();
	
	
			}catch(Exception e) {

			
								
			if(e.toString().toLowerCase().contains("unable to connect to sibs")){
				
			sibsReq = true;
			sibsRecon = true;
			println ("count " + selfTimer)
			if(tryConn == false){
				sibsReq = false;
				sibsRecon = false;
				e.printStackTrace();
				System.err.println("expireLcs: "+e.getCause()?.getMessage())
				println "\n--- CANNOT CONNECT TO SIBS (PLEASE TRY AFTER 15 Minutes): CIC Report, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"

				return Response.status(200).entity(gson.toJson([sibsretry : true,name:'CIC Report'])).build();
			}
			
			

					   System.out.println("Error Message Start : " + e + " : Error Message End");
					   selfTimer++;
					   System.out.println("Reconnecting to SIBS...");
	//				   Log.debug("Reconnecting to SIBS...");
	
					}else{
					sibsReq = false;
					sibsRecon = false;
					e.printStackTrace();
					throw new IllegalArgumentException(e.printStackTrace());
					}
			
			}
			
			}
			
			
			
			//test end
			
			println "\n--- END (SUCCESS): CIC Report, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : true,name:'CIC Report'])).build();
			
			
			}catch(Exception e){
			e.printStackTrace();
			System.err.println("Master: "+e.getCause()?.getMessage())
			println "\n--- END (FAILED): CIC Report, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : false])).build();
		}
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/extractCicHistorical")
	public Response extractCicHistorical(@Context UriInfo allUri){
		println "\n--- START: CIC Historical Report, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
		Gson gson = new Gson();
		MultivaluedMap<String, String> parameters = allUri.getQueryParameters();
		String reportDate = parameters?.getFirst(REPORT_DATE)?.toString();
		Assert.notNull(reportDate,"Report date must not be null!")
		println "reportDate:"+reportDate
		try{
			String masterDate = DATE_FORMATTER_DESC.format(DATE_FORMATTER_BATCH_CONTROLLER.parse(reportDate))
			println "Batch Run Date: "+masterDate
			Date runDate = DATE_FORMATTER_BATCH_CONTROLLER.parse(reportDate)
			println "runDate: " + runDate
			String systemDate = DATE_FORMATTER_DESC.format(new Date())
			println "System Date: "+systemDate
			cicExtractionJob.execute(masterDate,systemDate,runDate);
			println "\n--- END (SUCCESS): CIC Historical Report, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : true,name:'CIC Historical Report'])).build();
		}catch(Exception e){
			e.printStackTrace();
			System.err.println("Master: "+e.getCause()?.getMessage())
			println "\n--- END (FAILED): CIC Historical Report, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : false])).build();
		}
	}
	
	// End IBD-16-0219-01
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/yearEndInsert")
	public Response yearEndInsert(@Context UriInfo allUri){
		println "\n--- START: Year-end Insert, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
		Gson gson = new Gson();
		MultivaluedMap<String, String> parameters = allUri.getQueryParameters();
		String reportDate = parameters?.getFirst(REPORT_DATE)?.toString();
		Assert.notNull(reportDate,"Report date must not be null!")
		println "reportDate:"+reportDate
		try{
			String masterDate = DATE_FORMATTER_DESC.format(DATE_FORMATTER_BATCH_CONTROLLER.parse(reportDate)) + "-00.00.00"
			println "masterDate:"+masterDate
			yearEndInsertJob.execute(masterDate);
			println "\n--- END (SUCCESS): Year-end Insert, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : true,name:'Sequence Number Parameter Update'])).build();
		}catch(Exception e){
			e.printStackTrace();
			System.err.println("Master: "+e.getCause()?.getMessage())
			println "\n--- END (FAILED): Year-end Insert, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : false])).build();
		}
	}
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/yearEndHolidays")
	public Response yearEndHolidays(@Context UriInfo allUri){
		println "\n--- START: Year-end Holidays, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
		Gson gson = new Gson();
		MultivaluedMap<String, String> parameters = allUri.getQueryParameters();
		String reportDate = parameters?.getFirst(REPORT_DATE)?.toString();
		Assert.notNull(reportDate,"Report date must not be null!")
		println "reportDate:"+reportDate
		try {
			String masterDate = DATE_FORMATTER_DESC.format(DATE_FORMATTER_BATCH_CONTROLLER.parse(reportDate)) + "-00.00.00"
			holidayService.generateHolidays(DATE_FORMATTER_BATCH_CONTROLLER.parse(reportDate));
			println "\n--- END (SUCCESS): Year-end Holidays, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : true,name:'Generate Holidays'])).build();
		} catch(Exception e) {
			e.printStackTrace();
			System.err.println("Master: "+e.getCause()?.getMessage())
			println "\n--- END (FAILED): Year-end Holidays, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : false])).build();
		}
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/masterException")
	public Response generateMasterExceptionReport(@Context UriInfo allUri){
		println "\n--- START: Master Exception Report, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
		Gson gson = new Gson();
		MultivaluedMap<String, String> parameters = allUri.getQueryParameters();
		String reportDate = parameters?.getFirst(REPORT_DATE)?.toString();
		Assert.notNull(reportDate,"Report date must not be null!")
		println "reportDate:"+reportDate
		try{
			String masterExceptionDate = DATE_FORMATTER_DESC.format(DATE_FORMATTER_BATCH_CONTROLLER.parse(reportDate)) + "-00.00.00"
			println "masterExceptionDate:"+masterExceptionDate
			masterExcelFileGeneratorJob.executeMasterExceptionReport(masterExceptionDate);
			println "\n--- END (SUCCESS): Master Exception Report, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : true,name:'Master Exception Report'])).build();
		}catch(Exception e){
			e.printStackTrace();
			System.err.println("Master: "+e.getCause()?.getMessage())
			println "\n--- END (FAILED): Master Exception Report, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : false])).build();
		}
	}
	
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path("/revertToPending")
//    public Response revertUnapprovedTradeServices(@Context UriInfo allUri){
//        println "\n--- START: Revert Data Entry to Pending, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
//        Gson gson = new Gson();
//		try{
//			tradeServiceRevertJob.execute();
//            println "\n--- END (SUCCESS): Revert Data Entry to Pending, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
//			return Response.status(200).entity(gson.toJson([success : true,name:'Revert Data Entry to Pending'])).build();
//		}catch(Exception e){
//            e.printStackTrace();
//			System.err.println("revertToPending: "+e.getCause()?.getMessage())
//            println "\n--- END (FAILED): Revert Data Entry to Pending, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
//			return Response.status(200).entity(gson.toJson([success : false])).build();
//		}
//    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/etsToPending")
    public Response revertUnapprovedServiceInstructions(@Context UriInfo allUri){
    	println "\n--- START: Revert e-TS to Pending, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
    	Gson gson = new Gson();
		
		// ER# 20160905-019 Start
		sibsReq = true;
		sibsRecon = true;

		try {
			def selfTimer = 0;
			def tryConn = true;

			while(tryConn == true){
				try{
					if(selfTimer >= 30) {
						tryConn = false;
					}

					if(selfTimer != 0){
						timerStarts = 1000
						while(timerStarts != 60000){
							Thread.sleep(1000);
							timerStarts += 1000;
						}
					}

					serviceInstructionRevertJob.execute();

					tryConn = false;
					sibsReq = false;
					sibsRecon = false;

					println "\n--- END (SUCCESS): Revert e-TS to Pending, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
					return Response.status(200).entity(gson.toJson([success : true,name:'Revert e-TS to Pending'])).build();

				}catch(Exception e) {

					if(e.toString().toLowerCase().contains("unable to connect to sibs")){
						sibsReq = true;
						sibsRecon = true;

						if(tryConn == false){
							sibsReq = false;
							sibsRecon = false;
							e.printStackTrace();
							System.err.println("Revert e-TS to Pending: "+e.getCause()?.getMessage())
							println "\n--- END (SIBS Disconnection): Revert e-TS to Pending:" + DateUtil.convertToTimeString(new Date()) + "---"
							return Response.status(200).entity(gson.toJson([sibsretry : true,name:'Revert e-TS to Pending'])).build();
						}

						System.out.println("Error Message Start : " + e + " : Error Message End");
						selfTimer++;
						System.out.println("Reconnecting to SIBS...");

					} else{
						sibsReq = false;
						sibsRecon = false;
						e.printStackTrace();
						throw new IllegalArgumentException(e.printStackTrace());
					}

				}
			}

			println "\n--- END (SUCCESS): Revert e-TS to Pending, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : true,name:'Revert e-TS to Pending'])).build();
			// ER# 20160905-019 End
    	}catch(Exception e){
    		e.printStackTrace();
    		System.err.println("etsToPending: "+e.getCause()?.getMessage())
    		println "\n--- END (FAILED): Revert e-TS to Pending, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
    		return Response.status(200).entity(gson.toJson([success : false])).build();
    	}
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/expireLcs")
    public Response changeExpiredLcStatuses(@Context UriInfo allUri){
        Gson gson = new Gson();
		try{
            MultivaluedMap<String, String> parameters = allUri.getQueryParameters();
            String reportDate = parameters?.getFirst(REPORT_DATE)?.toString();
            Assert.notNull(reportDate,"Report date must not be null!")

			 setExpiredLcsStatusToExpired.execute(reportDate);
			 return Response.status(200).entity(gson.toJson([success : true,name:'Process Expired LCs'])).build();
		}catch(Exception e){
            e.printStackTrace();
			System.err.println("expireLcs: "+e.getCause()?.getMessage())
			return Response.status(200).entity(gson.toJson([success : false])).build();
		}
    }
	
	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/tagLcAsExpired")
    public Response changeExpiredLcStatusesWithAccounting(@Context UriInfo allUri){
		TestTimehehe = false;
        println "\n--- START: Tag LC as Expired, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
        Gson gson = new Gson();
        try {
            MultivaluedMap<String, String> parameters = allUri.getQueryParameters();
            String reportDate = parameters?.getFirst(REPORT_DATE)?.toString();
//            Assert.notNull(reportDate,"Report date must not be null!")
			//test
			
			def selfTimer = 0;
			def tryConn = true;
			System.out.println("Connecting to SIBS...");
				//Log.debug("Connecting to SIBS...");
			while(tryConn == true){
			try{
			
				
			if(selfTimer >= 30) //Terminator
			tryConn = false;
	
			if(selfTimer != 0){

			timerStarts = 1000
			while(timerStarts != 60000){
											
			Thread.sleep(1000); //sleep for 1 second
			timerStarts += 1000;


											
			}

			}
			
			
			
			

            if(reportDate == null){
				
				tradeProductExpireJob.execute(); // for expired LC
                tradeProductRepository.updateTrade("UPDATE TRADEPRODUCT SET STATUS = 'EXPIRED' WHERE DOCUMENTNUMBER IN (SELECT LC.DOCUMENTNUMBER FROM LETTEROFCREDIT LC JOIN TRADEPRODUCT TP ON LC.DOCUMENTNUMBER = TP.DOCUMENTNUMBER WHERE LC.TYPE <> 'CASH' AND TP.STATUS IN ('OPEN','REINSTATED') AND LC.EXPIRYDATE <= CAST(CURRENT_TIMESTAMP AS TIMESTAMP))");
			
				
            } else {
                // MM-dd-yyyy
				String[] split = reportDate.split("-")
				String MM = split[0]
				String dd = split[1]
				String yyyy = split[2]
				SimpleDateFormat df1 = new SimpleDateFormat("MM/dd/yyyy");
				SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = df1.parse(MM+"/"+dd+"/"+yyyy);  // Does not have time component, meaning this is midnight...
														// ...so we need to add one day as the "limit"
				
				//Expired LC Batch Run early morning including holidays and weekends
				//tag as 'EXPIRED' for those LCs which was matured a day before
				Calendar dateToday = GregorianCalendar.getInstance();
				dateToday.setTime(date);
				dateToday.add(GregorianCalendar.DATE, -1);
				Date dateToExpire = dateToday.getTime();
				
                String timestampFormattedDate = reportDate+"-00.00.00"
                println "timestampFormattedDate:"+timestampFormattedDate
				println "Date to Expire: "+dateToExpire+"-00.00.0"
               
                
                GregorianCalendar cal = GregorianCalendar.getInstance();
                cal.setTime(date);
				//cal.add(GregorianCalendar.DATE, 1);
				
                println ">>>>>>>>>>>>>>>>>> date = ${date}"
                //println ">>>>>>>>>>>>>>>>>> limit = ${cal.getTime()}"

                String update = "UPDATE TRADEPRODUCT SET STATUS = 'EXPIRED' WHERE DOCUMENTNUMBER IN (SELECT LC.DOCUMENTNUMBER FROM LETTEROFCREDIT LC JOIN TRADEPRODUCT TP ON LC.DOCUMENTNUMBER = TP.DOCUMENTNUMBER WHERE LC.TYPE <> 'CASH' AND TP.STATUS IN ('OPEN','REINSTATED') AND LC.EXPIRYDATE <= CAST('${df2.format(cal.getTime())}' AS TIMESTAMP))"
				tradeProductExpireJob.execute(reportDate);
				//update TradeProduct table and create a reversal entry for those recently expired LC
				println "setExpiredLcsStatusToExpired.execute();"
                tradeProductRepository.updateTrade(update);
				println "tradeProductExpireJob.execute();"
				 // for expired LC

            }
			
			
			
							
			tryConn = false;
			sibsReq = false;
			sibsRecon = false;
			
			//set false to exit loop if success
//					tryConn = false;
		     println "\n--- END (SUCCESS): Tag LC as Expired, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
            return Response.status(200).entity(gson.toJson([success : true,name:'Expire LCs'])).build();
			
	
			}catch(Exception e) {
			
						
											
						if(e.toString().toLowerCase().contains("unable to connect to sibs")){
							println "error ALLLLLLLLLLLLLLLLLLLLLLLLLLLLLAAAAAANNNNN"
						sibsReq = true;
						sibsRecon = true;
						println ("count " + selfTimer)
						if(tryConn == false){
							sibsReq = false;
							sibsRecon = false;
							e.printStackTrace();
							System.err.println("expireLcs: "+e.getCause()?.getMessage())
							println "\n--- END (SUCCESS): Tag LC as Expired, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
							return Response.status(200).entity(gson.toJson([sibsretry : true,name:'Expire LCs'])).build();
			}
						
						
			
								   System.out.println("Error Message Start : " + e + " : Error Message End");
								   selfTimer++;
								   System.out.println("Reconnecting to SIBS...");
				//				   Log.debug("Reconnecting to SIBS...");
				
								}else{
								sibsReq = false;
								sibsRecon = false;
								e.printStackTrace();
								throw new IllegalArgumentException(e.printStackTrace());
								}
						
						}
			}
								//test end
			
			
			
			
			
            println "\n--- END (SUCCESS): Tag LC as Expired, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
            return Response.status(200).entity(gson.toJson([success : true,name:'Expire LCs'])).build();
			}catch(Exception e){
            e.printStackTrace();
            System.err.println("expireLcs: "+e.getCause()?.getMessage())
            println "\n--- END (FAILED): Tag LC as Expired, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
            return Response.status(200).entity(gson.toJson([success : false])).build();
        }
    }


	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/sibsTimer")
	public Response testTimer(@Context UriInfo allUri){
	Gson gson = new Gson();
	
	if(sibsReq == true)
	sibsStatus = sibsExtractionJob.checkConnection();
	else
	sibsStatus = true;
	
	return Response.status(200).entity(gson.toJson([recon : sibsStatus])).build();

		
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/reverseOrCancelLc")
	public Response reverseOrCancelLc(@Context UriInfo allUri){
		println "\n--- START: Reverse or Cancel LCs, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
		Gson gson = new Gson();
		try {
			MultivaluedMap<String, String> parameters = allUri.getQueryParameters();
			String reportDate = parameters?.getFirst(REPORT_DATE)?.toString();
//            Assert.notNull(reportDate,"Report date must not be null!")
			if(reportDate == null){
				println "report date is null"
				// tradeProductExpireJob.execute();=> earmarking is done during process revalue batch
			} else {
				// MM-dd-yyyy
				String timestampFormattedDate = reportDate+"-00.00.00"
				println "timestampFormattedDate:"+timestampFormattedDate
				println "tradeProductExpireJob.execute();"
				//tradeProductExpireJob.execute(reportDate); => earmarking is done during process revalue batch
			}
			println "\n--- END (SUCCESS): Reverse or Cancel LCs, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : true,name:'Reverse Expired LC'])).build();
		}catch(Exception e){
			e.printStackTrace();
			System.err.println("expireLcs: "+e.getCause()?.getMessage())
			println "\n--- END (FAILED): Reverse or Cancel LCs, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : false])).build();
		}
	}

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/purgeEts")
    public Response purgeEts(@Context UriInfo allUri){
        println "\n--- START: Purge Unapproved ETS, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
        Gson gson = new Gson();
        String reportDate = allUri.getQueryParameters()?.getFirst(REPORT_DATE)?.toString();
        Assert.notNull(reportDate,"Report date must not be null!")
        println "reportDate: " + reportDate
        try{
			

			
		
			
			
			def selfTimer = 0;
			def tryConn = true;
			System.out.println("Connecting to SIBS...");
				//Log.debug("Connecting to SIBS...");
			while(tryConn == true){
			try{
			
				
			if(selfTimer >= 30) //Terminator
			tryConn = false;
	
			if(selfTimer != 0){

			timerStarts = 1000
			while(timerStarts != 60000){
											
			Thread.sleep(1000); //sleep for 1 second
			timerStarts += 1000;


											
			}

			}
			String[] documentNumbers = tradeServiceRepository.getDocumentNumbersOfUnapprovedEts(DATE_FORMATTER.parse(reportDate))
			println "documentNumbers: " + documentNumbers
		
			
			cancelDormantEtsJob.execute(reportDate);
			
			if (documentNumbers.length > 0){
				
				facilityReferenceNormalization.deleteOutstandingUnapprovedFacilityAvailment(documentNumbers)
			
				println facilityReferenceNormalization.test(documentNumbers)
			}
			
						
		
			sibsReq = false;
			sibsRecon = false;
			tryConn = false;
			

			//set false to exit loop if success

			println "\n--- END (SUCCESS): Purge Unapproved ETS, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : true,name:'Purge Unapproved ETS'])).build();
	   
		
	
			}catch(Exception e) {

			
								

//			checkConn = sibsExtractionJob.checkConnection();
			if(e.toString().toLowerCase().contains("unable to connect to sibs")){
//			if(checkConn == false){
				sibsReq = true;
				sibsRecon = true;
			println ("count " + selfTimer)
			if(tryConn == false){
				sibsReq = false;
				sibsRecon = false;
				e.printStackTrace();
				System.err.println("expireLcs: "+e.getCause()?.getMessage())
				println "\n--- CANNOT CONNECT TO SIBS (PLEASE TRY AFTER 15 Minutes): Purge Unapproved ETS, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"

				return Response.status(200).entity(gson.toJson([sibsretry : true,name:'Purge Unapproved ETS'])).build();
			}
			
			

					   System.out.println("Error Message Start : " + e + " : Error Message End");
					   selfTimer++;
					   System.out.println("Reconnecting to SIBS...");
	//				   Log.debug("Reconnecting to SIBS...");
	
					}else{
					sibsReq = false;
					sibsRecon = false;
					e.printStackTrace();
					throw new IllegalArgumentException(e.printStackTrace());
					}
			
			}
			
			}
			
			//test end
			
			
						
            println "\n--- END (SUCCESS): Purge Unapproved ETS, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : true,name:'Purge Unapproved ETS'])).build();
	   
			
			
			
	
	}catch(Exception e){
	   		System.err.println("purgeEts: "+e.getCause()?.getMessage())
            println "\n--- END (FAILED): Purge Unapproved ETS, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
		    return Response.status(200).entity(gson.toJson([success : false])).build();
	   }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/gl")
    public Response generateGl(@Context UriInfo allUri){
		println "\n--- START: GL Movement, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
        Gson gson = new Gson();
		try{
			MultivaluedMap<String, String> parameters = allUri.getQueryParameters();
			String reportDate = parameters?.getFirst(REPORT_DATE)?.toString();
			Assert.notNull(reportDate,"Report date must not be null!")
            println "reportDate = ${reportDate}"
            glReportGenerator.execute(GL_QUERY, DATE_FORMATTER.parse(reportDate), getDate(reportDate));
			println "tag all gl as posted"
			accountingService.updateIsPosted(true);
			
			Boolean balanceChecking = accountingService.balanceChecking();
			println "\n.. Checking Accounting Entries Debit vs Credit..."
			if(balanceChecking) {
				println "All are balance"
			}else {
				throw new Exception("GL Entires are Not Balance");
			}
			
            println "\n--- END (SUCCESS): GL Movement, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success:true,name:'GL Movement'])).build();
	   }catch(Exception e){
            e.printStackTrace();
	   		System.err.println("gl: "+e.getCause()?.getMessage())
            println "\n--- END (FAILED): GL Movement, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : false])).build();
	   }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/glis")
    public Response generateGlis(@Context UriInfo allUri){
		println "\n--- START: GLIS, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
        Gson gson = new Gson();
		try{
			MultivaluedMap<String, String> parameters = allUri.getQueryParameters();
			String reportDate = parameters?.getFirst(REPORT_DATE)?.toString();
			Assert.notNull(reportDate,"Report date must not be null!")
            println "reportDate = ${reportDate}"
			glisReportGenerator.execute(DATE_FORMATTER.parse(reportDate))
            println "\n--- END (SUCCESS): GLIS, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success:true,name:'GLIS Hand-Off File'])).build();
	   }catch(Exception e){
            e.printStackTrace();
	   		System.err.println("glis: "+e.getCause()?.getMessage())
            println "\n--- END (FAILED): GLIS, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : false])).build();
	   }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/abortPendingEtsReversal")
    public Response abortPendingEtsReversal(@Context UriInfo allUri){
		println "\n--- START: Abort Pending ETS Reversal, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
        Gson gson = new Gson();
		try{
			abortPendingEtsReversalJob.execute();
            println "\n--- END (SUCCESS): Abort Pending ETS Reversal, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : true,name:'Abort Pending ETS Reversal'])).build();
	   }catch(Exception e){
	   		System.err.println("abortPendingEtsReversal: "+ e.getCause()?.getMessage())
            println "\n--- END (FAILED): Abort Pending ETS Reversal, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
		    return Response.status(200).entity(gson.toJson([success : false])).build();
	   }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/synchronizeRoutingStatus")
    public Response synchronizeRoutingStatus(@Context UriInfo allUri){
		println "\n--- START: Synchronize Routing Status, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
		MultivaluedMap<String, String> parameters = allUri.getQueryParameters();
		String reportDate = parameters?.getFirst(REPORT_DATE)?.toString();
        Gson gson = new Gson();
        Assert.notNull(parameters?.getFirst(REPORT_DATE)?.toString(),"Report date must not be null!")
		String p_message = "";
        try{
            String dateStr = parameters?.getFirst(REPORT_DATE)?.toString();
            java.sql.Date date = getDate2(dateStr);
			
			p_message = synchronizeRoutingStatusCsvJob.execute(SYNCHRONIZE_ROUTING_STATUS, , 'callableFlag', date.toString());
            println "\n--- END (SUCCESS):  Synchronize Routing Status JOB, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : true,name: p_message.startsWith("2") ? p_message :'Synchronize Routing Status'])).build();
	   }catch(Exception e){
	   		System.err.println("synchronizeRoutingStatus: "+ e.getCause()?.getMessage())
            println "\n--- END (FAILED): Synchronize Routing Status, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
		    return Response.status(200).entity(gson.toJson([success : false,name: 'dbException', p_message : e.getCause()?.getMessage()])).build();
	   }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/params")
	public Response generateParameterFiles(@Context UriInfo allUri){
		println "\n--- START Parameter, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
		Gson gson = new Gson();
		Map returnMap = new HashMap();
		MultivaluedMap<String, String> parameters = allUri.getQueryParameters();
		String reportDate = parameters?.getFirst(REPORT_DATE)?.toString();
		Assert.notNull(reportDate,"Report date must not be null!")
		println "reportDate:"+reportDate
		try{


			//test

			def selfTimer = 0;
			def tryConn = true;
			System.out.println("Connecting to SIBS...");
			//Log.debug("Connecting to SIBS...");
			while(tryConn == true){
				try{


					if(selfTimer >= 30) //Terminator
						tryConn = false;

					if(selfTimer != 0){

						timerStarts = 1000
						while(timerStarts != 60000){

							Thread.sleep(1000); //sleep for 1 second
							timerStarts += 1000;



						}

					}



					glParameterFileJob.execute(GL_PARAMETER_FILE_SQL,DATE_FORMATTER_BATCH_CONTROLLER.parse(reportDate));



					tryConn = false;
					sibsReq = false;
					sibsRecon = false;

					println "\n--- END (SUCCESS): GL Parameter:" + DateUtil.convertToTimeString(new Date()) + "---"
					return Response.status(200).entity(gson.toJson([success : true,name:'GL Parameter'])).build();

				}catch(Exception e) {



					//			if(e.toString().toLowerCase().contains("unable to connect to sibs")){
					def states = sibsExtractionJob.checkConnection();
					if(states == false){
						sibsReq = true;
						sibsRecon = true;
						println ("count " + selfTimer)
						if(tryConn == false){
							sibsReq = false;
							sibsRecon = false;
							e.printStackTrace();
							System.err.println("expireLcs: "+e.getCause()?.getMessage())
							println "\n--- CANNOT CONNECT TO SIBS (PLEASE TRY AFTER 15 Minutes): GL Parameter:" + DateUtil.convertToTimeString(new Date()) + "---"
							return Response.status(200).entity(gson.toJson([sibsretry : true,name:'GL Parameter'])).build();

							//			println "\n--- CANNOT CONNECT TO SIBS (PLEASE TRY AFTER 15 Minutes):" + DateUtil.convertToTimeString(new Date()) + "---"
							//	        return Response.status(200).entity(gson.toJson([sibsretry : true,name:'GL Parameter'])).build();

						}



						System.out.println("Error Message Start : " + e + " : Error Message End");
						selfTimer++;
						System.out.println("Reconnecting to SIBS...");
						//				   Log.debug("Reconnecting to SIBS...");

					}else{
						sibsReq = false;
						sibsRecon = false;
						e.printStackTrace();
						throw new IllegalArgumentException(e.printStackTrace());
					}

				}

			}
			//test end



		}catch(Exception e){
			System.err.println("Parameter: "+ e.getCause()?.getMessage())
			println "\n--- END (FAILED): GL Parameter, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : false])).build();
		}
	}

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/allocation")
    public Response generateAllocation(@Context UriInfo allUri){
        println "\n--- START Allocation, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
        Gson gson = new Gson();
        Map returnMap = new HashMap();
        MultivaluedMap<String, String> parameters = allUri.getQueryParameters();
        String reportDate = parameters?.getFirst(REPORT_DATE)?.toString();
        println "reportDate:"+reportDate
        Assert.notNull(reportDate,"Report date must not be null!")
        try{
            glAllocationsReportJob.execute(DATE_FORMATTER_BATCH_CONTROLLER.parse(reportDate),"561501030000","10903");
			glAllocationsExcelJob.executeToExcel(DATE_FORMATTER_BATCH_CONTROLLER.parse(reportDate),"561501030000","10903");
            println "\n--- END (SUCCESS): Allocation:" + DateUtil.convertToTimeString(new Date()) + "---"
            return Response.status(200).entity(gson.toJson([success : true,name:'Allocation File'])).build();
        }catch(Exception e){
            e.printStackTrace()
            System.err.println("Allocation: "+ e.getCause()?.getMessage())
            println "\n--- END (FAILED): Allocation, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
            return Response.status(200).entity(gson.toJson([success : false])).build();
        }
    }
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/allocationException")
	public Response generateAllocationExceptionReport(@Context UriInfo allUri){
		println "\n--- START Allocation Exception Report, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
		Gson gson = new Gson();
		Map returnMap = new HashMap();
		MultivaluedMap<String, String> parameters = allUri.getQueryParameters();
		String reportDate = parameters?.getFirst(REPORT_DATE)?.toString();
		println "reportDate:"+reportDate
		Assert.notNull(reportDate,"Report date must not be null!")
		try{
			glAllocationsExcelJob.executeAllocationExceptionReport(DATE_FORMATTER_BATCH_CONTROLLER.parse(reportDate),"561501030000","10903");
			println "\n--- END (SUCCESS): Allocation Exception Report:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : true,name:'Allocation Exception Report'])).build();
		}catch(Exception e){
			e.printStackTrace()
			System.err.println("Allocation: "+ e.getCause()?.getMessage())
			println "\n--- END (FAILED): Allocation Exception Report, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : false])).build();
		}
	}

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/transaction")
    public Response generateTransactionFile(@Context UriInfo allUri){
		println "\n--- START: AMLA Transaction / Totals, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
        Gson gson = new Gson();
        MultivaluedMap<String, String> parameters = allUri.getQueryParameters();
        Assert.notNull(parameters?.getFirst(REPORT_DATE)?.toString(),"Report date must not be null!")
        try{
            String dateStr = parameters?.getFirst(REPORT_DATE)?.toString();
            java.sql.Date date = getDate2(dateStr);
            println "reportDate = ${dateStr}"
            transactionLogJob.execute(TRANSACTION_LOG_QUERY, date);
            amlaTotalsLogJob.execute(AMLA_TOTALS_QUERY, date, date, date);
			amlaExpiredLcService.deleteTransLogBatchFlag();
            println "\n--- END (SUCCESS): AMLA Transaction / Totals, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
            return Response.status(200).entity(gson.toJson([success:true,name:'AMLA Transaction'])).build();
        }catch(Exception e){
            e.printStackTrace();
            System.err.println("Transaction: "+e.getCause()?.getMessage())
            println "\n--- END (FAILED): AMLA Transaction / Totals, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : false])).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/customerAccount")
    public Response generateCustomerAccountFile(@Context UriInfo allUri){
		println "\n--- START: AMLA Customer Account, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
        Gson gson = new Gson();
        MultivaluedMap<String, String> parameters = allUri.getQueryParameters();
        Assert.notNull(parameters?.getFirst(REPORT_DATE)?.toString(),"Report date must not be null!")
		try{
            String dateStr = parameters?.getFirst(REPORT_DATE)?.toString();
            java.sql.Date date = getDate2(dateStr);
            println "reportDate = ${dateStr}"
			customerAccountLogGeneratorJob.execute(CUSTOMER_ACCOUNT_LOG_QUERY, date);
			amlaExpiredLcService.deleteCustomerAccBatchFlag();
            println "\n--- END (SUCCESS): AMLA Customer Account, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
		//return to original code
			return Response.status(200).entity(gson.toJson([success:true,name:'AMLA Customer Account'])).build();
		}catch(Exception e){
            e.printStackTrace();
			System.err.println("customer account: "+e.getCause()?.getMessage())
            println "\n--- END (FAILED): AMLA Customer Account, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			//return to original code
			return Response.status(200).entity(gson.toJson([success : false])).build();
		}
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/account")
    public Response generateAccountFile(@Context UriInfo allUri){
		println "\n--- START: AMLA Account, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
        Gson gson = new Gson();
        MultivaluedMap<String, String> parameters = allUri.getQueryParameters();
        Assert.notNull(parameters?.getFirst(REPORT_DATE)?.toString(),"Report date must not be null!")
		try{
            String dateStr = parameters?.getFirst(REPORT_DATE)?.toString();
            
            java.sql.Date date = getDate2(dateStr);
            println "reportDate = ${dateStr}"
			accountLogJob.execute(ACCOUNT_LOG_QUERY, date);
			amlaExpiredLcService.deleteAccountLogBatchFlag();
            println "\n--- END (SUCCESS): AMLA Account, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success:true,name:'AMLA Account'])).build();
		}catch(Exception e){
            e.printStackTrace();
			System.err.println("account: "+e.getCause()?.getMessage())
            println "\n--- END (FAILED): AMLA Account, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : false])).build();
		}
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/customer")
    public Response generateCustomerFile(@Context UriInfo allUri){
		println "\n--- START: AMLA Customer, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
        Gson gson = new Gson();
        MultivaluedMap<String, String> parameters = allUri.getQueryParameters();
        Assert.notNull(parameters?.getFirst(REPORT_DATE)?.toString(),"Report date must not be null!")
        try{
            String dateStr = parameters?.getFirst(REPORT_DATE)?.toString();
            
            java.sql.Date date = getDate2(dateStr);
            println "reportDate = ${dateStr}"
            customerLogGeneratorJob.execute(CUSTOMER_LOG_QUERY, date);
            println "\n--- END (SUCCESS): AMLA Customer, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
            return Response.status(200).entity(gson.toJson([success:true,name:'AMLA Customer'])).build();
        }catch(Exception e){
            e.printStackTrace();
            System.err.println("customer: "+e.getCause()?.getMessage())
            println "\n--- END (FAILED): AMLA Customer, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : false])).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/balance")
	public Response recordEndOfDayBalance(@Context UriInfo allUri) {

		println "\n--- START: Balance, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
		println 'recordEndOfDayBalance'
		Gson gson = new Gson();
		Map returnMap = new HashMap();
		MultivaluedMap<String, String> parameters = allUri.getQueryParameters();



		try {


			//test

			def selfTimer = 0;
			def tryConn = true;
			System.out.println("Connecting to SIBS...");
			//Log.debug("Connecting to SIBS...");
			while(tryConn == true){
				try{


					if(selfTimer >= 30) //Terminator
						tryConn = false;

					if(selfTimer != 0){

						timerStarts = 1000
						while(timerStarts != 60000){

							Thread.sleep(1000); //sleep for 1 second
							timerStarts += 1000;



						}

					}


					String currentDate = parameters?.getFirst(REPORT_DATE)?.toString()

					if (currentDate != null) {

						println "currentDate = ${currentDate}"
						dailyBalanceRecorderJob.execute(currentDate);

					} else {

						println "getAppServerDateString() = ${getAppServerDateString()}"
						dailyBalanceRecorderJob.execute(getAppServerDateString());
					}


					tryConn = false;
					sibsReq = false;
					sibsRecon = false;
					println "\n--- END (SUCCESS): Balance, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"


					return Response.status(200).entity(gson.toJson([success:true,name:'Balance'])).build();



				}catch(Exception e) {


					def states = sibsExtractionJob.checkConnection();
					if(states == false){
						//														if(e.toString().toLowerCase().contains("unable to connect to sibs")){

						sibsReq = true;
						sibsRecon = true;
						println ("count " + selfTimer)
						if(tryConn == false){
							sibsReq = false;
							sibsRecon = false;
							e.printStackTrace();


							println "\n--- CANNOT CONNECT TO SIBS (PLEASE TRY AFTER 15 Minutes): Balance, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
							return Response.status(200).entity(gson.toJson([sibsretry:true,name:'Balance'])).build();


						}



						System.out.println("Error Message Start : " + e + " : Error Message End");
						selfTimer++;
						System.out.println("Reconnecting to SIBS...");
						//				   Log.debug("Reconnecting to SIBS...");

					}else{
						sibsReq = false;
						sibsRecon = false;
						e.printStackTrace();
						throw new IllegalArgumentException(e.printStackTrace());
					}

				}

			}





		} catch(Exception e) {

			System.err.println("Balance: " + e.printStackTrace())
			System.err.println("Balance: " + e.getCause()?.getMessage())
			println "\n--- END (FAILED): Balance, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : false])).build();
		}




	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/cifNormalization")
	public Response updateCifNumber(@Context UriInfo allUri){
		println "\n--- START: CIF Normalization, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
		Gson gson = new Gson();
		// ER# 20140909-038 : Start
		MultivaluedMap<String, String> parameters = allUri.getQueryParameters();
		String reportDate = parameters?.getFirst(REPORT_DATE)?.toString()
		println "reportDate: " + reportDate
		Date appdate = getDate2(reportDate)
		try{
			long julianDate = toJulianDate(appdate)
			cifNormalizationProcess.execute(julianDate)
					
			println "\n--- END (SUCCESS): CIF Normalization, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success:true,name:'CIF Normalization'])).build();			
			// ER# 20140909-038 : End
	   }catch(Exception e){
			System.err.println("CIF Normalization: "+e);
			e.printStackTrace();
			println "\n--- END (FAILED): CIF Normalization, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : false])).build();
	   }
	}
	// ER# 20140909-038 : Start
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/cifNormalization2")
	public Response extractCifNumbers(@Context UriInfo allUri){
		println "\n--- START: Extract Normalized CIFs, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
		Gson gson = new Gson();
		
		try{
			cifNormalizationProcess.compareCif()
					
			println "\n--- END (SUCCESS): Extract Normalized CIFs, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success:true,name:'Extracrt Normalized CIFs'])).build();
			
	   }catch(Exception e){
			System.err.println("Extract Normalized CIFs: "+e);
			e.printStackTrace();
			println "\n--- END (FAILED): Extract Normalized CIFs, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : false])).build();
	   }
	}
	// ER# 20140909-038 : End
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/cifPurging")
	public Response cifPurging(@Context UriInfo allUri){
		println "\n--- START CIF Purging, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
		Gson gson = new Gson();
		try{
			
			cifPurgingGeneratorJob.execute();
			println "\n--- END (SUCCESS): CIF Purging, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success:true,name:'CIF Purging'])).build();
	   }catch(Exception e){
			System.err.println("CIF PURGING: "+e);			
			e.printStackTrace();
			println "\n--- END (FAILED): CIF Purging, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : false])).build();
	   }
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/expiredAmla")
	public Response expiredAmla(@Context UriInfo allUri){
		try{
			//test

			def selfTimer = 0;
			def tryConn = true;
			System.out.println("Connecting to SIBS...");
			//Log.debug("Connecting to SIBS...");
			while(tryConn == true){
				try{


					if(selfTimer >= 30) //Terminator
						tryConn = false;

					if(selfTimer != 0){

						timerStarts = 1000
						while(timerStarts != 60000){

							Thread.sleep(1000); //sleep for 1 second
							timerStarts += 1000;



						}

					}

					Gson gson = new Gson();
					SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
					MultivaluedMap<String, String> parameters = allUri.getQueryParameters();
					System.out.println("==================: "+parameters);
					String dateFrom = parameters.getFirst("dateFrom").toString();
					String dateTo = parameters.getFirst("dateTo").toString();
					System.out.println(dateFrom+" "+dateTo);

					Date date1 = new Date();
					Date date2 = new Date();

					if(parameters.getFirst("date")!=null){
						println("DAILY BATCH RUN");
						date1 = sdf.parse(parameters.getFirst("date").toString());
						date2 = sdf.parse(parameters.getFirst("date").toString());
					}else{
						println("ADHOC");
						date1 = sdf.parse(dateFrom);
						date2 = sdf.parse(dateTo);
					}

					amlaExpiredLcService.adhocExpiredAmla(date1,date2);

					tryConn = false;
					sibsReq = false;
					sibsRecon = false;

					println "\n--- END (SUCCESS): AMLA_EXPIRED, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"

					return Response.status(200).entity(gson.toJson([success:true,name:'Amla Expire LC'])).build();



				}catch(Exception e) {



					//			if(e.toString().toLowerCase().contains("unable to connect to sibs")){
					def states = sibsExtractionJob.checkConnection();
					if(states == false){
						sibsReq = true;
						sibsRecon = true;
						println ("count " + selfTimer)
						if(tryConn == false){
							sibsReq = false;
							sibsRecon = false;
							e.printStackTrace();
							System.err.println("CIF PURGING: "+e);
							println "\n--- CANNOT CONNECT TO SIBS (PLEASE TRY AFTER 15 Minutes): AMLA_EXPIRED, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"

							return Response.status(200).entity(gson.toJson([sibsretry:true,name:'Amla Expire LC'])).build();


						}



						System.out.println("Error Message Start : " + e + " : Error Message End");
						selfTimer++;
						System.out.println("Reconnecting to SIBS...");
						//				   Log.debug("Reconnecting to SIBS...");

					}else{
						sibsReq = false;
						sibsRecon = false;
						e.printStackTrace();
						throw new IllegalArgumentException(e.printStackTrace());
						
					}

				}

			}
			//test end



		}catch(Exception e){
			System.err.println("CIF PURGING: "+e);
			println "\n--- END (FAILED): AMLA_EXPIRED, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : false])).build();
		}

	}

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/dw")
    public Response generateDwParameterFiles(@Context UriInfo allUri){
		println "\n--- START DW Parameter, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
        Gson gson = new Gson();
        Map returnMap = new HashMap();
        MultivaluedMap<String, String> parameters = allUri.getQueryParameters();
		try{
	        String reportDate = parameters?.getFirst(REPORT_DATE)?.toString();
	        Assert.notNull(reportDate,"Report date must not be null!")
            println "reportDate:"+reportDate
	        dwParameterRecord.execute(DW_PARAMETER_FILE_SQL,DATE_FORMATTER_BATCH_CONTROLLER.parse(reportDate));
			println "\n--- END (SUCCESS): DW Parameter, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
	        return Response.status(200).entity(gson.toJson([success : true,name:'DW Parameter'])).build();
    	}catch(Exception e){
			e.printStackTrace();
			System.err.println("DW: "+e.getCause()?.getMessage())
			println "\n--- END (FAILED): DW Parameter, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : false])).build();
		}
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/cbr")
    public Response generateDwCbrParameterFiles(@Context UriInfo allUri){
		println "\n--- START CBR Parameter, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
        Gson gson = new Gson();
        Map returnMap = new HashMap();
        MultivaluedMap<String, String> parameters = allUri.getQueryParameters();
		try{
	        String reportDate = parameters?.getFirst(REPORT_DATE)?.toString();
	        Assert.notNull(reportDate,"Report date must not be null!")
            println "reportDate:"+reportDate
            cbrParameterFileGenerator.execute(CBR_PARAMETER_FILE_SQL,DATE_FORMATTER_BATCH_CONTROLLER.parse(reportDate));
			println "\n--- END (SUCCESS): CBR Parameter, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
	        return Response.status(200).entity(gson.toJson([success : true,name:'CBR Parameter'])).build();
    	}catch(Exception e){
			e.printStackTrace();
			System.err.println("CBR Parameter: "+e.getCause()?.getMessage())
			println "\n--- END (FAILED): CBR Parameter, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : false])).build();
		}
    }

	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/tagAsPending")
	public Response tagAsPending(@Context UriInfo allUri, String postRequestBody) {
        println "\n--- START: Tag As Pending, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
		Gson gson = new Gson()

		String result="";
		Map returnMap = new HashMap();

		try {
			Map formDetails = gson.fromJson(postRequestBody, Map.class);
			String errorMessage = ""

			try{
				pas5FilesLoaderService.tagAsPending()

				returnMap.put("success", true);
				returnMap.put("name", "Tag As Pending");

                println "\n--- END (SUCCESS): Tag As Pending, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"

			} catch(CasaServiceException e){
//				errorMessage = e.getErrorCode() + " : "  + e.getCasaErrorMessage();
				println "ERROR: "+ e.getErrorCode() + " : "  + e.getCasaErrorMessage();
//                returnMap.put("status", "error");
//                returnMap.put("error", errorMessage);
                println "\n--- END (FAILED): Tag As Pending, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
				returnMap.put("success", false);
			}
		
			
			} catch(Exception e) {

			Map errorDetails = new HashMap();

			e.printStackTrace();

//            errorDetails.put("code", e.getMessage());
//            errorDetails.put("description", e.toString());
//
//            returnMap.put("status", "error");
//            returnMap.put("error", errorDetails);

            println "\n--- END (FAILED): Tag As Pending, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"

			returnMap.put("success", false);
		}

		// format return data as json
		result = gson.toJson(returnMap);

		return Response.status(200).entity(result).build();
	}

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/revalueEarmark")
    public Response revalueEarmark(@Context UriInfo allUri, String postRequestBody) {
        println "\n--- START: Re Value Earmark, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
        Gson gson = new Gson()

        String result="";
        Map returnMap = new HashMap();

//        try {
           

            try{
				
				def selfTimer = 0;
				def tryConn = true;
				System.out.println("Connecting to SIBS...");
					//Log.debug("Connecting to SIBS...");
				while(tryConn == true){
				try{
				
					
				if(selfTimer >= 30) //Terminator
				tryConn = false;
		
				if(selfTimer != 0){

				timerStarts = 1000
				while(timerStarts != 60000){
												
				Thread.sleep(1000); //sleep for 1 second
				timerStarts += 1000;


												
				}

				}
				
				
				Map formDetails = gson.fromJson(postRequestBody, Map.class);
				String errorMessage = ""
				//Added from outside try catch
				
                batchFacilityRevaluationJob.execute();

                

				
				
				tryConn = false;
				sibsReq = false;
				sibsRecon = false;
				
				returnMap.put("success", true);
				returnMap.put("name", "Process Revalue");
				println "\n--- END (SUCCESS): Re Value Earmark, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
				//set false to exit loop if success
//					tryConn = false;
				        result = gson.toJson(returnMap);

        return Response.status(200).entity(result).build();
    
		
				}catch(Exception e) {
	
				
									
				if(e.toString().toLowerCase().contains("unable to connect to sibs")){
					
				sibsReq = true;
				sibsRecon = true;
				println ("count " + selfTimer)
				if(tryConn == false){
					sibsReq = false;
					sibsRecon = false;
					e.printStackTrace();
					System.err.println("expireLcs: "+e.getCause()?.getMessage())
					println "\n--- CANNOT CONNECT TO SIBS (PLEASE TRY AFTER 15 Minutes): Re Value Earmark, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"			
			
					returnMap.put("sibsretry", true);
					returnMap.put("name", "Process Revalue");
					
					}
				
				

						   System.out.println("Error Message Start : " + e + " : Error Message End");
						   selfTimer++;
						   System.out.println("Reconnecting to SIBS...");
		//				   Log.debug("Reconnecting to SIBS...");
		
						}else{
						sibsReq = false;
						sibsRecon = false;
						e.printStackTrace();
						throw new IllegalArgumentException(e.printStackTrace());
						}
				
				}
				
				}
						
				
		
				
				//test end

            }catch (Exception e){
			
			Map errorDetails = new HashMap();
//				errorMessage = e.getErrorCode() + " : "  + e.getCasaErrorMessage();
                println "ERROR: "+ e.printStackTrace()
//                returnMap.put("status", "error");
//                returnMap.put("error", errorMessage);
                println "\n--- END (FAILED): Re Value Earmark, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
                returnMap.put("success", false);
            }
//        } catch(Exception e) {
//
//            Map errorDetails = new HashMap();
//
//            e.printStackTrace();
//
////            errorDetails.put("code", e.getMessage());
////            errorDetails.put("description", e.toString());
////
////            returnMap.put("status", "error");
////            returnMap.put("error", errorDetails);
//
//            println "\n--- END (FAILED): Re Value Earmark, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
//
//            returnMap.put("success", false);
//        }

        // format return data as json
        result = gson.toJson(returnMap);

        return Response.status(200).entity(result).build();
    }
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/processCicls")
    public Response processCicls(@Context UriInfo allUri, String postRequestBody) {
		MultivaluedMap<String, String> parameters = allUri.getQueryParameters();
		Gson gson = new Gson()
		try{
	       	String reportDate = parameters?.getFirst(REPORT_DATE)?.toString();
			Assert.notNull(reportDate,"Report date must not be null!")
			println "\n--- SIMULAAAA Naaaa:" + reportDate + "---"
			ciclsProcessorJob.execute(reportDate);
			println "\n--- END (SUCCESS):" + DateUtil.convertToTimeString(new Date()) + "---"
	        return Response.status(200).entity(gson.toJson([success : true,name:'Process CICLS Records'])).build();
		}catch(Exception e){
			e.printStackTrace()
			System.err.println("Parameter: "+ e.getCause()?.getMessage())
			println "\n--- END (FAILED): Process CICLS, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : false])).build();
		}
    }

	@GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/generateCiclsFile")
    public Response generateCiclsFile(@Context UriInfo allUri){
		println "\n--- START: CICLS, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
        Gson gson = new Gson();
		try{
			MultivaluedMap<String, String> parameters = allUri.getQueryParameters();
			String reportDate = parameters?.getFirst(REPORT_DATE)?.toString();
            println "reportDate = ${reportDate}"
			ciclsReportGeneratorJob.execute(reportDate)
            println "\n--- END (SUCCESS): CICLS, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success:true,name:'Generate CICLS Hand-Off File'])).build();
	   }catch(Exception e){
            e.printStackTrace();
	   		System.err.println("cicls: "+e.getCause()?.getMessage())
            println "\n--- END (FAILED): Generate CICLS, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : false])).build();
	   }
    }
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/processRefBanks")
	public Response processRefBanks(@Context UriInfo allUri, String postRequestBody) {
		println "\n--- START: Process Ref Banks, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
		Gson gson = new Gson();

		String responseText="";
		Map returnMap = new HashMap();
		boolean responseStatus=true
		
		try {
			Map<String,Object> placeholder=new HashMap<String,Object>()
			List<String> columnList=new ArrayList<String>()
			int successCtr=0;
			int failCtr=0;
//			println "+++++++++rawr++++++++++++++++++++++"
//			println appProperties.object.getProperty("tfs.rmi.port")
//			appProperties.getObject()
//			println "+++++++++rawr++++++++++++++++++++++"
//			def fileMatcher = ~/(?i).*\.txt/
			FileInputStream fis = null
			boolean fileExist = false

			File refBankFile = new File(appProperties.object.getProperty('batch.bic.directory') + appProperties.object.getProperty('batch.bic.filename.all'))
			
			if(refBankFile == null){
				refBankFile = new File(appProperties.object.getProperty('batch.bic.directory') + appProperties.object.getProperty('batch.bic.filename.delta'))
			}
			
			if(refBankFile == null){
				throw new RuntimeException("FI.txt or FIDELTA.txt NOT FOUND")
			}
			
//			if(refBankFile.list() != null && refBankFile.list().length < 1){
//				throw new Exception("No File Found.")
//			}
			println refBankFile

			
			File backupFileDirectory= new File(appProperties.object.getProperty('batch.bic.backup.directory'))
			File bicBackupFile = null
			
			if(!backupFileDirectory.exists()){
				backupFileDirectory.mkdir()
			}
			
//			refBankFile.eachFileMatch(FileType.FILES,fileMatcher){f ->
				try{
					fis = new FileInputStream(refBankFile)
					bicBackupFile = new File(appProperties.object.getProperty('batch.bic.backup.directory') + refBankFile.getName())
					Scanner sc=new Scanner(fis);
					if(sc.hasNext()){
						String[] columnArray=sc.nextLine().split("\t")
							for(String s:columnArray){
								columnList.add(mapParametersToRefBankObject(s))
							}
					}
					
					while(sc.hasNext()){
						String[] oneLine=sc.nextLine().split("\t")
								
						placeholder=new HashMap<String,Object>()
						for(int x=0;x<columnList.size();x++){
							try{
								placeholder.put(columnList.get(x), oneLine[x])
							}catch(ArrayIndexOutOfBoundsException ex){
								continue;
							}
						}
						if(!refBankService.processRefBankDetails(placeholder)){
							failCtr++;
							responseStatus=false
						}else{
							successCtr++;
						}
					}
					
					//delete file after use
					fis.close()
					sc.close()
					if(refBankFile.exists()){
						FileUtil.copyFile(refBankFile, bicBackupFile)
						refBankFile.delete()						
					}
				}catch(Exception e){
					throw new Exception("EXCEPTION IN EACH FILE MATCH\n", e)
				}finally{
					IOUtils.closeQuietly(fis)
				}
//			}
			
			println "\n====FINISHED UPLOAD REF BANK=================="
			println "NO ERRORS ENCOUNTERED: "+responseStatus
			println "TOTAL ROWS: "+ (failCtr+successCtr)
			println "ROWS PROCESSED SUCCESS: "+successCtr
			println "ROWS PROCESSED FAIL: "+failCtr
			println "=============================================="
			
//			Map formDetails = gson.fromJson(postRequestBody, Map.class);
//			List<Map<String,Object>> bankList= (List<Map<String,Object>>) formDetails.get("responseTextList")
			
//			refBankService.processRefBankDetails(bankList);

			if(!responseStatus){
				returnMap.put("success", false);
				returnMap.put("failRows",failCtr);
				returnMap.put("totalRows",(failCtr+successCtr));
				returnMap.put("name", "Process Ref Banks");
				responseText = gson.toJson(returnMap);
				println "\n--- END (FAILED): Process Ref Banks, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
				return Response.status(404).entity(responseText).build();
			}else{
				returnMap.put("success", true);
				returnMap.put("successRows",successCtr);
				returnMap.put("totalRows",(failCtr+successCtr));
				returnMap.put("name", "Process Ref Banks");
				responseText = gson.toJson(returnMap);
				println "\n--- END (SUCESS): Process Ref Banks, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
				return Response.status(200).entity(responseText).build();
			}
		}catch(Exception e){
			e.printStackTrace();
			returnMap.put("success", false);
			returnMap.put("errorCode", e.getMessage());
			returnMap.put("description", e.toString());
			returnMap.put("name", "Process Ref Banks");
			responseText = gson.toJson(returnMap);
			println "\n--- END (FAILED): Process Ref Banks, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(500).entity(responseText).build();
		}
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/processRmaDocument")
	public Response processRmaDocument(@Context UriInfo allUri, String postRequestBody) {
		println "\n--- START: Process RMA Document, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
		Gson gson = new Gson();

		String responseText="";
		Map returnMap = new HashMap();

		try {

			int successCtr=0;
			int failCtr=0;
			int emptyCtr=0;
			int banksToSaveCtr=0;
			int banksProcessed=0;
			int processResult=0;
			
			def fileMatcher = ~/(?i).*\.xml/
			FileInputStream fis = null
			
			File rmaBankFile = new File(appProperties.object.getProperty('batch.rma.directory'))
			if(rmaBankFile.list() != null && rmaBankFile.list().length < 1){
				returnMap.put("success",true);
				returnMap.put("name", "Process Rma Document: WARNING!RMA file from SWIFT not found in folder /opt/tfs/SWIFT/RMAFile/, please update Runguide and inform TFS Team");
//				returnMap.put("name", "WARNING:RMA file from SWIFT not found in folder /opt/tfs/SWIFT/RMAFile/, please update Runguide and inform TFS Team");
				responseText = gson.toJson(returnMap);
				return Response.status(200).entity(responseText).build();
			}
			
			File backupFileDirectory= new File(appProperties.object.getProperty('batch.rma.backup.directory'))
			File rmaBankBackupFile = null
			
			if(!backupFileDirectory.exists()){
				backupFileDirectory.mkdir()
			}
			
			
			rmaBankFile.eachFileMatch(FileType.FILES,fileMatcher){f ->
				try{
					fis = new FileInputStream(f)
					rmaBankBackupFile = new File(appProperties.object.getProperty('batch.rma.backup.directory') + f.getName())
					SAXParserFactory saxFactory = SAXParserFactory.newInstance();
					SAXParser parser=saxFactory.newSAXParser();
					DefaultHandler handler = new RmaBankHandler();
					
					parser.parse(fis, handler);
					
					for(Map<String,Object> bankToSave:handler.getBanksToSave()){
						processResult=refBankService.processRmaDocument(bankToSave);
						switch(processResult){
						case 0:
							emptyCtr++;
							break;
						case -1:
							failCtr++;
							break;
						default:
							successCtr++;
							banksProcessed+=processResult;
							break;
						}
						banksToSaveCtr++;
					}
					//update ALL NULL REF BANKS
					refBankService.updateAllNullRmaRefBanks();
					//delete after use
					fis.close()
					if(f.exists()){
						FileUtil.copyFile(f, rmaBankBackupFile)
						f.delete()
					}
				}catch(Exception e){
					throw new Exception("Exception in Processing Each File in RMA",e)
				}finally{
					IOUtils.closeQuietly(fis)
				}
			}
			
			println "\n====FINISHED UPLOAD RMA DOCUMENT=================="
			println "TOTAL BANKS IDS TO SAVE: "+ banksToSaveCtr
			println "BANKS UPDATED SUCCESS: "+banksProcessed
			println "BANKS SAVE SUCCESS: "+successCtr
			println "BANKS SAVE FAIL: "+failCtr
			println "BANKS NOT FOUND IN DB: "+emptyCtr
			println "=============================================="
			
//			Map formDetails = gson.fromJson(postRequestBody, Map.class);
//			List<Map<String,Object>> bankList= (List<Map<String,Object>>) formDetails.get("responseTextList")
			
//			refBankService.processRefBankDetails(bankList);

				returnMap.put("success",true);
				returnMap.put("successBanks",successCtr);
				returnMap.put("failBanks",failCtr);
				returnMap.put("emptyBanks",emptyCtr);
				returnMap.put("processedBanks",banksProcessed);
				returnMap.put("totalBanks",banksToSaveCtr);
				returnMap.put("name", "Process Rma Document");
				responseText = gson.toJson(returnMap);
				println "\n--- END (SUCCESS): Process RMA Document, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
				return Response.status(200).entity(responseText).build();
				
		} catch(SAXException e) {
			e.printStackTrace();
			returnMap.put("success", false);
			returnMap.put("errorCode", e.getMessage());
			returnMap.put("description", "SAX EXCEPTION: "+e.toString());
			returnMap.put("name", "Process Rma Document");
			responseText = gson.toJson(returnMap);
			println "\n--- END (FAILED): Process RMA Document, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(500).entity(responseText).build();
		} catch(Exception e){
			e.printStackTrace();
			returnMap.put("success", false);
			returnMap.put("errorCode", e.getMessage());
			returnMap.put("description", e.toString());
			returnMap.put("name", "Process Rma Document");
			responseText = gson.toJson(returnMap);
			println "\n--- END (FAILED): Process RMA Document, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(500).entity(responseText).build();
		}
	}
	
	private String mapParametersToRefBankObject(String paramName){
		switch(paramName){
//			--List of  RefBank properties not mapped---
//			private String rmaFlag;
//			private String depositoryFlag;
//			private String glBankCode;
//			private String rbuAccount;
//			private String fcduAccount;
//			private Currency reimbursingCurrency;
//			private Date updateDate;
//			private String updatedBy;
//			private String glCodeRbu;
//			private String glCodeFcdu;
//			private Long cbCreditorCode;
//			private String deleteFlag;
			case "BIC CODE":
				return "bic"
				break;
			case "BRANCH CODE":
				return "branchCode"
				break;
			case "INSTITUTION NAME":
				return "institutionName"
				break;
			case "BRANCH INFORMATION":
				return "branchInfo"
				break;
			case "CITY HEADING":
				return "city"
				break;
			case "PHYSICAL ADDRESS 1":
				return "address1"
				break;
			case "PHYSICAL ADDRESS 2":
				return "address2"
				break;
			case "PHYSICAL ADDRESS 3":
				return "address3"
				break;
			case "PHYSICAL ADDRESS 4":
				return "address4"
				break;
			case "LOCATION":
				return "location"
				break;
			default:
				return paramName
		}
	}



	
    private java.sql.Date getDate(String dateString){
        Date runDate = DATE_FORMATTER_DESC.parse(dateString)
        return new java.sql.Date(runDate.getTime());
    }

    private java.sql.Date getDate2(String dateString){
        Date runDate = DATE_FORMATTER.parse(dateString)
        return new java.sql.Date(runDate.getTime());
    }

    private java.sql.Date getAppServerDate() {
        Date runDate = new Date();
        return new java.sql.Date(runDate.getTime());
    }

    private java.sql.Date getAppServerDateString() {
        Date runDate = new Date();
        String dateString = DATE_FORMATTER.format(runDate);
        return dateString;
    }

    // test for cif normalization log
//    @POST
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path("/logCifNormalization")
//    public Response logCifNormalization(@Context UriInfo allUri, String postRequestBody) {
//        Gson gson = new Gson()
//
//        String result="";
//        Map returnMap = new HashMap();
//
//        try {
//            def cifNormalizationMap = ["oldCifNumber":"CIFNUM-01",
//                                       "oldCifName":"O-CIFNAME",
//                                       "newCifNumber":"CIFNUM-02",
//                                       "newCifName":"N-CIFNAME",
//                                       "oldMainCifNumber":"MCIFNUM-01",
//                                       "oldMainCifName":"O-MAINCIFNAME",
//                                       "newMainCifNumber":"MCIFNUM-02",
//                                       "newMainCifName":"N-MAINCIFNAME"]
//
//            cifNormalizationLogService.saveCifNormalizationLog(cifNormalizationMap, new Date(), BigDecimal.ONE, BigDecimal.TEN);
//
//            cifNormalizationLogService.createLogFile();
//
//        } catch(Exception e) {
//
//            Map errorDetails = new HashMap();
//
//            e.printStackTrace();
//
//            errorDetails.put("code", e.getMessage());
//            errorDetails.put("description", e.toString());
//
//            returnMap.put("status", "error");
//            returnMap.put("error", e.getMessage());
//        }
//
//        // format return data as json
//        result = gson.toJson(returnMap);
//
//        return Response.status(200).entity(result).build();
//    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/cloneLnappf")
	public Response cloneLnappf(@Context UriInfo allUri) {
		println "\n--- START: Cloning LNAPPF, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
		Gson gson = new Gson();

		String result="";
		Map returnMap = new HashMap();
		Map jsonParams = new HashMap<String, String>();

		// get all query parameters
		MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

		try {
			//test

			def selfTimer = 0;
			def tryConn = true;
			System.out.println("Connecting to SIBS...");
			//Log.debug("Connecting to SIBS...");
			while(tryConn == true){
				try{


					if(selfTimer >= 30) //Terminator
						tryConn = false;

					if(selfTimer > 1){

						timerStarts = 1000
						while(timerStarts != 60000){

							Thread.sleep(1000); //sleep for 1 second
							timerStarts += 1000;



						}

					}

					facilityReferenceNormalization.executeFacilityReferenceNormalization();

					tryConn = false;
					sibsReq = false;
					sibsRecon = false;

					println "\n--- END (SUCCESS) : Cloning LNAPPF, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
					returnMap.put("success", true);
					returnMap.put("name", "Clone LNAPPF");

				}catch(Exception e) {



					//											if(e.toString().toLowerCase().contains("unable to connect to sibs")){
					def states = sibsExtractionJob.checkConnection();
					if(states == false){
						sibsReq = true;
						sibsRecon = true;
						println ("count " + selfTimer)
						if(tryConn == false){
							sibsReq = false;
							sibsRecon = false;
							e.printStackTrace();

							println "\n--- CANNOT CONNECT TO SIBS (PLEASE TRY AFTER 15 Minutes) : Cloning LNAPPF, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
							returnMap.put("sibsretry", true);
							returnMap.put("name", "Clone LNAPPF");
							//												println "\n--- CANNOT CONNECT TO SIBS (PLEASE TRY AFTER 15 Minutes):" + DateUtil.convertToTimeString(new Date()) + "---"
							//												return Response.status(200).entity(gson.toJson([sibsretry : true,name:'Extract SIBS GL Accounts'])).build();


						}



						System.out.println("Error Message Start : " + e + " : Error Message End");
						selfTimer++;
						System.out.println("Reconnecting to SIBS...");
						//				   Log.debug("Reconnecting to SIBS...");

					}else{
						sibsReq = false;
						sibsRecon = false;
						e.printStackTrace();
						throw new IllegalArgumentException(e.printStackTrace());
					}

				}

			}
			//test end



		} catch(Exception e) {
			e.printStackTrace();
			println "\n--- END (FAILED) : Cloning LNAPPF, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			returnMap.put("success", false);
		}

		// format return data as json
		result = gson.toJson(returnMap);

		return Response.status(200).entity(result).build();
	}

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getAllLnappf")
    public Response getAllLnappf(@Context UriInfo allUri) {
        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        try {
            try{
                def lnappfEntries = facilityReferenceNormalization.getAllLnappfEntries();

                returnMap.put("response", lnappfEntries)
                returnMap.put("success", true);
            }catch (Exception e){
                println "ERROR: "+ e.printStackTrace();
                returnMap.put("success", false);
            }
        } catch(Exception e) {
            e.printStackTrace();

            returnMap.put("success", false);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/purgeSecEmployeeAudit")
    public Response purgeSecEmployeeAudit(@Context UriInfo allUri) {
        println "\n--- START: Purge SEC_EMPLOYEE_AUDIT, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
        Gson gson = new Gson();
        try {
            purgeSecEmloyeeAuditJob.execute();
            println "\n--- END (SUCCESS): Purge SEC_EMPLOYEE_AUDIT, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
            return Response.status(200).entity(gson.toJson([success : true,name:'Purge SEC_EMPLOYEE_AUDIT'])).build();
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("Purge SEC_EMPLOYEE_AUDIT = " + e.getCause()?.getMessage())
            println "\n--- END (FAILED): Purge SEC_EMPLOYEE_AUDIT, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
            return Response.status(200).entity(gson.toJson([success : false])).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/updateAllocationUnitCode")
    public Response updateAllocationUnitCode(@Context UriInfo allUri) {

		String response = "true";
		boolean sibsretry = false;
        println "\n--- START: Update Allocation Unit Code, TIME: " + DateUtil.convertToTimeString(new Date()) + " ---\n"

        Gson gson = new Gson();

        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for(String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {
            // This param is for TESTING only
            // Acts as a filter if we do not want to update all records at the same time
            String cifNumber = jsonParams.get("cifNumber")

            response = allocationUnitCodeService.executeUpdate(cifNumber);
			
        } catch(IllegalArgumentException e){
			e.printStackTrace();
			if("UNABLE TO CONNECT TO SIBS".equalsIgnoreCase(e.getMessage())){
				sibsretry = true;
			}			
			response = "false";
        } catch(Exception e) {
            e.printStackTrace();
			response = "false";			
        }
		
		if(response.equalsIgnoreCase("true")) {
			println "\n--- END (SUCCESS) : Update Allocation Unit Code, TIME: " + DateUtil.convertToTimeString(new Date()) + " ---"
			return Response.status(200).entity(gson.toJson([success : true, name : 'Update Allocation Unit Code'])).build();
		} else {
			println "\n--- END (FAILED): Update Allocation Unit Code, TIME: " + DateUtil.convertToTimeString(new Date()) + " ---"
			return Response.status(200).entity(gson.toJson([success : false, sibsretry : sibsretry, name : 'Update Allocation Unit Code'])).build();
		}
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/updateTransactionRouting")
    public Response updateTransactionRouting(@Context UriInfo allUri) {
        println "\n--- START: Update Transaction Routing, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
        Gson gson = new Gson();
        try {
            uploadRoutingJob.execute();
            println "\n--- END (SUCCESS): Update Transaction Routing, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
            return Response.status(200).entity(gson.toJson([success : true,name:'Update Transaction Routing'])).build();
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("Update Transaction Routing = " + e.getCause()?.getMessage())
            println "\n--- END (FAILED): Update Transaction Routing, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
            return Response.status(200).entity(gson.toJson([success : false])).build();
        }
    }
	// ER# 20140909-038 : Start
	private long toJulianDate(Date date) {
		String dateStr = date.toString();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		System.out.println(cal.getTime());
		
		dateStr="";
		dateStr = String.valueOf(cal.get(Calendar.YEAR));
		dateStr = dateStr + String.valueOf(cal.get(Calendar.DAY_OF_YEAR));
		
		System.out.println("Julian Date: " + dateStr);
		
		long julianDate = Long.parseLong(dateStr);
		
		return julianDate;
	}
	// ER# 20140909-038 : End
	
	
	// Start IBD-16-0615-01
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/cifPurgingPhase1")
	public Response cifPurgingPhase1(@Context UriInfo allUri){
		println "\n--- START CIF Purging Phase1, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
		MultivaluedMap<String, String> parameters = allUri.getQueryParameters();
		String reportDate = parameters?.getFirst(REPORT_DATE)?.toString();
		String runDate = (reportDate.substring(6, 10) + reportDate.substring(0, 2) + reportDate.substring(3, 5))
		String runDate2 = (reportDate.substring(6, 10) + "-" + reportDate.substring(0, 2) + "-" + reportDate.substring(3, 5))
		String masterDate = DATE_FORMATTER_DESC.format(new Date())
		println "masterDate:"+masterDate
		println runDate2
		Gson gson = new Gson();
		try{
			cifPurgingJob.dropTables();
			cifPurgingJob.duplicateDB();
			cifPurgingJob.deleteTfcfaccs();
			cifPurgingGeneratorJob.setIsPurged(1);
			cifPurgingJob.executePhase1(runDate,runDate2);
			cifPurgingJob.updateAccountPurgingDetail(masterDate,runDate2);
			
			println "\n--- END (SUCCESS): CIF Purging Phase1, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success:true,name:'CIF Purging'])).build();
	   }catch(Exception e){
			System.err.println("CIF PURGING: " + e);
			println "\n--- END (FAILED): CIF Purging Phase1, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : false])).build();
	   }
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/cifPurgingPhase2")
	public Response cifPurgingPhase2(@Context UriInfo allUri){
		println "\n--- START CIF  Purging Phase2, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
		MultivaluedMap<String, String> parameters = allUri.getQueryParameters();
		String reportDate = parameters?.getFirst(REPORT_DATE)?.toString();
		println (reportDate.substring(6, 10) + reportDate.substring(0, 2) + reportDate.substring(3, 5))
		String appDate = (reportDate.substring(6, 10) + reportDate.substring(0, 2) + reportDate.substring(3, 5))
		println appDate
		Gson gson = new Gson();
		try{
			cifPurgingJob.executePhase2();
			
			println "\n--- END (SUCCESS): CIF Purging Phase2, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success:true,name:'CIF Purging'])).build();
	   }catch(Exception e){
			System.err.println("CIF PURGING: " + e);
			println "\n--- END (FAILED): CIF Purging Phase2, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : false])).build();
	   }
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/cifPurgingPhase3")
	public Response cifPurgingPhase3(@Context UriInfo allUri){
		println "\n--- START CIF  Purging Phase3, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
		MultivaluedMap<String, String> parameters = allUri.getQueryParameters();
		String reportDate = parameters?.getFirst(REPORT_DATE)?.toString();
		println (reportDate.substring(6, 10) + reportDate.substring(0, 2) + reportDate.substring(3, 5))
		String appDate = (reportDate.substring(6, 10) + reportDate.substring(0, 2) + reportDate.substring(3, 5))
		println appDate
		Gson gson = new Gson();
		try{
			cifPurgingJob.dropTables();
			
			println "\n--- END (SUCCESS): CIF Purging Phase3, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success:true,name:'CIF Purging'])).build();
	   }catch(Exception e){
			System.err.println("CIF PURGING: " + e);
			println "\n--- END (FAILED): CIF Purging Phase3, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
			return Response.status(200).entity(gson.toJson([success : false])).build();
	   }
	}
	
	// End IBD-16-0615-01
        
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/rerouteTradeServiceJob")
    public Response rerouteTradeServiceJob(@Context UriInfo allUri){
	println "\n--- START: Reroute Trade Service Job, TIME:" + DateUtil.convertToTimeString(new Date()) + "---"
        
		MultivaluedMap<String, String> parameters = allUri.getQueryParameters();
	
		println "Size: " + parameters.size();
		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			println  "Key : " + entry.getKey();
			println "Value : " + entry.getValue();
		}
        String documentNumber = parameters?.getFirst("docNumber")?.toString();
        String targetUser = parameters?.getFirst("newApprover")?.toString();
		String p_message = "";
		
        Gson gson = new Gson();
        Assert.notNull(parameters?.getFirst("docNumber")?.toString(),"Document Number must be provided!")
        Assert.notNull(parameters?.getFirst("newApprover")?.toString(),"Target User must be provided!")
        
        try{		
            println "Document Number : " + documentNumber;
            println "Target User : " + targetUser;
            p_message = rerouteTradeServiceJob.execute(documentNumber,targetUser);
            println "\n--- END (SUCCESS): Reroute TradeService JOB, TIME:" + DateUtil.convertToTimeString(new Date()) + " ---"
//            return Response.status(200).entity(gson.toJson([success : true, name:'Rereoute Trade Service Job'])).build();
			return Response.status(200).entity(gson.toJson([success : true,name: p_message.startsWith("2") ? p_message :'Rereoute Trade Service Job'])).build();
	   }catch(Exception e){
            System.err.println("rerouteTradeServiceJob: "+ e.getMessage())
            println "\n--- END (FAILED): Reroute TradeService JOB, TIME:" + DateUtil.convertToTimeString(new Date()) + " ---"
//            return Response.status(200).entity(gson.toJson([success : false])).build();
			return Response.status(200).entity(gson.toJson([success : false,name: '[DBException] ', p_message : e.getMessage()])).build();
	   }
    }
}
