/**
 * 
 */
package com.ucpb.tfs.application.query.instruction;

import com.incuventure.cqrs.query.Finder;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author Val
 *
 */
@Finder
public interface IServiceInstructionFinder {

	Map<String,?> findServiceInstruction(@Param("serviceInstructionId") String serviceInstructionId);

    List<Map<String,?>> findAllServiceInstruction();

    List<Map<String,?>> findAllApprovedServiceInstruction();

    List<Map<String,?>> etsInquiry(
            @Param("etsNumber") String etsNumber,
            @Param("cifName") String cifName,
            @Param("documentType") String documentType,
            @Param("documentClass") String documentClass,
            @Param("documentSubType1") String documentSubType1,
            @Param("serviceType") String serviceType,
            @Param("status") String status,
            @Param("createdDate") String createdDate,
            @Param("modifiedDate") String modifiedDate,
            @Param("approvedDate") String approvedDate,
            @Param("userId") String userId,
            @Param("cifNumber") String cifNumber,
            @Param("userActiveDirectoryId") String userActiveDirectoryId,
            @Param("unitcode") String unitcode
    );
}
