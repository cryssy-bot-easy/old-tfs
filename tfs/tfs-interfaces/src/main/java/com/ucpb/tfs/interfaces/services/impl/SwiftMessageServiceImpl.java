package com.ucpb.tfs.interfaces.services.impl;

import com.ucpb.tfs.interfaces.services.SwiftMessageService;
import com.ucpb.tfs.interfaces.services.exception.ValidationException;
import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.swift.message.splitter.Splitter;
import com.ucpb.tfs.swift.message.splitter.SplitterFactory;
import com.ucpb.tfs.swift.validator.ValidationError;
import com.ucpb.tfs.swift.message.writer.JaxbXmlSwiftMessageWriter;
import com.ucpb.tfs.swift.validator.SwiftValidator;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.integration.Message;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

/**
(revision)
Program Details: Include .fin as the file extension of the outgoing MT files
Revised by: Jesse James Joson
Date Revised: May 4, 2018
Project: CORE
Member Type: Java
Filename: SwiftMessageServiceImpl.java
 */
public class SwiftMessageServiceImpl implements SwiftMessageService {

    private MessageChannel messageChannel;
    private List<SwiftValidator> validators;
    private JdbcTemplate jdbcTemplate;
    private boolean validate = true;

    public SwiftMessageServiceImpl(MessageChannel messageChannel, JdbcTemplate jdbcTemplate){
        this.messageChannel = messageChannel;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String sendMessage(RawSwiftMessage message) throws ValidationException {
        if(validate && validators != null){
            validate(message);
        }
        MessageBuilder<RawSwiftMessage> builder = MessageBuilder.withPayload(message);
        String filename = generateFilename(message)+".fin";
        builder.setHeader("filename",filename);
        messageChannel.send(builder.build());
        return filename;
    }

    public void setValidators(List<SwiftValidator> validators) {
        this.validators = validators;
    }
    

    public void setValidate(boolean validate) {
		this.validate = validate;
	}


    private void validate(RawSwiftMessage message) throws ValidationException {
        List<ValidationError> errors = new ArrayList<ValidationError>();
        for(SwiftValidator validator : validators){
            errors.addAll(validator.validate(message));
        }
        if(!errors.isEmpty()){
        	throw new ValidationException(errors,"The input swift message was rejected because it contained validation errors");
        }
   }

    private String generateFilename(RawSwiftMessage message){
        int sequenceNumber = jdbcTemplate.queryForInt("VALUES (NEXT VALUE FOR MT_MESSAGE_SEQUENCE)");
        return String.format("5620MT%1$1sX.%2$03d",StringUtils.left(message.getMessageType(),1),sequenceNumber);
    }

}
