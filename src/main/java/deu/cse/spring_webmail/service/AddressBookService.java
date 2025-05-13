package deu.cse.spring_webmail.service;

import deu.cse.spring_webmail.model.AddressEntry;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author jaejin
 */
@Service
public class AddressBookService {
    private final List<AddressEntry> addressBook = new ArrayList<>();

    public List<AddressEntry> getAll() {
        return addressBook;
    }

    public void add(AddressEntry entry) {
        addressBook.add(entry);
    }
    
    public void deleteById(int id){
        addressBook.removeIf(entry -> entry.getId() == id);
    }
    
    public List<AddressEntry> search(String query) {
        String q = query.toLowerCase().replaceAll("-", "");  // 사용자 검색어에서 - 제거

        return addressBook.stream()
                .filter(entry ->
                    (entry.getName() != null && entry.getName().toLowerCase().contains(q)) ||
                    (entry.getEmail() != null && entry.getEmail().toLowerCase().contains(q)) ||
                    (entry.getPhone() != null && entry.getPhone().replaceAll("-", "").contains(q))  // 전화번호도 - 제거
                )
                .collect(Collectors.toList());
    }
    
    public void update(AddressEntry updatedEntry) {
        for (int i = 0; i < addressBook.size(); i++) {
            if (addressBook.get(i).getId() == updatedEntry.getId()) {
                addressBook.set(i, updatedEntry);
                return;
            }
        }
    }

    public AddressEntry getById(int id) {
        return addressBook.stream()
                .filter(entry -> entry.getId() == id)
                .findFirst()
                .orElse(null);
    }
}

