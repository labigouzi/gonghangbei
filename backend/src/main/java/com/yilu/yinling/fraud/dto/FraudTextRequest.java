package com.yilu.yinling.fraud.dto;

import jakarta.validation.constraints.NotBlank;

public record FraudTextRequest(@NotBlank(message = "检测文本不能为空") String content) { }
