package com.ucpb.tfs.swift.message.mt2series;

import com.ucpb.tfs.swift.message.AbstractSwiftMessage;
import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.swift.message.SwiftMessageSchemas;

import javax.xml.bind.annotation.XmlRootElement;

/**
 */
@XmlRootElement(namespace = SwiftMessageSchemas.MT2_NAMESPACE)
public class MT202 extends RawSwiftMessage{

    public int messageLimit() {
        return 2000;
    }

    public boolean divisible() {
        return false;
    }
}
