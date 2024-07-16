package com.ucpb.tfs.domain.service.event;

import com.incuventure.ddd.infrastructure.events.EventListener;
import com.ipc.rbac.domain.User;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.domain.instruction.ServiceInstructionId;
import com.ucpb.tfs.domain.instruction.event.ServiceInstructionCreatedEvent;
import com.ucpb.tfs.domain.instruction.event.ServiceInstructionRoutedEvent;
import com.ucpb.tfs.domain.instruction.event.ServiceInstructionTaggedEvent;
import com.ucpb.tfs.domain.instruction.event.ServiceInstructionUpdatedEvent;
import com.ucpb.tfs.domain.routing.Route;
import com.ucpb.tfs.domain.routing.RoutingInformation;
import com.ucpb.tfs.domain.routing.RoutingInformationId;
import com.ucpb.tfs.domain.routing.RoutingInformationRepository;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.enumTypes.TradeServiceStatus;
import com.ucpb.tfs.domain.task.Task;
import com.ucpb.tfs.domain.task.TaskReferenceNumber;
import com.ucpb.tfs.domain.task.TaskRepository;
import com.ucpb.tfs.domain.task.enumTypes.TaskStatus;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
  *
 */

//PROLOGUE:
//* 	(revision)
//SCR/ER Number:20160414-052
//SCR/ER Description: Transaction was approved in TSD, but is found on TSD Makers' screen the next day, with Pending status.
//[Revised by:] Allan Comboy Jr.
//[Date Deployed:] 04/14/2016
//Program [Revision] Details: check log if task and tradeservice is equal(TSD only)
//PROJECT: CORE
//MEMBER TYPE  : JAVA
//Project Name: RoutingInformationLogger.java
@Component
public class RoutingInformationLogger {

    @Autowired
    private RoutingInformationRepository routingInformationRepository;

    @Autowired
    private Mapper mapper;

    @Autowired
    TaskRepository taskRepository;
    
//    @EventListener
    public void addRoutingInformation(TradeServiceTaggedEvent event){
        addRoute(event, new RoutingInformationId(event.getTradeServiceId().toString()));
    
    }

//    @EventListener
    public void addRoutingInformation(ServiceInstructionTaggedEvent event){
        addRoute(event,new RoutingInformationId(event.getServiceInstruction().getServiceInstructionId().toString()));
    }

    private <T> void addRoute(T event,RoutingInformationId id){
        Route route = mapper.map(event,Route.class);
        routingInformationRepository.addRoutingInformation(route,id);
    }

    @EventListener
    public void addRoutingInformation(ServiceInstructionRoutedEvent event) {
    		

        // todo: this is a dirty little patch so we don't have to refactor to convert all RBAC users to UserId
        // go back and refactor this at a later point
        UserActiveDirectoryId fromADUser = new UserActiveDirectoryId(event.getRouteFromUser().toString());
        UserActiveDirectoryId toADUser = new UserActiveDirectoryId(event.getRoutedToUser().toString());

        Route route = new Route(new User(fromADUser,"",""), new User(toADUser,"",""), event.getServiceInstruction().getStatus().toString() );

        routingInformationRepository.addRoutingInformation(route,
                new RoutingInformationId(event.getServiceInstruction().getServiceInstructionId().toString()));
    
    
    
    }

    @EventListener
    public void addRoutingInformation(TradeServiceRoutedEvent event) {
    
    	
//    	if(event.getTradeServiceStatus() != TradeServiceStatus.APPROVED){

	      	  TradeService tradeService = event.getTradeService();
	    	   	 TradeServiceStatus tradeServiceStatus = event.getTradeServiceStatus();
	    	   	 TaskReferenceNumber taskReferenceNumber = new TaskReferenceNumber(tradeService.getTradeServiceId().toString());
	    	   	 Task getTask = taskRepository.load(taskReferenceNumber);  
	    	   	 System.out.println("Task Latest Status: "+getTask.getTaskStatus().toString() + " AND TradeService Latest Status: "+event.getTradeServiceStatus().toString());
	    	   	


		    	  UserActiveDirectoryId fromADUser = new UserActiveDirectoryId(event.getRoutedFromUser().toString());
		          UserActiveDirectoryId toADUser = new UserActiveDirectoryId(event.getRoutedToUser().toString());

		          Route route = new Route(new User(fromADUser,"",""), new User(toADUser,"",""), event.getTradeServiceStatus().toString() );

		         routingInformationRepository.addRoutingInformation(route,
	            new RoutingInformationId(event.getTradeService().getTradeServiceId().toString()));
		         
		         
		         System.out.println("Success Routes");
//		         throw new IndexOutOfBoundsException("Transaction Failed to route please contact TFS support");
	                


//	    		}

    	
    }
    


}
