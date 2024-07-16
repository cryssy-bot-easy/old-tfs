package com.ucpb.tfs.application.service.event;

import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.service.TradeProductNumber;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.enumTypes.DocumentClass;
import com.ucpb.tfs.domain.service.enumTypes.DocumentType;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import com.ucpb.tfs.domain.service.event.charge.FxNonLcSettlementEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

//import com.ucpb.tfs.domain.service.event.charge.DmNonLcSettlementEvent;

/**
 * User: giancarlo
 * Date: 12/20/12
 * Time: 2:49 PM
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:*est-accounting-engine-context.xml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ServiceChargesEventIntegrationTest {


    @Autowired
    DomainEventPublisher eventPublisher;

    @Test
    public void testEvent() {

        Map chargeDetails = new HashMap();
        chargeDetails.put("productAmount", "1000000");
        chargeDetails.put("TR_LOAN_FLAG", "Y");

        Boolean tBoolean;
        System.out.println("Test Start");
        System.out.println(chargeDetails);
        UserActiveDirectoryId userActiveDirectoryId = new UserActiveDirectoryId("angol");
//        ServiceInstruction serviceInstruction = new ServiceInstruction();
        TradeService tradeService = new TradeService(new DocumentNumber("12345"), new TradeProductNumber("12345"), DocumentClass.DA, DocumentType.FOREIGN, null, null, ServiceType.SETTLEMENT, userActiveDirectoryId, "12345");
        tradeService.setDetails(chargeDetails);
//        serviceInstruction.updateDetails(chargeDetails,userActiveDirectoryId);

//        FxNonLcSettlementEvent fxNonLcSettlementEvent = new FxNonLcSettlementEvent();
        FxNonLcSettlementEvent fxNonLcSettlementEvent = new FxNonLcSettlementEvent(tradeService, chargeDetails);
//        FxNonLcSettlementEvent fxNonLcSettlementEvent = new FxNonLcSettlementEvent(tradeService,serviceInstruction,userActiveDirectoryId,chargeDetails);
        //tBoolean = FxNonLcSettlementEvent.determineCompleteness(chargeDetails);
        eventPublisher.publish(fxNonLcSettlementEvent);

//        tradeService = new TradeService(new DocumentNumber("12345"), DocumentClass.DA, DocumentType.FOREIGN, null, null, ServiceType.SETTLEMENT, userActiveDirectoryId, "12345");
//        tradeService.setDetails(chargeDetails);

//        DmNonLcSettlementEvent dmNonLcSettlementEvent = new DmNonLcSettlementEvent(tradeService, chargeDetails);
//        DmNonLcSettlementEvent dmNonLcSettlementEvent = new DmNonLcSettlementEvent(tradeService,serviceInstruction,userActiveDirectoryId,chargeDetails);
        //tBoolean = DmNonLcSettlementEvent.determineCompleteness(chargeDetails);
//        eventPublisher.publish(dmNonLcSettlementEvent);


        System.out.println("Test End");

    }

}
