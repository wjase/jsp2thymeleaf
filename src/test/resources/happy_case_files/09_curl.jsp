<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:url var="someVar" value="/root/path/here"/>

<a href="<c:url value="/embedded/path" />">Some Link</a>
<a href="<c:url value="/embedded/path" ><c:param name="somekey" value="${value}"/><c:param name="somekey2" value="literal"/></c:url>">Some Link</a>