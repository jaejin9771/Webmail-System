/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.cse.spring_webmail.control;

import deu.cse.spring_webmail.service.UserAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author jaejin
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final UserAdminService userAdminService;

    @GetMapping("/menu")
    public String adminMenu(Model model) {
        model.addAttribute("userList", userAdminService.getUserList());
        return "admin/admin_menu";
    }

    @PostMapping("/add")
    public String addUser(@RequestParam String id,
                          @RequestParam String password,
                          RedirectAttributes attrs) {
        if (userAdminService.addUser(id, password)) {
            attrs.addFlashAttribute("msg", id + " 추가 성공");
        } else {
            attrs.addFlashAttribute("msg", id + " 추가 실패");
        }
        return "redirect:/admin/menu";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam String[] selectedUsers, RedirectAttributes attrs) {
        userAdminService.deleteUsers(selectedUsers);
        attrs.addFlashAttribute("msg", "선택한 사용자 삭제 완료");
        return "redirect:/admin/menu";
    }
}

