package com.ucpb.tfs.interfaces.gateway.parser;

import com.ucpb.tfs.interfaces.gateway.CasaResponse;
import com.ucpb.tfs.interfaces.gateway.TransactionCode;
import org.springframework.util.Assert;

/**
 */
public class ParserFactory {

    public ParsingStrategy getFailedResponseParser(TransactionCode transactionCode){
        Assert.notNull(transactionCode, "Transaction Code must not be null");
        ParsingStrategy parser = null;
        switch (transactionCode){
            case DEBIT_TO_CURRENT:
            case DEBIT_TO_SAVINGS:
            case CREDIT_TO_CURRENT:
            case CREDIT_TO_SAVINGS:
                parser = new DefaultFailedResponseParser();
                break;
            case DEBIT_ERROR_CORRECT_SAVINGS:
            case CREDIT_ERROR_CORRECT_CURRENT:
            case INQUIRE_STATUS_CURRENT:
            case INQUIRE_STATUS_SAVINGS:
            case INQ_STATUS_SAVINGS:
                parser = new FailedResponseParser();
                break;
            case DEBIT_TO_FOREIGN:
            case CREDIT_TO_FOREIGN:
                parser = new FailedResponseWithReverseIdParser();
                break;
        }
        return parser;
    }

    public ParsingStrategy getSuccessfulResponseParser(TransactionCode transactionCode){
        Assert.notNull(transactionCode, "Transaction Code must not be null");
        ParsingStrategy parser = null;
        switch (transactionCode){
            case DEBIT_TO_CURRENT:
            case DEBIT_TO_SAVINGS:
            case DEBIT_TO_FOREIGN:
            case CREDIT_TO_FOREIGN:
            case CREDIT_TO_CURRENT:
            case CREDIT_TO_SAVINGS:
            case DEBIT_ERROR_CORRECT_SAVINGS:
            case CREDIT_ERROR_CORRECT_CURRENT:
            case CREDIT_ERROR_CORRECT_FOREIGN:
            case DEBIT_ERROR_CORRECT_FOREIGN:
                parser = new SuccessfulWithReverseIdParser();
                break;
            case INQ_STATUS_SAVINGS:
            case INQUIRE_STATUS_CURRENT:
            case INQUIRE_STATUS_SAVINGS:
                parser = new SuccessfulStatusInquiryResponseParser();
                break;
        }
        return parser;
    }

}
