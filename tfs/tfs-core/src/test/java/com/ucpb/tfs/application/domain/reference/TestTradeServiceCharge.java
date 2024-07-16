package com.ucpb.tfs.application.domain.reference;

//import com.ucpb.tfs.application.bootstrap.TFSLookupProvider;
import com.ucpb.tfs.domain.reference.ChargeId;
import com.ucpb.tfs.domain.reference.ProductReference;
import com.ucpb.tfs.domain.reference.ProductReferenceRepository;
import com.ucpb.tfs.domain.reference.TradeServiceChargeReference;
import com.ucpb.tfs.domain.reference.infrastructure.repositories.hibernate.HibernateTradeServiceChargeReferenceRepository;
import com.ucpb.tfs.domain.service.enumTypes.DocumentClass;
import com.ucpb.tfs.domain.service.enumTypes.DocumentSubType1;
import com.ucpb.tfs.domain.service.enumTypes.DocumentType;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
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
import com.ucpb.tfs.application.bootstrap.ChargesLookup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: IPCVal
 * Date: 7/30/12
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:unitTestContext.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TransactionConfiguration(transactionManager = "transactionManager")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class TestTradeServiceCharge {

//    @Autowired
//    TFSLookupProvider tfsLookupProvider;

    @Autowired
    ChargesLookup chargesLookup;

    @Autowired
    ProductReferenceRepository productReferenceRepository;

    @Autowired
    HibernateTradeServiceChargeReferenceRepository tradeServiceChargeReferenceRepository;


    @Before
    public void Setup() {

        ProductReference productReference = new ProductReference("LC-FX-CASH", DocumentClass.LC, DocumentType.FOREIGN, DocumentSubType1.CASH, null);
        productReferenceRepository.save(productReference);


        Map<String, ChargeId> chargeIds = new HashMap<String, ChargeId>();
        chargeIds.put("BC", new ChargeId("BC"));
        chargeIds.put("CF", new ChargeId("CF"));
        chargeIds.put("DOCSTAMPS", new ChargeId("DOCSTAMPS"));
        chargeIds.put("CABLE", new ChargeId("CABLE"));
        chargeIds.put("SUP", new ChargeId("SUP"));
        chargeIds.put("CILEX", new ChargeId("CILEX"));
        chargeIds.put("NOTARIAL", new ChargeId("NOTARIAL"));
        chargeIds.put("INTEREST", new ChargeId("INTEREST"));
        chargeIds.put("BOOKING", new ChargeId("BOOKING"));
        chargeIds.put("CORRES-CONFIRMING", new ChargeId("CORRES-CONFIRMING"));
        chargeIds.put("CORRES-ADVISING", new ChargeId("CORRES-ADVISING"));
        chargeIds.put("CANCEL", new ChargeId("CANCEL"));


        // insert charges for Cash FXLC Opening
        ProductReference productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.FOREIGN, DocumentSubType1.CASH, null);

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BC"), productRef.getProductId(), ServiceType.OPENING, "new BigDecimal(amount) * 0.10B"));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("DOCSTAMPS"), productRef.getProductId(), ServiceType.OPENING, "200B"));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CABLE"), productRef.getProductId(), ServiceType.OPENING, "300B"));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("SUP"), productRef.getProductId(), ServiceType.OPENING, "400B"));




    }



    @Test
    public void TestProductCharges() {

        ProductReference productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.FOREIGN, DocumentSubType1.CASH, null);

        System.out.println("looking for charges for: " + productRef.getProductId().toString());

//        List<TradeServiceChargeReference> charges = tfsLookupProvider.getChargesForService(productRef.getProductId(), ServiceType.OPENING);
        List<TradeServiceChargeReference> charges = chargesLookup.getChargesForService(productRef.getProductId(), ServiceType.OPENING);

        if(charges != null) {
            for(TradeServiceChargeReference charge : charges ) {
                System.out.println(charge);
            }
        }
        else {
            System.out.println("no charges found");
        }

    }
}
