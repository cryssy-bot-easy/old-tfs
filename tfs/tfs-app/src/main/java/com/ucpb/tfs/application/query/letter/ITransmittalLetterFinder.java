package com.ucpb.tfs.application.query.letter;

import com.incuventure.cqrs.query.Finder;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * User: Marv
 * Date: 10/10/12
 */

@Finder
public interface ITransmittalLetterFinder {

	List<Map<String,?>> findAllTransmittalLetter();

    List<Map<String,?>> findAllSavedTransmittalLetter(@Param("tradeServiceId") String tradeServiceId);

    List<Map<String,?>> findAllDefaultTransmittalLetter(@Param("tradeServiceId") String tradeServiceId);

    List<Map<String,?>> findAllNewTransmittalLetter(@Param("tradeServiceId") String tradeServiceId);

}
