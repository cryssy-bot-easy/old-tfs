package com.ucpb.tfs.batch.job;

import au.com.bytecode.opencsv.CSVWriter;

import com.ucpb.tfs.batch.util.DbUtil;
import com.ucpb.tfs.batch.util.IOUtil;
import com.ucpb.tfs.batch.util.StringArrayMapper;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 */
public class CsvReportGeneratorJob implements SpringJob{

    private final ExpressionParser parser = new SpelExpressionParser(new SpelParserConfiguration(true, true));

    private static final String DEFAULT_FILENAME = "WIP.csv";

    private static final char DEFAULT_SEPARATOR = ',';

    private String query;

    private String filename;

    private char separator = DEFAULT_SEPARATOR;

    private final DataSource dataSource;

    private MessageChannel channel;

    private Object[] args;

    private char quoteCharacter = CSVWriter.NO_QUOTE_CHARACTER;

    private String lineEnd = CSVWriter.DEFAULT_LINE_END;

    private RowMapper<String[]> mapper = new StringArrayMapper();

    private static final SimpleDateFormat EXECUTION_DATE_FORMAT = new SimpleDateFormat("MMdd");

    public CsvReportGeneratorJob(String query,DataSource dataSource, MessageChannel channel){
        this.query = query;
        this.dataSource = dataSource;
        this.channel = channel;
    }

    public CsvReportGeneratorJob(String query, DataSource dataSource, String filename,MessageChannel channel){
        this(query,dataSource,channel);
        this.filename = (String) parser.parseExpression(filename).getValue(new StandardEvaluationContext(this));
    }

    public CsvReportGeneratorJob(String query, DataSource dataSource, String filename,MessageChannel channel, char quoteCharacter){
        this(query,dataSource,channel);
        this.filename = (String) parser.parseExpression(filename).getValue(new StandardEvaluationContext(this));
        this.quoteCharacter = quoteCharacter;
    }


    public void execute(){
        execute(query, new Date(), args);
    }

    public void execute(String reportDate){
        //TODO
        execute(query, new Date(), args);
    }

    public void execute(String inputQuery, Object... args) {
        Connection connection = null;
        CSVWriter csvWriter = null;
        File tempFile = null;
        try {

            if (args.length > 0) {
                if (args[0] instanceof java.sql.Date) {
                    java.sql.Date executionDate = (java.sql.Date)args[0];
                    tempFile = new File(filename + "_" + getExecutionDate(executionDate) + ".txt");
                } else {
                    tempFile = new File(filename + ".txt");
                }
            } else {
                tempFile = new File(filename + ".txt");
            }

            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(inputQuery);
            mapArguments(ps,args);
            ResultSet rs = ps.executeQuery();

            System.out.println("\nFILE NAME = " + tempFile.getAbsolutePath() + "\n");

            StringBuilder sb = new StringBuilder();
            sb.append((char)13);
            sb.append((char)10);
            // csvWriter = new CSVWriter(new FileWriter(tempFile),getSeparator(),quoteCharacter,lineEnd);
            csvWriter = new CSVWriter(new FileWriter(tempFile),getSeparator(),quoteCharacter,sb.toString());

            while(rs.next()){
                csvWriter.writeNext(mapper.mapRow(rs,rs.getRow()));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database exception", e);
        } catch (IOException e) {
            throw new RuntimeException("Error writing file " +  getFilename(),e);
        } catch (Exception e) {
            throw new RuntimeException("Exception ", e);
        }finally{
            DbUtil.closeQuietly(connection);
            IOUtil.closeQuietly(csvWriter);
        }

        sendToChannel(tempFile);


    }
    
    public String execute(String inputQuery, String flag, String date) {
        Connection connection = null;
        String P_MESSAGE = "";
        Integer P_RETVAL = 0;
        try {

            connection = dataSource.getConnection();
            CallableStatement stmt = connection.prepareCall(query);

            stmt.registerOutParameter("P_RETVAL", Types.INTEGER);
            stmt.registerOutParameter("P_MESSAGE", Types.VARCHAR);

            System.out.println("Setting In Parameter");
            stmt.setString("P_DATE", date);
            
            mapArguments(stmt);
            
            ResultSet rs = stmt.executeQuery();

            P_RETVAL = stmt.getInt(2);
            if(P_RETVAL == 2){
                P_MESSAGE = P_RETVAL.toString() + stmt.getString(3);
            }else{
                P_MESSAGE = stmt.getString(3);
            }

        	if(rs.next() == false){
            	System.out.println("DO NOT CREATE CSV");
        	}else{
            	System.out.println("CREATING CSV");
                CSVWriter csvWriter = null;
                File tempFile = null;
            	tempFile = new File(date+ "_" + filename +".csv");
            	
	            StringBuilder sb = new StringBuilder();
	            sb.append((char)13);
	            sb.append((char)10);
	            csvWriter = new CSVWriter(new FileWriter(tempFile),getSeparator(),quoteCharacter,sb.toString());
	
	            while(rs.next()){
	                csvWriter.writeNext(mapper.mapRow(rs,rs.getRow()));
	            }
	            IOUtil.closeQuietly(csvWriter);
	            sendToChannel(tempFile);
	        	System.out.println("RETVAL : " + P_RETVAL);
	        	System.out.println("MESSAGE : " + P_MESSAGE);
        	}
        	connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Database exception", e);
        } catch (IOException e) {
            throw new RuntimeException("Error writing file " +  getFilename(),e);
        } catch (Exception e) {
            throw new RuntimeException("Exception ", e);
        }finally{
            DbUtil.closeQuietly(connection);
        }

        
        return P_MESSAGE;

    }

    private void sendToChannel(File file){
        MessageBuilder<File> builder = MessageBuilder.withPayload(file);
        channel.send(builder.build());
    }

    private String getFilename() {
        return filename != null ? filename : DEFAULT_FILENAME;
    }

    private char getSeparator() {
        return separator;
    }

    public char getQuoteCharacter() {
        return quoteCharacter;
    }

    public void setSeparator(char separator) {
        this.separator = separator;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public void setMapper(RowMapper<String[]> rowMapper){
        this.mapper = rowMapper;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setLineEnd(String lineEnd) {
        this.lineEnd = lineEnd;
    }

    private void mapArguments(PreparedStatement ps, Object... arguments) throws SQLException {
        if(arguments != null){
            for(int ctr = 0; ctr < arguments.length; ctr++){
                ps.setObject(ctr + 1,arguments[ctr]);
            }
        }
    }

    private String getExecutionDate(java.sql.Date date){
        return EXECUTION_DATE_FORMAT.format(date);
    }
}
