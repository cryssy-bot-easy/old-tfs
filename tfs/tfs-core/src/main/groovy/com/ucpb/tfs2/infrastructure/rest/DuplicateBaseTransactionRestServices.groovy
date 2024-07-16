package com.ucpb.tfs2.infrastructure.rest
import com.google.gson.Gson
import com.incuventure.ddd.domain.DomainEventPublisher
import com.ucpb.tfs.application.service.AccountingService
import com.ucpb.tfs.application.service.TradeProductService
import com.ucpb.tfs.domain.condition.AdditionalCondition
import com.ucpb.tfs.domain.condition.ConditionCode
import com.ucpb.tfs.domain.condition.LcAdditionalCondition
import com.ucpb.tfs.domain.condition.LcAdditionalConditionRepository
import com.ucpb.tfs.domain.condition.enumTypes.ConditionType
import com.ucpb.tfs.domain.documents.DocumentCode
import com.ucpb.tfs.domain.documents.LcRequiredDocument
import com.ucpb.tfs.domain.documents.LcRequiredDocumentRepository
import com.ucpb.tfs.domain.documents.RequiredDocument
import com.ucpb.tfs.domain.documents.enumTypes.RequiredDocumentType
import com.ucpb.tfs.domain.instruction.ServiceInstruction
import com.ucpb.tfs.domain.instruction.ServiceInstructionId
import com.ucpb.tfs.domain.instruction.ServiceInstructionRepository
import com.ucpb.tfs.domain.instruction.enumTypes.ServiceInstructionStatus
import com.ucpb.tfs.domain.letter.TransmittalLetter
import com.ucpb.tfs.domain.payment.Payment
import com.ucpb.tfs.domain.payment.PaymentDetail
import com.ucpb.tfs.domain.payment.PaymentRepository
import com.ucpb.tfs.domain.product.DocumentNumber
import com.ucpb.tfs.domain.product.LetterOfCredit
import com.ucpb.tfs.domain.product.TradeProductRepository
import com.ucpb.tfs.domain.product.enums.LCType
import com.ucpb.tfs.domain.product.enums.TradeProductStatus
import com.ucpb.tfs.domain.product.event.LetterOfCreditCreatedEvent
import com.ucpb.tfs.domain.reference.GltsSequenceRepository
import com.ucpb.tfs.domain.reimbursing.InstructionToBank
import com.ucpb.tfs.domain.reimbursing.InstructionToBankCode
import com.ucpb.tfs.domain.reimbursing.LcInstructionToBank
import com.ucpb.tfs.domain.reimbursing.LcInstructionToBankRepository
import com.ucpb.tfs.domain.routing.*
import com.ucpb.tfs.domain.service.*
import com.ucpb.tfs.domain.service.enumTypes.DocumentClass
import com.ucpb.tfs.domain.service.enumTypes.DocumentSubType1
import com.ucpb.tfs.domain.service.enumTypes.DocumentSubType2
import com.ucpb.tfs.domain.service.enumTypes.DocumentType
import com.ucpb.tfs.domain.service.enumTypes.ServiceType
import com.ucpb.tfs.domain.service.enumTypes.TradeServiceStatus
import com.ucpb.tfs.domain.swift.SwiftCharge
import com.ucpb.tfs.domain.task.Task
import com.ucpb.tfs.domain.task.TaskReferenceNumber
import com.ucpb.tfs.domain.task.TaskRepository
import com.ucpb.tfs.interfaces.domain.Availment
import com.ucpb.tfs.interfaces.domain.enums.EarmarkingStatusDescription
import com.ucpb.tfs.interfaces.services.impl.FacilityServiceImpl
import org.apache.commons.lang.SerializationUtils
import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.lang3.text.WordUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.*
/**
 * Created with IntelliJ IDEA.
 * User: IPCVal
 * Date: 10/7/13
 * Time: 3:14 PM
 * To change this template use File | Settings | File Templates.
 */
@Path("/duplicateBase")
@Component
public class DuplicateBaseTransactionRestServices {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    RouteRepository routeRepository;

    @Autowired
    RoutingInformationRepository routingInformationRepository;

    @Autowired
    ServiceInstructionRepository serviceInstructionRepository;

    @Autowired
    TradeServiceRepository tradeServiceRepository;

    @Autowired
    private FacilityServiceImpl facilityService;

    @Autowired
    private TradeProductRepository tradeProductRepository;

    @Autowired
    private LcRequiredDocumentRepository lcRequiredDocumentRepository;

    @Autowired
    private LcInstructionToBankRepository lcInstructionToBankRepository;

    @Autowired
    private LcAdditionalConditionRepository lcAdditionalConditionRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AccountingService accountingService;

    @Autowired
    private GltsSequenceRepository gltsSequenceRepository;

    @Autowired
    DomainEventPublisher eventPublisher;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/tsdMaker")
    public Response duplicateTsdMaker(@Context UriInfo allUri) {

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

            String passedEts = ((String)jsonParams.get("ets")).trim();
            String passedAmount = ((String)jsonParams.get("amount")).trim();
            String passedCurrency = ((String)jsonParams.get("currency")).trim();

            BigDecimal dupAmount = new BigDecimal(passedAmount);
            Currency dupCurrency = Currency.getInstance(passedCurrency.toUpperCase());

            ServiceInstruction serviceInstruction = serviceInstructionRepository.load(new ServiceInstructionId(passedEts));

            if (serviceInstruction != null) {

                TradeService tradeService = tradeServiceRepository.load(new ServiceInstructionId(passedEts));

                DocumentClass documentClass = tradeService.getDocumentClass();
                ServiceInstructionStatus serviceInstructionStatus = serviceInstruction.getStatus();
                DocumentSubType1 documentSubType1 = tradeService.getDocumentSubType1();
                ServiceType serviceType = tradeService.getServiceType();

                // Can only duplicate approved LC Opening ETS with pending data entry
                if (documentClass.equals(DocumentClass.LC) &&
                    serviceType.equals(ServiceType.OPENING) &&
                    serviceInstructionStatus.equals(ServiceInstructionStatus.APPROVED) &&
                    TradeServiceStatus.PENDING.equals(tradeService.getStatus())) {

                    Task etsTask = taskRepository.load(new TaskReferenceNumber(passedEts));
                    Task tradeServiceTask = taskRepository.load(new TaskReferenceNumber(tradeService.getTradeServiceId().toString()));

                    RoutingInformation routingInformation = routingInformationRepository.getRoutingInformation(new RoutingInformationId(passedEts));

                    // 1) Duplicate ETS
                    ServiceInstruction dupServiceInstruction = SerializationUtils.clone(serviceInstruction);
                    String randomServiceInstructionId = "DUP-" + RandomStringUtils.random(8, true, true).toUpperCase();
                    dupServiceInstruction.setServiceInstructionId(new ServiceInstructionId(randomServiceInstructionId));  // Modify ETS number

                    // 2) Duplicate TradeService
                    TradeService dupTradeService = new TradeService();  // Generates a new TradeService ID
                    TradeServiceId dupTradeServiceId = dupTradeService.getTradeServiceId();
                    tradeServiceRepository.persist(dupTradeService);

                    dupTradeService = SerializationUtils.clone(tradeService);
                    dupTradeService.setTradeServiceId(dupTradeServiceId);
                    dupTradeService.setServiceInstructionId(dupServiceInstruction.getServiceInstructionId());

                    String randomDocumentNumber = "DUP-" + RandomStringUtils.random(13, true, true).toUpperCase();
                    DocumentNumber dupDocumentNumber = new DocumentNumber(randomDocumentNumber);  // Modify Document Number
                    TradeProductNumber dupTradeProductNumber = new TradeProductNumber(randomDocumentNumber);  // For LC Opening, Document Number = Trade Product Number
                    dupTradeService.setDocumentNumber(dupDocumentNumber);
                    dupTradeService.setTradeProductNumber(dupTradeProductNumber);

                    // 2.1) Duplicate charges of TradeService
                    Set<ServiceCharge> serviceChargeSet = tradeService.getServiceCharge();
                    Set<ServiceCharge> newServiceCharges = new HashSet<ServiceCharge>();
                    for (ServiceCharge serviceCharge : serviceChargeSet) {
                        ServiceCharge charge = SerializationUtils.clone(serviceCharge);
                        charge.setId(null);
                        newServiceCharges.add(charge);
                    }
                    dupTradeService.updateServiceCharges(newServiceCharges);

                    // 4) Duplicate ETS and TradeService (data entry) Tasks
                    Task dupEtsTask = SerializationUtils.clone(etsTask);
                    dupEtsTask.setTaskReferenceNumber(new TaskReferenceNumber(dupServiceInstruction.getServiceInstructionId().toString()))

                    Task dupTradeServiceTask = SerializationUtils.clone(tradeServiceTask);
                    dupTradeServiceTask.setTaskReferenceNumber(new TaskReferenceNumber(dupTradeService.getTradeServiceId().toString()))

                    // 5) Duplicate ETS and TradeService details
                    Map<String, Object> dupEtsDetails = dupServiceInstruction.getDetails();
                    dupEtsDetails.put("etsNumber", dupServiceInstruction.getServiceInstructionId().toString());
                    dupEtsDetails.put("tradeServiceId", dupTradeService.getTradeServiceId().toString());
                    dupEtsDetails.put("documentNumber", dupTradeService.getDocumentNumber().toString());
                    if (dupEtsDetails.get("tradeProductNumber") != null) {
                        dupEtsDetails.put("tradeProductNumber", dupTradeService.getTradeProductNumber().toString());
                    }
                    dupEtsDetails.put("amount", passedAmount);
                    dupEtsDetails.put("currency", passedCurrency.toUpperCase());
                    dupEtsDetails.put("savedCurrency", passedCurrency.toUpperCase());
                    // dupEtsDetails.put("settlementCurrency", "");
                    dupServiceInstruction.setDetails(dupEtsDetails);

                    Map<String, Object> dupTradeServiceDetails = dupTradeService.getDetails();
                    dupTradeServiceDetails.put("etsNumber", dupServiceInstruction.getServiceInstructionId().toString());
                    dupTradeServiceDetails.put("tradeServiceId", dupTradeService.getTradeServiceId().toString());
                    dupTradeServiceDetails.put("documentNumber", dupTradeService.getDocumentNumber().toString());
                    if (dupTradeServiceDetails.get("tradeProductNumber") != null) {
                        dupTradeServiceDetails.put("tradeProductNumber", dupTradeService.getTradeProductNumber().toString());
                    }
                    dupTradeServiceDetails.put("amount", passedAmount);
                    dupTradeServiceDetails.put("currency", passedCurrency.toUpperCase());
                    dupTradeServiceDetails.put("savedCurrency", passedCurrency.toUpperCase());
                    if (dupTradeServiceDetails.containsKey("chargesOverridenFlag")) {
                        dupTradeServiceDetails.remove("chargesOverridenFlag");
                    }
                    // dupTradeServiceDetails.put("settlementCurrency", "");
                    dupTradeService.setDetails(dupTradeServiceDetails);
                    dupTradeService.setProductCharge(dupAmount, dupCurrency);

                    println "\n----- ORIGINAL -----"
                    println "******************* original ETS number = ${serviceInstruction.getServiceInstructionId().toString()}"
                    println "******************* original TradeService ID = ${tradeService.getTradeServiceId().toString()}"
                    println "******************* original Document Number = ${tradeService.getDocumentNumber().toString()}"
                    println "******************* original TradeProduct Number = ${tradeService.getTradeProductNumber().toString()}"
                    println "******************* original amount = ${tradeService.getProductChargeAmount().toPlainString()}"
                    println "******************* original currency = ${tradeService.getProductChargeCurrency().toString()}"
                    println "----- DUPLICATE -----"
                    println "******************* duplicate ETS number = ${dupServiceInstruction.getServiceInstructionId().toString()}"
                    println "******************* duplicate TradeService ID = ${dupTradeService.getTradeServiceId().toString()}"
                    println "******************* duplicate Document Number = ${dupTradeService.getDocumentNumber().toString()}"
                    println "******************* duplicate TradeProduct Number = ${dupTradeService.getTradeProductNumber().toString()}"
                    println "******************* duplicate amount = ${tradeService.getProductChargeAmount().toPlainString()}"
                    println "******************* duplicate currency = ${dupTradeService.getProductChargeCurrency().toString()}\n"

                    // 6) Save
                    serviceInstructionRepository.merge(dupServiceInstruction);
                    tradeServiceRepository.merge(dupTradeService);
                    taskRepository.persist(dupEtsTask);
                    taskRepository.persist(dupTradeServiceTask);

                    // 7) Duplicate and save RoutingInformation
                    RoutingInformation dupRoutingInformation = SerializationUtils.clone(routingInformation);
                    dupRoutingInformation.setRoutingInformationId(new RoutingInformationId(dupServiceInstruction.getServiceInstructionId().toString()));

                    List<Route> routes = routeRepository.getAllRoutes(routingInformation.getRoutingInformationId());
                    println "\n******************* dupRoutingInformation.getRoutingInformationId() = ${dupRoutingInformation.getRoutingInformationId().toString()}"
                    println "******************* routes.size() = ${routes.size()}\n"
                    for (Route route : routes) {
                        Route dupRoute = SerializationUtils.clone(route);
                        dupRoute.setId(null);
                        routingInformationRepository.addRoutingInformation(dupRoute, dupRoutingInformation.getRoutingInformationId());
                    }

                    // 8) If Regular and Standby, duplicate earmark to SIBS
                    if (documentSubType1.equals(DocumentSubType1.REGULAR) || documentSubType1.equals(DocumentSubType1.STANDBY)) {

                        Availment dupAvailment = new Availment();
                        dupAvailment.setDocumentNumber(dupTradeService.getTradeProductNumber().toString());
                        dupAvailment.setCifNumber(dupTradeService.getCifNumber());
                        dupAvailment.setFacilityReferenceNumber((String)dupTradeServiceDetails.get("facilityReferenceNumber"));
                        dupAvailment.setOriginalAmount(dupAmount);
                        dupAvailment.setOutstandingBalance(dupAvailment.getOriginalAmount());
                        dupAvailment.setCurrencyCode(dupCurrency.getCurrencyCode());
                        dupAvailment.setStatusDescription(EarmarkingStatusDescription.CURRENT.toString());

                        facilityService.earmarkAvailment(dupAvailment);
                    }

                    List results = [
                            ["original ETS Number"       : serviceInstruction.getServiceInstructionId().toString()],
                            ["duplicate ETS Number"      : dupServiceInstruction.getServiceInstructionId().toString()],
                            ["original Document Number"  : tradeService.getTradeProductNumber().toString()],
                            ["duplicate Document Number" : dupTradeService.getTradeProductNumber().toString()],
                            ["original amount"           : tradeService.getProductChargeAmount().toPlainString()],
                            ["duplicate amount"          : dupTradeService.getProductChargeAmount().toPlainString()],
                            ["original currency"         : tradeService.getProductChargeCurrency().toString()],
                            ["duplicate currency"        : dupTradeService.getProductChargeCurrency().toString()]
                    ];

                    returnMap.put("status", "ok");
                    returnMap.put("details", results)

                } else {

                    returnMap.put("status", "error");
                    returnMap.put("details", "Can only duplicate approved LC Opening ETS with pending data entry.")
                }

            } else {

                returnMap.put("status", "error");
                returnMap.put("details", "ETS Number " + passedEts + " not found.")
            }

        } catch(Exception e) {

            e.printStackTrace();

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
    @Path("/lcOpening")
    public Response duplicateLcOpening(@Context UriInfo allUri) {

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

            String passedDocumentNumber = ((String)jsonParams.get("documentNumber")).trim();

            TradeService tradeService = tradeServiceRepository.load(new DocumentNumber(passedDocumentNumber), ServiceType.OPENING);

            if (tradeService != null) {

                LetterOfCredit lc = tradeProductRepository.load(new DocumentNumber(tradeService.getTradeProductNumber().toString()));

                ServiceInstruction serviceInstruction = serviceInstructionRepository.load(tradeService.getServiceInstructionId());

                Task etsTask = taskRepository.load(new TaskReferenceNumber(serviceInstruction.getServiceInstructionId().toString()));
                Task tradeServiceTask = taskRepository.load(new TaskReferenceNumber(tradeService.getTradeServiceId().toString()));

                RoutingInformation etsRoutingInformation = routingInformationRepository.getRoutingInformation(new RoutingInformationId(serviceInstruction.getServiceInstructionId().toString()));
                RoutingInformation dataEntryRoutingInformation = routingInformationRepository.getRoutingInformation(new RoutingInformationId(tradeService.getTradeServiceId().toString()));

                // 1) Duplicate ETS
                ServiceInstruction dupServiceInstruction = SerializationUtils.clone(serviceInstruction);
                String randomServiceInstructionId = "DUP-" + RandomStringUtils.random(8, true, true).toUpperCase();
                dupServiceInstruction.setServiceInstructionId(new ServiceInstructionId(randomServiceInstructionId));  // Modify ETS number

                // 2) Duplicate TradeService
                TradeService dupTradeService = new TradeService();  // Generates a new TradeService ID
                TradeServiceId dupTradeServiceId = dupTradeService.getTradeServiceId();
                tradeServiceRepository.persist(dupTradeService);

                dupTradeService = SerializationUtils.clone(tradeService);
                dupTradeService.setTradeServiceId(dupTradeServiceId);
                dupTradeService.setServiceInstructionId(dupServiceInstruction.getServiceInstructionId());

                String randomDocumentNumber = "DUP-" + RandomStringUtils.random(13, true, true).toUpperCase();
                DocumentNumber dupDocumentNumber = new DocumentNumber(randomDocumentNumber);  // Modify Document Number
                TradeProductNumber dupTradeProductNumber = new TradeProductNumber(randomDocumentNumber);  // For LC Opening, Document Number = Trade Product Number
                dupTradeService.setDocumentNumber(dupDocumentNumber);
                dupTradeService.setTradeProductNumber(dupTradeProductNumber);

                // 2.1) Duplicate charges of TradeService
                Set<ServiceCharge> dupServiceCharges = new HashSet<ServiceCharge>();
                for (ServiceCharge serviceCharge : tradeService.getServiceCharge()) {
                    ServiceCharge dupServiceCharge = SerializationUtils.clone(serviceCharge);
                    dupServiceCharge.setId(null);
                    dupServiceCharges.add(dupServiceCharge);
                }
                dupTradeService.updateServiceCharges(dupServiceCharges);

                // 2.2) Duplicate requiredDocument
                Set<RequiredDocument> dupRequiredDocuments = new HashSet<RequiredDocument>();
                for (RequiredDocument requiredDocument : tradeService.getRequiredDocument()) {
                    RequiredDocument dupRequiredDocument = SerializationUtils.clone(requiredDocument);
                    dupRequiredDocument.setId(null);
                    dupRequiredDocuments.add(dupRequiredDocument);
                }
                dupTradeService.updateRequiredDocuments(dupRequiredDocuments);

                // 2.3) Duplicate instructionToBank
                Set<InstructionToBank> dupInstructionToBanks = new HashSet<InstructionToBank>();
                for (InstructionToBank instructionToBank : tradeService.getInstructionToBank()) {
                    InstructionToBank dupInstructionToBank = SerializationUtils.clone(instructionToBank);
                    dupInstructionToBank.setId(null);
                    dupInstructionToBanks.add(dupInstructionToBank);
                }
                dupTradeService.updateInstructionToBank(dupInstructionToBanks);

                // 2.4) Duplicate additionalCondition
                Set<AdditionalCondition> dupAdditionalConditions = new HashSet<AdditionalCondition>();
                for (AdditionalCondition additionalCondition : tradeService.getAdditionalCondition()) {
                    AdditionalCondition dupAdditionalCondition = SerializationUtils.clone(additionalCondition);
                    dupAdditionalCondition.setId(null);
                    dupAdditionalConditions.add(dupAdditionalCondition);
                }
                dupTradeService.updateAdditionalCondition(dupAdditionalConditions);

                // 2.5) Duplicate transmittalLetter
                Set<TransmittalLetter> dupTransmittalLetters = new HashSet<TransmittalLetter>();
                for (TransmittalLetter transmittalLetter : tradeService.getTransmittalLetter()) {
                    TransmittalLetter dupTransmittalLetter = SerializationUtils.clone(transmittalLetter);
                    dupTransmittalLetter.setId(null);
                    dupTransmittalLetters.add(dupTransmittalLetter);
                }
                dupTradeService.updateTransmittalLetter(dupTransmittalLetters);

                // 2.6) Duplicate swiftCharge
                Set<SwiftCharge> dupSwiftCharges = new HashSet<SwiftCharge>();
                for (SwiftCharge swiftCharge : tradeService.getSwiftCharge()) {
                    SwiftCharge dupSwiftCharge = SerializationUtils.clone(swiftCharge);
                    dupSwiftCharge.setId(null);
                    dupSwiftCharges.add(dupSwiftCharge);
                }
                dupTradeService.updateSwiftCharge(dupSwiftCharges);

                // 4) Duplicate ETS and TradeService (data entry) Tasks
                Task dupEtsTask = SerializationUtils.clone(etsTask);
                dupEtsTask.setTaskReferenceNumber(new TaskReferenceNumber(dupServiceInstruction.getServiceInstructionId().toString()))

                Task dupTradeServiceTask = SerializationUtils.clone(tradeServiceTask);
                dupTradeServiceTask.setTaskReferenceNumber(new TaskReferenceNumber(dupTradeService.getTradeServiceId().toString()))

                // 5) Duplicate ETS and TradeService details
                Map<String, Object> dupEtsDetails = dupServiceInstruction.getDetails();
                dupEtsDetails.put("etsNumber", dupServiceInstruction.getServiceInstructionId().toString());
                dupEtsDetails.put("tradeServiceId", dupTradeService.getTradeServiceId().toString());
                dupEtsDetails.put("documentNumber", dupTradeService.getDocumentNumber().toString());
                if (dupEtsDetails.get("tradeProductNumber") != null) {
                    dupEtsDetails.put("tradeProductNumber", dupTradeService.getTradeProductNumber().toString());
                }
                // dupEtsDetails.put("settlementCurrency", "");
                dupServiceInstruction.setDetails(dupEtsDetails);

                Map<String, Object> dupTradeServiceDetails = dupTradeService.getDetails();
                dupTradeServiceDetails.put("etsNumber", dupServiceInstruction.getServiceInstructionId().toString());
                dupTradeServiceDetails.put("tradeServiceId", dupTradeService.getTradeServiceId().toString());
                dupTradeServiceDetails.put("documentNumber", dupTradeService.getDocumentNumber().toString());
                if (dupTradeServiceDetails.get("tradeProductNumber") != null) {
                    dupTradeServiceDetails.put("tradeProductNumber", dupTradeService.getTradeProductNumber().toString());
                }
                // dupTradeServiceDetails.put("settlementCurrency", "");
                dupTradeService.setDetails(dupTradeServiceDetails);

                println "\n----- ORIGINAL -----"
                println "******************* original ETS number = ${serviceInstruction.getServiceInstructionId().toString()}"
                println "******************* original TradeService ID = ${tradeService.getTradeServiceId().toString()}"
                println "******************* original Document Number = ${tradeService.getDocumentNumber().toString()}"
                println "******************* original TradeProduct Number = ${tradeService.getTradeProductNumber().toString()}"
                println "******************* original amount = ${tradeService.getProductChargeAmount().toPlainString()}"
                println "******************* original currency = ${tradeService.getProductChargeCurrency().toString()}"
                println "----- DUPLICATE -----"
                println "******************* duplicate ETS number = ${dupServiceInstruction.getServiceInstructionId().toString()}"
                println "******************* duplicate TradeService ID = ${dupTradeService.getTradeServiceId().toString()}"
                println "******************* duplicate Document Number = ${dupTradeService.getDocumentNumber().toString()}"
                println "******************* duplicate TradeProduct Number = ${dupTradeService.getTradeProductNumber().toString()}"
                println "******************* duplicate amount = ${tradeService.getProductChargeAmount().toPlainString()}"
                println "******************* duplicate currency = ${dupTradeService.getProductChargeCurrency().toString()}\n"

                // 6) Save ETS, TradeService, and Task
                serviceInstructionRepository.merge(dupServiceInstruction);
                tradeServiceRepository.merge(dupTradeService);
                taskRepository.persist(dupEtsTask);
                taskRepository.persist(dupTradeServiceTask);

                // 7) Duplicate and save RoutingInformation
                // ETS routing
                RoutingInformation dupEtsRoutingInformation = SerializationUtils.clone(etsRoutingInformation);
                dupEtsRoutingInformation.setRoutingInformationId(new RoutingInformationId(dupServiceInstruction.getServiceInstructionId().toString()));
                List<Route> etsRoutes = routeRepository.getAllRoutes(etsRoutingInformation.getRoutingInformationId());
                println "\n******************* dupEtsRoutingInformation.getRoutingInformationId() = ${dupEtsRoutingInformation.getRoutingInformationId().toString()}"
                println "******************* etsRoutes.size() = ${etsRoutes.size()}\n"
                for (Route route : etsRoutes) {
                    Route dupRoute = SerializationUtils.clone(route);
                    dupRoute.setId(null);
                    routingInformationRepository.addRoutingInformation(dupRoute, dupEtsRoutingInformation.getRoutingInformationId());
                }
                // Data entry routing
                RoutingInformation dupDataEntryRoutingInformation = SerializationUtils.clone(dataEntryRoutingInformation);
                dupDataEntryRoutingInformation.setRoutingInformationId(new RoutingInformationId(dupTradeService.getTradeServiceId().toString()));
                List<Route> dataEntryRoutes = routeRepository.getAllRoutes(dataEntryRoutingInformation.getRoutingInformationId());
                println "\n******************* dupDataEntryRoutingInformation.getRoutingInformationId() = ${dupDataEntryRoutingInformation.getRoutingInformationId().toString()}"
                println "******************* dataEntryRoutes.size() = ${dataEntryRoutes.size()}\n"
                for (Route route : dataEntryRoutes) {
                    Route dupRoute = SerializationUtils.clone(route);
                    dupRoute.setId(null);
                    routingInformationRepository.addRoutingInformation(dupRoute, dupDataEntryRoutingInformation.getRoutingInformationId());
                }

                // 8) Duplicate payment
                List<Payment> payments = paymentRepository.getAllPayments(tradeService.getTradeServiceId());
                for (Payment payment : payments) {

                    // Use constructor because Hibernate throws an error if deep clone is used
                    Payment dupPayment = new Payment(dupTradeService.getTradeServiceId(), payment.getChargeType());
                    dupPayment.setStatus(payment.getStatus());
                    dupPayment.setPaidDate(payment.getPaidDate());

                    Set<PaymentDetail> dupPaymentDetails = new HashSet<PaymentDetail>();
                    for (PaymentDetail paymentDetail : payment.getDetails()) {

                        PaymentDetail dupPaymentDetail = SerializationUtils.clone(paymentDetail);
                        dupPaymentDetail.setId(null);

                        dupPaymentDetails.add(dupPaymentDetail);
                    }

                    dupPayment.addNewPaymentDetails(dupPaymentDetails);
                    paymentRepository.saveOrUpdate(dupPayment);
                }

                // 9) Duplicate LC
                LetterOfCredit dupLc = TradeProductService.createLetterOfCredit(dupTradeService.getDocumentNumber(), dupTradeService.getDetails());

                // 9.1) Duplicate required document
                List<LcRequiredDocument> dupLcRequiredDocuments = new ArrayList<LcRequiredDocument>();
                for (RequiredDocument requiredDocument : dupTradeService.getRequiredDocument()) {
                    Map<String, Object> requiredDocumentFields = requiredDocument.getFields();
                    DocumentCode documentCode = null;
                    RequiredDocumentType requiredDocumentType = RequiredDocumentType.valueOf((String) requiredDocumentFields.get("requiredDocumentType"));
                    if (requiredDocumentType.equals(RequiredDocumentType.DEFAULT)) {
                        documentCode = new DocumentCode((String) requiredDocumentFields.get("documentCode"));
                    }
                    LcRequiredDocument dupLcRequiredDocument = new LcRequiredDocument(documentCode, (String) requiredDocumentFields.get("description"), requiredDocumentType);
                    dupLcRequiredDocuments.add(dupLcRequiredDocument);
                }
                dupLc.addRequiredDocuments(dupLcRequiredDocuments);

                // 9.2) Duplicate instruction to bank
                List<LcInstructionToBank> dupLcInstructionToBanks = new ArrayList<LcInstructionToBank>();
                for (InstructionToBank instructionToBank : dupTradeService.getInstructionToBank()) {
                    Map<String, Object> instructionsToBankFields = instructionToBank.getFields();
                    InstructionToBankCode instructionToBankCode = new InstructionToBankCode((String) instructionsToBankFields.get("instructionToBankCode"));
                    LcInstructionToBank dupLcInstructionToBank = new LcInstructionToBank(instructionToBankCode, (String) instructionsToBankFields.get("instruction"));
                    dupLcInstructionToBanks.add(dupLcInstructionToBank);
                }
                dupLc.addInstructionToBank(dupLcInstructionToBanks);

                // 9.3) Duplicate additional condition
                List<LcAdditionalCondition> dupLcAdditionalConditions = new ArrayList<LcAdditionalCondition>();
                for (AdditionalCondition additionalCondition : dupTradeService.getAdditionalCondition()) {
                    Map<String, Object> additionalConditionFields = additionalCondition.getFields();
                    ConditionCode conditionCode = null;
                    ConditionType conditionType = ConditionType.valueOf((String) additionalConditionFields.get("conditionType"));
                    if (conditionType.equals(ConditionType.DEFAULT)) {
                        conditionCode = new ConditionCode((String) additionalConditionFields.get("conditionCode"));
                    }
                    LcAdditionalCondition dupLcAdditionalCondition = new LcAdditionalCondition(conditionType, conditionCode, (String) additionalConditionFields.get("condition"));
                    dupLcAdditionalConditions.add(dupLcAdditionalCondition);
                }
                dupLc.addAdditionalCondition(dupLcAdditionalConditions);

                dupLc.updateStatus(TradeProductStatus.OPEN);

                String lastTransactionOpening = buildLastLcTransactionString(
                        dupTradeService.getServiceType(),
                        dupTradeService.getDocumentClass(),
                        dupTradeService.getDocumentType(),
                        dupTradeService.getDocumentSubType1(),
                        dupTradeService.getDocumentSubType2());

                dupLc.updateLastTransaction(lastTransactionOpening);

                tradeProductRepository.persist(dupLc);

                // 10) Generate accounting entries
                String glts = gltsSequenceRepository.getGltsSequence();
                accountingService.generateActualEntries(dupTradeService, glts, dupTradeService.getStatus().toString());
                gltsSequenceRepository.incrementGltsSequence();

                // 11) If Regular and Standby, duplicate earmark to SIBS
                if (lc.getType().equals(LCType.REGULAR) || lc.getType().equals(LCType.STANDBY)) {

                    Availment dupAvailment = new Availment();
                    dupAvailment.setDocumentNumber(dupTradeService.getTradeProductNumber().toString());
                    dupAvailment.setCifNumber(dupTradeService.getCifNumber());
                    dupAvailment.setFacilityReferenceNumber((String)dupTradeServiceDetails.get("facilityReferenceNumber"));
                    dupAvailment.setOriginalAmount(dupTradeService.getProductChargeAmount());
                    dupAvailment.setOutstandingBalance(dupAvailment.getOriginalAmount());
                    dupAvailment.setCurrencyCode(dupTradeService.getProductChargeCurrency().getCurrencyCode());
                    dupAvailment.setStatusDescription(EarmarkingStatusDescription.CURRENT.toString());

                    facilityService.earmarkAvailment(dupAvailment);
                }

                // 12) Fire LC created event for Corres Charge Advance, AMLA, and SWIFT
                LetterOfCreditCreatedEvent letterOfCreditCreatedEvent = new LetterOfCreditCreatedEvent(dupTradeService, dupLc);
                eventPublisher.publish(letterOfCreditCreatedEvent);

                List results = [
                        ["original ETS Number"       : serviceInstruction.getServiceInstructionId().toString()],
                        ["duplicate ETS Number"      : dupServiceInstruction.getServiceInstructionId().toString()],
                        ["original Document Number"  : tradeService.getTradeProductNumber().toString()],
                        ["duplicate Document Number" : dupTradeService.getTradeProductNumber().toString()]
                ];

                returnMap.put("status", "ok");
                returnMap.put("details", results)

            } else {

                returnMap.put("status", "error");
                returnMap.put("details", "Document Number " + passedDocumentNumber + " not found.")
            }

        } catch(Exception e) {

            e.printStackTrace();

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

    private String buildLastLcTransactionString(
            ServiceType serviceType,
            DocumentClass documentClass,
            DocumentType documentType,
            DocumentSubType1 documentSubType1,
            DocumentSubType2 documentSubType2) {

        String docTypeStr = "FX";

        System.out.println("serviceType.toString() >> " + serviceType.toString());
        System.out.println("documentClass.toString() >> " + documentClass.toString());
        System.out.println("documentType.toString() >> " + documentType.toString());
        System.out.println("documentSubType1.toString() >> " + documentSubType1.toString());
        System.out.println("documentSubType2.toString() >> " + documentSubType2.toString());

        if (documentType.equals(DocumentType.DOMESTIC)) {
            docTypeStr = "DM";
        }

        StringBuilder builder = new StringBuilder("");
        builder.append(docTypeStr);

        if (documentClass.equals(DocumentClass.INDEMNITY)) {
            builder.append("LC ");
            builder.append(WordUtils.capitalizeFully(documentClass.toString()));
        } else {
            builder.append(documentClass.toString().toUpperCase());
            builder.append(" ");
            builder.append(WordUtils.capitalizeFully(documentSubType1.toString()));
        }

        builder.append(" ");

        if (serviceType.equals(ServiceType.NEGOTIATION_DISCREPANCY)) {
            builder.append("Negotiation Discrepancy");
        } else {
            builder.append(WordUtils.capitalizeFully(serviceType.toString()));
        }

        return builder.toString();
    }
}
