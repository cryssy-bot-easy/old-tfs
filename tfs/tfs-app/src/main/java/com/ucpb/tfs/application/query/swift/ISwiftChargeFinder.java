package com.ucpb.tfs.application.query.swift;

import com.incuventure.cqrs.query.Finder;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * User: Marv
 * Date: 11/28/12
 */

@Finder
public interface ISwiftChargeFinder {

    List<Map<String,?>> findAllSwiftCharge();

    List<Map<String,?>> findAllSavedSwiftCharge(@Param("tradeServiceId") String tradeServiceId);

    List<Map<String,?>> findAllDefaultSwiftCharge(@Param("tradeServiceId") String tradeServiceId);

}
