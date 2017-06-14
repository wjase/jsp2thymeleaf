<%@ taglib uri='http://java.sun.com/jstl/core' prefix='c' %> 
<h3>forTokens</h3>
<pre>
&lt;c:forTokens var="item" delims="," items="fat,cat,sat,mat"&gt;
This is item \${item}&lt;br/&gt;
&lt;/c:forTokens&gt;
</pre>
<c:forTokens var="item" delims="," items="fat,cat,sat,mat">
    This is item ${item}<br/>
</c:forTokens>
