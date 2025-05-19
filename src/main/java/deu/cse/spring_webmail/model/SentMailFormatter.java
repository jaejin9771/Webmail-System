package deu.cse.spring_webmail.model;

import jakarta.mail.Message;
import lombok.extern.slf4j.Slf4j;
import jakarta.mail.Address;

@Slf4j
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
                .append("th:nth-child(2), td:nth-child(2) { width: 20%; }")
                .append("th:nth-child(3), td:nth-child(3) { width: 45%; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; text-align: center; }")
                .append("th:nth-child(4), td:nth-child(4) { width: 20%; }")
                .append("th:nth-child(5), td:nth-child(5) { width: 10%; text-align: center; }")
                .append("</style>");

        buffer.append("<table>");
        buffer.append("<tr>  <th> No. </th>  <th> 받는 사람 </th> <th> 제목 </th> <th> 보낸 날짜 </th> <th> 삭제 </th> </tr>");

        try {
            for (int i = messages.length - 1; i >= 0; i--) {
                Message message = messages[i];

                Address[] toAddrs = message.getRecipients(Message.RecipientType.TO);
                String to = (toAddrs != null && toAddrs.length > 0) ? toAddrs[0].toString() : "null";

                String subject = message.getSubject();
                String date = "날짜 없음";
                if (message.getSentDate() != null) {
                    date = message.getSentDate().toString();
                    date = date.substring(0, date.length() -8);
                }


                int no = baseNo + (messages.length - i);                 
                int realIndex = startIndex - (messages.length - 1 - i);   

                buffer.append("<tr>")
                        .append("<td>").append(no).append("</td>")
                        .append("<td>").append(to).append("</td>")
                        .append("<td><a href='show_sent_message?msgid=").append(realIndex).append("'>")
                        .append(subject).append("</a></td>")
                        .append("<td>").append(date).append("</td>")
                        .append("<td><a href='delete_sent_mail.do?msgid=").append(realIndex).append("'>삭제</a></td>")
                        .append("</tr>");
            }
        } catch (Exception e) {
            log.error("보낸 메일 리스트 오류: {}", e.getMessage());
        }

        buffer.append("</table>");

        buffer.append("<div style='text-align:center; margin-top:10px;'>");
        if (page > 1) {
            buffer.append("<a href='/webmail/sent_mail_list?page=").append(page - 1).append("'>이전</a> | ");
        }
        for (int i = Math.max(1, page - 1); i <= Math.min(totalPages, page + 1); i++) {
            buffer.append("<a href='/webmail/sent_mail_list?page=").append(i).append("'>").append(i).append("</a> ");
        }
        if (page < totalPages) {
            buffer.append("| <a href='/webmail/sent_mail_list?page=").append(page + 1).append("'>다음</a>");
        }
        buffer.append("</div>");

        return buffer.toString();
    }

}
