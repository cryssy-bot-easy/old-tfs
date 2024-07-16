package com.ucpb.tfs.interfaces.domain.transformer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.integration.Message;
import org.springframework.integration.support.MessageBuilder;

import com.ucpb.tfs.interfaces.domain.transformer.ListToStringTransformer;

public class ListToStringTransformerTest {

	private ListToStringTransformer transformer = new ListToStringTransformer();
	
	
	@Test
	public void successfullyConvertListToSingleString(){
		List<Integer> integers = generateIntegers(7);
		MessageBuilder<List<Integer>> messageBuilder = MessageBuilder.withPayload(integers);
		String result = transformer.transform(messageBuilder.build());
		assertNotNull(result);
		assertEquals(result,generateResult(7));
		System.out.println("RESULT: " + result);
	}
	
	
	private List<Integer> generateIntegers(int count){
		List<Integer> integers = new ArrayList<Integer>();
		for(int ctr = 0; ctr < count; ctr++){
			integers.add(Integer.valueOf(ctr));
		}
		return integers;
	}
	
	private String generateResult(int count){
		StringBuilder builder = new StringBuilder();
		for(int ctr = 0; ctr < count; ctr++){
			builder.append(ctr);
			builder.append('\n');
		}
		return builder.toString();
	}
}
