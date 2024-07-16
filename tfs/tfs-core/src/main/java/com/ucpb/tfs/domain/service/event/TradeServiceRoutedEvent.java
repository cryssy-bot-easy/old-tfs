package com.ucpb.tfs.domain.service.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.enumTypes.TradeServiceStatus;

//PROLOGUE:
//* 	(revision)
//SCR/ER Number:20160414-052
//SCR/ER Description: Transaction was approved in TSD, but is found on TSD Makers' screen the next day, with Pending status.
//[Revised by:] Allan Comboy Jr.
//[Date Deployed:] 04/14/2016
//Program [Revision] Details: add task status where status is set if it has already been executed(TSD only)
//PROJECT: CORE
//MEMBER TYPE  : JAVA
//Project Name: TradeServiceRoutedEvent.java

public class TradeServiceRoutedEvent implements DomainEvent {

    private TradeService tradeService;
    private TradeServiceStatus tradeServiceStatus;
    private UserActiveDirectoryId routedFromUser;
    private UserActiveDirectoryId routedToUser;
    private Boolean task = false; 

    public UserActiveDirectoryId getRoutedFromUser() {
        return routedFromUser;
    }

    public TradeServiceRoutedEvent() {
    }

    public TradeServiceRoutedEvent(TradeService tradeService, UserActiveDirectoryId routedToUser) {
        this.tradeService = tradeService;
        this.routedToUser = routedToUser;
    }

    public TradeServiceRoutedEvent(TradeService tradeService, TradeServiceStatus tradeServiceStatus, UserActiveDirectoryId routedToUser) {
        this.tradeService = tradeService;
        this.tradeServiceStatus = tradeServiceStatus;
        this.routedToUser = routedToUser;
    }

    public TradeServiceRoutedEvent(TradeService tradeService, TradeServiceStatus tradeServiceStatus, UserActiveDirectoryId routedFromUser, UserActiveDirectoryId routedToUser) {
        this.tradeService = tradeService;
        this.tradeServiceStatus = tradeServiceStatus;
        this.routedToUser = routedToUser;
        this.routedFromUser = routedFromUser;
   
    }

    public TradeService getTradeService() {
        return tradeService;
    }

    public TradeServiceStatus getTradeServiceStatus() {
        return tradeServiceStatus;
    }

    public UserActiveDirectoryId getRoutedToUser() {
        return routedToUser;
    }
    
    public void setTask(Boolean settask){   	
    	this.task= settask; 
    	System.out.println(task.toString() + "YOOO");
    }
    
    public Boolean getTask(){
    	return task;
   
    }
}
