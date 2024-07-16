package com.ucpb.tfs.util;

import com.ucpb.tfs.utils.StringToEnumConverter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 */
public class StringToEnumConverterTest {

    private StringToEnumConverter stringToEnumConverter = new StringToEnumConverter();


    private enum Status {
        PENDING,APPROVED,ABORTED;
    }

    @Test
    public void successfullyConvertValidStringToEnum(){
        Status status = null;
        assertEquals(Status.PENDING,stringToEnumConverter.convert(status,"PENDING",Status.class,String.class));
    }

    @Test
    public void failToConvertValidCamelCaseStringToEnum(){
        Status status = null;
        assertEquals(null,stringToEnumConverter.convert(null,"PenDinG",Status.class,String.class));
    }

    @Test
    public void failToConvertInvalidStringToEnum(){
        Status status = null;
        assertEquals(null,stringToEnumConverter.convert(status,"INVALIDENUM",Status.class,String.class));
    }

    @Test
    public void successfullyConvertEnumToString(){
        String value = "VALUE";
        assertEquals("PENDING",stringToEnumConverter.convert(value,Status.PENDING,String.class,Status.class));
    }


}
