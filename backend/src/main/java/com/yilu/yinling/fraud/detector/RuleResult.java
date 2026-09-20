package com.yilu.yinling.fraud.detector;

import java.util.List;

public record RuleResult(double score, List<String> tags, List<String> reasons) { }
