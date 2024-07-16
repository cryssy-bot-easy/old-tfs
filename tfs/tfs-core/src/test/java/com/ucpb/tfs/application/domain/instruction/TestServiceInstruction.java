package com.ucpb.tfs.application.domain.instruction;

import com.ucpb.tfs.application.query.instruction.IServiceInstructionFinder;
import com.ucpb.tfs.domain.instruction.ServiceInstruction;
import com.ucpb.tfs.domain.instruction.ServiceInstructionId;
import com.ucpb.tfs.domain.instruction.ServiceInstructionRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.Set;
import java.util.Calendar;
import java.util.Iterator;

/**
 * User: IPCVal
 * Date: 8/6/12
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:unitTestContext.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TransactionConfiguration(transactionManager = "transactionManager")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class TestServiceInstruction {

    @Inject
    private ServiceInstructionRepository serviceInstructionRepository;

    @Autowired
    private IServiceInstructionFinder serviceInstructionFinder;

    @Test
    public void TestPersistence() throws Exception {

        ServiceInstruction si = new ServiceInstruction("932");
        // si.generateDocumentNumber();
        serviceInstructionRepository.persist(si);
    }

    @Test
    public void TestHashMapPersistence() {

        Map<String, Object> details = new HashMap<String, Object>();
        Map<String, Object> productDetails = new HashMap <String, Object>();

        ServiceInstructionId sid = new ServiceInstructionId("ETS-1");

        details.put("dataField1", "value of dataField1");
        details.put("name", "Jett");

        ServiceInstruction si = new ServiceInstruction(sid, details, productDetails);

        // saveOrUpdate using the repository
        serviceInstructionRepository.persist(si);

        ServiceInstruction sid2 = serviceInstructionRepository.load(sid);
        Map<String, Object> ret = sid2.getDetails();

        Assert.assertEquals(ret.get("name"), "Jett");
        Assert.assertEquals(ret.get("dataField1"), "value of dataField1");

    }

    @Test
    public void TestEtsInquiry() throws Exception {

        List<Map<String, ?>> result = serviceInstructionFinder.etsInquiry(null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        System.out.println("result.size() = " + result.size());
    }
}
