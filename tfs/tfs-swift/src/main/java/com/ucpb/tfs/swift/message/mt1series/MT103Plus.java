package com.ucpb.tfs.swift.message.mt1series;

import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.swift.message.SwiftMessageSchemas;

import javax.xml.bind.annotation.XmlRootElement;

/**
 */
@XmlRootElement(namespace = SwiftMessageSchemas.MT1_NAMESPACE)
public class MT103Plus extends RawSwiftMessage {
}