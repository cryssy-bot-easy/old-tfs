package com.ucpb.tfs.application.query.interfaces;


import com.ucpb.tfs.application.query.interfaces.exception.LoanAlreadyReleasedException;

import java.util.Map;

public interface LoanService {

    public long insertLoan(Loan loan);

    public Loan getLoan(long accountNumber);

    public Map<String,Object> getLoanDetails(String documentNumber);

    public Boolean reverseLoan(Long accountNumber) throws LoanAlreadyReleasedException;
}

