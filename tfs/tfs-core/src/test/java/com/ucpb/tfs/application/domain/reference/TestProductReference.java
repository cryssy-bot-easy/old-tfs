package com.ucpb.tfs.application.domain.reference;

import com.ucpb.tfs.domain.reference.ProductReference;
import com.ucpb.tfs.domain.reference.ProductReferenceRepository;
import com.ucpb.tfs.domain.service.enumTypes.DocumentClass;
import com.ucpb.tfs.domain.service.enumTypes.DocumentSubType1;
import com.ucpb.tfs.domain.service.enumTypes.DocumentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:unitTestContext.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TransactionConfiguration(transactionManager = "transactionManager")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class TestProductReference {

    @Autowired
    ProductReferenceRepository productReferenceRepository;


    @Before
    public void Setup() {

        ProductReference productReference = new ProductReference("LC-FX-CASH", DocumentClass.LC, DocumentType.FOREIGN, DocumentSubType1.CASH, null);
        productReferenceRepository.save(productReference);


    }

    @Test
    public void testPersistence() {

        ProductReference productReference = productReferenceRepository.find(DocumentClass.LC, DocumentType.FOREIGN, DocumentSubType1.CASH, null);
        System.out.println(productReference.getProductId());

        System.out.println(productReferenceRepository.getCount());

    }

}
