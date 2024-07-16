<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:functions="http://www.incuventure.net/xslt/functions">

<!-- custom functions -->
<xsl:function name="functions:in-list">
    <xsl:param name="input"/>
    <xsl:param name="referenceList"/>
    <xsl:value-of select="contains(concat(',',$referenceList, ','),concat(',',$input, ','))"/>
</xsl:function>
</xsl:stylesheet>