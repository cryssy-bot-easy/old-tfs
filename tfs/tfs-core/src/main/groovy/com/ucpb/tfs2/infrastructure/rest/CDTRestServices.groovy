package com.ucpb.tfs2.infrastructure.rest
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ws.rs.*
import javax.ws.rs.core.*

import net.sf.saxon.instruct.ForEach;

import org.codehaus.plexus.util.StringUtils
import org.jboss.resteasy.plugins.providers.multipart.InputPart
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.PropertiesFactoryBean
import org.springframework.mail.MailSender
import org.springframework.stereotype.Component
import org.springframework.util.Assert

import com.google.gson.Gson
import com.ipc.rbac.domain.UserActiveDirectoryId
import com.sun.xml.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;
import com.ucpb.tfs.application.service.AccountingService
import com.ucpb.tfs.domain.accounting.AccountingEntryActualRepository
import com.ucpb.tfs.domain.cdt.*
import com.ucpb.tfs.domain.cdt.enums.AutoDebit
import com.ucpb.tfs.domain.cdt.enums.CDTStatus
import com.ucpb.tfs.domain.cdt.enums.PaymentRequestType
import com.ucpb.tfs.domain.cdt.services.PAS5FilesLoaderService
import com.ucpb.tfs.domain.email.CDTEmail
import com.ucpb.tfs.domain.email.Email
import com.ucpb.tfs.domain.email.MailFrom
import com.ucpb.tfs.domain.email.SmtpAuthenticator
import com.ucpb.tfs.domain.email.service.EmailService
import com.ucpb.tfs.domain.instruction.enumTypes.ServiceInstructionStatus
import com.ucpb.tfs.domain.payment.PaymentRepository
import com.ucpb.tfs.domain.payment.casa.CasaAccount
import com.ucpb.tfs.domain.payment.casa.parser.exception.InvalidAccountNumberFormatException
import com.ucpb.tfs.domain.payment.enumTypes.PaymentStatus
import com.ucpb.tfs.domain.reference.GltsSequenceRepository
import com.ucpb.tfs.domain.security.Employee
import com.ucpb.tfs.domain.security.EmployeeRepository
import com.ucpb.tfs.domain.security.UserId
import com.ucpb.tfs.domain.security.EmployeeRepository
import com.ucpb.tfs.domain.service.TradeService
import com.ucpb.tfs.domain.service.TradeServiceId
import com.ucpb.tfs.domain.service.TradeServiceReferenceNumber
import com.ucpb.tfs.domain.service.TradeServiceRepository
import com.ucpb.tfs.domain.service.enumTypes.DocumentClass
import com.ucpb.tfs.domain.service.enumTypes.ServiceType
import com.ucpb.tfs.domain.service.utils.TradeServiceReferenceNumberGenerator
import com.ucpb.tfs.interfaces.services.CustomerInformationFileService
import com.ucpb.tfs2.application.service.CDTService
import com.ucpb.tfs2.application.service.PaymentService
import com.ucpb.tfs2.application.service.casa.exception.CasaServiceException


//  PROLOGUE:
// 	(revision)
//	SCR/ER Number: SCR# IBD-16-1206-01
//	SCR/ER Description: To comply with the requirement for CIF archiving/purging of inactive accounts in TFS.
//	[Created by:] Allan Comboy and Lymuel Saul
//	[Date Deployed:] 12/20/2016
//	Program [Revision] Details: Add CDT Remittance and CDT Refund module.
//	PROJECT: CORE
//	MEMBER TYPE  : Groovy
//	Project Name: CDTRestServices

/**
 (revision)
SCR/ER Number:
SCR/ER Description: Add parameter on Response uploadFile()
[Revised by:] Jonh Henry Alabin
[Date deployed:]
Program [Revision] Details: Added parameters (user role, Email and Full name) for formatting of Email Notification
PROJECT: CORE
MEMBER TYPE  : Groovy

*/

/**
 (revision)
SCR/ER Number:
SCR/ER Description:
[Revised by:] Cedrick Nungay
[Date deployed:]
Program [Revision] Details: Added welcome email sending if the client is a newly register
PROJECT: CORE
MEMBER TYPE  : Groovy

*/

/**
 (revision)
SCR/ER Number:
SCR/ER Description:
[Revised by:] Cedrick Nungay
[Date deployed:]
Program [Revision] Details: Added subject handling for Upload Client
PROJECT: CORE
MEMBER TYPE  : Groovy

 */

/**
 (revision)
SCR/ER Number:
SCR/ER Description:
[Revised by:] Cedrick Nungay
[Date updated:] 01/11/2018
Program [Revision] Details: Changes email status sent on EMAIL_NOTIF table
PROJECT: CORE
MEMBER TYPE  : Groovy

 */


@Path("/cdt")
@Component
class CDTRestServices {
	
	@Autowired
	SmtpAuthenticator smtpAuthenticator;
	
	@Autowired
	MailFrom mailFrom;
	
	@Autowired
	MailSender mailSender;

    @Autowired
    CDTPaymentRequestRepository cdtPaymentRequestRepository

    @Autowired
    CDTPaymentHistoryRepository cdtPaymentHistoryRepository

    @Autowired
    RefPas5ClientRepository refPas5ClientRepository

    @Autowired
    CDTService cdtService

    @Autowired
    TradeServiceRepository tradeServiceRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    PAS5FilesLoaderService pas5FilesLoaderService;

    @Autowired
    CustomerInformationFileService customerInformationFileService

    @Autowired
    PaymentService paymentService

    @Autowired
    TradeServiceReferenceNumberGenerator tradeServiceReferenceNumberGenerator

    @Autowired
    AccountingEntryActualRepository accountingEntryActualRepository

    @Autowired
    AccountingService accountingService

    @Autowired
    GltsSequenceRepository gltsSequenceRepository

    @Autowired
    CDTRemittanceRepository cdtRemittanceRepository

	@Autowired
	EmployeeRepository employeeRepository
	
	@Autowired
	PropertiesFactoryBean appProperties
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/validateAabRefCode")
	public Response ValidateAabRefCode(@Context UriInfo allUri){
		Gson gson = new Gson();
		
		String result = "";
		Map returnMap = new HashMap();
		Map jsonParams = new HashMap<String, String>();
		
		MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();
		
		for(String key : mpAllQueParams.keySet()) {
			jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
		}
		
		String aabRefCode = jsonParams.get("aabRefCode");
		String clientName = "Client Name";
		
		try {
			
			List allRequests = refPas5ClientRepository.getClientsMatching(aabRefCode, clientName);
			
			returnMap.put("status", "ok");
			returnMap.put("details", allRequests)
			
		} catch(Exception e) {
			Map errorDetails = new HashMap();
		
			errorDetails.put("code", e.getMessage());
			errorDetails.put("description", e.toString());
		
			returnMap.put("status", "error");
			returnMap.put("error", errorDetails);
			
		}
		
		// format return data as json
		result = gson.toJson(returnMap);

		// todo: we should probably return the appropriate HTTP error codes instead of always returning 200
		return Response.status(200).entity(result).build();
	}

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/payment/search")
    public Response CDTPaymentSearch(@Context UriInfo allUri) {
//    public Response executeQuery(@PathParam("actionName") String query) {

        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for(String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());

        }
		

        try {

            //List allRequests = cdtPaymentRequestRepository.getAllRequests();
            println "/payment/search " + jsonParams

            List allRequests = cdtPaymentRequestRepository.getRequestsMatching(jsonParams.get("ref"),
                    jsonParams.get("ied"),
                    jsonParams.get("importer"),
                    jsonParams.get("request"),
                    jsonParams.get("status"),
                    jsonParams.get("txfrom") == null ? null : new Date(jsonParams.get("txfrom")),
                    jsonParams.get("txto") == null ? null : new Date(jsonParams.get("txto")),
                    jsonParams.get("unitcode"),
                    jsonParams.get("uploadDate") == null ? null : new Date(jsonParams.get("uploadDate")),
					jsonParams.get("aabRefCode"));

            returnMap.put("status", "ok");
            returnMap.put("details", allRequests)

        } catch(Exception e) {

            Map errorDetails = new HashMap();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/payment/searchEmailTable")
	public Response CDTEmailTableSearch(@Context UriInfo allUri) {
		Gson gson = new Gson();

		String result="";
		Map returnMap = new HashMap();
		Map jsonParams = new HashMap<String, String>();

		MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

		for(String key : mpAllQueParams.keySet()) {

			jsonParams.put(key, mpAllQueParams.getFirst(key).toString());

		}
		

		try {
			println "searchEmailTable " + jsonParams
			List allRequests = cdtPaymentRequestRepository.getRequestsMatchingEmailTable(
				 	jsonParams.get("iedieirdNumber"),
                    jsonParams.get("emailAddress"),
                    jsonParams.get("emailStatus"),
                    jsonParams.get("sentTime"),);

			returnMap.put("status", "ok");
			returnMap.put("details", allRequests)

		} catch(Exception e) {

			Map errorDetails = new HashMap();

			errorDetails.put("code", e.getMessage());
			errorDetails.put("description", e.toString());

			returnMap.put("status", "error");
			returnMap.put("error", errorDetails);
		}

		result = gson.toJson(returnMap);

		return Response.status(200).entity(result).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/payment/cdttodays")
	public Response CDTPaymentSearchcdttodays(@Context UriInfo allUri) {
//    public Response executeQuery(@PathParam("actionName") String query) {

		Gson gson = new Gson();

		String result="";
		Map returnMap = new HashMap();
		Map jsonParams = new HashMap<String, String>();

		// get all query parameters
		MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

		for(String key : mpAllQueParams.keySet()) {

			// if there are multiple instances of the same param, we only use the first one
			jsonParams.put(key, mpAllQueParams.getFirst(key).toString());

		}
		

		try {

			//List allRequests = cdtPaymentRequestRepository.getAllRequests();
			println "jsonParams " + jsonParams

			List allRequests = cdtPaymentRequestRepository.getcdtTodays(jsonParams.get("ref"),
					jsonParams.get("ied"),
					jsonParams.get("importer"),
					jsonParams.get("request"),
					jsonParams.get("status"),
					jsonParams.get("txfrom") == null ? null : new Date(jsonParams.get("txfrom")),
					jsonParams.get("txto") == null ? null : new Date(jsonParams.get("txto")),
					jsonParams.get("unitcode"),
					jsonParams.get("uploadDate") == null ? null : new Date(jsonParams.get("uploadDate")),
					jsonParams.get("aabRefCode"));

			returnMap.put("status", "ok");
			returnMap.put("details", allRequests)

		} catch(Exception e) {

			Map errorDetails = new HashMap();

			errorDetails.put("code", e.getMessage());
			errorDetails.put("description", e.toString());

			returnMap.put("status", "error");
			returnMap.put("error", errorDetails);
		}

		// format return data as json
		result = gson.toJson(returnMap);

		// todo: we should probably return the appropriate HTTP error codes instead of always returning 200
		return Response.status(200).entity(result).build();
	}
	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/remittance/search")
    public Response CDTRemittanceSearch(@Context UriInfo allUri) {
//    public Response executeQuery(@PathParam("actionName") String query) {
        println "CDTRemittanceSearch"

        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for(String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            //List allRequests = cdtPaymentRequestRepository.getAllRequests();

            println "reportType"+jsonParams.get("reportType").toString()
            println "dateOfRemitanceFrom"+jsonParams.get("dateOfRemitanceFrom")
            println "dateOfRemitanceTo"+jsonParams.get("dateOfRemitanceTo")
            println "collectionPeriodFrom"+jsonParams.get("collectionPeriodFrom")
            println "collectionPeriodTo"+jsonParams.get("collectionPeriodTo")

            List allRequests = cdtRemittanceRepository.getAllBy(
                    getPaymentRequestTypesFromString(jsonParams.get("reportType")?.toString()?:null),
                    jsonParams.get("dateOfRemitanceFrom") ? new Date((String) jsonParams.get("dateOfRemitanceFrom")) : null,
                    jsonParams.get("dateOfRemitanceTo") ? new Date((String) jsonParams.get("dateOfRemitanceTo")) : null,
                    jsonParams.get("collectionPeriodFrom") ? new Date((String) jsonParams.get("collectionPeriodFrom")) : null,
                    jsonParams.get("collectionPeriodTo") ? new Date((String) jsonParams.get("collectionPeriodTo")) : null);

            returnMap.put("status", "ok");
            returnMap.put("details", allRequests)

        } catch(Exception e) {

            Map errorDetails = new HashMap();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/payment/history")
    public Response CDTPaymentHistory(@Context UriInfo allUri) {
//    public Response executeQuery(@PathParam("actionName") String query) {
		

		
        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for(String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }
		
        try {



//			jsonParams.put("sentBOCDate", getDateWithoutTime(new Date()));
			List allRequests;
			Date sentBOCdate = getDateWithoutTime(new Date());
			if(jsonParams.get("confDate"))
			sentBOCdate = getDateWithoutTime(new Date(jsonParams.get("confDate")));
            //List allRequests = cdtPaymentRequestRepository.getAllRequests();
			

//			if(getDateWithoutTime(sentBOCdate) == getDateWithoutTime(new Date())){
//				
//			allRequests = cdtPaymentRequestRepository.getHistoryUpdatedToday((String) jsonParams.get("unitCode"))
//				
//			}else{
//			println "##################### " + jsonParams.get("test") + "!!!! ###################"
			allRequests = cdtPaymentRequestRepository.getHistoryUpdatedToday((String) jsonParams.get("unitCode"),getDateWithoutTime(sentBOCdate))
		   
//			}
           
            returnMap.put("status", "ok");
            returnMap.put("details", allRequests)

        } catch(Exception e) {

            Map errorDetails = new HashMap();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }
	
	private static Date getDateWithoutTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/payment/details")
    public Response CDTPaymentRequestDetails(@Context UriInfo allUri) {
//    public Response executeQuery(@PathParam("actionName") String query) {
        println 'CDTPaymentRequestDetails'
        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for(String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            //List allRequests = cdtPaymentRequestRepository.getAllRequests();

            CDTPaymentRequest paymentRequest = cdtPaymentRequestRepository.getPaymentRequestDetails((String) jsonParams.get("ied"))
//            List<RefPas5Client> pas5Client = refPas5ClientRepository.getClientsMatching(paymentRequest.clientName)
            RefPas5Client pas5Client = refPas5ClientRepository.load(paymentRequest.agentBankCode.toUpperCase())

            Map tradeService
            List payment

            if (paymentRequest != null) {

                tradeService = tradeServiceRepository.getTradeServiceBy2(new TradeServiceReferenceNumber(paymentRequest.getIedieirdNumber()), ServiceType.PAYMENT);

                if (tradeService != null) {
                    payment = paymentRepository.getPaymentBy(new TradeServiceId(tradeService["tradeServiceId"]["tradeServiceId"]))

                }
            }
			pas5Client.ccbdBranchUnitCode = paymentRequest.getBranchUnitCode();
			pas5Client.allocationUnitCode = paymentRequest.allocationUnitCode;
            // combine the payment request and client detail
            Map paymentRequestDetails = [
                paymentRequest : paymentRequest,
//                client : !pas5Client.isEmpty() ? pas5Client.get(0) : [], // returns empty list if past5Client is empty
                client: pas5Client,
                tradeService: tradeService,
                paymentDetails: payment
            ]

			
            Map tmpMap = gson.fromJson(gson.toJson(paymentRequestDetails), Map.class)
			println 'paymentRequestDetails #####' + paymentRequestDetails
            println 'tmpMap:'+tmpMap

            returnMap.put("status", "ok");
            returnMap.put("details", tmpMap)

        } catch(Exception e) {

            Map errorDetails = new HashMap();
            e.printStackTrace()
            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/payment/save")
	public Response savePayment(@Context UriInfo allUri, String postRequestBody) {

		println 'CDTRestServices savePayment'
		Gson gson = new Gson();

		String result="";
		Map returnMap = new HashMap();

		try {

			Map formDetails = gson.fromJson(postRequestBody, Map.class);

			// extract the known fields
			String iedNumber = formDetails["ied"];
			String documentNumber = formDetails["documentNumber"];
			String tradeServiceId = formDetails["tradeServiceId"];

			CDTPaymentRequest paymentRequest = cdtPaymentRequestRepository.getPaymentRequestDetails(iedNumber)

			// associate with the document number specified in the form
			paymentRequest.setDocumentNumber(documentNumber)
			paymentRequest.setPaymentReferenceNumber((String)formDetails.get("paymentReferenceNumber"))
			println "formDetails: " + formDetails

			// we call the service to save the CDT payment request along with the other necessary
			// domain classes
			Map returnDetails = cdtService.saveCDTPaymentRequest(paymentRequest, (Map) formDetails)

//            TradeService tradeService = null
//            if (tradeServiceId != null) {
//                tradeService = tradeServiceRepository.load(new TradeServiceId(tradeServiceId))
//            }
//
//            tradeService.getDetails().put('paymentRequestType',paymentRequest.paymentRequestType.toString())
//            tradeService.getDetails().put('documentNumber',paymentRequest.getDocumentNumber())
//            tradeService.getDetails().put('e2mStatus',paymentRequest.getE2mStatus())
//            tradeService.getDetails().put('amount',paymentRequest.getAmount())
//            tradeService.getDetails().put('finalDutyAmount',paymentRequest.getFinalDutyAmount())
//            tradeService.getDetails().put('finalTaxAmount',paymentRequest.getFinalTaxAmount())
//            tradeService.getDetails().put('ipf',paymentRequest.getIpf())
//            tradeService.getDetails().put('finalCharges',paymentRequest.getFinalCharges())
//            tradeService.getDetails().put('amountCollected',paymentRequest.getAmountCollected())
//            tradeService.getDetails().put('paymentReferenceNumber',paymentRequest.getPaymentReferenceNumber())
//            tradeService.getDetails().put('clientName',paymentRequest.getClientName())
//
//            tradeServiceRepository.saveOrUpdate(tradeService)

			returnMap.put("details", returnDetails)
			returnMap.put("status", "ok")

		} catch(Exception e) {

			Map errorDetails = new HashMap();

			e.printStackTrace();

			errorDetails.put("code", e.getMessage());
			errorDetails.put("description", e.toString());

			returnMap.put("status", "error");
			returnMap.put("error", errorDetails);
		}

		// format return data as json
		result = gson.toJson(returnMap);

		return Response.status(200).entity(result).build();

	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/refund/save")
	public Response saveRefund(@Context UriInfo allUri, String postRequestBody) {
		println 'CDTRestServices saveRefund'
		Gson gson = new Gson();

		String result="";
		Map returnMap = new HashMap();

		try {
			Map formDetails = gson.fromJson(postRequestBody, Map.class);

			// extract the known fields
			String iedNumber = formDetails["iedieirdNumber"];
			String documentNumber = formDetails["documentNumber"];
			String tradeServiceId = formDetails["tradeServiceId"];

			CDTPaymentRequest paymentRequest = cdtPaymentRequestRepository.getPaymentRequestDetails(iedNumber)

			// associate with the document number specified in the form
			paymentRequest.setDocumentNumber(documentNumber)
			paymentRequest.setPaymentReferenceNumber((String)formDetails.get("transactionReferenceNumber"))

			// we call the service to update the CDT payment request along with the other necessary
			// domain classes
			Map returnDetails = cdtService.saveCDTBranchRefund(paymentRequest, (Map) formDetails)

			returnMap.put("details", returnDetails)
			returnMap.put("status", "ok")

		} catch(Exception e) {

			Map errorDetails = new HashMap();

			e.printStackTrace();

			errorDetails.put("code", e.getMessage());
			errorDetails.put("description", e.toString());

			returnMap.put("status", "error");
			returnMap.put("error", errorDetails);
		}

		// format return data as json
		result = gson.toJson(returnMap);

		return Response.status(200).entity(result).build();

	}

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/importer/search")
    public Response cdtImporterSearch(@Context UriInfo allUri) {
//    public Response executeQuery(@PathParam("actionName") String query) {

        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for(String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            String importerName = jsonParams.get("importername") ? jsonParams.get("importername").toString().toUpperCase() : null
			String aabRefCode = jsonParams.get("aabRefCode") ?  jsonParams.get("aabRefCode").toString().toUpperCase() : null
			String importerTin =  jsonParams.get("importerTin") ?  jsonParams.get("importerTin").toString().toUpperCase() : null
			String customsClientNumber = jsonParams.get("customsClientNumber") ?  jsonParams.get("customsClientNumber").toString().toUpperCase() : null
			
			println "Importer Name: " + importerName
			println "AABREFCODE: " + aabRefCode
			println "Importer TIN: " + importerTin
			println "Customs Client Number: " + customsClientNumber
			
//            List allRequests = refPas5ClientRepository.getClientsMatching(importerName)
//            List allRequests = refPas5ClientRepository.getClientsMatching(importerName, jsonParams.get("uploader").toString())
            List allRequests = refPas5ClientRepository.getClientsMatching(importerName, aabRefCode, importerTin, customsClientNumber, jsonParams.get("unitCode").toString())

            returnMap.put("status", "ok");
            returnMap.put("details", allRequests)

        } catch(Exception e) {

            Map errorDetails = new HashMap();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/importer/details")
    public Response cdtImporterDetails(@Context UriInfo allUri) {
        println "cdtImporterDetails"
        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for(String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            //List allRequests = cdtPaymentRequestRepository.getAllRequests();

            RefPas5Client pas5Client = refPas5ClientRepository.load(jsonParams.get("agentBankCode").toString().toUpperCase())

            if (!pas5Client.containsCif()) {
                DecimalFormat decimalFormat = new DecimalFormat("####################")

                Double tinNumber

                if (!hasLettersOrWhitespace(pas5Client.getTin())) {
                    println 'pas5Client.getTin() ' + pas5Client.getTin()
                    tinNumber = new Double(pas5Client.getTin())
                    println 'tinNumber ' + pas5Client.getTin()
                }

                println 'decimalFormat.format(tinNumber) ' + decimalFormat.format(tinNumber)
                Map<String, Object> cifMap = customerInformationFileService.getCifByTinNumber(decimalFormat.format(tinNumber))
                println 'cifMap ' + cifMap
                if (cifMap != null) {
                    pas5Client.setCIFDetails(cifMap)

                    refPas5ClientRepository.merge(pas5Client)
                }
                println 'pas5Client.setCIFDetails'
            }


            returnMap.put("status", "ok");
            returnMap.put("details", pas5Client)

        } catch(Exception e) {

            Map errorDetails = new HashMap();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }
	
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getBranches")
	public Response getBranches(@Context UriInfo allUri, String postRequestBody) {
//		SELECT DISTINCT UNITCODE FROM CDTPAYMENTREQUEST;
		println "passed 4"
		CDTPaymentRequest paymentRequest = cdtPaymentRequestRepository.getAllBranch();
		println "passed 5"
	}


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/importer/save")
    public Response SaveImporter(@Context UriInfo allUri, String postRequestBody) {
        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();

        try {

            Map formDetails = gson.fromJson(postRequestBody, Map.class);

            // extract the known fields
            String ccn = formDetails["ccn"];
            String tin = formDetails["tin"];
            String bankCommission = formDetails["defaultBankCharge"];
            String feeSharing = formDetails["feeSharing"];
            String casaAccountNumber = formDetails["casaAccountNumber"];
            String autoDebitAuthority = formDetails["autoDebitAuthority"];
            String contactPerson = formDetails["contactPerson"];
            String email = formDetails["email"];
            String contactNumber = formDetails["phoneNumber"];
            String cifNumber = formDetails["cifNumber"];
            String cifName = formDetails["cifName"];
            String accountOffier = formDetails["accountOfficer"]
            String branchCode = formDetails["ccbdBranchUnitCode"]
			String branchEmail = formDetails["branchEmail"]
			String rmbmEmail = formDetails["rmbmEmail"]
			String newClient = formDetails["newClient"]
			
            println "branchCode " + branchCode
            // check for nulls and set default values for fields that are checked
            if (bankCommission == null) {
                bankCommission = 0;
            }

            if (feeSharing == null) {
                feeSharing = "NO"
            }

            AutoDebit autoDebit

            if (autoDebitAuthority != null && autoDebitAuthority != "") {
                autoDebit = AutoDebit.valueOf(autoDebitAuthority)
            }

            String agentBankCode = formDetails["agentBankCode"];
            println "agentBankCode " + agentBankCode
            RefPas5Client pas5Client = refPas5ClientRepository.load(agentBankCode);

            // check if this client exists
            if (pas5Client == null) {

                returnMap.put("details", "client not found!")
                returnMap.put("status", "error")

            }
            else {
                // associate with the document number specified in the form
                pas5Client.updateDetails(cifNumber, cifName, accountOffier, branchCode,
                        new BigDecimal(bankCommission ? bankCommission.replaceAll(",", "") : "0"),
                        feeSharing.equalsIgnoreCase("YES"),
                        casaAccountNumber,
                        autoDebit,
                        contactPerson,
                        email,
                        contactNumber,
						branchEmail,
						rmbmEmail)

                // we call the service to save the CDT payment request along with the other necessary
                // domain classes
                refPas5ClientRepository.update(pas5Client)

                Map returnDetails = gson.fromJson(gson.toJson(pas5Client), Map.class)

                returnMap.put("details", returnDetails)
                returnMap.put("status", "ok")
				
				
				if (newClient.equalsIgnoreCase('y')) {
					List<String> to = new ArrayList<>()
					List<String> cc = new ArrayList<>()
					String[] emails = email.split(";")
					
					emails.each {
						if (it == emails.first()) {
							to << it.trim()
						} else {
							cc << it.trim()
						}
					}
				
					EmailService emailService = new EmailService()
					Email emailDetails = new Email() {
						@Override
						public String getSubject() {
							return "LBP e2M Enrollment"
						}
	
						@Override
						public String getContent() {
							return "Thank you for trusting LBP for your e2M " +
								"transactions! We now have registered you in " +
								"our Trade System.<br/><br/>This is a " +
								"system-generated message. Please do not reply."
						}
	
						@Override
						public List<String> getTo() {
							return to
						}
	
						@Override
						public List<String> getCc() {
							return cc
						}
	
						@Override
						public void setTo() { }
	
						@Override
						public void setCc() { }
	
						@Override
						public String getRerouteContentFromOriginalRecipient(ServiceInstructionStatus txStatus, String routedTo) {
							return ""
						}
	
						@Override
						public String getRerouteContentToNewRecipient(ServiceInstructionStatus txStatus, String routedTo) {
							return ""
						}
					}
					emailService.sendCdtEmail(smtpAuthenticator, mailFrom, mailSender, emailDetails)
				}
            }
        } catch(Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        return Response.status(200).entity(result).build();

    }

    // the following is from: http://www.mkyong.com/webservices/jax-rs/file-upload-example-in-resteasy/
    @POST
    @Path("/upload")
    @Consumes("multipart/form-data")
    public Response uploadFile(MultipartFormDataInput input) {
        String fileName = "";

		
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();

		
        String fileType = uploadForm.get("fileType")[0].getBodyAsString();
		Date confDate = new Date();
		try{
			def tmp = uploadForm.get("confDate")[0].getBodyAsString();
		
		confDate =  new Date(tmp);


		}catch(Exception e){
		
		}
		
		
        List<InputPart> inputParts = uploadForm.get("uploadedFile");
		if(fileType.equalsIgnoreCase("history")){
		//additional to wash out old records based on the date
		String unitCodedel = uploadForm.get("cdtBookCode")[0].getBodyAsString();
		cdtPaymentHistoryRepository.delete(unitCodedel, confDate);
		println "deleted history"
		}
		
        for (InputPart inputPart : inputParts) {
			
            try {

                MultivaluedMap<String, String> header = inputPart.getHeaders();
                fileName = getFileName(header);

                //convert the uploaded file to inputstream
                InputStream inputStream = inputPart.getBody(InputStream.class,null);
                // pass the input stream directly to the loader service
                println "filetype is: " + fileType

                if (fileType.equalsIgnoreCase("transactions")) {
                    println "transactions Henry"
                    String cdtBookCode = uploadForm.get("cdtBookCode")[0].getBodyAsString();
                    println "cdtBookCode " + cdtBookCode
//                    pas5FilesLoaderService.loadPaymentRequest(inputStream, cdtBookCode)
                    String allocUnitCode = uploadForm.get("allocUnitCode")[0].getBodyAsString();
					String userrole = uploadForm.get("userrole")[0].getBodyAsString();
					String email = uploadForm.get("email")[0].getBodyAsString();
					String fullName = uploadForm.get("fullName")[0].getBodyAsString();
					println "userrole : " + userrole
					println "second email : " + email
					println "second fullName : " + fullName
                    pas5FilesLoaderService.loadPaymentRequest(inputStream, cdtBookCode, allocUnitCode, userrole, fullName, email)
                } else if (fileType.equalsIgnoreCase("clients")) {
                    println "clients na naman"

                    String userrole = uploadForm.get("userrole")[0].getBodyAsString();
                    String unitCode = uploadForm.get("unitCode")[0].getBodyAsString();				
                    println "userrole " + userrole
                    println "unitCode " + unitCode

                    pas5FilesLoaderService.loadClientFile(inputStream, userrole, unitCode)
                } else if (fileType.equalsIgnoreCase("history")) {
                    println "history"

                    String unitCode = uploadForm.get("cdtBookCode")[0].getBodyAsString();

                    pas5FilesLoaderService.loadHistory(inputStream, unitCode, confDate)
                }

                println "the file that was uploaded was ::: " + fileName

                System.out.println("Done");

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
		
		
		
        return Response.status(200)
                .entity("uploadFile is called, Uploaded file name : " + fileName).build();

    }

    //get uploaded filename, is there a easy way in RESTEasy?
    private String getFileName(MultivaluedMap<String, String> header) {

        String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {

                String[] name = filename.split("=");

                String finalFileName = name[1].trim().replaceAll("\"", "");
                return finalFileName;
            }
        }
        return "unknown";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/refund/details")
    public Response CDTPaymentRefundDetails(@Context UriInfo allUri) {
		
        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for(String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {
            //List allRequests = cdtPaymentRequestRepository.getAllRequests();
            CDTPaymentRequest paymentRequest = cdtPaymentRequestRepository.getPaymentRequestDetails(jsonParams.get("ied"))
            List<RefPas5Client> pas5Client = refPas5ClientRepository.getClientsMatching(paymentRequest.clientName)

            Map tradeService
            Map tradeServicePaid
            List payment

            def totalAmountOfPayment = 0
			def cdtAmount = 0
			def ipf = 0

            if (paymentRequest != null) {
				if(jsonParams.get("tsdInitiated").toString().equalsIgnoreCase("false")){
					tradeService = tradeServiceRepository.getTradeServiceByUnitCode(new TradeServiceReferenceNumber(paymentRequest.getIedieirdNumber()), ServiceType.REFUND, paymentRequest.getUnitCode());
				} else {
					tradeService = tradeServiceRepository.getTradeServiceByUnitCode(new TradeServiceReferenceNumber(paymentRequest.getIedieirdNumber()), ServiceType.REFUND, "909");
				}

//                tradeServicePaid = tradeServicePaid = tradeServiceRepository.getTradeServiceBy(new TradeServiceReferenceNumber(paymentRequest.getIedieirdNumber()), ServiceType.PAYMENT);
//                if (tradeServicePaid != null) {
//                    payment = paymentRepository.getPaymentBy(new TradeServiceId(tradeServicePaid["tradeServiceId"]["tradeServiceId"]))
//
//                    if (!payment.isEmpty()) {
//                        payment[0]?.details?.each {
//                            totalAmountOfPayment += it.amount
//                        }
//                    }
//                }
				Date date = null;
//				System.out.println("paymentRequest.getIPFRemittedDate()" + paymentRequest.getIPFRemittedDate());
//				if(paymentRequest.getIPFRemittedDate() == null){
				cdtAmount = paymentRequest.amount - paymentRequest.ipf
				totalAmountOfPayment = cdtAmount + paymentRequest.ipf
				ipf = paymentRequest.ipf
//				} else {
//					cdtAmount = paymentRequest.amount - paymentRequest.ipf
//					totalAmountOfPayment = cdtAmount
//				}				
            }

            // combine the payment request and client detail
            Map paymentRequestDetails = [
                    paymentRequest : paymentRequest,
                    client : !pas5Client.isEmpty() ? pas5Client.get(0) : [], // returns empty list if past5Client is empty
                    tradeService: tradeService,
                    totalAmountOfPayment: totalAmountOfPayment,
					cdtAmount: cdtAmount,
					ipf: ipf
            ]

            Map tmpMap = gson.fromJson(gson.toJson(paymentRequestDetails), Map.class)

            returnMap.put("status", "ok");
            returnMap.put("details", tmpMap)

        } catch(Exception e) {

            Map errorDetails = new HashMap();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    public static boolean hasLettersOrWhitespace(String s) {
        if (s == null) return false

        for (int i = 0; i < s.length(); i ++) {
            char c = s.charAt(i);

            if (Character.isLetter(c)) return true

            if (Character.isWhitespace(c)) return true

            if (!Character.isDigit(c)) {
                if (c.toString() in [",", "."]) {
                    continue
                } else {
                    return true
                }
            }
        }
        return false;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/sendToMobBoc")
    public Response sendToMobBoc(@Context UriInfo allUri, String postRequestBody) {
        Gson gson = new Gson();

		
		
        String result="";
        Map returnMap = new HashMap();

        try {
            Map formDetails = gson.fromJson(postRequestBody, Map.class);

            Assert.notNull(formDetails.userId, "User Id must not be null!");
            Assert.notNull(formDetails.accountNumber, "Account Number must not be null!");

            String referenceNumber;
            String errorMessage;

            BigDecimal amount = new BigDecimal(formDetails.amount?.replaceAll(",", ""))
            println "accountName " + formDetails.accountName

            try{
//                def response = getAccountName(formDetails.accountNumber, formDetails.userId, "PHP")

//                if (!"error".equals(response.status)) {
                TradeService tradeService

                if (formDetails.tradeServiceId) {
                    tradeService = tradeServiceRepository.load(new TradeServiceId(formDetails.tradeServiceId))

                    if (PaymentStatus.PAID.equals(tradeService.getPaymentStatus())) {
                        throw new Exception("CDT Already sent to MOB-BOC.");
                    }
                } else {
                    String tradeServiceReferenceNumber = tradeServiceReferenceNumberGenerator.generateReferenceNumber(formDetails.processingUnitCode);
                    tradeService = new TradeService(null, null, DocumentClass.CDT, null, null, null, ServiceType.PAYMENT, new UserActiveDirectoryId(formDetails.userId), tradeServiceReferenceNumber);
                    tradeService.getDetails().put("amount",amount)
                    tradeService.getDetails().put("tradeServiceReferenceNumber",tradeServiceReferenceNumber)
                    tradeService.getDetails().put("userId",formDetails.userId)
                    tradeService.getDetails().put("accountNumber",formDetails.accountNumber)
                    tradeService.getDetails().put("CDTUnitCodeForSendToMobBoc",formDetails.processingUnitCode)

//                    Calendar calendar = Calendar.getInstance()
//                    calendar.set(Calendar.HOUR_OF_DAY, 0);
//                    calendar.set(Calendar.MINUTE, 0);
//                    calendar.set(Calendar.SECOND, 0);
//                    calendar.set(Calendar.MILLISECOND, 0);
					Date confirmationDate = getDateWithoutTime(new Date());
					println ("00confirmationDate : " + formDetails.confDate + "\t\t" + confirmationDate)
					
					if(formDetails.confDate != null && !formDetails.confDate.toString().equalsIgnoreCase("")){
						tradeService.getDetails().put("systemDateSent",confirmationDate)
						confirmationDate = getDateWithoutTime(new Date(formDetails.confDate));						
						tradeService.getDetails().put("chosenConfirmationDate",confirmationDate)
					}
					println ("01confirmationDate : " + formDetails.confDate + "\t\t" + confirmationDate)
					
					def cdtListTest = cdtPaymentRequestRepository.getAllPaidRequests(formDetails.unitCode,confirmationDate)
					println "cdtListTest....."+cdtListTest
					if (cdtListTest == null || cdtListTest.isEmpty()) {
						throw new Exception("No CDT History file uploaded with confirmation dated " + confirmationDate.format("MMM dd, yyyy"));
					}
					
                    tradeService.setProcessDate(confirmationDate);
                    tradeService.setProcessingUnitCode(formDetails.processingUnitCode)

                    tradeServiceRepository.saveOrUpdate(tradeService)
                }
//				throw new Exception("Force Error")
                referenceNumber =  paymentService.sendToMobBoc(amount, formDetails.userId, formDetails.supervisorId, formDetails.accountNumber, tradeService, formDetails.accountName);
			
			
				def dateObj = new Date(formDetails.confDate)//.toString().replaceAll("/", "-") 
			
			
//			DateFormat newDateFormat = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
//			Date d = newDateFormat.parse(dateObj+" 00:00:00");
				
			
//				def confDate = getDateWithoutTime(new Date(dateObj));

                // tag as sent to mob boc
                def cdtList = cdtPaymentRequestRepository.getAllPaidRequests(formDetails.unitCode,dateObj)
                println "tagging as sent to mob-boc"

                cdtList.each {
                    it.status = CDTStatus.SENTTOBOC
                    it.dateSent = new Date()
                    cdtPaymentRequestRepository.merge(it)
					
					// Email here
//					try{
//						RefPas5Client refPas5Client =  refPas5ClientRepository.load(it.agentBankCode)
//						println "RefPas5Client: " + refPas5Client
//						if (refPas5Client.getEmail()){
//							Map<String, Object> paymentDetails = paymentRepository.getCDTPaymentDetails(it.iedieirdNumber)
//							println "Payment Details: " + paymentDetails;
//							EmailService emailService = new EmailService();
//							Email mailDetails = new DebitMemoEmail(refPas5Client, paymentDetails);
//							emailService.sendEmail(smtpAuthenticator, mailFrom, mailSender, mailDetails);
//						}
//					}catch(Exception e){
//						e.printStackTrace();
//					}	
                }

                String gltsNumber = gltsSequenceRepository.getGltsSequence();
                println ">>>>>>>>>>>>>>>>>>>>>>>>>>>Clearing accountingEntryActualRepository"
                accountingEntryActualRepository.delete(tradeService.getTradeServiceId());
                println "tradeService.getStatus().toString():"+tradeService?.getStatus()?.toString()
                accountingService.generateActualEntriesSendMobToBOC(tradeService, gltsNumber, "APPROVED" , amount);
                gltsSequenceRepository.incrementGltsSequence();

                returnMap.put("status", "ok");
                returnMap.put("details", ["referenceNumber": referenceNumber, "tradeServiceId": tradeService?.tradeServiceId?.tradeServiceId])
//                } else {
//                    returnMap.put("status", "error");
//                    returnMap.put("error", response.error.toString());
//                }
            } catch (CasaServiceException e){
                errorMessage = e.getErrorCode() + " : "  + e.getCasaErrorMessage();
				
                returnMap.put("status", "error");
                returnMap.put("error", errorMessage);
            } 
        } catch(Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", e.getMessage());
        }

        // format return data as json
        result = gson.toJson(returnMap);

        return Response.status(200).entity(result).build();
    }

    private Map<String, Object> getAccountName(String accountNumber, String userId, String currency) {
        String errorMessage;
        CasaAccount account;

        def response = [:]


        try{
//            account = paymentService.getAccountDetails(parameterMap["accountNumber"],parameterMap["userId"]);
            // MARV: added currency code for message string
            account = paymentService.getAccountDetails(accountNumber, userId, Currency.getInstance(currency));

            String accountName = "";
            try {
                List<Map<String,Object>> accounts = customerInformationFileService.getCasaAccountsByNumberAndCurrency(account?.getAccountNumber(), account?.getCurrency()?.getName());
                if (accounts.size() > 0) {
                    for(Map<String,Object> accountDetail : accounts) {
                        if (accountDetail.get("ACCOUNT_NAME").toString().trim().contains(account?.getAccountName()) || account?.getAccountName().contains(accountDetail.get("ACCOUNT_NAME").toString().trim())) {
                            if (!accountDetail.get("ACCOUNT_FULL_NAME").equals(null) && !accountDetail.get("ACCOUNT_FULL_NAME").toString().trim().isEmpty() && !accountDetail.get("ACCOUNT_FULL_NAME").toString().trim().equals("")) {
                                accountName = accountDetail.get("ACCOUNT_FULL_NAME").toString().trim();
                            } else {
                                accountName = accountDetail.get("ACCOUNT_NAME").toString().trim();
                            }
                        } else {
                            accountName = account?.getAccountName();
                        }
                    }
                } else {
                    accountName = account?.getAccountName();
                }
            } catch (Exception e) {
                accountName = account?.getAccountName();
            } finally {
                response = [success: StringUtils.isEmpty(errorMessage),
                        accountNumber : account?.getAccountNumber(),
                        status : account?.getAccountStatus(),
                        accountName : accountName,
                        currency : account?.getCurrency()?.getName(),
                        accountType : account?.getAccountType()];
            }
        } catch(CasaServiceException e) {
            errorMessage = e.getCasaErrorMessage();

            response = ["status": "error", "error": errorMessage]
        } catch(InvalidAccountNumberFormatException ianf) {
            errorMessage = ianf.getMessage();

            response = ["status": "error", "error": errorMessage]
        }

        return response
    }

    private List<PaymentRequestType> getPaymentRequestTypesFromString(String paymentRequestTypeString) {
        println "getPaymentRequestTypesFromString:"+paymentRequestTypeString
        List<PaymentRequestType> paymentRequestTypeList = new ArrayList<>()

        switch (paymentRequestTypeString) {

            case "FINAL_CDT":
                paymentRequestTypeList.add(PaymentRequestType.FINAL)
                break
            case "ADVANCE_CDT":
                paymentRequestTypeList.add(PaymentRequestType.ADVANCE)
                break
            case "IPF_EXPORT_CHARGES":
                paymentRequestTypeList.add(PaymentRequestType.IPF)
                paymentRequestTypeList.add(PaymentRequestType.EXPORT)
                break
            case "FINAL_ADVANCE_CDT":
                paymentRequestTypeList.add(PaymentRequestType.FINAL)
                paymentRequestTypeList.add(PaymentRequestType.ADVANCE)
                break
        }

        if(paymentRequestTypeString?.equalsIgnoreCase("")||paymentRequestTypeString == null){
            return null;
        } else {
            return paymentRequestTypeList
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getAllSentToMobBoc")
    public Response getAllSentToMobBoc(@Context UriInfo allUri) {
//    public Response executeQuery(@PathParam("actionName") String query) {
        println "getAllSentToMobBoc"
        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for(String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {
            println jsonParams.get("collectionPeriodFrom")
            println jsonParams.get("collectionPeriodTo")

            Calendar toCal = Calendar.getInstance()
            toCal.setTime(new Date((String) jsonParams.get("collectionPeriodTo")))
            toCal.set(Calendar.HOUR, 23)
            toCal.set(Calendar.MINUTE, 59)
            toCal.set(Calendar.SECOND, 59)
            toCal.set(Calendar.MILLISECOND, 999)

            def cdtList = cdtPaymentRequestRepository.getAllSentRequests(new Date((String) jsonParams.get("collectionPeriodFrom")),
                    toCal.getTime(),
                    getPaymentRequestTypesFromString(jsonParams.get("paymentRequestType").toString()))

            returnMap.put("status", "ok");
            returnMap.put("details", cdtList)

        } catch(Exception e) {

            Map errorDetails = new HashMap();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/debitFromRemittance")
    public Response debitFromRemittance(@Context UriInfo allUri, String postRequestBody) {
        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();

        try {
            Map formDetails = gson.fromJson(postRequestBody, Map.class);

            Assert.notNull(formDetails.userId, "User Id must not be null!");
            Assert.notNull(formDetails.accountNumber, "Account Number must not be null!");
	
		
            String referenceNumber;
            String errorMessage;

            BigDecimal amount = new BigDecimal(formDetails.amount?.replaceAll(",", ""))

            try{
                TradeService tradeService = tradeServiceRepository.load(new TradeServiceId(formDetails.tradeServiceId))
				def details = tradeService.getDetails()
				println ("NEW: " + amount+ "NEW: " + formDetails.userId+ "NEW: " + formDetails.supervisorId+ "NEW: " + formDetails.accountNumber+ "NEW: " + tradeService+ "NEW: " + formDetails.accountName)
                referenceNumber = paymentService.debitFromRemittance(amount, formDetails.userId, formDetails.supervisorId, formDetails.accountNumber, tradeService, formDetails.accountName)

				details.put("referenceNumber", referenceNumber)
				
				tradeService.setDetails(details)
				
				tradeServiceRepository.merge(tradeService)
				
                returnMap.put("status", "ok");
				returnMap.put("details", "");
			
//                returnMap.put("details", "")

            }catch (CasaServiceException e){
                errorMessage = e.getErrorCode() + " : "  + e.getCasaErrorMessage();

                returnMap.put("status", "error");
                returnMap.put("error", errorMessage);
            }
        } catch(Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        return Response.status(200).entity(result).build();
    }

	public  testRem(String tradeServiceId) {
	
		Gson gson = new Gson();

		String result="";
		Map returnMap = new HashMap();

		try {


			String referenceNumber;
			String errorMessage;



			try{
				TradeService tradeService = tradeServiceRepository.load(new TradeServiceId(tradeServiceId))

				Map<String, Object> details = tradeService.getDetails();


				PaymentRequestType paymentRequestType = null;
				if (tradeService.getDetails().get("reportType").equals("IPF")) {
					paymentRequestType = null;
				} else if (tradeService.getDetails().get("reportType").equals("FINAL_CDT")) {
					paymentRequestType = PaymentRequestType.FINAL;
				} else if (tradeService.getDetails().get("reportType").equals("ADVANCE_CDT")) {
					paymentRequestType = PaymentRequestType.ADVANCE;
				} else if (tradeService.getDetails().get("reportType").equals("EXPORT_CHARGES")) {
					paymentRequestType = PaymentRequestType.EXPORT;
				}


				println "##############################000000000000000000000000000002222222222222222222222222222222222222222";
				println tradeService.getDetails().get("reportType")
				println details.get("collectionPeriodFrom")
				println details.get("collectionPeriodTo")
				println details.get("paymentRequestType").toString()
				println tradeService.toString();
				println "##############################000000000000000000000000000002222222222222222222222222222222222222222";
				Calendar toCal = Calendar.getInstance()
				toCal.setTime(new Date((String) tradeService.getDetails().get("collectionPeriodTo")))
				toCal.set(Calendar.HOUR, 23)
				toCal.set(Calendar.MINUTE, 59)
				toCal.set(Calendar.SECOND, 59)
				toCal.set(Calendar.MILLISECOND, 999)


				def cdtList = cdtPaymentRequestRepository.getAllSentRequests(new Date((String) details.get("collectionPeriodFrom")),
						toCal.getTime(),
						getPaymentRequestTypesFromString(tradeService.getDetails().get("reportType")))

				println "##############################000000000000000000000000000002222222222222222222222222222222222222222###";
				println cdtList.toString();
			
				Date dateRemitted = new Date()

				for (CDTPaymentRequest cdtPaymentRequest : cdtList) {
					def sType = tradeService.getDetails().get("reportType").toString()
			
					println cdtPaymentRequest.toString();


					if(sType.equalsIgnoreCase("IPF")){
						println "ipf"
						cdtPaymentRequest.setIPFRemittedDate(dateRemitted);
						if(cdtPaymentRequest.getDateRemitted() != null)
						{
							cdtPaymentRequest.tagAsRemitted();
							cdtPaymentRequest.setStatus(CDTStatus.REMITTED);

						}
					}

					else if(sType.equalsIgnoreCase("FINAL_CDT") || sType.equalsIgnoreCase("ADVANCE_CDT") || sType.equalsIgnoreCase("EXPORT_CHARGES") ){
							println tradeService.getDetails().get("reportType").toString() + "##############################"
						if(tradeService.getDetails().get("reportType").toString().equalsIgnoreCase("FINAL_CDT") && !cdtPaymentRequest.getIedieirdNumber().toString().contains(" E")){
							println "FINAL 1st"
							println cdtPaymentRequest.getPaymentRequestType().toString() + "##########################"

							println "FINAL"
							cdtPaymentRequest.setDutiesAndTaxesRemittedDate(dateRemitted)
							cdtPaymentRequest.setDateRemitted(dateRemitted);
							if(cdtPaymentRequest.getIpf() <= 0 || cdtPaymentRequest.getIPFRemittedDate() != null)
							{
								cdtPaymentRequest.tagAsRemitted();
								cdtPaymentRequest.setStatus(CDTStatus.REMITTED);

							}

						}else if(sType.equalsIgnoreCase("ADVANCE_CDT") && !cdtPaymentRequest.getIedieirdNumber().toString().contains(" E")){
							println "ADVANCE 1st"

							println "ADVANCE"
							cdtPaymentRequest.setDutiesAndTaxesRemittedDate(dateRemitted)
							cdtPaymentRequest.setDateRemitted(dateRemitted);
							if(cdtPaymentRequest.getIpf() <= 0 || cdtPaymentRequest.getIPFRemittedDate() != null)
							{
								cdtPaymentRequest.tagAsRemitted();
								cdtPaymentRequest.setStatus(CDTStatus.REMITTED);

							}

						}else if(sType.equalsIgnoreCase("EXPORT_CHARGES") && cdtPaymentRequest.getIedieirdNumber().toString().contains(" E")){
							println "EXPORT 1st"
						
								println "EXPORT"
								cdtPaymentRequest.setDutiesAndTaxesRemittedDate(dateRemitted)
								cdtPaymentRequest.setDateRemitted(dateRemitted);
								if(cdtPaymentRequest.getIpf() <= 0 || cdtPaymentRequest.getIPFRemittedDate() != null )
								{
									cdtPaymentRequest.tagAsRemitted();
									cdtPaymentRequest.setStatus(CDTStatus.REMITTED);

								}
							
						}

					}else{
							println "ALL"

							if(cdtPaymentRequest.getDutiesAndTaxesRemittedDate() == null)
								cdtPaymentRequest.setDutiesAndTaxesRemittedDate(dateRemitted)
							if(cdtPaymentRequest.getIPFRemittedDate() == null)
								cdtPaymentRequest.setIPFRemittedDate(dateRemitted);
							if(cdtPaymentRequest.getDateRemitted() == null)
							cdtPaymentRequest.setDateRemitted(dateRemitted);
							
							cdtPaymentRequest.tagAsRemitted();
							cdtPaymentRequest.setStatus(CDTStatus.REMITTED);
						}
					println "tag as remitted "
					cdtPaymentRequestRepository.merge(cdtPaymentRequest)


				}

				returnMap.put("status", "ok");
				returnMap.put("details", "")

			}catch (CasaServiceException e){
				errorMessage = e.getErrorCode() + " : "  + e.getCasaErrorMessage();

				returnMap.put("status", "error");
				returnMap.put("error", errorMessage);
			}
		} catch(Exception e) {

			Map errorDetails = new HashMap();

			e.printStackTrace();

			errorDetails.put("code", e.getMessage());
			errorDetails.put("description", e.toString());

			returnMap.put("status", "error");
			returnMap.put("error", errorDetails);
		}

		// format return data as json
		result = gson.toJson(returnMap);

		println result.toString();
	};


	
	
	    // debit or credit for refund
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/errorCorrectCasa")
    public Response errorCorrectCasa(@Context UriInfo allUri, String postRequestBody) {
        Gson gson = new Gson()

        String result="";
        Map returnMap = new HashMap();

        try {
            Map formDetails = gson.fromJson(postRequestBody, Map.class);

            Assert.notNull(formDetails.userId, "User Id must not be null!");
            Assert.notNull(formDetails.accountNumber, "Account Number must not be null!");
            Assert.notNull(formDetails.currency, "Currency must not be null!");
            Assert.notNull(formDetails.amount, "Amount must not be null!");
            Assert.notNull(formDetails.type, "Type not be null!");
            Assert.notNull(formDetails.supervisorId, "Supervisor Id must not be null!");

            String referenceNumberDebit;
			String referenceNumberCredit;
            String errorMessage;

            BigDecimal amount = new BigDecimal(formDetails.amount?.replaceAll(",", ""))

            try{
                TradeService tradeService = tradeServiceRepository.load(new TradeServiceId(formDetails.tradeServiceId))

                Currency currency = Currency.getInstance(formDetails.currency)

                String accountName

                if ("DEBIT".equals(formDetails.type)) {
                    def account = paymentService.getAccountDetails(formDetails.accountNumber,
                            formDetails.userId,
                            currency);

                    accountName = account.getAccountName()
					
					println "accountName ==== " + accountName
					
					referenceNumberDebit =  paymentService.reverseCasa(formDetails.type,
																		formDetails.userId,
																		tradeService,
																		formDetails.supervisorId,
																		formDetails.accountNumber,
																		currency,
																		amount,
																		accountName,
																		tradeService.getDetails().get("referenceNumberDebit"));
                } else {
                    accountName = formDetails.accountName
					
					println "accountName ==== " + accountName
					
					referenceNumberCredit =  paymentService.reverseCasa(formDetails.type,
																		formDetails.userId,
																		tradeService,
																		formDetails.supervisorId,
																		formDetails.accountNumber,
																		currency,
																		amount,
																		accountName,
																		tradeService.getDetails().get("referenceNumberCredit"));
                }               

                returnMap.put("status", "ok");
                returnMap.put("details", ["referenceNumberCredit": referenceNumberCredit, "referenceNumberDebit": referenceNumberDebit])
            }catch (CasaServiceException e){
                errorMessage = e.getErrorCode() + " : "  + e.getCasaErrorMessage();

                returnMap.put("status", "error");
                returnMap.put("error", errorMessage);
            }
        } catch(Exception e) {
            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        return Response.status(200).entity(result).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/payCasa")
    public Response payCasa(@Context UriInfo allUri, String postRequestBody) {
        Gson gson = new Gson()

        String result="";
        Map returnMap = new HashMap();

        try {
            Map formDetails = gson.fromJson(postRequestBody, Map.class);

            Assert.notNull(formDetails.userId, "User Id must not be null!");
            Assert.notNull(formDetails.accountNumber, "Account Number must not be null!");
            Assert.notNull(formDetails.type, "Type must not be null!");
            Assert.notNull(formDetails.amount, "Amount must not be null!");

            String referenceNumberDebit;
			String referenceNumberCredit;
            String errorMessage;

            BigDecimal amount = new BigDecimal(formDetails.amount?.replaceAll(",", ""))

            Currency currency = Currency.getInstance(formDetails.currency)

            try{
                TradeService tradeService = tradeServiceRepository.load(new TradeServiceId(formDetails.tradeServiceId))

                def details = tradeService.getDetails()

                if ("CREDIT".equals((String) formDetails.type)) {
                    referenceNumberCredit =  paymentService.creditAccount(amount, currency, formDetails.userId, formDetails.supervisorId, formDetails.accountNumber, tradeService, formDetails.accountName);
                    details.put("creditTransactionStatus", "PAID")
                    println "formDetails.accountName ==== " + formDetails.accountName
					details.put("referenceNumberCredit", referenceNumberCredit)
                } else if ("DEBIT".equals((String) formDetails.type)) {
                    def account = paymentService.getAccountDetails(formDetails.accountNumber,
                            formDetails.userId,
                            Currency.getInstance(formDetails.currency));
                    println "account.getAccountName()==== " + account.getAccountName()
                    referenceNumberDebit =  paymentService.debitAccount(amount, formDetails.userId, formDetails.supervisorId, formDetails.accountNumber, tradeService, account.getAccountName());
                    details.put("debitTransactionStatus", "PAID")
					details.put("referenceNumberDebit", referenceNumberDebit)
                }

                tradeService.setDetails(details)

                tradeServiceRepository.merge(tradeService)

                returnMap.put("status", "ok");
                returnMap.put("details", ["referenceNumberCredit": referenceNumberCredit, "referenceNumberDebit": referenceNumberDebit])
            }catch (CasaServiceException e){
                errorMessage = e.getErrorCode() + " : "  + e.getCasaErrorMessage();

                returnMap.put("status", "error");
                returnMap.put("error", errorMessage);
            }
        } catch(Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        return Response.status(200).entity(result).build();
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/tagAsPaid")
    public Response tagAsPaid(@Context UriInfo allUri, String postRequestBody) {
        Gson gson = new Gson()

        String result="";
        Map returnMap = new HashMap();

        try {
            Map formDetails = gson.fromJson(postRequestBody, Map.class);
            String errorMessage = ""

            try{
                println "formDetails.iedierdNumber " + formDetails.iedierdNumber
//                pas5FilesLoaderService.tagAsPaid((String) formDetails.iedierdNumber)
                TradeService tradeService = tradeServiceRepository.load(new TradeServiceId(formDetails.tradeServiceId))

                pas5FilesLoaderService.tagAsPaid((String) formDetails.iedierdNumber, tradeService)

                returnMap.put("status", "ok");
                CDTPaymentRequest cdtPaymentRequest = cdtPaymentRequestRepository.load(formDetails.iedierdNumber);
                if (cdtPaymentRequest.getPaymentReferenceNumber()) {
                    returnMap.put("paymentReferenceNumber", cdtPaymentRequest.getPaymentReferenceNumber())
                }
            }catch (CasaServiceException e){
                errorMessage = e.getErrorCode() + " : "  + e.getCasaErrorMessage();

                returnMap.put("status", "error");
                returnMap.put("error", errorMessage);
            }
        } catch(Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        return Response.status(200).entity(result).build();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getCurrentTradeService")
    public Response getCurrentTradeService(@Context UriInfo allUri) {
//    public Response executeQuery(@PathParam("actionName") String query) {

        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for(String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

//            Calendar calendar = Calendar.getInstance()
//            calendar.set(Calendar.HOUR_OF_DAY, 0);
//            calendar.set(Calendar.MINUTE, 0);
//            calendar.set(Calendar.SECOND, 0);
//            calendar.set(Calendar.MILLISECOND, 0);
			
			
			Date confirmationDate = getDateWithoutTime(new Date());
			if(jsonParams.get("confDate") != null && !jsonParams.get("confDate").toString().equalsIgnoreCase("")){
				confirmationDate = getDateWithoutTime(new Date(jsonParams.get("confDate")));
			}

            def currentTradeService = tradeServiceRepository.getCurrentTradeService(confirmationDate, jsonParams.get("unitCode"))

            returnMap.put("status", "ok");
            returnMap.put("details", [tradeServiceId: currentTradeService?.tradeServiceId?.tradeServiceId,
                    sentAmount: currentTradeService?.details?.sentAmount])

        } catch(Exception e) {

            Map errorDetails = new HashMap();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/errorCorrectMobBoc")
    public Response errorCorrectMobBoc(@Context UriInfo allUri, String postRequestBody) {
        Gson gson = new Gson()

        String result="";
        Map returnMap = new HashMap();

        try {
            Map formDetails = gson.fromJson(postRequestBody, Map.class);

            Assert.notNull(formDetails.userId, "User Id must not be null!");
            Assert.notNull(formDetails.accountNumber, "Account Number must not be null!");
            Assert.notNull(formDetails.amount, "Amount must not be null!");
            Assert.notNull(formDetails.supervisorId, "Supervisor Id must not be null!");

            String referenceNumber;
            String errorMessage;

            BigDecimal amount = new BigDecimal(formDetails.amount?.replaceAll(",", ""))

            try{
                TradeService tradeService = tradeServiceRepository.load(new TradeServiceId(formDetails.tradeServiceId))

                if (PaymentStatus.UNPAID.equals(tradeService.getPaymentStatus())) {
                    throw new Exception("CDT already Error Corrected.");
                }

                referenceNumber =  paymentService.reverseCasaMobBoc(formDetails.userId,
                        tradeService,
                        formDetails.supervisorId,
                        formDetails.accountNumber,
                        amount,
                        formDetails.accountName,
                        tradeService.getDetails().get("referenceNumber"));

                accountingService.deleteActualEntries(tradeService.getTradeServiceId())
//                def cdtList = cdtPaymentRequestRepository.getAllSentToMobBoc()
                accountingService.deleteActualEntries(tradeService.getTradeServiceId())
                def cdtList = cdtPaymentRequestRepository.getAllSentToMobBoc(formDetails.unitCode)
                println "reverting back to paid"
                cdtList.each {
                    it.status = CDTStatus.PAID
                    it.dateSent = new Date()
                    cdtPaymentRequestRepository.merge(it)

//                    TradeService tr = tradeServiceRepository.load(new TradeServiceReferenceNumber(it.iedieirdNumber.toString()))
//                    accountingEntryActualRepository.delete(tr.getTradeServiceId());
                }

                returnMap.put("status", "ok");
                returnMap.put("details", ["referenceNumber": referenceNumber])
            }catch (CasaServiceException e){
                errorMessage = e.getErrorCode() + " : "  + e.getCasaErrorMessage();

                returnMap.put("status", "error");
                returnMap.put("error", errorMessage);
            }
        } catch(Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", e.getMessage());
        }

        // format return data as json
        result = gson.toJson(returnMap);

        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/history/getCurrentTotal")
    public Response getCurrentTotal(@Context UriInfo allUri) {
        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for(String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

		
		
		
		Date test = new Date()
		if(jsonParams.get("confDate"))
		test = new Date(jsonParams.get("confDate"))
		
		
        try {

            //List allRequests = cdtPaymentRequestRepository.getAllRequests();
            println "jsonParams " + jsonParams

		
            def totalHistoryAmount = cdtPaymentHistoryRepository.getTotalAmount(test, jsonParams.get("unitCode"));
		
		
            returnMap.put("status", "ok");
            returnMap.put("details", totalHistoryAmount)

        } catch(Exception e) {

            Map errorDetails = new HashMap();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/errorCorrectRemittance")
    public Response errorCorrectRemittance(@Context UriInfo allUri, String postRequestBody) {
        Gson gson = new Gson()

        String result="";
        Map returnMap = new HashMap();

        try {
            Map formDetails = gson.fromJson(postRequestBody, Map.class);

            Assert.notNull(formDetails.userId, "User Id must not be null!");
            Assert.notNull(formDetails.accountNumber, "Account Number must not be null!");
            Assert.notNull(formDetails.amount, "Amount must not be null!");
            Assert.notNull(formDetails.supervisorId, "Supervisor Id must not be null!");

            String referenceNumber;
            String errorMessage;

            BigDecimal amount = new BigDecimal(formDetails.amount?.replaceAll(",", ""))

            try{
                TradeService tradeService = tradeServiceRepository.load(new TradeServiceId(formDetails.tradeServiceId))

//                if (PaymentStatus.UNPAID.equals(tradeService.getPaymentStatus())) {
//                    throw new Exception("CDT already Error Corrected.");
//                }

                referenceNumber =  paymentService.reverseCasaRemittance(formDetails.userId,
                        tradeService,
                        formDetails.supervisorId,
                        formDetails.accountNumber,
                        amount,
                        formDetails.accountName,
                        tradeService.getDetails().get("referenceNumber"));

                Calendar toCal = Calendar.getInstance()
                toCal.setTime(new Date((String) formDetails.get("collectionPeriodTo")))
                toCal.set(Calendar.HOUR, 23)
                toCal.set(Calendar.MINUTE, 59)
                toCal.set(Calendar.SECOND, 59)
                toCal.set(Calendar.MILLISECOND, 999)

                def cdtList = cdtPaymentRequestRepository.getAllRemittedRequests(new Date((String) formDetails.get("collectionPeriodFrom")),
                        toCal.getTime(),
                        getPaymentRequestTypesFromString(formDetails.get("paymentRequestType").toString()))

                for (CDTPaymentRequest cdtPaymentRequest : cdtList) {
                    cdtPaymentRequest.tagAsNotRemitted();
                    println " tag as not remitted "
                    cdtPaymentRequestRepository.merge(cdtPaymentRequest)
                }

                cdtService.throwCdtRemittanceErrorCorrectedEvent(tradeService, cdtList);

                returnMap.put("status", "ok");
                returnMap.put("details", ["referenceNumber": referenceNumber])
            }catch (CasaServiceException e){
                errorMessage = e.getErrorCode() + " : "  + e.getCasaErrorMessage();

                returnMap.put("status", "error");
                returnMap.put("error", errorMessage);
            }
        } catch(Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", e.getMessage());
        }

        // format return data as json
        result = gson.toJson(returnMap);

        return Response.status(200).entity(result).build();
    }
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/tagAsNew")
    public Response tagAsNew(@Context UriInfo allUri, String postRequestBody) {
        Gson gson = new Gson()

        String result="";
        Map returnMap = new HashMap();

        try {
            Map formDetails = gson.fromJson(postRequestBody, Map.class);
            String errorMessage = ""

            try{
                println "formDetails.iedierdNumber " + formDetails.iedierdNumber
//                println "formDetails.tradeServiceId " + formDetails.tradeServiceId

//                TradeService tradeService = tradeServiceRepository.load(new TradeServiceId((String) formDetails.tradeServiceId))
                pas5FilesLoaderService.tagAsNew((String) formDetails.iedierdNumber)

                returnMap.put("status", "ok");

                CDTPaymentRequest cdtPaymentRequest = cdtPaymentRequestRepository.load(formDetails.iedierdNumber);
                if (cdtPaymentRequest.getPaymentReferenceNumber()) {
                    returnMap.put("paymentReferenceNumber", cdtPaymentRequest.getPaymentReferenceNumber())
                }
            }catch (CasaServiceException e){
                errorMessage = e.getErrorCode() + " : "  + e.getCasaErrorMessage();

                returnMap.put("status", "error");
                returnMap.put("error", errorMessage);
            }
        } catch(Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        return Response.status(200).entity(result).build();
    }
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
    @Path("/payment/generateTramsReport")
    public Response generateTramsReport(@Context UriInfo allUri) {
        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for(String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {
            println "jsonParams " + jsonParams

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

            def allCDTsForTrams = pas5FilesLoaderService.generateTrams(simpleDateFormat.parse(jsonParams.get("dateGenerated")))

            returnMap.put("status", "ok");
            returnMap.put("details", allCDTsForTrams);
        } catch(Exception e) {

           Map errorDetails = new HashMap();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }
	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/payment/resendEmail")
    public Response CDTPaymentSearchResendDetails(@Context UriInfo allUri) {
		
		Gson gson = new Gson();

		String result="";
		Map returnMap = new HashMap();
		Map jsonParams = new HashMap<String, String>();

		// get all query parameters
		MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

		for(String key : mpAllQueParams.keySet()) {
			// if there are multiple instances of the same param, we only use the first one
			jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
		}
		
		String iedieirdNumbers = jsonParams.get("iedieirdNumber");
		iedieirdNumbers = iedieirdNumbers.replace("[", "")
		iedieirdNumbers = iedieirdNumbers.replace("]", "")
		iedieirdNumbers = iedieirdNumbers.replace("\"", "")
		
		List <String> iedieirdList = iedieirdNumbers.split(",")
		Set<EmailNotif> emailSet = new HashSet<EmailNotif>();
		
		EmailNotif emailNotifs = new EmailNotif();
		List<String> finalEmailList = new ArrayList<String>();
		
		for (String iedieirdListStr : iedieirdList) {
			CDTPaymentRequest cdtPayment = cdtPaymentRequestRepository.load(iedieirdListStr);
			RefPas5Client refPas5Client = refPas5ClientRepository.load(cdtPayment.getAgentBankCode())
			
			HashMap rowData = new HashMap()
			
			
			rowData.put("iedieirdNumber", cdtPayment.getIedieirdNumber())
			rowData.put("agentBankCode", cdtPayment.getAgentBankCode())
			rowData.put("clientName", cdtPayment.getClientName())
			rowData.put("amount", cdtPayment.getAmount().toString().trim().replaceAll(",", ""))
			rowData.put("userrole", "samplerole")
			rowData.put("fullName", jsonParams.get("fullName"))
			rowData.put("email", jsonParams.get("email"))
			
			try {
				if (refPas5Client.getEmail()){
					EmailService emailService = new EmailService();
					String clientEmail1 = refPas5Client.getEmail();
					String clientEmail2 = refPas5Client.getRmbmEmail();
					String clientEmail3 = refPas5Client.getBranchEmail();
					String allEmail = clientEmail1 +" "+ clientEmail2 +" "+ clientEmail3;
					
					emailNotifs.setEmailAddress(allEmail)
					emailNotifs.setIedieirdNumber(iedieirdListStr)
					
					String subject = "Advance duties - "+ cdtPayment.getClientName() +" - "+ iedieirdListStr;
					if (cdtPayment.paymentRequestType.toString().equalsIgnoreCase("final")) {
						subject = "Final duties - "+ cdtPayment.getClientName() +" - "+ iedieirdListStr;
					}

					Email mailDetails = new CDTEmail(refPas5Client, rowData, "CASA", subject);
					emailService.sendCdtEmail(smtpAuthenticator, mailFrom, mailSender, mailDetails);
                    cdtPayment.setEmailed(Boolean.TRUE);
                    cdtPayment.setImportersEmail(clientEmail1);
                    cdtPayment.setRmbmEmail(clientEmail2);
                    cdtPayment.setBranchEmail(clientEmail3);
					emailNotifs.setEmailStatus("E-mail sent");
					emailNotifs.setSentTime(new Date());
				}
			} catch(Exception e) {
				e.printStackTrace();
                cdtPayment.setEmailed(Boolean.FALSE);
                emailNotifs.setEmailStatus("E-mail not sent");
				emailNotifs.setSentTime(null);
				returnMap.put("status", "error");
			}
			
			emailSet.add(emailNotifs);
			cdtPayment.setEmailNotifs(emailSet);
			
			cdtPaymentRequestRepository.merge(cdtPayment)
			returnMap.put("status", "ok");
		}
		
		result = gson.toJson(returnMap);

		// todo: we should probably return the appropriate HTTP error codes instead of always returning 200
		return Response.status(200).entity(result).build();
		
	}
//
//@GET
//@Produces(MediaType.APPLICATION_JSON)
//@Path("/payment/getEmployee")
//public Response generateEmailInfo(@Context UriInfo allUri) {
//	Gson gson = new Gson();
//
//	String result="";
//	Map returnMap = new HashMap();
//	Map jsonParams = new HashMap<String, String>();
//
//	// get all query parameters
//	MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();
//
//	for(String key : mpAllQueParams.keySet()) {
//
//		// if there are multiple instances of the same param, we only use the first one
//		jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
//	}
//
//	try {
//		println "jsonParams " + jsonParams
//
//		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
//
//		def allCDTsForTrams = pas5FilesLoaderService.generateTrams(simpleDateFormat.parse(jsonParams.get("dateGenerated")))
//
//		returnMap.put("status", "ok");
//		returnMap.put("details", allCDTsForTrams);
//	} catch(Exception e) {
//
//		Map errorDetails = new HashMap();
//
//		errorDetails.put("code", e.getMessage());
//		errorDetails.put("description", e.toString());
//
//		returnMap.put("status", "error");
//		returnMap.put("error", errorDetails);
//	}
//
//	// format return data as json
//	result = gson.toJson(returnMap);
//
//	// todo: we should probably return the appropriate HTTP error codes instead of always returning 200
//	return Response.status(200).entity(result).build();
//}
}
