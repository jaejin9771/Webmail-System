package deu.cse.spring_webmail.control;

import deu.cse.spring_webmail.model.AddressEntry;
import deu.cse.spring_webmail.service.AddressBookService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author jiye
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class AddressbookController {

    private final AddressBookService addressBookService;

    @GetMapping("/addressbook")
    public String showAddressBook(@RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "editEmail", required = false) String editEmail,
            @RequestParam(value = "sortBy", required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "order", required = false, defaultValue = "asc") String order,
            Model model, HttpSession session) {
        model.addAttribute("userid", session.getAttribute("userid"));
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("order", order);

        AddressEntry entryToEdit = (editEmail != null)
                ? addressBookService.getByEmail(editEmail)
                : new AddressEntry();
        model.addAttribute("addressEntry", entryToEdit);

        List<AddressEntry> filteredList = (query != null && !query.isBlank())
                ? addressBookService.search(query)
                : addressBookService.getAllSorted(sortBy, order);

        model.addAttribute("query", query);
        model.addAttribute("addressList", filteredList);
        return "addressbook/addressbook";
    }

    @PostMapping("/addressbook")
    public String registerAddress(@ModelAttribute AddressEntry entry,
            @RequestParam(value = "originalEmail", required = false) String originalEmail,
            RedirectAttributes redirectAttributes) {

        String cleanEmail = entry.getEmail().trim().replaceAll("^,+", "");
        entry.setEmail(cleanEmail);

        boolean isEdit = originalEmail != null && !originalEmail.isBlank();
        boolean isEmailChanged = isEdit && !cleanEmail.equalsIgnoreCase(originalEmail);

        boolean isDuplicate = (!isEdit && addressBookService.existsByEmail(cleanEmail))
                || (isEdit && isEmailChanged && addressBookService.existsByEmail(cleanEmail));

        if (isDuplicate) {
            redirectAttributes.addFlashAttribute("duplicateEmail", cleanEmail);
            redirectAttributes.addFlashAttribute("isEdit", isEdit);
            redirectAttributes.addFlashAttribute("entry", entry);
            return "redirect:/addressbook?duplicate=true";
        }

        if (isEdit && isEmailChanged) {
            addressBookService.deleteByEmail(originalEmail);
        }

        addressBookService.add(entry);

        String msg = entry.getEmail() + " 주소가 " + (isEdit ? "수정" : "등록") + "되었습니다.";
        redirectAttributes.addFlashAttribute("msg", msg);

        return "redirect:/addressbook";
    }

    @PostMapping("/addressbook/delete")
    public String deleteAddress(@RequestParam String email,
            RedirectAttributes redirectAttributes) {
        addressBookService.deleteByEmail(email);
        redirectAttributes.addFlashAttribute("msg", email + " 주소가 삭제되었습니다.");
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
