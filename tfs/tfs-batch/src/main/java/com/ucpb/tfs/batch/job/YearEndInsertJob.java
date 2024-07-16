package com.ucpb.tfs.batch.job;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.ucpb.tfs.batch.report.dw.TradeProduct;
import com.ucpb.tfs.batch.report.dw.dao.TradeProductDao;

/**
 * 	PROLOGUE:
	(New Class)
  	SCR/ER Number: ER# 
	SCR/ER Description: Error might occur on creating ets on January 2016 if the tables will not be updated.
	[Created by:] Jesse James Joson
	[Date created:] 09/17/2015
	Program [Revision] Details: Create table, and copy records from SIBS-GL to new table created named: GLBALANCE
 */


//Used for Sequence Number Parameter Update yearly Batch

public class YearEndInsertJob {

	@Autowired
	private TradeProductDao tradeProductDao;
	
//	private String[] product = { "01", "02", "03", "04", "05", "06", "07", "08","09", "10", "11", "12", "13", "14", "15",
//			"16", "17", "18","19", "20", "21", "22", "23", "24", "25", "26", "27", "28","AP", "AR" };
//
//	private String[] branch = { "102", "111", "125", "133", "134", "147", "151","155", "160", "165", "177", "179", "188", "196", "201", "202", "205", "211",
//			"217", "220", "221", "223", "224", "225", "226", "227", "228","239", "240", "241", "246", "247", "251", "302", "314", "316",
//			"317", "319", "402", "403", "404", "405", "406", "412", "413","417", "909", "910", "928", "929", "930", "931" };
	
	private List<TradeProduct> branch = new ArrayList<TradeProduct>(); 
	private List<TradeProduct> product  = new ArrayList<TradeProduct>();

	public void execute(String appDate) throws SQLException {
		appDate = appDate.substring(0, 4);
		int year = (Integer.parseInt(appDate) + 1);
		int num;
		
		product = tradeProductDao.getAllCdtProduct();
		branch = tradeProductDao.getAllCdtBranch();
		
		//Insert for DOC_NUM_SEQUENCE
		num = tradeProductDao.getMaxIdDocNum();
		insertDocNumSequence(num, year);
		
		//Insert for NEGOTIATION_NUM_SEQUENCE
		num = tradeProductDao.getMaxIdNego();
		insertNegotiationSequence(num, year);
		
		//Insert for IC_NUM_SEQUENCE
		num = tradeProductDao.getMaxIdIc();
		insertIcSequence(num, year);
		
		//Insert for INDEMNITY_NUM_SEQUENCE
		num = tradeProductDao.getMaxIdIndemnity();
		insertIndemnitySequence(num, year);
		
		//Insert for NON_LC_NUM_SEQUENCE
		num = tradeProductDao.getMaxIdNonLC();
		insertNonLcSequence(num, year);
		
		
	}
	
	
	private void insertDocNumSequence(int num, int year) {
		System.out.println("Insert DOC_NUM_SEQUENCE");
		System.out.println("ID\t Prd't\t One\tBranch\t Year");

		for(TradeProduct tpProduct:product) { 
			for(TradeProduct tpBranch:branch) { 
				num++;
				System.out.println(num + "\t " + tpProduct.getProductId()  + "\t 1 \t" + tpBranch.getBranchId() + " \t " + year);
				tradeProductDao.insertDocSequence(num, tpProduct.getProductId(), 1, tpBranch.getBranchId(), year);
			}
        }
		
	}
	
	private void insertNegotiationSequence(int num, int year) {
		System.out.println("Insert NEGOTIATION_NUM_SEQUENCE");
		System.out.println("ID\t Prd't\t One\t Branch\t Year");

		for(TradeProduct tpProduct:product) {
			if(tpProduct.getProductId().equals("15") ||
					tpProduct.getProductId().equals("16") ||
					tpProduct.getProductId().equals("17") ||
					tpProduct.getProductId().equals("18") ||
					tpProduct.getProductId().equals("19") ||
					tpProduct.getProductId().equals("20") ){
				num++;
				System.out.println(num + "\t " +  tpProduct.getProductId() + "\t 1 \t 909 \t " + year);
				tradeProductDao.insertNegotiationSequence(num,  tpProduct.getProductId(), 1, "909", year);
			}
		}
		
	}
	
	private void insertIcSequence(int num, int year) {
		System.out.println("Insert IC_NUM_SEQUENCE");
		System.out.println("ID\t Prd't\t One\t Branch\t Year");
		num++;
		System.out.println(num + "\t " + "15" + "\t 1 \t 909 \t " + year);
		tradeProductDao.insertIcSequence(num, "15", 1, "909", year);
		num++;
		System.out.println(num + "\t " + "18" + "\t 1 \t 909 \t " + year);
		tradeProductDao.insertIcSequence(num, "18", 1, "909", year);
		
	}
	
	private void insertIndemnitySequence(int num, int year) {
		System.out.println("Insert INDEMNITY_NUM_SEQUENCE");
		System.out.println("ID\t Prd't\t One\t Branch\t Year");
		num++;
		System.out.println(num + "\t " + "01" + "\t 1 \t 909 \t " + year);
		tradeProductDao.insertIndemnitySequence(num, "01", 1, "909", year);
		num++;
		System.out.println(num + "\t " + "02" + "\t 1 \t 909 \t " + year);
		tradeProductDao.insertIndemnitySequence(num, "02", 1, "909", year);
		
	}
	
	private void insertNonLcSequence(int num, int year) {
		System.out.println("Insert NON_LC_NUM_SEQUENCE");
		System.out.println("ID\t Prd't\t One\t Branch\t Year");
		num++;
		System.out.println(num + "\t " + "01" + "\t 1 \t 909 \t " + year);
		tradeProductDao.insertNonLcSequence(num, "01", 1, "909", year);
		num++;
		System.out.println(num + "\t " + "02" + "\t 1 \t 909 \t " + year);
		tradeProductDao.insertNonLcSequence(num, "02", 1, "909", year);
	}
	
}
