package com.yilu.yinling.financial.health;

import java.util.List;

public record FamilySummary(
        String elderName,
        int age,
        String retirementStatus,
        List<String> riskReminders
) { }
