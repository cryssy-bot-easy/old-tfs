package com.ucpb.tfs.interfaces.services.impl;

import com.ucpb.tfs.interfaces.services.exception.ValidationException;
import com.ucpb.tfs.swift.message.ApplicationHeader;
import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.swift.message.splitter.Splitter;
import com.ucpb.tfs.swift.message.splitter.SplitterFactory;
import com.ucpb.tfs.swift.message.writer.SwiftMessageWriter;
import com.ucpb.tfs.swift.validator.SwiftValidator;
import com.ucpb.tfs.swift.validator.ValidationError;
import org.junit.Before;
import org.junit.Test;
import org.springframework.integration.Message;
import org.springframework.integration.MessageChannel;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 */
public class SwiftMessageServiceImplTest {

    private SwiftMessageServiceImpl swiftMessageService;

    private MessageChannel messageChannel;

    private SwiftMessageWriter writer;

    private SwiftValidator validator;

    private JdbcTemplate jdbcTemplate;


    @Before
    public void setup(){
        messageChannel = mock(MessageChannel.class);
        writer = mock(SwiftMessageWriter.class);
        jdbcTemplate = mock(JdbcTemplate.class);
        when(writer.write(any(RawSwiftMessage.class))).thenReturn("THISISASWIFTMESSAGE");
        when(jdbcTemplate.queryForInt(anyString())).thenReturn(1,2,3,4,5,6,7,8,9,10);
        swiftMessageService = new SwiftMessageServiceImpl(messageChannel,jdbcTemplate);
        validator = mock(SwiftValidator.class);
//        swiftMessageService.setSwiftMessageWriter(writer);
    }


    @Test
    public void sendSwiftMessageToChannelIfMessagePassesValidation() throws ValidationException {
        when(validator.validate(any(RawSwiftMessage.class))).thenReturn(new ArrayList<ValidationError>());
        List<SwiftValidator> validators = new ArrayList<SwiftValidator>();
        validators.add(validator);
        swiftMessageService.setValidators(validators);
        swiftMessageService.setValidate(true);

        swiftMessageService.sendMessage(new RawSwiftMessage());
        verify(validator).validate(any(RawSwiftMessage.class));
        verify(messageChannel).send(any(Message.class));
    }

    @Test(expected = ValidationException.class)
    public void throwValidationExceptionOnFailedValidation() throws ValidationException {
        List<ValidationError> errorList = new ArrayList<ValidationError>();
        ValidationError error = new ValidationError("This message is invalid.");
        errorList.add(error);

        when(validator.validate(any(RawSwiftMessage.class))).thenReturn(errorList);
        List<SwiftValidator> validators = new ArrayList<SwiftValidator>();
        validators.add(validator);

        swiftMessageService.setValidators(validators);
        swiftMessageService.setValidate(true);

        swiftMessageService.sendMessage(new RawSwiftMessage());
    }

    @Test
    public void doNotValidateWhenValidateFlagIsFalse() throws ValidationException {
        List<ValidationError> errorList = new ArrayList<ValidationError>();
        ValidationError error = new ValidationError("This message is invalid.");
        errorList.add(error);

        when(validator.validate(any(RawSwiftMessage.class))).thenReturn(errorList);
        List<SwiftValidator> validators = new ArrayList<SwiftValidator>();
        validators.add(validator);

        swiftMessageService.setValidators(validators);
        swiftMessageService.setValidate(false);

        swiftMessageService.sendMessage(new RawSwiftMessage());

        verify(messageChannel).send(any(Message.class));
    }
}
