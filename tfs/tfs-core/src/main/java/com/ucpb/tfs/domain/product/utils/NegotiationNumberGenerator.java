package com.ucpb.tfs.domain.product.utils;

import com.ucpb.tfs.domain.product.TradeProductRepository;
import com.ucpb.tfs.domain.reference.ProductId;
import com.ucpb.tfs.domain.reference.ProductServiceReference;
import com.ucpb.tfs.domain.reference.ProductServiceReferenceRepository;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import com.ucpb.tfs.utils.DateUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;

/**
 * User: IPCVal
 * Date: 9/22/12
 */
public class NegotiationNumberGenerator {

//    private TradeProductRepository tradeProductRepository;
//    private ProductServiceReferenceRepository productServiceReferenceRepository;
    private static final int MAX_SEQUENCE_LENGTH = 5;

    public static String generateNegotiationNumber(String branchUnitCode, String processingUnitCode, String documentCode, String sequenceNumber) {

        if (sequenceNumber == null) {
            return null;
        }

        // TODO: Test Negotiation Number Generator
//        String random = "909"+"-"+"19"+"-"+"932"+"-"+"12"+"-" + RandomStringUtils.random(6, false, true);
        //sequence dapat ang RandomStringUtils
        String random = processingUnitCode+"-"+documentCode+"-"+branchUnitCode+"-"+ DateUtil.getLastTwoDigitsOfYear(new Date())+"-" +
                StringUtils.leftPad(sequenceNumber, MAX_SEQUENCE_LENGTH, '0');
        return random;
    }

//    public void setTradeProductRepository(TradeProductRepository tradeProductRepository) {
//        this.tradeProductRepository = tradeProductRepository;
//    }
//
//    public void setProductServiceReferenceRepository (ProductServiceReferenceRepository productServiceReferenceRepository){
//        this.productServiceReferenceRepository = productServiceReferenceRepository;
//    }
}
