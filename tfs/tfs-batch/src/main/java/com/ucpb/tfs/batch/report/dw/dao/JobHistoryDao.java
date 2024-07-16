package com.ucpb.tfs.batch.report.dw.dao;

import org.apache.ibatis.annotations.Param;

import com.ucpb.tfs.batch.job.JobHistory;

/**
 */
public interface JobHistoryDao {


    public int insertJobHistory(@Param("jobHistory")JobHistory history);

    public int flagJobAsSuccessful(@Param("recordId")Long id);

    public int flagJobAsFailed(@Param("recordId")Long id,@Param("errorMessage") String message);

}
