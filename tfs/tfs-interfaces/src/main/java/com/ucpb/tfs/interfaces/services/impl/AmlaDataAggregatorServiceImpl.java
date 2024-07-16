package com.ucpb.tfs.interfaces.services.impl;

import java.util.List;

import com.ucpb.tfs.interfaces.services.AmlaDataAggregatorService;

public class AmlaDataAggregatorServiceImpl implements AmlaDataAggregatorService{

	private static final char NEW_LINE = '\n';
	
	@Override
	public String aggregate(List<?> entries) {
		StringBuilder builder = new StringBuilder();
		for(Object entry : entries){
			builder.append(entry.toString());
			builder.append(NEW_LINE);
		}
		return builder.toString();
	}

}
