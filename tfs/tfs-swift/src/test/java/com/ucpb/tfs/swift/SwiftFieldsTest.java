package com.ucpb.tfs.swift;

import com.ucpb.tfs.swift.message.SwiftFields;
import org.junit.Ignore;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 */
public class SwiftFieldsTest {

    @Test
    public void successfullyValidateMT700Field31D(){
       String regex = "\\d{6}[a-zA-Z\\Q/-?:().,'+\\E\\s]{0,29}";
        assertTrue("110905 IN USA".matches(regex));
    }

    @Test
    public void validateAmountField(){
        String regex = "[a-zA-Z]{3}[\\d,]{0,15}";
        assertTrue("USD100,00".matches(regex));
    }

    @Test
    public void validateNotesFormat(){
        String regex = "(([a-zA-Z0-9\\Q/-?:().,'+\\E\\s]{0,65})){0,100}";
        String value = "+ CANDIES\n" +
                "+CANDIES\n" +
                "+PHILIPPINE STANDARD COMMODITY CLASSIFICATION CODE :783.11-03 \n" +
                "+FOB";
        assertTrue(value.matches(regex));
    }

    @Test
    public void validateNotesFormat2(){
        String regex = "(([a-zA-Z0-9\\Q/-?:().,'+\\E\\s]{0,65})){0,100}";
        String value = "+ TOYS\n" +
                "+CANDIES\n" +
                "+PHILIPPINE STANDARD COMMODITY CLASSIFICATION CODE :783.11-03 \n" +
                "+FOB";
        assertTrue(value.matches(regex));
    }

    @Test
    public void validXdataType(){
        String value = "+ CANDIES\n";
        String regex = "(([a-zA-Z0-9\\Q/-?:().,'+\\E\\s]{0,65})){0,100}";
        assertTrue(value.matches(regex));

    }

    @Test
    public void validCommentsDataType(){
        String regex = "([a-zA-Z0-9\\Q/-?:().,'+\\E\\s]{0,35}){0,4}";
        String value = "ALL CHARGES OUTSIDE THE PHILIPPINES\n" +
                "ARE FOR THE ACCOUNT OF BENEFICIARY\n" +
                "INCLUDING REIMBURSING CHARGES\n" +
                "WAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n";
        assertTrue(value.matches(regex));
    }

    @Test
    public void validNotesDataType(){
        String regex =  "([a-zA-Z0-9\\Q/-?:().,'+\\E\\s]{0,65}){0,100}";
        String value = "+ CANDIES\n" +
                "+CANDIES\n" +
                "+PHILIPPINE STANDARD COMMODITY CLASSIFICATION CODE :783.11-03 \n" +
                "+FOB";
        assertTrue(value.matches(regex));
    }

    @Test
    public void successfulOnValidApplicableFields(){
        String value = "UCP LATEST VERSION";
        assertFalse("UCP LATEST VERSION".contains("OTHR"));
        assertTrue(value.matches(SwiftFields.APPLICABLE_RULES_ONLY));
    }

    @Test
    public void successfulOnValidApplicableFieldsWithNarrative(){
        assertTrue("OTHR/12BLAHBLAHBLAH".matches(SwiftFields.RULES_WITH_NARRATIVE));
    }

    @Test
    public void validDocumentNumberForValuesWithNoSlashes(){
        assertTrue("VALIDDOCNUMBER".matches(SwiftFields.DOCUMENT_NUMBER));
    }

    @Test
    public void validDocumentNumberForValuesWithSingleSlashesInMiddle(){
        assertTrue("VALI/DDOCNU/MBER".matches(SwiftFields.DOCUMENT_NUMBER));
    }

//    @Test
    public void validPartyIdentifierAndIdentifierCode(){
        String partyIdentifier = "/C/12-12\n" +
                "CITIUS33CHI";
        assertTrue(partyIdentifier.matches(SwiftFields.PARTY_IDENTIFIER_AND_CODE));

        String identifier2 = "/52/48-48\n" +
                "John Doe\n" +
                "122 Peyton Place\n" +
                "Elyria, OH 22216";

        assertTrue(identifier2.matches("(/\\w)?(/[\\w\\d\\Q/-?:().,'+\\E\\s]{0,40})"));

    }

    @Test
    public void validIdentifier(){
        //"(/\\w)?(/[a-zA-Z0-9]{0,34}[\\s])?\\w{4}\\w{2}\\w{2}(\\w{3})?"
        assertTrue("/C".matches("(/\\w)?"));
        assertTrue("/12-12".matches("/([a-zA-Z0-9\\Q/-?:().,'+\\E]{0,34})"));
    }

    @Test
    public void matcherTest(){
        String json = "{\"documentSubType2\":\"SIGHT\",\"documentSubType1\":\"CASH\",\"etsNu\n" +
                "mber\":\"\",\"documentType\":\"FOREIGN\",\"bankCommissionNumerator\":1.0,\"confirmingFeeNu\n" +
                "merator\":1.0,\"mainCifName\":\"\",\"accountOfficer\":\"\",\"confirmingFeeDenominator\":4.0\n" +
                ",\"cilexNumerator\":1.0,\"type\":\"CASH\",\"documentClass\":\"LC\",\"mainCifNumber\":\"\",\"cur\n" +
                "rency\":\"PHP\",\"amount\":\"333333333.00\",\"confirmationInstructionsFlag\":\"Y\",\"commitm\n" +
                "entFeeDenominator\":4.0,\"processingUnitCode\":\"909\",\"referenceType\":\"ETS\",\"cifNumb\n" +
                "er\":\"\",\"tenor\":\"SIGHT\",\"bankCommissionDenominator\":8.0,\"tmp\":833333.3325,\"docume\n" +
                "ntNumber\":\"\",\"twentyDollarMinimum\":0.0,\"ccbdBranchUnitCode\":\"\",\"notarialAmount\":\n" +
                "800.0,\"marineInsurance\":\"\",\"etsDate\":\"10/09/2012\",\"priceTerm\":\"\",\"generalDescrip\n" +
                "tionOfGoods\":\"goods and services\",\"expiryDate\":\"10/25/2012\",\"otherPriceTerm\":\"\",\n" +
                "\"issueDate\":\"10/09/2012\",\"advisingFee\":20.0,\"suppliesFee\":50.0,\"advising\":\"Y\",\"s\n" +
                "erviceType\":\"Opening\",\"cableFee\":800.0,\"cilexDenominator\":8.0,\"cwtFlag\":\"Y\",\"mon\n" +
                "ths\":1.0,\"cifName\":\"\",\"usdToPHPSpecialRate\":0.0,\"commitmentFeeNumerator\":1.0,\"ad\n" +
                "vanceCorresChargesFlag\":\"Y\",\"fiftyDollarMinimum\":0.0}";
        String searchThis = json.replaceAll("\n","");
        System.out.println("****** " + searchThis);
        Matcher matcher = Pattern.compile("\"(\\w+)\":[\"]?([\\w\\d\\s\\Q!@#%^&*().-+\\E/]*)[\"]?,?").matcher(searchThis);
        System.out.println(matcher.pattern().pattern());
        assertTrue(matcher.find());
         while(matcher.find()){
             System.out.println(matcher.groupCount());
             System.out.println("*" + matcher.group(0)+ "*");
             System.out.println(matcher.group(1) + " : " + matcher.group(2));
         }
    }

    @Test
    public void periodCharacterTest(){
        assertTrue("a".matches("."));
        assertTrue("1".matches("."));
        assertTrue("#".matches("."));
        assertTrue(" ".matches("."));
        assertTrue(".".matches("."));
        assertTrue("\\".matches("\\\\"));
//        assertTrue("\n".matches("."));
    }

}
