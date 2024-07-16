package com.ucpb.tfs.swift.message.writer;

import com.ucpb.tfs.swift.message.ApplicationHeader;
import com.ucpb.tfs.swift.message.MessageBlock;
import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.swift.message.mt7series.MT700;
import org.junit.Test;

/**
 */
public class JaxbXmlSwiftMessageWriterTest {

   private JaxbXmlSwiftMessageWriter writer = new JaxbXmlSwiftMessageWriter();


   @Test
   public void successfullyWriteSwiftMessage(){
       RawSwiftMessage message = new RawSwiftMessage();
       ApplicationHeader applicationHeader = new ApplicationHeader();
       applicationHeader.setMessageType("740");
       message.setApplicationHeader(applicationHeader);
       MessageBlock messageBlock = new MessageBlock();
       messageBlock.addTag("30B","NEW VALUE");
       message.setMessageBlock(messageBlock);

       String result = writer.write(message);
       System.out.println(result);
   }
}
