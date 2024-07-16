package com.ucpb.tfs.domain.condition;

import static org.junit.Assert.*;

import java.util.Map;

import com.ucpb.tfs.domain.condition.AdditionalCondition;
import com.ucpb.tfs.domain.condition.enumTypes.ConditionType;

import org.junit.Test;

public class AdditionalConditionTest {

	@Test
	public void successfullyCallGetFields() {
		AdditionalCondition additionalCondition=new AdditionalCondition
				(ConditionType.NEW, new ConditionCode("wala lang"), "wala rin");
		Map<String,Object> temp=additionalCondition.getFields();
		for(Map.Entry<String,Object> e:temp.entrySet()){
			System.out.println("key:"+e.getKey() + " " + "value:"+e.getValue());
		}
		assertNotNull(temp);
	}
}
