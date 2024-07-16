package com.ucpb.tfs2.application.service;

import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.domain.email.Email;
import com.ucpb.tfs.domain.email.EmailException;
import com.ucpb.tfs.domain.email.MailFrom;
import com.ucpb.tfs.domain.email.RoutingEmail;
import com.ucpb.tfs.domain.email.SmtpAuthenticator;
import com.ucpb.tfs.domain.email.service.EmailService;
import com.ucpb.tfs.domain.instruction.ServiceInstruction;
import com.ucpb.tfs.domain.instruction.ServiceInstructionId;
import com.ucpb.tfs.domain.instruction.ServiceInstructionRepository;
import com.ucpb.tfs.domain.instruction.enumTypes.ServiceInstructionStatus;
import com.ucpb.tfs.domain.instruction.event.*;
import com.ucpb.tfs.domain.instruction.utils.EtsNumberGenerator;
import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.payment.PaymentDetail;
import com.ucpb.tfs.domain.payment.PaymentInstrumentType;
import com.ucpb.tfs.domain.payment.PaymentRepository;
import com.ucpb.tfs.domain.payment.enumTypes.PaymentStatus;
import com.ucpb.tfs.domain.security.Employee;
import com.ucpb.tfs.domain.security.EmployeeRepository;
import com.ucpb.tfs.domain.security.UserId;
import com.ucpb.tfs.domain.service.ChargeType;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.service.enumTypes.TradeServiceStatus;
import com.ucpb.tfs.domain.task.Task;
import com.ucpb.tfs.domain.task.TaskReferenceNumber;
import com.ucpb.tfs.domain.task.TaskRepository;
import com.ucpb.tfs.domain.task.enumTypes.TaskStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class ServiceInstructionService {

    @Inject
    ServiceInstructionRepository serviceInstructionRepository;

    @Inject
    TradeServiceRepository tradeServiceRepository;

    @Inject
    PaymentRepository paymentRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    EtsNumberGenerator etsNumberGenerator;

    @Autowired
    DomainEventPublisher eventPublisher;
    
    @Autowired
	SmtpAuthenticator smtpAuthenticator;
    
    @Autowired
	MailFrom mailFrom;

	@Autowired
	MailSender mailSender;

	@Autowired
	EmployeeRepository employeeRepository;
	
    public Map createServiceInstruction(Map parameterMap) {

        Map returnNewSI;

        // identify the user id for this SI
        UserId userId = new UserId(parameterMap.get("username").toString());
        UserActiveDirectoryId activeDirectoryId = new UserActiveDirectoryId(parameterMap.get("username").toString());

        String serviceInstructionId = etsNumberGenerator.generateServiceInstructionId((String) parameterMap.get("unitcode"));

        // instantiate a new Service Instruction passing along the paramter map to it
        ServiceInstruction ets = new ServiceInstruction(serviceInstructionId, parameterMap, userId);

        System.out.println ("new ETS --- "+ets);
        // all other products are automatically pending (no draft status)
        ets.tagStatus(ServiceInstructionStatus.PENDING);

        // save our new SI
        serviceInstructionRepository.persist(ets);

        // fetch it again so we can return it to the caller
        returnNewSI = serviceInstructionRepository.getServiceInstructionBy(new ServiceInstructionId(serviceInstructionId));

        // Fire event
        ServiceInstructionCreatedEvent etsCreatedEvent = new ServiceInstructionCreatedEvent(ets, ServiceInstructionStatus.PENDING, activeDirectoryId);
        eventPublisher.publish(etsCreatedEvent);

        // create a routed event. when an SI is created when it is saved as pending, it is technically routed to user who created it
        ServiceInstructionRoutedEvent siRoutedEvent = new ServiceInstructionRoutedEvent(ets, ServiceInstructionStatus.PENDING, new UserActiveDirectoryId(userId.toString()), activeDirectoryId);
        eventPublisher.publish(siRoutedEvent);

        // return the map
        return returnNewSI;

    }

    public Map reverseEts(Map parameterMap) {

        Map returnNewSI = null;

        UserActiveDirectoryId activeDirectoryId = new UserActiveDirectoryId(parameterMap.get("username").toString());
        UserId userId = new UserId(parameterMap.get("username").toString());

        // Load from repository
        ServiceInstructionId etsNumber = new ServiceInstructionId((String)parameterMap.get("etsNumber"));
        ServiceInstruction ets = serviceInstructionRepository.load(etsNumber);

        TradeService originalTradeService = tradeServiceRepository.load(etsNumber);

        TaskReferenceNumber reversedDETaskRef = new TaskReferenceNumber(originalTradeService.getTradeServiceId().toString());

        Task deTask = taskRepository.load(reversedDETaskRef);

        if(ets != null) {

            List<String> passOnKeys = new ArrayList<String>();
            Map<String, Object> passOnParams = new HashMap<String, Object>();

            passOnKeys.add("documentClass");
            passOnKeys.add("documentType");
            passOnKeys.add("documentSubType1");
            passOnKeys.add("documentSubType2");
            passOnKeys.add("documentSubType2");
            passOnKeys.add("serviceType");
            passOnKeys.add("processingUnitCode");
            passOnKeys.add("documentNumber");
            passOnKeys.add("etsNumber");
            passOnKeys.add("username");
            passOnKeys.add("unitcode");
            passOnKeys.add("referenceType");

            // todo: populate for others
            // nego :   lcNumber  mcueto
            // adjustmnet :  partialCashSettlementFlag
            // ammendment:

            Map etsDetails = ets.getDetails();

            for(String key: passOnKeys) {
                if(etsDetails.get(key) != null && !etsDetails.get(key).toString().equals("")){
                    passOnParams.put(key, etsDetails.get(key));
                    System.out.println(key + " = " + passOnParams.get(key).toString());
                }
            }

            // since etsNumber is not always populated in the map, use the etsNumber passed from the link to
            // set this reversals original etsNumber
            passOnParams.put("etsNumber", (String)parameterMap.get("etsNumber"));

            // todo: refactor this to do a map / replace lookup
          if(passOnParams.get("serviceType").toString().equalsIgnoreCase("OPENING")) {
                passOnParams.put("serviceType", "OPENING_REVERSAL");
          }else if(passOnParams.get("serviceType").toString().equalsIgnoreCase("UA_LOAN_SETTLEMENT")) {
        	  passOnParams.put("serviceType", "UA_LOAN_SETTLEMENT_REVERSAL");
            }else if(passOnParams.get("serviceType").toString().equalsIgnoreCase("SETTLEMENT")) {
                passOnParams.put("serviceType", "SETTLEMENT_REVERSAL");
            }else if(passOnParams.get("serviceType").toString().equalsIgnoreCase("AMENDMENT")) {
            	passOnParams.put("serviceType", "AMENDMENT_REVERSAL");
            }  

            passOnParams.put("reverseEts", "true");
            passOnParams.put("previousApprovers", "");

            // if original trade service exists (and it should since we can't create a reversal unless it is branch
            // approved) ...
            if(originalTradeService != null) {
                passOnParams.put("originalTradeServiceId", originalTradeService.getTradeServiceId().toString());
                passOnParams.put("originalTradeServiceStatus", originalTradeService.getStatus().toString());
            }

            if(deTask != null) {
                passOnParams.put("originalTaskStatus", deTask.getTaskStatus().toString());
            }

            // create and persist our reversal eTS
            String serviceInstructionId = etsNumberGenerator.generateServiceInstructionId((String)parameterMap.get("unitcode"));

            passOnParams.put("reversalEtsNumber", serviceInstructionId);

            passOnParams.put("amount", originalTradeService.getDetails().get("amount"));
            passOnParams.put("currency", originalTradeService.getDetails().get("currency"));

            passOnParams.put("cifNumber", originalTradeService.getDetails().get("cifNumber"));
            passOnParams.put("cifName", originalTradeService.getDetails().get("cifName"));
            passOnParams.put("mainCifNumber", originalTradeService.getDetails().get("mainCifNumber"));
            passOnParams.put("mainCifName", originalTradeService.getDetails().get("mainCifName"));
            passOnParams.put("accountOfficer", originalTradeService.getDetails().get("accountOfficer"));
            passOnParams.put("ccbdBranchUnitCode", originalTradeService.getDetails().get("ccbdBranchUnitCode"));


            ServiceInstruction reversalEts = new ServiceInstruction(serviceInstructionId, passOnParams, userId);
            reversalEts.tagStatus(ServiceInstructionStatus.PENDING);

            serviceInstructionRepository.persist(reversalEts);

            //ServiceInstructionCreatedEvent etsCreatedEvent = new ServiceInstructionCreatedEvent(reversalEts, ServiceInstructionStatus.PENDING, activeDirectoryId);
            ServiceInstructionCreatedEvent etsCreatedEvent = new ServiceInstructionCreatedEvent(reversalEts, ServiceInstructionStatus.PENDING, new UserActiveDirectoryId("TSD"));
            eventPublisher.publish(etsCreatedEvent);

            // create a routed event. when an SI is created when it is saved as pending, it is technically routed to user who created it
            ServiceInstructionRoutedEvent siRoutedEvent = new ServiceInstructionRoutedEvent(reversalEts, ServiceInstructionStatus.PENDING, new UserActiveDirectoryId(userId.toString()), activeDirectoryId);
            eventPublisher.publish(siRoutedEvent);

            // fetch it again so we can return it to the caller
            returnNewSI = serviceInstructionRepository.getServiceInstructionBy(new ServiceInstructionId(serviceInstructionId));

        }


        return returnNewSI;
    }

    public void markEtsForReversalForApproveBranch(ServiceInstructionId etsNumber, ServiceInstructionId reversalEtsNumber) {
        System.out.println("MARK FOR REVERSAL");
        if(etsNumber != null) {

            Boolean hasOnePaidItem = false;

            TradeService tradeService = tradeServiceRepository.load(etsNumber);

            List<Payment> payments = paymentRepository.getPaymentBy(tradeService.getTradeServiceId());

            for(Payment payment : payments) {
                for(PaymentDetail paymentDetail : payment.getDetails()) {

                    // for product/charges/refund payment consider if paid
                    // for settlement, only considered as paid if type is CASA (all others for settlement are auto-paid)
                    if((paymentDetail.getStatus() == PaymentStatus.PAID && (payment.getChargeType() == ChargeType.PRODUCT || payment.getChargeType() == ChargeType.SERVICE || payment.getChargeType() == ChargeType.REFUND)) ||
                       (paymentDetail.getStatus() == PaymentStatus.PAID && payment.getChargeType() == ChargeType.SETTLEMENT && paymentDetail.getPaymentInstrumentType() == PaymentInstrumentType.CASA )
                      ) {
                        hasOnePaidItem = true;
                        System.out.println("HAS ONE PAID ITEM");
                    }
                }
            }

            // save the old status in the map
            Map<String, Object> oldValues = new HashMap<String, Object>();
            if (tradeService.getDetails().get("oldTradeServiceStatus") == null) {
                oldValues.put("oldTradeServiceStatus", tradeService.getStatus());

                tradeService.updateDetails(oldValues, null);
            }

            // tag as reversed if there is no paid payment upon approving branch
            if(!hasOnePaidItem) {

                System.out.println("autoreversed");
                tradeService.tagStatus(TradeServiceStatus.REVERSED);
                ServiceInstruction serviceInstruction = serviceInstructionRepository.load(etsNumber);
                serviceInstruction.tagStatus(ServiceInstructionStatus.REVERSED);

                serviceInstructionRepository.merge(serviceInstruction);

                // set status of the reversal DE to paid
                TradeService reversalTradeService = tradeServiceRepository.load(reversalEtsNumber);
                reversalTradeService.paid();
                tradeServiceRepository.update(reversalTradeService);

                ServiceInstructionReversedEvent serviceInstructionReversedEvent = new ServiceInstructionReversedEvent(serviceInstruction.getServiceInstructionId(), tradeService.getTradeServiceId());
                eventPublisher.publish(serviceInstructionReversedEvent);
            }

            System.out.println("updating tradeService...");
            tradeServiceRepository.update(tradeService);
            System.out.println("status >> " + tradeService.getStatus());
        }
    }

    public void markEtsForReversal(ServiceInstructionId etsNumber, ServiceInstructionId reversalEtsNumber) {
    	System.out.println("MARK FOR REVERSAL");
        if(etsNumber != null) {

//            Boolean hasOnePaidItem = false;

            TradeService tradeService = tradeServiceRepository.load(etsNumber);
            
//            List<Payment> payments = paymentRepository.getPaymentBy(tradeService.getTradeServiceId());
//
//            for(Payment payment : payments) {
//                for(PaymentDetail paymentDetail : payment.getDetails()) {
//
//                    // for product/charges/refund payment consider if paid
//                    // for settlement, only considered as paid if type is CASA (all others for settlement are auto-paid)
//                    if((paymentDetail.getStatus() == PaymentStatus.PAID && (payment.getChargeType() == ChargeType.PRODUCT || payment.getChargeType() == ChargeType.SERVICE || payment.getChargeType() == ChargeType.REFUND)) ||
//                       (paymentDetail.getStatus() == PaymentStatus.PAID && payment.getChargeType() == ChargeType.SETTLEMENT && paymentDetail.getPaymentInstrumentType() == PaymentInstrumentType.CASA )
//                      ) {
//                        hasOnePaidItem = true;
//                        System.out.println("HAS ONE PAID ITEM");
//                    }
//                }
//            }

            // save the old status in the map
            Map<String, Object> oldValues = new HashMap<String, Object>();
            oldValues.put("oldTradeServiceStatus", tradeService.getStatus());

            tradeService.updateDetails(oldValues, null);

            // tag for reversal regardless if there is paid payment
            System.out.println("for reversal");
            tradeService.tagStatus(TradeServiceStatus.FOR_REVERSAL);

            ServiceInstruction serviceInstruction = serviceInstructionRepository.load(etsNumber);
            ServiceInstructionMarkedForReversalEvent serviceInstructionMarkedForReversalEvent = new ServiceInstructionMarkedForReversalEvent(serviceInstruction.getServiceInstructionId(), tradeService.getTradeServiceId());

            serviceInstruction.tagStatus(ServiceInstructionStatus.FOR_REVERSAL);

            eventPublisher.publish(serviceInstructionMarkedForReversalEvent);

//            if(hasOnePaidItem) {
//
//                System.out.println("for reversal");
//                tradeService.tagStatus(TradeServiceStatus.FOR_REVERSAL);
//
//                ServiceInstruction serviceInstruction = serviceInstructionRepository.load(etsNumber);
//                ServiceInstructionMarkedForReversalEvent serviceInstructionMarkedForReversalEvent = new ServiceInstructionMarkedForReversalEvent(serviceInstruction.getServiceInstructionId(), tradeService.getTradeServiceId());
//
//                serviceInstruction.tagStatus(ServiceInstructionStatus.FOR_REVERSAL);
//
//                eventPublisher.publish(serviceInstructionMarkedForReversalEvent);
//
//            }
//            else {
//
//                System.out.println("autoreversed");
//                tradeService.tagStatus(TradeServiceStatus.REVERSED);
//                ServiceInstruction serviceInstruction = serviceInstructionRepository.load(etsNumber);
//                serviceInstruction.tagStatus(ServiceInstructionStatus.REVERSED);
//
//                serviceInstructionRepository.merge(serviceInstruction);
//
//                // set status of the reversal DE to paid
//                TradeService reversalTradeService = tradeServiceRepository.load(reversalEtsNumber);
//                reversalTradeService.paid();
//                tradeServiceRepository.update(reversalTradeService);
//
//                ServiceInstructionReversedEvent serviceInstructionReversedEvent = new ServiceInstructionReversedEvent(serviceInstruction.getServiceInstructionId(), tradeService.getTradeServiceId());
//                eventPublisher.publish(serviceInstructionReversedEvent);
//
//            }

            System.out.println("updating tradeService...");
            tradeServiceRepository.update(tradeService);
            System.out.println("status >> " + tradeService.getStatus());
        }
    }

    public void unmarkEtsForReversal(ServiceInstructionId etsNumber) {

        TradeService tradeService = tradeServiceRepository.load(etsNumber);

        ServiceInstruction serviceInstruction = serviceInstructionRepository.load(etsNumber);

        String taskStatus = "";
        String originalTradeServiceId = "";
        String originalTradeServiceStatus = "";
        TradeService tradeServiceId;

        if(serviceInstruction.getDetails().containsKey("originalTradeServiceId") &&
           serviceInstruction.getDetails().containsKey("originalTaskStatus")
                ) {

            taskStatus = (String) serviceInstruction.getDetails().get("originalTaskStatus");
            originalTradeServiceId = (String) serviceInstruction.getDetails().get("originalTradeServiceId");
            originalTradeServiceStatus = (String) serviceInstruction.getDetails().get("originalTradeServiceStatus");

            TradeService originalTradeService = tradeServiceRepository.load(new TradeServiceId(originalTradeServiceId));

            if(originalTradeService != null) {

                TaskStatus originalTaskStatus = TaskStatus.valueOf(taskStatus);
                ServiceInstructionReversalUnmarkedEvent serviceInstructionMarkedForReversalEvent = new ServiceInstructionReversalUnmarkedEvent(serviceInstruction.getServiceInstructionId(), originalTradeService.getTradeServiceId(), originalTaskStatus);
                eventPublisher.publish(serviceInstructionMarkedForReversalEvent);

                originalTradeService.setStatus(TradeServiceStatus.valueOf(originalTradeServiceStatus));

                tradeServiceRepository.update(originalTradeService);

            }

        }

    }

    public void reverseDE(ServiceInstructionId etsNumber) {

        TradeService tradeService = tradeServiceRepository.load(etsNumber);

        System.out.println("reversal approved");
        if (tradeService.getStatus().equals(TradeServiceStatus.FOR_REVERSAL)) {
            tradeService.tagStatus(TradeServiceStatus.REVERSED);

            ServiceInstruction serviceInstruction = serviceInstructionRepository.load(etsNumber);
            serviceInstruction.tagStatus(ServiceInstructionStatus.REVERSED);

            serviceInstructionRepository.merge(serviceInstruction);

            ServiceInstructionReversedEvent serviceInstructionReversedEvent = new ServiceInstructionReversedEvent(serviceInstruction.getServiceInstructionId(), tradeService.getTradeServiceId());
            eventPublisher.publish(serviceInstructionReversedEvent);
        }
    }

    public void effectReversal(ServiceInstructionId etsNumber) {

        if(etsNumber != null) {

            Boolean hasOnePaidItem = false;

            TradeService tradeService = tradeServiceRepository.load(etsNumber);

            List<Payment> payments = paymentRepository.getPaymentBy(tradeService.getTradeServiceId());

            for(Payment payment : payments) {
                for(PaymentDetail paymentDetail : payment.getDetails()) {
                    if(paymentDetail.getStatus() == PaymentStatus.PAID) {
                        hasOnePaidItem = true;
                    }
                }
            }
        }

    }

    public Map updateServiceInstruction(Map parameterMap) {

        Map returnUpdatedSI = null;

        // todo: remove ad id once we fully transition to user id
        UserActiveDirectoryId userActiveDirectoryId = new UserActiveDirectoryId(parameterMap.get("username").toString());
        UserId userId = new UserId(parameterMap.get("username").toString());

        // .................

        // Load from repository
        ServiceInstructionId etsNumber = new ServiceInstructionId((String) parameterMap.get("etsNumber"));
        ServiceInstruction ets = serviceInstructionRepository.load(etsNumber);

        // Update details
        ets.updateDetails(parameterMap, userId);

        // Persist update
        serviceInstructionRepository.merge(ets);

        // Fire event
        ServiceInstructionUpdatedEvent etsUpdatedEvent = new ServiceInstructionUpdatedEvent(ets, userActiveDirectoryId);
        eventPublisher.publish(etsUpdatedEvent);

        return returnUpdatedSI;

    }

    public Map updateServiceInstruction2(Map parameterMap) {

        //Map returnUpdatedSI = null;

        // todo: remove ad id once we fully transition to user id
        UserActiveDirectoryId userActiveDirectoryId = new UserActiveDirectoryId(parameterMap.get("username").toString());
        UserId userId = new UserId(parameterMap.get("username").toString());

        // .................

        // Load from repository
        ServiceInstructionId etsNumber = new ServiceInstructionId((String) parameterMap.get("etsNumber"));
        ServiceInstruction ets = serviceInstructionRepository.load(etsNumber);

        // Update details
        ets.updateDetails(parameterMap, userId);

        System.out.println("hello parameter " + parameterMap.get("negotiationNumber"));
        System.out.println("hello parameter " + parameterMap.get("form"));

        if (parameterMap.get("negotiationNumber") == null &&
                "BP".equals(ets.getDetails().get("documentClass")) &&
                "FOREIGN".equals(ets.getDetails().get("documentType")) &&
                "NEGOTIATION".equals(ets.getDetails().get("serviceType")) &&
                "basicDetails".equals(parameterMap.get("form"))) {
            System.out.println("nego is null");

            Map etsDetails = ets.getDetails();

            System.out.println("etsDetails.negoNumber before " + etsDetails.get("negotiationNumber"));
            etsDetails.remove("negotiationNumber");
            System.out.println("etsDetails.negoNumber after " + etsDetails.get("negotiationNumber"));

            ets.setDetails(etsDetails);

            System.out.println("ets.negoNumber now " + ets.getDetails().get("negotiationNumber"));
        }

        // Persist update
        serviceInstructionRepository.merge(ets);

        // Fire event
        ServiceInstructionUpdatedEvent etsUpdatedEvent = new ServiceInstructionUpdatedEvent(ets, userActiveDirectoryId);
        eventPublisher.publish(etsUpdatedEvent);

        ServiceInstructionCurrencyOrAmountUpdatedEvent serviceInstructionCurrencyOrAmountUpdatedEvent = new ServiceInstructionCurrencyOrAmountUpdatedEvent(ets, userActiveDirectoryId );
        eventPublisher.publish(serviceInstructionCurrencyOrAmountUpdatedEvent);

        Map returnUpdatedSI = serviceInstructionRepository.getServiceInstructionBy(etsNumber);

        return returnUpdatedSI;

    }

    public Map reverseEtsNew(Map parameterMap) {

        Map returnNewSI = null;

        UserActiveDirectoryId activeDirectoryId = new UserActiveDirectoryId(parameterMap.get("username").toString());
        UserId userId = new UserId(parameterMap.get("username").toString());

        // Load from repository
        ServiceInstructionId etsNumber = new ServiceInstructionId((String)parameterMap.get("etsNumber"));
        ServiceInstruction ets = serviceInstructionRepository.load(etsNumber);

        TradeService originalTradeService = tradeServiceRepository.load(etsNumber);

        TaskReferenceNumber reversedDETaskRef = new TaskReferenceNumber(originalTradeService.getTradeServiceId().toString());

        Task deTask = taskRepository.load(reversedDETaskRef);

        if(ets != null) {

            List<String> passOnKeys = new ArrayList<String>();
            Map<String, Object> passOnParams = new HashMap<String, Object>();

            passOnKeys.add("documentClass");
            passOnKeys.add("documentType");
            passOnKeys.add("documentSubType1");
            passOnKeys.add("documentSubType2");
            passOnKeys.add("documentSubType2");
            passOnKeys.add("serviceType");
            passOnKeys.add("processingUnitCode");
            passOnKeys.add("documentNumber");
            passOnKeys.add("etsNumber");
            passOnKeys.add("username");
            passOnKeys.add("unitcode");
            passOnKeys.add("referenceType");

            // todo: populate for others
            // nego :   lcNumber  mcueto
            // adjustmnet :  partialCashSettlementFlag
            // ammendment:

            Map etsDetails = ets.getDetails();

            for(String key: passOnKeys) {
                if(etsDetails.get(key) != null && !etsDetails.get(key).toString().equals("")){
                    passOnParams.put(key, etsDetails.get(key));
                    System.out.println(key + " = " + passOnParams.get(key).toString());
                }
            }

            // since etsNumber is not always populated in the map, use the etsNumber passed from the link to
            // set this reversals original etsNumber
            passOnParams.put("etsNumber", (String)parameterMap.get("etsNumber"));

            // todo: refactor this to do a map / replace lookup
            if(passOnParams.get("serviceType").toString().equalsIgnoreCase("OPENING")) {
                passOnParams.put("serviceType", "OPENING_REVERSAL");
            } else if(passOnParams.get("serviceType").toString().equalsIgnoreCase("SETTLEMENT")) {
                passOnParams.put("serviceType", "SETTLEMENT_REVERSAL");
            }

            passOnParams.put("reverseEts", "true");
            passOnParams.put("previousApprovers", "");

            // if original trade service exists (and it should since we can't create a reversal unless it is branch
            // approved) ...
            if(originalTradeService != null) {
                passOnParams.put("originalTradeServiceId", originalTradeService.getTradeServiceId().toString());
                passOnParams.put("originalTradeServiceStatus", originalTradeService.getStatus().toString());
            }

            if(deTask != null) {
                passOnParams.put("originalTaskStatus", deTask.getTaskStatus().toString());
            }

            // create and persist our reversal eTS
            String serviceInstructionId = etsNumberGenerator.generateServiceInstructionId((String)parameterMap.get("unitcode"));

            passOnParams.put("reversalEtsNumber", serviceInstructionId);

            passOnParams.put("amount", originalTradeService.getDetails().get("amount"));
            passOnParams.put("currency", originalTradeService.getDetails().get("currency"));

            passOnParams.put("cifNumber", originalTradeService.getDetails().get("cifNumber"));
            passOnParams.put("cifName", originalTradeService.getDetails().get("cifName"));
            passOnParams.put("mainCifNumber", originalTradeService.getDetails().get("mainCifNumber"));
            passOnParams.put("mainCifName", originalTradeService.getDetails().get("mainCifName"));
            passOnParams.put("accountOfficer", originalTradeService.getDetails().get("accountOfficer"));
            passOnParams.put("ccbdBranchUnitCode", originalTradeService.getDetails().get("ccbdBranchUnitCode"));


            ServiceInstruction reversalEts = new ServiceInstruction(serviceInstructionId, passOnParams, userId);
            reversalEts.tagStatus(ServiceInstructionStatus.PENDING);

            serviceInstructionRepository.persist(reversalEts);

            //ServiceInstructionCreatedEvent etsCreatedEvent = new ServiceInstructionCreatedEvent(reversalEts, ServiceInstructionStatus.PENDING, activeDirectoryId);
            ServiceInstructionCreatedEvent etsCreatedEvent = new ServiceInstructionCreatedEvent(reversalEts, ServiceInstructionStatus.PENDING, new UserActiveDirectoryId("TSD"));
            eventPublisher.publish(etsCreatedEvent);

            // create a routed event. when an SI is created when it is saved as pending, it is technically routed to user who created it
            ServiceInstructionRoutedEvent siRoutedEvent = new ServiceInstructionRoutedEvent(reversalEts, ServiceInstructionStatus.PENDING, new UserActiveDirectoryId(userId.toString()), activeDirectoryId);
            eventPublisher.publish(siRoutedEvent);

            // fetch it again so we can return it to the caller
            returnNewSI = serviceInstructionRepository.getServiceInstructionBy(new ServiceInstructionId(serviceInstructionId));

        }


        return returnNewSI;
    }

    public void rerouteServiceInstruction(String etsNumber, String routedTo, String rerouteTo, String loggedInUsername) throws EmailException {
    	EmailService emailService = new EmailService();
    	System.out.println("Rerouting transaction....");
        Task task = taskRepository.load(new TaskReferenceNumber(etsNumber));

        task.setUserActiveDirectoryId(new UserActiveDirectoryId(rerouteTo));

        taskRepository.merge(task);

        ServiceInstruction ets = serviceInstructionRepository.load(new ServiceInstructionId(etsNumber));
        
        ServiceInstructionRoutedEvent siRoutedEvent = new ServiceInstructionRoutedEvent(ets, ets.getStatus(),
                new UserActiveDirectoryId(ets.getLastUser().toString()), new UserActiveDirectoryId(rerouteTo));

        eventPublisher.publish(siRoutedEvent);
        
        /**
         * 	05/26/2017 Redmine #4222  
         * 	Edit by Pat - Call e-mail service and feed the needed parameters such as sender and receiver
         */
        try{
	        Email routingEmail = null;
	        Employee employeeReceiver = employeeRepository.getEmployee(new UserId(new UserActiveDirectoryId(rerouteTo).toString()));
	        Map<String, Object> detailMap = ets.getDetails(); 
	        ServiceInstructionStatus txStatus = ets.getStatus();
	        Employee employeeSender = employeeRepository.getEmployee(new UserId(loggedInUsername));
	        Employee employeeRoutedTo = employeeRepository.getEmployee(new UserId(new UserActiveDirectoryId(routedTo).toString()));
	        String fullNameRoutedTo = employeeRoutedTo.getFullName();
	        String fullNameReroutedTo = employeeReceiver.getFullName();
	        
	        if (employeeReceiver.getReceiveEmail() != null && employeeReceiver.getReceiveEmail() == true 
	        		&& employeeRoutedTo.getReceiveEmail() != null && employeeRoutedTo.getReceiveEmail() == true ){
	    		routingEmail = new RoutingEmail(detailMap, employeeSender, employeeRoutedTo);
	    		emailService.sendRerouteEmailFromOriginalRecipient(smtpAuthenticator, mailFrom, mailSender, routingEmail, txStatus, fullNameReroutedTo);
	    		routingEmail = new RoutingEmail(detailMap, employeeSender, employeeReceiver);
	    		emailService.sendRerouteEmailToNewRecipient(smtpAuthenticator, mailFrom, mailSender, routingEmail, txStatus, fullNameRoutedTo);
	        }
        } catch(Exception e){
        	//Print error only.
        	e.printStackTrace();
         	throw new EmailException(e.getMessage());
        }
    }
}
