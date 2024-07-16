package com.ucpb.tfs.batch.job;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.ucpb.tfs.batch.report.dw.CicRecord;
import com.ucpb.tfs.batch.report.dw.dao.SilverlakeLocalDao;
import com.ucpb.tfs.batch.report.dw.dao.TradeProductDao;


/** PROLOGUE:
 * 	(New)
	SCR/ER Number:  SCR IBD-16-0219-01
	SCR/ER Description: Generate CIC File
	[Created by:] Jesse James Joson
	[Date Deployed:]  02/24/2016
	Program [New] Details: Add this class for execution of CIC file
	PROJECT: CORE
	MEMBER TYPE  : JAVA
	Project Name: CicExtractionJob
 */
 
 /** PROLOGUE:
 * 	(Revision)
	SCR/ER Number:  SCR IBD-16-0219-01
	SCR/ER Description: Generate CIC File
	[Created by:] Jesse James Joson
	[Date Deployed:]  03/30/2016
	Program [New] Details: Modify this class for execution of CIC file for monthly run; Also fix credit limit for Historical file
	PROJECT: CORE
	MEMBER TYPE  : JAVA
	Project Name: CicExtractionJob
 */
 
/** PROLOGUE:
* 	(Revision)
	SCR/ER Number:  SCR IBD-16-0219-01
	SCR/ER Description: Add folders directory and backup file of CIC monthly report.
	[Created by:] Jesse James Joson
	[Date Deployed:]  08/26/2016
	Program [Revision] Details: Parameterized the directories, and add a backup file for CIC monthly report.
	PROJECT: CORE
	MEMBER TYPE  : JAVA
	Project Name: CicExtractionJob
*/

/** PROLOGUE:
* 	(Revision)
	SCR/ER Number:  ER 20161013-050
	SCR/ER Description: To address exception on Closed accounts without closeDate, and for BGs to remove the main CIF and the facility details.
	[Created by:] Jesse James Joson
	[Date Deployed:]  10/13/2016
	Program [Revision] Details: If account is closed without closeDate use the last nego Date instead. 
	PROJECT: CORE
	MEMBER TYPE  : JAVA
	Project Name: CicExtractionJob
*/

public class CicExtractionJob {

    @Autowired
    private SilverlakeLocalDao silverlakeLocalDao;
    
    @Autowired
    private TradeProductDao tradeProductDao;
    
	private String directory;
	
	private String directoryBackup;
	
	private String filename;
	
	private String fileFormat;
	
	public CicExtractionJob(String directory,String directoryBackup,String filename,String fileFormat) {
		this.directory = directory;
		this.directoryBackup = directoryBackup;
		this.filename = filename;
		this.fileFormat = fileFormat;
	}
        
	public void execute(String appDate,String systemDate, Date runDate) throws IOException, ParseException {
		
		File file = new File("/opt/tfs/INTERFACE_FILES/CIC_Historical.txt");
		file.createNewFile();
		PrintWriter writer = null;
		writer = new PrintWriter(new FileWriter(file));
		
		StringBuilder sb = new StringBuilder();		
		sb.append((char)13);				
		sb.append((char)10);				
		String crlf = sb.toString();
		
		Boolean isAdhoc=false;
		if(!appDate.equalsIgnoreCase(systemDate))  {
			System.out.println("This is run by Adhoc.");
			isAdhoc=true;
		} else {
			System.out.println("This is run by Batch.");
			isAdhoc=false;
		}
		
		
		System.out.println("Getting records..");
		List<CicRecord> records = tradeProductDao.getCicAll(appDate);
		System.out.println("Evauluate records..");
		
		int ind = 1;
		for(CicRecord record : records) {
			
			long outstandingBalance;
			BigDecimal rate = getRate(record, isAdhoc, appDate, runDate);
			String toPrint;
			
			System.out.println("LC: " + record.getDocumentNumber() + " Transaction Type = " + record.getProductType() + " : " + record.getDocType() + " : " + record.getLcType() + " : " + record.getCashFlag());
			record.setReportDate(appDate.substring(8, 10).concat(appDate.substring(5,7)).concat(appDate.substring(0,4)));
			
			
			if (record.getProductType().equalsIgnoreCase("LC") && record.getDocType().equalsIgnoreCase("FOREIGN") && record.getLcType().equalsIgnoreCase("CASH")) {
				record.setTransactionType("CLF");
				if (tradeProductDao.getOpeningType(record.getDocumentNumber()).equalsIgnoreCase("REGULAR")) {
					record.setTransactionType("ULF");
					record.setAdjustDate(tradeProductDao.getAdjustmentDate(record.getDocumentNumber()));
					outstandingBalance = getOutsatndingBalance(record, runDate, rate);
					toPrint = putTogether(record,outstandingBalance);
					System.out.println(ind+".) " + toPrint);
					ind++;
					writer.append(toPrint);
					writer.print(crlf);
					writer.flush();
					record.setTransactionType("CLF");
					record.setOutstandingBalance(record.getCashAmount());
					record.setContractPhase("AC");
				}
			} else if (record.getProductType().equalsIgnoreCase("LC") && record.getDocType().equalsIgnoreCase("DOMESTIC") && record.getLcType().equalsIgnoreCase("CASH")) {
				record.setTransactionType("CLD");
				//Check if adjusted to full Cash
				if (tradeProductDao.getOpeningType(record.getDocumentNumber()).equalsIgnoreCase("REGULAR")) {
					record.setTransactionType("ULD");
					record.setAdjustDate(tradeProductDao.getAdjustmentDate(record.getDocumentNumber()));
					outstandingBalance = getOutsatndingBalance(record, runDate, rate);
					toPrint = putTogether(record,outstandingBalance);
					System.out.println(ind+".) " + toPrint);
					ind++;
					writer.append(toPrint);
					writer.print(crlf);
					writer.flush();
					record.setTransactionType("CLD");
					record.setOutstandingBalance(record.getCashAmount());
					record.setContractPhase("AC");
				}
			} else if (record.getProductType().equalsIgnoreCase("LC") && record.getDocType().equalsIgnoreCase("FOREIGN") && record.getLcType().equalsIgnoreCase("REGULAR") && record.getCashFlag()!=1) {
				record.setTransactionType("ULF");
			} else if (record.getProductType().equalsIgnoreCase("LC") && record.getDocType().equalsIgnoreCase("DOMESTIC") && record.getLcType().equalsIgnoreCase("REGULAR") && record.getCashFlag()!=1) {
				record.setTransactionType("ULD");
			} else if (record.getProductType().equalsIgnoreCase("LC") && record.getDocType().equalsIgnoreCase("FOREIGN") && record.getLcType().equalsIgnoreCase("STANDBY")) {
				record.setTransactionType("SLF");
			} else if (record.getProductType().equalsIgnoreCase("LC") && record.getDocType().equalsIgnoreCase("DOMESTIC") && record.getLcType().equalsIgnoreCase("STANDBY")) {
				record.setTransactionType("SLD");
			} else if (record.getProductType().equalsIgnoreCase("INDEMNITY") && record.getDocType().equalsIgnoreCase("BG")) {
				record.setTransactionType("SBF");
			} else if (record.getProductType().equalsIgnoreCase("LC") && record.getDocType().equalsIgnoreCase("FOREIGN") && record.getLcType().equalsIgnoreCase("REGULAR") && record.getCashFlag()==1) {
				record.setTransactionType("ULF");
				outstandingBalance = getOutsatndingBalance(record, runDate, rate);
				toPrint = putTogether(record,outstandingBalance);
				System.out.println(ind+".) " + toPrint);
				ind++;
				writer.append(toPrint);
				writer.print(crlf);
				writer.flush();
				record.setTransactionType("CLF");
				record.setOutstandingBalance(record.getCashAmount());
				record.setContractPhase("AC");
			}  else if (record.getProductType().equalsIgnoreCase("LC") && record.getDocType().equalsIgnoreCase("DOMESTIC") && record.getLcType().equalsIgnoreCase("REGULAR") && record.getCashFlag()==1) {
				record.setTransactionType("ULD");
				outstandingBalance = getOutsatndingBalance(record, runDate, rate);
				toPrint = putTogether(record,outstandingBalance);
				System.out.println(ind+".) " + toPrint);
				ind++;
				writer.append(toPrint);
				writer.print(crlf);
				writer.flush();
				record.setTransactionType("CLD");
				record.setOutstandingBalance(record.getCashAmount());
				record.setContractPhase("AC");
			}
			
			outstandingBalance = getOutsatndingBalance(record, runDate, rate);
			toPrint = putTogether(record,outstandingBalance);
			System.out.println(ind+".) " + toPrint);
			ind++;
			
			writer.append(toPrint);
			writer.print(crlf);
			writer.flush();
		}
		
		writer.flush();
		writer.close();
	}
	
	private String putTogether(CicRecord record,long osBalance) throws ParseException{
		StringBuilder str = new StringBuilder("");
		str.append(checlIfNull(record.getRecordType()) + "|");
		str.append("UB000350" + "|");  // Set the right CIC Code or Provider Code for UCPB; set as UB000350
		str.append(checlIfNull(record.getBranchCode()) + "|");
		str.append(checlIfNull(record.getReportDate()) + "|");
		str.append(checlIfNull(record.getCifNumber()) + "|");
		str.append("|");
		str.append(checlIfNull(record.getDocNumber()) + "|");
		str.append(checlIfNull(record.getContractType()) + "|");
		
		//Status of Partial Cash
		if (record.getContractPhase().equalsIgnoreCase("EXPIRED")) {
			str.append("CL" + "|");
		} else if (record.getContractPhase().equalsIgnoreCase("FULLCASH")) {
			str.append("CL" + "|");
		} else {
			str.append(checlIfNull(record.getContractPhase()) + "|");
		}
		
		str.append(checlIfNull(record.getContractStatus()) + "|");
		str.append(checlIfNull(record.getPesoCurrency()) + "|");
		str.append(checlIfNull(record.getOriginalCurrency()) + "|");
		str.append(checlIfNull(record.getStartDate()) + "|");
		str.append(checlIfNull(record.getRequestDate()) + "|");
		str.append(checlIfNull(record.getExpiryDate()) + "|");
		
		//Close Date of Partial Cash
		if (record.getContractPhase().equalsIgnoreCase("EXPIRED")) {
			str.append(checlIfNull(record.getExpiryDate()) + "|");
			record.setContractPhase("CL");
		} else if (record.getContractPhase().equalsIgnoreCase("FULLCASH")) {
			str.append(checlIfNull(record.formatDate(record.getAdjustDate())) + "|");
			record.setAdjustDate(null);
			record.setContractPhase("CL");		
		} else if (record.getContractPhase().equalsIgnoreCase("CL") && checlIfNull(record.getCloseDate()).equalsIgnoreCase("")) {  // ER 20161013-050
			str.append(checlIfNull(record.getLastPaymentDate()) + "|");
		} else {
			str.append(checlIfNull(record.getCloseDate()) + "|");
		}
		
		str.append(checlIfNull(record.getLastPaymentDate()) + "|");
		str.append(checlIfNull(record.getCreditCode()) + "|");
		str.append(checlIfNull(record.getResolutionFlag()) + "|");
		
		// ER 20161013-050 - Modify to set the value of MainCIF for Cash LCs and BGs as blank
		if (checlIfNull(record.getMainCifNumber()).equals("") && !(record.getTransactionType().equalsIgnoreCase("CLD") || record.getTransactionType().equalsIgnoreCase("CLF")
				|| record.getTransactionType().equalsIgnoreCase("SBF"))) {
			str.append(checlIfNull(record.getCifNumber()) + "|");
		} else {
			str.append(checlIfNull(record.getMainCifNumber()) + "|");
		}
		
		
		str.append(checlIfNull(record.getFacilityType()) + "|");
		str.append(checlIfNull(record.getFacilityId()) + "|");
		str.append(checlIfNull(record.getFacilityRefNumber()) + "|");
		str.append(record.getCreditLimit() + "|");
		str.append(checlIfNull(record.getTransactionType()) + "|");
		str.append(checlIfNull(record.getPurposeOfCredit()) + "|");
		
		if (record.getContractPhase().equalsIgnoreCase("CL")) {
			str.append("0");
		} else {
			str.append(osBalance);
		}
				
		//str.append("0|0||||0||||||||0||||||0||||||||0||||||0||||||||0||||||0||||||||0||||||0||||||||0||||||0||||||||0||||||||||||||||||||*");
		return str.toString();
		
	}
	
	private String checlIfNull(String str) {
		if(str==null) {
			str="";
		} else if (str=="null") {
			str="";
		} else if (str.equalsIgnoreCase("NULL")) {
			str="";
		}
		
		return str;
	}
	
	private long getOutsatndingBalance(CicRecord record, Date runDate,BigDecimal rate) throws ParseException {
		
		long osBalance;

		osBalance = (record.getOutstandingBalance().multiply(rate)).longValue();
		
		if (record.getTransactionType().equalsIgnoreCase("CLD") || record.getTransactionType().equalsIgnoreCase("CLF")) {
			System.out.println(rate + " * " + record.getCashAmount().subtract(record.getTotalNegotiatedCashAmount()));
			osBalance = (record.getCashAmount().subtract(record.getTotalNegotiatedCashAmount()).multiply(rate)).longValue();
		} else if (!(record.getTransactionType().equalsIgnoreCase("CLD") || record.getTransactionType().equalsIgnoreCase("CLF") || record.getTransactionType().equalsIgnoreCase("SBF"))) {
			System.out.println(rate + " * " + record.getOutstandingBalance().subtract(record.getCashAmount().subtract(record.getTotalNegotiatedCashAmount())));
			osBalance = (record.getOutstandingBalance().subtract(record.getCashAmount().subtract(record.getTotalNegotiatedCashAmount())).multiply(rate)).longValue();
		}
		
		if (record.getProductType().equalsIgnoreCase("LC")) {	
			
			if ((record.getTransactionType().equalsIgnoreCase("CLD") || record.getTransactionType().equalsIgnoreCase("CLF") || record.getTransactionType().equalsIgnoreCase("SBF")) 
					&& !record.getCloseDate().equals("")) {
				osBalance = 0;
				record.setContractPhase("CL");
			} else if ((record.getTransactionType().equalsIgnoreCase("CLD") || record.getTransactionType().equalsIgnoreCase("CLF") || record.getTransactionType().equalsIgnoreCase("SBF")) 
					&& record.getCashAmount().subtract(record.getTotalNegotiatedCashAmount()).compareTo(BigDecimal.ZERO)<=0) {
				osBalance = 0;
				record.setContractPhase("CL");
				record.setCloseDate(tradeProductDao.getCashNegoDate(record.getDocumentNumber()));
			} else if ((record.getTransactionType().equalsIgnoreCase("CLD") || record.getTransactionType().equalsIgnoreCase("CLF") || record.getTransactionType().equalsIgnoreCase("SBF")) 
					&& record.getCloseDate().equals("")) {
				//osBalance = 0;  Include now the outstanding balance for Cash LCs
				record.setContractPhase("AC");
			} else if (!(record.getTransactionType().equalsIgnoreCase("CLD") || record.getTransactionType().equalsIgnoreCase("CLF") || record.getTransactionType().equalsIgnoreCase("SBF")) 
					&& record.getCloseDate().equals("") && record.getExpDate().after(runDate)) {
				osBalance = (record.getOutstandingBalance().subtract(record.getCashAmount().subtract(record.getTotalNegotiatedCashAmount())).multiply(rate)).longValue();
				record.setContractPhase("AC");
			} else if(!(record.getTransactionType().equalsIgnoreCase("CLD") || record.getTransactionType().equalsIgnoreCase("CLF") || record.getTransactionType().equalsIgnoreCase("SBF")) 
					&& record.getCloseDate().equals("") && !record.getExpDate().after(runDate)) {
				osBalance = 0;
				record.setContractPhase("EXPIRED");
			} else if(!(record.getTransactionType().equalsIgnoreCase("CLD") || record.getTransactionType().equalsIgnoreCase("CLF") || record.getTransactionType().equalsIgnoreCase("SBF")) 
					&& !record.getCloseDate().equals("") && !record.getExpDate().after(runDate)) {
				osBalance = 0;
				record.setContractPhase("CL");
			}
				
			if (osBalance<=0 && !record.getContractPhase().equalsIgnoreCase("EXPIRED") && !(record.getTransactionType().equalsIgnoreCase("CLD") || record.getTransactionType().equalsIgnoreCase("CLF") || record.getTransactionType().equalsIgnoreCase("SBF"))) {
				osBalance = 0;
				record.setContractPhase("CL");
			}
			
			if (!(record.getTransactionType().equalsIgnoreCase("CLD") || record.getTransactionType().equalsIgnoreCase("CLF") || record.getTransactionType().equalsIgnoreCase("SBF")) 
					&& record.getCloseDate().equals("") && record.getExpDate().after(runDate) && record.getAdjustDate()!=null) {
				osBalance = 0;
				record.setContractPhase("FULLCASH");
			}
					
		}
		
		if (record.getProductType().equalsIgnoreCase("INDEMNITY")) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(record.getStartDate2());
			calendar.add(Calendar.YEAR, 1);
			record.setExpDate(calendar.getTime());
			record.setCreditLimit(osBalance); // Set the credit limit of BGs
			
			if(!record.getExpDate().after(runDate) && record.getCloseDate().equals("")) {
				record.setContractPhase("CL");
				record.setCloseDate(calendar.getTime());
				osBalance=0;
			} else if (!record.getCloseDate().equals("")) {
				record.setContractPhase("CL");
				osBalance=0;
			} else {
				record.setContractPhase("AC");
			}
			
			
			String bgNumber = checlIfNull(record.getFacilityRefNumber());
			
			System.out.print("\t   Pair LC: " + bgNumber + "<<");
			Map<String,String> bgMap = tradeProductDao.getFacilityInfo(bgNumber);
			System.out.println("bgMap: " + bgMap);
			
			if (bgMap!=null) {
				record.setMainCifNumber(checlIfNull(bgMap.get("MAINCIFNUMBER")));
				record.setFacilityId(checlIfNull(bgMap.get("FACILITYID")));
				record.setFacilityType(checlIfNull(bgMap.get("FACILITYTYPE")));
				record.setFacilityRefNumber(checlIfNull(bgMap.get("FACILITYREFERENCENUMBER")));
			} else {
				record.setFacilityRefNumber("");
			}
			
			//osBalance=0;
			
		}
		
		if (checlIfNull(record.getFacilityType()).equalsIgnoreCase("") && !checlIfNull(record.getFacilityRefNumber()).equalsIgnoreCase("") && record.getProductType().equalsIgnoreCase("LC")) {
			record.setFacilityType(record.getFacilityRefNumber().substring(4, 7));
		} else if (checlIfNull(record.getFacilityType()).equalsIgnoreCase("") && !checlIfNull(record.getFacilityRefNumber()).equalsIgnoreCase("") && record.getProductType().equalsIgnoreCase("INDEMNITY")) {
			record.setFacilityType(record.getFacilityRefNumber().substring(4, 7));
		}
		
		// Modify to set the value of MainCIF, FacilityType, FacilityId and FacilityReferenceNumber for Cash LC as blank
		if (record.getTransactionType().equalsIgnoreCase("CLD") || record.getTransactionType().equalsIgnoreCase("CLF")) {
			record.setMainCifNumber("");
			record.setFacilityType("");
			record.setFacilityId("");
			record.setFacilityRefNumber("");
			record.setCreditLimit(record.getCashAmount().abs().multiply(rate).longValue()); // Set the credit limit of Cash LCs
		}
		
		// ER 20161013-050 - Modify to set the value of MainCIF, FacilityType, FacilityId and FacilityReferenceNumber for BGs as blank
		if (record.getTransactionType().equalsIgnoreCase("SBF")) {
			record.setMainCifNumber("");
			record.setFacilityType("");
			record.setFacilityId("");
			record.setFacilityRefNumber("");
		}
		
		return osBalance;
		
	}
	
	
	private BigDecimal getRate(CicRecord record, Boolean isAdhoc,String appDate, Date runDate) {
		BigDecimal rate=BigDecimal.ONE;
		
		String sourceCurrency = record.getOriginalCurrency();
		String tempDate = appDate.substring(5, 7).concat(appDate.substring(8, 10)).concat(appDate.substring(2, 4));
		int date = Integer.parseInt(tempDate);
		try{
		if(isAdhoc && !sourceCurrency.equalsIgnoreCase("PHP")) {
			rate=silverlakeLocalDao.getHistoricalRevalRateForCic(date, sourceCurrency, "PHP", 18);
		} else if (!isAdhoc && !sourceCurrency.equalsIgnoreCase("PHP") ){
			rate=silverlakeLocalDao.getAngolConversionRate(sourceCurrency, "PHP", 18);
		} else {
			rate=BigDecimal.ONE;
		}
		}catch(Exception e){
    		 e.printStackTrace();
    	     throw new IllegalArgumentException("UNABLE TO CONNECT TO SIBS");
    		
            
    	}
		return rate;
		
	}
	
	private String getTransactiontype(String transactionType) {
		
		if (transactionType.equalsIgnoreCase("TF111") || transactionType.equalsIgnoreCase("TF112")) {
			transactionType="ULF";
		} else if (transactionType.equalsIgnoreCase("TF113")) {
			transactionType="CLF";
		} else if (transactionType.equalsIgnoreCase("TF114")) {
			transactionType="SBF";
		} else if (transactionType.equalsIgnoreCase("TF115") || transactionType.equalsIgnoreCase("TF116")) {
			transactionType="SLF";
		} else if (transactionType.equalsIgnoreCase("TF211") || transactionType.equalsIgnoreCase("TF212")) {
			transactionType="ULD";
		} else if (transactionType.equalsIgnoreCase("TF213") || transactionType.equalsIgnoreCase("TF214")) {
			transactionType="SLD";
		} else if (transactionType.equalsIgnoreCase("TF217")) {
			transactionType="CLD";
		} 
		
		return transactionType;
		
	}
	
	private String getContractPhase(String status) {
		
		if (status.equalsIgnoreCase("TF-A") || status.equalsIgnoreCase("TF-B")) {
			status="CL";
		} else if (status.equalsIgnoreCase("TF-C") || status.equalsIgnoreCase("TF-D")) {
			status="AC";
		}
				
		return status;
		
	}
	
	private Date generateDate(String date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
		Date genDate = null;
		
		if (!date.equalsIgnoreCase("0")) {
			date = date.substring(4, 6) + "-" + date.substring(6, 8) + "-" + date.substring(0, 4);
			genDate = sdf.parse(date);
		} 	
		
		return genDate;
		
	}
	
	public void executeMonthly(String appDate,String systemDate, Date runDate) throws IOException, ParseException {

		File file = new File(this.directory + this.filename + this.fileFormat);
		file.createNewFile();
		PrintWriter writer = null;
		writer = new PrintWriter(new FileWriter(file));
		
		StringBuilder sb = new StringBuilder();		
		sb.append((char)13);
		sb.append((char)10);
		String crlf = sb.toString();
		
		Boolean isAdhoc=false;
		if(!appDate.equalsIgnoreCase(systemDate))  {
			System.out.println("This is run by Adhoc.");
			isAdhoc=true;
		} else {
			System.out.println("This is run by Batch.");
			isAdhoc=false;
		}
		
		String day=appDate.substring(8, 10);
		
		System.out.println("Getting records.. " + day);
		List<CicRecord> records = tradeProductDao.getMonthlyCic(day);
		System.out.println("Evauluate records..");
		
		//Backup file
		File fileBackup = new File(this.directoryBackup + this.filename + "_" + appDate.substring(5, 7) + day + this.fileFormat);
		fileBackup.createNewFile();
		PrintWriter writerBackup = null;
		writerBackup = new PrintWriter(new FileWriter(fileBackup));
		
		int ind = 1;
		for(CicRecord record : records) {
			BigDecimal outstandingBalance = record.getOutstandingBalance().abs();
			BigDecimal rate = getRate(record, isAdhoc, appDate, runDate);
			long osBalance = outstandingBalance.multiply(rate).longValue();
			long cashAmount = record.getCashAmount().abs().multiply(rate).longValue();
			String toPrint;
			
			
			record.setTransactionType(getTransactiontype(record.getProductType()));
			record.setContractPhase(getContractPhase(record.getContractPhase()));
			record.setStartDate(generateDate(record.getStartDateStr()));
			record.setRequestDate(generateDate(record.getStartDateStr()));
			record.setCloseDate(generateDate(record.getCloseDateStr()));
			record.setLastPaymentDate(generateDate(record.getLastPaymentDateStr()));
			record.setReportDate(appDate.substring(8, 10).concat(appDate.substring(5,7)).concat(appDate.substring(0,4)));
			
			// This will set the  expiry date of BGs to IssueDate + 1 year
			if (!record.getProductType().equalsIgnoreCase("TF114")) {
				record.setDocNumber(record.getRefNumber());
				record.setExpiryDate(generateDate(record.getExpiryDateStr()));
			} else {
				record.setDocNumber(record.getDocumentNumber());
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(generateDate(record.getStartDateStr()));
				calendar.add(Calendar.YEAR, 1);
				record.setExpDate(calendar.getTime());
				record.setCreditLimit(osBalance);
				if (calendar.getTime().before(runDate)) {
					osBalance=0;
					record.setContractPhase("CL");
					record.setCloseDate(calendar.getTime());
				}
			}	
					
			if (record.getContractPhase().equalsIgnoreCase("CL")) {
				osBalance=0;
			} 
			
			// This will set the close date for the Cash Part of Partially adjusted to Cash; if the cash part is fully nego
			if (record.getContractPhase().equalsIgnoreCase("CL") && (record.getTransactionType().equalsIgnoreCase("CLD") || record.getTransactionType().equalsIgnoreCase("CLF"))) {
				record.setCloseDate(tradeProductDao.getCashNegoDate(record.getDocumentNumber()));
			}
			
			// Modify to set the value of MainCIF, FacilityType, FacilityId and FacilityReferenceNumber for Cash LC as blank
			if (record.getTransactionType().equalsIgnoreCase("CLD") || record.getTransactionType().equalsIgnoreCase("CLF")) {
				record.setMainCifNumber("");
				record.setFacilityType("");
				record.setFacilityId("");
				record.setFacilityRefNumber("");
				record.setCreditLimit(cashAmount); // Set the credit limit of Cash LCs
			}
			
			// ER 20161013-050 - Modify to set the value of MainCIF, FacilityType, FacilityId and FacilityReferenceNumber for BGs as blank 
			if (record.getTransactionType().equalsIgnoreCase("SBF")) {
				record.setMainCifNumber("");
				record.setFacilityType("");
				record.setFacilityId("");
				record.setFacilityRefNumber("");
			}
			
			toPrint = putTogether(record,osBalance);
			System.out.println(ind+".) " + toPrint);
			ind++;
			
			writer.append(toPrint);
			writer.print(crlf);
			writer.flush();
			
			writerBackup.append(toPrint);
			writerBackup.print(crlf);
			writerBackup.flush();
		}
		
		writer.flush();
		writer.close();

		writerBackup.flush();
		writerBackup.close();
		
		
	}
	
}
