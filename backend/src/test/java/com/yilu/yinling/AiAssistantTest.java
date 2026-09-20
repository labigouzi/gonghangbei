package com.yilu.yinling;

import com.yilu.yinling.ai.client.MockLlmClient;
import com.yilu.yinling.ai.mapper.ConversationMapper;
import com.yilu.yinling.ai.mapper.MessageMapper;
import com.yilu.yinling.ai.service.AssistantService;
import com.yilu.yinling.user.mapper.UserMapper;
import com.yilu.yinling.profile.mapper.ElderlyProfileMapper;
import com.yilu.yinling.fraud.mapper.FraudDetectionRecordMapper;
import com.yilu.yinling.admin.service.AdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AiAssistantTest {
    @Autowired MockMvc mockMvc;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired MockLlmClient mockLlmClient;
    @MockBean UserMapper userMapper;
    @MockBean ConversationMapper conversationMapper;
    @MockBean MessageMapper messageMapper;
    @MockBean AssistantService assistantService;
    @MockBean ElderlyProfileMapper elderlyProfileMapper;
    @MockBean FraudDetectionRecordMapper fraudDetectionRecordMapper;
    @MockBean AdminService adminService;

    @Test
    void mockLlmReturnsSafeElderlyFinanceAnswer() {
        String answer = mockLlmClient.chat("system", "如何规划养老");
        org.junit.jupiter.api.Assertions.assertTrue(answer.contains("养老金"));
        org.junit.jupiter.api.Assertions.assertTrue(answer.contains("不构成投资"));
    }

    @Test
    void unauthenticatedChatIsRejected() throws Exception {
        mockMvc.perform(post("/api/v1/assistant/chat")
                        .contentType("application/json")
                        .content("{\"question\":\"如何规划养老？\"}"))
                .andExpect(status().isUnauthorized());
    }
}
