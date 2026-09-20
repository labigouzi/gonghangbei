package com.yilu.yinling;

import com.yilu.yinling.fraud.mapper.FraudDetectionRecordMapper;
import com.yilu.yinling.fraud.service.FraudDetectionService;
import com.yilu.yinling.fraud.vo.FraudResult;
import com.yilu.yinling.fraud.vo.RiskLevel;
import com.yilu.yinling.fraud.vo.FraudImageResult;
import com.yilu.yinling.fraud.ocr.OcrClient;
import com.yilu.yinling.fraud.ocr.OcrResult;
import com.yilu.yinling.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.mock.web.MockMultipartFile;
import com.yilu.yinling.ai.mapper.ConversationMapper;
import com.yilu.yinling.ai.mapper.MessageMapper;
import com.yilu.yinling.ai.service.AssistantService;
import com.yilu.yinling.profile.mapper.ElderlyProfileMapper;
import com.yilu.yinling.user.mapper.UserMapper;
import com.yilu.yinling.admin.service.AdminService;
import com.yilu.yinling.storage.FileStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FraudControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired ObjectMapper objectMapper;
    @MockBean FraudDetectionService fraudDetectionService;
    @MockBean FraudDetectionRecordMapper fraudDetectionRecordMapper;
    @MockBean ConversationMapper conversationMapper;
    @MockBean MessageMapper messageMapper;
    @MockBean AssistantService assistantService;
    @MockBean ElderlyProfileMapper elderlyProfileMapper;
    @MockBean UserMapper userMapper;
    @MockBean OcrClient ocrClient;
    @MockBean AdminService adminService;
    @MockBean FileStorageService fileStorageService;

    @Test
    void unauthenticatedFraudDetectionIsRejected() throws Exception {
        mockMvc.perform(post("/api/v1/fraud/detect/text")
                        .contentType("application/json")
                        .content("{\"content\":\"请点击链接\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedImageIsOcrParsedAndDetected() throws Exception {
        User user = new User(); user.setId(3L); user.setUsername("demo"); user.setPassword(passwordEncoder.encode("secret123")); user.setStatus(1);
        when(userMapper.selectOne(any())).thenReturn(user);
        when(ocrClient.recognize(any())).thenReturn(new OcrResult("您的养老金账户异常，请点击链接认证", "mock", 0.99));
        when(fileStorageService.upload(any())).thenReturn("http://localhost/files/pension.png");
        var expected = new FraudImageResult("您的养老金账户异常，请点击链接认证", "pension.png", new FraudResult(RiskLevel.HIGH, BigDecimal.valueOf(66), List.of("养老诈骗"), "原因", "建议"));
        when(fraudDetectionService.detectImage(eq(3L), anyString(), anyString(), anyString(), any(Long.class), anyString())).thenReturn(expected);
        String login = mockMvc.perform(post("/api/v1/auth/login").contentType("application/json").content("{\"username\":\"demo\",\"password\":\"secret123\"}")).andReturn().getResponse().getContentAsString();
        String token = objectMapper.readTree(login).path("data").path("token").asText();
        mockMvc.perform(multipart("/api/v1/fraud/detect/image").file(new MockMultipartFile("file", "pension.png", "image/png", new byte[]{1})).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        verify(fraudDetectionService).detectImage(eq(3L), eq("您的养老金账户异常，请点击链接认证"), eq("http://localhost/files/pension.png"), eq("pension.png"), eq(1L), eq("image/png"));
    }
}
