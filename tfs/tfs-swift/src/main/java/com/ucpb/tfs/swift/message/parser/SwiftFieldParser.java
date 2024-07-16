package com.ucpb.tfs.swift.message.parser;

/**
 * Generic interface for translating field contents to
 * SWIFT Format.
 */
public interface SwiftFieldParser {

    public String parse(String field);


}
