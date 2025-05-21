package deu.cse.spring_webmail.model;

import jakarta.mail.Message;
import jakarta.servlet.http.HttpServletRequest;

public class SentMailFormatter {

    public String getSentMessageTable(Message[] messages, String userid, int page, int pageSize, int totalMessages) {
        StringBuilder buffer = new StringBuilder();
        int baseNo = (page - 1) * pageSize;
        int totalPages = (int) Math.ceil((double) totalMessages / pageSize);
        int startIndex = totalMessages - (page - 1) * pageSize;

        buffer.append("<style>")
                .append("table { table-layout: fixed; width: 100%; word-wrap: break-word; }")
                .append("th, td { border: 1px solid #333; padding: 8px; text-align: left; }")
                .append("th:nth-child(1), td:nth-child(1) { width: 5%; }")
                .append("th:nth-child(2), td:nth-child(2) { width: 20%; }")  // 받은 사람(To)
                .append("th:nth-child(3), td:nth-child(3) { width: 45%; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; text-align: center; }")
                .append("th:nth-child(4), td:nth-child(4) { width: 20%; }")
                .append("th:nth-child(5), td:nth-child(5) { width: 10%; }")
                .append("</style>");

        buffer.append("<table>");
        buffer.append("<tr><th>No.</th><th>받는 사람</th><th>제목</th><th>보낸 날짜</th><th>삭제</th></tr>");

        for (int i = messages.length - 1; i >= 0; i--) {
            MessageParser parser = new MessageParser(messages[i], userid);
            parser.parse(false);
            int no = baseNo + (messages.length - i);
            int realIndex = startIndex - (messages.length - 1 - i);

            buffer.append("<tr>")
                  .append("<td>").append(no).append("</td>")
                  .append("<td>").append(parser.getToAddress()).append("</td>")
                  .append("<td><a href=show_sent_message?msgid=")
                  .append(realIndex).append(">")
                  .append(parser.getSubject()).append("</a></td>")
                  .append("<td>").append(parser.getSentDate()).append("</td>")
                  .append("<td><a href='#' onclick=\"confirmDelete(").append(realIndex).append(")\">삭제</a></td>")
                  .append("</tr>");
        }

        buffer.append("</table>");

        // 페이지 링크 추가
        buffer.append("<div style='text-align:center; margin-top:10px;'>");
        if (page > 1) {
            buffer.append("<a href=sent_menu?page=").append(page - 1).append(">이전</a> | ");
        }
        for (int i = Math.max(1, page - 1); i <= Math.min(totalPages, page + 1); i++) {
            buffer.append("<a href=sent_menu?page=").append(i).append(">").append(i).append("</a> ");
        }
        if (page < totalPages) {
            buffer.append("| <a href=sent_menu?page=").append(page + 1).append(">다음</a>");
        }
        buffer.append("</div>");

        return buffer.toString();
    }
}
