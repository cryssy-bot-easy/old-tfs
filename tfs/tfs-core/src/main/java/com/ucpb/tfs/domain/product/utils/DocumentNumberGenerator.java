package com.ucpb.tfs.domain.product.utils;

import com.ucpb.tfs.domain.product.TradeProductRepository;
import com.ucpb.tfs.utils.DateUtil;
import com.ucpb.tfs.utils.LuhnUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * User: IPCVal
 * Date: 8/9/12
 */
public class DocumentNumberGenerator {

	private static final int MAX_SEQUENCE_LENGTH = 5;
	
	private TradeProductRepository tradeProductRepository;
	
    public static String generateDocumentNumber() {
        // TODO: Rework Document Number Generator
        String random = "909-01-932-12-" + RandomStringUtils.random(5, false, true);
        random = random + "1";
        return random;
    }

    public String generateDocumentNumber(String branchUnitCode, String documentCode, String processingUnitCode) {
        System.out.println("GENERATING DOCUMENT NUMBER");
        System.out.println("DOC NUMBER PARTS:");
        System.out.println("[BRANCH UNIT CODE] " + branchUnitCode);
        System.out.println("[DOCUMENT CODE CODE] " + documentCode);
        System.out.println("[PROCESSING UNIT CODE] " + processingUnitCode);
        String sequenceNumber = tradeProductRepository.getDocumentNumberSequence(documentCode, processingUnitCode, Calendar.getInstance().get(Calendar.YEAR));

        if (sequenceNumber == null) {
    		return null;
    	}

        tradeProductRepository.incrementDocumentNumberSequence(documentCode, processingUnitCode, Calendar.getInstance().get(Calendar.YEAR));

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

    public String generateSettlementAccountNumber(String documentCode) {
        String sequenceNumber = tradeProductRepository.getSettlementAccountSequence(documentCode);

        if (sequenceNumber == null) {
            return null;
        }

        return sequenceNumber;
    }
}
