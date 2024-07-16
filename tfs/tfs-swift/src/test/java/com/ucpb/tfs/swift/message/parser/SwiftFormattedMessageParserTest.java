package com.ucpb.tfs.swift.message.parser;

import com.ucpb.tfs.swift.message.MessageBlock;
import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.swift.message.Tag;
import com.ucpb.tfs.util.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static junit.framework.Assert.*;

public class SwiftFormattedMessageParserTest {

    private SwiftFormattedMessageParser parser = new SwiftFormattedMessageParser();

    private static final String MESSAGE_BODY_CONTENTS = "\\-+\\sMessage Text\\s\\-+(.*)\\s(\\-+\\sMessage Trailer\\s\\-+)";


    @Test
    public void successfullyRetrieveMessageType() throws IOException, ParseException {
        RawSwiftMessage message = parser.parse(FileUtil.getFileAsString("/swift/messages/incoming/I00012012112234.txt"));
        assertEquals("730",message.getMessageType());
    }

    @Test
    public void successfullyRetrieveAllMessageBlockContents() throws IOException, ParseException {
        RawSwiftMessage message = parser.parse(FileUtil.getFileAsString("/swift/messages/incoming/I00012012112234.txt"));
        MessageBlock messageBlock = message.getMessageBlock();
        assertNotNull(messageBlock);

        List<Tag> contents = messageBlock.getTags();
        assertFalse(contents.isEmpty());
        for(Tag tag : contents){
            System.out.println(tag.getTagName() + ":" + tag.getValue());
        }
        assertEquals("AD219239",messageBlock.getTagValue("20"));
        assertEquals("FX56202012006300",messageBlock.getTagValue("21"));
        assertEquals("121119",messageBlock.getTagValue("30"));
        assertEquals("WE ACK RCPT OF YR A/M CREDIT N HAVE\n" +
                "            ADVISED IT TO THE BENEF ADDING\n" +
                "            OUR CONFIRMATION AS PER YR\n" +
                "            INSTRUCTION THRU DEUTHKHHXXX, RGDS\n" +
                "            MAUREEN NG (TEAM HEAD-TEAM J)/DW",StringUtils.trim(messageBlock.getTagValue("72")));
    }

    @Test
    public void successfullyRetrieveAllMessageBlockContentsOfConfirmation() throws IOException, ParseException {
        RawSwiftMessage message = parser.parse(FileUtil.getFileAsString("/swift/messages/incoming/00220002-1.PRT"));
        MessageBlock messageBlock = message.getMessageBlock();
        assertNotNull(messageBlock);

        for(Tag tag : messageBlock.getTags()){
            System.out.println("T: " + tag.getTagName() + " : " + tag.getValue());
        }

        assertEquals(26, messageBlock.getTags().size());

        assertEquals("1/1",messageBlock.getTagValue("27"));
        assertEquals("IRREVOCABLE",messageBlock.getTagValue("40A"));

    }

    @Ignore("Is this input valid?")
    @Test
    public void successfullyRetrieveAllMessageBlockContentsOfConfirmationMt() throws IOException, ParseException {
        RawSwiftMessage message = parser.parse(FileUtil.getFileAsString("/swift/messages/incoming/O00012012112233.txt"));
        MessageBlock messageBlock = message.getMessageBlock();
        assertNotNull(messageBlock);

        List<Tag> contents = messageBlock.getTags();
        assertFalse(contents.isEmpty());
        for(Tag tag : contents){
            System.out.println(tag.getTagName() + ":" + tag.getValue());
        }
    }

    @Test
    public void extractMessageBody() throws IOException {
        String fileContents = FileUtil.getFileAsString("/swift/messages/incoming/I00012012112234.txt");
        Matcher matcher = Pattern.compile(MESSAGE_BODY_CONTENTS,Pattern.DOTALL).matcher(fileContents);
        assertTrue(matcher.find());
        System.out.println(matcher.group(1));
    }

    @Test
    public void messageFieldsRegexTest(){
        String fields = "       32B: Currency Code, Amount\r\n" +
                "            Currency       : JPY (YEN)\r\n" +
                "            Amount         :                    #15,#\r\n";

//        String fields ="       41D: Available With...By... - Name&Addr\r\n" +
//                "            ANY BANK\r\n" +
//                "            BY NEGOTIATION\r\n";
        Matcher matcher = Pattern.compile("(\\d+\\w?):\\s([.[^\\n]]+\\n)\\s([.\\s\\w\\Q:()#,\\E]+)(?=\\n)",Pattern.DOTALL).matcher(fields);
        assertTrue(matcher.find());
//        assertEquals("ANY BANK\r\n" +
//                "            BY NEGOTIATION", StringUtils.trim(matcher.group(3)));
        assertEquals("Currency       : JPY (YEN)\r\n" +
                "            Amount         :                    #15,#", StringUtils.trim(matcher.group(3)));
    }

    @Test
    public void messageFieldForConfirmationRegexTest(){
        String pattern = "(\\d+\\w?):\\s([.[^\\n]]+\\n)\\s+([./\\d\\s\\w\\Q:()#,\\E]+?(?=\\d+\\w?:|\\z))";
//        String messageBody = "        27: Sequence of Total\r\n" +
//                "            1/1\r\n";
//        String messageBody = "        27: Sequence of Total\r\n" +
//                "            1/1\r\n" +
//                "       40A: Form of Documentary Credit\r\n" +
//                "            IRREVOCABLE\r\n";
        String messageBody = "        50: Applicant\n" +
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
                "            BY NEGOTIATION";

        Matcher matcher = Pattern.compile(pattern,Pattern.DOTALL).matcher(messageBody);

        assertTrue(matcher.find());
        assertEquals("50",matcher.group(1));
        assertEquals("MONARK EQUIPMENT COR MONARK EQUIPME\n" +
                "            NT COR 123 IMPRTR ST TONDO MLA",StringUtils.trim(matcher.group(3)));

        assertTrue(matcher.find());
        assertEquals("59",matcher.group(1));
        assertEquals("MALOU 14 CHAMPACA ST CAINTA RIZAL P\n" +
                "            HILS",StringUtils.trim(matcher.group(3)));

        assertTrue(matcher.find());
        assertEquals("32B", matcher.group(1));
        assertEquals("Currency       : JPY (YEN)\n" +
                "            Amount         :                   #100,#", StringUtils.trim(matcher.group(3)));

        assertTrue(matcher.find());
        assertEquals("41D",matcher.group(1));
        assertEquals("ANY BANK\n" +
                "            BY NEGOTIATION",StringUtils.trim(matcher.group(3)));


    }

    @Test
    public void successfullyRetrieveMt700Type() throws IOException, ParseException {
        RawSwiftMessage message = parser.parse(FileUtil.getFileAsString("/swift/messages/incoming/00200003.PRT"));
        assertEquals("700",message.getMessageType());
    }

    @Test
    public void successfullyRetrieveMessageBodyDetails() throws IOException, ParseException {
        RawSwiftMessage message = parser.parse(FileUtil.getFileAsString("/swift/messages/incoming/00200003.PRT"));
        MessageBlock messageBlock = message.getMessageBlock();
        assertEquals("1/1",messageBlock.getTagValue("27"));

        assertEquals("1/1",messageBlock.getTagValue("27"));
        assertEquals("IRREVOCABLE",messageBlock.getTagValue("40A"));
        assertEquals("TEST202008004972",messageBlock.getTagValue("20"));
        assertEquals("081104",messageBlock.getTagValue("31C"));
        assertEquals("UCP LATEST VERSION",messageBlock.getTagValue("40E"));
        assertEquals("081204 IN INDONESIA",messageBlock.getTagValue("31D"));
        assertEquals("JJED PHILIPPINES INC.\n" +
                "            54 P. CRUZ ST., MANDALUYONG CITY,\n" +
                "            PHILIPPINES 1501",messageBlock.getTagValue("50"));
        assertEquals("PT. THE UNIVENUS\n" +
                "            JL. RAYA SERANG KM. 12, CIKUPA\n" +
                "            TANGERANG, INDONESIA",messageBlock.getTagValue("59"));
        assertEquals("USD1,",messageBlock.getTagValue("32B"));
        assertEquals("10/10",messageBlock.getTagValue("39A"));
        assertEquals("ANY BANK\n" +
                "            BY ACCEPTANCE",messageBlock.getTagValue("41D"));
        assertEquals("90 DAYS FROM B/L DATE",messageBlock.getTagValue("42C"));
        assertEquals("UCPBPHMM\n" +
                "            UNITED COCONUT PLANTERS BANK\n" +
                "            MANILA  PH",messageBlock.getTagValue("42A"));
        assertEquals("ALLOWED",messageBlock.getTagValue("43P"));
        assertEquals("ALLOWED",messageBlock.getTagValue("43T"));
        assertEquals("ANY INDONESIAN PORT",messageBlock.getTagValue("44E"));
        assertEquals("NORTH HARBOR MANILA (MANILA INTERNATIONAL CONTAINER PORT)",messageBlock.getTagValue("44F"));
        assertEquals("+1X40'HC CTR STC JRT LIVI 250M 830 CTNS. AND HRT DISPENSERS\n" +
                "             10 CTNS (FREE OF CHARGE)\n" +
                "             PER PROFORMA INVOICE NO. PI-042/10/2008\n" +
                "            +PHILIPPINE STANDARD COMMODITY CLASSIFICATION CODE :642.94-04\n" +
                "            +CIF NORTH MANILA",messageBlock.getTagValue("45A"));
        assertEquals("+SIGNED COMMERCIAL INVOICE IN  TRIPLICATE\n" +
                "            +PACKING LIST\n" +
                "            +ONE FULL SET OF AT LEAST THREE ORIGINAL CLEAN 'ON BOARD' OCEAN\n" +
                "            BILLS OF LADING IN NEGOTIABLE AND TRANSFERABLE FORM AND ONE NON-\n" +
                "            NEGOTIABLE COPY ISSUED TO THE ORDER OF UNITED COCONUT PLANTERS\n" +
                "            BANK MARKED FREIGHT PREPAID NOTIFY\n" +
                "            APPLICANT\n" +
                "            +TRANSFERABLE MARINE INSURANCE POLICY OR CERTIFICATE. WAR RISK\n" +
                "            INSURANCE POLICY OR CERTIFICATE INCLUDING STRIKES, RIOTS, CIVIL\n" +
                "            COMMOTION AND MARINE EXTENSION CLAUSES\n" +
                "            IN DUPLICATE FOR  110PCT.    OF FULL INVOICE, VALUE FROM POINT OF\n" +
                "            ORIGIN TO WAREHOUSE AT DESTINATION.\n" +
                "            +BENEF'S CERTIFICATE THAT COPIES OF COMMERCIAL INVOICE, PACKING\n" +
                "             LIST AND ONE FULL SET OF NON-NEGOTIABLE SHIPPING DOCUMENTS\n" +
                "             HAVE BEEN AIRMAILED DIRECTLY TO BUYER INCLUDING ORIGINAL,\n" +
                "             DUPLICATE, TRIPLICATE CERTIFICATE OF ORIGIN VIA DHL.",messageBlock.getTagValue("46A"));
        assertEquals("+OCEAN BILL OF LADING MUST BE DATED WITHIN THE\n" +
                "             VALIDITY PERIOD OF THIS CREDIT\n" +
                "            +ALL DOCUMENTS MUST INDICATE COMMODITY CLASSIFICATION\n" +
                "            CODE AND LC NUMBER AS INDICATED ABOVE.\n" +
                "            +BL       DATED PRIOR TO ISSUANCE OF THIS CREDIT NOT ALLOWED.\n" +
                "            +A FEE OF USD  60.00 (OR EQUIVALENT) WILL BE CHARGED TO THE\n" +
                "            BENEFICIARY IF DOCUMENTS CONTAINING DISCREPANCIES ARE\n" +
                "            PRESENTED FOR PAYMENT/REIMBURSEMENT UNDER THIS LC.  THIS FEE\n" +
                "            WILL BE CHARGED FOR EACH SET OF DISCREPANT DOCUMENTS PRESENTED\n" +
                "            WHICH REQUIRE OUR OBTAINING ACCEPTANCE FROM OUR CUSTOMER.\n" +
                "            ++/-10PCT TOLERANCE ON AMOUNT AND QUANTITY ALLOWED.\n" +
                "            +REIMBURSEMENT CHARGES, ACCEPTANCE FEE AND STAMP DUTY (IF ANY)\n" +
                "             ARE FOR APPLICANT'S ACCOUNT.\n" +
                "            +ALL CHARGES INCURRED IN BENEF'S COUNTRY ARE FOR BENEF'S ACCOUNT\n" +
                "             AND ALL CHARGES INCURRED IN APPLICANT'S COUNTRY ARE FOR\n" +
                "             APPLICANT'S ACCOUNT.\n" +
                "            +SHIPPER TO FAX SHIPPING DETAILS AFTER SHIPMENT IS EFFECTED AND\n" +
                "             A CERTIFICATE TO THIS EFFECT IS REQUIRED.",messageBlock.getTagValue("47A"));
        assertEquals("ALL CHARGES OUTSIDE THE PHILIPPINES\n" +
                "            ARE FOR THE ACCOUNT OF BENEFICIARY\n" +
                "            EXCEPT REIMBURSING CHARGES WHICH\n" +
                "            ARE FOR APPLICANT'S ACCOUNT.",messageBlock.getTagValue("71B"));
        assertEquals("WITHOUT",messageBlock.getTagValue("49"));
        assertEquals("/2000090649754\n" +
                "            PNBPUS33PHL\n" +
                "            WACHOVIA BANK, NA\n" +
                "            (INTERNATIONAL OPERATIONS)\n" +
                "            PHILADELPHIA,PA  US",messageBlock.getTagValue("53A"));
        assertEquals("+NEGOTIATING BANK MUST FORWARD ALL DOCS NEGOTIATED UNDER THIS\n" +
                "            CREDIT TO UNITED COCONUT PLANTERS BANK -TRADE SERVICES DEPARTMENT\n" +
                "            AT UCPB BLDG., MAKATI AVENUE, MAKATI CITY\n" +
                "            IN ONE LOT VIA COURIER.+DRAFTS DRAWN UNDER THIS CREDIT MUST BE\n" +
                "            MARKED DRAWN UNDER LC NO.FX56202008004972   WE UNDERTAKE TO HONOR\n" +
                "            DRAFT/S DRAWN AND PRESENTED IN COMPLIANCE WITH TERMS AND\n" +
                "            CONDITIONS OF THIS CREDIT +THIS IS THE OPERATIVE INSTRUMENT.",messageBlock.getTagValue("78"));

    }

    //This test case doesn't really belong here. But should i even bother to make another class?
    //What will i name it, if ever? For the meantime, the test case is here
    @Test
    public void splitMessageTest() throws IOException {
        String fileContents = FileUtil.getFileAsString("/swift/messages/incoming/00220002.PRT");
        String pattern = "\\s\\-{21}  Instance Type and Transmission \\-{14}\\s.+?(?=(\\s\\-{21}  Instance Type and Transmission \\-{14}\\s|\\z))";
        Matcher matcher = Pattern.compile(pattern,Pattern.DOTALL).matcher(fileContents);

        assertTrue(matcher.find());
        System.out.println(matcher.group(0));
        System.out.println("**************************************");
        assertTrue(matcher.find());
        System.out.println(matcher.group(0));


    }

    @Test
    public void successfullyConvertAmountToSwiftFormat(){
        String originalFormat = "Currency       : USD (US DOLLAR)\n" +
                "            Amount         :                     #1,#";
        Matcher matcher = Pattern.compile(SwiftFormattedMessageParser.CURRENCY_AMOUNT_FORMAT).matcher(originalFormat);
        assertTrue(matcher.find());
        assertEquals("USD",matcher.group(1));
        assertEquals("1,",matcher.group(2));
    }

    @Test
    public void randomRegexTest(){
        assertEquals("1,000,00","1,000.00".replaceAll("\\.",","));
        String fieldValue = "            Currency       : JPY (YEN)\n" +
                "            Amount         :                    #15,#";
        assertTrue(fieldValue.matches("([.\\s\\w\\Q:()#,\\E]+)"));

    }

}
