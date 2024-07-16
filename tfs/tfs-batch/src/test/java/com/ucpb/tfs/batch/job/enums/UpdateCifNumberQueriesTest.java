package com.ucpb.tfs.batch.job.enums;

import static org.junit.Assert.*;
import com.ucpb.tfs.batch.job.enums.UpdateCifNumberQueries;
import org.junit.Test;

public class UpdateCifNumberQueriesTest {

	@Test
	public void testAllUpdateCifNumberValues() {
		for(UpdateCifNumberQueries query: UpdateCifNumberQueries.values()){
			System.out.println(query);
			assertNotNull(query);
		}
	}
}
