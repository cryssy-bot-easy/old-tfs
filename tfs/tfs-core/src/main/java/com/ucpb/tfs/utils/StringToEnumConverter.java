package com.ucpb.tfs.utils;

import org.dozer.CustomConverter;

public class StringToEnumConverter implements CustomConverter {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object convert(Object destination, Object source, Class<?> destinationClass,    Class<?> sourceClass) {
        Object result = null;
        if(source == null){
	        return null;
	    }
	    
        if(java.lang.String.class.equals(destinationClass)){
            result = source.toString();
        }else if(destinationClass.isEnum()){
        	try{
        		result = Enum.valueOf((Class<Enum>)destinationClass,source.toString());
        	}catch (IllegalArgumentException e){
        		//ignore exception
        	}
        }
        return result;
	}

}
