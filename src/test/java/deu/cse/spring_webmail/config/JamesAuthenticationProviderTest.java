package deu.cse.spring_webmail.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JamesAuthenticationProviderTest {

    private JamesAuthenticationProvider provider;

    @BeforeEach
    void setUp() throws Exception {
        provider = Mockito.spy(new JamesAuthenticationProvider());

        // Reflection을 사용해 admin.id 값 설정
        Field adminIdField = JamesAuthenticationProvider.class.getDeclaredField("adminId");
        adminIdField.setAccessible(true);
        adminIdField.set(provider, "admin@admin.local");
    }

    @Test
    @DisplayName("정상 사용자 로그인 시 ROLE_USER 부여 확인")
    void testAuthenticateWithValidUser() {
        String username = "user@domain.com";
        String password = "password123";
        Authentication auth = new UsernamePasswordAuthenticationToken(username, password);

        doReturn(true).when(provider).authenticateWithJamesPOP3(username, password);
        Authentication result = provider.authenticate(auth);

        assertNotNull(result);
        assertEquals(username, result.getPrincipal());
        assertEquals(password, result.getCredentials());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    @DisplayName("관리자 로그인 시 ROLE_ADMIN 부여 확인")
    void testAuthenticateWithAdminUser() {
        String username = "admin@admin.local";
        String password = "adminpass";
        Authentication auth = new UsernamePasswordAuthenticationToken(username, password);

        doReturn(true).when(provider).authenticateWithJamesPOP3(username, password);
        Authentication result = provider.authenticate(auth);

        assertNotNull(result);
        assertEquals(username, result.getPrincipal());
        assertEquals(password, result.getCredentials());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("잘못된 비밀번호 입력 시 인증 실패 확인")
    void testAuthenticateWithInvalidCredentials() {
        String username = "wrong@domain.com";
        String password = "wrongpass";
        Authentication auth = new UsernamePasswordAuthenticationToken(username, password);

        doReturn(false).when(provider).authenticateWithJamesPOP3(username, password);

        assertThrows(BadCredentialsException.class, () -> {
            provider.authenticate(auth);
        });
    }

    @Test
    @DisplayName("supports() 메서드는 UsernamePasswordAuthenticationToken을 지원해야 함")
    void testSupportsUsernamePasswordAuthenticationToken() {
        assertTrue(provider.supports(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("supports() 메서드는 사용자 정의 Authentication 타입을 지원하지 않아야 함")
    void testSupportsOtherAuthenticationType() {
        class DummyAuth implements Authentication {
            @Override public String getName() { return null; }
            @Override public Object getPrincipal() { return null; }
            @Override public Object getDetails() { return null; }
            @Override public Object getCredentials() { return null; }
            @Override public boolean isAuthenticated() { return false; }
            @Override public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
                throw new IllegalArgumentException("DummyAuth는 인증 상태를 변경할 수 없습니다.");
            }
            @Override public java.util.Collection<org.springframework.security.core.GrantedAuthority> getAuthorities() {
                return null;
            }
        }

        assertFalse(provider.supports(DummyAuth.class));
    }
}
