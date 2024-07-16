package com.ucpb.tfs.application.commandHandler;

/**
 * 
 * @author Marvin Volante <marvin.volante@incuventure.net>
 * 
 */

import com.incuventure.cqrs.annotation.CommandHandler;
import com.incuventure.cqrs.token.TokenProvider;
import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.application.command.SaveBasicDetailsFormCommand;
import com.ucpb.tfs.application.service.ChargesService;
import com.ucpb.tfs.application.service.TradeServiceService;
import com.ucpb.tfs.domain.condition.AdditionalCondition;
import com.ucpb.tfs.domain.condition.ConditionCode;
import com.ucpb.tfs.domain.condition.enumTypes.ConditionType;
import com.ucpb.tfs.domain.documents.DocumentCode;
import com.ucpb.tfs.domain.documents.RequiredDocument;
import com.ucpb.tfs.domain.documents.enumTypes.RequiredDocumentType;
import com.ucpb.tfs.domain.instruction.ServiceInstruction;
import com.ucpb.tfs.domain.instruction.ServiceInstructionId;
import com.ucpb.tfs.domain.instruction.ServiceInstructionRepository;
import com.ucpb.tfs.domain.instruction.enumTypes.ServiceInstructionStatus;
import com.ucpb.tfs.domain.instruction.event.ServiceInstructionCurrencyOrAmountUpdatedEvent;
import com.ucpb.tfs.domain.instruction.event.ServiceInstructionUpdatedEvent;
import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.payment.PaymentRepository;
import com.ucpb.tfs.domain.payment.event.PaymentLcCurrencyChangedEvent;
import com.ucpb.tfs.domain.product.TradeProductRepository;
import com.ucpb.tfs.domain.reference.AdditionalConditionReference;
import com.ucpb.tfs.domain.reference.AdditionalConditionReferenceRepository;
import com.ucpb.tfs.domain.reference.RequiredDocumentsReference;
import com.ucpb.tfs.domain.reference.RequiredDocumentsReferenceRepository;
import com.ucpb.tfs.domain.security.UserId;
import com.ucpb.tfs.domain.service.ChargeType;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.service.enumTypes.DocumentClass;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import com.ucpb.tfs.domain.service.event.TradeServiceUpdatedEvent;
import com.ucpb.tfs.domain.task.Task;
import com.ucpb.tfs.domain.task.TaskReferenceNumber;
import com.ucpb.tfs.domain.task.TaskRepository;
import com.ucpb.tfs.domain.task.enumTypes.TaskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class SaveBasicDetailsFormCommandHandler implements CommandHandler<SaveBasicDetailsFormCommand> {

    @Inject
    ServiceInstructionRepository serviceInstructionRepository;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    DomainEventPublisher eventPublisher;

    @Inject
    TradeServiceRepository tradeServiceRepository;

    @Inject
    TradeProductRepository tradeProductRepository;

    @Inject
    ChargesService chargesService;

    @Inject
    TaskRepository taskRepository;

    @Inject
    PaymentRepository paymentRepository;
    
    @Inject
    RequiredDocumentsReferenceRepository requiredDocumentsReferenceRepository;
    
    @Inject
    AdditionalConditionReferenceRepository additionalConditionReferenceRepository;


    @Override
	public void handle(SaveBasicDetailsFormCommand command) {

        try {

            /*
            * Combines ETS and Data Entry handling
            */

            Map<String, Object> parameterMap = command.getParameterMap();

            // temporary prints parameters
            printParameters(parameterMap);

            //Adjustment of StandbyTagging
            adjustmentChecking(parameterMap);
            
            // todo: remove ad id once we fully transition to user id
            UserActiveDirectoryId userActiveDirectoryId = new UserActiveDirectoryId(command.getUserActiveDirectoryId());
            UserId userId = new UserId(parameterMap.get("username").toString());
            if (((String)parameterMap.get("referenceType")).equals("ETS")) {

                // Load from repository
                ServiceInstructionId etsNumber = new ServiceInstructionId((String)parameterMap.get("etsNumber"));
                ServiceInstruction ets = serviceInstructionRepository.load(etsNumber);

                // TradeServiceId tradeServiceId = new TradeServiceId((String)parameterMap.get("tradeServiceId"));
                // TradeService tradeService = tradeServiceRepository.load(tradeServiceId);

                // In LC OPENING, if LC currency has been changed, delete all payment details that has been setup.
                // Before saving the new ETS details, check if LC currency has been changed.
                DocumentClass documentClass = DocumentClass.valueOf((String)parameterMap.get("documentClass"));
                ServiceType serviceType = ServiceType.valueOf(((String)parameterMap.get("serviceType")).toUpperCase());
                Boolean lcCurrencyChanged = Boolean.FALSE;
                if (documentClass.equals(DocumentClass.LC) && serviceType.equals(ServiceType.OPENING)) {
                    Currency newCurrency = Currency.getInstance((String)parameterMap.get("currency"));
                    Currency oldCurrency = Currency.getInstance((String)ets.getDetails().get("currency"));
                    if (!oldCurrency.equals(newCurrency)) {
                        lcCurrencyChanged = Boolean.TRUE;
                        System.out.println("lcCurrencyChanged");
                    }
                }
                Boolean lcAmountChanged = Boolean.FALSE;
                if (documentClass.equals(DocumentClass.LC) && serviceType.equals(ServiceType.OPENING)) {
                    String newAmountStr = (String)parameterMap.get("amount");
                    String oldAmountStr = (String)ets.getDetails().get("amount");

                    BigDecimal newAmount = new BigDecimal(newAmountStr);
                    BigDecimal oldAmount = new BigDecimal(oldAmountStr);
                    if(! (newAmount.compareTo(oldAmount) == 0)){
                        lcAmountChanged = Boolean.TRUE;
                        System.out.println("lcAmountChanged");
                    }

                }
                



                // Update details
//                ets.updateDetails(parameterMap, userActiveDirectoryId);
                ets.updateDetails(parameterMap, userId);

                ServiceInstructionUpdatedEvent etsUpdatedEvent = null;
                if (parameterMap.get("saveAs") != null && ((String)parameterMap.get("saveAs")).equals("DRAFT")) {

                    // if the user clicked on Save As Draft, update the status of the DE in TSD
                    TradeService tradeService = tradeServiceRepository.load(etsNumber);
                    TaskReferenceNumber taskReferenceNumber = new TaskReferenceNumber(tradeService.getTradeServiceId().toString());

                    Task task = taskRepository.load(taskReferenceNumber);

                    // we just use whatevef user is currently assigned to the task
                    task.updateStatus(TaskStatus.DRAFT,  task.getUserActiveDirectoryId());

                    etsUpdatedEvent = new ServiceInstructionUpdatedEvent(ets, ServiceInstructionStatus.DRAFT, userActiveDirectoryId);

                } else {

                    // etsUpdatedEvent = new ServiceInstructionUpdatedEvent(ets, userActiveDirectoryId, tradeService);
                    etsUpdatedEvent = new ServiceInstructionUpdatedEvent(ets, userActiveDirectoryId);
                }

                // Persist update
                serviceInstructionRepository.merge(ets);

                // Fire event
                eventPublisher.publish(etsUpdatedEvent);

                // If LC currency has been changed, fire event
                // Listener will delete all payment details that have been setup
                if (lcCurrencyChanged) {
                    PaymentLcCurrencyChangedEvent paymentLcCurrencyChangedEvent = new PaymentLcCurrencyChangedEvent(ets.getServiceInstructionId());
                    eventPublisher.publish(paymentLcCurrencyChangedEvent);
                }


                ServiceInstructionCurrencyOrAmountUpdatedEvent serviceInstructionCurrencyOrAmountUpdatedEvent = new ServiceInstructionCurrencyOrAmountUpdatedEvent(ets, userActiveDirectoryId );
                eventPublisher.publish(serviceInstructionCurrencyOrAmountUpdatedEvent);


                // Add token to registry
                tokenProvider.addTokenForId(command.getToken(), ets.getServiceInstructionId().toString());

            } else {

                TradeServiceId tradeServiceId = new TradeServiceId((String)parameterMap.get("tradeServiceId"));
                TradeService tradeService = tradeServiceRepository.load(tradeServiceId);

                System.out.println("userrole: " + parameterMap.get("userrole"));

                // if this is the TSD Maker, get a reference to the task
                if(parameterMap.get("userrole").equals("TSDM")) {
                    TaskReferenceNumber taskReferenceNumber = new TaskReferenceNumber(tradeServiceId.toString());
                    Task savedTask = taskRepository.load(taskReferenceNumber);

                    System.out.println("TSDM **************** ");
                    UserActiveDirectoryId currentOwner = savedTask.getUserActiveDirectoryId();

                    // if the task is still owned by TSD, have the current user claim this
                    if(currentOwner.toString().equals("TSD")) {
                        savedTask.updateStatus(savedTask.getTaskStatus(), new UserActiveDirectoryId(userId.toString()));
                        taskRepository.merge(savedTask);
                        System.out.println("I AM UPDATING THE OWNER **************** ");
                    }
                }

                // Below will never happen because this part is for the TSD side.
                // So this is commented out.
                // The applyCharges method is moved after the TradeService is updated below.

/*
                // Load from repository
                ServiceInstructionId etsNumber = new ServiceInstructionId((String)parameterMap.get("etsNumber"));
                ServiceInstruction ets = serviceInstructionRepository.load(etsNumber);
                // Update details
                ets.updateDetails(parameterMap, userActiveDirectoryId);

                // a trade service item was modified, we call the service to delete old charges
                tradeServiceRepository.deleteServiceCharges(tradeService.getTradeServiceId());
                // we add charges to it
                chargesService.applyCharges(tradeService, ets);
*/

                // set the owner of this document to the user who save this first
                tradeService.setTSDOwner(userId);
                
                //for LC OPENING with VIEW RELATED LC
                if(parameterMap.containsKey("relatedRequiredDocumentsList") || parameterMap.containsKey("relatedAddedDocumentsList")) {
	                List<RequiredDocument> requiredDocumentsList = new ArrayList<RequiredDocument>();
	
	                int sequenceNumber = 1;
	                // add default required documents
	                if(!(parameterMap.get("relatedRequiredDocumentsList").equals(""))){
		                for(Map<String, Object> map : (List<Map<String, Object>>)parameterMap.get("relatedRequiredDocumentsList")){
		                	
		                    DocumentCode documentCode = new DocumentCode((String)map.get("documentCode"));
		
		                    RequiredDocumentsReference requiredDocumentsReference = requiredDocumentsReferenceRepository.load(documentCode);
		
		                    RequiredDocumentType requiredDocumentType = null;
		
		                    if(requiredDocumentsReference != null) {
		                        requiredDocumentType = RequiredDocumentType.DEFAULT;
		                    }else{
		                        requiredDocumentType = RequiredDocumentType.NEW;
		                    }
		
		                    RequiredDocument rd = new RequiredDocument(documentCode, (String)map.get("description"), requiredDocumentType,sequenceNumber);
		                    sequenceNumber++;
		                    requiredDocumentsList.add(rd);
		                }
	                }

	
	            
	                
	                if(!(parameterMap.get("relatedAddedDocumentsList").equals(""))){
		                // add new required document
	                    sequenceNumber = 1;
		                for(Map<String, Object> map : (List<Map<String, Object>>)parameterMap.get("relatedAddedDocumentsList")){
		                    RequiredDocument rd = new RequiredDocument(null, (String)map.get("description"), RequiredDocumentType.NEW,sequenceNumber);
		                    sequenceNumber++;
		                    requiredDocumentsList.add(rd);
		                }
		                if(tradeService.getRequiredDocument() != null || !tradeService.getRequiredDocument().isEmpty()) {
		                    tradeServiceRepository.deleteRequiredDocuments(tradeService.getTradeServiceId());
		                }
	                }
	                

	
	                tradeService.addRequiredDocuments(requiredDocumentsList);
	                
	                if(parameterMap.containsKey("relatedRequiredDocumentsList")) {
	                    parameterMap.remove("relatedRequiredDocumentsList");
	                }
	
	                if(parameterMap.containsKey("relatedAddedDocumentsList")) {
	                    parameterMap.remove("relatedAddedDocumentsList");
	                }
                }
                
                if(parameterMap.containsKey("relatedAdditionalConditionsList") || parameterMap.containsKey("relatedAddedAdditionalConditionsList")) {
                    
                List<AdditionalCondition> additionalConditionList = new ArrayList<AdditionalCondition>();
                int sequenceNumber = 1;
                if (!(parameterMap.get("relatedAdditionalConditionsList").equals(""))){
                    
                    for(Map<String, Object> map : (List<Map<String, Object>>)parameterMap.get("relatedAdditionalConditionsList")){

                        ConditionCode conditionCode = new ConditionCode((String)map.get("conditionCode"));

                        AdditionalConditionReference additionalConditionReference = additionalConditionReferenceRepository.load(conditionCode);

                        ConditionType conditionType = null;

                        if(additionalConditionReference != null) {
                            conditionType = ConditionType.DEFAULT;
                        }else{
                            conditionType = ConditionType.NEW;
                        }

                        AdditionalCondition ac = new AdditionalCondition(conditionType, conditionCode, (String)map.get("condition"),sequenceNumber);
                        sequenceNumber++;
                        additionalConditionList.add(ac);
                    }

                }
                sequenceNumber = 1;
                
                if (!(parameterMap.get("relatedAddedAdditionalConditionsList").equals(""))){
                	// add new additional condition
                    for(Map<String, Object> map : (List<Map<String, Object>>)parameterMap.get("relatedAddedAdditionalConditionsList")){
                        AdditionalCondition ac = new AdditionalCondition(ConditionType.NEW, null, (String)map.get("condition"),sequenceNumber);
                        sequenceNumber++;
                        additionalConditionList.add(ac);
                    }
                    if(tradeService.getAdditionalCondition() != null || !tradeService.getAdditionalCondition().isEmpty()) {
                        tradeServiceRepository.deleteAdditionalConditions(tradeService.getTradeServiceId());
                    }
                }

                tradeService.addAdditionalCondition(additionalConditionList);

                if(parameterMap.containsKey("relatedAdditionalConditionsList")) {
                    parameterMap.remove("relatedAdditionalConditionsList");
                }

                if(parameterMap.containsKey("relatedAddedAdditionalConditionsList")) {
                    parameterMap.remove("relatedAddedAdditionalConditionsList");
                }
                }

                // Update TradeService
                tradeService = TradeServiceService.updateTradeServiceDetails(tradeService, parameterMap, userActiveDirectoryId, "N");
                tradeService = TradeServiceService.updateProductCharge(tradeService, parameterMap, userActiveDirectoryId);
                Map<String, Object> details = tradeService.getDetails();
                Payment paymentProduct = paymentRepository.get(tradeService.getTradeServiceId(), ChargeType.PRODUCT);


//                BigDecimal totalAmountPaidInPHP = paymentProduct.getTotalPrePayment(Currency.getInstance("PHP"));
//                System.out.println("totalAmountPaidInPHP :" + totalAmountPaidInPHP);
//                BigDecimal pesoAmountPaid = paymentProduct.getTotalPrePaymentWithCurrency(Currency.getInstance("PHP"));
//                System.out.println("pesoAmountPaid :" + pesoAmountPaid);
//                BigDecimal productChargeAmountNetOfPesoAmountPaid = BigDecimal.ZERO;
//                if (totalAmountPaidInPHP != null && pesoAmountPaid != null) {
//                    productChargeAmountNetOfPesoAmountPaid = totalAmountPaidInPHP.subtract(pesoAmountPaid);
//                    System.out.println("productChargeAmountNetOfPesoAmountPaid:" + productChargeAmountNetOfPesoAmountPaid);
//                    details.put("productChargeAmountNetOfPesoAmountPaid", productChargeAmountNetOfPesoAmountPaid);
//                } else {
//                    details.put("productChargeAmountNetOfPesoAmountPaid", BigDecimal.ZERO);
//                }


//                // a trade service item was modified, we call the service to delete old charges
//                tradeServiceRepository.deleteServiceCharges(tradeService.getTradeServiceId());
//                // we add charges to it
//                chargesService.applyCharges(tradeService, details);
//                tradeService.getDetails().put("chargesOverridenFlag","N");
//                tradeServiceRepository.merge(tradeService);

                // Fire event
                // This updates basic details so no status is passed.
                TradeServiceUpdatedEvent tradeServiceUpdatedEvent = new TradeServiceUpdatedEvent(tradeService.getTradeServiceId(), parameterMap, userActiveDirectoryId);
                eventPublisher.publish(tradeServiceUpdatedEvent);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	// temporary prints parameters
	private void printParameters(Map<String, Object> parameterMap) {
		System.out.println("inside save basic details form command handler...");
		Iterator it = parameterMap.entrySet().iterator();
		
		while(it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			System.out.println(pairs.getKey() + " = " + pairs.getValue());
		}		
	}
	
	
	private void adjustmentChecking(Map<String, Object> parameterMap) {
		try {
	        if(parameterMap.get("type").toString().equalsIgnoreCase("STANDBY") && parameterMap.get("documentClass").toString().equalsIgnoreCase("LC")
	        		&& parameterMap.get("serviceType").toString().equalsIgnoreCase("Adjustment")) {
	        	//Check standbyTaggingAdjustment if correct.
	        	if(parameterMap.get("standbyTagging").toString().equalsIgnoreCase(parameterMap.get("standbyTaggingOriginalValue").toString())) {
	        		parameterMap.put("standbyTaggingAdjustment", "NO");
	        	} else {
	        		parameterMap.put("standbyTaggingAdjustment", "YES");
	        	}
	        	
	        	System.out.println("New standbyTaggingAdjustment ===== "+ parameterMap.get("standbyTaggingAdjustment"));
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
