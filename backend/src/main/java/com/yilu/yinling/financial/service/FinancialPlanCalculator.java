package com.yilu.yinling.financial.service;

import com.yilu.yinling.financial.dto.FinancialPlanRequest;
import com.yilu.yinling.financial.vo.FinancialPlanResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class FinancialPlanCalculator {
    private static final String DEFAULT_MEDICAL_NEED = "暂无特别医疗需求";
    private static final String DEFAULT_TRAVEL_NEED = "暂无特别旅行需求";

    public Calculation calculate(FinancialPlanRequest request) {
        String stage = request.age() >= 70 ? "养老保障阶段" : request.age() >= 60 ? "退休初期" : "退休准备阶段";
        String riskLevel = riskLevel(request.riskPreference());
        BigDecimal assets = request.assetAmount().setScale(2, RoundingMode.HALF_UP);
        List<FinancialPlanResponse.Allocation> allocations = List.of(
                allocation("生活备用资金", assets, 20, "用于日常生活和突发开支，优先保证随时可用"),
                allocation("养老保障资金", assets, 60, "用于长期养老与医疗保障，重视安全和稳定"),
                allocation("灵活消费资金", assets, 20, "用于旅行、兴趣和家庭陪伴等弹性需求")
        );
        return new Calculation(stage, riskLevel, valueOrDefault(request.medicalNeed(), DEFAULT_MEDICAL_NEED),
                valueOrDefault(request.travelNeed(), DEFAULT_TRAVEL_NEED), allocations);
    }

    private FinancialPlanResponse.Allocation allocation(String category, BigDecimal assets, int percentage, String description) {
        return new FinancialPlanResponse.Allocation(category,
                assets.multiply(BigDecimal.valueOf(percentage)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP),
                percentage, description);
    }

    private String riskLevel(String preference) {
        if (preference != null && (preference.contains("激进") || preference.equalsIgnoreCase("HIGH"))) return "HIGH";
        if (preference != null && (preference.contains("平衡") || preference.equalsIgnoreCase("MEDIUM"))) return "MEDIUM";
        return "LOW";
    }

    private String valueOrDefault(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }

    public record Calculation(
            String stage,
            String riskLevel,
            String medicalNeed,
            String travelNeed,
            List<FinancialPlanResponse.Allocation> allocations
    ) { }
}
