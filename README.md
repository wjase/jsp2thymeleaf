
#Jsp2Thymeleaf - an extensible JSP to Thmeleaf converter

This project is intended to automate over 95% of jsp to thymeleaf conversion

It features:

 i) A great JSP parser thanks to the excellent jsp2jspx project which I uncovered on
 an archeological SourceForge dig and resurrected from Subversion.

 ii) A converter framework allowing you to add converters for your own 
 tag libraries in java, groovy or python.

 iii) A configurable maven plugin to automate the conversion of pages either
 entirely or more sensibly, page by page or fragment by fragment. Used in
 conjunction with the com.cybernostics:spring-thymeleaf-jsp library (which allows
 jsp pages to work alongside thymeleaf, even including thymeleaf fragments), you can
 run and test your project at any stage in the conversion.

#Running it

  i) From the command line:
 java -jar Jsp2Thymeleaf [src_files|*.jsp] [destpath|.] --taglibs=[pathlist] --urimap=[path]
 
 where:
  destpath - is the path where the converted files will end up
  taglibs - a list of taglib converters, either *.groovy, or *.py.
  urimap - is a map of any custom uri's you use to select taglibs from your jsps   

  ii) From the maven plugin
  Include the com.cybernostics:maven-jsp2thymeleaf plugin:

        <plugin>
            <artifactId>maven-jsp2thymeleaf</artifactId>
            <groupId>com.cybernostics</groupId>
            <configuration>
                <src></src>
                <dest>${build.output.path}</dest>
                <includes>
                   <include>MyPage.jsp</include>
                </includes>
                <excludes>
                   <exclude>MyOtherPage.jsp</exclude>
                </excludes>
            </configuration>
        </plugin>

#JSP coverage


#JSTL coverage

+-----+---------------------------+
|Done?| Tag                       |
+-----+---------------------------+
| No  | c:catch                   |
| No  | c:choose                  |
| Yes | c:forEach                 |
| No  | c:forTokens               |
| Yes | c:if                      |
| No  | c:import                  |
| No  | c:otherwise               |
| Yes | c:out                     |
| No  | c:param                   |
| No  | c:redirect                |
| No  | c:remove                  |
| No  | c:set                     |
| No  | c:url                     |
| No  | c:when                    |
| No  | fmt:bundle                |
| No  | fmt:formatDate            |
| No  | fmt:formatNumber          |
| No  | fmt:message               |
| No  | fmt:param                 |
| No  | fmt:parseDate             |
| No  | fmt:parseNumber           |
| No  | fmt:requestEncoding       |
| No  | fmt:setBundle             |
| No  | fmt:setLocale             |
| No  | fmt:setTimeZone           |
| No  | fmt:timeZone              |
| No  | fn:contains()             |
| No  | fn:containsIgnoreCase()   |
| No  | fn:endsWith()             |
| No  | fn:escapeXml()            |
| No  | fn:indexOf()              |
| No  | fn:join()                 |
| No  | fn:length()               |
| No  | fn:replace()              |
| No  | fn:split()                |
| No  | fn:startsWith()           |
| No  | fn:substring()            |
| No  | fn:substringAfter()       |
| No  | fn:substringBefore()      |
| No  | fn:toLowerCase()          |
| No  | fn:toUpperCase()          |
| No  | fn:trim()                 |
| No  | sql:dateParam             |
| No  | sql:param                 |
| No  | sql:query                 |
| No  | sql:setDataSource         |
| No  | sql:transaction           |
| No  | sql:update                |
| No  | x:choose                  |
| No  | x:forEach                 |
| No  | x:if                      |
| No  | x:otherwise               |
| No  | x:out                     |
| No  | x:param                   |
| No  | x:parse                   |
| No  | x:set                     |
| No  | x:transform               |
| No  | x:when                    |
+-----+---------------------------+