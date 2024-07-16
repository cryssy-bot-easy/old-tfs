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
public interface IAccountsPayableFinder {
//public interface IAccountsPayableFinder extends ISettlementAccountFinder {

    List<Map<String,?>> apMonitoringInquiry(@Param("referenceNumber") String referenceNumber,
											@Param("cifName") String cifName,
								            @Param("status") String status,
								            @Param("documentNumber") String documentNumber,
								            @Param("natureOfTransaction") String natureOfTransaction,
								            @Param("unitCode") String unitCode,
								            @Param("unitcode") String unitcode);

//    Map<String, ?> findAccountsPayable(@Param("settlementAccountNumber") String settlementAccountNumber);
    Map<String, ?> findAccountsPayable(@Param("id") String id);
    
    List<Map<String,?>> findAllAccountsPayableByCifNumber(@Param("cifNumber") String cifNumber, @Param("currency") String currency);

    List<Map<String,?>> findAllMultipleAccountsPayable(@Param("documentNumber") String documentNumber);

    List<Map<String,?>> findAllApByCifNumberAndCurrency(@Param("cifNumber") String cifNumber, @Param("currency") String currency);

    List<Map<String,?>> findAllApBySettlementAcctNo(@Param("cifNumber") String cifNumber, @Param("currency") String currency, @Param("settlementAccountNumber") String settlementAccountNumber);

    List<Map<String,?>> findAllApById(@Param("id") String id);

}
