package com.ucpb.tfs.core.batch.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ucpb.tfs.application.service.CifNormalizationLogService;
import com.ucpb.tfs.batch.util.DbUtil;
import javax.sql.DataSource;

import com.ucpb.tfs.domain.accounting.enumTypes.BookCode;
import com.ucpb.tfs.domain.instruction.ServiceInstruction;
import com.ucpb.tfs.domain.instruction.ServiceInstructionId;
import com.ucpb.tfs.domain.instruction.ServiceInstructionRepository;
import org.springframework.beans.factory.annotation.Autowired;


import com.ucpb.tfs.batch.cif.CibsDetailsTable;
import com.ucpb.tfs.batch.cif.CibsMasterTable;
import com.ucpb.tfs.domain.payment.PaymentDetail;
import com.ucpb.tfs.batch.report.dw.dao.SilverlakeLocalDao;

import com.ucpb.tfs.batch.job.enums.UpdateCifNumberQueries;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.payment.PaymentRepository;

/**
 * PROLOGUE
 *  Description: Replaced usage of silverlakeDao into silverlakeLocalDao
 *  [Revised by:] Cedrick C. Nungay
 *  [Date revised:] 01/25/2024
*/
public class CifNormalization {

	private SilverlakeLocalDao silverlakeLocalDao;
	
	@Autowired
    TradeServiceRepository tradeServiceRepository;

    @Autowired
    ServiceInstructionRepository serviceInstructionRepository;

    @Autowired
    private CifNormalizationLogService cifNormalizationLogService;

	@Autowired
	PaymentRepository paymentRepository;
    
	private final String tfsInsertMasterQuery = "INSERT INTO CIFNORM_CFNMSTA " +
			"(CFNCIF,CFNBRN,CFNSNM,CFNUID,CFNWID,CFNTIM,CFNDT7,CFNDT6,CFNCFO,CFNUSR," +
			"CFNWDA,CFNDA7,CFNDA6,CFNTME,CFNAPR) " +
			"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private final String tfsInsertDetailsQuery = "INSERT INTO CIFNORM_LNAALOG " +
			"(FACREF,OAANO,OFCODE,OFSEQ,OCFIN,OMAANO,OMFCDE,OMFSEQ,NAANO,NFCODE,NFSEQ," +
//			"NMAANO,NMFCDE,NMFSEQ,NCIFN,CHGDT6,CHGDT7,CHGTME) " +
//            "NMAANO,NMFCDE,NMFSEQ,NCIFN,CHGDT6,CHGDT7,CHGTME,CFNSNM) " +
            "NMAANO,NMFCDE,NMFSEQ,NCIFN,CHGDT6,CHGDT7,CHGTME) " +
//			"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private final String deleteTfsMasterQuery = "DELETE FROM CIFNORM_CFNMSTA";
	private final String deleteTfsDetailsQuery = "DELETE FROM CIFNORM_LNAALOG";

	private final DataSource tfsDataSource;
	private Connection tfsConn = null;
	
	
	public CifNormalization(DataSource tfsDataSource){
		this.tfsDataSource=tfsDataSource;
	}
	
	public void setSilverlakeLocalDao(SilverlakeLocalDao silverlakeLocalDao){
		this.silverlakeLocalDao=silverlakeLocalDao;
	}
	
	public boolean execute(){
		boolean result=false;
		try{
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			System.out.println("~~~~~~~~ CIF NORMALIZATION: CLONING SIBS Tables ~~~~~~~~~~~");
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			cloneCibsDb();
			
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			System.out.println("~~~~~~~~ CIF NORMALIZATION: UPDATING TFS Tables ~~~~~~~~~~~");
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			updateCifTables();
			
			result = true;
		}catch(SQLException ex){
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			System.out.println("~~~~~~~ CIF NORMALIZATION: Failed >> SQLException ~~~~~~~~~");
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			
			if(!ex.getNextException().equals(null)){
				ex.getNextException().printStackTrace();
			}else{
				ex.printStackTrace();
			}
		}catch(Exception e){	
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			System.out.println("~~~~~~~~~~~~~~~ CIF NORMALIZATION: Failed  ~~~~~~~~~~~~~~~~");
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			e.printStackTrace();
		}
		
		return result;
	}
	
	private void cloneCibsDb() throws SQLException{
		try{
			initializeConnection();
			PreparedStatement tfsInsertMasterPs=tfsConn.prepareStatement(tfsInsertMasterQuery);
			PreparedStatement tfsInsertDetailsPs=tfsConn.prepareStatement(tfsInsertDetailsQuery);
			PreparedStatement deleteMasterPs=tfsConn.prepareStatement(deleteTfsMasterQuery);
			PreparedStatement deleteDetailsPs=tfsConn.prepareStatement(deleteTfsDetailsQuery);
			List<CibsMasterTable> cibsMasterTable=silverlakeLocalDao.getCibsMasterTable(0,0);
			List<CibsDetailsTable> cibsDetailsTable=silverlakeLocalDao.getCibsDetailsTable(0,0);
			
			//CLEAN TABLES FIRST
			deleteMasterPs.executeUpdate();
			deleteDetailsPs.executeUpdate();
			
			tfsConn.commit();

            List<Map<String, String>> oldCifNumbers = new ArrayList<Map<String, String>>();

			for(CibsMasterTable tbl:cibsMasterTable){
				tfsInsertMasterPs.setString(1, tbl.getCFNCIF());
				tfsInsertMasterPs.setBigDecimal(2, tbl.getCFNBRN());
				tfsInsertMasterPs.setString(3, tbl.getCFNSNM());
				tfsInsertMasterPs.setString(4, tbl.getCFNUID());
				tfsInsertMasterPs.setString(5, tbl.getCFNWID());
				tfsInsertMasterPs.setBigDecimal(6, tbl.getCFNTIM());
				tfsInsertMasterPs.setBigDecimal(7, tbl.getCFNDT7());
				tfsInsertMasterPs.setBigDecimal(8, tbl.getCFNDT6());
				tfsInsertMasterPs.setString(9, tbl.getCFNCFO());

                // retrieves all old cif numbers and place it on an empty list
                Map<String, String> oldCifMap = new HashMap<String, String>();
                oldCifMap.put(tbl.getCFNCFO(), tbl.getCFNSNM());
                oldCifNumbers.add(oldCifMap);

				tfsInsertMasterPs.setString(10, tbl.getCFNUSR());
				tfsInsertMasterPs.setString(11, tbl.getCFNWDA());
				tfsInsertMasterPs.setBigDecimal(12, tbl.getCFNDA7());
				tfsInsertMasterPs.setBigDecimal(13, tbl.getCFNDA6());
				tfsInsertMasterPs.setBigDecimal(14, tbl.getCFNTME());
				tfsInsertMasterPs.setString(15, tbl.getCFNAPR());
				tfsInsertMasterPs.addBatch();
			}
			tfsInsertMasterPs.executeBatch();	
			
			for(CibsDetailsTable tbl:cibsDetailsTable){
				tfsInsertDetailsPs.setString(1, tbl.getFACREF());
				tfsInsertDetailsPs.setString(2, tbl.getOAANO());
				tfsInsertDetailsPs.setString(3, tbl.getOFCODE());
				tfsInsertDetailsPs.setBigDecimal(4, tbl.getOFSEQ());
				tfsInsertDetailsPs.setString(5, tbl.getOCFIN());
				tfsInsertDetailsPs.setString(6, tbl.getOMAANO());
				tfsInsertDetailsPs.setString(7, tbl.getOMFCDE());
				tfsInsertDetailsPs.setBigDecimal(8, tbl.getOMFSEQ());
				tfsInsertDetailsPs.setString(9, tbl.getNAANO());
				tfsInsertDetailsPs.setString(10, tbl.getNFCODE());
				tfsInsertDetailsPs.setBigDecimal(11, tbl.getNFSEQ());
				tfsInsertDetailsPs.setString(12, tbl.getNMAANO());
				tfsInsertDetailsPs.setString(13, tbl.getNMFCDE());
				tfsInsertDetailsPs.setBigDecimal(14, tbl.getNMFSEQ());
				tfsInsertDetailsPs.setString(15, tbl.getNCIFN());
				tfsInsertDetailsPs.setBigDecimal(16, tbl.getCHGDT6());
				tfsInsertDetailsPs.setBigDecimal(17, tbl.getCHGDT7());
				tfsInsertDetailsPs.setBigDecimal(18, tbl.getCHGTME());

//                String cifName = getCifName(oldCifNumbers, tbl.getOCFIN());
//
//                tfsInsertDetailsPs.setString(19, cifName);


				tfsInsertDetailsPs.addBatch();
			}
			tfsInsertDetailsPs.executeBatch();
			tfsConn.commit();
		}catch(SQLException ex){
			
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			System.out.println("~~~~ CIF NORMALIZATION: ERROR IN CLONING SIBS Tables ~~~~~~");
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			
			ex.printStackTrace();
			throw ex;
		}finally{
			DbUtil.closeQuietly(tfsConn);
		}
	}

    private String getCifName(List<Map<String, String>> oldCifNumbers, String oldCifNumber) {
        System.out.println("retrieving cif name...");
        // iterates list of old cif numbers
        for (Map<String, String> oldCifMap : oldCifNumbers) {
            // returns value if old cif number matches
            System.out.println(oldCifMap.get(oldCifNumber) + " : " + oldCifNumber);

            if (oldCifMap.get(oldCifNumber) != null) {
                return oldCifMap.get(oldCifNumber);
            }
        }

        return null;
    }
	
	private void updateCifTables() throws SQLException{
		try{
			updateCifNumberQueriesCollection();
			updateTradeProductTable();
			updateTradeServiceReferences();
            updateCifNames();
		}catch(SQLException ex){
			
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			System.out.println("~~~~~~CIF NORMALIZATION: FAILED TO UPDATE TFS TABLES~~~~~~~");
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			
			ex.printStackTrace();
			throw ex;
		}finally{
			DbUtil.closeQuietly(tfsConn);
		}
	}
	
	private void updateCifNumberQueriesCollection() throws SQLException{
		try{
			initializeConnection();
			Statement updatePs=tfsConn.createStatement();
			
			for(UpdateCifNumberQueries query:UpdateCifNumberQueries.values()){
				updatePs.addBatch(query.toString());
			}
			updatePs.executeBatch();
			tfsConn.commit();
		}catch(SQLException ex){
			System.out.println("\nError in Updating CIF Number Queries:\n");
			ex.printStackTrace();
			throw ex;
		}finally{
			DbUtil.closeQuietly(tfsConn);
		}
	}

	private void updateTradeProductTable() throws SQLException{
		String insertQuery="UPDATE TRADEPRODUCT SET CIFNUMBER = ?, "+
                "MAINCIFNUMBER = ?, "+
                "FACILITYTYPE = ?, "+
                "FACILITYID = ?, "+
                "FACILITYREFERENCENUMBER = ?, "+
                "CIFNAME = ?, "+
                "MAINCIFNAME = ? "+

                "WHERE CIFNUMBER=? "+
                "AND MAINCIFNUMBER=? "+
                "AND FACILITYTYPE=? "+
                "AND FACILITYID=?";

        System.out.println("insertQuery\n" + insertQuery);

        String getCifRecordsToUpdateQuery="SELECT " +
                "TP.DOCUMENTNUMBER, " +
                "CL.OCFIN,CL.OAANO,CL.OFCODE, CL.OFSEQ,CL.NCIFN,CL.NAANO,CL.NFCODE,CL.NFSEQ,CL.FACREF, " +
                "TP.CIFNAME, TP.MAINCIFNAME, " +
                "(SELECT DISTINCT(CFNSNM) FROM CIFNORM_CFNMSTA WHERE CFNCIF = CL.NCIFN) AS NEWCIFNAME, " +
                "(SELECT DISTINCT(CFNSNM) FROM CIFNORM_CFNMSTA WHERE CFNCIF = CL.NAANO) AS NEWMAINCIFNAME " +
                "FROM " +
				"CIFNORM_LNAALOG AS CL " +
                "INNER JOIN " +
                "TRADEPRODUCT AS TP ON " +
				"TP.CIFNUMBER=CL.OCFIN AND TP.MAINCIFNUMBER=CL.OAANO AND " +
				"TP.FACILITYTYPE=CL.OFCODE AND TP.FACILITYID != '' AND CAST(TP.FACILITYID AS DECIMAL(11)) = CL.OFSEQ";
        System.out.println("getCifRecordsToUpdateQuery\n" + getCifRecordsToUpdateQuery);

//        String insertIntoCifNormLogQuery="INSERT INTO CIFNORM_LOG (OLDCIFNO,NEWCIFNO,INSERTDATE) VALUES(?,?,?)";

        try{
			initializeConnection();
			PreparedStatement recordsToUpdateStatement=tfsConn.prepareStatement(getCifRecordsToUpdateQuery);
			PreparedStatement recordsToInsertStatement=tfsConn.prepareStatement(insertQuery);
//			PreparedStatement insertIntoCifNormLogStatement=tfsConn.prepareStatement(insertIntoCifNormLogQuery);
			ResultSet recordsToUpdateResultSet=recordsToUpdateStatement.executeQuery();
			
			while(recordsToUpdateResultSet.next()){
                System.out.println("DOCUMENT NUMBER TO UPDATE : " + recordsToUpdateResultSet.getString("DOCUMENTNUMBER"));

                recordsToInsertStatement.setString(1, recordsToUpdateResultSet.getString("NCIFN"));
				recordsToInsertStatement.setString(2, recordsToUpdateResultSet.getString("NAANO"));
				recordsToInsertStatement.setString(3, recordsToUpdateResultSet.getString("NFCODE"));
				recordsToInsertStatement.setString(4, recordsToUpdateResultSet.getBigDecimal("NFSEQ").toString());
				recordsToInsertStatement.setString(5, recordsToUpdateResultSet.getString("FACREF"));

                System.out.println("CIFNAME " + recordsToUpdateResultSet.getString("CIFNAME") + " = " + "NEWCIFNAME " + recordsToUpdateResultSet.getString("NEWCIFNAME"));
                if (recordsToUpdateResultSet.getString("CIFNAME") != null &&
                        !recordsToUpdateResultSet.getString("CIFNAME").equals(recordsToUpdateResultSet.getString("NEWCIFNAME"))) {

                    System.out.println("updating new cifname with " + recordsToUpdateResultSet.getString("NEWCIFNAME"));
                    recordsToInsertStatement.setString(6, recordsToUpdateResultSet.getString("NEWCIFNAME"));
                } else {
                    recordsToInsertStatement.setString(6, null);
                }

                System.out.println("MAINCIFNAME " + recordsToUpdateResultSet.getString("MAINCIFNAME") + " = " + "NEWMAINCIFNAME " + recordsToUpdateResultSet.getString("NEWMAINCIFNAME"));
                if (recordsToUpdateResultSet.getString("MAINCIFNAME") != null &&
                        !recordsToUpdateResultSet.getString("MAINCIFNAME").equals(recordsToUpdateResultSet.getString("NEWMAINCIFNAME"))) {
                    System.out.println("updating new main cifname with " + recordsToUpdateResultSet.getString("NEWMAINCIFNAME"));
                    recordsToInsertStatement.setString(7, recordsToUpdateResultSet.getString("NEWMAINCIFNAME"));
                } else {
                    recordsToInsertStatement.setString(7, null);
                }

				recordsToInsertStatement.setString(8, recordsToUpdateResultSet.getString("OCFIN"));
				recordsToInsertStatement.setString(9, recordsToUpdateResultSet.getString("OAANO"));
				recordsToInsertStatement.setString(10, recordsToUpdateResultSet.getString("OFCODE"));
				recordsToInsertStatement.setString(11, recordsToUpdateResultSet.getBigDecimal("OFSEQ").toString());

				recordsToInsertStatement.addBatch();

//                insertIntoCifNormLogStatement.setString(1,recordsToUpdateResultSet.getString("OCFIN"));
//                insertIntoCifNormLogStatement.setString(2,recordsToUpdateResultSet.getString("NCIFN"));
//                insertIntoCifNormLogStatement.setDate(3, new java.sql.Date(new Date().getTime()));
//
//                insertIntoCifNormLogStatement.addBatch();

                Map<String, String> cifNormaliationMap = new HashMap<String, String>();
                cifNormaliationMap.put("oldCifNumber", recordsToUpdateResultSet.getString("OCFIN"));
                cifNormaliationMap.put("oldCifName", recordsToUpdateResultSet.getString("CIFNAME"));

                cifNormaliationMap.put("newCifNumber", recordsToUpdateResultSet.getString("NCIFN"));
                cifNormaliationMap.put("newCifName", recordsToUpdateResultSet.getString("NEWCIFNAME"));

                cifNormaliationMap.put("oldMainCifNumber", recordsToUpdateResultSet.getString("OAANO"));
                cifNormaliationMap.put("oldMainCifName", recordsToUpdateResultSet.getString("MAINCIFNAME"));

                cifNormaliationMap.put("newMainCifNumber", recordsToUpdateResultSet.getString("NAANO"));
                cifNormaliationMap.put("newMainCifName", recordsToUpdateResultSet.getString("NEWMAINCIFNAME"));

                cifNormalizationLogService.saveCifNormalizationLog(cifNormaliationMap, new Date(),
                        recordsToUpdateResultSet.getBigDecimal("OFSEQ"), recordsToUpdateResultSet.getBigDecimal("NFSEQ"));

                System.out.println("EXECUTING QUERY: " + recordsToInsertStatement.toString());

			}

				recordsToInsertStatement.executeBatch();
//                insertIntoCifNormLogStatement.executeBatch();
                tfsConn.commit();
		}catch(SQLException ex){
			System.out.println("\nError in Updating Trade Product Table:\n");
			ex.printStackTrace();
			throw ex;
		}finally{
			DbUtil.closeQuietly(tfsConn);
		}
	}

	private void updateTradeServiceReferences() throws SQLException{
        System.out.println("updateTradeServiceReferences .. ");
//        String getCifRecordsToUpdateQuery="SELECT CL.OCFIN,CL.OAANO,CL.OFCODE, "+
////				"CL.OFSEQ,CL.NCIFN,CL.NAANO,CL.NFCODE,CL.NFSEQ,CL.FACREF FROM "+
//                "CL.OFSEQ,CL.NCIFN, CL.CFNSNM, CL.NAANO,CL.NFCODE,CL.NFSEQ,CL.FACREF FROM "+
//				"CIFNORM_LNAALOG AS CL,(SELECT TS.CIFNUMBER,TS.MAINCIFNUMBER, " +
//				"TS.FACILITYTYPE,TS.FACILITYID FROM TRADESERVICE AS TS "+
//				"INNER JOIN PAYMENT AS PY ON TS.TRADESERVICEID=PY.TRADESERVICEID "+
//				"INNER JOIN PAYMENTDETAIL AS PD ON PY.ID=PD.PAYMENTID) AS RS "+
//				"WHERE "+
//				"RS.CIFNUMBER=CL.OCFIN AND RS.MAINCIFNUMBER=CL.OAANO AND "+
////				"RS.FACILITYTYPE=CL.OFCODE AND CAST(RS.FACILITYID AS DECIMAL(11)) = CL.OFSEQ";
//                "RS.FACILITYTYPE=CL.OFCODE AND RS.FACILITYID != '' AND CAST(RS.FACILITYID AS DECIMAL(11)) = CL.OFSEQ";
//        String getCifRecordsToUpdateQuery = "SELECT " +
////                "CL.OCFIN,CL.OAANO,CL.OFCODE, CL.OFSEQ,CL.NCIFN, CL.CFNSNM, CL.NAANO,CL.NFCODE,CL.NFSEQ,CL.FACREF " +
//                                            "RS.TRADESERVICEID, " +
//                                            "CL.NCIFN, CL.CFNSNM, CL.NAANO,CL.NFCODE,CL.NFSEQ,CL.FACREF " +
//                                            "FROM CIFNORM_LNAALOG CL " +
//                                            "INNER JOIN " +
//                                            "(SELECT " +
//                                            "TS.TRADESERVICEID, " +
//                                            "TS.CIFNUMBER, " +
//                                            "TS.MAINCIFNUMBER, " +
//                                            "TS.FACILITYTYPE, " +
//                                            "TS.FACILITYID " +
//                                            "FROM TRADESERVICE TS " +
//                                            "LEFT JOIN PAYMENT P ON " +
//                                            "P.TRADESERVICEID = TS.TRADESERVICEID " +
//                                            "LEFT JOIN PAYMENTDETAIL PD ON " +
//                                            "PD.PAYMENTID = P.ID) RS " +
//                                            "ON RS.CIFNUMBER=CL.OCFIN " +
//                                            "AND RS.MAINCIFNUMBER=CL.OAANO " +
//                                            "AND RS.FACILITYTYPE=CL.OFCODE " +
//                                            "AND RS.FACILITYID != '' AND CAST(RS.FACILITYID AS DECIMAL(11)) = CL.OFSEQ";

//        String getCifRecordsToUpdateQuery = "SELECT " +
//                "RS.TRADESERVICEID, " +
//        "CL.NCIFN, CL.CFNSNM, CL.NAANO,CL.NFCODE,CL.NFSEQ,CL.FACREF , CL.OCFIN " +
//        "FROM " +
//        "(SELECT " +
//        "TS.TRADESERVICEID, " +
//        "TS.CIFNUMBER, " +
//        "TS.MAINCIFNUMBER, " +
//        "TS.FACILITYTYPE, " +
//        "TS.FACILITYID " +
//        "FROM TRADESERVICE TS " +
//        ") RS " +
//        "INNER JOIN " +
//        "CIFNORM_LNAALOG CL " +
//        "ON RS.CIFNUMBER=CL.OCFIN " +
//        "AND RS.MAINCIFNUMBER=CL.OAANO " +
//        "LEFT JOIN " +
//        "PAYMENT P ON " +
//        "P.TRADESERVICEID = RS.TRADESERVICEID " +
//        "LEFT JOIN PAYMENTDETAIL PD ON " +
//        "PD.PAYMENTID = P.ID " +
//        "AND RS.FACILITYTYPE=CL.OFCODE " +
//        "AND RS.FACILITYID != '' AND CAST(RS.FACILITYID AS DECIMAL(11)) = CL.OFSEQ";

//        String getCifRecordsToUpdateQuery = "SELECT RS.TRADESERVICEID, CL.NCIFN, CL.CFNSNM, CL.NAANO, CL.NFCODE, CL.NFSEQ, CL.FACREF, CL.OCFIN FROM (" +
//                "SELECT TS.TRADESERVICEID, TS.CIFNUMBER, TS.MAINCIFNUMBER, PD.FACILITYTYPE, PD.FACILITYID FROM " +
//                "TRADESERVICE TS " +
//                "INNER JOIN PAYMENT P " +
//                "ON TS.TRADESERVICEID = P.TRADESERVICEID " +
//                "INNER JOIN PAYMENTDETAIL PD " +
//                "ON P.ID = PD.PAYMENTID " +
//                "WHERE TS.STATUS IN ('MARV', 'PENDING', 'PREPARED', 'CHECKED', 'RETURNED', 'RETURNED_TO_BRANCH', 'FOR_REVERSAL')) RS " +
//                "INNER JOIN " +
//                "CIFNORM_LNAALOG CL " +
//                "ON RS.FACILITYTYPE = CL.OFCODE AND " +
//                "RS.FACILITYID = CL.OFSEQ AND " +
//                "RS.CIFNUMBER = CL.OCFIN AND " +
//                "RS.MAINCIFNUMBER = CL.OAANO";

        String getCifRecordsToUpdateQuery = "SELECT RS.TRADESERVICEID, RS.CIFNUMBER, CL.OCFIN, CL.NCIFN, RS.MAINCIFNUMBER, CL.OAANO, CL.NAANO, CL.NFCODE, CL.NFSEQ, CL.FACREF, CL.OCFIN, CL.OFSEQ FROM (" +
                "SELECT TS.TRADESERVICEID, TS.CIFNUMBER, TS.MAINCIFNUMBER, TS.FACILITYTYPE AS FACILITYTYPE_1, PD.FACILITYTYPE AS FACILITYTYPE_2, TS.FACILITYID AS FACILITYID_1, PD.FACILITYID AS FACILITYID_2 FROM " +
                "TRADESERVICE TS " +
                "INNER JOIN PAYMENT P " +
                "ON TS.TRADESERVICEID = P.TRADESERVICEID " +
                "INNER JOIN PAYMENTDETAIL PD " +
                "ON P.ID = PD.PAYMENTID " +
                "WHERE TS.STATUS IN ('MARV', 'PENDING', 'PREPARED', 'CHECKED', 'RETURNED', 'RETURNED_TO_BRANCH', 'FOR_REVERSAL')) RS " +
                "INNER JOIN " +
                "CIFNORM_LNAALOG CL " +
                "ON " +
                "(RS.FACILITYTYPE_1 = CL.OFCODE OR RS.FACILITYTYPE_2 = CL.OFCODE) AND " +
                "(RS.FACILITYID_1 = CL.OFSEQ OR RS.FACILITYID_2 = CL.OFSEQ) AND " +
                "RS.CIFNUMBER = CL.OCFIN AND " +
                "RS.MAINCIFNUMBER = CL.OAANO";


        System.out.println("getCifRecordsToUpdateQuery\n" + getCifRecordsToUpdateQuery);
        try{
			initializeConnection();
			PreparedStatement recordsToUpdateStatement=tfsConn.prepareStatement(getCifRecordsToUpdateQuery);
			ResultSet recordsToUpdateResultSet=recordsToUpdateStatement.executeQuery();
			
			while(recordsToUpdateResultSet.next()){
//				updateTradeServiceTable(recordsToUpdateResultSet.getString("OCFIN"), // old cifnumber
//						recordsToUpdateResultSet.getString("OAANO"), // old main cif number
//						recordsToUpdateResultSet.getString("OFCODE"),
//						recordsToUpdateResultSet.getBigDecimal("OFSEQ").toString(),
//						recordsToUpdateResultSet.getString("FACREF"),
//						recordsToUpdateResultSet.getString("NCIFN"),
//						recordsToUpdateResultSet.getString("NAANO"),
//						recordsToUpdateResultSet.getString("NFCODE"),
//						recordsToUpdateResultSet.getBigDecimal("NFSEQ").toString());
                updateTradeServiceTable(recordsToUpdateResultSet.getString("TRADESERVICEID"),
                        recordsToUpdateResultSet.getString("NCIFN"),
                        recordsToUpdateResultSet.getString("NAANO"),
                        recordsToUpdateResultSet.getString("NFCODE"),
                        recordsToUpdateResultSet.getString("NFSEQ"),
                        recordsToUpdateResultSet.getString("FACREF"),
                        recordsToUpdateResultSet.getString("OFSEQ"));
			}
			
			tfsConn.commit();
		}catch(SQLException ex){
			System.out.println("\nError in Updating Trade Service References:\n");
			ex.printStackTrace();
			throw ex;
		}finally{
			DbUtil.closeQuietly(tfsConn);
		}
	}

    private void updateTradeServiceTable(String tradeServiceId, String newCifNumber, String newMainCifNumber,
                                         String newFacilityType, String newFacilityId, String facilityReferenceNumber, String oldFacilityId) {

        System.out.println("[start new updateTradeServiceTable]");
        System.out.println("params [tradeServiceId] = " + tradeServiceId);
        System.out.println("params [newCifNumber] = " + newCifNumber);
        System.out.println("params [newMainCifNumber] = " + newMainCifNumber);
        System.out.println("params [newFacilityType] = " + newFacilityType);
        System.out.println("params [newFacilityId] = " + newFacilityId);
        System.out.println("params [facilityReferenceNumber] = " + facilityReferenceNumber);

        TradeService tradeService = tradeServiceRepository.load(new TradeServiceId(tradeServiceId));
        Map<String, String> etsMap = new HashMap<String, String>();

        if (tradeService.getFacilityId().equals(oldFacilityId)) {
            tradeService.setCifNumber(newCifNumber);
            tradeService.setMainCifNumber(newMainCifNumber);
            tradeService.setFacilityType(newFacilityType);
            tradeService.setFacilityId(newFacilityId);

            Map<String, Object> details = tradeService.getDetails();

            // set new values
//        System.out.println("[TS]cifNumber " + details.get("cifNumber").toString() + " = " + newCifNumber);
            if(details.get("cifNumber") != null) {
                details.put("cifNumber", newCifNumber);
            }

//        System.out.println("[TS]mainCifNumber " + details.get("mainCifNumber").toString() + " = " + newMainCifNumber);
            if(details.get("mainCifNumber") != null) {
                details.put("mainCifNumber", newMainCifNumber);
            }

//        System.out.println("[TS]facilityType " + details.get("facilityType").toString() + " = " + newFacilityType);
            if(details.get("facilityType") != null) {
                details.put("facilityType", newFacilityType);
            }

//        System.out.println("[TS]facilityId " + details.get("facilityId") + " = " + newFacilityId);
            if(details.get("facilityId") != null) {
                details.put("facilityId", newFacilityId);
            }

//        System.out.println("[TS]facilityReferenceNumber " + details.get("facilityReferenceNumber") + " = " + facilityReferenceNumber);
            if(details.get("facilityReferenceNumber") != null) {
                details.put("facilityReferenceNumber", facilityReferenceNumber);
            }

            etsMap.put("serviceInstructionId", tradeService.getServiceInstructionId() != null ? tradeService.getServiceInstructionId().toString() : null);
            etsMap.put("cifNumber", newCifNumber);
            etsMap.put("mainCifNumber", newMainCifNumber);
            etsMap.put("facilityType", newFacilityType);
            etsMap.put("facilityId", newFacilityId);
            etsMap.put("facilityReferenceNumber", facilityReferenceNumber);

            tradeService.updateDetails(details);
            tradeServiceRepository.merge(tradeService);
        }

//        updatePaymentTable(tradeService.getTradeServiceId(),newFacilityType,
//                newFacilityId,facilityReferenceNumber);

        updatePaymentTable(tradeService.getTradeServiceId(),newFacilityType,
                newFacilityId,facilityReferenceNumber, new Integer(oldFacilityId));

        System.out.println("[end updateTradeServiceTable]");
        System.out.println("etsMap : " + etsMap);
        if (!etsMap.isEmpty()) {
            System.out.println("etsMap is not empty.. updating ets...");
            updateServiceInstructionTable(etsMap);
        }
    }

    private void updateServiceInstructionTable(Map<String, String> etsMap) {
        System.out.println("[start new updateServiceInstructionTable]");

        if (etsMap.get("serviceInstructionId") != null) {
            ServiceInstruction serviceInstruction = serviceInstructionRepository.load(new ServiceInstructionId(etsMap.get("serviceInstructionId")));

            Map<String, Object> etsDetails = serviceInstruction.getDetails();

            System.out.println("[ETS]cifNumber " + etsDetails.get("cifNumber") + " = " + etsMap.get("cifNumber"));
            if (etsDetails.get("cifNumber") != null) {
                etsDetails.put("cifNumber", etsMap.get("cifNumber"));
            }

            System.out.println("[ETS]mainCifNumber " + etsDetails.get("mainCifNumber") + " = " + etsMap.get("mainCifNumber"));
            if (etsDetails.get("mainCifNumber") != null) {
                etsDetails.put("mainCifNumber", etsMap.get("mainCifNumber"));
            }

            System.out.println("[ETS]faciltyType " + etsDetails.get("faciltyType") + " = " + etsMap.get("faciltyType"));
            if (etsDetails.get("faciltyType") != null) {
                etsDetails.put("faciltyType", etsMap.get("faciltyType"));
            }

            System.out.println("[ETS]facilityId " + etsDetails.get("facilityId") + " = " + etsMap.get("facilityId"));
            if (etsDetails.get("facilityId") != null) {
                etsDetails.put("facilityId", etsMap.get("facilityId"));
            }

            System.out.println("[ETS]facilityReferenceNumber " + etsDetails.get("facilityReferenceNumber") + " = " + etsMap.get("facilityReferenceNumber"));
            if (etsDetails.get("facilityReferenceNumber") != null) {
                etsDetails.put("facilityReferenceNumber", etsMap.get("facilityReferenceNumber"));
            }

            serviceInstruction.setDetails(etsDetails);

            serviceInstructionRepository.merge(serviceInstruction);

            System.out.println("[end updateServiceInstructionTable]");
        }
    }

	private void updateTradeServiceTable(String cifNumber,String mainCifNumber,
		String facilityType,String facilityId,String facilityReferenceNumber,String newCifNumber,String newMainCifNumber,
		String newFacilityType,String newFacilityId){

        System.out.println("[start updateTradeServiceTable]");
        System.out.println("params [cifNumber] = " + cifNumber); // old cif number
        System.out.println("params [mainCifNumber] = " + mainCifNumber); // old main cif number
        System.out.println("params [facilityType] = " + facilityType);
        System.out.println("params [facilityId] = " + facilityId);

        List<TradeService> tradeServiceList = tradeServiceRepository.load(
				cifNumber,mainCifNumber,facilityType,facilityId);

        List<Map<String, String>> etsMapList = new ArrayList<Map<String, String>>();

		for(TradeService tradeService : tradeServiceList){
            Map<String, String> etsMap = new HashMap<String, String>();
            etsMap.put("serviceInstructionId", tradeService.getServiceInstructionId() != null ? tradeService.getServiceInstructionId().toString() : null);
            etsMap.put("cifNumber", newCifNumber);
            etsMap.put("mainCifNumber", newMainCifNumber);
            etsMap.put("facilityType", newFacilityType);
            etsMap.put("facilityId", newFacilityId);
            etsMap.put("facilityReferenceNumber", facilityReferenceNumber);

            etsMapList.add(etsMap);

			tradeService.setCifNumber(newCifNumber);
			tradeService.setMainCifNumber(newMainCifNumber);
			tradeService.setFacilityType(newFacilityType);
			tradeService.setFacilityId(newFacilityId);

            Map<String, Object> details = tradeService.getDetails();

            // set new values
//            System.out.println("[TS]cifNumber " + details.get("cifNumber").toString() + " = " + newCifNumber);
			if(details.get("cifNumber") != null) {
				details.put("cifNumber", newCifNumber);
			}

//            System.out.println("[TS]mainCifNumber " + details.get("mainCifNumber").toString() + " = " + newMainCifNumber);
            if(details.get("mainCifNumber") != null) {
				details.put("mainCifNumber", newMainCifNumber);
			}

//            System.out.println("[TS]facilityType " + details.get("facilityType").toString() + " = " + newFacilityType);
			if(details.get("facilityType") != null) {
				details.put("facilityType", newFacilityType);
			}

//            System.out.println("[TS]facilityId " + details.get("facilityId").toString() + " = " + newFacilityId);
			if(details.get("facilityId") != null) {
				details.put("facilityId", newFacilityId);
			}

//            System.out.println("[TS]facilityReferenceNumber " + details.get("facilityReferenceNumber").toString() + " = " + facilityReferenceNumber);
			if(details.get("facilityReferenceNumber") != null) {
				details.put("facilityReferenceNumber", facilityReferenceNumber);
			}

            tradeService.updateDetails(details);
			tradeServiceRepository.merge(tradeService);
			updatePaymentTable(tradeService.getTradeServiceId(),newFacilityType,
					newFacilityId,facilityReferenceNumber);
		}

        System.out.println("[end updateTradeServiceTable]");
        updateServiceInstructionTable(etsMapList);
	}

    private void updateServiceInstructionTable(List<Map<String, String>> etsMapList) {
        System.out.println("[start updateServiceInstructionTable]");

        for (Map<String, String> etsMap: etsMapList) {
            ServiceInstruction serviceInstruction = serviceInstructionRepository.load(new ServiceInstructionId(etsMap.get("serviceInstructionId")));

            Map<String, Object> etsDetails = serviceInstruction.getDetails();

            System.out.println("[ETS]cifNumber " + etsDetails.get("cifNumber") + " = " + etsMap.get("cifNumber"));
            if (etsDetails.get("cifNumber") != null) {
                etsDetails.put("cifNumber", etsMap.get("cifNumber"));
            }

            System.out.println("[ETS]mainCifNumber " + etsDetails.get("mainCifNumber") + " = " + etsMap.get("mainCifNumber"));
            if (etsDetails.get("mainCifNumber") != null) {
                etsDetails.put("mainCifNumber", etsMap.get("mainCifNumber"));
            }

            System.out.println("[ETS]faciltyType " + etsDetails.get("faciltyType") + " = " + etsMap.get("faciltyType"));
            if (etsDetails.get("faciltyType") != null) {
                etsDetails.put("faciltyType", etsMap.get("faciltyType"));
            }

            System.out.println("[ETS]facilityId " + etsDetails.get("facilityId") + " = " + etsMap.get("facilityId"));
            if (etsDetails.get("facilityId") != null) {
                etsDetails.put("facilityId", etsMap.get("facilityId"));
            }

            System.out.println("[ETS]facilityReferenceNumber " + etsDetails.get("facilityReferenceNumber") + " = " + etsMap.get("facilityReferenceNumber"));
            if (etsDetails.get("facilityReferenceNumber") != null) {
                etsDetails.put("facilityReferenceNumber", etsMap.get("facilityReferenceNumber"));
            }

            serviceInstruction.setDetails(etsDetails);

            serviceInstructionRepository.merge(serviceInstruction);
        }

        System.out.println("[end updateServiceInstructionTable]");
    }

    private void executeUpdateCifName(String tradeServiceId, String newCifName, String newMainCifName) {
        System.out.println("executeUpdateCifName..");
        System.out.println("params [tradeServiceId] " + tradeServiceId);
        System.out.println("params [newCifName] " + newCifName);
        System.out.println("params [newMainCifName] " + newMainCifName);

        TradeService tradeService = tradeServiceRepository.load(new TradeServiceId(tradeServiceId));

        System.out.println("[TS]cifName " + tradeService.getCifName() + " = " + newCifName);
        if (tradeService.getCifName() != null && !tradeService.getCifName().equals(newCifName)) {
            tradeService.setCifName(newCifName);
        }

        System.out.println("[TS]mainCifName " + tradeService.getMainCifName() + " = " + newMainCifName);
        if (tradeService.getMainCifName() != null && !tradeService.getMainCifName().equals(newMainCifName)) {
            tradeService.setMainCifName(newMainCifName);
        }

        Map<String, Object> tsDetails = tradeService.getDetails();

        System.out.println("[TSD]cifName " + tradeService.getCifName() + " = " + newCifName);
        if (tsDetails.get("cifName") != null) {
            tsDetails.put("cifName", newCifName);
        }

        System.out.println("[TSD]mainCifName " + tradeService.getMainCifName() + " = " + newMainCifName);
        if (tsDetails.get("mainCifName") != null) {
            tsDetails.put("mainCifName", newMainCifName);
        }

        tradeService.updateDetails(tsDetails);

        tradeServiceRepository.merge(tradeService);

        executeUpdateServiceInstructionCifNames(tradeService.getServiceInstructionId(), newCifName, newMainCifName);
    }

    private void executeUpdateServiceInstructionCifNames(ServiceInstructionId serviceInstructionId, String newCifName, String newMainCifName) {
        System.out.println("executeUpdateServiceInstructionCifNames .. ");

        if (serviceInstructionId != null) {
            ServiceInstruction serviceInstruction = serviceInstructionRepository.load(serviceInstructionId);

            Map<String, Object> etsDetails = serviceInstruction.getDetails();

            System.out.println("[ETS]cifName" + etsDetails.get("cifName") + " = " + newCifName);
            if (etsDetails.get("cifName") != null) {
                etsDetails.put("cifName", newCifName);
            }

            System.out.println("[ETS]mainCifName" + etsDetails.get("mainCifName") + " = " + newMainCifName);
            if (etsDetails.get("mainCifName") != null) {
                etsDetails.put("mainCifName", newMainCifName);
            }

            serviceInstruction.setDetails(etsDetails);

            serviceInstructionRepository.merge(serviceInstruction);
        }
    }

    private void updateCifNames() throws SQLException {
        System.out.println("[start updateCifNames]");
        String getTradeServiceToUpdateQuery = "SELECT " +
                                              "TS.TRADESERVICEID, " +
                                              "(SELECT DISTINCT(CFNSNM) FROM CIFNORM_CFNMSTA WHERE CFNCIF = TS.CIFNUMBER) AS CIFNAME, " +
                                              "(SELECT DISTINCT(CFNSNM) FROM CIFNORM_CFNMSTA WHERE CFNCIF = TS.MAINCIFNUMBER) AS MAINCIFNAME " +
                                              "FROM TRADESERVICE TS " +
                                              "WHERE TS.STATUS IN ('MARV', 'PENDING', 'PREPARED', 'CHECKED', 'RETURNED', 'RETURNED_TO_BRANCH', 'FOR_REVERSAL')";


        try {
            initializeConnection();
            PreparedStatement recordsToUpdateStatement = tfsConn.prepareStatement(getTradeServiceToUpdateQuery);
            ResultSet recordsToUpdateResultSet = recordsToUpdateStatement.executeQuery();

            while(recordsToUpdateResultSet.next()) {
                executeUpdateCifName(recordsToUpdateResultSet.getString("TRADESERVICEID"),
                        recordsToUpdateResultSet.getString("CIFNAME"),
                        recordsToUpdateResultSet.getString("MAINCIFNAME"));
            }
            System.out.println("[end updateCifNames]");
            tfsConn.commit();
        } catch(SQLException ex) {
            System.out.println("\nError in Updating CIF Names:\n");
            ex.printStackTrace();
            throw ex;
        } finally {
            DbUtil.closeQuietly(tfsConn);
        }
    }

    private void updateTradeServiceTable(String cifNumber,String mainCifNumber,
                                         String facilityType,String facilityId,String facilityReferenceNumber,String newCifNumber,String newMainCifNumber,
                                         String newFacilityType,String newFacilityId,String newCifName){

        System.out.println("updating trade service table..");

        List<TradeService> tradeServiceList = tradeServiceRepository.load(
                cifNumber,mainCifNumber,facilityType,facilityId);
        Gson gson = new Gson();
        String tempCifNumber="";
        String tempMainCifNumber="";
        String tempFacilityType="";
        String tempFacilityId="";
        String tempFacilityReferenceNumber="";

        String tempCifName = "";

        for(TradeService tradeService : tradeServiceList){
            tradeService.setCifNumber(newCifNumber);
            tradeService.setMainCifNumber(newMainCifNumber);
            tradeService.setFacilityType(newFacilityType);
            tradeService.setFacilityId(newFacilityId);

            tradeService.setCifName(newCifName);

            Map<String,String> details = gson.fromJson(tradeService.getTradeServiceDetails(), new TypeToken<HashMap<String, String>>() {
            }.getType());
            tempCifNumber = details.get("cifNumber");
            tempMainCifNumber = details.get("mainCifNumber");
            tempFacilityType = details.get("facilityType");
            tempFacilityId = details.get("facilityId");
            tempFacilityReferenceNumber = details.get("facilityReferenceNumber");

            tempCifName = details.get("cifName");

            if(tempCifNumber != null) {
                details.remove("cifNumber");
                details.put("cifNumber", tempCifNumber);
            }
            if(tempMainCifNumber != null) {
                details.remove("mainCifNumber");
                details.put("mainCifNumber", tempMainCifNumber);
            }
            if(tempFacilityType != null) {
                details.remove("facilityType");
                details.put("facilityType", tempFacilityType);
            }
            if(tempFacilityId != null) {
                details.remove("facilityId");
                details.put("facilityId", tempFacilityId);
            }
            if(tempFacilityReferenceNumber != null) {
                details.remove("facilityReferenceNumber");
                details.put("facilityReferenceNumber", tempFacilityReferenceNumber);
            }

            System.out.println("tempCifName = " + tempCifName);
            if (tempCifName != null) {
                // no need to remove the key since if you put a new value on an existing key, it will just replace the old value.
                details.put("cifName", newCifName);
            }

            tradeService.setTradeServiceDetails(gson.toJson(details));
            tradeServiceRepository.merge(tradeService);
            updatePaymentTable(tradeService.getTradeServiceId(),newFacilityType,
                    newFacilityId,facilityReferenceNumber);
        }
    }
	
	private void updatePaymentTable(TradeServiceId tradeServiceId,
		String newFacilityType,String newFacilityId,String facilityReferenceNumber){
			List<Payment> payments = paymentRepository.getPaymentBy(tradeServiceId);
			for(Payment payment : payments){
				for(PaymentDetail paymentDetail : payment.getDetails()){
					paymentDetail.setFacilityId(Integer.parseInt(newFacilityId));
					paymentDetail.setFacilityReferenceNumber(facilityReferenceNumber);
					paymentDetail.setFacilityType(newFacilityType);
				}
				paymentRepository.merge(payment);
			}
	}

    private void updatePaymentTable(TradeServiceId tradeServiceId,
                                    String newFacilityType,String newFacilityId,String facilityReferenceNumber,
                                    Integer oldFacilityId){
        List<Payment> payments = paymentRepository.getPaymentBy(tradeServiceId);

        Boolean match = Boolean.FALSE;

        for(Payment payment : payments){
            for(PaymentDetail paymentDetail : payment.getDetails()){
                if (paymentDetail.getFacilityId().compareTo(oldFacilityId) == 0) {
                    paymentDetail.setFacilityId(Integer.parseInt(newFacilityId));
                    paymentDetail.setFacilityReferenceNumber(facilityReferenceNumber);
                    paymentDetail.setFacilityType(newFacilityType);

                    match = Boolean.TRUE;
                }
            }
            if (match == Boolean.TRUE) {
                paymentRepository.merge(payment);
            }
        }
    }
	
	
	private void initializeConnection() throws SQLException{
		if(tfsConn != null){
			tfsConn.close();
			tfsConn=null;
		}
		tfsConn=tfsDataSource.getConnection();
	}
}