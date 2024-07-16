package com.ucpb.tfs.swift.message.builder;

import com.ucpb.tfs.swift.message.ApplicationHeader;

/**
 */
public interface ApplicationHeaderBuilder<T> {

    public ApplicationHeader build(T source, String type);

}
