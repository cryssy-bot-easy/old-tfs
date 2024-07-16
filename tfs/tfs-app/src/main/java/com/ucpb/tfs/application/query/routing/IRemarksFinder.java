package com.ucpb.tfs.application.query.routing;

import com.incuventure.cqrs.query.Finder;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 */
@Finder
public interface IRemarksFinder {

    public List<Map<String,?>> findAllRemarksByRemarkId(@Param("remarkId")ArrayList<String> remarkId);

}
