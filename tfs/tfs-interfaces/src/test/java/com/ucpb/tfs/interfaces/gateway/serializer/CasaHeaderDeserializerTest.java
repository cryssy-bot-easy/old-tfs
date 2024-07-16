package com.ucpb.tfs.interfaces.gateway.serializer;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CasaHeaderDeserializerTest {

	private CasaHeaderDeserializer casaHeaderDeserializer = new CasaHeaderDeserializer();
	
	private MockInputStream mockInputStream;
	
	@Before
	public void setup(){
		mockInputStream = new MockInputStream();
        mockInputStream.clearMockResponses();
    }
	
	
	@Test
	public void successfullyDeserializeDoubleDigitHeader() throws IOException{
//		mockInputStream.addMockResponse("66".getBytes(CasaSerializer.DEFAULT_ENCODING));
        byte[] response = new byte[2];
        response[0] = 0;
        response[1] = 66;
        mockInputStream.addMockResponse(response);
		assertEquals(66,casaHeaderDeserializer.deserialize(mockInputStream).intValue());
	}
	
	@Test
	public void successfullyDeserializeSingleDigitHeader() throws IOException{
        byte[] response = new byte[2];
        response[0] = 0;
        response[1] = 6;
        mockInputStream.addMockResponse(response);
        assertEquals(6,casaHeaderDeserializer.deserialize(mockInputStream).intValue());
	}
	
	@Test
	public void returnLengthZeroOnEmptyRead() throws IOException{
		mockInputStream.clearMockResponses();
		int length = casaHeaderDeserializer.deserialize(mockInputStream);
		assertEquals(0,length);
	}
	
}
