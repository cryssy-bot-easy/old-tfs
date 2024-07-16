package com.ucpb.tfs.swift.validator.xml.parser;

import com.ucpb.tfs.swift.validator.xml.XmlErrorCode;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 */
public class ErrorMessageParserFactory {

    private static final ErrorMessageParser DEFAULT_ERROR_MESSAGE_PARSER = new DefaultErrorMessageParser();
    private static List<ErrorMessageParser> ERROR_MESSAGE_PARSERS_LIST = new ArrayList<ErrorMessageParser>();

    private ErrorMessageParserFactory(){
        //should not be instantiated
    }

    static {
        ERROR_MESSAGE_PARSERS_LIST.add(new InvalidPatternParser());
        ERROR_MESSAGE_PARSERS_LIST.add(new InvalidMaxLengthParser());
        ERROR_MESSAGE_PARSERS_LIST.add(new InvalidMinLengthParser());
        ERROR_MESSAGE_PARSERS_LIST.add(new InvalidElementParser());
        ERROR_MESSAGE_PARSERS_LIST.add(new InvalidContentParser());
        ERROR_MESSAGE_PARSERS_LIST.add(new InvalidIncompleteContentParser());
    }
    
    public static ErrorMessageParser getInstance(String errorMessage){
    	Matcher matcher;
        for (ErrorMessageParser messageParser : ERROR_MESSAGE_PARSERS_LIST){
        	matcher=Pattern.compile(messageParser.getFormat(), Pattern.DOTALL).matcher(errorMessage);
        	
            if(matcher.matches()){
                return messageParser;
            }
        }
        return DEFAULT_ERROR_MESSAGE_PARSER;
    }
}