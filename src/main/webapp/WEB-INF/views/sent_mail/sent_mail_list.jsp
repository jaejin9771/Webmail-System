
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

    <!-- 검색 영역 -->
    <div id="search-area" style="margin: 20px;">
        <form action="sent_mail_list" method="get">
            <select name="searchType">
                <option value="subject">제목</option>
                <option value="to">받는 사람</option>
            </select>
            <input type="text" name="keyword" required />
            <button type="submit">검색</button>
        </form>
    </div>
    
    <script>
        function confirmDelete(msgid) {
            if (confirm("정말로 이 메일을 삭제하시겠습니까?")) {
                window.location.href = "delete_sent_mail.do?msgid=" + msgid;
            }
        }
    </script>

    <!-- 메일 표시 영역 -->
    <div id="main">
        <c:out value="${messageList}" escapeXml="false" />
    </div>

    <%@include file="../footer.jspf" %>
</body>
</html>
