package com.ucpb.tfs.util;

import com.ucpb.tfs.domain.condition.ConditionCode;
import com.ucpb.tfs.domain.condition.AdditionalCondition;
import com.ucpb.tfs.domain.condition.enumTypes.ConditionType;
import com.ucpb.tfs.domain.reimbursing.InstructionToBankCode;
import com.ucpb.tfs.domain.reimbursing.InstructionToBank;
import com.ucpb.tfs.utils.SwiftFormatter;
import org.junit.Test;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;

/**
 */
public class SwiftFormatterTest {


    @Test
    public void formatInstructionsToBank(){
        Set<InstructionToBank> instructionsToBank = new HashSet<InstructionToBank>();

        InstructionToBank instruction = new InstructionToBank(new InstructionToBankCode("CODE"),"THIS IS A NEW INSTRUCTION THAT MUST BE FOLLOWED. BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH");

        instructionsToBank.add(instruction);

        String result = SwiftFormatter.formatInstructionsToBank(instructionsToBank);
        assertEquals("+THIS IS A NEW INSTRUCTION THAT MUST BE FOLLOWED. BLAH BLAH BLAH \r\n" +
                "BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH \r\n" +
                "BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH \r\n" +
                "BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH",result);

    }

    @Test
    public void formatConditions(){
        Set<AdditionalCondition> conditions = new HashSet<AdditionalCondition>();
        AdditionalCondition condition = new AdditionalCondition(ConditionType.NEW,new ConditionCode("1234"),"THIS IS A CONDITION TO BE FOLLOWED. THIS IS A CONDITION TO BE FOLLOWED. THIS IS A CONDITION TO BE FOLLOWED. THIS IS A CONDITION TO BE FOLLOWED. THIS IS A CONDITION TO BE FOLLOWED. THIS IS A CONDITION TO BE FOLLOWED. THIS IS A CONDITION TO BE FOLLOWED. THIS IS A CONDITION TO BE FOLLOWED. THIS IS A CONDITION TO BE FOLLOWED.");
        conditions.add(condition);


        assertEquals("+THIS IS A CONDITION TO BE FOLLOWED. THIS IS A CONDITION TO BE \r\n" +
                "FOLLOWED. THIS IS A CONDITION TO BE FOLLOWED. THIS IS A \r\n" +
                "CONDITION TO BE FOLLOWED. THIS IS A CONDITION TO BE FOLLOWED. \r\n" +
                "THIS IS A CONDITION TO BE FOLLOWED. THIS IS A CONDITION TO BE \r\n" +
                "FOLLOWED. THIS IS A CONDITION TO BE FOLLOWED. THIS IS A \r\n" +
                "CONDITION TO BE FOLLOWED.",SwiftFormatter.formatConditions(conditions));
    }

    @Test
    public void formatValidCurrencyAmountDateInputs() throws ParseException {
        assertEquals("870108PHP100,00",SwiftFormatter.formatCurrencyAmountDate("PHP","100.00","01/08/1987"));
    }
    
    @Test
    public void formatValidNarrative() {
    	String test="+field 47a please add:\n"+
    	 "''blah blah blah blah''\n"+
    	"+PLEASE DEDUCT SOMETHING 12345 OUT OF NOWHERE "+
    	"PLEASE DEDUCT SOMETHING 12345 OUT OF NOWHERE "+
    	"PLEASE DEDUCT SOMETHING 12345 OUT OF NOWHERE\n"+
    	"+all other terms and conditions remain unchanged.\n" +
    	"+rawr: asd\n" +
    	"YOU MUST BE MORE THAN FIFTY CHARACTERSSSSSSSSSSSSSSSSSSSSSSSSSSS\n" +
    	"+walang langzzz.";
    	System.out.println(SwiftFormatter.formatNarrative(50,test));
    	assertEquals("+field 47a please add:\r\n"+
			"''blah blah blah blah''\r\n"+
			"+PLEASE DEDUCT SOMETHING 12345 OUT OF NOWHERE \r\n"+ 
			"PLEASE DEDUCT SOMETHING 12345 OUT OF NOWHERE \r\n"+
			"PLEASE DEDUCT SOMETHING 12345 OUT OF NOWHERE\r\n"+
			"+all other terms and conditions remain unchanged.\r\n"+
			"+rawr:+asd+"+
			"YOU MUST BE MORE THAN FIFTY \r\n"+
			"CHARACTERSSSSSSSSSSSSSSSSSSSSSSSSSSS\r\n"+
			"+walang langzzz.\r\n",
			SwiftFormatter.formatNarrative(50,test));
    }

    @Test
    public void formatValidNarrativeWithoutColon() {
    	String test="PADDING+wala lang.\n+walang lang.";
    	System.out.println(SwiftFormatter.formatNarrative(50,test));
    	assertEquals(
    			"PADDING\r\n+wala lang.\r\n"+
    			"+walang lang.\r\n",
    			SwiftFormatter.formatNarrative(50,test));
    }
    
    @Test
    public void formatDescriptionOfGoods(){
//    	String[] test={"GENERAL DESCRIPTION 1","GENERAL DESCRIPTION 2"};
    	String test1 = "GENERAL DESCRIPTION";
    	String test2 = "PRICE TERM";
    	String result=SwiftFormatter.formatDescriptionOfGoods(test1, test2);
    	System.out.println(result);
    	assertEquals("+GENERAL DESCRIPTION 1\r\n"+
    				"+GENERAL DESCRIPTION 2\r\n"+
    				"+GENERAL DESCRIPTION 3",result);
    }
}
