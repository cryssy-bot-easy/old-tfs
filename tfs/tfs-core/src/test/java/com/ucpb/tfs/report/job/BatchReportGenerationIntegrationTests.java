package com.ucpb.tfs.report.job;

import com.google.gson.Gson;
import com.ucpb.tfs.batch.job.*;
import com.ucpb.tfs.report.dw.job.DailyBalanceRecorderJob;
import com.ucpb.tfs.utils.DateUtil;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * @author Robbie
 *
 * Not a test class. This is a convenience class that will be used to manually invoke the daily batch jobs.
 */
@Ignore("Should not be run as part of the build process")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:amla-report-integration-tests.xml")
public class BatchReportGenerationIntegrationTests {


    @Autowired
    @Qualifier("transactionLogGeneratorJob")
    private CsvReportGeneratorJob transactionLogJob;

    @Autowired
    @Qualifier("accountLogGeneratorJob")
    private CsvReportGeneratorJob accountLogJob;

    @Autowired
//    @Qualifier("customerAccountLogGeneratorJob")
    private CsvReportGeneratorJob customerAccountLogGeneratorJob;

    @Autowired
    @Qualifier("glReportGeneratorJob")
    private FixedFileReportGeneratorJob glReportGenerator;

    @Autowired
    private MasterFileReportGeneratorJob masterFileReportGeneratorJob;

    @Autowired
    @Qualifier("customerLogGeneratorJob")
    private CsvReportGeneratorJob customerLogGeneratorJob;

//    @Autowired
//    @Qualifier("recordEndOfDayBalanceJob")
//    private DailyBalanceRecorderJob recordEndOfDayBalanceJob;


    @Autowired(required = false)
//    @Qualifier("setExpiredLcsStatusToExpired")
    private CancelExpiredLettersOfCreditJob setExpiredLcsStatusToExpired;

    @Autowired
    private AllocationFileReportGeneratorJob glAllocationsReportJob;

    @Autowired
    @Qualifier("dailyBalanceRecorderJob")
    private SqlRunnerJob dailyBalanceRecorderJob;


    @Autowired(required = false)
//    @Qualifier("cancelDormantEtsJob")
    private BatchEtsPurgingJob cancelDormantEtsJob;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final Gson GSON = new Gson();

    @Test
    public void generateTransactionLogReport() throws InterruptedException {
        transactionLogJob.setQuery("select * from TRANSACTIONLOG WHERE DAYS(TXNDATE) = DAYS({ts '2012-10-26 21:43:38.430000'})");
        transactionLogJob.execute();
        Thread.sleep(3000);

        transactionLogJob.setQuery("select * from TRANSACTIONLOG WHERE DAYS(TXNDATE) = DAYS({ts '2012-10-29 21:43:38.430000'})");
        transactionLogJob.execute();
        Thread.sleep(3000);

        transactionLogJob.setQuery("select * from TRANSACTIONLOG WHERE DAYS(TXNDATE) = DAYS({ts '2012-10-31 21:43:38.430000'})");
        transactionLogJob.execute();
        Thread.sleep(3000);

        transactionLogJob.setQuery("select * from TRANSACTIONLOG WHERE DAYS(TXNDATE) = DAYS({ts '2012-11-02 21:43:38.430000'})");
        transactionLogJob.execute();
        Thread.sleep(3000);

        transactionLogJob.setQuery("select * from TRANSACTIONLOG WHERE DAYS(TXNDATE) = DAYS({ts '2012-11-05 21:43:38.430000'})");
        transactionLogJob.execute();
        Thread.sleep(3000);

        transactionLogJob.setQuery("select * from TRANSACTIONLOG WHERE DAYS(TXNDATE) = DAYS({ts '2012-11-07 21:43:38.430000'})");
        transactionLogJob.execute();
        Thread.sleep(3000);

        transactionLogJob.setQuery("select * from TRANSACTIONLOG WHERE DAYS(TXNDATE) = DAYS({ts '2012-11-09 21:43:38.430000'})");
        transactionLogJob.execute();

        transactionLogJob.setQuery("select * from TRANSACTIONLOG WHERE DAYS(TXNDATE) = DAYS({ts '2012-11-14 21:43:38.430000'})");
        transactionLogJob.execute();
        Thread.sleep(3000);

    }

    @Test
    public void generateAmlaFiles() throws InterruptedException {
        transactionLogJob.setQuery("select * from TRANSACTIONLOG WHERE DAYS(TXNDATE) = DAYS({ts '2013-02-13 21:43:38.430000'})");
        transactionLogJob.execute();
        Thread.sleep(3000);

        accountLogJob.setQuery("SELECT * FROM ACCOUNTLOG WHERE DAYS(DATE_CREATED) = DAYS({ts '2013-02-13 21:43:38.430000'})");
        accountLogJob.execute();

        customerAccountLogGeneratorJob.setQuery("SELECT * from CUSTOMERACCOUNT WHERE DAYS(DATE_CREATED) = DAYS({ts '2013-02-13 21:43:38.430000'})");
        customerAccountLogGeneratorJob.execute();
    }

    @Test
    public void generateAccountLogReport() throws InterruptedException {
        accountLogJob.setQuery("SELECT * FROM ACCOUNTLOG WHERE DAYS(DATE_CREATED) = DAYS({ts '2012-10-26 21:43:38.430000'})");
        accountLogJob.execute();
        Thread.sleep(3000);

        accountLogJob.setQuery("SELECT * FROM ACCOUNTLOG WHERE DAYS(DATE_CREATED) = DAYS({ts '2012-10-29 21:43:38.430000'})");
        accountLogJob.execute();
        Thread.sleep(3000);

        accountLogJob.setQuery("SELECT * FROM ACCOUNTLOG WHERE DAYS(DATE_CREATED) = DAYS({ts '2012-10-31 21:43:38.430000'})");
        accountLogJob.execute();
        Thread.sleep(3000);

        accountLogJob.setQuery("SELECT * FROM ACCOUNTLOG WHERE DAYS(DATE_CREATED) = DAYS({ts '2012-11-02 21:43:38.430000'})");
        accountLogJob.execute();
        Thread.sleep(3000);

        accountLogJob.setQuery("SELECT * FROM ACCOUNTLOG WHERE DAYS(DATE_CREATED) = DAYS({ts '2012-11-05 21:43:38.430000'})");
        accountLogJob.execute();
        Thread.sleep(3000);

        accountLogJob.setQuery("SELECT * FROM ACCOUNTLOG WHERE DAYS(DATE_CREATED) = DAYS({ts '2012-11-07 21:43:38.430000'})");
        accountLogJob.execute();
        Thread.sleep(3000);

        accountLogJob.setQuery("SELECT * FROM ACCOUNTLOG WHERE DAYS(DATE_CREATED) = DAYS({ts '2012-11-09 21:43:38.430000'})");
        accountLogJob.execute();

        accountLogJob.setQuery("SELECT * FROM ACCOUNTLOG WHERE DAYS(DATE_CREATED) = DAYS({ts '2012-11-14 21:43:38.430000'})");
        accountLogJob.execute();
        Thread.sleep(3000);

    }

    @Test
    public void generateCustomerAccountLogReport() throws InterruptedException {
        customerAccountLogGeneratorJob.setQuery("SELECT * from CUSTOMERACCOUNT WHERE DAYS(DATE_CREATED) = DAYS({ts '2012-10-26 21:43:38.430000'})");
        customerAccountLogGeneratorJob.execute();
        Thread.sleep(3000);

        customerAccountLogGeneratorJob.setQuery("SELECT * from CUSTOMERACCOUNT WHERE DAYS(DATE_CREATED) = DAYS({ts '2012-10-29 21:43:38.430000'})");
        customerAccountLogGeneratorJob.execute();
        Thread.sleep(3000);

        customerAccountLogGeneratorJob.setQuery("SELECT * from CUSTOMERACCOUNT WHERE DAYS(DATE_CREATED) = DAYS({ts '2012-10-31 21:43:38.430000'})");
        customerAccountLogGeneratorJob.execute();
        Thread.sleep(3000);

        customerAccountLogGeneratorJob.setQuery("SELECT * from CUSTOMERACCOUNT WHERE DAYS(DATE_CREATED) = DAYS({ts '2012-11-02 21:43:38.430000'})");
        customerAccountLogGeneratorJob.execute();
        Thread.sleep(3000);

        customerAccountLogGeneratorJob.setQuery("SELECT * from CUSTOMERACCOUNT WHERE DAYS(DATE_CREATED) = DAYS({ts '2012-11-05 21:43:38.430000'})");
        customerAccountLogGeneratorJob.execute();
        Thread.sleep(3000);

        customerAccountLogGeneratorJob.setQuery("SELECT * from CUSTOMERACCOUNT WHERE DAYS(DATE_CREATED) = DAYS({ts '2012-11-07 21:43:38.430000'})");
        customerAccountLogGeneratorJob.execute();
        Thread.sleep(3000);

        customerAccountLogGeneratorJob.setQuery("SELECT * from CUSTOMERACCOUNT WHERE DAYS(DATE_CREATED) = DAYS({ts '2012-11-09 21:43:38.430000'})");
        customerAccountLogGeneratorJob.execute();

        customerAccountLogGeneratorJob.setQuery("SELECT * from CUSTOMERACCOUNT WHERE DAYS(DATE_CREATED) = DAYS({ts '2012-11-14 21:43:38.430000'})");
        customerAccountLogGeneratorJob.execute();
        Thread.sleep(3000);

    }

    @Test
    public void generateCustomerLogReport(){
        customerLogGeneratorJob.execute();
    }

    @Test
    public void generateGlFile() throws InterruptedException {
        glReportGenerator.setQuery("SELECT * FROM INT_ACCENTRYACTUAL GL INNER JOIN TRADESERVICE TS ON TS.TRADESERVICEID = GL.TRADESERVICEID WHERE DAYS(CREATEDDATE) = DAYS({ts '2012-11-14 21:43:38.430000'})");
        glReportGenerator.execute();
//        Thread.sleep(6000);
    }

    @Test
    public void generateMasterFile(){
        masterFileReportGeneratorJob.execute();
    }

//    @Ignore
    @Test
    public void generateAllocationFile(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,2013);
        calendar.set(Calendar.DAY_OF_MONTH,13);
        calendar.set(Calendar.MONTH,Calendar.FEBRUARY);

        glAllocationsReportJob.execute(calendar.getTime(),"561501030000","10903");
    }

    @Test
    public void recordEndOfDayBalances(){
        dailyBalanceRecorderJob.execute();
    }

    @Ignore
    @Test
    public void populateGlEntryEffectiveDates() throws ParseException {
        List<Map<String,Object>> tradeServices = jdbcTemplate.queryForList("SELECT * FROM TRADESERVICE");
        for(Map<String,Object> tradeService : tradeServices){
            String details = (String)tradeService.get("DETAILS");
            Map<String,Object> parsedDetails = GSON.fromJson(details,Map.class);
            String processDate = (String) parsedDetails.get("processDate");
            System.out.println("***** PROCESS DATE :" + processDate);
            if(processDate != null && processDate.matches("\\d{2}/\\d{2}/\\d{4}")){
                System.out.println("***** DATE TYPE: " + processDate.getClass());
                jdbcTemplate.update("UPDATE INT_ACCENTRYACTUAL SET EFFECTIVEDATE = ? where tradeServiceId = ?", DateUtil.convertToDate(processDate,"MM/dd/yyyy"),tradeService.get("TRADESERVICEID"));
            }
        }
    }



    @Ignore
    @Test
    public void expireAllOldLcs(){
        setExpiredLcsStatusToExpired.execute();
    }

    @Ignore
    @Test
    public void cancelDormantEts(){
        cancelDormantEtsJob.execute();
    }

}
