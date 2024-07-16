package com.ucpb.tfs.functionaltests;

import com.ucpb.tfs.batch.util.FileUtil;
import com.ucpb.tfs.interfaces.services.exception.ValidationException;
import com.ucpb.tfs.interfaces.services.impl.SwiftMessageServiceImpl;
import com.ucpb.tfs.swift.message.AbstractSwiftMessage;
import com.ucpb.tfs.swift.message.ApplicationHeader;
import com.ucpb.tfs.swift.message.MessageBlock;
import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.swift.message.mt7series.MT700;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertTrue;

/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:swift-config-context.xml","classpath:placeholder-resolver.xml"})
public class SwiftConfigurationIntegrationTest {


    @Autowired
    private SwiftMessageServiceImpl service;

    private RawSwiftMessage message;

    private File outputFile = new File("src/test/resources/5620MT7x.1");

    @Before
    public void setup(){
        message = new RawSwiftMessage();
        MessageBlock block = new MessageBlock();
        block.addTag("20A", "NEW VALUE");
        block.addTag("20","121313");
        message.setMessageBlock(block);

        ApplicationHeader applicationHeader = new ApplicationHeader();
        applicationHeader.setMessageType("700");
        message.setApplicationHeader(applicationHeader);
    }

    @Before
    @After
    public void cleanup(){
        outputFile.delete();
    }

    @Test
    public void sendMtMessage() throws ValidationException, IOException {
        service.sendMessage(message);
        assertTrue(outputFile.exists());
        List<String> fileContents = FileUtil.read(outputFile);
        for(String content : fileContents){
            System.out.println(content);
        }
    }
}
