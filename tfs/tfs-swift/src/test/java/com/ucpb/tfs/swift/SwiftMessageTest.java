package com.ucpb.tfs.swift;

import com.ucpb.tfs.swift.message.MT747;
import com.ucpb.tfs.swift.message.SwiftMessage;
import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 */
@Ignore("The SwiftMessage class is no longer being maintained/used")
public class SwiftMessageTest {

    public SwiftMessage swiftMessage;


    @Test
    public void successfullyConvertMessageBodyToSwiftMessageFormat(){

        MT747 mt747 = new MT747();
        mt747.setField20("blah blah blah");
        mt747.setField30("hmm");

        String swiftMessageBody = mt747.getBlock4();
        System.out.println(swiftMessageBody);
        assertTrue(swiftMessageBody.contains("30:"));
        assertTrue(swiftMessageBody.contains("BLAH BLAH BLAH"));
        assertTrue(swiftMessageBody.contains("HMM"));

    }

    @Test
    public void successfullyConvertToSwiftMessageString(){
        MT747 mt747 = new MT747();
        mt747.setField20("blah blah blah");
        mt747.setField30("hmm");

        String message = mt747.toString();
        System.out.println(message);
        assertEquals("{1:}{2:}{4:\n" +
                ":20:BLAH BLAH BLAH\n" +
//                ":21:\n" +
                ":30:HMM" +
//                ":31E:\n" +
//                ":32B:\n" +
//                ":33B:\n" +
//                ":34B:\n" +
//                ":39A:\n" +
//                ":39B:\n" +
//                ":39C:\n" +
//                ":72:\n" +
//                ":77A:-}"
                "\n-}",message);
    }

    @Test
    public void successfullyConvertMessageBodyToSwiftFormat(){
        MT747 mt747 = new MT747();
        mt747.setField20("blah blah blah");
        mt747.setField30("hmm");

        String message = mt747.getBlock4();
        System.out.println(message);
        assertEquals("\n:20:BLAH BLAH BLAH\n" +
//                ":21:\n" +
                ":30:HMM"
//                ":31E:\n" +
//                ":32B:\n" +
//                ":33B:\n" +
//                ":34B:\n" +
//                ":39A:\n" +
//                ":39B:\n" +
//                ":39C:\n" +
//                ":72:\n" +
//                ":77A:"
                ,message);
    }


}
