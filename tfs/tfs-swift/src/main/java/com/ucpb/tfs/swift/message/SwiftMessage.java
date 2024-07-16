package com.ucpb.tfs.swift.message;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.List;

/**
 */
@Deprecated
public class SwiftMessage {

    private String block3;

    private BasicHeader basicHeader = new BasicHeader();
    private ApplicationHeader applicationHeader = new ApplicationHeader();

    private static final String FIELD = "field";
    private static final char NEW_LINE = '\n';

    public SwiftMessage(){
        //do nothing
    }

    public int messageLimit(){
        return -1;
    }

    public boolean messageLimitReached(){
        return messageLimit() != -1 ? messageLimit() > toString().length() : false;
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        if(basicHeader!= null){
            builder.append("{1:" + basicHeader.asMTFormat() + "}");
        }
        if(applicationHeader!= null){
            builder.append("{2:" + applicationHeader.asMTFormat() + "}");
        }
        if(getBlock3() != null){
            builder.append("{3:" + getBlock3() + "}");
        }
        if(getBlock4() != null){
            builder.append("{4:" + getBlock4() + "\n-}");
        }
        return builder.toString();
    }

    public final byte[] pack() {
        return toString().getBytes();
    }

    public final byte[] pack(String encoding) throws UnsupportedEncodingException {
        return toString().getBytes(encoding);
    }

    public String getBlock3() {
        return block3;
    }

    public void setBlock3(String block3) {
        this.block3 = block3;
    }

    public final String getBlock4(){
        StringBuilder sb = new StringBuilder();
        for(Field field : this.getClass().getDeclaredFields()){
            if(field.getName().startsWith(FIELD)){
                field.setAccessible(true);
                String tag = field.getName().substring(FIELD.length());
                try {
                    Object value = field.get(this);
                    if(value != null){
                        sb.append(NEW_LINE + getFieldValue(value,tag));
                    }
                } catch (IllegalAccessException e) {
                    //do nothing
                }
            }
        }
        return sb.toString();
    }

    private String getFieldValue(Object value,String tag){
        String result = "";
        if(value instanceof List){
            for(Object item : (List)value){
                result.concat(String.format(":%1$:%2$s\n",tag.toUpperCase(),item.toString().toUpperCase()));
            }
            return result;
        }
        return String.format(":%1$s:%2$s",tag.toUpperCase(),value.toString().toUpperCase());
    }

    public BasicHeader getBasicHeader() {
        return basicHeader;
    }

    public void setBasicHeader(BasicHeader basicHeader) {
        this.basicHeader = basicHeader;
    }

    public ApplicationHeader getApplicationHeader() {
        return applicationHeader;
    }

    public void setApplicationHeader(ApplicationHeader applicationHeader) {
        this.applicationHeader = applicationHeader;
    }

}
