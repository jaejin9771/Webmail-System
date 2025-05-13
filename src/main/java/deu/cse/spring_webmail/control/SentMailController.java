package deu.cse.spring_webmail.control;

import deu.cse.spring_webmail.model.SentMail;
import deu.cse.spring_webmail.model.SentMailFormatter;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class SentMailController {

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.datasource.driver-class-name}")
    private String jdbcDriver;

    @GetMapping("/sent_mail_list")
    public String showSentMail(HttpSession session, Model model) {
        String userid = (String) session.getAttribute("userid");
        if (userid == null) {
            return "redirect:/login";
        }

        SentMail sentMail = new SentMail(jdbcUrl, dbUser, dbPassword, jdbcDriver);
        List<SentMail> sentList = sentMail.getSentMailList(userid);

        SentMailFormatter formatter = new SentMailFormatter();
        String messageList = formatter.getMessageTable(sentList);

        model.addAttribute("messageList", messageList);
        return "sent_mail/sent_mail_list";
    }
}


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.cse.spring_webmail.control;

/**
 *
 * @author user
 */
import deu.cse.spring_webmail.model.SentMail;
import deu.cse.spring_webmail.model.SentMailFormatter;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class SentMailController {

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.datasource.driver-class-name}")
    private String jdbcDriver;

    @GetMapping("/sent_mail_list")
    public String showSentMail(HttpSession session, Model model) {
        String userid = (String) session.getAttribute("userid");
        if (userid == null) {
            return "redirect:/login";
        }

        SentMail sentMail = new SentMail(jdbcUrl, dbUser, dbPassword, jdbcDriver);
        List<SentMail> sentList = sentMail.getSentMailList(userid);

        SentMailFormatter formatter = new SentMailFormatter();
        String messageList = formatter.getMessageTable(sentList);

        model.addAttribute("messageList", messageList);
        return "sent_mail/sent_mail_list";
    }
}


