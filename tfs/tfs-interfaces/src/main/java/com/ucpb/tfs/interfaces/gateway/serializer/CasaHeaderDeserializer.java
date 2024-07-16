package com.ucpb.tfs.interfaces.gateway.serializer;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.serializer.Deserializer;

public class CasaHeaderDeserializer implements Deserializer<Integer> {

	public static final int MAX_HEADER_LENGTH = 2;
	private String encoding;

	@Override
	public Integer deserialize(InputStream input) throws IOException {
		int totalBytesRead = 0;
		int bytesRead = 0;
		byte[] messageLength = new byte[MAX_HEADER_LENGTH];
		
		while (totalBytesRead != MAX_HEADER_LENGTH
				&& (bytesRead = input.read(messageLength, totalBytesRead, MAX_HEADER_LENGTH - totalBytesRead)) != -1) {
			totalBytesRead += bytesRead;
		}
        return combineBytes(messageLength[0],messageLength[1]);
	}

	public String getEncoding() {
		return encoding != null ? encoding : CasaSerializer.DEFAULT_ENCODING;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

    private int combineBytes(byte firstByte, byte secondBye){
        return ((firstByte << 8) | (secondBye & 0xFF));
    }

}
