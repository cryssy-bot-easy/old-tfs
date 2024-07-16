package com.ucpb.tfs.swift.message;

import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * Generic representation of a swift message
 */
@XmlRootElement(namespace = SwiftMessageSchemas.SWIFT_MESSAGE)
@XmlType(propOrder={"basicHeader","applicationHeader","userHeader","messageBlock","tags","trailer"})
@XmlAccessorType(XmlAccessType.PROPERTY)

public class RawSwiftMessage {

    private BasicHeader basicHeader;
    private ApplicationHeader applicationHeader;
    private UserHeader userHeader;
    private MessageBlock messageBlock ;
    private Trailer trailer;


    public String getReference(){
        return messageBlock.getReference();
    }

    @XmlAttribute(name = "type")
    public String getMessageType(){
        if(applicationHeader != null){
            return applicationHeader.getMessageType();
        }
        return null;
    }

    @XmlTransient
    public String getMessageSender() {
        if(basicHeader != null && !StringUtils.isEmpty(basicHeader.getLtIdentifier())){
            return basicHeader.getLtIdentifier().substring(0,8);
        }
        return null;
    }

    @XmlTransient
    public Integer getSequenceNumber(){
        if(messageBlock != null){
            return messageBlock.getSequenceNumber();
        }
        return null;
    }

    @XmlTransient
    public Integer getSequenceTotal(){
        if(messageBlock != null){
            return messageBlock.getSequenceTotal();
        }
        return null;
    }

//    @XmlTransient
    public Trailer getTrailer() {
        return trailer;
    }

    @XmlElement(name = "trailer",namespace = SwiftMessageSchemas.SWIFT_MESSAGE)
    public void setTrailer(Trailer trailer) {
        this.trailer = trailer;
    }

    public BasicHeader getBasicHeader() {
        return basicHeader;
    }

    @XmlElement(name = "application_header",namespace = SwiftMessageSchemas.SWIFT_MESSAGE)
    public ApplicationHeader getApplicationHeader() {
        return applicationHeader;
    }

    @XmlElement(name = "basic_header",namespace = SwiftMessageSchemas.SWIFT_MESSAGE)
    public void setBasicHeader(BasicHeader basicHeader) {
        this.basicHeader = basicHeader;
    }

    public void setApplicationHeader(ApplicationHeader applicationHeader) {
        this.applicationHeader = applicationHeader;
    }

    public UserHeader getUserHeader() {
        return userHeader;
    }


    @XmlElement(name = "user_header",namespace = SwiftMessageSchemas.SWIFT_MESSAGE)
    public void setUserHeader(UserHeader userHeader) {
        this.userHeader = userHeader;
    }

    @XmlElement(name = "message_block",namespace = SwiftMessageSchemas.SWIFT_MESSAGE)
    public MessageBlock getMessageBlock() {
        return messageBlock;
    }

    public void update(String tagName, String value){
        messageBlock.update(tagName,value);
    }

    public void setMessageBlock(MessageBlock messageBlock) {
        this.messageBlock = messageBlock;
    }

    @XmlAnyElement
    @XmlElementWrapper(name = "tags", namespace = SwiftMessageSchemas.SWIFT_MESSAGE)
    public List<Tag> getTags(){
    	if(messageBlock != null){
    		return messageBlock.getTags();
    	}
    	return Collections.emptyList();
    }

    public Tag fetch(String tagName){
       return messageBlock.fetch(tagName);
    }

    public void remove(String tagName){
        messageBlock.remove(tagName);
    }

    /**
     * Returns the length of the Message's body.
     * @return message body length
     */
    public int length(){
        return messageBlock.length();
    }
    
    public void addTag(String tagName,String tagValue){
    	if(messageBlock == null){
    		messageBlock=new MessageBlock();
    	}
    	messageBlock.addTag(tagName, tagValue);
    }
    
    public void addTag(String tagName,String tagValue,int tagPosition){
    	if(messageBlock == null){
    		messageBlock=new MessageBlock();
    	}
    	messageBlock.addTag(tagName, tagValue,tagPosition);
    }

    public void setReceiverAddress(SwiftAddress address){
        if(applicationHeader == null){
            applicationHeader = new ApplicationHeader();
        }
        applicationHeader.setReceiverAddress(address);
    }

}
