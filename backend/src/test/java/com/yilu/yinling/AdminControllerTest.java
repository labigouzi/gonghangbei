package com.yilu.yinling;

import com.yilu.yinling.admin.service.AdminService;
import com.yilu.yinling.admin.vo.OverviewStats;
import com.yilu.yinling.ai.mapper.ConversationMapper;
import com.yilu.yinling.ai.mapper.MessageMapper;
import com.yilu.yinling.ai.service.AssistantService;
import com.yilu.yinling.fraud.mapper.FraudDetectionRecordMapper;
import com.yilu.yinling.profile.mapper.ElderlyProfileMapper;
import com.yilu.yinling.user.entity.User;
import com.yilu.yinling.user.mapper.UserMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminControllerTest {
    @Autowired MockMvc mockMvc; @Autowired PasswordEncoder encoder; @Autowired ObjectMapper objectMapper;
    @MockBean UserMapper userMapper; @MockBean AdminService adminService; @MockBean AssistantService assistantService;
    @MockBean ConversationMapper conversationMapper; @MockBean MessageMapper messageMapper;
    @MockBean FraudDetectionRecordMapper fraudMapper; @MockBean ElderlyProfileMapper profileMapper;

    @Test void adminCanAccessOverview() throws Exception {
        when(userMapper.selectOne(any())).thenReturn(user(1L)); when(userMapper.selectRoleCodes(1L)).thenReturn(List.of("ADMIN"));
        when(adminService.overview()).thenReturn(new OverviewStats(1, 2, 3, 1));
        String token = login();
        mockMvc.perform(get("/api/v1/admin/dashboard/overview").header("Authorization", "Bearer " + token)).andExpect(status().isOk());
    }
    @Test void ordinaryUserIsForbiddenFromAdmin() throws Exception {
        when(userMapper.selectOne(any())).thenReturn(user(2L)); when(userMapper.selectRoleCodes(2L)).thenReturn(List.of("USER"));
        String token = login();
        mockMvc.perform(get("/api/v1/admin/dashboard/overview").header("Authorization", "Bearer " + token)).andExpect(status().isForbidden());
    }
    private String login() throws Exception { String body=mockMvc.perform(post("/api/v1/auth/login").contentType("application/json").content("{\"username\":\"demo\",\"password\":\"secret123\"}")).andReturn().getResponse().getContentAsString(); JsonNode root=objectMapper.readTree(body); return root.path("data").path("token").asText(); }
    private User user(Long id) { User u=new User(); u.setId(id); u.setUsername("demo"); u.setPassword(encoder.encode("secret123")); u.setStatus(1); return u; }
}
