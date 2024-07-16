<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:sch="http://www.ascc.net/xml/schematron"
                xmlns:add="http://www.topologi.com/add"
                version="1.0"
                add:dummy-for-xmlns="">
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
        <xsl:apply-templates select="/" mode="M2"/>
    </xsl:template>
    <xsl:template match="/" priority="4000" mode="M1">
        <xsl:choose>
            <xsl:when test="add:add"/>
            <xsl:otherwise>The root element must be add.</xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates mode="M1"/>
    </xsl:template>
    <xsl:template match="add:add" priority="3999" mode="M1">
        <xsl:choose>
            <xsl:when test="@sum"/>
            <xsl:otherwise>The element add must have a sum attribute</xsl:otherwise>
        </xsl:choose>
        <xsl:choose>
            <xsl:when test="count(*) = count(add:item)"/>
            <xsl:otherwise>The element add can only have item child elements.</xsl:otherwise>
        </xsl:choose>
        <xsl:choose>
            <xsl:when test="count(add:item) &gt;= 1"/>
            <xsl:otherwise>The element add must have at least one item element.</xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates mode="M1"/>
    </xsl:template>
    <xsl:template match="add:item" priority="3998" mode="M1">
        <xsl:choose>
            <xsl:when test="number(.)"/>
            <xsl:otherwise>The content of the item element must be a number.</xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates mode="M1"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M1"/>
    <xsl:template match="add:add" priority="4000" mode="M2">
        <xsl:choose>
            <xsl:when test="@sum = sum(add:item)"/>
            <xsl:otherwise>The value of the sum attribute should be the sum of all the values in the item child elements.</xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates mode="M2"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M2"/>
    <xsl:template match="text()" priority="-1"/>
</xsl:stylesheet>