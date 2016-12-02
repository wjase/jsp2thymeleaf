<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
    <head>
        <title><c:choose> Tag Example</title>
    </head>
    <body>
        <c:set var="salary" scope="session" value="${2000*2}"/>
        <p>Your salary is : <c:out value="${salary}"/></p>
        <c:choose>
            <c:when test="${salary <= 0}">
                Salary is very low to survive.
            </c:when>
            <c:when test="${salary > 1000}">
                Salary is very good.
            </c:when>
            <c:otherwise>
                No comment sir...
            </c:otherwise>
        </c:choose>
    </body>
</html>