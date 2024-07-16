package com.ucpb.tfs.application.commandHandler;

import com.incuventure.cqrs.annotation.CommandHandler;
import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.application.command.TagAsPostApprovedCommand;
import com.ucpb.tfs.domain.reference.GltsSequenceRepository;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.Map;

/**
 * User: Marv
 * Date: 11/27/12
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
//Project Name: TagAsPostApprovedCommandHandler.java
@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class TagAsPostApprovedCommandHandler implements CommandHandler<TagAsPostApprovedCommand> {

    @Autowired
    DomainEventPublisher eventPublisher;

    @Inject
    TradeServiceRepository tradeServiceRepository;

    @Autowired
    GltsSequenceRepository gltsSequenceRepository;
    
    @Autowired
    TradeServiceEventListeners tradeServiceEventListeners;
    
    @Autowired
    TaskRepository taskRepository;

    
    int stopLoop = 0;
    
    @Override
    public void handle(TagAsPostApprovedCommand command) {
    	
    	Boolean loopStop = false;
        Boolean frstloopStop = false;          
        Boolean tradeServiceloopStop = false;    
        Boolean TTEloopStop = false;
        Map<String, Object> parameterMap = command.getParameterMap();

        // temporary prints parameters
        printParameters(parameterMap);

        UserId userId = new UserId(parameterMap.get("username").toString());
        UserActiveDirectoryId fromADUser = new UserActiveDirectoryId(parameterMap.get("username").toString());

        UserActiveDirectoryId userActiveDirectoryId = new UserActiveDirectoryId("NA");

        TradeServiceId tradeServiceId = new TradeServiceId((String)parameterMap.get("tradeServiceId"));
        TradeService tradeService = tradeServiceRepository.load(tradeServiceId);

        // last user
        tradeService.updateLastUser(fromADUser);

        //tradeService.updateStatus(TradeServiceStatus.APPROVED, userActiveDirectoryId);
        tradeService.updateStatus(TradeServiceStatus.POST_APPROVED, userId);
        
        System.out.println(":::" + tradeService.getStatus());
        
        tradeServiceRepository.merge(tradeService);

  
        
        tradeService = tradeServiceRepository.load(tradeServiceId);

        
        
        while(!tradeServiceloopStop){
        if(tradeService.getStatus() == TradeServiceStatus.POST_APPROVED){
        	
        System.out.println("tradeServiceRepository Merge done!");
        
        	 
        // Fire event
        
        String gltsNumber = gltsSequenceRepository.getGltsSequence();
        TradeServiceTaggedEvent tradeServiceUpdatedEvent = new TradeServiceTaggedEvent(tradeService.getTradeServiceId(), parameterMap, TradeServiceStatus.POST_APPROVED, userActiveDirectoryId, gltsNumber);
        
        
        while(!TTEloopStop){
        if(tradeServiceUpdatedEvent.getTradeServiceStatus() == TradeServiceStatus.POST_APPROVED){
        
        
        eventPublisher.publish(tradeServiceUpdatedEvent);
        
        System.out.println("eventPublisher for tradeServiceUpdatedEvent done!");
        	
        	
      

        TradeServiceRoutedEvent tradeServiceRoutedEvent = new TradeServiceRoutedEvent(tradeService, TradeServiceStatus.POST_APPROVED, fromADUser, userActiveDirectoryId);
        stopLoop = 0;
        while(!frstloopStop){//loop start
        if(tradeServiceRoutedEvent.getTradeServiceStatus()==TradeServiceStatus.POST_APPROVED){
        
        tradeServiceEventListeners.updateTask(tradeServiceRoutedEvent);
           System.out.println("updating task: done!");
           System.out.println(tradeService.getTradeServiceId().toString() + " : TRADESERVICE ID");
           //this loop will only be useful if the transaction failed at first attempt.
      	 stopLoop = 0; //fetch starting time
      	 while(!loopStop){
           TaskReferenceNumber taskReferenceNumber = new TaskReferenceNumber(tradeService.getTradeServiceId().toString());
           
       	
           Task checkTask = taskRepository.load(taskReferenceNumber);
           System.out.println(checkTask.getTaskStatus() + "ohh yeah");
           
           if (checkTask != null) {
            	
            	if(checkTask.getTaskStatus().toString() == TradeServiceStatus.POST_APPROVED.toString() && tradeService.getStatus().toString() == TradeServiceStatus.POST_APPROVED.toString()){
            		System.out.println("Task Status: " + checkTask.getTaskStatus());
            		tradeServiceRoutedEvent.setTask(true);                       		                 		                 	
            		 eventPublisher.publish(tradeServiceRoutedEvent);
            		 loopStop = true;
            	}
            	
            } //end of checkTask != null    
           cntr();  
           if(stopLoop >= 30){
               loopStop= true;
               throw new IndexOutOfBoundsException("Transaction Failed to route. Please contact TFS support! - " + tradeService.getDocumentNumber().toString() +" : " + tradeServiceId.toString());
               }  
                  }
        	 
        	
         System.out.println("eventPublisher for tradeServiceRoutedEvent done! ");
          frstloopStop = true;
        }//end of tradeServiceRoutedEvent.getTradeServiceStatus()==TradeServiceStatus.POST_APPROVED          
        cntr(); 
        if(stopLoop >= 30){
            loopStop= true;
            throw new IndexOutOfBoundsException("Transaction Failed to route. Please contact TFS support! - " + tradeService.getDocumentNumber().toString() +" : " + tradeServiceId.toString());
            
        }
        }
                
        TTEloopStop = true;
        }//end of if(tradeServiceTaggedEvent.getTradeServiceStatus() == TradeServiceStatus.PREPARED)           
        cntr(); 
        if(stopLoop >= 30){
        	TTEloopStop= true;
            throw new IndexOutOfBoundsException("Transaction Failed to route. Please contact TFS support! - " + tradeService.getDocumentNumber().toString() +" : " + tradeServiceId.toString());
            }
        
       }
        
        
        
        
        tradeServiceloopStop = true;
        }//end of tradeService.getStatus() == TradeServiceStatus.POST_APPROVED
        cntr();
        if(stopLoop >= 30){
        	tradeServiceloopStop= true;
            throw new IndexOutOfBoundsException("Transaction Failed to route. Please contact TFS support! - " + tradeService.getDocumentNumber().toString() +" : " + tradeServiceId.toString());
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
        System.out.println("inside tag as post approved command handler...");
        Iterator it = parameterMap.entrySet().iterator();

        while(it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
        }
    }

}
