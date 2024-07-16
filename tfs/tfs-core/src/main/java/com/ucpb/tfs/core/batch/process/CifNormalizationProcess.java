package com.ucpb.tfs.core.batch.process;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.ucpb.tfs.batch.cif.CibsDetailsTable;
import com.ucpb.tfs.batch.cif.CibsMasterTable;
import com.ucpb.tfs.batch.report.dw.CifNormalizationModel;
import com.ucpb.tfs.batch.report.dw.dao.SilverlakeLocalDao;
import com.ucpb.tfs.batch.report.dw.dao.TradeProductDao;
import com.ucpb.tfs.batch.util.DbUtil;

/**PROLOGUE:
 * 	(revision)
	SCR/ER Number: ER# 20140909-038
	SCR/ER Description: CIF Normalization Not Working in TFS
	[Revised by:] Jesse James Joson
	[Date Created:] 08/05/2016
	Program [Revision] Details: The CIF Normalization was redesigned, since not all tables are normalized.
	PROJECT: CORE
	MEMBER TYPE  : JAVA
	Project Name: CifNormalizationProcess
 *
 */

/**PROLOGUE:
 * 	(revision)
	SCR/ER Number: ER# 20140909-038
	SCR/ER Description: CIF Normalization Not Working in TFS
	[Revised by:] Jesse James Joson
	[Date Revised:] 08/20/2016
	Program [Revision] Details: Address error on extract normalized CIFs, change schema used for table CFNMSTA.
	PROJECT: CORE
	MEMBER TYPE  : JAVA
	Project Name: CifNormalizationProcess
 *
 */

/**PROLOGUE:
 * 	(revision)
	SCR/ER Number: ER# 20140909-038
	SCR/ER Description: CIF Normalization Not Working in TFS
	[Revised by:] Jesse James Joson
	[Date Revised:] 08/25/2016
	Program [Revision] Details: Address error migrated Data where records are complete in TRADEPRODUCT Table but null on some fields in TRADESERVICE table.
	PROJECT: CORE
	MEMBER TYPE  : JAVA
	Project Name: CifNormalizationProcess
 *
 */

/**
 * PROLOGUE
 *  Description: Replaced usage of silverlakeDao into silverlakeLocalDao
 *  [Revised by:] Cedrick C. Nungay
 *  [Date revised:] 01/25/2024
*/

public class CifNormalizationProcess {
	private SilverlakeLocalDao silverlakeLocalDao;
	private TradeProductDao tradeProductDao;
	private final DataSource tfsDataSource;
	private final DataSource sibsDataSource;
	
	private Connection tfsConn = null;
	private Connection sibsConn = null;
	
	private final String deleteTfsCfnmstaQuery = "DELETE FROM CIFNORM_CFNMSTA ";
	private final String deleteTfsLnaalogQuery = "DELETE FROM CIFNORM_LNAALOG";
	private final String selectTfsCfnmstaQuery = "SELECT MAX(CFNDA7) AS DATE FROM CIFNORM_CFNMSTA WHERE ISUPDATED IS NULL";
	private final String selectTfsLnaalogQuery = "SELECT MAX(CHGDT7) AS DATE FROM CIFNORM_LNAALOG WHERE ISUPDATED IS NULL";

	private final String selectTfsCfnmstaQuery2 = "SELECT MAX(CFNDA7) AS DATE FROM CIFNORM_CFNMSTA";
	private final String selectTfsLnaalogQuery2 = "SELECT MAX(CHGDT7) AS DATE FROM CIFNORM_LNAALOG";
	
	private final String tfsInsertCfnmstaQuery = "INSERT INTO CIFNORM_CFNMSTA " +
			"(CFNCIF,CFNBRN,CFNSNM,CFNUID,CFNWID,CFNTIM,CFNDT7,CFNDT6,CFNCFO,CFNUSR," +
			"CFNWDA,CFNDA7,CFNDA6,CFNTME,CFNAPR) " +
			"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private final String tfsInsertLnaalogQuery = "INSERT INTO CIFNORM_LNAALOG " +
			"(FACREF,OAANO,OFCODE,OFSEQ,OCFIN,OMAANO,OMFCDE,OMFSEQ,NAANO,NFCODE,NFSEQ," +
            "NMAANO,NMFCDE,NMFSEQ,NCIFN,CHGDT6,CHGDT7,CHGTME) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private final String tfsSelectCfnmstaQuery = "SELECT CFNCIF,CFNCFO,CFNSNM,CFNDA7 FROM CIFNORM_CFNMSTA WHERE ISUPDATED IS NULL ORDER BY CFNDA7,CFNTME,CFNCIF ASC ";
	private final String tfsSelectLnaalogQuery = "SELECT OAANO,OFCODE,OFSEQ,OCFIN,NAANO,NFCODE,NFSEQ,NCIFN,CHGDT7,FACREF FROM CIFNORM_LNAALOG WHERE ISUPDATED IS NULL ORDER BY CHGDT7,CHGTME,NCIFN ASC";
	
	private final List<String> cifNumberColumns = Arrays.asList("IMPORTERCIFNUMBER","APPLICANTCIFNUMBER","CIFNUMBER",
			"CIFNO","CIF_NO","CUSTOMER_NUMBER","MAINCIFNUMBER"); 
	private final List<String> paymentTables = Arrays.asList("ETSPAYMENTDETAIL","PAYMENTDETAIL");	
	private final List<String> exportTables = Arrays.asList("EXPORTBILLS","EXPORTBILLS_AUDIT");	
	private Boolean repeat = true;
	
	
	public CifNormalizationProcess(DataSource tfsDataSource, DataSource sibsDataSource){
		this.tfsDataSource=tfsDataSource;
		this.sibsDataSource=sibsDataSource;
	}
	
	public void setSilverlakeLocalDao(SilverlakeLocalDao silverlakeLocalDao){
		this.silverlakeLocalDao=silverlakeLocalDao;
	}
	
	public void setTradeProductDao(TradeProductDao tradeProductDao){
		this.tradeProductDao=tradeProductDao;
	}
	
	private void initializeConnection() throws SQLException{
		if(tfsConn != null){
			tfsConn.close();
			tfsConn=null;
		}
		tfsConn=tfsDataSource.getConnection();
	}
	
	private void initializeSibs() throws SQLException{
		if(sibsConn != null){
			sibsConn.close();
			sibsConn=null;
		}
		sibsConn=sibsDataSource.getConnection();
	}
	
	public void execute(long appDate) throws Exception{
		try {
			if (tableChecking()) {
				copySibsTable(appDate);
			}		
			
			updateCifnumbers();			
		} catch(SQLException ex){

			ex.printStackTrace();
			tfsConn.close();
			Thread.sleep(5000);	
			
			if (repeat) {
				repeat = false;
				execute(appDate);
			} else {
				throw ex;
			}			
		} catch (Exception e) {			
			e.printStackTrace();
			tfsConn.close();
			Thread.sleep(5000);		
			throw e;
			
		} finally {
			tfsConn.close();
			Thread.sleep(5000);		
		}
	}
	
	private long getMaxDate(String tableName) throws SQLException {
		
		// Start: to delete CFNMSTA and LNAALOG TFS table
		initializeConnection();
		long maxDate= 0;
		
		PreparedStatement selectTfsCfnmstaQueryPs = tfsConn.prepareStatement(selectTfsCfnmstaQuery);
		PreparedStatement selectTfsLnaalogQueryPs = tfsConn.prepareStatement(selectTfsLnaalogQuery);
		PreparedStatement selectTfsCfnmstaQueryPs2 = tfsConn.prepareStatement(selectTfsCfnmstaQuery2);
		PreparedStatement selectTfsLnaalogQueryPs2 = tfsConn.prepareStatement(selectTfsLnaalogQuery2);
		
		ResultSet listOfDate=null;
		
		if (tableName.equalsIgnoreCase("selectTfsCfnmstaQuery")) {
			listOfDate = selectTfsCfnmstaQueryPs.executeQuery();
		} else if (tableName.equalsIgnoreCase("selectTfsLnaalogQuery")) {
			listOfDate = selectTfsLnaalogQueryPs.executeQuery();
		} else if (tableName.equalsIgnoreCase("selectTfsCfnmstaQuery2")) {
			listOfDate = selectTfsCfnmstaQueryPs2.executeQuery();
		} else if (tableName.equalsIgnoreCase("selectTfsLnaalogQuery2")) {
			listOfDate = selectTfsLnaalogQueryPs2.executeQuery();
		}
		
		while (listOfDate.next()) {	
			maxDate = listOfDate.getLong("DATE");
		}
		
		System.out.println("Date: " + maxDate);
		
		selectTfsCfnmstaQueryPs.close();
		selectTfsLnaalogQueryPs.close();
		
		return maxDate;

	}
	
	private void copySibsTable(long appDate) throws SQLException{
		
			initializeConnection();
			
			// Start: Copy in SIBS Table
			System.out.println("Select records from Cfnmsta_SIBS");
			List<CibsMasterTable> recordsCfnmsta=silverlakeLocalDao.getCibsMasterTable(getMaxDate("selectTfsCfnmstaQuery"), appDate);
			PreparedStatement deleteTfsCfnmstaPs=tfsConn.prepareStatement(deleteTfsCfnmstaQuery);
			if (getMaxDate("selectTfsCfnmstaQuery")==0) {
				
				recordsCfnmsta=silverlakeLocalDao.getCibsMasterTable(getMaxDate("selectTfsCfnmstaQuery2"),appDate);			
				if (recordsCfnmsta.size()>0) {
					System.out.println("Deleting copy of CFNMSTA on TFS table");
					deleteTfsCfnmstaPs.executeUpdate();
					tfsConn.commit();
				}
				
			}
			
			tfsConn.commit();
			
			PreparedStatement tfsInsertCfnmstaPs=tfsConn.prepareStatement(tfsInsertCfnmstaQuery);
			System.out.println("Insert records to CIFNORM_CFNMSTA");	
			for(CibsMasterTable record:recordsCfnmsta){
				//System.out.println("New CIF: " + record.getCFNCIF() + "\tOld CIF: " + record.getCFNCFO() + "\tDated: " + record.getCFNDA7());
				tfsInsertCfnmstaPs.setString(1, record.getCFNCIF());
				tfsInsertCfnmstaPs.setBigDecimal(2, record.getCFNBRN());
				tfsInsertCfnmstaPs.setString(3, record.getCFNSNM());
				tfsInsertCfnmstaPs.setString(4, record.getCFNUID());
				tfsInsertCfnmstaPs.setString(5, record.getCFNWID());
				tfsInsertCfnmstaPs.setBigDecimal(6, record.getCFNTIM());
				tfsInsertCfnmstaPs.setBigDecimal(7, record.getCFNDT7());
				tfsInsertCfnmstaPs.setBigDecimal(8, record.getCFNDT6());
				tfsInsertCfnmstaPs.setString(9, record.getCFNCFO());
				tfsInsertCfnmstaPs.setString(10, record.getCFNUSR());
				tfsInsertCfnmstaPs.setString(11, record.getCFNWDA());
				tfsInsertCfnmstaPs.setBigDecimal(12, record.getCFNDA7());
				tfsInsertCfnmstaPs.setBigDecimal(13, record.getCFNDA6());
				tfsInsertCfnmstaPs.setBigDecimal(14, record.getCFNTME());
				tfsInsertCfnmstaPs.setString(15, record.getCFNAPR());
				tfsInsertCfnmstaPs.executeUpdate();
			}
			tfsConn.commit();		
			tfsInsertCfnmstaPs.close();
			
			List<CibsDetailsTable> recordsLnaalog= silverlakeLocalDao.getCibsDetailsTable(getMaxDate("selectTfsLnaalogQuery"),appDate);
			PreparedStatement deleteTfsLnaalogPs=tfsConn.prepareStatement(deleteTfsLnaalogQuery);
			if (getMaxDate("selectTfsLnaalogQuery")==0) {
				
				recordsLnaalog=silverlakeLocalDao.getCibsDetailsTable(getMaxDate("selectTfsLnaalogQuery2"),appDate);			
				if (recordsLnaalog.size()>0) {
					System.out.println("Deleting copy of CFNMSTA on TFS table");
					deleteTfsLnaalogPs.executeUpdate();
				}
				
			}
			
			tfsConn.commit();
			
			System.out.println("Insert records to CIFNORM_LNAALOG");	
			PreparedStatement tfsInsertLnaalogPs=tfsConn.prepareStatement(tfsInsertLnaalogQuery);
			for(CibsDetailsTable record:recordsLnaalog){
//				System.out.println("Dated: " + record.getCHGDT7()  + "\tOld CIF: " + record.getOCFIN() + "\tNew CIF: "+ record.getNCIFN() + 
//						"\tOld MainCIF: " + record.getOAANO() + "\tNew MainCIF: " + record.getNAANO() + "\tOld FacType: " + record.getOFCODE() +
//						"\tNew FacType: " + record.getNFCODE() + "\tOld FacID: " + record.getOFSEQ() + "\tNew FacID: " + record.getNFSEQ());
				tfsInsertLnaalogPs.setString(1, record.getFACREF());
				tfsInsertLnaalogPs.setString(2, record.getOAANO());
				tfsInsertLnaalogPs.setString(3, record.getOFCODE());
				tfsInsertLnaalogPs.setBigDecimal(4, record.getOFSEQ());
				tfsInsertLnaalogPs.setString(5, record.getOCFIN());
				tfsInsertLnaalogPs.setString(6, record.getOMAANO());
				tfsInsertLnaalogPs.setString(7, record.getOMFCDE());
				tfsInsertLnaalogPs.setBigDecimal(8, record.getOMFSEQ());
				tfsInsertLnaalogPs.setString(9, record.getNAANO());
				tfsInsertLnaalogPs.setString(10, record.getNFCODE());
				tfsInsertLnaalogPs.setBigDecimal(11, record.getNFSEQ());
				tfsInsertLnaalogPs.setString(12, record.getNMAANO());
				tfsInsertLnaalogPs.setString(13, record.getNMFCDE());
				tfsInsertLnaalogPs.setBigDecimal(14, record.getNMFSEQ());
				tfsInsertLnaalogPs.setString(15, record.getNCIFN());
				tfsInsertLnaalogPs.setBigDecimal(16, record.getCHGDT6());
				tfsInsertLnaalogPs.setBigDecimal(17, record.getCHGDT7());
				tfsInsertLnaalogPs.setBigDecimal(18, record.getCHGTME());
				tfsInsertLnaalogPs.executeUpdate();
			}

			tfsConn.commit();
			
			System.out.println("SIBS Table already copied.");
			
	
			DbUtil.closeQuietly(tfsConn);
		
				
	}
	
	private void updateCifnumbers() throws IOException, SQLException, InterruptedException{
			PrintWriter writer = null;
	    	File file = new File("/opt/tfs/INTERFACE_FILES/CIFNormalization.txt");
			file.createNewFile();
	    	
	    	writer = new PrintWriter(new FileWriter(file));
	    	
	    	StringBuilder sb = new StringBuilder();		
			sb.append((char)13);				
			sb.append((char)10);				
			String crlf = sb.toString();
	    	
			writer.append(printHeader());
			writer.print(crlf);
			writer.flush();
			
			Map<String, String> cifMap = new HashMap<String, String>();
			
			initializeConnection();
			
			PreparedStatement getTfsLnaalogPs=tfsConn.prepareStatement(tfsSelectLnaalogQuery);
			
			ResultSet tfsLnaalogRecords = getTfsLnaalogPs.executeQuery();
			
			System.out.println("CIF to Update: ");
			

			while(tfsLnaalogRecords.next()){
				cifMap.put("oldCif", tfsLnaalogRecords.getString("OCFIN").trim().toUpperCase());
				cifMap.put("newCif", tfsLnaalogRecords.getString("NCIFN").trim().toUpperCase());
				cifMap.put("oldMainCif", tfsLnaalogRecords.getString("OAANO").trim().toUpperCase());
				cifMap.put("newMainCif", tfsLnaalogRecords.getString("NAANO").trim().toUpperCase());
				cifMap.put("oldFacType", tfsLnaalogRecords.getString("OFCODE").trim().toUpperCase());
				cifMap.put("newFacType", tfsLnaalogRecords.getString("NFCODE").trim().toUpperCase());
				cifMap.put("oldFacId", tfsLnaalogRecords.getString("OFSEQ").trim());
				cifMap.put("newFacId", tfsLnaalogRecords.getString("NFSEQ").trim());
				cifMap.put("facRef", tfsLnaalogRecords.getString("FACREF").trim().toUpperCase());
								
				//System.out.println("Date: \t" + tfsLnaalogRecords.getString("CHGDT7") + "\tFacility Referen:\t" + cifMap.get("facRef"));
				/*System.out.println("OLD CIF: " + cifMap.get("oldCif") + "\tNew CIF: " + cifMap.get("newCif"));
				System.out.println("OLD MAIN CIF: " + cifMap.get("oldMainCif") + "\tNew MAIN CIF: " + cifMap.get("newMainCif") );
				System.out.println("OLD Faility type: " + cifMap.get("oldFacType") + "\tNew Faility type: " + cifMap.get("newFacType"));
				System.out.println("OLD Faility ID: " + cifMap.get("oldFacId") + "\tNew Faility ID: " + cifMap.get("newFacId"));
				*/
				updateNonLc(cifMap, writer, crlf, tfsConn);
				
				updateLc(cifMap, writer, crlf, tfsConn);
				
				updateExportBills(cifMap, writer, crlf, tfsConn);
								
				//System.out.println(">>Updating CIFNORM_LNAALOG...");
				String updateLnaalog = "UPDATE CIFNORM_LNAALOG SET ISUPDATED = 'Y' WHERE " +
						" OCFIN='" + tfsLnaalogRecords.getString("OCFIN") + "' AND NCIFN='" + tfsLnaalogRecords.getString("NCIFN") +
						"' AND OAANO='" + tfsLnaalogRecords.getString("OAANO") + "' AND NAANO='" + tfsLnaalogRecords.getString("NAANO") +
						"' AND OFCODE='" + tfsLnaalogRecords.getString("OFCODE") + "' AND NFCODE='" + tfsLnaalogRecords.getString("NFCODE") +
						"' AND OFSEQ='" + tfsLnaalogRecords.getString("OFSEQ") + "' AND NFSEQ='" + tfsLnaalogRecords.getString("NFSEQ") +
						"' AND FACREF='" + tfsLnaalogRecords.getString("FACREF") + "' AND CHGDT7='" + tfsLnaalogRecords.getString("CHGDT7") + "' ";
				PreparedStatement updateLnaalogPs=tfsConn.prepareStatement(updateLnaalog);
				//System.out.println(updateLnaalog);
				//System.out.println(updateLnaalogPs.executeUpdate());
				updateLnaalogPs.executeUpdate();
				tfsConn.commit();	
				
				updateLnaalogPs.close();
				Thread.sleep(5000);	
				
			}			
			
			PreparedStatement getTfsCfnmstaPs=tfsConn.prepareStatement(tfsSelectCfnmstaQuery);
									
			ResultSet tfsCfnmstaRecords = getTfsCfnmstaPs.executeQuery();
						
			System.out.println("CIF to Update: ");
			cifMap = new HashMap<String, String>();
			
			while(tfsCfnmstaRecords.next()){
				cifMap.put("oldCif", tfsCfnmstaRecords.getString("CFNCFO").trim().toUpperCase());
				cifMap.put("newCif", tfsCfnmstaRecords.getString("CFNCIF").trim().toUpperCase());
												
				//System.out.println("\t>>>>>OLDCIF: " + tfsCfnmstaRecords.getString("CFNCFO") + "\tNEWCIF: " + tfsCfnmstaRecords.getString("CFNCIF") + "\tDATE: " + tfsCfnmstaRecords.getString("CFNDA7"));
				
				for (String columnName: cifNumberColumns) {
					Map<String,List<Map<String,String>>> tableMapping = createListofTable(columnName);
					
					for (Map<String,String> record: tableMapping.get(columnName)) {
						String selectAffectedAccounts = "SELECT " + record.get("key") + " as UNIQUEIDENTIFIER FROM " + record.get("table") +
								" WHERE " + columnName + " = '" + tfsCfnmstaRecords.getString("CFNCFO") + "'";
						//System.out.println(selectAffectedAccounts);
						PreparedStatement getAccount = tfsConn.prepareStatement(selectAffectedAccounts);
						
						ResultSet accounts = getAccount.executeQuery();
						
						while (accounts.next()){
							//System.out.println(accounts.getString("UNIQUEIDENTIFIER"));
							Map<String,String> tempMap = new HashMap<String,String>();
							tempMap.put("TABLE", record.get("table"));
							tempMap.put("UNIQUEIDENTIFIER", (record.get("key") + " : " + accounts.getString("UNIQUEIDENTIFIER")));
							tempMap.put("OLDCIF", cifMap.get("oldCif"));
							tempMap.put("NEWCIF",cifMap.get("newCif"));
							writer.append(printContent(tempMap));
							writer.print(crlf);
							writer.flush();
							
							if (record.get("table").equalsIgnoreCase("TRADESERVICE")) {
								String tradeServiceId =  accounts.getString("UNIQUEIDENTIFIER");
								updateCifOnTradeService(tradeServiceId, cifMap, writer, crlf, columnName);
								
							}
						}
						
						String updateAffectedAccountsQuery = "UPDATE " + record.get("table") + " SET " + columnName + 
								" = '" + tfsCfnmstaRecords.getString("CFNCIF").toUpperCase() + "' WHERE " + columnName + " = '" + tfsCfnmstaRecords.getString("CFNCFO").toUpperCase() + "'" ;
																	
						PreparedStatement updateAffectedAccountsPs = tfsConn.prepareStatement(updateAffectedAccountsQuery);
						updateAffectedAccountsPs.executeUpdate();
						tfsConn.commit();						
						
						getAccount.close();
						updateAffectedAccountsPs.close();
						
					}
					
				}
				
				//System.out.println(">>Updating CIFNORM_CFNMSTA...");
				String updateCfnmsta = "UPDATE CIFNORM_CFNMSTA SET ISUPDATED = 'Y' WHERE " +
						"CFNCIF='" + tfsCfnmstaRecords.getString("CFNCIF") + "' AND CFNCFO='" + tfsCfnmstaRecords.getString("CFNCFO") +
						"' AND CFNDA7='" + tfsCfnmstaRecords.getString("CFNDA7") + "' " ;
				PreparedStatement updateCfnmstaPs=tfsConn.prepareStatement(updateCfnmsta);
				//System.out.println(updateCfnmsta);
				//System.out.println(updateCfnmstaPs.executeUpdate());
				updateCfnmstaPs.executeUpdate();
				tfsConn.commit();						
				
				updateCfnmstaPs.close();

				Thread.sleep(5000);	
			}
			
			
			writer.flush();
			writer.close();
		
	}
	
	private Map<String,List<Map<String,String>>> createListofTable(String columnName) {
				
		List<String> withImporterCifNumber = Arrays.asList("DOCUMENTAGAINSTPAYMENT_AUDIT", "DOCUMENTAGAINSTPAYMENT", "DOCUMENTAGAINSTACCEPTANCE_AUDIT",
				"DOCUMENTAGAINSTACCEPTANCE","DIRECTREMITTANCE","DIRECTREMITTANCE_AUDIT","OPENACCOUNT_AUDIT","OPENACCOUNT","LETTEROFCREDIT","LETTEROFCREDIT_AUDIT");
		List<String> withApplicantCifNumber = Arrays.asList("LETTEROFCREDIT","LETTEROFCREDIT_AUDIT");
		List<String> withCifNumber = Arrays.asList("ACCOUNTSRECEIVABLE","ACCOUNTSPAYABLE","MARGINALDEPOSIT","REBATE",
				"TRADEPRODUCT","TRADEPRODUCT_AUDIT","TRADESERVICE");
		List<String> withCifNo = Arrays.asList("CDTPAYMENTREQUEST","REFPAS5CLIENT");
		List<String> withCifNo2 = Arrays.asList("REF_CASA_ACCOUNT","REF_TFCIFNOS","REF_TFCLNT");//CBS_CIF_NO
		List<String> withCbsCifNo = Arrays.asList("CBS_CIF_NO");
		List<String> withCifNumber2 = Arrays.asList("CIF_NUMBER");
		List<String> withMainCifNumber = Arrays.asList("TRADEPRODUCT","TRADEPRODUCT_AUDIT","TRADESERVICE");
		
		
		List<Map<String,String>> tempList = new ArrayList<Map<String,String>>();
		Map<String,List<Map<String,String>>> tableMapping = new HashMap<String,List<Map<String,String>>>();
		
		List<String> tempTableList=null;
		Map<String,String> keyMapping = new HashMap<String,String>();
		
		if (columnName.equalsIgnoreCase("IMPORTERCIFNUMBER") || columnName.equalsIgnoreCase("APPLICANTCIFNUMBER")) {
			tempTableList = (columnName.equalsIgnoreCase("IMPORTERCIFNUMBER") ? withImporterCifNumber : withApplicantCifNumber) ;
			for (String tableName: tempTableList) {
				keyMapping.put("table", tableName);
				if (tableName.endsWith("AUDIT")) {
					keyMapping.put("key", "REV_ID");
				} else {
					keyMapping.put("key", "DOCUMENTNUMBER");
				}
				tempList.add(keyMapping);
				keyMapping = new HashMap<String,String>();
			}
			tableMapping.put(columnName, tempList);
			//System.out.println(tableMapping);
			
		} else if(columnName.equalsIgnoreCase("CIFNUMBER")) {
			for (String tableName: withCifNumber) {
				keyMapping.put("table", tableName);
				keyMapping.put("key", "ID");
				if (tableName.equalsIgnoreCase("TRADEPRODUCT") || tableName.equalsIgnoreCase("TRADEPRODUCT_AUDIT") || 
						tableName.equalsIgnoreCase("TRADESERVICE")) {
					keyMapping.put("key", (tableName.equalsIgnoreCase("TRADEPRODUCT") ? "DOCUMENTNUMBER" : "REV_ID"));	
					keyMapping.put("key", (tableName.equalsIgnoreCase("TRADESERVICE") ? "TRADESERVICEID" : keyMapping.get("key")));
				}
				tempList.add(keyMapping);
				keyMapping = new HashMap<String,String>();
			}
			tableMapping.put(columnName, tempList);
			//System.out.println(tableMapping);
			
		} else if(columnName.equalsIgnoreCase("CIFNO") || columnName.equalsIgnoreCase("CIF_NO")) {
			tempTableList = (columnName.equalsIgnoreCase("CIFNO") ? withCifNo : withCifNo2);
			for (String tableName: tempTableList) {
				keyMapping.put("table", tableName);
				if (tableName.equalsIgnoreCase("CDTPAYMENTREQUEST")) {
					keyMapping.put("key", "IEDIEIRDNO");
				} else if (tableName.equalsIgnoreCase("REFPAS5CLIENT")) {
					keyMapping.put("key", "AABREFCODE");
				} else if (tableName.equalsIgnoreCase("REF_CASA_ACCOUNT")) {
					keyMapping.put("key", "ID");
				} else if (tableName.equalsIgnoreCase("REF_TFCIFNOS")) {
					keyMapping.put("key", "CIF_NO");
				} else if (tableName.equalsIgnoreCase("REF_TFCLNT")) {
					keyMapping.put("key", "CL_NO");
				}
				tempList.add(keyMapping);
				keyMapping = new HashMap<String,String>();
			}
			tableMapping.put(columnName, tempList);
			//System.out.println(tableMapping);
			
		} else if(columnName.equalsIgnoreCase("CBS_CIF_NO") || columnName.equalsIgnoreCase("CIF_NUMBER")) {			
			tempTableList = (columnName.equalsIgnoreCase("CBS_CIF_NO")) ? withCbsCifNo : withCifNumber2;
			for (String tableName: tempTableList) {
				keyMapping.put("table", tableName);
				if (tableName.equalsIgnoreCase("CBS_CIF_NO")) {
					keyMapping.put("key", "ID");
				} else if (tableName.equalsIgnoreCase("CIF_NUMBER")) {
					keyMapping.put("key", "CIF_NUMBER");
				} 
				tempList.add(keyMapping);
				keyMapping = new HashMap<String,String>();
			}
			tableMapping.put(columnName, tempList);
			//System.out.println(tableMapping);
			
		}  else if(columnName.equalsIgnoreCase("CUSTOMER_NUMBER")) {			
			keyMapping.put("table", "CUSTOMERACCOUNT");
			keyMapping.put("key", "ID");
			
			tempList.add(keyMapping);
			keyMapping = new HashMap<String,String>();
			
			tableMapping.put(columnName, tempList);
			//System.out.println(tableMapping);
			
		} else if(columnName.equalsIgnoreCase("MAINCIFNUMBER")) {	
			for (String tableName: withMainCifNumber) {
				keyMapping.put("table", tableName);
				keyMapping.put("key", (tableName.equalsIgnoreCase("TRADEPRODUCT") ? "DOCUMENTNUMBER" : "REV_ID"));				
				keyMapping.put("key", (tableName.equalsIgnoreCase("TRADESERVICE") ? "TRADESERVICEID" : keyMapping.get("key")));
				
				tempList.add(keyMapping);
				keyMapping = new HashMap<String,String>();
			}
			tableMapping.put(columnName, tempList);
			//System.out.println(tableMapping);
		} 
		
		return tableMapping;
		
	}
	
	private String printHeader( ) {
		StringBuilder sb = new StringBuilder("");
		sb.append("REPORT DATE|");
		sb.append("TABLE|");
		sb.append("UNIQUE IDENTIFIER|");
		sb.append("OLD CIF|");
		sb.append("OLD MAINCIF|");
		sb.append("OLD FACILITY TYPE|");
		sb.append("OLD FACILITY ID|");
		sb.append("NEW CIF|");
		sb.append("NEW MAINCIF|");
		sb.append("NEW FACILITY TYPE|");
		sb.append("NEW FACILITY ID|");
		sb.append("FACILITY REFERENCE|");
		return sb.toString();
	}
	
	private String printContent(Map<String,String> result) {
		StringBuilder sb = new StringBuilder("");
		List<String> contents = Arrays.asList("TABLE","UNIQUEIDENTIFIER","OLDCIF","OLDMAINCIF","OLDFACILITYTYPE",
				"OLDFACILITYID","NEWCIF","NEWMAINCIF","NEWFACILITYTYPE","NEWFACILITYID","NEWFACREF");
		sb.append(new SimpleDateFormat("MM/dd/yyyy").format(new Date()) + "|");
		for (String content: contents) {
			if (result.containsKey(content)){
				sb.append(result.get(content) + "|");
			} else {
				sb.append("|");
			}
		}
		return sb.toString();
	}
	
	private void updateNonLc(Map<String, String> cifMap, PrintWriter writer, String crlf, Connection tfsConn) throws SQLException, IOException{
				
		Map<String,String> tempMap = new HashMap<String,String>();
		
		String selectNonLcFromTp = "SELECT DOCUMENTNUMBER AS DOCUMENTNUMBER FROM TRADEPRODUCT " + "WHERE CIFNUMBER = '" +
				cifMap.get("oldCif") + "' AND MAINCIFNUMBER = '" + cifMap.get("oldMainCif") + "' AND PRODUCTTYPE IN ('DA','DP','DR','OA')";

		PreparedStatement psSelectNonLcFromTp = tfsConn.prepareStatement(selectNonLcFromTp);
		ResultSet documentNumbers = psSelectNonLcFromTp.executeQuery();
		
		while(documentNumbers.next()){
			//System.out.println(documentNumbers.getString("DOCUMENTNUMBER"));
			String selectNonLcFromTs = "SELECT TRADESERVICEID AS TRADESERVICEID FROM TRADESERVICE " +
					"WHERE TRADEPRODUCTNUMBER = '" + documentNumbers.getString("DOCUMENTNUMBER") + "' ";
			PreparedStatement psSelectNonLcFromTs = tfsConn.prepareStatement(selectNonLcFromTs);
			ResultSet tradeServiceIds = psSelectNonLcFromTs.executeQuery();
			
			while(tradeServiceIds.next()){
				String tradeServiceId = tradeServiceIds.getString("TRADESERVICEID");
				updateTradeService(cifMap, writer, crlf, tradeServiceId);
	            updatePaymentTables(cifMap, writer, crlf, tradeServiceId);
	            
			}
			
			String updateNonLcFromTp = "UPDATE TRADEPRODUCT SET CIFNUMBER = '" + cifMap.get("newCif") + "' , MAINCIFNUMBER = '" + 
					cifMap.get("newMainCif") + "' WHERE DOCUMENTNUMBER = '" + documentNumbers.getString("DOCUMENTNUMBER") + "' " ;
			PreparedStatement psUpdateNonLcFromTp = tfsConn.prepareStatement(updateNonLcFromTp);
			psUpdateNonLcFromTp.executeUpdate();
			tfsConn.commit();
            
            tempMap = new HashMap<String,String>();
			tempMap.put("TABLE", "TRADEPRODUCT");
			tempMap.put("UNIQUEIDENTIFIER", "DOCUMENTNUMBER : " + documentNumbers.getString("DOCUMENTNUMBER"));
			tempMap.put("OLDCIF", cifMap.get("oldCif"));
			tempMap.put("OLDMAINCIF", cifMap.get("oldMainCif"));
			tempMap.put("NEWCIF", cifMap.get("newCif"));
			tempMap.put("NEWMAINCIF", cifMap.get("newMainCif"));
			writer.append(printContent(tempMap));
			writer.print(crlf);
			writer.flush();
            				
			psUpdateNonLcFromTp.close();
		}
		
		psSelectNonLcFromTp.close();
		
		
		String selectNonLcFromTpa = "SELECT REV_ID AS REV_ID FROM TRADEPRODUCT_AUDIT " + "WHERE CIFNUMBER = '" +
				cifMap.get("oldCif") + "' AND MAINCIFNUMBER = '" + cifMap.get("oldMainCif") + "' AND PRODUCTTYPE IN ('DA','DP','DR','OA')";
		PreparedStatement psSelectNonLcFromTpa = tfsConn.prepareStatement(selectNonLcFromTpa);
		ResultSet revIds = psSelectNonLcFromTpa.executeQuery();
		
		while(revIds.next()) {
			tempMap = new HashMap<String,String>();
			tempMap.put("TABLE", "TRADEPRODUCT_AUDIT");
			tempMap.put("UNIQUEIDENTIFIER", "REV_ID : " + revIds.getString("REV_ID"));
			tempMap.put("OLDCIF", cifMap.get("oldCif"));
			tempMap.put("OLDMAINCIF", cifMap.get("oldMainCif"));
			tempMap.put("NEWCIF", cifMap.get("newCif"));
			tempMap.put("NEWMAINCIF", cifMap.get("newMainCif"));
			writer.append(printContent(tempMap));
			writer.print(crlf);
			writer.flush();
		}
		
		String updateNonLcFromTpa = "UPDATE TRADEPRODUCT_AUDIT SET CIFNUMBER = '" + cifMap.get("newCif") + "' , MAINCIFNUMBER = '" + 
				cifMap.get("newMainCif") + "' WHERE CIFNUMBER = '" + cifMap.get("oldCif") + "' AND MAINCIFNUMBER = '" + cifMap.get("oldMainCif") + 
				"' AND PRODUCTTYPE IN ('DA','DP','DR','OA')";
		PreparedStatement psUpdateNonLcFromTpa = tfsConn.prepareStatement(updateNonLcFromTpa);
		psUpdateNonLcFromTpa.executeUpdate();
		tfsConn.commit();
		
		psUpdateNonLcFromTpa.close();
		
		psSelectNonLcFromTpa.close();
	
	}
	
	private void updateLc(Map<String, String> cifMap, PrintWriter writer, String crlf, Connection tfsConn) throws SQLException, IOException{
		
		Map<String,String> tempMap = new HashMap<String,String>();
		
		String selectLcFromTp = "SELECT DOCUMENTNUMBER,CIFNUMBER,MAINCIFNUMBER,FACILITYID,FACILITYTYPE,FACILITYREFERENCENUMBER FROM TRADEPRODUCT " +
				"WHERE CIFNUMBER = '" + cifMap.get("oldCif") + "' AND MAINCIFNUMBER = '" + cifMap.get("oldMainCif") + "' AND PRODUCTTYPE ='LC'";

		PreparedStatement psSelectLcFromTp = tfsConn.prepareStatement(selectLcFromTp);
		ResultSet lcs = psSelectLcFromTp.executeQuery();
		
		while(lcs.next()){
			Map<String,String> lc = new HashMap<String,String>();
			lc.put("DOCUMENTNUMBER", (lcs.getString("DOCUMENTNUMBER") != null ? lcs.getString("DOCUMENTNUMBER") : ""));
			lc.put("CIFNUMBER", (lcs.getString("CIFNUMBER") != null ? lcs.getString("CIFNUMBER").trim() : ""));
			lc.put("MAINCIFNUMBER", (lcs.getString("MAINCIFNUMBER") != null ? lcs.getString("MAINCIFNUMBER").trim() : ""));
			lc.put("FACILITYID", (lcs.getString("FACILITYID") != null ? lcs.getString("FACILITYID").trim() : ""));
			lc.put("FACILITYTYPE", (lcs.getString("FACILITYTYPE") != null ? lcs.getString("FACILITYTYPE").trim() : ""));
			lc.put("FACILITYREFERENCENUMBER", (lcs.getString("FACILITYREFERENCENUMBER") != null ? lcs.getString("FACILITYREFERENCENUMBER").trim() : ""));
			
			
//			System.out.println(lc.get("DOCUMENTNUMBER") + "\t" + lc.get("CIFNUMBER") + "\t" + lc.get("MAINCIFNUMBER") + "\t" +
//					lc.get("FACILITYID") + "\t" + lc.get("FACILITYTYPE") + "\t" + lc.get("FACILITYREFERENCENUMBER"));
//			
			if (lc.get("FACILITYREFERENCENUMBER") == null || lc.get("FACILITYREFERENCENUMBER").equalsIgnoreCase("")) {
				//System.out.println(lc.get("DOCUMENTNUMBER") + " is CASH.");
				
				String selectLcFromTs = "SELECT TRADESERVICEID AS TRADESERVICEID FROM TRADESERVICE " +
						"WHERE TRADEPRODUCTNUMBER = '" + lcs.getString("DOCUMENTNUMBER") + "' ";
				PreparedStatement psSelectLcFromTs = tfsConn.prepareStatement(selectLcFromTs);
				ResultSet tradeServiceIds = psSelectLcFromTs.executeQuery();
				
				while(tradeServiceIds.next()){
					String tradeServiceId = tradeServiceIds.getString("TRADESERVICEID");
					updateTradeService(cifMap, writer, crlf, tradeServiceId);
	            	updatePaymentTables(cifMap, writer, crlf, tradeServiceId);

				}

				String updateLcFromTp = "UPDATE TRADEPRODUCT SET CIFNUMBER = '" + cifMap.get("newCif") + "' , MAINCIFNUMBER = '" + 
						cifMap.get("newMainCif") + "' WHERE DOCUMENTNUMBER = '" + lcs.getString("DOCUMENTNUMBER") + "' " ;
				PreparedStatement psUpdateLcFromTp = tfsConn.prepareStatement(updateLcFromTp);
				psUpdateLcFromTp.executeUpdate();
				tfsConn.commit();
				
	            tempMap = new HashMap<String,String>();
				tempMap.put("TABLE", "TRADEPRODUCT");
				tempMap.put("UNIQUEIDENTIFIER", "DOCUMENTNUMBER : " + lcs.getString("DOCUMENTNUMBER"));
				tempMap.put("OLDCIF", cifMap.get("oldCif"));
				tempMap.put("OLDMAINCIF", cifMap.get("oldMainCif"));
				tempMap.put("NEWCIF", cifMap.get("newCif"));
				tempMap.put("NEWMAINCIF", cifMap.get("newMainCif"));
				writer.append(printContent(tempMap));
				writer.print(crlf);
				writer.flush();
				
				psUpdateLcFromTp.close();
				
				
				psSelectLcFromTs.close();
				
	            String selectLcFromTpa = "SELECT REV_ID AS REV_ID FROM TRADEPRODUCT_AUDIT " + "WHERE CIFNUMBER = '" +
	    				cifMap.get("oldCif") + "' AND MAINCIFNUMBER = '" + cifMap.get("oldMainCif") + "' AND PRODUCTTYPE = 'LC' " + 
	            		"AND (FACILITYREFERENCENUMBER='' OR FACILITYREFERENCENUMBER is null)";
	    		PreparedStatement psSelectLcFromTpa = tfsConn.prepareStatement(selectLcFromTpa);
	    		ResultSet revIds = psSelectLcFromTpa.executeQuery();
	    		
	    		while(revIds.next()) {
	    			tempMap = new HashMap<String,String>();
	    			tempMap.put("TABLE", "TRADEPRODUCT_AUDIT");
	    			tempMap.put("UNIQUEIDENTIFIER", "REV_ID : " + revIds.getString("REV_ID"));
	    			tempMap.put("OLDCIF", cifMap.get("oldCif"));
	    			tempMap.put("OLDMAINCIF", cifMap.get("oldMainCif"));
	    			tempMap.put("NEWCIF", cifMap.get("newCif"));
	    			tempMap.put("NEWMAINCIF", cifMap.get("newMainCif"));
	    			writer.append(printContent(tempMap));
	    			writer.print(crlf);
	    			writer.flush();
	    		}
	    		
	    		String updateLcFromTpa = "UPDATE TRADEPRODUCT_AUDIT SET CIFNUMBER = '" + cifMap.get("newCif") + "' , MAINCIFNUMBER = '" + 
	    				cifMap.get("newMainCif") + "' WHERE CIFNUMBER = '" + cifMap.get("oldCif") + "' AND MAINCIFNUMBER = '" + cifMap.get("oldMainCif") + 
	    				"' AND PRODUCTTYPE = 'LC' AND (FACILITYREFERENCENUMBER='' OR FACILITYREFERENCENUMBER is null)";
	    		PreparedStatement psUpdateLcFromTpa = tfsConn.prepareStatement(updateLcFromTpa);
	    		psUpdateLcFromTpa.executeUpdate();
	    		tfsConn.commit();
	    		
	    		psUpdateLcFromTpa.close();
	    		
	    		psSelectLcFromTpa.close();
								
			} else if (lc.get("FACILITYID").equalsIgnoreCase(cifMap.get("oldFacId")) && lc.get("FACILITYREFERENCENUMBER").equalsIgnoreCase(cifMap.get("facRef")) && 
					(lc.get("FACILITYTYPE").equalsIgnoreCase(cifMap.get("oldFacType")) || cifMap.get("oldFacType").equalsIgnoreCase("FCN")) ) {
				
				//System.out.println(lc.get("DOCUMENTNUMBER") + " is NOT CASH.");
				
				String selectLcFromTs = "SELECT TRADESERVICEID, SERVICEINSTRUCTIONID, DETAILS, CIFNUMBER, MAINCIFNUMBER, FACILITYID, FACILITYTYPE FROM TRADESERVICE " +
						"WHERE TRADEPRODUCTNUMBER = '" + lcs.getString("DOCUMENTNUMBER") + "' ";
				PreparedStatement psSelectLcFromTs = tfsConn.prepareStatement(selectLcFromTs);
				ResultSet tradeServiceIds = psSelectLcFromTs.executeQuery();
				
				Map<String, String> cifDetailsMap = new HashMap<String,String>();
				cifDetailsMap.put("newCif", "\"" + cifMap.get("newCif") + "\"");
				cifDetailsMap.put("newMainCif", "\"" + cifMap.get("newMainCif") + "\"");
				cifDetailsMap.put("newFacId", "\"" + cifMap.get("newFacId") + "\"");
				cifDetailsMap.put("newFacType", "\"" + cifMap.get("newFacType") + "\"");
				cifDetailsMap.put("facRef", "\"" + cifMap.get("facRef") + "\"");
				
				cifDetailsMap.put("oldCif", "\"" + cifMap.get("oldCif") + "\"");
				cifDetailsMap.put("oldMainCif", "\"" + cifMap.get("oldMainCif") + "\"");
				cifDetailsMap.put("oldFacId", "\"" + cifMap.get("oldFacId") + "\"");
				cifDetailsMap.put("oldFacType", "\"" + cifMap.get("oldFacType") + "\"");
				
				while(tradeServiceIds.next()){
					
					String tradeServiceId = tradeServiceIds.getString("TRADESERVICEID");
					//System.out.println(tradeServiceId);
					List<CifNormalizationModel>	records = tradeProductDao.getTradeService(tradeServiceId);
					
					for (CifNormalizationModel record : records) {
						List<Map<String,String>> columnNames = new ArrayList<Map<String,String>>();
						Map<String,String> columnContentMap = new HashMap<String,String>();
						record.generateDetailsMap();
						
						String serviceInstructionId = (record.getServiceInstructionId() != null ? record.getServiceInstructionId().toString() : "");
			            
						if ((!returnBlankIfNull(record.getCifNumber()).equalsIgnoreCase("")) && 
								(record.getCifNumber().equalsIgnoreCase(cifMap.get("oldCif")))) {
							record.setCifNumber(cifMap.get("newCif"));
							columnContentMap= new HashMap<String,String>();
							columnContentMap.put("columnName","CIFNUMBER");
							columnContentMap.put("columnContent",record.getCifNumber());
							columnNames.add(columnContentMap);
						} 					
						if ((!returnBlankIfNull(record.getMainCifNumber()).equalsIgnoreCase("")) && 
								(record.getMainCifNumber().equalsIgnoreCase(cifMap.get("oldMainCif")))) {
							record.setMainCifNumber(cifMap.get("newMainCif"));
							columnContentMap= new HashMap<String,String>();
							columnContentMap.put("columnName","MAINCIFNUMBER");
							columnContentMap.put("columnContent",record.getMainCifNumber());
							columnNames.add(columnContentMap);
						}
						if ((!returnBlankIfNull(record.getFacilityId()).equalsIgnoreCase("")) && 
								(record.getFacilityId().equalsIgnoreCase(cifMap.get("oldFacId")))) {
							record.setFacilityId(cifMap.get("newFacId"));;
							columnContentMap= new HashMap<String,String>();
							columnContentMap.put("columnName","FACILITYID");
							columnContentMap.put("columnContent",record.getFacilityId());
							columnNames.add(columnContentMap);
						} 	
						if ((!returnBlankIfNull(record.getFacilityType()).equalsIgnoreCase("")) && 
								(record.getFacilityType().equalsIgnoreCase(cifMap.get("oldFacType")))) {
							record.setFacilityType(cifMap.get("newFacType"));
							columnContentMap= new HashMap<String,String>();
							columnContentMap.put("columnName","FACILITYTYPE");
							columnContentMap.put("columnContent",record.getFacilityType());
							columnNames.add(columnContentMap);
						} 
						
						if (record.getDetails() != null) {
							//System.out.println("Before update Details.. " + record.getDetails());
							if ((record.getDetails().containsKey("\"cifNumber\"") && !returnBlankIfNull(record.getDetails().get("\"cifNumber\"").toString()).equalsIgnoreCase("")) && 
									(record.getDetails().get("\"cifNumber\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldCif")))) {
								record.getDetails().put("\"cifNumber\"", cifDetailsMap.get("newCif"));
				            }
				            if ((record.getDetails().containsKey("\"mainCifNumber\"") && !returnBlankIfNull(record.getDetails().get("\"mainCifNumber\"").toString()).equalsIgnoreCase("")) && 
				            		(record.getDetails().get("\"mainCifNumber\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldMainCif")))) {
				            	record.getDetails().put("\"mainCifNumber\"", cifDetailsMap.get("newMainCif"));
				            }
				            
				            if ((record.getDetails().containsKey("\"facilityId\"") && !returnBlankIfNull(record.getDetails().get("\"facilityId\"").toString()).equalsIgnoreCase("")) && 
									(record.getDetails().get("\"facilityId\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldFacId")))) {
								record.getDetails().put("\"facilityId\"", cifDetailsMap.get("newFacId"));
				            }
				            if ((record.getDetails().containsKey("\"facilityType\"") && !returnBlankIfNull(record.getDetails().get("\"facilityType\"").toString()).equalsIgnoreCase("")) && 
				            		(record.getDetails().get("\"facilityType\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldFacType")))) {
				            	record.getDetails().put("\"facilityType\"", cifDetailsMap.get("newFacType"));
				            }
				            if ((record.getDetails().containsKey("\"facilityReferenceNumber\"") && !returnBlankIfNull(record.getDetails().get("\"facilityReferenceNumber\"").toString()).equalsIgnoreCase("")) && 
				            		(record.getDetails().get("\"facilityReferenceNumber\"").toString().equalsIgnoreCase(cifDetailsMap.get("facRef")))) {
				            	record.getDetails().put("\"facilityReferenceNumber\"", cifDetailsMap.get("facRef"));
				            }
				            if ((record.getDetails().containsKey("\"cifNumberFrom\"") && !returnBlankIfNull(record.getDetails().get("\"cifNumberFrom\"").toString()).equalsIgnoreCase("")) && 
				            		(record.getDetails().get("\"cifNumberFrom\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldCif")))) {
				            	record.getDetails().put("\"cifNumberFrom\"", cifDetailsMap.get("newCif"));
				            }
				            if ((record.getDetails().containsKey("\"cifNumberTo\"") && !returnBlankIfNull(record.getDetails().get("\"cifNumberTo\"").toString()).equalsIgnoreCase("")) && 
				            		(record.getDetails().get("\"cifNumberTo\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldCif")))) {
				            	record.getDetails().put("\"cifNumberTo\"", cifDetailsMap.get("newCif"));
				            }
				            if ((record.getDetails().containsKey("\"mainCifNumberFrom\"") && !returnBlankIfNull(record.getDetails().get("\"mainCifNumberFrom\"").toString()).equalsIgnoreCase("")) && 
				            		(record.getDetails().get("\"mainCifNumberFrom\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldMainCif")))) {
				            	record.getDetails().put("\"mainCifNumberFrom\"", cifDetailsMap.get("newMainCif"));
				            }
				            if ((record.getDetails().containsKey("\"mainCifNumberTo\"") && !returnBlankIfNull(record.getDetails().get("\"mainCifNumberTo\"").toString()).equalsIgnoreCase("")) && 
				            		(record.getDetails().get("\"mainCifNumberTo\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldMainCif")))) {
				            	record.getDetails().put("\"mainCifNumberTo\"", cifDetailsMap.get("newMainCif"));
				            }
				            if ((record.getDetails().containsKey("\"importerCifNumber\"") && !returnBlankIfNull(record.getDetails().get("\"importerCifNumber\"").toString()).equalsIgnoreCase("")) && 
				            		(record.getDetails().get("\"importerCifNumber\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldCif")))) {
				            	record.getDetails().put("\"importerCifNumber\"", cifDetailsMap.get("newCif"));
				            }
				            if ((record.getDetails().containsKey("\"applicantCifNumber\"") && !returnBlankIfNull(record.getDetails().get("\"applicantCifNumber\"").toString()).equalsIgnoreCase("")) && 
				            		(record.getDetails().get("\"applicantCifNumber\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldCif")))) {
				            	record.getDetails().put("\"applicantCifNumber\"", cifDetailsMap.get("newCif"));
				            }
				            
				           // System.out.println("After Update Details.. " + record.getDetails());
				            record.saveDetails(record.getDetails());
							columnContentMap= new HashMap<String,String>();
							columnContentMap.put("columnName","DETAILS");
				            columnContentMap.put("columnContent",record.getDetailsStr());
				            columnNames.add(columnContentMap);
						}	

						
						if(columnNames != null && !columnNames.isEmpty()) {
							// Put inside the if statement to execute only once there is affected TRADESERVICE record.
							tempMap = new HashMap<String,String>();
							tempMap.put("TABLE", "TRADESERVICE");
							tempMap.put("UNIQUEIDENTIFIER", "TRADESERVICEID : " + tradeServiceId);
							tempMap.put("OLDCIF", cifMap.get("oldCif"));
							tempMap.put("OLDMAINCIF", cifMap.get("oldMainCif"));
							tempMap.put("OLDFACILITYID", cifMap.get("oldFacId"));
							tempMap.put("OLDFACILITYTYPE", cifMap.get("oldFacType"));
							tempMap.put("NEWCIF", cifMap.get("newCif"));
							tempMap.put("NEWMAINCIF", cifMap.get("newMainCif"));
							tempMap.put("NEWFACILITYID", cifMap.get("newFacId"));
							tempMap.put("NEWFACILITYTYPE", cifMap.get("newFacType"));
							tempMap.put("NEWFACREF", cifMap.get("facRef"));
							writer.append(printContent(tempMap));
							writer.print(crlf);
							writer.flush();
							
							for(Map<String,String> column : columnNames){
				            	String updateSqript = updateIndividualColumn("TRADESERVICE", column.get("columnName"), column.get("columnContent"), "TRADESERVICEID", tradeServiceId);
				            	// Rename to avoid confusion.
				            	PreparedStatement updateTradeServicePs2 = tfsConn.prepareStatement(updateSqript);
				            	//System.out.println(updateTradeServicePs.executeUpdate());
				            	updateTradeServicePs2.executeUpdate();
				    			tfsConn.commit();
				    			
								updateTradeServicePs2.close();	    			
							}
							
						}
						
						if (!returnBlankIfNull(serviceInstructionId.toString()).equalsIgnoreCase("")) {
			            	//System.out.println(serviceInstructionId);
			            	
			            	List<CifNormalizationModel> serviceRecords = tradeProductDao.getServiceInstruction(serviceInstructionId);
			            	            	
			            	for (CifNormalizationModel servicerecord : serviceRecords) {
			            		servicerecord.generateServiceDetailsMap();
			            		
			            		if (servicerecord.getDetails() != null) {
			                	//	System.out.println("Before update Details.. " + servicerecord.getDetails());
									
			                		if ((servicerecord.getDetails().containsKey("\"cifNumber\"") && !returnBlankIfNull(servicerecord.getDetails().get("\"cifNumber\"").toString()).equalsIgnoreCase("")) && 
											(servicerecord.getDetails().get("\"cifNumber\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldCif")))) {
				            			servicerecord.getDetails().put("\"cifNumber\"", cifDetailsMap.get("newCif"));
						            }
						            if ((servicerecord.getDetails().containsKey("\"mainCifNumber\"") && !returnBlankIfNull(servicerecord.getDetails().get("\"mainCifNumber\"").toString()).equalsIgnoreCase("")) && 
						            		(servicerecord.getDetails().get("\"mainCifNumber\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldMainCif")))) {
						            	servicerecord.getDetails().put("\"mainCifNumber\"", cifDetailsMap.get("newMainCif"));
						            }
						            if ((servicerecord.getDetails().containsKey("\"facilityId\"") && !returnBlankIfNull(servicerecord.getDetails().get("\"facilityId\"").toString()).equalsIgnoreCase("")) && 
											(servicerecord.getDetails().get("\"facilityId\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldFacId")))) {
				            			servicerecord.getDetails().put("\"facilityId\"", cifDetailsMap.get("newFacId"));
						            }
						            if ((servicerecord.getDetails().containsKey("\"facilityType\"") && !returnBlankIfNull(servicerecord.getDetails().get("\"facilityType\"").toString()).equalsIgnoreCase("")) && 
											(servicerecord.getDetails().get("\"facilityType\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldFacType")))) {
				            			servicerecord.getDetails().put("\"facilityType\"", cifDetailsMap.get("newFacType"));
						            }
						            if ((servicerecord.getDetails().containsKey("\"facilityReferenceNumber\"") && !returnBlankIfNull(servicerecord.getDetails().get("\"facilityReferenceNumber\"").toString()).equalsIgnoreCase("")) && 
											(servicerecord.getDetails().get("\"facilityReferenceNumber\"").toString().equalsIgnoreCase(cifDetailsMap.get("facRef")))) {
				            			servicerecord.getDetails().put("\"facilityReferenceNumber\"", cifDetailsMap.get("facRef"));
						            }
						            if ((servicerecord.getDetails().containsKey("\"cifNumberFrom\"") && !returnBlankIfNull(servicerecord.getDetails().get("\"cifNumberFrom\"").toString()).equalsIgnoreCase("")) && 
						            		(servicerecord.getDetails().get("\"cifNumberFrom\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldCif")))) {
						            	servicerecord.getDetails().put("\"cifNumberFrom\"", cifDetailsMap.get("newCif"));
						            }
						            if ((servicerecord.getDetails().containsKey("\"cifNumberTo\"") && !returnBlankIfNull(servicerecord.getDetails().get("\"cifNumberTo\"").toString()).equalsIgnoreCase("")) && 
						            		(servicerecord.getDetails().get("\"cifNumberTo\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldCif")))) {
						            	servicerecord.getDetails().put("\"cifNumberTo\"", cifDetailsMap.get("newCif"));
						            }
						            if ((servicerecord.getDetails().containsKey("\"mainCifNumberFrom\"") && !returnBlankIfNull(servicerecord.getDetails().get("\"mainCifNumberFrom\"").toString()).equalsIgnoreCase("")) && 
						            		(servicerecord.getDetails().get("\"mainCifNumberFrom\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldMainCif")))) {
						            	servicerecord.getDetails().put("\"mainCifNumberFrom\"", cifDetailsMap.get("newMainCif"));
						            }
						            if ((servicerecord.getDetails().containsKey("\"mainCifNumberTo\"") && !returnBlankIfNull(servicerecord.getDetails().get("\"mainCifNumberTo\"").toString()).equalsIgnoreCase("")) && 
						            		(servicerecord.getDetails().get("\"mainCifNumberTo\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldMainCif")))) {
						            	servicerecord.getDetails().put("\"mainCifNumberTo\"", cifDetailsMap.get("newMainCif"));
						            }
						            if ((servicerecord.getDetails().containsKey("\"importerCifNumber\"") && !returnBlankIfNull(servicerecord.getDetails().get("\"importerCifNumber\"").toString()).equalsIgnoreCase("")) && 
						            		(servicerecord.getDetails().get("\"importerCifNumber\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldCif")))) {
						            	servicerecord.getDetails().put("\"importerCifNumber\"", cifDetailsMap.get("newCif"));
						            }
						            if ((servicerecord.getDetails().containsKey("\"applicantCifNumber\"") && !returnBlankIfNull(servicerecord.getDetails().get("\"applicantCifNumber\"").toString()).equalsIgnoreCase("")) && 
						            		(servicerecord.getDetails().get("\"applicantCifNumber\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldCif")))) {
						            	servicerecord.getDetails().put("\"applicantCifNumber\"", cifDetailsMap.get("newCif"));
						            }
						            
						           // System.out.println("After update Details.. " + servicerecord.getDetails());			            
						            servicerecord.saveDetails(servicerecord.getDetails());
						            						            
						            String updateServiceInstruction = "UPDATE SERVICEINSTRUCTION SET DETAILS = '" + servicerecord.getDetailsStr() + "' " +
						            		"WHERE SERVICEINSTRUCTIONID = '" + serviceInstructionId + "' ";
						            						            
									tempMap = new HashMap<String,String>();
									tempMap.put("TABLE", "SERVICEINSTRUCTION");
									tempMap.put("UNIQUEIDENTIFIER", "SERVICEINSTRUCTIONID : " + serviceInstructionId);
									tempMap.put("OLDCIF", cifMap.get("oldCif"));
									tempMap.put("OLDMAINCIF", cifMap.get("oldMainCif"));
									tempMap.put("OLDFACILITYID", cifMap.get("oldFacId"));
									tempMap.put("OLDFACILITYTYPE", cifMap.get("oldFacType"));
									tempMap.put("NEWCIF", cifMap.get("newCif"));
									tempMap.put("NEWMAINCIF", cifMap.get("newMainCif"));
									tempMap.put("NEWFACILITYID", cifMap.get("newFacId"));
									tempMap.put("NEWFACILITYTYPE", cifMap.get("newFacType"));
									tempMap.put("NEWFACREF", cifMap.get("facRef"));
									writer.append(printContent(tempMap));
									writer.print(crlf);
									writer.flush();
									
									PreparedStatement updateServiceInstructionPs = tfsConn.prepareStatement(updateServiceInstruction);
//									System.out.println(updateServiceInstruction);
//									System.out.println(updateServiceInstructionPs.executeUpdate());
									updateServiceInstructionPs.executeUpdate();
									tfsConn.commit();
									
									updateServiceInstructionPs.close();
				            	}
			            		
			            		
			       
			            	}
			            }
						
			            updatePaymentTables(cifMap, writer, crlf, tradeServiceId);
						
					}
					
				}
				
				String updateLcFromTp = "UPDATE TRADEPRODUCT SET CIFNUMBER = '" + cifMap.get("newCif") + "' , MAINCIFNUMBER = '" + 
				cifMap.get("newMainCif") + "', FACILITYID = '" + cifMap.get("newFacId") + "', FACILITYTYPE = '" + 
				cifMap.get("newFacType") + "' WHERE DOCUMENTNUMBER = '" + lcs.getString("DOCUMENTNUMBER") + "' " ;
				PreparedStatement psUpdateLcFromTp = tfsConn.prepareStatement(updateLcFromTp);
				psUpdateLcFromTp.executeUpdate();
				tfsConn.commit();
				
				tempMap = new HashMap<String,String>();
				tempMap.put("TABLE", "TRADEPRODUCT");
				tempMap.put("UNIQUEIDENTIFIER", "DOCUMENTNUMBER : " + lcs.getString("DOCUMENTNUMBER"));
				tempMap.put("OLDCIF", cifMap.get("oldCif"));
				tempMap.put("OLDMAINCIF", cifMap.get("oldMainCif"));
				tempMap.put("OLDFACILITYID", cifMap.get("oldFacId"));
				tempMap.put("OLDFACILITYTYPE", cifMap.get("oldFacType"));
				tempMap.put("NEWCIF", cifMap.get("newCif"));
				tempMap.put("NEWMAINCIF", cifMap.get("newMainCif"));
				tempMap.put("NEWFACILITYID", cifMap.get("newFacId"));
				tempMap.put("NEWFACILITYTYPE", cifMap.get("newFacType"));
				tempMap.put("NEWFACREF", cifMap.get("facRef"));
				writer.append(printContent(tempMap));
				writer.print(crlf);
				writer.flush();
				
				psUpdateLcFromTp.close();


	            String selectLcFromTpa = "SELECT REV_ID AS REV_ID FROM TRADEPRODUCT_AUDIT WHERE CIFNUMBER = '" +
	    				cifMap.get("oldCif") + "' AND MAINCIFNUMBER = '" + cifMap.get("oldMainCif") + "' AND PRODUCTTYPE = 'LC' " + 
	            		"AND FACILITYID = '" + cifMap.get("oldFacId") + "' AND FACILITYTYPE = '" + cifMap.get("oldFacType") + "' " +
	    				"AND TRIM(FACILITYREFERENCENUMBER) = '" + cifMap.get("facRef") + "'";
	            
	    		PreparedStatement psSelectLcFromTpa = tfsConn.prepareStatement(selectLcFromTpa);
	    		ResultSet revIds = psSelectLcFromTpa.executeQuery();
	    		
	    		while(revIds.next()) {
	    			tempMap = new HashMap<String,String>();
	    			tempMap.put("TABLE", "TRADEPRODUCT_AUDIT");
	    			tempMap.put("UNIQUEIDENTIFIER", "REV_ID : " + revIds.getString("REV_ID"));
	    			tempMap.put("OLDCIF", cifMap.get("oldCif"));
					tempMap.put("OLDMAINCIF", cifMap.get("oldMainCif"));
					tempMap.put("OLDFACILITYID", cifMap.get("oldFacId"));
					tempMap.put("OLDFACILITYTYPE", cifMap.get("oldFacType"));
					tempMap.put("NEWCIF", cifMap.get("newCif"));
					tempMap.put("NEWMAINCIF", cifMap.get("newMainCif"));
					tempMap.put("NEWFACILITYID", cifMap.get("newFacId"));
					tempMap.put("NEWFACILITYTYPE", cifMap.get("newFacType"));
					tempMap.put("NEWFACREF", cifMap.get("facRef"));
					writer.append(printContent(tempMap));
	    			writer.print(crlf);
	    			writer.flush();
	    		}
	    		
	    		String updateLcFromTpa ="UPDATE TRADEPRODUCT_AUDIT SET CIFNUMBER = '" + cifMap.get("newCif") + "' , MAINCIFNUMBER = '" + 
						cifMap.get("newMainCif") + "', FACILITYID = '" + cifMap.get("newFacId") + "', FACILITYTYPE = '" + 
						cifMap.get("newFacType") + "' WHERE CIFNUMBER = '" + cifMap.get("oldCif") + "' AND MAINCIFNUMBER = '" + 
						cifMap.get("oldMainCif") + "' AND PRODUCTTYPE = 'LC' AND FACILITYID = '" + cifMap.get("oldFacId") + 
						"' AND FACILITYTYPE = '" + cifMap.get("oldFacType") + "' AND TRIM(FACILITYREFERENCENUMBER) = '" + cifMap.get("facRef") + "'";
	    		PreparedStatement psUpdateLcFromTpa = tfsConn.prepareStatement(updateLcFromTpa);
	    		psUpdateLcFromTpa.executeUpdate();
	    		tfsConn.commit();
	    		
	    		psUpdateLcFromTpa.close();
	    		
	    		psSelectLcFromTpa.close();
			} else if (!lc.get("FACILITYREFERENCENUMBER").equalsIgnoreCase("")) {
				//System.out.println(lc.get("DOCUMENTNUMBER") + " is NOT CASH.");

				String selectLcFromTs = "SELECT TRADESERVICEID, SERVICEINSTRUCTIONID, DETAILS, CIFNUMBER, MAINCIFNUMBER, FACILITYID, FACILITYTYPE FROM TRADESERVICE " +
						"WHERE TRADEPRODUCTNUMBER = '" + lcs.getString("DOCUMENTNUMBER") + "' ";
				PreparedStatement psSelectLcFromTs = tfsConn.prepareStatement(selectLcFromTs);
				ResultSet tradeServiceIds = psSelectLcFromTs.executeQuery();
				
				while(tradeServiceIds.next()){
					String tradeServiceId = tradeServiceIds.getString("TRADESERVICEID");
					//System.out.println(tradeServiceId);
					updatePaymentTables(cifMap, writer, crlf, tradeServiceId);
				}
			}
			
		}
		
		psSelectLcFromTp.close();
	}
	
	private void updateExportBills(Map<String, String> cifMap, PrintWriter writer, String crlf, Connection tfsConn) throws SQLException, IOException{
		
		Map<String,String> tempMap = new HashMap<String,String>();
		
		String selectEbFromTp = "SELECT DOCUMENTNUMBER,CIFNUMBER,MAINCIFNUMBER FROM TRADEPRODUCT " +
				"WHERE CIFNUMBER = '" + cifMap.get("oldCif") + "' AND PRODUCTTYPE ='EXPORT_BILLS' AND (MAINCIFNUMBER = '" +
				cifMap.get("oldMainCif") + "' OR MAINCIFNUMBER IS NULL OR MAINCIFNUMBER='')";

		PreparedStatement psSelectEbFromTp = tfsConn.prepareStatement(selectEbFromTp);
		ResultSet exportBills = psSelectEbFromTp.executeQuery();
		
		while(exportBills.next()) {
			//System.out.println(exportBills.getString("DOCUMENTNUMBER"));  
			
			String selectEbFromTs = "SELECT TRADESERVICEID AS TRADESERVICEID FROM TRADESERVICE " +
					"WHERE TRADEPRODUCTNUMBER = '" + exportBills.getString("DOCUMENTNUMBER") + "' ";
			PreparedStatement psSelectEbFromTs = tfsConn.prepareStatement(selectEbFromTs);
			ResultSet tradeServiceIds = psSelectEbFromTs.executeQuery();
			
			while(tradeServiceIds.next()) {
				String tradeServiceId = tradeServiceIds.getString("TRADESERVICEID");
				
				updateTradeService(cifMap, writer, crlf, tradeServiceId);
				updatePaymentTables(cifMap, writer, crlf, tradeServiceId);
				
				
			}
			
			for(String tableName: exportTables) {
				String key = "";
				key = (tableName.equalsIgnoreCase("EXPORTBILLS") ? "DOCUMENTNUMBER" : "REV_ID");
				
            	String selectEbFromExportTables = "SELECT " + key + " as UNIQUEIDENTIFIER FROM " + tableName + 
            			" WHERE FACILITYTYPE='" + cifMap.get("oldFacType") + "' AND FACILTIYID='" + cifMap.get("oldFacId") + 
            			"' AND DOCUMENTNUMBER = '" + exportBills.getString("DOCUMENTNUMBER") + "' ";
		           
            	PreparedStatement psSelectEbFromExportTables = tfsConn.prepareStatement(selectEbFromExportTables);
     			ResultSet exports = psSelectEbFromExportTables.executeQuery();
            	
            	while (exports.next()) {
    				String updateAffectedAccountsQuery = "UPDATE " + tableName+ " SET FACILITYTYPE='" + cifMap.get("newFacType") + "', FACILTIYID='" + 
	            			cifMap.get("newFacId") + "' WHERE ID='" + exports.getString("UNIQUEIDENTIFIER") + "'";
					
					PreparedStatement updateAffectedAccountsPs = tfsConn.prepareStatement(updateAffectedAccountsQuery);
					updateAffectedAccountsPs.executeUpdate();
					tfsConn.commit();
					
					updateAffectedAccountsPs.close();
					
					tempMap = new HashMap<String,String>();
					tempMap.put("TABLE", tableName);
					tempMap.put("UNIQUEIDENTIFIER", key + " : " + exports.getString("UNIQUEIDENTIFIER"));
					tempMap.put("OLDFACILITYID", cifMap.get("oldFacId"));
					tempMap.put("OLDFACILITYTYPE", cifMap.get("oldFacType"));
					tempMap.put("NEWFACILITYID", cifMap.get("newFacId"));
					tempMap.put("NEWFACILITYTYPE", cifMap.get("newFacType"));
					tempMap.put("NEWFACREF", cifMap.get("facRef"));
					writer.append(printContent(tempMap));
					writer.print(crlf);
					writer.flush();
					
            	}	            	
            	psSelectEbFromExportTables.close();
            }
			
			List<Map<String,String>> columnNames = new ArrayList<Map<String,String>>();
			Map<String,String> columnContentMap = new HashMap<String,String>();
            
			columnContentMap.put("columnName","CIFNUMBER");
            columnContentMap.put("columnContent",cifMap.get("newCif"));
            columnNames.add(columnContentMap);
      
            columnContentMap= new HashMap<String,String>();
            if (!returnBlankIfNull(exportBills.getString("MAINCIFNUMBER")).equalsIgnoreCase("") && 
            		exportBills.getString("MAINCIFNUMBER").equalsIgnoreCase(cifMap.get("oldMainCif"))) {
            	columnContentMap.put("columnName","MAINCIFNUMBER");
                columnContentMap.put("columnContent",cifMap.get("newMainCif"));
                columnNames.add(columnContentMap);
            }
			
			if(columnNames != null && !columnNames.isEmpty()) {
				for(Map<String,String> column : columnNames){
	            	String updateSqript = updateIndividualColumn("TRADEPRODUCT", column.get("columnName"), column.get("columnContent"), "DOCUMENTNUMBER", exportBills.getString("DOCUMENTNUMBER"));
	            	PreparedStatement psUpdateLcFromTp = tfsConn.prepareStatement(updateSqript);
	    			psUpdateLcFromTp.executeUpdate();
	    			tfsConn.commit();
	    			
	    			psUpdateLcFromTp.close();	    			
				}
			}
			
			tempMap = new HashMap<String,String>();
			tempMap.put("TABLE", "TRADEPRODUCT");
			tempMap.put("UNIQUEIDENTIFIER", "DOCUMENTNUMBER : " + exportBills.getString("DOCUMENTNUMBER"));
			tempMap.put("OLDCIF", cifMap.get("oldCif"));
			tempMap.put("OLDMAINCIF", cifMap.get("oldMainCif"));
			tempMap.put("NEWCIF", cifMap.get("newCif"));
			tempMap.put("NEWMAINCIF", cifMap.get("newMainCif"));
			writer.append(printContent(tempMap));
			writer.print(crlf);
			writer.flush();
						
			String selectLcFromTpa = "SELECT REV_ID AS REV_ID FROM TRADEPRODUCT_AUDIT WHERE CIFNUMBER = '" +
    				cifMap.get("oldCif") + "' AND MAINCIFNUMBER = '" + cifMap.get("oldMainCif") + 
    				"' AND DOCUMENTNUMBER='" + exportBills.getString("DOCUMENTNUMBER") + "' " ;
            
    		PreparedStatement psSelectLcFromTpa = tfsConn.prepareStatement(selectLcFromTpa);
    		ResultSet revIds = psSelectLcFromTpa.executeQuery();    		
    		
    		while(revIds.next()) {   			
    			
    			tempMap = new HashMap<String,String>();
    			tempMap.put("TABLE", "TRADEPRODUCT_AUDIT");
    			tempMap.put("UNIQUEIDENTIFIER", "REV_ID : " + revIds.getString("REV_ID"));
    			tempMap.put("OLDCIF", cifMap.get("oldCif"));
				tempMap.put("OLDMAINCIF", cifMap.get("oldMainCif"));
				tempMap.put("NEWCIF", cifMap.get("newCif"));
				tempMap.put("NEWMAINCIF", cifMap.get("newMainCif"));
    			writer.print(crlf);
    			writer.flush();
    			
    			columnContentMap= new HashMap<String,String>();
        		columnNames = new ArrayList<Map<String,String>>();
        		
    			columnContentMap.put("columnName","CIFNUMBER");
                columnContentMap.put("columnContent",cifMap.get("newCif"));
                columnNames.add(columnContentMap);
          
                columnContentMap= new HashMap<String,String>();
                if (!returnBlankIfNull(exportBills.getString("MAINCIFNUMBER")).equalsIgnoreCase("") && 
                		exportBills.getString("MAINCIFNUMBER").equalsIgnoreCase(cifMap.get("oldMainCif"))) {
                	columnContentMap.put("columnName","MAINCIFNUMBER");
                    columnContentMap.put("columnContent",cifMap.get("newMainCif"));
                    columnNames.add(columnContentMap);
                }
    			
    			if(columnNames != null && !columnNames.isEmpty()) {
    				for(Map<String,String> column : columnNames){
    	            	String updateSqript = updateIndividualColumn("TRADEPRODUCT_AUDIT", column.get("columnName"), column.get("columnContent"), "REV_ID", revIds.getString("REV_ID"));
    	            	PreparedStatement psUpdateLcFromTp = tfsConn.prepareStatement(updateSqript);
    	    			psUpdateLcFromTp.executeUpdate();
    	    			tfsConn.commit();
    	    			
    	    			psUpdateLcFromTp.close();	    			
    				}
    			} 
    		}
    		psSelectLcFromTpa.close();
    		psSelectEbFromTs.close();
		}
		
		psSelectEbFromTp.close();
		
	}
		
	private void updatePaymentTables(Map<String, String> cifMap, PrintWriter writer, String crlf, String tradeServiceId) throws SQLException, IOException {
		Map<String,String> tempMap = new HashMap<String,String>();
		
		for(String tableName: paymentTables) {
			String selectLcFromPaymentDetails = "SELECT ID as UNIQUEIDENTIFIER FROM " + tableName + " WHERE PAYMENTID IN (SELECT ID FROM PAYMENT WHERE TRADESERVICEID = '" + 
	    			tradeServiceId +"') AND FACILITYTYPE='" + cifMap.get("oldFacType") + "' AND FACILITYID='" + cifMap.get("oldFacId") +
	    			"' AND TRIM(FACILITYREFERENCENUMBER) ='" + cifMap.get("facRef") + "' " ;	           
	           
	    	PreparedStatement psSelectLcFromPaymentDetails = tfsConn.prepareStatement(selectLcFromPaymentDetails);
				ResultSet paymentDetails = psSelectLcFromPaymentDetails.executeQuery();
	    	
	    	while (paymentDetails.next()) {
				String updateAffectedAccountsQuery = "UPDATE " + tableName+ " SET FACILITYTYPE='" + cifMap.get("newFacType") + "', FACILITYID='" + 
	        			cifMap.get("newFacId") + "', FACILITYREFERENCENUMBER ='" + cifMap.get("facRef") + "' WHERE ID='" + paymentDetails.getString("UNIQUEIDENTIFIER") + "'";
				
				PreparedStatement updateAffectedAccountsPs = tfsConn.prepareStatement(updateAffectedAccountsQuery);
				updateAffectedAccountsPs.executeUpdate();
				tfsConn.commit();
				
				updateAffectedAccountsPs.close();
				
				tempMap = new HashMap<String,String>();
				tempMap.put("TABLE", tableName);
				tempMap.put("UNIQUEIDENTIFIER", "ID : " + paymentDetails.getString("UNIQUEIDENTIFIER"));
				tempMap.put("OLDFACILITYID", cifMap.get("oldFacId"));
				tempMap.put("OLDFACILITYTYPE", cifMap.get("oldFacType"));
				tempMap.put("NEWFACILITYID", cifMap.get("newFacId"));
				tempMap.put("NEWFACILITYTYPE", cifMap.get("newFacType"));
				tempMap.put("NEWFACREF", cifMap.get("facRef"));
				writer.append(printContent(tempMap));
				writer.print(crlf);
				writer.flush();
	    	}
    	
    	psSelectLcFromPaymentDetails.close();
		}
	}
	
	private void updateTradeService(Map<String, String> cifMap, PrintWriter writer, String crlf, String tradeServiceId) throws SQLException, IOException{
		Map<String,String> tempMap = new HashMap<String,String>();
		Map<String, String> cifDetailsMap = new HashMap<String,String>();
		cifDetailsMap.put("newCif", "\"" + cifMap.get("newCif") + "\"");
		cifDetailsMap.put("newMainCif", "\"" + cifMap.get("newMainCif") + "\"");
		cifDetailsMap.put("oldCif", "\"" + cifMap.get("oldCif") + "\"");
		cifDetailsMap.put("oldMainCif", "\"" + cifMap.get("oldMainCif") + "\"");
		
		
		//System.out.println(tradeServiceId);
		
		String serviceInstructionId = "";
			
		List<CifNormalizationModel>	records = tradeProductDao.getTradeService(tradeServiceId);
		
		for (CifNormalizationModel record : records) {
			List<Map<String,String>> columnNames = new ArrayList<Map<String,String>>();
			Map<String,String> columnContentMap = new HashMap<String,String>();
			record.generateDetailsMap();
			
			serviceInstructionId = (record.getServiceInstructionId() != null ? record.getServiceInstructionId().toString() : "");
            			
			if ((!returnBlankIfNull(record.getCifNumber()).equalsIgnoreCase("")) && 
					(record.getCifNumber().equalsIgnoreCase(cifMap.get("oldCif")))) {
				record.setCifNumber(cifMap.get("newCif"));
				columnContentMap= new HashMap<String,String>();
				columnContentMap.put("columnName","CIFNUMBER");
				columnContentMap.put("columnContent",record.getCifNumber());
				columnNames.add(columnContentMap);
			} 					
			if ((!returnBlankIfNull(record.getMainCifNumber()).equalsIgnoreCase("")) && 
					(record.getMainCifNumber().equalsIgnoreCase(cifMap.get("oldMainCif")))) {
				record.setMainCifNumber(cifMap.get("newMainCif"));
				columnContentMap= new HashMap<String,String>();
				columnContentMap.put("columnName","MAINCIFNUMBER");
				columnContentMap.put("columnContent",record.getMainCifNumber());
				columnNames.add(columnContentMap);
			}
			
			if (record.getDetails() != null) {
				//System.out.println("Details.. " + record.getDetails().toString());
				if ((record.getDetails().containsKey("\"cifNumber\"") && !returnBlankIfNull(record.getDetails().get("\"cifNumber\"").toString()).equalsIgnoreCase("")) && 
						(record.getDetails().get("\"cifNumber\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldCif")))) {
					record.getDetails().put("\"cifNumber\"", cifDetailsMap.get("newCif"));
	            }
	            if ((record.getDetails().containsKey("\"mainCifNumber\"") && !returnBlankIfNull(record.getDetails().get("\"mainCifNumber\"").toString()).equalsIgnoreCase("")) && 
	            		(record.getDetails().get("\"mainCifNumber\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldMainCif")))) {
	            	record.getDetails().put("\"mainCifNumber\"", cifDetailsMap.get("newMainCif"));
	            }
	            if ((record.getDetails().containsKey("\"cifNumberFrom\"") && !returnBlankIfNull(record.getDetails().get("\"cifNumberFrom\"").toString()).equalsIgnoreCase("")) && 
	            		(record.getDetails().get("\"cifNumberFrom\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldCif")))) {
	            	record.getDetails().put("\"cifNumberFrom\"", cifDetailsMap.get("newCif"));
	            }
	            if ((record.getDetails().containsKey("\"cifNumberTo\"") && !returnBlankIfNull(record.getDetails().get("\"cifNumberTo\"").toString()).equalsIgnoreCase("")) && 
	            		(record.getDetails().get("\"cifNumberTo\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldCif")))) {
	            	record.getDetails().put("\"cifNumberTo\"", cifDetailsMap.get("newCif"));
	            }
	            if ((record.getDetails().containsKey("\"mainCifNumberFrom\"") && !returnBlankIfNull(record.getDetails().get("\"mainCifNumberFrom\"").toString()).equalsIgnoreCase("")) && 
	            		(record.getDetails().get("\"mainCifNumberFrom\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldMainCif")))) {
	            	record.getDetails().put("\"mainCifNumberFrom\"", cifDetailsMap.get("newMainCif"));
	            }
	            if ((record.getDetails().containsKey("\"mainCifNumberTo\"") && !returnBlankIfNull(record.getDetails().get("\"mainCifNumberTo\"").toString()).equalsIgnoreCase("")) && 
	            		(record.getDetails().get("\"mainCifNumberTo\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldMainCif")))) {
	            	record.getDetails().put("\"mainCifNumberTo\"", cifDetailsMap.get("newMainCif"));
	            }
	            if ((record.getDetails().containsKey("\"importerCifNumber\"") && !returnBlankIfNull(record.getDetails().get("\"importerCifNumber\"").toString()).equalsIgnoreCase("")) && 
	            		(record.getDetails().get("\"importerCifNumber\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldCif")))) {
	            	record.getDetails().put("\"importerCifNumber\"", cifDetailsMap.get("newCif"));
	            }
	            if ((record.getDetails().containsKey("\"applicantCifNumber\"") && !returnBlankIfNull(record.getDetails().get("\"applicantCifNumber\"").toString()).equalsIgnoreCase("")) && 
	            		(record.getDetails().get("\"applicantCifNumber\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldCif")))) {
	            	record.getDetails().put("\"applicantCifNumber\"", cifDetailsMap.get("newCif"));
	            }
	           // System.out.println("After Update Details.. " + record.getDetails());
	            record.saveDetails(record.getDetails());
				columnContentMap= new HashMap<String,String>();
				columnContentMap.put("columnName","DETAILS");
	            columnContentMap.put("columnContent",record.getDetailsStr());
	            columnNames.add(columnContentMap);
			}	
			  

			
			PreparedStatement updateTradePs = null;
			//System.out.println(columnNames);
			if(columnNames != null || !columnNames.isEmpty()) {
				// Put inside the if statement to execute only once there is affected TRADESERVICE record.
				tempMap = new HashMap<String,String>();
				tempMap.put("TABLE", "TRADESERVICE");
				tempMap.put("UNIQUEIDENTIFIER", "TRADESERVICEID : " + tradeServiceId);
				tempMap.put("OLDCIF", cifMap.get("oldCif"));
				tempMap.put("OLDMAINCIF", cifMap.get("oldMainCif"));
				tempMap.put("NEWCIF", cifMap.get("newCif"));
				tempMap.put("NEWMAINCIF", cifMap.get("newMainCif"));
				writer.append(printContent(tempMap));
				writer.print(crlf);
				writer.flush();
				
				for(Map<String,String> column : columnNames){
	            	String updateSqript = updateIndividualColumn("TRADESERVICE", column.get("columnName"), column.get("columnContent"), "TRADESERVICEID", tradeServiceId);
	            	updateTradePs = tfsConn.prepareStatement(updateSqript);
//	            	System.out.println(updateTradePs.executeUpdate());
	            	updateTradePs.executeUpdate();
	    			tfsConn.commit();
	    			
	    			updateTradePs.close();	    			
				}
				
				// To ensure that updateTradePs will not be null before to close.
				if (updateTradePs != null){
					updateTradePs.close();	
				}
			}
						
			if (!returnBlankIfNull(serviceInstructionId.toString()).equalsIgnoreCase("")) {
				//System.out.println(serviceInstructionId);
            	
            	List<CifNormalizationModel> serviceRecords = tradeProductDao.getServiceInstruction(serviceInstructionId);
            	
            	for (CifNormalizationModel servicerecord : serviceRecords) {
            		servicerecord.generateServiceDetailsMap();
            		
            		if (servicerecord.getDetails() != null) {
                		//System.out.println("Before update Details.. " + servicerecord.getDetails());
						
	            		if ((servicerecord.getDetails().containsKey("\"cifNumber\"") && !returnBlankIfNull(servicerecord.getDetails().get("\"cifNumber\"").toString()).equalsIgnoreCase("")) && 
								(servicerecord.getDetails().get("\"cifNumber\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldCif")))) {
	            			servicerecord.getDetails().put("\"cifNumber\"", cifDetailsMap.get("newCif"));
			            }
			            if ((servicerecord.getDetails().containsKey("\"mainCifNumber\"") && !returnBlankIfNull(servicerecord.getDetails().get("\"mainCifNumber\"").toString()).equalsIgnoreCase("")) && 
			            		(servicerecord.getDetails().get("\"mainCifNumber\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldMainCif")))) {
			            	servicerecord.getDetails().put("\"mainCifNumber\"", cifDetailsMap.get("newMainCif"));
			            }
			            if ((servicerecord.getDetails().containsKey("\"cifNumberFrom\"") && !returnBlankIfNull(servicerecord.getDetails().get("\"cifNumberFrom\"").toString()).equalsIgnoreCase("")) && 
			            		(servicerecord.getDetails().get("\"cifNumberFrom\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldCif")))) {
			            	servicerecord.getDetails().put("\"cifNumberFrom\"", cifDetailsMap.get("newCif"));
			            }
			            if ((servicerecord.getDetails().containsKey("\"cifNumberTo\"") && !returnBlankIfNull(servicerecord.getDetails().get("\"cifNumberTo\"").toString()).equalsIgnoreCase("")) && 
			            		(servicerecord.getDetails().get("\"cifNumberTo\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldCif")))) {
			            	servicerecord.getDetails().put("\"cifNumberTo\"", cifDetailsMap.get("newCif"));
			            }
			            if ((servicerecord.getDetails().containsKey("\"mainCifNumberFrom\"") && !returnBlankIfNull(servicerecord.getDetails().get("\"mainCifNumberFrom\"").toString()).equalsIgnoreCase("")) && 
			            		(servicerecord.getDetails().get("\"mainCifNumberFrom\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldMainCif")))) {
			            	servicerecord.getDetails().put("\"mainCifNumberFrom\"", cifDetailsMap.get("newMainCif"));
			            }
			            if ((servicerecord.getDetails().containsKey("\"mainCifNumberTo\"") && !returnBlankIfNull(servicerecord.getDetails().get("\"mainCifNumberTo\"").toString()).equalsIgnoreCase("")) && 
			            		(servicerecord.getDetails().get("\"mainCifNumberTo\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldMainCif")))) {
			            	servicerecord.getDetails().put("\"mainCifNumberTo\"", cifDetailsMap.get("newMainCif"));
			            }
			            if ((servicerecord.getDetails().containsKey("\"importerCifNumber\"") && !returnBlankIfNull(servicerecord.getDetails().get("\"importerCifNumber\"").toString()).equalsIgnoreCase("")) && 
			            		(servicerecord.getDetails().get("\"importerCifNumber\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldCif")))) {
			            	servicerecord.getDetails().put("\"importerCifNumber\"", cifDetailsMap.get("newCif"));
			            }
			            if ((servicerecord.getDetails().containsKey("\"applicantCifNumber\"") && !returnBlankIfNull(servicerecord.getDetails().get("\"applicantCifNumber\"").toString()).equalsIgnoreCase("")) && 
			            		(servicerecord.getDetails().get("\"applicantCifNumber\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldCif")))) {
			            	servicerecord.getDetails().put("\"applicantCifNumber\"", cifDetailsMap.get("newCif"));
			            }

			           // System.out.println("After update Details.. " + servicerecord.getDetails());			            
			            servicerecord.saveDetails(servicerecord.getDetails());
			            
			            
			            String updateServiceInstruction = "UPDATE SERVICEINSTRUCTION SET DETAILS = '" + servicerecord.getDetailsStr() + "' " +
			            		"WHERE SERVICEINSTRUCTIONID = '" + serviceInstructionId + "'";
			            
			            
						tempMap = new HashMap<String,String>();
						tempMap.put("TABLE", "SERVICEINSTRUCTION");
						tempMap.put("UNIQUEIDENTIFIER", "SERVICEINSTRUCTIONID : " + serviceInstructionId);
						tempMap.put("OLDCIF", cifMap.get("oldCif"));
						tempMap.put("OLDMAINCIF", cifMap.get("oldMainCif"));
						tempMap.put("NEWCIF", cifMap.get("newCif"));
						tempMap.put("NEWMAINCIF", cifMap.get("newMainCif"));
						writer.append(printContent(tempMap));
						writer.print(crlf);
						writer.flush();
						
						PreparedStatement updateServiceInstructionPs = tfsConn.prepareStatement(updateServiceInstruction);
//						System.out.println(updateServiceInstruction);
//						System.out.println(updateServiceInstructionPs.executeUpdate());
						updateServiceInstructionPs.executeUpdate();
						tfsConn.commit();
						
						updateServiceInstructionPs.close();
	            	}
            		
            	}
            }
			
		}	
								
	}
	
	private void updateCifOnTradeService(String tradeServiceId,Map<String,String> cifMap,PrintWriter writer, String crlf, String columnName ) throws SQLException {
		Map<String,String> tempMap = new HashMap<String,String>();		
		Map<String, String> cifDetailsMap = new HashMap<String,String>();
		cifDetailsMap.put("newCif", "\"" + cifMap.get("newCif") + "\"");
		cifDetailsMap.put("oldCif", "\"" + cifMap.get("oldCif") + "\"");
			
		//System.out.println(tradeServiceId);
		
		String serviceInstructionId = "";
			
		List<CifNormalizationModel>	records = tradeProductDao.getTradeService(tradeServiceId);
		
		for (CifNormalizationModel record : records) {
			record.generateDetailsMap();
			
			if (record.getDetails() != null) {
				//System.out.println("Details.. " + record.getDetails().toString());
				
				if (columnName.equalsIgnoreCase("CIFNUMBER")) {
					
						if ((record.getDetails().containsKey("\"cifNumber\"") && !returnBlankIfNull(record.getDetails().get("\"cifNumber\"").toString()).equalsIgnoreCase("")) && 
								(record.getDetails().get("\"cifNumber\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldCif")))) {
							record.getDetails().put("\"cifNumber\"", cifDetailsMap.get("newCif"));
						}
			            if ((record.getDetails().containsKey("\"cifNumberFrom\"") && !returnBlankIfNull(record.getDetails().get("\"cifNumberFrom\"").toString()).equalsIgnoreCase("")) && 
			            		(record.getDetails().get("\"cifNumberFrom\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldCif")))) {
			            	record.getDetails().put("\"cifNumberFrom\"", cifDetailsMap.get("newCif"));
			            }
			            if ((record.getDetails().containsKey("\"cifNumberTo\"") && !returnBlankIfNull(record.getDetails().get("\"cifNumberTo\"").toString()).equalsIgnoreCase("")) && 
			            		(record.getDetails().get("\"cifNumberTo\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldCif")))) {
			            	record.getDetails().put("\"cifNumberTo\"", cifDetailsMap.get("newCif"));
			            }
			            if ((record.getDetails().containsKey("\"importerCifNumber\"") && !returnBlankIfNull(record.getDetails().get("\"importerCifNumber\"").toString()).equalsIgnoreCase("")) && 
			            		(record.getDetails().get("\"importerCifNumber\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldCif")))) {
			            	record.getDetails().put("\"importerCifNumber\"", cifDetailsMap.get("newCif"));
			            }
			            if ((record.getDetails().containsKey("\"applicantCifNumber\"") && !returnBlankIfNull(record.getDetails().get("\"applicantCifNumber\"").toString()).equalsIgnoreCase("")) && 
			            		(record.getDetails().get("\"applicantCifNumber\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldCif")))) {
			            	record.getDetails().put("\"applicantCifNumber\"", cifDetailsMap.get("newCif"));
			            }
					
				} else if (columnName.equalsIgnoreCase("MAINCIFNUMBER")) {
											
						if ((record.getDetails().containsKey("\"mainCifNumber\"") && !returnBlankIfNull(record.getDetails().get("\"mainCifNumber\"").toString()).equalsIgnoreCase("")) && 
								(record.getDetails().get("\"mainCifNumber\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldCif")))) {
							record.getDetails().put("\"mainCifNumber\"", cifDetailsMap.get("newCif"));
						}
			            if ((record.getDetails().containsKey("\"mainCifNumberFrom\"") && !returnBlankIfNull(record.getDetails().get("\"mainCifNumberFrom\"").toString()).equalsIgnoreCase("")) && 
			            		(record.getDetails().get("\"mainCifNumberFrom\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldCif")))) {
			            	record.getDetails().put("\"mainCifNumberFrom\"", cifDetailsMap.get("newCif"));
			            }
			            if ((record.getDetails().containsKey("\"mainCifNumberTo\"") && !returnBlankIfNull(record.getDetails().get("\"mainCifNumberTo\"").toString()).equalsIgnoreCase("")) && 
			            		(record.getDetails().get("\"mainCifNumberTo\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldCif")))) {
			            	record.getDetails().put("\"mainCifNumberTo\"", cifDetailsMap.get("newCif"));
			            }
										
				}

	            record.saveDetails(record.getDetails());
			}
			serviceInstructionId = (record.getServiceInstructionId() != null ? record.getServiceInstructionId().toString() : "");
			
			String updateTradeService = "UPDATE TRADESERVICE SET DETAILS = '" + record.getDetailsStr() + "', " +
					columnName + " = '" + cifMap.get("newCif") + "' WHERE TRADESERVICEID = '" + tradeServiceId + "'";
			
			PreparedStatement updateTradeServicesPs = tfsConn.prepareStatement(updateTradeService);
//			System.out.println(updateTradeService);
//			System.out.println(updateTradeServicesPs.executeUpdate());
			updateTradeServicesPs.executeUpdate();
			tfsConn.commit();
			
			updateTradeServicesPs.close();
			
			if (!returnBlankIfNull(serviceInstructionId.toString()).equalsIgnoreCase("")) {
				//System.out.println(serviceInstructionId);
            	
            	List<CifNormalizationModel> serviceRecords = tradeProductDao.getServiceInstruction(serviceInstructionId);
            	
            	for (CifNormalizationModel servicerecord : serviceRecords) {
            		servicerecord.generateServiceDetailsMap();
            		
            		if (servicerecord.getDetails() != null) {

                		//System.out.println("Before update Details.. " + servicerecord.getDetails());
                		
            			if (columnName.equalsIgnoreCase("CIFNUMBER")) {
            				
		            		if ((servicerecord.getDetails().containsKey("\"cifNumber\"") && !returnBlankIfNull(servicerecord.getDetails().get("\"cifNumber\"").toString()).equalsIgnoreCase("")) && 
									(servicerecord.getDetails().get("\"cifNumber\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldCif")))) {
		            			servicerecord.getDetails().put("\"cifNumber\"", cifDetailsMap.get("newCif"));
				            }
				            if ((servicerecord.getDetails().containsKey("\"cifNumberFrom\"") && !returnBlankIfNull(servicerecord.getDetails().get("\"cifNumberFrom\"").toString()).equalsIgnoreCase("")) && 
				            		(servicerecord.getDetails().get("\"cifNumberFrom\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldCif")))) {
				            	servicerecord.getDetails().put("\"cifNumberFrom\"", cifDetailsMap.get("newCif"));
				            }
				            if ((servicerecord.getDetails().containsKey("\"cifNumberTo\"") && !returnBlankIfNull(servicerecord.getDetails().get("\"cifNumberTo\"").toString()).equalsIgnoreCase("")) && 
				            		(servicerecord.getDetails().get("\"cifNumberTo\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldCif")))) {
				            	servicerecord.getDetails().put("\"cifNumberTo\"", cifDetailsMap.get("newCif"));
				            }
				            if ((servicerecord.getDetails().containsKey("\"importerCifNumber\"") && !returnBlankIfNull(servicerecord.getDetails().get("\"importerCifNumber\"").toString()).equalsIgnoreCase("")) && 
				            		(servicerecord.getDetails().get("\"importerCifNumber\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldCif")))) {
				            	servicerecord.getDetails().put("\"importerCifNumber\"", cifDetailsMap.get("newCif"));
				            }
				            if ((servicerecord.getDetails().containsKey("\"applicantCifNumber\"") && !returnBlankIfNull(servicerecord.getDetails().get("\"applicantCifNumber\"").toString()).equalsIgnoreCase("")) && 
				            		(servicerecord.getDetails().get("\"applicantCifNumber\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldCif")))) {
				            	servicerecord.getDetails().put("\"applicantCifNumber\"", cifDetailsMap.get("newCif"));
				            }
				            
            			} else if (columnName.equalsIgnoreCase("MAINCIFNUMBER")) {
            				
				            if ((servicerecord.getDetails().containsKey("\"mainCifNumber\"") && !returnBlankIfNull(servicerecord.getDetails().get("\"mainCifNumber\"").toString()).equalsIgnoreCase("")) && 
				            		(servicerecord.getDetails().get("\"mainCifNumber\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldCif")))) {
				            	servicerecord.getDetails().put("\"mainCifNumber\"", cifDetailsMap.get("newCif"));
				            }
				            if ((servicerecord.getDetails().containsKey("\"mainCifNumberFrom\"") && !returnBlankIfNull(servicerecord.getDetails().get("\"mainCifNumberFrom\"").toString()).equalsIgnoreCase("")) && 
				            		(servicerecord.getDetails().get("\"mainCifNumberFrom\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldCif")))) {
				            	servicerecord.getDetails().put("\"mainCifNumberFrom\"", cifDetailsMap.get("newCif"));
				            }
				            if ((servicerecord.getDetails().containsKey("\"mainCifNumberTo\"") && !returnBlankIfNull(servicerecord.getDetails().get("\"mainCifNumberTo\"").toString()).equalsIgnoreCase("")) && 
				            		(servicerecord.getDetails().get("\"mainCifNumberTo\"").toString().equalsIgnoreCase(cifDetailsMap.get("oldCif")))) {
				            	servicerecord.getDetails().put("\"mainCifNumberTo\"", cifDetailsMap.get("newCif"));
				            }
				            
            			}

						//System.out.println("After update Details.. " + servicerecord.getDetails());
            			servicerecord.saveDetails(servicerecord.getDetails());
        	            
			            String updateServiceInstruction = "UPDATE SERVICEINSTRUCTION SET DETAILS = '" + servicerecord.getDetailsStr() + "' " +
			            		"WHERE SERVICEINSTRUCTIONID = '" + serviceInstructionId + "'";
			            
			            
						tempMap = new HashMap<String,String>();
						tempMap.put("TABLE", "SERVICEINSTRUCTION");
						tempMap.put("UNIQUEIDENTIFIER", "SERVICEINSTRUCTIONID : " + serviceInstructionId);
						tempMap.put("OLDCIF", cifMap.get("oldCif"));
						tempMap.put("NEWCIF", cifMap.get("newCif"));
						writer.append(printContent(tempMap));
						writer.print(crlf);
						writer.flush();
						
						
						PreparedStatement updateServiceInstructionPs = tfsConn.prepareStatement(updateServiceInstruction);
//						System.out.println(updateServiceInstruction);
//						System.out.println(updateServiceInstructionPs.executeUpdate());
						updateServiceInstructionPs.executeUpdate();
						tfsConn.commit();
						
						updateServiceInstructionPs.close();
	            	}
            		            		
            	}
            }
			
		}	
        
	}
	
	private String updateIndividualColumn(String tableName, String columnName, String columnContent, String uniqueIdentifier, String uniqueIdentifierContent){
		
		String updateStr = "UPDATE " + tableName + " SET " + columnName + " = '" + columnContent + "' WHERE " + 
				uniqueIdentifier + " = '" + uniqueIdentifierContent + "' ";
//		System.out.println(updateStr);
		return updateStr;
		
	}
	
	private String returnBlankIfNull(String str) {
		if(str==null) {
			str="";
		}
		return str;
	}

	public void compareCif() throws Exception {
		initializeConnection();
		initializeSibs();
		
		if (tableChecking()) {
			String selectScript = "SELECT DISTINCT TRIM(CIFNUMBER) FROM (SELECT DISTINCT CIFNUMBER FROM TRADESERVICE " + 
					"WHERE CIFNUMBER IS NOT NULL AND CIFNUMBER <> '' UNION " +
					"SELECT DISTINCT TRIM(CIFNUMBER) FROM TRADEPRODUCT_AUDIT WHERE CIFNUMBER IS NOT NULL AND CIFNUMBER <> '')";
			PreparedStatement ps = tfsConn.prepareStatement(selectScript);
			ResultSet rs = ps.executeQuery();
			String cifs = "";
			
			while(rs.next()) {
				//System.out.println(rs.getString(1));
				cifs = cifs + "'" + rs.getString(1).trim() + "',";
			}
			cifs = cifs.substring(0, cifs.length() - 1);
			
			System.out.println(cifs);
			
			ps.close();
					
			//Change Schema to used UCDATPBWC was used instead of UCDATUBWC
			selectScript = "SELECT CFNCIF,CFNBRN,CFNSNM,CFNUID,CFNWID,CFNTIM,CFNDT7,CFNDT6,CFNCFO,CFNUSR,CFNWDA," +
					"CFNDA7,CFNDA6,CFNTME,CFNAPR FROM TFSDB2S.CFNMSTA WHERE TRIM(CFNCFO) IN (" + cifs + ") UNION " + 
					"SELECT CFNCIF,CFNBRN,CFNSNM,CFNUID,CFNWID,CFNTIM,CFNDT7,CFNDT6,CFNCFO,CFNUSR,CFNWDA,CFNDA7," +
					"CFNDA6,CFNTME,CFNAPR FROM TFSDB2S.CFNMSTA WHERE TRIM(CFNCFO) IN (SELECT TRIM(CFNCIF) FROM TFSDB2S.CFNMSTA) " ;
			
			System.out.println(selectScript);
			ps = sibsConn.prepareStatement(selectScript);
			ResultSet rs2 = ps.executeQuery();
			
			while(rs2.next()) {
				System.out.println(rs2.getString(1) + "\t:\t" + rs2.getString(9));
				
				PreparedStatement tfsInsertCfnmstaPs=tfsConn.prepareStatement(tfsInsertCfnmstaQuery);
				System.out.println("Insert records to CIFNORM_CFNMSTA");	
					//System.out.println("New CIF: " + record.getCFNCIF() + "\tOld CIF: " + record.getCFNCFO() + "\tDated: " + record.getCFNDA7());
				tfsInsertCfnmstaPs.setString(1, rs2.getString(1));
				tfsInsertCfnmstaPs.setBigDecimal(2, rs2.getBigDecimal(2));
				tfsInsertCfnmstaPs.setString(3, rs2.getString(3));
				tfsInsertCfnmstaPs.setString(4, rs2.getString(4));
				tfsInsertCfnmstaPs.setString(5, rs2.getString(5));
				tfsInsertCfnmstaPs.setBigDecimal(6, rs2.getBigDecimal(6));
				tfsInsertCfnmstaPs.setBigDecimal(7, rs2.getBigDecimal(7));
				tfsInsertCfnmstaPs.setBigDecimal(8, rs2.getBigDecimal(8));
				tfsInsertCfnmstaPs.setString(9, rs2.getString(9));
				tfsInsertCfnmstaPs.setString(10, rs2.getString(10));
				tfsInsertCfnmstaPs.setString(11, rs2.getString(11));
				tfsInsertCfnmstaPs.setBigDecimal(12, rs2.getBigDecimal(12));
				tfsInsertCfnmstaPs.setBigDecimal(13, rs2.getBigDecimal(13));
				tfsInsertCfnmstaPs.setBigDecimal(14, rs2.getBigDecimal(14));
				tfsInsertCfnmstaPs.setString(15, rs2.getString(15));
				tfsInsertCfnmstaPs.executeUpdate();
				
				tfsConn.commit();		
				tfsInsertCfnmstaPs.close();
				
			}
			
			ps.close();
			
			selectScript = "SELECT DISTINCT TRIM(FACILITYREFERENCENUMBER) FROM TRADEPRODUCT_AUDIT WHERE FACILITYREFERENCENUMBER IS NOT NULL " +
				"AND FACILITYREFERENCENUMBER <> '' UNION SELECT DISTINCT TRIM(FACILITYREFERENCENUMBER) FROM PAYMENTDETAIL " + 
				"WHERE FACILITYREFERENCENUMBER IS NOT NULL AND FACILITYREFERENCENUMBER <> '' ";
						
			ps = tfsConn.prepareStatement(selectScript);
			ResultSet rs3 = ps.executeQuery();
			String facRef = "";
			
			while(rs3.next()) {
				//System.out.println(rs3.getString(1));
				facRef = facRef + "'" + rs3.getString(1).trim() + "',";
			}
			facRef = facRef.substring(0, facRef.length() - 1);
			
			System.out.println(facRef);
			
			ps.close();
			
			selectScript = "SELECT FACREF,OAANO,OFCODE,OFSEQ,OCFIN,OMAANO,OMFCDE,OMFSEQ,NAANO,NFCODE,NFSEQ,NMAANO,NMFCDE,NMFSEQ," +
					"NCIFN,CHGDT6,CHGDT7,CHGTME from TFSDB2S.LNAALOG WHERE TRIM(FACREF) IN (" + facRef + ") ";
			

			System.out.println(selectScript);
			ps = sibsConn.prepareStatement(selectScript);
			rs2 = ps.executeQuery();
			
			while(rs2.next()) {
				System.out.println(rs2.getString(1));
				
				System.out.println("Insert records to CIFNORM_LNAALOG");	
				PreparedStatement tfsInsertLnaalogPs=tfsConn.prepareStatement(tfsInsertLnaalogQuery);	
					//System.out.println("New CIF: " + record.getCFNCIF() + "\tOld CIF: " + record.getCFNCFO() + "\tDated: " + record.getCFNDA7());
				tfsInsertLnaalogPs.setString(1, rs2.getString(1));
				tfsInsertLnaalogPs.setString(2, rs2.getString(2));
				tfsInsertLnaalogPs.setString(3, rs2.getString(3));
				tfsInsertLnaalogPs.setBigDecimal(4, rs2.getBigDecimal(4));
				tfsInsertLnaalogPs.setString(5, rs2.getString(5));
				tfsInsertLnaalogPs.setString(6, rs2.getString(6));
				tfsInsertLnaalogPs.setString(7,rs2.getString(7));
				tfsInsertLnaalogPs.setBigDecimal(8, rs2.getBigDecimal(8));
				tfsInsertLnaalogPs.setString(9, rs2.getString(9));
				tfsInsertLnaalogPs.setString(10, rs2.getString(10));
				tfsInsertLnaalogPs.setBigDecimal(11, rs2.getBigDecimal(11));
				tfsInsertLnaalogPs.setString(12, rs2.getString(12));
				tfsInsertLnaalogPs.setString(13, rs2.getString(13));
				tfsInsertLnaalogPs.setBigDecimal(14, rs2.getBigDecimal(14));
				tfsInsertLnaalogPs.setString(15, rs2.getString(15));
				tfsInsertLnaalogPs.setBigDecimal(16, rs2.getBigDecimal(16));
				tfsInsertLnaalogPs.setBigDecimal(17, rs2.getBigDecimal(17));
				tfsInsertLnaalogPs.setBigDecimal(18, rs2.getBigDecimal(18));
				tfsInsertLnaalogPs.executeUpdate();
				
				tfsConn.commit();		
				tfsInsertLnaalogPs.close();
				
			}
			
			ps.close();
			
		}
		
		
		
	}
	
	private Boolean tableChecking() throws Exception {
		initializeConnection();
		
		long maxDate = 0;
		boolean toReturn = false;
		String selectScript = "select max(CFNDA7) from CIFNORM_CFNMSTA where ISUPDATED is null";
		
		PreparedStatement ps = tfsConn.prepareStatement(selectScript);
		ResultSet rs = ps.executeQuery();
		
		while (rs.next()) {
			maxDate = rs.getLong(1);
		}
		
		if (maxDate==0) {
			 toReturn = true;
		} else {
			 toReturn = false;
			 return toReturn;
		}
		ps.close();
		
		Thread.sleep(3000);
		
		
		selectScript = "select max(CHGDT7) from CIFNORM_LNAALOG where ISUPDATED is null";
		ps = tfsConn.prepareStatement(selectScript);
		rs = ps.executeQuery();
		
		while (rs.next()) {
			maxDate = rs.getLong(1);
		}
		
		if (maxDate==0) {
			 toReturn = true;
		} else {
			 toReturn = false;
		}
		
		Thread.sleep(3000);
		
		return toReturn;
	}
	
}
