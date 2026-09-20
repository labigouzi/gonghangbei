package com.yilu.yinling.profile.dto;
import jakarta.validation.constraints.NotBlank;
public record ProfileGenerateRequest(@NotBlank(message = "个人描述不能为空") String description) {}
