package com.ucpb.tfs.application.query.payment;

import java.util.Map;

/**
 */
public interface LoanFinder {

    public Map<String,Object> getLoan(String accountNumber);

}
