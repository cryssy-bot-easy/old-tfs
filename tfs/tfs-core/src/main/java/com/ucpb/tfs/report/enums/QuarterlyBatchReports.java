package com.ucpb.tfs.report.enums;

import java.util.ArrayList;
import java.util.List;

public enum QuarterlyBatchReports {

	AP_Others("AP_Others.pdf","runApOthers",null),
	AR_Others("AR_Others.pdf","runArOthers",null),
	Quarterly_Report_on_Foreign_Standby_LCs_Opened("Quarterly_Report_on_Foreign_Standby_LCs_Opened.pdf","runQuarterlyReportOnForeignStandybyLcsOpened",null);
	
	String label;
	String functionName;
	String activationKey;
	
	private QuarterlyBatchReports(String label,String functionName,String activationKey){
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
		for(QuarterlyBatchReports temp:QuarterlyBatchReports.values()){
			labels.add(temp.toString());
		}
		return labels;
	}
	
	public static List<String> getAllFunctions(){
		List<String> functions = new ArrayList<String>();
		for(QuarterlyBatchReports temp: QuarterlyBatchReports.values()){
			functions.add(temp.getFunctionName());
		}
		return functions;
	}
	
	@Override
	public String toString(){
		return this.label;
	}
}