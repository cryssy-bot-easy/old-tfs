package com.ucpb.tfs2.application.service;

import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ucpb.tfs.domain.reference.event.CountryCreatedEvent;
import com.ucpb.tfs.domain.reference.event.CountryUpdatedEvent;
import com.ucpb.tfs.domain.sysparams.RefCountry;
import com.ucpb.tfs.domain.sysparams.RefCountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * User: IPCVal
 * Date: 1/22/13
 */
@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class RefCountryService {

    @Autowired
    private RefCountryRepository refCountryRepository;

    @Autowired
    private DomainEventPublisher eventPublisher;

    public String saveRefCountryDetail(Map parameters) throws Exception {

        String saveMode  = (String) parameters.get("saveMode");


        RefCountry refCountry = null;


        if (saveMode != null && saveMode.equals("edit")) {
            System.out.println("edit");
            refCountry = refCountryRepository.getCountry((String)parameters.get("countryCode"));

            refCountry.updateDetails(parameters);

            refCountryRepository.merge(refCountry);

            eventPublisher.publish(new CountryUpdatedEvent(refCountry));

        } else if (saveMode != null && saveMode.equals("add")) {
            //System.out.println("add");

            //System.out.println(parameters);
            refCountry = new RefCountry(parameters);

            String countryCode  = (String) parameters.get("countryCode");

            if (countryCode != null && !countryCode.trim().isEmpty()) {

                Long count = refCountryRepository.checkIfCountryExists(countryCode);

                if (count == null || count == 0) {

                    refCountry = refCountryRepository.save(refCountry);

                    eventPublisher.publish(new CountryCreatedEvent(refCountry));
                } else if (count != null && count > 0) {

                    String err = "Cannot add a country code that already exists.";
                    throw new Exception(err);
                }


            } else {

                refCountry = refCountryRepository.save(refCountry);
                eventPublisher.publish(new CountryCreatedEvent(refCountry));
            }
        }

        return refCountry.getCountryCode();
    }
}
