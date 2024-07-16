package com.ucpb.tfs.swift.message.parser;

import com.ucpb.tfs.swift.message.MessageBlock;
import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.util.FileUtil;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 */
public class SwiftReportMessageParserTest {

    private SwiftReportMessageParser parser = new SwiftReportMessageParser();



    @Test
    public void successfullyParseReportFormattedSwiftMessage() throws IOException, ParseException {
        RawSwiftMessage parsedMessage = parser.parse(FileUtil.getFileAsString("/swift/00290002.prt"));
        assertNotNull(parsedMessage);
        MessageBlock messageBlock = parsedMessage.getMessageBlock();
        assertEquals("DM56202012010774",messageBlock.getTagValue("20"));
    }

    @Test
    public void successfullyParseSwiftMessageConfirmation() throws IOException, ParseException {
        RawSwiftMessage parsedMessage = parser.parse(FileUtil.getFileAsString("/swift/mt760confirmation.prt"));
        assertNotNull(parsedMessage);
        MessageBlock messageBlock = parsedMessage.getMessageBlock();
        assertEquals("DM56202012010774",messageBlock.getTagValue("20"));
    }

    @Test
    public void successfullyParsePrtFile() throws IOException, ParseException {
        RawSwiftMessage parsedMessage = parser.parse(FileUtil.getFileAsString("/swift/00290002.prt"));
        assertNotNull(parsedMessage);
        MessageBlock messageBlock = parsedMessage.getMessageBlock();
        assertEquals("1/1",messageBlock.getTagValue("27"));
        assertEquals("DM56202012010774",messageBlock.getTagValue("20"));
        assertEquals("ISSUE",messageBlock.getTagValue("23"));
        assertEquals("121109",messageBlock.getTagValue("30"));
        assertEquals("ISPR",messageBlock.getTagValue("40C"));
        assertEquals("BENEFICIARY: MIGHTY CORPORATION\n" +
                "             9110 SULTANA CORNER\n" +
                "             TRABAJO STREETS MAKATI\n" +
                "             PHILIPPINES\n" +
                ".\n" +
                "APPLICANT:   MARIGOLD CORPORATION\n" +
                "             KM 9, MC ARTHUR HIGHWAY, TALOMO\n" +
                "             THAILAND\n" +
                ".\n" +
                "GENTLEMEN,\n" +
                ".\n" +
                "WHEREAS, MARIGOLD CORPORATION WITH OFFICE ADDRESS KM 9, MC\n" +
                "ARTHUR HIGHWAY, TALOMO, THAILAND, (HEREINAFTER CALLED ''THE\n" +
                "BUYER'') HAS REQUESTED TO ISSUE A IRREVOCABLE FOREIGN STANDBY\n" +
                "LETTER OF CREDIT IN FAVOR OF MIGHTY CORPORATION, 9110 SULTANA\n" +
                "CORNER, TRABAJO STREETS, MAKATI (HEREINAFTER CALLED ''THE\n" +
                "SELLER'') FOR THAI BAHT: FIFTY MILLION BAHT ONLY\n" +
                "THIS REPRESENTS GUARANTEE PAYMENT FOR THE PURCHASE OF\n" +
                "VARIOUS PRODUCTS",messageBlock.getTagValue("77C"));
    }


}
