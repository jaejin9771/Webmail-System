package deu.cse.spring_webmail.control;

import deu.cse.spring_webmail.model.ImapAgent;
import deu.cse.spring_webmail.service.MailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@PropertySource("classpath:/system.properties")
@Slf4j
public class SentMailController {

    private final MailService mailService;

    @Autowired private HttpSession session;
    @Autowired private HttpServletRequest request;

    @GetMapping("/sent_mail_list")
    public String showSentMenu(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "searchType", required = false) String searchType,
            @RequestParam(name = "keyword", required = false) String keyword,
            Model model) {

        String host = (String) session.getAttribute("host");
        String userid = (String) session.getAttribute("userid");
        String password = (String) session.getAttribute("password");

        String sentMessageListHtml = mailService.getSentMessageTable(
                host, userid, password,
                page, 20,
                searchType, keyword,
                request
        );

        model.addAttribute("messageList", sentMessageListHtml);
        model.addAttribute("currentPage", page);

        return "sent_mail/sent_mail_list";
    }

    @GetMapping("/show_sent_message")
    public String showSentMessage(@RequestParam("msgid") Integer msgId, Model model) {
        log.debug("show_sent_message: msgid = {}", msgId);

        String host = (String) session.getAttribute("host");
        String userid = (String) session.getAttribute("userid");
        String password = (String) session.getAttribute("password");

        ImapAgent imap = new ImapAgent(host, userid, password);
        imap.setRequest(request);

        String msg = imap.getSentMessage(msgId);
        session.setAttribute("sender", imap.getSender());
        session.setAttribute("subject", imap.getSubject());
        session.setAttribute("body", imap.getBody());

        model.addAttribute("msg", msg);
        return "sent_mail/show_sent_mail";
    }

    @GetMapping("/delete_sent_mail.do")
    public String deleteSentMail(@RequestParam("msgid") Integer msgId, RedirectAttributes attrs) {

        String host = (String) session.getAttribute("host");
        String userid = (String) session.getAttribute("userid");
        String password = (String) session.getAttribute("password");

        ImapAgent imap = new ImapAgent(host, userid, password);
        boolean success = imap.deleteSentMessage(msgId, true);

        if (success) {
            attrs.addFlashAttribute("msg", "보낸 메시지 삭제 성공");
        } else {
            attrs.addFlashAttribute("msg", "보낸 메시지 삭제 실패");
        }

        return "redirect:/sent_mail_list";
    }
}

