package com.ucpb.tfs.swift;

import com.ucpb.tfs.swift.message.MT700;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:parser-config.xml")
public class SwiftMessageParserIntegrationTest {

    @Autowired
    @Qualifier("swiftMessageParser")
    private SwiftMessageParser swiftMessageParser;

    private static final StringBuilder swiftMessage = new StringBuilder();
    private static File validSwiftMessage;



    @BeforeClass
    public static void setup() throws IOException {
        validSwiftMessage = new File("tfs-swift/src/test/resources/swift/5620MT7X.089");
        assertTrue(validSwiftMessage.exists());

        BufferedReader reader = new BufferedReader(new FileReader(validSwiftMessage));
        String line = null;
        while((line = reader.readLine()) != null){
            swiftMessage.append(line);
            swiftMessage.append('\n');
        }
    }

    @Test
    public void successfullyParseValidMT700() throws ParseException {
        MT700 mt700 = (MT700) swiftMessageParser.parse(swiftMessage.toString());
        assertNotNull(mt700);
        assertEquals("700",mt700.getApplicationHeader().getMessageType());
        assertEquals("IRREVOCABLE",mt700.getField40A());
        assertEquals("FX56202011005203",mt700.getField20());
        assertEquals("110803",mt700.getField31C());
        assertEquals("UCP LATEST VERSION",mt700.getField40E());
        assertEquals("111002 IN USA",mt700.getField31D());
        assertEquals("MIGHTY CORP MIGHTY CORP",mt700.getField50());
//        assertEquals("LETTY MANILA",mt700.getField59());
        assertEquals("USD100,00",mt700.getField32B());
        assertEquals("SIGHT",mt700.getField42C());
        assertEquals("ANY BANK\n" +
                "BY NEGOTIATION",mt700.getField41D());
        assertEquals("+OCEAN BILL OF LADING MUST BE DATED WITHIN THE\n" +
                " VALIDITY PERIOD OF THIS CREDIT\n" +
                "+ALL DOCUMENTS MUST INDICATE COMMODITY CLASSIFICATION\n" +
                "CODE AND LC NUMBER AS INDICATED ABOVE.\n" +
                "+BL       DATED PRIOR TO ISSUANCE OF THIS CREDIT NOT ALLOWED.\n" +
                "+A FEE OF USD  20.00 (OR EQUIVALENT) WILL BE CHARGED TO THE\n" +
                "BENEFICIARY IF DOCUMENTS CONTAINING DISCREPANCIES ARE \n" +
                "PRESENTED FOR PAYMENT/REIMBURSEMENT UNDER THIS LC.  THIS FEE \n" +
                "WILL BE CHARGED FOR EACH SET OF DISCREPANT DOCUMENTS PRESENTED\n" +
                "WHICH REQUIRE OUR OBTAINING ACCEPTANCE FROM OUR CUSTOMER.\n" +
                "+NEGOTIATING BANK MUST PRESENT ALL DOCS AND REIMB CLAIMS  UNDER\n" +
                "THIS CREDIT TO THE CONF. BANK.  BANK OF CHINA\n" +
                "WHICH HOLDS SPECIAL PAYMENT AND REIMBURSEMENT INSTRUCTIONS.",mt700.getField47A());
        assertEquals("+ TOYS\n" +
                "+CANDIES\n" +
                "+PHILIPPINE STANDARD COMMODITY CLASSIFICATION CODE :783.11-03 \n" +
                "+FOB",mt700.getField45A());

        assertEquals("+SIGNED COMMERCIAL INVOICE IN  TRIPLICATE\n" +
                "+PACKING LIST\n" +
                "+ONE FULL SET OF AT LEAST THREE ORIGINAL CLEAN 'ON BOARD' OCEAN\n" +
                "BILLS OF LADING IN NEGOTIABLE AND TRANSFERABLE FORM AND ONE NON-\n" +
                "NEGOTIABLE COPY ISSUED TO THE ORDER OF UNITED COCONUT PLANTERS \n" +
                "BANK MARKED FREIGHT COLLECT NOTIFY\n" +
                "APPLICANT",mt700.getField46A());

    }

}
