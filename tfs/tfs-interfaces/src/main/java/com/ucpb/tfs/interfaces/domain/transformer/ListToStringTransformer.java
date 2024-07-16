package com.ucpb.tfs.interfaces.domain.transformer;

import java.util.List;

import org.springframework.integration.Message;

public class ListToStringTransformer {

	private static final char NEW_LINE = '\n';

	public String transform(Message<?> message){
		List<?> list = (List<?>) message.getPayload();
		StringBuilder builder = new StringBuilder();
		for(Object item : list){
			builder.append(item.toString());
			builder.append(NEW_LINE);
		}
		return builder.toString();
	}
	
}
