package com.ucpb.tfs.interfaces.services.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ucpb.tfs.interfaces.domain.enums.RateType;
import com.ucpb.tfs.interfaces.repositories.RatesRepository;
import com.ucpb.tfs.interfaces.services.impl.RatesServiceImpl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:*com/ucpb/tfs/interfaces/repositories/repository-test-context.xml")
public class RatesServiceImplTest {

	@Autowired
	private RatesServiceImpl ratesService;
	
	private RatesRepository ratesRepository;
	
	@Before
	public void setup(){
		ratesRepository = mock(RatesRepository.class);
		ratesService.setRatesRepository(ratesRepository);
	}
	
	@Test
	public void successfullyDelegateGetDailyRatesToRepo(){
		ratesService.getDailyRates();
//		verify(ratesRepository).getRates(anyInt());
		verify(ratesRepository).getRates();
	}
	
	@Test
	public void successfullyDelegateGetRatesByBaseCurrencyToRepo(){
		ratesService.getRatesByBaseCurrency("PHP");
		verify(ratesRepository).getRatesByBaseCurrency(anyInt(), eq("PHP"));
	}
	
	@Test
	public void successfullyDelegateGetConversionRateByTypeToRepo(){
		ratesService.getConversionRateByType("PHP", "USD", RateType.BANK_NOTE_SELL);
//		verify(ratesRepository).getConversionRateByType(eq("%PHP%"), eq("%USD%"), eq(RateType.BANK_NOTE_SELL), anyInt());
		verify(ratesRepository).getConversionRateByType(eq("%PHP%"), eq("%USD%"), eq(RateType.BANK_NOTE_SELL));

	}
	
}
