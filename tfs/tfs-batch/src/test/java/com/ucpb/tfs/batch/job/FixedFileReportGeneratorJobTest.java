package com.ucpb.tfs.batch.job;

import com.ucpb.tfs.batch.report.dw.MovementRecord;
import com.ucpb.tfs.batch.util.BeanRowMapper;
import com.ucpb.tfs.batch.util.IOUtil;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.integration.MessageChannel;

import javax.sql.DataSource;
import java.io.*;
import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

/**
 */
@Ignore
public class FixedFileReportGeneratorJobTest {

    private static final String OUTPUT_FILE_PATH = "OUT.txt";
    private FixedFileReportGeneratorJob job;
    private DataSource ds;
    private ResultSet rs;
    private Connection connection;
    private PreparedStatement statement;
    private ResultSetMetaData metaData;
    private BeanRowMapper mapper;

    private File outputFile = new File("OUT_" + getExecutionDate(new Date()) + ".txt");



    @Before
    public void setup() throws SQLException {
        rs = mock(ResultSet.class);
        ds = mock(DataSource.class);
        connection = mock(Connection.class);
        statement = mock(PreparedStatement.class);
        metaData = mock(ResultSetMetaData.class);
        mapper = mock(BeanRowMapper.class);

        when(rs.next()).thenReturn(true,true,false);
        when(ds.getConnection()).thenReturn(connection);
        when(connection.prepareStatement("mockQuery")).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(rs);
        when(rs.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnType(1)).thenReturn(Types.VARCHAR);
        when(metaData.getColumnType(2)).thenReturn(Types.VARCHAR);
        when(metaData.getColumnType(3)).thenReturn(Types.DATE);
        when(metaData.getColumnCount()).thenReturn(3);
        when(rs.getString(1)).thenReturn("JUAN","JUANA");
        when(rs.getString(2)).thenReturn("DELA CRUZ","CHANGE");
        when(rs.getObject("FIRST_NAME")).thenReturn("JUAN","JUANA");
        when(rs.getObject("LAST_NAME")).thenReturn("DELA CRUZ","CHANGE");
        when(rs.getObject("DATE_HIRED")).thenReturn(new java.util.Date(1350361429912L), new java.util.Date(1350361429912L));
        //1350361429912 - October 16, 2012
        when(mapper.mapRow(any(ResultSet.class),anyInt())).thenReturn(buildMovementRecord());

        job = new FixedFileReportGeneratorJob("mockQuery",ds,mock(MessageChannel.class),"OUT");
        job.setMapper(mapper);


    }

    @Before
    public void clearWorkspace(){
        outputFile.delete();
    }

    @After
    public void cleanup(){
        outputFile.delete();
    }

    @Test
    public void successfullyWriteFixedFormatFile() throws SQLException {

        assertFalse(outputFile.exists());
        job.execute();
        verify(ds).getConnection();
        verify(ds).getConnection();
        verify(connection).prepareStatement("mockQuery");
        verify(rs,times(3)).next();
        assertTrue(outputFile.exists());
        List<String> contents = getFileContents();
        assertEquals(2,contents.size());
//        assertTrue(contents.get(0).matches("[909RG0000000835110101000000000PHP\\s][\\d]{6}[\\d]{6}[C0000010000300000000000100003000000909909DL]"));
        assertEquals("909RG0000000835110101000000000PHP " + formatDate(new java.util.Date()) +  formatDate(new java.util.Date(1350361429912L)) +"C0000010000300000000000100003000000909909"
                + StringUtils.leftPad("",40," ") + "20202020202020202020TD852000000001" ,contents.get(0));
    }

    private String formatDate(java.util.Date date){
        SimpleDateFormat format = new SimpleDateFormat("MMddyy");
        return format.format(date);
    }

    private MovementRecord buildMovementRecord(){
        MovementRecord record = new MovementRecord();
        record.setTransactionBranch("909");
        record.setBookCode("RG");
        record.setTransactionAccount("0000000835110101000");
        //transaction cost center - 000
        //transaction product code - 000
        record.setCurrencyType("PHP");
        //transaction posting date - 052010
        //
        record.setTransactionEffectiveDate(new java.util.Date(1350361429912L));
        record.setTransactionCode("C");
        record.setTransactionAmount(new BigDecimal("000001000030000.00"));
        record.setTransactionBaseAmount(new BigDecimal("000001000030000.00"));
        record.setSourceBranch("909");
        record.setRespondingBranch("909");
        record.setTransactionReferenceNumber("20202020202020202020");
        record.setTransactionSequenceNumber(Long.valueOf(1));

        return record;
    }


    private List<String> getFileContents(){
        List<String> fileContents = new ArrayList<String>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(outputFile));
            String line = reader.readLine();
            while(line != null){
                fileContents.add(line);
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            IOUtil.closeQuietly(reader);
        }
        return fileContents;
    }

    private String getExecutionDate(java.util.Date date){
        return new SimpleDateFormat("yyyyMMdd").format(date);
    }

}
