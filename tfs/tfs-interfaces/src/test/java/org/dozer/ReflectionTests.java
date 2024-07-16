package org.dozer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public class ReflectionTests {

	
	@Test
	public void testReflectionOnMethods(){
		for(Method method : MockObject1.class.getDeclaredMethods()){
			System.out.println("NAME: " + method.getName());
		}
	}
	
	@Test
	public void successfullyGetSimpleField() throws SecurityException, NoSuchFieldException{
		Field field = Wrapper.class.getDeclaredField("mockObject1");
		assertNotNull(field);
	}
	
	@Test(expected=NoSuchFieldException.class)
	public void failToGetNestedFields() throws SecurityException, NoSuchFieldException{
		Field field = Wrapper.class.getDeclaredField("mockObject1.values");
	}

    @Test
    public void getSimpleNameTest(){
        assertEquals("MockObject1",MockObject1.class.getSimpleName());
    }
	
}
