package com.yilu.yinling.financial.health;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Profile("demo")
public class DemoFinancialHealthService implements FinancialHealthService {
    private final FinancialHealthScoreCalculator calculator;

    public DemoFinancialHealthService(FinancialHealthScoreCalculator calculator) {
        this.calculator = calculator;
    }

    @Override
    public FinancialHealthScore getScore(Long userId) {
        return calculator.calculate(new FinancialHealthScoreCalculator.HealthInput(
                68, new BigDecimal("6000"), new BigDecimal("500000"),
                "稳健", "健康养老", "关注日常医疗保障"
        ));
    }
}
