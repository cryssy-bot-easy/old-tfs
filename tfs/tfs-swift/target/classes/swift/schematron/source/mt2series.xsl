<?xml version="1.0" encoding="UTF-8"?>
<schema xmlns="http://www.ascc.net/xml/schematron"
            xmlns:iso="http://purl.oclc.org/dsdl/schematron"
            xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <ns uri="http://www.ucpb.com.ph/tfs/schemas/mt2series" prefix="mt2"/>
    
    <pattern name="Check Structure">
    	<rule context="mt2:mt202">
            <assert test="((//mt2:field56A or //mt2:field56D) and (//mt2:field57A or //mt2:field57B or //mt2:field57D)) or not((//mt2:field56A or //mt2:field56D))">
                Field 57a must be present if Field 56a is present (Error C81)|</assert>
        </rule>
    </pattern>
</schema>