package com.ucpb.tfs.functionaltests;

import com.ucpb.tfs.domain.mtmessage.MtMessageRepository;
import com.ucpb.tfs.domain.mtmessage.SwiftMessageParserService;
import org.codehaus.plexus.util.IOUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:swift-config-context.xml")
public class SwiftMessageParserServiceIntegrationTest {

    @Autowired
    private SwiftMessageParserService parserService;

    private File validMtMessage;

    @Autowired
    private MtMessageRepository messageRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private File swiftInputFile;


    @Before
    public void setup() throws IOException {
        parserService.setMoveFile(false);
        jdbcTemplate.execute("DELETE FROM MTMESSAGE");
    }

    @After
    public void teardown(){
        if(swiftInputFile != null){
            swiftInputFile.delete();
        }
    }

    @Test
    public void persistValidMessagesToRepository() throws IOException, InterruptedException {
        swiftInputFile = generateValidMtMessageFile("tfs-core/src/test/resources/swift/input/MT.PRT");
        assertEquals(0,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM MTMESSAGE"));
        Thread.sleep(10000);
        assertEquals(2,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM MTMESSAGE"));

        Map<String,Object> message1 = jdbcTemplate.queryForMap("SELECT DOCUMENTNUMBER,MESSAGECLASS FROM MTMESSAGE WHERE DOCUMENTNUMBER = 'TEST202008007173'");
        assertEquals("TEST202008007173",message1.get("DOCUMENTNUMBER"));
        assertEquals("CONFIRMATION",message1.get("MESSAGECLASS"));


        //TEST202008007149
        Map<String,Object> message2 = jdbcTemplate.queryForMap("SELECT DOCUMENTNUMBER,MESSAGECLASS FROM MTMESSAGE WHERE DOCUMENTNUMBER = 'TEST202008007149'");
        assertEquals("TEST202008007149",message2.get("DOCUMENTNUMBER"));
        assertEquals("INCOMING",message2.get("MESSAGECLASS"));
    }


    private File generateValidMtMessageFile(String filename) throws IOException {
        File message = new File(filename);
        PrintWriter writer = new PrintWriter(new FileWriter(message));
        writer.println("\t---------------------  Instance Type and Transmission --------------\t\n" +
                "       Notification (Transmission) of Original sent to SWIFT (ACK)\n" +
                "       Network Delivery Status   : Network Ack \n" +
                "       Priority/Delivery         : Normal\n" +
                "       Message Input Reference   : 1310 090524UCPBPHMMAXXX0266000934\n" +
                "\t--------------------------- Message Header -------------------------\t\n" +
                "       Swift Input                   : FIN 700 Issue of a Documentary Credit\n" +
                "       Sender   : UCPBPHMMXXX \n" +
                "                  UNITED COCONUT PLANTERS BANK\n" +
                "                  MANILA PH\n" +
                "       Receiver : UCPBPHMMXXX\n" +
                "                  UNITED COCONUT PLANTERS BANK\n" +
                "                  MANILA PH\n" +
                "\t--------------------------- Message Text ---------------------------\t\n" +
                "        27: Sequence of Total\n" +
                "            1/1\n" +
                "       40A: Form of Documentary Credit\n" +
                "            IRREVOCABLE\n" +
                "        20: Documentary Credit Number\n" +
                "            TEST202008007173\n" +
                "       31C: Date of Issue\n" +
                "            080919\n" +
                "       40E: Applicable Rules\n" +
                "            UCP LATEST VERSION\n" +
                "       31D: Date and Place of Expiry\n" +
                "            081210 IN JAPAN\n" +
                "        50: Applicant\n" +
                "            MONARK EQUIPMENT COR MONARK EQUIPME\n" +
                "            NT COR 123 IMPRTR ST TONDO MLA\n" +
                "        59: Beneficiary - Name & Address\n" +
                "            MALOU 14 CHAMPACA ST CAINTA RIZAL P\n" +
                "            HILS\n" +
                "       32B: Currency Code, Amount\n" +
                "            Currency       : JPY (YEN)\n" +
                "            Amount         :                   #100,#\n" +
                "       41D: Available With...By... - Name&Addr\n" +
                "            ANY BANK\n" +
                "            BY NEGOTIATION\n" +
                "       42C: Drafts at...\n" +
                "            SIGHT\n" +
                "       42A: Drawee - BIC\n" +
                "            AEIBUS33XXX\n" +
                "            AMERICAN EXPRESS BANK, LTD.\n" +
                "            NEW YORK,NY  US\n" +
                "       43P: Partial Shipments\n" +
                "            NOT ALLOWED\n" +
                "       43T: Transhipment\n" +
                "            NOT ALLOWED\n" +
                "       44A: Pl of Tking in Chrg / of Rceipt\n" +
                "            DSGDS\n" +
                "       44E: Port of Loading/Airport of Dep.\n" +
                "            ANY PORT IN MLA\n" +
                "       44F: Port of Dischrge/Airport of Dest\n" +
                "            ANY PORT IN MLA\n" +
                "       44B: Pl of Final Dest / of Delivery\n" +
                "            DGGGGG\n" +
                "       44C: Latest Date of Shipment\n" +
                "            081210\n" +
                "       45A: Descriptn of Goods &/or Services\n" +
                "            +FOB\n" +
                "       46A: Documents Required\n" +
                "            +SIGNED COMMERCIAL INVOICE IN  TRIPLICATE\n" +
                "            +PACKING LIST\n" +
                "            +ONE FULL SET OF AT LEAST THREE ORIGINAL CLEAN 'ON BOARD' OCEAN\n" +
                "            BILLS OF LADING IN NEGOTIABLE AND TRANSFERABLE FORM AND ONE NON-\n" +
                "            NEGOTIABLE COPY ISSUED TO THE ORDER OF UNITED COCONUT PLANTERS \n" +
                "            BANK MARKED FREIGHT COLLECT NOTIFY\n" +
                "            FXLC FULL SET\n" +
                "            +BENEFICIARY CERTIFICATE THAT COPY OF COMMERCIAL INVOICE, PACKING\n" +
                "            LIST AND ONE FULL SET OF NON-NEGOTIABLE SHIPPING DOCUMENTS HAVE\n" +
                "            BEEN AIRMAILED DIRECTLY TO BUYER.\n" +
                "            DOC REQ - 1\n" +
                "            DOC REQD -2\n" +
                "       47A: Additional Conditions\n" +
                "            +OCEAN BILL OF LADING/AIR WAYBILL MUST BE DATED WITHIN THE\n" +
                "             VALIDITY PERIOD OF THIS CREDIT\n" +
                "            +ALL DOCUMENTS MUST INDICATE COMMODITY CLASSIFICATION\n" +
                "            CODE AND LC NUMBER AS INDICATED ABOVE.\n" +
                "            +BL/AWB   DATED PRIOR TO ISSUANCE OF THIS CREDIT NOT ALLOWED.\n" +
                "            +A FEE OF USD  20.00 (OR EQUIVALENT) WILL BE CHARGED TO THE\n" +
                "            BENEFICIARY IF DOCUMENTS CONTAINING DISCREPANCIES ARE \n" +
                "            PRESENTED FOR PAYMENT/REIMBURSEMENT UNDER THIS LC.  THIS FEE \n" +
                "            WILL BE CHARGED FOR EACH SET OF DISCREPANT DOCUMENTS PRESENTED\n" +
                "            WHICH REQUIRE OUR OBTAINING ACCEPTANCE FROM OUR CUSTOMER.\n" +
                "            +NEGOTIATING BANK MUST PRESENT ALL DOCS AND REIMB CLAIMS  UNDER\n" +
                "            THIS CREDIT TO THE CONF. BANK.  ANY BANK\n" +
                "            WHICH HOLDS SPECIAL PAYMENT AND REIMBURSEMENT INSTRUCTIONS.\n" +
                "            ADD CON - 1\n" +
                "       71B: Charges\n" +
                "            ALL CHARGES OUTSIDE THE PHILIPPINES\n" +
                "            ARE FOR THE ACCOUNT OF BENEFICIARY\n" +
                "            INCLUDING REIMBURSING CHARGES\n" +
                "            TEST OTHER CHARGES\n" +
                "        49: Confirmation Instructions\n" +
                "            CONFIRM\n" +
                "       53A: Reimbursing Bank - BIC\n" +
                "            /2000090649754\n" +
                "            PNBPUS33PHL\n" +
                "            WACHOVIA BANK, NA\n" +
                "            (INTERNATIONAL OPERATIONS)\n" +
                "            PHILADELPHIA,PA  US\n" +
                "        78: Instr to Payg/Accptg/Negotg Bank\n" +
                "            +NEGOTIATING BANK MUST ADVISE US OF NEGOTIATION DETAILS BY TESTED\n" +
                "            CABLE AND ANY ADDITIONAL TRANSIT INTEREST THAT MAY ARISE FOR NON-\n" +
                "            COMPLIANCE SHALL BE FOR THE ACCOUNT OF NEGOTIATING BANK\n" +
                "            +NEGOTIATING BANK MUST FORWARD ALL DOCS NEGOTIATED UNDER THIS\n" +
                "            CREDIT TO UNITED COCONUT PLANTERS BANK -TRADE SERVICES DEPARTMENT\n" +
                "            AT UCPB BLDG., MAKATI AVENUE, MAKATI CITY\n" +
                "            IN ONE LOT VIA COURIER.+DRAFTS DRAWN UNDER THIS CREDIT MUST BE\n" +
                "            MARKED DRAWN UNDER LC NO.FX56202008007173   WE UNDERTAKE TO HONOR\n" +
                "            DRAFT/S DRAWN AND PRESENTED IN COMPLIANCE WITH TERMS AND\n" +
                "            CONDITIONS OF THIS CREDIT +THIS IS THE OPERATIVE INSTRUMENT.\n" +
                "\t--------------------------- Message Trailer ------------------------\t\n" +
                "       {CHK:B280E09BE89B}\n" +
                "       PKI Signature: MAC-Equivalent\n" +
                "\t---------------------------- Interventions -------------------------\t\n" +
                "       Category      : Network Report\n" +
                "       Creation Time : 24/05/09 13:09:54\n" +
                "       Application   : SWIFT Interface\n" +
                "       Operator      : SYSTEM\n" +
                "       Text\n" +
                "       {1:F21UCPBPHMMAXXX0266000934}{4:{177:0905241310}{451:0}}\n" +
                "\t\t\n" +
                "\t---------------------  Instance Type and Transmission --------------\t\n" +
                "       Notification (Transmission) of Original sent to SWIFT (ACK)\n" +
                "       Network Delivery Status   : Network Ack \n" +
                "       Priority/Delivery         : Normal\n" +
                "       Message Input Reference   : 1318 090524UCPBPHMMAXXX0266000935\n" +
                "\t--------------------------- Message Header -------------------------\t\n" +
                "       Swift Output                   : FIN 700 Issue of a Documentary Credit\n" +
                "       Sender   : UCPBPHMMXXX \n" +
                "                  UNITED COCONUT PLANTERS BANK\n" +
                "                  MANILA PH\n" +
                "       Receiver : UCPBPHMMXXX\n" +
                "                  UNITED COCONUT PLANTERS BANK\n" +
                "                  MANILA PH\n" +
                "\t--------------------------- Message Text ---------------------------\t\n" +
                "        27: Sequence of Total\n" +
                "            1/1\n" +
                "       40A: Form of Documentary Credit\n" +
                "            IRREVOCABLE\n" +
                "        20: Documentary Credit Number\n" +
                "            TEST202008007149\n" +
                "       31C: Date of Issue\n" +
                "            080915\n" +
                "       40E: Applicable Rules\n" +
                "            UCP LATEST VERSION\n" +
                "       31D: Date and Place of Expiry\n" +
                "            081212 IN SWITZERLAND\n" +
                "        50: Applicant\n" +
                "            MONARK EQUIPMENT COR MONARK EQUIPME\n" +
                "            NT COR 123 IMPORTERS ST MANILA\n" +
                "        59: Beneficiary - Name & Address\n" +
                "            MALOU 134 KATIPUNAN RD ST. IGNATIUS\n" +
                "             VILLAGE QC\n" +
                "       32B: Currency Code, Amount\n" +
                "            Currency       : JPY (YEN)\n" +
                "            Amount         :                    #15,#\n" +
                "       41D: Available With...By... - Name&Addr\n" +
                "            ANY BANK\n" +
                "            BY NEGOTIATION\n" +
                "       42C: Drafts at...\n" +
                "            SIGHT\n" +
                "       42A: Drawee - BIC\n" +
                "            AEIBUS33XXX\n" +
                "            AMERICAN EXPRESS BANK, LTD.\n" +
                "            NEW YORK,NY  US\n" +
                "       43P: Partial Shipments\n" +
                "            NOT ALLOWED\n" +
                "       43T: Transhipment\n" +
                "            NOT ALLOWED\n" +
                "       44A: Pl of Tking in Chrg / of Rceipt\n" +
                "            SDGGAS\n" +
                "       44E: Port of Loading/Airport of Dep.\n" +
                "            ANY PORT IN JAPAN\n" +
                "       44F: Port of Dischrge/Airport of Dest\n" +
                "            ANY PORT IN MLA\n" +
                "       44C: Latest Date of Shipment\n" +
                "            081212\n" +
                "       45A: Descriptn of Goods &/or Services\n" +
                "            +FOB\n" +
                "       46A: Documents Required\n" +
                "            +SIGNED COMMERCIAL INVOICE IN  TRIPLICATE\n" +
                "            +PACKING LIST\n" +
                "            +AIR WAYBILL ADDRESSED TO UNITED COCONUT PLANTERS\n" +
                "            BANK, MANILA, MARKED FREIGHT COLLECT NOTIFY\n" +
                "            WAYBILL FXLV\n" +
                "            +BENEFICIARY CERTIFICATE THAT COPY OF COMMERCIAL INVOICE, PACKING\n" +
                "            LIST AND ONE FULL SET OF NON-NEGOTIABLE SHIPPING DOCUMENTS HAVE\n" +
                "            BEEN AIRMAILED DIRECTLY TO BUYER.\n" +
                "            DOC REQD - 1\n" +
                "            DOC REQD - 2\n" +
                "       47A: Additional Conditions\n" +
                "            +OCEAN BILL OF LADING/AIR WAYBILL MUST BE DATED WITHIN THE\n" +
                "             VALIDITY PERIOD OF THIS CREDIT\n" +
                "            +ALL DOCUMENTS MUST INDICATE COMMODITY CLASSIFICATION\n" +
                "            CODE AND LC NUMBER AS INDICATED ABOVE.\n" +
                "            +BL/AWB   DATED PRIOR TO ISSUANCE OF THIS CREDIT NOT ALLOWED.\n" +
                "            +A FEE OF USD  20.00 (OR EQUIVALENT) WILL BE CHARGED TO THE\n" +
                "            BENEFICIARY IF DOCUMENTS CONTAINING DISCREPANCIES ARE\n" +
                "            PRESENTED FOR PAYMENT/REIMBURSEMENT UNDER THIS LC.  THIS FEE\n" +
                "            WILL BE CHARGED FOR EACH SET OF DISCREPANT DOCUMENTS PRESENTED\n" +
                "            WHICH REQUIRE OUR OBTAINING ACCEPTANCE FROM OUR CUSTOMER.\n" +
                "            +NEGOTIATING BANK MUST PRESENT ALL DOCS AND REIMB CLAIMS  UNDER\n" +
                "            THIS CREDIT TO THE CONF. BANK.  ANY BANK\n" +
                "            WHICH HOLDS SPECIAL PAYMENT AND REIMBURSEMENT INSTRUCTIONS.\n" +
                "            ADD CON - 1\n" +
                "            ADD CON - 2\n" +
                "       71B: Charges\n" +
                "            ALL CHARGES OUTSIDE THE PHILIPPINES\n" +
                "            ARE FOR THE ACCOUNT OF BENEFICIARY\n" +
                "            INCLUDING REIMBURSING CHARGES\n" +
                "            TEST OTHER CHARGES\n" +
                "        49: Confirmation Instructions\n" +
                "            CONFIRM\n" +
                "       53A: Reimbursing Bank - BIC\n" +
                "            /2000090649754\n" +
                "            PNBPUS33PHL\n" +
                "            WACHOVIA BANK, NA\n" +
                "            (INTERNATIONAL OPERATIONS)\n" +
                "            PHILADELPHIA,PA  US\n" +
                "        78: Instr to Payg/Accptg/Negotg Bank\n" +
                "            +CONFIRMING BANK MUST ADVISE US THEIR CONF. AND REF. NO.\n" +
                "            +NEGOTIATING BANK MUST ADVISE US OF NEGOTIATION DETAILS BY TESTED\n" +
                "            CABLE AND ANY ADDITIONAL TRANSIT INTEREST THAT MAY ARISE FOR NON-\n" +
                "            COMPLIANCE SHALL BE FOR THE ACCOUNT OF NEGOTIATING BANK\n" +
                "            +NEGOTIATING BANK MUST FORWARD ALL DOCS NEGOTIATED UNDER THIS\n" +
                "            CREDIT TO UNITED COCONUT PLANTERS BANK -TRADE SERVICES DEPARTMENT\n" +
                "            AT UCPB BLDG., MAKATI AVENUE, MAKATI CITY\n" +
                "            IN ONE LOT VIA COURIER.+DRAFTS DRAWN UNDER THIS CREDIT MUST BE\n" +
                "            MARKED DRAWN UNDER LC NO.FX56202008007149   WE UNDERTAKE TO HONOR\n" +
                "            DRAFT/S DRAWN AND PRESENTED IN COMPLIANCE WITH TERMS AND\n" +
                "            CONDITIONS OF THIS CREDIT +THIS IS THE OPERATIVE INSTRUMENT.\n" +
                "\t--------------------------- Message Trailer ------------------------\t\n" +
                "       {CHK:7B918B6AA6D3}\n" +
                "       PKI Signature: MAC-Equivalent\n" +
                "\t---------------------------- Interventions -------------------------\t\n" +
                "       Category      : Network Report\n" +
                "       Creation Time : 24/05/09 13:18:24\n" +
                "       Application   : SWIFT Interface\n" +
                "       Operator      : SYSTEM\n" +
                "       Text\n" +
                "       {1:F21UCPBPHMMAXXX0266000935}{4:{177:0905241318}{451:0}}");

        IOUtil.close(writer);

        return message;
    }
}
