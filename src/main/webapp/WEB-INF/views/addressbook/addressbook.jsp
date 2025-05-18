<%-- 
    Document   : addressbook.jsp
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
        <title>주소록</title>
    </head>
    <body>
        <%@include file="../header.jspf"%>

        <div id="sidebar">
            <jsp:include page="../sidebar_previous_menu.jsp" />
        </div>

        <div id="main">

            <table style="width: 700px;">
                <tr>
                    <th colspan="2">주소록 추가</th>
                </tr>
                <form method="post" action="${pageContext.request.contextPath}/addressbook">
                    <input type="hidden" name="id" value="${addressEntry.id}">
                    <tr>
                        <td>이름</td>
                        <td><input type="text" name="name" size="80"
                                   value="${not empty addressEntry.name ? addressEntry.name : ''}"
                                   placeholder="홍길동"></td>
                    </tr>
                    <tr>
                        <td>이메일</td>
                        <td><input type="email" name="email" size="80"
                                   value="${not empty addressEntry.email ? addressEntry.email : ''}"
                                   required placeholder="email@email.com"></td>
                    </tr>
                    <tr>
                        <td>그룹</td>
                        <td><input type="text" name="group" id="group" size="80"
                                   value="${not empty addressEntry.group ? addressEntry.group : ''}"
                                   placeholder="학교, 직장, 친구 등"></td>
                    </tr>
                    <tr>
                        <td colspan="2" align="center">
                            <button type="submit">등록</button>
                        </td>
                    </tr>
                </form>
            </table>

            <br>

            <table style="width: 700px;">
                <form method="get" action="${pageContext.request.contextPath}/addressbook">
                    <tr>
                        <td>
                            <input type="text" name="query" size="80"
                                   placeholder="이름, 이메일, 전화번호 검색" value="${query}">
                        </td>
                        <td>
                            <button type="submit">검색</button>
                        </td>
                    </tr>
                </form>
            </table>

            <br>

            <table style="width: 700px;">
                <tr>
                    <th colspan="5">주소록 목록</th>
                </tr>
                <tr>
                    <td style="width: 25%">이름</td>
                    <td style="width: 34%">이메일</td>
                    <td style="width: 25%">그룹</td>
                    <td colspan="2">수정 / 삭제</td>
                </tr>
                <c:forEach var="entry" items="${addressList}">
                    <tr>
                        <td>${entry.name}</td>
                        <td>${entry.email}</td>
                        <td>${entry.group}</td>
                        <td>
                            <form method="get" action="${pageContext.request.contextPath}/addressbook" style="display:inline-block;">
                                <input type="hidden" name="editId" value="${entry.id}">
                                <button type="submit">수정</button>
                            </form>
                        </td>
                        <td>
                            <form method="post" action="${pageContext.request.contextPath}/addressbook/delete" style="display:inline-block;">
                                <input type="hidden" name="id" value="${entry.id}">
                                <button type="submit">삭제</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </table>

        </div>

        <%@include file="../footer.jspf"%>

 <c:if test="${param.duplicate == 'true'}">
    <script>
        const email = "${duplicateEmail}";
        const isEdit = ${isEdit == true ? "true" : "false"};

        const confirmMsg = isEdit
            ? "이미 있는 이메일입니다. 그래도 수정하시겠습니까?"
            : "이미 있는 이메일입니다. 새롭게 등록하시겠습니까?";

        const result = confirm(confirmMsg);

        if (result) {
            const form = document.createElement('form');
            form.method = 'post';
            form.action = '${pageContext.request.contextPath}/addressbook';

            const addHidden = (name, value) => {
                const input = document.createElement('input');
                input.type = 'hidden';
                input.name = name;
                input.value = value;
                form.appendChild(input);
            };

            addHidden('email', email);
            addHidden('force', 'true');

            // ✅ 이름과 그룹 함께 전송
            addHidden('name', "${entry.name}");
            addHidden('group', "${entry.group}");

            if (isEdit) {
                addHidden('id', "${entry.id}");
            }

            document.body.appendChild(form);
            form.submit();
        } else {
            if (!isEdit) {
                const queryForm = document.querySelector("form[action$='/addressbook']:not([method='post'])");
                const queryInput = queryForm.querySelector("input[name='query']");
                queryInput.value = email;
                queryForm.submit();
            }
        }
    </script>
</c:if>


    </body>
</html>