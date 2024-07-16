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
//Project Name: TagAsPreparedCommandHandler.java
import com.incuventure.cqrs.annotation.CommandHandler;
import com.incuventure.cqrs.token.TokenProvider;
import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.application.command.instruction.TagAsPreparedCommand;
import com.ucpb.tfs.domain.email.Email;
import com.ucpb.tfs.domain.email.MailFrom;
import com.ucpb.tfs.domain.email.RoutingEmail;
import com.ucpb.tfs.domain.email.SmtpAuthenticator;
import com.ucpb.tfs.domain.email.service.EmailService;
import com.ucpb.tfs.domain.instruction.ServiceInstruction;
import com.ucpb.tfs.domain.instruction.ServiceInstructionId;
import com.ucpb.tfs.domain.instruction.ServiceInstructionRepository;
import com.ucpb.tfs.domain.instruction.enumTypes.ServiceInstructionStatus;
import com.ucpb.tfs.domain.instruction.event.ServiceInstructionRoutedEvent;
import com.ucpb.tfs.domain.instruction.event.ServiceInstructionTaggedEvent;
import com.ucpb.tfs.domain.reference.GltsSequenceRepository;
import com.ucpb.tfs.domain.security.Employee;
import com.ucpb.tfs.domain.security.EmployeeRepository;
import com.ucpb.tfs.domain.security.UserId;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.service.enumTypes.TradeServiceStatus;
import com.ucpb.tfs.domain.service.event.TradeServiceEventListeners;
import com.ucpb.tfs.domain.service.event.TradeServiceRoutedEvent;
import com.ucpb.tfs.domain.service.event.TradeServiceTaggedEvent;
import com.ucpb.tfs.domain.task.Task;
import com.ucpb.tfs.domain.task.TaskReferenceNumber;
import com.ucpb.tfs.domain.task.TaskRepository;
import com.ucpb.tfs2.application.service.ServiceInstructionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import java.util.Iterator;
import java.util.Map;

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class TagAsPreparedCommandHandler implements CommandHandler<TagAsPreparedCommand>{

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
    GltsSequenceRepository gltsSequenceRepository;

	@Autowired
	SmtpAuthenticator smtpAuthenticator;

	@Autowired
	MailFrom mailFrom;

	@Autowired
	MailSender mailSender;

	@Autowired
	EmployeeRepository employeeRepository;
	
	 @Autowired
	 TradeServiceEventListeners tradeServiceEventListeners;
		    
	 @Autowired
	 TaskRepository taskRepository;
	 
	 int stopLoop = 0;
	 
    @Override
	public void handle(TagAsPreparedCommand command) {

        /*
         * Combines ETS and Data Entry handling
         */

        // TODO handle save ets basic details command

        Map<String, Object> parameterMap = command.getParameterMap();
        

        // temporary prints parameters
        printParameters(parameterMap);

//        UserActiveDirectoryId userActiveDirectoryId = new UserActiveDirectoryId(command.getUserActiveDirectoryId());
        // the target id will be the one referred to in the routeTo field
        UserActiveDirectoryId userActiveDirectoryId = new UserActiveDirectoryId(parameterMap.get("routeTo").toString());
        UserId userId = new UserId(parameterMap.get("username").toString());
        UserActiveDirectoryId fromADUser = new UserActiveDirectoryId(parameterMap.get("username").toString());  
        
        if (((String)parameterMap.get("referenceType")).equals("ETS")) {

            // Load from repository
            ServiceInstructionId etsNumber;

            if(parameterMap.get("reversalEtsNumber") != null) {
                etsNumber = new ServiceInstructionId((String)parameterMap.get("reversalEtsNumber"));
                ServiceInstructionId originalEts = new ServiceInstructionId((String)parameterMap.get("etsNumber"));

                try {
                    System.out.println("MARKING ETS FOR REVERSAL");
                    serviceInstructionService.markEtsForReversal(originalEts, etsNumber);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }


            } else {
                etsNumber = new ServiceInstructionId((String)parameterMap.get("etsNumber"));
            }

            ServiceInstruction ets = serviceInstructionRepository.load(etsNumber);

            // Set status
            ets.updateStatus(ServiceInstructionStatus.PREPARED, userId);

            // sets last user
            ets.setLastUser(userId);

            // Persist
            serviceInstructionRepository.merge(ets);

            // Fire event
            ServiceInstructionTaggedEvent etsUpdatedEvent = new ServiceInstructionTaggedEvent(ets, ServiceInstructionStatus.PREPARED, userActiveDirectoryId);
            eventPublisher.publish(etsUpdatedEvent);

            // create a routed event. the user passed is the user the SI will be routed to
            //ServiceInstructionRoutedEvent siRoutedEvent = new ServiceInstructionRoutedEvent(ets, ServiceInstructionStatus.PREPARED, userActiveDirectoryId);
            ServiceInstructionRoutedEvent siRoutedEvent;
            

           	siRoutedEvent = new ServiceInstructionRoutedEvent(ets, ServiceInstructionStatus.PREPARED, fromADUser, userActiveDirectoryId);
            
            eventPublisher.publish(siRoutedEvent);

         // Add to token registry
            tokenProvider.addTokenForId(command.getToken(), ets.getServiceInstructionId().toString());
            
        } else {

            //TradeServiceId tradeServiceId = new TradeServiceId((String)parameterMap.get("tradeServiceId"));
            //TradeService tradeService = tradeServiceRepository.load(tradeServiceId);

            TradeService tradeService;

            // Load from repository
            TradeServiceId tradeServiceId;

//            if(parameterMap.get("reversalDENumber") != null) {
//                tradeServiceId  = new TradeServiceId((String)parameterMap.get("reversalDENumber"));
//                tradeService = tradeServiceRepository.load(tradeServiceId);
//
//            } else {
//                tradeServiceId = new TradeServiceId((String)parameterMap.get("tradeServiceId"));
//                tradeService = tradeServiceRepository.load(tradeServiceId);
//            }

            Boolean isReversal = false;

            ServiceInstructionId etsNumber = null;
            ServiceInstructionId originalEts = null;

            if(parameterMap.get("reversalDENumber") != null) {
                originalEts = new ServiceInstructionId((String)parameterMap.get("etsNumber"));
                tradeServiceId  = new TradeServiceId((String)parameterMap.get("reversalDENumber"));

                tradeService = tradeServiceRepository.load(tradeServiceId);
                isReversal = true;

            } else {
                tradeServiceId = new TradeServiceId((String)parameterMap.get("tradeServiceId"));
                tradeService = tradeServiceRepository.load(tradeServiceId);
            }

            // last user
            tradeService.updateLastUser(fromADUser);

            // prepared by
            tradeService.updatePreparedBy(fromADUser);

//            tradeService.updateStatus(TradeServiceStatus.PREPARED, userActiveDirectoryId);
            tradeService.updateStatus(TradeServiceStatus.PREPARED, userId);

           

            if(isReversal) {
                serviceInstructionService.reverseDE(originalEts);
            }
            
            System.out.println(":::" + tradeService.getStatus());
            
            // Fire event
            tradeServiceRepository.merge(tradeService);
            
            tradeService = tradeServiceRepository.load(tradeServiceId);
       
            Boolean loopStop = false;
            Boolean frstloopStop = false;          
            Boolean tradeServiceloopStop = false;    
            Boolean TTEloopStop = false;    
           
            //TradeService Status loop verify
            while(!tradeServiceloopStop){
            	
            if(tradeService.getStatus() == TradeServiceStatus.PREPARED){           	
            System.out.println("TradeService Verified");             
            String gltsNumber = gltsSequenceRepository.getGltsSequence();           
            TradeServiceTaggedEvent tradeServiceTaggedEvent = new TradeServiceTaggedEvent(tradeService.getTradeServiceId(), parameterMap, TradeServiceStatus.PREPARED, userActiveDirectoryId, gltsNumber);                                
           //TradeService Tagged Event loop verify
            while(!TTEloopStop){
            	
            if(tradeServiceTaggedEvent.getTradeServiceStatus() == TradeServiceStatus.PREPARED){
            eventPublisher.publish(tradeServiceTaggedEvent);           
            System.out.println("tradeServiceTaggedEvent Verified");              
            TradeServiceRoutedEvent tradeServiceRoutedEvent = new TradeServiceRoutedEvent(tradeService, TradeServiceStatus.PREPARED, fromADUser, userActiveDirectoryId);
            stopLoop = 0;
          //TradeService Routed Event loop verify
            while(!frstloopStop){
            	
            if(tradeServiceRoutedEvent.getTradeServiceStatus() == TradeServiceStatus.PREPARED){    
            	System.out.println("tradeServiceRoutedEvent Verified"); 
            	 tradeServiceEventListeners.updateTask(tradeServiceRoutedEvent);
            	 System.out.println("");
            	 stopLoop = 0;
            	  //this loop will only be useful if the transaction failed at first attempt.
            	  //fetch starting time
            	 
           //TradeService Routed before proceeding to task
            	 while(!loopStop){
            		            		     
                TaskReferenceNumber taskReferenceNumber = new TaskReferenceNumber(tradeService.getTradeServiceId().toString());                              	
                 Task checkTask = taskRepository.load(taskReferenceNumber);      
                 if (checkTask != null) {
                	
                 	if(checkTask.getTaskStatus().toString() == TradeServiceStatus.PREPARED.toString() && tradeService.getStatus().toString() == TradeServiceStatus.PREPARED.toString()){
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
            	frstloopStop= true;
                
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
           }//end of if(tradeService.getStatus() == TradeServiceStatus.PREPARED)            
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
		System.out.println("inside tag as prepared command handler...");
		Iterator it = parameterMap.entrySet().iterator();
		
		while(it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			System.out.println(pairs.getKey() + " = " + pairs.getValue());
		}		
	}
}
