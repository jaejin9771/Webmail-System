package deu.cse.spring_webmail.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserAdminService {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String username;
    private final String password;

    /**
     * 생성자 주입 방식으로 외부 설정값 및 RestTemplate을 주입받음
     *
     * @param restTemplate REST API 호출을 위한 객체
     * @param baseUrl James Web Admin API 기본 주소
     * @param username 관리자 인증용 사용자 이름
     * @param password 관리자 인증용 비밀번호
     */
    public UserAdminService(RestTemplate restTemplate,
                            @Value("${james.webadmin.base-url}") String baseUrl,
                            @Value("${james.webadmin.auth.id}") String username,
                            @Value("${james.webadmin.auth.password}") String password) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.username = username;
        this.password = password;
    }

    /**
     * Basic 인증 헤더를 생성
     *
     * @return 인증이 포함된 HttpHeaders 객체
     */
    private HttpHeaders createAuthHeaders() {
        String auth = username + ":" + password;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodedAuth);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    /**
     * James 서버에 새로운 사용자를 추가
     *
     * @param userId       생성할 사용자 ID
     * @param userPassword 생성할 사용자의 비밀번호
     * @return 성공 시 true, 실패 시 false
     */
    public boolean addUser(String userId, String userPassword) {
        String url = baseUrl + "/users/" + userId;

        String jsonBody = String.format("{\"password\": \"%s\"}", userPassword);
        HttpEntity<String> request = new HttpEntity<>(jsonBody, createAuthHeaders());

        try {
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.PUT, request, Void.class);
            return response.getStatusCode() == HttpStatus.NO_CONTENT;
        } catch (Exception e) {
            log.error("addUser() failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 사용자 목록에서 여러 사용자를 삭제
     *
     * @param userList 삭제할 사용자 ID 배열
     * @return 모든 사용자 삭제 성공 시 true, 하나라도 실패하면 false
     */
    public boolean deleteUsers(String[] userList) {
        boolean allSuccess = true;

        for (String userId : userList) {
            String url = baseUrl + "/users/" + userId;
            HttpEntity<Void> request = new HttpEntity<>(createAuthHeaders());

            try {
                ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, request, Void.class);
                if (response.getStatusCode() != HttpStatus.NO_CONTENT) {
                    allSuccess = false;
                }
            } catch (Exception e) {
                log.error("deleteUser({}) failed: {}", userId, e.getMessage());
                allSuccess = false;
            }
        }

        return allSuccess;
    }

    /**
     * James 서버에 등록된 모든 사용자 목록을 조회
     *
     * @return 사용자 ID 리스트 (오름차순 정렬), 실패 시 빈 리스트 반환
     */
    public List<String> getUserList() {
        String url = baseUrl + "/users";
        HttpEntity<Void> request = new HttpEntity<>(createAuthHeaders());

        try {
            ResponseEntity<Map<String, List<String>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    new ParameterizedTypeReference<>() {}
            );

            List<String> users = response.getBody().getOrDefault("users", List.of());
            return users.stream().sorted().collect(Collectors.toList());

        } catch (Exception e) {
            log.error("getUserList() failed: {}", e.getMessage());
            return List.of();
        }
    }
}