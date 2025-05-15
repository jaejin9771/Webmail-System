<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>보낸 메일 보기</title>
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
    </head>
    <body>
        <%@include file="../header.jspf" %>

        <div id="sidebar">
            <jsp:include page="slidebar_sent_menu.jsp" /> 
        </div>

        <div id="msgBody">
            ${msg}
        </div>

        <%@include file="../footer.jspf" %>
    </body>
</html>
