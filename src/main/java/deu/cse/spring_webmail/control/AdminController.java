package deu.cse.spring_webmail.control;

import deu.cse.spring_webmail.service.UserAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final UserAdminService userAdminService;

    @GetMapping("/admin_menu")
    public String adminMenu(Model model) {
        List<String> userList = userAdminService.getUserList();
        model.addAttribute("userList", userList);
        return "admin/admin_menu";
    }

    @GetMapping("/add_user")
    public String addUserPage() {
        return "admin/add_user";
    }

    @PostMapping("/add_user")
    public String addUserDo(@RequestParam String id,
                             @RequestParam String password,
                             RedirectAttributes attrs) {
        boolean result = userAdminService.addUser(id, password);
        if (result) {
            attrs.addFlashAttribute("msg", String.format("사용자(%s) 추가 성공", id));
        } else {
            attrs.addFlashAttribute("msg", String.format("사용자(%s) 추가 실패", id));
        }
        return "redirect:/admin/admin_menu";
    }

    @GetMapping("/delete_user")
    public String deleteUserPage(Model model) {
        List<String> userList = userAdminService.getUserList();
        model.addAttribute("userList", userList);
        return "admin/delete_user";
    }

    @PostMapping("/delete_user")
    public String deleteUserDo(@RequestParam("selectedUsers") String[] selectedUsers,
                               RedirectAttributes attrs) {
        boolean result = userAdminService.deleteUsers(selectedUsers);
        if (result) {
            attrs.addFlashAttribute("msg", "선택된 사용자를 성공적으로 삭제했습니다.");
        } else {
            attrs.addFlashAttribute("msg", "일부 사용자를 삭제하지 못했습니다.");
        }
        return "redirect:/admin/admin_menu";
    }
}
