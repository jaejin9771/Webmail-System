package deu.cse.spring_webmail.model;

import jakarta.mail.*;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class Pop3AgentTest {

    private Pop3Agent pop3Agent;
    private Session mockSession;
    private Store mockStore;
    private Folder mockFolder;
    private Message mockMessage;
    private HttpServletRequest mockRequest;
    private MessageFormatter mockFormatter;

    @BeforeEach
    void setUp() throws Exception {
        pop3Agent = new Pop3Agent("localhost", "user", "pass");

        // mock 객체 생성
        mockSession = mock(Session.class);
        mockStore = mock(Store.class);
        mockFolder = mock(Folder.class);
        mockMessage = mock(Message.class);
        mockRequest = mock(HttpServletRequest.class);
        mockFormatter = mock(MessageFormatter.class);

        // Pop3Agent에 mock 주입
        pop3Agent.setSession(mockSession);
        pop3Agent.setStore(mockStore);
        pop3Agent.setRequest(mockRequest);
        pop3Agent.setMessageFormatter(mockFormatter);

        // 기본 store 동작 설정
        when(mockSession.getStore("pop3")).thenReturn(mockStore);
        when(mockStore.getFolder("INBOX")).thenReturn(mockFolder);
    }

    @Test
    @DisplayName("validate() 성공 테스트")
    void testValidateSuccess() throws Exception {
        doNothing().when(mockStore).connect(anyString(), anyString(), anyString());
        doNothing().when(mockStore).close();

        boolean result = pop3Agent.validate();

        assertTrue(result);
    }

    @Test
    @DisplayName("getMessages() - 메시지 3개 반환")
    void testGetMessages() throws Exception {
        Message[] messages = new Message[]{mock(Message.class), mock(Message.class), mock(Message.class)};

        when(mockStore.getFolder("INBOX")).thenReturn(mockFolder);
        when(mockFolder.getMessageCount()).thenReturn(10);
        when(mockFolder.getMessages(8, 10)).thenReturn(messages);  // page = 1, pageSize = 3
        when(mockFolder.isOpen()).thenReturn(true);

        Message[] result = pop3Agent.getMessages(1, 3);

        assertNotNull(result);
        assertEquals(3, result.length);
    }

    @Test
    @DisplayName("getMessage() - 단일 메시지 내용 포맷 반환")
    void testGetMessage() throws Exception {
        when(mockFolder.getMessage(anyInt())).thenReturn(mockMessage);
        when(mockFormatter.getMessage(mockMessage)).thenReturn("formatted message");
        when(mockFormatter.getSender()).thenReturn("sender@example.com");
        when(mockFormatter.getSubject()).thenReturn("Test Subject");
        when(mockFormatter.getBody()).thenReturn("This is the body");

        String result = pop3Agent.getMessage(1);

        assertTrue(result.contains("formatted message"));
        assertEquals("sender@example.com", pop3Agent.getSender());
        assertEquals("Test Subject", pop3Agent.getSubject());
        assertEquals("This is the body", pop3Agent.getBody());
    }

    @Test
    @DisplayName("paginateMessages() - 5개 중 2~4만 추출")
    void testPaginateMessages() {
        List<Message> messageList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            messageList.add(mock(Message.class));
        }

        Message[] result = pop3Agent.paginateMessages(messageList, 2, 3);  // page 2, size 3 → index 3~5

        assertEquals(2, result.length);  // index 3~4 (index 5는 없음)
    }

    @Test
    @DisplayName("getTotalMessageCount() - 총 메시지 수 반환")
    void testGetTotalMessageCount() throws Exception {
        when(mockFolder.getMessageCount()).thenReturn(20);

        int count = pop3Agent.getTotalMessageCount();

        assertEquals(20, count);  // 0이 나오면 connectToStore 실패
    }
}
