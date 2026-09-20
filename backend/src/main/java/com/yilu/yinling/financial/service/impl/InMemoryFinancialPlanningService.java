package com.yilu.yinling.financial.service.impl;

import com.yilu.yinling.financial.dto.FinancialPlanRequest;
import com.yilu.yinling.financial.entity.FinancialProduct;
import com.yilu.yinling.financial.prompt.FinancialPromptTemplate;
import com.yilu.yinling.financial.service.FinancialPlanCalculator;
import com.yilu.yinling.financial.service.FinancialPlanningService;
import com.yilu.yinling.financial.service.ProductRecommendationService;
import com.yilu.yinling.financial.vo.FinancialPlanResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Profile("demo")
public class InMemoryFinancialPlanningService implements FinancialPlanningService {
    private final FinancialPlanCalculator calculator;
    private final ProductRecommendationService products;
    private final FinancialPromptTemplate prompt = new FinancialPromptTemplate();
    private final AtomicLong ids = new AtomicLong(1);

    public InMemoryFinancialPlanningService(FinancialPlanCalculator calculator) {
        this.calculator = calculator;
        this.products = new InMemoryProductRecommendationService(demoProducts());
    }

    @Override public FinancialPlanResponse generate(Long userId, FinancialPlanRequest request) {
        var calculation = calculator.calculate(request);
        var recommendations = products.recommend(request.age(), request.riskPreference(), request.retirementGoal());
        String report = prompt.fallback(request, calculation, recommendations);
        return response(ids.getAndIncrement(), calculation, recommendations, report);
    }

    private FinancialPlanResponse response(Long id, FinancialPlanCalculator.Calculation calculation,
                                           List<FinancialPlanResponse.ProductRecommendation> recommendations, String report) {
        return new FinancialPlanResponse(id, calculation.riskLevel(), calculation.stage(),
                "根据您的年龄、收入、资产和风险偏好，建议先保障生活与养老安全，再安排灵活消费。",
                calculation.allocations(), recommendations, report,
                "规划和产品均为比赛模拟知识，不构成投资或购买建议，不承诺收益。",
                List.of("用户画像", "风险评估", "资金规划", "产品匹配", "AI报告"));
    }

    private List<FinancialProduct> demoProducts() {
        return List.of(product(1, "个人养老金", "养老储蓄", "LOW", "养老"), product(2, "养老储蓄产品", "储蓄", "LOW", "稳健"), product(3, "养老保险", "保险", "LOW", "健康养老"), product(4, "稳健型养老理财", "理财", "MEDIUM_LOW", "稳健"), product(5, "老年医疗保障服务", "保障", "LOW", "医疗"));
    }

    private FinancialProduct product(long id, String name, String type, String risk, String tags) {
        FinancialProduct p = new FinancialProduct(); p.setId(id); p.setProductName(name); p.setProductType(type); p.setRiskLevel(risk); p.setMinAge(40); p.setMaxAge(100); p.setSuitableTags(tags); p.setDescription("比赛模拟数据"); return p;
    }
}
