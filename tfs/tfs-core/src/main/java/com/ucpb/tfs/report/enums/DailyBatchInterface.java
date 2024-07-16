package com.ucpb.tfs.report.enums;

import java.util.ArrayList;
import java.util.List;

public enum DailyBatchInterface {
	
	/*	PROLOGUE:
	 * 	(revision)
	  	SCR/ER Number: IBD-15-0828-01
		SCR/ER Description: Comparison of Balances in DW and SIBS-GL
		[Revised by:] Jesse James Joson
		[Date revised:] 09/17/2015
		Program [Revision] Details: add new objects that will call function in other class
    	INPUT: Extract_SIBS_GL_Accounts
    	OUTPUT: Daily_Master_GL_Summary.xls & Daily_Master_GL_DailyBalance_Summary.xls
    	PROCESS: Called by another class to call the declared function needed to execute the report
     */

	/*	PROLOGUE:
	 * 	(revision)
	  	SCR/ER Number: 
		SCR/ER Description:
		[Revised by:] Crystiann Puso
		[Date revised:] 02/22/2024
		Program [Revision] Details: add new objects ( Cicls Process and Generate Cicls File ) that will call function in other class
    	INPUT: Date
    	OUTPUT: ITD_CICLS_(Date).txt and ITD_REJECT_CICLS_(Date).txt 
     */

	Synchronize_Routing_Status("Synchronize Routing Status","synchronizeRoutingStatus"),
	Abort_Pending_ETS_Reversal("Abort Pending ETS Reversal","abortPendingEtsReversal"),
//	Revert_Data_Entry_to_Pending("Revert Data Entry to Pending","revertToPending"),
//	Process_Expired_LCs("Process Expired LCs"),
	Purge_Unapproved_ETS("Purge Unapproved ETS","purgeEts"),
	GL_Movement("GL Movement","gl"),
	Master_File("Master File","master"),
	Extract_SIBS_GL_Accounts("Extract SIBS GL Accounts","extractSibs"),
	DW_Parameter("DW Parameter","dw"),
	CBR_Parameter("CBR Parameter","cbr"),
	AMLA_Transaction("AMLA Transaction","transaction"),
	AMLA_Account("AMLA Account","account"),
	AMLA_Customer_Account("AMLA Customer Account","customerAccount"),
	AMLA_Customer("AMLA Customer","customer"),
	Balance("Balance","balance"),
	Tag_As_Pending("Tag As Pending","tagAsPending"),
	GL_Parameter("GL Parameter","params"),
	CICLS_HandOffFile("generateCiclsFile", "generateCiclsFile"),
	CICLS_Records("processCicls ","processCicls"),
	Allocation_File("Allocation File","allocation"),
	Master_Exception_Report("Master Exception Report","masterException"),
	Allocation_Exception_Report("Allocation Exception Report","allocationException");
	
	String label;
	String functionName;
		
	private DailyBatchInterface(String label,String functionName){
		this.label=label;
		this.functionName=functionName;
	}
	
	public String getFunctionName(){
		return this.functionName;
	}
	
	public static List<String> getAllLabels(){
		List<String> labels = new ArrayList<String>();
		for(DailyBatchInterface temp:DailyBatchInterface.values()){
			labels.add(temp.toString());
		}
		return labels;
	}
	
	public static List<String> getAllFunctions(){
		List<String> functions = new ArrayList<String>();
		for(DailyBatchInterface temp: DailyBatchInterface.values()){
			functions.add(temp.getFunctionName());
		}
		return functions;
	}
	
	@Override
	public String toString(){
		return this.label;
	}
}