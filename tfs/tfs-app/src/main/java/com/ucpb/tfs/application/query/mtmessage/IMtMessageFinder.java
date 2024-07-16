package com.ucpb.tfs.application.query.mtmessage;


import com.incuventure.cqrs.query.Finder;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * User: Marv
 * Date: 10/10/12
 */

@Finder
public interface IMtMessageFinder {

    Map<String, ?> findMtMessage(@Param("id") String id);

}
