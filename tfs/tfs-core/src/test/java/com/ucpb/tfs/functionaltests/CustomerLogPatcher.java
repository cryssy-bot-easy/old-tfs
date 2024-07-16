package com.ucpb.tfs.functionaltests;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.audit.CustomerLog;
import com.ucpb.tfs.domain.reference.event.CustomerCreatedEvent;
import com.ucpb.tfs.domain.service.event.AmlaInformationLogger;
import com.ucpb.tfs.domain.sysparams.RefCustomer;
import com.ucpb.tfs.domain.sysparams.RefCustomerRepository;
import com.ucpb.tfs.utils.BeanMapper;
import org.hibernate.SessionFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Not a test class. As the name indicates, this class is used to populate the customer log table
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration
@ContextConfiguration("classpath:amla-report-integration-tests.xml")
public class CustomerLogPatcher {

    @Autowired
    private RefCustomerRepository refCustomerRepository;

    @Autowired
    private AmlaInformationLogger amlaInformationLogger;

    @Resource(name = "customerLogMappers")
    private Map<Class<? extends DomainEvent>,BeanMapper> customerMappers;

    @Autowired
    private SessionFactory sessionFactory;


    @Test
    @Rollback(false)
    public void regenerateCustomerLog(){
        List<RefCustomer> customers = refCustomerRepository.getAllCustomers();
        System.out.println("****** CUSTOMERS: " + customers.size());
        for(RefCustomer customer : refCustomerRepository.getAllCustomers()){
            CustomerCreatedEvent event = new CustomerCreatedEvent(customer);
            try{
                amlaInformationLogger.logCustomerCreatedEvent(event);
            }catch (SpelEvaluationException e){
                throw new RuntimeException("Error mapping customer id:" +  customer.getCustomerId());
            }
        }
        sessionFactory.getCurrentSession().flush();
    }


    @Ignore("Not a test case. Used to single out specific erroneous records for easy debugging")
    @Test
    public void mapCustomerToLog(){
        BeanMapper customerMapper = customerMappers.get("com.ucpb.tfs.domain.reference.event.CustomerCreatedEvent");
        RefCustomer customer = refCustomerRepository.getCustomer(Long.valueOf(1));
        CustomerLog customerLog = (CustomerLog) customerMapper.map(new CustomerCreatedEvent(customer));

    }
}
