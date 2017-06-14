<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="salary" scope="session" value="${2000*2}"/>
<c:set var="salary" scope="page" value="${2000*2}"/>
<c:set var="salary" scope="application" value="${2000*2}"/>