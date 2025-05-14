package deu.cse.spring_webmail.service;

import deu.cse.spring_webmail.model.MessageFormatter;
import deu.cse.spring_webmail.model.Pop3Agent;
import jakarta.mail.Message;
import org.springframework.stereotype.Service;
import deu.cse.spring_webmail.model.ImapAgent;

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

    public String getPagedMessageTable(String host, String userid, String password, int page, int pageSize) {
        Pop3Agent agent = new Pop3Agent(host, userid, password);
        Message[] messages = agent.getMessages(page, pageSize);
        int totalMessages = agent.getTotalMessageCount();

        MessageFormatter formatter = new MessageFormatter(userid);
        return formatter.getMessageTable(messages, page, pageSize, totalMessages);
    }
    
        public String getSentMessageList(String host, String userid, String password) {
        ImapAgent agent = new ImapAgent(host, userid, password);
        return agent.getMessageList();  // 보낸 메일함 (IMAP)
    }
}
