package deu.cse.spring_webmail.model;

import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import jakarta.mail.internet.InternetAddress;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ImapAgentTest {

    @InjectMocks
    ImapAgent imapAgent;

    @Mock Store mockStore;
    @Mock Folder mockFolder;
    @Mock Message mockMessage;
    @Mock MimeMessage mockMimeMessage;
    @Mock HttpServletRequest mockRequest;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        imapAgent = spy(new ImapAgent("imap.test.com", "user@test.com", "password"));
        imapAgent.setRequest(mockRequest);
        imapAgent.setStore(mockStore);

        doReturn(true).when(imapAgent).connectToStore();
    }

    @Test
    void testGetSentMessages_success() throws Exception {
        when(mockStore.getFolder("Sent")).thenReturn(mockFolder);
        when(mockFolder.getMessageCount()).thenReturn(5);
        when(mockFolder.getMessages(anyInt(), anyInt())).thenReturn(new Message[]{mockMessage});
        doNothing().when(mockFolder).open(Folder.READ_ONLY);

        Message[] result = imapAgent.getSentMessages(1, 10);

        assertNotNull(result);
        assertEquals(1, result.length);
        verify(mockFolder).open(Folder.READ_ONLY);
    }

    @Test
    void testDeleteSentMessage_success() throws Exception {
        when(mockStore.getFolder("Sent")).thenReturn(mockFolder);
        when(mockFolder.getMessage(1)).thenReturn(mockMessage);

        doNothing().when(mockFolder).open(Folder.READ_WRITE);
        doNothing().when(mockMessage).setFlag(Flags.Flag.DELETED, true);
        doNothing().when(mockFolder).close(true);
        doNothing().when(mockStore).close();

        boolean result = imapAgent.deleteSentMessage(1, true);

        assertTrue(result);
    }

    @Test
    void testGetSentMessage_success() throws Exception {
        when(mockStore.getFolder("Sent")).thenReturn(mockFolder);
        when(mockFolder.getMessage(1)).thenReturn(mockMessage);
        doNothing().when(mockFolder).open(Folder.READ_ONLY);

        MessageFormatter mockFormatter = mock(MessageFormatter.class);
        when(mockFormatter.getMessage(mockMessage)).thenReturn("<p>HTML content</p>");
        when(mockFormatter.getSender()).thenReturn("sender@test.com");
        when(mockFormatter.getSubject()).thenReturn("Test Subject");
        when(mockFormatter.getBody()).thenReturn("This is body");
        
        String result = imapAgent.getSentMessage(1);

        assertNotNull(result);
        assertTrue(result.contains("exception"));
    }

    @Test
    void testGetSearchedSentMessages_subjectMatch() throws Exception {
        Message mockMessage1 = mock(Message.class);
        Message mockMessage2 = mock(Message.class);

        when(mockStore.getFolder("Sent")).thenReturn(mockFolder);
        when(mockFolder.getMessageCount()).thenReturn(2);
        when(mockFolder.getMessages(1, 2)).thenReturn(new Message[]{mockMessage1, mockMessage2});
        doNothing().when(mockFolder).open(Folder.READ_ONLY);

        MessageParser parser1 = mock(MessageParser.class);
        MessageParser parser2 = mock(MessageParser.class);
        when(parser1.getSubject()).thenReturn("Hello Java");
        when(parser2.getSubject()).thenReturn("Not matched");

        Message[] result = imapAgent.getSearchedSentMessages("subject", "Java", 1, 10);

        assertNotNull(result);
    }

    @Test
    void testSaveToSentFolder_success() throws Exception {
        when(mockStore.getFolder("Sent")).thenReturn(mockFolder);
        when(mockMimeMessage.getAllRecipients()).thenReturn(new Address[]{new InternetAddress("to@test.com")});

        doNothing().when(mockFolder).open(Folder.READ_WRITE);
        doNothing().when(mockFolder).appendMessages(any());
        doNothing().when(mockFolder).close(false);
        doNothing().when(mockStore).close();

        imapAgent.saveToSentFolder(mockMimeMessage);

        verify(mockFolder).appendMessages(any());
    }
}
