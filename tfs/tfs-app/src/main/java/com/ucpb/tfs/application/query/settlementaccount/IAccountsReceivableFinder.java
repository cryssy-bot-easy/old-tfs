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
public interface IAccountsReceivableFinder {
//public interface IAccountsReceivableFinder extends ISettlementAccountFinder {

    List<Map<String,?>> arMonitoringInquiry(@Param("referenceNumber") String referenceNumber,
    										@Param("cifName") String cifName,
                                            @Param("status") String status,
                                            @Param("documentNumber") String documentNumber,
                                            @Param("unitCode") String unitCode,
                                            @Param("unitcode") String unitcode);

//    Map<String,?> findAccountsReceivable(@Param("settlementAccountNumber") String settlementAccountNumber);
    Map<String,?> findAccountsReceivable(@Param("id") String id);

//    List<Map<String,?>> findAllAccountsReceivableByCifNumber(@Param("cifNumber") String cifNumber);

//    List<Map<String,?>> findAllMultipleAccountsReceivable(@Param("documentNumber") String documentNumber);
//
//    List<Map<String,?>> findAllArByCifNumberAndCurrency(@Param("cifNumber") String cifNumber, @Param("currency") String currency);
//
//    List<Map<String,?>> findAllArBySettlementAcctNo(@Param("cifNumber") String cifNumber, @Param("currency") String currency, @Param("settlementAccountNumber") String settlementAccountNumber);
//
//    List<Map<String,?>> findAllArById(@Param("cifNumber") String cifNumber, @Param("currency") String currency, @Param("id") String id);
}
