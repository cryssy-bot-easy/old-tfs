package com.ucpb.tfs.domain.mtmessage;

import com.ucpb.tfs.domain.mtmessage.enumTypes.MessageClass;
import com.ucpb.tfs.domain.service.utils.TradeServiceReferenceNumberGenerator;
import com.ucpb.tfs.domain.task.Task;
import com.ucpb.tfs.domain.task.TaskRepository;
import com.ucpb.tfs.swift.message.MessageBlock;
import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.swift.message.parser.ParseException;
import com.ucpb.tfs.swift.message.parser.SwiftMessageParser;
import com.ucpb.tfs.util.FileUtil;
import org.codehaus.plexus.util.IOUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.integration.Message;
import org.springframework.integration.MessageChannel;

import java.io.*;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
(revision)
Program Details: Follow the new constructor on SwiftMessageParserService.java
Revised by: Jesse James Joson
Date Revised: May 4, 2018
Project: CORE
Member Type: Java
Filename: SwiftMessageParserServiceTest.java
 */
public class SwiftMessageParserServiceTest {

    private MessageChannel invalidMessagesChannel;

    private MessageChannel parsedMessagesChannel;

    private MtMessageService mtMessageService;

    private SwiftMessageParser messageParser;

    private TradeServiceReferenceNumberGenerator referenceNumberGenerator;

    private SwiftMessageParserService swiftMessageParserService;

    private File mtMessage;

    @Before
    public void setup() throws IOException {
        invalidMessagesChannel = mock(MessageChannel.class);
        parsedMessagesChannel = mock(MessageChannel.class);
        mtMessageService = mock(MtMessageService.class);
        messageParser = mock(SwiftMessageParser.class);
        referenceNumberGenerator = mock(TradeServiceReferenceNumberGenerator.class);

        when(referenceNumberGenerator.generateReferenceNumber(anyString())).thenReturn("1212-1212-13144");

        swiftMessageParserService = new SwiftMessageParserService(messageParser,invalidMessagesChannel,parsedMessagesChannel,mtMessageService,"/opt/tfs/SWIFT/regex.prt");

        mtMessage = generateValidMtMessage("MT700.txt");

    }

    @After
    public void cleanup(){
        mtMessage.delete();
    }

    @Test
    public void persistSuccessfullyParsedMessages() throws ParseException {
        RawSwiftMessage message = new RawSwiftMessage();
        MessageBlock messageBlock = new MessageBlock();
        messageBlock.addTag("20","1234567890");
        message.setMessageBlock(messageBlock);
        when(messageParser.parse(anyString())).thenReturn(message);
        swiftMessageParserService.parse(mtMessage);


        verify(invalidMessagesChannel,never()).send(any(Message.class));
        verify(mtMessageService,times(2)).persist(any(MtMessage.class));
        verify(parsedMessagesChannel).send(any(Message.class));
    }

    @Test
    public void sendInvaildMessagesToInvalidMessageChannel() throws ParseException {
        when(messageParser.parse(anyString())).thenThrow(new ParseException("This is a parse exception"));
        swiftMessageParserService.parse(mtMessage);

        verify(invalidMessagesChannel).send(any(Message.class));
        verify(parsedMessagesChannel,never()).send(any(Message.class));
        verify(referenceNumberGenerator,never()).generateReferenceNumber("909");
        verify(mtMessageService, never()).persist(any(MtMessage.class));
        verify(parsedMessagesChannel,never()).send(any(Message.class));

    }

    @Test
    public void sucessfullyDetermineMessageType() throws IOException {
        String message = FileUtil.getFileAsString("/mtMessages/I00012012112234.txt");
        assertEquals(MessageClass.INCOMING,extractMessageClassFromBody(message));
        String message2 = FileUtil.getFileAsString("/mtMessages/O00012012112233.txt");
        assertEquals(MessageClass.CONFIRMATION,extractMessageClassFromBody(message2));
    }

    private File generateValidMtMessage(String filename) throws IOException {
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

    private MessageClass extractMessageClassFromBody(String inputMessage){
        MessageClass messageClass = null;
        if(inputMessage.contains("Swift Input                   :")){
            messageClass = MessageClass.CONFIRMATION;
        }else if(inputMessage.contains("Swift Output                  :")){
            messageClass = MessageClass.INCOMING;
        }else{
            messageClass = MessageClass.OUTGOING;
        }
        return messageClass;
    }

}
