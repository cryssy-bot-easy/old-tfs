package com.ucpb.tfs.swift.message;

import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 */
public final class MessageBlock {

    private static final String REFERENCE_TAG_NAME = "20";
    private static final String RELATED_REFERENCE_TAG_NAME = "21";
    private static final String SEQUENCE_NUMBER_TAG = "27";

    private List<Tag> tags = new ArrayList<Tag>();

    public MessageBlock(List<Tag> tags){
        this.tags = tags;
    }

    public MessageBlock(){

    }

    public void clear(){
        tags.clear();
    }

    public String getReference(){
        Tag referenceTag = getTagByName(REFERENCE_TAG_NAME);
        return referenceTag != null ? referenceTag.getValue() : null;
    }

    public String getRelatedReference(){
        Tag relatedReferenceTag = getTagByName(RELATED_REFERENCE_TAG_NAME);
        return relatedReferenceTag != null ? relatedReferenceTag.getValue() : null;
    }

    public Integer getSequenceNumber(){
        String sequence = getTagValue(SEQUENCE_NUMBER_TAG);
        if(!StringUtils.isEmpty(sequence)){
            return Integer.parseInt(sequence.split("/")[0]);
        }
        return null;
    }

    public Integer getSequenceTotal(){
        String sequence = getTagValue(SEQUENCE_NUMBER_TAG);
        if(!StringUtils.isEmpty(sequence)){
            return Integer.parseInt(sequence.split("/")[1]);
        }
        return null;
    }

    public String getTagValue(String tagName){
        Tag tag = getTagByName(tagName);
        return tag != null ? tag.getValue() : null;
    }

    public List<Tag> getTags(){
        return Collections.unmodifiableList(tags);
    }

    public void addTag(String tagName,String tagValue){
        tags.add(new Tag(tagName,tagValue));
    }

    public void addTag(String tagName,String tagValue,int position){
        tags.add(position,new Tag(tagName,tagValue));
    }

    public void remove(String tagName){
        tags.remove(new Tag(tagName,""));
    }

    public void addTag(Tag tag){
        tags.add(tag);
    }

    public void addTag(Tag tag,int position){
        tags.add(position,tag);
    }


    public void addTags(List<Tag> tags){
        tags.addAll(tags);
    }

    public Tag fetch(String tagName){
        for(int ctr = 0; ctr < tags.size(); ctr++){
            Tag tag = tags.get(ctr);
            if(tag.getTagName().equalsIgnoreCase(tagName)){
                return tags.remove(ctr);
            }

        }
        return null;
    }

    public Tag getTagByName(String tagName){
        for(Tag tag : tags){
            if(tag.getTagName().equalsIgnoreCase(tagName)){
                return new Tag(tag.getTagName(),tag.getValue());
            }
        }
        return null;
    }

    public void update(String tagName, String newValue){
        Tag tag = getTagByName(tagName);
        if(tag != null){
            int location = tags.indexOf(tag);
            tags.remove(tag);
            tag.setValue(newValue);
            tags.add(location,tag);
        }
    }

    public List<Tag> getTagsByName(String tagName){
        List<Tag> matchingTags = new ArrayList<Tag>();
        for(Tag tag : tags){
            if(tag.getTagName().equalsIgnoreCase(tagName)){
                matchingTags.add(tag);
            }
        }
        return matchingTags;
    }

    public int length(){
        StringBuilder sb = new StringBuilder();
        for(Tag tag : tags){
            sb.append(tag.getValue());
        }
        return sb.length();
    }



}
