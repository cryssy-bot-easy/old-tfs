package com.ucpb.tfs.domain.cdt;

import com.ucpb.tfs.domain.cdt.services.PAS5FilesLoaderService;
import com.ucpb.tfs.utils.UtilSetFields;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.util.HashMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:postlc-unitTestContext.xml")
@Transactional
//@Transactional(propagation = Propagation.REQUIRED)
//@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class CDTTests {

    @Autowired
    RefPas5ClientRepository refPas5ClientRepository;

    @Autowired
    CDTPaymentRequestRepository cdtPaymentRequestRepository;

    @Autowired
    PAS5FilesLoaderService pas5FilesLoaderService;

    @Test
    @Rollback(false)
    public void testClientFileUpload() {

        URL url = this.getClass().getResource("/cdt/PAS5CLIENTS.xls");

        System.out.println("processing client file: " + url.getFile());

        pas5FilesLoaderService.loadClientFile(url.getFile().toString());

    }

    @Test
    @Rollback(false)
    public void testPaymentRequestFileUpload() {

        URL url = this.getClass().getResource("/cdt/PAS5TODAY.xls");

        System.out.println("processing payment request file: " + url.getFile());

        pas5FilesLoaderService.loadPaymentRequest(url.getFile().toString());

    }

    @Test
    @Rollback(false)
    public void testHistoryFileUpload() {

        URL url = this.getClass().getResource("/cdt/PAS5PAYMENTHISTORY.xls");

        System.out.println("processing payment history file: " + url.getFile());

        pas5FilesLoaderService.loadHistory(url.getFile().toString());

    }

//    @Test
    @Rollback(false)
    public void testAbandonedFileUpload() {

        URL url = this.getClass().getResource("/cdt/PAS5ABANDONED.xls");

        System.out.println("processing abandoned file: " + url.getFile());

        pas5FilesLoaderService.loadAbandoned(url.getFile().toString());

    }


//    @Test                                                                                                 d
    @Rollback(false)
    public void testPas5Reference() {

        RefPas5Client refPas5Client = new RefPas5Client();

        HashMap refPas5Data = new HashMap();

        refPas5Data.put("ccn", "IM0003144011");

        UtilSetFields.copyMapToObject(refPas5Client, refPas5Data);

        refPas5ClientRepository.persist(refPas5Client);

    }

    @Test
    @Rollback(false)
    public void testHistoryRetrieval() {
        String unitCode = "";
        for(CDTPaymentRequest cdtPaymentRequest : cdtPaymentRequestRepository.getHistoryUpdatedToday(unitCode)) {
            System.out.println(cdtPaymentRequest.getIedieirdNumber());
        }

    }


}
