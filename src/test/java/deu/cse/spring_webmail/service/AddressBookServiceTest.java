package deu.cse.spring_webmail.service;

import deu.cse.spring_webmail.model.AddressEntry;
import deu.cse.spring_webmail.repository.AddressBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AddressBookServiceTest {

    @Mock
    AddressBookRepository repository;

    @InjectMocks
    AddressBookService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // SecurityContext mocking
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testAdd() {
        AddressEntry entry = new AddressEntry("test@example.com", "홍길동", "010-1234-5678", "지인");
        service.add(entry);

        verify(repository).save(argThat(saved -> 
            saved.getUsername().equals("testuser") && saved.getEmail().equals("test@example.com")
        ));
    }

    @Test
    void testGetAll() {
        AddressEntry entry1 = new AddressEntry("a@a.com", "홍길동", "010", "친구");
        entry1.setUsername("testuser");
        AddressEntry entry2 = new AddressEntry("b@b.com", "이영희", "010", "직장");
        entry2.setUsername("otheruser");

        when(repository.findAll()).thenReturn(List.of(entry1, entry2));

        List<AddressEntry> result = service.getAll();

        assertEquals(1, result.size());
        assertEquals("a@a.com", result.get(0).getEmail());
    }

    @Test
    void testSearch() {
        AddressEntry entry1 = new AddressEntry("a@a.com", "홍길동", "010-0000-0000", "친구");
        entry1.setUsername("testuser");

        when(repository.findAll()).thenReturn(List.of(entry1));

        List<AddressEntry> results = service.search("홍");
        assertEquals(1, results.size());
    }

    @Test
    void testExistsByEmail() {
        when(repository.existsByEmailAndUsername("a@a.com", "testuser")).thenReturn(true);

        assertTrue(service.existsByEmail("a@a.com"));
    }

    @Test
    void testGetByEmail_Owned() {
        AddressEntry entry = new AddressEntry("a@a.com", "홍길동", "010", "친구");
        entry.setUsername("testuser");

        when(repository.findById("a@a.com")).thenReturn(Optional.of(entry));

        AddressEntry result = service.getByEmail("a@a.com");
        assertNotNull(result);
    }

    @Test
    void testGetByEmail_NotOwned() {
        AddressEntry entry = new AddressEntry("a@a.com", "홍길동", "010", "친구");
        entry.setUsername("otheruser");

        when(repository.findById("a@a.com")).thenReturn(Optional.of(entry));

        AddressEntry result = service.getByEmail("a@a.com");
        assertNull(result);
    }

    @Test
    void testGetAllSortedByNameAsc() {
        AddressEntry entry1 = new AddressEntry("a@a.com", "김영희", "010", "가족");
        entry1.setUsername("testuser");
        entry1.setCreatedAt(LocalDateTime.now());

        AddressEntry entry2 = new AddressEntry("b@b.com", "박철수", "010", "직장");
        entry2.setUsername("testuser");
        entry2.setCreatedAt(LocalDateTime.now().minusDays(1));

        when(repository.findAll()).thenReturn(List.of(entry2, entry1));

        List<AddressEntry> sorted = service.getAllSorted("name", "asc");

        assertEquals("김영희", sorted.get(0).getName());
    }
}
