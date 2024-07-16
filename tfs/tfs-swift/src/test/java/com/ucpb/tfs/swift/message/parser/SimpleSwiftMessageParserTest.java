package com.ucpb.tfs.swift.message.parser;

import com.ucpb.tfs.swift.message.ApplicationHeader;
import com.ucpb.tfs.swift.message.BasicHeader;
import com.ucpb.tfs.swift.message.MessageBlock;
import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.util.FileUtil;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 */
public class SimpleSwiftMessageParserTest {

    private SimpleSwiftMessageParser parser = new SimpleSwiftMessageParser();


    @Test
    public void successfullyParseMT700() throws IOException, ParseException {
        String swiftMessage = FileUtil.getFileAsString("/swift/5620MT7X.087");
        RawSwiftMessage parsedMessage = parser.parse(swiftMessage);

        ApplicationHeader appHeader = parsedMessage.getApplicationHeader();
        assertEquals("I",appHeader.getIoIdentifier());
        assertEquals("700",appHeader.getMessageType());
        assertEquals("PNBPUS33XPHL",appHeader.getReceiverAddress().getAddressWithLtPadding());
        assertEquals("N",appHeader.getMessagePriority());
        assertEquals("",appHeader.getDeliveryMonitoring());
        assertEquals("",appHeader.getObsolescencePeriod());

        BasicHeader basicHeader = parsedMessage.getBasicHeader();
        assertNotNull(basicHeader);
        assertEquals("F",basicHeader.getApplicationIdentifier());
        assertEquals("01",basicHeader.getServiceIndentifier());
        assertEquals("UCPBPHMMAXXX",basicHeader.getLtIdentifier());
        assertEquals("0000",basicHeader.getSessionNumber());
        assertEquals("000000",basicHeader.getSequenceNumber());

        MessageBlock block = parsedMessage.getMessageBlock();

        assertEquals("1/1",block.getTagByName("27").getValue());
        assertEquals("IRREVOCABLE",block.getTagByName("40A").getValue());
        assertEquals("110803",block.getTagByName("31C").getValue());
        assertEquals("FX56202011005190",block.getTagByName("20").getValue());
        assertEquals("+ CANDIES\n" +
                "+CANDIES\n" +
                "+PHILIPPINE STANDARD COMMODITY CLASSIFICATION CODE :783.11-03 \n" +
                "+FOB",block.getTagByName("45A").getValue());
        assertEquals("+SIGNED COMMERCIAL INVOICE IN  TRIPLICATE\n" +
                "+PACKING LIST\n" +
                "+ONE FULL SET OF AT LEAST THREE ORIGINAL CLEAN 'ON BOARD' OCEAN\n" +
                "BILLS OF LADING IN NEGOTIABLE AND TRANSFERABLE FORM AND ONE NON-\n" +
                "NEGOTIABLE COPY ISSUED TO THE ORDER OF UNITED COCONUT PLANTERS \n" +
                "BANK MARKED FREIGHT PREPAID NOTIFY\n" +
                "APPLICANT",block.getTagByName("46A").getValue());

    }
}
