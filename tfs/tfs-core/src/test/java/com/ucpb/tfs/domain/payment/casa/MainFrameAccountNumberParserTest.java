package com.ucpb.tfs.domain.payment.casa;

import com.ucpb.tfs.domain.payment.casa.parser.MainFrameAccountNumberParser;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;

/**
 * Created with IntelliJ IDEA.
 * User: Robbie
 * Date: 5/16/13
 * Time: 1:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class MainFrameAccountNumberParserTest {


    private MainFrameAccountNumberParser parser = new MainFrameAccountNumberParser();


    @Test
    public void rejectIncompatibleAccountNumber(){
        assertFalse(parser.canParse("112400000076"));
    }
}
