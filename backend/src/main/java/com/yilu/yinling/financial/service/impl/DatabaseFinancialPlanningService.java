package com.yilu.yinling.financial.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yilu.yinling.ai.client.LlmClient;
import com.yilu.yinling.financial.dto.FinancialPlanRequest;
import com.yilu.yinling.financial.entity.ElderlyFinancialPlan;
import com.yilu.yinling.financial.mapper.ElderlyFinancialPlanMapper;
import com.yilu.yinling.financial.prompt.FinancialPromptTemplate;
import com.yilu.yinling.financial.service.FinancialPlanCalculator;
import com.yilu.yinling.financial.service.FinancialPlanningService;
import com.yilu.yinling.financial.service.ProductRecommendationService;
import com.yilu.yinling.financial.vo.FinancialPlanResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.ObjectProvider;
import java.util.List;

@Service
@Profile("!demo")
public class DatabaseFinancialPlanningService implements FinancialPlanningService {
    private static final Logger log = LoggerFactory.getLogger(DatabaseFinancialPlanningService.class);
    private final FinancialPlanCalculator calculator;
    private final ProductRecommendationService products;
    private final ObjectProvider<ElderlyFinancialPlanMapper> planMapperProvider;
    private final LlmClient llmClient;
    private final ObjectMapper objectMapper;
    private final FinancialPromptTemplate prompt = new FinancialPromptTemplate();

    public DatabaseFinancialPlanningService(FinancialPlanCalculator calculator, ProductRecommendationService products,
                                            ObjectProvider<ElderlyFinancialPlanMapper> planMapperProvider, LlmClient llmClient, ObjectMapper objectMapper) {
        this.calculator = calculator; this.products = products; this.planMapperProvider = planMapperProvider; this.llmClient = llmClient; this.objectMapper = objectMapper;
    }

    @Override public FinancialPlanResponse generate(Long userId, FinancialPlanRequest request) {
        var calculation = calculator.calculate(request);
        var recommendations = products.recommend(request.age(), request.riskPreference(), request.retirementGoal());
        String fallback = prompt.fallback(request, calculation, recommendations);
        String report;
        try {
            report = llmClient.chat(prompt.build(request, calculation, recommendations), "请为我生成养老金融规划报告");
            if (report == null || report.isBlank()) report = fallback;
        } catch (RuntimeException exception) {
            log.warn("Financial planning LLM failed, using local report: {}", exception.getMessage());
            report = fallback;
        }
        FinancialPlanResponse response = response(null, calculation, recommendations, report);
        ElderlyFinancialPlan entity = entity(userId, request, calculation, response);
        ElderlyFinancialPlanMapper planMapper = planMapperProvider.getIfAvailable();
        if (planMapper != null) planMapper.insert(entity);
        return response(entity.getId(), calculation, recommendations, report);
    }

    private ElderlyFinancialPlan entity(Long userId, FinancialPlanRequest request, FinancialPlanCalculator.Calculation calculation, FinancialPlanResponse response) {
        ElderlyFinancialPlan entity = new ElderlyFinancialPlan();
        entity.setUserId(userId); entity.setAge(request.age()); entity.setMonthlyIncome(request.monthlyIncome()); entity.setAssetAmount(request.assetAmount());
        entity.setRiskPreference(request.riskPreference()); entity.setRetirementGoal(request.retirementGoal());
        entity.setMedicalNeed(calculation.medicalNeed()); entity.setTravelNeed(calculation.travelNeed());
        try { entity.setPlanResult(objectMapper.writeValueAsString(response)); }
        catch (JsonProcessingException exception) { entity.setPlanResult(response.report()); }
        return entity;
    }

    private FinancialPlanResponse response(Long id, FinancialPlanCalculator.Calculation calculation,
                                           List<FinancialPlanResponse.ProductRecommendation> recommendations, String report) {
        return new FinancialPlanResponse(id, calculation.riskLevel(), calculation.stage(),
                "根据您的年龄、收入、资产和风险偏好，建议先保障生活与养老安全，再安排灵活消费。",
                calculation.allocations(), recommendations, report,
                "规划和产品均为知识参考，不构成投资或购买建议，不承诺收益。",
                List.of("用户画像", "风险评估", "资金规划", "产品匹配", "AI报告"));
    }
}
