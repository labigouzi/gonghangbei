package com.yilu.yinling.financial.health;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yilu.yinling.financial.entity.ElderlyFinancialPlan;
import com.yilu.yinling.financial.mapper.ElderlyFinancialPlanMapper;
import com.yilu.yinling.profile.service.ProfileService;
import com.yilu.yinling.profile.vo.ProfileView;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Profile("!demo")
public class DefaultFinancialHealthService implements FinancialHealthService {
    private static final Pattern NUMBER = Pattern.compile("\\d+(?:\\.\\d+)?");
    private final FinancialHealthScoreCalculator calculator;
    private final ProfileService profiles;
    private final ObjectProvider<ElderlyFinancialPlanMapper> planProvider;

    public DefaultFinancialHealthService(FinancialHealthScoreCalculator calculator, ProfileService profiles,
                                         ObjectProvider<ElderlyFinancialPlanMapper> planProvider) {
        this.calculator = calculator;
        this.profiles = profiles;
        this.planProvider = planProvider;
    }

    @Override
    public FinancialHealthScore getScore(Long userId) {
        ProfileView profile = profiles.get(userId);
        ElderlyFinancialPlanMapper plans = planProvider.getIfAvailable();
        ElderlyFinancialPlan plan = plans == null ? null : plans.selectOne(new LambdaQueryWrapper<ElderlyFinancialPlan>()
                .eq(ElderlyFinancialPlan::getUserId, userId)
                .orderByDesc(ElderlyFinancialPlan::getCreatedAt)
                .last("LIMIT 1"));
        return calculator.calculate(new FinancialHealthScoreCalculator.HealthInput(
                plan != null && plan.getAge() != null ? plan.getAge() : profile == null ? 65 : profile.age(),
                plan != null && plan.getMonthlyIncome() != null ? plan.getMonthlyIncome() : income(profile),
                plan != null && plan.getAssetAmount() != null ? plan.getAssetAmount() : new BigDecimal("200000"),
                plan != null ? plan.getRiskPreference() : profile == null ? "稳健" : profile.riskPreference(),
                plan != null ? plan.getRetirementGoal() : profile == null ? "安心养老" : profile.pensionDemand(),
                plan != null ? plan.getMedicalNeed() : profile == null ? null : profile.healthStatus()
        ));
    }

    private BigDecimal income(ProfileView profile) {
        if (profile == null || profile.monthlyIncome() == null) return new BigDecimal("4000");
        Matcher matcher = NUMBER.matcher(profile.monthlyIncome());
        if (!matcher.find()) return new BigDecimal("4000");
        BigDecimal first = new BigDecimal(matcher.group());
        if (!matcher.find()) return first;
        return first.add(new BigDecimal(matcher.group())).divide(BigDecimal.valueOf(2));
    }
}
