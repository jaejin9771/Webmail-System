package deu.cse.spring_webmail.config;

import jakarta.mail.Session;
import jakarta.mail.Store;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;

@Slf4j
@Component
public class JamesAuthenticationProvider implements AuthenticationProvider {

    @Value("${admin.id}")
    private String adminId;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        if (authenticateWithJamesPOP3(username, password)) {
            String role = "ROLE_USER";
            if (adminId.equalsIgnoreCase(username)) {
                role = "ROLE_ADMIN";
            }

            return new UsernamePasswordAuthenticationToken(
                    username,
                    password,
                    List.of(new SimpleGrantedAuthority(role))
            );
        }

        throw new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다.");
    }

    protected boolean authenticateWithJamesPOP3(String username, String password) {
        try {
            log.debug("POP3 인증 시도:");
            log.debug("username: {}", username);
            log.debug("password: {}", password);  // 민감 정보이므로 실제 운영에서는 로그 기록 주의!

            Properties props = new Properties();
            props.put("mail.pop3.host", "localhost");
            props.put("mail.pop3.port", "110");

            Session session = Session.getDefaultInstance(props);
            Store store = session.getStore("pop3");

            store.connect(username, password);  // 인증 시도
            store.close();

            log.debug("[DEBUG] 인증 성공");
            return true;
        } catch (Exception e) {
            log.error("[DEBUG] 인증 실패: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
