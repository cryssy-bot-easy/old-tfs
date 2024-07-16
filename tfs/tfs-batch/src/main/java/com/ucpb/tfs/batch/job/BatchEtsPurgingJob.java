package com.ucpb.tfs.batch.job;

import com.ucpb.tfs.batch.dao.BatchProcessDao;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 */
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class BatchEtsPurgingJob implements SpringJob {

    private BatchProcessDao batchProcessDao;

    @Override
    public void execute() {
        batchProcessDao.purgeUnactedEts(new Date());
    }

    @Override
    public void execute(String reportDate) {
        Date queryDate;
		try {
			queryDate = new SimpleDateFormat("MM-dd-yyyy").parse(reportDate);
			batchProcessDao.purgeUnactedEts(queryDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
    }

    public void setBatchProcessDao(BatchProcessDao batchProcessDao) {
        this.batchProcessDao = batchProcessDao;
    }
}
