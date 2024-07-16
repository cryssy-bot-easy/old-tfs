package com.ucpb.tfs.swift.message.mt7series;

import com.ucpb.tfs.swift.message.AbstractSwiftMessage;
import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.swift.message.SwiftMessageSchemas;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;
import java.util.List;

/**
 */
@XmlRootElement(namespace = SwiftMessageSchemas.MT7_NAMESPACE)
public class MT750 extends RawSwiftMessage {

    public MT750(){

    }

    public int messageLimit() {
        return 10000;
    }

    public boolean divisible() {
        return true;
    }

}
