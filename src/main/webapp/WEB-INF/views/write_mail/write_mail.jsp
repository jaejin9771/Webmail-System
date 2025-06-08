<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>메일 쓰기 화면</title>
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
        <style>
            .suggestions {
                position: absolute;
                background-color: white;
                list-style-type: none;
                margin: 0;
                padding: 0;
                max-height: 150px;
                overflow-y: auto;
                width: 500px;
                z-index: 999;
                font-size: 0.9em;
                color: #666666;
            }
            .suggestions li {
                padding: 8px;
                cursor: pointer;
                transition: background-color 0.2s ease;
            }
            .suggestions li:hover {
                background-color: #f0f0f0;
            }
        </style>
    </head>
    <body>
        <%@include file="../header.jspf"%>

        <div id="sidebar">
            <jsp:include page="../sidebar_previous_menu.jsp" />
        </div>

        <div id="main">
            <form enctype="multipart/form-data" method="POST" action="write_mail.do">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                <table>
                    <tr>
                        <td>수신</td>
                        <td>
                            <input type="text" name="to" id="to" size="80" autocomplete="off"
                                   value="${!empty param['sender'] ? param['sender'] : ''}" />
                            <ul id="to-suggestions" class="suggestions"></ul>
                        </td>
                    </tr>
                    <tr>
                        <td>참조</td>
                        <td>
                            <input type="text" name="cc" id="cc" size="80" autocomplete="off">
                            <ul id="cc-suggestions" class="suggestions"></ul>
                        </td>
                    </tr>
                    <tr>
                        <td>메일 제목</td>
                        <td>
                            <input type="text" name="subj" size="80"
                                   value="${!empty param['sender'] ? 'RE: ' + sessionScope['subject'] : ''}" />
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2">본문</td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <textarea rows="15" name="body" cols="80">
                                <c:if test="${!empty param['sender']}">
                            &#10;&#10;&#10;----&#10;${sessionScope['body']}
                                </c:if>
                            </textarea>
                        </td>
                    </tr>
                    <tr>
                        <td>첨부 파일</td>
                        <td><input type="file" name="file1" size="80" /></td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <input type="submit" value="메일 보내기">
                            <input type="reset" value="다시 입력">
                        </td>
                    </tr>
                </table>
            </form>
        </div>

        <%@include file="../footer.jspf"%>

        <!-- ✅ 자동완성 스크립트: testing 파라미터 없을 때만 실행 -->
        <c:if test="${empty param.testing}">
            <script>
                function setupAutocomplete(inputId, suggestionId) {
                    const input = document.getElementById(inputId);
                    const suggestionBox = document.getElementById(suggestionId);

                    input.addEventListener('input', () => {
                        const keyword = input.value.toLowerCase();
                        if (keyword.length === 0) {
                            suggestionBox.innerHTML = '';
                            suggestionBox.style.display = 'none';
                            return;
                        }

                        fetch('${contextPath}/api/addressbook/emails?q=' + encodeURIComponent(keyword))
                                .then(res => res.json())
                                .then(data => {
                                    suggestionBox.innerHTML = '';
                                    suggestionBox.style.display = 'block';

                                    data.forEach(entry => {
                                        const li = document.createElement('li');
                                        li.innerHTML = "<strong>" + entry.name + "</strong> &lt;" + entry.email + "&gt;";
                                        li.addEventListener('click', () => {
                                            input.value = entry.email;
                                            suggestionBox.innerHTML = '';
                                            suggestionBox.style.display = 'none';
                                        });
                                        suggestionBox.appendChild(li);
                                    });
                                })
                                .catch(err => console.error("주소록 자동완성 오류:", err));
                    });

                    input.addEventListener('focus', () => input.dispatchEvent(new Event('input')));
                    input.addEventListener('blur', () => {
                        setTimeout(() => {
                            suggestionBox.innerHTML = '';
                            suggestionBox.style.display = 'none';
                        }, 200);
                    });
                }

                window.onload = function () {
                    setupAutocomplete('to', 'to-suggestions');
                    setupAutocomplete('cc', 'cc-suggestions');
                };
            </script>
        </c:if>

    </body>
</html>
