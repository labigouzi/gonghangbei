package com.yilu.yinling.admin.vo;
import java.util.Map;
public record RiskStatistics(Map<String, Long> levels, Map<String, Long> tags) { }
