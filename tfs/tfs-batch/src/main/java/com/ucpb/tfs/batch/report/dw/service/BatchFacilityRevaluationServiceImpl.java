package com.ucpb.tfs.batch.report.dw.service;

import com.ucpb.tfs.batch.report.dw.AllocationFileRecord;
import com.ucpb.tfs.batch.report.dw.Earmark;
import com.ucpb.tfs.batch.report.dw.PaymentDetail;
import com.ucpb.tfs.batch.report.dw.dao.AllocationDao;
import com.ucpb.tfs.batch.report.dw.dao.SilverlakeLocalDao;
import com.ucpb.tfs.batch.report.dw.dao.TradeProductDao;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * <pre>
 * Program_id    : BatchFacilityRevaluationServiceImpl
 * Program_name  : Batch Facility Revaluation Service
 * Process_Mode  : WEB
 * Frequency     : Daily
 * Input         : N/A
 * Output        : N/A
 * Description   : Contains SIBS - related methods where mybatis calls from tfs-core are mapped
 * Called In     : BatchFacilityRevaluationJob.java
 *
 * REVISIONS
 * 
 * Description: Replaced usage of silverlakeDao into silverlakeLocalDao
 * Revised by:  Cedrick C. Nungay
 * Date revised:01/25/2024
*/
public class BatchFacilityRevaluationServiceImpl implements BatchFacilityRevaluationService {

    private SilverlakeLocalDao silverlakeLocalDao;

    @Override
    public boolean revalue() {
        List<Earmark> earmarkList = silverlakeLocalDao.getEarmarksTrade();

        for (Earmark earmark : earmarkList) {
            System.out.println("Earmark Start");
            printEarmark(earmark);
            try {
              BigDecimal outstandingBalanceOriginal = earmark.getTOSBAL();
              BigDecimal originalAmount = earmark.getORGLMT();
              
              //CONVERT DIRECTLY TO PHP
              if(!earmark.getCURTYP().trim().equalsIgnoreCase("PHP")){
            	BigDecimal revalueRate = silverlakeLocalDao.getAngolConversionRate(earmark.getCURTYP().trim(), "PHP", 18);
                BigDecimal outstandingBalanceLocal = outstandingBalanceOriginal.multiply(revalueRate);
                BigDecimal originalAmountLocal = originalAmount.multiply(revalueRate);
                earmark.setTLOSBAL(outstandingBalanceLocal);
                earmark.setLORGAM(originalAmountLocal);
                
                printEarmark(earmark);
                System.out.println("After Revalue");
                System.out.println("_____________________________________");
                System.out.println("oishiiiiiiiiiiiiiiiiiiiiiiiiiiii11111- revalue " + earmark.getTLOSBAL());
                System.out.println("oishiiiiiiiiiiiiiiiiiiiiiiiiiiii22222 - revalue " + earmark.getLORGAM());
                int counter = silverlakeLocalDao.updateLocalOutstandingBalance(earmark);
                counter = silverlakeLocalDao.updateLocalOriginalAmount(earmark);
                
              }else{
                System.out.println("_____________________________________");
                System.out.println("earmark in peso no need to revalue.");
                System.out.println("_____________________________________");            	  
              }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    private void printEarmark(Earmark earmark) {
        System.out.println("Document Number:"+earmark.getACCTNO());
        System.out.println("Facility Reference Number:"+earmark.getAFCPNO());
        System.out.println("Cif Number:"+earmark.getCIFNO());
        System.out.println("Currency Type:"+earmark.getCURTYP());
        System.out.println("System Code:"+earmark.getSYSCOD());
        System.out.println("Outstanding Balance Local:"+earmark.getTLOSBAL());
        System.out.println("Outstanding Balance:"+earmark.getTOSBAL());
    }

    public void setSilverlakeLocalDao(SilverlakeLocalDao silverlakeLocalDao) {
        this.silverlakeLocalDao = silverlakeLocalDao;
    }

}
