<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<script>
 function doSomething(){
     var someVar="<c:out value="${someVar}"/>";
     var someOtherVar = ["${valueHere}"];
 }
</script>