package com.ucpb.tfs.core.batch.process

import java.sql.Connection
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.sql.ResultSetMetaData; 
import org.apache.commons.lang.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;

import com.ucpb.tfs.batch.cif.CibsDetailsTable;
import com.ucpb.tfs.batch.cif.CibsMasterTable;
import com.ucpb.tfs.batch.util.DbUtil;

import javax.sql.DataSource;

class McoReportPersistenceService {
	
	private SimpleDateFormat dateConverter = new SimpleDateFormat("yyyy-MM-dd")
	private final DataSource tfsDataSource;
	private Connection tfsConn = null;
	private final String MCODB = "MCO_MONTH_END_DATA"
	private final String mcoInsertQuery = "INSERT INTO $MCODB (DOCUMENTNUMBER,TRANSACTIONTYPE,REPORTDATE,EXPIRYDATE,CURRENCY,OUTSTANDINGBALANCE,DOCUMENTTYPE) " +
	"VALUES (?,?,?,?,?,?,?)"
	
	public McoReportPersistenceService(DataSource tfsDataSource){
		this.tfsDataSource=tfsDataSource;
	}
	
	public boolean persist(String query,String type,String reportDate){
		boolean success=true;
		try{
			initializeConnection();
			
			PreparedStatement insertToMco = tfsConn.prepareStatement(mcoInsertQuery);
			PreparedStatement dataToSave = tfsConn.prepareStatement(query);
			
			ResultSet monthEndData = dataToSave.executeQuery();
			boolean hasDocTypeColumn = hasColumn("DOCUMENTTYPE",monthEndData.getMetaData())
			
			while(monthEndData.next()){
				insertToMco.setString(1, monthEndData.getString("DOCUMENTNUMBER"));									
				insertToMco.setString(2, type);
				insertToMco.setDate(3, new java.sql.Date(dateConverter.parse(reportDate).getTime()));
				insertToMco.setDate(4, monthEndData.getDate("EXPIRYDATE"))
				insertToMco.setString(5, monthEndData.getString("CURRENCY"));
				insertToMco.setString(6, monthEndData.getString("OUTSTANDINGBALANCE"));
				if(hasDocTypeColumn){
					insertToMco.setString(7, monthEndData.getString("DOCUMENTTYPE"));
				}else{
					insertToMco.setString(7, "N/A");
				}
				insertToMco.addBatch();
			}
			insertToMco.executeBatch();
			tfsConn.commit();
		}catch(SQLException e){
			e.printStackTrace();
			success=false;		
		}catch(Exception e){
			e.printStackTrace();
			success=false;
		}finally{
			DbUtil.closeQuietly(tfsConn);
		}
		return success;
	}
	
	public boolean deleteAllData(){
		boolean success = true;
		try{
			initializeConnection();
			
			PreparedStatement deleteMco = tfsConn.prepareStatement("DELETE FROM $MCODB");
			PreparedStatement restartIdColumn = tfsConn.prepareStatement("ALTER TABLE $MCODB ALTER COLUMN ID RESTART WITH 1");
			deleteMco.executeUpdate();
			restartIdColumn.executeUpdate();
			tfsConn.commit();
			
		}catch(SQLException e){
			e.printStackTrace();
			success=false;
		}catch(Exception e){
			e.printStackTrace();
			success=false;
		}finally{
			DbUtil.closeQuietly(tfsConn);
		}
	}
	
	private boolean hasColumn(String columnName, ResultSetMetaData resultSetMetaData){
		if(StringUtils.isBlank(columnName) || resultSetMetaData == null){
			return false;
		}
		for(int x = 1;x <= resultSetMetaData.getColumnCount();x++){
			if(columnName.equalsIgnoreCase(resultSetMetaData.getColumnName(x))){
				return true;
			}
		}
		return false;
	}
	
	private void initializeConnection() throws SQLException{
		if(tfsConn != null){
			tfsConn.close();
			tfsConn=null;
		}
		tfsConn=tfsDataSource.getConnection();
	}
}
