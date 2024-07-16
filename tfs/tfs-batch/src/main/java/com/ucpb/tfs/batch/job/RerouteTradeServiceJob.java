package com.ucpb.tfs.batch.job;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Date;

import javax.sql.DataSource;

import com.ucpb.tfs.batch.util.DbUtil;
/**
 * User: ITDRMM
 * Date: 9/21/2020
 * 
 * Reroute TradeService Job to a different user
 * 
 */
public class RerouteTradeServiceJob {
    
    private final DataSource dataSource;

    public RerouteTradeServiceJob(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    public String execute(String documentNumber, String targetUser) {
        
        System.out.println("com.ucpb.tfs.batch.job.RerouteTradeServiceJob.execute()");    	
        Connection conn = null;
        String P_MESSAGE = "";
        Integer P_RETVAL = 0;
        
        try {
        	
        	conn = dataSource.getConnection();
        	CallableStatement stmt = conn.prepareCall("{call REROUTE_TRADESERVICE(?,?,?,?)}");
        	stmt.setString(1,documentNumber);
        	stmt.setString(2, targetUser);
        	stmt.registerOutParameter(3, Types.INTEGER);
        	stmt.registerOutParameter(4, Types.VARCHAR);
        	stmt.execute();
            System.out.println("Return Value: " + stmt.getInt(3));
            System.out.println("Return Message: " + stmt.getString(4));
            
            P_RETVAL = stmt.getInt(3);
            if(P_RETVAL == 2){
                P_MESSAGE = P_RETVAL.toString() + stmt.getString(4);
            }else{
                P_MESSAGE = stmt.getString(4);
            }
            stmt.close();
            conn.commit();
            System.out.println("\n############################### [" + new Date() + "] EXECUTED: RerouteTradeServiceJob");

        } catch (SQLException ex) {
        	System.out.println("Error" + ex.toString());
        	P_MESSAGE = "SQLException: " + ex.getMessage();
        } finally {
            DbUtil.closeQuietly(conn);
        }
        
        return P_MESSAGE;
   }
}