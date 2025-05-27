package deu.cse.spring_webmail.model;

import jakarta.mail.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class Pop3Agent {

    @Getter @Setter private String host;
    @Getter @Setter private String userid;
    @Getter @Setter private String password;
    @Getter @Setter private Store store;
    @Getter @Setter private String excveptionType;
    @Getter @Setter private HttpServletRequest request;

    @Getter private String sender;
    @Getter private String subject;
    @Getter private String body;

    @Setter private Session session;
    @Setter private MessageFormatter messageFormatter;

    public Pop3Agent(String host, String userid, String password) {
        this.host = host;
        this.userid = userid;
        this.password = password;
    }

    public boolean validate() {
        boolean status = false;
        try {
            status = connectToStore();
            if (store != null) store.close();
        } catch (Exception ex) {
            log.error("Pop3Agent.validate() error : ", ex);
            status = false;
        } finally {
            return status;
        }
    }

    public boolean deleteMessage(int msgid, boolean really_delete) {
        boolean status = false;
        if (!connectToStore()) return false;

        try {
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);

            Message msg = folder.getMessage(msgid);
            msg.setFlag(Flags.Flag.DELETED, really_delete);

            folder.close(true); // expunge
            store.close();
            status = true;
        } catch (Exception ex) {
            log.error("deleteMessage() error: {}", ex.getMessage());
        } finally {
            return status;
        }
    }

    public Message[] getMessages(int page, int pageSize) {
        Message[] messages = null;
        try {
            if (!connectToStore()) return new Message[0];

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            int totalMessages = inbox.getMessageCount();
            int end = totalMessages - (page - 1) * pageSize;
            int start = Math.max(end - pageSize + 1, 1);

            messages = inbox.getMessages(start, end);
            request.setAttribute("messageStartIndex", start);
        } catch (Exception e) {
            log.error("getMessages(page) error: ", e);
        }
        return messages;
    }

    public int getTotalMessageCount() {
        int count = 0;
        try {
            if (!connectToStore()) return 0;

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            count = inbox.getMessageCount();
        } catch (Exception e) {
            log.error("getTotalMessageCount() error: ", e);
        }
        return count;
    }

    public String getMessage(int n) {
        String result = "POP3  서버 연결이 되지 않아 메시지를 볼 수 없습니다.";

        if (!connectToStore()) {
            log.error("POP3 connection failed!");
            return result;
        }

        try {
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
            Message message = folder.getMessage(n);

            if (messageFormatter == null) {
                messageFormatter = new MessageFormatter(userid);
            }

            messageFormatter.setRequest(request);
            result = messageFormatter.getMessage(message);
            sender = messageFormatter.getSender();
            subject = messageFormatter.getSubject();
            body = messageFormatter.getBody();

            folder.close(true);
            store.close();
        } catch (Exception ex) {
            log.error("getMessage() exception: ", ex);
            result = "getMessage() exception: " + ex;
        } finally {
            return result;
        }
    }

    public List<Message> searchMessages(String type, String keyword) {
        List<Message> matchedMessages = new ArrayList<>();
        try {
            if (!connectToStore()) return matchedMessages;

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            int totalMessages = inbox.getMessageCount();
            Message[] allMessages = inbox.getMessages(1, totalMessages);

            for (int i = allMessages.length - 1; i >= 0; i--) {
                MessageParser parser = new MessageParser(allMessages[i], userid);
                parser.parse(false);

                String target = "";
                if ("subject".equalsIgnoreCase(type)) {
                    target = parser.getSubject();
                } else if ("from".equalsIgnoreCase(type)) {
                    target = parser.getFromAddress();
                }

                if (target != null && target.toLowerCase().contains(keyword.toLowerCase())) {
                    matchedMessages.add(allMessages[i]);
                }
            }
        } catch (Exception e) {
            log.error("searchMessages() error: ", e);
        }
        return matchedMessages;
    }

    public Message[] paginateMessages(List<Message> messages, int page, int pageSize) {
        int total = messages.size();
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, total);

        if (start >= total) return new Message[0];

        Message[] pageMessages = new Message[end - start];
        for (int i = start; i < end; i++) {
            pageMessages[i - start] = messages.get(i);
        }
        return pageMessages;
    }

    public boolean connectToStore() {
        boolean status = false;

        try {
            if (session == null) {
                Properties props = System.getProperties();
                props.setProperty("mail.pop3.host", host);
                props.setProperty("mail.pop3.user", userid);
                props.setProperty("mail.pop3.apop.enable", "false");
                props.setProperty("mail.pop3.disablecapa", "true");
                props.setProperty("mail.debug", "false");
                props.setProperty("mail.pop3.debug", "false");
                session = Session.getInstance(props);
            }

            store = session.getStore("pop3");
            store.connect(host, userid, password);
            status = true;
        } catch (Exception ex) {
            log.error("connectToStore 예외: {}", ex.getMessage());
        } finally {
            return status;
        }
    }
}
