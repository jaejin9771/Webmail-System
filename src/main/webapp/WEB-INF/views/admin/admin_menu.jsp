<%-- 
    Document   : admin_menu.jsp
    Author     : jongmin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>사용자 관리 메뉴</title>
        <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/main_style.css" />
        <script>
            <c:if test="${!empty msg}">
            alert("${msg}");
            </c:if>
        </script>
    </head>
    <body>
        <%@ include file="../header.jspf" %>

        <div id="sidebar">
            <jsp:include page="sidebar_admin_menu.jsp" />
        </div>


        <div id="main">
            <h2> 메일 사용자 목록 </h2>
            <!-- 아래 코드는 위와 같이 Java Beans와 JSTL을 이용하는 코드로 바꾸어져야 함 -->

            <!--  HTML 문법에서는 EL 표현식 사용 가능
            root = ${root_id}
            -->

            <!--
            <ul>
            <%  // EL 표현식 사용하면 더 간단하게 표현 가능
                for (String userId : (java.util.List<String>)request.getAttribute("userList")) {
                    out.println("<li>" + userId + "</li>");
                }
            %>
        </ul>
            -->

            <ul>
                <c:forEach items="${userList}" var="user">
                    <li> ${user} </li>
                    </c:forEach>
            </ul>
        </div>

        <%@include file="../footer.jspf" %>
    </body>
</html>
