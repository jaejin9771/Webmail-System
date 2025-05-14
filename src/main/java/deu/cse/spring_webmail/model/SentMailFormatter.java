package deu.cse.spring_webmail.model;

import java.util.List;
import jakarta.mail.Message;
import deu.cse.spring_webmail.model.MessageParser;

public class SentMailFormatter {

    public String getSentMessageTable(Message[] messages, String userid) {
        StringBuilder buffer = new StringBuilder();

        buffer.append("<table>");
        buffer.append("<tr> "
                + " <th> No. </th> "
                + " <th> 받는 사람 </th>"
                + " <th> 제목 </th>     "
                + " <th> 보낸 날짜 </th>   "
                + " <th> 삭제 </th>   "
                + " </tr>");

        for (int i = messages.length - 1; i >= 0; i--) {
            MessageParser parser = new MessageParser(messages[i], userid);
            parser.parse(false);  // envelope만

            buffer.append("<tr> "
                    + " <td>" + (i + 1) + " </td> "
                    + " <td>" + parser.getToAddress() + "</td>" 
                    + " <td> "
                    + " <a href='show_sent_message?msgid=" + (i + 1) + "' title=\"메일 보기\"> "
                    + parser.getSubject() + "</a> </td>"
                    + " <td>" + parser.getSentDate() + "</td>"
                    + " <td>"
                    + "<a href='delete_sent_mail.do?msgid=" + (i + 1) + "'> 삭제 </a>" + "</td>"
                    + " </tr>");
        }

        buffer.append("</table>");
        return buffer.toString();
    }
}
