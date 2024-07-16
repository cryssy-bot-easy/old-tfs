package com.ucpb.tfs.swift.message.splitter;

import com.ucpb.tfs.swift.message.RawSwiftMessage;

import java.util.ArrayList;
import java.util.List;

public class MT700Splitter extends Splitter{

    @Override
    public List<RawSwiftMessage> split(RawSwiftMessage rawSwiftMessage) {
    	int actualCount = 1;
        List<RawSwiftMessage> result = new ArrayList<RawSwiftMessage>();
        List<RawSwiftMessage> messages = new ArrayList<RawSwiftMessage>();
        
        messages.add(evaluate(rawSwiftMessage, "45A", "45B"));
        messages.add(evaluate(rawSwiftMessage, "46A", "46B"));
        messages.add(evaluate(rawSwiftMessage, "47A", "47B"));

        rawSwiftMessage.update("27",getSequence(actualCount,getTotalCount()));
        
        for(RawSwiftMessage message: messages){
        	if(message != null){
        		actualCount++;
        		message.addTag("27",getSequence(actualCount,getTotalCount()),0);
        	}
        }
        
        messages.add(0, rawSwiftMessage);
        
        addIfNotNull(result,messages);
        return result;
    }

    @Override
    public int messageLimit(){
        return 6500;
    }

    @Override
    public int lineLimit(){
    	return 100;
    }
    
    /**
     * Check if the tag reached the limit
     * @param source
     * @param sourceTag
     * @param destinationTag
     * @return new swift message
     */
    private RawSwiftMessage evaluate(RawSwiftMessage source,String sourceTag,String destinationTag){
    	RawSwiftMessage destination = null;
        if(isLimitReached(source,sourceTag)){
        	try{
	        	destination = getInstance(source);
	            if(source.getMessageBlock().getTagByName("20") != null){
	            	destination.addTag("20",source.getMessageBlock().getTagByName("20").getValue());
	            }
            	transfer(source,destination,sourceTag,destinationTag);
            	appendTotalCount(1);
            }catch(UnsupportedOperationException uoe){
            	uoe.printStackTrace();
            }
         }
    	return destination;
    }
    
    
    private void addIfNotNull(List<RawSwiftMessage> destination, List<RawSwiftMessage> messages){
       for(RawSwiftMessage message : messages){
           if(message != null){
               destination.add(message);
           }
       }
    }

    private String getSequence(int sequence, int total){
        return sequence + "/" + total;
    }
}
