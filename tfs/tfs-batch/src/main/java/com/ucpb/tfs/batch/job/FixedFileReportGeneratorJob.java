package com.ucpb.tfs.batch.job;

import com.ancientprogramming.fixedformat4j.format.FixedFormatManager;
import com.ancientprogramming.fixedformat4j.format.impl.FixedFormatManagerImpl;
import com.ucpb.tfs.batch.report.dw.GLParameterRecord;
import com.ucpb.tfs.batch.report.dw.dao.SilverlakeLocalDao;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 */
public class FixedFileReportGeneratorJob<T> implements SpringJob{

    private static final String DEFAULT_FILENAME = "WIP.txt";

    private static FixedFormatManager manager = new FixedFormatManagerImpl();

    private final ExpressionParser parser = new SpelExpressionParser(new SpelParserConfiguration(true, true));

    private String query;

    private String filenamePrefix;

    private DataSource dataSource;

    private MessageChannel channel;

    private BeanRowMapper<T> mapper;

    private Object[] args;

    private static final SimpleDateFormat EXECUTION_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    private String filenameProperty;

    private SilverlakeLocalDao silverlakeLocalDao;

    public FixedFileReportGeneratorJob(String query, DataSource dataSource, MessageChannel channel){
        this.query = query;
        this.dataSource = dataSource;
        this.channel = channel;
    }

    public FixedFileReportGeneratorJob(String query, DataSource dataSource, MessageChannel channel, String filenamePrefix){
        this(query,dataSource,channel);
        this.filenamePrefix = filenamePrefix;
    }

    public FixedFileReportGeneratorJob(SilverlakeLocalDao silverlakeLocalDao, MessageChannel channel, String filenamePrefix) {
        this.silverlakeLocalDao = silverlakeLocalDao;
        this.channel = channel;
        this.filenamePrefix = filenamePrefix;
    }

    public void execute() {
        System.out.println("FixedFileReportGeneratorJob execute:"+ query);
        execute(query,new Date(),args);
    }

    public void execute(String reportDate) {
        System.out.println("FixedFileReportGeneratorJob execute:"+ query);
        //TODO
        execute(query,new Date(),args);
    }

    public void execute(String customQuery, Date executionDate, Object... arguments) {

        if (this.filenamePrefix.equalsIgnoreCase("TF_GL")) {  // Ugly, but asked to be done on the last minute
            executeQueryFromSibs(customQuery, executionDate, arguments);
        } else {
            executeQueryFromTfsDb(customQuery, executionDate, arguments);
        }
    }

    private void executeQueryFromSibs(String customQuery, Date executionDate, Object... arguments) {

        System.out.println(">>>>> executeQueryFromSibs: date = " + executionDate);
        PrintWriter writer = null;
        File tempFile = null;
        String actualFilename = "";

        try {

            actualFilename = filenamePrefix + "_" + getExecutionDate(executionDate) + ".txt";
            tempFile = new File(actualFilename);
            writer = new PrintWriter(new FileWriter(actualFilename));

            List<GLParameterRecord> parameterRecords = silverlakeLocalDao.getDwGlParameters();

            for (GLParameterRecord parameterRecord : parameterRecords) {

                writer.print(manager.export(parameterRecord).trim());

                // Force to write CR LF even in Linux
                StringBuilder sb = new StringBuilder();
                sb.append((char)13);
                sb.append((char)10);
                String crlf = sb.toString();
                writer.print(crlf);
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error writing file " +  actualFilename,e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Exception ", e);
        } finally {
            IOUtil.closeQuietly(writer);
        }

        sendToChannel(tempFile, actualFilename);
    }

    private void executeQueryFromTfsDb(String customQuery, Date executionDate, Object... arguments) {

        System.out.println(">>>>> executeQueryFromTfsDb: " + query + " | date:" + executionDate);
        Connection connection = null;
        PrintWriter writer = null;
        File tempFile = null;
        String actualFilename = "";

        try {

            actualFilename = filenamePrefix + "_" + getExecutionDate(executionDate) + ".txt";
            tempFile = new File(actualFilename);
            writer = new PrintWriter(new FileWriter(actualFilename));
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(customQuery);
            System.out.println(arguments);
            if(arguments!=null){ //Some batch jobs using this do not have arguments hence the necessity to differentiate
                mapArguments(ps,arguments);
            }
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
//                writer.println(manager.export(mapper.mapRow(rs,0)));
                writer.print(manager.export(mapper.mapRow(rs,0)).trim());

                // Force to write CR LF even in Linux
                StringBuilder sb = new StringBuilder();
                sb.append((char)13);
                sb.append((char)10);
                String crlf = sb.toString();
                writer.print(crlf);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database exception",e);
        } catch (IOException e) {
            throw new RuntimeException("Error writing file " +  actualFilename,e);
        }finally{
            DbUtil.closeQuietly(connection);
            IOUtil.closeQuietly(writer);
        }

        sendToChannel(tempFile, actualFilename);
    }

    private void sendToChannel(File file, String filename){
        MessageBuilder<File> builder = MessageBuilder.withPayload(file);
        builder.setHeader(getFilenameProperty(), filename);
        channel.send(builder.build());
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

    private String getExecutionDate(Date date){
        return EXECUTION_DATE_FORMAT.format(date);
    }

    public String getFilenameProperty() {
        return filenameProperty != null ? filenameProperty : DEFAULT_FILENAME;
    }

    public void setFilenameProperty(String filenameProperty) {
        this.filenameProperty = filenameProperty;
    }

}
