package com.ucpb.tfs.domain.mtmessage;

import com.ucpb.tfs.domain.mtmessage.enumTypes.MessageClass;
import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.swift.message.parser.SwiftMessageParser;
import com.ucpb.tfs.util.FileUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
(revision)
Program Details: This will replace the long dash to short dash on Incoming MT files which TFS can read
                 It will also replace the word SPACE to actual space for the Incoming MT files coming from SWIFT Message Routing System
Revised by: Jesse James Joson
Date Revised: May 4, 2018
Project: CORE
Member Type: Java
Filename: SwiftMessageParserService.java
 */
@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class SwiftMessageParserService {

    private SwiftMessageParser messageParser;

    private MessageChannel invalidMessagesOutputChannel;

    private MessageChannel parsedMessagesOutputChannel;

    private MtMessageService mtMessageService;
    private String regex;

    private boolean moveFile = true;

    public SwiftMessageParserService(){
        //default constructor. needed by spring integration for some reason.
    }

    public SwiftMessageParserService(SwiftMessageParser parser, MessageChannel invalidMessagesOutputChannel, MessageChannel parsedMessagesOutputChannel, MtMessageService mtMessageService, String regex){
        this.messageParser = parser;
        this.invalidMessagesOutputChannel = invalidMessagesOutputChannel;
        this.parsedMessagesOutputChannel = parsedMessagesOutputChannel;
        this.mtMessageService = mtMessageService;
        this.regex = regex;
    }

    public void parse(File source) {
        try {
            String fileContents = FileUtil.getFileAsString(source);
            File regexFile = new File(regex);
            String regexStr = FileUtil.getFileAsString(regexFile); 
            regexStr = regexStr.replace("\n", "");
            fileContents = fileContents.replace(regexStr, "-");
            fileContents = fileContents.replace("[SPACE]", " ");
            List<String> firstPass =  extractToIndividualMessages(fileContents);
            if(firstPass!=null && !firstPass.isEmpty()){
                for(String swiftMessage : firstPass){

                    RawSwiftMessage raw = messageParser.parse(swiftMessage);
                    MessageClass messageClass = extractMessageClassFromBody(swiftMessage);
                    MtMessage message = new MtMessage(raw.getReference(),messageClass,swiftMessage,raw.getMessageType());
                    message.setSequenceNumber(raw.getSequenceNumber());
                    message.setSequenceTotal(raw.getSequenceTotal());
                    message.setFilename(source.getName());
//                printMtMessage(message);
                    mtMessageService.persist(message);

                }
            } else {
                List<String> secondPass =  extractToIndividualMessagesSharepoint(fileContents);
                if(secondPass!=null && !secondPass.isEmpty()){
                    for(String swiftMessage : secondPass){

                        RawSwiftMessage raw = messageParser.parse(swiftMessage);
                        MessageClass messageClass = extractMessageClassFromBody(swiftMessage);
                        MtMessage message = new MtMessage(raw.getReference(),messageClass,swiftMessage,raw.getMessageType());
                        message.setSequenceNumber(raw.getSequenceNumber());
                        message.setSequenceTotal(raw.getSequenceTotal());
                        message.setFilename(source.getName());
                        mtMessageService.persist(message);
                    }
                } else {
                    List<String> thirdPass = extractToIndividualMessagesSharepointVersion2(fileContents);
                    if(thirdPass!=null && !thirdPass.isEmpty()){
                        for(String swiftMessage : thirdPass){

                            RawSwiftMessage raw = messageParser.parse(swiftMessage);
                            MessageClass messageClass = extractMessageClassFromBody(swiftMessage);
                            MtMessage message = new MtMessage(raw.getReference(),messageClass,swiftMessage,raw.getMessageType());
                            message.setSequenceNumber(raw.getSequenceNumber());
                            message.setSequenceTotal(raw.getSequenceTotal());
                            message.setFilename(source.getName());
                            mtMessageService.persist(message);
                        }
                    } else {

                        List<String> fourthPass = extractToIndividualMessagesSharepointVersion3(fileContents);
                        if(fourthPass!=null && !fourthPass.isEmpty()){
                            for(String swiftMessage : fourthPass){

                                RawSwiftMessage raw = messageParser.parse(swiftMessage);
                                MessageClass messageClass = extractMessageClassFromBody(swiftMessage);
                                MtMessage message = new MtMessage(raw.getReference(),messageClass,swiftMessage,raw.getMessageType());
                                message.setSequenceNumber(raw.getSequenceNumber());
                                message.setSequenceTotal(raw.getSequenceTotal());
                                message.setFilename(source.getName());
                                mtMessageService.persist(message);
                            }
                        }

                    }

            }
        }

        } catch (Exception e){
            System.out.println("__________________________________________________________________________________________________");
            e.printStackTrace();
            if(moveFile){
                sendToChannel(source, invalidMessagesOutputChannel);
            }
            System.out.println("__________________________________________________________________________________________________");
            return;
        }
        if(moveFile){
            System.out.println("__________________________________________________________________________________________________");
            sendToChannel(source,parsedMessagesOutputChannel);
            System.out.println("Sending to Channel:"+source.getName());
            System.out.println("__________________________________________________________________________________________________");
        }
    }

    private void printMtMessage(MtMessage message) {

        System.out.println("id:"+message.getId());
        System.out.println("filename:"+message.getFilename());
        System.out.println("message class:"+message.getMessageClass());
        System.out.println("mt type:"+message.getMtType());
        System.out.println("sequence number:"+message.getSequenceNumber());
        System.out.println("sequence total:"+message.getSequenceTotal());
        System.out.println("tradeservice id:"+message.getTradeServiceId());
        System.out.println("message:"+message.getMessage());
    }

    public void setMoveFile(boolean moveFile) {
        this.moveFile = moveFile;
    }

    private <T> void sendToChannel(T payload,MessageChannel outputChannel){
        MessageBuilder builder = MessageBuilder.withPayload(payload);
        outputChannel.send(builder.build());
    }

    private List<String> extractToIndividualMessages(String fileContents){
        System.out.println("extractToIndividualMessages");
        String pattern = "\\s\\-{21}  Instance Type and Transmission \\-{14}\\s.+?(?=(\\s\\-{21}  Instance Type and Transmission \\-{14}\\s|\\z))";
        Matcher matcher = Pattern.compile(pattern, Pattern.DOTALL).matcher(fileContents);
        List<String> swiftMessages = new ArrayList<String>();
        while(matcher.find()){
            swiftMessages.add(matcher.group(0));
            System.out.println("angol matcher:" + matcher.group(0));
            System.out.println("swiftMessages.size() within finder:"+ swiftMessages.size());
        }

        System.out.println("swiftMessages.size() last:"+ swiftMessages.size());
        return swiftMessages;
    }

    private List<String> extractToIndividualMessagesSharepoint(String fileContents){
        System.out.println("extractToIndividualMessagesSharepoint");
        String pattern = "\\s\\-{21}  Instance Type and Transmission \\-{14}\\s.+?(?=(\\s\\-{5} Interventions \\-{5}\\s|\\z))";
        Matcher matcher = Pattern.compile(pattern, Pattern.DOTALL).matcher(fileContents);
        List<String> swiftMessages = new ArrayList<String>();
        while(matcher.find()){
            swiftMessages.add(matcher.group(0));
            System.out.println("angol matcher:" + matcher.group(0));
            System.out.println("swiftMessages.size() within finder:"+ swiftMessages.size());
        }

        System.out.println("swiftMessages.size() last:"+ swiftMessages.size());
        return swiftMessages;
    }

    private List<String> extractToIndividualMessagesSharepointVersion2(String fileContents){
        System.out.println("extractToIndividualMessagesSharepointVersion2");
        String pattern = "\\s\\-{21}  Instance Type and Transmission \\-{14}\\s.+?(?=(\\s\\-{5} Message Trailer \\-{5}\\s|\\z))";
        Matcher matcher = Pattern.compile(pattern, Pattern.DOTALL).matcher(fileContents);
        List<String> swiftMessages = new ArrayList<String>();
        while(matcher.find()){
            swiftMessages.add(matcher.group(0));
            System.out.println("matcher.group(0):"+matcher.group(0));
            System.out.println("matcher.group(1):"+matcher.group(1));
            System.out.println("angol matcher:" + matcher.group(0));
            System.out.println("swiftMessages.size() within finder:"+ swiftMessages.size());
        }

        System.out.println("swiftMessages.size() last:"+ swiftMessages.size());
        return swiftMessages;
    }

    private List<String> extractToIndividualMessagesSharepointVersion3(String fileContents){
        System.out.println("extractToIndividualMessagesSharepointVersion3");
        System.out.println("fileContents before:"+fileContents);
        System.out.println("fileContents after:"+fileContents.replace("Â",""));
        if(fileContents.compareTo(fileContents.replace("Â",""))!=0){
            System.out.println("Removing Â");
        }
        List<String> swiftMessages = new ArrayList<String>();
        String header01 = "---------------------  Instance Type and Transmission --------------" ;
        header01 = header01.replace("Â","");
        String header03 = "---  Instance Type and Transmission ---" ;
        String header04 = "Instance Type and Transmission --------------" ;
        System.out.println("header01:"+header01);
        String header02 = "---------------------";
        header02 = header02+" ";
        header02 = header02+" ";
        header02 = header02+"Instance";
        header02 = header02+" ";
        header02 = header02+"Type";
        header02 = header02+" ";
        header02 = header02+"and";
        header02 = header02+" ";
        header02 = header02+"Transmission";
        header02 = header02+" ";
        header02 = header02+"--------------";
        System.out.println("header02:"+header02);
        header02 = header02.replace("Â","");


        String footer01 = "--------------------------- Message Trailer ------------------------" ;
        footer01 = footer01.replace("Â","");
        System.out.println("footer01:"+footer01);
        String footer02 = "Message Trailer" ;
        System.out.println("footer02:"+footer02);
        String footer = "---------------------------";
        footer = footer+" ";
        footer = footer+"Message";
        footer = footer+" ";
        footer = footer+"Trailer";
        footer = footer+" ";
        footer = footer+"------------------------";

        System.out.println("footer:"+footer);
        System.out.println("fileContents.contains(header01):"+fileContents.contains(header01));
        System.out.println("fileContents.contains(header02):"+fileContents.contains(header02));
        System.out.println("fileContents.contains(header03):"+fileContents.contains(header03));
        System.out.println("fileContents.contains(header04):"+fileContents.contains(header04));
        System.out.println("fileContents.contains(footer):"+fileContents.contains(footer));
        System.out.println("fileContents.contains(footer01):"+fileContents.contains(footer01));
        System.out.println("fileContents.contains(footer02):"+fileContents.contains(footer02));
        System.out.println("fileContents.indexOf(header01):"+fileContents.indexOf(header01));
        System.out.println("fileContents.indexOf(header02):"+fileContents.indexOf(header02));
        System.out.println("fileContents.indexOf(header03):"+fileContents.indexOf(header03));
        System.out.println("fileContents.indexOf(header04):"+fileContents.indexOf(header04));
        System.out.println("fileContents.indexOf(footer):"+fileContents.indexOf(footer));
        System.out.println("fileContents.indexOf(footer01):"+fileContents.indexOf(footer01));
        System.out.println("fileContents.indexOf(footer02):"+fileContents.indexOf(footer02));
//        int start = fileContents.indexOf("---------------------  Instance Type and Transmission --------------");
//        int end = fileContents.lastIndexOf("--------------------------- Message Trailer ------------------------");
        int start = fileContents.indexOf(header01);
        if(start==-1){
            fileContents.indexOf(header02);
        }
        int end = fileContents.lastIndexOf(footer);
        if (end == -1){
            fileContents.indexOf(footer02);
        }
        System.out.println("start:"+start);
        System.out.println("end:"+end);
        if( start> -1 && end >-1){
            String message = fileContents.substring(start,end);
            System.out.println("message using start and end:"+message);
            swiftMessages.add(message);
        }

//        if(fileContents.contains("---------------------  Instance Type and Transmission --------------")
//                && fileContents.contains("--------------------------- Message Trailer ------------------------")){

//        if(fileContents.contains(header) && fileContents.contains(footer)){
//
//            String message = fileContents.substring(start,end);
//            System.out.println("message:"+ message);
//            swiftMessages.add(message);
//
//            //int lastIndex = fileContents.lastIndexOf("---------------------  Instance Type and Transmission --------------");
//            //currentIndex = fileContents.indexOf("---------------------  Instance Type and Transmission --------------",currentIndex);
//            int lastIndex = fileContents.lastIndexOf(header);
//            int firstIndex = fileContents.indexOf(header);
//
//
//            if(firstIndex == lastIndex){
//                System.out.println("SAME SAME SAME");
//            }
//        }

        System.out.println("swiftMessages.size() last:"+ swiftMessages.size());
        return swiftMessages;
    }


    private MessageClass extractMessageClassFromBody(String inputMessage){
        MessageClass messageClass = null;
        if(StringUtils.contains(inputMessage,"Swift Input")){
            messageClass = MessageClass.CONFIRMATION;
        }else if(StringUtils.contains(inputMessage,"Swift Output")){
            messageClass = MessageClass.INCOMING;
        }
        return messageClass;
    }

}
