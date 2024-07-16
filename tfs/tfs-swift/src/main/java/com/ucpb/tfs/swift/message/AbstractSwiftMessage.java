package com.ucpb.tfs.swift.message;

import com.ucpb.tfs.swift.message.Tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 */
public abstract class AbstractSwiftMessage {

    private RawSwiftMessage rawSwiftMessage;

    public abstract int messageLimit();

    public abstract boolean divisible();

    public AbstractSwiftMessage(){
        this.rawSwiftMessage = new RawSwiftMessage();
    }

    public AbstractSwiftMessage(RawSwiftMessage rawSwiftMessage){
        this.rawSwiftMessage = rawSwiftMessage;
    }

    public RawSwiftMessage getRawSwiftMessage() {
        return rawSwiftMessage;
    }


    public void addTag(String tag, String value){
        rawSwiftMessage.getMessageBlock().addTag(tag,value);
    }

    public int length(){
        return rawSwiftMessage.length();
    }

    public final boolean limitReached(){
        return length() > messageLimit();
    }


    public void addTag(Tag tag){
        rawSwiftMessage.getMessageBlock().addTag(tag);
    }

    public void addTag(Tag tag, int position){
        rawSwiftMessage.getMessageBlock().addTag(tag,position);
    }

    public Tag fetch(String tagName){
        return rawSwiftMessage.fetch(tagName);
    }





}
