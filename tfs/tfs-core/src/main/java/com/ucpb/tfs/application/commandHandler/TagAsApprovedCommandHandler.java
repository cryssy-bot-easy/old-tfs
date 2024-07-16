package com.ucpb.tfs.application.commandHandler;

/**
 *
 * @author Marvin Volante <marvin.volante@incuventure.net>
 *
 */

//PROLOGUE:
//* 	(revision)
//SCR/ER Number:20160414-052
//SCR/ER Description: Transaction was approved in TSD, but is found on TSD Makers' screen the next day, with Pending status.
//[Revised by:] Allan Comboy Jr.
//[Date Deployed:] 04/14/2016
//Program [Revision] Details: Synchronized calling of modules/functions connected to Eventlisteners(TSD only)
//PROJECT: CORE
//MEMBER TYPE  : JAVA
//Project Name: TagAsApprovedCommandHandler.java

import com.incuventure.cqrs.annotation.CommandHandler;
import com.incuventure.cqrs.token.TokenProvider;
import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.application.command.instruction.TagAsApprovedCommand;
import com.ucpb.tfs.domain.instruction.ServiceInstruction;
import com.ucpb.tfs.domain.instruction.ServiceInstructionId;
import com.ucpb.tfs.domain.instruction.ServiceInstructionRepository;
import com.ucpb.tfs.domain.instruction.enumTypes.ServiceInstructionStatus;
import com.ucpb.tfs.domain.instruction.event.ServiceInstructionRoutedEvent;
import com.ucpb.tfs.domain.instruction.event.ServiceInstructionTaggedEvent;
import com.ucpb.tfs.domain.payment.EtsPayment;
import com.ucpb.tfs.domain.payment.EtsPaymentDetail;
import com.ucpb.tfs.domain.payment.EtsPaymentRepository;
import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.payment.PaymentDetail;
import com.ucpb.tfs.domain.payment.PaymentRepository;
import com.ucpb.tfs.domain.reference.GltsSequenceRepository;
import com.ucpb.tfs.domain.security.UserId;
import com.ucpb.tfs.domain.service.*;
import com.ucpb.tfs.domain.service.enumTypes.TradeServiceStatus;
import com.ucpb.tfs.domain.service.event.RoutingInformationLogger;
import com.ucpb.tfs.domain.service.event.TradeServiceEventListeners;
import com.ucpb.tfs.domain.service.event.TradeServiceRoutedEvent;
import com.ucpb.tfs.domain.service.event.TradeServiceTaggedEvent;
import com.ucpb.tfs.domain.task.Task;
import com.ucpb.tfs.domain.task.TaskReferenceNumber;
import com.ucpb.tfs.domain.task.TaskRepository;
import com.ucpb.tfs2.application.service.ServiceInstructionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.ucpb.tfs.domain.task.TaskRepository;

import javax.inject.Inject;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class TagAsApprovedCommandHandler implements CommandHandler<TagAsApprovedCommand>{

    @Inject
    ServiceInstructionRepository serviceInstructionRepository;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    DomainEventPublisher eventPublisher;

    @Inject
    TradeServiceRepository tradeServiceRepository;

    @Autowired
    ServiceInstructionService serviceInstructionService;

    @Autowired
    EtsPaymentRepository etsPaymentRepository;
    
    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    GltsSequenceRepository gltsSequenceRepository;

    @Autowired
    EtsServiceChargeRepository etsServiceChargeRepository;
    
    @Autowired
    TradeServiceEventListeners tradeServiceEventListeners;
    
    @Autowired
    TaskRepository taskRepository;

    @Autowired
    RoutingInformationLogger routingInformationLogger;
    
    int stopLoop = 0;
    
    @Override
	public void handle(TagAsApprovedCommand command) {

        /*
         * Combines ETS and Data Entry handling
         */

        // TODO handle save ets basic details command

        Map<String, Object> parameterMap = command.getParameterMap();

        // temporary prints parameters
        printParameters(parameterMap);

//        UserActiveDirectoryId userActiveDirectoryId = new UserActiveDirectoryId(command.getUserActiveDirectoryId());
        UserId userId = new UserId(parameterMap.get("username").toString());
        UserActiveDirectoryId fromADUser = new UserActiveDirectoryId(parameterMap.get("username").toString());

        if (((String)parameterMap.get("referenceType")).equals("ETS")) {

            // for approval of eTS, we do not route it to anyone specific so we dump it to a generic
            // TSD bucket
            UserActiveDirectoryId userActiveDirectoryId = new UserActiveDirectoryId("TSD");

            // Load from repository
            ServiceInstructionId etsNumber;
            if(parameterMap.get("reversalEtsNumber") != null) {
                System.out.println("approve ets reversal");
                etsNumber = new ServiceInstructionId((String)parameterMap.get("reversalEtsNumber"));
                ServiceInstructionId originalEtsNumber = new ServiceInstructionId((String)parameterMap.get("etsNumber"));
                serviceInstructionService.markEtsForReversalForApproveBranch(originalEtsNumber, etsNumber);
            } else {
                System.out.println("approve ets regular");
                etsNumber = new ServiceInstructionId((String)parameterMap.get("etsNumber"));
            }

            ServiceInstruction ets = serviceInstructionRepository.load(etsNumber);

            // load the associated trade service
            TradeService ts = tradeServiceRepository.load(etsNumber);

            // Set status
//            ets.updateStatus(ServiceInstructionStatus.APPROVED, userActiveDirectoryId);
            ets.updateStatus(ServiceInstructionStatus.APPROVED, userId);

            // sets last user
            ets.setLastUser(userId);

            // Persist
            serviceInstructionRepository.merge(ets);
            System.out.println("Persisted!");

//            // fix for issue#3896
//            if (ts.getDocumentType().equals(DocumentType.DOMESTIC) &&
//                    ts.getDocumentClass().equals(DocumentClass.LC) &&
//                    ts.getDocumentSubType1().equals(DocumentSubType1.CASH) &&
//                    ts.getServiceType().equals(ServiceType.NEGOTIATION)) {
//
//                Payment payment = paymentRepository.get(ts.getTradeServiceId(), ChargeType.SETTLEMENT);
//
//                if (payment != null && !payment.containsPddtsOrSwift()) {
//                    ts.setAsNoPaymentRequired();
//
//                    tradeServiceRepository.merge(ts);
//                }
//            }
            
            etsServiceChargeRepository.delete(ts.getTradeServiceId());
            Set<ServiceCharge> serviceChargeSet =  ts.getServiceCharge();
            for (ServiceCharge charge : serviceChargeSet) {
                //new Charge
                EtsServiceCharge etsServiceCharge = new EtsServiceCharge(charge);
                etsServiceCharge.setTradeServiceId(ts.getTradeServiceId());
                etsServiceChargeRepository.persist(etsServiceCharge);
            }
            
            List<Payment> payment = paymentRepository.getAllPayments(ts.getTradeServiceId());
            
            TradeServiceId tradeServiceIdEtsPayment = null;
            ChargeType chargeTypeEtsPayment = null;
            Set<PaymentDetail> detailsEtsPayment = null;

            for(Payment pay : payment) {
            	tradeServiceIdEtsPayment = pay.getTradeServiceId();
            	chargeTypeEtsPayment = pay.getChargeType();
            	detailsEtsPayment = pay.getDetails();

            	EtsPayment oldEtsPayment = etsPaymentRepository.get(tradeServiceIdEtsPayment, chargeTypeEtsPayment);
            	if (oldEtsPayment != null){
            		etsPaymentRepository.delete(oldEtsPayment);
            	}
            	
            	EtsPayment etsPayment = new EtsPayment(tradeServiceIdEtsPayment, chargeTypeEtsPayment);
                Set<PaymentDetail> paymentDetailSet = detailsEtsPayment;
                Set<EtsPaymentDetail> tempEtsPaymentDetail = new HashSet<EtsPaymentDetail>();
                
                for(PaymentDetail pd : paymentDetailSet) {
                	EtsPaymentDetail etsPaymentDetail = new EtsPaymentDetail(pd.getAccountName(), pd.getAgriAgraTagging(),
																pd.getAmount(), pd.getAmountInLcCurrency(),
																pd.getBookingCurrency(), pd.getCurrency(),
																pd.getFacilityId(), pd.getFacilityReferenceNumber(),
																pd.getFacilityType(), pd.getInterestRate(),
																pd.getInterestTerm(), pd.getInterestTermCode(),
																pd.getLoanMaturityDate(), pd.getLoanTerm(),
																pd.getLoanTermCode(), pd.getNumberOfFreeFloatDays(),
																pd.getPassOnRateThirdToPhp(), pd.getPassOnRateThirdToUsd(),
																pd.getPassOnRateUsdToPhp(), pd.getPaymentCode(),
																pd.getPaymentInstrumentType(), pd.getPaymentTerm(),
																pd.getPnNumber(), pd.getReferenceId(),
																pd.getReferenceNumber(), pd.getRepricingTerm(),
																pd.getRepricingTermCode(), pd.getSequenceNumber(),
																pd.getSpecialRateThirdToPhp(), pd.getSpecialRateThirdToUsd(),
																pd.getSpecialRateUsdToPhp(), pd.getThirdToPhpRateDescription(),
																pd.getThirdToPhpRateName(), pd.getThirdToUsdRateDescription(),
																pd.getThirdToUsdRateName(), pd.getUrr(),
																pd.getUrrRateDescription(), pd.getUrrRateName(),
																pd.getUsdToPhpRateDescription(), pd.getUsdToPhpRateName(),
																pd.getWithCramApproval());
                	tempEtsPaymentDetail.add(etsPaymentDetail);
                }
                        
            	etsPayment.deleteAllEtsPaymentDetails();
            	etsPayment.addNewEtsPaymentDetails(tempEtsPaymentDetail);
            	etsPaymentRepository.persist(etsPayment);
            }
                                                
            // Fire event
            ServiceInstructionTaggedEvent etsUpdatedEvent = new ServiceInstructionTaggedEvent(ets, ServiceInstructionStatus.APPROVED, userActiveDirectoryId);
            eventPublisher.publish(etsUpdatedEvent);

            // create a routed event. we pass the trade service id so we can update the task status of that one as well
//            ServiceInstructionRoutedEvent siRoutedEvent = new ServiceInstructionRoutedEvent(ets, ServiceInstructionStatus.APPROVED, userActiveDirectoryId, ts.getTradeServiceId());
            ServiceInstructionRoutedEvent siRoutedEvent = new ServiceInstructionRoutedEvent(ets, ServiceInstructionStatus.APPROVED, fromADUser, userActiveDirectoryId, ts.getTradeServiceId());
            eventPublisher.publish(siRoutedEvent);

            // Add to token registry
            tokenProvider.addTokenForId(command.getToken(), ets.getServiceInstructionId().toString());

        } else {
//step 1
        	//set all waiting loop to false
        	Boolean loopStop = false;
            Boolean frstloopStop = false;          
            Boolean tradeServiceloopStop = false;    
            Boolean TTEloopStop = false;  
            Boolean isReversal = false;

            // the target id will be the one referred to in the routeTo field
            UserActiveDirectoryId userActiveDirectoryId = new UserActiveDirectoryId("NA");

            //TradeServiceId tradeServiceId = new TradeServiceId((String)parameterMap.get("tradeServiceId"));
            //TradeService tradeService = tradeServiceRepository.load(tradeServiceId);

            TradeService tradeService;

            // Load from repository
            TradeServiceId tradeServiceId;

            ServiceInstructionId etsNumber = null;
            ServiceInstructionId originalEts = null;
   

            if(parameterMap.get("reversalDENumber") != null){
                etsNumber = new ServiceInstructionId((String)parameterMap.get("reversalDENumber"));
                originalEts = new ServiceInstructionId((String)parameterMap.get("etsNumber"));
                tradeServiceId  = new TradeServiceId((String)parameterMap.get("reversalDENumber"));

                tradeService = tradeServiceRepository.load(tradeServiceId);
                isReversal = true;

            }else{
                tradeServiceId = new TradeServiceId((String)parameterMap.get("tradeServiceId"));
                tradeService = tradeServiceRepository.load(tradeServiceId);
            }

            
            
       
            // last user
            tradeService.updateLastUser(fromADUser);

            //tradeService.updateStatus(TradeServiceStatus.APPROVED, userActiveDirectoryId);
            tradeService.updateStatus(TradeServiceStatus.APPROVED, userId);      

            if(isReversal) {
                serviceInstructionService.reverseDE(originalEts);
            }
            
            //start the tradeservice checking 	
            while(!tradeServiceloopStop){
            if(tradeService.getStatus() == TradeServiceStatus.APPROVED){            
           //system still needs to explicitly update table especially when hibernate cannot automatically call update for the tables once you close the session        	
            tradeServiceRepository.merge(tradeService); 
            System.out.println("tradeServiceRepository Merge done!");   
                                        

            
            tradeService = tradeServiceRepository.load(tradeServiceId);
            	
            // Fire event
            String gltsNumber = gltsSequenceRepository.getGltsSequence();
            TradeServiceTaggedEvent tradeServiceUpdatedEvent = new TradeServiceTaggedEvent(tradeService.getTradeServiceId(), parameterMap, TradeServiceStatus.APPROVED, userActiveDirectoryId, gltsNumber);
            
            //tradese    
            while(!TTEloopStop){	
            if(tradeServiceUpdatedEvent.getTradeServiceStatus() == TradeServiceStatus.APPROVED){
                   
            eventPublisher.publish(tradeServiceUpdatedEvent);
            System.out.println("eventPublisher for tradeServiceUpdatedEvent done! ");
            TradeServiceRoutedEvent tradeServiceRoutedEvent = new TradeServiceRoutedEvent(tradeService, TradeServiceStatus.APPROVED, fromADUser, userActiveDirectoryId);
            
            stopLoop = 0;
            while(!frstloopStop){
            if(tradeServiceRoutedEvent.getTradeServiceStatus() == TradeServiceStatus.APPROVED){
                                   
            tradeServiceEventListeners.updateTask(tradeServiceRoutedEvent);
            System.out.println("updating task: done!");
            System.out.println(tradeService.getTradeServiceId().toString() + " : TRADESERVICE ID");
            //this loop will only be useful if the transaction failed at first attempt.
          	 stopLoop = 0; //fetch starting time
        	 while(!loopStop){

            TaskReferenceNumber taskReferenceNumber = new TaskReferenceNumber(tradeService.getTradeServiceId().toString());
            Task checkTask = taskRepository.load(taskReferenceNumber);
            tradeService = tradeServiceRepository.load(tradeServiceId);
       
            if (checkTask != null) {
            	
            	if(checkTask.getTaskStatus().toString() == TradeServiceStatus.APPROVED.toString() && tradeService.getStatus().toString() == TradeServiceStatus.APPROVED.toString()){
            		System.out.println("Task Status: " + checkTask.getTaskStatus());
            
             		tradeServiceRoutedEvent.setTask(true);   
            
            		eventPublisher.publish(tradeServiceRoutedEvent);
            		loopStop = true;
            		
            	}
            	
            }          
            cntr(); 
            if(stopLoop >= 30){
                loopStop= true;
                throw new IndexOutOfBoundsException("Transaction Failed to route. Please contact TFS support! - " + tradeService.getDocumentNumber().toString() +" : " + tradeServiceId.toString());
                }              
        	 		}
            
        	 System.out.println("eventPublisher for tradeServiceRoutedEvent done! ");            
            frstloopStop = true;
            }
            cntr();
            if(stopLoop >= 30){
                loopStop= true;               
//                return ['lsOut':stopLoop];
                throw new IndexOutOfBoundsException("Transaction Failed to route. Please contact TFS support! - " + tradeService.getDocumentNumber().toString() +" : " + tradeServiceId.toString());
                
            }
            }
            
            TTEloopStop = true;
            }//if(tradeServiceTaggedEvent.getTradeServiceStatus() == TradeServiceStatus.PREPARED)           
            cntr(); 
            if(stopLoop >= 30){
            	TTEloopStop= true;
                throw new IndexOutOfBoundsException("Transaction Failed to route. Please contact TFS support! - " + tradeService.getDocumentNumber().toString() +" : " + tradeServiceId.toString());
                }
            
           }
            
            
            tradeServiceloopStop = true;
            }//end of tradeService.getStatus() == TradeServiceStatus.CHECKED
            cntr();
            if(stopLoop >= 30){
            	tradeServiceloopStop= true;
                throw new IndexOutOfBoundsException("Transaction Failed to route. Please contact TFS support! - " + tradeService.getDocumentNumber().toString() +" : " + tradeServiceId.toString());
                }
            }
        
        }
    }
	
    

    

	public void cntr() {
		
	     try {
            Thread.sleep(2000);                 //2000 milliseconds is one second.
            stopLoop++;
          
        	} catch(InterruptedException ex){
            Thread.currentThread().interrupt();
        } 
	
		
	}
    
	// temporary prints parameters
	private void printParameters(Map<String, Object> parameterMap) {
		System.out.println("inside tag as approved command handler...");
		Iterator it = parameterMap.entrySet().iterator();
		
		while(it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			System.out.println(pairs.getKey() + " sss= " + pairs.getValue());
		}		
	}
}
