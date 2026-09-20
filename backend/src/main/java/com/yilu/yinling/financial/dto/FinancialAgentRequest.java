package com.yilu.yinling.financial.dto;
import io.swagger.v3.oas.annotations.media.Schema; import jakarta.validation.constraints.NotBlank;
public record FinancialAgentRequest(@Schema(example="我68岁，有50万存款，每月退休金6000元，退休后如何养老？") @NotBlank(message="问题不能为空") String message) { }
