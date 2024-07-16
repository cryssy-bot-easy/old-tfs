package com.ucpb.tfs2.application.service;

import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.application.service.ChargesService;
import com.ucpb.tfs.domain.attach.Attachment;
import com.ucpb.tfs.domain.instruction.ServiceInstruction;
import com.ucpb.tfs.domain.instruction.ServiceInstructionId;
import com.ucpb.tfs.domain.instruction.ServiceInstructionRepository;
import com.ucpb.tfs.domain.instruction.enumTypes.ServiceInstructionStatus;
import com.ucpb.tfs.domain.instruction.utils.EtsNumberGenerator;
import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.payment.PaymentDetail;
import com.ucpb.tfs.domain.payment.PaymentRepository;
import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.reference.ChargeId;
import com.ucpb.tfs.domain.routing.RemarksRepository;
import com.ucpb.tfs.domain.routing.Remark;
import com.ucpb.tfs.domain.security.UserId;
import com.ucpb.tfs.domain.service.*;
import com.ucpb.tfs.domain.service.enumTypes.*;
import com.ucpb.tfs.domain.service.event.TradeServiceSavedEvent;
import com.ucpb.tfs.domain.service.event.TradeServiceUpdatedEvent;
import com.ucpb.tfs.domain.service.utils.TradeServiceReferenceNumberGenerator;
import com.ucpb.tfs.domain.task.Task;
import com.ucpb.tfs.domain.task.TaskReferenceNumber;
import com.ucpb.tfs.domain.task.TaskRepository;
import com.ucpb.tfs.domain.task.enumTypes.TaskReferenceType;
import com.ucpb.tfs.domain.task.enumTypes.TaskStatus;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class TradeServiceService {

    @Inject
    TradeServiceRepository tradeServiceRepository;

    @Inject
    ServiceInstructionRepository serviceInstructionRepository;

    @Inject
    TaskRepository taskRepository;

    @Autowired
    EtsNumberGenerator etsNumberGenerator;

    @Autowired
    DomainEventPublisher eventPublisher;

    @Autowired
    TradeServiceReferenceNumberGenerator tradeServiceReferenceNumberGenerator;

    @Autowired
    DocumentNumberService documentNumberService;

    @Autowired
    TradeServiceReferenceNumberService tradeServiceReferenceNumberService;

    @Autowired
    ChargesService chargesService;

    @Autowired
    RemarksRepository remarksRepository;

    @Autowired
    PaymentRepository paymentRepository;

    public TradeService saveTradeService(TradeService tradeService,
                                         DocumentClass documentClass,
                                         DocumentType documentType,
                                         DocumentSubType1 documentSubType1,
                                         DocumentSubType2 documentSubType2,
                                         ServiceType serviceType,
                                         Map parameterMap) {
        System.out.println("saveTradeService");


        String donotreset="N";
        if(parameterMap.containsKey("donotreset")&& parameterMap.get("donotreset")!=null && parameterMap.get("donotreset").toString().equalsIgnoreCase("Y") ){
            donotreset="Y";
        }

        // we get the common stuff first
        // identify the user id for this SI
        UserId userId = new UserId(parameterMap.get("username").toString());
        UserActiveDirectoryId activeDirectoryId = new UserActiveDirectoryId(parameterMap.get("username").toString());

        // When the logged-in user is TSD, the user's unit code is the processing unit code
        String processingUnitCode = (String) parameterMap.get("unitcode");

        String cwtFlagParam ="";
        String cwtFlagTradeServiceDetails ="";
        String sendMt730FlagParam = "";
        String sendMt730FlagTradeServiceDetails ="";

        Boolean noPayment = Boolean.FALSE;

        try {

            Boolean tradeServiceExists = tradeService != null;

            // if the tradeService already exists, we just update it
            if (tradeServiceExists) {
                System.out.println("TRADESERVICE EXISTS...");
                System.out.println(tradeService.getDocumentClass() + " " + tradeService.getServiceType());

                // For TSD-initiated transactions, if an existing TradeService is saved, reset chargesOverridenFlag
                if (tradeService.getTradeServiceReferenceNumber() != null && !tradeService.getTradeServiceReferenceNumber().toString().isEmpty()) {
                    System.out.println(">>>>>>>>>>> TSD-initiated transaction: resetting chargesOverridenFlag to 'N'");
                    tradeService.getDetails().put("chargesOverridenFlag","N");
                }




                if(tradeService.getDocumentClass().equals(DocumentClass.EXPORT_ADVISING)){
                    System.out.println("in export advising");
                    //Check if what is changed is cwtFlag or MT Flag
                    //sendMt730Flag 1 or 0
                    //cwtFlag 1 or 0

                    if(tradeService.getDetails().containsKey("cwtFlag") && tradeService.getDetails().get("cwtFlag") != null){
                        cwtFlagTradeServiceDetails = (String) tradeService.getDetails().get("cwtFlag");
                        System.out.println("cwtFlagTradeServiceDetails:"+cwtFlagTradeServiceDetails);
                    } else {
                        cwtFlagTradeServiceDetails = "0";
                    }
                    if(parameterMap.containsKey("cwtFlag") && parameterMap.get("cwtFlag") != null){
                        cwtFlagParam = (String)parameterMap.get("cwtFlag");
                        System.out.println("cwtFlagParam:"+cwtFlagParam);
                    } else {
                        cwtFlagParam = "0";
                    }

                    if(tradeService.getDetails().containsKey("sendMt730Flag") && tradeService.getDetails().get("sendMt730Flag") != null){
                        sendMt730FlagTradeServiceDetails = (String) tradeService.getDetails().get("sendMt730Flag");
                        System.out.println("sendMt730FlagTradeServiceDetails:"+sendMt730FlagTradeServiceDetails);
                    } else {
                        sendMt730FlagTradeServiceDetails = "0";
                    }
                    if(parameterMap.containsKey("sendMt730Flag") && parameterMap.get("sendMt730Flag") != null){
                        sendMt730FlagParam = (String)parameterMap.get("sendMt730Flag");
                        System.out.println("sendMt730FlagParam:"+sendMt730FlagParam);
                    } else {
                        sendMt730FlagParam = "0";
                    }

                    if(cwtFlagParam.equalsIgnoreCase(cwtFlagTradeServiceDetails) && sendMt730FlagParam.equalsIgnoreCase(sendMt730FlagTradeServiceDetails)){
//                        tradeService.getDetails().put("chargesOverridenFlag","Y");      //Will be reset only when changes are made that can affect the charges of the tradeservice
                        System.out.println("chargesOverridenFlag Y");
                    } else {
                        tradeService.getDetails().put("chargesOverridenFlag","N");      //Will be reset only when changes are made that can affect the charges of the tradeservice
                        System.out.println("chargesOverridenFlag N");
                    }
                } else {

                    if(parameterMap.containsKey("SAVE_DATA_ENTRY")&& parameterMap.get("SAVE_DATA_ENTRY")!=null && parameterMap.get("SAVE_DATA_ENTRY").toString().equalsIgnoreCase("Y") ){
                        //DO NOT RESET
//                        tradeService.getDetails().put("chargesOverridenFlag","N");
//                        System.out.println("chargesOverridenFlag Y");
                    } else {
                        //RESET
                        tradeService.getDetails().put("chargesOverridenFlag","N");
                        System.out.println("chargesOverridenFlag N");
                    }



                }

                if (tradeService.getDocumentClass().equals(DocumentClass.CDT) && tradeService.getServiceType().equals(ServiceType.REFUND)) {
                    noPayment = true;
                }

                if ("".equals(parameterMap.get("documentsEnclosedList"))) {
                    Map map = tradeService.getDetails();
                    map.remove("documentsEnclosed");

                    tradeService.setDetails(map);
                }

                if ("".equals(parameterMap.get("enclosedInstructionList"))) {
                    Map map = tradeService.getDetails();
                    map.remove("enclosedInstruction");

                    tradeService.setDetails(map);
                }

                if (parameterMap.get("documentsEnclosedList") != null && parameterMap.get("enclosedInstructionList") != null) {
                    if (parameterMap.get("additionalInstruction") == null) {
                        Map map = tradeService.getDetails();
                        map.remove("additionalInstruction");

                        tradeService.setDetails(map);
                    }
                }

            } else {

                // if the TS does not exist yet ...
                DocumentNumber documentNumber = null;
                TradeProductNumber tradeProductNumber = null;
                TradeServiceReferenceNumber referenceNumber = null;

                // special case for cdt refund since cdt refund needs to be routed
                if (parameterMap.get("documentClass").equals("CDT") && parameterMap.get("serviceType").equals("REFUND")) {
                    documentNumber = new DocumentNumber((String) parameterMap.get("documentNumber"));
                    if (parameterMap.get("tradeProductNumber") != null) {
                        tradeProductNumber = new TradeProductNumber((String) parameterMap.get("tradeProductNumber"));
                    } else {
                        tradeProductNumber = new TradeProductNumber(documentNumber.toString());
                    }
                    referenceNumber = new TradeServiceReferenceNumber((String) parameterMap.get("iedieirdNumber"));
                    System.out.println("referenceNumber >> " + referenceNumber);

                    noPayment = true;

                } else if (parameterMap.get("documentClass").equals("CORRES_CHARGE") && parameterMap.get("etsNumber") == null && parameterMap.get("serviceInstructionId") == null) {
                    System.out.println("CORRES CHARGE: ");

                    String unitcode = (String) parameterMap.get("unitcode");

                    if (parameterMap.get("documentNumber").equals("NON-REF")) { // no reference
                        documentNumber = documentNumberService.generateDocumentNumber(unitcode, null, DocumentClass.CORRES_CHARGE, null, null, null, null);

                        noPayment = Boolean.TRUE;
                    } else { // tsd initiated
                        documentNumber = new DocumentNumber((String) parameterMap.get("documentNumber"));
                    }

                    referenceNumber = new TradeServiceReferenceNumber(tradeServiceReferenceNumberGenerator.generateReferenceNumber(unitcode));
                    
                } else if (parameterMap.get("documentClass").equals("REBATE")) {
                	System.out.println("REBATE: " + parameterMap.get("rebateDocumentNumber"));
                	
                	
                	if (parameterMap.get("rebateDocumentNumber") == null || "".equals(parameterMap.get("rebateDocumentNumber").toString())) { // no reference
                		documentNumber = documentNumberService.generateDocumentNumber(processingUnitCode, null, DocumentClass.REBATE, null, null, null, null);
                		
                		noPayment = Boolean.TRUE;
                	} else { // tsd initiated
                		documentNumber = new DocumentNumber((String) parameterMap.get("rebateDocumentNumber"));
                	}
                	
                	referenceNumber = new TradeServiceReferenceNumber(tradeServiceReferenceNumberGenerator.generateReferenceNumber(processingUnitCode));

                } else {

                    if (parameterMap.get("documentNumber") != null && !parameterMap.get("documentNumber").toString().isEmpty()) {
                        System.out.println("documentNumber passed");
                        documentNumber = new DocumentNumber((String) parameterMap.get("documentNumber"));
                    } else {
                        System.out.println("documentNumber not passed");
                        documentNumber = documentNumberService.generateDocumentNumber(processingUnitCode,
                                documentClass,
                                documentType,
                                documentSubType1,
                                documentSubType2,
                                serviceType);
                    }

                    tradeProductNumber = new TradeProductNumber(documentNumber.toString());

                    referenceNumber = tradeServiceReferenceNumberService.generateReferenceNumber(processingUnitCode, documentClass, documentType, documentSubType1, documentSubType2, serviceType);
                }

                // generate the reference number
                // TODO: refactor this to use the ref # generator service
                // OLD: String tradeServiceReferenceNumber = tradeServiceReferenceNumberGenerator.generateReferenceNumber(processingUnitCode);

                // Create and persist TradeService
                if (tradeProductNumber == null) {
                    tradeProductNumber = new TradeProductNumber(documentNumber.toString());
                }
                tradeService = new TradeService(documentNumber, tradeProductNumber, documentClass, documentType, documentSubType1, documentSubType2, serviceType, activeDirectoryId, referenceNumber.toString());
                tradeService.tagStatus(TradeServiceStatus.PENDING);
                tradeService.getDetails().put("chargesOverridenFlag", "N");
            }

            tradeService.updateDetails(parameterMap, activeDirectoryId);

            // adds rates to tradeService for corres charge
            if (tradeService.getDocumentClass().equals(DocumentClass.CORRES_CHARGE) && tradeService.getServiceInstructionId() == null) {
                Iterator it = parameterMap.entrySet().iterator();

                while (it.hasNext()) {
                    Map.Entry pairs = (Map.Entry) it.next();
                    if (pairs.getKey().toString().contains("_text_pass_on_rate") ||
                            pairs.getKey().toString().contains("_pass_on_rate_cash") ||
                            pairs.getKey().toString().contains("_text_special_rate") ||
                            pairs.getKey().toString().contains("_special_rate_cash")) {

                        tradeService.getDetails().put((String) pairs.getKey(), pairs.getValue());
                    }
                }
            }

            System.out.println("tradeService.getStatus()::"+tradeService.getStatus());
            System.out.println("tradeService.getDetails().get(\"chargesOverridenFlag\")::"+tradeService.getDetails().get("chargesOverridenFlag"));
            if(tradeService.getDetails().get("chargesOverridenFlag").toString().equalsIgnoreCase("N")
                    && !tradeService.getStatus().equals(TradeServiceStatus.PREPARED)
                    && !tradeService.getStatus().equals(TradeServiceStatus.CHECKED)
                    && !tradeService.getStatus().equals(TradeServiceStatus.APPROVED)
                    && !tradeService.getStatus().equals(TradeServiceStatus.POSTED)
                    && !tradeService.getStatus().equals(TradeServiceStatus.POST_APPROVED)){
                System.out.println();
                // we add charges to it
                System.out.println("i will not reset reset?????????"+donotreset);
                if(donotreset.equalsIgnoreCase("N")){
                    chargesService.applyCharges(tradeService, parameterMap);
                } else {
                    tradeService.getDetails().put("chargesOverridenFlag","Y");
                }
            }


            if (noPayment) {
                tradeService.setAsNoPaymentRequired();
            }

            // call the repository to persist the new ts/changes
            tradeServiceRepository.merge(tradeService);

            // dispatch a savedEvent (will eventually be handled by routing module
            // dispatches TradeServiceSavedEvent if tradeService does not exist, otherwise dispatches TradeServiceUpdatedEvent
            if (!tradeServiceExists) {
                System.out.println("tradeServiceSavedEvent");
                TradeServiceSavedEvent tradeServiceSavedEvent = new TradeServiceSavedEvent(tradeService.getTradeServiceId(), tradeService.getDetails(), tradeService.getStatus(), activeDirectoryId);
                eventPublisher.publish(tradeServiceSavedEvent);
            } else {
                System.out.println("tradeServiceUpdatedEvent");
                TradeServiceUpdatedEvent tradeServiceUpdatedEvent = new TradeServiceUpdatedEvent(tradeService.getTradeServiceId(), parameterMap, activeDirectoryId);
                eventPublisher.publish(tradeServiceUpdatedEvent);
            }
        } catch (Exception e) {
            System.out.println("ERROR ====================== ");
            e.printStackTrace();
        }


        // return the map
        return tradeService;

    }

    public TradeService duplicateTradeService(
                                         TradeService oldTradeService,
                                         TradeService tradeService,
                                         DocumentClass documentClass,
                                         DocumentType documentType,
                                         DocumentSubType1 documentSubType1,
                                         DocumentSubType2 documentSubType2,
                                         ServiceType serviceType,
                                         Map parameterMap) {


        // we get the common stuff first
        // identify the user id for this SI
        UserId userId = new UserId(parameterMap.get("username").toString());
        UserActiveDirectoryId activeDirectoryId = new UserActiveDirectoryId(parameterMap.get("username").toString());

        // When the logged-in user is TSD, the user's unit code is the processing unit code
        String processingUnitCode = (String) parameterMap.get("unitcode");
        try {

            Boolean tradeServiceExists = tradeService != null;

            // if the tradeService already exists, we just update it
            if (tradeServiceExists) {

                // For TSD-initiated transactions, if an existing TradeService is saved, reset chargesOverridenFlag
                if (tradeService.getTradeServiceReferenceNumber() != null && !tradeService.getTradeServiceReferenceNumber().toString().isEmpty()) {
                    System.out.println(">>>>>>>>>>> TSD-initiated transaction: resetting chargesOverridenFlag to 'N'");
                    tradeService.getDetails().put("chargesOverridenFlag","N");
                }

            } else {

                // if the TS does not exist yet ...
                DocumentNumber documentNumber = null;
                TradeProductNumber tradeProductNumber = null;
                TradeServiceReferenceNumber referenceNumber = null;

                // special case for cdt refund since cdt refund needs to be routed
                if (parameterMap.get("documentClass").equals("CDT") && parameterMap.get("serviceType").equals("REFUND")) {
                    documentNumber = new DocumentNumber((String) parameterMap.get("documentNumber"));
                    if (parameterMap.get("tradeProductNumber") != null) {
                        tradeProductNumber = new TradeProductNumber((String) parameterMap.get("tradeProductNumber"));
                    } else {
                        tradeProductNumber = new TradeProductNumber(documentNumber.toString());
                    }
                    referenceNumber = new TradeServiceReferenceNumber((String) parameterMap.get("iedieirdNumber"));
                    System.out.println("referenceNumber >> " + referenceNumber);
                } else {

                    if (parameterMap.get("documentNumber") != null && !parameterMap.get("documentNumber").toString().isEmpty()) {
                        System.out.println("documentNumber passed");
                        documentNumber = new DocumentNumber((String) parameterMap.get("documentNumber"));
                    } else {
                        System.out.println("documentNumber not passed");
                        documentNumber = documentNumberService.generateDocumentNumber(processingUnitCode,
                                documentClass,
                                documentType,
                                documentSubType1,
                                documentSubType2,
                                serviceType);
                    }

                    tradeProductNumber = new TradeProductNumber(documentNumber.toString());

                    referenceNumber = tradeServiceReferenceNumberService.generateReferenceNumber(processingUnitCode, documentClass, documentType, documentSubType1, documentSubType2, serviceType);
                }

                // generate the reference number
                // TODO: refactor this to use the ref # generator service
                // OLD: String tradeServiceReferenceNumber = tradeServiceReferenceNumberGenerator.generateReferenceNumber(processingUnitCode);

                // Create and persist TradeService
                if (tradeProductNumber == null) {
                    tradeProductNumber = new TradeProductNumber(documentNumber.toString());
                }
                tradeService = new TradeService(documentNumber, tradeProductNumber, documentClass, documentType, documentSubType1, documentSubType2, serviceType, activeDirectoryId, referenceNumber.toString());
                tradeService.tagStatus(TradeServiceStatus.PENDING);
            }

            tradeService.updateDetails(parameterMap, activeDirectoryId);
            // we add charges to it

            Set<ServiceCharge> serviceChargeSet = oldTradeService.getServiceCharge();
            Set<Attachment> attachmentSet = oldTradeService.getAttachments();

            if (serviceChargeSet != null && !serviceChargeSet.isEmpty()) {
                tradeService.setServiceCharge(serviceChargeSet);
            }

            if (attachmentSet != null && !attachmentSet.isEmpty()) {
                tradeService.setAttachment(attachmentSet);
            }

            // call the repository to persist the new ts/changes

            Map<String, Object> oldDetails = oldTradeService.getDetails();
            oldDetails.put("hasDuplicate", true);
            oldTradeService.updateDetails(oldDetails);

            tradeServiceRepository.merge(oldTradeService);
            tradeServiceRepository.merge(tradeService);


            List<Remark> remarkList = remarksRepository.getRemarks(oldTradeService.getTradeServiceId().toString());
            for (Remark remark : remarkList) {
                remarksRepository.addRemark(remark.duplicateRemark(tradeService.getTradeServiceId().toString()));
            }

            // dispatch a savedEvent (will eventually be handled by routing module
            // dispatches TradeServiceSavedEvent if tradeService does not exist, otherwise dispatches TradeServiceUpdatedEvent
            if (!tradeServiceExists) {
                System.out.println("tradeServiceSavedEvent");
                TradeServiceSavedEvent tradeServiceSavedEvent = new TradeServiceSavedEvent(tradeService.getTradeServiceId(), tradeService.getDetails(), tradeService.getStatus(), activeDirectoryId);
                eventPublisher.publish(tradeServiceSavedEvent);
            } else {
                System.out.println("tradeServiceUpdatedEvent");
                TradeServiceUpdatedEvent tradeServiceUpdatedEvent = new TradeServiceUpdatedEvent(tradeService.getTradeServiceId(), parameterMap, activeDirectoryId);
                eventPublisher.publish(tradeServiceUpdatedEvent);
            }
        } catch (Exception e) {
            System.out.println("ERROR ====================== ");
            e.printStackTrace();
        }


        // return the map
        return tradeService;

    }

    public HashMap<String,Object> getTrLoanAmount(TradeServiceId tradeServiceId) {
        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);
        BigDecimal trloanamount = BigDecimal.ZERO;
        String currency = "";
        //BigDecimal thirdAmount = BigDecimal.ZERO;
        String temp = "0";
        if (tradeService != null) {
            Payment paymentProduct = paymentRepository.get(tradeServiceId, ChargeType.PRODUCT);

            if (paymentProduct != null) {
                if (tradeService.getDocumentClass().equals(DocumentClass.DA)
                        || tradeService.getDocumentClass().equals(DocumentClass.DP)
                        || tradeService.getDocumentClass().equals(DocumentClass.OA)
                        || tradeService.getDocumentClass().equals(DocumentClass.DR)) {

                    PaymentDetail trPaymentDetail = paymentProduct.getTRLoanPayment();

                    if (trPaymentDetail != null) {
                        trloanamount = trPaymentDetail.getAmount();
                        currency = trPaymentDetail.getCurrency().getCurrencyCode();
                    }

                }
            }
        }
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("currency",currency);
        result.put("trloanamount",trloanamount.toPlainString());
        System.out.println("result:"+result);
        return result;
    }

    public String getOtherSettlementAmount(TradeServiceId tradeServiceId) {
        //IN USD
        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);
        BigDecimal trloanamount = BigDecimal.ZERO;
        String currency = "";
        //BigDecimal thirdAmount = BigDecimal.ZERO;
        String temp = "0";
        if (tradeService != null) {
            Payment paymentProduct = paymentRepository.get(tradeServiceId, ChargeType.PRODUCT);

            if (paymentProduct != null) {
                if (tradeService.getDocumentClass().equals(DocumentClass.DA)
                        || tradeService.getDocumentClass().equals(DocumentClass.DP)
                        || tradeService.getDocumentClass().equals(DocumentClass.OA)
                        || tradeService.getDocumentClass().equals(DocumentClass.DR)) {

                    temp = paymentProduct.getTotalPreUsdWithCurrencyUrrNotTr().toPlainString();

                }
            }
        }

        System.out.println("getOtherSettlementAmount:"+temp);
        return temp;
    }

    public String getRemittanceFlag(TradeServiceId tradeServiceId) {
        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);

        if (tradeService != null && tradeService.getDetails() != null) {
            if (tradeService.getDetails().containsKey("remittanceFlag")) {
                String temp = (String) tradeService.getDetails().get("remittanceFlag");
                if (temp != null) {
                    return temp;
                }
            }
        }
        return "";
    }

    public String getCableFeeFlag(TradeServiceId tradeServiceId) {
        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);
        if (tradeService != null && tradeService.getDetails() != null) {
            if (tradeService.getDetails().containsKey("cableFeeFlag")) {
                String temp = (String) tradeService.getDetails().get("cableFeeFlag");
                if (temp != null) {
                    return temp;
                }
            }
        }
        return "";
    }

    public String getCwtFlag(TradeServiceId tradeServiceId) {
        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);
        if (tradeService != null && tradeService.getDetails() != null) {
            if (tradeService.getDetails().containsKey("cwtFlag")) {
                String temp = (String) tradeService.getDetails().get("cwtFlag");
                if (temp != null) {
                    return temp;
                }
            }
        }
        return "";
    }

    public String getChargesOverridenFlag(TradeServiceId tradeServiceId) {
        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);

        if (tradeService != null && tradeService.getDetails() != null) {
            if (tradeService.getDetails().containsKey("chargesOverridenFlag")) {
                String temp = (String) tradeService.getDetails().get("chargesOverridenFlag");
                if (temp != null) {
                    return temp;
                }
            }
        }

        return "";
    }

    public BigDecimal getOriginalServiceChargeAmount(TradeServiceId tradeServiceId, String chargeId) {
        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);
        BigDecimal temp = tradeService.getServiceCharge(new ChargeId(chargeId));

        if (temp != null) {
            return temp;
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getServiceChargeDefault(TradeServiceId tradeServiceId, String chargeId) {
        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);
        BigDecimal temp = tradeService.getServiceChargeDefault(new ChargeId(chargeId));

        if (temp != null) {
            return temp;
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getServiceChargeNoCwt(TradeServiceId tradeServiceId, String chargeId) {
        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);
        BigDecimal temp = tradeService.getServiceChargeNoCwt(new ChargeId(chargeId));

        if (temp != null) {
            return temp;
        } else {
            return BigDecimal.ZERO;
        }
    }

    public Currency getOriginalServiceChargeCurrency(TradeServiceId tradeServiceId, String chargeId) {
        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);
        return tradeService.getServiceChargeCurrency(new ChargeId(chargeId));
    }

    public String getConversionToBeUsed(TradeServiceId tradeServiceId) {
        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);
        String style = "sell-urr"; //sell-urr
        if(tradeService != null){
            Payment paymentProduct = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.PRODUCT);
            if (paymentProduct != null) {

                String productCurrency = "";
                if (tradeService.getDetails().containsKey("currency")) {
                    productCurrency = (String) tradeService.getDetails().get("currency");
                } else if (tradeService.getDetails().containsKey("negotiationCurrency")) {
                    productCurrency = (String) tradeService.getDetails().get("negotiationCurrency");
                } else if (tradeService.getDetails().containsKey("settlementCurrency")) {
                    productCurrency = (String) tradeService.getDetails().get("settlementCurrency");
                } else if (tradeService.getDetails().containsKey("productCurrency")) {
                    productCurrency = (String) tradeService.getDetails().get("productCurrency");

                }

                //Determine how product was paid using product payment and use this to compute PHP Base Amount
                // THIRD settled with THIRD will simply use THIRD-USD Sell Rate and USD-PHP urr
                // USD settled with USD will used USD-PHP urr
                // THIRD settled with USD will use THIRD to USD Sell Rate and USD-PHP urr
                // Multiple currency settlement of THIRD will use THIRD to USD Sell Rate and USD to PHP Sell Rate
                // Multiple currency settlement of USD will use USD to PHP Sell Rate

                Set<PaymentDetail> paymentDetails = paymentProduct.getDetails();
                Boolean multiplePaymentCurrency = Boolean.FALSE;
                Boolean settledWithForeign = Boolean.FALSE;
                Boolean withPhp = Boolean.FALSE;
                Currency tCurrency = null;
                for (PaymentDetail paymentDetail : paymentDetails) {
                    Currency curr = paymentDetail.getCurrency();
                    if (tCurrency == null) {
                        tCurrency = curr;
                    } else if (!tCurrency.equals(curr)) {
                        multiplePaymentCurrency = Boolean.TRUE;
                    }

                    if (!curr.getCurrencyCode().equalsIgnoreCase("PHP")) {
                        settledWithForeign = Boolean.TRUE;
                    }

                    if (curr.getCurrencyCode().equalsIgnoreCase("PHP")) {
                        withPhp = Boolean.TRUE;
                    }
                }

                if(settledWithForeign && multiplePaymentCurrency && !withPhp){
                    style = "sell-urr" ;
                } else if (multiplePaymentCurrency &&  withPhp && !productCurrency.equalsIgnoreCase("PHP")) {
                    style = "sell-sell";
                } else if (settledWithForeign && !withPhp && !productCurrency.equalsIgnoreCase("PHP")) {
                    style = "sell-urr";
                } else {
                    style = "sell-sell";
                }
            }
        }
        return style;
    }

    public String getCilexAmount(TradeServiceId tradeServiceId) {
        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);

        if (tradeService.getDetails().containsKey("productChargeAmountNetOfPesoAmountPaid")) {
            String temp = (String) tradeService.getDetails().get("productChargeAmountNetOfPesoAmountPaid");
            if (temp != null) {
                return temp;
            }
        }
        return "0";
    }

    public List buildPaymentModeMap(TradeServiceId tradeServiceId) {
        System.out.println("buildPaymentModeMap tradeServiceId:"+tradeServiceId);
        Payment payment = paymentRepository.get(tradeServiceId,ChargeType.PRODUCT);
        System.out.println("payment:"+payment);
        Hibernate.initialize(payment);
        ArrayList<Object> formattedPaymentList = new ArrayList();
        if (payment!= null){
            Set<PaymentDetail> paymentDetailList = payment.getDetails();
            for (PaymentDetail paymentDetail : paymentDetailList) {
                HashMap<String,String> a = new HashMap<String,String>();
                a.put("mode",paymentDetail.getPaymentInstrumentType().toString());
                a.put("currency",paymentDetail.getCurrency().getCurrencyCode());
                a.put("amount",paymentDetail.getAmount().toPlainString());
                System.out.println(a);
                formattedPaymentList.add(a);
            }

//            paymentDetailList.each { pay ->
//                    formattedPaymentList.add([mode: pay.getPaymentInstrumentType().toString() , currency: pay.getCurrency().getCurrencyCode(), amount: pay.getAmount()])
//            }
        }
        System.out.println("formattedPaymentList:"+formattedPaymentList);
        return formattedPaymentList;

    }

    public List buildPaymentModeMapSettlement(TradeServiceId tradeServiceId) {
        System.out.println("buildPaymentModeMapSettlement tradeServiceId:"+tradeServiceId);
        Payment payment = paymentRepository.get(tradeServiceId,ChargeType.SETTLEMENT);
        System.out.println("payment:"+payment);
        Hibernate.initialize(payment);
        ArrayList<Object> formattedPaymentList = new ArrayList();
        if (payment!= null){
            Set<PaymentDetail> paymentDetailList = payment.getDetails();
            for (PaymentDetail paymentDetail : paymentDetailList) {
                HashMap<String,String> a = new HashMap<String,String>();
                a.put("mode",paymentDetail.getPaymentInstrumentType().toString());
                a.put("currency",paymentDetail.getCurrency().getCurrencyCode());
                a.put("amount",paymentDetail.getAmount().toPlainString());
                System.out.println(a);
                formattedPaymentList.add(a);
            }

//            paymentDetailList.each { pay ->
//                    formattedPaymentList.add([mode: pay.getPaymentInstrumentType().toString() , currency: pay.getCurrency().getCurrencyCode(), amount: pay.getAmount()])
//            }
        }
        System.out.println("formattedPaymentList:"+formattedPaymentList);
        return formattedPaymentList;

    }

    public String getRemittanceFlagFromSettlement(TradeServiceId tradeServiceId){
        String flag = "N";
        Payment payment = paymentRepository.get(tradeServiceId,ChargeType.SETTLEMENT);
        Hibernate.initialize(payment);
        ArrayList<Object> formattedPaymentList = new ArrayList();
        if (payment!= null){
            Set<PaymentDetail> paymentDetailList = payment.getDetails();
            for (PaymentDetail paymentDetail : paymentDetailList) {
                if(paymentDetail.getPaymentInstrumentType().toString().equalsIgnoreCase("PDDTS")){
                    flag="Y";
                }
            }

        }
        System.out.println(flag);
        return flag;
    }

    public String getCableFlagFromSettlement(TradeServiceId tradeServiceId){
        String flag = "N";
        Payment payment = paymentRepository.get(tradeServiceId,ChargeType.SETTLEMENT);
        Hibernate.initialize(payment);
        ArrayList<Object> formattedPaymentList = new ArrayList();
        if (payment!= null){
            Set<PaymentDetail> paymentDetailList = payment.getDetails();
            for (PaymentDetail paymentDetail : paymentDetailList) {
                if(paymentDetail.getPaymentInstrumentType().toString().equalsIgnoreCase("SWIFT")){
                    flag="Y";
                }
            }

        }
        System.out.println(flag);
        return flag;
    }

    public String getTradeServiceProperty(TradeServiceId tradeServiceId, String propertyName) {
        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);

        if (tradeService != null && tradeService.getDetails() != null) {
            if (tradeService.getDetails().containsKey(propertyName.trim())) {
                String temp = (String) tradeService.getDetails().get(propertyName.trim());
                if (temp != null) {
                    return temp;
                }
            }
        }
        return "";
    }

    public String getDocumentClass(TradeServiceId tradeServiceId){
        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);

        DocumentClass documentClass = tradeService.getDocumentClass();
        if(documentClass==null){
            return "";
        } else {
            return documentClass.toString();
        }
    }

    public String getDocumentType(TradeServiceId tradeServiceId){
        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);

        DocumentType documentType = tradeService.getDocumentType();
        if(documentType==null){
            return "";
        } else {
            return documentType.toString();
        }
    }

    public String getDocumentSubType1(TradeServiceId tradeServiceId){
        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);

        DocumentSubType1 documentSubType1 = tradeService.getDocumentSubType1();
        if(documentSubType1==null){
            return "";
        } else {
            return documentSubType1.toString();
        }
    }

    public String getDocumentSubType2(TradeServiceId tradeServiceId){
        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);

        DocumentSubType2 documentSubType2 = tradeService.getDocumentSubType2();
        if(documentSubType2==null){
            return "";
        } else {
            return documentSubType2.toString();
        }
    }

    public TradeService getTradeService(TradeServiceId tradeServiceId) {
        return tradeServiceRepository.load(tradeServiceId);
    }

    public void unReverseTradeService(ServiceInstructionId serviceInstructionId) {
        System.out.println("returning to FOR_REVERSAL");
        ServiceInstruction serviceInstruction = serviceInstructionRepository.load(serviceInstructionId);

        if (serviceInstruction.getStatus().equals(ServiceInstructionStatus.REVERSED)) {
            System.out.println("is reversed...");
            serviceInstruction.setStatus(ServiceInstructionStatus.FOR_REVERSAL);

            TradeService tradeService = tradeServiceRepository.load(serviceInstructionId);
            tradeService.setStatus(TradeServiceStatus.FOR_REVERSAL);

            tradeServiceRepository.merge(tradeService);
            serviceInstructionRepository.merge(serviceInstruction);
        }
    }

    public void returnToDefaultStatus(ServiceInstructionId serviceInstructionId) {
        TradeService tradeService = tradeServiceRepository.load(serviceInstructionId);

        if (tradeService.getDetails().get("oldTradeServiceStatus") != null) {
            TradeServiceStatus tradeServiceStatus = TradeServiceStatus.valueOf((String) tradeService.getDetails().get("oldTradeServiceStatus"));

            TaskStatus taskStatus = TaskStatus.valueOf((String) tradeService.getDetails().get("oldTradeServiceStatus"));

            tradeService.setStatus(tradeServiceStatus);

            tradeServiceRepository.merge(tradeService);

            ServiceInstruction serviceInstruction = serviceInstructionRepository.load(serviceInstructionId);

            serviceInstruction.setStatus(ServiceInstructionStatus.APPROVED);

            TaskReferenceNumber taskReferenceNumber = new TaskReferenceNumber(tradeService.getTradeServiceId().toString());

            Task task = taskRepository.load(taskReferenceNumber);

            if (task != null) {
                task.updateStatus(taskStatus, tradeService.getUserActiveDirectoryId());

                taskRepository.merge(task);
            } else {
                task = new Task(taskReferenceNumber, TaskReferenceType.DATA_ENTRY, taskStatus, tradeService.getUserActiveDirectoryId());
                taskRepository.persist(task);
            }
        }
    }
}