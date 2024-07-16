package com.ucpb.tfs.swift.validator.xml.parser;

import com.ucpb.tfs.swift.validator.ValidationError;
import com.ucpb.tfs.swift.validator.xml.parser.exception.ParserConfigurationException;
import org.springframework.util.Assert;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public abstract class ErrorMessageParser {

    protected abstract ValidationError mapToValidationError(String[] response);

    protected abstract String getFormat();

    public final ValidationError parse(String errorMessage){
        Assert.notNull(errorMessage, "Response to be parsed must not be null!");
        ValidationError error = null;
        Matcher matcher = Pattern.compile(getFormat(),Pattern.DOTALL).matcher(errorMessage);

        if(matcher.find()){
            error = mapToValidationError(toStringArray(matcher));
        }else{
            throw new ParserConfigurationException("Parser configuration does not match the given error message: '" + errorMessage + "'");
        }
        return error;
    }

    private String[] toStringArray(Matcher matcher){
        String[] map = new String[matcher.groupCount()];
        for(int ctr = 0; ctr < matcher.groupCount(); ctr++){
            map[ctr] = matcher.group(ctr+1);
        }
        return map;
    }
}