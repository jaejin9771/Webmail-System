package deu.cse.spring_webmail.model;

import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author junho
 */

@Slf4j
@NoArgsConstructor
public class ImapAgent {

    @Getter @Setter private String host;
    @Getter @Setter private String userid;
    @Getter @Setter private String password;
    @Getter @Setter private Store store;
    @Getter @Setter private String excveptionType;
    @Getter @Setter private HttpServletRequest request;

    @Getter private String sender;
    @Getter private String subject;
    @Getter private String body;

    public ImapAgent(String host, String userid, String password) {
        this.host = host;
        this.userid = userid;
        this.password = password;
    }

    public boolean deleteSentMessage(int msgid, boolean reallyDelete) {
        boolean status = false;
        if (!connectToStore()) return false;

        try {
            Folder folder = store.getFolder("Sent");
            folder.open(Folder.READ_WRITE);

            Message msg = folder.getMessage(msgid);
            msg.setFlag(Flags.Flag.DELETED, reallyDelete);

            folder.close(true);
            store.close();
            status = true;
        } catch (Exception ex) {
            log.error("deleteSentMessage() error: {}", ex.getMessage());
        } finally {
            return status;
        }
    }

    public Message[] getSentMessages(int page, int pageSize) {
        Message[] messages = null;
        try {
            if (!connectToStore()) return null;

            Folder folder = store.getFolder("Sent");
            folder.open(Folder.READ_ONLY);

            int totalMessages = folder.getMessageCount();
            int end = totalMessages - (page - 1) * pageSize;
            int start = Math.max(end - pageSize + 1, 1);

            messages = folder.getMessages(start, end);
            request.setAttribute("messageStartIndex", start);

        } catch (Exception e) {
            log.error("getSentMessages() error: ", e);
        }

        return messages;
    }

    public int getSentTotalMessageCount() {
        int count = 0;
        try {
            if (!connectToStore()) return 0;

            Folder folder = store.getFolder("Sent");
            folder.open(Folder.READ_ONLY);
            count = folder.getMessageCount();

        } catch (Exception e) {
            log.error("getSentTotalMessageCount() error: ", e);
        }
        return count;
    }

    public String getSentMessage(int n) {
        String result = "IMAP 서버 연결이 되지 않아 메시지를 볼 수 없습니다.";

        if (!connectToStore()) {
            log.error("IMAP connection failed!");
            return result;
        }

        try {
            Folder folder = store.getFolder("Sent");
            folder.open(Folder.READ_ONLY);

            Message message = folder.getMessage(n);
            MessageFormatter formatter = new MessageFormatter(userid);
            formatter.setRequest(request);
            result = formatter.getMessage(message);

            sender = formatter.getSender();
            subject = formatter.getSubject();
            body = formatter.getBody();

            folder.close(false);
            store.close();
        } catch (Exception ex) {
            log.error("getSentMessage() error: ", ex);
            result = "getSentMessage() exception = " + ex;
        } finally {
            return result;
        }
    }

    public Message[] getSearchedSentMessages(String type, String keyword, int page, int pageSize) {
        List<Message> matchedMessages = new ArrayList<>();
        try {
            if (!connectToStore()) return null;

            Folder folder = store.getFolder("Sent");
            folder.open(Folder.READ_ONLY);

            int totalMessages = folder.getMessageCount();
            Message[] allMessages = folder.getMessages(1, totalMessages);

            for (int i = allMessages.length - 1; i >= 0; i--) {
                MessageParser parser = new MessageParser(allMessages[i], userid);
                parser.parse(false);

                String target = "";
                if ("subject".equalsIgnoreCase(type)) {
                    target = parser.getSubject();
                } else if ("from".equalsIgnoreCase(type)) {
                    target = parser.getFromAddress();
                } else if ("to".equalsIgnoreCase(type)) {
                    target = parser.getToAddress();
                }

                if (target != null && target.toLowerCase().contains(keyword.toLowerCase())) {
                    matchedMessages.add(allMessages[i]);
                }
            }

            int totalMatched = matchedMessages.size();
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, totalMatched);

            if (start >= totalMatched) return new Message[0];

            Message[] pageMessages = new Message[end - start];
            for (int i = start; i < end; i++) {
                pageMessages[i - start] = matchedMessages.get(i);
            }

            return pageMessages;

        } catch (Exception ex) {
            log.error("getSearchedSentMessages() error: ", ex);
            return null;
        }
    }

    boolean connectToStore() {
        boolean status = false;
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imap");
        props.setProperty("mail.imap.port", "993");
        props.setProperty("mail.imap.ssl.enable", "true");
        props.setProperty("mail.imap.ssl.trust", "*");
        props.setProperty("mail.debug", "false");

        try {
            Session session = Session.getInstance(props);
            store = session.getStore("imap");
            store.connect(host, userid, password);
            status = true;

            Folder sentFolder = store.getFolder("Sent");
            if (!sentFolder.exists()) {
                sentFolder.create(Folder.HOLDS_MESSAGES);
            }
        } catch (Exception ex) {
            log.error("connectToStore() error: {}", ex.getMessage());
        } finally {
            return status;
        }
    }

    public void saveToSentFolder(Message message) {
        try {
            if (!(message instanceof MimeMessage)) {
                log.warn("지원되지 않는 메시지 타입: {}", message.getClass().getName());
                return;
            }

            if (!connectToStore()) {
                log.error("IMAP 연결 실패 - 보낸 메일 저장 불가");
                return;
            }

            Folder sentFolder = store.getFolder("Sent");
            if (!sentFolder.exists()) {
                sentFolder.create(Folder.HOLDS_MESSAGES);
            }

            sentFolder.open(Folder.READ_WRITE);

            Message[] messages = new Message[]{new MimeMessage((MimeMessage) message)};
            sentFolder.appendMessages(messages);

            sentFolder.close(false);
            store.close();

            log.info("보낸 메일을 Sent 폴더에 저장 완료");

        } catch (Exception e) {
            log.error("saveToSentFolder() 오류: ", e);
        }
    }
}
