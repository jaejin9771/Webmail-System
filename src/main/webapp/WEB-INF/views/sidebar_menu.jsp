<%-- 
    Document   : sidebar_menu
    Created on : 2022. 6. 10., 오후 3:25:30
    Author     : skylo
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>웹메일 시스템 메뉴</title>
    </head>
    <body>
        <br> <br>
        
        <span style="color: indigo"> <strong>사용자: <%= session.getAttribute("username") %> </strong> </span> <br>

        <p> <a href="main_menu"> 메일 읽기 </a> </p>
        <p> <a href="write_mail"> 메일 쓰기 </a> </p>
        <p> <a href="sent_mail_list"> 보낸 메일 읽기 </a> </p>
        <p> <a href="addressbook"> 주소록 </a> </p>
        
        <p><a href="${pageContext.request.contextPath}/logout">로그아웃</a></p>
    </body>
</html>
