package com.yilu.yinling.financial.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yilu.yinling.financial.mapper.FinancialProductMapper;
import com.yilu.yinling.financial.service.ProductRecommendationService;
import com.yilu.yinling.financial.vo.FinancialPlanResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.ObjectProvider;
import java.util.List;

@Service
@Profile("!demo")
public class DatabaseProductRecommendationService implements ProductRecommendationService {
    private final ObjectProvider<FinancialProductMapper> mapperProvider;
    public DatabaseProductRecommendationService(ObjectProvider<FinancialProductMapper> mapperProvider) { this.mapperProvider = mapperProvider; }
    @Override public List<FinancialPlanResponse.ProductRecommendation> recommend(int age, String riskPreference, String retirementGoal) {
        FinancialProductMapper mapper = mapperProvider.getIfAvailable();
        if (mapper == null) return List.of();
        return new InMemoryProductRecommendationService(mapper.selectList(new QueryWrapper<>())).recommend(age, riskPreference, retirementGoal);
    }
}
