package com.ucpb.tfs.swift.message.writer;

import com.ucpb.tfs.swift.message.RawSwiftMessage;

import java.io.File;

/**
 */
public interface SwiftMessageWriter {

    public String write(RawSwiftMessage source);

    public void write(RawSwiftMessage source, File file);


}
