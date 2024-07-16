package com.ucpb.tfs.application.query.documents;

import com.incuventure.cqrs.query.Finder;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * User: Marv
 * Date: 10/10/12
 */

/**
 * Description:   Added findAllOriginalDocuments and findAllDocuments for MT707
 * Modified by:   Cedrick C. Nungay
 * Date Modified: 08/24/2018
 */

@Finder
public interface IRequiredDocumentFinder {

	List<Map<String,?>> findAllRequiredDocuments(@Param("documentType") String documentType);

    List<Map<String,?>> findAllSavedRequiredDocuments(@Param("tradeServiceId") String tradeServiceId);

    List<Map<String,?>> findAllDefaultDocuments(@Param("tradeServiceId") String tradeServiceId, @Param("documentType") String documentType);

    List<Map<String,?>> findAllNewDocuments(@Param("tradeServiceId") String tradeServiceId);

    List<Map<String,?>> findAllOriginalDocuments(@Param("documentNumber") String documentNumber);

    List<Map<String,?>> findAllDocuments(@Param("documentNumber") String documentNumber, @Param("tradeServiceId") String tradeServiceId, @Param("documentType") String documentType);

    List<Map<String,?>> findDefaultDocuments(@Param("documentNumber") String documentNumber, @Param("documentType") String documentType);

    List<String> findAllRequiredDocument(@Param("tradeServiceId") String tradeServiceId);

}
