package deu.cse.spring_webmail.service;

import deu.cse.spring_webmail.model.Pop3Agent;
import org.springframework.stereotype.Service;

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

    public String getMessageList(String host, String userid, String password) {
        Pop3Agent agent = new Pop3Agent();
        agent.setHost(host);
        agent.setUserid(userid);
        agent.setPassword(password);
        return agent.getMessageList();
    }
}

