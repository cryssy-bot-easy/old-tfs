package com.ucpb.tfs2.webservices
import com.google.gson.Gson
import com.ucpb.tfs2.application.service.OutgoingMTService
import org.jboss.resteasy.client.ClientRequest
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.transaction.annotation.Transactional

import javax.ws.rs.core.MediaType

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:postlc-unitTestContext.xml")
@Transactional
class PaymentRestServiceTest {

    @Autowired
    OutgoingMTService outgoingMTService;

    @Test
    @Rollback(false)
    public void testCDTPaymentSave() {

        Gson gson = new Gson()
        ClientRequest request = new ClientRequest("http://localhost:9090/tfs-core/api/cdt/payment/save");

        // test data
        Map testData =  [
            username: "jsgamboa",
            ied: "2012P07  D001963",
            documentNumber: "TESTDOCU",
            messageType: "MT799",
            paymentDetails : [
                [amount:"100", tradeSuspenseAccount:"", paymentMode:"CASA", accountNumber:"12121", status:"Not Paid", modeOfPayment:"CASA", currency:"PHP"],
                [amount:"200", tradeSuspenseAccount:"xxxx", paymentMode:"CHECK", accountNumber:"", status:"Not Paid", modeOfPayment:"Check", currency:"PHP"]
            ]
        ]

        // convert test map data to JSON
        String jsontext = gson.toJson(testData)

        // request.accept("application/xml").pathParameter("id", 1).body( MediaType.APPLICATION_XML, xmltext);
        request.accept(MediaType.APPLICATION_JSON_TYPE).body(MediaType.APPLICATION_JSON_TYPE, jsontext)

        String response = request.postTarget(String.class)

        Map responseMap = gson.fromJson(response, Map.class)

        println "the service returned: " + responseMap
    }

}
