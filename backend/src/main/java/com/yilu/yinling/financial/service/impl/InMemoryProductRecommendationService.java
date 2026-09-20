package com.yilu.yinling.financial.service.impl;

import com.yilu.yinling.financial.entity.FinancialProduct;
import com.yilu.yinling.financial.service.ProductRecommendationService;
import com.yilu.yinling.financial.vo.FinancialPlanResponse;

import java.util.Comparator;
import java.util.List;

public class InMemoryProductRecommendationService implements ProductRecommendationService {
    private final List<FinancialProduct> products;

    public InMemoryProductRecommendationService(List<FinancialProduct> products) {
        this.products = List.copyOf(products);
    }

    @Override
    public List<FinancialPlanResponse.ProductRecommendation> recommend(int age, String riskPreference, String retirementGoal) {
        boolean highRisk = contains(riskPreference, "激进") || "HIGH".equalsIgnoreCase(riskPreference);
        int limit = highRisk ? 2 : 4;
        return products.stream()
                .filter(product -> suitableAge(product, age))
                .filter(product -> highRisk || !"HIGH".equalsIgnoreCase(product.getRiskLevel()))
                .sorted(Comparator.comparingInt(this::priority).thenComparing(FinancialProduct::getId))
                .limit(limit)
                .map(product -> recommendation(product, retirementGoal))
                .toList();
    }

    private boolean suitableAge(FinancialProduct product, int age) {
        return (product.getMinAge() == null || age >= product.getMinAge())
                && (product.getMaxAge() == null || age <= product.getMaxAge());
    }

    private int priority(FinancialProduct product) {
        if (contains(product.getProductName(), "个人养老金")) return 1;
        if (contains(product.getProductName(), "养老储蓄")) return 2;
        if (contains(product.getProductName(), "养老保险")) return 3;
        if (contains(product.getProductName(), "稳健")) return 4;
        return 10;
    }

    private FinancialPlanResponse.ProductRecommendation recommendation(FinancialProduct product, String goal) {
        String reason = "适合当前年龄和风险偏好，可用于了解" + product.getProductType() + "类养老金融服务";
        if (goal != null && !goal.isBlank()) reason += "，并与“" + goal.trim() + "”目标进行比较";
        return new FinancialPlanResponse.ProductRecommendation(product.getId(), product.getProductName(),
                product.getProductType(), reason, "产品信息仅作比赛知识演示，不构成购买建议，也不承诺收益。");
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.contains(keyword);
    }
}
