package deu.cse.spring_webmail.repository;

import deu.cse.spring_webmail.model.AddressEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author jiye
 */
@Repository
public interface AddressBookRepository extends JpaRepository<AddressEntry, String> {

    boolean existsByEmailAndUsername(String email, String username);

    Page<AddressEntry> findByUsername(String username, Pageable pageable);

    List<AddressEntry> findByUsernameAndNameContainingIgnoreCaseOrUsernameAndEmailContainingIgnoreCase(
            String username1, String name,
            String username2, String email);
}
