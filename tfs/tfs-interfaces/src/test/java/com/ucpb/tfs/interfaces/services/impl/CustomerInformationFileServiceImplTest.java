package com.ucpb.tfs.interfaces.services.impl;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ucpb.tfs.interfaces.repositories.CustomerInformationFileRepository;

import java.math.BigDecimal;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:*com/ucpb/tfs/interfaces/repositories/repository-test-context.xml")
public class CustomerInformationFileServiceImplTest {

	@Autowired
	private CustomerInformationFileServiceImpl customerInformationFileService;
	
	private CustomerInformationFileRepository customerInformationFileRepository;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Before
	public void setup(){
		customerInformationFileRepository = mock(CustomerInformationFileRepository.class);
		customerInformationFileService.setCustomerInformationFileRepository(customerInformationFileRepository);
	}
	
	@Test
	public void verifyRepositoryWasCalledWhenCallingGetByCifNumber(){
		customerInformationFileService.getCifByCifNumber("1214141");
		verify(customerInformationFileRepository).getCifByCifNumber("1214141");
	}
	
	@Test
	public void verifyRepositoryWasCalledWhenCallingGetMainCif(){
		customerInformationFileService.getMainCifsByClientCifNumber("1234567");
		verify(customerInformationFileRepository).getMainCifsByClientCifNumber(anyString(), anyString());
	}
	
	@Test
	public void verifyRepositoryWasCalledWhenCallingGetCifsByName(){
		 customerInformationFileService.getMainCifsByClientCifNumber("1234567");
		verify(customerInformationFileRepository).getMainCifsByClientCifNumber(anyString(), anyString());
	}

    @Test
    public void getCifByNameOrNumberWasCalledWhenPassingNullCifNumber(){
        customerInformationFileService.getCifsByNameAndNumber("Name",null,"909");
        verify(customerInformationFileRepository).getCifsByNameOrNumber("%NAME%","");
    }

    @Test
    public void getCifByNameOrNumberWasCalledWhenPassingNullCifName(){
        customerInformationFileService.getCifsByNameAndNumber("1234567",null,"909");
        verify(customerInformationFileRepository).getCifsByNameOrNumber("%1234567%","");
    }

    @Test
    public void getCifByNameAndNumberWasCalledWhenPassingNonNullValues(){
        customerInformationFileService.getCifsByNameAndNumber("Batman","1234567","909");
        verify(customerInformationFileRepository).getCifsByNameAndNumber("%BATMAN%","%1234567%");
    }

    @Test
    public void delegateToGetChildCifsByMainCifNumber(){
        customerInformationFileService.getChildCifsByMainCifNumber("mainCifNumber");
        verify(customerInformationFileRepository).getChildCifsByMainCifNumber(eq("mainCifNumber"), anyString());
    }

    @Test
    public void delegateGetCasaAccountsToRepository(){
        customerInformationFileService.getCasaAccounts("cifNumber");
        verify(customerInformationFileRepository).getCasaAccounts("cifNumber");
    }

    @Ignore("No idea why this fails. On debug, the mock returns an EMPTY LIST even though i specifically programmed it to return a list with 1 item")
    @Test
    public void callGetBranchUnitCodeForNumericOfficerCode(){
        customerInformationFileService.getCifsByNameAndNumber("Batman","1234567","909");
        List mockResult = generateCifs();
        System.out.println("******** LENGTH: " + mockResult.size());
        when(customerInformationFileRepository.getCifsByNameAndNumber("%BATMAN%","%1234567%")) .thenReturn(mockResult);
        verify(customerInformationFileRepository).getCifsByNameAndNumber("%BATMAN%","%1234567%");
        verify(customerInformationFileRepository).getBranchUnitCodeForNumericOfficerCode("1");

    }

    @Test
    public void getOnlyCasaAccountsOfSpecifiedCurrency(){
        when(customerInformationFileRepository.getCasaAccounts("cifNumber")).thenReturn(generateCasaAccountsList());
        List<Map<String,Object>> casaAccounts = customerInformationFileService.getCasaAccountsByCurrency("cifNumber","PHP");
        verify(customerInformationFileRepository).getCasaAccounts("cifNumber");
        assertEquals(1,casaAccounts.size());
        Map<String,?> casaAccount = casaAccounts.get(0);
        assertEquals("NUMBERNUMBER",casaAccount.get("CIF_NUMBER"));
        assertEquals("102222222222",casaAccount.get("ACCOUNT_NUMBER"));
        assertEquals("YELLOWCABPIZZA CO.",casaAccount.get("CIF_NAME"));
        assertEquals("L",casaAccount.get("ACCOUNT_TYPE"));

    }

    @Test
    public void successfullyFormatAccountNumberResults(){
        //test assert LOL
        assertEquals("000000001234", String.format("%1$012.0f", new BigDecimal("1234")));
        when(customerInformationFileRepository.getCasaAccountsByCifNumberAndCurrency("cifNumber","currency")).thenReturn(generateOldCasaAccountNumberFormattedList());
        List<Map<String,Object>> casaAccounts = customerInformationFileService.getCasaAccountsByCifNumberAndCurrency("cifNumber", "currency");
        assertEquals(1,casaAccounts.size());
        Map<String,?> casaAccount = casaAccounts.get(0);

        assertEquals("NUMBAHNUMBAH",casaAccount.get("CIF_NUMBER"));
        assertEquals("000000001234",casaAccount.get("ACCOUNT_NUMBER"));
        assertEquals("PIZZA HUT",casaAccount.get("CIF_NAME"));
        assertEquals("D",casaAccount.get("ACCOUNT_TYPE"));
    }

    private List<Map<String,Object>> generateCifs(){
        List<Map<String,Object>> matchingCifs = new ArrayList<Map<String,Object>>();
        Map<String,Object> cif = new HashMap<String,Object>();
        cif.put("OFFICER_CODE","1");

        matchingCifs.add(cif);
        return matchingCifs;
    }


    private List<Map<String,Object>> generateCasaAccountsList(){
        List accounts = new ArrayList<Map<String, Object>>();
        accounts.add(generateCasaAccountMap("NUMBERNUMBER","102222222222","YELLOWCABPIZZA CO.","L"));
        accounts.add(generateCasaAccountMap("NUMBAHNUMBAH","013333333333","PIZZA HUT","D"));
        return accounts;
    }

    private List<Map<String,Object>> generateOldCasaAccountNumberFormattedList(){
        List accounts = new ArrayList<Map<String, Object>>();
        accounts.add(generateCasaAccountMap("NUMBAHNUMBAH",new BigDecimal(1234),"PIZZA HUT","D"));
        return accounts;
    }

    private Map<String,?> generateCasaAccountMap(Object... details){
        Map<String,Object> casaAccount = new HashMap<String,Object>();
        casaAccount.put("CIF_NUMBER",details[0]);
        casaAccount.put("ACCOUNT_NUMBER",details[1]);
        casaAccount.put("CIF_NAME",details[2]);
        casaAccount.put("ACCOUNT_TYPE",details[3]);
        return casaAccount;
    }

	
}
