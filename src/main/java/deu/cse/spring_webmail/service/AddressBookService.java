package deu.cse.spring_webmail.service;

import deu.cse.spring_webmail.model.AddressEntry;
import deu.cse.spring_webmail.repository.AddressBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

    public List<AddressEntry> getAll() {
        return repository.findAll();
    }

    public void add(AddressEntry entry) {
        repository.save(entry);
    }

    public void deleteByEmail(String email) {
        repository.deleteById(email);
    }

    public List<AddressEntry> search(String query) {
        String q = query.toLowerCase().replaceAll("-", "");

        return repository.findAll().stream()
                .filter(entry
                        -> (entry.getName() != null && entry.getName().toLowerCase().contains(q))
                || (entry.getEmail() != null && entry.getEmail().toLowerCase().contains(q))
                || (entry.getPhone() != null && entry.getPhone().replaceAll("-", "").contains(q))
                || (entry.getCategory() != null && entry.getCategory().toLowerCase().contains(q)))
                .toList();
    }

    public void update(AddressEntry entry) {
        repository.save(entry);
    }

    public AddressEntry getByEmail(String email) {
        return repository.findById(email).orElse(null);
    }

    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
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
                .sorted(comparator)
                .toList();
    }
}
