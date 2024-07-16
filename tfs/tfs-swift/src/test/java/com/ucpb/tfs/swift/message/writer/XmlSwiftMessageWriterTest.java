package com.ucpb.tfs.swift.message.writer;

import com.ucpb.tfs.swift.message.ApplicationHeader;
import com.ucpb.tfs.swift.message.MessageBlock;
import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.swift.message.SwiftAddress;

import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 */
@Ignore("Message writer has been deprecated")
public class XmlSwiftMessageWriterTest {

    private XmlSwiftMessageWriter writer = new XmlSwiftMessageWriter();


    @Test
    public void writeAsXml(){
        RawSwiftMessage swiftMessage = new RawSwiftMessage();
        ApplicationHeader header = new ApplicationHeader();
        SwiftAddress address = new SwiftAddress();
        address.setBankIdentifierCode("12345678");
        address.setBranchCode("ABC");

        header.setReceiverAddress(address);
        header.setMessageType("700");
        swiftMessage.setApplicationHeader(header);
        MessageBlock block4 = new MessageBlock();
        block4.addTag("45","tag45");
        block4.addTag("32","tag32");
        block4.addTag("82","newTag");


        swiftMessage.setMessageBlock(block4);

        String xml = writer.write(swiftMessage);
        assertEquals("<MT700><basic_header><application_identifier></application_identifier><service_identifier></service_identifier><lt_identifier></lt_identifier><session_number></session_number><sequence_number></sequence_number></basic_header><application_header><io_identifier></io_identifier><message_type>700</message_type><receiver_address>12345678XABC</receiver_address><delivery_monitoring></delivery_monitoring><obsolence_period></obsolence_period></application_header><field45>TAG45</field45><field32>TAG32</field32><field82>NEWTAG</field82></MT700>",xml);
    }


}
