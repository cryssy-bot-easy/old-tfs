package com.ucpb.tfs2.application.service;

import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ucpb.tfs.domain.reference.event.CustomerCreatedEvent;
import com.ucpb.tfs.domain.reference.event.CustomerUpdatedEvent;
import com.ucpb.tfs.domain.sysparams.RefCustomer;
import com.ucpb.tfs.domain.sysparams.RefCustomerRepository;
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
public class RefCustomerService {

    @Autowired
    private RefCustomerRepository refCustomerRepository;

    @Autowired
    private DomainEventPublisher eventPublisher;

    public Long saveRefCustomerDetail(Map parameters) throws Exception {

        String saveMode  = (String) parameters.get("saveMode");

        /*System.out.println("\n###### SAVEMODE = " + saveMode + "\n");*/

        RefCustomer refCustomer = null;

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date clientBirthday = null;
        String nationOfBirth = null;
        String country=null;

        if (parameters.get("clientType").toString().equals("1")) {

            if (parameters.get("dateOfBirth") != null && !((String)parameters.get("dateOfBirth")).isEmpty()) {
                clientBirthday = dateFormat.parse((String)parameters.get("dateOfBirth"));
            }

            if (parameters.get("nationOfBirth") != null && !((String)parameters.get("nationOfBirth")).isEmpty()) {
                if(((String) parameters.get("nationOfBirth")).length()>4)
                    nationOfBirth = ((String) parameters.get("nationOfBirth")).substring(0,3);
            }

        } else {

            if (parameters.get("country") != null && !((String)parameters.get("country")).isEmpty()) {
                if(((String) parameters.get("country")).length()>4)
                    country = ((String) parameters.get("country")).substring(0,3);
            }

            if (parameters.get("dateOfIncorporation") != null && !((String)parameters.get("dateOfIncorporation")).isEmpty()) {
                clientBirthday = dateFormat.parse((String)parameters.get("dateOfIncorporation"));
            }
        }
        parameters.put("nationOfBirth",nationOfBirth);
        parameters.put("clientBirthday", clientBirthday);
        parameters.put("country",country);

        if (saveMode != null && saveMode.equals("edit")) {

            refCustomer = refCustomerRepository.getCustomer(new Long((String)parameters.get("customerId")));

            refCustomer.updateDetails(parameters);

            refCustomerRepository.merge(refCustomer);

            eventPublisher.publish(new CustomerUpdatedEvent(refCustomer));

        } else if (saveMode != null && saveMode.equals("add")) {

            refCustomer = new RefCustomer(parameters);

            String centralBankCode  = (String) parameters.get("centralBankCode");

            if (centralBankCode != null && !centralBankCode.trim().isEmpty()) {

                Long count = refCustomerRepository.checkIfCustomerExists(centralBankCode);

                if (count == null || count == 0) {

                    refCustomer = refCustomerRepository.save(refCustomer);

                    eventPublisher.publish(new CustomerCreatedEvent(refCustomer));
                } else if (count != null && count > 0) {

                    String err = "Cannot add a CB code that already exists.";
                    throw new Exception(err);
                }

            } else {

                refCustomer = refCustomerRepository.save(refCustomer);
                eventPublisher.publish(new CustomerCreatedEvent(refCustomer));
            }
        }

        return refCustomer.getCustomerId();
    }

    public void deleteRefCustomerDetail(Map parameters) throws Exception {

        RefCustomer refCustomer = null;
        refCustomer = refCustomerRepository.getCustomer(new Long((String)parameters.get("customerId")));
        //refCustomer.deleteDetails(parameters);
        refCustomerRepository.delete(refCustomer);
        //eventPublisher.publish(new CustomerUpdatedEvent(refCustomer));
    }
}
