package com.ucpb.tfs.domain.reference.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.sysparams.RefCustomer;

/**
 */
public class CustomerUpdatedEvent implements DomainEvent{

    private RefCustomer refCustomer;


    public CustomerUpdatedEvent(RefCustomer refCustomer){
        this.refCustomer = refCustomer;
    }

    public RefCustomer getRefCustomer() {
        return refCustomer;
    }
}
