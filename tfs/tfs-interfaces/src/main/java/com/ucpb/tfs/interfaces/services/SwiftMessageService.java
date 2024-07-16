package com.ucpb.tfs.interfaces.services;

import com.ucpb.tfs.interfaces.services.exception.ValidationException;
import com.ucpb.tfs.swift.message.AbstractSwiftMessage;
import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.swift.message.SwiftMessage;

/**
 */
public interface SwiftMessageService {


    /**
     * Generates and sends the Swift message then returns a unique identifier (sequence number,filename, etc)
     * for the generated message (if applicable)
     * @param message
     * @return the unique identifier (sequence number,filename, etc) of the generated message
     * @throws ValidationException
     */
    public String sendMessage(RawSwiftMessage message) throws ValidationException;

}
