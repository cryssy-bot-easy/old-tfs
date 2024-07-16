package com.ucpb.tfs.domain.product.utils;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;

import com.ucpb.tfs.domain.product.TradeProductRepository;
import com.ucpb.tfs.utils.DateUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;


@RunWith(PowerMockRunner.class)
@ContextConfiguration("classpath:*generator-test-context.xml")
@PrepareForTest( { DateUtil.class })
public class DocumentNumberGeneratorTest {

	private DocumentNumberGenerator documentNumberGenerator = new DocumentNumberGenerator();
	
	private TradeProductRepository tradeProductRepository;
	
	@Before
	public void setup(){
		tradeProductRepository = mock(TradeProductRepository.class);
		when(tradeProductRepository.getDocumentNumberSequence(anyString(), anyString(), anyInt())).thenReturn("1","2","3","4");
		documentNumberGenerator.setTradeProductRepository(tradeProductRepository);
	    PowerMockito.mockStatic(DateUtil.class);
	    when(DateUtil.getLastTwoDigitsOfYear(any(Date.class))).thenReturn("12");
	}
	
	@Test
	public void successfullyGenerateDocumentNumber(){
		String documentNumber1 = documentNumberGenerator.generateDocumentNumber("33", "01", "3333");
		String documentNumber2 = documentNumberGenerator.generateDocumentNumber("33", "02", "3333");
		assertFalse(documentNumber1.equals(documentNumber2));
		assertEquals("33-01-3333-12-00001-0",documentNumber1);
		assertEquals("33-02-3333-12-00002-1",documentNumber2);
	}

	@Test
	public void doNotGenerateADocumentNumberWhenNoDataFound(){
		when(tradeProductRepository.getDocumentNumberSequence(anyString(), anyString(), anyInt())).thenReturn(null);
		String documentNumber = documentNumberGenerator.generateDocumentNumber("STUFF", "CODE", "CODE");
		assertNull(documentNumber);
	}
}
