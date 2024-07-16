package com.ucpb.tfs.swift.message.writer;

import com.ucpb.tfs.swift.message.RawSwiftMessage;

/**
 */
public interface XmlWriter {

    public String write(RawSwiftMessage source);
}
