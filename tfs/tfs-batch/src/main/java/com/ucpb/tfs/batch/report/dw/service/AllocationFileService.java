package com.ucpb.tfs.batch.report.dw.service;

import com.ucpb.tfs.batch.report.dw.AllocationFileRecord;

import java.util.Date;
import java.util.List;


public interface AllocationFileService {

    public List<AllocationFileRecord> getProductAllocations(Date currentDate, String fxProfitOrLossAccountingCode, String treasuryAllocationCode);

    public List<AllocationFileRecord> getProductAverageDailyBalanceRecords(Date currentDate);
    
    public List<AllocationFileRecord> getProductAllocationsException(Date currentDate, String fxProfitOrLossAccountingCode, String treasuryAllocationCode);

    public List<AllocationFileRecord> getProductAverageDailyBalanceRecordsException(Date currentDate);
}
