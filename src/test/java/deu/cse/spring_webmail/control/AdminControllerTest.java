package deu.cse.spring_webmail.control;

import deu.cse.spring_webmail.repository.AddressBookRepository;
import deu.cse.spring_webmail.service.UserAdminService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser(username = "admin", roles = {"ADMIN"})
@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @MockBean
    private AddressBookRepository addressBookRepository;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserAdminService userAdminService;

    @Test
    @DisplayName("GET /admin/admin_menu - 사용자 목록 페이지 반환")
    void testAdminMenu() throws Exception {
        when(userAdminService.getUserList()).thenReturn(List.of("user1@domain.com", "user2@domain.com"));

        mockMvc.perform(get("/admin/admin_menu"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin_menu"))
                .andExpect(model().attributeExists("userList"));
    }

    @Test
    @DisplayName("GET /admin/add_user - 사용자 추가 페이지 반환")
    void testAddUserPage() throws Exception {
        mockMvc.perform(get("/admin/add_user"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/add_user"));
    }

    @Test
    @DisplayName("POST /admin/add_user - 사용자 추가 성공")
    void testAddUserSuccess() throws Exception {
        when(userAdminService.addUser("newuser", "password")).thenReturn(true);

        mockMvc.perform(post("/admin/add_user")
                        .param("id", "newuser")
                        .param("password", "password")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/admin_menu"))
                .andExpect(flash().attribute("msg", "사용자(newuser) 추가 성공"));
    }

    @Test
    @DisplayName("POST /admin/add_user - 사용자 추가 실패")
    void testAddUserFail() throws Exception {
        when(userAdminService.addUser("existing", "password")).thenReturn(false);

        mockMvc.perform(post("/admin/add_user")
                        .param("id", "existing")
                        .param("password", "password")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/admin_menu"))
                .andExpect(flash().attribute("msg", "사용자(existing) 추가 실패"));
    }

    @Test
    @DisplayName("GET /admin/delete_user - 사용자 삭제 페이지 반환")
    void testDeleteUserPage() throws Exception {
        when(userAdminService.getUserList()).thenReturn(List.of("user1", "user2"));

        mockMvc.perform(get("/admin/delete_user"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/delete_user"))
                .andExpect(model().attributeExists("userList"));
    }

    @Test
    @DisplayName("POST /admin/delete_user - 사용자 삭제 성공")
    void testDeleteUserSuccess() throws Exception {
        when(userAdminService.deleteUsers(new String[]{"user1", "user2"})).thenReturn(true);

        mockMvc.perform(post("/admin/delete_user")
                        .param("selectedUsers", "user1", "user2")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/admin_menu"))
                .andExpect(flash().attribute("msg", "선택된 사용자를 성공적으로 삭제했습니다."));
    }

    @Test
    @DisplayName("POST /admin/delete_user - 사용자 삭제 실패")
    void testDeleteUserFail() throws Exception {
        when(userAdminService.deleteUsers(new String[]{"user1"})).thenReturn(false);

        mockMvc.perform(post("/admin/delete_user")
                        .param("selectedUsers", "user1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/admin_menu"))
                .andExpect(flash().attribute("msg", "일부 사용자를 삭제하지 못했습니다."));
    }
}
