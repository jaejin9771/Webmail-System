/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.cse.spring_webmail.control;

import deu.cse.spring_webmail.model.MessageFormatter;
import deu.cse.spring_webmail.model.Pop3Agent;
import deu.cse.spring_webmail.service.MailService;
import jakarta.mail.Message;
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
    public String showMainMenu(@RequestParam(name = "page", defaultValue = "1") int page, Model model) {
        String host = (String) session.getAttribute("host");
        String userid = (String) session.getAttribute("userid");
        String password = (String) session.getAttribute("password");

        String htmlTable = mailService.getPagedMessageTable(host, userid, password, page, 20);

        model.addAttribute("messageList", htmlTable);
        model.addAttribute("currentPage", page);
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
