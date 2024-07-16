package com.ucpb.tfs2.services

import com.google.gson.Gson
import com.ipc.rbac.domain.UserActiveDirectoryId
import com.ucpb.tfs.domain.payment.Payment
import com.ucpb.tfs.domain.product.DocumentNumber
import com.ucpb.tfs.domain.service.TradeService
import com.ucpb.tfs.domain.service.enumTypes.DocumentClass
import com.ucpb.tfs.domain.service.enumTypes.ServiceType
import com.ucpb.tfs2.application.service.PaymentService
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.transaction.annotation.Transactional


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:postlc-unitTestContext.xml")
@Transactional
class PaymentServiceTest {

    @Autowired
    PaymentService paymentService;

    @Test
    public void testPaymentCreation() {

        UserActiveDirectoryId userActiveDirectoryId = new UserActiveDirectoryId("test")
        DocumentNumber docNumber = new DocumentNumber("test")
        String documentNumber = "test"

        TradeService tradeService = new TradeService(docNumber, DocumentClass.CDT, null, null, null, ServiceType.PAYMENT, userActiveDirectoryId, documentNumber)

        def dmap = [[accountNumber: 1, b:""], [a:2, c:3]]

        def paymentDetails = [ "paymentDetails" : [
                [amount:"100", tradeSuspenseAccount:"", paymentMode:"CASH", accountNumber:"12121", status:"Not Paid", modeOfPayment:"Cash", currency:"PHP"],
                [amount:"200", tradeSuspenseAccount:"xxxx", paymentMode:"CHECK", accountNumber:"", status:"Not Paid", modeOfPayment:"Check", currency:"PHP"]
             ]
        ]
//
        Payment payment = paymentService.createProductPaymentFromMap(tradeService, (Map) paymentDetails)

        Gson gson = new Gson()

        println gson.fromJson(gson.toJson(payment), Map.class)

        println payment

    }

}
