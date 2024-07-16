package com.ucpb.tfs.application

import com.ucpb.tfs.domain.instruction.ServiceInstruction
import com.ucpb.tfs.domain.instruction.ServiceInstructionId
import com.ucpb.tfs.domain.product.LetterOfCredit
import com.ucpb.tfs.utils.UtilSetFields
import org.junit.Test

/**
 * User: Jett
 * Date: 7/16/12
 */
class FXLCTest {

    @Test
    public void testFXLC() {

        // documentType = fx or dm
        // type = cash, regular, standby, deferred, revolving

        def productAttributes = ['product' : 'lc', 'type' : 'cash', 'documentType' : 'fx']
        def productDetails = ['shipper' : 'jett',
                'usancePeriod': '30',
                'purpose': 'to test this',
                'documentType': 'FOREIGN',
                'partialShipment' : 'true',
                'amountNegotiated' : '1500000.55']

        // instantiate passing along attributes
        ServiceInstructionId sid = new ServiceInstructionId("sid1");
        ServiceInstruction si = new ServiceInstruction(sid, productDetails, productAttributes)

        // instantiate a new LC
        LetterOfCredit lc = new LetterOfCredit();
        UtilSetFields.copyMapToObject(lc, productDetails);
        println lc.usancePeriod
        println lc.documentType

        def lcopeningSI = ['type':'ETS']
        ServiceInstruction serviceInstruction = new ServiceInstruction();
        UtilSetFields.copyMapToObject(serviceInstruction, lcopeningSI)
        println serviceInstruction.type

    }

}
