package com.yilu.yinling.financial.controller;

import com.yilu.yinling.common.response.Result;
import com.yilu.yinling.financial.dto.FinancialPlanRequest;
import com.yilu.yinling.financial.service.FinancialPlanningService;
import com.yilu.yinling.financial.vo.FinancialPlanResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/financial")
@Tag(name = "AI养老金融规划")
public class FinancialPlanController {
    private final FinancialPlanningService planningService;
    public FinancialPlanController(FinancialPlanningService planningService) { this.planningService = planningService; }

    @PostMapping("/plan")
    @Operation(summary = "生成AI养老金融规划", description = "根据年龄、退休收入、资产规模、风险偏好和养老目标生成知识型规划报告；不构成投资建议。")
    public Result<FinancialPlanResponse> plan(Authentication authentication, @Valid @RequestBody FinancialPlanRequest request) {
        return Result.ok(planningService.generate((Long) authentication.getDetails(), request));
    }
}
