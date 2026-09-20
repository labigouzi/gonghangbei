package com.yilu.yinling;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("demo")
class FinancialDemoIntegrationTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void demoLoginAndPlanningWorkWithoutExternalServices() throws Exception {
        String login = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content("{\"username\":\"admin\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String token = objectMapper.readTree(login).path("data").path("token").asText();

        mockMvc.perform(post("/api/v1/financial/plan")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content("""
                                {"age":68,"monthlyIncome":6000,"assetAmount":500000,
                                 "riskPreference":"稳健","retirementGoal":"健康养老"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.riskLevel").value("LOW"))
                .andExpect(jsonPath("$.data.retirementStage").value("退休初期"))
                .andExpect(jsonPath("$.data.allocation[1].amount").value(300000.00))
                .andExpect(jsonPath("$.data.report").value(org.hamcrest.Matchers.containsString("【风险提醒】")))
                .andExpect(jsonPath("$.data.financialChain.length()").value(5));
    }

    @Test
    void demoFinancialAgentReturnsSixStepsRiskAndKnowledgeSources() throws Exception {
        String token = demoToken();
        mockMvc.perform(post("/api/v1/financial/agent/chat")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content("{\"message\":\"我68岁，有50万存款，每月退休金6000元，想稳健养老\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.intent").value("RETIREMENT_PLAN"))
                .andExpect(jsonPath("$.data.agentChain.length()").value(6))
                .andExpect(jsonPath("$.data.agentChain[0].name").value("意图识别Agent"))
                .andExpect(jsonPath("$.data.agentChain[5].name").value("报告生成Agent"))
                .andExpect(jsonPath("$.data.financialAnalysis.riskAssessment.score").value(28))
                .andExpect(jsonPath("$.data.financialAnalysis.riskAssessment.level").value("LOW"))
                .andExpect(jsonPath("$.data.knowledgeSources.length()").value(3));
    }

    @Test
    void unauthenticatedFinancialAgentIsRejected() throws Exception {
        mockMvc.perform(post("/api/v1/financial/agent/chat")
                        .contentType("application/json")
                        .content("{\"message\":\"如何养老\"}"))
                .andExpect(status().isUnauthorized());
    }

    private String demoToken() throws Exception {
        String login = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content("{\"username\":\"admin\",\"password\":\"123456\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(login).path("data").path("token").asText();
    }
}
