package com.ucpb.tfs.batch.job;

import com.ucpb.tfs.batch.util.FileUtil;
import com.ucpb.tfs.batch.util.IOUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.integration.MessageChannel;

import javax.sql.DataSource;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
public class CsvReportGeneratorJobTest {

    private static final String OUTPUT_FILE_PATH = "'tfs-batch/src/test/resources/OUT.csv'";
    private CsvReportGeneratorJob job;
    private DataSource ds;
    private ResultSet rs;
    private Connection connection;
    private PreparedStatement statement;
    private ResultSetMetaData metaData;

    private File outputFile = new File("tfs-batch/src/test/resources/OUT.csv");
    public static final char DEFAULT_QUOTE_CHARACTER = '"';


    @Before
    public void setup() throws SQLException {
        rs = mock(ResultSet.class);
        ds = mock(DataSource.class);
        connection = mock(Connection.class);
        statement = mock(PreparedStatement.class);
        metaData = mock(ResultSetMetaData.class);

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
        when(rs.getObject(1)).thenReturn("JUAN","JUANA");
        when(rs.getObject(2)).thenReturn("DELA CRUZ","CHANGE");
        when(rs.getObject(3)).thenReturn(new Date(1350361429912L), new Date(1350361429912L));
        //1350361429912 - October 16, 2012

        job = new CsvReportGeneratorJob("mockQuery",ds,OUTPUT_FILE_PATH,mock(MessageChannel.class),DEFAULT_QUOTE_CHARACTER);
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
    public void successfullyWriteCsvFile() throws InterruptedException, IOException, SQLException {

       assertFalse(outputFile.exists());
       job.execute();
       Thread.sleep(5000);
        verify(connection).prepareStatement("mockQuery");
       assertTrue(outputFile.exists());
       List<String> contents = FileUtil.read(outputFile);
       assertEquals(2,contents.size());
       assertEquals("\"JUAN\",\"DELA CRUZ\",\"10/16/2012\"",contents.get(0));
       assertEquals("\"JUANA\",\"CHANGE\",\"10/16/2012\"",contents.get(1));
    }

    @Test
    public void successfullyExecuteCustomQueryAndParameters() throws SQLException, InterruptedException, IOException {
        //cancel out previous setting
        when(connection.prepareStatement("mockQuery")).thenReturn(null);
        when(connection.prepareStatement("mockQuery2")).thenReturn(statement);

        when(statement.executeQuery()).thenReturn(rs);
        when(rs.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnType(1)).thenReturn(Types.VARCHAR);
        when(metaData.getColumnType(2)).thenReturn(Types.VARCHAR);
        when(metaData.getColumnType(3)).thenReturn(Types.DATE);
        when(metaData.getColumnCount()).thenReturn(3);
        when(rs.getString(1)).thenReturn("JUAN2","JUANA2");
        when(rs.getString(2)).thenReturn("DELA CRUZ2","CHANGE2");
        when(rs.getObject(1)).thenReturn("JUAN2","JUANA2");
        when(rs.getObject(2)).thenReturn("DELA CRUZ2","CHANGE2");
        when(rs.getObject(3)).thenReturn(new Date(1350361429912L), new Date(1350361429912L));

        assertFalse(outputFile.exists());
        job.execute("mockQuery2","param1","param2");
        Thread.sleep(5000);
        verify(connection).prepareStatement("mockQuery2");
        verify(statement).setObject(1,"param1");
        verify(statement).setObject(2,"param2");
        assertTrue(outputFile.exists());
        List<String> contents = FileUtil.read(outputFile);
        assertEquals(2,contents.size());
        assertEquals("\"JUAN2\",\"DELA CRUZ2\",\"10/16/2012\"",contents.get(0));
        assertEquals("\"JUANA2\",\"CHANGE2\",\"10/16/2012\"",contents.get(1));
    }

}
