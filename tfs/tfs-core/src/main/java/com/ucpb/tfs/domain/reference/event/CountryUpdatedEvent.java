package com.ucpb.tfs.domain.reference.event;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.sysparams.RefCountry;

/**
 */
public class CountryUpdatedEvent implements DomainEvent{

    private RefCountry refCountry;


    public CountryUpdatedEvent(RefCountry refCountry){
        this.refCountry = refCountry;
    }

    public RefCountry getRefCountry() {
        return refCountry;
    }
}
