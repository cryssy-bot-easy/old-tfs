package com.ucpb.tfs.batch.job;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.ucpb.tfs.batch.report.dw.TradeProduct;
import com.ucpb.tfs.batch.report.dw.dao.SilverlakeLocalDao;
import com.ucpb.tfs.batch.report.dw.dao.TradeProductDao;

public class SibsExtractionJob {

	/*	PROLOGUE:
	 * 	(New Class)
	  	SCR/ER Number: IBD-15-0828-01
		SCR/ER Description: Comparison of Balances in DW and SIBS-GL
		[Created by:] Jesse James Joson
		[Date created:] 09/17/2015
		Program [Revision] Details: Create table, and copy records from SIBS-GL to new table created named: GLBALANCE
    	INPUT: GLBALANCE
    	OUTPUT: Daily_Master_GL_Summary.xls & Daily_Master_GL_DailyBalance_Summary.xls
    	PROCESS: Select records from SIBS-GL table GLMAST, copy those records in GLBALANCE table in TFS tables
     */
	 
	 
	 	/*	PROLOGUE:
	 * 	(New Class)
	  	SCR/ER Number: 20151012-043
		SCR/ER Description: Extract SIBS does not post Failed Status in UI
		[Created by:] Jesse James Joson
		[Date created:] 09/17/2015
		Program [Revision] Details: move out in try Catch and throw exception in the execute method.
		PROJECT: CORE
		MEMBER TYPE  : JAVA
		Project Name: SibsExtractionJob
     */
	
    @Autowired
    private SilverlakeLocalDao silverlakeLocalDao;
    
    @Autowired
    private TradeProductDao tradeProductDao;
        
    private TradeProduct tradeProduct;
    
    //Method that will trigger the execution of the class
    public void execute(String appDate) throws SQLException {
    		//First get the yesterday Date
    		//Then other functions will be called in this method below
	        getDateYesterday(appDate);
	    
    }
    
    //Method to get date yesterday; This method will call other methods to be done in this class
    public void getDateYesterday(String appDate) throws SQLException {
    	String newAppDate = appDate.substring(5, 7) + "/"+ appDate.substring(8, 10) + "/" + appDate.substring(0, 4);
			 
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy"); 
	    Date startDate = null;
	    
	    try {
	        startDate = df.parse(newAppDate);
	        String newDateString = df.format(startDate);
	        //System.out.println(newDateString);
	    } catch (ParseException e) {
	    	System.out.println(">>>>>Error Parsing Date<<<<<");
	        e.printStackTrace();
	    }
			
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(startDate);
	    cal.add(Calendar.DATE, -1);
	    Date dateBefore = cal.getTime();
	    
	    newAppDate = df.format(dateBefore);
	    
	    appDate= newAppDate.substring(3, 5);
    	
	    //call this method to start copying of GL balance
	    copyGLBalance(appDate);
	    
    }
    
    //Method that will copy GL balance from SIBS-GL to TFS Table
    public void copyGLBalance(String appDate) throws SQLException {
    	//delete first existing records to not duplicate records
    	deleteGLBalance(appDate);
    	
    	List<TradeProduct> records = null;

    	
    	try{
            System.out.println("getAngolConversionRate");
		records = silverlakeLocalDao.getGlBalance();
    	}catch(Exception e){
   		 e.printStackTrace();
   	     throw new IllegalArgumentException("UNABLE TO CONNECT TO SIBS");
           
   	}

	
	
		
		
		
		
		BigDecimal orgBalance = new BigDecimal("0"); 
		BigDecimal phpBalance = new BigDecimal("0"); 
		String day = appDate;
		String acctno;
		String gmctyp;
		System.out.println(">>>>>Copying Gl Balance<<<<<");

		for(TradeProduct record : records){
			acctno = record.getAcctno();
			gmctyp = record.getGmctyp();
			
			if (day.equalsIgnoreCase("01")) {
				orgBalance = record.getDayOrg01();
				phpBalance = record.getDayPhp01();
			} else if (day.equalsIgnoreCase("02")) {
				orgBalance = record.getDayOrg02();
				phpBalance = record.getDayPhp02();
			} else if (day.equalsIgnoreCase("03")) {
				orgBalance = record.getDayOrg03();
				phpBalance = record.getDayPhp03();
			} else if (day.equalsIgnoreCase("04")) {
				orgBalance = record.getDayOrg04();
				phpBalance = record.getDayPhp04();
			} else if (day.equalsIgnoreCase("05")) {
				orgBalance = record.getDayOrg05();
				phpBalance = record.getDayPhp05();
			} else if (day.equalsIgnoreCase("06")) {
				orgBalance = record.getDayOrg06();
				phpBalance = record.getDayPhp06();
			} else if (day.equalsIgnoreCase("07")) {
				orgBalance = record.getDayOrg07();
				phpBalance = record.getDayPhp07();
			} else if (day.equalsIgnoreCase("08")) {
				orgBalance = record.getDayOrg08();
				phpBalance = record.getDayPhp08();
			} else if (day.equalsIgnoreCase("09")) {
				orgBalance = record.getDayOrg09();
				phpBalance = record.getDayPhp09();
			} else if (day.equalsIgnoreCase("10")) {
				orgBalance = record.getDayOrg10();
				phpBalance = record.getDayPhp10();
			} else if (day.equalsIgnoreCase("11")) {
				orgBalance = record.getDayOrg11();
				phpBalance = record.getDayPhp11();
			} else if (day.equalsIgnoreCase("12")) {
				orgBalance = record.getDayOrg12();
				phpBalance = record.getDayPhp12();
			} else if (day.equalsIgnoreCase("13")) {
				orgBalance = record.getDayOrg13();
				phpBalance = record.getDayPhp13();
			} else if (day.equalsIgnoreCase("14")) {
				orgBalance = record.getDayOrg14();
				phpBalance = record.getDayPhp14();
			} else if (day.equalsIgnoreCase("15")) {
				orgBalance = record.getDayOrg15();
				phpBalance = record.getDayPhp15();
			} else if (day.equalsIgnoreCase("16")) {
				orgBalance = record.getDayOrg16();
				phpBalance = record.getDayPhp16();
			} else if (day.equalsIgnoreCase("17")) {
				orgBalance = record.getDayOrg17();
				phpBalance = record.getDayPhp17();
			} else if (day.equalsIgnoreCase("18")) {
				orgBalance = record.getDayOrg18();
				phpBalance = record.getDayPhp18();
			} else if (day.equalsIgnoreCase("19")) {
				orgBalance = record.getDayOrg19();
				phpBalance = record.getDayPhp19();
			} else if (day.equalsIgnoreCase("20")) {
				orgBalance = record.getDayOrg20();
				phpBalance = record.getDayPhp20();
			} else if (day.equalsIgnoreCase("21")) {
				orgBalance = record.getDayOrg21();
				phpBalance = record.getDayPhp21();
			} else if (day.equalsIgnoreCase("22")) {
				orgBalance = record.getDayOrg22();
				phpBalance = record.getDayPhp22();
			} else if (day.equalsIgnoreCase("23")) {
				orgBalance = record.getDayOrg23();
				phpBalance = record.getDayPhp23();
			} else if (day.equalsIgnoreCase("24")) {
				orgBalance = record.getDayOrg24();
				phpBalance = record.getDayPhp24();
			} else if (day.equalsIgnoreCase("25")) {
				orgBalance = record.getDayOrg25();
				phpBalance = record.getDayPhp25();
			} else if (day.equalsIgnoreCase("26")) {
				orgBalance = record.getDayOrg26();
				phpBalance = record.getDayPhp26();
			} else if (day.equalsIgnoreCase("27")) {
				orgBalance = record.getDayOrg27();
				phpBalance = record.getDayPhp27();
			} else if (day.equalsIgnoreCase("28")) {
				orgBalance = record.getDayOrg28();
				phpBalance = record.getDayPhp28();
			} else if (day.equalsIgnoreCase("29")) {
				orgBalance = record.getDayOrg29();
				phpBalance = record.getDayPhp29();
			} else if (day.equalsIgnoreCase("30")) {
				orgBalance = record.getDayOrg30();
				phpBalance = record.getDayPhp30();
			} else if (day.equalsIgnoreCase("31")) {
				orgBalance = record.getDayOrg31();
				phpBalance = record.getDayPhp31();
			} else {
				System.out.println(">>>>>>Day did not Capture<<<<<<<");
			} 																	
			
			System.out.println(day + "\t" + acctno + "\t" + gmctyp + "\t" + orgBalance + "\t" + phpBalance);
			
			//start inserting records
			tradeProductDao.insertGlBalance(day, acctno, gmctyp, orgBalance, phpBalance);
		}	
		

		    	
    }
        
    //Method that will delete existing records
    public void deleteGLBalance(String appDate) throws SQLException {
    	tradeProductDao.deleteGlBalance(appDate);
    	System.out.println("<<<<<Deleting from GLBALANCE>>>>>");
	}
	
	
	    public boolean checkConnection(){
    	

    	try{
    	System.out.println("Checking SIBS Connection");
    	String checkCon = silverlakeLocalDao.getAllocationUnitCodeForNumericCif("");
    	System.out.println("Connection Success" + checkCon);	
    	return true;
    	}catch(Exception e){
        System.out.println("Connection SIBS FAILED");	
    	System.out.println("This is the connection checking! no data provided");	
    	e.printStackTrace();
    	System.out.println("This is the connection checking! no data provided");
    	return false;
    	}
    }

}
