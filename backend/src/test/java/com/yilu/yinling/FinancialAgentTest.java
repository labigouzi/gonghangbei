package com.yilu.yinling;

import com.yilu.yinling.ai.agent.IntentRecognitionAgent;
import com.yilu.yinling.ai.agent.RiskScoreCalculator;
import com.yilu.yinling.ai.agent.UserProfileAgent;
import com.yilu.yinling.ai.agent.AgentContext;
import com.yilu.yinling.ai.agent.model.AgentModels.IntentResult;
import com.yilu.yinling.profile.service.ProfileService;
import com.yilu.yinling.financial.mapper.ElderlyFinancialPlanMapper;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import com.yilu.yinling.financial.dto.FinancialPlanRequest;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class FinancialAgentTest {
    @Test void recognizesRetirementPlanIntent() {
        var result = new IntentRecognitionAgent().recognize("我68岁，有50万存款，退休后如何养老？");
        assertEquals("RETIREMENT_PLAN", result.intent());
        assertTrue(result.confidence() >= 0.9);
    }

    @Test void recognizesFraudIntent() {
        assertEquals("FRAUD_DETECTION", new IntentRecognitionAgent().recognize("收到养老补贴短信，点击链接是真的吗？").intent());
    }

    @Test void recognizesConsultationRiskAndTravelIntents() {
        var agent = new IntentRecognitionAgent();
        assertEquals("FINANCIAL_CONSULTATION", agent.recognize("请介绍个人养老金制度").intent());
        assertEquals("RISK_CONSULTATION", agent.recognize("高收益产品有哪些风险？").intent());
        assertEquals("TRAVEL_RETIREMENT", agent.recognize("退休后想去旅游养老").intent());
    }

    @Test void calculatesDemoScoreAsLowRisk() {
        var request = new FinancialPlanRequest(68, new BigDecimal("6000"), new BigDecimal("500000"), "稳健", "健康养老", null, null);
        var result = new RiskScoreCalculator().calculate(request);
        assertEquals(28, result.score());
        assertEquals("LOW", result.level());
    }

    @Test void appliesRiskThresholds() {
        var calculator = new RiskScoreCalculator();
        var low = calculator.calculate(new FinancialPlanRequest(68, new BigDecimal("6000"), new BigDecimal("500000"), "稳健", "养老", null, null));
        var high = calculator.calculate(new FinancialPlanRequest(55, new BigDecimal("12000"), new BigDecimal("3000000"), "激进", "增值", null, null));
        assertTrue(low.score() <= 30);
        assertTrue(high.score() >= 61);
    }

    @Test void extractsDemoValuesFromNaturalLanguageWithoutAssumptions() {
        var factory = new DefaultListableBeanFactory();
        var agent = new UserProfileAgent(factory.getBeanProvider(ProfileService.class), factory.getBeanProvider(ElderlyFinancialPlanMapper.class));
        var context = new AgentContext(1L, "我68岁，有50万存款，每月退休金6000元，想稳健养老");
        context.intent = new IntentResult("RETIREMENT_PLAN", 0.95);

        agent.enrich(context);

        assertEquals(68, context.request.age());
        assertEquals(new BigDecimal("500000"), context.request.assetAmount());
        assertEquals(new BigDecimal("6000"), context.request.monthlyIncome());
        assertEquals("稳健", context.request.riskPreference());
        assertTrue(context.assumptions.isEmpty());
    }
}
