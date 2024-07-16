package com.ucpb.tfs.batch.job;

import com.ancientprogramming.fixedformat4j.format.FixedFormatManager;
import com.ancientprogramming.fixedformat4j.format.impl.FixedFormatManagerImpl;
import com.ucpb.tfs.batch.report.dw.GlisHandoffRecord;
import com.ucpb.tfs.batch.report.dw.GlisHandoffRecon;
import com.ucpb.tfs.batch.util.BeanRowMapper;
import com.ucpb.tfs.batch.util.DbUtil;
import com.ucpb.tfs.batch.util.IOUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.support.MessageBuilder;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 */
public class GlisReportGeneratorJob implements SpringJob {
    private MessageChannel channel;
    private final DataSource dataSource;
    private String query;
    private String reconQuery;
    private static FixedFormatManager manager = new FixedFormatManagerImpl();
    private BeanRowMapper<GlisHandoffRecord> mapper;
    private BeanRowMapper<GlisHandoffRecon> reconMapper;
    private String sourceSystemCode;
    private String updateSql;
    private static final String DATE_FORMAT = "MMddyyyy";
    private static final String DATE_FORMAT_SH = "MMddyy";
    private static String crlf; 


    public GlisReportGeneratorJob(MessageChannel channel, DataSource dataSource, String query, String reconQuery){
        this.channel = channel;
        this.dataSource = dataSource;
        this.query = query;
        this.reconQuery = reconQuery;
    }


    public void execute() {
        System.out.println("execute 00");
        execute(new Date());
    }

    public void execute(String reportDate) {
        System.out.println("execute 01");
        execute(new Date());
    }

    private void writeGlisFile(String fileName, List<GlisHandoffRecord> records, Date executionDate) {
        System.out.println("Writing GLIS file - " + fileName);
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(fileName));
            BigDecimal debitTotal = BigDecimal.ZERO;
            BigDecimal creditTotal = BigDecimal.ZERO;

            for(GlisHandoffRecord r : records) {
                if (r.getTransactionCode().equalsIgnoreCase("61") ||
                        r.getTransactionCode().equalsIgnoreCase("71")) {
                    debitTotal = debitTotal.add(r.getAmount());
                } else {
                    creditTotal = creditTotal.add(r.getAmount());
                }
            }

            writer.print("HR");
            writer.print(StringUtils.leftPad(String.valueOf(records.size()), 8, "0"));
            writer.print("DR");
            writer.print(StringUtils.leftPad(debitTotal.toString().replace(".", ""), 17, "0"));
            writer.print("CR");
            writer.print(StringUtils.leftPad(creditTotal.toString().replace(".", ""), 17, "0"));
            writer.print(crlf); 

            for(GlisHandoffRecord r : records) {
                r.setEffectiveDate(executionDate);
                writer.print(manager.export(r));
                writer.print(crlf);
            }
            writer.print("9999");
            writer.print(crlf); 
        } catch (IOException e) {
            throw new RuntimeException("Error writing file " +  fileName, e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            IOUtil.closeQuietly(writer);
        }
    }

    private void writeInterfaceFile(String fileName, List<GlisHandoffRecord> records, Date executionDate) {
        System.out.println("Writing GLIS Interface file - "+fileName);
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(fileName));
            BigDecimal debitTotal = BigDecimal.ZERO;
            BigDecimal creditTotal = BigDecimal.ZERO;

            for(GlisHandoffRecord r : records) {
                if (r.getTransactionCode().equalsIgnoreCase("61") ||
                        r.getTransactionCode().equalsIgnoreCase("71")) {
                    debitTotal = debitTotal.add(r.getAmount());
                } else {
                    creditTotal = creditTotal.add(r.getAmount());
                }
            }

            String comma = ",";
            writer.print("COLUMN1,COLUMN2,COLUMN3,COLUMN4,COLUMN5,COLUMN6,COLUMN7");
            writer.print(crlf); 
            writer.print("HR,");
            writer.print(String.valueOf(records.size()) + comma);
            writer.print("DR,");
            writer.print(debitTotal.toString() + comma);
            writer.print("CR,");
            writer.print(creditTotal.toString() + comma);
            writer.print(crlf); 

            for(GlisHandoffRecord r : records) {
                r.setEffectiveDate(executionDate);
                writer.print(r.exportToExcel().trim());
                writer.print(crlf);
            }
            writer.print("9999");
            writer.print(crlf); 
        } catch (IOException e) {
            throw new RuntimeException("Error writing file " +  fileName, e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            IOUtil.closeQuietly(writer);
        }
    }

    private void writeReconFile(String fileName, List<GlisHandoffRecon> records, Date executionDate) {
        System.out.println("Writing GLIS Recon file - " + fileName);
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(fileName));
            BigDecimal debitTotal = BigDecimal.ZERO;
            BigDecimal creditTotal = BigDecimal.ZERO;

            for(GlisHandoffRecon r : records) {
                if (r.getTransactionCode().equalsIgnoreCase("61") ||
                        r.getTransactionCode().equalsIgnoreCase("71")) {
                    debitTotal = debitTotal.add(r.getAmount());
                } else {
                    creditTotal = creditTotal.add(r.getAmount());
                }
            }

            String comma = ",";
            writer.print("Company Number,Currency Type,Book Code,UCPB Account Number,");
            writer.print("UCPB Account Name,LBP Account Number,LBP Account Name,Transaction Code,");
            writer.print("Responsibility Number, Source Code, Amount,Peso Amount,Effective Date,");
            writer.print("GLTS Number,Reference Number,Remarks,AccEntry ID, Is Approved?");
            writer.print(crlf);

            for(GlisHandoffRecon r : records) {
                r.setEffectiveDate(executionDate);
                writer.print(r.exportToExcel().trim());
                writer.print(crlf);
            }
            writer.print("9999");
            writer.print(crlf); 
        } catch (IOException e) {
            throw new RuntimeException("Error writing file " +  fileName, e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            IOUtil.closeQuietly(writer);
        }
    }

    public void execute(Date executionDate) {
        System.out.println("execute 02");
        Connection connection = null;
        // Force to write CR LF even in Linux
        StringBuilder sb = new StringBuilder();
        String parsedDate = parseDate(executionDate, DATE_FORMAT);
        sb.append((char)13);
        sb.append((char)10);
        crlf = sb.toString();

        String glisFileName = sourceSystemCode + parsedDate + ".txt";
        File tempGlisFile = new File(glisFileName);
        String intefaceFileName = "LBPInterfaceFile" + parsedDate + ".csv";
        File tempInterfaceFile = new File(intefaceFileName);
        String reconFileName = "ReconciliationFile" + parsedDate + ".csv";
        File tempReconFile = new File(reconFileName);
        List<GlisHandoffRecord> glisRecords = new ArrayList<GlisHandoffRecord>();
        List<GlisHandoffRecon> reconRecords = new ArrayList<GlisHandoffRecon>();

        try {
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, parsedDate);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                glisRecords.add(mapper.mapRow(rs,0));
            }

            PreparedStatement psRecon = connection.prepareStatement(reconQuery);
            psRecon.setString(1, parsedDate);
            ResultSet rsRecon = psRecon.executeQuery();
            while(rsRecon.next()) {
                reconRecords.add(reconMapper.mapRow(rsRecon,0));
            }

            PreparedStatement updatePs = connection.prepareStatement(updateSql);
            updatePs.setString(1, parsedDate);
            updatePs.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database exception",e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }

        writeGlisFile(glisFileName, glisRecords, executionDate);
        sendToChannel(tempGlisFile, glisFileName);
        writeInterfaceFile(intefaceFileName, glisRecords, executionDate);
        sendToChannel(tempInterfaceFile, intefaceFileName);
        writeReconFile(reconFileName, reconRecords, executionDate);
        sendToChannel(tempReconFile, reconFileName);
    }

    private <T> void sendToChannel(T payload, String filename) {
        MessageBuilder<T> builder = MessageBuilder.withPayload(payload);
        channel.send(builder.build());

        System.out.println("\n############################### GlisReportGeneratorJob.sendToChannel EXECUTED! \n");
    }

    private String parseDate(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public void setMapper(BeanRowMapper mapper) {
        this.mapper = mapper;
    }

    public void setReconMapper(BeanRowMapper reconMapper) {
        this.reconMapper = reconMapper;
    }

    public void setSourceSystemCode(String sourceSystemCode) {
        this.sourceSystemCode = sourceSystemCode;
    }

    public void setUpdateSql(String updateSql) {
        this.updateSql = updateSql;
    }
}
