package com.yilu.yinling.financial.prompt;

import com.yilu.yinling.financial.dto.FinancialPlanRequest;
import com.yilu.yinling.financial.service.FinancialPlanCalculator;
import com.yilu.yinling.financial.vo.FinancialPlanResponse;

import java.util.List;

public class FinancialPromptTemplate {
    public String build(FinancialPlanRequest request, FinancialPlanCalculator.Calculation calculation,
                        List<FinancialPlanResponse.ProductRecommendation> recommendations) {
        return "你是忆路银龄的AI养老金融顾问。请使用老人容易理解的语言，不承诺收益，不替用户做投资或购买决定。\n"
                + facts(request, calculation, recommendations)
                + "请严格按六个标题分段回答。";
    }

    public String fallback(FinancialPlanRequest request, FinancialPlanCalculator.Calculation calculation,
                           List<FinancialPlanResponse.ProductRecommendation> recommendations) {
        return "【用户情况】您今年" + request.age() + "岁，退休收入约" + request.monthlyIncome() + "元，资产约" + request.assetAmount() + "元。\n"
                + "【养老需求分析】目前处于" + calculation.stage() + "，养老目标是" + request.retirementGoal() + "。医疗需求：" + calculation.medicalNeed() + "；旅行需求：" + calculation.travelNeed() + "。\n"
                + "【资金规划建议】建议先预留" + calculation.allocations().get(0).amount() + "元备用，再安排" + calculation.allocations().get(1).amount() + "元养老保障和" + calculation.allocations().get(2).amount() + "元灵活消费。\n"
                + "【金融产品建议】可以先了解" + productNames(recommendations) + "，再向正规机构咨询。\n"
                + "【风险提醒】不承诺收益，不替您决定；不要向陌生人转账或泄露密码。\n"
                + "【温馨提示】这是一份知识演示报告，请结合家庭实际情况和官方信息判断。";
    }

    private String facts(FinancialPlanRequest request, FinancialPlanCalculator.Calculation calculation,
                         List<FinancialPlanResponse.ProductRecommendation> recommendations) {
        return "【用户情况】年龄：" + request.age() + "岁；退休收入：" + request.monthlyIncome() + "元；资产：" + request.assetAmount() + "元；风险偏好：" + request.riskPreference() + "。\n"
                + "【养老需求分析】目标：" + request.retirementGoal() + "；医疗需求：" + calculation.medicalNeed() + "；旅行需求：" + calculation.travelNeed() + "。\n"
                + "【资金规划建议】备用资金" + calculation.allocations().get(0).amount() + "元，养老保障资金" + calculation.allocations().get(1).amount() + "元，灵活消费资金" + calculation.allocations().get(2).amount() + "元。\n"
                + "【金融产品建议】仅供知识演示，可了解：" + productNames(recommendations) + "。\n"
                + "【风险提醒】产品信息仅作模拟，不构成购买建议，请通过官方机构核实。\n"
                + "【温馨提示】先保障日常生活和医疗需要，再根据家庭情况逐步安排。\n";
    }

    private String productNames(List<FinancialPlanResponse.ProductRecommendation> recommendations) {
        return recommendations.stream().map(FinancialPlanResponse.ProductRecommendation::productName)
                .reduce((a, b) -> a + "、" + b).orElse("暂无");
    }
}
