<%@ page import="com.google.gwt.http.client.RequestException" %>
<%--
  Created by IntelliJ IDEA.
  User: thomas
  Date: 04/08/2017
  Time: 17:09
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Throwable throwable = (Throwable)
            request.getAttribute("javax.servlet.error.exception");
    // if a request exception forward the exception
    if(throwable instanceof RequestException) {
        request.getRequestDispatcher(request.getRequestURL().toString()).forward(request, response);
    }
%>
<<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">

    <title>Page Error</title>

</head>

<body>

Error with webpage

</body>
</html>
