package com.ucpb.tfs.batch.job;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.integration.MessageChannel;
import org.springframework.integration.support.MessageBuilder;

import com.ucpb.tfs.batch.report.dw.AllocationFileRecord;
import com.ucpb.tfs.batch.report.dw.service.AllocationFileService;
import com.ucpb.tfs.batch.util.IOUtil;

public class AllocationExcelFileGeneratorJob implements SpringJob {

    public static final String DEFAULT_FILENAME_PROPERTY = "filename";
    private static final long serialVersionUID = -460101957440425582L;
    public static final String CONTRA_UNIT = "10909";

	private MessageChannel channel;

	private AllocationFileService allocationFileService;

    private Date targetDate;

    private static final BigDecimal NEGATIVE = new BigDecimal("-1");

    private String mainOfficeBranch = "30001";

    private String filenamePrefix;

    private String filenameProperty;

    private String profitLossAccountingCode = "561501030000";

    private String treasuryAllocationCode = "10903";


    public AllocationExcelFileGeneratorJob(MessageChannel channel, String filenamePrefix, AllocationFileService allocationFileService) {
		this.channel = channel;
        this.filenamePrefix = filenamePrefix;
        this.allocationFileService = allocationFileService;
	}
    
	public void execute() {
		executeToExcel(new Date(),profitLossAccountingCode, treasuryAllocationCode);
	}

    public void execute(String reportDate) {
        //TODO
    	executeToExcel(new Date(), profitLossAccountingCode, treasuryAllocationCode);
    }

    public void executeToExcel(Date targetDate, String fxProfitOrLossAccountingCode, String treasuryAllocationCode) {

        System.out.println("AllocationFileReportGeneratorJob executeToExcel:"+targetDate);
        PrintWriter writer = null;
        File tempFile = null;
        String actualFilename = "";
        
        try {
        	
            actualFilename = filenamePrefix + "_" + getDate(targetDate) + ".csv";
            System.out.println("actualFilename = "+actualFilename);
            tempFile = new File(actualFilename);

            writer = new PrintWriter(new FileWriter(actualFilename));

            List<AllocationFileRecord> allocationsIncome = allocationFileService.getProductAllocations(targetDate, fxProfitOrLossAccountingCode, treasuryAllocationCode);
            System.out.println("allocationsIncome.size() = " + allocationsIncome.size());

            List<AllocationFileRecord> allocationsAdb = allocationFileService.getProductAverageDailyBalanceRecords(targetDate);
            System.out.println("allocationsAdb.size())) = " + allocationsAdb.size());
            
            List<AllocationFileRecord> transactions = generateAndCombineWithContraRecordsIncome(allocationsIncome);
            transactions.addAll(generateAndCombineWithContraRecordsAdb(allocationsAdb));
            
            writer.println(exportToExcelHeader());
            for (AllocationFileRecord record : transactions) {

                writer.print(record.exportToExcel().trim());

                // Force to write CR LF even in Linux
                StringBuilder sb = new StringBuilder();
                sb.append((char)13);
                sb.append((char)10);
                String crlf = sb.toString();
                writer.print(crlf);

            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing file " + actualFilename, e);
        } finally {
            IOUtil.closeQuietly(writer);
        }

        sendToChannel(tempFile,actualFilename);
    }
    
    public String exportToExcelHeader(){
    	String COMMA = ",";
    	StringBuilder str = new StringBuilder("");
    	str.append("Processing Date" + COMMA);
    	str.append("Booking Date" + COMMA);
    	str.append("Application ID" + COMMA);
    	str.append("GL Account ID" + COMMA);
    	str.append("Book Code" + COMMA);
    	str.append("Allocations Unit" + COMMA);
    	str.append("Currency ID" + COMMA);
    	str.append("Application Account ID" + COMMA);
    	str.append("Customer ID" + COMMA);
    	str.append("Product ID" + COMMA);
    	str.append("Transaction Amount (PHP)" + COMMA);
    	str.append("Transaction Amount (ORG)" + COMMA);
    	str.append("Transaction Amount (USD)" + COMMA);
    	str.append("Transaction Type" + COMMA);
    	str.append("Last Repricing Date" + COMMA);
    	str.append("Next repricing Date" + COMMA);
    	str.append("Contract Term Day" + COMMA);
    	str.append("Contract Term Type" + COMMA);
    	str.append("Past Due Flag" + COMMA);
    	str.append("ADB Amount" + COMMA);
    	
    	return str.toString();
    }
    
    public void executeAllocationExceptionReport(Date targetDate, String fxProfitOrLossAccountingCode, String treasuryAllocationCode) {

        System.out.println("AllocationFileReportGeneratorJob executeAllocationExceptionReport:"+targetDate);
        PrintWriter writer = null;
        File tempFile = null;
        String actualFilename = "";
        
        try {

            actualFilename = "TFS_" + getDate(targetDate) + "_Allocation_Exception_Report.csv";
            System.out.println("actualFilename = "+actualFilename);
            tempFile = new File(actualFilename);

            writer = new PrintWriter(new FileWriter(actualFilename));

            List<AllocationFileRecord> allocationsIncome = allocationFileService.getProductAllocationsException(targetDate, fxProfitOrLossAccountingCode, treasuryAllocationCode);
            System.out.println("allocationsIncome.size() = " + allocationsIncome.size());

            List<AllocationFileRecord> allocationsAdb = allocationFileService.getProductAverageDailyBalanceRecordsException(targetDate);
            System.out.println("allocationsAdb.size())) = " + allocationsAdb.size());
            
            List<AllocationFileRecord> transactions = generateAndCombineWithContraRecordsIncome(allocationsIncome);
            transactions.addAll(generateAndCombineWithContraRecordsAdb(allocationsAdb));
            
            writer.println(exceptionHeader());
            for (AllocationFileRecord record : transactions) {

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
            throw new RuntimeException("Error writing file " + actualFilename, e);
        } finally {
            IOUtil.closeQuietly(writer);
        }

        sendToChannel(tempFile,actualFilename);
    }
    
    public String exceptionHeader(){
    	String COMMA = ",";
    	StringBuilder str = new StringBuilder("");
    	str.append("Exception Code" + COMMA);
    	str.append("Branch Unit Code" + COMMA);
    	str.append("Officer Code" + COMMA);
    	str.append("Processing Date" + COMMA);
    	str.append("Booking Date" + COMMA);
    	str.append("Application ID" + COMMA);
    	str.append("GL Account ID" + COMMA);
    	str.append("Book Code" + COMMA);
    	str.append("Allocations Unit" + COMMA);
    	str.append("Currency ID" + COMMA);
    	str.append("Application Account ID" + COMMA);
    	str.append("Customer ID" + COMMA);
    	str.append("Product ID" + COMMA);
    	str.append("Transaction Amount (PHP)" + COMMA);
    	str.append("Transaction Amount (ORG)" + COMMA);
    	str.append("Transaction Amount (USD)" + COMMA);
    	str.append("Transaction Type" + COMMA);
    	str.append("Last Repricing Date" + COMMA);
    	str.append("Next repricing Date" + COMMA);
    	str.append("Contract Term Day" + COMMA);
    	str.append("Contract Term Type" + COMMA);
    	str.append("Past Due Flag" + COMMA);
    	str.append("ADB Amount" + COMMA);
    	
    	return str.toString();
    }

    /**
     * This method is used to "format" the resulting output file.
     * The output list would be paired with its contra entry.
     *
     * @param allocations  List of Allocations
     * @return List of Allocations and their contra accounts
     */
    private List<AllocationFileRecord> generateAndCombineWithContraRecordsIncome(List<AllocationFileRecord> allocations) {

        List<AllocationFileRecord> transactions = new ArrayList<AllocationFileRecord>();

        for (AllocationFileRecord allocationFileRecord : allocations) {

            if(allocationFileRecord == null || allocationFileRecord.getAllocationUnit()==null || allocationFileRecord.getAllocationUnit().isEmpty()){
                allocationFileRecord.setAllocationUnit("-");
            }
            
            if(allocationFileRecord.getOfficerCode()==null){
                allocationFileRecord.setOfficerCode("-");
            }
            
            if(allocationFileRecord.getExceptionCode()==null){
                allocationFileRecord.setExceptionCode("-");
            }
        	
            allocationFileRecord.setAllocationUnit(allocationFileRecord.getAllocationUnit().trim());

            AllocationFileRecord contra = new AllocationFileRecord(allocationFileRecord);

            //contra.setAllocationUnit(getMainOfficeBranch());
            // contra.setOriginalTransactionAmount(NEGATIVE.multiply(allocationFileRecord.getOriginalTransactionAmountUnMultiplied()));
            // contra.setTotalAmount(NEGATIVE.multiply(allocationFileRecord.getTotalAmount()));
            if(allocationFileRecord.getGlEntryType().trim().equalsIgnoreCase("CREDIT")){
	            contra.setOriginalTransactionAmount(allocationFileRecord.getOriginalTransactionAmountUnMultiplied());
	
	            allocationFileRecord.setOriginalTransactionAmount(NEGATIVE.multiply(allocationFileRecord.getOriginalTransactionAmountUnMultiplied()));
	            allocationFileRecord.setTotalAmount(NEGATIVE.multiply(allocationFileRecord.getTotalAmount()));
            } else {
            	contra.setOriginalTransactionAmount(NEGATIVE.multiply(allocationFileRecord.getOriginalTransactionAmountUnMultiplied()));
            	contra.setTotalAmount(NEGATIVE.multiply(allocationFileRecord.getTotalAmount()));
            }

            if (allocationFileRecord.getAllocationUnit().trim().startsWith("30") && allocationFileRecord.getBranchUnitCode() != null) {
                //use 10 appended to branch unit code
                contra.setAllocationUnit("10"+allocationFileRecord.getBranchUnitCode().trim());
            } else {
                contra.setAllocationUnit(allocationFileRecord.getAllocationUnit());
            }

            contra.setOfficerCode(allocationFileRecord.getOfficerCode());
            contra.setExceptionCode(allocationFileRecord.getExceptionCode());
            
//            if(contra.getAllocationUnit().length()!=5){
//                contra.setAllocationUnit(CONTRA_UNIT);
//            }            
                       
            if( allocationFileRecord.getPesoAdbAmount() != null) {
            	contra.setPesoAdbAmount(NEGATIVE.multiply(allocationFileRecord.getPesoAdbAmount()));           	
            }

            transactions.add(allocationFileRecord);
            transactions.add(contra);
        }

        return transactions;
    }

    /**
     * This method is used to "format" the resulting output file.
     * The output list would be paired with its contra entry.
     *
     * @param allocations  List of Allocations
     * @return List of Allocations for ADB amounts and their contra accounts
     */
    private List<AllocationFileRecord> generateAndCombineWithContraRecordsAdb(List<AllocationFileRecord> allocations){
        List<AllocationFileRecord> transactions = new ArrayList<AllocationFileRecord>();
        for(AllocationFileRecord allocationFileRecord : allocations){
            System.out.println("allocationFileRecord:"+allocationFileRecord);
            if(allocationFileRecord!=null){
                if(allocationFileRecord.getProductId()!=null){
                    if(allocationFileRecord.getProductId().equalsIgnoreCase("TF217")||allocationFileRecord.getProductId().equalsIgnoreCase("TF113")){
                        BigDecimal cashToSwap =  allocationFileRecord.getOriginalTransactionAmount();
                        allocationFileRecord.setOriginalTransactionAmount(NEGATIVE.multiply(cashToSwap));
                    }
                }

                if(allocationFileRecord.getAllocationUnit()==null || allocationFileRecord.getAllocationUnit().isEmpty()){
                    allocationFileRecord.setAllocationUnit("-");
                }
                
                if(allocationFileRecord.getOfficerCode()==null){
                    allocationFileRecord.setOfficerCode("-");
                }
                
                if(allocationFileRecord.getExceptionCode()==null){
                    allocationFileRecord.setExceptionCode("-");
                }

                allocationFileRecord.setAllocationUnit(allocationFileRecord.getAllocationUnit().trim());

                AllocationFileRecord contra = new AllocationFileRecord(allocationFileRecord);
               
                contra.setOriginalTransactionAmount(NEGATIVE.multiply(allocationFileRecord.getOriginalTransactionAmountUnMultiplied()));
                
                //handling of Null values
                BigDecimal allocationFileRecordGetTotalAmount = allocationFileRecord.getTotalAmount() != null && 
                		allocationFileRecord.getTotalAmount().toString().isEmpty()? 
                				new BigDecimal(allocationFileRecord.getTotalAmount().toString()) : BigDecimal.ZERO;
                
                contra.setTotalAmount(NEGATIVE.multiply(allocationFileRecordGetTotalAmount));
                
                if(allocationFileRecord.getAllocationUnit().trim() == "-"){
                	contra.setAllocationUnit("-");
                } else {
                	contra.setAllocationUnit("10909");
                }
                
                contra.setOfficerCode(allocationFileRecord.getOfficerCode());
                contra.setExceptionCode(allocationFileRecord.getExceptionCode());

                if(allocationFileRecord.getPesoAdbAmount()!=null) {
                	contra.setPesoAdbAmount(NEGATIVE.multiply(allocationFileRecord.getPesoAdbAmount()));
                }
                transactions.add(allocationFileRecord);
                transactions.add(contra);
            }
        }
        return transactions;
    }

    private void sendToChannel(File file,String filename) {
		MessageBuilder<File> builder = MessageBuilder.withPayload(file);
        builder.setHeader(getFilenameProperty(),filename);
        channel.send(builder.build());
	}
    
    public String getDate(Date date){
    	SimpleDateFormat dateToString = new SimpleDateFormat("MMdd");
        String newAppDate = "0";
        
        if(date == null){
        	newAppDate = "0";
        } else {
        	newAppDate = dateToString.format(date);
        }
		return newAppDate;	
    }

    public String getMainOfficeBranch() {
        return mainOfficeBranch != null ? mainOfficeBranch : CONTRA_UNIT;
    }

    public void setMainOfficeBranch(String mainOfficeBranch) {
        this.mainOfficeBranch = mainOfficeBranch;
    }

    public String getFilenameProperty() {
        return filenameProperty != null ? filenameProperty : DEFAULT_FILENAME_PROPERTY;
    }

    public void setFilenameProperty(String filenameProperty) {
        this.filenameProperty = filenameProperty;
    }

    public String getFilenamePrefix() {
        return filenamePrefix;
    }

    public void setFilenamePrefix(String filenamePrefix) {
        this.filenamePrefix = filenamePrefix;
    }

    public void setProfitLossAccountingCode (String profitLossAccountingCode){
        this.profitLossAccountingCode = profitLossAccountingCode;
    }

    public void setTreasuryAllocationCode (String treasuryAllocationCode){
        this.treasuryAllocationCode = treasuryAllocationCode;
    }
}
