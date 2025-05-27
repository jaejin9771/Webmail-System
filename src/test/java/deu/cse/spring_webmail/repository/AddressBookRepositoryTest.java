package deu.cse.spring_webmail.repository;

import deu.cse.spring_webmail.model.AddressEntry;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=none", // 테스트 중에 테이블 자동 변경 방지
        "spring.datasource.url=jdbc:mysql://localhost:3306/webmail?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF-8&serverTimezone=Asia/Seoul"
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AddressBookRepositoryTest {

    @Autowired
    private AddressBookRepository repository;

    private static final String TEST_EMAIL = "test1@example.com";
    private static final String TEST_USERNAME = "user1";

    @BeforeEach
    void setUp() {
        AddressEntry entry = new AddressEntry();
        entry.setEmail(TEST_EMAIL);
        entry.setName("Tester");
        entry.setPhone("010-0000-0000");
        entry.setCategory("Friend");
        entry.setUsername(TEST_USERNAME);
        repository.save(entry);
    }

    @AfterEach
    void tearDown() {
        repository.deleteById(TEST_EMAIL);
    }

    @Test
    @Order(1)
    void testExistsByEmailAndUsername() {
        boolean exists = repository.existsByEmailAndUsername(TEST_EMAIL, TEST_USERNAME);
        assertThat(exists).isTrue();
    }

    @Test
    @Order(2)
    void testFindByUsernameWithPaging() {
        var page = repository.findByUsername(TEST_USERNAME, PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().get(0).getUsername()).isEqualTo(TEST_USERNAME);
    }

    @Test
    @Order(3)
    void testFindByUsernameAndNameOrEmailContaining() {
        List<AddressEntry> result = repository.findByUsernameAndNameContainingIgnoreCaseOrUsernameAndEmailContainingIgnoreCase(
                TEST_USERNAME, "test",
                TEST_USERNAME, "test1@example.com"
        );
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getEmail()).isEqualTo(TEST_EMAIL);
    }
}
