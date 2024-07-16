package com.ucpb.tfs.swift;

import com.ucpb.tfs.swift.message.MT700;
import com.ucpb.tfs.util.FileUtil;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 */
//@Ignore
@RunWith(MockitoJUnitRunner.class)
public class SwiftMessageParserTest {

    @Mock
    private SpelEvaluatingSwiftMessageBuilder messageBuilder;

    @InjectMocks
    private SwiftMessageParser parser = new SwiftMessageParser();

    private static final StringBuilder swiftMessage = new StringBuilder();
    private static File validSwiftMessage;



    @BeforeClass
    public static void setup() throws IOException {
        validSwiftMessage = FileUtil.getFile("/swift/5620MT7X.089");
        assertTrue(validSwiftMessage.exists());

        BufferedReader reader = new BufferedReader(new FileReader(validSwiftMessage));
        String line = null;
        while((line = reader.readLine()) != null){
            swiftMessage.append(line);
            swiftMessage.append('\n');
        }
    }

    @Before
    public void setupMessageBuilder(){
        when(messageBuilder.build(eq("700"),any(Map.class))).thenReturn(new MT700());
    }

    @Test
    public void successfullyParseMTMessage() throws ParseException {
        System.out.println("***** PARSING: " + swiftMessage.toString());
        MT700 message = (MT700) parser.parse(swiftMessage.toString());
        assertNotNull(message);
        assertEquals("700",message.getApplicationHeader().getMessageType());
    }


}
