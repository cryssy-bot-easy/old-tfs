package com.ucpb.tfs.util;

import static org.junit.Assert.*;

import java.util.List;

import com.ucpb.tfs.swift.message.mt1series.MT103;
import com.ucpb.tfs.swift.message.writer.JaxbXmlSwiftMessageWriter;
import com.ucpb.tfs.swift.message.writer.XmlWriter;
import org.junit.Test;

import com.ucpb.tfs.swift.message.ApplicationHeader;
import com.ucpb.tfs.swift.message.BasicHeader;
import com.ucpb.tfs.swift.message.MessageBlock;
import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.swift.message.SwiftAddress;
import com.ucpb.tfs.swift.message.writer.JaxbSwiftMessageWriter;
import com.ucpb.tfs.swift.validator.xml.SwiftXmlValidator;

import org.springframework.beans.BeanUtils;

public class SwiftMessageFormatterIntegrationTest {

	private XmlWriter writer = new JaxbXmlSwiftMessageWriter();
	private XmlFormatter xmlFormatter = new XmlFormatter("/swift/formatter/swift-format.xsl");
	private XmlValidator mt4seriesValidator = new XmlValidator(
			"/swift/schemas/mt4series.xsd");
	private SwiftXmlValidator swiftXmlValidator = new SwiftXmlValidator("/swift/schemas/swift-master.xsd", "/swift/formatter/swift-format.xsl");

	@Test
	public void formatAndValidateValidMt410() {

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
		applicationHeader.setMessageType("410");
		applicationHeader.setObsolescencePeriod("N");

		SwiftAddress address = new SwiftAddress();
		address.setCompleteAddress("AAAAAAAAXXXX");
		applicationHeader.setReceiverAddress(address);

		message.setBasicHeader(basicHeader);
		message.setApplicationHeader(applicationHeader);
		
		MessageBlock messageBlock = new MessageBlock();
		messageBlock.addTag("20", "documentnumber");
        messageBlock.addTag("21","REFERENCE");
        messageBlock.addTag("32A","081216EUR100000,");
        messageBlock.addTag("72","SENDER TO RECEIVER INFORMATION");

        message.setMessageBlock(messageBlock);

		String marshalledMessage = writer.write(message);
		System.out.println("****** MARSHALLED MESSAGE *****");
		System.out.println(marshalledMessage);
		System.out.println("****** MARSHALLED MESSAGE ******");
		
		String formattedMessage = xmlFormatter.formatXmlString(marshalledMessage);
		
		System.out.println("******* FORMATTED MESSAGE ******");
		System.out.println(formattedMessage);
		System.out.println("******* FORMATTED MESSAGE ******");

		List<String> errors = mt4seriesValidator.validate(formattedMessage);
		System.out.println(errors);
		
//		assertTrue(errors.isEmpty());
		assertTrue(swiftXmlValidator.validate(message).isEmpty());
	}

    @Test
    public void generateMt103RawMessage(){
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
        applicationHeader.setMessageType("103");
        SwiftAddress address = new SwiftAddress();
        address.setBankIdentifierCode("ASASASAS");
        address.setBranchCode("XXX");
        applicationHeader.setReceiverAddress(address);
        applicationHeader.setMessagePriority("N");
        swiftMessage.setApplicationHeader(applicationHeader);

        MessageBlock messageBlock = new MessageBlock();
        messageBlock.addTag("56A","Dummy value");
        swiftMessage.setMessageBlock(messageBlock);

        String formattedMessage = xmlFormatter.formatXmlString(writer.write(swiftMessage));
        System.out.println(formattedMessage);

    }

}
