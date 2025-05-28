<%-- 
    Document   : addressbook.jsp
    Author     : jiye
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
            <table style="width: 750px;">
                <tr>
                    <th colspan="2">주소록 추가</th>
                </tr>
                <form method="post" action="${pageContext.request.contextPath}/addressbook">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                    <input type="hidden" name="originalEmail" value="${addressEntry.email}">
                    <tr>
                        <td>이름</td>
                        <td><input type="text" name="name" size="85"
                                   value="${not empty addressEntry.name ? addressEntry.name : ''}"
                                   placeholder="홍길동"></td>
                    </tr>
                    <tr>
                        <td>이메일</td>
                        <td><input type="email" name="email" size="85"
                                   value="${not empty addressEntry.email ? addressEntry.email : ''}"
                                   required placeholder="email@email.com"></td>
                    </tr>
                    <tr>
                        <td>전화번호</td>
                        <td><input type="text" name="phone" size="85"
                                   value="${not empty addressEntry.phone ? addressEntry.phone : ''}"
                                   placeholder="010-1234-5678"
                                   accept=""oninput="this.value = this.value.replace(/[^0-9\-]/g, '')"></td>
                    </tr>
                    <tr>
                        <td>카테고리</td>
                        <td><input type="text" name="category" size="85"
                                   value="${not empty addressEntry.category ? addressEntry.category : ''}"
                                   placeholder="가족, 친구, 회사 등"></td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <button type="submit">등록</button>
                        </td>
                    </tr>
                </form>
            </table>

            <br>

            <table style="width: 750px;">
                <form method="get" action="${pageContext.request.contextPath}/addressbook">
                    <tr>
                        <td>
                            <input type="text" name="query" size="85"
                                   placeholder="이름, 이메일, 전화번호 검색" value="${query}">
                        </td>
                        <td>
                            <button type="submit">검색</button>
                        </td>
                    </tr>
                </form>
            </table>

            <br>

            <table style="width: 750px;">
                <form method="get" action="${pageContext.request.contextPath}/addressbook">
                    <tr>
                        <td style="width: 42%">
                            <label for="sortBy">정렬 기준:</label>
                            <select name="sortBy" id="sortBy">
                                <option value="createdAt" ${sortBy == 'createdAt' ? 'selected' : ''}>등록순</option>
                                <option value="name" ${sortBy == 'name' ? 'selected' : ''}>이름</option>
                                <option value="email" ${sortBy == 'email' ? 'selected' : ''}>이메일</option>
                                <option value="category" ${sortBy == 'category' ? 'selected' : ''}>카테고리</option>
                            </select>
                        </td>
                        <td style="width: 42%">
                            <label for="order">정렬 방식:</label>
                            <select name="order" id="order">
                                <option value="asc" ${order == 'asc' ? 'selected' : ''}>오름차순</option>
                                <option value="desc" ${order == 'desc' ? 'selected' : ''}>내림차순</option>
                            </select>
                        </td>
                        <td>
                            <button type="submit">정렬</button>
                        </td>
                    </tr>
                </form>
            </table>

            <table style="width: 750px;">
                <tr>
                    <th colspan="6">주소록 목록</th>
                </tr>
                <tr>
                    <td style="width: 21%">이름</td>
                    <td style="width: 21%">이메일</td>
                    <td style="width: 21%">전화번호</td>
                    <td style="width: 21%">카테고리</td>
                    <td colspan="2">수정 / 삭제</td>
                </tr>
                <c:forEach var="entry" items="${addressList}">
                    <tr>
                        <td>${entry.name}</td>
                        <td>${entry.email}</td>
                        <td>${entry.phone}</td>
                        <td>${entry.category}</td>
                        <td>
                            <form method="get" action="${pageContext.request.contextPath}/addressbook"
                                  style="display:inline-block;"
                                  onsubmit="return confirm('${entry.email} 주소를 수정하시겠습니까?');">
                                <input type="hidden" name="editEmail" value="${entry.email}">
                                <button type="submit">수정</button>
                            </form>

                        </td>
                        <td>
                            <form method="post" action="${pageContext.request.contextPath}/addressbook/delete"
                                  style="display:inline-block;"
                                  onsubmit="return confirm('${entry.email} 주소를 삭제하시겠습니까?');">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                <input type="hidden" name="email" value="${entry.email}">
                                <button type="submit">삭제</button>
                            </form>

                        </td>
                    </tr>
                </c:forEach>
            </table>
            <div style="margin-top: 10px;">
                <c:if test="${totalPages > 1}">
                    <c:forEach begin="0" end="${totalPages - 1}" var="i">
                        <a href="?page=${i}&sortBy=${sortBy}&order=${order}&query=${query}"
                           style="${i == currentPage ? 'font-weight:bold;' : ''}">
                            [${i + 1}]
                        </a>
                    </c:forEach>
                </c:if>
            </div>
        </div>

        <%@include file="../footer.jspf"%>

        <c:if test="${param.duplicate == 'true'}">
            <script>
                const email = "${duplicateEmail}";
                const isEdit = ${isEdit == true ? "true" : "false"};

                alert("이미 등록된 이메일입니다.")

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

                    addHidden('${_csrf.parameterName}', '${_csrf.token}'); // CSRF 추가
                    addHidden('email', email);
                    addHidden('force', 'true');
                    addHidden('name', "${entry.name}");
                    addHidden('phone', "${entry.phone}");
                    addHidden('category', "${entry.category}");

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
        <c:if test="${not empty msg}">
            <script>
                alert('${msg}');
            </script>
        </c:if>    
    </body>
</html>
