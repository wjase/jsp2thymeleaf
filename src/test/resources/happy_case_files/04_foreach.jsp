<%@ taglib uri='http://java.sun.com/jstl/core' prefix='c' %> 
<c:foreach var="anItem" items="${itemlist}" varStatus='statusVar'>
    First Form here
</c:foreach>
<c:foreach var="anItem" items="${itemlist}">
    Second Form here
</c:foreach>
<c:foreach var="anotherItem" begin="1" end='10' step='2'>
    Third Form here
</c:foreach>
<c:foreach var="anotherItem" begin="1" end='10'>
    Fourth Form here
</c:foreach>
<c:foreach var="anotherItem" begin="1" end='10' step='2' varStatus='statusVar'>
    Fifth Form here
</c:foreach>
    
