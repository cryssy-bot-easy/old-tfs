package com.ucpb.tfs.core.batch.process.allocationUnitCode.dao;

import com.ucpb.tfs.core.batch.process.allocationUnitCode.ServiceInstructionRecord;
import com.ucpb.tfs.core.batch.process.allocationUnitCode.TfsAllocationUnitCodeRecord;
import com.ucpb.tfs.core.batch.process.allocationUnitCode.TradeServiceRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * User: IPCVal
 */
public interface AllocationUnitCodeDao {

    public List<TfsAllocationUnitCodeRecord> getAllActiveDistinctCifNumbers(@Param("cifNumber") String cifNumber);

    public List<TradeServiceRecord> getAllActiveTradeServices(@Param("cifNumber") String cifNumber);

    public ServiceInstructionRecord getServiceInstruction(@Param("serviceInstructionId") String serviceInstructionId);

    public int updateTradeProductLc(@Param("tfsAllocationUnitCodeRecord") TfsAllocationUnitCodeRecord tfsAllocationUnitCodeRecord);

    public int updateTradeProductIndemnity(@Param("tfsAllocationUnitCodeRecord") TfsAllocationUnitCodeRecord tfsAllocationUnitCodeRecord);

    public int updateTradeProductNonLc(@Param("tfsAllocationUnitCodeRecord") TfsAllocationUnitCodeRecord tfsAllocationUnitCodeRecord);

    public int updateTradeProductExportAdvising(@Param("tfsAllocationUnitCodeRecord") TfsAllocationUnitCodeRecord tfsAllocationUnitCodeRecord);

    public int updateTradeProductExportBills(@Param("tfsAllocationUnitCodeRecord") TfsAllocationUnitCodeRecord tfsAllocationUnitCodeRecord);

    public int updateTradeProductImportAdvancePayment(@Param("tfsAllocationUnitCodeRecord") TfsAllocationUnitCodeRecord tfsAllocationUnitCodeRecord);

    public int updateTradeProductExportAdvancePayment(@Param("tfsAllocationUnitCodeRecord") TfsAllocationUnitCodeRecord tfsAllocationUnitCodeRecord);

    public int updateMarginalDeposit(@Param("tfsAllocationUnitCodeRecord") TfsAllocationUnitCodeRecord tfsAllocationUnitCodeRecord);

    public int updateAccountsPayable(@Param("tfsAllocationUnitCodeRecord") TfsAllocationUnitCodeRecord tfsAllocationUnitCodeRecord);

    public int updateAccountsReceivable(@Param("tfsAllocationUnitCodeRecord") TfsAllocationUnitCodeRecord tfsAllocationUnitCodeRecord);

    public int updateRebate(@Param("tfsAllocationUnitCodeRecord") TfsAllocationUnitCodeRecord tfsAllocationUnitCodeRecord);

    public int updateRefPas5Client(@Param("tfsAllocationUnitCodeRecord") TfsAllocationUnitCodeRecord tfsAllocationUnitCodeRecord);

    public int updateTradeService(@Param("tradeServiceRecord") TradeServiceRecord tradeServiceRecord);

    public int updateServiceInstruction(@Param("serviceInstructionRecord") ServiceInstructionRecord serviceInstructionRecord);
}
