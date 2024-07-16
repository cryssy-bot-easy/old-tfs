package com.ucpb.tfs.application.domain.product;

import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.product.TradeProduct;
import com.ucpb.tfs.domain.product.TradeProductRepository;
import com.ucpb.tfs.domain.product.enums.ProductType;
import com.ucpb.tfs.domain.product.utils.DocumentNumberGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * User: IPCVal
 * Date: 8/6/12
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:unitTestContext.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TransactionConfiguration(transactionManager = "transactionManager")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class TestTradeProduct {

    @Inject
    private TradeProductRepository tradeProductRepository;

    @Test
    public void TestPersistence() throws Exception {

        TradeProduct lc = new TradeProduct(new DocumentNumber(DocumentNumberGenerator.generateDocumentNumber()), ProductType.LC);
        tradeProductRepository.persist(lc);
    }
}
