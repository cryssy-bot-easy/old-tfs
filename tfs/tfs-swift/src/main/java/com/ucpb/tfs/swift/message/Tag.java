package com.ucpb.tfs.swift.message;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
@XmlRootElement(name = "tag", namespace = SwiftMessageSchemas.SWIFT_MESSAGE)
public class Tag {

    private static final String DELIMITER = "+";
    private static final String NEW_LINE = "\n";
    private String tagName;

    private String value;

    private static final Map<Character,String> INVALID_CHARACTERS = new HashMap<Character,String>();


    static {
        INVALID_CHARACTERS.put('<',"??4C");
        INVALID_CHARACTERS.put('!',"??4F");
        INVALID_CHARACTERS.put('&',"??50");
        INVALID_CHARACTERS.put('|',"??5A");
        INVALID_CHARACTERS.put('$',"??5B");
        INVALID_CHARACTERS.put('*',"??5C");
        INVALID_CHARACTERS.put(';',"??5E");
        INVALID_CHARACTERS.put('^',"??5F");
        INVALID_CHARACTERS.put('%',"??6C");
        INVALID_CHARACTERS.put('_',"??6D");
        INVALID_CHARACTERS.put('>',"??6E");
        INVALID_CHARACTERS.put('`',"??79");
        INVALID_CHARACTERS.put('#',"??7B");
        INVALID_CHARACTERS.put('@',"??7C");
        INVALID_CHARACTERS.put('=',"??7E");
        INVALID_CHARACTERS.put('"',"??7F");
        INVALID_CHARACTERS.put('~',"??A1");
        INVALID_CHARACTERS.put('[',"??AD");
        INVALID_CHARACTERS.put(']',"??BD");
        INVALID_CHARACTERS.put('{',"??C0");
        INVALID_CHARACTERS.put('}',"??D0");
        INVALID_CHARACTERS.put('\\',"??E0");
    }

    public Tag(){
        //default constructor
    }

    public Tag(String tagName, String value){
        this.tagName = tagName.toUpperCase();
        this.value = value != null ? StringUtils.trimTrailingWhitespace(value.toUpperCase()) : "";
//        this.value = removeInvalidCharacters(value.toUpperCase());
    }

    public Tag(String tagName, List<String> values){
        StringBuilder sb = new StringBuilder();
        for(String value : values){
            sb.append(DELIMITER).append(value).append(NEW_LINE);
        }
        this.tagName = tagName.toUpperCase();
        this.value = sb.toString();
    }


    @XmlElement
    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        Assert.notNull(tagName,"Tag name must not be null");
        this.tagName = tagName.toUpperCase();
    }

    @XmlElement
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value != null ? StringUtils.trimTrailingWhitespace(value.toUpperCase()) : "";
//        this.value = removeInvalidCharacters(value.toUpperCase());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag1 = (Tag) o;
        if (!tagName.equals(tag1.tagName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return tagName.hashCode();
    }

    private String removeInvalidCharacters(String source){
        StringBuilder s = new StringBuilder(source.length());
        char[] characters = source.toCharArray();
        for(int ctr = 0; ctr < characters.length; ctr++){
            if(ctr == characters.length-1 && (characters[ctr] == ' ' || characters[ctr] == '\n')){
                break;
            }
            if(INVALID_CHARACTERS.containsKey(characters[ctr])){
                s.append(INVALID_CHARACTERS.get(characters[ctr]));
            }else {
                s.append(characters[ctr]);
            }
        }

        while (s.length() > 0 && Character.isWhitespace(s.charAt(s.length() - 1))) {
            s.deleteCharAt(s.length() - 1);
        }

        return s.toString();
    }
}
