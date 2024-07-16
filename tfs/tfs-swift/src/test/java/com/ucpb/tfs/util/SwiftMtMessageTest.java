package com.ucpb.tfs.util;

import com.ucpb.tfs.swift.message.SwiftMessageSchemas;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertTrue;

/**
 */
public class SwiftMtMessageTest {

    private XmlValidator xmlValidator = new XmlValidator("/swift/schemas/swift-message.xsd");

    private static final String SWIFT_SCHEMA = "xmlns=\"" + SwiftMessageSchemas.SWIFT_MESSAGE + "\"";


    @Test
    public void validSwiftMessageHasNoErrors(){
        String swiftMessage = "<SwiftMessage " + SWIFT_SCHEMA  +" " +
                "><basic_header><application_identifier>A</application_identifier><service_identifier>01</service_identifier" +
                "><lt_identifier>AAAAAAAAAAAA</lt_identifier><session_number>0000</session_number><sequence_number>000000</sequence_number></basic_header><application_header><io_identifier>I</io_identifier><message_type>700</message_type><receiver_address><bank_identifier_code></bank_identifier_code><branch_code></branch_code></receiver_address><message_priority>N</message_priority></application_header></SwiftMessage>";

        List<String> errors = xmlValidator.validate(swiftMessage);
        printErrors(errors);
        assertTrue(errors.isEmpty());
    }


    private void printErrors(List<String> errors){
        for(String error : errors){
            System.out.println(error);
        }
    }

}
