package deu.cse.spring_webmail.control;

import deu.cse.spring_webmail.model.ImapAgent;
import deu.cse.spring_webmail.model.MessageFormatter;
import deu.cse.spring_webmail.service.MailService;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
public class SentMailController {

    @Autowired private HttpSession session;
    @Autowired private HttpServletRequest request;
    @Autowired private MailService mailService;

    // 보낸 메일 목록 보기
    @GetMapping("/sent_mail_list")
    public String sentMailList(Model model) {
        String host = (String) session.getAttribute("host");
        String userid = (String) session.getAttribute("userid");
        String password = (String) session.getAttribute("password");

        String messageList = mailService.getSentMessageList(host, userid, password);
        model.addAttribute("messageList", messageList);

        return "sent_mail/sent_mail_list";
    }

    // 보낸 메일 상세 보기
    @GetMapping("/show_sent_message")
    public String showSentMessage(@RequestParam Integer msgid, Model model) {
        String host = (String) session.getAttribute("host");
        String userid = (String) session.getAttribute("userid");
        String password = (String) session.getAttribute("password");

        ImapAgent imap = new ImapAgent(host, userid, password);
        String msg = getSentMessage(imap, msgid, userid);

        model.addAttribute("msg", msg);
        return "sent_mail/show_sent_mail";
    }

    private String getSentMessage(ImapAgent imap, int msgid, String userid) {
        String result = "보낸편지함 메시지를 불러올 수 없습니다.";

        try {
            if (!imap.connectToStore()) {
                return result;
            }

            Folder sentFolder = imap.getStore().getFolder("Sent");
            if (!sentFolder.exists()) {
                log.error("Sent 폴더가 존재하지 않습니다.");
                return result;
            }

            sentFolder.open(Folder.READ_ONLY);
            Message message = sentFolder.getMessage(msgid);

            MessageFormatter formatter = new MessageFormatter(userid);
            formatter.setRequest(request);
            result = formatter.getMessage(message);

            sentFolder.close(false);
            imap.getStore().close();
        } catch (Exception ex) {
            log.error("보낸 메시지 로딩 오류", ex);
        }

        return result;
    }
}
