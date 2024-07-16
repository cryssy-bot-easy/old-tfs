package com.ucpb.tfs.swift.message;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 */
@XmlRootElement(name = "user_header", namespace = SwiftMessageSchemas.SWIFT_MESSAGE)
public class UserHeader {

    private List<UserTag> userTags = new ArrayList<UserTag>();

    public void addUserTag(String tag, String value){
        this.userTags.add(new UserTag(tag,value));
    }

    public List<UserTag> getUserTags() {
        return Collections.unmodifiableList(userTags);
    }

    /**
     * Returns the value of the FIRST instance of the tag
     * @param tagName
     * @return The value of the specified tag
     */
    public String getTagValue(String tagName){
        for(UserTag tag : userTags){
            if(tag.getTag().equals(tagName)){
                return tag.getValue();
            }
        }
        return null;
    }

    /**
     * Returns all values of that specified tag
     * @param tagName
     * @return a list of all tag values
     */
    public List<String> getTagValues(String tagName){
        List<String> tagValues = new ArrayList<String>();
        for(UserTag tag : userTags){
            if(tag.getTag().equals(tagName)){
                tagValues.add(tag.getValue());
            }
        }
        return tagValues;
    }

    public boolean isEmpty(){
        return userTags.isEmpty();
    }

}
