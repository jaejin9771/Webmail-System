package deu.cse.spring_webmail.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SpringSecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("로그인 페이지 접근 테스트")
    void testLoginPageAccessible() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));  // "/" 경로에서 로그인 페이지 반환 가정
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("ROLE_ADMIN 사용자가 관리자 페이지 접근")
    void testAdminAccessWithAdminRole() throws Exception {
        mockMvc.perform(get("/admin/admin_menu"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("ROLE_USER 사용자가 관리자 페이지 접근 시 거부")
    void testAdminAccessDeniedForUserRole() throws Exception {
        mockMvc.perform(get("/admin/admin_menu"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("ROLE_USER 사용자가 사용자 메뉴 접근")
    void testUserAccessWithUserRole() throws Exception {
        mockMvc.perform(get("/main_menu"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("로그인 실패 시 /login_fail로 리다이렉션")
    void testLoginFailureRedirect() throws Exception {
        mockMvc.perform(get("/login_fail"))
                .andExpect(status().isOk())
                .andExpect(view().name("login_fail"));  // 실패 뷰 이름
    }

    @Test
    @DisplayName("정적 리소스 접근 필터 제외 확인")
    void testStaticResourcesIgnored() throws Exception {
        mockMvc.perform(get("/css/main_style.css"))
                .andExpect(status().isOk());  // static resource가 필터 제외되는지 확인
    }
}
