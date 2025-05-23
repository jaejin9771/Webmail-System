package deu.cse.spring_webmail.model;

import jakarta.mail.Message;

/**
 *
 * @author junho
 */

public class SentMailFormatter {

    public String getSentMessageTable(Message[] messages, String userid, int page, int pageSize, int totalMessages) {
        StringBuilder buffer = new StringBuilder();
        int baseNo = (page - 1) * pageSize;
        int totalPages = (int) Math.ceil((double) totalMessages / pageSize);
        int startIndex = totalMessages - (page - 1) * pageSize;

        buffer.append(makeTableStyle());
        buffer.append("<table>");
        buffer.append(makeTableHeader());

        for (int i = messages.length - 1; i >= 0; i--) {
            MessageParser parser = new MessageParser(messages[i], userid);
            parser.parse(true);
            
            System.out.println("subject = " + parser.getSubject());
            System.out.println("toAddress = " + parser.getToAddress());
            System.out.println("sentDate = " + parser.getSentDate());

            int no = baseNo + (messages.length - i);
            int realIndex = startIndex - (messages.length - 1 - i);
            buffer.append(makeTableRow(parser, no, realIndex));
        }

        buffer.append("</table>");
        buffer.append(makePagination(page, totalPages));
        return buffer.toString();
    }

    private String makeTableStyle() {
        return new StringBuilder()
            .append("<style>")
            .append("table { table-layout: fixed; width: 100%; word-wrap: break-word; }")
            .append("th, td { border: 1px solid #333; padding: 8px; text-align: left; }")
            .append("th:nth-child(1), td:nth-child(1) { width: 5%; }")
            .append("th:nth-child(2), td:nth-child(2) { width: 20%; }")
            .append("th:nth-child(3), td:nth-child(3) { width: 45%; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; text-align: center; }")
            .append("th:nth-child(4), td:nth-child(4) { width: 20%; }")
            .append("th:nth-child(5), td:nth-child(5) { width: 10%; }")
            .append("</style>")
            .toString();
    }

    private String makeTableHeader() {
        return "<tr><th>No.</th><th>받는 사람</th><th>제목</th><th>보낸 날짜</th><th>삭제</th></tr>";
    }

    private String makeTableRow(MessageParser parser, int no, int realIndex) {
        return new StringBuilder()
            .append("<tr>")
            .append("<td>").append(no).append("</td>")
            .append("<td>").append(escapeHtml(parser.getToAddress())).append("</td>")
            .append("<td><a href='show_sent_message?msgid=")
            .append(realIndex).append("'>")
            .append(escapeHtml(parser.getSubject())).append("</a></td>")
            .append("<td>").append(parser.getSentDate()).append("</td>")
            .append("<td><a href='#' onclick=\"confirmDelete(").append(realIndex).append(")\">삭제</a></td>")
            .append("</tr>")
            .toString();
    }

    private String makePagination(int page, int totalPages) {
        StringBuilder buffer = new StringBuilder();
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

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#x27;");
    }
}

