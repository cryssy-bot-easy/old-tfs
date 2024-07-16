<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:sch="http://www.ascc.net/xml/schematron"
                xmlns:mt4="http://www.ucpb.com.ph/tfs/schemas/mt4series"
                version="2.0"
                mt4:dummy-for-xmlns="">
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
   <xsl:template match="mt4:mt400" priority="4000" mode="M1">
      <xsl:if test="(//mt4:field57A or //mt4:field57D) and not((//mt4:field53A or //mt4:field53B or //mt4:field53D) and (//mt4:field54A or //mt4:field54B or //mt4:field54D))">Field 57a may only be present when fields 53a and 54a are both present (Error C11)|</xsl:if>
      <xsl:choose>
         <xsl:when test="substring(//mt4:field32A/text(),7,3) = substring(//mt4:field33A/text(),7,3)    or substring(//mt4:field32B/text(),1,3) = substring(//mt4:field33A/text(),7,3)    or substring(//mt4:field32K/text(),7,3) = substring(//mt4:field33A/text(),7,3)"/>
         <xsl:otherwise>The currency code for Fields 32a and 33A must be the same (Error C02)|</xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates mode="M1"/>
   </xsl:template>
   <xsl:template match="mt4:mt410 | mt4:mt412" priority="3999" mode="M1">
      <xsl:choose>
         <xsl:when test="count(//mt4:field20) &lt; 11"/>
         <xsl:otherwise>Error Code T10:Field 20 may not appear more than ten times.|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="count(//mt4:field21) &lt; 11"/>
         <xsl:otherwise>Error Code T10:Field 21 may not appear more than ten times.|</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
         <xsl:when test="count(//mt4:field32A) &lt; 11"/>
         <xsl:otherwise>Error Code T10:Field 32A may not appear more than ten times.|</xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates mode="M1"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M1"/>
   <xsl:template match="text()" priority="-1"/>
</xsl:stylesheet>