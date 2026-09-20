package com.yilu.yinling.financial.service;

import com.yilu.yinling.financial.vo.FinancialPlanResponse;

import java.util.List;

public interface ProductRecommendationService {
    List<FinancialPlanResponse.ProductRecommendation> recommend(int age, String riskPreference, String retirementGoal);
}
