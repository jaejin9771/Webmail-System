package deu.cse.spring_webmail.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserAdminServiceTest {

    // RestTemplate을 Mock으로 선언하여 외부 요청을 막고 내부 로직만 테스트
    @Mock
    private RestTemplate restTemplate;

    // 테스트 대상 서비스
    private UserAdminService userAdminService;

    // 테스트용 설정값들 (application.yml의 값 대신 수동 입력)
    private final String baseUrl = "http://localhost:8000";
    private final String username = "admin";
    private final String password = "1234";

    /**
     * 테스트 실행 전마다 호출되는 초기화 메서드 Mockito의 Mock 객체 초기화 및 UserAdminService 인스턴스 생성
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // @Mock 사용 가능하게 초기화
        userAdminService = new UserAdminService(restTemplate, baseUrl, username, password);
    }

    /**
     * addUser() 성공 시 true를 반환하는지 검증
     */
    @Test
    void testAddUser_success() {
        String userId = "testuser";
        String userPassword = "testpass";
        String url = baseUrl + "/users/" + userId;

        // 성공적인 응답 시뮬레이션
        ResponseEntity<Void> response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        when(restTemplate.exchange(eq(url), eq(HttpMethod.PUT), any(HttpEntity.class), eq(Void.class)))
                .thenReturn(response);

        boolean result = userAdminService.addUser(userId, userPassword);

        assertTrue(result);
        verify(restTemplate, times(1)).exchange(eq(url), eq(HttpMethod.PUT), any(HttpEntity.class), eq(Void.class));
    }

    /**
     * addUser() 실패 시 false 반환 확인
     */
    @Test
    void testAddUser_failure() {
        String userId = "failuser";
        String password = "pass";

        // 예외 발생 시뮬레이션
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(Void.class)))
                .thenThrow(new RuntimeException("Server error"));

        boolean result = userAdminService.addUser(userId, password);

        assertFalse(result);
    }

    /**
     * deleteUsers(): 한 명이라도 실패하면 false를 반환하는지 확인
     */
    @Test
    void testDeleteUsers_partialFailure() {
        String[] userList = {"user1", "user2"};
        String url1 = baseUrl + "/users/user1";
        String url2 = baseUrl + "/users/user2";

        // 첫 번째는 성공, 두 번째는 실패하도록 설정
        when(restTemplate.exchange(eq(url1), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Void.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));
        when(restTemplate.exchange(eq(url2), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Void.class)))
                .thenThrow(new RuntimeException("delete error"));

        boolean result = userAdminService.deleteUsers(userList);

        assertFalse(result);
    }

    /**
     * deleteUsers(): 모든 사용자가 정상 삭제될 경우 true 반환
     */
    @Test
    void testDeleteUsers_allSuccess() {
        String[] userList = {"user1", "user2"};

        // 모든 사용자에 대해 성공적인 응답 설정
        for (String userId : userList) {
            String url = baseUrl + "/users/" + userId;
            when(restTemplate.exchange(eq(url), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Void.class)))
                    .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));
        }

        boolean result = userAdminService.deleteUsers(userList);

        assertTrue(result);
    }

    /**
     * getUserList(): 사용자 목록이 정상 반환되는 경우, 정렬된 리스트 반환 확인
     */
    @Test
    void testGetUserList_success() {
        String url = baseUrl + "/users";

        // 실제 James 서버에서 받는 JSON 문자열을 모의
        String mockJson = """
        [
          {"username": "bob"},
          {"username": "alice"}
        ]
        """;

        ResponseEntity<String> response = new ResponseEntity<>(mockJson, HttpStatus.OK);

        // RestTemplate mock 설정 (String.class 타입으로 응답)
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(response);

        // 실행
        List<String> result = userAdminService.getUserList();

        // 검증
        assertEquals(List.of("alice", "bob"), result);  // 정렬된 리스트
    }

    /**
     * getUserList(): 서버 오류로 예외 발생 시 빈 리스트 반환
     */
    @Test
    void testGetUserList_failure() {
        // 예외 발생 시뮬레이션
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                ArgumentMatchers.<ParameterizedTypeReference<Map<String, List<String>>>>any()
        )).thenThrow(new RuntimeException("get error"));

        List<String> result = userAdminService.getUserList();

        assertTrue(result.isEmpty());
    }
}
