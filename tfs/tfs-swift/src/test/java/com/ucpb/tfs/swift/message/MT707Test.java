package com.ucpb.tfs.swift.message;

import com.ucpb.tfs.swift.SwiftMessageBuilder;
import com.ucpb.tfs.swift.SwiftMessageParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:parser-config.xml")
public class MT707Test {

    private MT707 mt707;
    private File mt;

    @Autowired
    @Qualifier("swiftMessageBuilder")
    private SwiftMessageBuilder swiftMessageBuilder;


    @Test
    public void mapValuesToMt707(){
        Map<String,Object> details = new HashMap<String,Object>();
        details.put("maximumCreditAmount","200");
        details.put("issueDate","12/12/2012");
        details.put("amountTo","88888888.88");
        details.put("productAmount","7777777");
        details.put("documentNumber","documentNumbah");
        details.put("exporterName","exporter name");
        details.put("exporterAddress","exporter address");


        mt707 = (MT707) swiftMessageBuilder.build("707",details);
        assertNotNull(mt707);

        assertEquals("121212",mt707.getField31C());
//        assertEquals("documentNumbah",mt707.getField20());
        assertEquals("exporter name exporter address",mt707.getField59());
    }



}
