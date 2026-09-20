package com.yilu.yinling;

import com.yilu.yinling.user.entity.User;
import com.yilu.yinling.user.mapper.UserMapper;
import com.yilu.yinling.profile.mapper.ElderlyProfileMapper;
import com.yilu.yinling.fraud.mapper.FraudDetectionRecordMapper;
import com.yilu.yinling.admin.service.AdminService;
import com.yilu.yinling.ai.service.AssistantService;
import com.yilu.yinling.ai.vo.ChatResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired ObjectMapper objectMapper;
    @MockBean UserMapper userMapper;
    @MockBean AssistantService assistantService;
    @MockBean ElderlyProfileMapper elderlyProfileMapper;
    @MockBean FraudDetectionRecordMapper fraudDetectionRecordMapper;
    @MockBean AdminService adminService;

    @Test
    void loginReturnsJwtForValidCredentials() throws Exception {
        User user = new User();
        user.setId(1L); user.setUsername("demo"); user.setPassword(passwordEncoder.encode("secret123"));
        user.setStatus(1);
        when(userMapper.selectOne(any())).thenReturn(user);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content("{\"username\":\"demo\",\"password\":\"secret123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.token").isNotEmpty());
    }

    @Test
    void loginTokenCanCallProtectedAiEndpoint() throws Exception {
        User user = new User();
        user.setId(1L); user.setUsername("demo"); user.setPassword(passwordEncoder.encode("secret123")); user.setStatus(1);
        when(userMapper.selectOne(any())).thenReturn(user);
        when(assistantService.chat(anyLong(), any())).thenReturn(new ChatResponse("养老规划建议", 7L, "请通过官方渠道核实", java.util.List.of("个人养老金基础知识")));

        String loginBody = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content("{\"username\":\"demo\",\"password\":\"secret123\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(loginBody);
        String token = root.path("data").path("token").asText();

        mockMvc.perform(post("/api/v1/assistant/chat")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content("{\"question\":\"老人如何做好养老规划？\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.answer").value("养老规划建议"))
                .andExpect(jsonPath("$.data.conversationId").value(7));
    }
}
