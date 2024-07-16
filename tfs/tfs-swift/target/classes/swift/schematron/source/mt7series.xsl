<?xml version="1.0" encoding="UTF-8"?>
<schema xmlns="http://www.ascc.net/xml/schematron"
            xmlns:iso="http://purl.oclc.org/dsdl/schematron"
            xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <ns uri="http://www.ucpb.com.ph/tfs/schemas/mt7series" prefix="mt7"/>
    
    <pattern name="Check Structure">
    	<rule context="mt7:mt700">
			<assert test="not(//mt7:field39A) or not(//mt7:field39B)">
				Either field 39A or 39B, but not both, may be present (Error D05)|
			</assert>
			<assert test="(//mt7:field42C  or  not(//mt7:field42A or //mt7:field42D))
						and ((//mt7:field42A or //mt7:field42D)  or not(//mt7:field42C))"> 
					When used, fields 42C and 42a must both be present (Error C90)|
			</assert>
			<assert test="(((//mt7:field42A  and  //mt7:field42C)
							 and not(//mt7:field42M or //mt7:field42P)) or 
 							(//mt7:field42M and not((//mt7:field42A  and  //mt7:field42C) or //mt7:field42P)) or
  							(//mt7:field42P and not((//mt7:field42A  and  //mt7:field42C) or //mt7:field42M)))
  							or not((//mt7:field42A  and  //mt7:field42C) or //mt7:field42M or //mt7:field42P)">
				Either fields 42C and 42a together, or field 42M alone,
				or field 42P alone may be present. No other combination of these fields is allowed (Error C90)|
			</assert>
			<assert test="not(//mt7:field44C) or not(//mt7:field44D)">
				Either field 44C or 44D, but not both, may be present (Error D06)|
			</assert>
    	</rule>
    	<rule context="mt7:mt707">
    		<assert test="not(//mt7:field32B or //mt7:field33B) or //mt7:field34B">
    			If either field 32B or 33B is present, field 34B must also be present (Error C12)|
    		</assert>
    		<assert test="not(//mt7:field34B) or (//mt7:field32B or //mt7:field33B)">
    			If field 34B is present, either field 32B or 33B must also be present (Error C12)|
    		</assert>
    		<assert test="not(//mt7:field23) or (//mt7:field52A or //mt7:field52D)">
		    	If field 23 is present, field 52a must also be present (Error C16)|
    		</assert>
    		<assert test="not(//mt7:field39A) or not(//mt7:field39B)">
				Either field 39A or 39B, but not both, may be present (Error D05)|
			</assert>
    		<assert test="not(//mt7:field44C) or not(//mt7:field44D)">
				Either field 44C or 44D, but not both, may be present (Error D06)|
			</assert>
			<assert test="//mt7:field31E or //mt7:field32B or //mt7:field33B or //mt7:field34B or
						  //mt7:field39A or //mt7:field39B or //mt7:field39C or //mt7:field44A or
						  //mt7:field44E or //mt7:field44F or //mt7:field44B or //mt7:field44C or
						  //mt7:field44D or //mt7:field79 or //mt7:field72">
				At least one of the fields 31E, 32B, 33B, 34B, 39A, 39B, 39C, 44A, 44E, 44F, 44B, 44C, 44D, 79 or 72 must be present (Error C30)|
			</assert>
    		<!--<assert test="(not(//mt7:field32B and //mt7:field33B) or-->
						<!--substring(//mt7:field32B/text(),1,3) = substring(//mt7:field33B/text(),1,3)) and-->
						<!--(not(//mt7:field33B and //mt7:field34B) or-->
						<!--substring(//mt7:field33B/text(),1,3) = substring(//mt7:field34B/text(),1,3)) and-->
						<!--(not(//mt7:field32B and //mt7:field34B) or-->
						<!--substring(//mt7:field32B/text(),1,3) = substring(//mt7:field34B/text(),1,3))">-->
				<!--The currency code in the amount fields 32B, 33B, and 34B must be the same. (Error C02)|-->
			<!--</assert>-->
            <assert test="(not(//mt7:field32B and //mt7:field33B and //mt7:field34B)) or
						(substring(//mt7:field32B/text(),1,3) = substring(//mt7:field33B/text(),1,3) and not(//mt7:field34B)) or

						(substring(//mt7:field33B/text(),1,3) = substring(//mt7:field34B/text(),1,3) and not(//mt7:field32B)) or

						(substring(//mt7:field32B/text(),1,3) = substring(//mt7:field34B/text(),1,3) and not(//mt7:field33B))
						or
						(substring(//mt7:field32B/text(),1,3) = substring(//mt7:field33B/text(),1,3) and substring(//mt7:field32B/text(),1,3) = substring(//mt7:field34B/text(),1,3))">
                The currency code in the amount fields 32B, 33B, and 34B must be the same. (Error C02)|
            </assert>
    	</rule>
    	<rule context="mt7:mt730">
    		<assert test="not(//mt7:field25) or not(//mt7:field57A)">
    			Either field 25 or 57a, but not both, may be present (Error C77)|
    		</assert>
    		<assert test="not(//mt7:field32D) or not(//mt7:field57A)">
    			If field 32D is present, field 57a must not be present (Error C78)|
    		</assert>
    	</rule>
    	<rule context="mt7:mt740">
    		<assert test="not(//mt7:field39A) or not(//mt7:field39B)">
    			Either field 39A or 39B, but not both, may be present (Error D05)|
    		</assert>
    		<assert test="(//mt7:field42C  or  not(//mt7:field42A or //mt7:field42D))
						and ((//mt7:field42A or //mt7:field42D)  or not(//mt7:field42C))"> 
					When used, fields 42C and 42a must both be present (Error C90)|
			</assert>
			<assert test="(((//mt7:field42A  and  //mt7:field42C)
							 and not(//mt7:field42M or //mt7:field42P)) or 
 							(//mt7:field42M and not((//mt7:field42A  and  //mt7:field42C) or //mt7:field42P)) or
  							(//mt7:field42P and not((//mt7:field42A  and  //mt7:field42C) or //mt7:field42M)))
  							or not((//mt7:field42A  and  //mt7:field42C) or //mt7:field42M or //mt7:field42P)">
				Either fields 42C and 42a together, or field 42M alone, or field 42P alone may be present. No other combination of these fields is allowed (Error C90)|
			</assert>
			<assert test="not(//mt7:field58A) or not(//mt7:field59)">
				Either field 58a or 59, but not both, may be present (Error D84)|
			</assert>
    	</rule>
    	<rule context="mt7:mt747">
    		<assert test="//mt7:field31E or //mt7:field32B or //mt7:field33B or //mt7:field34B or
    					  //mt7:field39A or //mt7:field39B or //mt7:field39C or //mt7:field72 or
    					  //mt7:field77A">
    			At least one of the fields 31E, 32B, 33B, 34B, 39A, 39B,39C, 72 or 77A must be present (Error C15)|
    		</assert>
    		<assert test="not(//mt7:field32B or //mt7:field33B) or //mt7:field34B">
    			If either field 32B or 33B is present, field 34B must also be present (Error C12)|
    		</assert>
    	    <report test="//mt7:field34B and (not(//mt7:field32B) and not(//mt7:field33B))">
    			If field 34B is present, either field 32B or 33B must also be present (Error C12)|
    		</report>
    		<assert test="not(//mt7:field39A) or not(//mt7:field39B)">
				Either field 39A or 39B, but not both, may be present (Error D05)|
			</assert>
    		<assert test="(not(//mt7:field32B and //mt7:field33B) or 
						substring(//mt7:field32B/text(),1,3) = substring(//mt7:field33B/text(),1,3)) and
						(not(//mt7:field33B and //mt7:field34B) or 
						substring(//mt7:field33B/text(),1,3) = substring(//mt7:field34B/text(),1,3)) and
						(not(//mt7:field32B and //mt7:field34B) or 
						substring(//mt7:field32B/text(),1,3) = substring(//mt7:field34B/text(),1,3))">
				The currency code in the amount fields 32B, 33B, and 34B must be the same (Error C02)|
			</assert>
    	</rule>
    	<rule context="mt7:mt750">
    		<assert test="not(//mt7:field33B or //mt7:field71B or //mt7:field73) or //mt7:field34B">
    			If field 33B and/or field 71B and/or field 73 is/are present, field 34B must also be present (Error C13)|
    		</assert>
    		<assert test="(not(//mt7:field32B and //mt7:field34B) or 
 							substring(//mt7:field32B/text(),1,3) = substring(//mt7:field34B/text(),1,3))"> 
				The currency code in the amount fields 32B, and 34B must be the same (Error C02)|
			</assert>
    	</rule>
    	<rule context="mt7:mt752">
    		<assert test="((//mt7:field32B and //mt7:field71B) and (//mt7:field33A or //mt7:field33B)) or
    						not(//mt7:field32B and //mt7:field71B)">
    			If fields 32B and 71B are both present, then field 33a must also be present (Error C18)|
    		</assert>
    		<assert test="(
    		                (not(//mt7:field32B) and not(//mt7:field33A) and not(//mt7:field33B)) or
 							((substring(//mt7:field32B/text(),1,3) = substring(//mt7:field33A/text(),7,3)) and not(//mt7:field33B)) or
 							((substring(//mt7:field32B/text(),1,3) = substring(//mt7:field33B/text(),1,3)) and not(//mt7:field33A)) or
 							(//mt7:field32B and not(//mt7:field33A) and not(//mt7:field33B)) or
 							(not(//mt7:field32B) and //mt7:field33A and not(//mt7:field33B)) or
                            (not(//mt7:field32B) and not(//mt7:field33A) and //mt7:field33B)
                         )">
				The currency code in the amount fields 32B and 33a must be the same (Error C02)|
			</assert>
    	</rule>
    	<rule context="mt7:mt734">
    		<assert test="not(//mt7:field73) or //mt7:field33A">
    			If field 73 is present, field 33a must also be present (Error code(s): C17)|
    		</assert>
    		<assert test="(
				(not(//mt7:field32A) and not(//mt7:field33A) and not(//mt7:field33B)) or
				((substring(//mt7:field32A/text(),7,3) = substring(//mt7:field33A/text(),7,3)) and not(//mt7:field33B)) or
				((substring(//mt7:field32A/text(),7,3) = substring(//mt7:field33B/text(),1,3)) and not(//mt7:field33A)) or
				(//mt7:field32A and not(//mt7:field33A) and not(//mt7:field33B)) or
				(not(//mt7:field32A) and //mt7:field33A and not(//mt7:field33B)) or
                (not(//mt7:field32A) and not(//mt7:field33A) and //mt7:field33B)
			)">
				The currency code in the amount fields 32A and 33a must be the same (Error code(s): C02)|
			</assert>
    	</rule>
    </pattern>
</schema>