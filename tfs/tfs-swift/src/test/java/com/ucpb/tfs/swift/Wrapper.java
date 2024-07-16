
package com.ucpb.tfs.swift;

import java.util.Map;

/**
 */
public class Wrapper {

    private Map<String,String> details;

    public Wrapper(Map<String,String> details){
        this.details = details;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public void setDetails(Map<String, String> details) {
        this.details = details;
    }
}
