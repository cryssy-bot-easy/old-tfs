package com.ucpb.tfs.batch.job;

import com.ucpb.tfs.batch.dao.BatchProcessDao;
import com.ucpb.tfs.batch.report.dw.service.BatchFacilityRevaluationService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 */
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class BatchFacilityRevaluationJob implements SpringJob {

    private BatchFacilityRevaluationService batchFacilityRevaluationService;



    @Override
    public void execute() {
        batchFacilityRevaluationService.revalue();
    }

    @Override
    public void execute(String reportDate) {
        batchFacilityRevaluationService.revalue();
    }

    public void setBatchFacilityRevaluationService(BatchFacilityRevaluationService batchFacilityRevaluationService) {
        this.batchFacilityRevaluationService = batchFacilityRevaluationService;
    }
}
