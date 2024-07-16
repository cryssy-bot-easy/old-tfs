package com.ucpb.tfs.batch.job;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.sql.DataSource;

import com.ucpb.tfs.batch.util.DbUtil;
import com.ucpb.tfs.batch.util.IOUtil;


/**
	PROLOGUE:
 	(revision)
	SCR/ER Number: ER# 20160201-003
	SCR/ER Description: Error found in the generated interface file TFCFACCS.csv
	[Revised by:] Jesse James Joson
	[Date revised:] 2/01/2016
	Program [Revision] Details: Add the Carriage Return, every end of the line before line feed.
	PROJECT: CORE
	MEMBER TYPE  : JAVA
	Project Name: CifPurgingGeneratorJob
 */

/**  PROLOGUE:
 * 	(revision)
	SCR/ER Number: SCR# IBD-16-0615-01
	SCR/ER Description: To comply with the requirement for CIF archiving/purging of inactive accounts in TFS.
	[Created by:] Jesse James Joson
	[Date Created:] 09/22/2016
	Program [Revision] Details: Add some select for CDT without CIFs, and to saved in DB the records if account purging was executed.
	PROJECT: CORE
	MEMBER TYPE  : Java
	Project Name: CifPurgingJob
 */
 
 
 /**  PROLOGUE:
 * 	(revision)
	SCR/ER Number: SCR# IBD-16-1206-01
	SCR/ER Description: To comply with the requirement for CIF archiving/purging of inactive accounts in TFS.
	[Created by:] Allan Comboy and Lymuel Saul
	[Date Created:] 09/22/2016
	Program [Revision] Details: Add CDT Remittance and CDT Refund module.
	PROJECT: CORE
	MEMBER TYPE  : Java
	Project Name: CifPurgingJob
 */

public class CifPurgingGeneratorJob implements SpringJob {

	/*
	 * CIF PURGING TEMPLATE
	 * index[0]=CCBDBRANCHUNITCODE
	 * index[1]=CIFNUMBER
	 * index[2]='TF'
	 * index[3]=DOCUMENTNUMBER
	 * index[4]=PROCESSUNITCODE
	 * index[5]=LASTMODIFIEDDATE
	 * index[6]=STATUS
	 * 
	 * VALIDATIONS FROM EVERY COLUMN IS NOT YET COMPLETE
	 */

	/*	PROLOGUE:
	 * revision
	  	SCR/ER Number: ER#: 20151014-050
		SCR/ER Description: CIF Purging, encounter errors in fields of CIF number,Open Date,Last Transaction Date and Status
		[Created by:] Jesse James Joson
		[Date revised:] 11/16/2015
		Program [Revision] Details: Extract CIF number instead of Main CIF number, get first transaction for open date, get last transaction for last trandate, and for EBC once settled tagged as inactive
		PROJECT: CORE
		MEMBER TYPE  : JAVA
     */
	
	
	private String directory;
	private final DataSource dataSource;

	// edited
	private final String LCQuery = "SELECT tp.CCBDBRANCHUNITCODE, tp.CIFNUMBER,'TF',tp.DOCUMENTNUMBER,DATE(lca.PROCESSDATE), " 
					+ "CASE "
					+ "when lc.LASTMODIFIEDDATE is null then DATE(lc.PROCESSDATE) " 
					+ "else DATE(lc.LASTMODIFIEDDATE) " 
					+ "END as modifiedDate, " 
					+ "tp.STATUS, "
					+ "lc.OUTSTANDINGBALANCE, "
					+ "lc.TYPE, "
					+ "lc.CASHFLAG, lc.EXPIRYDATE "
					+ "from TRADEPRODUCT tp inner join LETTEROFCREDIT lc on tp.DOCUMENTNUMBER = lc.DOCUMENTNUMBER " 
					+ "inner join " 
					+ "( " 
					+ "select lcc.DOCUMENTNUMBER, lcc.PROCESSDATE " 
					+ "from LETTEROFCREDIT_AUDIT lcc " 
					+ "WHERE lcc.REV_ID = (select min(rev_id) from LETTEROFCREDIT_AUDIT where DOCUMENTNUMBER = lcc.DOCUMENTNUMBER) " 
					+ ")lca "
					+ "on lca.DOCUMENTNUMBER = tp.DOCUMENTNUMBER ";

	// edited
	private final String DAQuery = "SELECT "
			+ " (SELECT CCBDBRANCHUNITCODE from TRADEPRODUCT where DOCUMENTNUMBER = da.DOCUMENTNUMBER) CCBDBRANCH, " 
			+ " (SELECT CIFNUMBER from TRADEPRODUCT where DOCUMENTNUMBER = da.DOCUMENTNUMBER) CIF, " 
			+ " 'TF' TFS, " 
			+ " da.DOCUMENTNUMBER DOCUMENTNUMBER, " 
			+ " (select DATE(PROCESSDATE) from DOCUMENTAGAINSTACCEPTANCE_AUDIT where DOCUMENTNUMBER = da.DOCUMENTNUMBER and REV_ID = (select min(rev_id) from DOCUMENTAGAINSTACCEPTANCE_AUDIT where DOCUMENTNUMBER = da.DOCUMENTNUMBER)) DATEPROCESS, " 
			+ " (select DATE(PROCESSDATE) from DOCUMENTAGAINSTACCEPTANCE_AUDIT where DOCUMENTNUMBER = da.DOCUMENTNUMBER and REV_ID = (select max(rev_id) from DOCUMENTAGAINSTACCEPTANCE_AUDIT where DOCUMENTNUMBER = da.DOCUMENTNUMBER)) LASTPROCESS, " 
			+ " (select status from TRADEPRODUCT where DOCUMENTNUMBER = da.DOCUMENTNUMBER) STATUS, "
			+ " (select DATE(CANCELLEDDATE) from DOCUMENTAGAINSTACCEPTANCE_AUDIT where DOCUMENTNUMBER = da.DOCUMENTNUMBER and REV_ID = (select max(rev_id) from DOCUMENTAGAINSTACCEPTANCE_AUDIT where DOCUMENTNUMBER = da.DOCUMENTNUMBER)) LASTCANCELLED,"
			+ " (select DATE(SETTLEDDATE) from DOCUMENTAGAINSTACCEPTANCE_AUDIT where DOCUMENTNUMBER = da.DOCUMENTNUMBER and REV_ID = (select max(rev_id) from DOCUMENTAGAINSTACCEPTANCE_AUDIT where DOCUMENTNUMBER = da.DOCUMENTNUMBER)) LASTSETTLED "
			+ " from DOCUMENTAGAINSTACCEPTANCE_AUDIT da, " 
			+ " REVINFO rev " 
			+ " where da.REV_ID = rev.REV " 
			+ " group by da.DOCUMENTNUMBER ";

	// edited
	private final String DPQuery = "select "
			+ "(SELECT CCBDBRANCHUNITCODE from TRADEPRODUCT where DOCUMENTNUMBER = dp.DOCUMENTNUMBER) CCBDBRANCH, "
			+ "(SELECT CIFNUMBER from TRADEPRODUCT where DOCUMENTNUMBER = dp.DOCUMENTNUMBER) CIF, "
			+ "'TF' TFS, "
			+ "dp.DOCUMENTNUMBER DOCUMENTNUMBER, "
			+ "(SELECT DATE(PROCESSDATE) from DOCUMENTAGAINSTPAYMENT_AUDIT where DOCUMENTNUMBER = dp.DOCUMENTNUMBER and REV_ID = (select min(rev_id) from DOCUMENTAGAINSTPAYMENT_AUDIT where DOCUMENTNUMBER = dp.DOCUMENTNUMBER)) DATEPROCESS, "
			+ "(SELECT DATE(PROCESSDATE) from DOCUMENTAGAINSTPAYMENT_AUDIT where DOCUMENTNUMBER = dp.DOCUMENTNUMBER and REV_ID = (select max(rev_id) from DOCUMENTAGAINSTPAYMENT_AUDIT where DOCUMENTNUMBER = dp.DOCUMENTNUMBER)) LASTDATEMODIFIED, "
			+ "(SELECT status from TRADEPRODUCT where DOCUMENTNUMBER = dp.DOCUMENTNUMBER) STATUS, "
			+ "(select DATE(CANCELLEDDATE) from DOCUMENTAGAINSTPAYMENT_AUDIT where DOCUMENTNUMBER = dp.DOCUMENTNUMBER and REV_ID = (select max(rev_id) from DOCUMENTAGAINSTPAYMENT_AUDIT where DOCUMENTNUMBER = dp.DOCUMENTNUMBER)) LASTCANCELLED,"
			+ "(select DATE(SETTLEDDATE) from DOCUMENTAGAINSTPAYMENT_AUDIT where DOCUMENTNUMBER = dp.DOCUMENTNUMBER and REV_ID = (select max(rev_id) from DOCUMENTAGAINSTPAYMENT_AUDIT where DOCUMENTNUMBER = dp.DOCUMENTNUMBER)) LASTSETTLED "
			+ "FROM DOCUMENTAGAINSTPAYMENT_AUDIT dp," + "REVINFO rev "
			+ "where dp.REV_ID = rev.REV " + "group by dp.DOCUMENTNUMBER";

	// edited
	private final String DRQuery = "select "
			+ "(SELECT CCBDBRANCHUNITCODE from TRADEPRODUCT where DOCUMENTNUMBER = dr.DOCUMENTNUMBER) CCBDBRANCH, "
			+ "(SELECT CIFNUMBER from TRADEPRODUCT where DOCUMENTNUMBER = dr.DOCUMENTNUMBER) CIF, "
			+ "'TF' TFS, "
			+ "dr.DOCUMENTNUMBER DOCUMENTNUMBER, "
			+ "(SELECT DATE(PROCESSDATE) from DIRECTREMITTANCE_AUDIT where DOCUMENTNUMBER = dr.DOCUMENTNUMBER and REV_ID = (select min(rev_id) from DIRECTREMITTANCE_AUDIT where DOCUMENTNUMBER = dr.DOCUMENTNUMBER)) DATEPROCESS, "
			+ "(SELECT DATE(PROCESSDATE) from DIRECTREMITTANCE_AUDIT where DOCUMENTNUMBER = dr.DOCUMENTNUMBER and REV_ID = (select max(rev_id) from DIRECTREMITTANCE_AUDIT where DOCUMENTNUMBER = dr.DOCUMENTNUMBER)) LASTDATEMODIFIED, "
			+ "(SELECT status from TRADEPRODUCT where DOCUMENTNUMBER = dr.DOCUMENTNUMBER) STATUS, "
			+ "(select DATE(CANCELLEDDATE) from DIRECTREMITTANCE_AUDIT where DOCUMENTNUMBER = dr.DOCUMENTNUMBER and REV_ID = (select max(rev_id) from DIRECTREMITTANCE_AUDIT where DOCUMENTNUMBER = dr.DOCUMENTNUMBER)) LASTCANCELLED, "
			+ "(select DATE(SETTLEDDATE) from DIRECTREMITTANCE_AUDIT where DOCUMENTNUMBER = dr.DOCUMENTNUMBER and REV_ID = (select max(rev_id) from DIRECTREMITTANCE_AUDIT where DOCUMENTNUMBER = dr.DOCUMENTNUMBER)) LASTSETTLED "
			+ "from DIRECTREMITTANCE_AUDIT dr, " + "REVINFO rev "
			+ "where dr.REV_ID = rev.REV " + "group by dr.DOCUMENTNUMBER";

	// edited
	private final String OAQuery = "select "
			+ "(SELECT CCBDBRANCHUNITCODE from TRADEPRODUCT where DOCUMENTNUMBER = oa.DOCUMENTNUMBER) CCBDBRANCH, "
			+ "(SELECT CIFNUMBER from TRADEPRODUCT where DOCUMENTNUMBER = oa.DOCUMENTNUMBER) CIF, "
			+ "'TF' TFS, "
			+ "oa.DOCUMENTNUMBER DOCUMENTNUMBER, "
			+ "(SELECT DATE(PROCESSDATE) from OPENACCOUNT_AUDIT where DOCUMENTNUMBER = oa.DOCUMENTNUMBER and REV_ID = (select min(rev_id) from OPENACCOUNT_AUDIT where DOCUMENTNUMBER = oa.DOCUMENTNUMBER)) DATEPROCESS, "
			+ "(SELECT DATE(PROCESSDATE) from OPENACCOUNT_AUDIT where DOCUMENTNUMBER = oa.DOCUMENTNUMBER and REV_ID = (select max(rev_id) from OPENACCOUNT_AUDIT where DOCUMENTNUMBER = oa.DOCUMENTNUMBER)) LASTDATEMODIFIED, "
			+ "(SELECT status from TRADEPRODUCT where DOCUMENTNUMBER = oa.DOCUMENTNUMBER) STATUS, "
			+ " (select DATE(CANCELLEDDATE) from OPENACCOUNT_AUDIT where DOCUMENTNUMBER = oa.DOCUMENTNUMBER and REV_ID = (select max(rev_id) from OPENACCOUNT_AUDIT where DOCUMENTNUMBER = oa.DOCUMENTNUMBER)) LASTCANCELLED, "
			+ " (select DATE(SETTLEDDATE) from OPENACCOUNT_AUDIT where DOCUMENTNUMBER = oa.DOCUMENTNUMBER and REV_ID = (select max(rev_id) from OPENACCOUNT_AUDIT where DOCUMENTNUMBER = oa.DOCUMENTNUMBER)) LASTSETTLED "
			+ "from OPENACCOUNT_AUDIT oa, " + "REVINFO rev "
			+ "where oa.REV_ID = rev.REV " + "group by oa.DOCUMENTNUMBER";

//	private final String CDTQuery = "select rp.BRANCHUNITCODE, cp.CIFNO, 'TF', cp.IEDIEIRDNO, date(cp.UPLOAD_DATE)uploadDate,date(cp.DATEPAID)datePaid,date(date_sent) dateSend, cp.STATUS from CDTPAYMENTREQUEST cp inner join REFPAS5CLIENT rp on cp.AABREFCODE=rp.AABREFCODE where cp.CIFNO != 'NONE'";
//	edited
	private final String CDTQuery = "select rp.BRANCHUNITCODE, cp.CIFNO, 'TF', cp.IEDIEIRDNO, date(cp.UPLOAD_DATE)uploadDate,date(cp.DATEPAID)datePaid,date(date_sent) dateSend,cp.STATUS,date(cp.FOR_REFUND_DATE)forrefundDate,date(cp.DATE_REFUNDED)refundDate,date(cp.DATE_REMITTED)dateRem from CDTPAYMENTREQUEST cp inner join REFPAS5CLIENT rp on cp.AABREFCODE=rp.AABREFCODE where cp.CIFNO != 'NONE'";

	// edited
	private final String ADVISINGQuery = "SELECT "
			+ "(SELECT CCBDBRANCHUNITCODE from TRADEPRODUCT where DOCUMENTNUMBER = ad.DOCUMENTNUMBER) CCBDBRANCH, "
			+ "(SELECT CIFNUMBER from TRADEPRODUCT where DOCUMENTNUMBER = ad.DOCUMENTNUMBER) CIF, "
			+ "'TF' TFS, "
			+ "ad.DOCUMENTNUMBER DOCUMENTNUMBER, "
			+ "(SELECT DATE(PROCESSDATE) from EXPORTADVISING_AUDIT where DOCUMENTNUMBER = ad.DOCUMENTNUMBER and REV_ID = (select min(rev_id) from EXPORTADVISING_AUDIT where DOCUMENTNUMBER = ad.DOCUMENTNUMBER)) DATEPROCESS, "
			+ "(SELECT DATE(PROCESSDATE) from EXPORTADVISING_AUDIT where DOCUMENTNUMBER = ad.DOCUMENTNUMBER and REV_ID = (select max(rev_id) from EXPORTADVISING_AUDIT where DOCUMENTNUMBER = ad.DOCUMENTNUMBER)) LASTDATEMODIFIED, "
			+ "(SELECT status from TRADEPRODUCT where DOCUMENTNUMBER = ad.DOCUMENTNUMBER) STATUS, "
			+ "(SELECT DATE(EXPIRYDATE) from EXPORTADVISING_AUDIT where DOCUMENTNUMBER = ad.DOCUMENTNUMBER and REV_ID = (select max(rev_id) from EXPORTADVISING_AUDIT where DOCUMENTNUMBER = ad.DOCUMENTNUMBER)) ExpiryDate, "
			+ "(SELECT DATE(CANCELLATIONDATE) from EXPORTADVISING_AUDIT where DOCUMENTNUMBER = ad.DOCUMENTNUMBER and REV_ID = (select max(rev_id) from EXPORTADVISING_AUDIT where DOCUMENTNUMBER = ad.DOCUMENTNUMBER)) cancellationdate "
			+ "from EXPORTADVISING_AUDIT ad, " + "REVINFO rev "
			+ "where ad.REV_ID = rev.REV " + "group by ad.DOCUMENTNUMBER";

	// edited
	private final String EBQuery = "select "
			+ "(select CCBDBRANCHUNITCODE from TRADEPRODUCT where DOCUMENTNUMBER = eb.DOCUMENTNUMBER) CCBDBRANCH, "
			+ "(select CIFNUMBER from TRADEPRODUCT where DOCUMENTNUMBER = eb.DOCUMENTNUMBER) CIF, "
			+ "'TF' TFS, "
			+ "eb.DOCUMENTNUMBER DOCUMENTNUMBER, "
			+ "(select DATE(PROCESSDATE) from EXPORTBILLS_AUDIT where DOCUMENTNUMBER = eb.DOCUMENTNUMBER and REV_ID = (select min(rev_id) from EXPORTBILLS_AUDIT where DOCUMENTNUMBER = eb.DOCUMENTNUMBER)) DATEPROCESS, "
			+ "(select DATE(PROCESSDATE) from EXPORTBILLS_AUDIT where DOCUMENTNUMBER = eb.DOCUMENTNUMBER and REV_ID = (select max(rev_id) from EXPORTBILLS_AUDIT where DOCUMENTNUMBER = eb.DOCUMENTNUMBER)) LASTDATEMODIFIED, "
			+ "(select status from TRADEPRODUCT where DOCUMENTNUMBER = eb.DOCUMENTNUMBER) STATUS, "
			+ "(select outstandingamount from EXPORTBILLS eb1 where eb1.DOCUMENTNUMBER=eb.DOCUMENTNUMBER), "
			+ "(select DATE(SETTLEMENTDATE) from EXPORTBILLS_AUDIT where DOCUMENTNUMBER = eb.DOCUMENTNUMBER and REV_ID = (select max(rev_id) from EXPORTBILLS_AUDIT where DOCUMENTNUMBER = eb.DOCUMENTNUMBER)) settleddate "
			+ "from EXPORTBILLS_AUDIT eb, " + "REVINFO rev "
			+ "where eb.REV_ID = rev.REV " + "group by eb.DOCUMENTNUMBER";
	
	private final String CDTQuery2 = "select rp.BRANCHUNITCODE, cp.CIFNO, 'TF', cp.IEDIEIRDNO, date(cp.UPLOAD_DATE)uploadDate,date(cp.DATEPAID)datePaid,date(date_sent) dateSend, cp.STATUS from CDTPAYMENTREQUEST cp inner join REFPAS5CLIENT rp on cp.AABREFCODE=rp.AABREFCODE where cp.IEDIEIRDNO not in (select DOCUMENTNUMBER from TFCFACCS)";


	// Marginal Deposit Remove by the User will now use the LC
	// private final String MDQuery =
	// "SELECT CCBDBRANCHUNITCODE, CIFNUMBER, 'TF', SETTLEMENTACCOUNTNUMBER, DATE(MODIFIEDDATE) PROCESS,DATE(MODIFIEDDATE) MODIFY, '1' FROM MARGINALDEPOSIT";

	private final String INQuery = "select tp.CCBDBRANCHUNITCODE,"
			+ "tp.CIFNUMBER,"
			+ "'TF',"
			+ "ind.INDEMNITYNUMBER,"
			+ "date(ind.INDEMNITYISSUEDATE),"
			+ "date(ind.PROCESSDATE),"
			+ "date(ind.CANCELLATIONDATE) "
			+ "from INDEMNITY ind inner join TRADEPRODUCT tp on ind.REFERENCENUMBER = tp.DOCUMENTNUMBER where ind.INDEMNITYTYPE='BG'";

	public CifPurgingGeneratorJob(String directory, DataSource dataSource) {
		this.directory = directory;
		this.dataSource = dataSource;
	}

	@Override
	public void execute() throws Exception {
		
		StringBuilder sb = new StringBuilder();		
		sb.append((char)13);				
		sb.append((char)10);				
		String crlf = sb.toString();
		
		Connection connection = null;
		PrintWriter writer = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
				   
		System.out.println("Here in CifPurgingGeneratorJob...");
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//			File file = new File(directory);
//			file.createNewFile();
//			writer = new PrintWriter(new FileWriter(file));
			
			connection = dataSource.getConnection();
			
			if (isPurged==0) {
				File file = new File(directory);
				file.createNewFile();
				writer = new PrintWriter(new FileWriter(file));
				
			}
			else if (isPurged==1) {
				String selectScript = "SELECT * FROM TFCFACCS";
				PreparedStatement selectPs = connection.prepareStatement(selectScript);
				ResultSet result = selectPs.executeQuery();
								
				if(result.next()) {
					isPurged = 2;
				}
				
			}
			// LETTER OF CREDIT
			System.out.println("LETTER OF CREDIT");
			ps = connection.prepareStatement(LCQuery);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (rs.getString(2) != null) {
					if (!rs.getString(2).equals("")) {
						String bcode = rs.getString(1);
						String status = rs.getString(7);
						String openingDate = sdf.format(rs.getDate(5));
						String lastMDate = sdf.format(rs.getDate(6));
						if (status.equalsIgnoreCase("CLOSED")
								|| status.equalsIgnoreCase("CANCELLED")) {
							status = "2";
						} else if (status.equalsIgnoreCase("EXPIRED")) {
							if ((rs.getString(9).equalsIgnoreCase("CASH") || rs.getInt(10) == 1) && rs.getFloat(8) != 0) {
								status = "1";
							} else {
								status = "2";
							}
						} else {
							status = "1";
						}
						
						bcode = BranchCodeCheck(bcode);

						if (isPurged==0) {
							Calendar lastDate = Calendar.getInstance();
							if (rs.getString(7).equalsIgnoreCase("EXPIRED") && (rs.getDate(11).after(rs.getDate(6)) || rs.getDate(11).equals(rs.getDate(6)))) {								
								lastDate.setTime(rs.getDate(11));
								lastDate.add(Calendar.DATE, 1);
								Date tempDate = lastDate.getTime();
								lastMDate = sdf.format(tempDate);
							}
							
							String appendString = bcode	+ ","
									+ rs.getString(2).trim() + ","
									+ rs.getString(3) + ","
									+ padRight(rs.getString(4).replace("-",""),20) + ","
									+ openingDate + ","
									+ lastMDate + ","
									+ status;
							writer.append(appendString);
							writer.print(crlf);
						} else if (isPurged==1) {
							Calendar lastDate = Calendar.getInstance();
							if (rs.getString(7).equalsIgnoreCase("EXPIRED") && (rs.getDate(11).after(rs.getDate(6)) || rs.getDate(11).equals(rs.getDate(6)))) {								
								lastDate.setTime(rs.getDate(11));
								lastDate.add(Calendar.DATE, 1);
								Date tempDate = lastDate.getTime();
								lastMDate = sdf.format(tempDate);
							}
							
							
							String insertScript = "INSERT INTO TFCFACCS VALUES('" + bcode + "','" + rs.getString(2).trim() + "','" +
									rs.getString(4) + "'," + openingDate + "," + lastMDate + "," + status + ",'LC',0)";
							PreparedStatement insertPs = connection.prepareStatement(insertScript);
							insertPs.executeUpdate();
							connection.commit();
							
							insertPs.close();
						}
					}
				}
			}

			System.out.println("DOCUMENT AGAINS ACCEPTANCE");
			// DOCUMENT AGAINST ACCEPTANCE
			ps = connection.prepareStatement(DAQuery);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (rs.getString(2) != null) {
					if (!rs.getString(2).equals("")) {
						String bcode = rs.getString(1);
						String status = rs.getString(7);
						String openingDate = sdf.format(rs.getDate(5));
						String lastMDate;
						
						if (rs.getDate(9) != null && rs.getDate(8) != null) {
							if (rs.getDate(9).after(rs.getDate(8))) {
								lastMDate = sdf.format(rs.getDate(9));
							} else {
								lastMDate = sdf.format(rs.getDate(8));
							}
						} else if (rs.getDate(9) != null) {
							lastMDate = sdf.format(rs.getDate(9));
						} else if (rs.getDate(8) != null) {
							lastMDate = sdf.format(rs.getDate(8));
						} else {
							lastMDate = sdf.format(rs.getDate(6));
						}
						
						
						if (status.equalsIgnoreCase("EXPIRED")
								|| status.equalsIgnoreCase("CLOSED")
								|| status.equalsIgnoreCase("CANCELLED")) {
							status = "2";
						} else {
							status = "1";
						}
						bcode = BranchCodeCheck(bcode);
						
						if (isPurged==0) {
							String appendString = bcode	+ ","
									+ rs.getString(2).trim() + ","
									+ rs.getString(3) + ","
									+ padRight(rs.getString(4).replace("-",""),20) + ","
									+ openingDate + "," + lastMDate + ","
									+ status;
							writer.append(appendString);
							writer.print(crlf);

						} else if (isPurged==1) {
							String insertScript = "INSERT INTO TFCFACCS VALUES('" + bcode + "','" + rs.getString(2).trim() + "','" +
									rs.getString(4) + "'," + openingDate + "," + lastMDate + "," + status + ",'DA',0)";
							PreparedStatement insertPs = connection.prepareStatement(insertScript);
							insertPs.executeUpdate();
							connection.commit();
							
							insertPs.close();
						}
					}
				}
			}
			System.out.println("DDOCUMENT AGAINS PAYMENT");
			// DOCUMENT AGAINST PAYMENT
			ps = connection.prepareStatement(DPQuery);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (rs.getString(2) != null) {
					if (!rs.getString(2).equals("")) {
						String bcode = rs.getString(1);
						String status = rs.getString(7);
						String openingDate = sdf.format(rs.getDate(5));
						String lastMDate = sdf.format(rs.getDate(6));
						if (status.equalsIgnoreCase("EXPIRED")
								|| status.equalsIgnoreCase("CLOSED")
								|| status.equalsIgnoreCase("CANCELLED")) {
							status = "2";
						} else {
							status = "1";
						}
						
						if (rs.getDate(9) != null && rs.getDate(8) != null) {
							if (rs.getDate(9).after(rs.getDate(8))) {
								lastMDate = sdf.format(rs.getDate(9));
							} else {
								lastMDate = sdf.format(rs.getDate(8));
							}
						} else if (rs.getDate(9) != null) {
							lastMDate = sdf.format(rs.getDate(9));
						} else if (rs.getDate(8) != null) {
							lastMDate = sdf.format(rs.getDate(8));
						} else {
							lastMDate = sdf.format(rs.getDate(6));
						}
						
						bcode = BranchCodeCheck(bcode);
						if (isPurged==0) {
							String appendString = bcode	+ ","
									+ rs.getString(2).trim() + ","
									+ rs.getString(3) + ","
									+ padRight(rs.getString(4).replace("-",""),20) + ","
									+ openingDate + "," + lastMDate + ","
									+ status;
							writer.append(appendString);
							writer.print(crlf);

						} else if (isPurged==1) {
							String insertScript = "INSERT INTO TFCFACCS VALUES('" + bcode + "','" + rs.getString(2).trim() + "','" +
									rs.getString(4) + "'," + openingDate + "," + lastMDate + "," + status + ",'DP',0)";
							PreparedStatement insertPs = connection.prepareStatement(insertScript);
							insertPs.executeUpdate();
							connection.commit();
							
							insertPs.close();
						}
					}
				}
			}
			System.out.println("DIRECT REMITANCE!");
			// DIRECT REMITANCE
			ps = connection.prepareStatement(DRQuery);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (rs.getString(2) != null) {
					if (!rs.getString(2).equals("")) {
						String bcode = rs.getString(1);
						String status = rs.getString(7);
						String openingDate = sdf.format(rs.getDate(5));
						String lastMDate = sdf.format(rs.getDate(6));
						if (status.equalsIgnoreCase("EXPIRED")
								|| status.equalsIgnoreCase("CLOSED")
								|| status.equalsIgnoreCase("CANCELLED")) {
							status = "2";
						} else {
							status = "1";
						}

						if (rs.getDate(9) != null && rs.getDate(8) != null) {
							if (rs.getDate(9).after(rs.getDate(8))) {
								lastMDate = sdf.format(rs.getDate(9));
							} else {
								lastMDate = sdf.format(rs.getDate(8));
							}
						} else if (rs.getDate(9) != null) {
							lastMDate = sdf.format(rs.getDate(9));
						} else if (rs.getDate(8) != null) {
							lastMDate = sdf.format(rs.getDate(8));
						} else {
							lastMDate = sdf.format(rs.getDate(6));
						}
						
						bcode = BranchCodeCheck(bcode);

						if (isPurged==0) {
							String appendString = bcode	+ ","
									+ rs.getString(2).trim() + ","
									+ rs.getString(3) + ","
									+ padRight(rs.getString(4).replace("-",""),20) + ","
									+ openingDate + "," + lastMDate + ","
									+ status;
							writer.append(appendString);
							writer.print(crlf);

						} else if (isPurged==1) {
							String insertScript = "INSERT INTO TFCFACCS VALUES('" + bcode + "','" + rs.getString(2).trim() + "','" +
									rs.getString(4) + "'," + openingDate + "," + lastMDate + "," + status + ",'DR',0)";
							PreparedStatement insertPs = connection.prepareStatement(insertScript);
							insertPs.executeUpdate();
							connection.commit();
							
							insertPs.close();
						}
					}
				}
			}
			System.out.println("OPEN ACCOUNT!");
			// OVER ACCEPTANCE HAHAH
			ps = connection.prepareStatement(OAQuery);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (rs.getString(2) != null) {
					if (!rs.getString(2).equals("")) {
						String bcode = rs.getString(1);
						String status = rs.getString(7);
						String openingDate = sdf.format(rs.getDate(5));
						String lastMDate = sdf.format(rs.getDate(6));
						String docuNum = rs.getString(4);

						if (docuNum.length() == 19) {
							docuNum = docuNum.substring(0, 7) + "00"
									+ docuNum.substring(7);
						}

						if (status.equalsIgnoreCase("EXPIRED")
								|| status.equalsIgnoreCase("CLOSED")
								|| status.equalsIgnoreCase("CANCELLED")) {
							status = "2";
						} else {
							status = "1";
						}
						
						if (rs.getDate(9) != null && rs.getDate(8) != null) {
							if (rs.getDate(9).after(rs.getDate(8))) {
								lastMDate = sdf.format(rs.getDate(9));
							} else {
								lastMDate = sdf.format(rs.getDate(8));
							}
						} else if (rs.getDate(9) != null) {
							lastMDate = sdf.format(rs.getDate(9));
						} else if (rs.getDate(8) != null) {
							lastMDate = sdf.format(rs.getDate(8));
						} else {
							lastMDate = sdf.format(rs.getDate(6));
						}
						
						bcode = BranchCodeCheck(bcode);
						if (isPurged==0) {
							String appendString = bcode + ","
									+ rs.getString(2).trim() + ","
									+ rs.getString(3) + ","
									+ padRight(docuNum.replace("-",""),20) + ","
									+ openingDate + "," + lastMDate + ","
									+ status;
							writer.append(appendString);
							writer.print(crlf);

						} else if (isPurged==1) {
							String insertScript = "INSERT INTO TFCFACCS VALUES('" + bcode + "','" + rs.getString(2).trim() + "','" +
									rs.getString(4) + "'," + openingDate + "," + lastMDate + "," + status + ",'OA',0)";
							PreparedStatement insertPs = connection.prepareStatement(insertScript);
							insertPs.executeUpdate();
							connection.commit();
							
							insertPs.close();
						}
					}
				}
			}
			System.out.println("CDT----------!");
			// CDT
			
			ps = connection.prepareStatement(CDTQuery);
									
			rs = ps.executeQuery();
			while (rs.next()) {
				if (rs.getString(2) != null) {
					if (!rs.getString(2).equals("")) {
						String tempDate = "";
						String bcode = rs.getString(1);
						String status = rs.getString(8);
						String processDate = sdf.format(rs.getDate(5));
						

						String paidDate = "";
						String sentDate = "";
						
						
						// SUPPY NULL VALUES TO DATE

						if (rs.getDate(7) == null) {
							sentDate = processDate;
						} else {
							sentDate = sdf.format(rs.getDate(7));
						}

						bcode = BranchCodeCheck(bcode);
						if (status.matches("PAID") ) {
							// SUPPY NULL VALUES TO DATE
			
							  paidDate = processDate;
							
							tempDate = paidDate;
						} else 	if(status.matches("REFUNDED")){
							// SUPPY NULL VALUES TO DATE
							if (rs.getDate(10) != null) {
								paidDate = sdf.format(rs.getDate(10));
							} else {
								paidDate = processDate;
							}
							tempDate = paidDate;
						} else if (status.matches("REMITTED")) {
							// SUPPY NULL VALUES TO DATE
							if (rs.getDate(11) != null) {
								paidDate = sdf.format(rs.getDate(11));
							} else {
								paidDate = processDate;
							}
							tempDate = paidDate;
						} else if (status.matches("FORREFUND")) {
							// SUPPY NULL VALUES TO DATE
							if (rs.getDate(9) != null) {
								paidDate = sdf.format(rs.getDate(9));
							} else {
								paidDate = processDate;
							}
							tempDate = paidDate;
						} else if (status.matches("SENTTOBOC")) {
							// SUPPY NULL VALUES TO DATE
							if (rs.getDate(6) != null) {
								paidDate = sdf.format(rs.getDate(6));
							} else {
								paidDate = processDate;
							}
							tempDate = paidDate;
						} else {
							
					
							tempDate = processDate;
						}

						if (status.equalsIgnoreCase("REFUNDED") || status.equalsIgnoreCase("REMITTED") || status.equalsIgnoreCase("FORREFUND")) {
							status = "2";
						} else {
							status = "1";
						}

						if (isPurged==0) {
							String appendString = bcode	+ ","
									+ rs.getString(2).trim() + ","
									+ rs.getString(3) + ","
									+ padRight((rs.getString(4).replace("-","")).replaceAll("\\s+",""),20) + ","
									+ processDate + "," + tempDate + ","
									+ status;
							writer.append(appendString);
							writer.print(crlf);

						} else if (isPurged==1) {
							String insertScript = "INSERT INTO TFCFACCS VALUES('" + bcode + "','" + rs.getString(2).trim() + "','" +
									rs.getString(4) + "'," + processDate + "," + tempDate + "," + status + ",'CDT',0)";
							PreparedStatement insertPs = connection.prepareStatement(insertScript);
							insertPs.executeUpdate();
							connection.commit();
							
							insertPs.close();
						}

					}
				}
			}
			System.out.println("ADIVISING QUERY!");
			// ADVISING
			ps = connection.prepareStatement(ADVISINGQuery);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (rs.getString(2) != null) {
					if (!rs.getString(2).equals("")) {
						String bcode = rs.getString(1);
						String status = rs.getString(7);
						String openingDate = sdf.format(rs.getDate(5));
						String lastMDate = sdf.format(rs.getDate(6));
						String docuNum = rs.getString(4);
						if (status.equalsIgnoreCase("EXPIRED")
								|| status.equalsIgnoreCase("CLOSED")
								|| status.equalsIgnoreCase("CANCELLED")) {
							status = "2";
						} else if(rs.getDate(8).before(new Date())) {
							status = "2";
						} else {
							status = "1";
						}
						
						if(docuNum.length()==19){
							docuNum=docuNum.substring(0,7)+"00"+docuNum.substring(7);
						}
						
						if (rs.getDate(9) != null) {
							lastMDate = sdf.format(rs.getDate(9));
						}
						
						bcode = BranchCodeCheck(bcode);
						if (isPurged==0) {
							String appendString = bcode	+ ","
									+ rs.getString(2).trim() + ","
									+ rs.getString(3) + ","
									+ padRight(docuNum.replace("-",""),20) + ","
									+ openingDate + "," + lastMDate + ","
									+ status;
							writer.append(appendString);
							writer.print(crlf);

						} 
					}
				}
			}
			System.out.println("EXPORT BILLS!");
			// EXPORT BILLS
			ps = connection.prepareStatement(EBQuery);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (rs.getString(2) != null) {
					if (!rs.getString(2).equals("")) {
						String bcode = rs.getString(1);
						String status = rs.getString(7);
						String openingDate = sdf.format(rs.getDate(5));
						String lastMDate = sdf.format(rs.getDate(6));
						String docuNum = rs.getString(4);
						if (status.equalsIgnoreCase("EXPIRED")
								|| status.equalsIgnoreCase("CLOSED")
								|| status.equalsIgnoreCase("CANCELLED")
								|| status.equalsIgnoreCase("SETTLED")) {
							status = "2";
						} else {
							status = "1";
						}
						
						if(docuNum.length()==19){
							docuNum=docuNum.substring(0,7)+"00"+docuNum.substring(7);
						}
						
						bcode = BranchCodeCheck(bcode);
						
						if (rs.getDate(9) != null) {
							lastMDate = sdf.format(rs.getDate(9));
						}

						if (isPurged==0) {
							String appendString = bcode	+ ","
									+ rs.getString(2).trim() + ","
									+ rs.getString(3) + ","
									+ padRight(docuNum.replace("-",""),20) + ","
									+ openingDate + "," + lastMDate + ","
									+ status;
							writer.append(appendString);
							writer.print(crlf);

						} else if (isPurged==1) {
							String insertScript = "INSERT INTO TFCFACCS VALUES('" + bcode + "','" + rs.getString(2).trim() + "','" +
									docuNum + "'," + openingDate + "," + lastMDate + "," + status + ",'EB',0)";
							PreparedStatement insertPs = connection.prepareStatement(insertScript);
							insertPs.executeUpdate();
							connection.commit();
							
							insertPs.close();
						}
					}
				}
			}

			System.out.println("BANG GUARANTEE!");
			// BANG GURANTEDEE
			ps = connection.prepareStatement(INQuery);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (rs.getString(2) != null) {
					if (!rs.getString(2).equals("")) {
						String bcode = rs.getString(1);
						Date statusDate = rs.getDate(7);
						String openingDate = sdf.format(rs.getDate(5));
						String lastMDate = sdf.format(rs.getDate(6));
						String status = "";
						if (statusDate != null) {
							status = "2";
							lastMDate = sdf.format(rs.getDate(7));
						} else {
							status = "1";
						}
						bcode = BranchCodeCheck(bcode);
						if (isPurged==0) {
							String appendString = bcode	+ ","
									+ rs.getString(2).trim() + ","
									+ rs.getString(3) + ","
									+ padRight(rs.getString(4).replace("-",""),20) + ","
									+ openingDate + "," + lastMDate + ","
									+ status;
							writer.append(appendString);
							writer.print(crlf);

						} 
					}
				}
			}
			
			System.out.println("CDT part 2----------!");
			
			if (isPurged==1) {
				ps = connection.prepareStatement(CDTQuery2);
										
				rs = ps.executeQuery();
				while (rs.next()) {
					String tempDate = "";
					String bcode = rs.getString(1);
					String status = rs.getString(8);
					String processDate = sdf.format(rs.getDate(5));

					String paidDate = "";
					String sentDate = "";
					// SUPPY NULL VALUES TO DATE
					if (rs.getDate(6) != null) {
						paidDate = sdf.format(rs.getDate(6));
					} else {
						paidDate = processDate;
					}
					// SUPPY NULL VALUES TO DATE

					if (rs.getDate(7) == null) {
						sentDate = processDate;
					} else {
						sentDate = sdf.format(rs.getDate(7));
					}

					bcode = BranchCodeCheck(bcode);
					if (status.matches("PAID")
							|| status.matches("REFUNDED")) {
						tempDate = paidDate;
					} else if (status.matches("SENTTOBOC")) {
						tempDate = sentDate;
					} else {
						tempDate = processDate;
					}

					if (status.equalsIgnoreCase("SENTTOBOC") || status.equalsIgnoreCase("REFUNDED")) {
						status = "2";
					} else {
						status = "1";
					}

					String insertScript = "INSERT INTO TFCFACCS VALUES('" + bcode + "','NONE','" +
							rs.getString(4) + "'," + processDate + "," + tempDate + "," + status + ",'CDT',0)";
					PreparedStatement insertPs = connection.prepareStatement(insertScript);
					insertPs.executeUpdate();
					connection.commit();
					
					insertPs.close();
					

				
				}
			}
		} catch (Exception e) {
			System.out.println(e);
			throw e;

		} finally {
			if (isPurged==0) {
				writer.close();	
				IOUtil.closeQuietly(writer);
			}
			
			isPurged = 0;
			ps.close();
			DbUtil.closeQuietly(connection);
		}

	}

	public String BranchCodeCheck(String bcode) {
		String result = bcode;
		if (bcode != null) {
			if (bcode.length() == 2) {
				result = "0" + bcode;
			} else if (bcode.length() == 1) {
				result = "00" + bcode;
			}
		} else {
			result = "001";
		}
		return result;
	}

	public static String padRight(String s, int n) {
		return String.format("%1$-" + n + "s", s);
	}

	@Override
	public void execute(String reportDate) throws Exception {
		// TODO Auto-generated method stub

	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	private int isPurged = 0;
	
	public int getIsPurged() {
		return isPurged;
	}

	public void setIsPurged(int isPurge) throws Exception {
		this.isPurged = isPurge;
		execute();
	}

}
