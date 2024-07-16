package com.ucpb.tfs.swift;

import com.ucpb.tfs.swift.message.SwiftMessage;

/**
 */
public interface SwiftMessageBuilder {

    public SwiftMessage build(String type, Object fields);

}
