package com.ucpb.tfs.interfaces.services;

import java.util.List;
import java.util.Map;

import com.ucpb.tfs.interfaces.domain.Loan;
import com.ucpb.tfs.interfaces.services.exception.LoanAlreadyReleasedException;
import com.ucpb.tfs.interfaces.services.impl.NonExistentLoanException;
import org.apache.ibatis.annotations.Param;

public interface LoanService {

	public long insertLoan(Loan loan, boolean overbalanceOverride);
	
	public Loan getLoan(long accountNumber);

    public Loan getLoanRequestBySequenceNumber(long sequenceNumber);

    public Map<String,Object> getLoanDetails(String documentNumber);

    public long reverseLoan(Long accountNumber, String user) throws LoanAlreadyReleasedException, NonExistentLoanException;

    public List<Map<String,Object>> getLoanErrorRecord(Long sequenceNumber);

    public boolean isHoliday(String date);

}
