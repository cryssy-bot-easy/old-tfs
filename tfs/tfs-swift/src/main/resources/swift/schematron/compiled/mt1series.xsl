<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:sch="http://www.ascc.net/xml/schematron"
                xmlns:mt1="http://www.ucpb.com.ph/tfs/schemas/mt1series"
                version="2.0"
                mt1:dummy-for-xmlns="">
   <xsl:template match="*|@*" mode="schematron-get-full-path">
      <xsl:apply-templates select="parent::*" mode="schematron-get-full-path"/>
      <xsl:text>/</xsl:text>
      <xsl:if test="count(. | ../@*) = count(../@*)">@</xsl:if>
      <xsl:value-of select="name()"/>
      <xsl:text>[</xsl:text>
      <xsl:value-of select="1+count(preceding-sibling::*[name()=name(current())])"/>
      <xsl:text>]</xsl:text>
   </xsl:template>
   <xsl:template match="/">
      <xsl:apply-templates select="/" mode="M1"/>
   </xsl:template>
   <xsl:template match="mt1:mt103/mt1:field72" priority="4000" mode="M1">
      <xsl:if test="not(contains(text(),'/ACC/') or contains(text(),'/INS/') or contains(text(),'/INT/') or contains(text(),'/REC/'))">Field 72 must contain any of these codes: ACC, INS, INT, REC|</xsl:if>
      <xsl:apply-templates mode="M1"/>
   </xsl:template>
   <xsl:template match="mt1:mt103/mt1:field53B" priority="3999" mode="M1">
      <xsl:if test="(//mt1:field23B/text() = 'SPRI' or //mt1:field23B/text() = 'SSTD' or //mt1:field23B/text() = 'SPAY') and not(starts-with(text(),'/'))">Party Identifier for Field 53B is required if Field 23B contains either SPRI, SSTD or SPAY (Error E04)|</xsl:if>
      <xsl:apply-templates mode="M1"/>
   </xsl:template>
   <xsl:template match="mt1:mt103" priority="3998" mode="M1">
      <xsl:choose>
         <xsl:when test="((//mt1:field55A or //mt1:field55B or //mt1:field55D) and (//mt1:field53A or //mt1:field53B or //mt1:field53D) and             (//mt1:field54A or //mt1:field54B or //mt1:field54D)) or not((//mt1:field55A or //mt1:field55B or //mt1:field55D))"/>
         <xsl:otherwise>Fields 53a and 54a must be present if Field 55a is present (Error E06)|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="not(//mt1:field23B) or not(contains(//mt1:field23B[1]/text(),'SPRI')) or (contains(//mt1:field23E[1]/text(),'TELB') or contains(//mt1:field23E[1]/text(),'SDVA') or contains(//mt1:field23E[1]/text(),'PHOB') or contains(//mt1:field23E[1]/text(),'INTC')) or not(//mt1:field23E)"/>
         <xsl:otherwise>Field23E must contain either SDVA,TELB or PHOB or INTC if Field23B contains SPRI|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="(//mt1:field70[1] and not(//mt1:field77T[1])) or             (not(//mt1:field70[1]) and //mt1:field77T[1]) or             (not(//mt1:field70[1]) and not(//mt1:field77T[1]))"/>
         <xsl:otherwise>Field 70 and Field 77T are mutually exclusive|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="((//mt1:field23B[1]/text() = 'SPRI' or //mt1:field23B[1]/text() = 'SSTD' or //mt1:field23B[1]/text() = 'SPAY') and not(//mt1:field53D)) or (//mt1:field23B[1]/text() != 'SPRI' and //mt1:field23B[1]/text() != 'SSTD' and //mt1:field23B[1]/text() != 'SPAY') or not(//mt1:field23B) "/>
         <xsl:otherwise>Field 53D must not be used if the value of Field 23B is either SPRI,SSTD or SPAY|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="((//mt1:field23B[1]/text() = 'SPRI' or //mt1:field23B[1]/text() = 'SSTD' or //mt1:field23B[1]/text() = 'SPAY') and not(//mt1:field54B) and not(//mt1:field54D)) or (//mt1:field23B[1]/text() != 'SPRI' or //mt1:field23B[1]/text() != 'SSTD' or //mt1:field23B[1]/text() != 'SPAY') or not(//mt1:field23B) "/>
         <xsl:otherwise>Only option A may be used for Field 54 if the value of Field 23B is equal to either SPRI,SSTD or SPAY (Error E05)|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="((//mt1:field23B[1]/text() = 'SPRI' or //mt1:field23B[1]/text() = 'SSTD' or //mt1:field23B[1]/text() = 'SPAY') and not(//mt1:field55B) and not(//mt1:field55D)) or (//mt1:field23B[1]/text() != 'SPRI' or //mt1:field23B[1]/text() != 'SSTD' or //mt1:field23B[1]/text() != 'SPAY') or not(//mt1:field23B)"/>
         <xsl:otherwise>Only option A may be used for Field 55 if the value of Field 23B is equal to either SPRI, SSTD or SPAY (Error E05)|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="((//mt1:field56A or //mt1:field56C or //mt1:field56D) and (//mt1:field57A or //mt1:field57B or //mt1:field57C or //mt1:field57D)) or not((//mt1:field56A or //mt1:field56C or //mt1:field56D))"/>
         <xsl:otherwise>Field 57a must be present if Field 56a is present (Error C81)|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="(contains(//mt1:field71A[1],'OUR') and not(//mt1:field71F)) or not(contains(//mt1:field71A[1],'OUR'))"/>
         <xsl:otherwise>Field 71F must not be present if Field 71A is equal to OUR|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="(contains(//mt1:field71A[1],'SHA') and not(//mt1:field71G)) or not(contains(//mt1:field71A[1],'SHA'))"/>
         <xsl:otherwise>Field 71G must not be present if Field 71A is equal to SHA|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="(contains(//mt1:field71A[1],'BEN') and not(//mt1:field71G)) or not(contains(//mt1:field71A[1],'BEN'))"/>
         <xsl:otherwise>Field 71G must not be present if Field 71A is equal to BEN|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="(contains(//mt1:field71A[1],'BEN') and //mt1:field71F[1]) or not(contains(//mt1:field71A[1],'BEN'))"/>
         <xsl:otherwise>At least one Field 71F must be present if Field 71A is equal to BEN|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="(//mt1:field33B and (//mt1:field71F or //mt1:field71G)) or             (not(//mt1:field71F) and not(//mt1:field71G))"/>
         <xsl:otherwise>Field 33B is mandatory if either 71F or 71G is present|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="(not(//mt1:field56A) and //mt1:field23E[1]/text() != 'TELI' and //mt1:field23E[1]/text() != 'PHOI') or //mt1:field56A or not(//mt1:field23E[1])"/>
         <xsl:otherwise>Field 23E must not equal to TELI or PHOI if Field 56A is not present|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="(not((//mt1:field57A or //mt1:field57B or //mt1:field57C or //mt1:field57D)) and //mt1:field23E[1]/text() != 'TELE' and //mt1:field23E[1]/text() != 'PHON') or (//mt1:field57A or //mt1:field57B or //mt1:field57C or //mt1:field57D) or not(//mt1:field23E[1])"/>
         <xsl:otherwise>Field 23E must not equal to TELE or PHON if Field 57a is not present (Error E45)|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="not(//mt1:field71G) or not(//mt1:field32A) or (substring(//mt1:field32A[1]/text(),7,3) = substring(//mt1:field71G[1]/text(),1,3))"/>
         <xsl:otherwise>The currency code for Fields 71G and 32A must be the same (Error C02)|</xsl:otherwise>
      </xsl:choose>
      <xsl:if test="contains(//mt1:field23E[1]/text(),'CHQB') and (starts-with(//mt1:field59A[1]/text(),'/') or starts-with(//mt1:field59[1]/text(),'/'))">Account Field in Field 59A is not allowed if Field 23E is equal to CHQB (Error E18)|</xsl:if>
      <xsl:choose>
         <xsl:when test="not(//mt1:field33B) or not(//mt1:field32A) or (substring(//mt1:field32A/text(),7,3) = substring(//mt1:field33B/text(),1,3) and not(//mt1:field36)) or (substring(//mt1:field32A/text(),7,3) != substring(//mt1:field33B/text(),1,3) and //mt1:field36)"/>
         <xsl:otherwise>Field 36 is required only if the currencies of Field 32A and Field 33B are different|</xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates mode="M1"/>
   </xsl:template>
   <xsl:template match="mt1:mt103/mt1:field23B" priority="3997" mode="M1">
      <xsl:choose>
         <xsl:when test="((//mt1:field23B/text() = 'SPRI' or              //mt1:field23B/text() = 'SSTD' or //mt1:field23B/text() = 'SPAY') and (//mt1:field59 or //mt1:field59A)) or //mt1:field23B/text() = 'CRED'"/>
         <xsl:otherwise>Account field of Field 59a is Mandatory if Field 23B is equal to SPRI, SSTD or SPAY (Error E10)|</xsl:otherwise>
      </xsl:choose>
      <xsl:if test="//mt1:field23B/text() = 'SPRI' and ((//mt1:field56A) or (//mt1:field56C) or (//mt1:field56D))">Field 56a must not be present if Field 23B is equal to SPRI (Error E16)|</xsl:if>
      <xsl:apply-templates mode="M1"/>
   </xsl:template>
   <xsl:template match="mt1:mt103Plus" priority="3996" mode="M1">
      <xsl:choose>
         <xsl:when test="not(//mt1:field33B) or not(//mt1:field32A) or (substring(//mt1:field32A/text(),7,3) = substring(//mt1:field33B/text(),1,3) and not(//mt1:field36)) or (substring(//mt1:field32A/text(),7,3) != substring(//mt1:field33B/text(),1,3) and //mt1:field36)"/>
         <xsl:otherwise>Field 36 is required only if the currencies of Field 32A and Field 33B are different (Error D75)|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="not(//mt1:field23B) or not(contains(//mt1:field23B[1]/text(),'SPRI')) or (contains(//mt1:field23E[1]/text(),'TELB') or contains(//mt1:field23E[1]/text(),'SDVA') or contains(//mt1:field23E[1]/text(),'PHOB') or contains(//mt1:field23E[1]/text(),'INTC')) or not(//mt1:field23E)"/>
         <xsl:otherwise>Field23E must contain either SDVA,INTC if Field23B contains SPRI (Error E01)|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="((//mt1:field55A or //mt1:field55B or //mt1:field55D) and (//mt1:field53A or //mt1:field53B or //mt1:field53D) and             (//mt1:field54A or //mt1:field54B or //mt1:field54D)) or not((//mt1:field55A or //mt1:field55B or //mt1:field55D))"/>
         <xsl:otherwise>Fields 53a and 54a must be present if Field 55a is present (Error E06)|</xsl:otherwise>
      </xsl:choose>
      <xsl:if test="//mt1:field23B/text() = 'SPRI' and ((//mt1:field56A) or (//mt1:field56C) or (//mt1:field56D))">Field 56A must not be present if Field 23B is equal to SPRI (Error E16)|</xsl:if>
      <xsl:choose>
         <xsl:when test="(contains(//mt1:field71A[1],'OUR') and not(//mt1:field71F)) or not(contains(//mt1:field71A[1],'OUR'))"/>
         <xsl:otherwise>Field 71F must not be present if Field 71A is equal to OUR (Error E13)|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="(contains(//mt1:field71A[1],'SHA') and not(//mt1:field71G)) or not(contains(//mt1:field71A[1],'SHA'))"/>
         <xsl:otherwise>Field 71G must not be present if Field 71A is equal to SHA (Error D50)|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="(contains(//mt1:field71A[1],'BEN') and //mt1:field71F[1]) or not(contains(//mt1:field71A[1],'BEN'))"/>
         <xsl:otherwise>At least one Field 71F must be present if Field 71A is equal to BEN (Error E15)|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="(//mt1:field33B and (//mt1:field71F or //mt1:field71G)) or             (not(//mt1:field71F) and not(//mt1:field71G))"/>
         <xsl:otherwise>Field 33B is mandatory if either 71F or 71G is present (Error D51)|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="not(//mt1:field71G) or not(//mt1:field32A) or (substring(//mt1:field32A[1]/text(),7,3) = substring(//mt1:field71G[1]/text(),1,3))"/>
         <xsl:otherwise>The currency code for Fields 71G and 32A must be the same (Error C02)|</xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates mode="M1"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M1"/>
   <xsl:template match="text()" priority="-1"/>
</xsl:stylesheet>