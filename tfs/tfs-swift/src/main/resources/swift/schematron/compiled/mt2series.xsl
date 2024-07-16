<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:sch="http://www.ascc.net/xml/schematron"
                xmlns:mt2="http://www.ucpb.com.ph/tfs/schemas/mt2series"
                version="2.0"
                mt2:dummy-for-xmlns="">
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
   <xsl:template match="mt2:mt202" priority="4000" mode="M1">
      <xsl:choose>
         <xsl:when test="((//mt2:field56A or //mt2:field56D) and (//mt2:field57A or //mt2:field57B or //mt2:field57D)) or not((//mt2:field56A or //mt2:field56D))"/>
         <xsl:otherwise>Field 57a must be present if Field 56a is present (Error C81)|</xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates mode="M1"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M1"/>
   <xsl:template match="text()" priority="-1"/>
</xsl:stylesheet>