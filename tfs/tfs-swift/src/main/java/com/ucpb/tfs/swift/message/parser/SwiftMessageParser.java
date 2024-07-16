package com.ucpb.tfs.swift.message.parser;

import com.ucpb.tfs.swift.message.RawSwiftMessage;

/**
 */
public interface SwiftMessageParser {

    public RawSwiftMessage parse(String message) throws ParseException;

}
