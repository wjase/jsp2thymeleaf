<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
Form1:<br/>
<c:url value="/file1.jsp" var="url"/>

Form2:<br/>
<c:url value="/file1.jsp"/>

Form3:<br/>
<c:url value="/file1.jsp" var="url" context="barry"/>

Form4:<br/>
<c:url value="/file1.jsp" var="url" scope="session"/>

Form5:<br/>
<a href="<c:url value="/file1.jsp"/>">TEST</a>

