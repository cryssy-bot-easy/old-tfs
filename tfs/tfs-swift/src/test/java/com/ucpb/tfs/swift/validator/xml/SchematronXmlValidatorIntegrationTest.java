package com.ucpb.tfs.swift.validator.xml;

import com.ucpb.tfs.swift.message.ApplicationHeader;
import com.ucpb.tfs.swift.message.BasicHeader;
import com.ucpb.tfs.swift.message.MessageBlock;
import com.ucpb.tfs.swift.message.SwiftAddress;
import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.swift.message.mt1series.MT103;
import com.ucpb.tfs.swift.validator.ValidationError;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 */
public class SchematronXmlValidatorIntegrationTest {


    private SchematronXmlValidator validator;

    @Before
    public void setup(){
        validator = new SchematronXmlValidator("/swift/schematron/compiled/swift-master.xsl","/swift/formatter/swift-format.xsl");
    }


    @Test
    public void validMt103ProducesNoValidationErrors(){

        RawSwiftMessage mt103 = buildSwiftMessage("103",MT103.class);
        MessageBlock messageBlock = new MessageBlock();
        messageBlock.addTag("20","9090792813000019");
        messageBlock.addTag("23B","CRED");
        messageBlock.addTag("26T","A00");
        messageBlock.addTag("32A","130225USD121561724,");
        messageBlock.addTag("33B", "USD121561724,");
//        messageBlock.addTag("36","12,1000");
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

        mt103.setMessageBlock(messageBlock);

        List<ValidationError> errors = validator.validate(mt103);
        for(ValidationError error : errors){
            System.out.println(error.getMessage());
        }

        assertTrue(errors.isEmpty());
    }

    @Test
    public void field57AMustBePresent(){
        RawSwiftMessage mt103 = buildSwiftMessage("103",MT103.class);
        MessageBlock messageBlock = new MessageBlock();
        messageBlock.addTag("56A","Dummy value");

        mt103.setMessageBlock(messageBlock);

        List<ValidationError> errors = validator.validate(mt103);
        assertFalse(errors.isEmpty());

        assertEquals("Field 57a must be present if Field 56a is present (Error C81)",errors.get(0).getMessage());
    }


    private RawSwiftMessage buildSwiftMessage(String messageType, Class<? extends RawSwiftMessage> messageClass){
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
        applicationHeader.setMessageType(messageType);
        SwiftAddress address = new SwiftAddress();
        address.setBankIdentifierCode("ASASASAS");
        address.setBranchCode("XXX");
        applicationHeader.setReceiverAddress(address);
        applicationHeader.setMessagePriority("N");
        swiftMessage.setApplicationHeader(applicationHeader);

        return swiftMessage;
    }

}
