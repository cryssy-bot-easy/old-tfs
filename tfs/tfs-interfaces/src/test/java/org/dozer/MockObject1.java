package org.dozer;

import java.util.ArrayList;
import java.util.List;

public class MockObject1 {
	private String value;

	private List<String> values = new ArrayList<String>();
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public void addValue(String value){
		this.values.add(value);
	}
	
	
}
