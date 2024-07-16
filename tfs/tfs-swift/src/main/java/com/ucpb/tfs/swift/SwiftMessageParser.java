package com.ucpb.tfs.swift;

import com.ucpb.tfs.swift.message.ApplicationHeader;
import com.ucpb.tfs.swift.message.SwiftAddress;
import com.ucpb.tfs.swift.message.SwiftMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public class SwiftMessageParser {

    private static final String APP_HEADER = "\\{(2):([IO])(\\d{3})(.*)\\}";
    private static final String BASIC_HEADER = "\\{(1):([A-Z])(\\d{2})([\\w[^_]]{12})(\\d{4})(\\d{6})\\}";
    private static final String MESSAGE_BLOCK = "\\{(4:)(\\s.*)*\\Q-}\\E";
    private static final String MESSAGE_BODY_FIELDS = ":(\\d+\\w*):(([^:].+\\s)+)";

    private SwiftMessageBuilder messageBuilder;

    public SwiftMessage parse(String message) throws ParseException {
        ApplicationHeader appHeader = getApplicationHeader(message);
        Map<String,String> fields = getMessageBlockFields(getBlock(message, MESSAGE_BLOCK, "Message Block"));
        SwiftMessage swiftMessage = messageBuilder.build(appHeader.getMessageType(), fields);
        swiftMessage.setApplicationHeader(appHeader);
        return swiftMessage;
    }

    public void setMessageBuilder(SwiftMessageBuilder messageBuilder) {
        this.messageBuilder = messageBuilder;
    }

    private Map<String,String> getMessageBlockFields(String messageBlock){
        Matcher matcher = getMatcherForPattern(messageBlock, MESSAGE_BODY_FIELDS);
        Map<String,String> fields = new HashMap<String,String>();
        while(matcher.find()){
            fields.put(matcher.group(1), matcher.group(2));
        }
        return fields;
    }

    private ApplicationHeader getApplicationHeader(String message) throws ParseException {
        Matcher matcher = getMatcherForPattern(message,APP_HEADER);
        if(matcher.find()){
            ApplicationHeader header = new ApplicationHeader();
            header.setIoIdentifier(matcher.group(2));
            header.setMessageType(matcher.group(3));

            SwiftAddress address = new SwiftAddress();
            address.setCompleteAddress(matcher.group(4));
            header.setReceiverAddress(address);
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
