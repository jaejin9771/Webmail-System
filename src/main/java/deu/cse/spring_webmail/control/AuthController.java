/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.cse.spring_webmail.control;

import deu.cse.spring_webmail.model.MessageFormatter;
import deu.cse.spring_webmail.model.Pop3Agent;
import deu.cse.spring_webmail.service.MailService;
import jakarta.mail.Message;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author jaejin
 */
@Controller
@RequiredArgsConstructor
@PropertySource("classpath:/system.properties")
public class AuthController {

    private final HttpSession session;
    private final MailService mailService;

    @Value("${admin.id}")
    private String ADMIN_ID;

    @PostMapping("/login")
    public String login(@RequestParam String userid,
            @RequestParam String passwd,
            RedirectAttributes attrs) {

        if (session.getAttribute("host") == null) {
            session.setAttribute("host", "localhost");  // 또는 JAMES_HOST
        }
        String host = (String) session.getAttribute("host");

        if (mailService.validateLogin(host, userid, passwd)) {
            session.setAttribute("userid", userid);
            session.setAttribute("password", passwd);
            return userid.equals(ADMIN_ID) ? "redirect:/admin/admin_menu" : "redirect:/main_menu";
        }

        attrs.addFlashAttribute("msg", "로그인 실패");
        return "redirect:/login_fail";
    }

    @GetMapping("/main_menu")
    public String showMainMenu(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "searchType", required = false) String searchType,
            @RequestParam(name = "keyword", required = false) String keyword,
            HttpServletRequest request,
            Model model) {

        String host = (String) session.getAttribute("host");
        String userid = (String) session.getAttribute("userid");
        String password = (String) session.getAttribute("password");

        Pop3Agent agent = new Pop3Agent(host, userid, password);
        agent.setRequest(request);

        final int pageSize = 20;

        try {
            Message[] messages;
            int totalCount;

            if (searchType != null && keyword != null && !keyword.trim().isEmpty()) {
                messages = agent.getSearchedMessages(searchType, keyword.trim(), page, pageSize);
                totalCount = messages.length;  // 검색 결과 수만큼만 출력
            } else {
                messages = agent.getMessages(page, pageSize);
                totalCount = agent.getTotalMessageCount();
            }

            MessageFormatter formatter = new MessageFormatter(userid);
            formatter.setRequest((jakarta.servlet.http.HttpServletRequest) session.getAttribute("request"));  // 필요 시 처리
            String htmlTable = formatter.getMessageTable(messages, page, pageSize, totalCount);

            model.addAttribute("messageList", htmlTable);
            model.addAttribute("currentPage", page);

        } catch (Exception e) {
            model.addAttribute("msg", "메일 목록을 불러오는 중 오류 발생: " + e.getMessage());
        }

        return "main_menu";
    }

    @GetMapping("/logout")
    public String logout() {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/login_fail")
    public String loginFail() {
        return "login_fail";
    }
}
