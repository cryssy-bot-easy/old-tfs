package com.ucpb.tfs.batch.job;

import com.ucpb.tfs.batch.report.dw.MovementRecord;
import com.ucpb.tfs.batch.util.BeanRowMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.integration.MessageChannel;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import static org.mockito.Mockito.*;

/**
 */
public class GlReportGeneratorJobTest {

    private GlReportGeneratorJob glReportGeneratorJob;

    private DataSource ds;

    private MessageChannel messageChannel;

    private Connection connection;

    private PreparedStatement queryPs;

    private PreparedStatement updatePs;

    private ResultSet queryResult;

    private BeanRowMapper mapper;

    @Before
    public void setup() throws SQLException {
        ds = mock(DataSource.class);
        messageChannel = mock(MessageChannel.class);
        mapper = mock(BeanRowMapper.class);
        connection = mock(Connection.class);
        queryPs = mock(PreparedStatement.class);
        updatePs = mock(PreparedStatement.class);
        queryResult = mock(ResultSet.class);

        when(ds.getConnection()).thenReturn(connection);

        MovementRecord record = new MovementRecord();
        record.setTransactionSequenceNumber(Long.valueOf("123"));

        when(mapper.mapRow(any(ResultSet.class),eq(0))).thenReturn(record);

        glReportGeneratorJob = new GlReportGeneratorJob("custom query",ds,messageChannel,"file","dwfile");
        glReportGeneratorJob.setUpdateSql("update sql");
        glReportGeneratorJob.setMapper(mapper);


    }

    @Test
    public void runUpdateSqlForEveryResultSet() throws SQLException {
        when(connection.prepareStatement("query sql")).thenReturn(queryPs);
        when(queryPs.executeQuery()).thenReturn(queryResult);
        when(connection.prepareStatement("update sql")).thenReturn(updatePs);
        when(queryResult.next()).thenReturn(true,true,true,false);

        glReportGeneratorJob.execute("query sql",new Date(),"update sql",null);

        verify(ds).getConnection();
        verify(queryResult,times(4)).next();
        verify(updatePs,times(3)).executeUpdate();

    }



}
