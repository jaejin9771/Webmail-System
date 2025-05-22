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

    @GetMapping("/main_menu")
    public String showMainMenu(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "searchType", required = false) String searchType,
            @RequestParam(name = "keyword", required = false) String keyword,
            HttpServletRequest request,
            Model model) {

        String host = (String) session.getAttribute("host");
        String userid = (String) session.getAttribute("username");
        String password = (String) session.getAttribute("password");

        String messageListHtml = mailService.getMessageTable(
                host, userid, password,
                page, 20,
                searchType, keyword,
                request
        );

        model.addAttribute("messageList", messageListHtml);
        model.addAttribute("currentPage", page);

        return "main_menu";
    }

    @GetMapping("/logout")
    public String logout() {
        session.invalidate();
        return "redirect:/main_menu";
    }

    @GetMapping("/login_fail")
    public String loginFail() {
        return "login_fail";
    }
}
