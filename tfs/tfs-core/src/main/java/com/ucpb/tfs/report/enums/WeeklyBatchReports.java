package com.ucpb.tfs.report.enums;

import java.util.ArrayList;
import java.util.List;

public enum WeeklyBatchReports {

	Weekly_Schedule_of_Doc_Stamps_108("Weekly_Schedule_of_Doc_Stamps_108.csv","runWeeklyScheduleDocStamps","viewWeeklyScheduleDocStamps108"),
	Weekly_Schedule_of_Doc_Stamps_113("Weekly_Schedule_of_Doc_Stamps_113.csv","runWeeklyScheduleDocStamps","viewWeeklyScheduleDocStamps113"),
	Weekly_Schedule_of_Doc_Stamps_TR("Weekly_Schedule_of_Doc_Stamps_TR.csv","runWeeklyScheduleDocStamps","viewWeeklyScheduleDocStampsTR"),
	Weekly_Report_on_Maturing_Usance_LCs("Weekly_Report_on_Maturing_Usance_LCs.pdf","runWeeklyReportOnMaturingUsancLc",null);
	
	
	String label;
	String functionName;
	String activationKey;
	
	private WeeklyBatchReports(String label,String functionName,String activationKey){
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
		for(WeeklyBatchReports temp:WeeklyBatchReports.values()){
			labels.add(temp.toString());
		}
		return labels;
	}
	
	public static List<String> getAllFunctions(){
		List<String> functions = new ArrayList<String>();
		for(WeeklyBatchReports temp: WeeklyBatchReports.values()){
			functions.add(temp.getFunctionName());
		}
		return functions;
	}
	
	@Override
	public String toString(){
		return this.label;
	}
}
