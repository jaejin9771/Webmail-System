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
        Pop3Agent agent = new Pop3Agent(host, userid, password);
        return agent.validate();
    }

    //사용자의 메일함에서 메일을 검색하거나 전체 목록을 가져와 HTML 테이블로 반환
    public String getMessageTable(
            String host, String userid, String password,
            int page, int pageSize,
            String searchType, String keyword,
            HttpServletRequest request
    ) {
        // POP3 서버에 연결하여 메일을 가져올 Agent 객체 생성
        Pop3Agent agent = new Pop3Agent(host, userid, password);
        agent.setRequest(request);  // 삭제 버튼을 위한 인덱스 추적용 request 전달

        Message[] messages;
        int totalCount;

        try {
            // 검색 조건이 있을 경우
            if (searchType != null && keyword != null && !keyword.trim().isEmpty()) {
                // 필터링된 메일 목록 반환
                messages = agent.getSearchedMessages(searchType, keyword.trim(), page, pageSize);
                totalCount = messages.length;  // 검색 결과 개수만큼 출력
            } else {
                // 전체 메일 목록 반환
                messages = agent.getMessages(page, pageSize);
                totalCount = agent.getTotalMessageCount();  // 전체 메일 개수
            }

            // HTML 테이블을 구성하기 위한 Formatter 사용
            MessageFormatter formatter = new MessageFormatter(userid);
            formatter.setRequest(request);  // 첨부파일 경로 등 설정용
            return formatter.getMessageTable(messages, page, pageSize, totalCount, "inbox");

        } catch (Exception e) {
            // 예외 발생 시 사용자에게 오류 메시지 출력
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
            if (searchType != null && keyword != null && !keyword.trim().isEmpty()) {
                messages = agent.getSearchedSentMessages(searchType, keyword.trim(), page, pageSize);
                totalCount = messages.length;
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
