package com.ucpb.tfs.domain.payment.casa;

import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: Robbie
 * Date: 5/16/13
 * Time: 1:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class CasaAccountFactoryTest {


    @Test
    public void parseValidCasaAccount(){
        CasaAccount account = CasaAccountFactory.getInstance("112400000076");
    }

}
