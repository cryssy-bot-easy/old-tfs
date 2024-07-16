package com.ucpb.tfs2.infrastructure.rest

import com.google.gson.Gson
import com.ucpb.tfs.application.service.AccountingService
import com.ucpb.tfs.application.service.ChargesService
import com.ucpb.tfs.domain.accounting.AccountingEntryActualRepository
import com.ucpb.tfs.domain.accounting.AccountingEntryActualVariablesRepository
import com.ucpb.tfs.domain.accounting.AccountingEntryRepository
import com.ucpb.tfs.domain.accounting.ProfitLossHolderRepository
import com.ucpb.tfs.domain.instruction.ServiceInstructionId
import com.ucpb.tfs.domain.instruction.ServiceInstructionRepository
import com.ucpb.tfs.domain.mtmessage.MtMessageRepository
import com.ucpb.tfs.domain.mtmessage.MtMessage
import com.ucpb.tfs.domain.payment.PaymentRepository
import com.ucpb.tfs.domain.product.DocumentNumber
import com.ucpb.tfs.domain.product.TradeProduct
import com.ucpb.tfs.domain.product.TradeProductRepository
import com.ucpb.tfs.domain.reference.GltsSequenceRepository
import com.ucpb.tfs.domain.service.*
import com.ucpb.tfs.domain.service.enumTypes.*
import com.ucpb.tfs2.application.service.TradeServiceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.*

/*
	 (revision)
	 SCR/ER Number: ER# 20170109-040
	 SCR/ER Description: Transaction allowed to be created even the facility is expired
	 [Revised by:] Jesse James Joson
	 [Date revised:] 1/17/2017
	 Program [Revision] Details: Check the expiry date of the facility before allowing to amend LC
	 Member Type: Groovy
	 Project: Core
	 Project Name: TradeServiceRestServices.groovy
*/

@Path("/tradeservice")
@Component
class TradeServiceRestServices {

    @Autowired
    TradeServiceRepository tradeServiceRepository;

    @Autowired
    ServiceInstructionRepository serviceInstructionRepository;

    @Autowired
    TradeServiceService tradeServiceService

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    AccountingService accountingService;

    @Autowired
    AccountingEntryActualRepository accountingEntryActualRepository;

    @Autowired
    AccountingEntryActualVariablesRepository accountingEntryActualVariablesRepository;

    @Autowired
    GltsSequenceRepository gltsSequenceRepository;

    @Autowired
    ChargesService chargesService;

    @Autowired
    AccountingEntryRepository accountingEntryRepository;

    @Autowired
    MtMessageRepository mtMessageRepository;

    @Autowired
    ProfitLossHolderRepository profitLossHolderRepository;
	
	@Autowired
	TradeProductRepository tradeProductRepository;
	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/details")
    public Response getTradeServiceDetails(@Context UriInfo allUri) {

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            Map tradeServiceRecord;

            // in the repository calls below, we use getTradeServiceBy instead of load because we need to eager load
            // the entire object graph for the matching record

            // if trade service id was specified ...
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceRecord = tradeServiceRepository.getTradeServiceBy(new TradeServiceId(jsonParams.get("tradeServiceId").toString()));
            }

            // if ETS number was specified ...
            if (jsonParams.get("etsNumber") != null) {

                tradeServiceRecord = tradeServiceRepository.getTradeServiceBy(new ServiceInstructionId(jsonParams.get("etsNumber").toString()));

                // if we did not specify all records to be retrieved, check if the call requested the ETS as well
                // if it did, retrieve the ets and include it in the map
                if (jsonParams.get("includeETS") != null) {
                    Map ets = serviceInstructionRepository.getServiceInstructionBy(new ServiceInstructionId(jsonParams.get("etsNumber").toString()))

                    tradeServiceRecord.put("ets", ets)
                }
            }

            if (jsonParams.get("includePayments") != null) {
                List payments = paymentRepository.getAllPayments(new TradeServiceId(tradeServiceRecord.get("tradeServiceId").getAt("tradeServiceId")))

                tradeServiceRecord.put("payments", payments)
            }

            if (jsonParams.get("deNumber") != null) {
                tradeServiceRecord = tradeServiceRepository.getTradeServiceBy(new TradeServiceReferenceNumber(jsonParams.get("deNumber").toString()));
            }

            if (jsonParams.get("tradeServiceReferenceNumber") != null) {
                tradeServiceRecord = tradeServiceRepository.getTradeServiceBy(new TradeServiceReferenceNumber(jsonParams.get("tradeServiceReferenceNumber").toString()));
            }

            // if secret all was specified was specified ...
            if (jsonParams.get("all") != null) {

                // pass null and no criteria is added
                List allTradeServices;
                allTradeServices = tradeServiceRepository.getAllTradeService();
                returnMap.put("details", allTradeServices)

            } else {
                returnMap.put("details", tradeServiceRecord)
            }
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

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
    @Path("/save")
    public Response saveDataEntry(@Context UriInfo allUri, String postRequestBody) {

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();

        Map jsonParams = gson.fromJson(postRequestBody, Map.class);

        try {

            TradeService tradeService = null

            String tradeServiceId = jsonParams.tradeServiceId

            String userName = jsonParams.username
            String unitCode = jsonParams.unitcode

            // determine the document type and service for this request
            DocumentClass documentClass = null;
            if (jsonParams.get("documentClass") != null && !((String) jsonParams.get("documentClass")).trim().equals("")) {
                documentClass = DocumentClass.valueOf((String) jsonParams.get("documentClass"));
            }

            DocumentType documentType = null;
            if (jsonParams.get("documentType") != null && !((String) jsonParams.get("documentType")).trim().equals("")) {
                documentType = DocumentType.valueOf((String) jsonParams.get("documentType"));
            }

            DocumentSubType1 documentSubType1 = null;
            if (jsonParams.get("documentSubType1") != null && !((String) jsonParams.get("documentSubType1")).trim().equals("")) {
                documentSubType1 = DocumentSubType1.valueOf((String) jsonParams.get("documentSubType1"));
            }

            DocumentSubType2 documentSubType2 = null;
            if (jsonParams.get("documentSubType2") != null && !((String) jsonParams.get("documentSubType2")).trim().equals("")) {
                documentSubType2 = DocumentSubType2.valueOf((String) jsonParams.get("documentSubType2"));
            }

            ServiceType serviceType = null;
            if (jsonParams.get("serviceType") != null && !((String) jsonParams.get("serviceType")).trim().equals("")) {
                serviceType = ServiceType.valueOf(((String) jsonParams.get("serviceType")).toUpperCase());
            }

            // we need to have the fields listed here present in order to do something
            if (documentClass == null || serviceType == null || userName == null || unitCode == null) {
                returnMap.put("details", "invalid or missing parameters")
                returnMap.put("status", "error")
            } else {

                // if a TradeServiceId was passed, we retrieve the record from the repository
                if (tradeServiceId != null) {
                    tradeService = tradeServiceRepository.load(new TradeServiceId(tradeServiceId))
                }
                jsonParams.put('SAVE_DATA_ENTRY','Y')

                jsonParams = jsonParams.sort { it.key }
                println '*******************'
                jsonParams.each {
                    println it
                }
                println '*******************'

                if (documentClass in ["IMPORT_CHARGES", "EXPORT_CHARGES"]) {
                    if (jsonParams.get("currency") == null) {
                        jsonParams.put("currency", "PHP")
                    }
                }

                // call the service to create a new SI
                tradeService = tradeServiceService.saveTradeService(tradeService, documentClass, documentType, documentSubType1, documentSubType2, serviceType, jsonParams)

                if(jsonParams.get("mtMessageId")!=null && jsonParams.get("mtMessageId")!=''){
                    String mtMessageIdStr =jsonParams.get("mtMessageId");
                    Long mtMessageId = new Long(mtMessageIdStr);
                    println "mtMessageId:"+mtMessageId
                    MtMessage mtMessage =  mtMessageRepository.load(mtMessageId)
                    mtMessage.setTradeServiceId(tradeService.getTradeServiceId())
                    mtMessageRepository.merge(mtMessage)
                }

                // get a full map of the trade service we just saved
                Map savedTradeService = tradeServiceRepository.getTradeServiceBy(tradeService.getTradeServiceId())

                returnMap.put("details", savedTradeService)
                returnMap.put("status", "ok")
            }


        } catch (Exception e) {

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
    @Path("/generateSpecificActualAccountingEntry")
    public Response generateSpecificTradeServiceActualAccountingEntries(@Context UriInfo allUri) {

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {
            String tradeserviceidstr = null;
            if (jsonParams.get("tradeserviceid") != null && !((String) jsonParams.get("tradeserviceid")).trim().equals("")) {
                tradeserviceidstr = jsonParams.get("tradeserviceid").toString();
            }

            TradeServiceId tradeServiceId = new TradeServiceId(tradeserviceidstr);
            TradeService tradeService = tradeServiceRepository.load(tradeServiceId)



            if (tradeService != null) {
                TradeServiceStatus tradeServiceStatus = tradeService.getStatus();
                if (
                        tradeServiceStatus.equals(TradeServiceStatus.APPROVED)
                                || tradeServiceStatus.equals(TradeServiceStatus.POST_APPROVED)
                                || tradeServiceStatus.equals(TradeServiceStatus.PRE_APPROVED)
                                || tradeServiceStatus.equals(TradeServiceStatus.PREPARED)
                                || tradeServiceStatus.equals(TradeServiceStatus.POSTED)
                ) {
//                    String gltsNumber = gltsSequenceRepository.getGltsSequence();
//                    println ">>>>>>>>>>>>>>>>>>>>>>>>>>>Clearing accountingEntryActualRepository"
//                    accountingEntryActualRepository.delete(tradeServiceId);
//                    accountingService.generateActualEntriesWebService(tradeServiceId, gltsNumber);
//                    gltsSequenceRepository.incrementGltsSequence();
                }

                String gltsNumber = gltsSequenceRepository.getGltsSequence();
                println ">>>>>>>>>>>>>>>>>>>>>>>>>>>Clearing accountingEntryActualRepository"
                accountingEntryActualRepository.delete(tradeServiceId);
                profitLossHolderRepository.delete(tradeServiceId.toString());
                accountingService.generateActualEntriesWebService(tradeServiceId, gltsNumber, tradeService.getStatus().toString());
                gltsSequenceRepository.incrementGltsSequence();
            }



            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

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
    @Path("/generateActualAccountingEntry")
    public Response generateTradeServiceActualAccountingEntries(@Context UriInfo allUri) {

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {
            println ">>>>>>>>>>>>>>>>>>>>>>>>>>>Clearing accountingEntryActualRepository"
            accountingEntryActualRepository.clear();
            accountingEntryActualVariablesRepository.clear(); 

            List<TradeService> allTradeServices;
            allTradeServices = tradeServiceRepository.list();

            for (TradeService tradeService in allTradeServices) {
                TradeServiceStatus tradeServiceStatus = tradeService.getStatus();

                if (
                        tradeServiceStatus.equals(TradeServiceStatus.APPROVED)
                                || tradeServiceStatus.equals(TradeServiceStatus.POST_APPROVED)
                                || tradeServiceStatus.equals(TradeServiceStatus.PRE_APPROVED)
                                || tradeServiceStatus.equals(TradeServiceStatus.PREPARED)
                                || tradeServiceStatus.equals(TradeServiceStatus.POSTED)
                ) {
                    String gltsNumber = gltsSequenceRepository.getGltsSequence();
                    TradeServiceId tradeServiceId = (TradeServiceId) tradeService.getTradeServiceId();
                    accountingService.generateActualEntriesWebService(tradeServiceId, gltsNumber, tradeServiceStatus.toString());
                    gltsSequenceRepository.incrementGltsSequence();
                }
            }

            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

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
    @Path("/generateActualAccountingEntryAll")
    public Response generateTradeServiceActualAccountingEntriesAll(@Context UriInfo allUri) {

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {
            println ">>>>>>>>>>>>>>>>>>>>>>>>>>>Clearing accountingEntryActualRepository"
            accountingEntryActualRepository.clear();
            accountingEntryActualVariablesRepository.clear();

            List<TradeService> allTradeServices;
            allTradeServices = tradeServiceRepository.list();

            for (TradeService tradeService in allTradeServices) {
                TradeServiceStatus tradeServiceStatus = tradeService.getStatus();

                if (
                        tradeServiceStatus.equals(TradeServiceStatus.APPROVED)
                                || tradeServiceStatus.equals(TradeServiceStatus.POST_APPROVED)
                                || tradeServiceStatus.equals(TradeServiceStatus.PRE_APPROVED)
                                || tradeServiceStatus.equals(TradeServiceStatus.PREPARED)
                                || tradeServiceStatus.equals(TradeServiceStatus.POSTED)
                ) {

                }
                String gltsNumber = gltsSequenceRepository.getGltsSequence();
                TradeServiceId tradeServiceId = (TradeServiceId) tradeService.getTradeServiceId();
                accountingService.generateActualEntriesWebService(tradeServiceId, gltsNumber, tradeServiceStatus.toString());
                gltsSequenceRepository.incrementGltsSequence();
            }

            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

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
    @Path("/accountingEntry")
    public Response getTradeServiceAccountingEntryActual(@Context UriInfo allUri) {

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

//            Map tradeServiceRecord;

            // in the repository calls below, we use getTradeServiceBy instead of load because we need to eager load
            // the entire object graph for the matching record

            returnMap.put("entries", [])
            // if trade service id was specified ...
            if (jsonParams.get("tradeServiceId") != null) {
//                getTradeServiceBy();
                TradeServiceId tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
                Boolean doesTradeServiceRecordExist = tradeServiceRepository.exists(tradeServiceId)
                TradeService tradeServiceRecord = tradeServiceRepository.load(new TradeServiceId(jsonParams.get("tradeServiceId").toString()))
                //new TradeServiceId(jsonParams.get("tradeServiceId").toString())

                if (doesTradeServiceRecordExist == Boolean.TRUE) {
                    List accountingEntryActualList = []

                    if ((jsonParams.get("type").equals("TRANSACTION"))) {
                        println "viewing transaction accounting entries..."
                        accountingEntryActualList = accountingEntryActualRepository.getTransactionEntries(new TradeServiceId(jsonParams.get("tradeServiceId").toString()));
                    } else if ((jsonParams.get("type").equals("PAYMENT"))) {
                        println "viewing payment accounting entries..."
                        accountingEntryActualList = accountingEntryActualRepository.getPaymentEntries(new TradeServiceId(jsonParams.get("tradeServiceId").toString()));

                    } else {
                        println "viewing all accounting entries..."
                        accountingEntryActualList = accountingEntryActualRepository.getEntries(new TradeServiceId(jsonParams.get("tradeServiceId").toString()));
                    }

                    def dateOfImplem = new Date()
                    // Added date condition to display UCPB accounting entries with transactions before 07/15/2023
                    dateOfImplem.set(year: 2023, month: Calendar.JULY, dayOfMonth: 14)
                    def accountingEntries
                    if (dateOfImplem.compareTo(tradeServiceRecord.createdDate) <= 0) {
                        def glMapping = accountingEntryActualRepository.getGlMapping(jsonParams.get("tradeServiceId").toString())
                        def mapping
                        accountingEntries = accountingEntryActualList.collect({ actual ->
                            mapping = glMapping.find { it.accountingCode.equalsIgnoreCase(actual.accountingCode) &&
                                 it.bookCode.equalsIgnoreCase(actual.bookCode) && it.bookCurrency.equalsIgnoreCase(actual.bookCurrency) }
                            if (mapping != null) {
                                actual.accountingCode = mapping.lbpAccountingCode
                                actual.particulars = mapping.lbpParticulars
                            } else {
                                actual.particulars = "NOT MAPPED: $actual.particulars"
                            }
                            actual
                        })
                    } else {
                        accountingEntries = accountingEntryActualList
                    }
                    

                    returnMap.put("entries", accountingEntries);
                } else {
                    println "Trade Service Not Found!!!!!!!"
                }
            } else {
                println "Parameter tradeServiceId not found!!!"
            }

            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

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
    @Path("/checkAccountingEntryAgainstGlMast")
    public Response checkAccountingEntryAgainstGlMast(@Context UriInfo allUri) {

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

//            Map tradeServiceRecord;

            // in the repository calls below, we use getTradeServiceBy instead of load because we need to eager load
            // the entire object graph for the matching record

//            accountingService.getInvalidAccountingCode();
            accountingService.getGlNotFound();
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

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
    @Path("/getRemittanceFlag")
    public Response getRemittanceFlag(@Context UriInfo allUri) {
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        String remittanceFlag = "N"
        try {

            if (jsonParams.get("tradeServiceId") != null) {
                TradeServiceId tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
                remittanceFlag = tradeServiceService.getRemittanceFlag(tradeServiceId)
            } else if (jsonParams.get("tradeserviceid") != null) {
                TradeServiceId tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
                remittanceFlag = tradeServiceService.getRemittanceFlag(tradeServiceId)
            }

//            Map tradeServiceRecord;

            // in the repository calls below, we use getTradeServiceBy instead of load because we need to eager load
            // the entire object graph for the matching record

            returnMap.put("remittanceFlag", remittanceFlag);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

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
    @Path("/getCwtFlag")
    public Response getCwtFlag(@Context UriInfo allUri) {
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        String cwtFlag = "N"
        try {

            if (jsonParams.get("tradeServiceId") != null) {
                TradeServiceId tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
                cwtFlag = tradeServiceService.getCwtFlag(tradeServiceId)
            } else if (jsonParams.get("tradeserviceid") != null) {
                TradeServiceId tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
                cwtFlag = tradeServiceService.getCwtFlag(tradeServiceId)
            }

//            Map tradeServiceRecord;

            // in the repository calls below, we use getTradeServiceBy instead of load because we need to eager load
            // the entire object graph for the matching record

            returnMap.put("cwtFlag", cwtFlag);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

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
    @Path("/getCableFeeFlag")
    public Response getCableFeeFlag(@Context UriInfo allUri) {

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        String cableFeeFlag = "N"
        try {

            if (jsonParams.get("tradeServiceId") != null) {
                TradeServiceId tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
                cableFeeFlag = tradeServiceService.getCableFeeFlag(tradeServiceId)
            } else if (jsonParams.get("tradeserviceid") != null) {
                TradeServiceId tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
                cableFeeFlag = tradeServiceService.getCableFeeFlag(tradeServiceId)
            }

//            Map tradeServiceRecord;

            // in the repository calls below, we use getTradeServiceBy instead of load because we need to eager load
            // the entire object graph for the matching record


            returnMap.put("cableFeeFlag", cableFeeFlag);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

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
    @Path("/getChargesOverridenFlag")
    public Response getChargesOverridenFlag(@Context UriInfo allUri) {

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        String chargesOverridenFlag = "N"
        try {

            if (jsonParams.get("tradeServiceId") != null) {
                TradeServiceId tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
                chargesOverridenFlag = tradeServiceService.getChargesOverridenFlag(tradeServiceId)
            } else if (jsonParams.get("tradeserviceid") != null) {
                TradeServiceId tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
                chargesOverridenFlag = tradeServiceService.getChargesOverridenFlag(tradeServiceId)
            }

//            Map tradeServiceRecord;

            // in the repository calls below, we use getTradeServiceBy instead of load because we need to eager load
            // the entire object graph for the matching record

            returnMap.put("chargesOverridenFlag", chargesOverridenFlag);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

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
    @Path("/getTrLoanAmount")
    public Response getTrLoanAmount(@Context UriInfo allUri) {

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        HashMap<String, Object> res = new HashMap<String, BigDecimal>()
        try {

            if (jsonParams.get("tradeServiceId") != null) {
                TradeServiceId tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
                res = tradeServiceService.getTrLoanAmount(tradeServiceId)
            }

//            Map tradeServiceRecord;

            // in the repository calls below, we use getTradeServiceBy instead of load because we need to eager load
            // the entire object graph for the matching record

            returnMap.put("trloanamount", res.get("trloanamount"));
            returnMap.put("currency", res.get("currency"));
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

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
    @Path("/getServiceChargeAmount")
    public Response getServiceChargeAmount(@Context UriInfo allUri) {

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        println "jsonParams:" + jsonParams

        String serviceChargeAmount = "0"
        try {

            if (jsonParams.get("tradeServiceId") != null) {
                TradeServiceId tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
                serviceChargeAmount = chargesService.computeServiceCharge(jsonParams)
            }

            // in the repository calls below, we use getTradeServiceBy instead of load because we need to eager load
            // the entire object graph for the matching record

            returnMap.put("serviceChargeAmount", serviceChargeAmount);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

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
    @Path("/getServiceChargeAmountTotal")
    public Response getServiceChargeAmountTotal(@Context UriInfo allUri) {

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        String trloanamount = "0"
        try {

            if (jsonParams.get("tradeServiceId") != null) {
                TradeServiceId tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
                trloanamount = tradeServiceService.getTrLoanAmount(tradeServiceId)
            }

//            Map tradeServiceRecord;

            // in the repository calls below, we use getTradeServiceBy instead of load because we need to eager load
            // the entire object graph for the matching record

            returnMap.put("trloanamount", trloanamount);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

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
    @Path("/getServiceCharge")
    public Response getServiceCharge(@Context UriInfo allUri) {
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        String cableFeeFlag = "N"
        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
                cableFeeFlag = tradeServiceService.getCableFeeFlag(tradeServiceId)
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
                cableFeeFlag = tradeServiceService.getCableFeeFlag(tradeServiceId)
            }

            String chargeId = "BC"
            if (jsonParams.get("chargeId") != null) {
                chargeId = jsonParams.get("chargeId")
            } else if (jsonParams.get("chargeid") != null) {
                chargeId = jsonParams.get("chargeid")
            }
            println "chargeId:" + chargeId

            BigDecimal amount = tradeServiceService.getOriginalServiceChargeAmount(tradeServiceId, chargeId)
            String currency = tradeServiceService.getOriginalServiceChargeCurrency(tradeServiceId, chargeId).toString()
//            Map tradeServiceRecord;

            // in the repository calls below, we use getTradeServiceBy instead of load because we need to eager load
            // the entire object graph for the matching record

            returnMap.put("amount", amount.toPlainString());
            returnMap.put("currency", currency);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

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
    @Path("/getNoCwtServiceCharge")
    public Response getNoCwtServiceCharge(@Context UriInfo allUri) {
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        String cableFeeFlag = "N"
        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
                cableFeeFlag = tradeServiceService.getCableFeeFlag(tradeServiceId)
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
                cableFeeFlag = tradeServiceService.getCableFeeFlag(tradeServiceId)
            }

            String chargeId = "BC"
            if (jsonParams.get("chargeId") != null) {
                chargeId = jsonParams.get("chargeId")
            } else if (jsonParams.get("chargeid") != null) {
                chargeId = jsonParams.get("chargeid")
            }
            println "chargeId:" + chargeId

            BigDecimal amount = tradeServiceService.getServiceChargeNoCwt(tradeServiceId, chargeId)
            String currency = "PHP"
//            Map tradeServiceRecord;

            // in the repository calls below, we use getTradeServiceBy instead of load because we need to eager load
            // the entire object graph for the matching record

            returnMap.put("amount", amount.toPlainString());
            returnMap.put("currency", currency);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

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
    @Path("/getDefaultServiceCharge")
    public Response getDefaultServiceCharge(@Context UriInfo allUri) {
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        String cableFeeFlag = "N"
        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
                cableFeeFlag = tradeServiceService.getCableFeeFlag(tradeServiceId)
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
                cableFeeFlag = tradeServiceService.getCableFeeFlag(tradeServiceId)
            }

            String chargeId = "BC"
            if (jsonParams.get("chargeId") != null) {
                chargeId = jsonParams.get("chargeId")
            } else if (jsonParams.get("chargeid") != null) {
                chargeId = jsonParams.get("chargeid")
            }
            println "chargeId:" + chargeId

            BigDecimal amount = tradeServiceService.getServiceChargeDefault(tradeServiceId, chargeId)
            String currency = "PHP"
//            Map tradeServiceRecord;

            // in the repository calls below, we use getTradeServiceBy instead of load because we need to eager load
            // the entire object graph for the matching record

            returnMap.put("amount", amount.toPlainString());
            returnMap.put("currency", currency);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

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
    @Path("/getConversionToBeUsed")
    public Response getConversionToBeUsed(@Context UriInfo allUri) {

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        String style = "sell-sell" //option sell-urr
        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
                style = tradeServiceService.getConversionToBeUsed(tradeServiceId)
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
                style = tradeServiceService.getConversionToBeUsed(tradeServiceId)
            }

//            Map tradeServiceRecord;

            // in the repository calls below, we use getTradeServiceBy instead of load because we need to eager load
            // the entire object graph for the matching record

            returnMap.put("style", style);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

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
    @Path("/getCilexAmount")
    public Response getCilexAmount(@Context UriInfo allUri) {

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        String cilexAmount = "0" //option sell-urr
        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
                cilexAmount = tradeServiceService.getCilexAmount(tradeServiceId)
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
                cilexAmount = tradeServiceService.getCilexAmount(tradeServiceId)
            }

            returnMap.put("cilexAmount", cilexAmount);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

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
    @Path("/getOtherSettlementAmount")
    public Response getTotalPreUsdWithCurrencyUrrNotTr(@Context UriInfo allUri) {

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        String cilexAmount = "0" //option sell-urr
        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
                cilexAmount = tradeServiceService.getOtherSettlementAmount(tradeServiceId)
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
                cilexAmount = tradeServiceService.getOtherSettlementAmount(tradeServiceId)
            }

            returnMap.put("otherAmount", cilexAmount);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

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
    @Path("/getAdvanceCorresChargesFlag")
    public Response getAdvanceCorresChargesFlag(@Context UriInfo allUri) {

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        String advanceCorresChargesFlag = "0" //option sell-urr
        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
                advanceCorresChargesFlag = tradeServiceService.getTradeServiceProperty(tradeServiceId, "advanceCorresChargesFlag")
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
                advanceCorresChargesFlag = tradeServiceService.getTradeServiceProperty(tradeServiceId, "advanceCorresChargesFlag")
            }
            println "advanceCorresChargesFlag::::::::::" + advanceCorresChargesFlag
            returnMap.put("advanceCorresChargesFlag", advanceCorresChargesFlag);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

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
    @Path("/getConfirmationInstructionsFlag")
    public Response getConfirmationInstructionsFlag(@Context UriInfo allUri) {

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        String confirmationInstructionsFlag = "0" //option sell-urr
        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
                confirmationInstructionsFlag = tradeServiceService.getTradeServiceProperty(tradeServiceId, "confirmationInstructionsFlag")
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
                confirmationInstructionsFlag = tradeServiceService.getTradeServiceProperty(tradeServiceId, "confirmationInstructionsFlag")
            }

            println "confirmationInstructionsFlag::::::::::" + confirmationInstructionsFlag
            returnMap.put("confirmationInstructionsFlag", confirmationInstructionsFlag);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

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
    @Path("/getActiveServiceInstruction")
    public Response getActiveServiceInstruction(@Context UriInfo allUri) {

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            TradeProductNumber tradeProductNumber = new TradeProductNumber((String) jsonParams.get("tradeProductNumber"))
			List activeTradeServiceList = tradeServiceRepository.getAllActiveTradeService(tradeProductNumber)
            List serviceInstructionsList = tradeServiceRepository.getAllActiveServiceInstructionIdsByTradeProductNumber(tradeProductNumber)

            List activeServiceInstructionList = serviceInstructionRepository.findActiveServiceInstructions(serviceInstructionsList)

			returnMap.put("results", activeTradeServiceList)
            returnMap.put("details", activeServiceInstructionList)
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

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
    @Path("/getActiveTradeService")
    public Response getActiveTradeService(@Context UriInfo allUri) {

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {
            TradeServiceId tradeServiceId = new TradeServiceId((String) jsonParams.get("tradeServiceId"))
            TradeProductNumber tradeProductNumber = new TradeProductNumber((String) jsonParams.get("tradeProductNumber"))
            ServiceType serviceType = jsonParams.get("serviceType") ? ServiceType.valueOf((String) jsonParams.get("serviceType")) : null
            List activeTradeServiceList = tradeServiceRepository.getAllActiveTradeService(tradeServiceId,
                    tradeProductNumber,
                    serviceType,
                    jsonParams.get("isNotPrepared") ? Boolean.TRUE : Boolean.FALSE)

            returnMap.put("details", activeTradeServiceList)
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

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
    @Path("/getUpdateAccountingEntryAccountingCode")
    public Response getUpdateAccountingEntryAccountingCode(@Context UriInfo allUri) {

        println "getUpdateAccountingEntryCode"

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

//            TradeServiceId tradeServiceId;
//            if (jsonParams.get("tradeServiceId") != null) {
//                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
//            } else if (jsonParams.get("tradeserviceid") != null) {
//                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
//            }

            String id = jsonParams.get("id").toString()
            String code = jsonParams.get("code").toString()
            println "id:" + id
            println "code:" + code

            String reply = accountingEntryRepository.updateAccountingCode(Long.valueOf(id).longValue(), code);



            returnMap.put("result", reply);
            returnMap.put("status", "ok");

        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

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
    @Path("/getUpdateAccountingEntryFormula")
    public Response getUpdateAccountingEntryFormula(@Context UriInfo allUri) {

        println "getUpdateAccountingEntryFormula"

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

//            TradeServiceId tradeServiceId;
//            if (jsonParams.get("tradeServiceId") != null) {
//                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
//            } else if (jsonParams.get("tradeserviceid") != null) {
//                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
//            }

            String id = jsonParams.get("id").toString()
            String formula = jsonParams.get("formula").toString()
            println "id:" + id
            println "formula:" + formula

            String reply = accountingEntryRepository.updateFormulaValue(Long.valueOf(id).longValue(), formula);



            returnMap.put("result", reply);
            returnMap.put("status", "ok");

        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

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
    @Path("/getUpdateAccountingEntryFormulaPeso")
    public Response getUpdateAccountingEntryFormulaPeso(@Context UriInfo allUri) {

        println "getUpdateAccountingEntryFormulaPeso"

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

//            TradeServiceId tradeServiceId;
//            if (jsonParams.get("tradeServiceId") != null) {
//                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
//            } else if (jsonParams.get("tradeserviceid") != null) {
//                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
//            }

            String id = jsonParams.get("id").toString()
            String code = jsonParams.get("formula").toString()
            println "id:" + id
            println "formula:" + formula

            String reply = accountingEntryRepository.updateFormulaPesoValue(Long.valueOf(id).longValue(), formula);



            returnMap.put("result", reply);
            returnMap.put("status", "ok");

        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

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
    @Path("/getApprovedTransactions")
    public Response getApprovedTransactions(@Context UriInfo allUri) {
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();

        Map jsonParams = new HashMap();

        // get all rest parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key));

        }

        try {
            def tradeServiceIds = tradeServiceRepository.getAllApprovedTradeServiceIds(new TradeProductNumber(jsonParams.get("tradeProductNumber")))

            returnMap.put("status", "ok");
            returnMap.put("response", tradeServiceIds);

        }
        catch (Exception e) {

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
    @Path("/getApprovedTradeServiceIdsForImportCharges")
    public Response getAllApprovedTradeServiceIdsForImportCharges(@Context UriInfo allUri) {
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();

        Map jsonParams = new HashMap();

        // get all rest parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key));

        }

        try {
            def tradeServiceIds = tradeServiceRepository.getAllApprovedTradeServiceIdsForImportCharges(new TradeProductNumber(jsonParams.get("tradeProductNumber")))

            returnMap.put("status", "ok");
            returnMap.put("response", tradeServiceIds);

        }
        catch (Exception e) {

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
    @Path("/getApprovedTradeServiceIdsForLcRefund")
    public Response getAllApprovedTradeServiceIdsForLcRefund(@Context UriInfo allUri) {
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();

        Map jsonParams = new HashMap();

        // get all rest parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key));

        }

        try {
            def tradeServiceIds = tradeServiceRepository.getAllApprovedTradeServiceIdsForLcRefund(new TradeProductNumber(jsonParams.get("tradeProductNumber")))

            returnMap.put("status", "ok");
            returnMap.put("response", tradeServiceIds);

        }
        catch (Exception e) {

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
    @Path("/getApprovedTradeServiceIdsForExportCharges")
    public Response getAllApprovedTradeServiceIdsForExportCharges(@Context UriInfo allUri) {
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();

        Map jsonParams = new HashMap();

        // get all rest parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key));

        }

        try {
            def tradeServiceIds = tradeServiceRepository.getAllApprovedTradeServiceIdsForExportCharges((String) jsonParams.get("tradeProductNumber"))

            returnMap.put("status", "ok");
            returnMap.put("response", tradeServiceIds);

        }
        catch (Exception e) {

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
    @Path("/getUpdateSql")
    public Response getUpdateSql(@Context UriInfo allUri) {
        println "updateSql"

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

//            TradeServiceId tradeServiceId;
//            if (jsonParams.get("tradeServiceId") != null) {
//                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
//            } else if (jsonParams.get("tradeserviceid") != null) {
//                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
//            }

            String line = jsonParams.get("sql").toString()
            println "line:" + line

            String reply = accountingEntryRepository.updateAccounting(line);



            returnMap.put("result", reply);
            returnMap.put("status", "ok");

        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

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
    @Path("/getSettlementCurrency")
    public Response getChargesSettlementCurrency(@Context UriInfo allUri) {

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        String settlementCurrency = "" //option sell-urr
        try {

            TradeServiceId tradeServiceId;
            if (jsonParams.get("tradeServiceId") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
                settlementCurrency = tradeServiceService.getTradeServiceProperty(tradeServiceId, "settlementCurrency")
            } else if (jsonParams.get("tradeserviceid") != null) {
                tradeServiceId = new TradeServiceId(jsonParams.get("tradeserviceid").toString())
                settlementCurrency = tradeServiceService.getTradeServiceProperty(tradeServiceId, "settlementCurrency")
            }

            println "settlementCurrency::::::::::" + settlementCurrency
            returnMap.put("settlementCurrency", settlementCurrency);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

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
	@Path("/updateTradeServicePost")
	public Response updateTradeService(@Context UriInfo allUri) {
		println "in updateTradeServicePost"
		Gson gson = new Gson();

		String result = "";
		Map returnMap = new HashMap();
		Map jsonParams = new HashMap<String, String>();

		// get all query parameters
		MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

		for (String key : mpAllQueParams.keySet()) {
			// if there are multiple instances of the same param, we only use the first one
			jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
		}
		
		try {
			TradeServiceId tradeServiceId;
			if (jsonParams.get("tradeServiceId") != null) {
				tradeServiceId = new TradeServiceId(jsonParams.get("tradeServiceId").toString())
				TradeService tradeService = tradeServiceRepository.load(tradeServiceId)
				
				Map<String, Object> details = tradeService.getDetails()
				details.putAll(jsonParams)
				tradeService.updateDetails(details)
				tradeServiceRepository.merge(tradeService)
			}
			returnMap.put("status", "ok");
		} catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

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
	@Path("/loadTradeproduct")
	public Response loadTradeproduct(@Context UriInfo allUri) {
		println "here@loadTradeproduct"
		
		Gson gson = new Gson();

		String result = "";
		Map returnMap = new HashMap();
		Map jsonParams = new HashMap<String, String>();

		// get all query parameters
		MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

		for (String key : mpAllQueParams.keySet()) {
			// if there are multiple instances of the same param, we only use the first one
			jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
		}
		
		try {
			DocumentNumber documentNumber = new DocumentNumber(jsonParams.get("documentNumber").toString())
			TradeProduct tradeProduct = tradeProductRepository.load(documentNumber);
			
			returnMap.put("cifNumber", tradeProduct.getCifNumber());
			returnMap.put("facilityRefNo", tradeProduct.getFacilityReferenceNumber());
			returnMap.put("facilityType", tradeProduct.getFacilityType());
			returnMap.put("facilityId", tradeProduct.getFacilityId());
		
			returnMap.put("status", "ok");
		} catch (Exception e) {
	
				Map errorDetails = new HashMap();
	
				e.printStackTrace();
	
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
}