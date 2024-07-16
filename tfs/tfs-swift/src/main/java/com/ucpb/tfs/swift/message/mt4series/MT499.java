package com.ucpb.tfs.swift.message.mt4series;

import com.ucpb.tfs.swift.message.AbstractSwiftMessage;
import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.swift.message.SwiftMessageSchemas;

import javax.xml.bind.annotation.XmlRootElement;


/**
 */
@XmlRootElement(namespace = SwiftMessageSchemas.MT4_NAMESPACE)
public class MT499 extends RawSwiftMessage {


    public int messageLimit() {
        return 2000;
    }

    public boolean divisible() {
        return false;
    }

}
