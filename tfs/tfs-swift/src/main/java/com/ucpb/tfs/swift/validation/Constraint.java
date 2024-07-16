package com.ucpb.tfs.swift.validation;

import java.util.HashSet;
import java.util.Set;

/**
 */
public class Constraint {

    private String name;

    private String pattern;

    private String type;

    private int minLength;

    private int maxLength;

    private Set<String> allowedValues;

    public Constraint(){

    }

    public Constraint(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public void setAllowedValue(String value){
        if(allowedValues == null){
            allowedValues = new HashSet<String>();
        }
        allowedValues.add(value);
    }

    public Set<String> getAllowedValues(){
        return allowedValues;
    }


}
