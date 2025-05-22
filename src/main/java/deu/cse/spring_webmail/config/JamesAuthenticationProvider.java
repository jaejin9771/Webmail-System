package deu.cse.spring_webmail.config;

import jakarta.mail.Session;
import jakarta.mail.Store;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;

@Component
public class JamesAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        if (authenticateWithJamesPOP3(username, password)) {
            String role = "ROLE_USER";
            if ("admin@admin.local".equalsIgnoreCase(username)) {
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

    private boolean authenticateWithJamesPOP3(String username, String password) {
        try {
            System.out.println("POP3 try:");
            System.out.println("username: " + username);
            System.out.println("password:: " + password);

            Properties props = new Properties();
            props.put("mail.pop3.host", "localhost");
            props.put("mail.pop3.port", "110");

            Session session = Session.getDefaultInstance(props);
            Store store = session.getStore("pop3");

            store.connect(username, password);  // 여기가 실패함
            store.close();

            System.out.println("[DEBUG] 인증 성공");
            return true;
        } catch (Exception e) {
            System.out.println("[DEBUG] 인증 실패: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
