<%-- 
    Document   : sent_mail_list
    Created on : 2025. 5. 12., 오전 5:03:18
    Author     : user
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>보낸 메일함</title>
    <link type="text/css" rel="stylesheet" href="css/main_style.css" />
    <script>
        <c:if test="${!empty msg}">
            alert("${msg}");
        </c:if>
    </script>
</head>
<body>
    <%@include file="../header.jspf" %>

    <div id="sidebar">
        <jsp:include page="../sidebar_menu.jsp" />
    </div>

    <div id="main">
        <h2>보낸 메일함</h2>

        ${messageList}
    </div>

    <%@include file="../footer.jspf" %>
</body>
</html>

