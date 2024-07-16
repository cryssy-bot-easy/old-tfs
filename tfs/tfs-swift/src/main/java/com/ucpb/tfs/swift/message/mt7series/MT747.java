package com.ucpb.tfs.swift.message.mt7series;

import com.ucpb.tfs.swift.message.AbstractSwiftMessage;
import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.swift.message.SwiftMessageSchemas;

import javax.xml.bind.annotation.XmlRootElement;

/**
 */
@XmlRootElement(namespace = SwiftMessageSchemas.MT7_NAMESPACE)
public class MT747 extends RawSwiftMessage {

    public int messageLimit() {
        return 2000;
    }

    public boolean divisible() {
        return false;
    }
}
