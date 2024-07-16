package com.ucpb.tfs.report.enums;

import java.util.ArrayList;
import java.util.List;

public enum DailyBatchReports {

	/*	PROLOGUE:
	 * 	(revision)
	  	SCR/ER Number: IBD-15-0828-01
		SCR/ER Description: Comparison of Balances in DW and SIBS-GL
		[Revised by:] Jesse James Joson
		[Date revised:] 09/17/2015
		Program [Revision] Details: add new objects that will call function in other class
    	INPUT: Daily_Master_GL_Summary & Daily_Master_GL_DailyBalance_Summary
    	OUTPUT: Daily_Master_GL_Summary.xls & Daily_Master_GL_DailyBalance_Summary.xls
    	PROCESS: Called by another class to call the declared function needed to execute the report
     */
	
	Consolidated_Daily_Report_on_Deposits_Collected("Consolidated_Daily_Report_on_Deposits_Collected.pdf","runConsolidatedDailyReportDepositsCollected","remittance"),
	Consolidated_Daily_Report_on_Deposits_Collected_2("Consolidated_Daily_Report_on_Deposits_Collected_2.pdf","runConsolidatedDailyReportDepositsCollected",null),
	Consolidated_Report_on_Daily_Collections_of_Customs_Duties_Taxes_and_Other_Levies("Consolidated_Report_on_Daily_Collections_of_Customs_Duties_Taxes_and_Other_Levies.pdf","runConsolidatedReportDaliyCollectionsCdtOtherLevies","remittance"),
	Consolidated_Report_on_Daily_Collections_of_Customs_Duties_Taxes_and_Other_Levies_2("Consolidated_Report_on_Daily_Collections_of_Customs_Duties_Taxes_and_Other_Levies_2.pdf","runConsolidatedReportDaliyCollectionsCdtOtherLevies",null),
	Consolidated_Report_on_Daily_Collections_of_Customs_Duties_Taxes_and_Other_Levies_3("Consolidated_Report_on_Daily_Collections_of_Customs_Duties_Taxes_and_Other_Levies_3.pdf","runConsolidatedReportDailyCollectionsAll",null),
	Consolidated_Report_on_Daily_Collections_of_Customs_Duties_Taxes_and_Other_Levies_4("Consolidated_Report_on_Daily_Collections_of_Customs_Duties_Taxes_and_Other_Levies_4.pdf","runConsolidatedReportDailyCollectionsImportExport",null),
	Consolidated_Report_on_Daily_Collections_of_Customs_Duties_Taxes_and_Other_Levies_5("Consolidated_Report_on_Daily_Collections_of_Customs_Duties_Taxes_and_Other_Levies_5.pdf","runConsolidatedReportDailyCollectionsFinalAdvance",null),
	Consolidated_Report_On_Daily_Collections_Of_Export_Documentary_Stamp_Fees("Consolidated_Report_On_Daily_Collections_Of_Export_Documentary_Stamp_Fees.pdf","runConsolidatedReportDailyCollectionsExportDocumentaryStampFees","remittance"),
	Consolidated_Report_On_Daily_Collections_Of_Export_Documentary_Stamp_Fees_2("Consolidated_Report_On_Daily_Collections_Of_Export_Documentary_Stamp_Fees_2.pdf","runConsolidatedReportDailyCollectionsExportDocumentaryStampFees",null),
	Consolidated_Report_on_Daily_Collections_of_Import_Processing_Fees("Consolidated_Report_on_Daily_Collections_of_Import_Processing_Fees.pdf","runConsolidatedReportDailyCollectionsImportProcessingFees","remittance"),
	Consolidated_Report_on_Daily_Collections_of_Import_Processing_Fees_2("Consolidated_Report_on_Daily_Collections_of_Import_Processing_Fees_2.pdf","runConsolidatedReportDailyCollectionsImportProcessingFees",null),
	Daily_Master_GL_Summary("Daily_Master_GL_Summary.xls","runDailyMasterGLSummary",null),
	Daily_Master_GL_DailyBalance_Summary("Daily_Master_GL_DailyBalance_Summary.xls","runDailyMasterGLDailyBalanceSummary",null),
	Daily_Foreign_Cash_LCs_Opened("Daily_Foreign_Cash_LCs_Opened.pdf","runDailyForeignCashLcOpened",null),
	Daily_Foreign_LCs_Opened_Report_with_CDT_Details("Daily_Foreign_LCs_Opened_Report_with_CDT_Details.pdf","runDailyFxlcOpenedReportCdtDetails",null),
	Daily_Foreign_Regular_LCs_Opened("Daily_Foreign_Regular_LCs_Opened.pdf","runDailyForeignRegularLcOpened",null),
	Daily_Funding_Report("Daily_Funding_Report.pdf","runDailyFunding",null),
	Daily_Outstanding_Foreign_LCs("Daily_Outstanding_Foreign_LCs.pdf","runDailyOutstandingForeignLc",null),
	Daily_Outstanding_LCs_CCBD_Report("Daily_Outstanding_LCs_CCBD_Report.pdf","dailyOutstandingCCBD",null),
	Daily_Report_on_Processed_Refund("Daily_Report_on_Processed_Refund.pdf","runDailyReportProcessedRefunds",null),
	Daily_Summary_of_Accounting_Entries("Daily_Summary_of_Accounting_Entries.pdf","runDailySummaryOfAccountingEntries",null),
	TFS_CASA_Posting_Report("TFS_CASA_Posting_Report.pdf","runTfsCasaPostingReport","ALL"),
	Trade_Services_AMLA_Reported_Transactions("Trade_Services_AMLA_Reported_Transactions.pdf","runTradeServicesAMLAReportedTransactions",null),
	TRAMS("TRAMS.csv","runTramsReport",null);
	
	String label;
	String functionName;
	String activationKey;
	
	private DailyBatchReports(String label,String functionName,String activationKey){
		this.label=label;
		this.functionName=functionName;
		this.activationKey=activationKey;
	}
	
	public String getFunctionName(){
		return this.functionName;
	}
	
	public String getActivationKey(){
		return this.activationKey;
	}
	
	public static List<String> getAllLabels(){
		List<String> labels = new ArrayList<String>();
		for(DailyBatchReports temp:DailyBatchReports.values()){
			labels.add(temp.toString());
		}
		return labels;
	}
	
	public static List<String> getAllFunctions(){
		List<String> functions = new ArrayList<String>();
		for(DailyBatchReports temp: DailyBatchReports.values()){
			functions.add(temp.getFunctionName());
		}
		return functions;
	}
	
	@Override
	public String toString(){
		return this.label;
	}
}