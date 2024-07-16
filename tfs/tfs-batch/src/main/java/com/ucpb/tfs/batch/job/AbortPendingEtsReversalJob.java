package com.ucpb.tfs.batch.job;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ucpb.tfs.batch.util.DbUtil;

import javax.sql.DataSource;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

/**
 * User: IPCVal
 * Date: 7/23/13
 */
public class AbortPendingEtsReversalJob {

    private final DataSource dataSource;

    // For original TradeService
    private static final String UPDATE_SQL_ORIG_TRADE_SERVICE = "UPDATE TRADESERVICE SET MODIFIEDDATE = ?, STATUS = ? WHERE TRADESERVICEID = ?";
    // For original ETS
    private static final String UPDATE_SQL_ORIG_ETS = "UPDATE SERVICEINSTRUCTION SET STATUS = 'APPROVED', MODIFIEDDATE = ? WHERE SERVICEINSTRUCTIONID = ?";
    // For original TradeService Task
    private static final String UPDATE_SQL_ORIG_TASK_TRADE_SERVICE = "UPDATE TASK SET MODIFIEDDATE = ?, TASKSTATUS = ? WHERE TASKREFERENCENUMBER = ?";
    // For original ETS Task
    private static final String UPDATE_SQL_ORIG_TASK_ETS = "UPDATE TASK SET TASKSTATUS = 'APPROVED', MODIFIEDDATE = ? WHERE TASKREFERENCENUMBER = ?";

    // For reversal TradeService
    private static final String UPDATE_SQL_REV_TRADE_SERVICE = "UPDATE TRADESERVICE SET STATUS = 'ABORTED', MODIFIEDDATE = ? WHERE TRADESERVICEID = ?";
    // For reversal ETS
    private static final String UPDATE_SQL_REV_ETS = "UPDATE SERVICEINSTRUCTION SET STATUS = 'ABORTED', MODIFIEDDATE = ? WHERE SERVICEINSTRUCTIONID = ?";
    // For reversal TradeService Task
    private static final String UPDATE_SQL_REV_TASK_TRADE_SERVICE = "UPDATE TASK SET TASKSTATUS = 'ABORTED', MODIFIEDDATE = ? WHERE TASKREFERENCENUMBER = ?";
    // For reversal ETS Task
    private static final String UPDATE_SQL_REV_TASK_ETS = "UPDATE TASK SET TASKSTATUS = 'ABORTED', MODIFIEDDATE = ? WHERE TASKREFERENCENUMBER = ?";

    public AbortPendingEtsReversalJob(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void execute() {

        Connection connection = null;
        try {

            connection = dataSource.getConnection();

            // For original TradeService
            PreparedStatement psOrigTradeService = connection.prepareStatement(UPDATE_SQL_ORIG_TRADE_SERVICE);
            // For original ETS
            PreparedStatement psOrigEts = connection.prepareStatement(UPDATE_SQL_ORIG_ETS);
            // For original TradeService Task
            PreparedStatement psOrigTaskTradeService = connection.prepareStatement(UPDATE_SQL_ORIG_TASK_TRADE_SERVICE);
            // For original ETS Task
            PreparedStatement psOrigTaskEts = connection.prepareStatement(UPDATE_SQL_ORIG_TASK_ETS);

            // For reversal TradeService
            PreparedStatement psReversalTradeService = connection.prepareStatement(UPDATE_SQL_REV_TRADE_SERVICE);
            // For reversal ETS
            PreparedStatement psReversalEts = connection.prepareStatement(UPDATE_SQL_REV_ETS);
            // For reversal TradeService Task
            PreparedStatement psReversalTaskTradeService = connection.prepareStatement(UPDATE_SQL_REV_TASK_TRADE_SERVICE);
            // For reversal ETS Task
            PreparedStatement psReversalTaskEts = connection.prepareStatement(UPDATE_SQL_REV_TASK_ETS);

            StringBuilder sb = new StringBuilder("");
            sb.append("SELECT TS.TRADESERVICEID, TS.SERVICETYPE, TS.STATUS AS TSSTATUS, TS.DETAILS AS TSDETAILS, ");
            sb.append("SI.SERVICEINSTRUCTIONID, SI.DETAILS AS SIDETAILS, SI.STATUS AS SISTATUS ");
            sb.append("FROM TRADESERVICE TS ");
            sb.append("LEFT OUTER JOIN SERVICEINSTRUCTION SI ON TS.SERVICEINSTRUCTIONID = SI.SERVICEINSTRUCTIONID ");
            sb.append("WHERE TS.SERVICETYPE LIKE '%REVERSAL%' AND SI.STATUS IN ('PENDING','PREPARED','CHECKED')");

            PreparedStatement ps = connection.prepareStatement(sb.toString());
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {

                String reversalTradeServiceId = rs.getString("TRADESERVICEID");
                String reversalTradeServiceServiceType = rs.getString("SERVICETYPE");
                String reversalTradeServiceStatus = rs.getString("TSSTATUS");
                String reversalEtsId = rs.getString("SERVICEINSTRUCTIONID");
                String reversalEtsStatus = rs.getString("SISTATUS");

                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, String>>(){}.getType();
                Map<String, String> tsDetails = gson.fromJson(rs.getString("TSDETAILS"), type);
                Map<String, String> siDetails = gson.fromJson(rs.getString("SIDETAILS"), type);

                String originalTradeServiceId = tsDetails.get("originalTradeServiceId");
                String originalTradeServiceStatus = tsDetails.get("originalTradeServiceStatus");
                String originalTaskStatus = tsDetails.get("originalTaskStatus");
                String originalEtsId = siDetails.get("etsNumber");

/*
                System.out.println("\n############");
                System.out.println("reversalTradeServiceId = " + reversalTradeServiceId);
                System.out.println("reversalTradeServiceServiceType = " + reversalTradeServiceServiceType);
                System.out.println("reversalTradeServiceStatus = " + reversalTradeServiceStatus);
                System.out.println("reversalEtsId = " + reversalEtsId);
                System.out.println("reversalEtsStatus = " + reversalEtsStatus);
                System.out.println("**********");
                System.out.println("originalTradeServiceId = " + originalTradeServiceId);
                System.out.println("originalTradeServiceStatus = " + originalTradeServiceStatus);
                System.out.println("originalTaskStatus = " + originalTaskStatus);
                System.out.println("originalEtsId = " + originalEtsId);
                System.out.println("############");
*/

                // For original TradeService
                psOrigTradeService.setTimestamp(1, new Timestamp(new Date().getTime()));
                psOrigTradeService.setString(2, originalTradeServiceStatus);
                psOrigTradeService.setString(3, originalTradeServiceId);
                psOrigTradeService.executeUpdate();

                // For original ETS
                psOrigEts.setTimestamp(1, new Timestamp(new Date().getTime()));
                psOrigEts.setString(2, originalEtsId);
                psOrigEts.executeUpdate();

                // For original TradeService Task
                psOrigTaskTradeService.setTimestamp(1, new Timestamp(new Date().getTime()));
                psOrigTaskTradeService.setString(2, originalTaskStatus);
                psOrigTaskTradeService.setString(3, originalTradeServiceId);
                psOrigTaskTradeService.executeUpdate();

                // For original ETS Task
                psOrigTaskEts.setTimestamp(1, new Timestamp(new Date().getTime()));
                psOrigTaskEts.setString(2, originalEtsId);
                psOrigTaskEts.executeUpdate();

                // For reversal TradeService
                psReversalTradeService.setTimestamp(1, new Timestamp(new Date().getTime()));
                psReversalTradeService.setString(2, reversalTradeServiceId);
                psReversalTradeService.executeUpdate();

                // For reversal ETS
                psReversalEts.setTimestamp(1, new Timestamp(new Date().getTime()));
                psReversalEts.setString(2, reversalEtsId);
                psReversalEts.executeUpdate();

                // For reversal TradeService Task
                psReversalTaskTradeService.setTimestamp(1, new Timestamp(new Date().getTime()));
                psReversalTaskTradeService.setString(2, reversalTradeServiceId);
                psReversalTaskTradeService.executeUpdate();

                // For reversal ETS Task
                psReversalTaskEts.setTimestamp(1, new Timestamp(new Date().getTime()));
                psReversalTaskEts.setString(2, reversalEtsId);
                psReversalTaskEts.executeUpdate();
            }

            connection.commit();

            System.out.println("\n############################### [" + new Date() + "] EXECUTED: AbortPendingEtsReversalJob\n");

        } catch (Exception e) {
            System.out.println("############################### [" + new Date() + "] EXCEPTION: AbortPendingEtsReversalJob.execute()");
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            DbUtil.closeQuietly(connection);
        }
    }
}
