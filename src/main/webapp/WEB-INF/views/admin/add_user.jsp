<%-- 
    Document   : add_user.jsp
    Author     : jongmin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>사용자 추가 화면</title>
        <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/main_style.css" />
    </head>
    <body>
        <%@ include file="../header.jspf" %>

        <div id="sidebar">
            <jsp:include page="sidebar_admin_previous_menu.jsp" />
        </div>

        <div id="main">
            추가로 등록할 사용자 ID와 암호를 입력해 주시기 바랍니다. <br> <br>

            <form name="AddUser" action="${pageContext.request.contextPath}/admin/add_user" method="POST">

                <%-- csrf.jspf --%>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

                <table border="0" align="left">
                    <tr>
                        <td>사용자 ID</td>
                        <td> <input type="text" name="id" value="" size="20" />  </td>
                    </tr>
                    <tr>
                        <td>암호 </td>
                        <td> <input type="password" name="password" value="" /> </td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <input type="submit" value="등록" name="register" />
                            <input type="reset" value="초기화" name="reset" />
                        </td>
                    </tr>
                </table>

            </form>
        </div>

        <%@include file="../footer.jspf" %>
    </body>
</html>
