<%-- 
    Document   : main_menu
    Created on : 2022. 6. 10., 오후 3:15:45
    Author     : skylo
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>

<!-- 제어기에서 처리하면 로직 관련 소스 코드 제거 가능!
<jsp:useBean id="pop3" scope="page" class="deu.cse.spring_webmail.model.Pop3Agent" />
<%
    pop3.setHost((String) session.getAttribute("host"));
    pop3.setUserid((String) session.getAttribute("username"));
    pop3.setPassword((String) session.getAttribute("password"));
%>
-->

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>주메뉴 화면</title>
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
        <script>
            <c:if test="${!empty msg}">
            alert("${msg}");
            </c:if>
        </script>
    </head>
    <body>
        <%@include file="header.jspf"%>

        <div id="sidebar">
            <jsp:include page="sidebar_menu.jsp" />
        </div>

        <!-- 검색 영역 -->
        <div id="search-area" style="margin: 20px;">
            <form action="<c:url value='/main_menu'/>" method="get">
                <select name="searchType">
                    <option value="subject">제목</option>
                    <option value="from">보낸 사람</option>
                </select>
                <input type="text" name="keyword" required />
                <button type="submit">검색</button>
            </form>
        </div>
        
        <script>
            function confirmDelete(msgid) {
                if (confirm("정말로 이 메일을 삭제하시겠습니까?")) {
                    window.location.href = "delete_mail.do?msgid=" + msgid;
                }
            }
        </script>

        <!-- 메일 표시 영역 -->
        <div id="main">
            <c:out value="${messageList}" escapeXml="false" />
        </div>

        <%@include file="footer.jspf"%>
    </body>
</html>
