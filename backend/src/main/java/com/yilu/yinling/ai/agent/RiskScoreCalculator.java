package com.yilu.yinling.ai.agent;

import com.yilu.yinling.ai.agent.model.AgentModels.RiskResult;
import com.yilu.yinling.financial.dto.FinancialPlanRequest;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.ArrayList;

@Component
public class RiskScoreCalculator {
    public RiskResult calculate(FinancialPlanRequest request) {
        int score = 10;
        var warnings = new ArrayList<String>();
        if (request.age() >= 65) score += 8;
        if (request.age() >= 75) score += 5;
        if (request.assetAmount().compareTo(new BigDecimal("200000")) < 0) { score += 15; warnings.add("先保留足够的生活和医疗应急资金"); }
        if (request.monthlyIncome().compareTo(new BigDecimal("4000")) < 0) score += 12;
        String preference = request.riskPreference();
        if (preference.contains("激进") || preference.equalsIgnoreCase("HIGH")) score += 55;
        else if (preference.contains("平衡") || preference.equalsIgnoreCase("MEDIUM")) score += 30;
        else score += 10;
        score = Math.min(100, score);
        warnings.add("警惕高收益、保本等承诺，不向陌生账户转账");
        warnings.add("办理金融业务前请通过官方渠道核实");
        return new RiskResult(score, score <= 30 ? "LOW" : score <= 60 ? "MEDIUM" : "HIGH", warnings);
    }
}
