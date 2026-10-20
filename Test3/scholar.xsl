<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="html" encoding="UTF-8" indent="yes"/>

    <!-- Index every author by its id: key('authorById', 'a1') returns that author.
         This is what resolves the IDREF / IDREFS values (authorRef, authorRefs). -->
    <xsl:key name="authorById" match="scholar/authors/author" use="@id"/>

    <!-- Prints the name of ONE author, given its id.
         Works whether the name is stored as an attribute (<author name="...">)
         or as a child element (<author><name>...</name></author>).
         If the id does not exist, the raw id is printed so the problem is visible. -->
    <xsl:template name="authorName">
        <xsl:param name="id"/>
        <xsl:variable name="author" select="key('authorById', $id)"/>
        <xsl:choose>
            <xsl:when test="$author">
                <xsl:value-of select="$author/@name | $author/name"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$id"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- Receives a space-separated list of ids (IDREFS) and prints
         the author names separated by ", ", keeping the order of the list. -->
    <xsl:template name="getAuthors">
        <xsl:param name="ids"/>

        <xsl:variable name="list" select="normalize-space($ids)"/>

        <xsl:if test="$list != ''">
            <xsl:variable name="firstID"
                          select="substring-before(concat($list, ' '), ' ')"/>
            <xsl:variable name="remainingIDs"
                          select="substring-after($list, ' ')"/>

            <xsl:call-template name="authorName">
                <xsl:with-param name="id" select="$firstID"/>
            </xsl:call-template>

            <xsl:if test="$remainingIDs != ''">
                <xsl:text>, </xsl:text>
                <xsl:call-template name="getAuthors">
                    <xsl:with-param name="ids" select="$remainingIDs"/>
                </xsl:call-template>
            </xsl:if>
        </xsl:if>
    </xsl:template>

    <!-- Page -->
    <xsl:template match="/">
        <html>
        <head>
            <meta charset="UTF-8"/>
            <title>Scholar</title>
            <link rel="stylesheet" type="text/css" href="./src/Test2/style.css"/>
        </head>
        <body>

            <h1>Investigators</h1>

            <table class="table">
                <thead>
                    <tr>
                        <th>Investigator</th>
                        <th>Host Institution</th>
                        <th>Total Citations</th>
                        <th>Citations of the current year</th>
                        <th>Articles</th>
                    </tr>
                </thead>
                <tbody>
                    <xsl:for-each select="scholar/investigators/investigator">
                        <tr>
                            <td>
                                <!-- authorRef holds ONE id (IDREF) -->
                                <xsl:call-template name="authorName">
                                    <xsl:with-param name="id" select="@authorRef"/>
                                </xsl:call-template>
                            </td>
                            <td><xsl:value-of select="host_institution"/></td>
                            <td><xsl:value-of select="total_citations"/></td>
                            <td><xsl:value-of select="citations_current_year"/></td>
                            <td>
                                <table class="table">
                                    <thead>
                                        <tr>
                                            <th>Title</th>
                                            <th>Date</th>
                                            <th>Journal</th>
                                            <th>Publisher</th>
                                            <th>Citations</th>
                                            <th>Authors</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <xsl:for-each select="article">
                                            <tr>
                                                <td><xsl:value-of select="title"/></td>
                                                <td><xsl:value-of select="date"/></td>
                                                <td><xsl:value-of select="journal"/></td>
                                                <td><xsl:value-of select="publisher"/></td>
                                                <td><xsl:value-of select="article_citations"/></td>
                                                <td>
                                                    <!-- authorRefs holds SEVERAL ids (IDREFS) -->
                                                    <xsl:call-template name="getAuthors">
                                                        <xsl:with-param name="ids" select="@authorRefs"/>
                                                    </xsl:call-template>
                                                </td>
                                            </tr>
                                        </xsl:for-each>
                                    </tbody>
                                </table>
                            </td>
                        </tr>
                    </xsl:for-each>
                </tbody>
            </table>

            <h1>Statistics</h1>

            <div class="statistics">
                <p class="stat">
                    Total number of researchers:
                    <xsl:value-of select="scholar/statistics/total_researchers"/>
                </p>
                <p class="stat">
                    Total number of venues:
                    <xsl:value-of select="scholar/statistics/total_venues"/>
                </p>
                <p class="stat">
                    Total number of citations:
                    <xsl:value-of select="scholar/statistics/total_citations"/>
                </p>
                <p class="stat">
                    The most cited investigator of the current year:
                    <xsl:value-of select="//scholar/statistics/mostCitedInvestigatorCurrentYear"/>
                </p>
                <p class="stat">
                    Total number of articles:
                    <xsl:value-of select="scholar/statistics/totalArticles"/>
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
                    <xsl:for-each select="scholar/statistics/top_publication">
                        <tr>
                            <td><xsl:value-of select="title"/></td>
                            <td class="citations"><xsl:value-of select="article_citations"/></td>
                        </tr>
                    </xsl:for-each>
                </tbody>
            </table>

        </body>
        </html>
    </xsl:template>

</xsl:stylesheet>
