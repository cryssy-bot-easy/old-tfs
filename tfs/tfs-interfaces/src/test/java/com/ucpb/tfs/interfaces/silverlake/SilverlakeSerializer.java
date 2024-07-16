package com.ucpb.tfs.interfaces.silverlake;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.serializer.Deserializer;
import org.springframework.core.serializer.Serializer;

import com.ucpb.tfs.interfaces.gateway.CasaResponse;
import com.ucpb.tfs.interfaces.gateway.serializer.CasaHeaderDeserializer;
import com.ucpb.tfs.interfaces.gateway.serializer.CasaSerializer;

public class SilverlakeSerializer implements Deserializer<String>, Serializer<CasaResponse>{

	public static final String DEFAULT_ENCODING = "CP1047";

	private String encoding;
	
	private CasaHeaderDeserializer casaHeaderDeserializer;
	
	public String getEncoding() {
		return encoding != null ? encoding : DEFAULT_ENCODING;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setCasaHeaderDeserializer(CasaHeaderDeserializer casaHeaderDeserializer) {
		this.casaHeaderDeserializer = casaHeaderDeserializer;
	}
	
	@Override
	public void serialize(CasaResponse response, OutputStream output)
			throws IOException {
		byte[] rawResponse = StringUtils.rightPad("0000refNumtaskId01",48, " ").getBytes(CasaSerializer.DEFAULT_ENCODING);
		output.write(String.valueOf(rawResponse.length).getBytes(CasaSerializer.DEFAULT_ENCODING));
		output.write(rawResponse);
		output.flush();
	}

	@Override
	public String deserialize(InputStream input) throws IOException {
		int messageLength = casaHeaderDeserializer.deserialize(input);
		int totalBytesRead = 0;
		int bytesRead = 0;
		byte[] data = new byte[messageLength];
		StringBuilder builder = new StringBuilder();
		while(totalBytesRead!= messageLength && (bytesRead = input.read(data,0,messageLength - totalBytesRead)) != -1){
			totalBytesRead += bytesRead;
			builder.append(new String(data,getEncoding()));
		}
		
		System.out.println("RECEIVED INPUT : " + builder.toString());
		return builder.toString();
	}

}
