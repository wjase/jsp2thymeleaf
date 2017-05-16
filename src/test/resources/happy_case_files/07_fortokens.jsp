<%@ taglib uri='http://java.sun.com/jstl/core' prefix='c' %> 
<h3>fortokens</h3>
<pre>
&lt;c:fortokens var="item" delims="," items="fat,cat,sat,mat"&gt;
This is item \${item}&lt;br/&gt;
&lt;/c:fortokens&gt;
</pre>
<c:fortokens var="item" delims="," items="fat,cat,sat,mat">
    This is item ${item}<br/>
</c:fortokens>
