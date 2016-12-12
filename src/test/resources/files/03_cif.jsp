<%@ taglib uri='http://java.sun.com/jstl/core' prefix='c' %> 
<%@ taglib uri='http://java.sun.com/jstl/functions' prefix='fn' %> 
<c:if test='${!param.amount}' id='barry'> 
    Some text here
</c:if>
<c:if test="${fn:contains(name, searchString)}">Boo</c:if>
