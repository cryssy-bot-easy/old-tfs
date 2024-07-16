package com.ucpb.tfs.swift.message.parser;

import com.ucpb.tfs.swift.message.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Default implementation of the SwiftMessageParser.
 *
 */
public class SimpleSwiftMessageParser implements SwiftMessageParser {

    private static final String INPUT_APPLICATION_HEADER = "\\{(2):([IO])(\\d{3})([\\w[^_]]{11,12})([SUN])(\\d?)([\\d{3}]?)\\}";
    private static final String OUTPUT_APPLICATION_HEADER = "\\{(2):([IO])(\\d{3})(.*)\\}";

    private static final String BASIC_HEADER = "\\{(1):([FAL])(\\d{2})([\\w[^_]]{12})(\\d{4})(\\d{6})\\}";
    private static final String MESSAGE_BLOCK = "\\{(4:)(\\s.*)*\\Q-}\\E";
    private static final String MESSAGE_BODY_FIELDS = ":(\\d+\\w*):(([^:].+\\s)+)";

    @Override
    public RawSwiftMessage parse(String message) throws ParseException{
        System.out.println("SimpleSwiftMessageParser");
        RawSwiftMessage swiftMessage = new RawSwiftMessage();
        swiftMessage.setBasicHeader(getBasicHeader(message));
        swiftMessage.setApplicationHeader(getApplicationHeader(message));
        swiftMessage.setMessageBlock(getMessageBlock(getBlock(message,MESSAGE_BLOCK,"Message Block")));
        return swiftMessage;
    }


    private MessageBlock getMessageBlock(String messageBlock){
        Matcher matcher = getMatcherForPattern(messageBlock, MESSAGE_BODY_FIELDS);
        List<Tag> outputTags = new ArrayList<Tag>();
        while(matcher.find()){
            outputTags.add(new Tag(matcher.group(1),matcher.group(2)));
        }
        return new MessageBlock(outputTags);
    }

    private BasicHeader getBasicHeader(String message) throws ParseException {
        Matcher matcher = getMatcherForPattern(message,BASIC_HEADER);
        if(matcher.find()){

            BasicHeader header = new BasicHeader();
            header.setApplicationIdentifier(matcher.group(2));
            header.setServiceIndentifier(matcher.group(3));
            header.setLtIdentifier(matcher.group(4));
            header.setSessionNumber(matcher.group(5));
            header.setSequenceNumber(matcher.group(6));

            return header;
        }
        throw new ParseException("Message does not contain a basic header");
    }

    private ApplicationHeader getApplicationHeader(String message) throws ParseException {
        Matcher matcher = getMatcherForPattern(message, INPUT_APPLICATION_HEADER);
        if(matcher.find()){
            ApplicationHeader header = new ApplicationHeader();
            header.setIoIdentifier(matcher.group(2));
            header.setMessageType(matcher.group(3));

            SwiftAddress address = new SwiftAddress();
            address.setCompleteAddress(matcher.group(4));
            header.setReceiverAddress(address);

            header.setMessagePriority(matcher.group(5));
            header.setDeliveryMonitoring(matcher.group(6));
            header.setObsolescencePeriod(matcher.group(7));

            return header;
        }
        throw new ParseException("Message does not contain an application header");
    }

    private String getBlock(String message, String blockRegex, String blockName) throws ParseException {
        Matcher matcher = getMatcherForPattern(message,blockRegex);
        if(matcher.find()){
            return matcher.group();
        }
        throw new ParseException("Message does not contain block: " + blockName);
    }

    private Matcher getMatcherForPattern(String string, String regex){
        return Pattern.compile(regex).matcher(string);
    }
}
