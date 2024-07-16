package com.ucpb.tfs.interfaces.services.impl;

import com.ucpb.tfs.interfaces.domain.Availment;
import com.ucpb.tfs.interfaces.repositories.FacilityRepository;
import com.ucpb.tfs.interfaces.services.RatesService;
import com.ucpb.tfs.interfaces.services.SequenceService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:*com/ucpb/tfs/interfaces/repositories/repository-test-context.xml")
public class FacilityServiceImplTest {

	@Autowired
	private FacilityServiceImpl facilityService;
	
	private FacilityRepository facilityRepository;

    private RatesService ratesService;

    private SequenceService sequenceService;
	
	@Before
	public void setup(){
		facilityRepository = mock(FacilityRepository.class);
        ratesService = mock(RatesService.class);
        sequenceService = mock(SequenceService.class);

		facilityService.setFacilityRepository(facilityRepository);
        facilityService.setRatesService(ratesService);
        facilityService.setSequenceService(sequenceService);

        Map<String,Object> conversion = new HashMap<String,Object>();
        conversion.put("CONVERSION_RATE",BigDecimal.ONE);
        when(ratesService.getConversionRateByType(anyString(), anyString(), anyInt())).thenReturn(BigDecimal.ONE);
	}
	
	@Test
	public void delegateGetFacilityByCif(){
		facilityService.getFacilitiesByCifNumber("1234567");
		verify(facilityRepository).getFacilitiesByCifNumber(eq("1234567"), anyString());
	}

    @Test
    public void successfullyDelegateGetFacilitiesByCifNumberAndFacilityTypesToRepository(){
        String[] input = new String[]{"FFT", "FTF"};
        List<Map<String,Object>> facilities = new ArrayList<Map<String,Object>>();

        Map<String,Object> facility = new HashMap<String,Object>();
        facility.put(FacilityServiceImpl.CLIENT_CIF_NUMBER,"CIFNUMBER");
        facility.put(FacilityServiceImpl.FACILITY_ID,new BigDecimal("1"));
        facility.put(FacilityServiceImpl.FACILITY_TYPE,"FFT");

        facilities.add(facility);
        //when(facilityRepository.getFacilitiesByType("CIFNUMBER",input)).thenReturn(facilities);

        facilityService.getFacilitiesByCifNumberAndFacilityTypes("CIFNUMBER", "FFT", "FTF");
        //verify(facilityRepository).getFacilitiesByType("CIFNUMBER", input);
//        verify(sequenceService).getFacilityBalanceSequence();
    }
	
	@Test
	public void callUpdateLoanIfLoanAlreadyExists(){
		Availment availment = new Availment();
		availment.setDocumentNumber("DocumentNumber1");
        availment.setOriginalAmount(BigDecimal.TEN);
        availment.setOutstandingBalance(BigDecimal.TEN);
		
		when(facilityRepository.updateFacilityAvailment(availment)).thenReturn(Integer.valueOf(1));
		facilityService.earmarkAvailment(availment);
		verify(facilityRepository,never()).getFacilityAvailmentCount("DocumentNumber1");
		verify(facilityRepository).updateFacilityAvailment(availment);
	}
	
	@Test
	public void callInsertIfLoanDoesNotExist(){
		Availment availment = new Availment();
		availment.setDocumentNumber("DocumentNumber1");
        availment.setOriginalAmount(new BigDecimal("102"));
        availment.setOutstandingBalance(new BigDecimal("132"));

		when(facilityRepository.updateFacilityAvailment(availment)).thenReturn(Integer.valueOf(0));
		facilityService.earmarkAvailment(availment);
		verify(facilityRepository).updateFacilityAvailment(availment);
		verify(facilityRepository).insertFacilityAvailment(availment);
	}
	
	@Test
	public void delegateDeleteToRepositoryWhenUnearmarking(){
		facilityService.unearmarkAvailment("DocumentNumber1");
		verify(facilityRepository).deleteFacilityAvailment("DocumentNumber1");
	}
	
	@Test
	public void delegateToGetFacilityByCifAndType(){
		facilityService.getFacilitiesByCifNumberAndType("cifNumber", "TYPE1");
		verify(facilityRepository).getFacilitiesByCifNumberAndType(eq("cifNumber"), anyString(), eq("TYPE1"));
	}

    @Test
    public void delegateToUpdateFacilityBalance(){
        Availment availment = new Availment();
        availment.setDocumentNumber("DocumentNumber");
        availment.setOriginalAmount(new BigDecimal("12133"));
        availment.setOutstandingBalance(new BigDecimal("432"));

        when(facilityRepository.getAvailment("DocumentNumber")).thenReturn(availment);
        when(facilityRepository.updateFacilityAvailmentBalance(availment)).thenReturn(Integer.valueOf(1));
        assertTrue(facilityService.updateAvailmentAmount("DocumentNumber", "PHP", new BigDecimal(131), Boolean.FALSE));
        verify(facilityRepository).getAvailment("DocumentNumber");
        verify(facilityRepository).updateFacilityAvailmentBalance(any(Availment.class));
    }

    @Test
    public void dontDelegateWhenNoAvailmentWasQueried(){
        when(facilityRepository.getAvailment("DocumentNumber")).thenReturn(null);
        assertFalse(facilityService.updateAvailmentAmount("DocumentNumber", "PHP", new BigDecimal(131), Boolean.FALSE));
        verify(facilityRepository,never()).updateFacilityAvailment(any(Availment.class));

    }

    @Test
    public void delegateGetFacilitiesByCifNumberByFacilityTypesToRepo(){
        facilityService.getFacilitiesByCifNumberAndFacilityTypes("cifNumber", "TYPE1", "TYPE2", "TYPE3");
        verify(facilityRepository).getFacilitiesByType("cifNumber", "TYPE1", "TYPE2", "TYPE3");

    }
		
	
}
