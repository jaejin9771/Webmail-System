<%-- 
    Document   : index
    Created on : 2022. 6. 10., 오후 2:19:43
    Author     : skylo
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>로그인 화면</title>
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
    </head>
    <body>
        <%@include file="header.jspf"%>


        <div id="login_form">
            <form method="POST" action="${pageContext.request.contextPath}/login">
                사용자: <input type="text" name="username" size="20" autofocus> <br />
                암&nbsp;&nbsp;&nbsp;호: <input type="password" name="password" size="20"> <br /> <br />
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                <input type="submit" value="로그인" name="B1">&nbsp;&nbsp;&nbsp;
                <input type="reset" value="다시 입력" name="B2">
            </form>
        </div>


        <%@include file="footer.jspf"%>
    </body>
</html>
