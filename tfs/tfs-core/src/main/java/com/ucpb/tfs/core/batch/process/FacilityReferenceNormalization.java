package com.ucpb.tfs.core.batch.process;

import com.ucpb.tfs.batch.report.dw.dao.SilverlakeLocalDao;
import com.ucpb.tfs.batch.facility.FacilityReference;
import com.ucpb.tfs.batch.util.DbUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by Marv on 2/25/14.
 */

/*  PROLOGUE:
 * 	(revision)
	SCR/ER Number:  20160613-044
	SCR/ER Description: Program abnormally terminates during SIBS DB access time-out.
	[Revised by:] Allan Comboy Jr.
	[Date Deployed:]  06/14/2016
	Program [Revision] Details: to reconnect when disconnected to sibs (for 4 additional programs)
	PROJECT: CORE
	MEMBER TYPE  : Groovy
	Project Name: BatchRestServices
 */

/**
 * PROLOGUE
 *  Description: Replaced usage of silverlakeDao into silverlakeLocalDao
 *  [Revised by:] Cedrick C. Nungay
 *  [Date revised:] 01/25/2024
*/
public class FacilityReferenceNormalization {

    private SilverlakeLocalDao silverlakeLocalDao;
    private final DataSource tfsDataSource;
    private Connection CONNECTION = null;

    private final String POPULATE_REF_LNAPPF_QUERY = "INSERT INTO REF_LNAPPF " +
            "(AFCIF_NO, AFFCDE) " +
            "VALUES " +
            "(?,?)";

    private final String CLEAR_REF_LNAPPF_QUERY = "DELETE FROM REF_LNAPPF";

    public FacilityReferenceNormalization(DataSource tfsDataSource) {
        this.tfsDataSource = tfsDataSource;
    }

    public void setSilverlakeLocalDao(SilverlakeLocalDao silverlakeLocalDao) {
        this.silverlakeLocalDao = silverlakeLocalDao;
    }

    public boolean executeFacilityReferenceNormalization(){
        boolean result = false;

        try {
            cloneLnappfTable();

            result = true;
        } catch (SQLException sqlE) {
            if (!sqlE.getNextException().equals(null)) {
            	
                sqlE.getNextException().printStackTrace();
            
            } else {
                sqlE.printStackTrace();
               
            }
        } catch (Exception e) {
        	
            e.printStackTrace();
           
        }

        return result;
    }

    public List<FacilityReference> getAllLnappfEntries() {
        List<FacilityReference> lnappfEntries = silverlakeLocalDao.getFacilityReferenceEntries();

        return lnappfEntries;
    }

    private void cloneLnappfTable() throws SQLException {

        try {
            initializeConnection();

            PreparedStatement populateRefLnappfPS = CONNECTION.prepareStatement(POPULATE_REF_LNAPPF_QUERY);
            PreparedStatement clearRefLnappfPS = CONNECTION.prepareStatement(CLEAR_REF_LNAPPF_QUERY);

            // clear first
            clearRefLnappfPS.executeUpdate();
try{
            List<FacilityReference> lnappfEntries = silverlakeLocalDao.getFacilityReferenceEntries();

            for (FacilityReference facilityReference : lnappfEntries) {
                populateRefLnappfPS.setString(1, facilityReference.getAFCIF_NO());
                populateRefLnappfPS.setString(2, facilityReference.getAFFCDE());

                populateRefLnappfPS.addBatch();
            }
}catch(Exception e){	
	e.printStackTrace();
    throw new IllegalArgumentException("UNABLE TO CONNECT TO SIBS");
}

            populateRefLnappfPS.executeBatch();

            CONNECTION.commit();
        } catch (SQLException sqlE) {
            System.out.println("\nError in Cloning LNAPPF table:\n");
            sqlE.printStackTrace();

            if (sqlE.getNextException() != null) {
                System.out.println("------------");
                sqlE.getNextException().printStackTrace();
                System.out.println("------------");
            }

            throw sqlE;
        } finally{
            DbUtil.closeQuietly(CONNECTION);
        }
    }

    private void initializeConnection() throws SQLException{
        if(CONNECTION != null){
            CONNECTION.close();
            CONNECTION = null;
        }

        CONNECTION = tfsDataSource.getConnection();
    }
    
    public void deleteOutstandingUnapprovedFacilityAvailment(String[] documentNumbers){
    	try{
    	int i = silverlakeLocalDao.deleteOutstandingUnapprovedFacilityAvailment(documentNumbers);
    	System.out.println("script success: " + i);
    }catch(Exception e){
  		 e.printStackTrace();
  	     throw new IllegalArgumentException("UNABLE TO CONNECT TO SIBS");
          
  	}
    }
    
    public List<Map<String,?>> test(String[] documentNumbers){
    	try{
    	return silverlakeLocalDao.test(documentNumbers);
    	}catch(Exception e){
     		 e.printStackTrace();
     	     throw new IllegalArgumentException("UNABLE TO CONNECT TO SIBS");
     		
             
     	}
    	
    }
}
