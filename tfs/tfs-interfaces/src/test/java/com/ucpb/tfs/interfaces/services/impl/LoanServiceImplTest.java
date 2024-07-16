package com.ucpb.tfs.interfaces.services.impl;

import com.ucpb.tfs.interfaces.services.SequenceService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ucpb.tfs.interfaces.domain.Loan;
import com.ucpb.tfs.interfaces.repositories.LoanRepository;
import com.ucpb.tfs.interfaces.services.exception.LoanAlreadyReleasedException;
import com.ucpb.tfs.interfaces.services.impl.LoanServiceImpl;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:*com/ucpb/tfs/interfaces/repositories/repository-test-context.xml")
public class LoanServiceImplTest {

	
	@Autowired
	@Qualifier("LoanService")
	private LoanServiceImpl loanService;
	
	private LoanRepository loanRepository;
    private SequenceService sequenceService;
	
	@Before
	public void setup(){
		loanRepository = mock(LoanRepository.class);
		loanService.setLoanRepository(loanRepository);
        sequenceService = mock(SequenceService.class);
        loanService.setSequenceService(sequenceService);
	}
	
	@Test
	public void delegateInsertLoanToRepository(){
		Loan loan = new Loan();
		loanService.insertLoan(loan,false);
		verify(loanRepository).insertLoan(loan,"");
        verify(sequenceService).getLoanSequence();
	}
	
	@Test
	public void delegateGetLoanToRepository(){
		loanService.getLoan(123L);
		verify(loanRepository).getLoan(123L);
	}

    @Test
    public void delegateIsHolidayToRepository(){
        loanService.isHoliday("12/01/2013");
        verify(loanRepository).isHoliday(120113);
    }
	
	@Test
	public void successfullyReverseAValidLoan() throws LoanAlreadyReleasedException, NonExistentLoanException {
		Loan loan = new Loan();
		when(loanRepository.getLoan(123L)).thenReturn(loan);
        when(sequenceService.getLoanSequence()).thenReturn(1L);
		when(loanRepository.insertLoan(loan,"")).thenReturn(1);
		assertEquals(1L,loanService.reverseLoan(123L,"user"));
		verify(loanRepository).insertLoan(loan,"");
	}

    @Ignore
	@Test(expected = LoanAlreadyReleasedException.class)
	public void failToReverseAProcessedLoan() throws LoanAlreadyReleasedException, NonExistentLoanException {
		Loan loan = new Loan();
		loan.setTransactionStatus("Y");
		when(loanRepository.getLoan(123L)).thenReturn(loan);
		when(loanRepository.insertLoan(loan,"")).thenReturn(1);
		loanService.reverseLoan(123L,"user");
	}
	
	@Test(expected = NonExistentLoanException.class)
	public void failToUpdateNonExistentLoan() throws LoanAlreadyReleasedException, NonExistentLoanException {
		when(loanRepository.getLoan(123L)).thenReturn(null);
		loanService.reverseLoan(123L,"user");
	}
	

}
