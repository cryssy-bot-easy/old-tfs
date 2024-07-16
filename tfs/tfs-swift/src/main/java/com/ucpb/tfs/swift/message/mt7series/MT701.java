package com.ucpb.tfs.swift.message.mt7series;

import com.ucpb.tfs.swift.message.AbstractSwiftMessage;
import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.swift.message.SwiftMessageSchemas;

import javax.xml.bind.annotation.XmlRootElement;


/**
 */
@XmlRootElement(namespace = SwiftMessageSchemas.MT7_NAMESPACE)
public class MT701 extends RawSwiftMessage {


    public MT701() {
        //default constructor
    }


    public int messageLimit() {
        return 10000;
    }

    public boolean divisible() {
        return false;
    }
}
