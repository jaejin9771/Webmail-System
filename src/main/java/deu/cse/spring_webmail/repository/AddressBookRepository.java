package deu.cse.spring_webmail.repository;

import deu.cse.spring_webmail.model.AddressEntry;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 *
 * @author jiye
 */
public interface AddressBookRepository extends JpaRepository<AddressEntry, String> {
    boolean existsByEmail(String email);
}
