package com.ucpb.tfs.interfaces.gateway.parser;

import com.ucpb.tfs.interfaces.gateway.CasaResponse;
import org.springframework.util.Assert;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public abstract class ParsingStrategy {

    protected abstract CasaResponse mapToCasaResponse(String[] response);

    protected abstract String getFormat();

    public final CasaResponse parse(String response) throws ParseException {
        Assert.notNull(response, "Response to be parsed must not be null!");
        System.out.println("response from silverlake : " + response);
        System.out.println("getFormat() : " + getFormat());

        CasaResponse casaResponse = null;
        if(response.matches(getFormat())){

            Matcher matcher = Pattern.compile(getFormat()).matcher(response);
            if(matcher.find()){
                casaResponse = mapToCasaResponse(toStringArray(matcher));
            }
        }else{
            throw new ParseException("Response : " + response + "does not match defined pattern: " + getFormat());
        }
        return casaResponse;
    }

    private String[] toStringArray(Matcher matcher){
        String[] map = new String[matcher.groupCount()];
        for(int ctr = 0; ctr < matcher.groupCount(); ctr++){
            map[ctr] = matcher.group(ctr+1);
        }
        return map;
    }
}
