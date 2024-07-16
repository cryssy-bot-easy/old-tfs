package com.ucpb.tfs.interfaces.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:*amla-app-context.xml")
public class AmlaReportIntegrationTest {

	private static final String OUTPUT_FILE_PATH = "src/test/resources/amla/processed/TRN76.txt";
	
	private File outputFile;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Before
	public void setup(){
		outputFile = new File(OUTPUT_FILE_PATH);
		outputFile.delete();
	}
	
//	@After
	public void teardown(){
		outputFile.delete();
	}
	
	@Test
    @Ignore
	public void successfullyGenerateReportToDirectory() throws InterruptedException{
		assertFalse(outputFile.exists());
		assertEquals(2,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM TRANSACTIONLOG"));
		//give time for the poller to write the report
		Thread.sleep(6000);
		assertTrue(outputFile.exists());
		List<String> fileContents = getFileContents();
        assertNotNull(fileContents);
        assertEquals(2,fileContents.size());
//		String fileContents = "7777,2012-08-28 00:00:00.000000000" + '\n' +
//							  "8888,2012-08-28 00:00:00.000000000";
	}

    @Test
    @Ignore
    public void forceReportGeneration() throws InterruptedException{
        assertFalse(outputFile.exists());
        jdbcTemplate.execute("DELETE FROM TRANSACTIONLOG");
        jdbcTemplate.execute("INSERT INTO TRANSACTIONLOG \n" +
                "(TXNREFERENCENUMBER,TXNDATE,DEALNUMBER,TRANSACTIONTYPECODE,TRANSACTIONSUBTYPE,TRANSACTIONMODE\n" +
                ",TRANSACTIONAMOUNT,DIRECTION,BRANCHCODE,ACCOUNTNUMBER,SETTLEMENTCURRENCY,EXCHANGERATE,SETTLEM" +
                "ENTAMOUNT,PURPOSE,CPACCOUNTNO,CPNAME1,CPNAME2,CPNAME3,CP_ADDRESS1,CP_ADDRESS2,CP_ADDRESS3,CPI" +
                "NSTITUTION,CPINSTITUTIONCOUNTRY,CORRESPONDENTBANKNAME,CORRESPONDENTCOUNTRYCODE,CORRESPONDENTA" +
                "DDRESS1,CORRESPONDENTADDRESS2,CORRESPONDENTADDRESS3,INTRINSTITUTIONNAME,INTRINSTITUTIONCOUNTR" +
                "Y,INTRINSTITUTIONADDR1,INTRINSTITUTIONADDR2,INTRINSTITUTIONADDR3,BENEFICIARYNAME1,BENEFICIARY" +
                "NAME2,BENEFICIARYNAME3,BENEFICIARYCOUNTRY,BENEFICIARYADDR1,BENEFICIARYADDR2,BENEFICIARYADDR3," +
                "PRODUCTTYPE,PRODUCTOWNERNAME1,PRODUCTOWNERNAME2,PRODUCTOWNERNAME3,PRODUCTOWNERADDR1,PRODUCTOW" +
                "NERADDR2,PRODUCTOWNERADDR3,INCEPTIONDATE,MATURITYDATE,NARRATION,REMARKS,NATURE,FUNDSSOURCE,CE" +
                "RTIFIEDDOCUMENTS,INPUTDATE,TRANSACTIONCODE,PAYMENTMODE) VALUES ('TF11071204000822',{ts '2012" +
                "-11-07 00:00:00'},'NA','LCC','NA',' " +
                "',323333333.00,'OUTGOING','909','TFSSA038030','PHP',null,0.00,null,null,'CBIN0280001',null,nu" +
                "ll,'ex \n" +
                "add',null,null,null,'destination',null,null,null,null,null,null,null,null,null,null,'CBIN0280" +
                "001',null,null,null,'ex add',null,null,'TFSS1',null,null,null,null,null,null,{ts '2012-11-07 " +
                "00:00:00'},{ts '2012-11-15 00:00:00'},null,null,null,null,null,{ts '2012-11-07 " +
                "16:22:05'},'LCOPN','CASH')");
//        assertEquals(2,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM TRANSACTIONLOG"));
        //give time for the poller to write the report
        Thread.sleep(6000);
//        assertTrue(outputFile.exists());
        List<String> fileContents = getFileContents();
        assertNotNull(fileContents);
//        assertEquals(2,fileContents.size());
//		String fileContents = "7777,2012-08-28 00:00:00.000000000" + '\n' +
//							  "8888,2012-08-28 00:00:00.000000000";
    }
	
	private List<String> getFileContents(){
        List<String> fileContents = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(outputFile));
			String line = reader.readLine();
			while(line != null){
			    fileContents.add(line);
				line = reader.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			IOUtils.closeQuietly(reader);
		}
		return fileContents;
	}
	
}
