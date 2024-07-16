package com.ucpb.tfs.report.enums;

import java.util.ArrayList;
import java.util.List;

public enum YearlyBatchReports {

	Year_End_Report_on_Foreign_LCS_Opened_per_Advising_Bank("Year_End_Report_on_Foreign_LCS_Opened_per_Advising_Bank.pdf","runYearEndReportOnForeignLcsOpenedPerAdvisingBank",null),
	Year_End_Report_on_Foreign_LCS_Opened_per_Confirming_Bank("Year_End_Report_on_Foreign_LCS_Opened_per_Confirming_Bank.pdf","runYearEndReportOnForeignLcsOpenedPerConfirmingBank",null),
	Year_End_Report_on_Foreign_LCS_Opened_per_Country("Year_End_Report_on_Foreign_LCS_Opened_per_Country.pdf","runYearEndReportOnForeignLcsOpenedPerCountry",null),
	YTD_Customs_Duties_And_Taxes_And_Other_Levies("YTD_Customs_Duties_And_Taxes_And_Other_Levies.pdf","runYtdCustomsDutiesAndTaxesAndOtherLevies",null),
	YTD_Transaction_Count_Import_and_Export("YTD_Transaction_Count_Import_and_Export.pdf","runYtdTransactionCountImportExportReport",null);
	
	String label;
	String functionName;
	String activationKey;
	
	private YearlyBatchReports(String label,String functionName,String activationKey){
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
		for(YearlyBatchReports temp:YearlyBatchReports.values()){
			labels.add(temp.toString());
		}
		return labels;
	}
	
	public static List<String> getAllFunctions(){
		List<String> functions = new ArrayList<String>();
		for(YearlyBatchReports temp: YearlyBatchReports.values()){
			functions.add(temp.getFunctionName());
		}
		return functions;
	}
	
	@Override
	public String toString(){
		return this.label;
	}
}
