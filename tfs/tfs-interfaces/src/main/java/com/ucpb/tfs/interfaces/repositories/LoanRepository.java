package com.ucpb.tfs.interfaces.repositories;

import com.ucpb.tfs.interfaces.domain.Loan;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;


public interface LoanRepository {

	public int insertLoan(@Param("loan")Loan loan, @Param("overrideFlag")String overrideFlag);
	
	public Loan getLoan(@Param("accountNumber") long accountNumber);
	
	public Loan getLoanBySequenceNumber(@Param("sequenceNumber") long sequenceNumber);

    public Map<String,Object> getLoanDetails(@Param("documentNumber")String documentNumber);
	
	public List<Map<String,Object>> getLoanErrorRecord(@Param("sequenceNumber") long sequenceNumber);

    public int isHoliday(@Param("date")int date);
	
}
