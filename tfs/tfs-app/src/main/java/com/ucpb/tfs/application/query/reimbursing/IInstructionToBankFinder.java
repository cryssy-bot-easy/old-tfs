package com.ucpb.tfs.application.query.reimbursing;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * User: Marv
 * Date: 11/4/12
 */

public interface IInstructionToBankFinder {

    List<Map<String, ?>> findAllDefaultInstructionToBank(@Param("tradeServiceId") String tradeServiceId);

    List<Map<String, ?>> findAllSavedInstructionToBank(@Param("tradeServiceId") String tradeServiceId);

    List<Map<String, ?>> findAllSavedLcInstructionToBank(@Param("documentNumber") String documentNumber);

    List<Map<String, ?>> findAllApprovedInstructionToBank(@Param("documentNumber") String documentNumber);

}
