package deu.cse.spring_webmail.model;

import jakarta.mail.Message;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class MessageFormatter {

    @NonNull
    private String userid;
    private HttpServletRequest request;

    @Getter private String sender;
    @Getter private String subject;
    @Getter private String body;

    public String getMessageTable(Message[] messages, int page, int pageSize, int totalMessages) {
        StringBuilder buffer = new StringBuilder();
        int baseNo = (page - 1) * pageSize;
        int totalPages = (int) Math.ceil((double) totalMessages / pageSize);
        int startIndex = totalMessages - baseNo;

        appendTableStyle(buffer);
        buffer.append("<table>").append("<tr><th>No.</th><th>보낸 사람</th><th>제목</th><th>보낸 날짜</th><th>삭제</th></tr>");

        for (int i = messages.length - 1; i >= 0; i--) {
            MessageParser parser = new MessageParser(messages[i], userid);
            parser.parse(false);
            int no = baseNo + (messages.length - i);
            int realIndex = startIndex - (messages.length - 1 - i);

            buffer.append("<tr>")
                  .append("<td>").append(no).append("</td>")
                  .append("<td>").append(parser.getFromAddress()).append("</td>")
                  .append("<td><a href=show_message?msgid=").append(realIndex).append(">")
                  .append(parser.getSubject()).append("</a></td>")
                  .append("<td>").append(parser.getSentDate()).append("</td>")
                  .append("<td><a href='#' onclick=\"confirmDelete(").append(realIndex).append(")\">삭제</a></td>")
                  .append("</tr>");
        }
        
        buffer.append("</table>");
        appendPagination(buffer, page, totalPages);
        return buffer.toString();
    }

    public String getMessage(Message message) {
        MessageParser parser = new MessageParser(message, userid, request);
        parser.parse(true);

        sender = parser.getFromAddress();
        subject = parser.getSubject();
        body = parser.getBody();

        StringBuilder buffer = new StringBuilder();
        buffer.append("보낸 사람: ").append(sender).append(" <br>")
              .append("받은 사람: ").append(parser.getToAddress()).append(" <br>")
              .append("Cc &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; : ").append(parser.getCcAddress()).append(" <br>")
              .append("보낸 날짜: ").append(parser.getSentDate()).append(" <br>")
              .append("제 &nbsp;&nbsp;&nbsp;  목: ").append(subject).append(" <br> <hr>")
              .append(body);

        String attachedFile = parser.getFileName();
        if (attachedFile != null) {
            buffer.append("<br> <hr> 첨부파일: <a href=download?userid=")
                  .append(userid)
                  .append("&filename=")
                  .append(attachedFile.replaceAll(" ", "%20"))
                  .append(" target=_top> ")
                  .append(attachedFile).append("</a> <br>");
        }
        
        return buffer.toString();
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
    
    private void appendTableStyle(StringBuilder buffer) {
        buffer.append("<style>")
              .append("table { table-layout: fixed; width: 100%; word-wrap: break-word; }")
              .append("th, td { border: 1px solid #333; padding: 8px; text-align: left; }")
              .append("th:nth-child(1), td:nth-child(1) { width: 5%; }")
              .append("th:nth-child(2), td:nth-child(2) { width: 20%; }")
              .append("th:nth-child(3), td:nth-child(3) { width: 45%; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; text-align: center; }")
              .append("th:nth-child(4), td:nth-child(4) { width: 20%; }")
              .append("th:nth-child(5), td:nth-child(5) { width: 10%; }")
              .append("</style>");
    }
    
    private void appendPagination(StringBuilder buffer, int page, int totalPages) {
        buffer.append("<div style='text-align:center; margin-top:10px;'>");
        if (page > 1) {
            buffer.append("<a href=main_menu?page=").append(page - 1).append(">이전</a> | ");
        }
        for (int i = Math.max(1, page - 1); i <= Math.min(totalPages, page + 1); i++) {
            buffer.append("<a href=main_menu?page=").append(i).append(">" + i + "</a> ");
        }
        if (page < totalPages) {
            buffer.append("| <a href=main_menu?page=").append(page + 1).append(">다음</a>");
        }
        buffer.append("</div>");
    }
}