package com.ucpb.tfs.util;

import com.ucpb.tfs.swift.message.SwiftMessageSchemas;
import org.junit.Ignore;
import org.junit.Test;

import com.ucpb.tfs.swift.message.constants.ApplicableRules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 */
public class SwiftFieldsDeclarationTest {

    private XmlValidator xmlValidator = new XmlValidator("/swift/schemas/swift-fields.xsd");

    private static final String SWIFT_SCHEMA = "xmlns=\"" + SwiftMessageSchemas.SWIFT_FIELDS + "\"";

    @Test
    public void validField40A(){
    	List<String> errors = validate(wrapToField("field40A","'Valid12/?:()-.,+ \r\n "));
        assertTrue(errors.isEmpty());
    }

    @Test
    public void invalidLengthField40A(){
        List<String> errors = validate(wrapToField("field40A","invalid1Invalid2/-?:().,'+{} "));
        assertFalse(errors.isEmpty());
    }
//  [a-zA-Z0-9/\-?:().,'+{}\s]*
    @Test
    public void validField53A(){
        List<String> errors = validate(wrapToField("field53A","/36143038\n" +
                "CITIUS33"));
        assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField53A(){
        List<String> errors = validate(wrapToField("field53A",""));
        assertFalse(errors.isEmpty());
    }
     
    @Test
    public void validField53B(){
        List<String> errors = validate(wrapToField("field53B","/4116127003 CITIGROUP GLOBAL" +
                " MARKETS DEUTSCHLAND AG"));
        assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField53B(){
        List<String> errors = validate(wrapToField("field53B","/ABCDE /ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 " +
        		"/ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"));
        assertFalse(errors.isEmpty());
    }
    
    @Test
    public void validField53D(){
        List<String> errors = validate(wrapToField("field53D","/36143038\n" +
                "CITIUS33"));
        assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField53D(){
        List<String> errors = validate(wrapToField("field53D",""));
        assertFalse(errors.isEmpty());
    }
    
    @Test
    public void validField54A(){
        List<String> errors = validate(wrapToField("field54A","/36143038\n" +
                "CITIUS33"));
        assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField54A(){
        List<String> errors = validate(wrapToField("field54A",""));
        assertFalse(errors.isEmpty());
    }
     
    @Test
    public void validField54B(){
        List<String> errors = validate(wrapToField("field54B","/36143038\n" +
                "CITIUS33"));
        assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField54B(){
        List<String> errors = validate(wrapToField("field54B","/ABCDE /ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 " +
        		"/ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"));
        assertFalse(errors.isEmpty());
    }
    
    @Test
    public void validField54D(){
        List<String> errors = validate(wrapToField("field54D","/36143038\n" +
                "CITIUS33"));
        assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField54D(){
        List<String> errors = validate(wrapToField("field54D",""));
        assertFalse(errors.isEmpty());
    }
    
    @Test
    public void validField27(){
        List<String> errors = validate(wrapToField("field27","2/2"));
        assertTrue(errors.isEmpty());
    }

    @Test
    public void invalidField27(){
        List<String> errors = validate(wrapToField("field27","A/A"));
        assertFalse(errors.isEmpty());
    }

    @Test
    public void validField20(){
         List<String> errors = validate(wrapToField("field20","vAl/-?:().,'+ "));
         assertTrue(errors.isEmpty());
    }

    @Test
    public void invalidLengthField20(){
         List<String> errors = validate(wrapToField("field20","ThIs LengTH iS InVaLiD/-?:().,'+{} "));
         assertFalse(errors.isEmpty());
    } 
    
    @Test
    public void validField23(){
         List<String> errors = validate(wrapToField("field23","vAl/-?:().,'+ "));
         assertTrue(errors.isEmpty());
    }

    @Test
    public void invalidLengthField23(){
         List<String> errors = validate(wrapToField("field23","ThIs LengTH iS InVaLiD/-?:().,'+{} "));
         assertFalse(errors.isEmpty());
    } 

    @Test
    public void validField31C(){
         List<String> errors = validate(wrapToField("field31C","123456"));
         assertTrue(errors.isEmpty());
    }

    @Test
    public void invalidMinLengthField31C(){
         List<String> errors = validate(wrapToField("field31C","123"));
         assertFalse(errors.isEmpty());
    } 

    @Test
    public void invalidMaxLengthField31C(){
    	List<String> errors = validate(wrapToField("field31C","03132013"));
    	assertFalse(errors.isEmpty());
    } 

    @Test
    public void invalidFormatField31C(){
    	List<String> errors = validate(wrapToField("field31C","A3B32C13"));
    	assertFalse(errors.isEmpty());
    } 
    
    @Test
    public void validField40EWith35X(){
        List<String> errors = validate(wrapToField("field40E","1ThisFieldiSValid/-?:().,'+T/AbCa567890123456/-?:().,'+12ERTCY"));
        assertTrue(errors.isEmpty());
    }

    @Test
    public void validField40EWithout35X(){
    	List<String> errors = validate(wrapToField("field40E","1234567890ABCDEFRTYUasdeqwrewe"));
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void invalidField40EWithoutSlash(){
        List<String> errors = validate(wrapToField("field40E","1ThisFieldiSValid/-?:().,'+{}T AbCa567890123456/-?:().,'+{}12ERTCY"));
        assertFalse(errors.isEmpty());
    }

    @Test
    public void invalidField40E(){
    	List<String> errors = validate(wrapToField("field40E",""));
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void validField31D(){
         List<String> errors = validate(wrapToField("field31D","061212THEQUICKBROWNFOXJUMPSOVERTHEL"));
         assertTrue(errors.isEmpty());
    }

    @Test
    public void invalidField31D(){
         List<String> errors = validate(wrapToField("field31D","03132013THEQUICKBROWNFOXJUMPSOVERTHELAZYDOG"));
         assertFalse(errors.isEmpty());
    }

    @Test
    public void invalidField31DEmpty(){
    	List<String> errors = validate(wrapToField("field31D",""));
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void validField51AFull(){
    	List<String> errors = xmlValidator.validate(wrapToField("field51A","/A/1234567890123456789012345678901324\nABCDAB1A12B"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void validField51AWithout1a(){
    	List<String> errors = xmlValidator.validate(wrapToField("field51A","/ABCDEFGHIJKLMNOPQRSTWXYZ123\nABCDAB1A12B"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void validField51AWithout3c(){
    	List<String> errors = xmlValidator.validate(wrapToField("field51A","/ABCDEFGHIJKLMNOPQRSTWXYZ123\nABCDAB1A"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void validField51AWithout34X(){
    	List<String> errors = xmlValidator.validate(wrapToField("field51A","/AABCDAB1A12B"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void invalidField51AWithoutNewLine(){
    	List<String> errors = xmlValidator.validate(wrapToField("field51A","/ABCDEFGHIJKLMNOPQRSTWXYZ123ABCDAB1A12B"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void invalidField51AWithoutRequiredFields(){
    	List<String> errors = xmlValidator.validate(wrapToField("field51A","/ABCDEFGHIJKLMNOPQRSTWXYZ123\n12B"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void nullField51A(){
    	List<String> errors = xmlValidator.validate(wrapToField("field51A",""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void validField51DFull(){
    	List<String> errors = xmlValidator.validate(wrapToField("field51D","/B/13123123ASDFAASDADASER453451231\n"+
    				"12345678901234567890123456789012345\n"+
    				"12345678901234567890123456789012345\n"+
    				"12345678901234567890123456789012345\n"+
    				"12345678901234567890123456789012345\n"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void validField51DWithout1a(){
    	List<String> errors = xmlValidator.validate(wrapToField("field51D","/13123123ASDFAASDADASER453451231\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void validField51DWithout34x(){
    	List<String> errors = xmlValidator.validate(wrapToField("field51D","/A\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void invalidField51D(){
    	List<String> errors = xmlValidator.validate(wrapToField("field51D","/A/1234567890123456789012345678901234\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\nX"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void invalidField51DNull(){
    	List<String> errors = xmlValidator.validate(wrapToField("field51D",""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void errorOnFormatField51D(){
    	List<String> errors = xmlValidator.validate(wrapToField("field51D","/\\/l4jl23j4lJASLFJLAKSJL3K4J5LJLKAJFLASJKLFKJ34L5JLAJDFL345JLAKJFLJ"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void validField50(){
    	List<String> errors = xmlValidator.validate(wrapToField("field50","1234567890123456789012345678901234\n"+
    			"1234567890123456789012345678901234\n"+
    			"1234567890123456789012345678901234\n"+
    			"1234567890123456789012345678901234\n"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidLengthField50(){
    	List<String> errors = xmlValidator.validate(wrapToField("field50","12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void invalidField50Null(){
    	List<String> errors = xmlValidator.validate(wrapToField("field50",""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void validField50AFull(){
    	List<String> errors = xmlValidator.validate(wrapToField("field50A","/234567890123456789012345678901234\n"+
    			"ABCDDC121AC"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void validField50AWithout34X(){
    	List<String> errors = xmlValidator.validate(wrapToField("field50A",""+
    			"ABCDDC121AC"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void validField50AWithout3c(){
    	List<String> errors = xmlValidator.validate(wrapToField("field50A","/234567890123456789012345678901234\n"+
    			"ABCDDC12"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void invalidLengthField50A(){
    	List<String> errors = xmlValidator.validate(wrapToField("field50A","/12345678901234567890123456789012345123\n"+
    			"ABCDDC12"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void invalidField50ANull(){
    	List<String> errors = xmlValidator.validate(wrapToField("field50A",""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    
    @Test
    public void validField59(){
    	List<String> errors = xmlValidator.validate(wrapToField("field59","LG LTD. \r\n" +
    	"LG GWANGHWAMUN BLDG., 92, SINMUNNO \r\n"+
		"5-GA, JONGNO-GU, SEOUL, 110-783, \r\n"+
		"SOUTH KOREA TEL: (82 2) 6924 3916 \r\n"+
		"FAX : (82 2) 6924 3064"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void validField59Without34X(){
    	List<String> errors = xmlValidator.validate(wrapToField("field59",""+
    			"/123456789012345678901234567890123\n"+
    			"/123456789012345678901234567890123\n"+
    			"/123456789012345678901234567890123\n"+
    			"/123456789012345678901234567890123"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void invalidLengthField59(){
    	List<String> errors = xmlValidator.validate(wrapToField("field59","/123456789012345678901234567890123456789\n"+
    			"/123456789012345678901234567890123456789\n"+
    			"/123456789012345678901234567890123456798\n"+
    			"/123456789012345678901234567890123456789\n"+
    			"/123456789012345678901234567890123456789"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void invalidField59Null(){
    	List<String> errors = xmlValidator.validate(wrapToField("field59",""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void validField59AFull(){
    	List<String> errors = xmlValidator.validate(wrapToField("field59A","/234567890123456789012345678901234\n"+
    			"ABCDDC121AC"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void validField59AWithout34X(){
    	List<String> errors = xmlValidator.validate(wrapToField("field59A",""+
    			"ABCDDC121AC"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void validField59AWithout3c(){
    	List<String> errors = xmlValidator.validate(wrapToField("field59A","/234567890123456789012345678901234\n"+
    			"ABCDDC12"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    
    @Test
    public void invalidLengthField59A(){
    	List<String> errors = xmlValidator.validate(wrapToField("field59A","/12345678901234567890123456789012345123\n"+
    			"ABCDDC12"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void invalidField59ANull(){
    	List<String> errors = xmlValidator.validate(wrapToField("field59A",""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void noErrorField32B(){
    	List<String> errors = xmlValidator.validate(wrapToField("field32B","PHP123456789012,55"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void validField32BWithNoDecimal(){
        List<String> errors = xmlValidator.validate(wrapToField("field32B","PHP123456789012,"));
        assertTrue(errors.isEmpty());
    }

    @Test
    public void errorOnFormatField32B(){
    	List<String> errors = xmlValidator.validate(wrapToField("field32B","AB123456789012345.555"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void noErrorField33A(){
    	List<String> errors = xmlValidator.validate(wrapToField("field33A","012345ABC100,50"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField33A(){
    	List<String> errors = xmlValidator.validate(wrapToField("field33A","ABCDE123456789.0000"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void noErrorField33B(){
    	List<String> errors = xmlValidator.validate(wrapToField("field33B","ABC1234567890123,55"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField33B(){
    	List<String> errors = xmlValidator.validate(wrapToField("field33B","AB123456789012345,555"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void noErrorField34A(){
    	List<String> errors = xmlValidator.validate(wrapToField("field34A","012345ABC100,50"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField34A(){
    	List<String> errors = xmlValidator.validate(wrapToField("field34A","ABCDE123456789,00"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void noErrorField34B(){
    	List<String> errors = xmlValidator.validate(wrapToField("field34B","ABC1234567890123,55"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField34B(){
    	List<String> errors = xmlValidator.validate(wrapToField("field34B","AB123456789012345.555"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void noErrorField39A(){
    	List<String> errors = xmlValidator.validate(wrapToField("field39A","1/02"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void errorOnFormatField39A(){
    	List<String> errors = xmlValidator.validate(wrapToField("field39A","123/12.2"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void noErrorField39B(){
    	List<String> errors = xmlValidator.validate(wrapToField("field39B","345ASDFBC/:-?"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void errorOnFormatField39B(){
    	List<String> errors = xmlValidator.validate(wrapToField("field39B","!23452ASDBC{}"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void validField39C(){
    	List<String> errors = xmlValidator.validate(wrapToField("field39C","1234567890123456798012345678901324\n"+
    			"1234567890123456798012345678901324\n"+
    			"1234567890123456798012345678901324\n"+
    			"1234567890123456798012345678901324\n"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidLengthField39C(){
    	List<String> errors = xmlValidator.validate(wrapToField("field39C","12345678901234567980123456789013245\n"+
    			"12345678901234567980123456789013245\n"+
    			"12345678901234567980123456789013245\n"+
    			"12345678901234567980123456789013245\n"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void invalidField39CNull(){
    	List<String> errors = xmlValidator.validate(wrapToField("field39C",""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void validField41A(){
    	List<String> errors = xmlValidator.validate(wrapToField("field41A","ASDQWE1CA43"+
    			"12345678901234"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField41A(){
    	List<String> errors = xmlValidator.validate(wrapToField("field41A","AS3DQWE12A43"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void validField41D(){
    	List<String> errors = xmlValidator.validate(wrapToField("field41D","1234567890123456798012345678901324\n"+
    			"1234567890123456798012345678901324\n"+
    			"1234567890123456798012345678901324\n"+
    			"1234567890123456798012345678901324\n"+
    			"12345678901234"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidLengthField41D(){
    	List<String> errors = xmlValidator.validate(wrapToField("field39C","1234567890123456798012345678901324\n"+
    			"1234567890123456798012345678901324\n"+
    			"1234567890123456798012345678901324\n"+
    			"1234567890123456798012345678901324\n"+
    			"123456789012345"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void noErrorField42C(){
    	List<String> errors = xmlValidator.validate(wrapToField("field42C","123456789012345678901234567890/:-+{} "));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void errorOnFormatField42C(){
    	List<String> errors = xmlValidator.validate(wrapToField("field42C","!23452ASDBC{}12354ASDFDFASSDFA"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void noErrorField42A(){
    	List<String> errors = xmlValidator.validate(wrapToField("field42A","/SASDFGHJKLQWERTYUIOPASD/GHKJOTPYKRJWWWWWW114A3"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void errorOnFormatField42A(){
    	List<String> errors = xmlValidator.validate(wrapToField("field42A","!23452ASDBC{}12354ASDFDFASSDFA "));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void validField42DFull(){
    	List<String> errors = xmlValidator.validate(wrapToField("field42D","/A/123456789012345678901234567890123\n"+
    			"1234567890123456798012345678901324\n"+
    			"1234567890123456798012345678901324\n"+
    			"1234567890123456798012345678901324\n"+
    			"1234567890123456798012345678901324\n"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void validField42DWithout1a(){
    	List<String> errors = xmlValidator.validate(wrapToField("field42D","/123456789012345678901234567890123\n"+
    			"1234567890123456798012345678901324\n"+
    			"1234567890123456798012345678901324\n"+
    			"1234567890123456798012345678901324\n"+
    			"1234567890123456798012345678901324\n"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void validField42DWithout34x(){
    	List<String> errors = xmlValidator.validate(wrapToField("field42D","/A"+
    			"1234567890123456798012345678901324\n"+
    			"1234567890123456798012345678901324\n"+
    			"1234567890123456798012345678901324\n"+
    			"1234567890123456798012345678901324\n"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidLengthField42D(){
    	List<String> errors = xmlValidator.validate(wrapToField("field42D","/A/123456789012345678901234567890123\n"+
    			"1234567890123456798012345678901324\n"+
    			"1234567890123456798012345678901324\n"+
    			"1234567890123456798012345678901324\n"+
    			"1234567890123456798012345678901324\nX"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void invalidLengthField42DNull(){
    	List<String> errors = xmlValidator.validate(wrapToField("field42D",""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void validField42M(){
    	List<String> errors = xmlValidator.validate(wrapToField("field42M","1234567890123456789012345678901234\n"+
    			"1234567890123456789012345678901234\n"+
    			"1234567890123456789012345678901234\n"+
    			"1234567890123456789012345678901234\n"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidLengthField42M(){
    	List<String> errors = xmlValidator.validate(wrapToField("field42M","12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void invalidField42MNull(){
    	List<String> errors = xmlValidator.validate(wrapToField("field42M",""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void validField42P(){
    	List<String> errors = xmlValidator.validate(wrapToField("field42P","1234567890123456789012345678901234\n"+
    			"1234567890123456789012345678901234\n"+
    			"1234567890123456789012345678901234\n"+
    			"1234567890123456789012345678901234\n"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidLengthField42P(){
    	List<String> errors = xmlValidator.validate(wrapToField("field42P","12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void invalidField42PNull(){
    	List<String> errors = xmlValidator.validate(wrapToField("field42P",""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void noErrorField43P(){
    	List<String> errors = xmlValidator.validate(wrapToField("field43P","35X35X35X35X35X35X35X35X35X3535X35X"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void errorOnFormatField43P(){
    	List<String> errors = xmlValidator.validate(wrapToField("field43P","35X335X35X35X35X35X35X35X35X35X35X35X35X35X35X35X35X"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void noErrorField43T(){
    	List<String> errors = xmlValidator.validate(wrapToField("field43T","35X35X35X35X35X35X35X35X35X3535X35X"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void errorOnFormatField43T(){
    	List<String> errors = xmlValidator.validate(wrapToField("field43T","35X335X35X35X35X35X35X35X35X35X35X35X35X35X35X35X35X"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void noErrorField44A(){
    	List<String> errors = xmlValidator.validate(wrapToField("field44A","65X65X65X65X65X65X65X65X65X65X65X65X65X65X65X65X65X65X65X"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void errorOnFormatField44A(){
    	List<String> errors = xmlValidator.validate(wrapToField("field44A","65X65X65X65X65X65X65X65X65X65X65X65X65X65X65X65X65X65X65X65X65X65X65X65X65X"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
     @Test
    public void noErrorField44E() {
    	List<String> errors = xmlValidator.validate(wrapToField("field44E", "FX-RS-03 PORT OF LOADING FX-RS-03"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidMinLengthField44E() {
    	List<String> errors = xmlValidator.validate(wrapToField("field44E", ""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void invalidMaxLengthField44E() {
    	List<String> errors = xmlValidator.validate(wrapToField("field44E", "FX-RS-03 PORT OF LOADING FX-RS-03FX-RS-03 PORT OF LOADING FX-RS-03"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void noErrorField44F() {
    	List<String> errors = xmlValidator.validate(wrapToField("field44F", "FX-RS-03 PORT OF DISCHARGE FX-RS-03"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidMinLengthField44F() {
    	List<String> errors = xmlValidator.validate(wrapToField("field44F", ""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void invalidMaxLengthField44F() {
    	List<String> errors = xmlValidator.validate(wrapToField("field44F", "FX-RS-03 PORT OF LOADING FX-RS-03FX-RS-03 PORT OF LOADING FX-RS-03"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void noErrorField44B() {
    	List<String> errors = xmlValidator.validate(wrapToField("field44B", "FX-RS-03 PLACE OF FINAL DESTINATION FX-RS-03"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidMinLengthField44B() {
    	List<String> errors = xmlValidator.validate(wrapToField("field44B", ""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void invalidMaxLengthField44B() {
    	List<String> errors = xmlValidator.validate(wrapToField("field44B", "FX-RS-03 PORT OF LOADING FX-RS-03FX-RS-03 PORT OF LOADING FX-RS-03"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void noErrorField44C() {
    	List<String> errors = xmlValidator.validate(wrapToField("field44C", "121106"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField44C() {
    	List<String> errors = xmlValidator.validate(wrapToField("field44C", "abcde12345"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void noErrorField44D() {
    	List<String> errors = xmlValidator.validate(wrapToField("field44D", "azAZ09/-?:().,'+{}  \n 0123456789"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField44D() {
    	List<String> errors = xmlValidator.validate(wrapToField("field44D", ""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void noErrorField45A() {
    	List<String> errors = xmlValidator.validate(wrapToField("field45A", "CANDIES" +
    			"\nCANDIES" +
    			"\nPHILIPPINE STANDARD COMMODITY CLASSIFICATION CODE :783.11-03\nFOB"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField45A() {
    	List<String> errors = xmlValidator.validate(wrapToField("field45A", ""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void noErrorField45B() {
    	List<String> errors = xmlValidator.validate(wrapToField("field45B", "Description of Goods and/or Services"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField45B() {
    	List<String> errors = xmlValidator.validate(wrapToField("field45B", ""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void noErrorField46A() {
    	List<String> errors = xmlValidator.validate(wrapToField("field46A", "SIGNED COMMERCIAL INVOICE IN  TRIPLICATE" +
    			"\nPACKING LIST" +
    			"\nONE FULL SET OF AT LEAST THREE ORIGINAL CLEAN 'ON BOARD' OCEAN BILLS OF LADING IN NEGOTIABLE AND TRANSFERABLE FORM AND ONE NON-NEGOTIABLE COPY ISSUED TO THE ORDER OF UNITED COCONUT PLANTERS BANK MARKED FREIGHT PREPAID NOTIFY APPLICANT"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField46A() {
    	List<String> errors = xmlValidator.validate(wrapToField("field46A", ""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void noErrorField46B() {
    	List<String> errors = xmlValidator.validate(wrapToField("field46B", "DOCUMENTS REQUIRED"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField46B() {
    	List<String> errors = xmlValidator.validate(wrapToField("field46B", ""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void noErrorField47A() {
    	List<String> errors = xmlValidator.validate(wrapToField("field47A", "OCEAN BILL OF LADING MUST BE DATED WITHIN THE VALIDITY PERIOD OF THIS CREDIT" +
    			"\nALL DOCUMENTS MUST INDICATE COMMODITY CLASSIFICATION CODE AND LC NUMBER AS INDICATED ABOVE." +
    			"\nBL DATED PRIOR TO ISSUANCE OF THIS CREDIT NOT ALLOWED.\nA FEE OF USD  20.00 (OR EQUIVALENT) WILL BE CHARGED TO THE BENEFICIARY IF DOCUMENTS CONTAINING DISCREPANCIES ARE PRESENTED FOR PAYMENT/REIMBURSEMENT UNDER THIS LC. THIS FEE WILL BE CHARGED FOR EACH SET OF DISCREPANT DOCUMENTS PRESENTED WHICH REQUIRE OUR OBTAINING ACCEPTANCE FROM OUR CUSTOMER." +
    			"\nNEGOTIATING BANK MUST PRESENT ALL DOCS AND REIMB CLAIMS UNDER THIS CREDIT TO THE CONF. BANK.  BANK OF CHINA WHICH HOLDS SPECIAL PAYMENT AND REIMBURSEMENT INSTRUCTIONS."));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
   
    @Test
    public void invalidField47A() {
    	List<String> errors = xmlValidator.validate(wrapToField("field47A", ""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void noErrorField47B() {
    	List<String> errors = xmlValidator.validate(wrapToField("field47B", "ADDITIONAL CONDITIONS"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField47B() {
    	List<String> errors = xmlValidator.validate(wrapToField("field47B", ""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void noErrorField71B() {
    	List<String> errors = xmlValidator.validate(wrapToField("field71B", "ALL CHARGES OUTSIDE THE PHILIPPINES ARE FOR THE ACCOUNT OF BENEFICIARY INCLUDING REIMBURSING CHARGES"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField71B() {
    	List<String> errors = xmlValidator.validate(wrapToField("field71B", ""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void noErrorField48() {
    	List<String> errors = xmlValidator.validate(wrapToField("field48", "PERIOD FOR PRESENTATION"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField48() {
    	List<String> errors = xmlValidator.validate(wrapToField("field48", ""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void noErrorField49() {
    	List<String> errors = xmlValidator.validate(wrapToField("field49", "CONFIRM"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidMaxLengthField49() {
    	List<String> errors = xmlValidator.validate(wrapToField("field49", "CONFIRM!"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void invalidMinLengthField49() {
    	List<String> errors = xmlValidator.validate(wrapToField("field49", ""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void validField52AFull() {
    	List<String> errors = xmlValidator.validate(wrapToField("field52A", "/A/123456789012345678901234567890123\n"+
    			"ABCDEF12B13"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void validField52AWithout1a() {
    	List<String> errors = xmlValidator.validate(wrapToField("field52A", "/123456789012345678901234567890123\n"+
    			"ABCDEF12B13"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void validField52AWithout34X() {
    	List<String> errors = xmlValidator.validate(wrapToField("field52A", "/A"+
    			"ABCDEF12B13"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void validField52AWithout3C() {
    	List<String> errors = xmlValidator.validate(wrapToField("field52A", "/A/123456789012345678901234567890123\n"+
    			"ABCDEF12"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void invalidLengthField52A() {
    	List<String> errors = xmlValidator.validate(wrapToField("field52A", "/A/123456789012345678901234567890123\n"+
    			"ABCDEF12B123"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void invalidField52ANull() {
    	List<String> errors = xmlValidator.validate(wrapToField("field52A", ""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void validField52DFull(){
    	List<String> errors = xmlValidator.validate(wrapToField("field52D","/B/13123123ASDFAASDADASER453451231\n"+
    				"12345678901234567890123456789012345\n"+
    				"12345678901234567890123456789012345\n"+
    				"12345678901234567890123456789012345\n"+
    				"12345678901234567890123456789012345\n"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void validField52DWithout1a(){
    	List<String> errors = xmlValidator.validate(wrapToField("field52D","/13123123ASDFAASDADASER453451231\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void validField52DWithout34x(){
    	List<String> errors = xmlValidator.validate(wrapToField("field52D","/A\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void invalidField52D(){
    	List<String> errors = xmlValidator.validate(wrapToField("field52D","/A/234567890123456789012345678901234\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\nMAXLENGTH"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void invalidField52DNull(){
    	List<String> errors = xmlValidator.validate(wrapToField("field52D",""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void errorOnFormatField52D(){
    	List<String> errors = xmlValidator.validate(wrapToField("field52D","/\\/l4jl23j4lJASLFJLAKSJL3K4J5LJLKAJFLASJKLFKJ34L5JLAJDFL345JLAKJFLJ"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void noErrorField57A() {
    	List<String> errors = xmlValidator.validate(wrapToField("field57A", "/A/ABCDE ABCDEF12345"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField57A() {
    	List<String> errors = xmlValidator.validate(wrapToField("field57A", "/1/Abc ABCDE12345"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void noErrorField57B() {
    	List<String> errors = xmlValidator.validate(wrapToField("field57B", "/A/ABCDE ABC123"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField57B() {
    	List<String> errors = xmlValidator.validate(wrapToField("field57B", "/AAAAAaaaaa11111 /BBBBBbbbbb22222 /CCCCCccccc33333 /DDDDDddddd44444 /EEEEEeeeee55555"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void noErrorField57C() {
    	List<String> errors = xmlValidator.validate(wrapToField("field57C", "/1234567890123456789012345678901234"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField57C() {
    	List<String> errors = xmlValidator.validate(wrapToField("field57C", "/AAAAAaaaaa11111 /BBBBBbbbbb22222 /CCCCCccccc33333 /DDDDDddddd44444 /EEEEEeeeee55555"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void noErrorField57D() {
    	List<String> errors = xmlValidator.validate(wrapToField("field57D", "/A/ABCDE ABC123"));
    	printErrors(errors);
        assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField57D() {
    	List<String> errors = xmlValidator.validate(wrapToField("field57D", ""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
      
    @Test
    public void noErrorField78() {
    	List<String> errors = xmlValidator.validate(wrapToField("field78",
    			"+UPON RECEIPT OF COMPLYING DOCUMENTS, WE WILL REMIT PAYMENT\r\n"+ 
    			"ACCORDING TO YOUR INSTRUCTIONS.\r\n"+
    			"+NEGOTIATING BANK MUST FORWARD ALL DOCUMENTS NEGOTIATED UNDER\r\n"+  
    			"THIS CREDIT TO UNITED COCONUT PLANTERS BANK - TRADE SERVICES\r\n"+ 
    			"DEPARTMENT AT UCPB BLDG., MAKATI AVENUE, MAKATI CITY IN ONE LOT\r\n"+ 
    			"VIA COURIER.\r\n"+
    			"+NEGOTIATING BANK MUST ADVISE US OF NEGOTIATION DETAILS BY\r\n"+ 
    			"TESTED CABLE AND ANY ADDITIONAL TRANSIT INTEREST THAT MAY ARISE\r\n"+ 
    			"FOR NON-COMPLIANCE SHALL BE FOR THE ACCOUNT OF NEGOTIATING BANK.\r\n"+
    			"+CONFIRMING BANK MUST FORWARD ALL DOCUMENTS NEGOTIATED UNDER\r\n"+  
    			"THIS CREDIT TO UNITED COCONUT PLANTERS BANK - TRADE SERVICES\r\n"+ 
    			"DEPARTMENT AT UCPB BLDG., MAKATI AVENUE, MAKATI CITY IN ONE LOT\r\n"+ 
    			"VIA COURIER."));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField78() {
    	List<String> errors = xmlValidator.validate(wrapToField("field78", ""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void noErrorField72() {
    	List<String> errors = xmlValidator.validate(wrapToField("field72", "FX-RS-03 SENDER TO RECEIVER"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void noErrorField72ForMultiLine() {
        List<String> errors = xmlValidator.validate(wrapToField("field72", "FX-RS-03 SENDER TO RECEIVER\n" +
                "LINE 2 IS HERE\n" +
                "LINE 3 IS HERE\n" +
                "LINE 4 IS HERE\n" +
                "LINE 5 IS HERE\n"));
        printErrors(errors);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void failIfOneLineExceedsCharacterLimit() {
        List<String> errors = xmlValidator.validate(wrapToField("field72", "FX-RS-03 SENDER TO RECEIVER\n" +
                "THIS LINE EXCEEDS THIRTY FIVE CHARACTERS\n" +
                "LINE 3 IS HERE\n" +
                "LINE 4 IS HERE\n" +
                "LINE 5 IS HERE\n"));
        printErrors(errors);
        assertFalse(errors.isEmpty());
    }

    @Test
    public void failIfExceedingSixLines() {
        List<String> errors = xmlValidator.validate(wrapToField("field72", "FX-RS-03 SENDER TO RECEIVER\n" +
                "LINE 2 IS HERE\n" +
                " LINE 3 IS HERE\n" +
                " LINE 4 IS HERE\n" +
                " LINE 5 IS HERE\n" +
                " LINE 6 IS HERE\n" +
                "LINE 7 is here"));
        printErrors(errors);
        assertFalse(errors.isEmpty());
    }

    @Test
    public void passSingleLineStructuredNarrative() {
        List<String> errors = xmlValidator.validate(wrapToField("structured-narrative", "/CODE/NARRATIVE"));
        printErrors(errors);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void passMultiLineStructuredNarrative() {
        List<String> errors = xmlValidator.validate(wrapToField("structured-narrative", "/CODE/NARRATIVE\n//THISIS A CONTINUATION OF THE NARR"));
        printErrors(errors);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void failNarrativeBecauseOneLineExceedsLimit() {
        List<String> errors = xmlValidator.validate(wrapToField("structured-narrative", "/CODE/NARRATIVE\n//THISIS A CONTINUATION OF THE NAR\n//THISIS A CONTINUATION OF THE NARRATIIIIIIIIVE"));
        printErrors(errors);
        assertFalse(errors.isEmpty());
    }

    @Test
    public void passSixLineNarrative(){
        List<String> errors = xmlValidator.validate(wrapToField("structured-narrative", "/CODE/NARRATIVE\n//THISIS A CONTINUATION OF THE NAR\n" +
                "//THISIS A CONTINUATION OF THE NAR\n//THISIS A CONTINUATION OF THE NAR\n//THISIS A CONTINUATION OF THE NAR\n//THISIS A CONTINUATION OF THE NAR"));
        printErrors(errors);
        assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField72() {
    	List<String> errors = xmlValidator.validate(wrapToField("field72", ""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }  
    
     @Test
    public void noErrorField30() {
    	List<String> errors = xmlValidator.validate(wrapToField("field30", "061012"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void errorOnFormatField30() {
    	List<String> errors = xmlValidator.validate(wrapToField("field30", "012120145"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }   
    
    @Test
    public void noErrorField25() {
    	List<String> errors = xmlValidator.validate(wrapToField("field25", "ABCDE abcde 12345"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
     
    @Test
    public void invalidField25() {
    	List<String> errors = xmlValidator.validate(wrapToField("field25", ""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void validField40CWithOptional35X() {
    	List<String> errors = xmlValidator.validate(wrapToField("field40C", "ABCD/-axz340ty9/-?:().,'+{}\nABCDEFREDERT"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void validField40CWithoutOptional35X() {
    	List<String> errors = xmlValidator.validate(wrapToField("field40C", "ABCD"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidMaxLength40C() {
    	List<String> errors = xmlValidator.validate(wrapToField("field40C", "THIS STATEMENT IS THE INVALID LENGTH OF 40C"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }   

    @Test
    public void invalidMinLength40C() {
    	List<String> errors = xmlValidator.validate(wrapToField("field40C", "WTF"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }   

    
    @Test
    public void noErrorField40F() {
    	List<String> errors = xmlValidator.validate(wrapToField("field40F", "ABCDE abcde 12345"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
     
    @Test
    public void invalidField40F() {
    	List<String> errors = xmlValidator.validate(wrapToField("field40F", ""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void noErrorField77B() {
        List<String> errors = xmlValidator.validate(wrapToField("field77B", "/ORDERRES/BE//MEILAAN 1, 9000 GENT"));
        printErrors(errors);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void errorOnFormatField77B() {
        List<String> errors = xmlValidator.validate(wrapToField("field77B", ""));
        printErrors(errors);
        assertFalse(errors.isEmpty());
    }

    @Test
    public void noErrorField71G() {
        List<String> errors = xmlValidator.validate(wrapToField("field71G", "EUR2,50"));
        printErrors(errors);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void errorOnFormatField71G() {
        List<String> errors = xmlValidator.validate(wrapToField("field71G", "PHPP103,20"));
        printErrors(errors);
        assertFalse(errors.isEmpty());
    }

    @Test
    public void noErrorField71F() {
        List<String> errors = xmlValidator.validate(wrapToField("field71F", "EUR2,50"));
        printErrors(errors);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void errorOnFormatField71F() {
        List<String> errors = xmlValidator.validate(wrapToField("field71F", "PHPP103,20"));
        printErrors(errors);
        assertFalse(errors.isEmpty());
    }



    @Test
    public void noErrorField77C() {
    	List<String> errors = xmlValidator.validate(wrapToField("field77C", "ABCD/THEQUICKBROWNFOXJUMPSOVERTHELAZYDOG/ABCD/THEQUICKBROWNFOXJUMPSOVERTHELAZYDOG/"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void errorOnFormatField77C() {
    	List<String> errors = xmlValidator.validate(wrapToField("field77C", "$#@!)(*ABCD/THEQUICKBROWNFOXJUMPSOVERTHELAZYDOG/ABCD/THEQUICKBROWNFOXJUMPSOVERTHELAZYDOG/!@()*()"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }   

    @Test
    public void validField77T(){
    	List<String> errors = xmlValidator.validate(wrapToField("field77T", "azAZ09.,-()/='+:?!*;{@#_"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());    	
    }

    @Test
    public void validField77TNull(){
    	List<String> errors = xmlValidator.validate(wrapToField("field77T", ""));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());    	
    }

    @Test
    public void invalidField77T(){
    	List<String> errors = xmlValidator.validate(wrapToField("field77T", "[]"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());    	
    }
    
    
    @Test
    public void noErrorField58A() {
    	List<String> errors = xmlValidator.validate(wrapToField("field58A", "/A/ABCDE ABCDEF12345"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField58A() {
    	List<String> errors = xmlValidator.validate(wrapToField("field58A", "/1/Abc ABCDE12345"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void noErrorField58B() {
    	List<String> errors = xmlValidator.validate(wrapToField("field58B", "/A/ABCDE ABCDEF12345"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField58B() {
    	List<String> errors = validate(wrapToField("field55B","/ABCDE /ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 " +
        		"/ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
      
    @Test
    public void noErrorField58D() {
    	List<String> errors = xmlValidator.validate(wrapToField("field58D", "/A/ABCDE ABC123"));
    	printErrors(errors);
        assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField58D() {
    	List<String> errors = xmlValidator.validate(wrapToField("field58D", ""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }  
    
    @Test
    public void noErrorField71A() {
    	List<String> errors = xmlValidator.validate(wrapToField("field71A", "ABC"));
    	printErrors(errors);
        assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField71A() {
    	List<String> errors = xmlValidator.validate(wrapToField("field71A", "a1!"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void validField21() {
    	List<String> errors = xmlValidator.validate(wrapToField("field21", "vAl/-?:().,'+{} "));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
     
    @Test
    public void invalidMaxLengthField21() {
    	List<String> errors = xmlValidator.validate(wrapToField("field21", "ThIs LengTH iS InVaLiD/-?:().,'+{} "));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void invalidMinLengthField21() {
    	List<String> errors = xmlValidator.validate(wrapToField("field21", ""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void noErrorField31E() {
    	List<String> errors = xmlValidator.validate(wrapToField("field31E", "123456"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
     
    @Test
    public void invalidField31E() {
    	List<String> errors = xmlValidator.validate(wrapToField("field31E", "ABC123"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void noErrorField77A() {
    	List<String> errors = xmlValidator.validate(wrapToField("field77A", "Narrative"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField77A() {
    	List<String> errors = xmlValidator.validate(wrapToField("field77A", ""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void noErrorField26E() {
    	List<String> errors = xmlValidator.validate(wrapToField("field26E", "0"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField26E() {
    	List<String> errors = xmlValidator.validate(wrapToField("field26E", "100"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void noErrorField26T() {
    	List<String> errors = xmlValidator.validate(wrapToField("field26T", "99B"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidMinLengthField26T() {
    	List<String> errors = xmlValidator.validate(wrapToField("field26T", "10"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void invalidMaxLengthField26T() {
    	List<String> errors = xmlValidator.validate(wrapToField("field26T", "1000"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void noErrorField79() {
    	List<String> errors = xmlValidator.validate(wrapToField("field79", "asdfasdfasfasdfasdfasfasdf" +
    			"asdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasd" +
    			"fasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfas" +
    			"dfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdf" +
    			"asfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfa" +
    			"sdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdf" +
    			"asfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfas" +
    			"dfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasd" +
    			"fasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfas" +
    			"fasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasf" +
    			"asdfazxcvzxcvsdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfas" +
    			"dfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfas" +
    			"dfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfa" +
    			"sdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasd" +
    			"fasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdf" +
    			"asfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfas" +
    			"fasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfas" +
    			"dfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdf" +
    			"asdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfas" +
    			"dfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfa" +
    			"sfasdfasdfasfasdfasdfasfasdfasdfasfasdfazxcvzxcvsdfasfasdfasdfasfasdfasdfasfasdfa" +
    			"sdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfa" +
    			"sfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasd" +
    			"fasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdf" +
    			"asfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfasdfasdfasfas"));
    	printErrors(errors);
    	
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField79() {
    	List<String> errors = xmlValidator.validate(wrapToField("field79", ""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void noErrorField73() {
    	List<String> errors = xmlValidator.validate(wrapToField("field73", "1234567890123456789012345678901234\n"+
    			"1234567890123456789012345678901234\n"+
    			"1234567890123456789012345678901234\n"+
    			"1234567890123456789012345678901234\n"+
    			"1234567890123456789012345678901234\n"+
    			"1234567890123456789012345678901234\n"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField73() {
    	List<String> errors = xmlValidator.validate(wrapToField("field73", ""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void noErrorField77J() {
    	List<String> errors = xmlValidator.validate(wrapToField("field77J", "Discrepancies"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField77J() {
    	List<String> errors = xmlValidator.validate(wrapToField("field77J", ""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void validField13C(){
        List<String> errors = validate(wrapToField("field13C","/CLSTIME/0915+0200"));
        assertTrue(errors.isEmpty());
    }

    @Test
    public void invalidField13C(){
    	List<String> errors = validate(wrapToField("field13C","//CLSTIME/0915+0200"));
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void noErrorField32D() {
    	List<String> errors = xmlValidator.validate(wrapToField("field32D", "012345ABC123456789012,99"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void errorOnFormatField32D() {
    	List<String> errors = xmlValidator.validate(wrapToField("field32D", "012345ABC1234567890123,999"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }  

    @Test
    public void noErrorField32K() {
    	List<String> errors = xmlValidator.validate(wrapToField("field32K", "A123BCDEF12345678912345,"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void errorOnFormatField32K() {
    	List<String> errors = xmlValidator.validate(wrapToField("field32K", "A123BCDEF12345678912345"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }  
    
    @Test
    public void validField32A(){
    	List<String> errors = validate(wrapToField("field32A","122010ABC12345678901234,"));
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField32A(){
        List<String> errors = validate(wrapToField("field32A","ALDIALFDKFLH"));
        assertFalse(errors.isEmpty());
    }

    @Test
    public void noErrorField56A() {
        List<String> errors = xmlValidator.validate(wrapToField("field56A", "/A/ABCDE ABCDEF12345"));
        printErrors(errors);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void invalidField56A() {
        List<String> errors = xmlValidator.validate(wrapToField("field56A", "/1/Abc ABCDE12345"));
        printErrors(errors);
        assertFalse(errors.isEmpty());
    }

    @Test
    public void noErrorField56C() {
    	List<String> errors = xmlValidator.validate(wrapToField("field56C", "/A/ABCDE ABCDEF12345"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField56C() {
    	List<String> errors = xmlValidator.validate(wrapToField("field56C", "/12345678901234567890123456789012345"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void validField56DFull(){
    	List<String> errors = xmlValidator.validate(wrapToField("field56D","/B/13123123ASDFAASDADASER453451231\n"+
    				"12345678901234567890123456789012345\n"+
    				"12345678901234567890123456789012345\n"+
    				"12345678901234567890123456789012345\n"+
    				"12345678901234567890123456789012345\n"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void validField56DWithout1a(){
    	List<String> errors = xmlValidator.validate(wrapToField("field56D","/13123123ASDFAASDADASER453451231\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void validField56DWithout34x(){
    	List<String> errors = xmlValidator.validate(wrapToField("field56D","/A\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void invalidField56D(){
    	List<String> errors = xmlValidator.validate(wrapToField("field56D","/A/234567890123456789012345678901234\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\nMAXLENGTH"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void invalidField56DNull(){
    	List<String> errors = xmlValidator.validate(wrapToField("field56D",""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void errorOnFormatField56D(){
    	List<String> errors = xmlValidator.validate(wrapToField("field56D","/\\/l4jl23j4lJASLFJLAKSJL3K4J5LJLKAJFLASJKLFKJ34L5JLAJDFL345JLAKJFLJ"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }


    @Test
    public void validField23B() {
        List<String> errors = xmlValidator.validate(wrapToField("field23B", "AD3C"));
        printErrors(errors);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void invalidMinLengthField23B() {
        List<String> errors = xmlValidator.validate(wrapToField("field23B", "A2C"));
        printErrors(errors);
        assertFalse(errors.isEmpty());
    }

    @Test
    public void invalidMaxLengthField23B() {
    	List<String> errors = xmlValidator.validate(wrapToField("field23B", "AB1DE"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void invalidFormatField23B() {
    	List<String> errors = xmlValidator.validate(wrapToField("field23B", "aS1F"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void validField23ESlashWith30X() {
    	List<String> errors = xmlValidator.validate(wrapToField("field23E", "AS1F/ ThIsFiElDiS VaLiD/-?:().,'+{}"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void validField23EWithout30X() {
    	List<String> errors = xmlValidator.validate(wrapToField("field23E", "AS1F"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void invalidField23EWithoutSlash() {
    	List<String> errors = xmlValidator.validate(wrapToField("field23E", "AS1F ThIsFiElDiS VaLiD/-?:().,'+{}"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void invalidField23EWithout30X() {
    	List<String> errors = xmlValidator.validate(wrapToField("field23E", "aS1F"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void invalidField23EWith30X() {
    	List<String> errors = xmlValidator.validate(wrapToField("field23E", "ABCD ThIs FiElD iS iNVaLiD/-?:().,'+{}"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void validField50F(){
        List<String> errors = validate(wrapToField("field50F","1234567890123456789012345678901234\n" +
                "1234567890123456789012345678901234\n" +
                "1234567890123456789012345678901234\n" +
                "1234567890123456789012345678901234\n" +
                "1234567890123456789012345678901234"));
        assertTrue(errors.isEmpty());
    }

    @Test
    public void invalidLengthField50F(){
    	List<String> errors = validate(wrapToField("field50F","1234567890123456789012345678901234\n" +
    			"1234567890123456789012345678901234\n" +
    			"1234567890123456789012345678901234\n" +
    			"1234567890123456789012345678901234\n" +
    			"1234567890123456789012345678901234\n\n\n"));
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void invalidField50FNull(){
        List<String> errors = validate(wrapToField("field50F",""));
        assertFalse(errors.isEmpty());
    }

    @Test
    public void validField36(){
        List<String> errors = validate((wrapToField("field36","121313,4009")));
        assertTrue(errors.isEmpty());

    }

    @Test
    public void invalidField36(){
        List<String> errors = validate((wrapToField("field36","121313.4009")));
        assertFalse(errors.isEmpty());
    }

    @Test
    public void validField55AFull(){
    	List<String> errors = xmlValidator.validate(wrapToField("field55A","/A/SERTWQ1234\nABCDAB1A12B"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void validField55AWithout1a(){
    	List<String> errors = xmlValidator.validate(wrapToField("field55A","/ABCDEFGHIJKLMNOPQRSTWXYZ123\nABCDAB1A12B"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void validField55AWithout3c(){
    	List<String> errors = xmlValidator.validate(wrapToField("field55A","/ABCDEFGHIJKLMNOPQRSTWXYZ123\nABCDAB1A"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void validField55AWithout34X(){
    	List<String> errors = xmlValidator.validate(wrapToField("field55A","/AABCDAB1A12B"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void invalidField55AWithoutNewLine(){
    	List<String> errors = xmlValidator.validate(wrapToField("field55A","/ABCDEFGHIJKLMNOPQRSTWXYZ123ABCDAB1A12B"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void invalidField55AWithoutRequiredFields(){
    	List<String> errors = xmlValidator.validate(wrapToField("field55A","/ABCDEFGHIJKLMNOPQRSTWXYZ123\n12B"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void nullField55A(){
    	List<String> errors = xmlValidator.validate(wrapToField("field55A",""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void validField55B(){
        List<String> errors = validate(wrapToField("field55B","/36143038\n" +
                "CITIUS33"));
        assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidField55B(){
        List<String> errors = validate(wrapToField("field55B","/ABCDE /ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 " +
        		"/ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"));
        assertFalse(errors.isEmpty());
    }
    
    @Test
    public void validField55DFull(){
    	List<String> errors = xmlValidator.validate(wrapToField("field55D","/B/13123123ASDFAASDADASER453451231\n"+
    				"12345678901234567890123456789012345\n"+
    				"12345678901234567890123456789012345\n"+
    				"12345678901234567890123456789012345\n"+
    				"12345678901234567890123456789012345\n"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void validField55DWithout1a(){
    	List<String> errors = xmlValidator.validate(wrapToField("field55D","/13123123ASDFAASDADASER453451231\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void validField55DWithout34x(){
    	List<String> errors = xmlValidator.validate(wrapToField("field55D","/A\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void invalidField55D(){
    	List<String> errors = xmlValidator.validate(wrapToField("field55D","/A/234567890123456789012345678901234\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\nMAXLENGTH"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void invalidField55DNull(){
    	List<String> errors = xmlValidator.validate(wrapToField("field55D",""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void errorOnFormatField55D(){
    	List<String> errors = xmlValidator.validate(wrapToField("field55D","/\\/l4jl23j4lJASLFJLAKSJL3K4J5LJLKAJFLASJKLFKJ34L5JLAJDFL345JLAKJFLJ"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void validField70(){
    	List<String> errors = xmlValidator.validate(wrapToField("field70","1234567890123456789012345678901234\n"+
    			"1234567890123456789012345678901234\n"+
    			"1234567890123456789012345678901234\n"+
    			"1234567890123456789012345678901234\n"));
    	printErrors(errors);
    	assertTrue(errors.isEmpty());
    }
    
    @Test
    public void invalidLengthField70(){
    	List<String> errors = xmlValidator.validate(wrapToField("field70","12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"+
    			"12345678901234567890123456789012345\n"));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }

    @Test
    public void invalidField70Null(){
    	List<String> errors = xmlValidator.validate(wrapToField("field70",""));
    	printErrors(errors);
    	assertFalse(errors.isEmpty());
    }
    
    @Test
    public void validField50K(){
        List<String> errors = validate("/ACCOUNTNUMBER FIELDSOMETHING\n" +
                "ADDRESS LINE1 \n" +
                "ADDRESS LINE2 \n" +
                "ADDRESS LINE3 \n" +
                "ADDRESS LINE4 \n","field50K");
        assertTrue(errors.isEmpty());
    }

    @Test
    public void validField50KWithout34X(){
    	List<String> errors = validate("" +
    			"ADDRESS LINE1 \n" +
    			"ADDRESS LINE2 \n" +
    			"ADDRESS LINE3 \n" +
    			"ADDRESS LINE4 \n","field50K");
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void invalidField50KBecauseItIsHas5Lines(){
        List<String> errors = validate("/ACCOUNTNUMBER\n" +
                "ADDRESS LINE1 \n" +
                "ADDRESS LINE2 \n" +
                "ADDRESS LINE3 \n" +
                "ADDRESS LINE4 \n" +
                "ADDRESS LINE5 \n","field50K");
        assertFalse(errors.isEmpty());
    }

    private List<String> validate(String value,String field){
        return validate(wrapToField(field,value));
    }

    private List<String> validate(String field){
        List<String> errors = xmlValidator.validate(field);
        printErrors(errors);
        return errors;
    }

    private void printErrors(List<String> errors){
        for(String error : errors){
            System.out.println(error);
        }
    }

    private String wrapToField(String fieldName,String value){
        return "<" + fieldName + " " + SWIFT_SCHEMA + ">" + value + "</" + fieldName + ">";
    }
}