/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.cse.spring_webmail.model;

import jakarta.mail.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import java.util.*;

@Slf4j
@NoArgsConstructor
public class ImapAgent {

    @Getter @Setter private String host;
    @Getter @Setter private String userid;
    @Getter @Setter private String password;
    @Getter @Setter private Store store;
    @Getter @Setter private Folder folder;

    public ImapAgent(String host, String userid, String password) {
        this.host = host;
        this.userid = userid;
        this.password = password;
    }

    public boolean connectToStore() {
        boolean status = false;

        try {
            Properties props = new Properties();

            props.setProperty("mail.store.protocol", "imap");
            props.setProperty("mail.imap.port", "993");               // James가 SSL 포트 993 열고 있음
            props.setProperty("mail.imap.ssl.enable", "true");        // SSL 사용
            props.setProperty("mail.imap.ssl.trust", "*");            // 인증서 검증 생략 (테스트 목적)

            Session session = Session.getInstance(props);
            store = session.getStore("imap");
            
            store.connect(host, userid, password);

            status = true;
        } catch (Exception ex) {
            log.error("IMAP 서버 연결 실패: {}", ex.getMessage());
        } finally {
            return status;
        }
    }

    // SMTP로 보낸 메시지를 보낸편지함에 저장
    public boolean saveToSentFolder(Message msg) {
        if (!connectToStore()) return false;

        try {
            Folder sentFolder = store.getFolder("Sent");
            if (!sentFolder.exists()) {
                sentFolder.create(Folder.HOLDS_MESSAGES);
            }
            sentFolder.open(Folder.READ_WRITE);

            msg.setSentDate(new Date());
            msg.saveChanges();
            
            sentFolder.appendMessages(new Message[]{msg});
            sentFolder.close(false);
            store.close();
            
            return true;
        } catch (Exception e) {
            log.error("보낸편지함 저장 실패: {}", e.getMessage());
            return false;
        }
    }
    public String getMessageList() {
        String result = "";

        try {
            Folder defaultFolder = store.getDefaultFolder();
            Folder[] folders = defaultFolder.list("*");

            // 폴더 가져오기 및 생성
            folder = store.getFolder("Sent");
            if (!folder.exists()) {
                folder.create(Folder.HOLDS_MESSAGES);
            }

            folder.open(Folder.READ_ONLY);

            // 메시지 목록 불러오기
            Message[] messages = folder.getMessages();

            FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE);
            folder.fetch(messages, fp);

            // HTML 테이블로 변환
            SentMailFormatter formatter = new SentMailFormatter();
            result = formatter.getSentMessageTable(messages, userid, 1, messages.length, messages.length);

            folder.close(false);    
            store.close();

        } catch (Exception e) {
            e.printStackTrace();
            log.error("보낸 메일함 불러오기 실패: {}", e.getMessage());
            result = "보낸 메일함 로딩 실패: " + e.getMessage();
        }
        return result;
    }  
    
        public Message getSentMessage(int msgid) throws MessagingException {
            if (!connectToStore()) {
                throw new MessagingException("IMAP 연결 실패");
            }

            Folder sentFolder = store.getFolder("Sent");
            if (!sentFolder.exists()) {
                throw new MessagingException("Sent 폴더가 존재하지 않음");
            }

        sentFolder.open(Folder.READ_ONLY);
        Message message = sentFolder.getMessage(msgid);

        return message;
    }
        
    public boolean deleteSentMail(int msgid) {
        boolean status = false;

        if (!connectToStore()) {
            return status;
        }

        try {
            
            Folder sentFolder = store.getFolder("Sent");
            sentFolder.open(Folder.READ_WRITE);

            // msgid에 해당하는 메시지 가져와 삭제 플래그 설정
            Message msg = sentFolder.getMessage(msgid);
            msg.setFlag(Flags.Flag.DELETED, true);

            // 폴더 닫을 때 expunge = true로 설정해서 실제 삭제 반영
            sentFolder.close(true);
            store.close();
            status = true;
        } catch (Exception ex) {
            log.error("deleteMessageInSent() error: {}", ex.getMessage());
        } finally {
            return status;
        }
    }
    
    public Message[] getSentMessages() {
        if (!connectToStore()) {
        log.error("IMAP 서버 연결 실패!");
        return new Message[0];
        }
        
        try {
            Folder sentFolder = store.getFolder("Sent");
            if (!sentFolder.exists()) {
                sentFolder.create(Folder.HOLDS_MESSAGES);
            }

            sentFolder.open(Folder.READ_ONLY);

            Message[] messages = sentFolder.getMessages();

            FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE);
            sentFolder.fetch(messages, fp);

            return messages;

        } catch (Exception e) {
            log.error("보낸 메일 목록 조회 실패: {}", e.getMessage());
            return new Message[0];
        }
    }

}      


