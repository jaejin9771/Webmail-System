<%-- 
    Document   : db_error.jsp
    Author     : jiye
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
    <head>
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
        <title>DB 연결 오류</title>
        <style>
            #main {
                text-align: center;
            }
        </style>
    </head>
    <body>
        <%@ include file="../header.jspf" %>
        <div id="sidebar">
            <jsp:include page="../sidebar_previous_menu.jsp" />
        </div>
        <div id="main">       
            <h2 style="color: red;">데이터베이스 오류</h2>
            <p>현재 서버와의 연결에 문제가 발생했습니다.<br>
               잠시 후 다시 시도해 주세요.</p>
        </div>

        <%@ include file="../footer.jspf" %>
    </body>
</html>