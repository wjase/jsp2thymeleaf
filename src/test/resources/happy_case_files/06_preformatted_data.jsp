<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri='http://java.sun.com/jstl/core' prefix='c' %> 

<h3>forEach - Form 1 (List iterator)</h3>
<pre>
&lt;c:forEach var="item" items="\${fn:splitTokens('red,green,blue')}" varStatus="status"&gt;
This is item \${item}. Status is \${status.index}&lt;br/&gt;
&lt;/c:forEach&gt;
                
</pre>
<c:forEach var="item" items="${fn:split('red,green,blue')}" varStatus="status">
    This is item ${item}. Status is ${status.index}<br/>
</c:forEach>
