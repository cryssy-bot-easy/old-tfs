package com.ucpb.tfs.swift.message.field;

import com.ucpb.tfs.swift.message.Tag;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 */
public class TagTest {


    @Test
    public void changeLabelToUpperCase(){
        Tag tag = new Tag();
        tag.setTagName("ba");
        assertEquals("BA",tag.getTagName());
    }

    @Test
    public void changeValueToUpperCase(){
        Tag tag = new Tag();
        tag.setValue("aq");
        assertEquals("AQ",tag.getValue());
    }

    @Test
    public void retainNewLine(){
        Tag tag = new Tag("A","some\r\nDaY");
        assertEquals("SOME\r\nDAY",tag.getValue());
    }

    @Test
    public void removeTrailingWhitespace(){
        Tag tag = new Tag();
        tag.setTagName("A");
        tag.setValue("Hello World ");
        assertEquals("HELLO WORLD",tag.getValue());
    }

    @Test
    public void removeTrailingNewline(){
        Tag tag = new Tag();
        tag.setTagName("A");
        tag.setValue("Hello World\n");
        assertEquals("HELLO WORLD",tag.getValue());
    }

    @Ignore("Temporarily removed invalid character substitution")
    @Test
    public void substituteInvalidCharacters(){
        Tag tag = new Tag("<","<");
        Tag tag1 = new Tag("!","!");
        Tag tag2= new Tag("&","&");
        Tag tag3 = new Tag("|","|");
        Tag tag4 = new Tag("$","$");
        Tag tag5 = new Tag("*","*");
        Tag tag6 = new Tag(";",";");
        Tag tag7 = new Tag("^","^");
        Tag tag8 = new Tag("%","%");
        Tag tag9 = new Tag("_","_");
        Tag tag10 = new Tag(">",">");
        Tag tag11 = new Tag("`","`");
        Tag tag12 = new Tag("#","#");
        Tag tag13 = new Tag("@","@");
        Tag tag14 = new Tag("=","=");
        Tag tag15 = new Tag("\"","\"");
        Tag tag16 = new Tag("~","~");
        Tag tag17 = new Tag("[","[");
        Tag tag18 = new Tag("]","]");
        Tag tag19 = new Tag("{","{");
        Tag tag20 = new Tag("}","}");
        Tag tag21 = new Tag("\\","\\");

        assertEquals("??4C",tag.getValue());
        assertEquals("??4F",tag1.getValue());
        assertEquals("??50",tag2.getValue());
        assertEquals("??5A",tag3.getValue());
        assertEquals("??5B",tag4.getValue());
        assertEquals("??5C",tag5.getValue());
        assertEquals("??5E",tag6.getValue());
        assertEquals("??5F",tag7.getValue());
        assertEquals("??6C",tag8.getValue());
        assertEquals("??6D",tag9.getValue());
        assertEquals("??6E",tag10.getValue());
        assertEquals("??79",tag11.getValue());
        assertEquals("??7B",tag12.getValue());
        assertEquals("??7C",tag13.getValue());
        assertEquals("??7E",tag14.getValue());
        assertEquals("??7F",tag15.getValue());
        assertEquals("??A1",tag16.getValue());
        assertEquals("??AD",tag17.getValue());
        assertEquals("??BD",tag18.getValue());
        assertEquals("??C0",tag19.getValue());
        assertEquals("??D0",tag20.getValue());
        assertEquals("??E0",tag21.getValue());

    }



}
