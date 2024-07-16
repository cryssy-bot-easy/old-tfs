package com.ucpb.tfs.application.domain.service;

import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.application.query.service.ITradeServiceFinder;
import com.ucpb.tfs.domain.instruction.ServiceInstructionId;
import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.reference.ChargeId;
import com.ucpb.tfs.domain.service.TradeProductNumber;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.service.enumTypes.DocumentClass;
import com.ucpb.tfs.domain.service.enumTypes.DocumentSubType1;
import com.ucpb.tfs.domain.service.enumTypes.DocumentType;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
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
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Map;

/**
 * User: IPCVal
 * Date: 8/16/12
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:unitTestContext.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TransactionConfiguration(transactionManager = "transactionManager")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class TestTradeService {

    @Inject
    private TradeServiceRepository tradeServiceRepository;

    @Autowired
    private ITradeServiceFinder tradeServiceFinder;

    @Test
    public void TestPersistence() throws Exception {

        TradeService tradeService = new TradeService(new ServiceInstructionId(), null, null, null, null, null, null, null, null);
        tradeServiceRepository.persist(tradeService);
        System.out.println("tradeServiceId = " + tradeService.getTradeServiceId().toString());
    }

    @Test
    public void TestLcInquiry() throws Exception {

        List<Map<String, ?>> result = tradeServiceFinder.lcInquiry(null, null, null, null, null, null, null, null, null, null, null);
        System.out.println("result.size() = " + result.size());
    }

    @Test
    public void testServiceCharges() {

        TradeService tradeService = new TradeService(new ServiceInstructionId(), new DocumentNumber("12345"), new TradeProductNumber("12345"), DocumentClass.LC, DocumentType.FOREIGN, DocumentSubType1.CASH, null, ServiceType.OPENING, new UserActiveDirectoryId("nai0797"));

        // add a charge
//        tradeService.addCharge(new ChargeId("BC"), new BigDecimal(500), Currency.getInstance("PHP"));
        tradeService.setProductCharge(new BigDecimal(1000000), Currency.getInstance("USD"));

        // save to the database
//        tradeServiceRepository.saveOrUpdate(tradeService);
        tradeServiceRepository.persist(tradeService);

        TradeServiceId tid = tradeService.getTradeServiceId();

        TradeService ts2 = tradeServiceRepository.load(tid);

//        ts2.updateCharge(new ChargeId("BC"), new BigDecimal(1000), Currency.getInstance("PHP"));
        tradeServiceRepository.persist(ts2);

    }
}
