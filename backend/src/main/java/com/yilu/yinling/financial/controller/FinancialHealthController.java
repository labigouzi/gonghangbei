package com.yilu.yinling.financial.controller;

import com.yilu.yinling.common.response.Result;
import com.yilu.yinling.financial.health.FamilySummary;
import com.yilu.yinling.financial.health.FamilySummaryService;
import com.yilu.yinling.financial.health.FinancialHealthScore;
import com.yilu.yinling.financial.health.FinancialHealthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/financial")
@Tag(name = "银龄金融健康")
public class FinancialHealthController {
    private final FinancialHealthService healthService;
    private final FamilySummaryService familyService;

    public FinancialHealthController(FinancialHealthService healthService, FamilySummaryService familyService) {
        this.healthService = healthService;
        this.familyService = familyService;
    }

    @GetMapping("/health-score")
    @Operation(summary = "获取银龄金融健康评分")
    public Result<FinancialHealthScore> healthScore(Authentication authentication) {
        return Result.ok(healthService.getScore((Long) authentication.getDetails()));
    }

    @GetMapping("/family-summary")
    @Operation(summary = "获取家庭协同摘要")
    public Result<FamilySummary> familySummary(Authentication authentication) {
        return Result.ok(familyService.getSummary((Long) authentication.getDetails()));
    }
}
