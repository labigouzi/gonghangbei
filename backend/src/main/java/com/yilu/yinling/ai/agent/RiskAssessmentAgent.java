package com.yilu.yinling.ai.agent;

import com.yilu.yinling.ai.agent.model.AgentModels.RiskResult;
import com.yilu.yinling.financial.dto.FinancialPlanRequest;
import org.springframework.stereotype.Component;

@Component
public class RiskAssessmentAgent {
    private final RiskScoreCalculator calculator;
    public RiskAssessmentAgent(RiskScoreCalculator calculator) { this.calculator = calculator; }
    public RiskResult assess(FinancialPlanRequest request) { return calculator.calculate(request); }
}
