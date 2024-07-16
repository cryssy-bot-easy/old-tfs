<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:mt="http://www.ucpb.com.ph/tfs/schemas/swift-message"
                xmlns:mt1="http://www.ucpb.com.ph/tfs/schemas/mt1series"
                mt1:dummy-for-xmlns=""
                exclude-result-prefixes="xsl">

    <!--<xsl:import href="identity.xsl"/>-->
    <xsl:output method="xml" version="1.0" encoding="UTF-8"
                indent="yes" />

    <xsl:param name="root-ns" select="namespace-uri(/*)"/>

    <!-- identity function -->
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <!-- remove user header tag. -->
    <xsl:template match="mt:user_header">
    </xsl:template>

    <!-- remove message block tag. TODO: fix jaxb configuration so that this tag is not created during marshalling. -->
    <xsl:template match="mt:message_block">
    </xsl:template>

    <!-- remove the trailer tag. TODO: fix jaxb configuration so that this tag is not created during marshalling. -->
    <xsl:template match="mt:trailer">
    </xsl:template>


    <xsl:template match="mt:tags">
        <xsl:for-each select="mt:tag">
            <xsl:element name="field{tagName}" namespace="{$root-ns}">
                <!--<xsl:namespace name="ns" select="$root-ns"/>-->
                <xsl:value-of select="value"/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>
    
    
    

</xsl:stylesheet>