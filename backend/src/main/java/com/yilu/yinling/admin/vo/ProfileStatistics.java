package com.yilu.yinling.admin.vo;
import java.util.Map;
public record ProfileStatistics(Map<String, Long> riskPreferences, Map<String, Long> digitalFinanceLevels) { }
