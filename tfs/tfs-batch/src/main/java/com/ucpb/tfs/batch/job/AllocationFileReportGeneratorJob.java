package com.ucpb.tfs.batch.job;

import com.ancientprogramming.fixedformat4j.format.FixedFormatManager;
import com.ancientprogramming.fixedformat4j.format.impl.FixedFormatManagerImpl;
import com.ucpb.tfs.batch.report.dw.AllocationFileRecord;
import com.ucpb.tfs.batch.report.dw.service.AllocationFileService;
import com.ucpb.tfs.batch.util.IOUtil;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.support.MessageBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 */
//TODO: REFACTOR JOBS. EXTRACT TO SUPER CLASS
public class AllocationFileReportGeneratorJob implements SpringJob {

    /**
	 * 
	 */
    private static final SimpleDateFormat EXECUTION_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    public static final String DEFAULT_FILENAME_PROPERTY = "filename";
    private static final long serialVersionUID = -460101957440425582L;
    public static final String CONTRA_UNIT = "10909";

    private final ExpressionParser parser = new SpelExpressionParser(new SpelParserConfiguration(true, true));

    private static final String DEFAULT_FILENAME = "WIP.txt";

	private static FixedFormatManager manager = new FixedFormatManagerImpl();

	private MessageChannel channel;

	private AllocationFileService allocationFileService;

    private Date targetDate;

    private static final BigDecimal NEGATIVE = new BigDecimal("-1");

    private String mainOfficeBranch = "30001";

    private String filenamePrefix;

    private String filenameProperty;

    private String profitLossAccountingCode = "561501030000";

    private String treasuryAllocationCode = "10903";


    public AllocationFileReportGeneratorJob(MessageChannel channel, String filenamePrefix, AllocationFileService allocationFileService) {
		this.channel = channel;
        this.filenamePrefix = filenamePrefix;
        this.allocationFileService = allocationFileService;
	}

	public void execute() {
        execute(new Date(),profitLossAccountingCode, treasuryAllocationCode);
	}

    public void execute(String reportDate) {
        //TODO
        execute(new Date(), profitLossAccountingCode, treasuryAllocationCode);
    }


    public void execute(String reportDate, String fxProfitOrLossAccountingCode, String treasuryAllocationCode) {
        //TODO

        execute(new Date(), fxProfitOrLossAccountingCode,treasuryAllocationCode);
    }

    public void execute(Date targetDate, String fxProfitOrLossAccountingCode, String treasuryAllocationCode) {

        System.out.println("AllocationFileReportGeneratorJob execute:"+targetDate);
        PrintWriter writer = null;
        File tempFile = null;
        String actualFilename = "";
        
        try {

            actualFilename = filenamePrefix + "_" + getExecutionDate(targetDate) + ".txt";
            System.out.println("actualFilename = "+actualFilename);
            tempFile = new File(actualFilename);

            writer = new PrintWriter(new FileWriter(actualFilename));

            List<AllocationFileRecord> allocationsIncome = allocationFileService.getProductAllocations(targetDate, fxProfitOrLossAccountingCode, treasuryAllocationCode);
            System.out.println("allocationsIncome.size() = " + allocationsIncome.size());

            List<AllocationFileRecord> allocationsAdb = allocationFileService.getProductAverageDailyBalanceRecords(targetDate);
            System.out.println("allocationsAdb.size())) = " + allocationsAdb.size());
            
            List<AllocationFileRecord> transactions = generateAndCombineWithContraRecordsIncome(allocationsIncome);
            transactions.addAll(generateAndCombineWithContraRecordsAdb(allocationsAdb));

            for (AllocationFileRecord record : transactions) {

                writer.print(manager.export(record).trim());

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

        	// not applicable because no allocation unit code is null or empty
            //if(allocationFileRecord == null || allocationFileRecord.getAllocationUnit()==null || allocationFileRecord.getAllocationUnit().isEmpty()){
            //    allocationFileRecord.setAllocationUnit(getMainOfficeBranch());
            //}
        	
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

            if(contra.getAllocationUnit().length()!=5){
                contra.setAllocationUnit(CONTRA_UNIT);
            }            
                       
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

                // not applicable because no allocation unit code is null or empty
                //if( allocationFileRecord.getAllocationUnit()==null || allocationFileRecord.getAllocationUnit().isEmpty()){
                //    allocationFileRecord.setAllocationUnit(getMainOfficeBranch());
                //}
                
                allocationFileRecord.setAllocationUnit(allocationFileRecord.getAllocationUnit().trim());

                AllocationFileRecord contra = new AllocationFileRecord(allocationFileRecord);
                //contra.setAllocationUnit(getMainOfficeBranch());
                contra.setOriginalTransactionAmount(NEGATIVE.multiply(allocationFileRecord.getOriginalTransactionAmountUnMultiplied()));
                
                //handling of Null values
                BigDecimal allocationFileRecordGetTotalAmount = allocationFileRecord.getTotalAmount() != null && 
                		allocationFileRecord.getTotalAmount().toString().isEmpty()? 
                				new BigDecimal(allocationFileRecord.getTotalAmount().toString()) : BigDecimal.ZERO;
                
                contra.setTotalAmount(NEGATIVE.multiply(allocationFileRecordGetTotalAmount));
                contra.setAllocationUnit("10909");

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

    private String getExecutionDate(Date date){
        return EXECUTION_DATE_FORMAT.format(date);
    }

    public void setProfitLossAccountingCode (String profitLossAccountingCode){
        this.profitLossAccountingCode = profitLossAccountingCode;
    }

    public void setTreasuryAllocationCode (String treasuryAllocationCode){
        this.treasuryAllocationCode = treasuryAllocationCode;
    }
}
