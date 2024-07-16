package com.ucpb.tfs.swift.message.builder;

import com.ucpb.tfs.swift.message.RawSwiftMessage;

/**
 *
 */
public interface RawSwiftMessageBuilder {

    public void build(Object source, String type, RawSwiftMessage message);

}
