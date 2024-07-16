package com.ucpb.tfs.application.service;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.ucpb.tfs.batch.util.DbUtil;
import com.ucpb.tfs.batch.util.IOUtil;
import com.ucpb.tfs.domain.accounting.AccountingEntryActual;
import com.ucpb.tfs.domain.accounting.AccountingEntryActualRepository;
import com.ucpb.tfs.domain.audit.AccountLog;
import com.ucpb.tfs.domain.audit.Address;
import com.ucpb.tfs.domain.audit.Beneficiary;
import com.ucpb.tfs.domain.audit.Counterparty;
import com.ucpb.tfs.domain.audit.CustomerAccount;
import com.ucpb.tfs.domain.audit.TransactionLog;
import com.ucpb.tfs.domain.audit.infrastructure.repositories.AccountLogRepository;
import com.ucpb.tfs.domain.audit.infrastructure.repositories.CustomerAccountLogRepository;
import com.ucpb.tfs.domain.audit.infrastructure.repositories.TransactionLogRepository;
import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.product.TradeProductRepository;
import com.ucpb.tfs.domain.reference.GltsSequenceRepository;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import com.ucpb.tfs.domain.service.utils.ReferenceNumberGenerator;
import com.ucpb.tfs.interfaces.services.impl.RatesServiceImpl;

/*	PROLOGUE:
 	(revision)
	SCR/ER Number: 20151005-019
	SCR/ER Description: No Beneficiary Name or Counterparty Name in the TRN71 of DM56202013013467 on Sept 17
	[Revised by:] Lymuel Arrome Saul
	[Date revised:] 09/30/2015
	Program [Revision] Details: Add default condition which sets Beneficiary Name in BENEFICIARYNAME3 and Counterparty Name in CPNAME3
	 							if the IndividualCorporateTagging is null/blank, or the DocumentType is null/blank , or the details field
	 							is null/blank on TradeService Table
	Date deployment: 10/13/2015
	Member Type: JAVA
	Project: CORE
	Project Name: AmlaExpiredLcService.java
*/

public class AmlaExpiredLcService {
	
	   private GltsSequenceRepository gltsSequenceRepository;

	    private RatesServiceImpl ratesService;

	    private DataSource tfsDataSource;
	    
	    private TransactionLogRepository transLogRepository;
	    
	    private AccountLogRepository accountLogRepository;

		private CustomerAccountLogRepository customerAccountLogRepository;
	    
	    private AccountingEntryActualRepository accountingEntryActualRepository;
	    
	    private TradeProductRepository tradeProductRepository;
	    
	    private TradeServiceRepository tradeServiceRepository;
	    
		private String directory;



		public AmlaExpiredLcService(GltsSequenceRepository gltsSequenceRepository, DataSource tfsDataSource) {
	    
	        this.gltsSequenceRepository = gltsSequenceRepository;
	        this.tfsDataSource = tfsDataSource;
	    }
	    
	    
	   public void adhocExpiredAmla(Date reportDateFrom, Date reportDateTo){
		   
		   //FOR FUTURE PROGRAMMER PLEASE DO MORE VALIDATION FOR EACH RESULT STATMENT FOR NULL VALUES
		   
		 PrintWriter writer = null;
		 PrintWriter accWriter = null;
		 PrintWriter cacWriter = null;
		 PreparedStatement ps =null;
		 
		   StringBuilder sb = new StringBuilder();
		   sb.append((char)13);
		   sb.append((char)10);
		   String crlf = sb.toString();
		 
	    try{
	    	
	    	
	    	
	    	
	    	Calendar cal = Calendar.getInstance();    
	    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    	SimpleDateFormat textFormatDate = new SimpleDateFormat("MM/dd/yyyy");
	    	SimpleDateFormat toDate = new SimpleDateFormat("MMMM d, yyyy HH:mm:ss aa");
	    	System.out.println(toDate.format(new Date()));
	    	File file = new File(directory+"/"+"TRN71_"+"ExpiredLC"+".txt");
	    	File file2 = new File(directory+"/"+"ACC71_"+"ExpiredLC"+".txt");
	    	File file3 = new File(directory+"/"+"CAC71_"+"ExpiredLC"+".txt");
			file.createNewFile();
			file2.createNewFile();
			file3.createNewFile();
			writer = new PrintWriter(new FileWriter(file));
			accWriter = new PrintWriter(new FileWriter(file2));
			cacWriter= new PrintWriter(new FileWriter(file3));
	    	
	    	String documentNumber = "";
	    	String documentType = "";
	    	
	    	Date pDate = null;
	    	Date mDate = null;
	    	
	    	String TXNDATE = "";
	    	String TXNREFERENCENUMBER = "";
	    	String DEALNUMBER = "NA";
	    	String TRANSACTIONTYPECODE = "ILCC";
	    	String TRANSACTIONSUBTYPE = "NA";
	    	String TRANSACTIONMODE = "";
	    	String TRANSACTIONAMOUNT = "";
	    	String DEBIT_CREDIT_FLAG = "D";
	    	String DIRECTION = "O";
	    	String BRANCHCODE = "";
	    	String ACCOUNTNUMBER = "";
	    	String SETTLEMENTCURRENCY = "";
	    	BigDecimal exhangeRate = null;
	    	BigDecimal settleAmount = null;
	    	String PURPOSE = "";
	    	String CPACCOUNTNO = "";
	    	String CPNAME1 = "";
	    	String CPNAME2 = "";
	    	String CPNAME3 = "";
	    	String CPINSTITUTION = "";
	    	String CPINSTITUTIONCOUNTRY = ""; 
	    	String CP_ADDRESS1 = "";
	    	String CP_ADDRESS2 = "";
	    	String CP_ADDRESS3 = "";
	    	String CORRESPONDENTBANKNAME = "";
	    	String CORRESPONDENTCOUNTRYCODE = "";
	    	String CORRESPONDENTADDRESS1 = "";
	    	String CORRESPONDENTADDRESS2 = "";
	    	String CORRESPONDENTADDRESS3 = "";
	    	String INTRINSTITUTIONNAME = "";
	    	String INTRINSTITUTIONCOUNTRY = "";
	    	String INTRINSTITUTIONADDR1 = "";
	    	String INTRINSTITUTIONADDR2 = "";
	    	String INTRINSTITUTIONADDR3 = "";
	    	String BENEFICIARYNAME1 = "";
	    	String BENEFICIARYNAME2 = "";
	    	String BENEFICIARYNAME3 = "";
	    	String BENEFICIARYADDR1 = "";
	    	String BENEFICIARYADDR2 = "";
	    	String BENEFICIARYADDR3 = "";
	    	String BENEFICIARYCOUNTRY = "";
	    	String PRODUCTTYPE = "";
	    	String PRODUCTOWNERNAME1 = "";
	    	String PRODUCTOWNERNAME2 = "";
	    	String PRODUCTOWNERNAME3 = "";
	    	String PRODUCTOWNERADDR1 = "";
	    	String PRODUCTOWNERADDR2 = "";
	    	String PRODUCTOWNERADDR3 = "";
	    	String processDate = "";
	    	String maturityDate = "";
	    	String NARRATION = "";
	    	String REMARKS = "";
	    	String NATURE = "";
	    	String FUNDSSOURCE = "";
	    	String CERTIFIEDDOCUMENTS = "";
	    	String INPUTDATE = "";
	    	String REGULARDOCUMENTS = "";
	    	String TRANSACTIONCODE = "LCC";
	    	String PAYMENTMODE = "LCFRM";
	    	String AMOUNTTOCLAIM = "";
	    	String NOOFSHARES = "";
	    	String NETASSETVALUE = "";
	    	String ISSUERNAME1 = "";
	    	String ISSUERNAME2 = "";
	    	String ISSUERNAME3 = "";
	    	String ISSUERADDRESS1 = "";
	    	String ISSUERADDRESS2 = "";
	    	String ISSUERADDRESS3 = "";
	    	String BENEFICIARYACCOUNTNO = "";
	    	
	  
	    	String ACCOUNTTYPE = "400";
	    	String INITIALDEPOSIT = "";
	    	String ACCOUNTPURPOSE = "";
	    	String MONTHLYESTIMATEDTRANSACTIONCOUNT = "";
	    	String MONTHLYESTIMATEDTRANSACTIONVOLUME = "";
	    	String TRANSACTIONTYPES = "ILCC";
	    	String RISKSCORE = "";
	    	String ACCOUNTBALANCE = "0";
	    	String APPLICATIONCODE = "0";
	    	String STATUS = "C";
	    	String PAYROLLTAG = "0";
	    
	    	List l = accountingEntryActualRepository.getAllByDate(reportDateFrom, reportDateTo);
	    	
	    	System.out.println("LLIST SIZE: "+l.size());
	    	
	    	
	    	
		    	for(int x=0; x<l.size(); x++){
		    		//0 documentNumberStr
		    		//1 gltsNumber, 
		    		//2 originalAmount,
		    		//3 pesoAmount,
		    		//4 originalCurrency,
		    		//5 unitCode
		    		//6 effective date
		    		TransactionLog trans = new TransactionLog();
		    		AccountLog acount = new AccountLog();
		    		CustomerAccount customer = new CustomerAccount();
		    		Beneficiary ben = new Beneficiary();
		    		Counterparty counterP = new Counterparty();
		    		Address address = new Address();
		    		
		    		Object[] row = (Object[])l.get(x);  
		    		System.out.println(row[0]);
		    		System.out.println(row[1]);
		    		
		    		cal.setTime(sdf.parse(row[6].toString()));
					//cal.add(Calendar.DATE,1);
		    
		    		TXNREFERENCENUMBER = new ReferenceNumberGenerator().generate((row[1]).toString());
		    		ACCOUNTNUMBER = "TFSS"+row[0].toString();
		    		SETTLEMENTCURRENCY = row[4].toString();
		    		documentNumber = row[0].toString();
		    		BRANCHCODE = row[5].toString();
		    		INPUTDATE = textFormatDate.format(cal.getTime());
		    		TXNDATE = textFormatDate.format(cal.getTime());
		    		
		    		
		    		System.out.println("ERROR part1=");
		    		Map <String, String>map = tradeProductRepository.loadToMap(new DocumentNumber(documentNumber));
		    		System.out.println("ERROR part2=");
		    		//.get("individualCorporateFlag").toString();
		    		String individualCorporateFlaG = "";
		    		Map map2 = tradeServiceRepository.load(new DocumentNumber(documentNumber), ServiceType.OPENING).getDetails();
		    		
		    		if(map2!=null){
		    			if(map2.size()!=0){
		    				Object j = tradeServiceRepository.load(new DocumentNumber(documentNumber), ServiceType.OPENING).getDetails().get("individualCorporateFlag");
		    				if(j!=null){
		    					individualCorporateFlaG =tradeServiceRepository.load(new DocumentNumber(documentNumber), ServiceType.OPENING).getDetails().get("individualCorporateFlag").toString();
		    				}else{
		    					individualCorporateFlaG = "C";
		    				}
		    			}
		    		}else{
		    			individualCorporateFlaG = "C";
		    		}
		    
		    		System.out.println("ERROR part3=");
		    		
		    		String cifNumber = tradeServiceRepository.load(new DocumentNumber(documentNumber), ServiceType.OPENING).getCifNumber();
		    		System.out.println("ERROR part4=");
		    				  
		    		documentType = map.get("documentType");
		    		processDate = map.get("processDate");
		    		maturityDate = map.get("expiryDate");
		    		
		    		if(map.get("beneficiaryAddress")!=null){
		    			address.setAddress1(map.get("beneficiaryAddress"));
		    		}else{
		    			address.setAddress1("");
		    		}
		    		
		    	
		    		pDate = toDate.parse(map.get("processDate").toString());
		    		mDate = toDate.parse(map.get("expiryDate").toString());
		    		
		    		processDate = textFormatDate.format(pDate);
		    		maturityDate = textFormatDate.format(mDate);
		    		TRANSACTIONAMOUNT =  new BigDecimal(row[3].toString()).toString();
		    		
		    		System.out.println(TXNREFERENCENUMBER);
		    		System.out.println(map);
		    		System.out.println(map.get("beneficiaryName"));
		    		System.out.println(documentType);
		    		System.out.println("processDate: "+processDate);
		    		System.out.println("expiryDate: "+maturityDate);
		    		System.out.println("individualCorporateFlaG: "+individualCorporateFlaG);
		    		System.out.println("cifNumber: "+cifNumber);
		    	
					if(SETTLEMENTCURRENCY.equalsIgnoreCase("PHP")){
						exhangeRate = new BigDecimal(0);
						settleAmount = new BigDecimal(0);
					}else if(SETTLEMENTCURRENCY.equalsIgnoreCase("USD")){
						exhangeRate = new BigDecimal(row[3].toString()).divide(new BigDecimal(row[2].toString()), 2);
						settleAmount = new BigDecimal(row[2].toString());
					}else{
						exhangeRate = new BigDecimal(row[3].toString()).divide(new BigDecimal(row[2].toString()), 2);
						settleAmount = new BigDecimal(row[2].toString());
					}
		    		
					//STTART... SETTER FOR BENEFIARY
					if(individualCorporateFlaG!=null){
						if(individualCorporateFlaG.equals("C")){
							if(documentType.equalsIgnoreCase("FOREIGN")){
								BENEFICIARYNAME3 = map.get("exporterName");
								BENEFICIARYADDR1 = address.getAddress1();
								CPNAME3 = map.get("exporterName");
								CP_ADDRESS1 = address.getAddress1();
								ben.setName3(map.get("exporterName"));
								ben.setAddress(address);
								counterP.setName3(map.get("exporterName"));
								counterP.setAddress(address);
								
							}else{
								BENEFICIARYNAME3 = map.get("beneficiaryName");
								BENEFICIARYADDR1 = address.getAddress1();
								CPNAME3 = map.get("beneficiaryName");
								CP_ADDRESS1 = address.getAddress1();
								ben.setName3(map.get("beneficiaryName"));
								ben.setAddress(address);
								counterP.setName3(map.get("beneficiaryName"));
								counterP.setAddress(address);
							}
							
						}else if(individualCorporateFlaG.equals("I")){
							if(documentType.equalsIgnoreCase("FOREIGN")){
								BENEFICIARYNAME1 = map.get("exporterName");
								BENEFICIARYADDR1 = address.getAddress1();
								CPNAME1 = map.get("exporterName");
								CP_ADDRESS1 = address.getAddress1();
								ben.setName1(map.get("exporterName"));
								ben.setAddress(address);
								counterP.setName1(map.get("exporterName"));
								counterP.setAddress(address);
							}else{
								BENEFICIARYNAME1 = map.get("beneficiaryName");
								BENEFICIARYADDR1 = address.getAddress1();
								CPNAME1 = map.get("beneficiaryName");
								CP_ADDRESS1 = address.getAddress1();
								ben.setName1(map.get("beneficiaryName"));
								ben.setAddress(address);
								counterP.setName1(map.get("beneficiaryName"));
								counterP.setAddress(address);
							}
						}else{
							if(map.get("exporterName") != null && !map.get("exporterName").isEmpty()){
								BENEFICIARYNAME3 = map.get("exporterName");
								BENEFICIARYADDR1 = address.getAddress1();
								CPNAME3 = map.get("exporterName");
								CP_ADDRESS1 = address.getAddress1();
								ben.setName3(map.get("exporterName"));
								ben.setAddress(address);
								counterP.setName3(map.get("exporterName"));
								counterP.setAddress(address);								
							}else{
								BENEFICIARYNAME3 = map.get("beneficiaryName");
								BENEFICIARYADDR1 = address.getAddress1();
								CPNAME3 = map.get("beneficiaryName");
								CP_ADDRESS1 = address.getAddress1();
								ben.setName3(map.get("beneficiaryName"));
								ben.setAddress(address);
								counterP.setName3(map.get("beneficiaryName"));
								counterP.setAddress(address);
							}
						}					
		    		}else{
		    			if(documentType.equalsIgnoreCase("FOREIGN")){
		    				BENEFICIARYNAME3 = map.get("exporterName");
		    				BENEFICIARYADDR1 = address.getAddress1();
		    				CP_ADDRESS1 = address.getAddress1();
		    				CPNAME3 = map.get("exporterName");
							ben.setName3(map.get("exporterName"));
							counterP.setName3(map.get("exporterName"));
						}else if(documentType.equalsIgnoreCase("DOMESTIC")){
							BENEFICIARYNAME3 = map.get("beneficiaryName");
							BENEFICIARYADDR1 = address.getAddress1();
							CPNAME3 = map.get("exporterName");
							CP_ADDRESS1 = address.getAddress1();
							ben.setName3(map.get("beneficiaryName"));
							counterP.setName3(map.get("beneficiaryName"));
						}else{
							if(map.get("exporterName") != null && !map.get("exporterName").isEmpty()){
								BENEFICIARYNAME3 = map.get("exporterName");
								BENEFICIARYADDR1 = address.getAddress1();
								CPNAME3 = map.get("exporterName");
								CP_ADDRESS1 = address.getAddress1();
								ben.setName3(map.get("exporterName"));
								ben.setAddress(address);
								counterP.setName3(map.get("exporterName"));
								counterP.setAddress(address);								
							}else{
								BENEFICIARYNAME3 = map.get("beneficiaryName");
								BENEFICIARYADDR1 = address.getAddress1();
								CPNAME3 = map.get("beneficiaryName");
								CP_ADDRESS1 = address.getAddress1();
								ben.setName3(map.get("beneficiaryName"));
								ben.setAddress(address);
								counterP.setName3(map.get("beneficiaryName"));
								counterP.setAddress(address);
							}
						}
		    		}
					//END FOR BENE
				
		    		trans.setTransactionReferenceNumber(TXNREFERENCENUMBER);
		    		trans.setTransactionDate(sdf.parse(row[6].toString()));
		    		trans.setDealNumber("NA");
		    		trans.setTransactionTypeCode("ILCC");
		    		trans.setTransactionSubtype("NA");
		    		trans.setBranchCode(BRANCHCODE);
		    		trans.setAccountNumber(ACCOUNTNUMBER);
		    		trans.setTransactionAmount(new BigDecimal(row[3].toString()));
		    		trans.setTransactionType(DEBIT_CREDIT_FLAG);
		    		trans.setDirection(DIRECTION);
		    		trans.setSettlementCurrency(Currency.getInstance(SETTLEMENTCURRENCY));
		    		trans.setExchangeRate(exhangeRate);
		    		trans.setSettlementAmount(settleAmount);
		    		trans.setProductType("TFSS1");
		    		trans.setTransactionCode("LCC");
		    		trans.setPaymentMode("LCFRM");
		    		trans.setBeneficiary(ben);
		    		trans.setCounterparty(counterP);
		    		trans.setInceptionDate(pDate);
		    	    trans.setMaturityDate(mDate);
		    		trans.setInputDate(new Date());
		    		trans.setBatchFlag(1);
		    		
		    		
		    		acount.setDateCreated(sdf.parse(row[6].toString()));
		    		acount.setAccountNumber(ACCOUNTNUMBER);
		    		acount.setAccountType("400");
		    		acount.setTransactionTypes("ILCC");
		    		acount.setBranchCode(row[5].toString());
		    		acount.setOpeningDate(pDate);
		    		acount.setAccountBalance(new BigDecimal(0));
		    		acount.setApplicationCode("0");
		    		acount.setStatus("C");
		    		acount.setClosingDate(sdf.parse(row[6].toString()));
		    		acount.setAccountCurrency(SETTLEMENTCURRENCY);
		    		acount.setPayRollTag("0");
		    		acount.setBatchFlag(1);
		    		//acount.set
		    		
		    		customer.setDateCreated(sdf.parse(row[6].toString()));
		    		customer.setAccountNumber(ACCOUNTNUMBER);
		    		customer.setCustomerNumber(cifNumber);
		    		customer.setBatchFlag(1);

		    		transLogRepository.persist(trans);
		    		accountLogRepository.persist(acount);
		    		customerAccountLogRepository.persist(customer);
		    		
		    		String appendString = TXNDATE+"|"+
		    				TXNREFERENCENUMBER+"|"+
		    				DEALNUMBER+"|"+
		    				TRANSACTIONTYPECODE+"|"+
		    				TRANSACTIONSUBTYPE+"|"+
		    				TRANSACTIONMODE+"|"+
		    				TRANSACTIONAMOUNT+"|"+
		    				DEBIT_CREDIT_FLAG+"|"+
		    				DIRECTION+"|"+
		    				BRANCHCODE+"|"+
		    				ACCOUNTNUMBER+"|"+
		    				SETTLEMENTCURRENCY+"|"+
		    				exhangeRate.toString()+"|"+
		    				settleAmount.toString()+"|"+
		    				PURPOSE+"|"+
		    				CPACCOUNTNO+"|"+
		    				CPNAME1+"|"+
		    				CPNAME2+"|"+
		    				CPNAME3+"|"+
		    				CPINSTITUTION+"|"+
		    				CPINSTITUTIONCOUNTRY+"|"+
		    				CP_ADDRESS1+"|"+
		    				CP_ADDRESS2+"|"+
		    				CP_ADDRESS3+"|"+
		    				CORRESPONDENTBANKNAME+"|"+
		    				CORRESPONDENTCOUNTRYCODE+"|"+
		    				CORRESPONDENTADDRESS1+"|"+
		    				CORRESPONDENTADDRESS2+"|"+
		    				CORRESPONDENTADDRESS3+"|"+
		    				INTRINSTITUTIONNAME+"|"+
		    				INTRINSTITUTIONCOUNTRY+"|"+
		    				INTRINSTITUTIONADDR1+"|"+
		    				INTRINSTITUTIONADDR2+"|"+
		    				INTRINSTITUTIONADDR3+"|"+
		    				BENEFICIARYNAME1+"|"+
		    				BENEFICIARYNAME2+"|"+
		    				BENEFICIARYNAME3+"|"+
		    				BENEFICIARYADDR1+"|"+
		    				BENEFICIARYADDR2+"|"+
		    				BENEFICIARYADDR3+"|"+
		    				BENEFICIARYCOUNTRY+"|"+
		    				PRODUCTTYPE+"|"+
		    				PRODUCTOWNERNAME1+"|"+
		    				PRODUCTOWNERNAME2+"|"+
		    				PRODUCTOWNERNAME3+"|"+
		    				PRODUCTOWNERADDR1+"|"+
		    				PRODUCTOWNERADDR2+"|"+
		    				PRODUCTOWNERADDR3+"|"+
		    		    	processDate+"|"+
		    		    	maturityDate+"|"+
		    				NARRATION+"|"+
		    				REMARKS+"|"+
		    				NATURE+"|"+
		    				FUNDSSOURCE+"|"+
		    				CERTIFIEDDOCUMENTS+"|"+
		    				INPUTDATE+"|"+
		    				REGULARDOCUMENTS+"|"+
		    				TRANSACTIONCODE+"|"+
		    				PAYMENTMODE+"|"+
		    				AMOUNTTOCLAIM+"|"+
		    				NOOFSHARES+"|"+
		    				NETASSETVALUE+"|"+
		    				ISSUERNAME1+"|"+
		    				ISSUERNAME2+"|"+
		    				ISSUERNAME3+"|"+
		    				ISSUERADDRESS1+"|"+
		    				ISSUERADDRESS2+"|"+
		    				ISSUERADDRESS3+"|"+
		    				BENEFICIARYACCOUNTNO;
		    				
					writer.append(appendString);
					writer.print(crlf);
					
					String accAppendString = ACCOUNTNUMBER+"|"+
							ACCOUNTTYPE+"|"+
							INITIALDEPOSIT+"|"+
							ACCOUNTPURPOSE+"|"+
							MONTHLYESTIMATEDTRANSACTIONCOUNT+"|"+
							MONTHLYESTIMATEDTRANSACTIONVOLUME+"|"+
							TRANSACTIONTYPES+"|"+
							BRANCHCODE+"|"+
							processDate+"|"+
							TXNDATE+"|"+
		    		    	RISKSCORE+"|"+
		    		    	ACCOUNTBALANCE+"|"+
		    		    	APPLICATIONCODE+"|"+
		    		    	STATUS+"|"+
		    		    	SETTLEMENTCURRENCY+"|"+
		    		    	PAYROLLTAG;
		    		    	
		    		   accWriter.append(accAppendString);
		    		   writer.print(crlf);
		    		   
		    		 String cacAppendString = cifNumber+"|"+
		    				 			ACCOUNTNUMBER;
		    				 
		    			cacWriter.append(cacAppendString);
		    			writer.print(crlf);	 
		    		
		    	}
	    	
	    	}catch(Exception e){
	    		System.out.println(e);
	    	}finally {
			 	writer.close();
			 	accWriter.close();
			 	cacWriter.close();
	            IOUtil.closeQuietly(writer);

	        }
	    	
	    }
		
		public void deleteTransLogBatchFlag(){
		   transLogRepository.deleteBatchFlag(1);
		 
	   }
	   public void deleteAccountLogBatchFlag(){
		   accountLogRepository.deleteBatchFlag(1);
	   }
	   
	   public void deleteCustomerAccBatchFlag(){
		   customerAccountLogRepository.deleteBatchFlag(1);
	   }

	   
	   
	    public void setGltsSequenceRepository(GltsSequenceRepository gltsSequenceRepository) {
	        this.gltsSequenceRepository = gltsSequenceRepository;
	    }

	    public void setRatesService(RatesServiceImpl ratesService) {
	        this.ratesService = ratesService;
	    }

	    public void setTfsDataSource(DataSource tfsDataSource) {
	        this.tfsDataSource = tfsDataSource;
	    }
	    
	    public void setTransLogRepository(TransactionLogRepository transLogRepository) {
	  		this.transLogRepository = transLogRepository;
	  	}
	    
	    public void setAccountingEntryActualRepository(AccountingEntryActualRepository accountingEntryActualRepository) {
			this.accountingEntryActualRepository = accountingEntryActualRepository;
		}
	    public void setAccountLogRepository(AccountLogRepository accountLogRepository) {
			this.accountLogRepository = accountLogRepository;
		}
	    public void setCustomerAccountLogRepository(CustomerAccountLogRepository customerAccountLogRepository) {
			this.customerAccountLogRepository = customerAccountLogRepository;
		}
	    public void setDirectory(String directory) {
			this.directory = directory;
		}
		public void setTradeProductRepository(
				TradeProductRepository tradeProductRepository) {
			this.tradeProductRepository = tradeProductRepository;
		}
		public void setTradeServiceRepository(
					TradeServiceRepository tradeServiceRepository) {
				this.tradeServiceRepository = tradeServiceRepository;
		}
}
