package com.ucpb.tfs2.application.service;


import com.ucpb.tfs.domain.service.TradeServiceReferenceNumber;
import com.ucpb.tfs.domain.service.enumTypes.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;

public class TradeServiceReferenceNumberService {

    public TradeServiceReferenceNumber generateReferenceNumber(String processingUnitCode,
                                                 DocumentClass documentClass,
                                                 DocumentType documentType,
                                                 DocumentSubType1 documentSubType1,
                                                 DocumentSubType2 documentSubType2,
                                                 ServiceType serviceType) {

        // TODO: put real code here

        // Outgoing MT Generation
        if(documentClass == DocumentClass.MT) {

            if(serviceType == ServiceType.CREATE) {

                // just use a timestamp generator to generate reference for this
                DateFormat simpleDF = new SimpleDateFormat("yyyyMMddHHmmss");
                String mtTradeReference = "MTGEN" + simpleDF.format(new Date());

                return new TradeServiceReferenceNumber(mtTradeReference);
            }
        }

        // CDT Remittance
        if(documentClass == DocumentClass.CDT) {
            if(serviceType == ServiceType.REMITTANCE) {

                DateFormat simpleDF = new SimpleDateFormat("yyyyMMddHHmmss");
                String mtTradeReference = "CDT" + simpleDF.format(new Date());

                return new TradeServiceReferenceNumber(mtTradeReference);
            }
        }
        
        
        //EBP Settlement
        if(documentClass == DocumentClass.BP) {
        	if(serviceType == ServiceType.SETTLEMENT) {
        		DateFormat simpleDF = new SimpleDateFormat("yyyyMMddHHmmss");
                String mtTradeReference = "BPSET" + simpleDF.format(new Date());

                return new TradeServiceReferenceNumber(mtTradeReference);
        	}
        }
        
        //Payment of Other Import Charges
        if(documentClass == DocumentClass.IMPORT_CHARGES) {
        	if(serviceType == ServiceType.PAYMENT_OTHER) {
        		DateFormat simpleDF = new SimpleDateFormat("yy");
        		String mtTradeReference = processingUnitCode + "-" + simpleDF.format(new Date()) + "-" + RandomStringUtils.random(5, false, true);
        		
        		return new TradeServiceReferenceNumber(mtTradeReference);
        	}
        }

        return new TradeServiceReferenceNumber("");
    }

}
