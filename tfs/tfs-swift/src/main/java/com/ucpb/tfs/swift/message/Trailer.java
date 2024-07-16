package com.ucpb.tfs.swift.message;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 */
@XmlRootElement(name = "trailer",namespace = SwiftMessageSchemas.SWIFT_MESSAGE)
public class Trailer {

    private Map<String,String> trailerFields = new HashMap<String,String>();

    public void addField(String field, String value){
        trailerFields.put(field,value);
    }

    public String getFieldValue(String field){
        return trailerFields.get(field);
    }

    public boolean fieldExists(String field){
        return trailerFields.containsKey(field);
    }

    public Map<String,String> getFields(){
        return Collections.unmodifiableMap(trailerFields);
    }

}
