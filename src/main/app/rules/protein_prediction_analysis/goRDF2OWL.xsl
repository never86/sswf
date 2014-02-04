<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#" xmlns:owl="http://www.w3.org/2002/07/owl#"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:go="http://www.geneontology.org/dtds/go.dtd#"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
	<xsl:output method="xml" indent="yes" />
	<xsl:template match="/">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="rdf:RDF">
		<rdf:RDF xmlns="http://purl.org/obo/owl/" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
			xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#" xmlns:owl="http://www.w3.org/2002/07/owl#"
			xmlns:xsd="http://www.w3.org/2001/XMLSchema#">
			<xsl:apply-templates />
		</rdf:RDF>
	</xsl:template>

	<xsl:template match="go:term">
		<owl:Class>
			<xsl:apply-templates select="@*|*" />
		</owl:Class>
	</xsl:template>

	<xsl:template match="go:name">
		<rdfs:label>
			<xsl:value-of select="." />
		</rdfs:label>
	</xsl:template>

	<xsl:template match="go:is_a">
		<rdfs:subClassOf>
			<xsl:apply-templates select="@*|*" />
		</rdfs:subClassOf>
	</xsl:template>


	<xsl:template match="text()"></xsl:template>

	<xsl:template match="@rdf:about">
		<xsl:attribute name="rdf:about">
            <xsl:value-of select="." />
        </xsl:attribute>
	</xsl:template>
	
	<xsl:template match="@focus">
	</xsl:template>

	<xsl:template match="@rdf:resource">
		<xsl:attribute name="rdf:resource">
            <xsl:value-of select="." />
        </xsl:attribute>
	</xsl:template>

</xsl:stylesheet>
