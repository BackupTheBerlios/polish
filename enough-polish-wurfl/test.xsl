<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:transform version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="xml"
        indent="yes"
    	encoding="UTF-8"/>
   	
   	<!-- To disable standard output of text elements -->	
	<xsl:template match="text()|@*">
	</xsl:template>

<!--
	<xsl:template match="group[@id='display']">
	<xsl:variable name="width" select="capability[@name='resolution_width']/@value" />
	<xsl:variable name="height" select="capability[@name='resolution_height']/@value" />
	<device><capability name="ScreenSize" value="{$width}x{$height}" />
	</device>
	</xsl:template>
	
	<xsl:template match="/">
		<devices>
			<xsl:apply-templates />
		</devices>
	</xsl:template>
	-->
	
	<xsl:template match="device[not(@actual_device_root='true')]">
		<xsl:if test="count(child::*) &gt; 0">
		<xsl:copy-of select="."/>
		</xsl:if>
			
	</xsl:template>
	
</xsl:transform>