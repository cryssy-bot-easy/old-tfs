package com.ucpb.tfs.interfaces.gateway.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.ucpb.tfs.interfaces.gateway.serializer.CasaSerializer;

public class MockInputStream extends InputStream {

	private String mockResponse;

	private List<String> mockResponses = new ArrayList<String>();

	public MockInputStream() {

	}

	public MockInputStream(String response) {
		this.mockResponse = response;
	}

	@Override
	public int read() throws IOException {
		return mockResponse.getBytes(CasaSerializer.DEFAULT_ENCODING)[0];
	}

	@Override
	public int read(byte[] output) throws UnsupportedEncodingException {

		if (!mockResponses.isEmpty()) {
			byte[] out = mockResponses.remove(0).getBytes(
					CasaSerializer.DEFAULT_ENCODING);
			for (int ctr = 0; ctr < out.length; ctr++) {
				output[ctr] = out[ctr];
			}
			return out.length;
		}
		return -1;

	}

	@Override
	public int read(byte[] output, int offset, int length)
			throws UnsupportedEncodingException {
		return read(output);

	}

	public String getMockResponse() {
		return mockResponses.get(0);
	}

	public void addMockResponse(String mockResponse) {
		mockResponses.add(mockResponse);
	}

	public void addMockResponse(byte[] byteResponse)
			throws UnsupportedEncodingException {
		mockResponses.add(new String(byteResponse,
				CasaSerializer.DEFAULT_ENCODING));
	}
	
	public void clearMockResponses(){
		mockResponses.clear();
	}
}
