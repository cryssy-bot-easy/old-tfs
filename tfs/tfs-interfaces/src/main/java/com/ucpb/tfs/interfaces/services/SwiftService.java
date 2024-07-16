package com.ucpb.tfs.interfaces.services;


import com.ucpb.tfs.interfaces.services.exception.ValidationException;
import com.ucpb.tfs.swift.message.SwiftMessage;

public interface SwiftService {

	public void sendMessage(SwiftMessage message) throws ValidationException;
	
}
