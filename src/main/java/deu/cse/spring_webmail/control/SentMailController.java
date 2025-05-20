package deu.cse.spring_webmail.control;

import deu.cse.spring_webmail.model.ImapAgent;
import deu.cse.spring_webmail.model.MessageFormatter;
import deu.cse.spring_webmail.service.MailService;
import jakarta.mail.Message;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
@Slf4j
public class SentMailController {

    @Autowired private HttpSession session;
    @Autowired private HttpServletRequest request;
    @Autowired private MailService mailService;

    @GetMapping("/sent_mail_list")
    public String sentMailList(Model model, @RequestParam(defaultValue = "1") int page) {
        String host = (String) session.getAttribute("host");
        String userid = (String) session.getAttribute("userid");
        String password = (String) session.getAttribute("password");

        ImapAgent agent = new ImapAgent(host, userid, password);
        Message[] allMessages = agent.getSentMessages();
        int totalMessages = allMessages.length;
        int pageSize = 20;

        // 페이징된 메시지 배열 자르기
        int start = Math.max(0, totalMessages - page * pageSize);
        int end = Math.min(totalMessages, totalMessages - (page - 1) * pageSize);
        Message[] pagedMessages = new Message[end - start];
        System.arraycopy(allMessages, start, pagedMessages, 0, end - start);

        // HTML 테이블 생성
        String messageList = mailService.getSentMessageList(host, userid, password, pagedMessages, page, pageSize, totalMessages);
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
        String result = "보낸편지함 메시지를 불러올 수 없습니다.";

        try {
            Message message = imap.getSentMessage(msgid);
            if (message != null) {
                MessageFormatter formatter = new MessageFormatter(userid);
                formatter.setRequest(request);
                result = formatter.getMessage(message);
            }
        } catch (Exception e) {
            log.error("보낸 메시지 로딩 실패: {}", e.getMessage());
        }

        model.addAttribute("msg", result);
        return "sent_mail/show_sent_mail";
    }

    @GetMapping("/delete_sent_mail.do")
    public String deleteSentMailDo(@RequestParam("msgid") Integer msgid, RedirectAttributes attrs) {
        String host = (String) session.getAttribute("host");
        String userid = (String) session.getAttribute("userid");
        String password = (String) session.getAttribute("password");

        ImapAgent imap = new ImapAgent(host, userid, password);
        boolean deleteSuccessful = imap.deleteSentMail(msgid);

        if (deleteSuccessful) {
            attrs.addFlashAttribute("msg", "메시지 삭제를 성공하였습니다.");
        } else {
            attrs.addFlashAttribute("msg", "메시지 삭제를 실패하였습니다.");
        }

        return "redirect:/sent_mail_list";
    }

}
