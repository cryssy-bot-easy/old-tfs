package com.ucpb.tfs.interfaces.gateway.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;

import com.ucpb.tfs.interfaces.gateway.FinRequest;
import com.ucpb.tfs.interfaces.gateway.TransactionCode;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.ucpb.tfs.interfaces.gateway.CasaRequest;

public class CasaSerializerTest {

	private static final String DEFAULT_ENCODING = "CP1047";

	private final CasaSerializer casaSerializer = new CasaSerializer();
	
	private CasaHeaderDeserializer casaHeaderDeserializer = new CasaHeaderDeserializer();
	
	private OutputStream outputStream;
	
	private StringBuilder mockResponse;
	

	@Before
	public void setup(){
		casaSerializer.setCasaHeaderDeserializer(casaHeaderDeserializer);
		outputStream = mock(OutputStream.class);
		mockResponse = new StringBuilder();
		mockResponse
		.append("0000")
		.append("refNum")
		.append("taskId01")
		.append(StringUtils.leftPad("", 48," "));
	}
	
	@Test
	public void successfullySerializeCasaMessage() throws IOException{
		CasaRequest casaRequest = buildDefaultRequest();
		byte[] requestInBytes = casaRequest.pack(DEFAULT_ENCODING);
		casaSerializer.serialize(casaRequest, outputStream);
        verify(outputStream).write(0);
        verify(outputStream).write(casaRequest.toRequestString().length());
		verify(outputStream).write(requestInBytes);
	}
	
	@Test
	public void successfullyDeserializeSuccessfulResponse() throws IOException{
		int responseLength = mockResponse.toString().length();
        byte[] byteHeader = new byte[2];
        byteHeader[0] = 0;
        byteHeader[1] = 66;
        MockInputStream mockInputStream = new MockInputStream();
        mockInputStream.addMockResponse(byteHeader);
        mockInputStream.addMockResponse(mockResponse.toString());
		
		String response = casaSerializer.deserialize(mockInputStream);
		StringBuilder builder = new StringBuilder();
		builder.append("0000")
		.append("refNum")
		.append("taskId01")
		.append(StringUtils.leftPad("", 48,' '));
		System.out.println("*" + response + "*");
		assertEquals(StringUtils.trim(builder.toString()),StringUtils.trim(response));
	}
	
	private CasaRequest buildDefaultRequest(){
        FinRequest request = new FinRequest();
		request.setUsername("user");
		request.setPassword("pass");
		request.setBranchCode("341");
		request.setUserId("uId");
		request.setTransactionCode(TransactionCode.CREDIT_ERROR_CORRECT_FOREIGN);
		request.setAccountNumber("accountNum");
		request.setAmount(new BigDecimal("18.12"));

		return request;
	}
}
