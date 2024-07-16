package com.ucpb.tfs.interfaces.gateway.serializer;

import java.io.IOException;
import java.io.OutputStream;

import org.springframework.core.serializer.Serializer;

public class StringSerializer implements Serializer<String>{

	private String encoding;
	
	@Override
	public void serialize(String input, OutputStream output) throws IOException {
		output.write(0);
        output.write(input.length());
		output.write(input.getBytes(getEncoding()));
		output.flush();
	}

	public String getEncoding() {
		return encoding != null ? encoding : CasaSerializer.DEFAULT_ENCODING;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	
}
