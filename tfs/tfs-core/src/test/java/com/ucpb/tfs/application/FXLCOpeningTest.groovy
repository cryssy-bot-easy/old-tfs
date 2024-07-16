package com.ucpb.tfs.application

import org.junit.Before
import org.junit.Test
import com.ucpb.tfs.application.command.SaveETSAsDraftCommand

/**
 * User: Jett
 * Date: 7/18/12
 * @author Jett Gamboa
 */
class FXLCOpeningTest {


    SaveETSAsDraftCommand etsDraftCommand;

    @Before
    public void setup() {
        println "Setting up "

        def productAttributes = [
                'product' : 'lc',
                'type' : 'cash',
                'documentType' : 'fx'
        ]

        def productDetails  = [
            'cifNumber' : '12345',
            'cifName' : 'CIF M Name',
            'accountOfficer' : 'settlementaccount officer',
            'ccbdBranchUnitCode' :  '999',

        ]

    }


    @Test
    public void testFXLCOpeningCreateETS() {

        // test creation of a service instruction, assume that instruction was saved as Draft so we create
        // a working copy of the product
        System.out.println("Creating ETS");

    }

}
