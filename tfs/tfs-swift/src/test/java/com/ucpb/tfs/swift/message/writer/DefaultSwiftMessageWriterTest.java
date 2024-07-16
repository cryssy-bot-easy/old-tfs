package com.ucpb.tfs.swift.message.writer;

import com.ucpb.tfs.swift.message.*;
import net.sf.saxon.style.StylesheetStripper;
import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 */
public class DefaultSwiftMessageWriterTest {

    private DefaultSwiftMessageWriter writer = new DefaultSwiftMessageWriter();

    @Test
    public void doNotWriteNonExistentBlocks(){
        RawSwiftMessage message = new RawSwiftMessage();
        BasicHeader b=new BasicHeader();
        b.setApplicationIdentifier("AppIdentifier");
        message.setBasicHeader(b);
        MessageBlock block  = new MessageBlock();
        block.addTag("20","TAG20");
        block.addTag("21","REFERENCENUMBER");

        message.setMessageBlock(block);

        String formattedMessage = writer.write(message);
        System.out.println(formattedMessage);
        assertEquals("{1:AppIdentifier}{4:\r\n" +
                ":20:TAG20\r\n" +
                ":21:REFERENCENUMBER\r\n" +
                "-}",formattedMessage);
    }

    @Test
    public void doNotWriteEmptyMessageBlock(){
        RawSwiftMessage message = new RawSwiftMessage();

        BasicHeader basicHeader = new BasicHeader();
        basicHeader.setApplicationIdentifier("F");
        basicHeader.setLtIdentifier("MIDLGB22AXXX");
        basicHeader.setSessionNumber("0548");
        basicHeader.setSequenceNumber("034693");
        basicHeader.setServiceIndentifier("01");

        ApplicationHeader applicationHeader = new ApplicationHeader();
        applicationHeader.setDeliveryMonitoring("003");
        applicationHeader.setIoIdentifier("I");
        applicationHeader.setMessagePriority("N");
        applicationHeader.setMessageType("103");
        applicationHeader.setObsolescencePeriod("N");

        SwiftAddress address = new SwiftAddress();
        address.setCompleteAddress("AAAAAAAAXXXX");
        applicationHeader.setReceiverAddress(address);

        message.setBasicHeader(basicHeader);
        message.setApplicationHeader(applicationHeader);

        String formattedMessage = writer.write(message);
        System.out.println(formattedMessage);
        assertEquals("{1:F01MIDLGB22AXXX0548034693}{2:I103AAAAAAAAXXXXN003N}",formattedMessage);

    }

    @Test
    public void writeValidBasicHeaderBlock(){
        RawSwiftMessage message = new RawSwiftMessage();

        BasicHeader basicHeader = new BasicHeader();
        basicHeader.setApplicationIdentifier("AppIdentifier");
        basicHeader.setServiceIndentifier("ServiceIdentifier");
        basicHeader.setLtIdentifier("LtIdentifier");
        basicHeader.setSessionNumber("SessionNumber");
        basicHeader.setSequenceNumber("SequenceNumber");

        message.setBasicHeader(basicHeader);

        String formattedMessage = writer.write(message);
        System.out.println(formattedMessage);
        assertEquals("{1:AppIdentifierServiceIdentifierLtIdentifierSessionNumberSequenceNumber}",formattedMessage);

    }

    @Test
    public void writeValidApplicationHeaderBlock(){
        RawSwiftMessage message = new RawSwiftMessage();
        ApplicationHeader applicationHeader = new ApplicationHeader();
        applicationHeader.setIoIdentifier("IoIdentifier");
        applicationHeader.setMessageType("103");

        SwiftAddress address = new SwiftAddress();
        address.setCompleteAddress("AAAAAAAAXXXX");
        applicationHeader.setReceiverAddress(address);
        applicationHeader.setMessagePriority("MessagePriority");
        applicationHeader.setDeliveryMonitoring("DeliveryMonitoring");
        applicationHeader.setObsolescencePeriod("ObsolescencePeriod");

        message.setApplicationHeader(applicationHeader);
        String formattedMessage = writer.write(message);
        System.out.println(formattedMessage);
        assertEquals("{2:IoIdentifier103AAAAAAAAXXXXMessagePriorityDelivery" +
                "MonitoringObsolescencePeriod}",formattedMessage);
    }

    @Test
    public void successfullyWriteValidUserHeader(){
        RawSwiftMessage message = new RawSwiftMessage();
        UserHeader userHeader = new UserHeader();
        userHeader.addUserTag("119","STP");
        userHeader.addUserTag("TAG2","TAG 2 Value");
        message.setUserHeader(userHeader);

        String formattedMessage = writer.write(message);
        assertEquals("{3:{119:STP}{TAG2:TAG 2 VALUE}}",formattedMessage);
    }

    @Test
    @Ignore("Reponsibility for doing character replacement has been delegated to the Tag class")
    public void successfullyReplaceInvalidCharacters(){
        RawSwiftMessage message = new RawSwiftMessage();
        BasicHeader b=new BasicHeader();
        b.setApplicationIdentifier("AppIdentifier");
        message.setBasicHeader(b);
        MessageBlock block  = new MessageBlock();
        block.addTag("20","<");
        block.addTag("21","!");
        block.addTag("22","&");
        block.addTag("23","|");
        block.addTag("24","$");
        block.addTag("25","*");
        block.addTag("26",";");
        block.addTag("27","^");
        block.addTag("28","%");
        block.addTag("29","_");
        block.addTag("30",">");
        block.addTag("31","\"");
        block.addTag("32","`");
        block.addTag("33","#");
        block.addTag("34","@");
        block.addTag("35","=");
        block.addTag("36","~");
        block.addTag("37","[");
        block.addTag("38","]");
        block.addTag("39","{");
        block.addTag("40","}");
        block.addTag("41","\\");

        message.setMessageBlock(block);

        String formattedMessage = writer.write(message);
        System.out.println(formattedMessage);
        assertEquals("{1:AppIdentifier}{4:\n" +
                "20:??4C\n" +
                "21:??4F\n" +
                "22:??50\n" +
                "23:??5A\n" +
                "24:??5B\n" +
                "25:??5C\n" +
                "26:??5E\n" +
                "27:??5F\n" +
                "28:??6C\n" +
                "29:??6D\n" +
                "30:??6E\n" +
                "31:??79\n" +
                "32:??7B\n" +
                "33:??7C\n" +
                "34:??7E\n" +
                "35:??7F\n" +
                "36:??A1\n" +
                "37:??AD\n" +
                "38:??BD\n" +
                "39:??C0\n" +
                "40:??D0\n" +
                "41:??E0\n" +
                "-}",formattedMessage);
    }


}
