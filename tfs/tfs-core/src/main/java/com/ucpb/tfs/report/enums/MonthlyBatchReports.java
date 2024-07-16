package com.ucpb.tfs.report.enums;

import java.util.ArrayList;
import java.util.List;

public enum MonthlyBatchReports {

	Collected_2Percent_CWT("Collected_2%_CWT.pdf","runCollectedTwoPercentCwt",null),
	Consolidated_Report_on_Domestic_LCs_Opened_for_the_Month("Consolidated_Report_on_Domestic_LCs_Opened_for_the_Month.pdf","runConsolidatedReportDomesticLcOpenedForMonth",null),
	Consolidated_Report_on_Foreign_LCs_Opened_for_the_Month("Consolidated_Report_on_Foreign_LCs_Opened_for_the_Month.pdf","runConsolidatedReportOnForeignLcOpenedForMonth",null),
	Customs_Duties_And_Taxes_And_Other_Levies("Customs_Duties_And_Taxes_And_Other_Levies.pdf","runCustomsDutiesAndTaxesAndOtherLevies",null),
	Dm_Non_Lcs_Negotiated_For_The_Year_Classified_By_Top_30_Importer_And_Other_Local_Bank("Dm_Non_Lcs_Negotiated_For_The_Year_Classified_By_Top_30_Importer_And_Other_Local_Bank.pdf","runDmNonLcsNegoForTheYear",null),
	Domestic_LCs_Opened_for_the_Month("Domestic_LCs_Opened_for_the_Month.pdf","runDomesticLcOpenedForTheMonth",null),
	Export_Negotiations_for_the_Month_per_Client("Export_Negotiations_for_the_Month_per_Client.pdf","runExportNegofortheMonthperClient",null),
	Export_Negotiations_per_Collecting_Bank("Export_Negotiations_per_Collecting_Bank.pdf","runexportNegoperCollectingBank",null),
	Export_Payments_for_the_Month_per_Client("Export_Payments_for_the_Month_per_Client.pdf","runExportPaymentsfortheMonthperClient",null),
	Foreign_LCs_Opened_for_the_Month("Foreign_LCs_Opened_for_the_Month.pdf","runForeignLcOpenedForTheMonth",null),
	Fx_Non_Lcs_Negotiated_For_The_Year_Classified_By_Top_30_Importer_And_Remitting_Bank("Fx_Non_Lcs_Negotiated_For_The_Year_Classified_By_Top_30_Importer_And_Remitting_Bank.pdf","runFxNonLcsNegoForTheYear",null),
	List_of_Transactions_With_No_CIF_Number("List of Transactions With No CIF Number.xls","runListOfTransactionsWithNoCifNumber",null),
	Monthly_Transaction_Count("Monthly_Transaction_Count.pdf","runMonthlyTransactionCountReport",null),
	Outstanding_Bank_Guaranty("Outstanding_Bank_Guaranty.pdf","runOutstandingBankGuarantyReport",null),
	Outstanding_Domestic_Cash_LCs_Per_Currency("Outstanding_Domestic_Cash_LCs_Per_Currency.pdf","runOutstandingDomesticCashLcPerCurrency",null),
	Outstanding_Domestic_Cash_LCs_Per_Importer("Outstanding_Domestic_Cash_LCs_Per_Importer.pdf","runOutstandingDomesticCashLcPerImporter",null),
	Outstanding_Domestic_Sight_LCs_Per_Currency("Outstanding_Domestic_Sight_LCs_Per_Currency.pdf","runOutstandingDomesticSightLcPerCurrency",null),
	Outstanding_Domestic_Sight_LCs_Per_Importer("Outstanding_Domestic_Sight_LCs_Per_Importer.pdf","runOutstandingDomesticSightLcPerImporter",null),
	Outstanding_Domestic_Standby_LCs_Per_Currency("Outstanding_Domestic_Standby_LCs_Per_Currency.pdf","runOutstandingDomesticStandbyLcPerCurrency",null),
	Outstanding_Domestic_Standby_LCs_Per_Importer("Outstanding_Domestic_Standby_LCs_Per_Importer.pdf","runOutstandingDomesticStandbyLcPerImporter",null),
	Outstanding_Domestic_Usance_LCs_Per_Currency("Outstanding_Domestic_Usance_LCs_Per_Currency.pdf","runOutstandingDomesticUsanceLcPerCurrency",null),
	Outstanding_Domestic_Usance_LCs_Per_Importer("Outstanding_Domestic_Usance_LCs_Per_Importer.pdf","runOutstandingDomesticUsanceLcPerImporter",null),
	Outstanding_Export_Negotiations_Domestic("Outstanding_Export_Negotiations_Domestic.pdf","runOutstandingExportNegotiationDomestic",null),
	Outstanding_Export_Negotiations_Foreign("Outstanding_Export_Negotiations_Foreign.pdf","runOutstandingExportNegotiationForeign",null),
	Outstanding_Foreign_Cash_LCs_per_Currency("Outstanding_Foreign_Cash_LCs_per_Currency.pdf","runOutstandingForeignCashLcPerCurrency",null),
	Outstanding_Foreign_Cash_LCs_Per_Importer("Outstanding_Foreign_Cash_LCs_per_Importer2.pdf","runOutstandingForeignCashLcPerImporter",null),
	Outstanding_Foreign_Sight_LCs_per_Currency("Outstanding_Foreign_Sight_LCs_per_Currency.pdf","runOutstandingForeignSightLcPerCurrency",null),
	Outstanding_Foreign_Sight_LCs_per_Importer("Outstanding_Foreign_Sight_LCs_per_Importer.pdf","runOutstandingForeignSightLcPerImporter",null),
	Outstanding_Foreign_Standby_LCs_per_Currency("Outstanding_Foreign_Standby_LCs_per_Currency.pdf","runOutstandingForeignStandbyLcPerCurrency",null),
	Outstanding_Foreign_Standby_LCs_per_Importer("Outstanding_Foreign_Standby_LCs_per_Importer.pdf","runOutstandingForeignStandbyLcPerImporter",null),
	Outstanding_Foreign_Usance_LCs_per_Currency("Outstanding_Foreign_Usance_LCs_per_Currency.pdf","runOutstandingForeignUsanceLcPerCurrency",null),
	Outstanding_Foreign_Usance_LCs_per_Importer("Outstanding_Foreign_Usance_LCs_per_Importer.pdf","runOutstandingForeignUsanceLcPerImporter",null),
	Outstanding_Inward_Bills_For_Collection_Dm_Dp_Per_Currency("Outstanding_Inward_Bills_For_Collection_Dm_Dp_Per_Currency.pdf","runOutstandingInwardBillsForCollectionDmDaDpPerCurrency",null),
	Outstanding_Inward_Bills_For_Collection_Fx_Da_Dp_Oa_Dr_Per_Currency("Outstanding_Inward_Bills_For_Collection_Fx_Da_Dp_Oa_Dr_Per_Currency.pdf","runOutstandingInwardBillsForCollectionFxDaDpPerCurrency",null),
	Outstanding_Inward_Bills_for_Collection_FXLC_per_Currency("Outstanding_Inward_Bills_for_Collection_FXLC_per_Currency.pdf","runOutstandingInwardBillsforCollectionFXLCperCurrency",null),
	Outstanding_Inward_Bills_for_Collection_DMLC_per_Currency("Outstanding_Inward_Bills_for_Collection_DMLC_per_Currency.pdf","runOutstandingInwardBillsforCollectionDMLCperCurrency",null),
	Product_Availments_Exception_Report("Product_Availments_Exception_Report.xls","runProductAvailmentsReport",null),
	Product_Availments_Report("Product_Availments_Report.xls","runProductAvailmentsReport","notException"),
	Profitability_Monitoring_Exception_Report("Profitability_Monitoring_Exception_Report.xls","runProfitabilityMonitoringReport",null),
	Profitability_Monitoring_Report("Profitability_Monitoring_Report.xls","runProfitabilityMonitoringReport","notException"),
	Tfs_Mco_Report("Tfs_Mco_Report.pdf","runTfsMcoReport",null),
	Volumetrics_Report("Volumetrics_Report.pdf","runVolumetrics",null),
	YTD_Report_on_Domestic_LCs_Opened("YTD_Report_on_Domestic_LCs_Opened.pdf","runYtdReportOnDomesticLcOpened",null),
	YTD_Report_on_Domestic_LCs_Opened_Classified_by_top_30_Importer_and_Advising_Local_Bank_in_PHP("YTD_Report_on_Domestic_LCs_Opened_Classified_by_top_30_Importer_and_Advising_Local_Bank_in_PHP.pdf","runYtdReportOnDomesticLcOpenedClassifiedByBank",null),
	YTD_Report_on_Foreign_LCs_Opened("YTD_Report_on_Foreign_LCs_Opened.pdf","runYtdReportOnForeignLcOpened",null),
	YTD_Report_on_Foreign_LCs_Opened_Classified_by_top_30_Importer_and_Advising_Bank_in_USD("YTD_Report_on_Foreign_LCs_Opened_Classified_by_top_30_Importer_and_Advising_Bank_in_USD.pdf","runYtdReportOnForeignLcOpenedClassifiedByBank",null);
	
	
	String label;
	String functionName;
	String activationKey;
	
	private MonthlyBatchReports(String label,String functionName,String activationKey){
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
		for(MonthlyBatchReports temp:MonthlyBatchReports.values()){
			labels.add(temp.toString());
		}
		return labels;
	}
	
	public static List<String> getAllFunctions(){
		List<String> functions = new ArrayList<String>();
		for(MonthlyBatchReports temp: MonthlyBatchReports.values()){
			functions.add(temp.getFunctionName());
		}
		return functions;
	}
	
	@Override
	public String toString(){
		return this.label;
	}
}
