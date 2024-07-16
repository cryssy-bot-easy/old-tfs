package com.ucpb.tfs.swift.message.mt7series;

import javax.xml.bind.annotation.XmlRootElement;

import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.swift.message.SwiftMessageSchemas;

@XmlRootElement(namespace = SwiftMessageSchemas.MT7_NAMESPACE)
public class MT734 extends RawSwiftMessage {

    public int messageLimit() {
        return 10000;
    }

    public boolean divisible() {
        return false;
    }
}