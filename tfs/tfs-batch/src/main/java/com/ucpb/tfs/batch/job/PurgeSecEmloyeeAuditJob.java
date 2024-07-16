package com.ucpb.tfs.batch.job;

import com.ucpb.tfs.batch.util.DbUtil;
import org.apache.commons.lang.time.DateUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * User: IPCVal
 * Date: 8/14/14
 */
public class PurgeSecEmloyeeAuditJob {

    private final DataSource dataSource;

    public PurgeSecEmloyeeAuditJob(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void execute() {

        Date now = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        Date oneMonthAgo = DateUtils.addMonths(now, -1);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String oneMonthAgoStr = df.format(oneMonthAgo);

        System.out.println("oneMonthAgo = " + oneMonthAgo.toString());
        System.out.println("oneMonthAgoStr = " + oneMonthAgoStr);

        StringBuilder sb = new StringBuilder("");
        sb.append("delete from sec_employee_audit where id in ( ");
        sb.append("select a.id from sec_employee_audit a ");
        sb.append("join revinfo b on a.rev_id = b.rev ");
        sb.append("where ");
        // sb.append("a.id = 'CBYSA02' and ");  // For testing only, comment out afterwards
        sb.append("days(TIMESTAMP(DATE('1970-01-01'), TIME('00:00:00')) + (INT(CURRENT TIMEZONE/10000)) HOURS + (b.revtstmp/1000) SECONDS) < days(cast(? as TIMESTAMP))");
        sb.append(")");

        Connection connection = null;
        try {

            connection = dataSource.getConnection();

            PreparedStatement ps = connection.prepareStatement(sb.toString());

            ps.setString(1, oneMonthAgoStr);
            ps.executeUpdate();

            connection.commit();

            System.out.println("\n############################### [" + new Date() + "] EXECUTED: PurgeSecEmloyeeAuditJob");

        } catch (Exception e) {
            System.out.println("############################### [" + new Date() + "] EXCEPTION: PurgeSecEmloyeeAuditJob.execute()");
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
