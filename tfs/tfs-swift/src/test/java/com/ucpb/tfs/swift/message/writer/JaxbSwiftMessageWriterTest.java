package com.ucpb.tfs.swift.message.writer;

import com.ucpb.tfs.swift.message.*;
import com.ucpb.tfs.swift.message.mt4series.MT410;
import com.ucpb.tfs.swift.message.mt7series.MT700;
import com.ucpb.tfs.swift.message.mt7series.MT767;

import com.ucpb.tfs.swift.validator.ValidationError;
import com.ucpb.tfs.util.XmlFormatter;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;

/**
 * Not a test class. this is a convenience class used to
 * generate swift messages.
 *
 */
public class JaxbSwiftMessageWriterTest {

   private JaxbSwiftMessageWriter writer = new JaxbSwiftMessageWriter();

   private XmlFormatter xmlFormatter = new XmlFormatter("/swift/formatter/swift-format.xsl");



    @Test
   public void successfullyWriteSwiftMessage(){
       RawSwiftMessage message = new MT700();

       ApplicationHeader header = new ApplicationHeader();
       header.setMessageType("700");
       message.setApplicationHeader(header);
       UserHeader h =new UserHeader();
       message.setUserHeader(h);
       MessageBlock messageBlock = new MessageBlock();
       messageBlock.addTag("40A","NEW VALUE");
       message.setMessageBlock(messageBlock);
       
       String result = writer.write(message);

       System.out.println(result);
   }

    @Test
    public void successfullyWriteMt410(){
        RawSwiftMessage message = new MT410();

        ApplicationHeader header = new ApplicationHeader();
        header.setMessageType("410");
        SwiftAddress address = new SwiftAddress();
        address.setCompleteAddress("ASASASASXXX");
        header.setReceiverAddress(address);

        message.setApplicationHeader(header);

        MessageBlock messageBlock = new MessageBlock();
        messageBlock.addTag("20","DOCNUMBER");
        messageBlock.addTag("21","RELATEDREFERENCE");
        messageBlock.addTag("32A","121026USD755000,");
        messageBlock.addTag("72","SENDER TO RECEIVER");

        message.setMessageBlock(messageBlock);

        String result = writer.write(message);
        System.out.println(result);

    }

    @Test
    public void successfullyWriteMT103(){
        RawSwiftMessage swiftMessage = new com.ucpb.tfs.swift.message.mt1series.MT103();

        BasicHeader header = new BasicHeader();
        header.setApplicationIdentifier("F");
        header.setServiceIndentifier("01");
        header.setLtIdentifier("UCPBPHMMAXXX");
        header.setSessionNumber("0000");
        header.setSequenceNumber("000000");
        swiftMessage.setBasicHeader(header);

        ApplicationHeader applicationHeader = new ApplicationHeader();
        applicationHeader.setIoIdentifier("I");
        applicationHeader.setMessageType("103");
        SwiftAddress address = new SwiftAddress();
        address.setBankIdentifierCode("ASASASAS");
        address.setBranchCode("XXX");
        applicationHeader.setReceiverAddress(address);
        applicationHeader.setMessagePriority("N");
        swiftMessage.setApplicationHeader(applicationHeader);

        MessageBlock messageBlock = new MessageBlock();
        messageBlock.addTag("56A","9090792813000019");
        messageBlock.addTag("23B","CRED");
        messageBlock.addTag("26T","A00");
        messageBlock.addTag("32A","130225USD121561724,");
        messageBlock.addTag("33B", "USD121561724,");
        messageBlock.addTag("36","12,1000");
        messageBlock.addTag("50K","JJED PHILIPPINES INC\n" +
                "3F JJED BLDG.,\n" +
                "54 P. CRUZ \n" +
                "STREET\n" +
                "MANDALUYONG CITY");
        messageBlock.addTag("51A","ABNAAEADSHJ");
        messageBlock.addTag("53B","32080923802832");
        messageBlock.addTag("59","BENEF NAME\n" +
                "BENEF ADDRESS");
        messageBlock.addTag("70","/ANSI/ENVELOPE");
        messageBlock.addTag("71A","SHA");
        messageBlock.addTag("71F","PHP121212,");
        messageBlock.addTag("77B","REPORTING");


        swiftMessage.setMessageBlock(messageBlock);
        System.out.println("***** SOURCE *******");
        System.out.println(writer.write(swiftMessage));
        System.out.println("***** SOURCE *******");
        String formattedMessage = xmlFormatter.formatXmlString(writer.write(swiftMessage));

        System.out.println("******* FORMATTED MESSAGE ******");
        System.out.println(formattedMessage);
        System.out.println("******* FORMATTED MESSAGE ******");
    }

}
