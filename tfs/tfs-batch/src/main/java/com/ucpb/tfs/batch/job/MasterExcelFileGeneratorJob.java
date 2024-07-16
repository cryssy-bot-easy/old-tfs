package com.ucpb.tfs.batch.job;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.math.BigDecimal;

import org.springframework.integration.MessageChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import com.ancientprogramming.fixedformat4j.format.FixedFormatManager;
import com.ancientprogramming.fixedformat4j.format.impl.FixedFormatManagerImpl;
import com.ucpb.tfs.batch.report.dw.MasterFileRecord;
import com.ucpb.tfs.batch.report.dw.service.MasterFileService;
import com.ucpb.tfs.batch.util.IOUtil;
import com.ucpb.tfs.batch.report.dw.TradeProduct;
import com.ucpb.tfs.batch.report.dw.dao.TradeProductDao;


/**	PROLOGUE:
  	SCR/ER Number: ER 20170802-008
	SCR/ER Description: Error encountered in executing Tfs adhoc “Master File” for August 2, 2017.
	[Revised by:] Jesse James Joson
	[Date revised:] 08/03/2017
	Program [Revision] Details: Add condition for conversion of document number from xxxxxxxxxxxxxxxx (16 characters) to xxx-xx-xxx-xx-xxxxx-x that it will only be done if the document number have 16 characters. 
	Member Type: JAVA
	Project: CORE
	Project Name: MasterExcelFileGeneratorJob.java
 */

public class MasterExcelFileGeneratorJob implements SpringJob {
	
	/*	PROLOGUE:
	 * 	(revision - additional function)
	  	SCR/ER Number: IBD-15-0828-01
		SCR/ER Description: Comparison of Balances in DW and SIBS-GL
		[Revised by:] Jesse James Joson
		[Date revised:] 09/17/2015
		Program [Revision] Details: Create table, and copy Master File record to new table created named: MASTERTABLE
    	INPUT: MASTERTABLE 
    	OUTPUT: Daily_Master_GL_Summary.xls & Daily_Master_GL_DailyBalance_Summary.xls
    	PROCESS: Get records from Master File (.csv format) and overwrite records in MASTERTABLE with the same DAY in Run Date
    		Methods added: deleteRowMasterTable, addRowMasterTable, docNumGenerator, getFormmatedDate, getAmountDividedBy10000
	 */
	
	private MasterFileService masterFileService;

    private static FixedFormatManager manager = new FixedFormatManagerImpl();

    private String filename;

    private MessageChannel channel;

    private String prefix ="TF_MASTER";

    private static final SimpleDateFormat EXECUTION_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

	@Autowired
    private TradeProductDao tradeProductDao;
        
    private TradeProduct tradeProduct;

    public MasterExcelFileGeneratorJob(MasterFileService masterFileService, String filename, MessageChannel channel) {
        this.masterFileService = masterFileService;
        this.filename = filename;
        this.channel = channel;
    }

    @Override
    public void execute() {

    }
    
	@Override
	public void execute(String appDate) throws Exception {
		//This will be used for deleting existing records in MASTERTABLE
		try {
	        System.out.println("<<<<<Deleting From MASTERTABLE>>>>>");
	        deleteRowMasterTable(appDate.substring(8, 10));
	    } catch (Exception e) {
			// TODO: handle exception
	    	System.out.println("<<<<<<Error in Deleting from MasterTable>>>>>>>>>");
	    	e.printStackTrace();
		}
		
		System.out.println("MasterFileReportGeneratorJob executeToExcel:"+ appDate);
	       PrintWriter writer = null;
	        File tempFile = null;

	        try {
	            String newAppDate = appDate.replace("-00.00.00","");
	            
	            
	            filename = prefix + "_" + getDate(newAppDate) + ".csv";
	            System.out.println("filename:"+filename);

	            tempFile = new File(filename);
	            writer = new PrintWriter(new FileWriter(filename));
	            writer.println(exportToExcelHeader());
	            List<MasterFileRecord> records = masterFileService.getMasterFiles(appDate);
	            System.out.println(">>>>>Copying Master File Records<<<<<");
				for(MasterFileRecord record : records){
					//This will be the portion where records will be insert in MASTERTABLE
	                addRowMasterTable(record, appDate.substring(8, 10));
	                
					writer.print(record.exportToExcel().trim());

	                // Force to write CR LF even in Linux
	                StringBuilder sb = new StringBuilder();
	                sb.append((char)13);
	                sb.append((char)10);
	                String crlf = sb.toString();

	                writer.print(crlf);
	            }
	            
	        }catch (IOException e) {
	            e.printStackTrace();
	            throw new RuntimeException("Error writing file " + filename,e);
	        }finally{
	            IOUtil.closeQuietly(writer);
	            writer.close();
	            writer.flush();
	        }

	        System.out.println("tempfile:"+tempFile);
	        if(tempFile != null){
	            sendToChannel(tempFile);
	        }
		
	}
    
    public void executeToExcel(String appDate) {
        

    }
    
    public String exportToExcelHeader(){
    	String COMMA = ",";
    	StringBuilder str = new StringBuilder("");
    	str.append("Application Account ID" + COMMA);
    	str.append("Facility ID" + COMMA);
    	str.append("Customer ID" + COMMA);
    	str.append("Account Status ID" + COMMA);
    	str.append("Branch ID" + COMMA);
    	str.append("Outstanding Book Code" + COMMA);
    	str.append("Entity ID" + COMMA);
    	str.append("Outstanding Currency ID" + COMMA);
    	str.append("Product ID" + COMMA);
    	str.append("Open Date" + COMMA);
    	str.append("Last Negotiation Date" + COMMA);
    	str.append("Closed Date" + COMMA);
    	str.append("Maturity Date" + COMMA);
    	str.append("Last Amendment Date" + COMMA);
    	str.append("Last Reinstatement Date" + COMMA);
    	str.append("Outstanding Contingent Assets (Lcl)" + COMMA);
    	str.append("Outstanding Contingent Assets (Fcy)/CONT_ASSETS_FCY" + COMMA);
    	str.append("Outstanding Contingent Liabilities (Lcl)" + COMMA);
    	str.append("Outstanding Contingent Liabilities (Fcy)/ CONT_LIAB_FCY" + COMMA);
    	str.append("GL No of Contingent Assets" + COMMA);
    	str.append("GL No of Contingent Liabilities" + COMMA);
    	
    	return str.toString();
    }

    public void executeMasterExceptionReport(String appDate) {
        System.out.println("MasterFileReportGeneratorJob executeMasterExceptionReport:"+ appDate);
        PrintWriter writer = null;
        File tempFile = null;

        try {
            String newAppDate = appDate.replace("-00.00.00","");

            filename = "TFS_" + getDate(newAppDate) + "_Master_Exception_Report.csv";
            System.out.println("filename:"+filename);

            tempFile = new File(filename);
            writer = new PrintWriter(new FileWriter(filename));
            writer.println(exceptionHeader());
            List<MasterFileRecord> records = masterFileService.getMasterFilesException(appDate);
            for(MasterFileRecord record : records){
                writer.print(record.exportToExcelException().trim());

                // Force to write CR LF even in Linux
                StringBuilder sb = new StringBuilder();
                sb.append((char)13);
                sb.append((char)10);
                String crlf = sb.toString();

                writer.print(crlf);
            }
            writer.println("");
            writer.println("");
            writer.println("LEGEND:");
            writer.println("\"" +  "B1 - OFFICER CODE IS BRANCH, BUT NOT FOUND IN JHPARL" + "\"");
            writer.println("\"" +  "B2 - OFFICER CODE IS BRANCH, BUT NO BOOKING UNIT CODE" + "\"");
            writer.println("\"" +  "A1 - OFFICER CODE IS BRANCH, BUT NO ALLOCATION UNIT CODE FOUND IN JHPARL" + "\"");
            writer.println("\"" +  "A2 - OFFICER CODE IS BRANCH, BUT NO ALLOCATION UNIT CODE DUE TO NO BOOKING CODE" + "\"");
            writer.println("\"" +  "A3 - OFFICER CODE IS RM, BUT NO ALLOCATION UNIT CODE FOUND IN LNPAN4 AND JHOFFR" + "\"");
            writer.println("\"" +  "A4 - OFFICER CODE IS RM, BUT NO ALLOCATION UNIT CODE FOUND IN LNPAN4 AND JHPARL" + "\"");
            writer.println("\"" +  "A5 - OFFICER CODE IS RM, BUT NO ALLOCATION UNIT CODE FOUND IN LNPAN4 AND WRONG ALLOCATION UNIT CODE FOUND IN JHPARL" + "\"");
            writer.println("\"" +  "A6 - OFFICER CODE IS RM, BUT NO ALLOCATION UNIT CODE DUE TO NO BOOKING CODE" + "\"");
            writer.println("\"" +  "C1 - CIF NUMBER NOT FOUND IN CFMAST");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error writing file " + filename,e);
        } finally{
            IOUtil.closeQuietly(writer);
        }

        System.out.println("tempfile:"+tempFile);
        if(tempFile != null){
            sendToChannel(tempFile);
        }

    }   
    
    public String exceptionHeader(){
    	String COMMA = ",";
    	StringBuilder str = new StringBuilder("");
    	str.append("Exception Code" + COMMA);
    	str.append("Officer Code" + COMMA);
    	str.append("Application Account ID" + COMMA);
    	str.append("Facility ID" + COMMA);
    	str.append("Customer ID" + COMMA);
    	str.append("Account Status ID" + COMMA);
    	str.append("Branch ID" + COMMA);
    	str.append("Outstanding Book Code" + COMMA);
    	str.append("Entity ID" + COMMA);
    	str.append("Outstanding Currency ID" + COMMA);
    	str.append("Product ID" + COMMA);
    	str.append("Open Date" + COMMA);
    	str.append("Last Negotiation Date" + COMMA);
    	str.append("Closed Date" + COMMA);
    	str.append("Maturity Date" + COMMA);
    	str.append("Last Amendment Date" + COMMA);
    	str.append("Last Reinstatement Date" + COMMA);
    	str.append("Outstanding Contingent Assets (Lcl)" + COMMA);
    	str.append("Outstanding Contingent Assets (Fcy)/CONT_ASSETS_FCY" + COMMA);
    	str.append("Outstanding Contingent Liabilities (Lcl)" + COMMA);
    	str.append("Outstanding Contingent Liabilities (Fcy)/ CONT_LIAB_FCY" + COMMA);
    	str.append("GL No of Contingent Assets" + COMMA);
    	str.append("GL No of Contingent Liabilities" + COMMA);
    	   	
    	return str.toString();
    }
    
    public String getDate(String date){
    	SimpleDateFormat stringToDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateToString = new SimpleDateFormat("MMdd");
        
        if(date == null){
        	date = "0";
        } else {
        	try {
        		date = dateToString.format(stringToDate.parse(date));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				date = "0";
			}
        }
        
		return date;
    }
    
    public void setMasterFileService(MasterFileService masterFileService) {
        this.masterFileService = masterFileService;
    }

    private void sendToChannel(File file) {
        MessageBuilder<File> builder = MessageBuilder.withPayload(file);
        channel.send(builder.build());
    }
	
    
    //Method that will delete in MASTERTABLE
	public void deleteRowMasterTable(String appDate) throws SQLException {
    	tradeProductDao.deleteMasterData(appDate);
    }
	
	//Method that will get each data that will be insert in MASTERTABLE
	public void addRowMasterTable(MasterFileRecord record, String appDate) throws SQLException {
    	String day = appDate;
    	String applicationAccountId = docNumGenerator(record.getApplicationAccountId());
    	String facilityId = record.getFacilityId();
    	String customerId = record.getCustomerId();
    	String accountStatusId  = record.getAccountStatusId();
    	String branchId = record.getBranchId();
    	String entityId = record.getEntityId();
    	String oBookCode = record.getOutstandingBookCode();
    	String oCurrencyId = record.getOutstandingCurrencyId();
    	String productId = record.getProductId();
    	
    	String oDate = getFormmatedDate(record.getOpenDate());
    	String nDate = getFormmatedDate(record.getNegotiationDate());
    	String cDate = getFormmatedDate(record.getClosedDate());
    	String mDate = getFormmatedDate(record.getMaturityDate());
    	String lADate = getFormmatedDate(record.getLastAmendmentDate());
    	String lRDate = getFormmatedDate(record.getLastReinstatementDate());
    	String zero = "0";
    	BigDecimal oCAssets = getAmountDividedBy10000(record.getOutstandingContingentAssets());
    	
    	BigDecimal oCLiabilities = getAmountDividedBy10000(record.getOutstandingContingentLiabilities());
    	String cAssetsGlNumber = record.getContingentAssetsGlNumber();
    	String cLiabilitiesGlNumber = record.getContingentLiabilitiesGlNumber();
    	
    	System.out.println(applicationAccountId);
    	tradeProductDao.insertMasterData(day, applicationAccountId, facilityId, customerId, accountStatusId, branchId, entityId, oBookCode, oCurrencyId, productId, oDate, nDate, cDate, mDate, lADate, lRDate, zero, oCAssets, oCLiabilities, cAssetsGlNumber, cLiabilitiesGlNumber);
    }
    
	//Method that will be used to get the real value of the outstanding balance
    public BigDecimal getAmountDividedBy10000(BigDecimal amount) {

        if(amount!=null || !amount.equals(BigDecimal.ZERO)){
            return amount.divide(new BigDecimal("10000"), 2, BigDecimal.ROUND_FLOOR);
        } else {
            return  BigDecimal.ZERO;
        }

    }
    
    //Method that will format the date same with the record in Master File
    public String getFormmatedDate(Date date) {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    	if(date == null){
            return "0";
        } else {
        	return sdf.format(date);
        }
    	
    }
    
    //Method to have dash(-) in document number; It will be useful to check with DAILYBALANCE table
    public String docNumGenerator(String docNum){
    	StringBuilder dn = new StringBuilder("");
    	//Add condition that the conversion of document number will only be done if the lenght = 16, otherwise do not convert
    	if (docNum.substring(0, 3).equals("909") && docNum.length()==16) {
			dn.append(docNum.substring(0, 3) + "-");
			dn.append(docNum.substring(3, 5) + "-");
			dn.append(docNum.substring(5, 8) + "-");
			dn.append(docNum.substring(8, 10) + "-");
			dn.append(docNum.substring(10, 15) + "-");
			dn.append(docNum.substring(15, 16));
		} else {
			dn.append(docNum);			
		}
    	
    	return dn.toString();
    }
    
}
