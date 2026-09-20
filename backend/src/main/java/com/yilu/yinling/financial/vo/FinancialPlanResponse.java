package com.yilu.yinling.financial.vo;

import java.math.BigDecimal;
import java.util.List;

public record FinancialPlanResponse(
        Long planId,
        String riskLevel,
        String retirementStage,
        String summary,
        List<Allocation> allocation,
        List<ProductRecommendation> recommendations,
        String report,
        String riskNotice,
        List<String> financialChain
) {
    public record Allocation(String category, BigDecimal amount, int percentage, String description) { }

    public record ProductRecommendation(
            Long productId,
            String productName,
            String productType,
            String recommendationReason,
            String riskNotice
    ) { }
}
