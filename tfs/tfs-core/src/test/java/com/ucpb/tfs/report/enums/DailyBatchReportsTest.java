package com.ucpb.tfs.report.enums;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.springframework.util.Assert;

public class DailyBatchReportsTest {

	@Test
	public void testGetAllLabels() {
		List<String> labels = DailyBatchReports.getAllLabels();
		assertNotNull(labels);
		Assert.notEmpty(labels);
	}
	
	@Test
	public void testGetAllFunctions(){
		List<String> functions = DailyBatchReports.getAllFunctions();
		System.out.println(functions);
		assertNotNull(functions);
		Assert.notEmpty(functions);
	}
}
