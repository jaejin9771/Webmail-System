<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
<head>
    <link type="text/css" rel="stylesheet" href="css/main_style.css" />
    <title>주소록</title>
    <style>
        #suggestion-box {
            border: 1px double #999;
            background: #fff;
            display: none;
            padding: 5px;
            margin-top: 5px;
            width: 680px;
            position: absolute;
            z-index: 10;
        }
        #suggestion-box div {
            padding: 4px;
        }
        #suggestion-box div:hover {
            background-color: #eee;
            cursor: pointer;
        }
    </style>
</head>
<body>
<%@include file="../header.jspf"%>

<div id="sidebar">
    <jsp:include page="../sidebar_previous_menu.jsp"/>
</div>

<div id="main">

    <table style="width: 700px; position: relative;">
        <tr>
            <th colspan="2"><strong>주소록 추가</strong></th>
        </tr>
        <form method="post" action="${pageContext.request.contextPath}/addressbook">
            <input type="hidden" name="id" value="${addressEntry.id}">
            <tr>
                <td>이름</td>
                <td>
                    <input type="text" name="name" id="name" size="80"
                           value="${not empty addressEntry.name ? addressEntry.name : ''}"
                           required placeholder="홍길동">
                </td>
            </tr>
            <tr>
                <td>이메일</td>
                <td>
                    <input type="email" name="email" id="email" size="80"
                           value="${not empty addressEntry.email ? addressEntry.email : ''}"
                           required placeholder="email@email.com">
                </td>
            </tr>
            <tr>
                <td>전화번호</td>
                <td>
                    <input type="text" name="phone" id="phone" size="80"
                           value="${not empty addressEntry.phone ? addressEntry.phone : ''}"
                           placeholder="000-0000-0000" maxlength="13">
                </td>
            </tr>
            <tr>
                <td colspan="2" align="center">
                    <button type="submit">등록</button>
                </td>
            </tr>
        </form>
        <tr>
            <td colspan="2">
                <div id="suggestion-box"></div>
            </td>
        </tr>
    </table>

    <br>

    <table style="width: 700px">
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

    <table style="width: 700px">
        <tr>
            <th colspan="5"><strong>주소록 목록</strong></th>
        </tr>
        <tr>
            <td style="width: 25%">이름</td>
            <td style="width: 34%">이메일</td>
            <td style="width: 25%">전화번호</td>
            <td colspan="2">수정 / 삭제</td>
        </tr>
        <c:forEach var="entry" items="${addressList}">
            <tr>
                <td>${entry.name}</td>
                <td>${entry.email}</td>
                <td>${entry.phone}</td>
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

    <script>
        const phoneInput = document.getElementById("phone");
        const nameInput = document.getElementById("name");
        const emailInput = document.getElementById("email");
        const suggestionBox = document.getElementById("suggestion-box");

        const addressList = [
            <c:forEach var="entry" items="${addressList}" varStatus="status">
            {
                name: "${entry.name}",
                email: "${entry.email}",
                phone: "${entry.phone}"
            }<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        ];

        phoneInput.addEventListener("input", function (e) {
            let value = e.target.value.replace(/\D/g, "");
            if (value.length <= 3) {
                e.target.value = value;
            } else if (value.length <= 6) {
                e.target.value = value.replace(/(\d{3})(\d{1,3})/, "$1-$2");
            } else if (value.length <= 10) {
                e.target.value = value.replace(/(\d{3})(\d{3})(\d{1,4})/, "$1-$2-$3");
            } else if (value.length === 11) {
                e.target.value = value.replace(/(\d{3})(\d{4})(\d{4})/, "$1-$2-$3");
            } else {
                e.target.value = value.slice(0, 11).replace(/(\d{3})(\d{4})(\d{4})/, "$1-$2-$3");
            }
        });

        function showSuggestions(query, key) {
            if (!query) {
                suggestionBox.style.display = 'none';
                return;
            }

            const matches = addressList.filter(entry =>
                entry[key].toLowerCase().includes(query.toLowerCase())
            );

            if (matches.length === 0) {
                suggestionBox.style.display = 'none';
                return;
            }

            suggestionBox.innerHTML = matches.map(entry =>
                `<div>${entry.name} / ${entry.email} / ${entry.phone}</div>`
            ).join('');
            suggestionBox.style.display = 'block';
        }

        nameInput.addEventListener('input', () => {
            showSuggestions(nameInput.value, 'name');
        });
        emailInput.addEventListener('input', () => {
            showSuggestions(emailInput.value, 'email');
        });

        <c:if test="${not empty msg}">
        alert("${msg}");
        </c:if>
    </script>

</div>

<%@include file="../footer.jspf"%>
</body>
</html>
