package com.ucpb.tfs.application.query2;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * User: IPCVal
 * Date: 11/5/12
 */
public interface CorresChargeFinder {

    List<Map<String,?>> findCorresCharges(
         @Param("documentNumber") String documentNumber
    );

    List<Map<String,?>> findCorresChargeByDocumentNumber(
            @Param("documentNumber") String documentNumber
    );


}
