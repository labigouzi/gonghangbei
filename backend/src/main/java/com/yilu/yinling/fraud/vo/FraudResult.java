package com.yilu.yinling.fraud.vo;

import java.math.BigDecimal;
import java.util.List;

public record FraudResult(RiskLevel riskLevel, BigDecimal riskScore, List<String> riskTags,
                          String explanation, String suggestion) { }
