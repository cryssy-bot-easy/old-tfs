package com.ucpb.tfs.swift.message.builder;

import com.ucpb.tfs.swift.message.MessageBlock;

/**
 */
public interface SwiftMessageBlockBuilder<T> {

    public MessageBlock build(T source, String type);

}
