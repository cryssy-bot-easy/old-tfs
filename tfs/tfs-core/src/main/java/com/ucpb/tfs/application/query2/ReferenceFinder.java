package com.ucpb.tfs.application.query2;


import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ReferenceFinder {

    List<Map<String,?>> getAllCountries(@Param("keyword") String keyword);
    List<Map<String,?>> getAllISOCountries(@Param("keyword") String keyword);
    List<Map<String,?>> findBanksByKeyword(@Param("keyword") String keyword);
    List<Map<String,?>> findLocalBanksByKeyword(@Param("keyword") String keyword);
    List<Map<String,?>> findBankBySwiftAddress(@Param("bic") String bic,@Param("branchCode")String branchCode);
    List<Map<String,?>> findRmaBanksByKeyword(@Param("keyword") String keyword);
    List<Map<String,?>> findDepositoryBanksByKeyword(@Param("keyword") String keyword);
    List<Map<String,?>> findDepositoryBanksByKeywordAndCurrency(@Param("keyword") String keyword, @Param("currency") String currency);
    List<Map<String,?>> findImporterByKeyword(@Param("keyword") String keyword);
    List<Map<String,?>> findFirmLibByKeyword(@Param("keyword") String keyword);
    List<Map<String,?>> findAllImporterByKeyword(@Param("keyword") String keyword);
    List<Map<String,?>> findAllDigitalSignatories(@Param("keyword") String keyword);
    List<Map<String,?>> findAllReferenceProductService();
    List<Map<String,?>> findAllReferenceProductServiceById(@Param("id") Integer id);
    List<Map<String,?>> findAllReferenceCharge();
    List<Map<String,?>> findCommodityCode(@Param("keyword") String keyword);
    List<Map<String,?>> findParticulars(@Param("keyword") String keyword);
    List<Map<String,?>> findParticipantCode(@Param("keyword") String keyword);
    

}