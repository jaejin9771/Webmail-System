package deu.cse.spring_webmail.service;

import deu.cse.spring_webmail.model.AddressEntry;
import deu.cse.spring_webmail.repository.AddressBookRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.List;
/**
 *
 * @author jiye
 */
@Service
@RequiredArgsConstructor
public class AddressBookService {

    private final AddressBookRepository repository;

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public List<AddressEntry> getAll() {
        return repository.findAll().stream()
                .filter(e -> getCurrentUsername().equals(e.getUsername()))
                .toList();
    }

    public void add(AddressEntry entry) {
        entry.setUsername(getCurrentUsername());
        repository.save(entry);
    }

    public void deleteByEmail(String email) {
        AddressEntry entry = getByEmail(email);
        if (entry != null) {
            repository.delete(entry);
        }
    }

    public List<AddressEntry> search(String query) {
        String q = query.toLowerCase().replace("-", "");

        return repository.findAll().stream()
                .filter(entry -> getCurrentUsername().equals(entry.getUsername()))
                .filter(entry
                        -> (entry.getName() != null && entry.getName().toLowerCase().contains(q))
                || (entry.getEmail() != null && entry.getEmail().toLowerCase().contains(q))
                || entry.getPhone() != null && entry.getPhone().replace("-", "").contains(q)
                || (entry.getCategory() != null && entry.getCategory().toLowerCase().contains(q)))
                .toList();
    }

    public void update(AddressEntry entry) {
        repository.save(entry);
    }

    public AddressEntry getByEmail(String email) {
        return repository.findById(email)
                .filter(e -> getCurrentUsername().equals(e.getUsername()))
                .orElse(null);
    }

    public boolean existsByEmail(String email) {
        return repository.existsByEmailAndUsername(email, getCurrentUsername());
    }

    public List<AddressEntry> getAllSorted(String sortBy, String order) {
        Comparator<AddressEntry> comparator;
        switch (sortBy) {
            case "email" ->
                comparator = Comparator.comparing(AddressEntry::getEmail, String.CASE_INSENSITIVE_ORDER);
            case "category" ->
                comparator = Comparator.comparing(AddressEntry::getCategory, String.CASE_INSENSITIVE_ORDER);
            case "createdAt" ->
                comparator = Comparator.comparing(AddressEntry::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()));
            default ->
                comparator = Comparator.comparing(AddressEntry::getName, String.CASE_INSENSITIVE_ORDER);
        }
        if ("desc".equalsIgnoreCase(order)) {
            comparator = comparator.reversed();
        }

        return repository.findAll().stream()
                .filter(e -> getCurrentUsername().equals(e.getUsername()))
                .sorted(comparator)
                .toList();
    }

    public List<AddressEntry> getAllByUsername(String username) {
        return repository.findAll().stream()
                .filter(e -> username.equals(e.getUsername()))
                .toList();
    }

    public List<AddressEntry> findByKeywordForUser(String keyword, String username) {
        return repository.findByUsernameAndNameContainingIgnoreCaseOrUsernameAndEmailContainingIgnoreCase(
                username, keyword, username, keyword
        );
    }

    public Page<AddressEntry> getPagedEntries(String sortBy, String order, int page, int size) {
        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return repository.findByUsername(getCurrentUsername(), pageable);
    }
}
