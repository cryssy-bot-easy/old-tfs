package com.ucpb.tfs.domain.instruction.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.domain.instruction.ServiceInstruction;
import com.ucpb.tfs.domain.instruction.enumTypes.ServiceInstructionStatus;
import com.ucpb.tfs.domain.security.UserId;
import com.ucpb.tfs.domain.service.TradeServiceId;
import org.springframework.stereotype.Component;

/**
 * User: IPCVal
 * Date: 8/14/12
 */
@Component
public class ServiceInstructionRoutedEvent implements DomainEvent {

    private ServiceInstruction serviceInstruction;
    private ServiceInstructionStatus serviceInstructionStatus;
    private UserActiveDirectoryId routedToUser;
    private UserActiveDirectoryId routeFromUser;
    private TradeServiceId tradeServiceId;

    public ServiceInstructionRoutedEvent() {}

    public ServiceInstructionRoutedEvent(ServiceInstruction serviceInstruction, UserActiveDirectoryId routedToUser) {
        this.serviceInstruction = serviceInstruction;
        this.routedToUser = routedToUser;
    }

    public ServiceInstructionRoutedEvent(ServiceInstruction serviceInstruction, ServiceInstructionStatus serviceInstructionStatus, UserActiveDirectoryId routedToUser) {
        this.serviceInstruction = serviceInstruction;
        this.serviceInstructionStatus = serviceInstructionStatus;
        this.routedToUser = routedToUser;
    }

    public ServiceInstructionRoutedEvent(ServiceInstruction serviceInstruction, ServiceInstructionStatus serviceInstructionStatus, UserActiveDirectoryId routeFromUser, UserActiveDirectoryId routedToUser) {
        this.serviceInstruction = serviceInstruction;
        this.serviceInstructionStatus = serviceInstructionStatus;
        this.routedToUser = routedToUser;
        this.routeFromUser = routeFromUser;
    }

    // another constructor to be used when SI is approved
    public ServiceInstructionRoutedEvent(ServiceInstruction serviceInstruction, ServiceInstructionStatus serviceInstructionStatus, UserActiveDirectoryId routedToUser, TradeServiceId tradeServiceId) {
        this.serviceInstruction = serviceInstruction;
        this.serviceInstructionStatus = serviceInstructionStatus;
        this.routedToUser = routedToUser;
        this.tradeServiceId = tradeServiceId;
    }

    // another constructor to be used when SI is approved. this one has the routeFrom User
    public ServiceInstructionRoutedEvent(ServiceInstruction serviceInstruction, ServiceInstructionStatus serviceInstructionStatus, UserActiveDirectoryId routeFromUser, UserActiveDirectoryId routedToUser, TradeServiceId tradeServiceId) {
        this.serviceInstruction = serviceInstruction;
        this.serviceInstructionStatus = serviceInstructionStatus;
        this.routedToUser = routedToUser;
        this.tradeServiceId = tradeServiceId;
        this.routeFromUser = routeFromUser;
    }

    public ServiceInstruction getServiceInstruction() {
        return serviceInstruction;
    }

    public UserActiveDirectoryId getRouteFromUser() {
        return routeFromUser;
    }

    public UserActiveDirectoryId getRoutedToUser() {

        return routedToUser;
    }

    public ServiceInstructionStatus getServiceInstructionStatus() {
        return serviceInstructionStatus;
    }

    public UserActiveDirectoryId getUserActiveDirectoryId() {
        return routedToUser;
    }

    public TradeServiceId getTradeServiceId() {
        return tradeServiceId;
    }
}
