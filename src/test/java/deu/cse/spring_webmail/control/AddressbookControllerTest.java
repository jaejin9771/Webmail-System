package deu.cse.spring_webmail.control;

import deu.cse.spring_webmail.model.AddressEntry;
import deu.cse.spring_webmail.service.AddressBookService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AddressbookController.class)
@AutoConfigureMockMvc
public class AddressbookControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AddressBookService addressBookService;

    @Test
    @WithMockUser(username = "testuser")
    void testGetAddressBook_Default() throws Exception {
        List<AddressEntry> dummyList = List.of(
                new AddressEntry("a@a.com", "홍길동", "010", "친구")
        );
        Page<AddressEntry> dummyPage = new PageImpl<>(dummyList);
        when(addressBookService.getPagedEntries(any(), any(), anyInt(), anyInt())).thenReturn(dummyPage);

        mockMvc.perform(get("/addressbook"))
                .andExpect(status().isOk())
                .andExpect(view().name("addressbook/addressbook"))
                .andExpect(model().attributeExists("addressList"))
                .andExpect(model().attributeExists("username"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetAddressBook_WithEditEmail() throws Exception {
        when(addressBookService.getByEmail("edit@example.com"))
                .thenReturn(new AddressEntry("edit@example.com", "김편집", "010", "기타"));

        Page<AddressEntry> dummyPage = new PageImpl<>(List.of());
        when(addressBookService.getPagedEntries(any(), any(), anyInt(), anyInt())).thenReturn(dummyPage);

        mockMvc.perform(get("/addressbook").param("editEmail", "edit@example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("addressbook/addressbook"))
                .andExpect(model().attributeExists("addressEntry"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetAddressBook_WithQuery() throws Exception {
        when(addressBookService.search("홍길동")).thenReturn(
                List.of(new AddressEntry("hong@a.com", "홍길동", "010", "친구"))
        );

        mockMvc.perform(get("/addressbook").param("query", "홍길동"))
                .andExpect(status().isOk())
                .andExpect(view().name("addressbook/addressbook"))
                .andExpect(model().attributeExists("addressList"))
                .andExpect(model().attribute("query", "홍길동"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testPostAddressBook_NewEntry() throws Exception {
        when(addressBookService.existsByEmail("test@example.com")).thenReturn(false);

        mockMvc.perform(post("/addressbook")
                        .param("email", "test@example.com")
                        .param("name", "홍길동")
                        .param("phone", "010-1234-5678")
                        .param("category", "친구")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/addressbook"));

        verify(addressBookService).add(any(AddressEntry.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testPostAddressBook_Duplicate() throws Exception {
        when(addressBookService.existsByEmail("test@example.com")).thenReturn(true);

        mockMvc.perform(post("/addressbook")
                        .param("email", "test@example.com")
                        .param("name", "홍길동")
                        .param("phone", "010-1234-5678")
                        .param("category", "친구")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/addressbook?duplicate=true"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testPostAddressBook_EditWithEmailChange() throws Exception {
        when(addressBookService.existsByEmail("new@example.com")).thenReturn(false);

        mockMvc.perform(post("/addressbook")
                        .param("originalEmail", "old@example.com")
                        .param("email", "new@example.com")
                        .param("name", "홍길동")
                        .param("phone", "010-1234-5678")
                        .param("category", "친구")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/addressbook"));

        verify(addressBookService).deleteByEmail("old@example.com");
        verify(addressBookService).add(any(AddressEntry.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testDeleteAddress() throws Exception {
        mockMvc.perform(post("/addressbook/delete")
                        .param("email", "test@example.com")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/addressbook"));

        verify(addressBookService).deleteByEmail("test@example.com");
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetEmailsFiltered() throws Exception {
        List<AddressEntry> result = List.of(
                new AddressEntry("test@webmail.com", "홍길동", "010", "친구")
        );
        result.get(0).setUsername("testuser");

        when(addressBookService.findByKeywordForUser(eq("홍"), eq("testuser"))).thenReturn(result);

        mockMvc.perform(get("/api/addressbook/emails").param("q", "홍"))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"홍길동 <test@webmail.com>\"]"));
    }
}
