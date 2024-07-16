package com.ucpb.tfs.application.query.settlementaccount;

import com.incuventure.cqrs.query.Finder;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * User: Val
 * Date: 7/22/12
 */
@Finder
public interface IMarginalDepositFinder {
//public interface IMarginalDepositFinder extends ISettlementAccountFinder {

    List<Map<String,?>> mdCollectionInquiry(@Param("documentNumber") String documentNumber, @Param("cifName") String cifName, @Param("unitcode") String unitcode, @Param("unitCode") String unitCode);

    List<Map<String,?>> mdApplicationInquiry(@Param("documentNumber") String documentNumber, @Param("cifName") String cifName, @Param("expiryDate") String expiryDate, @Param("status") String status, @Param("unitcode") String unitcode, @Param("unitCode") String unitCode);

    Map<String, ?> findMarginalDeposit(@Param("settlementAccountNumber") String settlementAccountNumber);

    Map<String,?> getTotalMd(@Param("currency") String currency, @Param("documentNumber") String documentNumber);
}