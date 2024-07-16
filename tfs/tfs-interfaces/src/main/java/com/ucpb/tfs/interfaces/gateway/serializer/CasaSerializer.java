package com.ucpb.tfs.interfaces.gateway.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.ucpb.tfs.interfaces.gateway.CasaRequest;

import org.springframework.core.serializer.Deserializer;
import org.springframework.core.serializer.Serializer;

public class CasaSerializer implements Serializer<CasaRequest>, Deserializer<String>{

	public static final String DEFAULT_ENCODING = "CP1047";
    private static final int HEADER_PAD = 0;
    private static final int OFFSET = HEADER_PAD;

    private String encoding;
	
	private CasaHeaderDeserializer casaHeaderDeserializer;
	
	@Override
	public void serialize(CasaRequest request, OutputStream outputStream) throws IOException {
		byte[] byteRequest = request.pack(getEncoding());
		outputStream.write(HEADER_PAD);
        outputStream.write(byteRequest.length);
        outputStream.write(byteRequest);
		outputStream.flush();
	}
	
	@Override
	public String deserialize(InputStream inputStream) throws IOException {
		int messageLength = casaHeaderDeserializer.deserialize(inputStream);
		int totalBytesRead = 0;
		int bytesRead = 0;
		byte[] data = new byte[messageLength];
		StringBuilder builder = new StringBuilder();
		while(totalBytesRead!= messageLength && (bytesRead = inputStream.read(data, OFFSET,messageLength - totalBytesRead)) != -1){
			totalBytesRead += bytesRead;
			builder.append(new String(data,getEncoding()));
		}
		return builder.toString();
	}
	
	public String getEncoding() {
		return encoding != null ? encoding : DEFAULT_ENCODING;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setCasaHeaderDeserializer(CasaHeaderDeserializer casaHeaderDeserializer) {
		this.casaHeaderDeserializer = casaHeaderDeserializer;
	}

}
