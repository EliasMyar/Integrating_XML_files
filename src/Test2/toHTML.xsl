<?xml version="1.0" encoding="UTF-8"?>
<html xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xsl:version="1.0">
    
    
<head>
    <title>Scholar</title>
    <link rel="stylesheet" type="text/css" href="./src//Test2//style.css"/>
</head>

<body>
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

    <table class="publications">
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
                    <td>
                        <xsl:value-of select="title"/>
                    </td>
                    <td class="citations">
                        <xsl:value-of select="article_citations"/>
                    </td>
                </tr>
            </xsl:for-each>
        </tbody>
    </table>
</body>

    
    
</html>
