package com.ucpb.tfs.batch.job;

import com.ancientprogramming.fixedformat4j.format.FixedFormatManager;
import com.ancientprogramming.fixedformat4j.format.impl.FixedFormatManagerImpl;
import com.ucpb.tfs.batch.report.dw.MovementRecord;
import com.ucpb.tfs.batch.util.BeanRowMapper;
import com.ucpb.tfs.batch.util.DbUtil;
import com.ucpb.tfs.batch.util.IOUtil;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.support.MessageBuilder;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 */
public class GlReportGeneratorJob implements SpringJob{

    private static final String DEFAULT_FILENAME = "WIP.txt";

    private static final SimpleDateFormat EXECUTION_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
//    private static final SimpleDateFormat EXECUTION_DATE_FORMAT = new SimpleDateFormat("MMdd");
    private static final SimpleDateFormat DW_EXECUTION_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    // private static final SimpleDateFormat EXECUTION_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    private static final SimpleDateFormat TS_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static final String DEFAULT_FILENAME_PROPERTY = "filename";

    public static final String DEFAULT_DW_FILENAME_PREFIX = "";

    public static final String DEFAULT_DW_FILENAME_PROPERTY = "dwfilename";

    private static FixedFormatManager manager = new FixedFormatManagerImpl();

    private final ExpressionParser parser = new SpelExpressionParser(new SpelParserConfiguration(true, true));

    private String query;

    private final DataSource dataSource;

    private MessageChannel channel;

    private BeanRowMapper<MovementRecord> mapper;

    private Object[] args;

    private String updateSql;

    private String filenamePrefix;

    private String filenameProperty;

    private String dwFilenamePrefix;

    private String dwFilenameProperty;
    private static final String DEFAULT_UPDATE_SQL = "UPDATE INT_ACCENTRYACTUAL SET POSTINGDATE = ? WHERE ID = ?";

    
    public GlReportGeneratorJob(String query, DataSource dataSource, MessageChannel channel){
        this.query = query;
        this.dataSource = dataSource;
        this.channel = channel;
    }

    public GlReportGeneratorJob(String query, DataSource dataSource, MessageChannel channel, String filenamePrefix, String dwFilenamePrefix) {
        this(query,dataSource,channel);
        this.filenamePrefix = filenamePrefix;
        this.dwFilenamePrefix = dwFilenamePrefix;
    }

    public void execute() {
        System.out.println("execute 00");
        execute(query,new Date(),updateSql,args);
    }

    public void execute(String reportDate) {
        System.out.println("execute 01");
        execute(query,new Date(),updateSql,args);
    }

    public void execute(String customQuery, Date executionDate, Object... arguments) {
        System.out.println("execute 02");
        Connection connection = null;
        PrintWriter writer = null;
        File tempFile = null;
        String actualFilename = "";
        String dwFileName = "";

        try {
            System.out.println(executionDate);
            String execDate = getExecutionDate(executionDate);
            String dwExecDate = getExecutionDate(executionDate);
            actualFilename = filenamePrefix + "_" + execDate + ".txt";
            dwFileName = dwFilenamePrefix + "_" + dwExecDate + ".txt";
            // actualFilename = filenamePrefix;
            System.out.println("actualFilename:"+actualFilename);
            System.out.println("dwFileName:"+dwFileName);
            System.out.println("customQuery:"+customQuery);
            tempFile = new File(actualFilename);

            writer = new PrintWriter(new FileWriter(actualFilename));

            connection = dataSource.getConnection();

            String executionDateTimeStampString = TS_DATE_FORMAT.format(executionDate) +" 00:00:00";
            System.out.println("timestamp execution date:"+executionDateTimeStampString);

            //TODO: make use of ISPOSTED instead of getting the date and union queries of ISPOSTED <> 1
            PreparedStatement ps = connection.prepareStatement(customQuery);
            ps.setString(1,executionDateTimeStampString);			
			ps.setString(2,TS_DATE_FORMAT.format(executionDate));
           // ps.setString(2,executionDateTimeStampString); used ISPOSTED instead of getting the date
//            ps.setString(1,TS_DATE_FORMAT.format(executionDate));

            System.out.println("EXECUTING QUERY: " + ps.toString());
            
            
            System.out.println("\narguments[0] = " + arguments[0]);
            System.out.println("formatted date = " + TS_DATE_FORMAT.format(executionDate) + "\n");

            // mapArguments(ps,arguments);
            
            PreparedStatement update = connection.prepareStatement(DEFAULT_UPDATE_SQL);

            ResultSet rs = ps.executeQuery();

            Long counter = new Long("1");
            while(rs.next()) {
                MovementRecord record = mapper.mapRow(rs,0);
				record.setTransactionEffectiveDate(executionDate);
                record.setTransactionSequenceNumber(rs.getLong("ID"));
                counter = counter + new Long("1");
                writer.print(manager.export(record));

                // Force to write CR LF even in Linux
                StringBuilder sb = new StringBuilder();
                sb.append((char)13);
                sb.append((char)10);
                String crlf = sb.toString();

                writer.print(crlf);

                update.setTimestamp(1, new Timestamp(executionDate.getTime()));
                update.setLong(2, record.getTransactionSequenceNumber());

                update.executeUpdate();
            }
            
            
            connection.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database exception",e);
        } catch (IOException e) {
            throw new RuntimeException("Error writing file " +  actualFilename, e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            DbUtil.closeQuietly(connection);
            IOUtil.closeQuietly(writer);

        }

        sendToChannel(tempFile,actualFilename, dwFileName);
    }

    // private void sendToChannel(File file,String filename){
    private <T> void sendToChannel(T payload, String filename, String dwFilename) {

        // MessageBuilder<File> builder = MessageBuilder.withPayload(file);
        MessageBuilder<T> builder = MessageBuilder.withPayload(payload);
        builder.setHeader(getFilenameProperty(), filename);
        builder.setHeader(getDwFilenameProperty(), dwFilename);
        channel.send(builder.build());

        System.out.println("\n############################### GlReportGeneratorJob.sendToChannel EXECUTED! \n");
    }

    private void mapArguments(PreparedStatement ps, Object... arguments) throws SQLException {
        if(arguments != null){
            for(int ctr = 0; ctr < arguments.length; ctr++){
                ps.setObject(ctr + 1,arguments[ctr]);
            }
        }
    }

    public void setMapper(BeanRowMapper mapper) {
        this.mapper = mapper;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setUpdateSql(String updateSql) {
        this.updateSql = updateSql;
    }


    public String getDwFilenamePrefix(){
        return dwFilenamePrefix != null ? dwFilenamePrefix : DEFAULT_DW_FILENAME_PREFIX;
    }

    public String getFilenameProperty() {
        return filenameProperty != null ? filenameProperty : DEFAULT_FILENAME_PROPERTY;
    }

    public void setFilenameProperty(String filenameProperty) {
        this.filenameProperty = filenameProperty;
    }

    private String getExecutionDate(Date date){
        return EXECUTION_DATE_FORMAT.format(date);
    }

    private String getDwExecutionDate(Date date){
        return DW_EXECUTION_DATE_FORMAT.format(date);
    }

    public void setDwFilenameProperty(String dwFilenameProperty){
        this.dwFilenameProperty = dwFilenameProperty;
    }
    public String getDwFilenameProperty() {
        return dwFilenameProperty != null ? dwFilenameProperty : DEFAULT_DW_FILENAME_PROPERTY;
    }
}
