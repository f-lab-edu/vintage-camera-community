package com.zerozone.vintage.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerozone.vintage.account.AccountRepository;
import com.zerozone.vintage.account.AccountService;
import com.zerozone.vintage.account.SignUpForm;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ChatApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChatMessageService chatMessageService;

    @MockBean
    private ChatRoomService chatRoomService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    @BeforeEach
    void setup() {
        accountRepository.deleteAll();

        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickName("zerozone");
        signUpForm.setEmail("00zero0zone00@gmail.com");
        signUpForm.setPassword("test0000!");
        accountService.newAccountProcess(signUpForm);

        SignUpForm otherUserForm = new SignUpForm();
        otherUserForm.setNickName("otherUser");
        otherUserForm.setEmail("otherUser@google.com");
        otherUserForm.setPassword("password123!!");
        accountService.newAccountProcess(otherUserForm);
    }

    @Test
    @WithUserDetails(value = "zerozone", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void sendMessage() throws Exception {
        ChatMessage message = new ChatMessage();
        message.setAuthorId(1L);
        message.setAuthorName("zerozone");
        message.setOtherUserId(2L);
        message.setContent("하이루!");

        ChatMessage savedMessage = new ChatMessage();
        savedMessage.setId(1L);
        savedMessage.setAuthorId(1L);
        savedMessage.setAuthorName("zerozone");
        savedMessage.setOtherUserId(2L);
        savedMessage.setContent("하이루!");
        savedMessage.setRoomId(1L);

        Mockito.when(chatMessageService.saveMessage(Mockito.any(ChatMessage.class))).thenReturn(savedMessage);
        Mockito.when(chatRoomService.getOrCreateRoomId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(1L);

        mockMvc.perform(post("/api/chat/sendMessage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(message))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorName").value("zerozone"))
                .andExpect(jsonPath("$.content").value("하이루!"));
    }

    @Test
    @WithUserDetails(value = "zerozone", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void getMessages() throws Exception {
        ChatMessage message1 = new ChatMessage();
        message1.setId(1L);
        message1.setAuthorId(1L);
        message1.setAuthorName("zerozone");
        message1.setOtherUserId(2L);
        message1.setContent("하이루!");
        message1.setRoomId(1L);

        ChatMessage message2 = new ChatMessage();
        message2.setId(2L);
        message2.setAuthorId(2L);
        message2.setAuthorName("otherUser");
        message2.setOtherUserId(1L);
        message2.setContent("방가!");
        message2.setRoomId(1L);

        List<ChatMessage> messages = List.of(message1, message2);

        Mockito.when(chatMessageService.getMessagesByRoomId(Mockito.anyLong())).thenReturn(messages);

        mockMvc.perform(get("/api/chat/messages?roomId=1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].authorName").value("zerozone"))
                .andExpect(jsonPath("$.data[0].content").value("하이루!"))
                .andExpect(jsonPath("$.data[1].authorName").value("otherUser"))
                .andExpect(jsonPath("$.data[1].content").value("방가!"));
    }

    @Test
    @WithUserDetails(value = "zerozone", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void getRoomId() throws Exception {
        Mockito.when(chatRoomService.getRoomId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(
                Optional.of(new ChatRoom(1L, 1L, 2L, 0L)));

        mockMvc.perform(get("/api/chat/roomId?otherUserId=2")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(1L));
    }

    @Test
    @WithUserDetails(value = "zerozone", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createRoom() throws Exception {
        Mockito.when(chatRoomService.createRoom(Mockito.anyLong(), Mockito.anyLong())).thenReturn(1L);

        mockMvc.perform(post("/api/chat/room")
                        .param("otherUserId", "2")
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data").value(1L));
    }
}
