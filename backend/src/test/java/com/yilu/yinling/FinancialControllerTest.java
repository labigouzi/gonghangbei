package com.yilu.yinling;

import com.yilu.yinling.admin.service.AdminService;
import com.yilu.yinling.ai.service.AssistantService;
import com.yilu.yinling.financial.service.FinancialPlanningService;
import com.yilu.yinling.financial.vo.FinancialPlanResponse;
import com.yilu.yinling.fraud.mapper.FraudDetectionRecordMapper;
import com.yilu.yinling.profile.mapper.ElderlyProfileMapper;
import com.yilu.yinling.security.JwtTokenProvider;
import com.yilu.yinling.user.mapper.UserMapper;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FinancialControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired JwtTokenProvider tokens;
    @MockBean FinancialPlanningService planningService;
    @MockBean UserMapper userMapper;
    @MockBean AssistantService assistantService;
    @MockBean ElderlyProfileMapper profileMapper;
    @MockBean FraudDetectionRecordMapper fraudMapper;
    @MockBean AdminService adminService;

    @Test
    void authenticatedUserCanGeneratePlan() throws Exception {
        when(planningService.generate(eq(1L), any())).thenReturn(response());
        String token = tokens.create(1L, "admin", List.of("ADMIN", "USER"));

        mockMvc.perform(post("/api/v1/financial/plan")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content("""
                                {"age":68,"monthlyIncome":6000,"assetAmount":500000,
                                 "riskPreference":"稳健","retirementGoal":"健康养老"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.planId").value(1))
                .andExpect(jsonPath("$.data.riskLevel").value("LOW"))
                .andExpect(jsonPath("$.data.financialChain.length()").value(5));
    }

    @Test
    void unauthenticatedUserCannotGeneratePlan() throws Exception {
        mockMvc.perform(post("/api/v1/financial/plan")
                        .contentType("application/json")
                        .content("{\"age\":68,\"monthlyIncome\":6000,\"assetAmount\":500000,\"riskPreference\":\"稳健\",\"retirementGoal\":\"健康养老\"}"))
                .andExpect(status().isUnauthorized());
    }

    private FinancialPlanResponse response() {
        return new FinancialPlanResponse(1L, "LOW", "退休初期", "规划摘要",
                List.of(new FinancialPlanResponse.Allocation("养老保障资金", new BigDecimal("300000.00"), 60, "养老保障")),
                List.of(), "规划报告", "风险提示", List.of("用户画像", "风险评估", "资金规划", "产品匹配", "AI报告"));
    }
}
