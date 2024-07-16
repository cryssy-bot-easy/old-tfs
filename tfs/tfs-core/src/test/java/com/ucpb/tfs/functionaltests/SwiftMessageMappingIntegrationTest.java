package com.ucpb.tfs.functionaltests;

import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.product.ICNumber;
import com.ucpb.tfs.domain.product.LCNegotiationDiscrepancy;
import com.ucpb.tfs.domain.product.LetterOfCredit;
import com.ucpb.tfs.domain.product.event.LCAmendedEvent;
import com.ucpb.tfs.domain.product.event.LCNegotiationDiscrepancyCreatedEvent;
import com.ucpb.tfs.domain.product.event.LetterOfCreditCreatedEvent;
import com.ucpb.tfs.domain.service.TradeProductNumber;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.enumTypes.DocumentClass;
import com.ucpb.tfs.domain.service.enumTypes.DocumentType;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import com.ucpb.tfs.swift.message.MessageBlock;
import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.swift.message.Tag;
import com.ucpb.tfs.swift.message.builder.SwiftMessageBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:swift/message-builder.xml")
public class SwiftMessageMappingIntegrationTest {

    @Autowired
    private SwiftMessageBuilder builder;

    @Test
    public void successfullyMapValidMt760Details(){
        Map<String,Object> details = asMap(
                "furtherIdentification","ISSUE",
                "issueDate","03/13/2013",
                "applicableRules","ISPR",
                "detailsOfGuarantee","77C: Details of Guarantee\r\n             WE HAVE ESTABLISHED OUR IRREVOCABLE LETTER OF \r\n               CREDIT IN YOUR FAVOR AS DETAILED HEREIN SUBJECT TO SAUDI  LAW\r\n             BENEFICIARY\r\n                TABAQUERIA \r\n             APPLICANT\r\n                JJED PHILIPPINES INC \r\n             DATE AND PLACE OF EXPIRY\r\n                MARCH 15, 2013, __________\r\n               AT OUR COUNTERS\r\n             DOCUMENTARY CREDIT AMOUNT\r\n                RUB 5,568,945.00\r\n             AVAILABLE WITH\r\n                ANY BANK \r\n                __________\r\n                BY PAYMENT\r\n             ADDITIONAL DETAILS\r\n                BENEFICIARY:\r\n               XXXXXXXXXXXXXXXXXX\r\n               .\r\n               AT THE REQUEST OF OUR CUSTOMER JJED PHILIPPINES INC,. \r\n               \\u0027\\\"THE APPLICANT\\u0027\\\" WITH ADDRESS  AT 3F JJED BLDG, 54 P CRUZ STREET., \r\n            AND SUBJECT TO THE TERMS\r\n            AND CONDITIONS BELOW, WE HEREBY IRREVOCABLY AND UNCONDITIONALLY    \r\n            UNDERTAKE TO PAY TO YOU, WITHOUT REGARD TO ANY\r\n            OBJECTION, NOTICE OR CLAIM OF ANY KIND FROM THE\r\n            APPLICANT OR ANY OTHER PARTY OR ANY DISCUSSION\r\n            WHATSOEVER, AN AMOUNT OF RUB 5,568,945.00 UPON\r\n            YOUR REQUEST AT  OR AT SUCH OTHER ACCOUNT\r\n            AS MAY BE NOTIFIED TO US BY YOU UPON WRITTEN\r\n            TESTED TELEX OR AUTHENTICATED SWIFT DECLARATION\r\n            PRIOR TO PAYMENT REQUEST.\r\n            .\r\n             THIS LETTER OF CREDIT IS AVAILABLE FOR PAYMENT AT\r\n            OUR COUNTERS AT SIGHT. ONLY ONE DRAWING IN FULL AMOUNT  \r\n            MADE UNDER THIS LETTER OF CREDIT.\r\n            .\r\n            PAYMENT AS DETAILED ABOVE WILL BE EFFECTED BY US\r\n            WITHOUT REQUIREMENT OF ANY DOCUMENTARY\r\n            PRESENTATION ON YOUR PART.\r\n            .\r\n            THIS LETTER OF CREDIT WILL EXPIRE ON\r\n            MARCH 15, 2013 \r\n           .\r\n            THE BENEFIT OF THIS LETTER OF CREDIT IS NOT\r\n            ASSIGNABLE IN WHOLE OR IN PART.\r\n            .\r\n            THIS LETTER OF CREDIT IS IRREVOCABLE. \r\n            .\r\n            THIS LETTER OF CREDIT IS SUBJECT TO SAUDI LAW.\r\n            .\r\n            WE HEREIN CONFIRM ALL NECESSARY APPROVALS FOR THE ISSUANCE OF THIS LETTER\r\n            OF CREDIT HAD BEEN OBTAINED AND ARE IN FULL FORCE AND EFFECT, AND NOTHING \r\n            SHALL AFFECT OUR LIABILITY TO MAKE PAYMENT TO YOU UNDER THIS LETTER OF CREDIT.\r\n            WITHOUT PREJUDICE TO YOUR RIGHT TO SUBMIT TO ANY OTHER LAW, JURISDICUTION\r\n            AND VENUE, THIS LETTER OF CREDIT SHALL BE SUBJECT TO, GOVERNED BY AND \r\n            CONSTRUED IN ACCORDANCE WITH THE LAWS, RULES AND REGULATIONS OF KINGDOM\r\n            OF SAUDI ARABIA(KSA), AND IN THE EVENT OF ANY DISPUTE  ARISING  UNDER THIS LETTER\r\n            OF CREDIT, THE FORM OF JURISDICTION SHALL BE THE COMPETENT COURT IN RIYADH,KSA.\r\n\""
        );
        TradeService ts = new TradeService(
                new DocumentNumber("1-01-909-12-00011-9"),
                new TradeProductNumber("1-01-909-12-00011-9"),
                DocumentClass.LC,
                DocumentType.FOREIGN,
                null, //DocumentSubType1
                null, //DocumentSubType2
                ServiceType.OPENING,
                new UserActiveDirectoryId("branchm"),
                "TR-1234567-89");
        ts.updateDetails(details, new UserActiveDirectoryId("branchm"));

        List<RawSwiftMessage> messages = builder.build("760",ts);
        assertEquals(1,messages.size());

        RawSwiftMessage message = messages.get(0);
        MessageBlock contents = message.getMessageBlock();
        assertEquals("ISSUE",contents.getTagValue("23"));
        assertEquals("ISPR",contents.getTagValue("40C"));
        assertTrue(contents.getTagValue("77C").contains("DETAILS OF GUARANTEE"));
    }

    @Test
    public void mapOrderingBankAccountNumberIfPresent(){
        Map<String,Object> details = asMap(

                "orderingAccountNumber","ACCOUNTNUMBER",
                "orderingAddress","orderingAddrezz"


        );
        TradeService ts = new TradeService(
                new DocumentNumber("1-01-909-12-00011-9"),
                new TradeProductNumber("1-01-909-12-00011-9"),
                DocumentClass.DP,
                DocumentType.FOREIGN,
                null, //DocumentSubType1
                null, //DocumentSubType2
                ServiceType.SETTLEMENT,
                new UserActiveDirectoryId("branchm"),
                "TR-1234567-89");
        ts.updateDetails(details, new UserActiveDirectoryId("branchm"));
        //any settlement created event
//    	DPSettlementCreatedEvent e = new DPSettlementCreatedEvent(ts);


        List<RawSwiftMessage> messages = builder.build("103",ts);
        RawSwiftMessage message = messages.get(0);
        MessageBlock block = message.getMessageBlock();
        printTags(block);
        assertEquals("/ACCOUNTNUMBER\r\nORDERINGADDREZZ",block.getTagValue("50K"));
    }

    @Test
    public void mapImporterNameIfOrderingAccountNumberIsNotPresent(){
        Map<String,Object> details = asMap(
                "importerName","IMPORTER NAME",
                "importerAddress","IMPORTER ADDREZZ"
        );
        TradeService ts = new TradeService(
                new DocumentNumber("1-01-909-12-00011-9"),
                new TradeProductNumber("1-01-909-12-00011-9"),
                DocumentClass.DP,
                DocumentType.FOREIGN,
                null, //DocumentSubType1
                null, //DocumentSubType2
                ServiceType.SETTLEMENT,
                new UserActiveDirectoryId("branchm"),
                "TR-1234567-89");
        ts.updateDetails(details, new UserActiveDirectoryId("branchm"));
        //any settlement created event
//    	DPSettlementCreatedEvent e = new DPSettlementCreatedEvent(ts);


        List<RawSwiftMessage> messages = builder.build("103",ts);
        RawSwiftMessage message = messages.get(0);
        MessageBlock block = message.getMessageBlock();
        printTags(block);
        assertEquals("IMPORTER NAME\r\nIMPORTER ADDREZZ",block.getTagValue("50K"));
    }

    @Test
    public void successfulMappingOfMt103(){
   	
    	Map<String,Object> details = asMap(
                "accountWithInstitution","accountWithInstitution",
    			"timeIndication","12345",
                "timeIndicationField","TIME FIELD",
                "bankOperationCode","code123",
                "bankOperationTextArea","ADDITIONAL INFORMATION",
    			"instructionCode","code456",
    			"transactionTypeCode","code789",
    			"processDate","12/30/2009",
    			"currency","PHP",
    			"productAmount","1000.00",
    			"exchangeRate","1212.500",
    			"importerName","Importer Name",
                "importerAddress","Importer Address",
    			"sendingInstitution","Institution",

    			"orderInstIdentifierCode","Ordering",
                "bankNameAndAddress","Ordering institution name and address tralalala lalalalala",

    			"senderIdentifierCode","Sender",
                "sendersPartyIdentifier","Sender Location",
                "senderNameAndAddress","Sender Name and Address",

    			"receiverCorrIdentifierCode","Receiver",
                "receiverPartyIdentifier","receiver party identi",
                "receiverLocation","receiver location",
                "receiverNameAndAddress","Receiver address",

    			"thirdReimbursementInstitution","3rdInstitution",

    			"intermedIdentifierCode","intermediaryInstitution",

    			"acctWithInstIdentifierCode","accountInstitution",
                "acctWithInstPartyIdentifier","ACCOUNT IDENTIFIER",
                "accountLocation","ACCOUNT LOCATION",
                "accountNameAndAddress","ACCOUNT NAME AND ADDRESS",

                "beneficiaryAccountNumber","benefAccountNumber",
    			"beneficiaryName","BName",
    			"beneficiaryAddress","BAddress",
    			"remittanceInformation","remitInfo",
                "remittanceInformationTextArea","REMITTANCE NARRATION",
                "receiverCorrIdentifierCode","Receiver",
    			"detailsOfCharges","chargesDetails",
    			"senderChargesCurrency","PHP",
    			"senderCharges","12345.89",
    			"receiverChargesCurrency","USD",
    			"receiverCharges","123.5",
    			"senderToReceiverInformation","CODE1",
    			"senderToReceiverInformationTextArea","SENDERINFO",
    			"regulatoryReporting","CODE2",
    			"regulatoryReportingTextArea","REGREPORT",
    			"envelopeContents","CODE3",
    			"envelopeContentsTextArea","CONTENTS",
    			"receivingBank","AAAAAAAA"
    			
    	);
    	TradeService ts = new TradeService(
    					new DocumentNumber("1-01-909-12-00011-9"),
                        new TradeProductNumber("1-01-909-12-00011-9"),
    					DocumentClass.DP,
    					DocumentType.FOREIGN,
    					null, //DocumentSubType1
    					null, //DocumentSubType2
    					ServiceType.SETTLEMENT,
    					new UserActiveDirectoryId("branchm"),
    					"TR-1234567-89");
    	ts.updateDetails(details, new UserActiveDirectoryId("branchm"));
    	//any settlement created event
//    	DPSettlementCreatedEvent e = new DPSettlementCreatedEvent(ts);


		List<RawSwiftMessage> messages = builder.build("103",ts);
		RawSwiftMessage message = messages.get(0);
    	MessageBlock block = message.getMessageBlock();
    	printTags(block);
        assertEquals("AAAAAAAA",message.getApplicationHeader().getReceiverAddress().getBankIdentifierCode());
    	assertEquals("10190912000119",block.getReference());
    	assertEquals("/12345/TIME FIELD",block.getTagValue("13C"));
    	assertEquals("CODE123/ADDITIONAL INFORMATION",block.getTagValue("23B"));
    	assertEquals("CODE456",block.getTagValue("23E"));
    	assertEquals("CODE789",block.getTagValue("26T"));
    	assertEquals("091230PHP1000,",block.getTagValue("32A"));
    	assertEquals("PHP1000,",block.getTagValue("33B"));
    	assertEquals("1212,500",block.getTagValue("36"));
    	assertEquals("IMPORTER NAME" + "\r\n" + "IMPORTER ADDRESS",block.getTagValue("50K"));

        assertEquals("ORDERING",block.getTagValue("52A"));
        assertEquals("ORDERING INSTITUTION NAME AND \r\n" +
                "ADDRESS TRALALALA LALALALALA",block.getTagValue("52D"));


        assertEquals("SENDER",block.getTagValue("53A"));
        assertEquals("/SENDER LOCATION",block.getTagValue("53B"));
        assertEquals("SENDER NAME AND ADDRESS",block.getTagValue("53D"));

        assertEquals("RECEIVER",block.getTagValue("54A"));
        assertEquals("/RECEIVER PARTY IDENTI\r\nRECEIVER LOCATION",block.getTagValue("54B"));
        assertEquals("RECEIVER ADDRESS",block.getTagValue("54D"));

    	assertEquals("3RDINSTITUTION",block.getTagValue("55A"));

    	assertEquals("ACCOUNTINSTITUTION",block.getTagValue("57A"));
        assertEquals("/ACCOUNT IDENTIFIER\r\nACCOUNT LOCATION",block.getTagValue("57B"));
        assertEquals("/ACCOUNTWITHINSTITUTION\r\nACCOUNT NAME AND ADDRESS",block.getTagValue("57D"));

        assertEquals("/BENEFACCOUNTNUMBER\r\nBNAME\r\nBADDRESS",block.getTagValue("59"));

        assertEquals("/REMITINFO/REMITTANCE NARRATION",block.getTagValue("70"));
    	assertEquals("CHARGESDETAILS",block.getTagValue("71A"));
    	assertEquals("PHP12345,89",block.getTagValue("71F"));
    	assertEquals("USD123,50",block.getTagValue("71G"));
    	assertEquals("/CODE1/SENDERINFO",block.getTagValue("72"));
    	assertEquals("/CODE2/REGREPORT",block.getTagValue("77B"));
    	assertEquals("/CODE3/CONTENTS",block.getTagValue("77T"));
        assertEquals("AAAAAAAAXXXX",message.getApplicationHeader().getReceiverAddress().getAddressWithLtPadding());
    }

    @Test
    public void successfullyMapCompleteMt730Details(){

        Map<String,Object> details = asMap(
                "processDate","12/30/2009",
                "lcNumber","LC NUMBER",
                "senderToReceiverInformation","sender to receiver information",
                "issuingBank","ISSUINGBANK"
        );
        TradeService ts = new TradeService(
                new DocumentNumber("1-01-909-12-00011-9"),
                new TradeProductNumber("1-01-909-12-00011-9"),
                DocumentClass.EXPORT_ADVISING,
                DocumentType.FOREIGN,
                null, //DocumentSubType1
                null, //DocumentSubType2
                ServiceType.OPENING_ADVISING,
                new UserActiveDirectoryId("branchm"),
                "TR-1234567-89");
        ts.updateDetails(details, new UserActiveDirectoryId("branchm"));
        //any settlement created event
//    	DPSettlementCreatedEvent e = new DPSettlementCreatedEvent(ts);


        List<RawSwiftMessage> messages = builder.build("730",ts);
        RawSwiftMessage message = messages.get(0);
        MessageBlock block = message.getMessageBlock();
        printTags(block);
        assertEquals("10190912000119",block.getReference());
        assertEquals("ISSUINGB",message.getApplicationHeader().getReceiverAddress().getBankIdentifierCode());
        assertEquals("LC NUMBER",block.getTagValue("21"));
        assertEquals("091230",block.getTagValue("30"));
        assertEquals("SENDER TO RECEIVER INFORMATION",block.getTagValue("72"));

    }

    @Test
    public void successfulMappingOfMt103Plus(){

        Map<String,Object> details = asMap(
                "timeIndication","12345",
                "bankOperationCode","code123",
                "instructionOperationCode","code456",
                "transactionTypeCode","code789",
                "processDate","12/30/2009",
                "currency","PHP",
                "productAmount","1000.00",
                "exchangeRate","1212.500",
                "importerName","Importer Name",
                "importerAddress","Importer Address",
                "sendingInstitution","Institution",
                "orderInstIdentifierCode","Ordering",
                "senderIdentifierCode","Sender",
                "receiverCorrIdentifierCode","Receiver",
                "thirdReimbursementInstitution","3rdInstitution",
                "accountWithInstitution","accountInstitution",
                "beneficiaryName","BName",
                "beneficiaryAddress","BAddress",
                "remittanceInformation","contents",
                "remittanceInformationTextArea","REMITTANCE NARRATION",
                "detailsOfCharges","chargesDetails",
                "senderChargesCurrency","PHP",
                "senderCharges","12345.89",
                "receiverChargesCurrency","USD",
                "receiverCharges","123.5",
                "senderToReceiverInformation","SENDERINFO",
                "senderToReceiverInformationTextArea","text area sender to receiver information tralalalalalallalaalala ",
                "regulatoryReporting","code0980",
                "envelopeContents","contents",
                "detailsOfCharges","SHA",
                "receivingBank","ADDRESSSXDSD"
        );
        TradeService ts = new TradeService(
                new DocumentNumber("1-01-909-12-00011-9"),
                new TradeProductNumber("1-01-909-12-00011-9"),
                DocumentClass.DP,
                DocumentType.FOREIGN,
                null, //DocumentSubType1
                null, //DocumentSubType2
                ServiceType.SETTLEMENT,
                new UserActiveDirectoryId("branchm"),
                "TR-1234567-89");
        ts.updateDetails(details, new UserActiveDirectoryId("branchm"));
        //any settlement created event
//    	DPSettlementCreatedEvent e = new DPSettlementCreatedEvent(ts);


        List<RawSwiftMessage> messages = builder.build("103Plus",ts);
        RawSwiftMessage message = messages.get(0);
        MessageBlock block = message.getMessageBlock();
        printTags(block);

        assertEquals("10190912000119",block.getReference());
        assertEquals("/12345/",block.getTagValue("13C"));
        assertEquals("CODE123",block.getTagValue("23B"));
        assertEquals("CODE456",block.getTagValue("23E"));
        assertEquals("CODE789",block.getTagValue("26T"));
        assertEquals("091230PHP1000,",block.getTagValue("32A"));
        assertEquals("PHP1000,",block.getTagValue("33B"));
        assertEquals("1212,500",block.getTagValue("36"));
        assertEquals("IMPORTER NAME" + "\r\n" + "IMPORTER ADDRESS",block.getTagValue("50K"));
        assertEquals("INSTITUTION",block.getTagValue("51A"));
        assertEquals("3RDINSTITUTION",block.getTagValue("55A"));
        assertEquals("ACCOUNTINSTITUTION",block.getTagValue("57A"));
        assertEquals("BNAME" + "\r\n" + "BADDRESS",block.getTagValue("59"));

        assertEquals("/CONTENTS/REMITTANCE NARRATION",block.getTagValue("70"));
        assertEquals("SHA",block.getTagValue("71A"));
        assertEquals("PHP12345,89",block.getTagValue("71F"));
        assertEquals("USD123,50",block.getTagValue("71G"));
        assertEquals("/SENDERINFO/TEXT AREA SENDER TO \r\n" +
                "//RECEIVER INFORMATION \r\n" +
                "//TRALALALALALALLALAALALA",block.getTagValue("72"));
        assertEquals("CODE0980",block.getTagValue("77B"));
        assertEquals("/CONTENTS/",block.getTagValue("77T"));
        assertEquals("ADDRESSSXDSD",message.getApplicationHeader().getReceiverAddress().getAddressWithLtPadding());
    }
    
    @Test
    public void successfulMappingOfMt202(){
        Map<String,Object> nonLcDetails = asMap(
                "remittingBankReferenceNumber", "REM432",
                "processDate", "01/01/2013",
                "currency", "PHP",
                "timeIndicationMt202","CLSTIME",
                "timeIndicationFieldMt202","0915+0100",
                "bankIdentifierCodeMt202","INSTITUTION",
                "bankNameAndAddressMt202","inst name and add",
                "productAmount", "10000",
                "orderingInstitution","institution",
                "senderIdentifierCodeMt202","sender",
                "senderPartyIdentifierMt202","senders identifier",
                "senderLocationMt202","sender location",
                "senderNameAndAddressMt202","sender name and add",
                "receiverIdentifierCodeMt202","receiver",
                "receiver202PartyIdentifierMt202","receiver identifier",
                "receiverLocationMt202","receiver loc",
                "receiverNameAndAddressMt202","receiver name and add",
                "intermediaryIdentifierCodeMt202","intermediary",
                "intermediaryNameAndAddressMt202","intermediary name and add",
                "accountIdentifierCodeMt202","account institution",
                "accountWithBankIdentifierMt202","account partyIdentifier",
                "accountWithBankLocationMt202","account loc",
                "accountNameAndAddressMt202","account name and add",
                "beneficiaryBankPartyIDASelectDMt202","AT",
                "beneficiaryBankPartyIDATextMt202","ID A TEXT",
                "beneficiaryIdentifierCodeMt202","Identifier code",

                "beneficiaryBankPartyIDDSelectDMt202","BT",
                "beneficiaryBankPartyIDDTextMt202","ID D TEXT",
                "beneficiaryNameAndAddressMt202","beneficiary name and add",
                "senderToReceiverInformationMt202", "/BENCON/",
                "beneficiaryBankFlagMt202","A"
        );

        TradeService tradeService = new TradeService();
        tradeService.Service(new DocumentNumber("EXP98734"));
        tradeService.updateDetails(nonLcDetails, new UserActiveDirectoryId());
        
        List<RawSwiftMessage> messages = builder.build("202", tradeService);
        RawSwiftMessage message = messages.get(0);
        MessageBlock block = message.getMessageBlock();
        printTags(block);	


        assertEquals("EXP98734",block.getReference());	
        assertEquals("REM432",block.getRelatedReference());
        assertEquals("/CLSTIME/0915+0100",block.getTagValue("13C"));
        assertEquals("130101PHP10000,",block.getTagValue("32A"));
        assertEquals("INSTITUTION",block.getTagValue("52A"));
        assertEquals("INST NAME AND ADD",block.getTagValue("52D"));
        assertEquals("SENDER",block.getTagValue("53A"));
        assertEquals("/SENDERS IDENTIFIER\r\n" +
                "SENDER LOCATION",block.getTagValue("53B"));
        assertEquals("SENDER NAME AND ADD",block.getTagValue("53D"));
        assertEquals("RECEIVER",block.getTagValue("54A"));
        assertEquals("/RECEIVER IDENTIFIER\r\nRECEIVER LOC",block.getTagValue("54B"));
        assertEquals("RECEIVER NAME AND ADD",block.getTagValue("54D"));
        assertEquals("INTERMEDIARY",block.getTagValue("56A"));
        assertEquals("INTERMEDIARY NAME AND ADD",block.getTagValue("56D"));
        assertEquals("ACCOUNT INSTITUTION",block.getTagValue("57A"));
        assertEquals("ACCOUNT NAME AND ADD",block.getTagValue("57D"));
        assertEquals("IDENTIFIER CODE",block.getTagValue("58A"));
        assertEquals("/BENCON/",block.getTagValue("72"));
    }

    @Test
    public void map58DForMt202(){
        Map<String,Object> nonLcDetails = asMap(

                "beneficiaryBankPartyIDDSelectDMt202","BT",
                "beneficiaryBankPartyIDDTextMt202","ID D TEXT",
                "beneficiaryNameAndAddressMt202","beneficiary name and add",
                "senderToReceiverInformationMt202", "/BENCON/",
                "beneficiaryBankFlagMt202","D"
        );

        TradeService tradeService = new TradeService();
        tradeService.Service(new DocumentNumber("EXP98734"));
        tradeService.updateDetails(nonLcDetails, new UserActiveDirectoryId());

        List<RawSwiftMessage> messages = builder.build("202", tradeService);
        RawSwiftMessage message = messages.get(0);
        MessageBlock block = message.getMessageBlock();
        printTags(block);
        assertEquals("BENEFICIARY NAME AND ADD",block.getTagValue("58D"));
    }
    
    @Test
    public void successfulMappingOfMt400(){
    	Map<String,Object> details = asMap(
    			"documentNumber",new DocumentNumber("1-01-909-12-00011-9"),
    			"remittingBankReferenceNumber","TR-1234567-89",
    			"productAmount","567,293.67",
    			"processDate","01/01/2013",
    			"senderToReceiverInformationMt400","FX-RS-03 SENDER TO RECEIVER INFORMATION",
                "bankIdentifierCodeMt400","STERLING BANK OF ASIA",
                "senderIdentifierCodeMt400","36143038",
                "senderPartyIdentifierMt400","SENDER PARTY",
                "senderLocationMt400","sender location",
                "senderNameAndAddressMt400","sender name and address",

                "receiverIdentifierCodeMt400","46201",
                "receiver400PartyIdentifierMt400","RECEIVER PARTY IDENTIFIER",
                "receiverLocationMt400","receiver location",
                "receiverNameAndAddressMt400","receiver name and address",
                "accountIdentifierCodeMt400","567-12345-90008",
                "accountNameAndAddressMt400","account name and address",
                "beneficiaryIdentifierCodeMt400","UCPB",
                "beneficiaryBankLocationMt400","BENEF BANK LOCATION",
                "beneficiaryNameAndAddressMt400","beneficiary name and address",
                "detailsOfChargesDescriptionMt400","details of charges",
                "detailsOfChargesCurrencyMt400","PHP",
                "detailsOfChargesTextAreaMt400","charges text area",
                "detailsOfChargesTextFieldMt400","12,013,919.12",
    			"currency","PHP",
                "detailsOfAmountDescriptionMt400", "some description",
                "detailsOfAmountCurrencyMt400","PHP",
                "detailsOfAmountTextFieldMt400","12,000,000.43",
                "detailsOfAmountTextAreaMt400","narrative goes here" ,
                "detailsOfCharges","SOME DETAILS"
    			);
    	
    	TradeService ts=new TradeService();
    	ts.updateDetails(details, new UserActiveDirectoryId("branchm"));
    	
		List<RawSwiftMessage> messages = builder.build("400",ts);
        RawSwiftMessage message = messages.get(0);
    	MessageBlock block = message.getMessageBlock();
    	printTags(block);
    	assertEquals("10190912000119",block.getReference());
    	assertEquals("TR-1234567-89",block.getRelatedReference());
        assertEquals("/RECEIVER PARTY IDENTIFIER\r\n" +
                "RECEIVER LOCATION",block.getTagValue("54B"));
        assertEquals("RECEIVER NAME AND ADDRESS",block.getTagValue("54D"));
    	assertEquals("130101PHP567293,67",block.getTagByName("32A").getValue());
    	assertEquals("130101PHP567293,67",block.getTagByName("33A").getValue());	
    	assertEquals("FX-RS-03 SENDER TO RECEIVER \r\nINFORMATION",block.getTagByName("72").getValue());
    	assertEquals("STERLING BANK OF ASIA",block.getTagByName("52A").getValue());
    	assertEquals("36143038",block.getTagByName("53A").getValue());
    	assertEquals("46201",block.getTagByName("54A").getValue());
    	assertEquals("567-12345-90008",block.getTagByName("57A").getValue());
        assertEquals("ACCOUNT NAME AND ADDRESS",block.getTagByName("57D").getValue());
        assertEquals("UCPB",block.getTagByName("58A").getValue());
        assertEquals("BENEFICIARY NAME AND ADDRESS",block.getTagValue("58D"));
        assertEquals("/DETAILS OF CHARGES/PHP12013919,12\r\n" +
                "//CHARGES TEXT AREA",block.getTagByName("71B").getValue());
    	assertEquals("/SOME DESCRIPTION/PHP12000000,43\r\n" +
                "//NARRATIVE GOES HERE",block.getTagByName("73").getValue());
    }
    
    @Test
    public void successfulMappingOfMt410Da(){
        Map<String,Object> nonLcDetails = asMap(
                "remittingBankReferenceNumber", "REM432",
                "processDate", "01/01/2013",
                "currency", "PHP",
                "amount", "10000",
                "senderToReceiverInformation", "FX DA 001 SENDER TO RECEIVER INFORMATION"
        );

        TradeService tradeService = new TradeService();
        tradeService.Service(new DocumentNumber("EXP98734"));
        tradeService.updateDetails(nonLcDetails, new UserActiveDirectoryId());
        
        List<RawSwiftMessage> messages = builder.build("410", tradeService);
        RawSwiftMessage message = messages.get(0);
        MessageBlock block = message.getMessageBlock();
        printTags(block);	
        assertEquals("EXP98734",block.getTagByName("20").getValue());	
        assertEquals("REM432",block.getTagByName("21").getValue());
        assertEquals("130101PHP10000,",block.getTagByName("32A").getValue());
        assertEquals("FX DA 001 SENDER TO RECEIVER \r\n" +
                "INFORMATION",block.getTagByName("72").getValue());
    }
    
    @Test
    public void successfulMappingOfMt410Dp(){
        Map<String,Object> nonLcDetails = asMap(
                "remittingBankReferenceNumber", "REM432",
                "processDate", "01/01/2013",
                "currency", "PHP",
                "amount", "10000",
                "senderToReceiverInformation", "/BENCON/"
        );

        TradeService tradeService = new TradeService();
        tradeService.Service(new DocumentNumber("EXP98734"));
        tradeService.updateDetails(nonLcDetails, new UserActiveDirectoryId());
        
        List<RawSwiftMessage> messages = builder.build("410",tradeService);
        RawSwiftMessage message = messages.get(0);
        MessageBlock block = message.getMessageBlock();
        printTags(block);	
        assertEquals("EXP98734",block.getTagByName("20").getValue());	
        assertEquals("REM432",block.getTagByName("21").getValue());
        assertEquals("130101PHP10000,",block.getTagByName("32A").getValue());
        assertEquals("/BENCON/",block.getTagByName("72").getValue());
    }
    
    @Test
    public void successfulMappingOfMt412(){
        Map<String,Object> nonLcDetails = asMap(
//                "remittingBankReferenceNumber", "",
                "maturityDate", "01/01/2013",
                "currency", "PHP",
                "amount", "10000",
                "remittingBankReferenceNumber", "1234451511",
                "senderToReceiverInformation", "/BENCON/"
        );

        TradeService tradeService = new TradeService();
        tradeService.Service(new DocumentNumber("EXP98734"));
        tradeService.updateDetails(nonLcDetails, new UserActiveDirectoryId());
//        DASettlementCreatedEvent daSettlementCreatedEvent = new DASettlementCreatedEvent(tradeService);
        
        List<RawSwiftMessage> messages = builder.build("412", tradeService);
        RawSwiftMessage message = messages.get(0);
        MessageBlock block = message.getMessageBlock();
        printTags(block);	
        assertEquals("EXP98734",block.getTagByName("20").getValue());
        assertEquals("130101PHP10000,",block.getTagValue("32A"));
//        assertEquals("",block.getTagByName("21").getValue());
//        assertNull(block.getTagByName("32A"));
//
        assertEquals("/BENCON/",block.getTagByName("72").getValue());
    }

    @Test
    public void successfulMappingOfMt412ForEmptyMapInput(){
        Map<String,Object> nonLcDetails = new HashMap<String, Object>();

        TradeService tradeService = new TradeService();
//        DASettlementCreatedEvent daSettlementCreatedEvent = new DASettlementCreatedEvent(tradeService);

        List<RawSwiftMessage> messages = builder.build("412", tradeService);
        RawSwiftMessage message = messages.get(0);
        MessageBlock block = message.getMessageBlock();
        printTags(block);
        assertNotNull(message);
    }

    @Test
    public void successfullyMapLetterOfCreditDetailsToMT700(){
        Map<String,Object> lcDetails = asMap(
                "partialShipment","something",
                "adviseThroughBank","false",
                "documentNumber","1213141414141",
                "formOfDocumentaryCredit","I",
                "issueDate","12/12/2012",
                "applicableRules","EUCP",
                "currency","PHP",
                "amount","120000",
                "positiveToleranceLimit","12",
                "negativeToleranceLimit","1",
                "maximumCreditAmount","1",
                "issueDate","12/13/2013",
                "applicableRules","EUCP",
                "expiryDate","12/13/2013",
                "expiryCountryCode","PH",
                "positiveToleranceLimit","12",
                "negativeToleranceLimit","1",
                "maximumCreditAmount","30",
                "additionalAmountsCovered","covered",
                "identifierCode","AUAP",
                "availableBy","A",
                "tenorOfDraftNarrative","tenor",
                "drawee","THIS IS THE DRAWEE",
                "mixedPaymentDetails","m payment details",
                "deferredPaymentDetails","deferred payment",
                "partialShipment", "Y",
                "transShipment","trans",
                "placeOfTakingDispatchOrReceipt","place of taking dispatch",
                "placeOfFinalDestination","final destination",
                "portOfLoadingOrDeparture","port of loading",
                "portOfDischargeOrDestination","DYAN LANG SA AMIN_-0912309130(){}.,:/?,,,,,,46,,,98dfg,,,,,,,,",
                "latestShipmentDate","01/08/2012",
                "shipmentPeriod","THIS IS THE SHIPMENT PERIOD",
                "generalDescriptionOfGoods","goods and description",
                "requiredDocuments","documents",
                "availableWithFlag", "A",
                "reimbursingBankFlag","A",
                "adviseThroughBankFlag","A"
        );

        TradeService ts = new TradeService();
        ts.setDocumentNumber(new DocumentNumber("1213141414141"));
        ts.updateDetails(lcDetails,new UserActiveDirectoryId("someone"));



        List<RawSwiftMessage> messages = builder.build("700",ts);
        RawSwiftMessage message = messages.get(0);
        MessageBlock block = message.getMessageBlock();
        printTags(block);
        assertEquals("1213141414141",block.getReference());
        assertEquals("1/1",block.getTagValue("27"));
        assertEquals("IRREVOCABLE",block.getTagValue("40A"));
        assertEquals("131213",block.getTagValue("31C"));
        assertEquals("EUCP LATEST VERSION",block.getTagValue("40E"));
        assertEquals("131213 IN PH",block.getTagValue("31D"));
        assertEquals("TENOR",block.getTagValue("42C"));
        assertEquals("THIS IS THE DRAWEE",block.getTagValue("42A"));
        assertEquals("M PAYMENT DETAILS",block.getTagValue("42M"));
        assertEquals("DEFERRED PAYMENT",block.getTagValue("42P"));
        assertEquals("Y",block.getTagValue("43P"));
        assertEquals("TRANS",block.getTagValue("43T"));
        assertEquals("PLACE OF TAKING DISPATCH",block.getTagValue("44A"));
        assertEquals("FINAL DESTINATION",block.getTagValue("44B"));
        assertEquals("PORT OF LOADING",block.getTagValue("44E"));
        assertEquals("DYAN LANG SA \r\n" +
                "AMIN_-0912309130(){}.,:/?,,,,,,46,,,98DFG,,,,,,,,",block.getTagValue("44F"));
        assertEquals("120108",block.getTagValue("44C"));
    }
    
    @Test
    public void successfulMappingOfMt740(){

        Map<String,Object> lcDetails = asMap(
                "documentNumber", "DC.IMP 3410/3444",
                "reimbursingBankAccountNumber", "0123456789",
                "applicableRules", "URR LATEST VERSION",
                "expiryDate", "12/21/2012",
                "expiryCountryCode", "01",
                "exporterName", "Letty",
                "exporterAddress", "Manila",
                "currency", "USD",
                "amount", "31500.55",
                "positiveToleranceLimit", "10",
                "negativeToleranceLimit", "10",
                "maximumCreditAmount", "NOT EXCEEDING",
                "additionalAmountsCovered", "Additional Amounts Covered",
                "tenorOfDraftNarrative", "DRAFT AT SIGHT",
//                "drawee", "",
                "mixedPaymentDetails", "Mixed Payment Details",
                "deferredPaymentDetails", "Deferred Payment Details",
//                "charges", "",
                "senderToReceiverInformation", "FX-RS-03 SENDER TO RECEIVER INFORMATION"
        );

        TradeService ts = new TradeService();
        ts.setDocumentNumber(new DocumentNumber("DC.IMP 3410/3444"));
        ts.updateDetails(lcDetails, new UserActiveDirectoryId("random"));

        List<RawSwiftMessage> messages = builder.build("740",ts);
        RawSwiftMessage message = messages.get(0);
        MessageBlock block = message.getMessageBlock();
        printTags(block);
        assertEquals("DC.IMP 3410/3444",block.getTagByName("20").getValue());
        assertEquals("0123456789",block.getTagByName("25").getValue());
        assertEquals("URR LATEST VERSION",block.getTagByName("40F").getValue());
        assertEquals("121221 IN 01",block.getTagByName("31D").getValue());
        assertEquals("LETTY MANILA",block.getTagByName("59").getValue());
        assertEquals("USD31500,55",block.getTagByName("32B").getValue());
        assertEquals("10/10",block.getTagByName("39A").getValue());
        assertEquals("NOT EXCEEDING",block.getTagByName("39B").getValue());
        assertEquals("ADDITIONAL AMOUNTS COVERED",block.getTagByName("39C").getValue());
        assertNull(block.getTagByName("41A"));     
//        assertNull(block.getTagByName("41D"));        
        assertEquals("DRAFT AT SIGHT",block.getTagByName("42C").getValue());
//        assertEquals("",block.getTagByName("42A").getValue());
        assertEquals("MIXED PAYMENT DETAILS",block.getTagByName("42M").getValue());
        assertEquals("DEFERRED PAYMENT DETAILS",block.getTagByName("42P").getValue());
//        assertEquals("",block.getTagByName("71B").getValue());
        assertEquals("FX-RS-03 SENDER TO RECEIVER INFORMATION",block.getTagByName("72").getValue());

    }

    @Test
    public void successfulMappingOfMt747(){
    	Map<String,Object> lcDetails = asMap(
    			"documentNumber","ABCDEFGHIJKLMNOP",
    			"remittingBankReferenceNumber","REIMBURSINGBANKREFERENCE",
    			"date of original authorization to reimburse","DATE OF ORIGINAL AUTHORIZATION TO REIMBURSE",
    			"expiryDateTo","10/10/2010",
    			"currency","PHP",
    			"currentAmount","52000",
    			"refundAmount","10000",
    			"additionalAmountsCovered","ABCDEFGHIJKLMONOPQRSTUVWXYZ",
    			"senderToReceiverInformation","ABCDEFGHIJKLMONOPQRSTUVWXYZ",
    			"narrative","ABCDEFGHIJKLMONOPQRSTUVWXYZ"
    			);

    	LetterOfCredit letterOfCredit = new LetterOfCredit(new DocumentNumber("ABCDEFGHIJKLMNOP"),lcDetails);
    	letterOfCredit.updateDetails(lcDetails);
    	TradeService ts = new TradeService();
        ts.setDocumentNumber(new DocumentNumber("ABCDEFGHIJKLMNOP"));
        ts.setDetails(lcDetails);
    	LCAmendedEvent e = new LCAmendedEvent(ts, letterOfCredit,letterOfCredit,"");
    	
    	List<RawSwiftMessage> messages = builder.build("747",e.getTradeService());
        RawSwiftMessage message = messages.get(0);
    	MessageBlock block = message.getMessageBlock();
    	printTags(block);
    	assertEquals("ABCDEFGHIJKLMNOP",block.getTagByName("20").getValue());	
    	assertEquals("101010",block.getTagByName("31E").getValue());
    	assertEquals("PHP42000,",block.getTagByName("32B").getValue());
    	assertEquals("PHP10000,",block.getTagByName("33B").getValue());
    	assertEquals("PHP52000,",block.getTagByName("39B").getValue());
    	assertEquals("ABCDEFGHIJKLMONOPQRSTUVWXYZ",block.getTagByName("39C").getValue());
    	assertEquals("ABCDEFGHIJKLMONOPQRSTUVWXYZ",block.getTagByName("72").getValue());
    	assertEquals("ABCDEFGHIJKLMONOPQRSTUVWXYZ",block.getTagByName("77A").getValue());
    }
    
    @Test
    public void successfulMappingOfMt750(){
        LCNegotiationDiscrepancy lcNegoDiscrepancy = new LCNegotiationDiscrepancy(
        		new DocumentNumber("EXP98734"), new ICNumber(),new BigDecimal("34750.115"),
        		Currency.getInstance("USD"),"",null,null,false,false,
        		new BigDecimal("0"),false,false,false,"");

        TradeService ts = new TradeService();
        ts.setDocumentNumber(new DocumentNumber("EXP98734"));
        Map<String,Object> details = asMap(
            "negotiationCurrency","PHP",
            "negotiationAmount","121414.15",
            "negotiationBankRefNumber","DC.IMP 3410/3444",
            "senderToReceiverInformation","/BENCON/"
        );

        ts.setDetails(details);
        
        LCNegotiationDiscrepancyCreatedEvent lcNegoDiscrepancyEvent = new LCNegotiationDiscrepancyCreatedEvent(ts,lcNegoDiscrepancy);
       
        List<RawSwiftMessage> messages = builder.build("750", lcNegoDiscrepancyEvent.getTradeService());
        RawSwiftMessage message = messages.get(0);
        MessageBlock block = message.getMessageBlock();
        printTags(block);	
        
        assertEquals("EXP98734", block.getReference());
        assertEquals("DC.IMP 3410/3444",block.getTagValue("21"));
        assertEquals("/BENCON/",block.getTagByName("72").getValue());
        assertEquals("PHP121414,15",block.getTagValue("32A"));
    }
    
    @Test
    public void successfulMappingOfMt752(){
    	//TODO: Resolve field values in mt752.xml
        Map<String,Object> details = asMap(
        		//Needs to be populated
        		);
        TradeService ts=new TradeService();
        ts.Service(new DocumentNumber("EXP98734"));
        ts.updateDetails(details, new UserActiveDirectoryId("branchm"));
//        LCNegotiationCreatedEvent event=new LCNegotiationCreatedEvent(ts);

        List<RawSwiftMessage> messages = builder.build("752",ts);
        RawSwiftMessage message = messages.get(0);
        MessageBlock block = message.getMessageBlock();
        printTags(block);	
        assertEquals("EXP98734",block.getReference());	
////        assertEquals("DC.IMP 3410/3444",block.getTagByName("21").getValue());
//        assertEquals("ISSUE",block.getTagByName("23").getValue());	      
////        assertEquals("USD34750,11",block.getTagByName("32B").getValue());	      
//        assertEquals("/BENCON/",block.getTagByName("72").getValue());	      
//        //assertEquals("",block.getTagByName("").getValue());
    }

    @Test
    public void successfulMappingOfMt707(){
    	Map<String,Object> lcDetails = asMap(
//    			"documentNumber","ABCDEFGHIJKLMNOP",
    			"receiver reference","receiver reference",
    			"issuing bank reference","issuing bank reference",
    			"issuing bank A","issuing bank A",
    			"issuing bank D","issuing bank D",
    			"issueDate","10/13/2013",
    			"lastAmendmentDate","11/01/2013",
    			"numberOfAmendments","13",
    			"beneficiaryName","Eric Error",
    			"expiryDate","04/10/2003",
    			"currency","PHP",
    			"currentAmount","10000",
    			"refundAmount","5000",
    			"positiveToleranceLimit","2000",
    			"maximumCreditAmount","5000",
    			"additionalAmountsCovered","1500",
    			"placeOfTakingDispatchOrReceipt","Quezon City",
    			"portOfLoadingOrDeparture","Port 1",
    			"portOfDischargeOrDestination","Port 2",
    			"placeOfFinalDestination","Caloocan City",
    			"latestShipmentDate","06/06/2006",
    			"shipmentPeriod","Period Shipment",
    			"narrative","Narrative",
    			"senderToReceiverInformation","Information"
    			);

    	LetterOfCredit letterOfCredit = new LetterOfCredit(new DocumentNumber("ABCDEFGHIJKLMNOP"),lcDetails);
    	letterOfCredit.updateDetails(lcDetails);
        TradeService ts = new TradeService();
        ts.setDetails(lcDetails);
        ts.setDocumentNumber(new DocumentNumber("ABCDEFGHIJKLMNOP"));


    	LCAmendedEvent e = new LCAmendedEvent(ts, letterOfCredit,letterOfCredit,"");
    	
		List<RawSwiftMessage> messages = builder.build("707",e.getTradeService());
        RawSwiftMessage message = messages.get(0);
    	MessageBlock block = message.getMessageBlock();
    	printTags(block);
    	assertEquals("ABCDEFGHIJKLMNOP",block.getTagByName("20").getValue());	
    	assertEquals("RECEIVER REFERENCE",block.getTagByName("21").getValue());	
    	assertEquals("ISSUING BANK REFERENCE",block.getTagByName("23").getValue());	
    	assertEquals("ISSUING BANK A",block.getTagByName("52A").getValue());	
    	assertEquals("ISSUING BANK D",block.getTagByName("52D").getValue());
    	assertEquals("131013",block.getTagByName("31C").getValue());
    	assertEquals("131101",block.getTagByName("30").getValue());
    	assertEquals("13",block.getTagByName("26E").getValue());
    	assertEquals("ERIC ERROR",block.getTagByName("59").getValue());
    	assertEquals("030410",block.getTagByName("31E").getValue());
    	assertEquals("PHP5000,",block.getTagByName("32B").getValue());
    	assertEquals("PHP5000,",block.getTagByName("33B").getValue());
    	assertEquals("PHP10000,",block.getTagByName("34B").getValue());
    	assertEquals("PHP2000,",block.getTagByName("39A").getValue());
    	assertEquals("5000",block.getTagByName("39B").getValue());
    	assertEquals("1500",block.getTagByName("39C").getValue());
    	assertEquals("QUEZON CITY",block.getTagByName("44A").getValue());
    	assertEquals("PORT 1",block.getTagByName("44E").getValue());
    	assertEquals("PORT 2",block.getTagByName("44F").getValue());
    	assertEquals("CALOOCAN CITY",block.getTagByName("44B").getValue());
    	assertEquals("060606",block.getTagByName("44C").getValue());
    	assertEquals("PERIOD SHIPMENT",block.getTagByName("44D").getValue());
    	assertEquals("NARRATIVE",block.getTagByName("79").getValue());
    	assertEquals("INFORMATION",block.getTagByName("72").getValue());
    }
    
    @Test
    public void successfulMappingOfMt760(){
        Map<String,Object> lcDetails = asMap(
        		"1/1","1/1",
                "applicableRules","EUCP",
                "furtherIdentification","somebody",
                "documentNumber","1213141414141",
                "issueDate","12/12/2012",
                "applicableRules","ABCD12345678901234567890",
                "senderToReceiverInformation","fx-rs-03 sender to receiver information",
                "detailsOfGuarantee","details of guarantee",
                "availableWithFlag", "A",
                "reimbursingBankFlag","A",
                "adviseThroughBankFlag","A"
        );

        TradeService ts = new TradeService();
        ts.setDocumentNumber(new DocumentNumber("1213141414141"));
        ts.updateDetails(lcDetails,new UserActiveDirectoryId("someone"));
        LetterOfCreditCreatedEvent event = new LetterOfCreditCreatedEvent(ts,null,"");

        List<RawSwiftMessage> messages = builder.build("760",ts);
        RawSwiftMessage message = messages.get(0);
        MessageBlock block = message.getMessageBlock();
        printTags(block);
        assertEquals("1/1",block.getTagByName("27").getValue());	
        assertEquals("1213141414141",block.getTagByName("20").getValue());	
        assertEquals("SOMEBODY",block.getTagByName("23").getValue());	
        assertEquals("121212",block.getTagByName("30").getValue());	
        assertEquals("ABCD12345678901234567890",block.getTagByName("40C").getValue());
        assertEquals("DETAILS OF GUARANTEE",block.getTagByName("77C").getValue());
        assertEquals("FX-RS-03 SENDER TO RECEIVER \r\n" +
                "INFORMATION",block.getTagByName("72").getValue());
    }
    
    @Test
    public void successfulMappingOfMt767(){
        Map<String,Object> lcDetails = asMap(
        		"1/1","1/1",
                "documentNumber","1-01-909-12-00011-9",
                "relatedReference", "relatedReference",
                "furtherIdentification", "ISSUE",
                "issueDate","06/05/2012",
                "numberOfAmendments","10",
                "lastAmendmentDate", "06/05/2012",
                "narrative_ie", "WE HEREBY ADVISE YOU OF AMENDMENTS " +
                		"MADE TO THE REFERENCED PERFORMANCE GUARANTEE. " +
                		"OUR LIABILITY IS VALID AS AT 1 JULY 2012 " +
                		"AND SHALL EXPIRE ON 30 JUNE 2013.",
                "senderToReceiverInformation", "/BENCON/"
        );
        LetterOfCredit letterOfCredit = new LetterOfCredit(new DocumentNumber("1-01-909-12-00011-9"),lcDetails);
        letterOfCredit.updateDetails(lcDetails);
        TradeService ts = new TradeService();
        ts.setDetails(lcDetails);
        ts.setDocumentNumber(new DocumentNumber("10190912000119"));
        LCAmendedEvent lcAmendedEvent = new LCAmendedEvent(ts, letterOfCredit, letterOfCredit,"");



        List<RawSwiftMessage> messages = builder.build("767", lcAmendedEvent.getTradeService());
        RawSwiftMessage message = messages.get(0);
        MessageBlock block = message.getMessageBlock();
        printTags(block);
        assertEquals("1/1",block.getTagValue("27"));
        assertEquals("10190912000119",block.getTagValue("20"));
        assertEquals("RELATEDREFERENCE",block.getTagValue("21"));
//        assertEquals("ISSUE",block.getTagValue("23"));
        assertEquals("130708",block.getTagValue("30"));
        assertEquals("11",block.getTagValue("26E"));
        assertEquals("120605",block.getTagValue("31C"));
        assertEquals("WE HEREBY ADVISE YOU OF AMENDMENTS " +
                		"MADE TO THE REFERENCED PERFORMANCE GUARANTEE. " +
                		"OUR LIABILITY IS VALID AS AT 1 JULY 2012 " +
                		"AND SHALL EXPIRE ON 30 JUNE 2013.",block.getTagByName("77C").getValue());	
        assertEquals("/BENCON/",block.getTagByName("72").getValue());	
    }

    @Test
    public void successfulMappingOfMt400WithEmptyMap(){
        Map<String,Object> details = new HashMap<String,Object>();
        TradeService ts = new TradeService();
        ts.updateDetails(details, new UserActiveDirectoryId("branchm"));

        List<RawSwiftMessage> messages = builder.build("400",ts);
        RawSwiftMessage message = messages.get(0);
        assertNotNull(message);
    }

    @Test
    public void successfulMappingOfMt410WithEmptyMap(){
        Map<String,Object> details = new HashMap<String,Object>();
        TradeService ts = new TradeService();
        ts.updateDetails(details, new UserActiveDirectoryId("branchm"));

        List<RawSwiftMessage> messages = builder.build("410",ts);
        RawSwiftMessage message = messages.get(0);
        assertNotNull(message);
    }
    
    
    private void printTags(MessageBlock block){
        for(Tag tag : block.getTags()){
            System.out.println(tag.getTagName() + ":" + tag.getValue());
        }
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
