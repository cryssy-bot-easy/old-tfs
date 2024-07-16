package com.ucpb.tfs.batch.report.dw.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ucpb.tfs.batch.report.dw.TradeProduct;

//TODO: write a teardown script for the mock databases for repository-test-context
//TODO: or figure out why the schemas still exist in memory when all tests are run
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:repository-test-context.xml")
public class TradeProductDaoTest {
	
	@Autowired
	private TradeProductDao tradeProductDao;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	

    @Test
    public void getAllActiveDocumentsAgainstAcceptance(){
        printRow(jdbcTemplate.queryForList("SELECT * FROM DOCUMENTAGAINSTACCEPTANCE"));
//        List<TradeProduct> das = tradeProductDao.getActiveDocumentsAgainstAcceptance();
//        assertNotNull(das);
//        assertEquals(1,das.size());

    }

    @Test
    public void getAllActiveDocumentAgainstPayment(){
//        printRow(jdbcTemplate.queryForList("SELECT * FROM DOCUMENTAGAINSTPAYMENT"));
//        List<TradeProduct> dps = tradeProductDao.getActiveDocumentsAgainstPayment();
//        assertNotNull(dps);
//        assertEquals(1, dps.size());
    }

    @Test
    public void getAllActiveDirectRemittances(){
//        printRow(jdbcTemplate.queryForList("SELECT * FROM DIRECTREMITTANCE"));
//        List<TradeProduct> drs = tradeProductDao.getActiveDirectRemittances();
//        assertNotNull(drs);
//        assertEquals(1,drs.size());
    }

    @Test
    public void getAllActiveOpenAccounts(){
//        printRow(jdbcTemplate.queryForList("SELECT * FROM OPENACCOUNT"));
//        List<TradeProduct> oa = tradeProductDao.getActiveOpenAccounts();
//        assertNotNull(oa);
//        assertEquals(1,oa.size());

    }

    private void printRow(List<Map<String,Object>> queryResult){
        for(Map<String,Object> row : queryResult){
            for(Map.Entry<String,Object> column : row.entrySet()){
                System.out.print(column.getKey() + ":" + column.getValue() + "   ");
            }
            System.out.println(" ");
        }
    }




}
