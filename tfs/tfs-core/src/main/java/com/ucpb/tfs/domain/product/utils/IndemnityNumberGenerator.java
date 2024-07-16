package com.ucpb.tfs.domain.product.utils;

import com.ucpb.tfs.domain.product.TradeProductRepository;
import com.ucpb.tfs.domain.product.enums.IndemnityCodeEnum;
import com.ucpb.tfs.utils.DateUtil;
import com.ucpb.tfs.utils.LuhnUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * User: Marv
 * Date: 9/25/12
 */

public class IndemnityNumberGenerator {

    private static final int MAX_SEQUENCE_LENGTH = 5;

    private TradeProductRepository tradeProductRepository;

    public static String generateBGNumber() {
        // TODO: rework BG Number generator
        String random = "909-01-932-12-" + RandomStringUtils.random(5, false, true) + "-" + RandomStringUtils.random(1, false, true);
        return random;
    }

    public static String generateBENumber() {
        // TODO: rework BE Number generator
        String random = "909-02-932-12-" + RandomStringUtils.random(5, false, true) + "-" + RandomStringUtils.random(1, false, true);
        return random;
    }

    public String generateBgNumber(String branchUnitCode, String processingUnitCode) {
        String documentCode = IndemnityCodeEnum.BG_ISSUANCE.toString();
        return generateIndemnityNumber(branchUnitCode, documentCode, processingUnitCode);
    }

    public String generateBeNumber(String branchUnitCode, String processingUnitCode) {
        String documentCode = IndemnityCodeEnum.BE_ISSUANCE.toString();
        return generateIndemnityNumber(branchUnitCode, documentCode, processingUnitCode);
    }

    private String generateIndemnityNumber(String branchUnitCode, String documentCode, String processingUnitCode) {

        String sequenceNumber = tradeProductRepository.getIndemnityNumberSequence(documentCode, processingUnitCode, Calendar.getInstance().get(Calendar.YEAR));

        if (sequenceNumber == null) {
            return null;
        }

        tradeProductRepository.incrementIndemnityNumberSequence(documentCode, processingUnitCode, Calendar.getInstance().get(Calendar.YEAR));

        String number = String.format("%1$s-%2$s-%3$s-%4$s-%5$s",
                processingUnitCode,
                documentCode,
                branchUnitCode,
                DateUtil.getLastTwoDigitsOfYear(new Date()),
                StringUtils.leftPad(sequenceNumber, MAX_SEQUENCE_LENGTH, '0'));

        return number + "-" + LuhnUtil.getCheckDigit(number, "-");
    }

    public void setTradeProductRepository(TradeProductRepository tradeProductRepository) {
        this.tradeProductRepository = tradeProductRepository;
    }
}
