package deu.cse.spring_webmail.control;

import deu.cse.spring_webmail.model.AddressEntry;
import deu.cse.spring_webmail.service.AddressBookService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import lombok.extern.slf4j.Slf4j;
import java.util.stream.Collectors;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AddressbookController {

    private final AddressBookService addressBookService;

    @GetMapping("/addressbook")
    public String showAddressBook(@RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "editId", required = false) Integer editId,
            Model model, HttpSession session) {
        model.addAttribute("userid", session.getAttribute("userid"));

        // 수정할 항목 찾기
        AddressEntry entryToEdit = (editId != null)
                ? addressBookService.getById(editId)
                : new AddressEntry();

        model.addAttribute("addressEntry", entryToEdit);  // 입력칸에 자동 채움

        // 검색 처리
        List<AddressEntry> filteredList = (query != null && !query.isBlank())
                ? addressBookService.search(query)
                : addressBookService.getAll();

        model.addAttribute("query", query);
        model.addAttribute("addressList", filteredList);
        return "addressbook/addressbook";
    }

    @PostMapping("/addressbook")
    public String registerAddress(@ModelAttribute AddressEntry entry,
            @RequestParam(value = "force", required = false) boolean force,
            RedirectAttributes redirectAttributes) {

        boolean isNew = entry.getId() == 0;

        if (!force) {
            boolean isDuplicate = isNew
                    ? addressBookService.existsByEmail(entry.getEmail())
                    : addressBookService.existsByEmailExcludingId(entry.getEmail(), entry.getId());

            if (isDuplicate) {
                redirectAttributes.addFlashAttribute("duplicateEmail", entry.getEmail());
                redirectAttributes.addFlashAttribute("isEdit", !isNew);  // 등록인지 수정인지 구분
                redirectAttributes.addFlashAttribute("entry", entry);
                return "redirect:/addressbook?duplicate=true";
            }
        }

        if (isNew) {
            entry.setId(AddressEntry.getNextId());
            addressBookService.add(entry);
            redirectAttributes.addFlashAttribute("msg", "주소록 등록 완료: " + entry.getName());
        } else {
            addressBookService.update(entry);
            redirectAttributes.addFlashAttribute("msg", "주소록 수정 완료: " + entry.getName());
        }

        return "redirect:/addressbook";
    }

    @PostMapping("/addressbook/delete")
    public String deleteAddress(@RequestParam int id) {
        addressBookService.deleteById(id);
        return "redirect:/addressbook";
    }

    @GetMapping("/api/addressbook/emails")
    @ResponseBody
    public List<String> getAllEmailsWithNames() {
        return addressBookService.getAll().stream()
                .map(entry -> entry.getName() + " <" + entry.getEmail() + ">")
                .collect(Collectors.toList());
    }
}
