package com.ucpb.tfs.application.query.service;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.incuventure.cqrs.query.Finder;

@Finder
public interface ICBCodeFinder {

	Map<String,?> findCbCodeFromCif(@Param("cifNumber") String cifNumber);
}
