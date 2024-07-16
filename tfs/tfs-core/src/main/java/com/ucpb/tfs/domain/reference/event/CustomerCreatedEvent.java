package com.ucpb.tfs.domain.reference.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.sysparams.RefCustomer;

/**
 */
public class CustomerCreatedEvent implements DomainEvent{

    private RefCustomer refCustomer;


    public CustomerCreatedEvent(RefCustomer refCustomer){
        this.refCustomer = refCustomer;
    }

    public RefCustomer getRefCustomer() {
        return refCustomer;
    }
}
