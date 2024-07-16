package com.ucpb.tfs.swift.message.builder;

import com.ucpb.tfs.swift.message.ApplicationHeader;
import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.swift.message.Tag;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class SwiftMessageBuilderTest {

    private SwiftMessageBuilder swiftMessageBuilder = new SwiftMessageBuilder();


    @Test(expected = IllegalArgumentException.class)
    public void exceptionOnNullSourceMessage(){
        swiftMessageBuilder.wrap(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionOnNullApplicationHeader(){
        RawSwiftMessage rawSwiftMessage = new RawSwiftMessage();
        swiftMessageBuilder.wrap(rawSwiftMessage);
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionOnNullMessageType(){
        RawSwiftMessage rawSwiftMessage = new RawSwiftMessage();
        ApplicationHeader header = new ApplicationHeader();
        rawSwiftMessage.setApplicationHeader(header);

        swiftMessageBuilder.wrap(rawSwiftMessage);
    }

    @Test
    public void convertMessageTypeTo99(){
        RawSwiftMessage rawSwiftMessage = new RawSwiftMessage();
        ApplicationHeader header = new ApplicationHeader();
        header.setMessageType("701");

        rawSwiftMessage.setApplicationHeader(header);

        RawSwiftMessage wrapper = swiftMessageBuilder.wrap(rawSwiftMessage);
        assertEquals("799",wrapper.getMessageType());
    }

    @Test
    public void successfullyWrapMessageContents(){
        RawSwiftMessage rawSwiftMessage = new RawSwiftMessage();
        ApplicationHeader header = new ApplicationHeader();
        header.setMessageType("701");

        rawSwiftMessage.setApplicationHeader(header);
        rawSwiftMessage.addTag("30","this are the contents of tag 30");
        rawSwiftMessage.addTag("45","this is tag 45");
        rawSwiftMessage.addTag("50","LINE1\r\nLINE2\r\nline3");

        RawSwiftMessage wrapper = swiftMessageBuilder.wrap(rawSwiftMessage);
        List<Tag> tags = wrapper.getTags();

        assertEquals(":30:THIS ARE THE CONTENTS OF TAG 30\r\n" +
                ":45:THIS IS TAG 45\r\n" +
                ":50:LINE1\r\nLINE2\r\nLINE3",tags.get(0).getValue());
        assertEquals("799",wrapper.getMessageType());


    }

}
