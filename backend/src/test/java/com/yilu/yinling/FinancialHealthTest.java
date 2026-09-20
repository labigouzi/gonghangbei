package com.yilu.yinling;

import com.yilu.yinling.financial.health.FinancialHealthScoreCalculator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("demo")
class FinancialHealthTest {
    @Autowired MockMvc mockMvc;

    @Test
    void calculatesStableDemoHealthScoreWithFourBoundedDimensions() {
        var input = new FinancialHealthScoreCalculator.HealthInput(
                68,
                new BigDecimal("6000"),
                new BigDecimal("500000"),
                "稳健",
                "健康养老",
                "关注日常医疗保障"
        );

        var score = new FinancialHealthScoreCalculator().calculate(input);

        assertEquals(78, score.totalScore());
        assertEquals(4, score.dimensions().size());
        assertTrue(score.dimensions().stream().allMatch(item -> item.score() >= 0 && item.score() <= 100));
        assertTrue(score.disclaimer().contains("不构成投资建议"));
    }

    @Test
    void demoUserCanReadHealthScoreAndFamilySummary() throws Exception {
        String token = demoToken();

        mockMvc.perform(get("/api/v1/financial/health-score")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalScore").value(78))
                .andExpect(jsonPath("$.data.dimensions.length()").value(4))
                .andExpect(jsonPath("$.data.disclaimer").value(org.hamcrest.Matchers.containsString("不构成投资建议")));

        mockMvc.perform(get("/api/v1/financial/family-summary")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.elderName").value("演示管理员"))
                .andExpect(jsonPath("$.data.age").value(68))
                .andExpect(jsonPath("$.data.riskReminders.length()").value(org.hamcrest.Matchers.greaterThan(0)));
    }

    @Test
    void healthEndpointsRequireJwt() throws Exception {
        mockMvc.perform(get("/api/v1/financial/health-score")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/v1/financial/family-summary")).andExpect(status().isUnauthorized());
    }

    private String demoToken() throws Exception {
        String login = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content("{\"username\":\"admin\",\"password\":\"123456\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return new com.fasterxml.jackson.databind.ObjectMapper().readTree(login).path("data").path("token").asText();
    }
}
