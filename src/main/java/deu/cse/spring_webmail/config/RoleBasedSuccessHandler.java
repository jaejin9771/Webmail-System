package deu.cse.spring_webmail.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class RoleBasedSuccessHandler implements AuthenticationSuccessHandler {

    private final ServletContext servletContext;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {
        String username = authentication.getName();

        // 사용자가 폼에 입력한 비밀번호를 직접 가져옴
        String password = request.getParameter("password");
        
        // 보안상 credentials(비밀번호)는 세션에 저장하지 않음
        request.getSession().setAttribute("username", username);
        request.getSession().setAttribute("password", password);
        request.getSession().setAttribute("host", "localhost"); // 필요 시 외부 설정으로 대체

        // 역할 기반 리다이렉트 설정
        String redirectUrl = "/main_menu";  // 기본은 사용자 메뉴
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if ("ROLE_ADMIN".equals(authority.getAuthority())) {
                redirectUrl = "/admin/admin_menu";
                break;
            }
        }

        String contextPath = servletContext.getContextPath();
        String fullRedirectPath = contextPath + redirectUrl;
        log.info("로그인 성공 - 사용자: {}, 리다이렉트: {}", username, fullRedirectPath);

        response.sendRedirect(fullRedirectPath);
    }
}
