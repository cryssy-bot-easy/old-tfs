package com.ucpb.tfs.domain.product.utils;

import com.ucpb.tfs.domain.product.TradeProductRepository;
import com.ucpb.tfs.utils.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * User: IPCVal
 * Date: 9/22/12
 */
public class ICNumberGenerator {

    private static final int MAX_SEQUENCE_LENGTH = 6;

    private TradeProductRepository tradeProductRepository;

    public static String generateIcNumber() {

        // TODO: Rework IC Number Generator
        String random = "909-15-932-12-" + RandomStringUtils.random(6, false, true);
        return random;
    }

    public String generateIcNumber(String documentCode, String processingUnitCode, String branchUnitCode) {
        System.out.println("generateIcNumber");
        String sequenceNumber = tradeProductRepository.getIcNumberSequence(documentCode, processingUnitCode, Calendar.getInstance().get(Calendar.YEAR));
        System.out.println("sequenceNumber:"+sequenceNumber);

        if (sequenceNumber == null) {
            return null;
        }

        tradeProductRepository.incrementIcNumberSequence(documentCode, processingUnitCode, Calendar.getInstance().get(Calendar.YEAR));

        String number = String.format("%1$s-%2$s-%3$s-%4$s-%5$s",
                processingUnitCode,
                documentCode,
                branchUnitCode,
                DateUtil.getLastTwoDigitsOfYear(new Date()),
                StringUtils.leftPad(sequenceNumber, MAX_SEQUENCE_LENGTH, '0'));

        return number;
    }

    public void setTradeProductRepository(TradeProductRepository tradeProductRepository) {
        this.tradeProductRepository = tradeProductRepository;
    }
}
