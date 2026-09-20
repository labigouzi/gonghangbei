package com.yilu.yinling.financial.health;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class FinancialHealthScoreCalculator {
    private static final String DISCLAIMER = "本评分仅用于养老金融知识展示，不构成投资建议。";

    public FinancialHealthScore calculate(HealthInput input) {
        int assetSecurity = assetSecurity(input.assetAmount());
        int retirementPreparation = retirementPreparation(input.monthlyIncome(), input.retirementGoal());
        int riskControl = riskControl(input.riskPreference());
        int medicalProtection = medicalProtection(input.medicalNeed());
        var dimensions = List.of(
                new FinancialHealthScore.Dimension("资产安全", assetSecurity),
                new FinancialHealthScore.Dimension("养老准备", retirementPreparation),
                new FinancialHealthScore.Dimension("风险控制", riskControl),
                new FinancialHealthScore.Dimension("医疗保障", medicalProtection)
        );
        int total = clamp((int) Math.round(dimensions.stream().mapToInt(FinancialHealthScore.Dimension::score).average().orElse(0)));
        return new FinancialHealthScore(total, dimensions, warnings(input, medicalProtection), DISCLAIMER);
    }

    private int assetSecurity(BigDecimal assets) {
        if (assets == null) return 55;
        if (assets.compareTo(new BigDecimal("500000")) >= 0) return 85;
        if (assets.compareTo(new BigDecimal("200000")) >= 0) return 75;
        if (assets.compareTo(new BigDecimal("100000")) >= 0) return 65;
        return 50;
    }

    private int retirementPreparation(BigDecimal income, String goal) {
        int score = income == null ? 55 : income.compareTo(new BigDecimal("5000")) >= 0 ? 75 : income.compareTo(new BigDecimal("3000")) >= 0 ? 65 : 50;
        return clamp(score + (hasText(goal) ? 5 : 0));
    }

    private int riskControl(String preference) {
        if (!hasText(preference)) return 55;
        if (preference.contains("稳健") || preference.equalsIgnoreCase("LOW")) return 78;
        if (preference.contains("平衡") || preference.equalsIgnoreCase("MEDIUM")) return 68;
        return 48;
    }

    private int medicalProtection(String medicalNeed) {
        return hasText(medicalNeed) ? 69 : 55;
    }

    private List<String> warnings(HealthInput input, int medicalProtection) {
        var warnings = new java.util.ArrayList<String>();
        warnings.add("优先保留日常生活和突发支出的备用资金");
        if (medicalProtection < 70) warnings.add("建议与家人核对医疗保障和紧急联系人信息");
        if (input.riskPreference() != null && input.riskPreference().contains("激进")) warnings.add("请警惕高收益承诺，并通过正规金融机构核实产品信息");
        else warnings.add("办理金融业务前请通过官方渠道核实，不向陌生人转账");
        return List.copyOf(warnings);
    }

    private boolean hasText(String value) { return value != null && !value.isBlank(); }
    private int clamp(int value) { return Math.max(0, Math.min(100, value)); }

    public record HealthInput(
            Integer age,
            BigDecimal monthlyIncome,
            BigDecimal assetAmount,
            String riskPreference,
            String retirementGoal,
            String medicalNeed
    ) { }
}
