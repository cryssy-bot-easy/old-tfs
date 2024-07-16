package com.ucpb.tfs.functionaltests;

import com.incuventure.ddd.domain.DomainEvent;
import com.ucpb.tfs.domain.audit.AccountLog;
import com.ucpb.tfs.domain.audit.TransactionLog;
import com.ucpb.tfs.domain.product.*;
import com.ucpb.tfs.domain.product.event.*;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.utils.BeanMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:amla/accountlog-mapping.xml","classpath*:amla/customer-accountlog-mapping.xml","classpath*:amla/transactionlog-mapping.xml"})
public class AmlaMappingIntegrationTest {

    @Resource(name = "accountLogMappers")
    private Map<Class<? extends DomainEvent>,BeanMapper> accountLogMappers;

    @Resource(name = "transactionLogMappers")
    private Map<Class<? extends DomainEvent>,BeanMapper> transactionLogMappers;


    @Resource(name = "customerAccountLogMappers")
    private Map<Class<? extends DomainEvent>,BeanMapper> customerAccountMappers;



    @Test
    public void successfullyMapDpCancelledEventToTransactionLog(){
        BeanMapper mapper = transactionLogMappers.get("com.ucpb.tfs.domain.product.event.DPCancelledEvent");
        assertNotNull(mapper,"Event mapping has not been configured!");
        TradeService ts = new TradeService();
        Map<String,Object> details = asMap(
                "date","value",
                "processingUnitCode","909",
                "processDate","12/09/2013",
                "currency","PHP",
                "amount","1214914.19",
                "dateOfBlAirwayBill","12/12/2013",
                "maturityDate","12/12/2013",
                "documentType","FOREIGN",
                "currentRate",new BigDecimal("1213414.194")
        );
        ts.setDetails(details);

        Map<String,Object> dpDetails = asMap(
                "currency","USD",
                "amount","121314.19",
                "beneficiaryName","beneficiary name",
                "originalPort","originalPort",
                "maturityDate","12/01/2014"

        );


        DocumentAgainstPayment dp = new DocumentAgainstPayment();
        dp.updateDetails(dpDetails);

        DPCancelledEvent dpCancelledEvent = new DPCancelledEvent(ts,dp,"");
        TransactionLog log = (TransactionLog) mapper.map(dpCancelledEvent);


        assertEquals(Currency.getInstance("USD"),log.getSettlementCurrency());
    }

    @Test
    public void successfullyMapLcOpeningToTransactionLog(){
        BeanMapper mapper = transactionLogMappers.get("com.ucpb.tfs.domain.product.event.LetterOfCreditCreatedEvent");
        assertNotNull(mapper,"Event mapping has not been configured!");
        TradeService ts = new TradeService();
        Map<String,Object> details = asMap(
                "date","value",
                "processingUnitCode","909",
                "processDate","12/09/2013",
                "currency","PHP",
                "amount","1214914.19",
                "dateOfBlAirwayBill","12/12/2013",
                "maturityDate","12/12/2013",
                "documentType","FOREIGN",
                "currentRate",new BigDecimal("1213414.194") ,
                "beneficiaryName", "beneficiary name"
        );
        ts.setDetails(details);

        Map<String,Object> lcDetails = asMap(
                "currency","USD",
                "amount","121314.19",
                "originalPort","originalPort",
                "maturityDate","12/01/2014"

        );

        LetterOfCredit lc = new LetterOfCredit();
        lc.updateDetails(lcDetails);

        TransactionLog log = (TransactionLog) mapper.map(new LetterOfCreditCreatedEvent(ts,lc,""));
        assertEquals("beneficiary name",log.getCounterparty().getName1());
    }

    @Test
    public void successfullyMapDaCancelledEventToTransactionLog(){
        BeanMapper mapper = transactionLogMappers.get("com.ucpb.tfs.domain.product.event.DACancelledEvent");
        assertNotNull(mapper,"Event mapping has not been configured!");
        TradeService ts = new TradeService();
        Map<String,Object> details = asMap(
                "date","value",
                "processingUnitCode","909",
                "processDate","12/09/2013",
                "currency","PHP",
                "amount","1214914.19",
                "dateOfBlAirwayBill","12/12/2013",
                "maturityDate","12/12/2013",
                "documentType","FOREIGN",
                "currentRate",new BigDecimal("1213414.194")
        );
        ts.setDetails(details);

        Map<String,Object> daDetails = asMap(
                "currency","USD",
                "amount","121314.19",
                "beneficiaryName","beneficiary name",
                "originalPort","originalPort",
                "maturityDate","12/01/2014"

        );

        DocumentAgainstAcceptance da = new DocumentAgainstAcceptance();
        da.updateDetails(daDetails);

        DACancelledEvent daCancelledEvent = new DACancelledEvent(ts,da,"");

        TransactionLog log = (TransactionLog) mapper.map(daCancelledEvent);
        assertEquals(Currency.getInstance("USD"),log.getSettlementCurrency());
    }

    @Test
    public void mapDrCancelledEventToTransactionLog(){
        BeanMapper mapper = transactionLogMappers.get("com.ucpb.tfs.domain.product.event.DRCancelledEvent");
        assertNotNull(mapper,"Event mapping has not been configured!");
        TradeService ts = new TradeService();
        Map<String,Object> details = asMap(
                "date","value",
                "processingUnitCode","909",
                "processDate","12/09/2013",
                "currency","PHP",
                "amount","1214914.19",
                "dateOfBlAirwayBill","12/12/2013",
                "maturityDate","12/12/2013",
                "documentType","FOREIGN",
                "currentRate",new BigDecimal("1213414.194")
        );
        ts.setDetails(details);

        Map<String,Object> drDetails = asMap(
                "currency","USD",
                "amount","121314.19",
                "beneficiaryName","beneficiary name",
                "originalPort","originalPort",
                "maturityDate","12/01/2014"

        );

        DirectRemittance directRemittance = new DirectRemittance();
        directRemittance.updateDetails(drDetails);
        assertEquals(Currency.getInstance("USD"),directRemittance.getCurrency());
        DRCancelledEvent drCancelledEvent = new DRCancelledEvent(ts,directRemittance,"");

        TransactionLog log = (TransactionLog) mapper.map(drCancelledEvent);
        assertEquals(Currency.getInstance("USD"),log.getSettlementCurrency());
    }

    @Test
    public void mapOaCancelledEventToTransactionLog(){
        BeanMapper mapper = transactionLogMappers.get("com.ucpb.tfs.domain.product.event.OACancelledEvent");
        assertNotNull(mapper,"Event mapping has not been configured!");
        TradeService ts = new TradeService();
        Map<String,Object> details = asMap(
                "date","value",
                "processingUnitCode","909",
                "processDate","12/09/2013",
                "currency","PHP",
                "amount","1214914.19",
                "dateOfBlAirwayBill","12/12/2013",
                "maturityDate","12/12/2013",
                "documentType","FOREIGN",
                "currentRate",new BigDecimal("1213414.194")
        );
        ts.setDetails(details);

        Map<String,Object> oaDetails = asMap(
                "currency","USD",
                "amount","121314.19",
                "beneficiaryName","beneficiary name",
                "originalPort","originalPort",
                "maturityDate","12/01/2014"

        );

        OpenAccount oa = new OpenAccount();
        oa.updateDetails(oaDetails);

        OACancelledEvent oaCancelledEvent = new OACancelledEvent(ts,oa,"");
        TransactionLog log = (TransactionLog) mapper.map(oaCancelledEvent);
        assertEquals(Currency.getInstance("USD"),log.getSettlementCurrency());

    }


    @Test
    public void successfullyMapDaCreatedEventToAccountLog(){
        BeanMapper mapper = accountLogMappers.get("com.ucpb.tfs.domain.product.event.DACreatedEvent");
        assertNotNull(mapper,"Event mapping has not been configured!");
        TradeService ts = new TradeService();
        Map<String,Object> details = asMap(
                "date","value",
                "processingUnitCode","909",
                "processDate","12/09/2013",
                "currency","USD",
                "amount","1214914.19",
                "dateOfBlAirwayBill","12/12/2013",
                "maturityDate","12/12/2013",
                "documentType","FOREIGN"
        );
        ts.setDetails(details);
        DACreatedEvent event = new DACreatedEvent(ts,"");



        AccountLog accountLog = (AccountLog) mapper.map(event);
        assertEquals("TFSS1",accountLog.getAccountType());
    }

    @Test
    public void successfullyMapDaCancelledEventToAccountLog(){
        BeanMapper mapper = accountLogMappers.get("com.ucpb.tfs.domain.product.event.DACancelledEvent");
        assertNotNull(mapper,"Event mapping has not been configured!");
        TradeService ts = new TradeService();
        Map<String,Object> cancellationDetails = asMap(
                "date","value",
                "processingUnitCode","909",
                "processDate","12/09/2013",
                "currency","USD",
                "amount","1214914.19",
                "dateOfBlAirwayBill","12/12/2013",
                "maturityDate","12/12/2013",
                "documentType","FOREIGN"
        );
        ts.setDetails(cancellationDetails);

        Map<String,Object> creationDetails = asMap(
                "date","value",
                "processingUnitCode","909",
                "processDate","11/08/2012",
                "currency","USD",
                "amount","1214914.19",
                "dateOfBlAirwayBill","12/12/2013",
                "maturityDate","12/12/2013",
                "documentType","FOREIGN"
        );

        DocumentAgainstAcceptance da = new DocumentAgainstAcceptance(new DocumentNumber("document-number"),creationDetails);
        DACancelledEvent daCancelledEvent = new DACancelledEvent(ts,da,"");

        AccountLog accountLog = (AccountLog) mapper.map(daCancelledEvent);
        assertNotNull(accountLog.getOpeningDate());
        assertEquals(new Date("11/08/2012"),accountLog.getOpeningDate());
    }

    @Test
    public void successfullyMapDpCancelledEventToAccountLog(){
        BeanMapper mapper = accountLogMappers.get("com.ucpb.tfs.domain.product.event.DPCancelledEvent");
        assertNotNull(mapper,"Event mapping has not been configured!");
        TradeService ts = new TradeService();
        Map<String,Object> details = asMap(
                "date","value",
                "processingUnitCode","909",
                "processDate","12/09/2013",
                "currency","USD",
                "amount","1214914.19",
                "dateOfBlAirwayBill","12/12/2013",
                "maturityDate","12/12/2013",
                "documentType","FOREIGN"
        );
        ts.setDetails(details);

        Map<String,Object> creationDetails = asMap(
                "date","value",
                "processingUnitCode","909",
                "processDate","11/08/2012",
                "currency","USD",
                "amount","1214914.19",
                "dateOfBlAirwayBill","12/12/2013",
                "maturityDate","12/12/2013",
                "documentType","FOREIGN"
        );

        DocumentAgainstPayment dp = new DocumentAgainstPayment(new DocumentNumber("document-number"),creationDetails);
        DPCancelledEvent dpCancelledEvent = new DPCancelledEvent(ts,dp,"");

        AccountLog accountLog = (AccountLog) mapper.map(dpCancelledEvent);
        assertNotNull(accountLog.getOpeningDate());
        assertEquals(new Date("11/08/2012"),accountLog.getOpeningDate());

    }

    @Test
    public void successfullyMapOaCancelledEventToAccountLog(){
        BeanMapper mapper = accountLogMappers.get("com.ucpb.tfs.domain.product.event.OACancelledEvent");
        assertNotNull(mapper,"Event mapping has not been configured!");
        TradeService ts = new TradeService();
        Map<String,Object> details = asMap(
                "date","value",
                "processingUnitCode","909",
                "processDate","12/09/2013",
                "currency","USD",
                "amount","1214914.19",
                "dateOfBlAirwayBill","12/12/2013",
                "maturityDate","12/12/2013",
                "documentType","FOREIGN"
        );
        ts.setDetails(details);

        Map<String,Object> creationDetails = asMap(
                "date","value",
                "processingUnitCode","909",
                "processDate","11/08/2012",
                "currency","USD",
                "amount","1214914.19",
                "dateOfBlAirwayBill","12/12/2013",
                "maturityDate","12/12/2013",
                "documentType","FOREIGN"
        );

        OpenAccount oa = new OpenAccount(new DocumentNumber("document-number"),creationDetails);
        OACancelledEvent oaCancelledEvent = new OACancelledEvent(ts,oa,"");

        AccountLog accountLog = (AccountLog) mapper.map(oaCancelledEvent);
        assertNotNull(accountLog.getOpeningDate());
        assertEquals(new Date("11/08/2012"),accountLog.getOpeningDate());

    }

    @Test
    public void successfullyMapDrCancelledEventToAccountLog(){
        BeanMapper mapper = accountLogMappers.get("com.ucpb.tfs.domain.product.event.DRCancelledEvent");
        assertNotNull(mapper,"Event mapping has not been configured!");
        TradeService ts = new TradeService();
        Map<String,Object> details = asMap(
                "date","value",
                "processingUnitCode","909",
                "processDate","12/09/2013",
                "currency","USD",
                "amount","1214914.19",
                "dateOfBlAirwayBill","12/12/2013",
                "maturityDate","12/12/2013",
                "documentType","FOREIGN"
        );
        ts.setDetails(details);

        Map<String,Object> creationDetails = asMap(
                "date","value",
                "processingUnitCode","909",
                "processDate","11/08/2012",
                "currency","USD",
                "amount","1214914.19",
                "dateOfBlAirwayBill","12/12/2013",
                "maturityDate","12/12/2013",
                "documentType","FOREIGN"
        );

        DirectRemittance dr = new DirectRemittance(new DocumentNumber("document-number"),creationDetails);
        DRCancelledEvent drCancelledEvent = new DRCancelledEvent(ts,dr,"");

        AccountLog accountLog = (AccountLog) mapper.map(drCancelledEvent);
        assertNotNull(accountLog.getOpeningDate());
        assertEquals(new Date("11/08/2012"),accountLog.getOpeningDate());
    }

    private Map<String,Object> asMap(Object... input){
        Assert.isTrue(input.length % 2 == 0, "Input is invalid. Length is uneven");

        Map<String,Object> map = new HashMap<String,Object>();
        for(int ctr = 0; ctr < input.length; ctr = ctr + 2){
            map.put(input[ctr].toString(),input[ctr+1]);
        }
        return map;
    }
}
