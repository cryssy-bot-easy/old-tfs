package com.ucpb.tfs.interfaces.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Ignore//Configured to connect to UCPB database
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:*com/ucpb/tfs/interfaces/repositories/repository-integration-test-context.xml")
public class CustomerInformationFileRepositoryIntegrationTest {

	@Autowired
	private CustomerInformationFileRepository customerInformationFileRepository;
	
	@Test
	public void successfullyQueryACifNumber() {
		Map<String, ?> result = customerInformationFileRepository.getCifByCifNumber("A000001");
		assertNotNull(result);
		assertEquals("A000001",result.get("CFCIF#"));
		assertEquals("AGGABAO C C         ",result.get("CFSNME"));
		assertEquals("AGGABAO                                 ",result.get("CFNA1"));
		assertEquals("CYNTHIA                                 ",result.get("CFNA1A"));

	}

	
	@Test
	public void successfullyGetCifsWithSimilarNames(){
		List<Map<String,Object>> cifsList = customerInformationFileRepository.getCifsByCifName("ABAD M M            ");
		assertEquals(6,cifsList.size());
	}
	

}
