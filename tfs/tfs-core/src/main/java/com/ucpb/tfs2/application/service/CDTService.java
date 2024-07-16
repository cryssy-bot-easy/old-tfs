package com.ucpb.tfs2.application.service;

import com.google.gson.Gson;
import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.application.service.AccountingService;
import com.ucpb.tfs.domain.cdt.CDTPaymentRequest;
import com.ucpb.tfs.domain.cdt.CDTPaymentRequestRepository;
import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.payment.PaymentRepository;
import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.product.event.CDTRemittanceErrorCorrectedEvent;
import com.ucpb.tfs.domain.reference.GltsSequenceRepository;
import com.ucpb.tfs.domain.service.ChargeType;
import com.ucpb.tfs.domain.service.TradeProductNumber;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.service.enumTypes.DocumentClass;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import com.ucpb.tfs.domain.service.enumTypes.TradeServiceStatus;
import com.ucpb.tfs.domain.service.event.TradeServiceEventListeners;
import com.ucpb.tfs.domain.service.event.TradeServiceSingleItemPaidEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**  PROLOGUE:
 * 	(revision)
	SCR/ER Number: SCR# IBD-16-1206-01
	SCR/ER Description: To comply with the requirement for CIF archiving/purging of inactive accounts in TFS.
	[Created by:] Allan Comboy and Lymuel Saul
	[Date Deployed:] 12/20/2016
	Program [Revision] Details: Add CDT Remittance and CDT Refund module.
	PROJECT: CORE
	MEMBER TYPE  : Java
	Project Name: CDTService
 */


@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class CDTService {

    @Autowired
    TradeServiceRepository tradeServiceRepository;

    @Autowired
    CDTPaymentRequestRepository cdtPaymentRequestRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    PaymentService paymentService;

    @Autowired
    DocumentNumberService documentNumberService;

    @Autowired
    DomainEventPublisher eventPublisher;
    
    @Autowired
    GltsSequenceRepository gltsSequenceRepository;
    
    @Autowired
    AccountingService accountingService;
    
    public Map saveCDTPaymentRequest(CDTPaymentRequest paymentRequest, Map<String, Object> paymentDetails) {

        String documentNumber = paymentRequest.getDocumentNumber();
        String userid = (String) paymentDetails.get("username");

        UserActiveDirectoryId userActiveDirectoryId = new UserActiveDirectoryId(userid);
        DocumentNumber docNumber = new DocumentNumber(documentNumber);

        // this does the initial save of a CDT Payment Request, we use the IED number as bot the document number and
        // trade service reference
        TradeService tradeService = null;

//        tradeService = tradeServiceRepository.load(new TradeServiceReferenceNumber(paymentRequest.getIedieirdNumber()));
//        tradeService = tradeServiceRepository.load(new TradeServiceReferenceNumber(paymentRequest.getIedieirdNumber()), ServiceType.PAYMENT);
        if (paymentDetails.get("tradeServiceId") != null) {
            tradeService = tradeServiceRepository.load(new TradeServiceId((String) paymentDetails.get("tradeServiceId")));
        }

        // if trade service does not exist, create a new one
        if(tradeService == null) {
            //System.out.println("trade service is nll so creating a new one for CDT " + paymentDetails.get("processingUnitCode").toString());
            // Set tradeproductNumber = documentNumber
            TradeProductNumber tradeProductNumber = new TradeProductNumber(docNumber.toString());
            tradeService = new TradeService(docNumber, tradeProductNumber, DocumentClass.CDT, null, null, null, ServiceType.PAYMENT, userActiveDirectoryId, paymentRequest.getIedieirdNumber());
            tradeService.updateProductCharge(paymentDetails, userActiveDirectoryId);
            tradeService.updateDetails(paymentDetails, userActiveDirectoryId);

            // moved from groovy
            tradeService.getDetails().put("paymentRequestType",paymentRequest.getPaymentRequestType());
            tradeService.getDetails().put("documentNumber",paymentRequest.getDocumentNumber());
            tradeService.getDetails().put("e2mStatus",paymentRequest.getE2mStatus());
            tradeService.getDetails().put("amount",paymentRequest.getAmount());
            tradeService.getDetails().put("finalDutyAmount",paymentRequest.getFinalDutyAmount());
            tradeService.getDetails().put("finalTaxAmount",paymentRequest.getFinalTaxAmount());
            tradeService.getDetails().put("ipf",paymentRequest.getIpf());
            tradeService.getDetails().put("finalCharges",paymentRequest.getFinalCharges());
            tradeService.getDetails().put("amountCollected",paymentRequest.getAmountCollected());
            tradeService.getDetails().put("paymentReferenceNumber",paymentRequest.getPaymentReferenceNumber());
            tradeService.getDetails().put("clientName",paymentRequest.getClientName());

//            tradeService.setStatus(TradeServiceStatus.);
        }

        if (paymentDetails.get("bankCharge") != null && paymentDetails.get("bankCharge").toString().isEmpty() != true) {
            paymentRequest.setBankCharge(new BigDecimal(paymentDetails.get("bankCharge").toString().replaceAll(",", "")));
        }

        if (paymentDetails.get("cifNumber") != null && paymentDetails.get("cifNumber").toString().isEmpty() != true) {
            paymentRequest.setCifNumber(paymentDetails.get("cifNumber").toString());
        }

        tradeService.tagStatus(TradeServiceStatus.APPROVED);

//        tradeServiceRepository.saveOrUpdate(tradeService);
        tradeServiceRepository.merge(tradeService);

        // create the payment based on details provided
        //Payment payment = paymentService.createProductPaymentFromMap(tradeService, (Map) paymentDetails);

        // invoke repository services to pesist changes
        cdtPaymentRequestRepository.update(paymentRequest);
        //        paymentRepository.saveOrUpdate(payment);

        Map details = new HashMap();

        details.put("paymentRequest", paymentRequest);
        details.put("tradeService", tradeService);
//            details.put("paymentDetails", payment);

        Gson gson = new Gson();

        Map returnMap = gson.fromJson(gson.toJson(details), Map.class);

        return returnMap;
    }

    public void throwCdtRemittanceErrorCorrectedEvent(TradeService tradeService, List<CDTPaymentRequest> cdtPaymentRequests) {

        CDTRemittanceErrorCorrectedEvent cdtRemittanceErrorCorrectedEvent = new CDTRemittanceErrorCorrectedEvent(tradeService, cdtPaymentRequests);
        eventPublisher.publish(cdtRemittanceErrorCorrectedEvent);
    }

    public Map saveCDTBranchRefund(CDTPaymentRequest paymentRequest, Map<String, Object> paymentDetails) {

        String documentNumber = paymentRequest.getDocumentNumber();
        String userid = (String) paymentDetails.get("username");

        UserActiveDirectoryId userActiveDirectoryId = new UserActiveDirectoryId(userid);
        DocumentNumber docNumber = new DocumentNumber(documentNumber);

        // this does the initial save of a CDT Payment Request, we use the IED number as bot the document number and
        // trade service reference
        TradeService tradeService = null;

        if (paymentDetails.get("tradeServiceId") != null) {
            tradeService = tradeServiceRepository.load(new TradeServiceId((String) paymentDetails.get("tradeServiceId")));
        }

        // if trade service does not exist, create a new one
        if(tradeService == null) {
            //System.out.println("trade service is null so creating a new one for CDT " + paymentDetails.get("processingUnitCode").toString());
            // Set tradeproductNumber = documentNumber
            TradeProductNumber tradeProductNumber = new TradeProductNumber(docNumber.toString());
            tradeService = new TradeService(docNumber, tradeProductNumber, DocumentClass.CDT, null, null, null, ServiceType.REFUND, userActiveDirectoryId, paymentRequest.getIedieirdNumber());
            tradeService.updateProductCharge(paymentDetails, userActiveDirectoryId);
            tradeService.updateDetails(paymentDetails, userActiveDirectoryId);
            tradeService.setAsNoPaymentRequired();
            tradeService.tagStatus(TradeServiceStatus.PENDING);
            
            tradeService.getDetails().put("paymentRequestType",paymentRequest.getPaymentRequestType());
            tradeService.getDetails().put("documentNumber",paymentRequest.getDocumentNumber());
            tradeService.getDetails().put("e2mStatus",paymentRequest.getE2mStatus());
            tradeService.getDetails().put("amount",paymentRequest.getAmount());
            tradeService.getDetails().put("finalDutyAmount",paymentRequest.getFinalDutyAmount());
            tradeService.getDetails().put("finalTaxAmount",paymentRequest.getFinalTaxAmount());
            //tradeService.getDetails().put("ipf",paymentRequest.getIpf());
            tradeService.getDetails().put("finalCharges",paymentRequest.getFinalCharges());
            tradeService.getDetails().put("amountCollected",paymentRequest.getAmountCollected());
            tradeService.getDetails().put("paymentReferenceNumber",paymentRequest.getPaymentReferenceNumber());
            tradeService.getDetails().put("clientName",paymentRequest.getClientName());
            
            tradeServiceRepository.merge(tradeService);        
        } else if(tradeService != null && paymentDetails.get("modeOfRefund") != null){
            tradeService.updateProductCharge(paymentDetails, userActiveDirectoryId);
            tradeService.updateDetails(paymentDetails, userActiveDirectoryId);
        	tradeService.setAsNoPaymentRequired(); 
        	paymentRequest.refundPayment();
        	tradeService.tagStatus(TradeServiceStatus.APPROVED);
        
	        tradeServiceRepository.merge(tradeService);
	        cdtPaymentRequestRepository.update(paymentRequest);
	        
	        TradeServiceId tradeServiceId = tradeService.getTradeServiceId();
	        TradeServiceStatus tradeServiceStatus = tradeService.getStatus();
	        //System.out.println("tradeServiceId: " + tradeServiceId);
	        String gltsNumber = gltsSequenceRepository.getGltsSequence();
	
	        accountingService.deleteActualEntries(tradeServiceId);
	
	        // set tradeServiceStatus to empty string. this is special case for CDT since CDT has no routing, therefore there
	        // is no status applicable
	        String tradeServiceStatusString = "";
	
	        if (tradeServiceStatus != null) {
	            tradeServiceStatusString = tradeServiceStatus.toString();
	        }
	
	        accountingService.generateActualEntries(tradeService, gltsNumber, tradeServiceStatusString);
	
	        gltsSequenceRepository.incrementGltsSequence();
        }
        
        Map details = new HashMap();

        details.put("paymentRequest", paymentRequest);
        details.put("tradeService", tradeService);
        details.put("tsdInitiated", "false");
//            details.put("paymentDetails", payment);

        Gson gson = new Gson();

        Map returnMap = gson.fromJson(gson.toJson(details), Map.class);

        return returnMap;
    }

//    @EventListener
//    public void markCDTPaymentRequestAsPaid(PaymentRequestPaidEvent paymentRequestPaidEvent) {
//
//        CDTPaymentRequest paymentRequest = cdtPaymentRequestRepository.load(paymentRequestPaidEvent.getIedieirdNumber());
//
//        if(paymentRequest != null) {
//
//            DocumentNumber paymentReferenceNumber = documentNumberService.generateDocumentNumber(paymentRequestPaidEvent.getProcessingUnitCode(),
//                    null, DocumentClass.CDT, null, null, null, ServiceType.PAYMENT);
//
//            paymentRequest.setPaymentReferenceNumber(paymentReferenceNumber.toString());
//            paymentRequest.setStatus(CDTStatus.PAID);
//            cdtPaymentRequestRepository.merge(paymentRequest);
//        }
//
//    }
//
//    @EventListener
//    public void markCDTPaymentRequestAsUnpaid(PaymentRequestUnpaidEvent paymentRequestUnpaidEvent) {
//
//        CDTPaymentRequest paymentRequest = cdtPaymentRequestRepository.load(paymentRequestUnpaidEvent.getIedieirdNumber());
//
//        System.out.println("i am here unpaid");
//        if(paymentRequest != null) {
//            paymentRequest.setStatus(CDTStatus.NEW);
//            paymentRequest.clearPaymentReferenceNumber();
//            cdtPaymentRequestRepository.merge(paymentRequest);
//        }
//
//    }


}
