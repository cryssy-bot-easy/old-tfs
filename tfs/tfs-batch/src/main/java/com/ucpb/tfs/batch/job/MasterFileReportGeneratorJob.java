package com.ucpb.tfs.batch.job;

import com.ancientprogramming.fixedformat4j.format.FixedFormatManager;
import com.ancientprogramming.fixedformat4j.format.impl.FixedFormatManagerImpl;
import com.ucpb.tfs.batch.report.dw.MasterFileRecord;
import com.ucpb.tfs.batch.report.dw.service.MasterFileService;
import com.ucpb.tfs.batch.util.IOUtil;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.support.MessageBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 */
public class MasterFileReportGeneratorJob implements SpringJob {

    private MasterFileService masterFileService;

    private static FixedFormatManager manager = new FixedFormatManagerImpl();

    private String filename;

    private MessageChannel channel;

    private String prefix ="TF_MASTER";

    private static final SimpleDateFormat EXECUTION_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");


    public MasterFileReportGeneratorJob(MasterFileService masterFileService, String filename, MessageChannel channel) {
        this.masterFileService = masterFileService;
        this.filename = filename;
        this.channel = channel;
    }


    public void execute(String appDate) {
        System.out.println("MasterFileReportGeneratorJob execute:"+ appDate);
        PrintWriter writer = null;
        File tempFile = null;

        try {
            String newAppDate = appDate.replace("-00.00.00","");
            newAppDate = newAppDate.replace("-","");

            filename = prefix + "_" + newAppDate + ".txt";
            System.out.println("filename:"+filename);

            tempFile = new File(filename);
            writer = new PrintWriter(new FileWriter(filename));

            List<MasterFileRecord> records = masterFileService.getMasterFiles(appDate);
            for(MasterFileRecord record : records){
                writer.print(manager.export(record).trim());

                // Force to write CR LF even in Linux
                StringBuilder sb = new StringBuilder();
                sb.append((char)13);
                sb.append((char)10);
                String crlf = sb.toString();

                writer.print(crlf);
            }
//            for(MasterFileRecord record : records){
//                System.out.println(manager.export(record));
//            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error writing file " + filename,e);
        } finally{
            IOUtil.closeQuietly(writer);
        }

        System.out.println("tempfile:"+tempFile);
        if(tempFile != null){
            sendToChannel(tempFile);
        }

    }

    @Override
    public void execute() {
        System.out.println("MasterFileReportGeneratorJob execute");
        PrintWriter writer = null;
        File tempFile = null;
        try {
            tempFile = new File(filename);
            writer = new PrintWriter(new FileWriter(filename));

            List<MasterFileRecord> records = masterFileService.getMasterFiles(getAppServerDate().toString());
            for(MasterFileRecord record : records){
                writer.print(manager.export(record));

                // Force to write CR LF even in Linux
                StringBuilder sb = new StringBuilder();
                sb.append((char)13);
                sb.append((char)10);
                String crlf = sb.toString();

                writer.print(crlf);
            }
//            for(MasterFileRecord record : records){
//                System.out.println(manager.export(record));
//            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error writing file " + filename,e);
        } finally{
            IOUtil.closeQuietly(writer);
        }

        System.out.println("tempfile:"+tempFile);
        if(tempFile != null){
            sendToChannel(tempFile);
        }

    }

    public void setMasterFileService(MasterFileService masterFileService) {
        this.masterFileService = masterFileService;
    }

    private void sendToChannel(File file) {
        MessageBuilder<File> builder = MessageBuilder.withPayload(file);
        channel.send(builder.build());
    }

    private java.sql.Date getAppServerDate(){
        Date runDate = new Date();
        return new java.sql.Date(runDate.getTime());
    }

    private String getExecutionDate(Date date){
        return EXECUTION_DATE_FORMAT.format(date);
    }

}
