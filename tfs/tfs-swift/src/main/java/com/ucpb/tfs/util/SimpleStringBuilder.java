package com.ucpb.tfs.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ucpb.tfs.swift.message.writer.DefaultSwiftMessageWriter;

/**
 */
public class SimpleStringBuilder {

    private StringBuilder stringBuilder;

    public SimpleStringBuilder(){
        stringBuilder = new StringBuilder();
    }

    public SimpleStringBuilder(String string){
        stringBuilder = new StringBuilder(string != null ? string : "");
    }

    public SimpleStringBuilder append(String string){
        if(string != null){
            stringBuilder.append(string);
        }
        return this;
    }

    public void deleteCharAt(int index){
        stringBuilder.deleteCharAt(index);
    }

    public void setCharAt(int index, char ch){
        stringBuilder.setCharAt(index,ch);
    }

    public int charAt(int index){
        return stringBuilder.charAt(index);
    }

    public SimpleStringBuilder appendAndRestrict(int limit,String... stringArray){
        if(stringArray == null){
            return this;
        }
        for(String string : stringArray){
            if(string != null && !string.isEmpty()){
                StringBuilder temp = initialize(string);
                
                while(temp.length() > limit){
                    int splitLimit = getSplitLimit(temp,limit);
                    if(splitLimit == 0){
                    	stringBuilder.append(temp.toString()).
                    	append(DefaultSwiftMessageWriter.SWIFT_NEWLINE);
                    	break;
                    }else{
                    	stringBuilder.append(temp.substring(0, splitLimit)).
                    	append(DefaultSwiftMessageWriter.SWIFT_NEWLINE);
                    }
                    temp.delete(0,splitLimit);
                }

                if(temp.length() <= limit){
                    stringBuilder.append(temp.toString());
                }
                stringBuilder.append(DefaultSwiftMessageWriter.SWIFT_NEWLINE);
            }
        }
        trimTrailingWhitespace();
        return this;
    }
    
    public SimpleStringBuilder appendAndRestrictWithoutInitialize(int limit,String... stringArray){
        if(stringArray == null){
            return this;
        }
        
        List<String> splittedStrings = new ArrayList<String>();
        for(String string : stringArray){
        	if(string != null && !string.isEmpty()){
        		splittedStrings.addAll(splitEachNewLine(string));  
        	}
        }
        
        for(String string: splittedStrings){
            if(string != null && !string.isEmpty()){
                StringBuilder temp = new StringBuilder(string);
                while(temp.length() > limit){
                    int splitLimit = getSplitLimit(temp,limit);
                    if(splitLimit == 0){
                    	stringBuilder.append(temp.toString()).
                    	append(DefaultSwiftMessageWriter.SWIFT_NEWLINE);
                    	break;
                    }else{
                    	stringBuilder.append(temp.substring(0, splitLimit)).
                    	append(DefaultSwiftMessageWriter.SWIFT_NEWLINE);
                    }
                    temp.delete(0,splitLimit);
                }

                if(temp.length() <= limit){
                    stringBuilder.append(temp.toString());
                }
                stringBuilder.append(DefaultSwiftMessageWriter.SWIFT_NEWLINE);
            }
        }
        
        removeWhiteLines();
        trimTrailingWhitespace();
        return this;
    }

    public SimpleStringBuilder append(char c){
        stringBuilder.append(c);
        return this;
    }

    public SimpleStringBuilder replace(int start, int end, String string){
        stringBuilder.replace(start,end,string);
        return this;
    }

    public String toString(){
        return stringBuilder.toString();
    }

    public int length(){
        return stringBuilder.length();
    }

    private int getSplitLimit(StringBuilder sourceString, int limit){
        while(limit > 0 && !Character.isWhitespace(sourceString.charAt(limit-1))){
            limit--;
        }
        return limit;
    }

    public void trimTrailingWhitespace(){
        while (stringBuilder.length() > 0 && Character.isWhitespace(stringBuilder.charAt(stringBuilder.length() - 1))) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
    }

    public boolean hasText(){
        return stringBuilder.length() > 0;
    }

    public SimpleStringBuilder appendWithNewLine(String input){
        if(hasText()){
            append(DefaultSwiftMessageWriter.SWIFT_NEWLINE);
        }
        return append(input);
    }

    private StringBuilder initialize(String input){
        //this is a patch to remove "\n" strings being added to the address locations by the front end java script.
        StringBuilder sb = new StringBuilder(input.replaceAll("\\r"," ").replaceAll("\\n"," ").replaceAll("\\\\r"," ").replaceAll("\\\\n"," "));
        int ctr = sb.length() - 1;
        while(ctr >= 0){
            if(Character.isWhitespace(sb.charAt(ctr))){
                sb.setCharAt(ctr,' ');
                if((ctr -1 >= 0) && Character.isWhitespace(sb.charAt(ctr-1))){
                    sb.deleteCharAt(ctr -1);
                }
            }
            ctr--;
        }
        return sb;
    }
    
    private List<String> splitEachNewLine(String s){
    	String[] temp=s.split("\\n|\\r\\n|\\r|\\\\n|\\\\r");
    	if(temp != null){
    		return new ArrayList<String>(Arrays.asList(temp));    		    		
    	}else{
    		return null;
    	}
    }
    
    private void removeWhiteLines(){    	
    	List<String> temp=splitEachNewLine(stringBuilder.toString());
    	StringBuilder result=new StringBuilder();
    	for(String s:temp){
    		if(!s.isEmpty()){
    			result.append(s).append(DefaultSwiftMessageWriter.SWIFT_NEWLINE);
    		}
    	}
    	this.stringBuilder=result;
    }
}