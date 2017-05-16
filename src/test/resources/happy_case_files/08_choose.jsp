<%@ taglib uri='http://java.sun.com/jstl/core' prefix='c' %> 
<h3>Choose/When</h3>
<pre>
&lt;c:choose&gt;
    &lt;c:when test="\${false}"&gt;Should not contain this as the test is false.&lt;/c:when&gt;
    &lt;c:when test="\${true}"&gt;Should contain this as the test is true.&lt;/c:when&gt;
    &lt;c:otherwise&gt;Shouldn't contain this as the previous condition matched&lt;/c:otherwise&gt;
&lt;/c:choose&gt;
</pre>
<c:choose>
    <c:when test="${false}">Should not contain this as the test is false.</c:when>
    <c:when test="${true}">Should contain this as the test is true.</c:when>
    <c:otherwise>Shouldn't contain this as the previous condition matched</c:otherwise>
</c:choose>
