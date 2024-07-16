package com.ucpb.tfs.batch.job;

import com.ucpb.tfs.batch.report.dw.dao.CiclsDao;
import com.ucpb.tfs.batch.report.dw.CiclsHandoffRecord;

import com.ucpb.tfs.batch.util.IOUtil;

import com.ancientprogramming.fixedformat4j.format.FixedFormatManager;
import com.ancientprogramming.fixedformat4j.format.impl.FixedFormatManagerImpl;

import org.springframework.integration.MessageChannel;
import org.springframework.integration.support.MessageBuilder;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.File;

public class CiclsReportGeneratorJob implements SpringJob {

    private static FixedFormatManager manager = new FixedFormatManagerImpl();
	private static final String DATE_FORMAT = "MMddyy";
    private static String crlf; 
    private String successName = "ITD_CICLS_";
    private String failName = "ITD_REJECT_CICLS_";
    private CiclsDao ciclsDao;
    private MessageChannel channel;

    public CiclsReportGeneratorJob(MessageChannel channel){
        this.channel = channel;
    }
    
    @Override
    public void execute() throws Exception {
        ciclsDao.getCiclsRecords(new Date());
    }

    @Override
    public void execute(String reportDate) throws Exception {

    	SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        Date runDate = dateFormat.parse(reportDate);
        List<CiclsHandoffRecord> ciclsList = ciclsDao.getCiclsRecords(runDate);
        List<CiclsHandoffRecord> successList = new ArrayList<CiclsHandoffRecord>();
        List<CiclsHandoffRecord> failedList = new ArrayList<CiclsHandoffRecord>();
        String parsedDate = parseDate(runDate, DATE_FORMAT);
        String failFileName = failName + parsedDate + ".txt";
        String successFileName = successName + parsedDate + ".txt";
        File tempSuccessFile = new File(successFileName);
        File tempFailFile = new File(failFileName);

        for (CiclsHandoffRecord ciclsRec : ciclsList) {
            if(validateListCicls(ciclsRec) && validateCiclsLength(ciclsRec)){
                successList.add(ciclsRec);
            }
            else{
                failedList.add(ciclsRec);
            }
        }
        if(successList.size() > 0){
            writeSuccessFile(successFileName, successList);
            sendToChannel(tempSuccessFile, successFileName);
        }
        if(failedList.size() > 0){
            writeErrorFile(failFileName, failedList);
            sendToChannel(tempFailFile, failFileName);
        }
    }

    private void writeSuccessFile(String fileName, List<CiclsHandoffRecord> records) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(fileName));
            for(CiclsHandoffRecord r : records) {
                writer.println(manager.export(r));
             }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeQuietly(writer);
        }
    }

    private void writeErrorFile(String fileName, List<CiclsHandoffRecord> records) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(fileName));
            for(CiclsHandoffRecord r : records) {
                writer.println(manager.export(r));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeQuietly(writer);
        }
    }

    private <T> void sendToChannel(T payload, String filename) {
        MessageBuilder<T> builder = MessageBuilder.withPayload(payload);
        channel.send(builder.build());
    }

    private Boolean validateListCicls(CiclsHandoffRecord records) {
        return !( 
            records.getTinNumber() == null ||
            records.getTinNumber().trim().isEmpty() ||
            records.getClientName() == null ||
            records.getClientName().trim().isEmpty() ||
            records.getProcessDate() == null ||
            records.getCiclsProductCode() == null ||
            records.getCiclsProductCode().trim().isEmpty() ||
            records.getApprovedAmount() == null ||
            records.getOutstandingCurrent() == null ||
            records.getOutstandingPastDue() == null
        );
    }

    public Boolean validateCiclsLength(CiclsHandoffRecord ciclsRec) {
        if(ciclsRec.getApprovedAmount() != null || 
           ciclsRec.getOutstandingCurrent() != null ||
           ciclsRec.getOutstandingPastDue() != null) {
            int approvedAmount = ciclsRec.getApprovedAmount().precision();
            int outstandingCurrent = ciclsRec.getOutstandingCurrent().precision();
            int outstandingPastDue = ciclsRec.getOutstandingPastDue().precision();
            if(approvedAmount > 18 || 
               outstandingCurrent > 18 ||
               outstandingPastDue > 18 ){
                return false;
            }
        }
        return true;
    }

    public void setCiclsDao(CiclsDao ciclsDao) {
        this.ciclsDao = ciclsDao;
    }
    
    public CiclsDao getCiclsDao() {
        return ciclsDao;
    }

    private String parseDate(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

}
