package com.ucpb.tfs.swift.message.splitter;

import com.ucpb.tfs.swift.message.Tag;
import com.ucpb.tfs.swift.message.RawSwiftMessage;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public abstract class Splitter{

    public abstract List<RawSwiftMessage> split(RawSwiftMessage swiftMessage);
    
    private int totalCount = 1;
    
    protected abstract int messageLimit();

    protected abstract int lineLimit();
    
    protected boolean isLimitReached(RawSwiftMessage swiftMessage){
        return swiftMessage.length() > messageLimit();
    }

    protected boolean isLimitReached(RawSwiftMessage swiftMessage,String tagName){
    	boolean result = false;
    	if(swiftMessage.getMessageBlock().getTagByName(tagName) != null){
    		String input = swiftMessage.getMessageBlock().getTagByName(tagName).getValue();
    		if(isMessageLimitReached(input) || isLineLimitReached(input)){
    			result = true;
    		}
    	}
    	return result;
    }
    
    protected RawSwiftMessage getInstance(RawSwiftMessage source){
        RawSwiftMessage swiftMessage = new RawSwiftMessage();
        swiftMessage.setApplicationHeader(source.getApplicationHeader());
        swiftMessage.setBasicHeader(source.getBasicHeader());
        return swiftMessage;
    }

    protected int getTotalCount(){
    	return totalCount;
    }
    
    protected void appendTotalCount(int count){
    	totalCount+=count;
    }
    
    /**
     * Transfers the entire tag from source to target
     * @param source
     * @param target
     * @param sourceTag
     * @param destinationTag
     */
    protected void merge(RawSwiftMessage source, RawSwiftMessage target, String sourceTag, String destinationTag){
        Tag sourceTagValue = source.fetch(sourceTag);
        if(sourceTagValue != null){
            target.addTag(destinationTag,sourceTagValue.getValue());
        }
    }

    /**
     * Transfers the portion of the tag that reached the messageLimit
     * @param source
     * @param target
     * @param sourceTag
     * @param destinationTag
     * @param limit
     */
    protected void transfer(RawSwiftMessage source, RawSwiftMessage target, String sourceTag, String destinationTag) throws UnsupportedOperationException{
    	Tag sourceTagValue = source.getMessageBlock().getTagByName(sourceTag);
    	StringBuilder sb=new StringBuilder((sourceTagValue != null ? sourceTagValue.getValue() : ""));
    	if(sb.length() > messageLimit()){
    		source.update(sourceTag, sb.substring(0,messageLimit())); 
    		target.addTag(destinationTag,sb.substring(messageLimit(),sb.length()));
    	}else if(isLineLimitReached(sb.toString())){
    		Pattern pattern = Pattern.compile("(.$.?)",Pattern.MULTILINE|Pattern.DOTALL);
        	Matcher matcher = pattern.matcher(sb.toString());
        	String firstHalf="";
        	String secondHalf="";
        	int lineOffset = 1;
        	while(matcher.find()){
        		lineOffset++;
        		if(lineOffset > lineLimit()){
        			firstHalf = sb.toString().substring(0, matcher.end());
        			secondHalf = sb.toString().substring(matcher.end(), sb.toString().length());
        			break;
        		}
        	}
        	source.update(sourceTag, firstHalf); 
    		target.addTag(destinationTag,secondHalf);
    	}else{
    		throw new UnsupportedOperationException("Failed to transfer: No Condition matched.");
    	}
    }
    
    protected boolean isLineLimitReached(String input){
    	Pattern pattern = Pattern.compile("(.$)",Pattern.MULTILINE|Pattern.DOTALL);
    	Matcher matcher = pattern.matcher(input);    	
    	int lineCount = 0;
    	boolean result = false;
    	while(matcher.find()){
    		lineCount++;
    	}
    	if(lineCount > lineLimit()){
    		result = true;
    	}
    	return result;
    }
    
    protected boolean isMessageLimitReached(String input){
    	return input.length() > messageLimit();
    }
}