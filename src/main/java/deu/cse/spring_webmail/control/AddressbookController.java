package deu.cse.spring_webmail.control;

import deu.cse.spring_webmail.model.AddressEntry;
import deu.cse.spring_webmail.service.AddressBookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.Page;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.Principal;
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
    public String showAddressBook(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "editEmail", required = false) String editEmail,
            @RequestParam(value = "sortBy", required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "order", required = false, defaultValue = "asc") String order,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            Model model) {

        model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("order", order);

        AddressEntry entryToEdit = (editEmail != null)
                ? addressBookService.getByEmail(editEmail)
                : new AddressEntry();
        model.addAttribute("addressEntry", entryToEdit);

        if (query != null && !query.isBlank()) {
            List<AddressEntry> filteredList = addressBookService.search(query);
            model.addAttribute("addressList", filteredList);
            model.addAttribute("query", query);
        } else {
            Page<AddressEntry> addressPage = addressBookService.getPagedEntries(sortBy, order, page, size);
            model.addAttribute("addressList", addressPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", addressPage.getTotalPages());
        }

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
        redirectAttributes.addFlashAttribute("msg", cleanEmail + " 주소가 " + (isEdit ? "수정" : "등록") + "되었습니다.");
        return "redirect:/addressbook";
    }

    @PostMapping("/addressbook/delete")
    public String deleteAddress(@RequestParam String email, RedirectAttributes redirectAttributes) {
        addressBookService.deleteByEmail(email);
        redirectAttributes.addFlashAttribute("msg", email + " 주소가 삭제되었습니다.");
        return "redirect:/addressbook";
    }

    @GetMapping("/api/addressbook/emails")
    @ResponseBody
    public List<String> getEmailsFiltered(@RequestParam("q") String keyword, Principal principal) {
        String username = principal.getName();
        return addressBookService.findByKeywordForUser(keyword, username).stream()
                .map(entry -> entry.getName() + " <" + entry.getEmail() + ">")
                .collect(Collectors.toList());
    }
}
