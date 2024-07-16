package com.ucpb.tfs.batch.job;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import javax.sql.DataSource;

/**  PROLOGUE:
 * 	(revision)
	SCR/ER Number: SCR# IBD-16-0615-01
	SCR/ER Description: To comply with the requirement for CIF archiving/purging of inactive accounts in TFS.
	[Created by:] Jesse James Joson
	[Date Created:] 09/22/2016
	Program [Revision] Details: Main Class that process the account purging.
	PROJECT: CORE
	MEMBER TYPE  : Java
	Project Name: CifPurgingJob
 */

/**  PROLOGUE:
 * 	(revision)
	SCR/ER Number: ER 20161001-001
	SCR/ER Description: Failed to execute Purge accounts phase 2
	[Created by:] Jesse James Joson
	[Date Created:] 10/01/2016
	Program [Revision] Details: Do not include delete on revinfo those are for users table
	PROJECT: CORE
	MEMBER TYPE  : Java
	Project Name: CifPurgingJob
 */


public class CifPurgingJob {

	private final DataSource mydataSource;
	private Connection tfsConn = null;
	
	private final DataSource tempdataSource;
	private Connection tempConn = null;
	
	private final String purgingRetention;
	private final String purgingTerm;
	private final String purgingRetention2;
	private final String purgingTerm2;
	private final long threadSleep;
	private final String prodSchema;
	
	private List<String> withTradeServiceIds = Arrays.asList("TRADESERVICE","ADDITIONALCONDITION","INSTRUCTIONTOBANK","REQUIREDDOCUMENT",
			"SERVICECHARGE","PRODUCTREFUNDDETAIL","TRANSMITTALLETTER","ATTACHMENT","OTHERCHARGEDETAIL","SERVICEINSTRUCTION","REMARKS",
			"ROUTINGINFORMATION","ROUTES","ETSPAYMENT","ETSPAYMENTDETAIL","PAYMENT","PAYMENTDETAIL","TASK","ETSSERVICECHARGE","PLHOLDER",
			"MTMESSAGE","ACCOUNTLOG","CASATRANSACTIONLOG","CUSTOMERACCOUNT","DAILYFUNDING","INT_ACCENTRYACTUAL","TRANSACTIONLOG");
	
	private List<String> withDocNum= Arrays.asList("INT_ACCENTRYACTUAL","MTMESSAGE");

	private List<String> tradeProductTables= Arrays.asList("TRADEPRODUCT","TRADEPRODUCT_AUDIT","DAILYBALANCE");
	
	private List<String> oaTables= Arrays.asList("OPENACCOUNT","OPENACCOUNT_AUDIT");
	private List<String> drTables= Arrays.asList("DIRECTREMITTANCE","DIRECTREMITTANCE_AUDIT");
	private List<String> dpTables= Arrays.asList("DOCUMENTAGAINSTPAYMENT","DOCUMENTAGAINSTPAYMENT_AUDIT");
	private List<String> daTables= Arrays.asList("DOCUMENTAGAINSTACCEPTANCE","DOCUMENTAGAINSTACCEPTANCE_AUDIT");
	private List<String> bgTables= Arrays.asList("INDEMNITY","INDEMNITY_AUDIT");
	private List<String> cdtTables= Arrays.asList("CDTPAYMENTHISTORY","CDTPAYMENTREQUEST","EMAIL_NOTIF");
	
	private List<String> lcTables= Arrays.asList("LETTEROFCREDIT","LETTEROFCREDIT_AUDIT","LETTEROFCREDIT_LCADDITIONALCONDITION_AUDIT","LETTEROFCREDIT_LCINSTRUCTIONTOBANK_AUDIT",
			"LETTEROFCREDIT_LCNEGOTIATION_AUDIT","LETTEROFCREDIT_LCNEGOTIATIONDISCREPANCY_AUDIT","LETTEROFCREDIT_LCREQUIREDDOCUMENT_AUDIT");
	private List<String> lc2Tables= Arrays.asList("LCADDITIONALCONDITION","LCINSTRUCTIONTOBANK","LCNEGOTIATION","LCREQUIREDDOCUMENT",
			"LCADDITIONALCONDITION_AUDIT","LCINSTRUCTIONTOBANK_AUDIT","LCNEGOTIATION_AUDIT","LCREQUIREDDOCUMENT_AUDIT","LCNEGOTIATIONDISCREPANCY","LCNEGOTIATIONDISCREPANCY_AUDIT");
	
	private List<String> ebTables= Arrays.asList("EXPORTBILLS","EXPORTBILLS_AUDIT","EXPORTBILLS_DOCUMENTSENCLOSED_AUDIT","EXPORTBILLS_ENCLOSEDINSTRUCTION_AUDIT");
	private List<String> eb2Tables= Arrays.asList("DOCUMENTSENCLOSED","DOCUMENTSENCLOSED_AUDIT","ENCLOSEDINSTRUCTION","ENCLOSEDINSTRUCTION_AUDIT");

	private List<String> eaTables= Arrays.asList("EXPORTADVISING","EXPORTADVISING_AUDIT","ADVANCEPAYMENT","ADVANCEPAYMENT_AUDIT");
	

	private List<String> otherTables= Arrays.asList("ACCOUNTSPAYABLE","ACCOUNTSPAYABLEACTIVITY","ACCOUNTSRECEIVABLE","ACCOUNTSRECEIVABLEACTIVITY", 
			"REBATE","CORRESCHARGEACTUAL","CORRESCHARGEADVANCE","MARGINALDEPOSIT","MARGINALDEPOSITACTIVITY");
	
	private List<String> masterTables = Arrays.asList("LETTEROFCREDIT","LETTEROFCREDIT_AUDIT","DOCUMENTAGAINSTACCEPTANCE","DOCUMENTAGAINSTACCEPTANCE_AUDIT",
			"DOCUMENTAGAINSTPAYMENT","DOCUMENTAGAINSTPAYMENT_AUDIT","DIRECTREMITTANCE","DIRECTREMITTANCE_AUDIT","OPENACCOUNT","OPENACCOUNT_AUDIT",
			"EXPORTBILLS","EXPORTBILLS_AUDIT","EXPORTADVISING","EXPORTADVISING_AUDIT","TRADEPRODUCT","TRADEPRODUCT_AUDIT","ADVANCEPAYMENT","ADVANCEPAYMENT_AUDIT");
	
	private List<String> parent = Arrays.asList("RBAC_AUTHORIZATION","RBAC_AUTHORITYTYPE");
	
	public CifPurgingJob(DataSource mydataSource,DataSource tempdataSource,String purgingRetention, String purgingTerm,String purgingRetention2,
			String purgingTerm2,long threadSleep,String prodSchema) {
		this.mydataSource = mydataSource;
		this.tempdataSource = tempdataSource;
		this.purgingRetention = purgingRetention;
		this.purgingTerm = purgingTerm;
		this.purgingRetention2 = purgingRetention2;
		this.purgingTerm2 = purgingTerm2;
		this.threadSleep = threadSleep;
		this.prodSchema = prodSchema;
	}
	
	private void initializeConnection() throws SQLException{
		if(tfsConn != null){
			tfsConn.close();
			tfsConn=null;
		}
		tfsConn = mydataSource.getConnection();
		
		if(tempConn != null){
			tempConn.close();
			tempConn=null;
		}
		tempConn = tempdataSource.getConnection();
	}
	
	private void initializeList() throws SQLException{
		this.withTradeServiceIds = Arrays.asList("TRADESERVICE","ADDITIONALCONDITION","INSTRUCTIONTOBANK","REQUIREDDOCUMENT",
				"SERVICECHARGE","PRODUCTREFUNDDETAIL","TRANSMITTALLETTER","ATTACHMENT","OTHERCHARGEDETAIL","SERVICEINSTRUCTION","REMARKS",
				"ROUTINGINFORMATION","ROUTES","ETSPAYMENT","ETSPAYMENTDETAIL","PAYMENT","PAYMENTDETAIL","TASK","ETSSERVICECHARGE","PLHOLDER",
				"MTMESSAGE","ACCOUNTLOG","CASATRANSACTIONLOG","CUSTOMERACCOUNT","DAILYFUNDING","INT_ACCENTRYACTUAL","TRANSACTIONLOG");
		
		this.otherTables = Arrays.asList("ACCOUNTSPAYABLE","ACCOUNTSPAYABLEACTIVITY","ACCOUNTSRECEIVABLE","ACCOUNTSRECEIVABLEACTIVITY", 
				"REBATE","CORRESCHARGEACTUAL","CORRESCHARGEADVANCE","MARGINALDEPOSIT","MARGINALDEPOSITACTIVITY");
		
	}
	
	private String getMajorRetention() throws Exception {
		String retention = "";
		if(purgingTerm.equalsIgnoreCase("YEAR")) {
			retention = purgingRetention + "0000";
		} else {
			throw new Exception("The Purging Term was changed, please revise the program.");
		}
		
		return retention;		
	}
	
	private String getMinorRetention() throws Exception {		
		return (purgingRetention2 + " " + purgingTerm2);		
	}
		
	public void insert(List<List<Map<String, String>>> listOfListByTens) throws Exception {
		
		for (List<Map<String, String>> byTen : listOfListByTens) {
			//Collections.reverse(byTen);
			System.out.println(byTen);
			String inactiveByTen = "";	
			int counter=0;
			
			for (Map<String, String> eachAccount : byTen) {
				inactiveByTen = inactiveByTen + "'" + eachAccount.get("DOCUMENTNUMBER") + "',";
			}	
			
			inactiveByTen = inactiveByTen.substring(0,inactiveByTen.length()-1);
			
			insertToCommonTables(inactiveByTen);
			
			for (Map<String, String> inactiveAccountMap : byTen) {
				String inactiveAccount = inactiveAccountMap.get("DOCUMENTNUMBER");
				
				if(inactiveAccountMap.get("PRODUCTTYPE").equalsIgnoreCase("DA")) {
					purgeNonLc(daTables, inactiveAccount);
				} else if(inactiveAccountMap.get("PRODUCTTYPE").equalsIgnoreCase("DP")) {	
					purgeNonLc(dpTables, inactiveAccount);
				} else if(inactiveAccountMap.get("PRODUCTTYPE").equalsIgnoreCase("DR")) {
					purgeNonLc(drTables, inactiveAccount);					
				} else if(inactiveAccountMap.get("PRODUCTTYPE").equalsIgnoreCase("OA")) {
					purgeNonLc(oaTables, inactiveAccount);						
				} else if(inactiveAccountMap.get("PRODUCTTYPE").equalsIgnoreCase("CDT")) {
					purgeNonLc(cdtTables, inactiveAccount);					
				} else if(inactiveAccountMap.get("PRODUCTTYPE").equalsIgnoreCase("LC")) {
					purgeLc(lcTables,lc2Tables, inactiveAccount,"LETTEROFCREDIT_");
					purgeIndemnity(bgTables, inactiveAccount);
				} else if(inactiveAccountMap.get("PRODUCTTYPE").equalsIgnoreCase("EB")) {
					purgeLc(ebTables,eb2Tables, inactiveAccount,"EXPORTBILLS_");					
				}
				
				
				
				purgeOthersByAccount(otherTables, inactiveAccount);
				
				String updateScript = "UPDATE TFCFACCS SET ISPURGED = '1' WHERE DOCUMENTNUMBER = '" + inactiveAccount + "' ";
				PreparedStatement ps = tfsConn.prepareStatement(updateScript);
				System.out.println(updateScript);
				ps.executeUpdate();
				tfsConn.commit();
				
				ps.close();			
				
				
			}	
			
			counter++;
			if (counter==30) {
				Thread.sleep(threadSleep);
				counter=0;
			}
		}
	}
	
	public void executePhase1(String appDate,String runDate2) throws Exception {
		try {
			String retention = getMajorRetention();
			String retention2 = getMinorRetention();
			System.out.println("PHASE 1");
			initializeConnection();
			initializeList();
			System.out.println(appDate);
			long rundate = Long.parseLong(appDate.toString());
			int counter=1;
			
			List<Map<String, String>> inactiveAccounts = getInactiveAccounts(rundate,retention);			
						
			System.out.println("Inactive Accounts ... " + inactiveAccounts);
			List<List<Map<String, String>>> listOfListByTens = new ArrayList<List<Map<String, String>>>();
			List<Map<String, String>> listByTens = new ArrayList<Map<String, String>>();
			listByTens.clear();
			listOfListByTens.clear();
			
			
			for(Map<String, String> inactiveAccountMap : inactiveAccounts) {
				
				if(counter%20 != 0) {
					listByTens.add(inactiveAccountMap);	
					
				} else {				
					listByTens.add(inactiveAccountMap);
					listOfListByTens.add(listByTens);
					insert(listOfListByTens);
					
					listByTens.clear();
					listOfListByTens.clear();
				}
				
				
				if(counter==inactiveAccounts.size() && counter%20 != 0) {
					listOfListByTens.add(listByTens);
					insert(listOfListByTens);
					
					listByTens.clear();
					listOfListByTens.clear();
				}
				
				counter++;
			}
				
			
			purgeOthersByDate(otherTables, runDate2, retention2);
			counter=0;
			List<String> inactiveAccounts2 = getInactiveEa(rundate,runDate2,retention); 
			for (String inactiveAccount : inactiveAccounts2) {
							
				insertToCommonTables("'" + inactiveAccount + "'");
				
				purgeNonLc(eaTables, inactiveAccount);
				counter++;
				if (counter==350) {
					Thread.sleep(threadSleep);
					counter=0;
				}
			}
			

		} catch (Exception e) {
			Thread.sleep(5000);
			e.printStackTrace();
			throw e;
		} finally {
			closeConnection();
			Thread.sleep(5000);
		}
		
	}
	
	private List<Map<String, String>> getInactiveAccounts(long rundate,String retention) throws SQLException {
		List<Map<String, String>> inactiveAccounts = new ArrayList<Map<String,String>>();
		Map<String, String> temp = new HashMap<String, String>();		
		String selectInactiveAccounts = "SELECT DOCUMENTNUMBER,PRODUCTTYPE FROM TFCFACCS WHERE (LASTDATE + " + retention + ") <= " + rundate + 
				" AND STATUS = 2 AND ISPURGED = '0' AND PRODUCTTYPE <> 'BG' AND PRODUCTTYPE <> 'EA' ORDER BY LASTDATE" ;
		PreparedStatement ps = tfsConn.prepareStatement(selectInactiveAccounts);
//		System.out.println("Script ... " + selectInactiveAccounts);
		ResultSet rs = ps.executeQuery();
		
		while(rs.next()) {
			temp = new HashMap<String, String>();
			temp.put("DOCUMENTNUMBER", rs.getString(1));
			temp.put("PRODUCTTYPE", rs.getString(2));
			inactiveAccounts.add(temp);
		}
	
		ps.close();
		
		return inactiveAccounts;
	}
	
	private void insertToCommonTables(String inactiveAccount) throws Exception {
		
		String selectScript= "SELECT TRADESERVICEID,SERVICEINSTRUCTIONID FROM TRADESERVICE WHERE UCASE(TRADEPRODUCTNUMBER) IN (" + inactiveAccount + ") " +
				" OR TRADESERVICEREFERENCENUMBER IN (" + inactiveAccount + ")";
		PreparedStatement ps = tfsConn.prepareStatement(selectScript);
//		System.out.println(selectScript);
		ResultSet rs = ps.executeQuery();
		String tradeServiceIds = "";
		String serviceInstructionIds = "";
		//String documentNumbers = "";

		while (rs.next()) {
//			System.out.println("TRADESERVICEID .. " + rs.getString("TRADESERVICEID") + "\tSERVICEINSTRUCTIONID .. " + rs.getString("SERVICEINSTRUCTIONID"));
			tradeServiceIds = tradeServiceIds + "'" + rs.getString("TRADESERVICEID") + "'," ;
			serviceInstructionIds = serviceInstructionIds + "'" + rs.getString("SERVICEINSTRUCTIONID") + "',";
			//documentNumbers = documentNumbers + "'" + inactiveAccount + "',";		
		}
		
		if (!tradeServiceIds.equalsIgnoreCase("") && !serviceInstructionIds.equalsIgnoreCase("")) {
			tradeServiceIds = tradeServiceIds.substring(0, tradeServiceIds.length()-1);
			serviceInstructionIds = serviceInstructionIds.substring(0, serviceInstructionIds.length()-1);
			
			searchPerTable(tradeServiceIds, inactiveAccount,serviceInstructionIds);	
		} 
		
		ps.close();
		
		
		
		//documentNumbers = documentNumbers.substring(0, documentNumbers.length()-1);
		

		
		
		for (String docNum : withDocNum) {
			
			String selectScipt2 = "SELECT * FROM " + docNum + " WHERE (UCASE(DOCUMENTNUMBER) IN (" + inactiveAccount + ") OR UCASE(DOCUMENTNUMBER) IN (" +
					inactiveAccount.replace("-", "") + ")) AND (TRADESERVICEID IS NULL OR TRADESERVICEID = '')";
			PreparedStatement ps2 = tfsConn.prepareStatement(selectScipt2);
//			System.out.println(selectScipt2);
			ResultSet rs2 = ps2.executeQuery();
			
			copyToTempDb(rs2, docNum);
			ps2.close();
		}
		
		
		selectScript = "SELECT * FROM REVINFO WHERE REV IN (SELECT REV_ID FROM TRADEPRODUCT_AUDIT WHERE UCASE(DOCUMENTNUMBER) IN (" + 
				inactiveAccount + "))";
		PreparedStatement ps3 = tfsConn.prepareStatement(selectScript);
//		System.out.println(selectScript);
		rs = ps3.executeQuery();
		
		copyToTempDb(rs, "REVINFO");
		
		ps3.close();				
		for (String tradeProduct : tradeProductTables) {
			selectScript = "SELECT * FROM " + tradeProduct + " WHERE UCASE(DOCUMENTNUMBER) IN (" + inactiveAccount + ")";
			
			PreparedStatement ps4 = tfsConn.prepareStatement(selectScript);
			rs = ps4.executeQuery();
			if (tradeProduct.equalsIgnoreCase("DAILYBALANCE")) {
				tradeProduct = "DAILYBALANCE2";
			}
			
			copyToTempDb(rs, tradeProduct);
			ps4.close();
		}
		

		
		//Thread.sleep(5000);
	}
	
	private List<String> getInactiveEa(long rundate, String date, String retention) throws Exception {
		List<String> inactiveAccounts = new ArrayList<String>();
		String selectInactiveAccounts = "SELECT DOCUMENTNUMBER FROM EXPORTADVISING WHERE LCNUMBER IN (SELECT DOCUMENTNUMBER from TFCFACCS " +
						" WHERE (LASTDATE + " + retention + ") <= " + rundate + " AND STATUS = 2 UNION ALL SELECT REPLACE(DOCUMENTNUMBER,'-','') " + 
						" from TFCFACCS	WHERE (LASTDATE + " + retention + ") <= " + rundate + " AND STATUS = 2)	OR EXPIRYDATE + " + purgingRetention + " YEAR <= '" + 
						date + "' ";

		PreparedStatement ps = tfsConn.prepareStatement(selectInactiveAccounts);
		System.out.println("Script ... " + selectInactiveAccounts);
		ResultSet rs = ps.executeQuery();
		
		while(rs.next()) {
			inactiveAccounts.add(rs.getString(1));
		}
	
		ps.close();
		
		
		return inactiveAccounts;
	}

	private void searchPerTable(String tradeServiceId, String docNum, String serviceInstructionId) throws Exception {
		try {
			for (String withTradeServiceId : withTradeServiceIds) {
				String reference = "TRADESERVICEID";
				
				if(withTradeServiceId.equalsIgnoreCase("ATTACHMENT")) {
					reference = "ATTACHMENTID";
				} 
				
				String selectScipt = "SELECT * FROM " + withTradeServiceId + " WHERE " + reference + " IN (" + tradeServiceId + ") ";
												
				if(withTradeServiceId.equalsIgnoreCase("ATTACHMENT")) {
					selectScipt = "SELECT * FROM " + withTradeServiceId + " WHERE ATTACHMENTID IN (" + tradeServiceId + ") ";
				} else if(withTradeServiceId.equalsIgnoreCase("ETSPAYMENTDETAIL")) {
					selectScipt = "SELECT * FROM " + withTradeServiceId + " WHERE PAYMENTID IN (" +
							"SELECT ID FROM ETSPAYMENT WHERE TRADESERVICEID IN (" + tradeServiceId + ")) ";				
				} else if(withTradeServiceId.equalsIgnoreCase("PAYMENTDETAIL")) {
					selectScipt = "SELECT * FROM " + withTradeServiceId + " WHERE PAYMENTID IN (" +
							"SELECT ID FROM PAYMENT WHERE TRADESERVICEID IN (" + tradeServiceId + ")) ";		
				} else if (withTradeServiceId.equalsIgnoreCase("SERVICEINSTRUCTION")) {
					reference = "SERVICEINSTRUCTIONID";
					selectScipt = "SELECT * FROM " + withTradeServiceId + " WHERE SERVICEINSTRUCTIONID IN (" + serviceInstructionId + ") ";
				} else if (withTradeServiceId.equalsIgnoreCase("REMARKS")) {					
					selectScipt = "SELECT * FROM " + withTradeServiceId + " WHERE REMARK_ID IN (" + tradeServiceId + ") " + 
							" OR REMARK_ID IN (" + serviceInstructionId + ") ";
				} else if (withTradeServiceId.equalsIgnoreCase("ROUTES") || withTradeServiceId.equalsIgnoreCase("ROUTINGINFORMATION")) {
					selectScipt = "SELECT * FROM " + withTradeServiceId + " WHERE ROUTINGINFORMATIONID IN (" + tradeServiceId + ") " + 
							" OR ROUTINGINFORMATIONID IN (" + serviceInstructionId + ") ";
				} else if (withTradeServiceId.equalsIgnoreCase("TASK")) {
					selectScipt = "SELECT * FROM " + withTradeServiceId + " WHERE TASKREFERENCENUMBER IN (" + tradeServiceId + ") " +  
							" OR TASKREFERENCENUMBER IN (" + serviceInstructionId + ") ";
				} 
				
				PreparedStatement ps = tfsConn.prepareStatement(selectScipt);
//				System.out.println(selectScipt);
				ResultSet rs = ps.executeQuery();
				
				copyToTempDb(rs, withTradeServiceId);

				ps.close();
								
			} 
			//Thread.sleep(5000);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} 
	}
	
	private void copyToTempDb(ResultSet rs, String tablename) throws Exception {
		try {
			while(rs.next()) {
				ResultSetMetaData rsmd = rs.getMetaData();
	
				
				List<String> columnContents = new ArrayList<String>();
				columnContents.clear();
				StringBuilder sb =new StringBuilder("");
						
				for(int i=1; i<=rsmd.getColumnCount(); i++) {	
					if (rs.getString(i)!=null) {
						String str = rs.getString(i).replace("'", "''");					
						sb.append("'" + str + "'");	
					} else if (rs.getString(i)==null) {
						sb.append("NULL");
					}
					
					if (i!=rsmd.getColumnCount()){
						sb.append(",");
					}	
				}
				
				if (tablename.equalsIgnoreCase("REVINFO") || otherTables.contains(tablename)) {
					String select = "SELECT * FROM " + tablename + " WHERE " + rsmd.getColumnName(1) + " = '" + rs.getString(1) + "'" ;
					PreparedStatement ps = tempConn.prepareStatement(select);
					ResultSet rs2 = ps.executeQuery();
					
					
					if(!rs2.next()) {
						String insertAccount = "INSERT INTO " + tablename + " VALUES(" + sb.toString() + ")" ;
						
//						System.out.println(insertAccount);
						PreparedStatement ps2 = tempConn.prepareStatement(insertAccount);
						ps2.executeUpdate();
						
						tempConn.commit();
						ps2.close();
					}
					
					ps.close();
				} else {

					String insertAccount = "INSERT INTO " + tablename + " VALUES(" + sb.toString() + ")" ;
					
//					System.out.println(insertAccount);
					PreparedStatement ps2 = tempConn.prepareStatement(insertAccount);
					ps2.executeUpdate();
					
					tempConn.commit();
					ps2.close();
				}		
				
			}

			//Thread.sleep(5000);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	private void purgeLc(List<String> tables,List<String> tables2,String docNum, String prefix) throws Exception {
		for (String tableName : tables) {
			String selectScript = "SELECT * FROM " + tableName + " WHERE DOCUMENTNUMBER = '" + docNum + "' ";
			PreparedStatement ps = tfsConn.prepareStatement(selectScript);
//			System.out.println(selectScript);
			ResultSet rs = ps.executeQuery();
			
			copyToTempDb(rs, tableName);
			ps.close();
			
			
		}

		for (String tableName : tables2) {
						
			String selectScript = "SELECT * FROM " + tableName + " WHERE ID IN (SELECT ID FROM " + prefix + tableName + 
					"_AUDIT WHERE DOCUMENTNUMBER = '" + docNum + "') UNION ALL SELECT * FROM " + tableName + 
					" WHERE ID NOT IN (SELECT ID FROM " + prefix + tableName + "_AUDIT WHERE DOCUMENTNUMBER = '" + docNum + 
					"') AND DOCUMENTNUMBER = '" + docNum + "' ";
			
			if (tableName.contains("AUDIT")) {
				selectScript = "SELECT * FROM " + tableName + " WHERE ID IN (SELECT ID FROM " + prefix + tableName + 
						" WHERE DOCUMENTNUMBER = '" + docNum + "')";
			}
			
			if (tableName.equalsIgnoreCase("LCNEGOTIATIONDISCREPANCY")) {
				selectScript = "SELECT * FROM " + tableName + " WHERE ICNUMBER IN (SELECT ICNUMBER FROM LETTEROFCREDIT_" + tableName + 
						"_AUDIT WHERE DOCUMENTNUMBER = '" + docNum + "')";
			} else if (tableName.equalsIgnoreCase("LCNEGOTIATIONDISCREPANCY_AUDIT")) {
				selectScript = "SELECT * FROM " + tableName + " WHERE ICNUMBER IN (SELECT ICNUMBER FROM LETTEROFCREDIT_" + tableName + 
						" WHERE DOCUMENTNUMBER = '" + docNum + "')";
			}
			
			
			PreparedStatement ps = tfsConn.prepareStatement(selectScript);
//			System.out.println(selectScript);
			ResultSet rs = ps.executeQuery();

			copyToTempDb(rs, tableName);
			ps.close();
		}

		//Thread.sleep(5000);
	}
	
	private void purgeNonLc(List<String> tables,String docNum) throws Exception {
		
		for (String tableName : tables) {
			
			String selectScript = "SELECT * FROM " + tableName + " WHERE DOCUMENTNUMBER = '" + docNum + "' ";
			if (tableName.contains("CDTPAYMENT") || tableName.contains("EMAIL_NOTIF")) {
				selectScript = "SELECT * FROM " + tableName + " WHERE IEDIEIRDNO = '" + docNum + "' ";
			}
			PreparedStatement ps = tfsConn.prepareStatement(selectScript);
//			System.out.println(selectScript);
			ResultSet rs = ps.executeQuery();
			
			copyToTempDb(rs, tableName);
			ps.close();

		}
	}
	
	private void purgeIndemnity(List<String> tables,String docNum) throws Exception {
		String inactiveAccount = "";
		
		for (String tableName : tables) {
			String selectScript = "SELECT INDEMNITYNUMBER FROM " + tableName + " WHERE REFERENCENUMBER = '" + docNum + "' ";
			PreparedStatement psIndemnity = tfsConn.prepareStatement(selectScript);
//			System.out.println(selectScript);
			ResultSet rs = psIndemnity.executeQuery();
			
			
			if(!tableName.equalsIgnoreCase("INDEMNITY_AUDIT")) {
				
				while(rs.next()) {
					inactiveAccount = inactiveAccount + "'" + rs.getString(1) + "',";
					
				}	
				
				if (!inactiveAccount.equalsIgnoreCase("")) {
					inactiveAccount = inactiveAccount.substring(0,inactiveAccount.length()-1);					
					insertToCommonTables(inactiveAccount);
				}
				
				psIndemnity.close();				

				selectScript = "SELECT * FROM " + tableName + " WHERE REFERENCENUMBER = '" + docNum + "' ";
				
				PreparedStatement selectIndemnity = tfsConn.prepareStatement(selectScript);
//				System.out.println(selectScript);
				rs = selectIndemnity.executeQuery();

				copyToTempDb(rs, tableName);
				
				selectIndemnity.close();
				
				
			} else {
				if (!inactiveAccount.equalsIgnoreCase("")) {
					selectScript = "SELECT * FROM " + tableName + " WHERE INDEMNITYNUMBER IN (" + inactiveAccount + ") ";	
					selectScript = "SELECT * FROM " + tableName + " WHERE INDEMNITYNUMBER IN (" + inactiveAccount + ") ";					
					
					PreparedStatement selectIndemnity = tfsConn.prepareStatement(selectScript);
//						System.out.println(selectScript);
					rs = selectIndemnity.executeQuery();

					copyToTempDb(rs, tableName);
					
					selectIndemnity.close();
				}
				
				psIndemnity.close();
			}
			
			if (!psIndemnity.isClosed()) {
				psIndemnity.close();
			}

		}
		
	}
	
	private void purgeOthersByAccount(List<String> tables,String docNum) throws Exception {
		for (String tableName : tables) {
			String selectScript = "SELECT * FROM " + tableName + " WHERE UCASE(DOCUMENTNUMBER) = '" + docNum + "' ";
			
			if (tableName.equalsIgnoreCase("ACCOUNTSPAYABLE") || tableName.equalsIgnoreCase("ACCOUNTSRECEIVABLE") || 
					tableName.equalsIgnoreCase("MARGINALDEPOSIT")) {
				selectScript = "SELECT * FROM " + tableName + " WHERE UCASE(SETTLEMENTACCOUNTNUMBER) = '" + docNum +
						"' OR UCASE(REPLACE(SETTLEMENTACCOUNTNUMBER,'-','')) = '" + docNum + "'";
			} else if (tableName.equalsIgnoreCase("MARGINALDEPOSITACTIVITY")) {
				selectScript = "SELECT * FROM " + tableName + " WHERE MARGINALDEPOSITID IN (SELECT ID FROM MARGINALDEPOSIT WHERE " + 
						" UCASE(SETTLEMENTACCOUNTNUMBER) = '" + docNum + "' OR UCASE(REPLACE(SETTLEMENTACCOUNTNUMBER,'-','')) = '" + docNum + "')";		
			} else if (tableName.equalsIgnoreCase("ACCOUNTSPAYABLEACTIVITY")) {
				selectScript = "SELECT * FROM " + tableName + " WHERE ACCOUNTSPAYABLEID IN (SELECT ID FROM ACCOUNTSPAYABLE WHERE " + 
						" UCASE(SETTLEMENTACCOUNTNUMBER) = '" + docNum + "' OR UCASE(REPLACE(SETTLEMENTACCOUNTNUMBER,'-','')) = '" + docNum + "')";		
			} else if (tableName.equalsIgnoreCase("ACCOUNTSRECEIVABLEACTIVITY")) {
				selectScript = "SELECT * FROM " + tableName + " WHERE ACCOUNTSRECEIVABLEID IN (SELECT ID FROM ACCOUNTSRECEIVABLE WHERE " + 
						" UCASE(SETTLEMENTACCOUNTNUMBER) = '" + docNum + "' OR UCASE(REPLACE(SETTLEMENTACCOUNTNUMBER,'-','')) = '" + docNum + "')";		
			}
			
			PreparedStatement ps = tfsConn.prepareStatement(selectScript);
//			System.out.println(selectScript);
			ResultSet rs = ps.executeQuery();
			
			copyToTempDb(rs, tableName);
			ps.close();

		}
	}
	
	private void purgeOthersByDate(List<String> tables,String runDate, String retention) throws Exception {
		for (String tableName : tables) {
			String selectScript = "SELECT * FROM " + tableName + " WHERE UCASE(DOCUMENTNUMBER) NOT IN (SELECT DOCUMENTNUMBER FROM TRADEPRODUCT " + 
						" UNION ALL SELECT REPLACE(DOCUMENTNUMBER,'-','') FROM TRADEPRODUCT) AND PROCESSDATE + " + retention + " <= '" + runDate + "'";
			
			
			if (tableName.equalsIgnoreCase("ACCOUNTSPAYABLE") || tableName.equalsIgnoreCase("ACCOUNTSRECEIVABLE") || 
					tableName.equalsIgnoreCase("MARGINALDEPOSIT")) {
				selectScript = "SELECT * FROM " + tableName + " WHERE UCASE(SETTLEMENTACCOUNTNUMBER) NOT IN (SELECT DOCUMENTNUMBER FROM TRADEPRODUCT " + 
						" UNION ALL SELECT REPLACE(DOCUMENTNUMBER,'-','') FROM TRADEPRODUCT) AND MODIFIEDDATE + " + retention + " <= '" + runDate + "'";
				
			} else if (tableName.equalsIgnoreCase("ACCOUNTSPAYABLEACTIVITY")) {
				selectScript = "SELECT * FROM " + tableName + " WHERE ACCOUNTSPAYABLEID IN (SELECT ID FROM ACCOUNTSPAYABLE" + 
						" WHERE UCASE(SETTLEMENTACCOUNTNUMBER) NOT IN (SELECT DOCUMENTNUMBER FROM TRADEPRODUCT  UNION ALL SELECT REPLACE(DOCUMENTNUMBER,'-','')" +
						" FROM TRADEPRODUCT) AND MODIFIEDDATE + " + retention + " <= '" + runDate + "')";
				
			} else if (tableName.equalsIgnoreCase("ACCOUNTSRECEIVABLEACTIVITY") ) {
				selectScript = "SELECT * FROM " + tableName + " WHERE ACCOUNTSRECEIVABLEID IN (SELECT ID FROM ACCOUNTSRECEIVABLE" + 
						" WHERE UCASE(SETTLEMENTACCOUNTNUMBER) NOT IN (SELECT DOCUMENTNUMBER FROM TRADEPRODUCT  UNION ALL SELECT REPLACE(DOCUMENTNUMBER,'-','')" +
						" FROM TRADEPRODUCT) AND MODIFIEDDATE + " + retention + " <= '" + runDate + "')";

			} else if (tableName.contains("CORRESCHARGE")) {
				selectScript = "SELECT * FROM " + tableName + " WHERE UCASE(DOCUMENTNUMBER) NOT IN (SELECT DOCUMENTNUMBER FROM TRADEPRODUCT " + 
						" UNION ALL SELECT REPLACE(DOCUMENTNUMBER,'-','') FROM TRADEPRODUCT) AND CREATEDDATE + " + retention + " <= '" + runDate + "'";
			
			} else if (tableName.equalsIgnoreCase("MARGINALDEPOSITACTIVITY") ) {
				selectScript = "SELECT * FROM " + tableName + " WHERE MARGINALDEPOSITID IN (SELECT ID FROM MARGINALDEPOSIT" + 
						" WHERE UCASE(SETTLEMENTACCOUNTNUMBER) NOT IN (SELECT DOCUMENTNUMBER FROM TRADEPRODUCT  UNION ALL SELECT REPLACE(DOCUMENTNUMBER,'-','')" +
						" FROM TRADEPRODUCT) AND MODIFIEDDATE + " + retention + " <= '" + runDate + "')";

			} 
			PreparedStatement ps = tfsConn.prepareStatement(selectScript);
//			System.out.println(selectScript);
			ResultSet rs = ps.executeQuery();
			
			copyToTempDb(rs, tableName);
			ps.close();
			
			//Thread.sleep(5000);
		}
		
	}
	
	public void executePhase2() throws Exception {
		try {
			System.out.println("PHASE 2");
			initializeList();
			initializeConnection();
			deleteTfcfaccs();
			Collections.reverse(withTradeServiceIds);
			getAccountsToDelete(withTradeServiceIds);
			getAccountsToDelete(oaTables);
			getAccountsToDelete(drTables);
			getAccountsToDelete(dpTables);
			getAccountsToDelete(daTables);
			getAccountsToDelete(bgTables);
			Collections.reverse(cdtTables);
			getAccountsToDelete(cdtTables);
			getAccountsToDelete(lcTables);
			getAccountsToDelete(lc2Tables);
			getAccountsToDelete(ebTables);
			getAccountsToDelete(eb2Tables);
			getAccountsToDelete(eaTables);
			Collections.reverse(otherTables);
			getAccountsToDelete(otherTables);
			getAccountsToDelete(tradeProductTables);
			getAccountsToDelete(Arrays.asList("REVINFO"));
			
						
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			closeConnection();
		}
		
		
		
	}
	
	public void getAccountsToDelete(List<String> tables) throws Exception {
		try {
			for(String tableName : tables) {
				String reference = "ID";
				
				if(masterTables.contains(tableName)) {
					reference = "DOCUMENTNUMBER";
				} else if(tableName.contains("INDEMNITY")) {
					reference = "INDEMNITYNUMBER";
				} else if(tableName.contains("LCNEGOTIATIONDISCREPANCY")) {
					reference = "ICNUMBER";
				} else if(tableName.equalsIgnoreCase("CDTPAYMENTREQUEST")) {
					reference = "IEDIEIRDNO";
				} else if(tableName.equalsIgnoreCase("TRADESERVICE")) {
					reference = "TRADESERVICEID";
				} else if(tableName.equalsIgnoreCase("ROUTINGINFORMATION")) {
					reference = "ROUTINGINFORMATIONID";
				} else if(tableName.equalsIgnoreCase("SERVICEINSTRUCTION")) {
					reference = "SERVICEINSTRUCTIONID";
				} else if(tableName.equalsIgnoreCase("TASK")) {
					reference = "TASKREFERENCENUMBER";
				} else if(tableName.equalsIgnoreCase("TRANSACTIONLOG")) {
					reference = "TXNREFERENCENUMBER";
				}
				
				String selectScript = "SELECT " + reference + " FROM " + tableName + " ORDER BY " + reference;
				
				if(tableName.equalsIgnoreCase("DAILYBALANCE")) {
					selectScript = "SELECT " + reference + " FROM DAILYBALANCE2 " + " ORDER BY " + reference;
				}
				
				//Start ER 20161001-001
				if(tableName.equalsIgnoreCase("REVINFO")) {
					reference = "REV";
					selectScript = "SELECT REV FROM REVINFO WHERE REV NOT IN (SELECT REV_ID FROM SEC_EMPLOYEE_AUDIT) ORDER BY REV";
					
				} 
				//End ER 20161001-001
				
//				System.out.println(selectScript);
				PreparedStatement ps = tempConn.prepareStatement(selectScript);
				ResultSet rs = ps.executeQuery();
				
				
				deleteAccount(rs, tableName, reference);
				
				ps.close();

				//Thread.sleep(5000);
			}		
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	}
	
	
	public void deleteAccount(ResultSet rs, String tablename, String reference) throws Exception {
		try {
			int x = 0;
			String refValue = "";
			long counter = 1;
			boolean isNotEmpty = false;
			System.out.println("Deleting from table ... " + tablename );
			
			while(rs.next()) {
				
				if(!isNotEmpty) {
					isNotEmpty = true;
				}
				
				if(counter%1000 != 0) {
					refValue = refValue + "'" + rs.getString(1) + "',";
					counter++;
				} else {	
					refValue = refValue + "'" + rs.getString(1) + "'";
					counter++;
					String deleteScript = "DELETE FROM " + tablename + " WHERE " + reference + " IN (" + refValue + ")" ;
//					System.out.println(deleteScript);
					PreparedStatement ps2 = tfsConn.prepareStatement(deleteScript);
					ps2.executeUpdate();
					
					tfsConn.commit();
					ps2.close();
					
					refValue = "";
					isNotEmpty = false;
				}
				
			}
			counter = counter - 1 ;
			
			if ((counter%1000 != 0) && isNotEmpty) {
				String deleteScript = "DELETE FROM " + tablename + " WHERE " + reference + " IN (" + refValue.substring(0, refValue.length()-1) + ")" ;
//				System.out.println(deleteScript);
				PreparedStatement ps2 = tfsConn.prepareStatement(deleteScript);
				ps2.executeUpdate();
				
				tfsConn.commit();
				ps2.close();
				
				refValue = "";
			}
			
		} catch (Exception e) {
			Thread.sleep(5000);
			e.printStackTrace();
			throw e;
		} 
	}
	
	public void updateAccountPurgingDetail(String runDate,String adhocDate) throws SQLException {
		try {
			initializeConnection();
			String insertScript = "INSERT INTO ACCOUNTPURGINGDETAIL VALUES ('" + runDate + "','" + adhocDate + "'," +
					"'" + this.purgingRetention + " " + this.purgingTerm + "','"+ this.purgingRetention2 +
					" " + this.purgingTerm2 + "')";
			PreparedStatement prepareInsert = tfsConn.prepareStatement(insertScript);
			prepareInsert.executeUpdate();
			
			tfsConn.commit();
			prepareInsert.close();
			
			
			PreparedStatement prepareInsert2 = tempConn.prepareStatement(insertScript);
			prepareInsert2.executeUpdate();
			
			tempConn.commit();
			prepareInsert2.close();

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new SQLException();
		} finally {
			closeConnection();
		}	
		
	}
	
	public void deleteTfcfaccs() throws Exception {
		try {
			initializeConnection();
			
			String deleteScript = "DELETE FROM TFCFACCS";
			PreparedStatement ps = tfsConn.prepareStatement(deleteScript);
			ps.executeUpdate();
			
			tfsConn.commit();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} 
		
	}
	
	private void closeConnection() throws SQLException{
		if(tfsConn != null){
			tfsConn.close();
			tfsConn=null;
		}
		
		if(tempConn != null){
			tempConn.close();
			tempConn=null;
		}
	}
	
	private void createTables(List<String> tableList) throws Exception {
		
			for(String table : tableList) {
				
				DatabaseMetaData dbMeta = tfsConn.getMetaData();
				List<Map<String,String>> fkey = new ArrayList<Map<String,String>>();
				fkey.clear();
				List<Map<String,String>> pkey = new ArrayList<Map<String,String>>();
				pkey.clear();
				List<Map<String,String>> tableDetails = new ArrayList<Map<String,String>>();
				tableDetails.clear();
				ResultSet rsFkey = dbMeta.getImportedKeys(null, this.prodSchema, table);
	
				while(rsFkey.next()){
					Map<String,String> tempMap = new HashMap<String, String>();
					tempMap.put("schemaName", rsFkey.getString(2));
					tempMap.put("tableName", rsFkey.getString(3));
					tempMap.put("columnName", rsFkey.getString(4));
					tempMap.put("FschemaName", rsFkey.getString(6));
					tempMap.put("FtableName", rsFkey.getString(7));
					tempMap.put("FcolumnName", rsFkey.getString(8));
					tempMap.put("FKName", rsFkey.getString(12));
					
					fkey.add(tempMap);
					
				}			
				
				ResultSet rsPkey =   dbMeta.getPrimaryKeys(null, this.prodSchema, table);
				
				while(rsPkey.next()){
					Map<String,String> tempMap = new HashMap<String, String>();		

					tempMap.put("tableName", rsPkey.getString(3));
					tempMap.put("columnName", rsPkey.getString(4));
					tempMap.put("PKName", rsPkey.getString(6));
					
					pkey.add(tempMap);
				}			
			
				String select = "SELECT * FROM " + table + " FETCH FIRST 10 ROWS ONLY ";
				PreparedStatement selectPs = tfsConn.prepareStatement(select);
				
				ResultSet rs = selectPs.executeQuery();
				
				ResultSetMetaData rsMeta = rs.getMetaData();
				
				if (table.equalsIgnoreCase("DAILYBALANCE")) {
					table = "DAILYBALANCE2";
				}
				String createTable = "Create table " + table + " ( ";
				
				for(int counter = 1 ;counter <= rsMeta.getColumnCount(); counter++ ) {	
					String lenght = rsMeta.getScale(counter)!=0 ? ("(" + rsMeta.getPrecision(counter) + "," + 
							rsMeta.getScale(counter) + ")") : ("(" + rsMeta.getPrecision(counter) + ")");
					String isNull = rsMeta.isNullable(counter)==0 ? "NOT NULL" : "";
					String type = rsMeta.getColumnTypeName(counter);
					
					
					createTable = createTable + rsMeta.getColumnName(counter) + " ";
					if(type.equalsIgnoreCase("BIGINT") || type.equalsIgnoreCase("SMALLINT") || type.equalsIgnoreCase("INTEGER") ||
							type.equalsIgnoreCase("TIMESTAMP") || type.equalsIgnoreCase("DATE")){
						createTable = createTable + type + " " + isNull + " ,";
					} else {
						createTable = createTable + type + lenght + " " + isNull + " ,";	
					}
					
								
				}

				System.out.println("--" + table);
				createTable = createTable.substring(0, createTable.length()-1);
				
				
				if(!pkey.isEmpty()) {
					createTable = createTable + ",CONSTRAINT ";
					String primaryKey = "";
					String uniqueKey = "";
					
					for(Map<String,String> columnName : pkey) {
						primaryKey = primaryKey + columnName.get("columnName") + ",";
						uniqueKey = columnName.get("PKName");
					}
					primaryKey = primaryKey.substring(0, primaryKey.length()-1);
					
					createTable = createTable + uniqueKey + " PRIMARY KEY " + "(" + primaryKey + ") " ;
				}
				
				
				createTable = createTable + ")";
				
				System.out.println(createTable);
				PreparedStatement createPs = tempConn.prepareStatement(createTable);
				createPs.executeUpdate();
				
				tempConn.commit();
				createPs.close();
				
				if(!pkey.isEmpty()) {
					createTable = " CREATE UNIQUE INDEX ";
					String primaryKey = "";
					String uniqueKey = "";
					
					for(Map<String,String> columnName : pkey) {
						primaryKey = primaryKey + columnName.get("columnName") + ", ";
						uniqueKey = columnName.get("PKName");
					}
					primaryKey = primaryKey.substring(0, primaryKey.length()-2);
					
					createTable = createTable + uniqueKey + " ON " + table + "(" + primaryKey + ") " ;
					System.out.println(createTable);
					PreparedStatement createPs3 = tempConn.prepareStatement(createTable);
					createPs3.executeUpdate();
					
					tempConn.commit();
					createPs3.close();
				}

				if(!fkey.isEmpty()) {
					String tempParent = "";
					List<String> tempList = new ArrayList<String>();
					Map<String,String> tempMap = new HashMap<String, String>();
					
					for(Map<String,String> column : fkey) {
						
						if(tempParent.equalsIgnoreCase(column.get("tableName"))) {
							tempMap.put(tempParent, tempMap.get(tempParent) + "," + column.get("columnName"));		
							tempMap.put(tempParent + "FKEY", tempMap.get(tempParent + "FKEY") + "," + column.get("FcolumnName"));
						} else {
							tempMap.put(column.get("tableName"), column.get("columnName"));
							tempMap.put(column.get("tableName") + "FKEY", column.get("FcolumnName"));
							tempMap.put(column.get("tableName") + "UNIKEY", column.get("FKName"));
						}					
						
						tempParent = column.get("tableName") ;
						
						if(!tempList.contains(tempParent)) {
							tempList.add(tempParent);
						}
					}
					
					for(String parent : tempList) {
						createTable = " ALTER TABLE " + table + " ADD CONSTRAINT " + tempMap.get(parent + "UNIKEY") + " FOREIGN KEY (" + tempMap.get(parent + "FKEY") + ") REFERENCES " +
								parent + " (" + tempMap.get(parent) + ") ";				
						System.out.println(createTable);

						PreparedStatement createPs2 = tempConn.prepareStatement(createTable);
						createPs2.executeUpdate();
						
						tempConn.commit();
						createPs2.close();
					}
					
									
				}
				selectPs.close();
				createTable = createTable + ")";
	
				
				tempConn.commit();
				
				createPs.close();
			}
		
		
	}
	
	public void test() throws SQLException {
		initializeConnection();
		
		DatabaseMetaData db = tfsConn.getMetaData();
		ResultSet rs = db.getTables(null, this.prodSchema, null, null);
		
		while(rs.next()) {
			System.out.println(rs.getString(3));
		}
		
	}

	public void duplicateDB() throws Exception {
		try {
			initializeConnection();
			initializeList();
			
			createTables(Arrays.asList("REVINFO"));
			createTables(tradeProductTables);
			createTables(withTradeServiceIds);
			createTables(lcTables);
			createTables(lc2Tables);
			createTables(oaTables);
			createTables(drTables);
			createTables(dpTables);
			createTables(daTables);
			createTables(bgTables);
			createTables(cdtTables);
			createTables(ebTables);
			createTables(eb2Tables);
			createTables(eaTables);
			createTables(otherTables);
			createOtherTables();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			closeConnection();
		}
		
		
	}
	
	public void dropTables() throws Exception {
		try {
			initializeConnection();
			List<String> existingTables = new ArrayList<String>();
			List<String> checkingTables = new ArrayList<String>();
			
			DatabaseMetaData dbMeta = tfsConn.getMetaData();
			ResultSet rsTables = dbMeta.getTables(null, this.prodSchema, null, null);
			
			while (rsTables.next()) {
				existingTables.add(rsTables.getString(3));
			}

			DatabaseMetaData tempMeta = tempConn.getMetaData();
			ResultSet rsTables2 = tempMeta.getTables(null, this.prodSchema, null, null);
			
			while (rsTables2.next()) {
				checkingTables.add(rsTables2.getString(3));
			}
			
			
			for(String table : existingTables) {
				if(checkingTables.contains(table) && !table.equalsIgnoreCase("DMNONLC_TOP30_VW") && 
						!table.equalsIgnoreCase("FXNONLC_TOP30_VW") && !table.contains("QRTZ_") && !table.equalsIgnoreCase("VALUEHOLDER")) {
					String drop = "DROP TABLE " + table;
					System.out.println(drop);
					PreparedStatement dropPs = tempConn.prepareStatement(drop);
					dropPs.executeUpdate();
					tempConn.commit();
					dropPs.close();
				}			
			}
			
			if(checkingTables.contains("DAILYBALANCE2")) {
				String drop = "DROP TABLE DAILYBALANCE2" ;
				System.out.println(drop);
				PreparedStatement dropPs = tempConn.prepareStatement(drop);
				dropPs.executeUpdate();
				tempConn.commit();
				dropPs.close();
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			closeConnection();
		}
				
	}
		
	public void createOtherTables() throws Exception {
		List<String> existingTables = new ArrayList<String>();
		List<String> tablesToCreate = new ArrayList<String>();
		
		DatabaseMetaData tempMeta = tempConn.getMetaData();
		ResultSet rsTables = tempMeta.getTables(null, this.prodSchema, null, null);
		
		while (rsTables.next()) {
			existingTables.add(rsTables.getString(3));
		}
		
		DatabaseMetaData prodMeta = tfsConn.getMetaData();
		ResultSet rsTables2 = prodMeta.getTables(null, this.prodSchema, null, null);
		tablesToCreate.clear();		
		tablesToCreate.addAll(parent);
		
		while (rsTables2.next()) {
			String tablename = rsTables2.getString(3);
			
			if(!existingTables.contains(tablename) && !parent.contains(tablename) && !tablename.equalsIgnoreCase("DAILYBALANCE")) {
				tablesToCreate.add(tablename);
			}
		}
		
		createTables(tablesToCreate);
		
		for(String table : tablesToCreate){			
			if (!table.contains("QRTZ_") && !table.equalsIgnoreCase("VALUEHOLDER")) {
				System.out.println("Insert values to : " + table);
				
				if (table.equalsIgnoreCase("SEC_EMPLOYEE_AUDIT")) {
					String select = "SELECT * FROM REVINFO WHERE REV IN (SELECT REV_ID FROM " + table + ")";
					
					PreparedStatement selectPs0 = tfsConn.prepareStatement(select);
					ResultSet rs = selectPs0.executeQuery();
					
					copyToTempDb(rs, "REVINFO");
					selectPs0.close();
				} 

				String select = "SELECT * FROM " + table;
				PreparedStatement selectPs = tfsConn.prepareStatement(select);
				ResultSet rs = selectPs.executeQuery();
				
				copyToTempDb(rs, table);
				selectPs.close();
			}
		}
				
	}
}
