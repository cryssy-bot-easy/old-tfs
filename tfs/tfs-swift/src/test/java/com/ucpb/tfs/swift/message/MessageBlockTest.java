package com.ucpb.tfs.swift.message;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.*;

/**
 */
public class MessageBlockTest {

    private MessageBlock messageBlock;

    @Before
    public void setup(){
        messageBlock = new MessageBlock();
    }

    @Test
    public void addTagToMessageBlock(){
        assertTrue(messageBlock.getTags().isEmpty());
        messageBlock.addTag("tagname", "tagvalue");
        assertEquals(1, messageBlock.getTags().size());
    }

    @Test
    public void retrieveAddedTag(){
        assertTrue(messageBlock.getTags().isEmpty());
        messageBlock.addTag("21","tagvalue");
        messageBlock.addTag("32","tag3");
        assertEquals(2,messageBlock.getTags().size());
        assertNotNull(messageBlock.getTagByName("21"));
        assertEquals("TAGVALUE",messageBlock.getTagByName("21").getValue());
    }

    @Test
    public void retrieveTagValueOfExistingTag(){
        assertTrue(messageBlock.getTags().isEmpty());
        messageBlock.addTag("21", "tagvalue");
        assertEquals("TAGVALUE",messageBlock.getTagValue("21"));
    }

    @Test
    public void getNullOnExistingNonExistentTag(){
        assertTrue(messageBlock.getTags().isEmpty());
        assertNull(messageBlock.getTagByName("30"));
    }

    @Test
    public void retrieveTheFirstOccurrenceOfATag(){
        assertTrue(messageBlock.getTags().isEmpty());
        messageBlock.addTag("21","tagvalue");
        messageBlock.addTag("21","tag3");
        messageBlock.addTag("21","tag4");
        assertEquals(3,messageBlock.getTags().size());
        assertNotNull(messageBlock.getTagByName("21"));
        assertEquals("TAGVALUE",messageBlock.getTagByName("21").getValue());
    }

    @Test
    public void retrieveAllMatchingTags(){
        assertTrue(messageBlock.getTags().isEmpty());
        messageBlock.addTag("21","tagname");
        messageBlock.addTag("21","tag3");
        messageBlock.addTag("21","tag4");
        messageBlock.addTag("31","tag1");
        assertEquals(4,messageBlock.getTags().size());
        List<Tag> matchingTags = messageBlock.getTagsByName("21");
        assertEquals(3,matchingTags.size());
    }

    @Test
    public void removeExistingTagViaFetch(){
        assertTrue(messageBlock.getTags().isEmpty());
        messageBlock.addTag("21","tagname");

        assertEquals("TAGNAME",messageBlock.getTagByName("21").getValue());
        assertEquals("TAGNAME",messageBlock.fetch("21").getValue());
        assertEquals(null,messageBlock.getTagByName("21"));
    }

    @Test
    public void updateTagContents(){
        messageBlock.addTag("32","THIS");
        Tag tag = messageBlock.getTagByName("32");
        assertEquals("THIS",tag.getValue());
        messageBlock.update("32","NEWVALUE");
        assertEquals("NEWVALUE",messageBlock.getTagByName("32").getValue());
    }

    @Test
    public void updateOnlyTheFirstInstanceOfTheTagOccurence(){
        messageBlock.addTag("32","THIS");
        messageBlock.addTag("32","THAT");
        Tag tag = messageBlock.getTagByName("32");
        assertEquals("THIS",tag.getValue());
        messageBlock.update("32","NEWVALUE");
        List<Tag> tags = messageBlock.getTagsByName("32");
        assertEquals("NEWVALUE",tags.get(0).getValue());
        assertEquals("THAT",tags.get(1).getValue());

    }

}
