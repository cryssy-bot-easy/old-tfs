package com.ucpb.tfs.swift.message.builder;

import com.ucpb.tfs.swift.message.BasicHeader;

/**
 */
public interface BasicHeaderBuilder<T> {

    public BasicHeader build(T source, String type);
}
