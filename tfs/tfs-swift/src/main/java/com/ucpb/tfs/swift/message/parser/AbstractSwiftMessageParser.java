package com.ucpb.tfs.swift.message.parser;

import com.ucpb.tfs.swift.message.ApplicationHeader;
import com.ucpb.tfs.swift.message.BasicHeader;
import com.ucpb.tfs.swift.message.MessageBlock;
import com.ucpb.tfs.swift.message.RawSwiftMessage;

/**
 */
public abstract class AbstractSwiftMessageParser implements SwiftMessageParser {


    @Override
    public RawSwiftMessage parse(String message) throws ParseException{
        RawSwiftMessage swiftMessage = new RawSwiftMessage();
        swiftMessage.setBasicHeader(getBasicHeader(message));
        swiftMessage.setApplicationHeader(getApplicationHeader(message));
        swiftMessage.setMessageBlock(getMessageBlock(message));
        return swiftMessage;
    }

    protected abstract BasicHeader getBasicHeader(String message);

    protected abstract ApplicationHeader getApplicationHeader(String message);

    protected abstract MessageBlock getMessageBlock(String message);



}
