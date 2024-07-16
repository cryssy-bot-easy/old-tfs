package com.ucpb.tfs.swift.message.writer;

import com.ucpb.tfs.swift.message.ApplicationHeader;
import com.ucpb.tfs.swift.message.BasicHeader;
import com.ucpb.tfs.swift.message.UserHeader;
import com.ucpb.tfs.swift.message.Tag;
import com.ucpb.tfs.swift.message.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.StringWriter;
import java.io.Writer;

/**
 */
public class JaxbSwiftMessageWriter implements SwiftMessageWriter{


    @Override
    public String write(RawSwiftMessage message) {

        Writer stringWriter = new StringWriter();

        try {

            JAXBContext jaxbContext = JAXBContext.newInstance(message.getClass(),ApplicationHeader.class, UserHeader.class, BasicHeader.class,Tag.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(message, stringWriter);

            return stringWriter.toString();
        } catch (JAXBException e) {
            //TODO: create exception for writer.
            throw new RuntimeException(e);
        }

    }

    @Override
    public void write(RawSwiftMessage source, File file) {

    }
}
