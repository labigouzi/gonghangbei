package com.yilu.yinling.financial.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record FinancialPlanRequest(
        @Schema(example = "68") @NotNull(message = "年龄不能为空") @Min(value = 40, message = "年龄不能小于40岁") @Max(value = 100, message = "年龄不能大于100岁") Integer age,
        @Schema(example = "6000") @NotNull(message = "退休收入不能为空") @DecimalMin(value = "0", message = "退休收入不能为负数") BigDecimal monthlyIncome,
        @Schema(example = "500000") @NotNull(message = "资产规模不能为空") @DecimalMin(value = "0", message = "资产规模不能为负数") BigDecimal assetAmount,
        @Schema(example = "稳健") @NotBlank(message = "风险偏好不能为空") String riskPreference,
        @Schema(example = "健康养老") @NotBlank(message = "养老目标不能为空") String retirementGoal,
        @Schema(example = "慢病复诊便利", nullable = true) String medicalNeed,
        @Schema(example = "每年旅行2次", nullable = true) String travelNeed
) { }
