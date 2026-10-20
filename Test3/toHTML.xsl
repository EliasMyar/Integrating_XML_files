<?xml version="1.0" encoding="UTF-8"?>

<html xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xsl:version="1.0">
    
    
<head>
    <title>Scholar</title>
    <link rel="stylesheet" type="text/css" href="./src//Test3//style.css"/> 
</head>

<body>	
      <table class="table">
      
      <h1>Investigators</h1>
	<thead>
	        <tr>
	            <th>Investigator</th>
	            <th>Host Institution</th>
	            <th>Total Citations</th>
	            <th>Citations of the current year</th>
	            <th>Interests</th>
	            <th>Articles</th>
	            
	        </tr>
       </thead>
	
      
       <xsl:for-each select="//scholar/investigator">    	
       
       <tbody>       
       
       <tr>
	       <td><xsl:value-of select="//scholar/authors/author[@id = current()/@authorRef]"/></td>
	       <td><xsl:value-of select="host_institution"/></td>
	       <td><xsl:value-of select="total_citations"/></td>
	       <td><xsl:value-of select="citations_current_year"/></td>
	       <td>
	       <xsl:for-each select="interest">
	       
	       <xsl:value-of select="//scholar/interests/interest[@id = current()]"/>
	       <xsl:if test="position() != last()">, </xsl:if>
	       </xsl:for-each>
	       
	       </td>
	       <td>   
	       
		       <table class="table">
		       	
		       	<thead>
		       		<td>Title</td>
		       		<td>Date</td>
		       		<td>Journal</td>
		       		<td>Publisher</td>
		       		<td>Citations</td>
		       		<td>Authours</td>
		       	</thead>
		       	
		       	<tbody>		       	  
		       	 	<xsl:for-each select="article">
		       	 	<tr>
		       	 		<td><xsl:value-of select="title"/></td>
		       	 		<td><xsl:value-of select="date"/></td>
		       	 		<td><xsl:value-of select="journal"/></td>
		       	 		<td><xsl:value-of select="publisher"/></td>
		       	 		<td><xsl:value-of select="article_citations"/></td>
		       	 		<td><xsl:for-each select="author"> 
		       	 		
		       	 		<xsl:value-of select="//scholar/authors/author[@id = current()]"/>
		       	 		<xsl:if test="position() != last()">, </xsl:if>
		       	 		
		       	 		</xsl:for-each>
		       	 		
		       	 		</td>
		       	 		
		       	 	
		       	 	</tr>
		       	 </xsl:for-each>
		       	</tbody>
		       </table>
	       </td>
       </tr>
       
       </tbody>
	
	</xsl:for-each>
	</table>
       
       
       
       
    <h1>Statistics</h1>

    <div class="statistics">
        <p class="stat">
            Total number of researchers:
            <xsl:value-of select="//scholar/statistics/total_researchers"/>
        </p>

        <p class="stat">
            Total number of venues:
            <xsl:value-of select="//scholar/statistics/total_venues"/>
        </p>

        <p class="stat">
            Total number of citations:
            <xsl:value-of select="//scholar/statistics/total_citations"/>
        </p>

        <p class="stat">
            The most cited investigator of the current year:
            <xsl:value-of select="//scholar/statistics/mostCitedInvestigatorCurrentYear"/>
        </p>

        <p class="stat">
            Total number of articles:
            <xsl:value-of select="//scholar/statistics/totalArticles"/>
        </p>
    </div>

    <h3>Top Publications</h3>

    <table class="table">
        <thead>
            <tr>
                <th>Title</th>
                <th>Citations</th>
            </tr>
        </thead>

        <tbody>
            <xsl:for-each
                select="//scholar/statistics/top_publication">
                <tr>
                    <td><xsl:value-of select="title"/></td>
                    <td class="citations"> <xsl:value-of select="article_citations"/> </td>
                </tr>
            </xsl:for-each>
        </tbody>
    </table>
</body>

    
    
</html>
