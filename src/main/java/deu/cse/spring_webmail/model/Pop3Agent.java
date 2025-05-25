/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.cse.spring_webmail.model;

import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Store;
import java.util.Properties;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author skylo
 */
@Slf4j
@NoArgsConstructor        // 기본 생성자 생성
public class Pop3Agent {

    @Getter
    @Setter
    private String host;
    @Getter
    @Setter
    private String userid;
    @Getter
    @Setter
    private String password;
    @Getter
    @Setter
    private Store store;
    @Getter
    @Setter
    private String excveptionType;
    @Getter
    @Setter
    private HttpServletRequest request;

    // 220612 LJM - added to implement REPLY
    @Getter
    private String sender;
    @Getter
    private String subject;
    @Getter
    private String body;

    public Pop3Agent(String host, String userid, String password) {
        this.host = host;
        this.userid = userid;
        this.password = password;
    }

    public boolean validate() {
        try {
            boolean status = connectToStore();
            if (store != null) {
                store.close();
            }
            return status;
        } catch (Exception ex) {
            log.error("validate() error: {}", ex.getMessage());
            return false;
        }
    }

    public boolean deleteMessage(int msgid, boolean really_delete) {
        boolean status = false;

        if (!connectToStore()) {
            return status;
        }

        try {
            // Folder 설정
//            Folder folder = store.getDefaultFolder();
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);

            // Message에 DELETED flag 설정
            Message msg = folder.getMessage(msgid);
            msg.setFlag(Flags.Flag.DELETED, really_delete);

            // 폴더에서 메시지 삭제
            // Message [] expungedMessage = folder.expunge();
            // <-- 현재 지원 안 되고 있음. 폴더를 close()할 때 expunge해야 함.
            folder.close(true);  // expunge == true
            store.close();
            status = true;
        } catch (Exception ex) {
            log.error("deleteMessage() error: {}", ex.getMessage());
        } finally {
            return status;
        }
    }

    /*
     * 페이지 단위로 메일 목록을 보여주어야 함.
     */
    public Message[] getMessages(int page, int pageSize) {
        Message[] messages = null;
        try {
            if (!connectToStore()) {
                return null;
            }

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            int totalMessages = inbox.getMessageCount();
            int end = totalMessages - (page - 1) * pageSize;
            int start = Math.max(end - pageSize + 1, 1);

            messages = inbox.getMessages(start, end);
            request.setAttribute("messageStartIndex", start); // 삭제용 인덱스 추적용
        } catch (Exception e) {
            log.error("getMessages(page) error: ", e);
        }
        return messages;
    }

    public int getTotalMessageCount() {
        int count = 0;
        try {
            if (!connectToStore()) {
                return 0;
            }
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            count = inbox.getMessageCount();
        } catch (Exception e) {
            log.error("getTotalMessageCount() error: ", e);
        }
        return count;
    }

    public String getMessage(int n) {
        Folder folder = null;
        try {
            if (!connectToStore()) {
                return "POP3 서버 연결이 되지 않아 메시지를 볼 수 없습니다.";
            }

            folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
            Message message = folder.getMessage(n);

            MessageFormatter formatter = new MessageFormatter(userid);
            formatter.setRequest(request);
            String result = formatter.getMessage(message);

            this.sender = formatter.getSender();
            this.subject = formatter.getSubject();
            this.body = formatter.getBody();

            return result;
        } catch (Exception ex) {
            log.error("getMessage() error: {}", ex.getMessage());
            return "getMessage() 예외: " + ex.getMessage();
        } finally {
            closeResource(folder);
            closeStore();
        }
    }

    public Message[] getSearchedMessages(String type, String keyword, int page, int pageSize) {
        List<Message> matchedMessages = new ArrayList<>();

        try {
            if (!connectToStore()) {
                return null;
            }

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            int totalMessages = inbox.getMessageCount();
            Message[] allMessages = inbox.getMessages(1, totalMessages);

            for (int i = allMessages.length - 1; i >= 0; i--) {
                MessageParser parser = new MessageParser(allMessages[i], userid, request);
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

            // 페이징 처리
            int totalMatched = matchedMessages.size();
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, totalMatched);

            if (start >= totalMatched) {
                return new Message[0];
            }

            Message[] pageMessages = new Message[end - start];
            for (int i = start; i < end; i++) {
                pageMessages[i - start] = matchedMessages.get(i);
            }

            return pageMessages;

        } catch (Exception ex) {
            log.error("getSearchedMessages() error: ", ex);
            return null;
        }
    }

    private boolean connectToStore() {
        boolean status = false;
        Properties props = System.getProperties();
        // https://jakarta.ee/specifications/mail/2.1/apidocs/jakarta.mail/jakarta/mail/package-summary.html
        props.setProperty("mail.pop3.host", host);
        props.setProperty("mail.pop3.user", userid);
        props.setProperty("mail.pop3.apop.enable", "false");
        props.setProperty("mail.pop3.disablecapa", "true");  // 200102 LJM - added cf. https://javaee.github.io/javamail/docs/api/com/sun/mail/pop3/package-summary.html
        props.setProperty("mail.debug", "false");
        props.setProperty("mail.pop3.debug", "false");

        Session session = Session.getInstance(props);
        session.setDebug(false);

        try {
            store = session.getStore("pop3");
            store.connect(host, userid, password);
            status = true;
        } catch (Exception ex) {
            log.error("connectToStore 예외: {}", ex.getMessage());
        } finally {
            return status;
        }
    }

    private void closeResource(Folder folder) {
        try {
            if (folder != null && folder.isOpen()) {
                folder.close(false);
            }
        } catch (Exception ex) {
            log.warn("폴더 닫기 실패: {}", ex.getMessage());
        }
    }

    private void closeStore() {
        try {
            if (store != null) {
                store.close();
            }
        } catch (Exception ex) {
            log.warn("store 닫기 실패: {}", ex.getMessage());
        }
    }

}
