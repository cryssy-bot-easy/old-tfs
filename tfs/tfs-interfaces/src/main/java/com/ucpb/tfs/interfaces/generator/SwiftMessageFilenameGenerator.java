package com.ucpb.tfs.interfaces.generator;

import com.ucpb.tfs.swift.message.SwiftMessage;
import org.springframework.integration.Message;
import org.springframework.integration.file.FileNameGenerator;

public class SwiftMessageFilenameGenerator implements FileNameGenerator{

	@Override
	public String generateFileName(Message<?> message) {
		String messageType = (String) message.getHeaders().get("type");
        return "5620MT" + messageType.charAt(0) + "X." + "SEQ";
	}

}
