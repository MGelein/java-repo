<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tei="http://www.tei-c.org/ns/1.0">
	<xsl:output version="1.0" indent="yes" encoding="UTF-8" method="html" />  
	
	<!-- Apply templates to text elements  -->
	<xsl:template match="/">
		<xsl:apply-templates select="tei:TEI/tei:text"/>

	</xsl:template>
	
	<!-- match each text and apply templates to it -->	
	<xsl:template match="tei:text"> 		
		<xsl:variable name="this" select="."/>
		<div class="textContainer">
			<div style="background-color:rgb(219,229,241);padding:5px;width:38%;border: 1px solid navy;margin-bottom:10px;">
				<table>
					<tr>
						<th style="text-align:left">Fragment</th><td><xsl:value-of select="@n"/></td>
					</tr>
					<tr>
						<th style="text-align:left">Author</th><td><xsl:value-of select="//tei:teiHeader//tei:author"/></td>
					</tr>
					<tr>
						<th style="text-align:left">Work</th><td><xsl:value-of select="//tei:teiHeader//tei:title"/><xsl:text> </xsl:text><xsl:value-of select="//tei:teiHeader//tei:locus"/></td>
					</tr>
					<tr>
						<th style="text-align:left">Date</th><td><xsl:value-of select="//tei:teiHeader//tei:origDate"/></td>
					</tr>
					<!-- 
                    @Mees(18-5-14) added a Place tag to the xml structure and edited the XSL to 
                    show it in the header div
                    -->
					<tr>
                        <th style="text-align:left">Place</th><td><xsl:value-of select="//tei:teiHeader//tei:origPlace"/></td>
                    </tr>
				</table>
			</div>
			
			<table>
				<tr>
					<td style="width:40%;">
						<h4>Text</h4>
						<xsl:apply-templates select="$this/tei:seg/tei:displaySegment"/>	
					</td>
					
					<td style="width:40%;">
						<!-- @Mees (3-6-14) also now shows the language and author of the translation with n=0, in a lighter gray-->
						<h4>Translation - <span style="font-size:8px; color:#777777;"><xsl:value-of select="$this/tei:seg/tei:translation[@n='0']/@lang"/> (<xsl:value-of select="$this/tei:seg/tei:translation[@n='0']/tei:translationDesc/tei:author"/>)</span></h4>
						<!-- @Mees (3-6-14) only a translation with a n-attribute of 0 will be displayed. The n attributes is used
								as a priority number. Lower means better. 0 is the most important translation
							-->
						<xsl:apply-templates select="$this/tei:seg/tei:translation[@n='0']"/>
					</td>
					
					<td rowspan="2">
					
						<h4>Keywords</h4>
						<ul>
							<xsl:apply-templates select="$this//tei:keywords/tei:term[not(.=preceding::tei:term[ancestor::tei:text[@n=$this/@n]])]"/>
						</ul>
						
						<h4>Tags</h4>
						<b><i>Patronymic:</i></b><xsl:text> </xsl:text> 
						<xsl:for-each select="$this//tei:addName[@type='patronymic']">
							<xsl:call-template name="relationItem">
								<xsl:with-param name="thisNode" select="."/>
								<xsl:with-param name="relation">son of</xsl:with-param>
							</xsl:call-template>
						</xsl:for-each>
						<br/><br/>
						<b><i>Origin:</i></b><xsl:text> </xsl:text> 
						<xsl:for-each select="$this//tei:addName[@type='origin']">
							<xsl:call-template name="relationItem">
								<xsl:with-param name="thisNode" select="."/>
								<xsl:with-param name="relation">of</xsl:with-param>
							</xsl:call-template>
						</xsl:for-each>

					</td>
				</tr>
				<tr>
					<td>
						<h4>Apparatus Fontium</h4>
						<ol>
							<xsl:for-each select="$this//tei:note[@type='appFont']">
								<xsl:call-template name="propList">
									<xsl:with-param name="thisNode" select="."/>
								</xsl:call-template>
							</xsl:for-each>
						</ol>
					</td>
					<td>
						<h4>Annotation</h4>
						<ol>
							<xsl:for-each select="$this//tei:note[@type='annotation']">
								<xsl:call-template name="propList">
									<xsl:with-param name="thisNode" select="."/>
								</xsl:call-template>
							</xsl:for-each>
						</ol>
					</td>
				</tr>
				<tr>
					<td>
						<h4>Apparatus Criticus</h4>
						<ol type="I">
							<xsl:for-each select="$this//tei:note[@type='appCrit']">
								<xsl:call-template name="propList">
									<xsl:with-param name="thisNode" select="."/>
								</xsl:call-template>
							</xsl:for-each>
						</ol>
					</td>

				</tr>
			
			</table>
		</div>
		<hr/>
	</xsl:template>
	
	
	<!-- 
	TEMPLATE MATCH tei:lb
	When an lb element is encountered the line should begin with its n property i.e. "1. Line of text"
	-->
	<xsl:template match="tei:lb">
	
		<xsl:variable name="this" select="ancestor::tei:text/@n"/>
		
		<!-- when the current lb has preceding lb's output an HTML line-break -->	
		<xsl:if test="not(preceding::tei:lb[ancestor::tei:text[@n=$this/@n]])">
			<br></br>
		</xsl:if>
		
		<!-- Output the line number folowed by a whitespace -->
		<xsl:value-of select="@n"/><xsl:text> </xsl:text>
		
		<!-- Following the lb element will be the actual line contents, we do not need to output them here -->
	</xsl:template>



	<!-- 
	TEMPLATE MATCH tei:translation
	Output a line break after each section of "tei:translation", this is to make sure the Title of the text ends up on it's own separate line
	-->
	<xsl:template match="tei:translation">
		<xsl:apply-templates/>
		<br/>
	</xsl:template>
   	
	<!--
	@Mees Made by JJ, to prevent the author of the translation from being output
	-->	
	<xsl:template match="tei:translationDesc/tei:author"/>
	
	<!--
	@Mees Dont output anything if a translationDesc element is found
	-->
	<xsl:template match="//tei:translationDesc"/>

	<!-- 
	TEMPLATE MATCH tei:note
	Matches all notes embedded in text, places superscripted identifiers in the place of the note tags. 
	In a separate function the note contents are gathered and output.
	-->	
	<xsl:template match="tei:note">
		<sup>
		<!-- 
		@Mees(17-5-14) changed some stuff over here to make the notes a little less obtrusive.
		They're now smaller and to compensate a little red. To do this I added a <span> tag
		-->
		  <span style="font-size:8px; color:#880000;">
			<b>
				<xsl:choose>
					<xsl:when test="@type='appCrit'">
						<xsl:value-of select="@n"/>			
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="@n"/>
					</xsl:otherwise>
				</xsl:choose>
			</b>
		  </span>
		</sup>
		<xsl:text> </xsl:text>
	</xsl:template>

	<!-- 
	TEMPLATE NAME "proplist" and MATCH tei:name | tei:keywords/tei:term
	Universal lisitem generating template. Does nothing more then output a li element with the value attribute from @n 
	and the contents of the node as contents
	-->
	<xsl:template name="propList" match="tei:name | tei:keywords/tei:term">
		<xsl:param name="thisNode" select="."/>
		<li>
			<xsl:attribute name="value"><xsl:value-of select="$thisNode/@n"/></xsl:attribute>
			<xsl:value-of select="$thisNode"/>
		</li>
	</xsl:template>
	
	<!-- 
	TEMPLATE MATCH tei:name | tei:emph
	Outputs emphasized text when and emph tag is found.
	-->	
	<xsl:template match="tei:emph">
		<em>
			<xsl:value-of select="."/>
		</em>
	</xsl:template>

	<!-- 
	TEMPLATE name "relationItem"
	Outputs a text version of a relation found in a tei:addName node
	-->	
	<xsl:template name="relationItem">
		<xsl:param name="thisNode"/>
		<xsl:param name="relation"/>
		<xsl:value-of select="$thisNode/@nymRef"/><xsl:text> </xsl:text><xsl:value-of select="$relation"/><xsl:text> </xsl:text><xsl:value-of select="."/><xsl:text>; </xsl:text>
	</xsl:template>
	
</xsl:stylesheet>