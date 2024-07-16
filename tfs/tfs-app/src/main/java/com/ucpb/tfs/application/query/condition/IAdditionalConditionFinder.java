package com.ucpb.tfs.application.query.condition;

import com.incuventure.cqrs.query.Finder;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * User: Marv
 * Date: 10/10/12
 */

/**
 * Description:   Added findAllOriginalConditions and findAllConditions for MT707
 * Modified by:   Cedrick C. Nungay
 * Date Modified: 09/03/2018
 */

@Finder
public interface IAdditionalConditionFinder {

	List<Map<String,?>> findAllAdditionalCondition();

    List<Map<String,?>> findAllSavedAdditionalCondition(@Param("tradeServiceId") String tradeServiceId);

    List<Map<String,?>> findAllDefaultAdditionalCondition(@Param("tradeServiceId") String tradeServiceId);

    List<Map<String,?>> findAllNewAdditionalCondition(@Param("tradeServiceId") String tradeServiceId);

    List<Map<String,?>> findAllOriginalConditions(@Param("documentNumber") String documentNumber);

    List<Map<String,?>> findAllConditions(@Param("documentNumber") String documentNumber, @Param("tradeServiceId") String tradeServiceId);

    List<Map<String,?>> findDefaultConditions(@Param("documentNumber") String documentNumber);

    List<String> findAllAdditionalConditions(@Param("tradeServiceId") String tradeServiceId);

}
