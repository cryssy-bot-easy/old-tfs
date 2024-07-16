package com.ucpb.tfs.application.query.settlementaccount;

import com.incuventure.cqrs.query.Finder;
import org.apache.ibatis.annotations.Param;

import java.util.Map;
import java.util.Set;

/**
 * User: IPCVal
 * Date: 8/1/12
 */
@Finder
public interface ISettlementAccountFinder {

    Set<Map<String,?>> getAllActivity(@Param("settlementAccountNumber") String settlementAccountNumber);

    Set<Map<String,?>> getAllCredits(@Param("settlementAccountNumber") String settlementAccountNumber);

    Set<Map<String,?>> getAllDebits(@Param("settlementAccountNumber") String settlementAccountNumber);

    Map<String,?> getCreditsTotalAmountByCurrency(@Param("settlementAccountNumber") String settlementAccountNumber, @Param("currency") String currency);

    Map<String,?> getDebitsTotalAmountByCurrency(@Param("settlementAccountNumber") String settlementAccountNumber, @Param("currency") String currency);

    Set<Map<String,?>> getCreditsAllDistinctCurrencies(@Param("settlementAccountNumber") String settlementAccountNumber);

    Set<Map<String,?>> getDebitsAllDistinctCurrencies(@Param("settlementAccountNumber") String settlementAccountNumber);
}
