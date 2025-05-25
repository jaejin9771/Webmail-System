package deu.cse.spring_webmail.service;

import deu.cse.spring_webmail.model.MessageFormatter;
import deu.cse.spring_webmail.model.Pop3Agent;
import jakarta.mail.Message;
import org.springframework.stereotype.Service;
import deu.cse.spring_webmail.model.ImapAgent;
import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author jaejin
 */
@Service
public class MailService {

    public boolean validateLogin(String host, String userid, String password) {
        return new Pop3Agent(host, userid, password).validate();
    }

    public String getMessageTable(
            String host, String userid, String password,
            int page, int pageSize,
            String searchType, String keyword,
            HttpServletRequest request
    ) {
        Pop3Agent agent = new Pop3Agent(host, userid, password);
        agent.setRequest(request);

        Message[] messages;
        int totalCount;

        try {
            boolean isSearch = searchType != null && keyword != null && !keyword.trim().isEmpty();
            if (isSearch) {
                messages = agent.getSearchedMessages(searchType, keyword.trim(), page, pageSize);
                totalCount = messages != null ? messages.length : 0;
            } else {
                messages = agent.getMessages(page, pageSize);
                totalCount = agent.getTotalMessageCount();
            }

            MessageFormatter formatter = new MessageFormatter(userid);

            formatter.setRequest(request);  // 첨부파일 경로 등 설정용
            return formatter.getMessageTable(messages, page, pageSize, totalCount, "inbox");

        } catch (Exception e) {
            return "<p>메일을 가져오는 중 오류가 발생했습니다: " + e.getMessage() + "</p>";
        }
    }

    public String getSentMessageTable(
            String host, String userid, String password,
            int page, int pageSize,
            String searchType, String keyword,
            HttpServletRequest request
    ) {
        ImapAgent agent = new ImapAgent(host, userid, password);
        agent.setRequest(request);

        Message[] messages;
        int totalCount;

        try {
            boolean isSearch = searchType != null && keyword != null && !keyword.trim().isEmpty();
            if (isSearch) {
                messages = agent.getSearchedSentMessages(searchType, keyword.trim(), page, pageSize);
                totalCount = messages != null ? messages.length : 0;
            } else {
                messages = agent.getSentMessages(page, pageSize);
                totalCount = agent.getSentTotalMessageCount();
            }

            MessageFormatter formatter = new MessageFormatter(userid);
            formatter.setRequest(request);
            return formatter.getMessageTable(messages, page, pageSize, totalCount, "sent");

        } catch (Exception e) {
            return "<p>보낸 메일을 가져오는 중 오류가 발생했습니다: " + e.getMessage() + "</p>";
        }
    }
}