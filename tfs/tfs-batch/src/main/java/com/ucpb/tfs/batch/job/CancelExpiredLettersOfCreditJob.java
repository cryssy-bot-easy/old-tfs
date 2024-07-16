package com.ucpb.tfs.batch.job;

import com.ucpb.tfs.batch.dao.BatchProcessDao;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 */
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class CancelExpiredLettersOfCreditJob implements SpringJob {

    private BatchProcessDao batchProcessDao;

    @Override
    public void execute() {
        batchProcessDao.cancelExpiredLettersOfCredit(new Date());
    }

    public void execute(String reportDate) {
        //TODO:convert reportDate from String to Date

        if(batchProcessDao.countCancelExpiredLettersOfCredit(new Date()) < 1){
            System.out.println("NOTHING TO EXPIRE");
        } else {
            System.out.println("Records to be expired:"+batchProcessDao.countCancelExpiredLettersOfCredit(new Date()));
            batchProcessDao.cancelExpiredLettersOfCredit(new Date());
        }
    }


    public void setBatchProcessDao(BatchProcessDao batchProcessDao) {
        this.batchProcessDao = batchProcessDao;
    }


}
