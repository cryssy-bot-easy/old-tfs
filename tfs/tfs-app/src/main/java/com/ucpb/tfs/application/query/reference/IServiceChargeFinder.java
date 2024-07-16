package com.ucpb.tfs.application.query.reference;

import com.incuventure.cqrs.query.Finder;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * User: Marv
 * Date: 8/22/12
 */

@Finder
public interface IServiceChargeFinder {
    
    List<Map<String, ?>> findAllChargeByChargeId(@Param("chargeId") String chargeId);
    List<Map<String, ?>> findAllChargesByServiceInstructionId(@Param("serviceInstructionId") String serviceInstructionId);
    List<Map<String, ?>> findAllChargesByTradeServiceId(@Param("tradeServiceId") String tradeServiceId);
    List<Map<String, ?>> findAllApprovedEtsChargesByTradeServiceId(@Param("tradeServiceId") String tradeServiceId);
    List<Map<String, ?>> findSumServiceCharge(@Param("tradeServiceId")String tradeServiceId);   
}
