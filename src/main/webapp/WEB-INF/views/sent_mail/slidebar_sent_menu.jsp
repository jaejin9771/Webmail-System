<%-- 
    Document   : sidebar_adduser_menu
    Author     : jongmin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <br> <br> 

        <span style="color: indigo">
            <strong>사용자: <%= session.getAttribute("username") %> </strong>
        </span> <br> <br>
        
        <p><a href="sent_mail_list"> 이전 메뉴로 </a></p>
    </body>
</html>
