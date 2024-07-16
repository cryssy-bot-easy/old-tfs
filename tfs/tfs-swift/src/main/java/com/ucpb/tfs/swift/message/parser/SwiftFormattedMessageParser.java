package com.ucpb.tfs.swift.message.parser;

import com.ucpb.tfs.swift.message.ApplicationHeader;
import com.ucpb.tfs.swift.message.MessageBlock;
import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.swift.message.Tag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SwiftFormattedMessageParser implements SwiftMessageParser {

    public static final String MESSAGE_BODY_CONTENTS = "\\-+ Message Text \\-+(.*)\\s(\\-+ Message Trailer \\-+)";

    public static final String MESSAGE_BODY_FIELDS = "(\\d+\\w?):\\s([.[^\\n]]+\\n)\\s+([./\\d\\s\\w\\Q:()#,+.-'\\E]+?(?=\\d+\\w?:|\\z))";

    public static final String BASIC_HEADER_CONTENTS = "\\-+ Instance Type and Transmission \\-+(.*)\\s\\- Message Header \\-+";

    public static final String APPLICATION_HEADER_CONTENTS = "\\-+ Message Header \\-+(.*)\\s\\-+ Message Text \\-+";

    public final static String CURRENCY_AMOUNT_FORMAT = "Currency\\s+: (\\w{3}) \\([\\w\\s]+\\)\\s\\s+Amount\\s+:\\s+#(\\d+,?\\d*)#";

    private static final Set<String> CURRENCY_AMOUNT_TAGS = new HashSet<String>();

    static {
        CURRENCY_AMOUNT_TAGS.add("32B");
    }



    @Override
    public RawSwiftMessage parse(String message) throws ParseException {
        //System.out.println("SwiftFormattedMessageParser");
        RawSwiftMessage rawSwiftMessage = new RawSwiftMessage();
        rawSwiftMessage.setApplicationHeader(extractApplicationHeader(extractContents(message, APPLICATION_HEADER_CONTENTS)));
        try {
            rawSwiftMessage.setMessageBlock(extractMessageBlock(extractContents(message,MESSAGE_BODY_CONTENTS)));
        } catch (Exception pe){
            //System.out.println("Appending - Message Trailer -");
            rawSwiftMessage.setMessageBlock(extractMessageBlock(extractContents(message.concat("- Message Trailer -"),MESSAGE_BODY_CONTENTS)));
        }

        return rawSwiftMessage;
    }

    private MessageBlock extractMessageBlock(String message){
        System.out.println("extractMessageBlock");

        //Skip identifier tags (tags without values for matcher.group(3))
        if(message.contains("15A: New Sequence A")){
            System.out.println("Skip parsing of 15A");
            message = message.replace("15A: New Sequence A","");
        }
        if(message.contains("15B: New Sequence B")){
            System.out.println("Skip parsing of 15B");
            message = message.replace("15B: New Sequence B","");
        }

        Matcher matcher = Pattern.compile(MESSAGE_BODY_FIELDS,Pattern.DOTALL).matcher(message);
        List<Tag> outputTags = new ArrayList<Tag>();
        while(matcher.find()){
            //determine if tag is a currency tag
            
            if(CURRENCY_AMOUNT_TAGS.contains(matcher.group(1))){
                //System.out.println(">>>>>> extractMessageBlock: message - " + matcher.group(0) +" "+ matcher.group(1) +" "+matcher.group(3));
                outputTags.add(new Tag(matcher.group(1),convertCurrencyToSwiftFormat(matcher.group(3))));
            }else{
                //System.out.println(">>>>>> extractMessageBlock: message - " + matcher.group(0) +" "+ matcher.group(1) +" "+matcher.group(3));
                outputTags.add(new Tag(matcher.group(1),matcher.group(3)));
            }
        }

        return new MessageBlock(outputTags);
    }

    private ApplicationHeader extractApplicationHeader(String message){
        ApplicationHeader applicationHeader = new ApplicationHeader();
        Matcher matcher = Pattern.compile("\\s+(Swift Input|Swift Output)\\s+: \\w+ (\\d{3}) [\\w\\s]+").matcher(message);
        if(matcher.find()){
            applicationHeader.setMessageType(matcher.group(2));
        }
        return applicationHeader;
    }


    private String extractContents(String source,String regex) throws ParseException {
        //System.out.println(">>>>>> extractMessageBlock: source - " + source);
        //System.out.println(">>>>>> extractMessageBlock: regex - " + regex);
        Matcher matcher = Pattern.compile(regex,Pattern.DOTALL).matcher(source);
        if(matcher.find()){
            //System.out.println(">>>>>> extractMessageBlock: matcher.group(1) - " + matcher.group(1));
            return matcher.group(1);
        }
        throw new ParseException("Could not extract text from source");
    }

    private String convertCurrencyToSwiftFormat(String tagValue){
        String preformattedMessage = tagValue.replaceAll(",","").replaceAll("\\.",",");
        Matcher matcher = Pattern.compile(CURRENCY_AMOUNT_FORMAT).matcher(preformattedMessage);
        if(matcher.find()){
            String result = matcher.group(1) + matcher.group(2);
            if(!result.contains(",")){
                result += ",";
            }
            return result;
        }
        return null;
    }



}
