package com.ucpb.tfs.swift;

import com.ucpb.tfs.swift.message.Tag;
import com.ucpb.tfs.swift.message.ApplicationHeader;
import com.ucpb.tfs.swift.message.MessageBlock;
import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.swift.message.mt7series.MT700;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

/**
 */
public class JAXBExample {

    public static void main(String[] args) {

        RawSwiftMessage message = new MT700();
        MessageBlock messageBlock = new MessageBlock();
        messageBlock.addTag("40A","VALUE");
        messageBlock.addTag("36B","ANOTHERVALUE");
        message.setMessageBlock(messageBlock);

        ApplicationHeader header = new ApplicationHeader();
        header.setMessageType("700");

        message.setApplicationHeader(header);

        StringWriter stringWriter = new StringWriter();

        try {

            JAXBContext jaxbContext = JAXBContext.newInstance(MT700.class,Tag.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(message, stringWriter);
//            jaxbMarshaller.marshal(message, System.out);
            System.out.println(stringWriter.toString());

        } catch (JAXBException e) {
            e.printStackTrace();
        }

    }


}