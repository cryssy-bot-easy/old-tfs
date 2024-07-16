package com.ucpb.tfs.swift;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 */
@Ignore("Convenience class for testing regexes. Not really a test class?")
public class SwiftMessageRegexTest {

    private SwiftMessageParser swiftMessageParser;

    private static File validSwiftMessage;

    private static StringBuilder swiftMessage = new StringBuilder();
    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String APP_HEADER = "\\{(2:).+\\}";
    private static final String APP_HEADER_GROUPING = "\\{(2):([IO])(\\d{3})(.+)\\}";
    private static final String BASIC_HEADER = "(\\{(1):[^_}]+\\})?";
    private static final String BASIC_HEADER_GROUPING = "\\{(1):([A-Z])(\\d{2})([\\w[^_]]{12})(\\d{4})(\\d{6})\\}";
    private static final String MESSAGE_BODY = "\\{(4):(\\s.*)*\\Q-}\\E";
    private static final String MESSAGE_BODY_GROUPING = ":(\\d+\\w*):(([^:].+\\s)+)";

    private static final String OUTPUT_APPLICATION_HEADER = "\\{(2):([IO])(\\d{3})(\\d{4})(\\d{6}[\\w[^_]]{11,12}\\d{10})(\\d{6})(\\d{4})\\w\\}";

    @BeforeClass
    public static void setup() throws IOException {
        validSwiftMessage = new File("tfs-interfaces/src/test/resources/swift/5620MT7X.089");
        assertTrue(validSwiftMessage.exists());

        BufferedReader reader = new BufferedReader(new FileReader(validSwiftMessage));
        String line = null;
        while((line = reader.readLine()) != null){
            swiftMessage.append(line);
            swiftMessage.append('\n');
        }
    }

    @Test
    public void spaceMetaCharacterMatchesCrlf(){
        assertTrue("\r\n".matches("\\s\\s"));
    }

    @Test
    public void retrieveHeader(){
        Pattern pattern = Pattern.compile("\\{4:\\s*((:\\d+\\w?:[.:\\w\\d\\s'/\\.,\\(\\)\\+]+)*)\\s*\\Q-}\\E",Pattern.DOTALL);
        Matcher matcher = pattern.matcher("\u0001{1:F21UCPBPHMMAXXX0337002389}{4:{177:1302250923}{451:0}}{1:F01UCPBPHMMAXXX0337002389}{2:O7600931130225UCPBPHMMAXXX03370012231302250931N}{4:\r\n:27:1/1\r\n:20:DM56202012010774\r\n:23:ISSUE\r\n" +
                ":30:121109\r\n" +
                ":40C:ISPR\r\n" +
                ":77C:Beneficiary: MIGHTY CORPORATION\r\n" +
                "             9110 Sultana Corner\r\n" +
                "             Trabajo Streets Makati\r\n" +
                "             Philippines\r\n" +
                ".\r\n" +
                "Gentlemen,\r\n" +
                ".\r\n" +
                "WHEREAS, Marigold Corporation with office address KM 9, Mc\r\n" +
                "Arthur Highway, Talomo, Thailand, (hereinafter called ''The\r\n" +
                "Buyer'') has requested to issue a Irrevocable Foreign Standby\r\n" +
                "Letter of Credit in favor of MIGHTY CORPORATION, 9110 Sultana\r\n" +
                "Corner, Trabajo Streets, Makati (hereinafter called ''The\r\n" +
                "Seller'') for Thai Baht: FIFTY MILLION BAHT ONLY\r\n" +
                "This represents guarantee payment for the purchase of\r\n" +
                "Various Products\r\n" +
                ":72:BENCON\r\n-}");
        assertTrue(matcher.find());
    }

    @Test
    public void retrieveMessageBodyTags(){
        Pattern pattern = Pattern.compile("\\{4:\\s*((:\\d+\\w?:[.:\\w\\d\\s'/\\.,\\(\\)\\+]+)*)\\s*\\Q-}\\E",Pattern.DOTALL);
        Matcher matcher = pattern.matcher("{4:\r\n:27:1/1\r\n:20:DM56202012010774\r\n:23:ISSUE\r\n" +
                ":30:121109\r\n" +
                ":40C:ISPR\r\n" +
                ":77C:Beneficiary: MIGHTY CORPORATION\r\n" +
                "             9110 Sultana Corner\r\n" +
                "             Trabajo Streets Makati\r\n" +
                "             Philippines\r\n" +
                ".\r\n" +
                "Gentlemen,\r\n" +
                ".\r\n" +
                "WHEREAS, Marigold Corporation with office address KM 9, Mc\r\n" +
                "Arthur Highway, Talomo, Thailand, (hereinafter called ''The\r\n" +
                "Buyer'') has requested to issue a Irrevocable Foreign Standby\r\n" +
                "Letter of Credit in favor of MIGHTY CORPORATION, 9110 Sultana\r\n" +
                "Corner, Trabajo Streets, Makati (hereinafter called ''The\r\n" +
                "Seller'') for Thai Baht: FIFTY MILLION BAHT ONLY\r\n" +
                "This represents guarantee payment for the purchase of\r\n" +
                "Various Products\r\n" +
                ":72:BENCON\r\n-}");
        assertTrue(matcher.find());
    }


    @Test
    public void matches(){
       assertTrue("{2:I700PNBPUS33XPHLN}".matches(APP_HEADER));
    }

    @Test
    public void successfullyGetApplicationHeaderBlock(){
//        System.out.println(swiftMessage.toString());

        Pattern pattern = Pattern.compile(APP_HEADER);
        Matcher matcher = pattern.matcher(swiftMessage.toString());
        assertTrue(matcher.find());
        assertEquals("{2:I700PNBPUS33XPHLN}",matcher.group());
    }

    @Test
    public void successfullyGroupApplicationHeaderParts(){
        Pattern pattern = Pattern.compile(APP_HEADER_GROUPING);
        Matcher matcher = pattern.matcher(swiftMessage.toString());
        assertTrue(matcher.find());
        assertEquals("{2:I700PNBPUS33XPHLN}",matcher.group());
        assertEquals("2",matcher.group(1));
        assertEquals("I",matcher.group(2));
        assertEquals("700",matcher.group(3));
        assertEquals("PNBPUS33XPHLN",matcher.group(4));
    }

    @Test
    public void successfullyGetMessageBlock(){
        Pattern pattern = Pattern.compile(MESSAGE_BODY);
        Matcher matcher = pattern.matcher(swiftMessage.toString());
        assertTrue(matcher.find());
        assertEquals("{4:\n" +
                ":27:1/1\n" +
                ":40A:IRREVOCABLE\n" +
                ":20:FX56202011005203\n" +
                ":31C:110803\n" +
                ":40E:UCP LATEST VERSION\n" +
                ":31D:111002 IN USA\n" +
                ":50:MIGHTY CORP MIGHTY CORP\n" +
                ":59:LETTY MANILA\n" +
                ":32B:USD100,00\n" +
                ":41D:ANY BANK\n" +
                "BY NEGOTIATION\n" +
                ":42C:SIGHT\n" +
                ":42A:UCPBPHMM\n" +
                ":43P:NOT ALLOWED\n" +
                ":43T:NOT ALLOWED\n" +
                ":44E:USA\n" +
                ":44F:MLA\n" +
                ":44C:111002\n" +
                ":45A:+ TOYS\n" +
                "+CANDIES\n" +
                "+PHILIPPINE STANDARD COMMODITY CLASSIFICATION CODE :783.11-03 \n" +
                "+FOB\n" +
                ":46A:+SIGNED COMMERCIAL INVOICE IN  TRIPLICATE\n" +
                "+PACKING LIST\n" +
                "+ONE FULL SET OF AT LEAST THREE ORIGINAL CLEAN 'ON BOARD' OCEAN\n" +
                "BILLS OF LADING IN NEGOTIABLE AND TRANSFERABLE FORM AND ONE NON-\n" +
                "NEGOTIABLE COPY ISSUED TO THE ORDER OF UNITED COCONUT PLANTERS \n" +
                "BANK MARKED FREIGHT COLLECT NOTIFY\n" +
                "APPLICANT\n" +
                ":47A:+OCEAN BILL OF LADING MUST BE DATED WITHIN THE\n" +
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
                "WHICH HOLDS SPECIAL PAYMENT AND REIMBURSEMENT INSTRUCTIONS.\n" +
                ":71B:ALL CHARGES OUTSIDE THE PHILIPPINES\n" +
                "ARE FOR THE ACCOUNT OF BENEFICIARY\n" +
                "INCLUDING REIMBURSING CHARGES\n" +
                ":49:WITHOUT\n" +
                ":53D:/36143038\n" +
                "WELLS FARGO BANK, NY\n" +
                ":78:+NEGOTIATING BANK MUST ADVISE US OF NEGOTIATION DETAILS BY TESTED\n" +
                "CABLE AND ANY ADDITIONAL TRANSIT INTEREST THAT MAY ARISE FOR NON-\n" +
                "COMPLIANCE SHALL BE FOR THE ACCOUNT OF NEGOTIATING BANK\n" +
                "+NEGOTIATING BANK MUST FORWARD ALL DOCS NEGOTIATED UNDER THIS\n" +
                "CREDIT TO UNITED COCONUT PLANTERS BANK -TRADE SERVICES DEPARTMENT\n" +
                "AT UCPB BLDG., MAKATI AVENUE, MAKATI CITY\n" +
                "IN ONE LOT VIA COURIER.+DRAFTS DRAWN UNDER THIS CREDIT MUST BE\n" +
                "MARKED DRAWN UNDER LC NO.FX56202011005203   WE UNDERTAKE TO HONOR\n" +
                "DRAFT/S DRAWN AND PRESENTED IN COMPLIANCE WITH TERMS AND\n" +
                "CONDITIONS OF THIS CREDIT +THIS IS THE OPERATIVE INSTRUMENT.\n" +
                ":57D:BANK OF TOKYO\n" +
                "-}",matcher.group());
    }

    @Test
    public void successfullyGroupMessageBodyParts(){
        System.out.println(swiftMessage.toString());
        Pattern pattern = Pattern.compile(MESSAGE_BODY_GROUPING);
        Matcher matcher = pattern.matcher(swiftMessage.toString());
        assertTrue(matcher.find());
        assertEquals(":27:1/1", StringUtils.trimWhitespace(matcher.group()));
        assertEquals("27",StringUtils.trimWhitespace(matcher.group(1)));
        assertEquals("1/1",StringUtils.trimWhitespace(matcher.group(2)));

        matcher.find();
        assertEquals(":40A:IRREVOCABLE",StringUtils.trimWhitespace(matcher.group()));
        assertEquals("40A",StringUtils.trimWhitespace(matcher.group(1)));
        assertEquals("IRREVOCABLE",StringUtils.trimWhitespace(matcher.group(2)));

        matcher.find();
        assertEquals(":20:FX56202011005203",StringUtils.trimWhitespace(matcher.group()));
        assertEquals("20",StringUtils.trimWhitespace(matcher.group(1)));
        assertEquals("FX56202011005203",StringUtils.trimWhitespace(matcher.group(2)));

        matcher.find();
        assertEquals(":31C:110803",StringUtils.trimWhitespace(matcher.group()));
        assertEquals("31C",StringUtils.trimWhitespace(matcher.group(1)));
        assertEquals("110803",StringUtils.trimWhitespace(matcher.group(2)));
    }

    @Test
    public void successfullyGetBasicHeader(){
        Pattern pattern = Pattern.compile(BASIC_HEADER);
        Matcher matcher = pattern.matcher(swiftMessage.toString());
        assertTrue(matcher.find());
        assertEquals("{1:F01UCPBPHMMAXXX0000000000}",matcher.group());
    }

    @Test
    public void successfullyParseBasicHeader(){
        Pattern pattern = Pattern.compile(BASIC_HEADER_GROUPING);
        Matcher matcher = pattern.matcher(swiftMessage.toString());
        assertTrue(matcher.find());
        assertEquals("{1:F01UCPBPHMMAXXX0000000000}",matcher.group());
        assertEquals("1",matcher.group(1));
        assertEquals("F",matcher.group(2));
        assertEquals("01",matcher.group(3));
        assertEquals("UCPBPHMMAXXX",matcher.group(4));
        assertEquals("0000",matcher.group(5));
        assertEquals("000000",matcher.group(6));

    }



}
