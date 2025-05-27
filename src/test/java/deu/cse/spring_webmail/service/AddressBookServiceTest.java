package deu.cse.spring_webmail.service;

import deu.cse.spring_webmail.model.AddressEntry;
import deu.cse.spring_webmail.repository.AddressBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);
    }

    @Test void testAdd() {
        AddressEntry entry = new AddressEntry("test@example.com", "홍길동", "010-1234-5678", "지인");
        service.add(entry);
        verify(repository).save(argThat(e -> e.getUsername().equals("testuser")));
    }

    @Test void testGetAll() {
        AddressEntry e1 = new AddressEntry("a@a.com", "홍길동", "010", "친구");
        e1.setUsername("testuser");
        AddressEntry e2 = new AddressEntry("b@b.com", "이영희", "010", "직장");
        e2.setUsername("otheruser");
        when(repository.findAll()).thenReturn(List.of(e1, e2));
        List<AddressEntry> result = service.getAll();
        assertEquals(1, result.size());
    }

    @Test void testSearch() {
        AddressEntry e1 = new AddressEntry("a@a.com", "홍길동", "010-0000-0000", "친구");
        e1.setUsername("testuser");
        when(repository.findAll()).thenReturn(List.of(e1));
        List<AddressEntry> result = service.search("홍");
        assertEquals(1, result.size());
    }

    @Test void testExistsByEmail() {
        when(repository.existsByEmailAndUsername("a@a.com", "testuser")).thenReturn(true);
        assertTrue(service.existsByEmail("a@a.com"));
    }

    @Test void testGetByEmail_Owned() {
        AddressEntry e1 = new AddressEntry("a@a.com", "홍길동", "010", "친구");
        e1.setUsername("testuser");
        when(repository.findById("a@a.com")).thenReturn(Optional.of(e1));
        assertNotNull(service.getByEmail("a@a.com"));
    }

    @Test void testGetByEmail_NotOwned() {
        AddressEntry e1 = new AddressEntry("a@a.com", "홍길동", "010", "친구");
        e1.setUsername("otheruser");
        when(repository.findById("a@a.com")).thenReturn(Optional.of(e1));
        assertNull(service.getByEmail("a@a.com"));
    }

    @Test void testDeleteByEmail_Exists() {
        AddressEntry e1 = new AddressEntry("del@a.com", "삭제자", "010", "기타");
        e1.setUsername("testuser");
        when(repository.findById("del@a.com")).thenReturn(Optional.of(e1));
        service.deleteByEmail("del@a.com");
        verify(repository).delete(e1);
    }

    @Test void testDeleteByEmail_NotExists() {
        when(repository.findById("none@a.com")).thenReturn(Optional.empty());
        service.deleteByEmail("none@a.com");
        verify(repository, never()).delete(any());
    }

    @Test void testUpdate() {
        AddressEntry entry = new AddressEntry("update@a.com", "수정자", "010", "지인");
        entry.setUsername("testuser");
        service.update(entry);
        verify(repository).save(entry);
    }

    @Test void testGetAllSortedByNameAsc() {
        AddressEntry e1 = new AddressEntry("b@b.com", "홍길동", "010", "친구");
        AddressEntry e2 = new AddressEntry("a@a.com", "김철수", "010", "가족");
        e1.setUsername("testuser");
        e2.setUsername("testuser");
        when(repository.findAll()).thenReturn(List.of(e1, e2));
        List<AddressEntry> result = service.getAllSorted("name", "asc");
        assertEquals("김철수", result.get(0).getName());
    }

    @Test void testGetAllSortedByEmailDesc() {
        AddressEntry e1 = new AddressEntry("a@a.com", "김영희", "010", "가족");
        AddressEntry e2 = new AddressEntry("z@z.com", "박철수", "010", "직장");
        e1.setUsername("testuser");
        e2.setUsername("testuser");
        when(repository.findAll()).thenReturn(List.of(e1, e2));
        List<AddressEntry> result = service.getAllSorted("email", "desc");
        assertEquals("z@z.com", result.get(0).getEmail());
    }

    @Test void testGetAllSortedByCreatedAtAsc() {
        AddressEntry e1 = new AddressEntry("c@c.com", "홍길동", "010", "친구");
        AddressEntry e2 = new AddressEntry("d@d.com", "이영희", "010", "가족");
        e1.setUsername("testuser");
        e2.setUsername("testuser");
        e1.setCreatedAt(LocalDateTime.now().minusDays(1));
        e2.setCreatedAt(LocalDateTime.now());
        when(repository.findAll()).thenReturn(List.of(e2, e1));
        List<AddressEntry> result = service.getAllSorted("createdAt", "asc");
        assertEquals("c@c.com", result.get(0).getEmail());
    }

    @Test void testGetAllSorted_DefaultCase() {
        AddressEntry e1 = new AddressEntry("x@x.com", "홍길동", "010", "친구");
        AddressEntry e2 = new AddressEntry("y@y.com", "강감찬", "010", "직장");
        e1.setUsername("testuser");
        e2.setUsername("testuser");
        when(repository.findAll()).thenReturn(List.of(e1, e2));
        List<AddressEntry> result = service.getAllSorted("invalid", "asc");
        assertEquals("강감찬", result.get(0).getName());
    }

    @Test void testGetAllSorted_AscOrderBranch() {
        AddressEntry e1 = new AddressEntry("a@a.com", "김", "010", "친구");
        AddressEntry e2 = new AddressEntry("b@b.com", "박", "010", "친구");
        e1.setUsername("testuser");
        e2.setUsername("testuser");
        when(repository.findAll()).thenReturn(List.of(e2, e1));
        List<AddressEntry> result = service.getAllSorted("name", "asc"); // not "desc"
        assertEquals("김", result.get(0).getName());
    }

    @Test void testGetAllByUsername() {
        AddressEntry e1 = new AddressEntry("x@a.com", "이길동", "010", "가족");
        AddressEntry e2 = new AddressEntry("y@b.com", "홍승희", "010", "친구");
        e1.setUsername("user1");
        e2.setUsername("user2");
        when(repository.findAll()).thenReturn(List.of(e1, e2));
        List<AddressEntry> result = service.getAllByUsername("user1");
        assertEquals(1, result.size());
    }

    @Test void testFindByKeywordForUser() {
        AddressEntry e1 = new AddressEntry("hong@a.com", "홍길동", "010", "친구");
        e1.setUsername("testuser");
        when(repository.findByUsernameAndNameContainingIgnoreCaseOrUsernameAndEmailContainingIgnoreCase(
                "testuser", "홍", "testuser", "홍")).thenReturn(List.of(e1));
        List<AddressEntry> result = service.findByKeywordForUser("홍", "testuser");
        assertEquals(1, result.size());
    }

    @Test void testGetPagedEntries_Normal() {
        AddressEntry e1 = new AddressEntry("p@a.com", "페이지", "010", "기타");
        e1.setUsername("testuser");
        when(repository.findByUsername(eq("testuser"), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(e1)));
        var result = service.getPagedEntries("email", "asc", 0, 10);
        assertEquals(1, result.getContent().size());
    }

    @Test void testGetPagedEntries_InvalidSortField() {
        AddressEntry e1 = new AddressEntry("x@x.com", "무명", "010", "모름");
        e1.setUsername("testuser");
        when(repository.findByUsername(eq("testuser"), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(e1)));
        var result = service.getPagedEntries("invalidField", "desc", 0, 10);
        assertEquals(1, result.getContent().size());
    }
}
