package com.ucpb.tfs.swift.message;


import com.ucpb.tfs.swift.ParseException;
import com.ucpb.tfs.swift.SwiftMessageParser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 */
@Ignore("TODO:// compare test data with swift 2012 format")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:parser-config.xml")
public class MT700Test {

    private MT700 mt700;
    private File mt;

    @Autowired
    @Qualifier("swiftMessageParser")
    private SwiftMessageParser swiftMessageParser;
    private static final StringBuilder swiftMessage = new StringBuilder();
    private static File validSwiftMessage;

    @Before
    public void setupMT700(){
        mt700 = new MT700();

        mt700.setField40A("IRREVOCABLE");
        mt700.setField20("FX56202011005203");
        mt700.setField31C("110803");
        mt700.setField40E("UCP LATEST VERSION");
        mt700.setField31D("111002 IN USA");
        mt700.setField50("MIGHTY CORP MIGHTY CORP");
//        assertEquals("LETTY MANILA",mt700.getField59());
        mt700.setField32B("USD100,00");
        mt700.setField42C("SIGHT");
        mt700.setField41D("ANY BANK\n" +
                "BY NEGOTIATION");
        mt700.setField47A("+OCEAN BILL OF LADING MUST BE DATED WITHIN THE\n" +
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
                "WHICH HOLDS SPECIAL PAYMENT AND REIMBURSEMENT INSTRUCTIONS.");
        mt700.setField45A("+ TOYS\n" +
                "+CANDIES\n" +
                "+PHILIPPINE STANDARD COMMODITY CLASSIFICATION CODE :783.11-03 \n" +
                "+FOB");

        mt700.setField46A("+SIGNED COMMERCIAL INVOICE IN  TRIPLICATE\n" +
                "+PACKING LIST\n" +
                "+ONE FULL SET OF AT LEAST THREE ORIGINAL CLEAN 'ON BOARD' OCEAN\n" +
                "BILLS OF LADING IN NEGOTIABLE AND TRANSFERABLE FORM AND ONE NON-\n" +
                "NEGOTIABLE COPY ISSUED TO THE ORDER OF UNITED COCONUT PLANTERS \n" +
                "BANK MARKED FREIGHT COLLECT NOTIFY\n" +
                "APPLICANT");
    }

    @BeforeClass
    public static void setup() throws IOException {
        File tmp = new File("text.txt");
        System.out.println(tmp.getAbsolutePath());

        validSwiftMessage = new File("tfs-swift/src/test/resources/swift/5620MT7X.089");
        assertTrue(validSwiftMessage.exists());

        BufferedReader reader = new BufferedReader(new FileReader(validSwiftMessage));
        String line = null;
        while((line = reader.readLine()) != null){
            swiftMessage.append(line);
            swiftMessage.append('\n');
        }
    }

    @Before
    public void setupValidMT() throws ParseException {
        mt700 = (MT700) swiftMessageParser.parse(swiftMessage.toString());

    }



    @Test
    public void successfullyPassValidMT700(){
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<MT700>> errors =  validator.validate(mt700);
        Iterator<ConstraintViolation<MT700>> iterator = errors.iterator();
        while(iterator.hasNext()){
            System.out.println("MESSAGE: " + iterator.next().getMessage() + " ******");
        }

        assertTrue(errors.isEmpty());
    }

    @Test
    public void failInvalidMT(){
        MT700 mt = new MT700();
        mt.setField20("FIELD 20");
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<MT700>> errors =  validator.validate(mt);
        assertFalse(errors.isEmpty());
        Iterator<ConstraintViolation<MT700>> iterator = errors.iterator();
        while(iterator.hasNext()){
            System.out.println("MESSAGE: " + iterator.next().getMessage());
        }
    }
}
