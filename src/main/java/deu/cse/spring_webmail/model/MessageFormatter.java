/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.cse.spring_webmail.model;

import jakarta.mail.Message;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author skylo
 */
@Slf4j
@RequiredArgsConstructor
public class MessageFormatter {

    @NonNull
    private String userid;  // 파일 임시 저장 디렉토리 생성에 필요
    private HttpServletRequest request = null;

    // 220612 LJM - added to implement REPLY
    @Getter
    private String sender;
    @Getter
    private String subject;
    @Getter
    private String body;

    public String getMessageTable(Message[] messages, int page, int pageSize, int totalMessages) {
        StringBuilder buffer = new StringBuilder();
        int baseNo = (page - 1) * pageSize;
        int totalPages = (int) Math.ceil((double) totalMessages / pageSize);
        int startIndex = totalMessages - (page - 1) * pageSize;

        buffer.append("<style>")
                .append("table { table-layout: fixed; width: 100%; word-wrap: break-word; }")
                .append("th, td { border: 1px solid #333; padding: 8px; text-align: left; }")
                .append("th:nth-child(1), td:nth-child(1) { width: 5%; }")
                .append("th:nth-child(2), td:nth-child(2) { width: 20%; }")
                .append("th:nth-child(3), td:nth-child(3) { width: 45%; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; text-align: center; }")
                .append("th:nth-child(4), td:nth-child(4) { width: 20%; }")
                .append("th:nth-child(5), td:nth-child(5) { width: 10%; }")
                .append("</style>");

        buffer.append("<table>");
        buffer.append("<tr><th>No.</th><th>보낸 사람</th><th>제목</th><th>보낸 날짜</th><th>삭제</th></tr>");

        for (int i = messages.length - 1; i >= 0; i--) {
        MessageParser parser = new MessageParser(messages[i], userid);
        parser.parse(false);
        int no = baseNo + (messages.length - i);
        int realIndex = startIndex - (messages.length - 1 - i);  // 실제 서버 상 메일 인덱스

        buffer.append("<tr>")
              .append("<td>" + no + "</td>")
              .append("<td>" + parser.getFromAddress() + "</td>")
              .append("<td><a href=show_message?msgid=" + realIndex + ">" + parser.getSubject() + "</a></td>")
              .append("<td>" + parser.getSentDate() + "</td>")
              .append("<td><a href=delete_mail.do?msgid=" + realIndex + ">삭제</a></td>")
              .append("</tr>");
    }
    buffer.append("</table>");

        // 페이지 링크 추가
        buffer.append("<div style='text-align:center; margin-top:10px;'>");
        if (page > 1) {
            buffer.append("<a href=main_menu?page=" + (page - 1) + ">이전</a> | ");
        }
        for (int i = Math.max(1, page - 1); i <= Math.min(totalPages, page + 1); i++) {
            buffer.append("<a href=main_menu?page=" + i + ">" + i + "</a> ");
        }
        if (page < totalPages) {
            buffer.append("| <a href=main_menu?page=" + (page + 1) + ">다음</a>");
        }
        buffer.append("</div>");

        return buffer.toString();
    }

    public String getMessage(Message message) {
        StringBuilder buffer = new StringBuilder();

        // MessageParser parser = new MessageParser(message, userid);
        MessageParser parser = new MessageParser(message, userid, request);
        parser.parse(true);

        sender = parser.getFromAddress();
        subject = parser.getSubject();
        body = parser.getBody();

        buffer.append("보낸 사람: " + parser.getFromAddress() + " <br>");
        buffer.append("받은 사람: " + parser.getToAddress() + " <br>");
        buffer.append("Cc &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; : " + parser.getCcAddress() + " <br>");
        buffer.append("보낸 날짜: " + parser.getSentDate() + " <br>");
        buffer.append("제 &nbsp;&nbsp;&nbsp;  목: " + parser.getSubject() + " <br> <hr>");

        buffer.append(parser.getBody());

        String attachedFile = parser.getFileName();
        if (attachedFile != null) {
            buffer.append("<br> <hr> 첨부파일: <a href=download"
                    + "?userid=" + this.userid
                    + "&filename=" + attachedFile.replaceAll(" ", "%20")
                    + " target=_top> " + attachedFile + "</a> <br>");
        }

        return buffer.toString();
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
}
