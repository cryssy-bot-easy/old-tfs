
package com.ucpb.tfs.swift.validator.xml;

import com.ucpb.tfs.swift.message.*;
import com.ucpb.tfs.swift.message.mt7series.MT700;
import com.ucpb.tfs.swift.message.mt2series.MT202;
import com.ucpb.tfs.swift.message.mt4series.MT410;
import com.ucpb.tfs.swift.validator.ValidationError;
import com.ucpb.tfs.swift.validator.xml.SwiftXmlValidator;
import com.ucpb.tfs.util.FileUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static junit.framework.Assert.*;

/**
 */
public class SwiftXmlValidatorIntegrationTest {


    private SwiftXmlValidator swiftXmlValidator;



    @Before
    public void setup(){
        swiftXmlValidator = new SwiftXmlValidator("/swift/schemas/swift-master.xsd","/swift/formatter/swift-format.xsl");
    }

    @Test
    public void passValidMt760(){
        RawSwiftMessage swiftMessage = new RawSwiftMessage();

        BasicHeader header = new BasicHeader();
        header.setApplicationIdentifier("F");
        header.setServiceIndentifier("01");
        header.setLtIdentifier("UCPBPHMMAXXX");
        header.setSessionNumber("0000");
        header.setSequenceNumber("000000");
        swiftMessage.setBasicHeader(header);

        ApplicationHeader applicationHeader = new ApplicationHeader();
        applicationHeader.setIoIdentifier("I");
        applicationHeader.setMessageType("760");
        SwiftAddress address = new SwiftAddress();
        address.setBankIdentifierCode("ASASASAS");
        address.setBranchCode("XXX");
        applicationHeader.setReceiverAddress(address);
        applicationHeader.setMessagePriority("N");
        swiftMessage.setApplicationHeader(applicationHeader);

        swiftMessage.addTag("27","1/1");
        swiftMessage.addTag("20","DOCNUMBER");
        swiftMessage.addTag("23","23");
        swiftMessage.addTag("40C","AAAA");

        swiftMessage.addTag("77C","Details of Guarantee\r\n"+
			"WE HAVE ESTABLISHED OUR IRREVOCABLE LETTER OF\r\n"+
			"CREDIT IN YOUR FAVOR AS DETAILED HEREIN SUBJECT TO SAUDI LAW\r\n"+
			"BENEFICIARY\r\n"+
			"me\r\n"+
			"APPLICANT\r\n"+
			"TWINPACK CONTAINER C\r\n"+
			"DATE AND PLACE OF EXPIRY\r\n"+
			"OCTOBER 1, 2013, ARUBA\r\n"+
			"AT OUR COUNTERS\r\n"+
			"DOCUMENTARY CREDIT AMOUNT\r\n"+
			"USD 5,000.00\r\n"+
			"AVAILABLE WITH\r\n"+
			"ANY BANK\r\n"+
			"KBC BANKA AD\r\n"+
			"BY PAYMENT\r\n"+
			"ADDITIONAL DETAILS\r\n"+
			"BENEFICIARY:\r\n"+
			"XXXXXXXXXXXXXXXXXX\r\n"+
			".\r\n"+
			"AT THE REQUEST OF OUR CUSTOMER TWINPACK CONTAINER C,.\r\n"+
			"'THE APPLICANT' WITH ADDRESS AT 648 BO. LLANO, CALOOCAN, CITY.,\r\n"+
			"AND SUBJECT TO THE TERMS\r\n"+
			"AND CONDITIONS BELOW, WE HEREBY IRREVOCABLY AND UNCONDITIONALLY\r\n"+
			"UNDERTAKE TO PAY TO YOU, WITHOUT REGARD TO ANY\r\n"+
			"OBJECTION, NOTICE OR CLAIM OF ANY KIND FROM THE\r\n"+
			"APPLICANT OR ANY OTHER PARTY OR ANY DISCUSSION\r\n"+
			"WHATSOEVER, AN AMOUNT OF USD 5,000.00 UPON\r\n"+
			"YOUR REQUEST AT OR AT SUCH OTHER ACCOUNT\r\n"+
			"AS MAY BE NOTIFIED TO US BY YOU UPON WRITTEN\r\n"+
			"TESTED TELEX OR AUTHENTICATED SWIFT DECLARATION\r\n"+
			"PRIOR TO PAYMENT REQUEST.\r\n"+
			".\r\n"+
			"THIS LETTER OF CREDIT IS AVAILABLE FOR PAYMENT AT\r\n"+
			"OUR COUNTERS AT SIGHT. ONLY ONE DRAWING IN FULL AMOUNT\r\n"+
			"MADE UNDER THIS LETTER OF CREDIT.\r\n"+
			".\r\n"+
			"PAYMENT AS DETAILED ABOVE WILL BE EFFECTED BY US\r\n"+
			"WITHOUT REQUIREMENT OF ANY DOCUMENTARY\r\n"+
			"PRESENTATION ON YOUR PART.\r\n"+
			".\r\n"+
			"THIS LETTER OF CREDIT WILL EXPIRE ON\r\n"+
			"OCTOBER 1, 2013\r\n"+
			".\r\n"+
			"THE BENEFIT OF THIS LETTER OF CREDIT IS NOT\r\n"+
			"ASSIGNABLE IN WHOLE OR IN PART.\r\n"+
			".\r\n"+
			"THIS LETTER OF CREDIT IS IRREVOCABLE.\r\n"+
			".\r\n"+
			"THIS LETTER OF CREDIT IS SUBJECT TO SAUDI LAW.\r\n"+
			".\r\n"+
			"WE HEREIN CONFIRM ALL NECESSARY APPROVALS FOR THE ISSUANCE OF THIS LETTER\r\n"+
			"OF CREDIT HAD BEEN OBTAINED AND ARE IN FULL FORCE AND EFFECT, AND NOTHING\r\n"+
			"SHALL AFFECT OUR LIABILITY TO MAKE PAYMENT TO YOU UNDER THIS LETTER OF CREDIT.\r\n"+
			"WITHOUT PREJUDICE TO YOUR RIGHT TO SUBMIT TO ANY OTHER LAW, JURISDICUTION\r\n"+
			"AND VENUE, THIS LETTER OF CREDIT SHALL BE SUBJECT TO, GOVERNED BY AND\r\n"+
			"CONSTRUED IN ACCORDANCE WITH THE LAWS, RULES AND REGULATIONS OF KINGDOM\r\n"+
			"OF SAUDI ARABIA(KSA), AND IN THE EVENT OF ANY DISPUTE ARISING UNDER THIS LETTER\r\n"+
			"OF CREDIT, THE FORM OF JURISDICTION SHALL BE THE COMPETENT COURT IN RIYADH,KSA.");

        List<ValidationError> errors = swiftXmlValidator.validate(swiftMessage);
        printErrors(errors);
        assertTrue(errors.isEmpty());
    }


    @Test
    public void passValidMt410() throws IOException {
        RawSwiftMessage swiftMessage = new RawSwiftMessage();

        BasicHeader header = new BasicHeader();
        header.setApplicationIdentifier("F");
        header.setServiceIndentifier("01");
        header.setLtIdentifier("UCPBPHMMAXXX");
        header.setSessionNumber("0000");
        header.setSequenceNumber("000000");
        swiftMessage.setBasicHeader(header);

        ApplicationHeader applicationHeader = new ApplicationHeader();
        applicationHeader.setIoIdentifier("I");
        applicationHeader.setMessageType("410");
        SwiftAddress address = new SwiftAddress();
        address.setBankIdentifierCode("ASASASAS");
        address.setBranchCode("XXX");
        applicationHeader.setReceiverAddress(address);
        applicationHeader.setMessagePriority("N");
        swiftMessage.setApplicationHeader(applicationHeader);

        MessageBlock messageBlock = new MessageBlock();
        messageBlock.addTag("20","DOCNUMBER");
        messageBlock.addTag("21","RELATEDREFERENCE");
        messageBlock.addTag("32A","121026USD755000,");
        messageBlock.addTag("72","SENDER TO RECEIVER");
        swiftMessage.setMessageBlock(messageBlock);


        List<ValidationError> errors = swiftXmlValidator.validate(swiftMessage);
        printErrors(errors);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void passValidMt740() throws IOException {
        RawSwiftMessage swiftMessage = new RawSwiftMessage();

        
        BasicHeader header = new BasicHeader();
        header.setApplicationIdentifier("F");
        header.setServiceIndentifier("01");
        header.setLtIdentifier("UCPBPHMMAXXX");
        header.setSessionNumber("0000");
        header.setSequenceNumber("000000");
        swiftMessage.setBasicHeader(header);

        ApplicationHeader applicationHeader = new ApplicationHeader();
        applicationHeader.setIoIdentifier("I");
        applicationHeader.setMessageType("740");
        SwiftAddress address = new SwiftAddress();
        address.setBankIdentifierCode("ASASASAS");
        address.setBranchCode("XXX");
        applicationHeader.setReceiverAddress(address);
        applicationHeader.setMessagePriority("N");
        swiftMessage.setApplicationHeader(applicationHeader);

        MessageBlock messageBlock = new MessageBlock();
        messageBlock.addTag("20","90901113006340");
        messageBlock.addTag("25","3582092651001");
        messageBlock.addTag("40F","UCP,");
        messageBlock.addTag("31D","130531 IN 110");
        messageBlock.addTag("59","DANIEL LAGUNA");
        messageBlock.addTag("32B","USD995000,");
        messageBlock.addTag("39B","NOT EXCEEDING");
        messageBlock.addTag("41D","AMRONL2A BY PAYMENT");
        messageBlock.addTag("42C","DRAFT AT SIGHT");
        messageBlock.addTag("42A","UCPBPHMM");
        messageBlock.addTag("72","INFO");
        swiftMessage.setMessageBlock(messageBlock);


        List<ValidationError> errors = swiftXmlValidator.validate(swiftMessage);
        printErrors(errors);
        assertTrue(errors.isEmpty());
    }
    
    
    
    @Test
    public void failInvalidMt410() throws IOException {
        RawSwiftMessage swiftMessage = new RawSwiftMessage();

        BasicHeader header = new BasicHeader();
        header.setApplicationIdentifier("F");
        header.setServiceIndentifier("01");
        header.setLtIdentifier("UCPBPHMMAXXX");
        header.setSessionNumber("0000");
        header.setSequenceNumber("000000");
        swiftMessage.setBasicHeader(header);

        ApplicationHeader applicationHeader = new ApplicationHeader();
        applicationHeader.setIoIdentifier("I");
        applicationHeader.setMessageType("410");
        SwiftAddress address = new SwiftAddress();
        address.setBankIdentifierCode("ASASASAS");
        address.setBranchCode("XXX");
        applicationHeader.setReceiverAddress(address);
        swiftMessage.setApplicationHeader(applicationHeader);

        MessageBlock messageBlock = new MessageBlock();
        messageBlock.addTag("21","RELATEDREFERENCE");
        messageBlock.addTag("32A","121026USD755000,");
        swiftMessage.setMessageBlock(messageBlock);


        List<ValidationError> errors = swiftXmlValidator.validate(swiftMessage);
        printErrors(errors);
        assertFalse(errors.isEmpty());
    }

    @Test
    public void failInvalidMt700() throws IOException {
        RawSwiftMessage swiftMessage = new RawSwiftMessage();

        BasicHeader header = new BasicHeader();
        header.setApplicationIdentifier("F");
        header.setServiceIndentifier("01");
        header.setLtIdentifier("UCPBPHMMAXXX");
        header.setSessionNumber("0000");
        header.setSequenceNumber("000000");
        swiftMessage.setBasicHeader(header);

        ApplicationHeader applicationHeader = new ApplicationHeader();
        applicationHeader.setIoIdentifier("I");
        applicationHeader.setMessageType("700");
        SwiftAddress address = new SwiftAddress();
        address.setBankIdentifierCode("ASASASAS");
        address.setBranchCode("XXX");
        applicationHeader.setReceiverAddress(address);
        applicationHeader.setMessagePriority("N");
        swiftMessage.setApplicationHeader(applicationHeader);

        MessageBlock messageBlock = new MessageBlock();
        messageBlock.addTag("27","1/1");
        messageBlock.addTag("40A","REVOCABLE");
        messageBlock.addTag("20","90901113000012");
        messageBlock.addTag("31C","130201");
        messageBlock.addTag("40E","OTHR");
        messageBlock.addTag("31D","130228 IN 127");
        messageBlock.addTag("50","MARSMAN DRYSDALE MEDMOLAVE BLDG.2231 DON CHINO ROCES AVE.");
        messageBlock.addTag("59","STEEL CORP. PHILSTEEL TOWER,140AMORSOLO ST., LEGASPIVILLAGE, MAKATI");
        messageBlock.addTag("32B","USD123456,00");
        messageBlock.addTag("39A","10/10");
        messageBlock.addTag("39B","NOT EXCEEDING");
        messageBlock.addTag("39C","ADDITIONAL AMOUNTS COVERED");
        messageBlock.addTag("41A","IDENTIFIER CODEBY NEGOTIATION");
        messageBlock.addTag("42C","DRAFT AT SIGHT");
        messageBlock.addTag("42A","ABBLBDDH004");
        messageBlock.addTag("43P","ALLOWED");
        messageBlock.addTag("43T","ALLOWED");
        messageBlock.addTag("44A","PLACE IN CHARGE");
        messageBlock.addTag("44B","FINAL DESTINATION");
        messageBlock.addTag("44E","DEPARTURE");
        messageBlock.addTag("44F","ARRIVAL");
        messageBlock.addTag("44C","130228");
        messageBlock.addTag("45A","DESC OF GOODS AND SERVICES");
        messageBlock.addTag("49","MAY ADD");
        messageBlock.addTag("57B","ADVISER THROUGH BANK LOCATION");

        swiftMessage.setMessageBlock(messageBlock);


        List<ValidationError> errors = swiftXmlValidator.validate(swiftMessage);
        printErrors(errors);
        assertFalse(errors.isEmpty());
        for(ValidationError error : errors){
            assertNotNull(error);
        }
    }
    
    @Test
    public void passValidMt700() throws IOException {
        RawSwiftMessage swiftMessage = new RawSwiftMessage();

        BasicHeader header = new BasicHeader();
        header.setApplicationIdentifier("F");
        header.setServiceIndentifier("01");
        header.setLtIdentifier("UCPBPHMMAXXX");
        header.setSessionNumber("0000");
        header.setSequenceNumber("000000");
        swiftMessage.setBasicHeader(header);

        ApplicationHeader applicationHeader = new ApplicationHeader();
        applicationHeader.setIoIdentifier("I");
        applicationHeader.setMessageType("700");
        SwiftAddress address = new SwiftAddress();
        address.setBankIdentifierCode("ASASASAS");
        address.setBranchCode("XXX");
        applicationHeader.setReceiverAddress(address);
        applicationHeader.setMessagePriority("N");
        swiftMessage.setApplicationHeader(applicationHeader);

        MessageBlock messageBlock = new MessageBlock();
        messageBlock.addTag("27","1/1");
        messageBlock.addTag("40A","REVOCABLE");
        messageBlock.addTag("20","90901113000012");
        messageBlock.addTag("31C","130201");
        messageBlock.addTag("40E","UCP LATEST VERSION");
        messageBlock.addTag("31D","130228 IN 127");
        messageBlock.addTag("50","MARSMAN DRYSDALE MEDMOLAVE BLDG.2231 DON CHINO ROCES AVE.");
        messageBlock.addTag("59","STEEL CORP. PHILSTEEL TOWER,140AMORSOLO ST., LEGASPIVILLAGE, MAKATI");
        messageBlock.addTag("32B","USD123456,00");
        messageBlock.addTag("39A","10/10");
        messageBlock.addTag("41D","WELLS FARGO BANK, N.A.\r\nBY PAYMENT");
        messageBlock.addTag("42C","DRAFT AT SIGHT");
        messageBlock.addTag("42A","PNBPUS3NNYC");
        messageBlock.addTag("43P","ALLOWED");
        messageBlock.addTag("43T","ALLOWED");
        messageBlock.addTag("44A","PLACE OF TAKING IN CHARGE");
        messageBlock.addTag("44E","PORTOFLOADING");
        messageBlock.addTag("44F","PORT OF DISCHARGE");
        messageBlock.addTag("44B","PLACE OF FINAL DESTINATION");
        messageBlock.addTag("44C","130614");
        messageBlock.addTag("45A","DESC OF GOODS AND SERVICES");
        messageBlock.addTag("48","MARVIN ANTHONY VOLANTE");
        messageBlock.addTag("49","CONFIRM");
        messageBlock.addTag("53A","PNBPUS3NNYC");
        messageBlock.addTag("57A","PNBPUS3NNYC");
        messageBlock.addTag("72","SENDER TO RECEIVER INFORMATION");


        swiftMessage.setMessageBlock(messageBlock);


        List<ValidationError> errors = swiftXmlValidator.validate(swiftMessage);
        printErrors(errors);
        assertTrue(errors.isEmpty());
      
    }
    
    @Test
    public void failInvalidMt202() throws IOException {
        RawSwiftMessage swiftMessage = new RawSwiftMessage();
        BasicHeader header = new BasicHeader();
        header.setApplicationIdentifier("F");
        header.setServiceIndentifier("01");
        header.setLtIdentifier("UCPBPHMMAXXX");
        header.setSessionNumber("0000");
        header.setSequenceNumber("000000");
        swiftMessage.setBasicHeader(header);

        ApplicationHeader applicationHeader = new ApplicationHeader();
        applicationHeader.setIoIdentifier("I");
        applicationHeader.setMessageType("410");
        SwiftAddress address = new SwiftAddress();
        address.setBankIdentifierCode("ASASASAS");
        address.setBranchCode("XXX");
        applicationHeader.setMessagePriority("N");
        applicationHeader.setReceiverAddress(address);
        swiftMessage.setApplicationHeader(applicationHeader);

        MessageBlock messageBlock = new MessageBlock();
        messageBlock.addTag("20","90901113000024");
        swiftMessage.setMessageBlock(messageBlock);


        List<ValidationError> errors = swiftXmlValidator.validate(swiftMessage);
        printErrors(errors);
        assertFalse(errors.isEmpty());
    }

    
    
    
    private void printErrors(List<ValidationError> errorList){
        for(ValidationError error : errorList){
        	System.out.println("*** ERROR ***");
            System.out.println(error.getMessage());
        	System.out.println("*** ERROR ***");

        }
    }


}
