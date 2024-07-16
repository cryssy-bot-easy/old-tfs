package com.ucpb.tfs.swift.message.writer;

import com.ucpb.tfs.swift.message.Tag;
import com.ucpb.tfs.swift.message.*;

import javax.xml.XMLConstants;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 */
@Deprecated
public class XmlSwiftMessageWriter implements SwiftMessageWriter{

    private static final Map<String,String> namespaces = new HashMap<String,String>();
    private static final Map<String,String> messageNamespaces = new HashMap<String,String>();


    static{
        namespaces.put("xs", XMLConstants.W3C_XML_SCHEMA_NS_URI);
        namespaces.put("f","http://www.fieldnamespace.com");
        namespaces.put("swift","http://www.mtmessages.com");
    }

    static {
        messageNamespaces.put("700","http://www.ucpb.com.ph/tfs/schemas/mt7series");
    }

    @Override
    public String write(RawSwiftMessage source) {
        StringBuilder sb = new StringBuilder();
        sb.append("<MT" + source.getApplicationHeader().getMessageType() + ">");
        appendBasicHeader(sb,source.getBasicHeader());
        appendApplicationHeader(sb,source.getApplicationHeader());
        appendUserHeader(sb,source.getUserHeader());
        appendBody(sb,source.getMessageBlock());
        sb.insert(sb.length(),"</MT" + source.getApplicationHeader().getMessageType()+ ">");
        return sb.toString();
    }

    @Override
    public void write(RawSwiftMessage source, File file) {
        //TODO: implement me.
    }

    private void appendNamespaces(StringBuilder sb){
        for(Map.Entry<String,String> namespace : namespaces.entrySet()){
            sb.append("xmlns:" + namespace.getKey() + "=" + namespace.getValue());
        }
    }

    private void appendBasicHeader(StringBuilder sb, BasicHeader header){
        sb.append("<basic_header>");
        sb.append(encloseInXml("application_identifier",header.getApplicationIdentifier()));
        sb.append(encloseInXml("service_identifier",header.getApplicationIdentifier()));
        sb.append(encloseInXml("lt_identifier",header.getApplicationIdentifier()));
        sb.append(encloseInXml("session_number",header.getApplicationIdentifier()));
        sb.append(encloseInXml("sequence_number",header.getApplicationIdentifier()));
        sb.append("</basic_header>");
    }

    private void appendUserHeader(StringBuilder sb, UserHeader header){
        if (!header.isEmpty()) {
            for (UserTag tag : header.getUserTags()) {
                sb.append(encloseInXml(tag.getTag(),tag.getValue()));
            }
        }
    }

    private void appendApplicationHeader(StringBuilder sb, ApplicationHeader header){
        sb.append("<application_header>");
        sb.append(encloseInXml("io_identifier",header.getIoIdentifier()));
        sb.append(encloseInXml("message_type",header.getMessageType()));
        sb.append(encloseInXml("receiver_address",header.getReceiverAddress().getAddressWithLtPadding()));
        sb.append(encloseInXml("delivery_monitoring",header.getDeliveryMonitoring()));
        sb.append(encloseInXml("obsolence_period",header.getObsolescencePeriod()));
        sb.append("</application_header>");
    }

    private void appendBody(StringBuilder sb,MessageBlock body){
        for(Tag tag : body.getTags()){
            sb.append(appendTag(tag.getTagName(),tag.getValue()));
        }
    }

    private String appendTag(String tagName, String value){
        return "<field" + tagName + ">" + value + "</field" + tagName + ">";
    }

    private String encloseInXml(String tagName,String value){
        return "<" + tagName + ">" + value + "</" + tagName + ">";
    }




}
