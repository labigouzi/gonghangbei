package com.yilu.yinling.financial.health;

import java.util.List;

public record FinancialHealthScore(
        int totalScore,
        List<Dimension> dimensions,
        List<String> riskWarnings,
        String disclaimer
) {
    public record Dimension(String name, int score) { }
}
