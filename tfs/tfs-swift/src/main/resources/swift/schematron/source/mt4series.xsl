<?xml version="1.0" encoding="UTF-8"?>
<schema xmlns="http://www.ascc.net/xml/schematron"
            xmlns:iso="http://purl.oclc.org/dsdl/schematron"
            xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <ns uri="http://www.ucpb.com.ph/tfs/schemas/mt4series" prefix="mt4"/>
    
    <pattern name="Check Structure">
    	<rule context="mt4:mt400">
			<!--<assert test="not(//mt4:field57A) or (//mt4:field53A and //mt4:field54A)">-->
				<!--Error Code C11:Field 57a may only be present when fields 53a and 54a are both present.|-->
			<!--</assert>-->

            <report test="(//mt4:field57A or //mt4:field57D) and not((//mt4:field53A or //mt4:field53B or //mt4:field53D) and (//mt4:field54A or //mt4:field54B or //mt4:field54D))">
                Field 57a may only be present when fields 53a and 54a are both present (Error C11)|
            </report>

			<assert test="substring(//mt4:field32A/text(),7,3) = substring(//mt4:field33A/text(),7,3)
			or substring(//mt4:field32B/text(),1,3) = substring(//mt4:field33A/text(),7,3)
			or substring(//mt4:field32K/text(),7,3) = substring(//mt4:field33A/text(),7,3)">
                The currency code for Fields 32a and 33A must be the same (Error C02)|
			</assert>
    	</rule>

    	<rule context="mt4:mt410 | mt4:mt412">
			<assert test="count(//mt4:field20) &lt; 11">
				Error Code T10:Field 20 may not appear more than ten times.|
			</assert>
			<assert test="count(//mt4:field21) &lt; 11">
				Error Code T10:Field 21 may not appear more than ten times.|
			</assert>
			<assert test="count(//mt4:field32A) &lt; 11">
				Error Code T10:Field 32A may not appear more than ten times.|
			</assert>
<!-- 			<assert test="//mt4:field32A/text() = //mt4:field32A/text()"> -->
<!-- 				Error Code C02:The currency code in the amount field 32a must be the same for all occurrences of this field in the message.| -->
<!-- 			</assert> -->
    	</rule>
    </pattern>
</schema>