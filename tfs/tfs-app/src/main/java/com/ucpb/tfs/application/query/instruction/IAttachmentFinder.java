/**
 * 
 */
package com.ucpb.tfs.application.query.instruction;

import com.incuventure.cqrs.query.Finder;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author angol
 *
 */
@Finder
public interface IAttachmentFinder {

	//Map<String,?> findAttachment(@Param("serviceInstructionId") String serviceInstructionId);

    List<Map<String,?>> findAllAttachments();
    
    List<Map<String,?>> findAttachmentsOfEts(@Param("serviceInstructionId") String serviceInstructionId);

    List<Map<String,?>> findAttachmentsOfTradeService(@Param("tradeServiceId") String tradeServiceId);

    }
