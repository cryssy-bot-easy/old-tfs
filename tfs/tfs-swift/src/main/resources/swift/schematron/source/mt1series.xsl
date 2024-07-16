<?xml version="1.0" encoding="UTF-8"?>
<schema xmlns="http://www.ascc.net/xml/schematron"
            xmlns:iso="http://purl.oclc.org/dsdl/schematron"
            xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <ns uri="http://www.ucpb.com.ph/tfs/schemas/mt1series" prefix="mt1"/>



    <pattern name="Check structure">
        <rule context="mt1:mt103/mt1:field72">
            <report test="not(contains(text(),'/ACC/') or contains(text(),'/INS/') or contains(text(),'/INT/') or contains(text(),'/REC/'))">
                Field 72 must contain any of these codes: ACC, INS, INT, REC|
            </report>
        </rule>

        <rule context="mt1:mt103/mt1:field53B">
            <report test="(//mt1:field23B/text() = 'SPRI' or //mt1:field23B/text() = 'SSTD' or //mt1:field23B/text() = 'SPAY') and not(starts-with(text(),'/'))">
                Party Identifier for Field 53B is required if Field 23B contains either SPRI, SSTD or SPAY (Error E04)|
            </report>
        </rule>

        <rule context="mt1:mt103">

            <assert test="((//mt1:field55A or //mt1:field55B or //mt1:field55D) and (//mt1:field53A or //mt1:field53B or //mt1:field53D) and
            (//mt1:field54A or //mt1:field54B or //mt1:field54D)) or not((//mt1:field55A or //mt1:field55B or //mt1:field55D))">Fields 53a and 54a must be present if Field 55a is present (Error E06)|</assert>


            <assert test="not(//mt1:field23B) or not(contains(//mt1:field23B[1]/text(),'SPRI')) or (contains(//mt1:field23E[1]/text(),'TELB') or contains(//mt1:field23E[1]/text(),'SDVA') or contains(//mt1:field23E[1]/text(),'PHOB') or contains(//mt1:field23E[1]/text(),'INTC')) or not(//mt1:field23E)">Field23E must contain either SDVA,TELB or PHOB or INTC if Field23B contains SPRI|</assert>
            <!--<assert test="not(//mt1:field23B) or functions:in-list('TELB,SPRI,SDVA,INTC,PHOB',//mt1:field23E/text()) or not(//mt1:field23E)">Field23E must contain either SDVA,TELB or PHOB or INTC if Field23B contains SPRI|</assert>-->
			
            <assert test="(//mt1:field70[1] and not(//mt1:field77T[1])) or
            (not(//mt1:field70[1]) and //mt1:field77T[1]) or
            (not(//mt1:field70[1]) and not(//mt1:field77T[1]))">Field 70 and Field 77T are mutually exclusive|</assert>

            <assert test="((//mt1:field23B[1]/text() = 'SPRI' or //mt1:field23B[1]/text() = 'SSTD' or //mt1:field23B[1]/text() = 'SPAY') and not(//mt1:field53D)) or (//mt1:field23B[1]/text() != 'SPRI' and //mt1:field23B[1]/text() != 'SSTD' and //mt1:field23B[1]/text() != 'SPAY') or not(//mt1:field23B) ">Field 53D must not be used if the value of Field 23B is either SPRI,SSTD or SPAY|</assert>

            <assert test="((//mt1:field23B[1]/text() = 'SPRI' or //mt1:field23B[1]/text() = 'SSTD' or //mt1:field23B[1]/text() = 'SPAY') and not(//mt1:field54B) and not(//mt1:field54D)) or (//mt1:field23B[1]/text() != 'SPRI' or //mt1:field23B[1]/text() != 'SSTD' or //mt1:field23B[1]/text() != 'SPAY') or not(//mt1:field23B) ">Only option A may be used for Field 54 if the value of Field 23B is equal to either SPRI,SSTD or SPAY (Error E05)|</assert>

            <assert test="((//mt1:field23B[1]/text() = 'SPRI' or //mt1:field23B[1]/text() = 'SSTD' or //mt1:field23B[1]/text() = 'SPAY') and not(//mt1:field55B) and not(//mt1:field55D)) or (//mt1:field23B[1]/text() != 'SPRI' or //mt1:field23B[1]/text() != 'SSTD' or //mt1:field23B[1]/text() != 'SPAY') or not(//mt1:field23B)">Only option A may be used for Field 55 if the value of Field 23B is equal to either SPRI,
                SSTD or SPAY (Error E05)|</assert>

            <assert test="((//mt1:field56A or //mt1:field56C or //mt1:field56D) and (//mt1:field57A or //mt1:field57B or //mt1:field57C or //mt1:field57D)) or not((//mt1:field56A or //mt1:field56C or //mt1:field56D))">Field 57a must be present if Field 56a is present (Error C81)|</assert>

            <assert test="(contains(//mt1:field71A[1],'OUR') and not(//mt1:field71F)) or not(contains(//mt1:field71A[1],'OUR'))">Field 71F must not be present if Field 71A is equal to OUR|</assert>

            <assert test="(contains(//mt1:field71A[1],'SHA') and not(//mt1:field71G)) or not(contains(//mt1:field71A[1],'SHA'))">Field 71G must not be present if Field 71A is equal to SHA|</assert>

            <assert test="(contains(//mt1:field71A[1],'BEN') and not(//mt1:field71G)) or not(contains(//mt1:field71A[1],'BEN'))">Field 71G must not be present if Field 71A is equal to BEN|</assert>

            <assert test="(contains(//mt1:field71A[1],'BEN') and //mt1:field71F[1]) or not(contains(//mt1:field71A[1],'BEN'))">At least one Field 71F must be present if Field 71A is equal to BEN|</assert>

            <assert test="(//mt1:field33B and (//mt1:field71F or //mt1:field71G)) or
            (not(//mt1:field71F) and not(//mt1:field71G))">Field 33B is mandatory if either 71F or 71G is present|</assert>

            <assert test="(not(//mt1:field56A) and //mt1:field23E[1]/text() != 'TELI' and //mt1:field23E[1]/text() != 'PHOI') or //mt1:field56A or not(//mt1:field23E[1])">
                Field 23E must not equal to TELI or PHOI if Field 56A is not present|
            </assert>

            <assert test="(not((//mt1:field57A or //mt1:field57B or //mt1:field57C or //mt1:field57D)) and //mt1:field23E[1]/text() != 'TELE' and //mt1:field23E[1]/text() != 'PHON') or (//mt1:field57A or //mt1:field57B or //mt1:field57C or //mt1:field57D) or not(//mt1:field23E[1])">
                Field 23E must not equal to TELE or PHON if Field 57a is not present (Error E45)|
            </assert>

            <assert test="not(//mt1:field71G) or not(//mt1:field32A) or (substring(//mt1:field32A[1]/text(),7,3) = substring(//mt1:field71G[1]/text(),1,3))">
                The currency code for Fields 71G and 32A must be the same (Error C02)|
            </assert>

            <report test="contains(//mt1:field23E[1]/text(),'CHQB') and (starts-with(//mt1:field59A[1]/text(),'/') or starts-with(//mt1:field59[1]/text(),'/'))">Account Field in Field 59A is not allowed if
                Field 23E is equal to CHQB (Error E18)|</report>

            <!--<assert test="not(//mt1:field23E) or (contains(//mt1:field23E[1]/text(),'CHQB') and not(starts-with(//mt1:field59A[1]/text(),'/'))) or not(//mt1:field59A)">Account Field in Field 59A is not allowed if-->
            <!--Field 23E is equal to CHQB (Error E18)|</assert>-->

            <assert test="not(//mt1:field33B) or not(//mt1:field32A) or (substring(//mt1:field32A/text(),7,3) = substring(//mt1:field33B/text(),1,3) and not(//mt1:field36)) or (substring(//mt1:field32A/text(),7,3) != substring(//mt1:field33B/text(),1,3) and //mt1:field36)">Field 36 is required only if the currencies of Field 32A and Field 33B are different|</assert>
        </rule>

        <rule context="mt1:mt103/mt1:field23B">
            <assert test="((//mt1:field23B/text() = 'SPRI' or
             //mt1:field23B/text() = 'SSTD' or //mt1:field23B/text() = 'SPAY') and (//mt1:field59 or //mt1:field59A)) or //mt1:field23B/text() = 'CRED'">
            Account field of Field 59a is Mandatory if Field 23B is equal to SPRI, SSTD or SPAY (Error E10)|</assert>
<!-- <report test="(text() = 'SPRI' or text() = 'SSTD' or text() = 'SPAY') and not(starts-with(//mt1:field59A[1]/text(),'/') or starts-with(//mt1:field59[1]/text(),'/'))">Account field of Field 59a is Mandatory if Field 23B is equal to SPRI, SSTD or SPAY (Error E10)|</report> -->
            <report test="//mt1:field23B/text() = 'SPRI' and ((//mt1:field56A) or (//mt1:field56C) or (//mt1:field56D))">Field 56a must not be present if Field 23B is equal to SPRI (Error E16)|</report>
        </rule>


        <!-- check mt103Plus -->
        <rule context="mt1:mt103Plus">
            <assert test="not(//mt1:field33B) or not(//mt1:field32A) or (substring(//mt1:field32A/text(),7,3) = substring(//mt1:field33B/text(),1,3) and not(//mt1:field36)) or (substring(//mt1:field32A/text(),7,3) != substring(//mt1:field33B/text(),1,3) and //mt1:field36)">Field 36 is required only if the currencies of Field 32A and Field 33B are different (Error D75)|</assert>

        <assert test="not(//mt1:field23B) or not(contains(//mt1:field23B[1]/text(),'SPRI')) or (contains(//mt1:field23E[1]/text(),'TELB') or contains(//mt1:field23E[1]/text(),'SDVA') or contains(//mt1:field23E[1]/text(),'PHOB') or contains(//mt1:field23E[1]/text(),'INTC')) or not(//mt1:field23E)">Field23E must contain either SDVA,INTC if Field23B contains SPRI (Error E01)|</assert>

            <assert test="((//mt1:field55A or //mt1:field55B or //mt1:field55D) and (//mt1:field53A or //mt1:field53B or //mt1:field53D) and
            (//mt1:field54A or //mt1:field54B or //mt1:field54D)) or not((//mt1:field55A or //mt1:field55B or //mt1:field55D))">Fields 53a and 54a must be present if Field 55a is present (Error E06)|</assert>

        <report test="//mt1:field23B/text() = 'SPRI' and ((//mt1:field56A) or (//mt1:field56C) or (//mt1:field56D))">Field 56A must not be present if Field 23B is equal to SPRI (Error E16)|</report>


        <assert test="(contains(//mt1:field71A[1],'OUR') and not(//mt1:field71F)) or not(contains(//mt1:field71A[1],'OUR'))">Field 71F must not be present if Field 71A is equal to OUR (Error E13)|</assert>

        <assert test="(contains(//mt1:field71A[1],'SHA') and not(//mt1:field71G)) or not(contains(//mt1:field71A[1],'SHA'))">Field 71G must not be present if Field 71A is equal to SHA (Error D50)|</assert>

        <assert test="(contains(//mt1:field71A[1],'BEN') and //mt1:field71F[1]) or not(contains(//mt1:field71A[1],'BEN'))">At least one Field 71F must be present if Field 71A is equal to BEN (Error E15)|</assert>

        <assert test="(//mt1:field33B and (//mt1:field71F or //mt1:field71G)) or
            (not(//mt1:field71F) and not(//mt1:field71G))">Field 33B is mandatory if either 71F or 71G is present (Error D51)|</assert>

        <assert test="not(//mt1:field71G) or not(//mt1:field32A) or (substring(//mt1:field32A[1]/text(),7,3) = substring(//mt1:field71G[1]/text(),1,3))">
            The currency code for Fields 71G and 32A must be the same (Error C02)|
        </assert>
        </rule>

    </pattern>
</schema>
