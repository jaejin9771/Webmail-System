package deu.cse.spring_webmail.control;

import deu.cse.spring_webmail.repository.AddressBookRepository;
import deu.cse.spring_webmail.service.AddressBookService;
import deu.cse.spring_webmail.service.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MailService mailService;

    private MockHttpSession session;

    @MockBean
    private AddressBookRepository addressBookRepository;

    @MockBean
    private AddressBookService addressBookService;

    @MockBean
    private AddressbookController addressbookController;


    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        session.setAttribute("host", "localhost");
        session.setAttribute("username", "testuser");
        session.setAttribute("password", "password");
    }

    @Test
    @DisplayName("GET /main_menu returns view with mail list")
    void testShowMainMenu() throws Exception {
        when(mailService.getMessageTable(anyString(), anyString(), anyString(), anyInt(), anyInt(), any(), any(), any()))
                .thenReturn("<tr><td>Sample Mail</td></tr>");

        mockMvc.perform(get("/main_menu").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("main_menu"))
                .andExpect(model().attributeExists("messageList"))
                .andExpect(model().attribute("currentPage", 1));

        verify(mailService, times(1)).getMessageTable(
                eq("localhost"), eq("testuser"), eq("password"), eq(1), eq(20),
                any(), any(), any()
        );
    }

    @Test
    @DisplayName("GET /logout invalidates session and redirects")
    void testLogout() throws Exception {
        mockMvc.perform(get("/logout").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main_menu"));
    }

    @Test
    @DisplayName("GET /login_fail returns login_fail view")
    void testLoginFail() throws Exception {
        mockMvc.perform(get("/login_fail"))
                .andExpect(status().isOk())
                .andExpect(view().name("login_fail"));
    }
}
