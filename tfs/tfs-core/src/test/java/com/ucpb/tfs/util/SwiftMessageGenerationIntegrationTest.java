package com.ucpb.tfs.util;

import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.swift.message.builder.SwiftMessageBuilder;
import com.ucpb.tfs.swift.message.writer.SwiftMessageWriter;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 */
@ContextConfiguration("classpath:*transactionlog-app-context.xml")
@TransactionConfiguration(defaultRollback = false)
public class SwiftMessageGenerationIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private TradeServiceRepository tradeServiceRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SwiftMessageBuilder swiftMessageBuilder;


    @Before
    public void setup(){
        jdbcTemplate.update("INSERT INTO TRADESERVICE (TRADESERVICEID,SERVICEINSTRUCTIONID,TRADESERVICEREFERENCENUMBER,DOCUMENTNUMBER,TRADEPRODUCTNUMBER,STATUS,PROCESSID,DETAILS,CHARGESCURRENCY,APPROVERS,AMOUNT,DEFAULTAMOUNT,CURRENCY,CIFNUMBER,CIFNAME,MAINCIFNUMBER,MAINCIFNAME,FACILITYID,FACILITYTYPE,ACCOUNTOFFICER,SERVICETYPE,DOCUMENTTYPE,DOCUMENTCLASS,DOCUMENTSUBTYPE1,DOCUMENTSUBTYPE2,USERACTIVEDIRECTORYID,LASTUSER,PREPAREDBY,CREATEDDATE,MODIFIEDDATE,NARRATIVE,REASONFORCANCELLATION,PROCESSINGUNITCODE,CCBDBRANCHUNITCODE,ALLOCATIONUNITCODE,PASSONRATETHIRDTOUSDSERVICECHARGE,PASSONRATEUSDTOPHPSERVICECHARGE,PASSONRATETHIRDTOPHPSERVICECHARGE,PASSONURRSERVICECHARGE,SPECIALRATETHIRDTOUSDSERVICECHARGE,SPECIALRATEUSDTOPHPSERVICECHARGE,SPECIALRATETHIRDTOPHPSERVICECHARGE,SPECIALRATEURRSERVICECHARGE,PAYMENTSTATUS,REINSTATEFLAG,CREATEDBY) VALUES ('e8f14cd1-cf50-448d-8364-3db8ea508a80',null,'909-12-00180','909-08-928-12-00001-8','909-08-928-12-00001-8','POST_APPROVED',null,'{\"facilityType\":\"\",\"beneficiaryName\":\"Redstar Presents\",\"dateOfBlAirwayBill\":\"10/12/2012\",\"THB-PHP\":\"1.4220930\",\"SAR-USD_urr\":\"0.2666490\",\"LYD-PHP\":\"34.4245330\",\"documentClass\":\"DR\",\"JPY-PHP\":\"0.5544980\",\"instructionAction\":\"\",\"username\":\"tsdtst4\",\"routeTo\":\"tsdtst4\",\"processingUnitCode\":\"909\",\"statusAction\":\"postApprove\",\"SEK-PHP_urr\":\"6.3258740\",\"processDate\":\"10/26/2012\",\"SGD-USD\":\"0.7777600\",\"SEK-USD_urr\":\"0.1434490\",\"EUR-USD_urr\":\"1.4843500\",\"form\":\"instructionsAndRouting\",\"BND-USD_urr\":\"0.7155120\",\"DKK-USD\":\"0.1595730\",\"NOK-USD\":\"0.1633870\",\"EUR-PHP\":\"54.5141130\",\"CHF-PHP_urr\":\"44.0391840\",\"serviceType\":\"Negotiation\",\"facilityId\":\"\",\"NOK-USD_urr\":\"0.1780500\",\"INR-USD_urr\":\"0.0216497\",\"tsNumber\":\"\",\"AUD-PHP_urr\":\"43.4792010\",\"AUD-USD_urr\":\"0.9073500\",\"creationExchangeRateUsdToPHPUrr\":\"42.260000\",\"importerAddress\":\"LIANA\\u0027S COMPOUND, DR. A.\\r\\nSANTOS AVE., (SUCAT ROAD)\\r\\n\\r\\n\",\"HKD-USD_urr\":\"0.1290305\",\"CAD-USD\":\"0.9573240\",\"creationExchangeRateUsdToPHPSpecialRate\":\"42.470000\",\"PHP-PHP_urr\":\"1.0000000\",\"SEK-PHP\":\"6.6798950\",\"SAR-USD\":\"0.2533200\",\"accountOfficer\":\"MILAGROS ALCABAO\",\"mainCifName\":\"SAN PABLO MANUFACTUR\",\"userrole\":\"TSDO\",\"CHF-PHP\":\"46.5037550\",\"USD-PHP\":\"42.4700000\",\"creationExchangeRate\":\"54.514113\",\"AUD-USD\":\"1.0031290\",\"mainCifNumber\":\"S040279\",\"senderToReceiverInformation\":\"Sender to receiver information FX-DR-001\",\"currency\":\"EUR\",\"tradeServiceId\":\"e8f14cd1-cf50-448d-8364-3db8ea508a80\",\"GBP-USD_urr\":\"1.5907000\",\"EUR-PHP_urr\":\"52.8841640\",\"JPY-USD\":\"0.0124100\",\"USD-PHP_urr\":\"42.2600000\",\"CAD-PHP\":\"44.9689950\",\"NZD-USD\":\"0.7588600\",\"DKK-PHP\":\"7.4957250\",\"THB-PHP_urr\":\"1.3467160\",\"CHF-USD_urr\":\"0.9780430\",\"unitcode\":\"909\",\"maturityDate\":\"\",\"INR-PHP\":\"0.8021030\",\"USD-USD\":\"1.0000000\",\"JPY-USD_urr\":\"0.0111440\",\"SGD-PHP\":\"34.7499130\",\"SGD-USD_urr\":\"0.7156400\",\"documentSubType2\":\"\",\"documentSubType1\":\"\",\"etsNumber\":\"\",\"THB-USD\":\"0.0302740\",\"LYD-USD\":\"0.7704770\",\"GBP-USD\":\"1.5393300\",\"documentType\":\"FOREIGN\",\"EUR-USD\":\"1.2201150\",\"address1\":\"16TH FLOOR UCPB BLDG\",\"address2\":\"MAKATI AVENUE\",\"HKD-PHP\":\"5.5890900\",\"amount\":\"1400250.00\",\"cifNumber\":\"S040279\",\"BND-USD\":\"0.7577870\",\"documentNumber\":\"909-08-928-12-00001-8\",\"MYR-PHP_urr\":\"13.5253640\",\"BND-PHP\":\"35.5960450\",\"BHD-USD_urr\":\"2.6525550\",\"SGD-PHP_urr\":\"33.7109120\",\"originalPort_bspCode\":\"245\",\"NOK-PHP_urr\":\"7.2681620\",\"LYD-PHP_urr\":\"33.3952340\",\"importerName\":\"INT\\u0027L. ARTWORKS, INC\",\"DKK-USD_urr\":\"0.1994180\",\"PHP-PHP\":\"1.0000000\",\"CHF-USD\":\"0.9899960\",\"SEK-USD\":\"0.1422050\",\"currentRate\":\"1\",\"cifName\":\"SAN PABLO MANUFACTUR\",\"urr\":\"42.260000\",\"USD-USD_urr\":\"1.0000000\",\"DKK-PHP_urr\":\"7.0984630\",\"CAD-PHP_urr\":\"42.5857820\",\"NZD-USD_urr\":\"0.7365500\",\"CAD-USD_urr\":\"0.9692270\",\"MYR-USD\":\"0.2774380\",\"BHD-PHP\":\"118.3702780\",\"paymentDetails\":\"\",\"GBP-PHP_urr\":\"66.7200880\",\"referenceType\":\"DATA_ENTRY\",\"NZD-PHP\":\"35.6464500\",\"importerCbCode\":\"459610015\",\"remittingBankReferenceNumber\":\"\",\"HKD-PHP_urr\":\"5.4485440\",\"importerCifNumber\":\"\",\"BHD-PHP_urr\":\"112.0969790\",\"ccbdBranchUnitCode\":\"928\",\"MYR-PHP\":\"13.9422100\",\"NZD-PHP_urr\":\"33.7572880\",\"GBP-PHP\":\"68.7764750\",\"INR-PHP_urr\":\"0.7595940\",\"beneficiaryAddress\":\"B 5-8 Plaza Mont Kiara\\r\\\\nMont Kiara 50480\\r\\\\nKuala Lumpur, Malaysia\",\"INR-USD\":\"0.0170760\",\"THB-USD_urr\":\"0.0300436\",\"remittingBank\":\"\",\"HKD-USD\":\"0.1263510\",\"originalPort\":\"MY\",\"SAR-PHP_urr\":\"11.2687300\",\"longName\":\"SAN PABLO MANUFACTUR\",\"cifCbComplete\":\"true\",\"BND-PHP_urr\":\"33.7095680\",\"BHD-USD\":\"2.5199270\",\"NOK-PHP\":\"7.6749050\",\"JPY-PHP_urr\":\"0.5378980\"}',null,'',1400250.00,1400250.00,'EUR','S040279','SAN PABLO MANUFACTUR','S040279','SAN PABLO MANUFACTUR','','','MILAGROS ALCABAO','NEGOTIATION','FOREIGN','DR',null,null,'NA','tsdtst4','tsdtst8',{ts '2012-10-26 12:26:10'},{ts '2012-10-26 12:38:01'},null,null,'909','928',null,null,null,null,null,null,null,null,null,'NO_PAYMENT_REQUIRED',null,'tsdtst8')");

    }


    @Test
    public void successfullyFormatSwiftLocation(){
        TradeService ts = tradeServiceRepository.load(new TradeServiceId("e8f14cd1-cf50-448d-8364-3db8ea508a80"));
        assertNotNull(ts);
        Map<String,Object> details = ts.getDetails();

        String result = SwiftUtil.formatSwiftLocation("7989456123","Redstar Presents",(String)details.get("beneficiaryAddress"));

        assertEquals("/7989456123\r\n" +
                "Redstar Presents\r\n" +
                "B 5-8 Plaza Mont Kiara Mont Kiara \r\n" +
                "50480 Kuala Lumpur, Malaysia",result);


        List<RawSwiftMessage> messages = swiftMessageBuilder.build("103",ts);
        assertEquals(1,messages.size());

        RawSwiftMessage mt103 = messages.get(0);
        assertEquals("REDSTAR PRESENTS\r\n" +
                "B 5-8 PLAZA MONT KIARA MONT KIARA \r\n" +
                "50480 KUALA LUMPUR, MALAYSIA",mt103.getMessageBlock().getTagValue("59"));


    }


}
