package com.yilu.yinling;

import com.yilu.yinling.financial.dto.FinancialPlanRequest;
import com.yilu.yinling.financial.entity.FinancialProduct;
import com.yilu.yinling.financial.service.FinancialPlanCalculator;
import com.yilu.yinling.financial.service.impl.InMemoryProductRecommendationService;
import com.yilu.yinling.financial.prompt.FinancialPromptTemplate;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FinancialPlanTest {

    @Test
    void calculatesConservativeRetirementAllocationAndDefaultsOptionalNeeds() {
        var request = new FinancialPlanRequest(
                68, new BigDecimal("6000"), new BigDecimal("500000"),
                "稳健", "健康养老", null, " "
        );

        var result = new FinancialPlanCalculator().calculate(request);

        assertEquals("退休初期", result.stage());
        assertEquals("LOW", result.riskLevel());
        assertEquals("暂无特别医疗需求", result.medicalNeed());
        assertEquals("暂无特别旅行需求", result.travelNeed());
        assertEquals(new BigDecimal("100000.00"), result.allocations().get(0).amount());
        assertEquals(new BigDecimal("300000.00"), result.allocations().get(1).amount());
        assertEquals(new BigDecimal("100000.00"), result.allocations().get(2).amount());
    }

    @Test
    void lowRiskRecommendationPrioritizesSavingsPensionAndInsurance() {
        var service = new InMemoryProductRecommendationService(products());

        var recommendations = service.recommend(68, "稳健", "健康养老");

        assertEquals(List.of("个人养老金", "养老储蓄产品", "养老保险"),
                recommendations.stream().limit(3).map(r -> r.productName()).toList());
        assertTrue(recommendations.stream().allMatch(r -> r.riskNotice().contains("不构成购买建议")));
    }

    @Test
    void fallbackReportUsesRequiredSixSections() {
        var request = new FinancialPlanRequest(68, new BigDecimal("6000"), new BigDecimal("500000"), "稳健", "健康养老", null, null);
        var calculation = new FinancialPlanCalculator().calculate(request);
        var recommendations = new InMemoryProductRecommendationService(products()).recommend(68, "稳健", "健康养老");

        String report = new FinancialPromptTemplate().fallback(request, calculation, recommendations);

        for (String heading : List.of("【用户情况】", "【养老需求分析】", "【资金规划建议】", "【金融产品建议】", "【风险提醒】", "【温馨提示】")) {
            assertTrue(report.contains(heading));
        }
    }

    private List<FinancialProduct> products() {
        return List.of(
                product(1L, "稳健型养老理财", "理财", "MEDIUM_LOW", "稳健"),
                product(2L, "养老保险", "保险", "LOW", "健康养老"),
                product(3L, "养老储蓄产品", "储蓄", "LOW", "稳健"),
                product(4L, "个人养老金", "养老储蓄", "LOW", "养老")
        );
    }

    private FinancialProduct product(Long id, String name, String type, String risk, String tags) {
        FinancialProduct product = new FinancialProduct();
        product.setId(id);
        product.setProductName(name);
        product.setProductType(type);
        product.setRiskLevel(risk);
        product.setMinAge(50);
        product.setMaxAge(85);
        product.setDescription("比赛模拟产品");
        product.setSuitableTags(tags);
        return product;
    }
}
