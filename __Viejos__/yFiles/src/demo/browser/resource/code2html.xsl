<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                exclude-result-prefixes="y"
                version="1.0">

    <xsl:output method="html"
                indent="yes"
                omit-xml-declaration="yes" />

    <xsl:template match="/">
        <xsl:apply-templates />
    </xsl:template>

    <xsl:template match="code">
      <html>
        <head>
          <style type="text/css">
            <xsl:comment>
              body {
  font-family:Verdana, sans-serif; 
  margin-left:12px;
  margin-right:12px;
  margin-top:12px;
  margin-bottom:12px;
}

h1 { 
  padding-left:8px;
  padding-top:8px;
  padding-bottom:8px;
  background-color:#d0d0d0;
  color:#4079ea;
/*  color:#4d88ff; */
  font-size:18px;
  margin-bottom:5px;
}

p {   
  padding-bottom:0px;
  padding-top:0px;
  margin-top:3px;
  margin-bottom:0px;
/*  margin-left:10px; */
  margin-left:8px;
  font-size:12px;
  color:#000000;
}

h2 { 
  font-size:14px; 
  color:#4079ea; 
  margin-top:10px; 
  margin-bottom:0px; 
}

h3 { 
  font-size:13px; 
/*  margin-left:10px; */
  margin-left:8px;
  margin-top:8px; 
  margin-bottom:2px; 
}

table {
/*  margin-left:10px; */
  margin-left: 8px;
  margin-top: 8px;
  padding-top: 5px;
  padding-bottom: 5px;
  border: 1px solid white;
  vertical-align: baseline;
  border-collapse: collapse;
}

td { 
  font-size:12px;
  background-color: #E3EEFE;
  vertical-align:top;
  border: 1px solid white;
  vertical-align: baseline;
}

th {
  font-size:12px; 
  background-color:#a4b9ea;
  text-align: left;
}

a {
  color: #AA5522; font-weight:bold;
}
a:link {
  text-decoration:none;
}
a:visited {
  text-decoration:none; color:#772200;
}
a:active, 
a:focus, 
a:hover {
  text-decoration: underline;
}

div.li1 {
  font-size:12px;
  margin-top:8px; 
  margin-bottom:0px; 
  margin-left:10px; 
}
div.li2 { 
  margin-left:20px;
  margin-top:2px; 
  margin-bottom:2px; 
  font-weight:bold;
  font-size:12px; 
}

dl { 
  font-size:12px;
  margin-top:2px;
  margin-bottom:2px;
  margin-left:15px;
}
dt { 
  font-weight:bold;
  margin-top:4px;
/*  color:#000000; */
  color:#333366;
}
dd { 
  margin-left:15px;
}

li { 
  /* does not work in pre-1.5 Java JEditorPane :-( */
/*  list-style-type:none; */
  font-size:12px;
}

caption {
  font-size:12px;
}

.copyright { 
  font-size: 10px;
  font-style:italic;
  text-align:right;
  margin-top:20px;
}

code, tt { 
  font-family:monospace;
  font-size:12px;
  color:#0000C0;
}

            </xsl:comment>
          </style>
        </head>
        <body>
          <pre><xsl:apply-templates /></pre>
        </body>
      </html>
    </xsl:template>


    <xsl:template match="keyword">
      <font color="#a020f0"><xsl:value-of select="." /></font>
    </xsl:template>

    <xsl:template match="string">
      <font color="#bc8f8f"><xsl:value-of select="." /></font>
    </xsl:template>

    <xsl:template match="comment">
      <font color="#b22222"><xsl:value-of select="." /></font>
    </xsl:template>

</xsl:stylesheet>
