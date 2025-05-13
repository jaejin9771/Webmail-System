package deu.cse.spring_webmail.model;

import java.util.List;

public class SentMailFormatter {
    public String getMessageTable(List<SentMail> mails) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("<table>");
        buffer.append("<tr><th>No.</th><th>받은 사람</th><th>제목</th><th>보낸 날짜</th><th>삭제</th></tr>");

        int no = 1;
        for (SentMail mail : mails) {
            buffer.append("<tr>");
            buffer.append("<td>").append(no++).append("</td>");
            buffer.append("<td>").append(mail.getRecipient()).append("</td>");
            buffer.append("<td>").append(mail.getMailName()).append("</td>");
            buffer.append("<td>").append(mail.getDate()).append("</td>");
            buffer.append("<td><a href='#'>삭제</a></td>");
            buffer.append("</tr>");
        }

        buffer.append("</table>");
        return buffer.toString();
    }
}


