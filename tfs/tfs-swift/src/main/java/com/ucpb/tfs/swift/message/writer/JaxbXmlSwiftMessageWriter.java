package com.ucpb.tfs.swift.message.writer;

import com.ucpb.tfs.swift.message.*;
import com.ucpb.tfs.swift.message.mt1series.MT103;
import com.ucpb.tfs.swift.message.mt1series.MT103Plus;
import com.ucpb.tfs.swift.message.mt1series.MT199;
import com.ucpb.tfs.swift.message.mt2series.MT202;
import com.ucpb.tfs.swift.message.mt2series.MT299;
import com.ucpb.tfs.swift.message.mt4series.MT400;
import com.ucpb.tfs.swift.message.mt4series.MT410;
import com.ucpb.tfs.swift.message.mt4series.MT412;
import com.ucpb.tfs.swift.message.mt4series.MT499;
import com.ucpb.tfs.swift.message.mt7series.*;
import com.ucpb.tfs.swift.message.mt7series.MT700;
import com.ucpb.tfs.swift.message.mt7series.MT701;
import com.ucpb.tfs.swift.message.mt7series.MT707;
import com.ucpb.tfs.swift.message.mt7series.MT730;
import com.ucpb.tfs.swift.message.mt7series.MT740;
import com.ucpb.tfs.swift.message.mt7series.MT742;
import com.ucpb.tfs.swift.message.mt7series.MT747;
import com.ucpb.tfs.swift.message.mt7series.MT750;
import com.ucpb.tfs.swift.message.mt7series.MT752;
import com.ucpb.tfs.swift.message.mt7series.MT760;
import com.ucpb.tfs.swift.message.mt7series.MT767;
import com.ucpb.tfs.swift.message.mt7series.MT799;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import java.io.File;
import java.io.StringWriter;
import java.io.Writer;

/**
 */
public class JaxbXmlSwiftMessageWriter implements XmlWriter{

    private static final JAXBContext JAXB_CONTEXT;

    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance(
                    RawSwiftMessage.class,
                    ApplicationHeader.class,
                    SwiftAddress.class,
                    BasicHeader.class,
                    Tag.class);
        } catch (JAXBException e) {
            throw new RuntimeException("Failed to initialize the JAXB context",e);
        }
    }


    public String write(RawSwiftMessage message) {

        Writer stringWriter = new StringWriter();

        try {

            Marshaller jaxbMarshaller = JAXB_CONTEXT.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);

            if(!StringUtils.isEmpty(message.getMessageType())){
                JAXBElement jaxbElement = new JAXBElement(new QName(getNamespaceByType(message.getMessageType()), "mt" + message.getMessageType()),
                        message.getClass(),
                        message);
                jaxbMarshaller.marshal(jaxbElement, stringWriter);
            }  else{
                jaxbMarshaller.marshal(message,stringWriter);
            }
            return stringWriter.toString();
        } catch (JAXBException e) {
            //TODO: create exception for writer
            throw new RuntimeException(e);
        }

    }

    private String getNamespaceByType(String messageType){
        Assert.notNull(messageType);
        char messageClass = messageType.charAt(0);
        return "http://www.ucpb.com.ph/tfs/schemas/mt" + messageClass + "series";
    }

}
