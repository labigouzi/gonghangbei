package com.yilu.yinling.fraud.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record FraudRecordView(Long id, String inputType, String originalContent, String fileUrl, String ocrContent, String fileName, Long fileSize, String fileType, RiskLevel riskLevel,
                              BigDecimal riskScore, List<String> riskTags, String riskExplanation,
                              String suggestion, LocalDateTime createdAt) { }
