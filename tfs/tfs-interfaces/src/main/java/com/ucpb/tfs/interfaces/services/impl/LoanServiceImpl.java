package com.ucpb.tfs.interfaces.services.impl;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import com.ucpb.tfs.interfaces.domain.Loan;
import com.ucpb.tfs.interfaces.repositories.LoanRepository;
import com.ucpb.tfs.interfaces.services.LoanService;
import com.ucpb.tfs.interfaces.services.SequenceService;
import com.ucpb.tfs.interfaces.services.exception.LoanAlreadyReleasedException;
import com.ucpb.tfs.interfaces.util.DateUtil;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class LoanServiceImpl implements LoanService{

	private LoanRepository loanRepository;

    private SequenceService sequenceService;

    public long insertLoan(Loan loan, boolean hasCramOverride) {
        long sequenceNumber = sequenceService.getLoanSequence();
        loan.setTransactionSequenceNumber(sequenceNumber);
		loanRepository.insertLoan(loan, hasCramOverride ? Loan.WITH_CRAM_OVERRIDE : Loan.NO_CRAM_OVERRIDE);
        return sequenceNumber;
	}

    /**
     *  Retrieve the most recent loan request by account number.
     *  Although this works, it'd be preferable to query loan requests by their sequence number.
     * @param accountNumber
     * @return
     */

    @Deprecated
	public Loan getLoan(long accountNumber) {
		return loanRepository.getLoan(accountNumber);
	}

    @Override
    public Loan getLoanRequestBySequenceNumber(long sequenceNumber) {
        return loanRepository.getLoanBySequenceNumber(sequenceNumber);
    }

    public Map<String, Object> getLoanDetails(String documentNumber) {
        return loanRepository.getLoanDetails(documentNumber.replaceAll("-",""));
    }

    public void setLoanRepository(LoanRepository loanRepository) {
		this.loanRepository = loanRepository;

	}

	public long reverseLoan(Long accountNumber,String user) throws LoanAlreadyReleasedException, NonExistentLoanException {
		Loan loan = loanRepository.getLoan(accountNumber);

        if(loan != null){
			loan.setUnlinkFlag(Loan.UNLINK_FLAG);
            loan.setTrustUserId(user);
            long sequenceNumber = sequenceService.getLoanSequence();
            loan.setTransactionSequenceNumber(sequenceNumber);
            loanRepository.insertLoan(loan,Loan.NO_CRAM_OVERRIDE);
            return sequenceNumber;
		}
		
        throw new NonExistentLoanException();
	}

    @Override
    public List<Map<String, Object>> getLoanErrorRecord(Long sequenceNumber) {
        return loanRepository.getLoanErrorRecord(sequenceNumber);
    }

    @Override
    public boolean isHoliday(String date) {
        try {
            return loanRepository.isHoliday(DateUtil.formatDateStringToInt("MMddyy","MM/dd/yyyy",date)) > 0;
        } catch (ParseException e) {
            throw new RuntimeException("Source date format is invalid",e);
        }
    }

    public void setSequenceService(SequenceService sequenceService) {
        this.sequenceService = sequenceService;
    }






}
