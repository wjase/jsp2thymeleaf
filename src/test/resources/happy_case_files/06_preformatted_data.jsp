<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri='http://java.sun.com/jstl/core' prefix='c' %> 

<h3>foreach - Form 1 (List iterator)</h3>
<pre>
&lt;c:foreach var="item" items="\${fn:splitTokens('red,green,blue')}" varStatus="status"&gt;
This is item \${item}. Status is \${status.index}&lt;br/&gt;
&lt;/c:foreach&gt;
                
</pre>
<c:foreach var="item" items="${fn:split('red,green,blue')}" varStatus="status">
    This is item ${item}. Status is ${status.index}<br/>
</c:foreach>
