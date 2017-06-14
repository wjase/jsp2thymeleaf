<%@ taglib uri='http://java.sun.com/jstl/core' prefix='c' %> 
<c:forEach var="anItem" items="${itemlist}" varStatus='statusVar'>
    First Form here
</c:forEach>
<c:forEach var="anItem" items="${itemlist}">
    Second Form here
</c:forEach>
<c:forEach var="anotherItem" begin="1" end='10' step='2'>
    Third Form here
</c:forEach>
<c:forEach var="anotherItem" begin="1" end='10'>
    Fourth Form here
</c:forEach>
<c:forEach var="anotherItem" begin="1" end='10' step='2' varStatus='statusVar'>
    Fifth Form here
</c:forEach>
    
